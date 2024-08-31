package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.progressindicator.CircularProgressIndicator
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityStudyMaterialDetailsBinding
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader.FileUploaderCallback
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.downloader.Error
import info.passdaily.camrelconvertapp.services.downloader.OnDownloadListener
import info.passdaily.camrelconvertapp.services.downloader.PRDownloader
import info.passdaily.camrelconvertapp.services.downloader.StatusD
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.permission.sdk29AndUp
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File


@Suppress("DEPRECATION")
class StudyMaterialDetailsActivity : AppCompatActivity(),MaterialDetailsListener{

    var TAG = "StudyMaterialDetailsActivity"
    var toolbar: Toolbar? = null
    private lateinit var binding: ActivityStudyMaterialDetailsBinding

    //  lateinit var materialListener: MaterialListener

    private lateinit var studyMaterialStaffViewModel: StudyMaterialStaffViewModel
    var STUDY_MATERIAL_ID = 0
    var textViewTitle: TextView? = null
    var textViewSubject: TextView? = null
    var textViewClass: TextView? = null
    var textViewDate: TextView? = null
    var textViewDesc: ShowMoreTextView? = null
    var recyclerViewList: RecyclerView? = null

    var shimmerViewContainer: ShimmerFrameLayout? = null
    var textViewNoFilesStudent: TextView? = null
    var constraintButtonField: ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var constraintLayout: ConstraintLayout? = null

    var SERVER_URL = "StudyMaterial/UploadFiles"

    var getMaterialList = ArrayList<StudyMaterialDetailModel.File>()

    lateinit var studyMaterialAdapter : StudyMaterialAdapter

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var readPermission = false
    private var writePermission = false
    var maxCount = 10
    var maxCountSelection = 10

    var noDeleteImage = 0
    var fileNameList = ArrayList<FileList>()
    var jsonArrayList = ArrayList<FileList>()

    var pb: ProgressDialog? = null

    var constraintLayoutCamera : ConstraintLayout? = null
    var constraintLayoutAudio : ConstraintLayout? = null
    var constraintLayoutPDF : ConstraintLayout? = null
    var constraintLayoutVideo : ConstraintLayout? = null

    var buttonTakeTest : AppCompatButton? = null

    var textViewSelect : TextView? = null
    var constraintLayoutUpload  : ConstraintLayout? = null

    var dummyFileName = ArrayList<String>()

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var adminRole = 0
//    lateinit var studyMaterialStaffFragment: StudyMaterialStaffFragment


//    var mContext : Context? = null
//    fun onAttach(context: Context) {
//        if(mContext ==null){
//            mContext = context.applicationContext
//        }
//        try {
//            //toolBarClickListener = context as ToolBarClickListener
//            materialListener  = context as MaterialListener
//        }catch(e : Exception){
//            Log.i(TAG,"Exception $e")
//        }
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//       studyMaterialStaffFragment =
//            supportFragmentManager.fragments[0] as StudyMaterialStaffFragment

        localDBHelper = LocalDBHelper(this)
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE

        val extras = intent.extras
        if (extras != null) {
            STUDY_MATERIAL_ID = extras.getInt("STUDY_MATERIAL_ID")
        }
        studyMaterialStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StudyMaterialStaffViewModel::class.java]

        binding = ActivityStudyMaterialDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pb = ProgressDialog(this)
