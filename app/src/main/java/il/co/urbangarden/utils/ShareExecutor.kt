package il.co.urbangarden.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

abstract class ShareExecutor {

    companion object {
        fun shareContent(ctx: Context, text: String, imageUri: Uri?) {
            val shareIntent: Intent = Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, text)
            if (imageUri != null) {
                shareIntent.setType("image/*")
                    .putExtra(Intent.EXTRA_STREAM, imageUri)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try {
                ctx.startActivity(shareIntent)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(ctx, "no app found", Toast.LENGTH_LONG).show()
            }
        }
    }
}