package com.ingenieriiajhr.jhrCameraX

import android.annotation.SuppressLint
import android.graphics.*
import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


class LuminosityAnalyzer() : ImageAnalysis.Analyzer{


    //create method listener
    lateinit var bitmapResponse: BitmapResponse

    //init method listener
    fun addListener(bitmapResponse: BitmapResponse){
        this.bitmapResponse = bitmapResponse
    }


    private fun toBitmap(image: Image): Bitmap? {
        val planes = image.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        //U and V are swapped
        yBuffer[nv21, 0, ySize]
        vBuffer[nv21, ySize, vSize]
        uBuffer[nv21, ySize + vSize, uSize]
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
        val imageBytes = out.toByteArray()
        val createBitmap = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)
        val matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(createBitmap,0,0,createBitmap.width,createBitmap.height,matrix,true)
    }


    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        try {
            val imageGet = image.image
            //convert framge get in to bitmap
            val bitmapGet = toBitmap(imageGet!!)
            bitmapResponse.bitmapReturn(bitmapGet)
            image.close()
        } catch (e: IllegalStateException) {
            // Handle the exception here
            println("error en conversion")
        }
    }

}