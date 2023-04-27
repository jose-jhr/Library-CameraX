package com.ingenieriiajhr.librarycamerax

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ingenieriiajhr.jhrCameraX.BitmapResponse
import com.ingenieriiajhr.jhrCameraX.CameraJhr
import com.ingenieriiajhr.jhrCameraX.LuminosityAnalyzer
import com.ingenieriiajhr.librarycamerax.databinding.ActivityMainBinding
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    lateinit var binding :ActivityMainBinding
    lateinit var cameraJhr: CameraJhr
    val analyzer = LuminosityAnalyzer()

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
        cameraJhr.addlistenerResponse(object :BitmapResponse{
            override fun bitmapReturn(bitmap: Bitmap?) {
                if (bitmap!=null){
                    //val newBitmap = binding.cameraPreview.bitmap
                    val newBitmap2 = bitmap.rotate(180f)
                    runOnUiThread {
                        binding.imgBitMap.setImageBitmap(newBitmap2)
                        binding.imgBitMap2.setImageBitmap(newBitmap2)
                    }

                }
            }
        })
        cameraJhr.initBitmap()
        //selector camera LENS_FACING_FRONT = 0;    LENS_FACING_BACK = 1;
        //aspect Ratio  RATIO_4_3 = 0; RATIO_16_9 = 1;
        cameraJhr.start(0,0,binding.cameraPreview,true)
    }


    /**
     * @return bitmap rotate degrees
     */
    fun Bitmap.rotate(degrees:Float) = Bitmap.createBitmap(this,0,0,width,height,Matrix().apply { postRotate(degrees) },true)



}