package info.passdaily.camrelconvertapp.typeofuser.testing_area.share_page

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivitySharePageBinding
import info.passdaily.camrelconvertapp.services.Utils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class SharePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySharePageBinding

    var TAG ="SharePageActivity"

    var toolbar: Toolbar? = null

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val permissionstorage = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )


    var fileName: String? = null

    var extendFab : ExtendedFloatingActionButton? = null

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_share_page)

        binding = ActivitySharePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        extendFab = binding.extendedFab

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "ScreenShot Share"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }
        checkpermissions(this@SharePageActivity)


        extendFab?.setOnClickListener {
            takeScreenshot(window.decorView.rootView)


        }
        Utils.setStatusBarColor(this)
    }

    fun takeScreenshot(view: View): File? {
        val date = Date()
        try {
            // Initialising the directory of storage
            val dirpath: String =
                this@SharePageActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
            val file = File(dirpath)
            if (!file.exists()) {
                val mkdir = file.mkdir()
            }
            // File name : keeping file name unique using data time.
            val path = dirpath + "/" + date.time + ".jpeg"
            view.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false
            val imageurl = File(path)
            val outputStream = FileOutputStream(imageurl)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            outputStream.flush()
            outputStream.close()
            shareImageandText(path.toUri())
            Log.d(TAG, "takeScreenshot Path: $imageurl")
            Toast.makeText(this@SharePageActivity, "" + imageurl, Toast.LENGTH_LONG).show()
            return imageurl
        } catch (io: FileNotFoundException) {
            io.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    private fun shareImageandText(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)

        // putting uri of image to be shared
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        // adding text to share
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image")

//        // Add subject Here
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")

        // setting type to image
        intent.type = "image/png"

        // calling startactivity() to share
        startActivity(Intent.createChooser(intent, "Share Via"))
    }


    // check weather storage permission is given or not
    fun checkpermissions(activity: Activity?) {
        val permissions: Int =
            ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        // If storage permission is not given then request for External Storage Permission
        if (permissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissionstorage, REQUEST_EXTERNAL_STORAGE)
        }
    }




}