package com.ingenieriiajhr.jhrCameraX

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors

class CameraJhr(val context: Context) {

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

    var ifStartCamera = false

    private var imageAnalysis: ImageAnalysis? = null

    //process unique thread
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    //object analyzer image
    private var luminosityAnalyzer = LuminosityAnalyzer()

    //image preview
    private var imagePreview: Preview? = null

    //bitmap response two
    lateinit var responseBitmap:BitmapResponse

    //imageproxy response two
    lateinit var responseImageProxy: ImageProxyResponse


    fun addlistenerBitmap(response: BitmapResponse){
        this.responseBitmap = response
    }

    fun addlistenerImageProxy(response: ImageProxyResponse){
        this.responseImageProxy = response
    }


    /**
     * start camera
     * 0 @param selectorCamera Camera frontal
     * 1 @param selectorCamera Camera back
     *
     * 0 @parama spect ratio RATIO_4_3
     * 1 @param aspect ratio RATIO_16_9
     *
     * cameraPreview true or false
     */
    @SuppressLint("WrongConstant")
    fun start(selectorCamera: Int, aspectRatio: Int, view: PreviewView, cameraPreview:Boolean,returImageProxy: Boolean,returBitmap: Boolean){
        //get intance provider camera
        val cameraProviderFuture =ProcessCameraProvider.getInstance(context)

        CameraSelector.DEFAULT_BACK_CAMERA
        //select camera
        val cameraSelector = CameraSelector.Builder().requireLensFacing(selectorCamera).build()

        luminosityAnalyzer.returnBitmap = returBitmap
        luminosityAnalyzer.returnImageProxy = returImageProxy


        //camera is frontal?
        luminosityAnalyzer.isFrontCamera = selectorCamera ==0

        //properties analysis image
        imageAnalysis = ImageAnalysis.Builder().apply {
            //Delete the next image while analyzing the actual image, keep(mantener) last
            setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        }.build()

        imageAnalysis?.setAnalyzer(cameraExecutor,luminosityAnalyzer)

        cameraProviderFuture.addListener({
            imagePreview = Preview.Builder().apply {
                setTargetAspectRatio(aspectRatio)
                setTargetRotation(view.display.rotation)

            }.build()

            val cameraProvider = cameraProviderFuture.get()

            if (cameraPreview) {
                cameraProvider.bindToLifecycle((context as LifecycleOwner),cameraSelector,imagePreview,imageAnalysis)
                view.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                imagePreview?.setSurfaceProvider(view.surfaceProvider)
            }
            else{
                cameraProvider.bindToLifecycle((context as LifecycleOwner),cameraSelector,imageAnalysis)
            }

        },ContextCompat.getMainExecutor(context))

        ifStartCamera = true

    }

    /**
     * all elements comply condition expressed
     */
    fun allpermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(context,it) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * no permissions camera
     */
    fun noPermissions() {
        //version previus no permission required
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (context as Activity).requestPermissions(
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS)
        }else{
            Toast.makeText(context, "No presentas los permisos suficientes", Toast.LENGTH_SHORT).show()
        }
    }

    fun initBitmap(){
        luminosityAnalyzer.addListener(object :BitmapResponse{
            override fun bitmapReturn(bitmap: Bitmap?) {
                responseBitmap.bitmapReturn(bitmap)
            }
        })
    }

    fun initImageProxy(){
        luminosityAnalyzer.addListenerImageProxy(object :ImageProxyResponse{
            override fun imageProxyReturn(imageProxy: ImageProxy) {
                responseImageProxy.imageProxyReturn(imageProxy)
            }

        })
    }



}