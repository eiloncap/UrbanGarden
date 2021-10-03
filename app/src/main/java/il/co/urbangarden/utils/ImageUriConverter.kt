package il.co.urbangarden.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment.*
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.MediaColumns.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

abstract class ImageUriConverter {
    companion object {
        private const val title = "IMG"
        private val dateFormatter = SimpleDateFormat(
            "yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault()
        )
        private val filename:String
        get() = "${title}_of_${dateFormatter.format(Date())}.jpeg"
//        private fun getFilename() :String{
//            return "${title}_of_${dateFormatter.format(Date())}.jpeg"
//        }
        val getImageUri: (Context, Bitmap) -> Uri = { ctx, bm ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                saveImageInQ(ctx, bm) else legacySave(ctx, bm)
        }

        private fun legacySave(ctx: Context, bitmap: Bitmap): Uri {
            Log.d("eilon-share", "legacySave")
            val directory = getExternalStoragePublicDirectory(DIRECTORY_PICTURES)
            val file = File(directory, filename)
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            MediaScannerConnection.scanFile(
                ctx, arrayOf(file.absolutePath),
                null, null
            )
            return FileProvider.getUriForFile(
                ctx, "${ctx.packageName}.provider",
                file
            )
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        private fun saveImageInQ(ctx: Context, bitmap: Bitmap): Uri {
            Log.d("eilon-share", "saveImageInQ")
            val fos: OutputStream?
            val contentValues = ContentValues().apply {
                put(DISPLAY_NAME, filename)
                put(MIME_TYPE, "image/jpeg")
                put(RELATIVE_PATH, DIRECTORY_DCIM)
                put(IS_PENDING, 1)
            }

            val contentResolver = ctx.contentResolver
            val uri = contentResolver.insert(EXTERNAL_CONTENT_URI, contentValues)
            uri?.let { contentResolver.openOutputStream(it) }.also { fos = it }
            fos?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            fos?.flush()
            fos?.close()

            contentValues.clear()
            contentValues.put(IS_PENDING, 0)
            uri?.let {
                contentResolver.update(it, contentValues, null, null)
            }
            return uri!!
        }
    }
}

