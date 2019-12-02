package com.e.osu_id_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.TextureView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import kotlinx.android.synthetic.main.activity_photo_center_actvitiy.*
import kotlinx.android.synthetic.main.activity_second.*


val permissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)

class photo_session : AppCompatActivity() {

    private var lensFacing = CameraX.LensFacing.BACK
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_center_actvitiy)

        val strTry: String = intent.getStringExtra("student_barcode") as String
        val barcode: String = intent.getStringExtra("student_barcode") as String
        val fileName: String = intent.getStringExtra("FileName") as String
        val liveUpload: String = intent.getStringExtra("LiveUpload") as String

        // Display Barcode
        var textView = findViewById<TextView>(R.id.textView4)
        textView.text = strTry

        var live = "live"
        if (liveUpload == "false")
        {
            live = "notLive"
        }
        val file = File(externalMediaDirs.first(), live + "/" + fileName + "/unsent/" + barcode + ".jpg")


        bindCamera()

        // Takes an images and saves it in the local storage
        capture_button.setOnClickListener {
            imageCapture?.takePicture(file,
                object : ImageCapture.OnImageSavedListener {
                    override fun onError(error: ImageCapture.UseCaseError,
                                         message: String, exc: Throwable?) {
                        Log.e("Image", error.toString())
                    }
                    override fun onImageSaved(file: File) {
                        val randomIntent = Intent(this@photo_session, Review::class.java)

                        randomIntent.putExtra("FileName", fileName)
                        randomIntent.putExtra("FilePath", file.absolutePath)

                        println("333333333333333")
                        println(file.absolutePath)
                        println("444444444444444")

                        randomIntent.putExtra("student_barcode", barcode)
                        randomIntent.putExtra("LiveUpload", liveUpload)


                        startActivity(randomIntent)
                    }
                })
        }


        // Changes the flash mode when the button is clicked
        fab_flash.setOnClickListener {
            val flashMode = imageCapture?.flashMode
            if(flashMode == FlashMode.ON) {
                imageCapture?.flashMode = FlashMode.OFF
                fab_flash.backgroundTintList = (ContextCompat.getColorStateList(getApplicationContext(), R.color.colorPrimaryDark))
            } else {
                imageCapture?.flashMode = FlashMode.ON
                fab_flash.backgroundTintList = (ContextCompat.getColorStateList(getApplicationContext(), R.color.white))
            }
        }


    }
    /**
     * Check if the app has all permissions
     */
    private fun hasNoPermissions(): Boolean{
        return ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
    }

    /**
     * Request all permissions
     */
    fun requestPermission(){
        ActivityCompat.requestPermissions(this, permissions,0)
    }

    /**
     * Bind the Camera to the lifecycle
     */
    private fun bindCamera(){
        CameraX.unbindAll()

        // Preview config for the camera
        val previewConfig = PreviewConfig.Builder()
            .setLensFacing(lensFacing)
            .build()

        val preview = Preview(previewConfig)

        // The view that displays the preview
        val textureView: TextureView = findViewById(R.id.view_finder)

        // Handles the output data of the camera
        preview.setOnPreviewOutputUpdateListener { previewOutput ->
            // Displays the camera image in our preview view
            textureView.surfaceTexture = previewOutput.surfaceTexture
        }


        // Image capture config which controls the Flash and Lens
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .setTargetRotation(windowManager.defaultDisplay.rotation)
            .setLensFacing(lensFacing)
            .setFlashMode(FlashMode.AUTO)
            .build()

        imageCapture = ImageCapture(imageCaptureConfig)

        // Bind the camera to the lifecycle
        CameraX.bindToLifecycle(this as LifecycleOwner, imageCapture, preview)
    }

    override fun onStart() {
        super.onStart()

        // Check and request permissions
        if (hasNoPermissions()) {
            requestPermission()
        }
    }

}