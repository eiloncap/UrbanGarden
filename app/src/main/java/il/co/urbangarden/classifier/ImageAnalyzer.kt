package il.co.urbangarden.classifier

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import il.co.urbangarden.ml.Model
import il.co.urbangarden.utils.YuvToRgbConverter
import org.tensorflow.lite.support.image.TensorImage


// Listener for the result of the ImageAnalyzer
typealias RecognitionListener = (recognition: List<Recognition>) -> Unit

class ImageAnalyzer(ctx: Context, private val listener: RecognitionListener) :
    ImageAnalysis.Analyzer {

    companion object {
        // Constants
        private const val MAX_RESULT_DISPLAY = 3 // Maximum number of results displayed
        private const val TAG = "TFL Classify" // Name for logging
    }

    // TODO 1: Add class variable TensorFlow Lite Model
    private val flowerModel = Model.newInstance(ctx)
    // Initializing the flowerModel by lazy so that it runs in the same thread when the process
    // method is called.

    // TODO 6. Optional GPU acceleration


    override fun analyze(imageProxy: ImageProxy) {

        val items = mutableListOf<Recognition>()

        // TODO 2: Convert Image to Bitmap then to TensorImage
        val tfImage = TensorImage.fromBitmap(toBitmap(imageProxy))

        // TODO 3: Process the image using the trained model, sort and pick out the top results
        val outputs = flowerModel.process(tfImage)
            .probabilityAsCategoryList.apply {
                sortByDescending { it.score }
            }.take(MAX_RESULT_DISPLAY)

        // TODO 4: Converting the top probability items into a list of recognitions
        for (output in outputs) {
            items.add(Recognition(output.label, output.score))
        }

        // START - Placeholder code at the start of the codelab. Comment this block of code out.
//            for (i in 0 until MAX_RESULT_DISPLAY){
//                items.add(Recognition("Fake label $i", Random.nextFloat()))
//            }
        // END - Placeholder code at the start of the codelab. Comment this block of code out.

        // Return the result
        listener(items.toList())

        // Close the image,this tells CameraX to feed the next image to the analyzer
        imageProxy.close()
    }

    /**
     * Convert Image Proxy to Bitmap
     */
    private val yuvToRgbConverter = YuvToRgbConverter(ctx)
    private lateinit var bitmapBuffer: Bitmap
    private lateinit var rotationMatrix: Matrix

    @SuppressLint("UnsafeOptInUsageError")
    private fun toBitmap(imageProxy: ImageProxy): Bitmap? {

        val image = imageProxy.image ?: return null

        // Initialise Buffer
        if (!::bitmapBuffer.isInitialized) {
            // The image rotation and RGB image buffer are initialized only once
            Log.d(TAG, "Initalise toBitmap()")
            rotationMatrix = Matrix()
            rotationMatrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            bitmapBuffer = Bitmap.createBitmap(
                imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
            )
        }

        // Pass image to an image analyser
        yuvToRgbConverter.yuvToRgb(image, bitmapBuffer)

        // Create the Bitmap in the correct orientation
        return Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            rotationMatrix,
            false
        )
    }

}
