package com.ingenieriiajhr.jhrCameraX

import android.annotation.SuppressLint
import android.graphics.*
import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream


class LuminosityAnalyzer() : ImageAnalysis.Analyzer{

    var isFrontCamera = false

    //create method listener
    lateinit var bitmapResponse: BitmapResponse

    //create method listener imageProxy
    lateinit var imageProxyResponse: ImageProxyResponse

    //init method listener
    fun addListener(bitmapResponse: BitmapResponse){
        this.bitmapResponse = bitmapResponse
    }

    fun addListenerImageProxy(imageProxy: ImageProxyResponse){
        this.imageProxyResponse = imageProxy
    }

    //response boolean imageProxi
    var returnBitmap = true

    //response boolean bitmap
    var returnImageProxy = false


    /*
    private fun toBitmap(image: Image): Bitmap? {
        val yBuffer = image.planes[0].buffer // Y
        val vuBuffer = image.planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)


        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
       /* val matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(createBitmap,0,0,createBitmap.width,createBitmap.height,matrix,true)
    */
    }*/


    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        try {


            if (returnBitmap && !returnImageProxy){
                val bitmap = convertionAllBitmapOrImageProxy(imageProxy)
                bitmapResponse.bitmapReturn(bitmap)
            }else{
                if (!returnBitmap && returnImageProxy ){
                    imageProxyResponse.imageProxyReturn(imageProxy)
                }
            }

            imageProxy.close()

        } catch (e: IllegalStateException) {
            // Handle the exception here
            println("error en conversion")
        }
    }

    private fun convertionAllBitmapOrImageProxy(imageProxy: ImageProxy):Bitmap {
        val bitmapBuffer =
            Bitmap.createBitmap(
                imageProxy.width,
                imageProxy.height,
                Bitmap.Config.ARGB_8888
            )

        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }

        val matrix = Matrix().apply {
            // Rotate the frame received from the camera to be in the same direction as it'll be shown
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

            // flip image if user use front camera
            if (isFrontCamera) {
                postScale(
                    -1f,
                    1f,
                    imageProxy.width.toFloat(),
                    imageProxy.height.toFloat()
                )
            }
        }

        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
            matrix, true
        )

        return rotatedBitmap
    }


}