//        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
//        pb?.setMessage("Loading...")
//        pb?.isIndeterminate = true
        pb?.setCancelable(false)

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Study Material Details"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //   toolbar.setTitle("About-Us");
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }

        textViewTitle = binding.textViewTitle
        textViewSubject = binding.textViewSubject
        textViewClass = binding.textViewClass
        textViewDate = binding.textViewDate
        textViewDesc = binding.textViewDesc
        recyclerViewList = binding.recyclerViewList
        // recyclerViewList?.layoutManager = GridLayoutManager(this,3)
        recyclerViewList?.layoutManager = LinearLayoutManager(this)
        textViewNoFilesStudent = binding.textViewNoFilesStudent
        constraintLayout = binding.constraintLayout
        constraintButtonField = binding.constraintButtonField
        shimmerViewContainer = binding.shimmerViewContainer
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        buttonTakeTest= binding.buttonTakeTest

        constraintLayoutCamera = binding.constraintLayoutCamera
        constraintLayoutAudio = binding.constraintLayoutAudio
        constraintLayoutPDF = binding.constraintLayoutPDF
        constraintLayoutVideo = binding.constraintLayoutVideo

        textViewSelect = binding.textViewSelect
        constraintLayoutUpload = binding.constraintLayoutUpload

        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        //StudyMaterial/StudyMaterialView?MaterialId=

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }

        constraintLayoutCamera?.setOnClickListener {
            if (requestPermission()) {
                if (fileNameList.size < maxCount) {
                    maxCountSelection = maxCount - fileNameList.size
                    Toast.makeText(this, "Select $maxCountSelection ", Toast.LENGTH_SHORT).show()

                    val imageCollection = sdk29AndUp {
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val intent = Intent(Intent.ACTION_PICK, imageCollection)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.type = "image/*"
                    startForResult.launch(intent)
                } else {
                    Utils.getSnackBar4K(this, "Maximum Count Reached", constraintLayout!!)
                }
            }
        }

        constraintLayoutAudio?.setOnClickListener {
            if (requestPermission()) {
                mimeTypeFun("audio/*",Intent.ACTION_GET_CONTENT)
            }
        }

        constraintLayoutVideo?.setOnClickListener {
            if (requestPermission()) {
                mimeTypeFun("video/*",Intent.ACTION_PICK)
            }
        }
        constraintLayoutPDF?.setOnClickListener {
            if (requestPermission()) {
                mimeTypeFun("application/pdf",Intent.ACTION_OPEN_DOCUMENT)
            }
        }

        buttonTakeTest?.setOnClickListener {
            //  fileNames = ArrayList<String>()
            if ((fileNameList.size + jsonArrayList.size) != 0) {

                //  uploadFiles(fileNameList)
                //    progressStart();
//                    pb = ProgressDialog.show(this, "", "Uploading...", true)
//                    Thread {
                //creating new thread to handle Http Operations
//                        try {
//                            pb!!.show()
//                            for (i in fileNameList.indices) {
//                                if(fileNameList[i].fILETYPE == "Local"){
//                                    onFileUploadClick(i, fileNameList[i])
//                                   // SystemClock.sleep(3000)
//                                }
//                            }
//                            //Your code goes here
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                            Log.i(TAG, "Exception  ${e.printStackTrace()}")
//                        }
//                    }.start()


            } else {
                Utils.getSnackBar4K(this, "Select atleast one file", constraintLayout!!)
            }
        }


        getMaterialListStaff(STUDY_MATERIAL_ID)

        Utils.setStatusBarColor(this)
    }


    fun mimeTypeFun(mimeTypes: String, actionPick: String) {
        if (fileNameList.size < maxCount) {
            maxCountSelection = maxCount - fileNameList.size
            Toast.makeText(this, "Select $maxCountSelection ", Toast.LENGTH_SHORT).show()

            val intent =
                Intent(actionPick); // or ACTION_OPEN_DOCUMENT //ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = mimeTypes;
            startForResult.launch(intent)
        } else {
            Utils.getSnackBar4K(this, "Maximum Count Reached", constraintLayout!!)
        }
    }

    private fun getMaterialListStaff(studyMaterialId: Int) {
        fileNameList = ArrayList<FileList>()
        jsonArrayList = ArrayList<FileList>()
        studyMaterialStaffViewModel.getStudyMaterialDetails(studyMaterialId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            constraintLayout?.visibility = View.VISIBLE
                            val response = resource.data?.body()!!
                            getMaterialList = response.fileList as ArrayList<StudyMaterialDetailModel.File>
                            var materialDetails = response.materialDetails
                            for(i in materialDetails.indices){
                                textViewTitle?.text = materialDetails[i].sTUDYMETERIALTITLE
                                textViewClass?.text = materialDetails[i].cLASSNAME
                                textViewSubject?.text = materialDetails[i].sUBJECTNAME
                                if (!materialDetails[i].sTUDYMETERIALDATE.isNullOrBlank()) {
                                    val date1: Array<String> =
                                        materialDetails[i].sTUDYMETERIALDATE.split("T".toRegex()).toTypedArray()
                                    val ddddd: Long = Utils.longconversion(date1[0] + " " + date1[1])
                                    textViewDate?.text = Utils.formattedDateTime(ddddd)
                                }
                                textViewDesc?.text = materialDetails[i].sTUDYMETERIALDESCRIPTION
                                textViewDesc?.apply {
                                    setShowingLine(5)
                                    setShowMoreTextColor(resources.getColor(R.color.green_300))
                                    setShowLessTextColor(resources.getColor(R.color.green_300))
                                }
                            }

                            if (getMaterialList.isNotEmpty()) {
                                noDeleteImage = 2
                                textViewNoFilesStudent?.visibility = View.GONE
                                recyclerViewList?.visibility = View.VISIBLE
                                for (i in getMaterialList.indices) {
                                    jsonArrayList.add(
                                        FileList(
                                            getMaterialList[i].fILEID,
                                            getMaterialList[i].fILENAME,
                                            getMaterialList[i].fILETITLE,
                                            "Json",
                                            noDeleteImage
                                        )
                                    )
                                }
                                fileNameList.addAll(jsonArrayList)
                                if(fileNameList.size >= 10){
                                    constraintButtonField?.visibility = View.GONE
                                    textViewSelect?.visibility = View.GONE
                                    constraintLayoutUpload?.visibility = View.GONE
                                }else{
                                    constraintButtonField?.visibility = View.VISIBLE
                                    textViewSelect?.visibility = View.VISIBLE
                                    constraintLayoutUpload?.visibility = View.VISIBLE
                                }
                                studyMaterialAdapter = StudyMaterialAdapter(
                                    this@StudyMaterialDetailsActivity,
                                    this,
                                    fileNameList,
                                    this,
                                    TAG
                                )
                                recyclerViewList!!.adapter = studyMaterialAdapter

                            } else {
                                constraintButtonField?.visibility = View.VISIBLE
                                textViewSelect?.visibility = View.VISIBLE
                                constraintLayoutUpload?.visibility = View.VISIBLE
                                textViewNoFilesStudent?.visibility = View.VISIBLE
                                recyclerViewList?.visibility = View.GONE
                            }

                            Log.i(TAG, "getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {

                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewList?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text = resources.getString(R.string.no_internet)
                            Log.i(TAG, "getSubjectList ERROR")
                        }
                        Status.LOADING -> {

                            recyclerViewList?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getMaterialList = ArrayList<StudyMaterialDetailModel.File>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)

                            textEmpty?.text = resources.getString(R.string.loading)
                            Log.i(TAG, "getSubjectList LOADING")
                        }
                    }
                }
            })

    }
    /////

    class StudyMaterialAdapter(
        var studyMaterialDetailsActivity : StudyMaterialDetailsActivity,
        var materialDetailsListener: MaterialDetailsListener,
        var materialList: ArrayList<FileList>,
        var context: Context, var TAG: String
    ) : RecyclerView.Adapter<StudyMaterialAdapter.ViewHolder>() {
        var downLoadPos = 0
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView = view.findViewById(R.id.imageView)
            var imageViewOther: ImageView = view.findViewById(R.id.imageViewOther)
            var imageViewDelete: ImageView = view.findViewById(R.id.imageViewDelete)

            var imageViewDownloadButton : ImageView = view.findViewById(R.id.imageViewDownloadButton)

            var constraintDownload  : ConstraintLayout = view.findViewById(R.id.constraintDownload)
            var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
            var imageViewClose : ImageView = view.findViewById(R.id.imageViewClose)
            var textViewPercentage  : TextView = view.findViewById(R.id.textViewPercentage)

            var textViewProgress   : TextView = view.findViewById(R.id.textViewProgress)

            var perProgressBar : CircularProgressIndicator = view.findViewById(R.id.perProgressBar)

            var textViewTitle : TextView = view.findViewById(R.id.textViewTitle)

            var constraintText : ConstraintLayout = view.findViewById(R.id.constraintText)

        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.study_material_progress_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.textViewFileName.text = materialList[position].fILETITLE

            if(materialList[position].fILETITLE.isNotEmpty()){
                holder.textViewTitle.text = materialList[position].fILETITLE
            }else{
                holder.textViewTitle.text ="Enter File Name"
            }

            if (materialList[position].fILETYPE == "Json") {
                val path: String = materialList[position].fILENAME
                val mFile = File(path)
                holder.imageViewDownloadButton.visibility  =  View.GONE
                holder.perProgressBar.visibility  =  View.GONE
                holder.textViewProgress.visibility  =  View.GONE


                if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                    || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")
                ) {
                    // Word document
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //  .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_word)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".pdf") ||
                    mFile.toString().contains(".PDF")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // PDF file
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //     .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_pdf)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                    || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")
                ) {
                    // Powerpoint file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_power_point)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                    || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // Excel file
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //  .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_excel)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                    || mFile.toString().contains(".png") || mFile.toString()
                        .contains(".JPG") || mFile.toString().contains(".JPEG")
                    || mFile.toString().contains(".PNG")
                ) {

                    // JPG file
                    holder.imageViewOther.visibility = View.GONE
                    holder.imageView.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(Global.event_url + "/StudyMeterials/" + path)
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_gallery)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageView)
                } else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                    // Text file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_text)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".mp3") || mFile.toString()
                        .contains(".wav") || mFile.toString().contains(".ogg")
                    || mFile.toString().contains(".m4a") || mFile.toString()
                        .contains(".aac") || mFile.toString().contains(".wma") ||
                    mFile.toString().contains(".MP3") || mFile.toString()
                        .contains(".WAV") || mFile.toString().contains(".OGG")
                    || mFile.toString().contains(".M4A") || mFile.toString()
                        .contains(".AAC") || mFile.toString().contains(".WMA")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() // .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_voice)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        // .load(java.io.File(path))
                        .load(Global.event_url + "/StudyMeterials/" + path)
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_video_library)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                }
            }
            else if (materialList[position].fILETYPE == "Local") {
                val path: String = materialList[position].fILENAME
                Log.i(TAG, "path $path")
                val mFile = FileUtils.getReadablePathFromUri(context, path.toUri())


//                val index: Int = mFile!!.lastIndexOf('/')
//                val lastString: String = mFile.substring(index + 1)
//                Log.i(TAG,"lastString $lastString")
//
//


                Log.i(TAG, "mFile $mFile")
//                holder.imageViewDownloadButton.visibility  =  View.VISIBLE
//                holder.perProgressBar.visibility  =  View.GONE
//                holder.textViewProgress.visibility  =  View.GONE

                materialList[position].fILETYPE = "Uploaded"
                materialDetailsListener.onFileUploadProgress(position,mFile!!,holder.textViewTitle.text.toString(),
                    holder.perProgressBar, holder.imageViewDownloadButton,holder.textViewProgress)
                holder.imageViewDownloadButton.visibility  =  View.GONE
                holder.perProgressBar.visibility  =  View.VISIBLE
                holder.textViewProgress.visibility  =  View.VISIBLE//,materialList[position].fILENAME


                if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                    || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")
                ) {

                    // Word document
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_word)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".pdf") || mFile.toString().contains(".PDF")) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // PDF file

                    Glide.with(context)
                        .load(R.drawable.ic_file_pdf)
                        .into(holder.imageViewOther)

                } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                    || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")
                ) {
                    // Powerpoint file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_power_point)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                    || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // Excel file
                    Glide.with(context)
                        .load(R.drawable.ic_file_excel)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                    || mFile.toString().contains(".png") || mFile.toString()
                        .contains(".JPG") || mFile.toString().contains(".JPEG")
                    || mFile.toString().contains(".PNG")
                ) {
                    // JPG file
                    holder.imageViewOther.visibility = View.GONE
                    holder.imageView.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(materialList[position].fILENAME)
                        .into(holder.imageView)
                } else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                    // Text file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_text)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".mp3") || mFile.toString()
                        .contains(".wav") || mFile.toString().contains(".ogg")
                    || mFile.toString().contains(".m4a") || mFile.toString()
                        .contains(".aac") || mFile.toString().contains(".wma") ||
                    mFile.toString().contains(".MP3") || mFile.toString()
                        .contains(".WAV") || mFile.toString().contains(".OGG")
                    || mFile.toString().contains(".M4A") || mFile.toString()
                        .contains(".AAC") || mFile.toString().contains(".WMA") ||
                    mFile.toString().contains(".opus")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_voice)
                        .into(holder.imageViewOther)
                }
                else {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
//                    Glide.with(context)
//                        .load(R.drawable.ic_file_video)
//                        .into(holder.imageViewOther)

                    try {
                        val thumb = ThumbnailUtils.createVideoThumbnail(
                            mFile!!,
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )
                        holder.imageViewOther.setImageBitmap(thumb)
                    } catch (e: java.lang.Exception) {
                        Log.i("TAG", "Exception $e")
                    }
                }

            }
            else  if (materialList[position].fILETYPE == "Uploaded") {


                holder.textViewProgress.visibility  =  View.VISIBLE
                holder.textViewProgress.text = "Uploaded"
                holder.imageViewDownloadButton.visibility  =  View.GONE
                holder.perProgressBar.visibility  =  View.GONE

                val path: String = materialList[position].fILENAME
                val mFile = java.io.File(path)
                if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                    || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")
                ) {
                    // Word document
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //  .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_word)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".pdf") ||
                    mFile.toString().contains(".PDF")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // PDF file
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //     .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_pdf)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                    || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")
                ) {
                    // Powerpoint file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_power_point)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                    || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // Excel file
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //  .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_excel)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                    || mFile.toString().contains(".png") || mFile.toString()
                        .contains(".JPG") || mFile.toString().contains(".JPEG")
                    || mFile.toString().contains(".PNG")
                ) {

                    // JPG file
                    holder.imageViewOther.visibility = View.GONE
                    holder.imageView.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(Global.event_url + "/StudyMeterials/" + path)
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_gallery)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageView)
                } else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                    // Text file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_text)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".mp3") || mFile.toString()
                        .contains(".wav") || mFile.toString().contains(".ogg")
                    || mFile.toString().contains(".m4a") || mFile.toString()
                        .contains(".aac") || mFile.toString().contains(".wma") ||
                    mFile.toString().contains(".MP3") || mFile.toString()
                        .contains(".WAV") || mFile.toString().contains(".OGG")
                    || mFile.toString().contains(".M4A") || mFile.toString()
                        .contains(".AAC") || mFile.toString().contains(".WMA")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() // .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_voice)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else {
