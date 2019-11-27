package com.e.osu_id_app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row.view.*
import java.io.File
import android.graphics.BitmapFactory
import junit.framework.TestCase
import org.apache.commons.lang3.StringUtils
import org.junit.Test
import java.io.IOException
import java.nio.file.Files.move
import java.nio.file.Paths



class SessionCardAdapter(val context: Context, val sessioncards: List<SessionCard>) : RecyclerView.Adapter<SessionCardAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.row, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sessioncards.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val sessioncard = sessioncards[position]
        holder.setData(sessioncard, position)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun setData(sessionCard: SessionCard?, pos: Int) {
            val file = File(sessionCard!!.path)
            val path = file.getParentFile().getName()

            if (path == "sent")
            {
                itemView.imageButton5.setImageResource(R.drawable.upload1)
            }


            itemView.SessionTitle.text = sessionCard!!.title
            itemView.Session_Date.text = sessionCard!!.dateStr
            itemView.Session_Number_Uploaded.text = sessionCard!!.progressStr + " - " + path

            if (sessionCard!!.photo1 != "Null") {
                val bmp =
                    BitmapFactory.decodeFile(sessionCard!!.photo1)
                val rotatedBitmap = bmp.rotate(90)
                itemView.photoPreview1.setImageBitmap(rotatedBitmap)
            } else {
                itemView.photoPreview1.setVisibility(View.GONE)
            }

            if (sessionCard!!.photo2 != "Null") {

                val bmp1 =
                    BitmapFactory.decodeFile(sessionCard!!.photo2)
                val rotatedBitmap1 = bmp1.rotate(90)
                itemView.photoPreview2.setImageBitmap(rotatedBitmap1)
            } else {
                itemView.photoPreview2.setVisibility(View.GONE)
            }

            if (sessionCard!!.photo3 != "Null") {
                val bmp2 =
                    BitmapFactory.decodeFile(sessionCard!!.photo3)
                val rotatedBitmap2 = bmp2.rotate(90)
                itemView.photoPreview3.setImageBitmap(rotatedBitmap2)
            } else {
                itemView.photoPreview3.setVisibility(View.GONE)
            }





            // Continue Session
            itemView.imageButton4.setOnClickListener {
                val randomIntent = Intent(context, barcode_scan::class.java)
                val builder = AlertDialog.Builder(context)

                builder.setTitle("Continue " + sessionCard!!.title)
                builder.setMessage("Are you sure you would like to CONTINUE this Session?")

                builder.setPositiveButton("Yes") { dialog, which ->
                    run {
                        randomIntent.putExtra("FileName", sessionCard!!.title)
                        randomIntent.putExtra("Path", path)
                        randomIntent.putExtra("LiveUpload", "true")
                        context.startActivity(randomIntent)
                    }
                }

                // Display a neutral button on alert dialog
                builder.setNeutralButton("Cancel") { _, _ -> }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            }

            //Turn Live upload on or off

            itemView.imageButton5.setOnClickListener {
                val intent = Intent(context, MainActivity::class.java)


                if (path == "sent"){
                    var filePath = Paths.get(file.getAbsolutePath())
                    var dir = File("/storage/emulated/0/Android/media/com.osu_id_app/unsent/" + sessionCard!!.title)
                    var newFilePath = Paths.get(dir.getAbsolutePath())
                    move(filePath, newFilePath)
                }

                else {   // Files are in unsent Folder


                    var dir = File("/storage/emulated/0/Android/media/com.osu_id_app/unsent/" + sessionCard!!.title)

                    // Initiate Batch Upload on unsent folder
                    var b: B=B(dir)
                    b.start()

                }



                context.startActivity(intent)
            }


            // Delete Session
            itemView.imageButton6.setOnClickListener {

                val builder = AlertDialog.Builder(context)

                builder.setTitle("Delete " + sessionCard!!.title)
                builder.setMessage("Are you sure you would like to DELETE this Session?")

                //Yes Button
                builder.setPositiveButton("Yes") { dialog, which ->
                    run {

                        fun deleteRecursive(fileOrDirectory: File) {
                            if (fileOrDirectory.isDirectory)
                                for (child in fileOrDirectory.listFiles()!!)
                                    deleteRecursive(child)

                            fileOrDirectory.delete()
                        }

                        val intent = Intent(context, MainActivity::class.java)
                        val file = File(sessionCard!!.path)
                        val stageFile = File("/storage/emulated/0/Android/media/com.osu_id_app/stage/", sessionCard!!.title)
                        deleteRecursive(file)
                        deleteRecursive(stageFile)
                        context.startActivity(intent)
                    }}
                    // Display a neutral button on alert dialog
                    builder.setNeutralButton("Cancel") { _, _ -> }

                    // Finally, make the alert dialog using builder
                    val dialog: AlertDialog = builder.create()

                    // Display the alert dialog on app interface
                    dialog.show()

                }
            }

        }

}

//threading upload to background
class B(val dir: File) : Thread()
{
    var filesForUpload: Array<File>? = null
    val remoteDirectoryForUploads = "OSU_ID_APP/"



    override fun run() {

        println("44444444444444444444444444")
        println("dir is" + dir.absolutePath)

        // Initiate Connection
        sftpClient = SftpClient.create(createConnectionParameters())

        // Get Files for upload
        filesForUpload = dir.listFiles()

        val remoteFilePaths = ArrayList<String>()
        val filePairs = ArrayList<FilePair>()
        for (uploadFile in filesForUpload!!) {
            val remoteFilePath = remoteDirectoryForUploads + File.separator + uploadFile.name
            filePairs.add(FilePair(uploadFile.path, remoteFilePath))
            remoteFilePaths.add(remoteFilePath)
        }

        // Upload to SFTP
        if (sftpClient!!.upload(filePairs, 120*filesForUpload!!.size)) {

            // Successful upload
            println("Batch Upload Successful")

            // Move files to sent folder
            for (uploadFile in filesForUpload!!) {
                var filePath = Paths.get(uploadFile.getAbsolutePath())
                var filePathStr = uploadFile.absolutePath
                var newFilePathStr = filePathStr.replaceFirst("unsent", "sent", true)
                var newFilePathDir = File(newFilePathStr)
                var newFilePath = Paths.get(newFilePathDir.getAbsolutePath())
                move(filePath, newFilePath)
                println("Moved file to sent folder")
            }

        }

        else {  // Failed Upload

            println("Batch Upload Failed")

        }


    }

    // These environment variables must be defined on your machine
    private val ENVIRONMENT_VARIABLE_HOST = "KSFTP_HOST"
    private val ENVIRONMENT_VARIABLE_PORT = "KSFTP_PORT"
    private val ENVIRONMENT_VARIABLE_USERNAME = "KSFTP_USERNAME"
    private val ENVIRONMENT_VARIABLE_PASSWORD = "KSFTP_PASSWORD"

    // Remote directory for upload - a folder at the user's root level on SFTP server
    // Directory will be created if it does not exist

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

        val remoteFilePaths = java.util.ArrayList<String>()
        val filePairs = java.util.ArrayList<FilePair>()
        for (testFile in testFiles!!) {
            val remoteFilePath = remoteDirectoryForUploads + File.separator + testFile.name
            filePairs.add(FilePair(testFile.path, remoteFilePath))
            remoteFilePaths.add(remoteFilePath)
        }

        TestCase.assertTrue("Files were not uploaded!", sftpClient!!.upload(filePairs, 120*testFiles!!.size))
        TestCase.assertTrue("Files don't exist on server!", sftpClient!!.checkFiles(remoteFilePaths))
    }
}