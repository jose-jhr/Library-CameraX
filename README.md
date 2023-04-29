# Library-CameraX

 ```kotlin
 
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

 ```

 ```kotlin
 
dependencies {
	        
		
		// If you want to additionally use the CameraX View class
		implementation "androidx.camera:camera-view:1.3.0-alpha06"
		
		implementation 'com.github.jose-jhr:Library-CameraX:1.0.8'
		
	}
	
 ```








 ```kotlin
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

  ```
  
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

     <androidx.camera.view.PreviewView
        android:layout_width="match_parent"
        android:id="@+id/camera_preview"
        android:layout_height="match_parent"
        app:scaleType="fillStart"
        >
    </androidx.camera.view.PreviewView>

    <ImageView
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:id="@+id/imgBitMap"
        android:scaleType="fitStart"
        >
    </ImageView>

    <ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/imgBitMap2"
        android:layout_below="@id/imgBitMap"
        >
    </ImageView>

</RelativeLayout>

 ```



preview camera X
![image](https://user-images.githubusercontent.com/66834393/234764942-15df1800-58e4-46bf-8282-50bf637905c1.png)


































end Upload library


build.gradle from library below dependencies {}
 ```kotlin
 
 afterEvaluate{
    publishing {
        publications {
            release(MavenPublication) {
                from components.release
                groupId = 'com.github.ingenieriajhr'
                artifactId = 'android-camerax'
                version = '1.0.5'
            }
        }
    }
}
 ```
 
 
 file jitpack.yml project generic
 
  ```kotlin
  jdk:
  - openjdk11
   ```
 
 
 
 
