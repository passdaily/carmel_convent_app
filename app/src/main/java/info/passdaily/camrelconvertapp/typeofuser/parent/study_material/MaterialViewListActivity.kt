package info.passdaily.camrelconvertapp.typeofuser.parent.study_material

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityMaterialViewListBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.downloader.*
import java.io.File

@Suppress("DEPRECATION")
class MaterialViewListActivity : AppCompatActivity(),FileClickListener {

    var TAG = "MaterialViewListActivity"
    private lateinit var binding: ActivityMaterialViewListBinding
    var toolbar: Toolbar? = null

    var SUBJECT_NAME = ""
    var TITLE = ""
    var DESCRIPTION = ""
    var STUDY_MATERIAL_ID = 0

    var textViewSubject : TextView? = null
    var textViewTitle : TextView? = null
    var textViewDesc : TextView? = null
    var recyclerViewList : RecyclerView? = null

    var constraintLayoutNoFile  : ConstraintLayout? = null
    var constraintLayout  : ConstraintLayout? = null

    private var readPermission = false
    private var writePermission = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var studyInitViewModel: StudyInitViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //"TITLE", studyMater
        //"DESCRIPTION", stud
        //"STUDY_MATERIAL_ID"
        val extras = intent.extras
        if (extras != null) {
            SUBJECT_NAME = extras.getString("SUBJECT_NAME")!!
            TITLE = extras.getString("TITLE")!!
            DESCRIPTION = extras.getString("DESCRIPTION")!!
            STUDY_MATERIAL_ID = extras.getInt("STUDY_MATERIAL_ID")
        }


        studyInitViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[StudyInitViewModel::class.java]