//                    holder.imageViewOther.visibility = View.VISIBLE
//                    holder.imageView.visibility = View.GONE
//                    Glide.with(context)
//                        .load(java.io.File(path))
//                        .apply(
//                            RequestOptions.centerCropTransform()
//                                .dontAnimate() //   .override(imageSize, imageSize)
//                                .placeholder(R.drawable.ic_file_video)
//                        )
//                        .thumbnail(0.5f)
//                        .into(holder.imageViewOther)
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        // .load(java.io.File(path))
                        .load(Global.event_url + "/StudyMeterials/" + path)
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_video_library)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                }
            }

            holder.imageViewDownloadButton.setOnClickListener {
                val path: String = materialList[position].fILENAME
                Log.i(TAG, "path $path")
                val mFile = FileUtils.getReadablePathFromUri(context, path.toUri())
                Log.i(TAG, "mFile $mFile")
                holder.imageViewDownloadButton.visibility  =  View.GONE
                holder.perProgressBar.visibility  =  View.VISIBLE
                holder.textViewProgress.visibility  =  View.VISIBLE//,materialList[position].fILENAME
                materialDetailsListener.onFileUploadProgress(position,mFile!!,holder.textViewTitle.text.toString(),
                    holder.perProgressBar, holder.imageViewDownloadButton,holder.textViewProgress)
            }




            holder.constraintText.setOnClickListener {
                val inflate: View = LayoutInflater.from(context)
                    .inflate(R.layout.user_input_dialog_box, null as ViewGroup?)
                val builder = AlertDialog.Builder(context)
                builder.setView(inflate)
                val editText = inflate.findViewById<View>(R.id.userInputDialog) as EditText
                if(materialList[position].fILETITLE.isNotEmpty() && holder.textViewTitle.text.toString() != "Enter File Name"){
                    editText.setText(holder.textViewTitle.text.toString())
                }

                builder.setCancelable(false).setPositiveButton(
                    "Submit"
                ) { dialogInterface, _ ->
                    materialList[position].fILETITLE = editText.text.toString()
                    holder.textViewTitle.text = materialList[position].fILETITLE
                    //  materialDetailsListener.submitFile(materialList[position].fILENAME,materialList[position].fILETITLE,position)
                    dialogInterface.cancel()
                }.setNegativeButton(
                    "Cancel"
                ) { dialogInterface, _ -> dialogInterface.cancel() }

                //Creating dialog box
                val alert = builder.create()
                //Setting the title manually
                alert.setTitle("File Name")
                alert.show()
                val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                buttonbackground.setTextColor(Color.BLACK)

                val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                buttonbackground1.setTextColor(Color.BLACK)

            }

