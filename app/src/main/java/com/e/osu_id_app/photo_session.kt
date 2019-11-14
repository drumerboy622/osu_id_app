package com.e.osu_id_app

// Your IDE likely can auto-import these classes, but there are several
// different implementations so we list them here to disambiguate.
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Size
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import junit.framework.TestCase
import org.apache.commons.lang3.StringUtils
import org.junit.Test
import java.io.IOException
import java.util.*

private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class photo_session : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_center_actvitiy)

        val strTry: String = intent.getStringExtra("student_barcode") as String

        var textView = findViewById<TextView>(R.id.textView4)

        textView.text = strTry

        viewFinder = findViewById(R.id.view_finder)

        // Request camera permissions
        if (allPermissionsGranted()) {
            viewFinder.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Every time the provided texture view changes, recompute layout
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }

        // Create connection parameters
        sftpClient = SftpClient.create(createConnectionParameters())

        // Retrieve image directory location from local resources
        //val testImagesSourceDirectory = getImagesDirectory("/testImages/")
        //TestCase.assertTrue("Test images directory could not be found!", testImagesSourceDirectory.isDirectory)

        // Check for files in image directory
        //testFiles = testImagesSourceDirectory.listFiles()
        //TestCase.assertTrue("No test image files were found!", testFiles != null && testFiles!!.size > 0)
        //KsftpLog.logDebug("Found " + testFiles!!.size + " test image files")
    }

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var viewFinder: TextureView

    // These environment variables must be defined on your machine
    private val ENVIRONMENT_VARIABLE_HOST = "KSFTP_HOST"
    private val ENVIRONMENT_VARIABLE_PORT = "KSFTP_PORT"
    private val ENVIRONMENT_VARIABLE_USERNAME = "KSFTP_USERNAME"
    private val ENVIRONMENT_VARIABLE_PASSWORD = "KSFTP_PASSWORD"

    // Remote directory for upload - a folder at the user's root level on SFTP server
    // Directory will be created if it does not exist
    private val remoteDirectoryForUploads = "Folder1new/"

    private var testFiles: Array<File>? = null
    private var sftpClient: SftpClient? = null


    /**
     * Gets a file from the test resources package, or throws an exception if the test file doesn't exist.
     * @param relativeFilePath the file path, relative to "src/test/resources"
     */
    @Throws(Exception::class)
    private fun getImagesDirectory(relativeFilePath: String): File {
        var theRelativeFilePath = relativeFilePath
        val url = photo_session::class.java.getResource(theRelativeFilePath)

        val testFile = File(url!!.file)
        TestCase.assertTrue("No test file exists for relative path '$theRelativeFilePath'", testFile.exists())
        return testFile
    }

    /**
     * Ensures that a directory exists for the specified path, and returns the [File],
     * or `null` if it could not be created.

     * @param directoryPath the directory path to ensure
     */
    @Throws(IOException::class)
    private fun ensureDirectory(directoryPath: String): File {
        val errorMessage = "Could not create directory for path '$directoryPath'"
        if (StringUtils.isEmpty(directoryPath)) {
            throw IOException(errorMessage)
        }

        val directory = File(directoryPath)
        if (directory.exists()) {
            if (!directory.isDirectory) {
                throw IOException("File '$directory' exists and is not a directory. Unable to create directory.")
            }
        } else {
            if (!directory.mkdirs()) {
                // Double-check that some other thread or process hasn't made
                // the directory in the background
                if (!directory.isDirectory) {
                    throw IOException("Unable to create directory '$directory'")
                }
            }
        }

        if (!directory.isDirectory) {
            throw IOException(errorMessage)
        }
        return directory
    }

    /**
     * Creates new connection parameters.
     */
    private fun createConnectionParameters(): SftpConnectionParameters {
        return SftpConnectionParametersBuilder.newInstance().createConnectionParameters()
            .withHostFromEnvironmentVariable(ENVIRONMENT_VARIABLE_HOST)
            .withPortFromEnvironmentVariable(ENVIRONMENT_VARIABLE_PORT)
            .withUsernameFromEnvironmentVariable(ENVIRONMENT_VARIABLE_USERNAME)
            .withPasswordFromEnvironmentVariable(ENVIRONMENT_VARIABLE_PASSWORD)
            .create()
    }

    @Test
    @Throws(Exception::class)
    fun testAllSftpOperations() {
        executeBatchUpload()
    }

    @Throws(Exception::class)
    private fun executeBatchUpload() {

        val remoteFilePaths = ArrayList<String>()
        val filePairs = ArrayList<FilePair>()
        for (testFile in testFiles!!) {
            val remoteFilePath = remoteDirectoryForUploads + File.separator + testFile.name
            filePairs.add(FilePair(testFile.path, remoteFilePath))
            remoteFilePaths.add(remoteFilePath)
        }

        TestCase.assertTrue("Files were not uploaded!", sftpClient!!.upload(filePairs, 120*testFiles!!.size))
        TestCase.assertTrue("Files don't exist on server!", sftpClient!!.checkFiles(remoteFilePaths))
    }

    /*
    @Test
    @Throws(Exception::class)
    fun testBatchUploadTimeout() {
        val remoteFilePaths = ArrayList<String>()
        val filePairs = ArrayList<FilePair>()
        for (testFile in testFiles!!) {
            val remoteFilePath = remoteDirectoryForUploads + File.separator + testFile.name
            filePairs.add(FilePair(testFile.path, remoteFilePath))
            remoteFilePaths.add(remoteFilePath)
        }

        assertFailsWith(UploadTimeoutException::class, "Batch upload timed out with an unexpected exception") {
            client!!.upload(filePairs, 2, 5)
        }
    }*/

    private fun startCamera() {

        val barcode: String = intent.getStringExtra("student_barcode") as String
        val fileName: String = intent.getStringExtra("FileName") as String


        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(300, 400))
        }.build()


        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                // We don't set a resolution for image capture; instead, we
                // select a capture mode which will infer the appropriate
                // resolution based on aspect ration and requested mode
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        // Build the image capture use case and attach button click listener
        val imageCapture = ImageCapture(imageCaptureConfig)
        findViewById<ImageButton>(R.id.capture_button).setOnClickListener {

            val file = File(externalMediaDirs.first(), fileName + "/" + barcode + ".jpg")

            imageCapture.takePicture(file, executor,
                object : ImageCapture.OnImageSavedListener {
                    override fun onError(
                        imageCaptureError: ImageCapture.ImageCaptureError,
                        message: String,
                        exc: Throwable?
                    ) {
                        val msg = "Photo capture failed: $message"
                        Log.e("CameraXApp", msg, exc)
                        viewFinder.post {
                            makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onImageSaved(file: File) {
                        val randomIntent = Intent(this@photo_session, Review::class.java)

                        randomIntent.putExtra("FileName", fileName)
                        randomIntent.putExtra("FilePath", file.absolutePath)
                        randomIntent.putExtra("student_barcode", barcode)

                        val msg = "Photo capture succeeded: ${file.absolutePath}"
                        Log.d("CameraXApp", msg)
                        viewFinder.post {
                            makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }

                        // Upload to SFTP
                        sftpClient!!.upload(file.absolutePath, remoteDirectoryForUploads, 300);

                        startActivity(randomIntent)
                    }
                })
        }

        // Setup image analysis pipeline that computes average pixel luminance
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            // In our analysis, we care more about the latest image than
            // analyzing *every* image
            setImageReaderMode(
                ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

        // Build the image analysis use case and instantiate our analyzer
        val analyzerUseCase = ImageAnalysis(analyzerConfig).apply {
            setAnalyzer(executor, LuminosityAnalyzer())
        }


        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
        CameraX.bindToLifecycle(this, preview, imageCapture, analyzerUseCase)
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }

    /**
     * Process result from permission request dialog box, has the request
     * been granted? If yes, start Camera. Otherwise display a toast
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                viewFinder.post { startCamera() }
            } else {
                makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
        private var lastAnalyzedTimestamp = 0L

        /**
         * Helper extension function used to extract a byte array from an
         * image plane buffer
         */
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy, rotationDegrees: Int) {
            val currentTimestamp = System.currentTimeMillis()
            // Calculate the average luma no more often than every second
            if (currentTimestamp - lastAnalyzedTimestamp >=
                TimeUnit.SECONDS.toMillis(1)) {
                // Since format in ImageAnalysis is YUV, image.planes[0]
                // contains the Y (luminance) plane
                val buffer = image.planes[0].buffer
                // Extract image data from callback object
                val data = buffer.toByteArray()
                // Convert the data into an array of pixel values
                val pixels = data.map { it.toInt() and 0xFF }
                // Compute average luminance for the image
                val luma = pixels.average()
                // Log the new luma value
                Log.d("CameraXApp", "Average luminosity: $luma")
                // Update timestamp of last analyzed frame
                lastAnalyzedTimestamp = currentTimestamp
            }
        }
    }
}