        binding = ActivityMaterialViewListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "View Details"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //   toolbar.setTitle("About-Us");
            toolbar?.setNavigationOnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            }
        }

        constraintLayout = binding.constraintLayout

        constraintLayoutNoFile = binding.constraintLayoutNoFile

        textViewSubject = binding.textViewSubject
        textViewTitle = binding.textViewTitle
        textViewDesc = binding.textViewDesc
        recyclerViewList = binding.recyclerViewList
        recyclerViewList?.layoutManager = LinearLayoutManager(this)

        textViewSubject?.text = SUBJECT_NAME
        textViewTitle?.text = TITLE
        textViewDesc?.text = DESCRIPTION

        initFinction()
        Utils.setStatusBarColor(this)

    }

    private fun initFinction() {
        studyInitViewModel.getMaterialList(STUDY_MATERIAL_ID)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(response.filesDetails.isNotEmpty()){
                                recyclerViewList?.visibility = View.VISIBLE
                                constraintLayoutNoFile?.visibility = View.GONE
                                recyclerViewList?.adapter = MaterialListAdapter(this@MaterialViewListActivity,this,response.filesDetails, this,TAG)

                            }else{
                                recyclerViewList?.visibility = View.GONE
                                constraintLayoutNoFile?.visibility = View.VISIBLE

                            }

                        }
                        Status.ERROR -> {
                            recyclerViewList?.visibility = View.GONE
                            constraintLayoutNoFile?.visibility = View.VISIBLE
                            Log.i(TAG, "Status.ERROR ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "Status.ERROR ${Status.LOADING}")

                        }
                    }
                }
            })


        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }
    }

    class MaterialListAdapter(
        var materialViewListActivity: MaterialViewListActivity,
        var fileClickListener: FileClickListener,
        var filesDetails: ArrayList<MaterialListModel.FilesDetail>,
        var context: Activity,
        var TAG : String
    )
        : RecyclerView.Adapter<MaterialListAdapter.ViewHolder>() {

        var downLoadPos = 0

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageViewFile: ImageView = view.findViewById(R.id.imageViewFile)
            var textViewFileName: TextView = view.findViewById(R.id.textViewFileName)

            var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
            var textViewPercentage: TextView = view.findViewById(R.id.textViewPercentage)
            var imageViewClose: ImageView = view.findViewById(R.id.imageViewClose)
            var imageViewDownloadButton: ImageView = view.findViewById(R.id.imageViewDownloadButton)

            var imageViewOpenButton: AppCompatButton = view.findViewById(R.id.imageViewOpenButton)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int): MaterialListAdapter.ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.material_list_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.textViewFileName.text = filesDetails[position].fILETITLE

            val file = File(Utils.getRootDirPath(context)!!)
            if (!file.exists()) {
                file.mkdirs()
            } else {
                Log.i(TAG, "Already existing")
            }

            val check = File(file.path +"/catch/Parent/Study/"+ filesDetails[position].fILENAME)
            Log.i(TAG,"check $check")
            if (!check.isFile) {
                holder.imageViewDownloadButton.visibility = View.VISIBLE
                holder.imageViewOpenButton.visibility = View.GONE
            } else {
                holder.imageViewDownloadButton.visibility = View.GONE
                holder.imageViewOpenButton.visibility = View.VISIBLE
            }


            val path: String = filesDetails[position].fILENAME
            val mFile = File(path)
            if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")) {
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //  .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_background_word)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewFile)
            } else if (mFile.toString().contains(".pdf") ||
                mFile.toString().contains(".PDF")) {
                // PDF file
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //     .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_background_pdf)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewFile)
            } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")) {
                // Powerpoint file
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_background_ppt)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewFile)
            } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
            ) {
                // Excel file
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //  .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_background_excel)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewFile)
            } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                || mFile.toString().contains(".png") || mFile.toString()
                    .contains(".JPG") || mFile.toString().contains(".JPEG")
                || mFile.toString().contains(".PNG")) {
                // JPG file
                Glide.with(context)
                    .load(Global.study_url + path)
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_background_image))
                    .thumbnail(0.5f)
                    .into(holder.imageViewFile)
            } else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                // Text file
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_background_text)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewFile)
            } else if (mFile.toString().contains(".mp3") || mFile.toString()
                    .contains(".wav") || mFile.toString().contains(".ogg")
                || mFile.toString().contains(".m4a") || mFile.toString()
                    .contains(".aac") || mFile.toString().contains(".wma") ||
                mFile.toString().contains(".MP3") || mFile.toString()
                    .contains(".WAV") || mFile.toString().contains(".OGG")
                || mFile.toString().contains(".M4A") || mFile.toString()
                    .contains(".AAC") || mFile.toString().contains(".WMA")
            ) {
               //voice file
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() // .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_background_voice)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewFile)
            } else {
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_background_video)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewFile)
            }


            holder.imageViewDownloadButton.setOnClickListener {
                if(materialViewListActivity.requestPermission()) {
                    holder.imageViewDownloadButton.visibility = View.GONE
                    holder.imageViewOpenButton.visibility = View.GONE
                    holder.progressBar.visibility = View.VISIBLE
                    holder.textViewPercentage.visibility = View.VISIBLE


                    if (StatusD.RUNNING === PRDownloader.getStatus(position)) {
                        PRDownloader.pause(position)
                    }

                    if (StatusD.PAUSED === PRDownloader.getStatus(position)) {
                        PRDownloader.resume(position)
                    }
                    downLoadPos = position
                    var fileUrl = Global.study_url + filesDetails[position].fILENAME
                    var fileLocation = Utils.getRootDirPath(context) +"/catch/parent/Study/"
                    var fileName = filesDetails[position].fILENAME
                    Log.i(TAG, "fileUrl $fileUrl")
                    Log.i(TAG, "fileLocation $fileLocation")
                    Log.i(TAG, "fileName $fileName")

                    downLoadPos = PRDownloader.download(
                        fileUrl, fileLocation, fileName
                    )
                        .build()
                        .setOnStartOrResumeListener {
                            holder.imageViewClose.visibility = View.VISIBLE
                        }
                        .setOnPauseListener {
                            holder.imageViewClose.visibility = View.VISIBLE
                        }
                        .setOnCancelListener {
                            holder.imageViewClose.visibility = View.VISIBLE
                            downLoadPos = 0
                            holder.progressBar.visibility = View.GONE
                            holder.textViewPercentage.visibility = View.GONE
                            holder.imageViewClose.visibility = View.GONE
                            PRDownloader.cancel(downLoadPos)

                        }
                        .setOnProgressListener { progress ->
                            val progressPercent: Long =
                                progress.currentBytes * 100 / progress.totalBytes
                            holder.textViewPercentage.text = Utils.getProgressDisplayLine(
                                progress.currentBytes,
                                progress.totalBytes
                            )
                        }
                        .start(object : OnDownloadListener {
                            override fun onDownloadComplete() {
                                holder.imageViewDownloadButton.visibility = View.GONE
                                holder.imageViewOpenButton.visibility = View.VISIBLE
                                holder.progressBar.visibility = View.GONE
                                holder.textViewPercentage.visibility = View.GONE
                                holder.imageViewClose.visibility = View.GONE
                            }

                            override fun onError(error: Error) {
                                Log.i(TAG, "Error $error")
                                Toast.makeText(
                                    context,
                                    "Some Error occured",
                                    Toast.LENGTH_SHORT
                                ).show()
                                holder.textViewPercentage.text = ""
                                holder.imageViewDownloadButton.visibility = View.VISIBLE
                                holder.imageViewOpenButton.visibility = View.GONE
                                holder.progressBar.visibility = View.GONE
                                holder.textViewPercentage.visibility = View.GONE
                                holder.imageViewClose.visibility = View.GONE
                            }
                        })


                }

            }

            holder.imageViewClose.setOnClickListener {
                holder.imageViewDownloadButton.visibility = View.VISIBLE
                holder.imageViewOpenButton.visibility = View.GONE
                holder.progressBar.visibility =  View.GONE
                holder.textViewPercentage.visibility =  View.GONE
                holder.imageViewClose.visibility =  View.GONE
                PRDownloader.cancel(downLoadPos)
            }

            holder.imageViewOpenButton.setOnClickListener {
                if(materialViewListActivity.requestPermission()) {
                    fileClickListener.onViewFileClick(filesDetails[position].fILENAME)
                }
            }

        }

        override fun getItemCount(): Int {
            return  filesDetails.size
        }

    }


    fun requestPermission() : Boolean {

        var hasReadPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        }else {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }


        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermission = hasReadPermission
        writePermission = hasWritePermission || minSdk29

        val permissions = readPermission && writePermission


        val permissionToRequests = mutableListOf<String>()
        if (!writePermission) {
            permissionToRequests.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!readPermission) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_IMAGES)
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_VIDEO)
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_AUDIO)
            }else {
                permissionToRequests.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionToRequests.isNotEmpty()) {
            permissionsLauncher.launch(permissionToRequests.toTypedArray())
        }

        return permissions
    }

    override fun onViewFileClick(uriPath : String) {

        // Create URI
        var dwload =
            File(Utils.getRootDirPath(this)!!+"/catch/parent/Study/")

        if (!dwload.exists()) {
            dwload.mkdirs()
        } else {
            //  Toast.makeText(getActivity(), "Already existing", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Already existing")
        }

        val mFile = File("$dwload/$uriPath")
        var pdfURI = FileProvider.getUriForFile(this, this.packageName + ".provider", mFile)
        Log.i(TAG, "file $mFile")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = pdfURI
        try {
            if (mFile.toString().contains(".doc") || mFile.toString().contains(".DOC")) {
                // Word document
                intent.setDataAndType(pdfURI, "application/msword")
            } else if (mFile.toString().contains(".pdf") || mFile.toString().contains(".PDF")) {
                // PDF file
                intent.setDataAndType(pdfURI, "application/pdf")
            } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")
            ) {
                // Powerpoint file
                intent.setDataAndType(pdfURI, "application/vnd.ms-powerpoint")
            } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
            ) {
                // Excel file
                intent.setDataAndType(pdfURI, "application/vnd.ms-excel")
            } else if (mFile.toString().contains(".docx") || mFile.toString().contains(".DOCX")) {
                // docx file
                intent.setDataAndType(
                    pdfURI,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                || mFile.toString().contains(".png") || mFile.toString()
                    .contains(".JPG") || mFile.toString().contains(".JPEG")
                || mFile.toString().contains(".PNG")
            ) {
                // JPG file
                intent.setDataAndType(pdfURI, "image/*")
            } else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                // Text file
                intent.setDataAndType(pdfURI, "text/plain")
            } else if (mFile.toString().contains(".mp3") || mFile.toString()
                    .contains(".wav") || mFile.toString().contains(".ogg")
                || mFile.toString().contains(".m4a") || mFile.toString()
                    .contains(".aac") || mFile.toString().contains(".wma") ||
                mFile.toString().contains(".MP3") || mFile.toString()
                    .contains(".WAV") || mFile.toString().contains(".OGG")
                || mFile.toString().contains(".M4A") || mFile.toString()
                    .contains(".AAC") || mFile.toString().contains(".WMA")
            ) {
                intent.setDataAndType(pdfURI, "audio/*")

            } else if (mFile.toString().contains(".MP4") || mFile.toString().contains(".MOV")
                || mFile.toString().contains(".WMV") || mFile.toString()
                    .contains(".AVI") || mFile.toString().contains(".AVCHD") || mFile.toString()
                    .contains(".FLV")
                || mFile.toString().contains(".F4V")
                || mFile.toString().contains(".SWF")
                || mFile.toString().contains(".WEBM")
                || mFile.toString().contains(".HTML5")
                || mFile.toString().contains(".MKV")
            ) {
                // JPG file
                intent.setDataAndType(pdfURI, "video/*")
            }
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(intent)
        }catch(e : Exception){
            Log.i(TAG, "exception $e");
            Utils.getSnackBar4K(this@MaterialViewListActivity,"File format doesn't support",constraintLayout!!)
        }

    }
}


interface FileClickListener{
    fun onViewFileClick(uriPath : String)
}