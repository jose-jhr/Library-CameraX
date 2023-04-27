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
	        implementation 'com.github.jose-jhr:Library-CameraX:1.0.5'
	}
 ```





































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
 
 
 
 
