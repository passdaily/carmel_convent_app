package info.passdaily.camrelconvertapp.typeofuser.testing_area.share_page

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityShare2PageBinding
import info.passdaily.camrelconvertapp.services.Utils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class SharePage2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityShare2PageBinding

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

        binding = ActivityShare2PageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        extendFab = binding.extendedFab

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Text Image Share"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }
      //  checkpermissions(this@SharePage2Activity)


        extendFab?.setOnClickListener {
          //  takeScreenshot(window.decorView.rootView)

            shareImageandText(binding.textView158.text.toString(),binding.textView160.text.toString())

        }
        Utils.setStatusBarColor(this)
    }

    fun takeScreenshot(view: View): File? {
        val date = Date()
        try {
            // Initialising the directory of storage
            val dirpath: String =
                this@SharePage2Activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
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
           // shareImageandText(path.toUri())
            Log.d(TAG, "takeScreenshot Path: $imageurl")
            Toast.makeText(this@SharePage2Activity, "" + imageurl, Toast.LENGTH_LONG).show()
            return imageurl
        } catch (io: FileNotFoundException) {
            io.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    private fun shareImageandText(title: String, subject: String) {
//        val imageUri = Uri.parse("android.resource://" + packageName +"/drawable/"+ R.drawable.cal_bottom4)
        try {
            val imageUri = Uri.parse("http://demo.passdaily.in/Album_File/Image/F4710181CC9DE09EE03C7A6A0CEB52A7.jpg")
            //http://demo.passdaily.in/Album_File/Image/F4710181CC9DE09EE03C7A6A0CEB52A7.jpg
            val bmpUri = getLocalBitmapUri(this@SharePage2Activity,binding.imageView52,"share")
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, title +"\n"+subject)
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.type = "image/*";
            startActivity(Intent.createChooser(shareIntent, "Share Via"))



//            val bmpUri: Uri? =
//                Utils.getLocalBitmapUri(mContext!!, Global.imgShareView!!, "share_image")
//            // Construct a ShareIntent with link to image
//
//            val shareIntent = Intent()
//            shareIntent.action = Intent.ACTION_SEND
//            shareIntent.putExtra(Intent.EXTRA_TEXT, title)
//            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
//            shareIntent.type = "image/*"
//            // Launch sharing dialog for image
//            startActivity(Intent.createChooser(shareIntent, "Share this Product"))
        } catch (e: NullPointerException) {
            Log.i(TAG, "NullPointerException $e")
            //  shareMethod()
        }




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


    fun getLocalBitmapUri(context: Context, imageView: ImageView, file_name : String): Uri? {
        // Extract Bitmap from ImageView drawable
        val drawable = imageView.drawable
        var bmp: Bitmap? = null
        bmp = if (drawable is BitmapDrawable) {
            (imageView.drawable as BitmapDrawable).bitmap
        } else {
            return null
        }
        // Store image to default external storage directory
        var bmpUri: Uri? = null
        try {
            val cacheDir: File = context.applicationContext.filesDir
            val fileWithinMyDir = File(cacheDir, file_name + System.currentTimeMillis() + ".png")

            if (!fileWithinMyDir.exists()) {
                fileWithinMyDir.parentFile!!.mkdirs()
            }
            val out = FileOutputStream(fileWithinMyDir)
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
            //bmpUri = Uri.fromFile(file);
            bmpUri = FileProvider.getUriForFile(
                context, context.applicationContext
                    .packageName + ".provider", fileWithinMyDir
            )
        } catch (e: java.io.IOException) {
            e.printStackTrace()
        } catch (err : NullPointerException){
            err.printStackTrace()
        }
        return bmpUri
    }

}