//            holder.imageView.setOnClickListener {
//                Global.albumImageList = ArrayList<CustomImageModel>()
//
//                if (materialList[position].fILETYPE == "Json") {
//                    Global.albumImageList.add(
//                        CustomImageModel(
//                            Global.event_url+"/StudyMeterials/"
//                            +materialList[position].fILENAME)
//                    )
//                }else if (materialList[position].fILETYPE == "Local"){
//                    Global.albumImageList.add(CustomImageModel(materialList[position].fILENAME))
//                }
//                materialDetailsListener.onViewClick(materialList[position].fILENAME)
//            }

//            if (materialList[position].fILEDELETE == 2) {
//                holder.imageViewDelete.visibility = View.GONE
//            } else {
//                holder.imageViewDelete.visibility = View.VISIBLE
//            }
            holder.imageViewDelete.setBackgroundResource(R.drawable.ic_more_icon)
            holder.imageViewDelete.setOnClickListener {
                val popupMenu = PopupMenu(context, holder.imageViewDelete)
                popupMenu.inflate(R.menu.video_adapter_menu)
                popupMenu.menu.findItem(R.id.menu_edit).icon = context.resources.getDrawable(R.drawable.ic_icon_edit)
                popupMenu.menu.findItem(R.id.menu_report).icon = context.resources.getDrawable(R.drawable.ic_icon_about_gray)
                popupMenu.menu.findItem(R.id.menu_video).icon = context.resources.getDrawable(R.drawable.ic_icon_close)
                popupMenu.menu.findItem(R.id.menu_offline_video).icon = context.resources.getDrawable(R.drawable.ic_icon_delete_gray)
                popupMenu.menu.findItem(R.id.menu_download).icon = context.resources.getDrawable(R.drawable.ic_upload_icon)
                popupMenu.menu.findItem(R.id.menu_open).icon = context.resources.getDrawable(R.drawable.ic_upload_icon)

                val file = File(Utils.getRootDirPath(context)!!)
                if (!file.exists()) {
                    file.mkdirs()
                } else {
                    Log.i(TAG, "Already existing")
                }
                val check = File(file.path +"/catch/staff/Study/"+ materialList[position].fILENAME)
                if (!check.isFile) {
                    popupMenu.menu.findItem(R.id.menu_download).isVisible = true
                    popupMenu.menu.findItem(R.id.menu_open).isVisible = false
                }else{
                    popupMenu.menu.findItem(R.id.menu_download).isVisible = false
                    popupMenu.menu.findItem(R.id.menu_open).isVisible = true
                }

                if(materialList[position].fILETYPE == "Local"){
                    popupMenu.menu.findItem(R.id.menu_download).isVisible = false
                }

                popupMenu.menu.findItem(R.id.menu_edit).isVisible = false
                popupMenu.menu.findItem(R.id.menu_report).isVisible = false
                popupMenu.menu.findItem(R.id.menu_offline_video).isVisible = false
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_download -> {
//                            materialDetailsListener.onUpdateClickListener(materialList[position])
                            if(studyMaterialDetailsActivity.requestPermission()) {
                                holder.constraintDownload.visibility = View.VISIBLE
                                holder.textViewPercentage.visibility = View.VISIBLE
                                if (StatusD.RUNNING === PRDownloader.getStatus(position)) {
                                    PRDownloader.pause(position)
                                }

                                if (StatusD.PAUSED === PRDownloader.getStatus(position)) {
                                    PRDownloader.resume(position)
                                }
                                downLoadPos = position
                                var fileUrl = Global.event_url+"/StudyMeterials/" + materialList[position].fILENAME
                                var fileLocation = Utils.getRootDirPath(context) +"/catch/staff/Study/"
                                var fileName = materialList[position].fILENAME
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
                                        //    holder.constraintDownload.visibility = View.GONE
                                        holder.textViewPercentage.text ="Loading... "
                                        //  holder.imageViewClose.visibility = View.GONE
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
                                            holder.constraintDownload.visibility = View.GONE
                                            holder.textViewPercentage.visibility = View.GONE
                                        }

                                        override fun onError(error: Error) {
                                            Log.i(TAG, "Error $error")
                                            Toast.makeText(
                                                context,
                                                "Some Error occured",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            holder.constraintDownload.visibility = View.GONE
                                            holder.textViewPercentage.visibility = View.GONE
                                        }
                                    })

                            }
                            true
                        }
                        R.id.menu_open -> {
                            if(studyMaterialDetailsActivity.requestPermission()) {
                                materialDetailsListener.onViewClick(materialList[position].fILENAME)
                            }
                            true
                        }
                        R.id.menu_video -> {
                            Log.i(TAG,"materialList ${materialList[position].fILENAME}")
                            Log.i(TAG,"materialList ${materialList[position].fILEID}")
                            materialDetailsListener.onDeleteClick(position, materialList[position])
                            true
                        }
                        else -> false
                    }

                }
                popupMenu.show()

            }
            holder.imageViewClose.setOnClickListener {
                holder.constraintDownload.visibility = View.GONE
                holder.textViewPercentage.visibility = View.GONE
                PRDownloader.cancel(downLoadPos)
            }
        }

        override fun getItemCount(): Int {
            return materialList.size
        }

    }

    ///permission Part
    var startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
        noDeleteImage = 1
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val data: Intent? = it.data
            Log.i(TAG, "data $data")

            //If multiple image selected
            if (data?.clipData != null) {
                val count = data.clipData?.itemCount ?: 0

                val countPath = count + fileNameList.size
                if (countPath > 10) {
                    Toast.makeText(this, "You select more then $maxCount", Toast.LENGTH_SHORT)
                        .show()
                } else {
//                        fileNameList.addAll(jsonArrayList)
                    for (i in 0 until count) {
                        val imageUri: Uri? = data.clipData?.getItemAt(i)?.uri
                        dummyFileName.add(imageUri!!.toString())
                        fileNameList.add(
                            FileList(
                                0,
                                imageUri.toString(),
                                "",
                                "Local",
                                noDeleteImage
                            )
                        )
                    }
                }
                //     imageAdapter.addSelectedImages(selectedPaths)
            }

            //If single image selected
            else if (data?.data != null) {
                val imageUri: Uri? = data.data
                dummyFileName.add(imageUri!!.toString())
                fileNameList.add(
                    FileList(
                        0,
                        imageUri.toString(),
                        "",
                        "Local",
                        noDeleteImage
                    )
                )
            }
            if(fileNameList.size == 10){
                textViewSelect?.visibility = View.GONE
                constraintLayoutUpload?.visibility = View.GONE
            }else{
                textViewSelect?.visibility = View.VISIBLE
                constraintLayoutUpload?.visibility = View.VISIBLE
            }
            textViewNoFilesStudent?.visibility = View.GONE
            recyclerViewList?.visibility = View.VISIBLE
            studyMaterialAdapter = StudyMaterialAdapter(
                this@StudyMaterialDetailsActivity,
                this,
                fileNameList,
                this@StudyMaterialDetailsActivity,
                TAG
            )
            recyclerViewList?.adapter = studyMaterialAdapter

        }

    }

    fun requestPermission(): Boolean {

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

    @SuppressLint("NotifyDataSetChanged")
    override fun onDeleteClick(position: Int, fileList: FileList) {
        if (fileList.fILETYPE == "Json") {
            fileNameList.removeAt(position)
            jsonArrayList.removeAt(position)
            //OnlineExam/AssignmentFileDelete?AssignmentFileId=
            deleteStudyFile("StudyMaterial/FileDrop",fileList.fILEID,fileList.fILENAME)
        } else {
            fileNameList.removeAt(position)
        }
        studyMaterialAdapter.notifyDataSetChanged()
        constraintButtonField?.visibility = View.VISIBLE
        textViewSelect?.visibility = View.VISIBLE
        constraintLayoutUpload?.visibility = View.VISIBLE
        if (fileNameList.size == 0) {
            textViewNoFilesStudent?.visibility = View.VISIBLE
            recyclerViewList?.visibility = View.GONE

        }
    }



    fun deleteStudyFile(url: String,fILEID : Int,fILENAME: String) {
        //FileId
//        val paramsMap: MutableMap<String, Int> = HashMap()
//        paramsMap["FileId"] = fILEID
//        paramsMap["FileName"] = fILENAME
        //FileName
        studyMaterialStaffViewModel.getDeleteFiles(url,fILEID,fILENAME)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "1" -> {
                                    Log.i(TAG,"Deleted Successfully")
                                    //     Utils.getSnackBarGreen(this, "Deleted Successfully", constraintLayout!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"Please try again after sometime")
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }



    @SuppressLint("NewApi")
    override fun onFileUploadClick(position: Int, fileList: FileList) {
//
//
//        var selectedFilePath =
//            FileUtils.getReadablePathFromUri(this, fileList.fILENAME.toUri())
//        // this activity implements the ImageUploadCallback
//        val file = File(selectedFilePath!!)
//        val fileBody = ProgressRequestBody(file, "image", this)
///* Notice here the first argument in the createFormData function is the name of the key whose value will be the file you send */
//        val filePart = MultipartBody.Part.createFormData("STUDY_METERIAL_FILE", file.name, fileBody)
//        val apiInterface = RetrofitClientStaff.create().fileUploadAssignment(
//            SERVER_URL,
//            filePart
//        )
//        apiInterface.enqueue(object : Callback<FileResultModel?> {
//            override fun onResponse(
//                @NonNull call: Call<FileResultModel?>,
//                @NonNull response: Response<FileResultModel?>
//            ) {
//                onSuccess("Uploaded successfully")
//            }
//
//            override fun onFailure(@NonNull call: Call<FileResultModel?>, @NonNull t: Throwable) {
//                onError("Failed to upload image")
//            }
//        })
//
////        //pb!!.setMessage("Uploading Position  : ${position + 1} ")
////        pb!!.setMessage("Uploading : ${position + 1} / ${fileNameList.size}")
////      //  if (fileList.fILETYPE == "Local") {
////
////            var selectedFilePath =
////                FileUtils.getReadablePathFromUri(this, fileList.fILENAME.toUri())
////            Log.i(TAG, "position  $position")
////            val i = Log.i(TAG, "fileName  $selectedFilePath")
////
////            var imagenPerfil = if (selectedFilePath.toString().contains(".jpg") || selectedFilePath.toString()
////                    .contains(".jpeg")
////                || selectedFilePath.toString().contains(".png") || selectedFilePath.toString()
////                    .contains(".JPG") || selectedFilePath.toString().contains(".JPEG")
////                || selectedFilePath.toString().contains(".PNG")
////            ) {
////                val file1 = File(selectedFilePath!!)
//                val requestFile: RequestBody =
//                    file1.asRequestBody("image/*".toMediaTypeOrNull())
////                MultipartBody.Part.createFormData(
////                    "STUDY_METERIAL_FILE",
////                    file1.name,
////                    requestFile
////                )
////            } else {
////                val requestFile: RequestBody = RequestBody.create(
////                    "multipart/form-data".toMediaTypeOrNull(),
////                    selectedFilePath!!
////                )
////                // MultipartBody.Part is used to send also the actual file name
////                MultipartBody.Part.createFormData(
////                    "STUDY_METERIAL_FILE",
////                    selectedFilePath,
////                    requestFile
////                )
////            }
////
////
////            val apiInterface = RetrofitClientStaff.create().fileUploadAssignment(
////                SERVER_URL,
////                imagenPerfil
////            )
////            apiInterface.enqueue(object : Callback<FileResultModel> {
////                override fun onResponse(
////                    call: Call<FileResultModel>,
////                    resource: Response<FileResultModel>
////                ) {
////                    val response = resource.body()
////                    if (resource.isSuccessful) {
////                        Log.i(TAG, "response  $response")
////                      //  fileNames.add(response!!.dETAILS)
////                        submitFile(response?.dETAILS!!,position)
////                    }
//////                    if ((position + 1) == fileNameList.size) {
//////                        pb?.dismiss()
//////                        val arraylist = java.lang.String.join(",", fileNames)
//////                        Log.i(TAG, "str  $arraylist")
//////                        submitFile(arraylist)
//////                    }
////
////                }
////
////                override fun onFailure(call: Call<FileResultModel>, t: Throwable) {
////                    Log.i(TAG, "Throwable  $t")
////                }
////            })
//
//
    }


    override fun onFileUploadProgress(
        position: Int,
        fILEPATHName : String,
//        fILENAME: String,
        fILETITLE: String,
        perProgressBar: CircularProgressIndicator,
        imageViewDownloadButton: ImageView,
        textViewProgress: TextView
    ) {
        Log.i(TAG, "fILENAME $fILEPATHName");

//       var fileName = fILEPATHName
//        if (fILEPATHName.contains(".jpg") ||
//            fILEPATHName.contains(".jpeg") ||
//            fILEPATHName.contains(".png") ||
//            fILEPATHName.contains(".JPG") ||
//            fILEPATHName.contains(".JPEG") ||
//            fILEPATHName.contains(".PNG")
//        ) {
//            fileName = FileUtilsJava.compressImage(fILEPATHName)
//            Log.i(TAG,"FilePath_scaled $fileName");
//        }

        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(TAG,"selectedFilePath $fILEPATHName");
        filesToUpload[0] = File(fILEPATHName)
        Log.i(TAG,"filesToUpload $filesToUpload");

        showProgress("Uploading media ...",perProgressBar,textViewProgress)
        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload, "",object : FileUploaderCallback {
            override fun onError() {
                hideProgress(perProgressBar,textViewProgress)
                Log.i(TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {
                hideProgress(perProgressBar,textViewProgress)
                for (i in responses.indices) {
                    //val str = responses[i]
                    textViewProgress.text = "Uploaded"
                    perProgressBar.visibility = View.GONE
                    imageViewDownloadButton.visibility = View.GONE

                    //   Log.i(TAG, "RESPONSE $i ${responses[i]}")
                    submitFile(responses[i],fILETITLE,position)
                }
            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                updateProgress(totalpercent, "Uploading file $filenumber", "", perProgressBar,textViewProgress)
              //  Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })
    }


//    fun uploadFiles(fileNameList: ArrayList<FileList>) {
//        val filesToUpload = arrayOfNulls<File>(fileNameList.size)
//        for (i in 0 until fileNameList.size) {
//            var selectedFilePath = FileUtils.getReadablePathFromUri(this, fileNameList[i].fILENAME.toUri())
//            filesToUpload[i] = File(selectedFilePath!!)
//        }
//        showProgress("Uploading media ...")
//        val fileUploader = FileUploader()
//        fileUploader.uploadFiles("StudyMaterial/UploadFiles", "STUDY_METERIAL_FILE", filesToUpload, object : FileUploaderCallback {
//            override fun onError() {
//                hideProgress(perProgressBar)
//                Log.i(TAG,"onError ")
//            }
//
//            override fun onFinish(responses: Array<String>) {
//                hideProgress(perProgressBar)
//                for (i in responses.indices) {
//                    val str = responses[i]
//                    Log.i(TAG,"RESPONSE $i ${responses[i]}",)
//                }
//            }
//
//            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
//                updateProgress(totalpercent, "Uploading file $filenumber", "", perProgressBar)
//                Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
//            }
//        })
//    }


    fun updateProgress(
        progress: Int,
        title: String?,
        msg: String?,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView
    ) {
   //     Log.i(TAG,"updateProgress $progress")
//        perProgressBar.setTitle(title)
//        perProgressBar.setMessage(msg)
        textViewProgress.text = "$progress %"
        perProgressBar.progress = progress
    }

    fun showProgress(str: String?, perProgressBar: CircularProgressIndicator, textViewProgress: TextView) {
    //    Log.i(TAG,"showProgress $str")
        try {
            //perProgressBar.setCancelable(false)
            // perProgressBar.setTitle("Please wait")
            //perProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            perProgressBar.max = 100 // Progress Dialog Max Value
            // perProgressBar.setMessage(str)
//            if (perProgressBar.isShowing) perProgressBar.dismiss()
//            perProgressBar.show()
        } catch (e: java.lang.Exception) {
        }
    }

    fun hideProgress(perProgressBar: CircularProgressIndicator, textViewProgress: TextView) {
        try {
            Log.i(TAG,"hideProgress")
            // if (perProgressBar.isShowing) perProgressBar.dismiss()
        } catch (e: java.lang.Exception) {
        }
    }



    override fun submitFile(arraylist: String,fILETITLE : String,position : Int) {


        val url = "StudyMaterial/MaterialFileAdd"
        val jsonObject = JSONObject()
        try {
            //    hashMap.put("STUDY_METERIAL_ID", STUDY_METERIAL_ID);
            //        hashMap.put("FILE_TITLE", FILE_TITLE);
            //        hashMap.put("FILE_NAME", FILE_NAME);
            jsonObject.put("STUDY_METERIAL_ID", STUDY_MATERIAL_ID)
            jsonObject.put("FILE_TITLE", fILETITLE)
            jsonObject.put("FILE_NAME", arraylist)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG, "jsonObject $jsonObject")
//        val mediaType = "application/json; charset=utf-8".toMediaType()
//        val requestBody = jsonObject.toString().toRequestBody(mediaType)

        val requestBody = jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        studyMaterialStaffViewModel.getCommonPostFun(url, requestBody)
            .observe(this, Observer {
                it?.let { resource ->
                    Log.i(TAG, "resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            val error = resource.data.errorBody()?.string()
                            val message = resource.data.message()
                            val isSuccessful = resource.data.isSuccessful

                            Log.i(TAG, "error $error")
                            Log.i(TAG, "response $response")
                            Log.i(TAG, "message $message")
                            Log.i(TAG, "isSuccessful $isSuccessful")

                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Log.i(TAG, "SUCCESS")

                                    if ((position + 1) == fileNameList.size) {
                                        pb?.dismiss()
                                        dummyFileName = ArrayList<String>()
                                        getMaterialListStaff(STUDY_MATERIAL_ID)
                                        // materialListener.onCreateClick("")
                                    }
                                    // START_QUESTION_TIME = END_QUESTION_TIME
                                }
                                Utils.resultFun(response) == "FAIL" -> {
                                    Utils.getSnackBar4K(
                                        this,
                                        "Failed While submitting",
                                        constraintLayout!!
                                    )
                                }
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(
                                this,
                                "Please try again after sometime",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "loading")
                        }
                    }
                }
            })

    }

    override fun onViewClick(uriPath: String?) {

        // Create URI
        var dwload =
            File(Utils.getRootDirPath(this)!!+"/catch/staff/Study/")

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
            Utils.getSnackBar4K(this@StudyMaterialDetailsActivity,"File format doesn't support",constraintLayout!!)
        }

    }



}

class FileList(val fILEID : Int,var fILENAME: String,var fILETITLE : String, var fILETYPE: String,val fILEDELETE: Int )

interface MaterialDetailsListener{
    fun onDeleteClick(
        position: Int,
        fileList: FileList
    )

    fun onFileUploadClick(
        position: Int,
        fileList: FileList
    )

    fun onViewClick(fILENAME: String?)

    fun onFileUploadProgress(
        position: Int,
        fILEPATHName : String,
//        fILENAME: String,
        fILETITLE: String,
        perProgressBar: CircularProgressIndicator,
        imageViewDownloadButton: ImageView,
        textViewProgress: TextView
    )

    fun submitFile(arraylist: String,fILETITLE : String,position : Int)
}


