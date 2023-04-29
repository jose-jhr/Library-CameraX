package com.ingenieriiajhr.librarycamerax

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.camera.core.ImageProxy
import com.ingenieriiajhr.jhrCameraX.BitmapResponse
import com.ingenieriiajhr.jhrCameraX.CameraJhr
import com.ingenieriiajhr.jhrCameraX.ImageProxyResponse
import com.ingenieriiajhr.jhrCameraX.LuminosityAnalyzer
import com.ingenieriiajhr.librarycamerax.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    lateinit var binding :ActivityMainBinding
    lateinit var cameraJhr: CameraJhr

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init cameraJHR
        cameraJhr = CameraJhr(this)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (cameraJhr.allpermissionsGranted() && !cameraJhr.ifStartCamera){
            startCameraJhr()
        }else{
            cameraJhr.noPermissions()
        }
    }

    /**
     * start Camera Jhr
     */
    private fun startCameraJhr() {
        cameraJhr.addlistenerBitmap(object :BitmapResponse{
            override fun bitmapReturn(bitmap: Bitmap?) {
                if (bitmap!=null){
                    runOnUiThread {
                        binding.imgBitMap2.setImageBitmap(bitmap)
                    }
                }
            }
        })

        cameraJhr.addlistenerImageProxy(object :ImageProxyResponse{
            override fun imageProxyReturn(imageProxy: ImageProxy) {
                try {
                    val bitmap = Bitmap.createBitmap(imageProxy.width,imageProxy.height,Bitmap.Config.ARGB_8888)
                    imageProxy.use { bitmap.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
                    runOnUiThread {
                        binding.imgBitMap.setImageBitmap(bitmap)
                    }
                }catch (e: IllegalStateException) {
                    // Handle the exception here
                    println("error en conversion imageproxy")
                }

            }
        })

        cameraJhr.initBitmap()
        cameraJhr.initImageProxy()
        //selector camera LENS_FACING_FRONT = 0;    LENS_FACING_BACK = 1;
        //aspect Ratio  RATIO_4_3 = 0; RATIO_16_9 = 1;
        cameraJhr.start(0,0,binding.cameraPreview,false,false,true)
    }




    /**
     * @return bitmap rotate degrees
     */
    fun Bitmap.rotate(degrees:Float) = Bitmap.createBitmap(this,0,0,width,height,Matrix().apply { postRotate(degrees) },true)
    

}