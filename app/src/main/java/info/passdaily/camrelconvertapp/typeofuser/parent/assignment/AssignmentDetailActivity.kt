package info.passdaily.camrelconvertapp.typeofuser.parent.assignment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.progressindicator.CircularProgressIndicator
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityAssignmentDetailBinding
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.downloader.Error
import info.passdaily.camrelconvertapp.services.downloader.OnDownloadListener
import info.passdaily.camrelconvertapp.services.downloader.PRDownloader
import info.passdaily.camrelconvertapp.services.downloader.StatusD
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import info.passdaily.camrelconvertapp.services.permission.sdk29AndUp
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image.SlideshowDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat


@Suppress("DEPRECATION", "SetTextI18n")
@SuppressLint("NotifyDataSetChanged", "NewApi")
class AssignmentDetailActivity : AppCompatActivity(), AssignmentClickListener {

    var TAG = "AssignmentDetailActivity"

    private lateinit var binding: ActivityAssignmentDetailBinding

    //    String Get_AssignmentDetails= Global.url+"OnlineExam/AssignmentStatusByStudent?AccademicId=";
    ////http://demoapp.passdaily.in/PassDailyParentsApi/OnlineExam/AssignmentStatusByStudent?AccademicId=7&ClassId=2&StudentId=81&AssignmentId=8
    var SERVER_URL = "OnlineExam/UploadFiles"

    var toolbar: Toolbar? = null

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0

    var SUBJECT_NAME = ""
    var ASSIGNMENT_ID = 0
    var textViewQuestion: TextView? = null
    var textViewDescription: ShowMoreTextView? = null
    var textStartDate: TextView? = null
    var textEndDate: TextView? = null
    var textOutOffMark: TextView? = null

    var imageViewStatus: ImageView? = null
    var textViewStatus: TextView? = null

    var fileNames = ArrayList<String>()
    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var adminRole = 0

    var dummyFileName = ArrayList<String>()
    ////
    var textViewNofiles: TextView? = null
    var recyclerViewAttachItem: RecyclerView? = null
    var recyclerViewStudentAttach: RecyclerView? = null
    var textViewNoFilesStudent: TextView? = null
    var jsonArrayList = ArrayList<AssignmentFileLocal>()

    var fileNameList = ArrayList<AssignmentFileLocal>()
    var constraintLayoutUpload: ConstraintLayout? = null
    var buttonTakeTest: AppCompatButton? = null
    var constraintResult: ConstraintLayout? = null

    var constraintFirstLayout: ConstraintLayout? = null

    var textViewTotMarks: TextView? = null
    var textViewComment: TextView? = null
    var textViewDate: TextView? = null

    var noDeleteImage = 0

    var startTimeRaw = ""
    var endTimeRaw = ""
    var currentTime = ""

    var imageViewWord: ImageView? = null
    var imageViewExcel: ImageView? = null
    var imageViewText: ImageView? = null
    var imageViewPPT: ImageView? = null
    var imageViewImage: ImageView? = null
    var imageViewPDF: ImageView? = null
    var imageViewVoice: ImageView? = null

    private var readPermission = false
    private var writePermission = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    var arrayListItems = ""

    var pb: ProgressDialog? = null


    var maxCount = 10
    var maxCountSelection = 10

    private lateinit var assignmentDetailsViewModel: AssignmentDetailsViewModel
    var assignmentAnswerText: EditText? = null

    lateinit var studentAttachAdapter: StudentAttachAdapter

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        if (extras != null) {
            SUBJECT_NAME = extras.getString("SUBJECT_NAME")!!
            ASSIGNMENT_ID = extras.getInt("ASSIGNMENT_ID")
        }

        localDBHelper = LocalDBHelper(this)
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE


        var studentDBHelper = StudentDBHelper(this)
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID
//        setContentView(R.layout.activity_assignment_detail)
//        assignmentDetailsViewModel = ViewModelProvider(this)[AssignmentDetailsViewModel::class.java]

        assignmentDetailsViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[AssignmentDetailsViewModel::class.java]


        binding = ActivityAssignmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = SUBJECT_NAME
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //   toolbar.setTitle("About-Us");
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }

        Utils.setStatusBarColor(this)

        pb = ProgressDialog(this)
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Loading...")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)

        textViewQuestion = binding.textViewQuestion
        textViewDescription = binding.textViewDescription
        textStartDate = binding.textStartDate
        textEndDate = binding.textEndDate
        textOutOffMark = binding.textOutOffMark

        assignmentAnswerText = binding.assignmentAnswerText

        textViewNoFilesStudent = binding.textViewNoFilesStudent

        textViewStatus = binding.textViewStatus
        imageViewStatus = binding.imageViewStatus

        constraintLayoutUpload = binding.constraintLayoutUpload
        imageViewWord = binding.imageViewWord
        imageViewExcel = binding.imageViewExcel
        imageViewText = binding.imageViewText
        imageViewPPT = binding.imageViewPPT
        imageViewImage = binding.imageViewImage
        imageViewPDF = binding.imageViewPDF
        imageViewVoice = binding.imageViewVoice

        constraintFirstLayout = binding.constraintFirstLayout
        constraintFirstLayout!!.setOnClickListener {

            hideFocusListener()
        }

        buttonTakeTest = binding.buttonTakeTest
        constraintResult = binding.constraintResult

        textViewTotMarks = binding.textViewTotMarks
        textViewComment = binding.textViewComment
        textViewDate = binding.textViewDate

        //
        textViewNofiles = binding.textViewNofiles
        recyclerViewAttachItem = binding.recyclerViewAttachItem
        recyclerViewAttachItem?.layoutManager = GridLayoutManager(this, 3)

        recyclerViewStudentAttach = binding.recyclerViewStudentAttach
        recyclerViewStudentAttach?.layoutManager = GridLayoutManager(this, 4)

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }

        imageViewWord?.setOnClickListener {
            if (requestPermission()) {
                val mimeTypes = arrayOf(
                    "application/msword", //.doc
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"//.docx
                )
                mimeTypeFun(mimeTypes)
            }
        }
        imageViewExcel?.setOnClickListener {
            if (requestPermission()) {
                val mimeTypes = arrayOf(
                    "application/vnd.ms-excel", // .xls
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" // .xlsx
                )
                mimeTypeFun(mimeTypes)
            }
        }

        imageViewText?.setOnClickListener {
            if (requestPermission()) {
                mimeTypeFun("text/*")
            }
        }

        imageViewPPT?.setOnClickListener {
            if (requestPermission()) {
                val mimeTypes = arrayOf(
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation"//.ppt
                    , "application/vnd.ms-powerpoint"
                )
                mimeTypeFun(mimeTypes)
            }
        }

        imageViewImage?.setOnClickListener {
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
                    Utils.getSnackBar4K(this, "Maximum Count Reached", constraintFirstLayout!!)
                }
            }
        }

        imageViewPDF?.setOnClickListener {
            if (requestPermission()) {
                mimeTypeFun("application/pdf")
            }
        }
        //"audio/*"

        imageViewVoice?.setOnClickListener {
            if (requestPermission()) {
                mimeTypeFun("audio/*")
            }
        }


        buttonTakeTest?.setOnClickListener {
            fileNames = ArrayList<String>()
            if (assignmentDetailsViewModel.validateField(
                    assignmentAnswerText!!,
                    constraintFirstLayout!!,
                    this@AssignmentDetailActivity
                ) || (fileNameList.size + jsonArrayList.size) != 0
            ) {
//                if (dummyFileName.size != 0) {
//                    //    progressStart();
//                    pb = ProgressDialog.show(this, "", "Uploading...", true)
//                    Thread {
//                        //creating new thread to handle Http Operations
//                        try {
//                            for (i in fileNameList.indices) {
//                                onFileUploadClick(i, fileNameList[i])
//                                SystemClock.sleep(6000)
//                            }
//                            //Your code goes here
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                            Log.i(TAG, "Exception  ${e.printStackTrace()}")
//                        }
//                    }.start()
//                } else {

//                if(fileNameList.size != 0){
                for(i in fileNameList.indices){
                    if(fileNameList[i].fileType == "Uploaded"){
                        fileNameList[i].fileType = "Json"
                        arrayListItems += fileNameList[i].aSSIGNMENTFILE+","
                    }else{
                        arrayListItems = ""
                    }
                }
                Log.i(TAG,"arrayListItems $arrayListItems")
                if(arrayListItems.isNotEmpty()){
                    submitFile(Utils.removeLastChar(arrayListItems))
                }else{
                    submitFile("")
                }

//                }else{
//                    submitFile("")
//                }

//                }
//            } else {
//                Utils.getSnackBar4K(this, "Please answer this question", constraintFirstLayout!!)
            }
        }



        initMethod()
    }


    fun hideFocusListener() {
        val view = this.currentFocus

        // if nothing is currently
        // focus then this will protect
        // the app from crash

        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            val manager = getSystemService(
                INPUT_METHOD_SERVICE
            ) as InputMethodManager
            manager
                .hideSoftInputFromWindow(
                    view.windowToken, 0
                )
        }
    }
    fun mimeTypeFun(mimeTypes: Array<String>) {
        if (fileNameList.size < maxCount) {
            maxCountSelection = maxCount - fileNameList.size
            Toast.makeText(this, "Select $maxCountSelection ", Toast.LENGTH_SHORT).show()
            val intent =
                Intent(Intent.ACTION_OPEN_DOCUMENT); // or ACTION_OPEN_DOCUMENT //ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "*/*";
            startForResult.launch(intent)
        } else {
            Utils.getSnackBar4K(this, "Maximum Count Reached", constraintFirstLayout!!)
        }
    }


    fun mimeTypeFun(mimeTypes: String) {
        if (fileNameList.size < maxCount) {
            maxCountSelection = maxCount - fileNameList.size
            Toast.makeText(this, "Select $maxCountSelection ", Toast.LENGTH_SHORT).show()

            val intent =
                Intent(Intent.ACTION_OPEN_DOCUMENT); // or ACTION_OPEN_DOCUMENT //ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = mimeTypes;
            startForResult.launch(intent)
        } else {
            Utils.getSnackBar4K(this, "Maximum Count Reached", constraintFirstLayout!!)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initMethod() {
        fileNameList = ArrayList<AssignmentFileLocal>()
        jsonArrayList = ArrayList<AssignmentFileLocal>()
        assignmentDetailsViewModel.getAssignmentDetails(
            ACADEMICID,
            CLASSID,
            STUDENTID,
            ASSIGNMENT_ID
        )
            .observe(this, Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            val assignmentDetails = response.assignmentDetails
                            textViewQuestion?.text = assignmentDetails.aSSIGNMENTNAME
                            textViewDescription?.text = assignmentDetails.aSSIGNMENTDESCRIPTION
                            textViewDescription?.apply {
                                setShowingLine(5)
                                setShowMoreTextColor(resources.getColor(R.color.green_300))
                                setShowLessTextColor(resources.getColor(R.color.green_300))
                            }
                            binding.textChapterNo.text = "Chapter.No : ${assignmentDetails.cHAPTERNO}"
                            binding.textPageNo.text = "Page.No : ${assignmentDetails.pAGENO}"

                            if (assignmentDetails.aSSIGNMENTOUTOFFMARK > 0) {
                                textOutOffMark?.text =
                                    "Out of Marks : " + assignmentDetails.aSSIGNMENTOUTOFFMARK
                            } else {
                                textOutOffMark?.text =
                                    "Out of Mark : " + assignmentDetails.aSSIGNMENTOUTOFFMARK
                            }
                            if (!assignmentDetails.sTARTDATE.isNullOrBlank()) {
                                val date1: Array<String> =
                                    assignmentDetails.sTARTDATE.split("T".toRegex()).toTypedArray()
                                val ddddd: Long = Utils.longconversion(date1[0] + " " + date1[1])
                                textStartDate?.text = Utils.formattedDateTime(ddddd)
                                startTimeRaw = date1[0] + " " + date1[1]
                            }
                            if (!assignmentDetails.eNDDATE.isNullOrBlank()) {
                                val date1: Array<String> =
                                    assignmentDetails.eNDDATE.split("T".toRegex()).toTypedArray()
                                val ddddd: Long = Utils.longconversion(date1[0] + " " + date1[1])
                                textEndDate?.text = Utils.formattedDateTime(ddddd)
                                endTimeRaw = date1[0] + " " + date1[1]
                            }

                            if (!assignmentDetails.tIMENOW.isNullOrBlank()) {
                                val dateC: Array<String> =
                                    assignmentDetails.tIMENOW.split("T".toRegex()).toTypedArray()
                                currentTime = dateC[0] + " " + dateC[1]
                            }
                            if (response.assignmentAttachmentList.isNotEmpty()) {
                                textViewNofiles?.visibility = View.GONE
                                recyclerViewAttachItem?.visibility = View.VISIBLE
                                recyclerViewAttachItem?.adapter = StaffAttachAdapter(this,this,response.assignmentAttachmentList, this,TAG)
                            } else {
                                textViewNofiles?.visibility = View.VISIBLE
                                recyclerViewAttachItem?.visibility = View.GONE
                            }
//                            try {
//                                assignmentAnswerText?.setText(response.assignmentStudentStatus.aSSIGNMENTDETAILS)
//                            } catch (err: NullPointerException) {
//                                assignmentAnswerText?.setText("")
//                                Log.i(TAG, "NullPointerException $err")
//                            }
                            var assignmentStudentStatus = response.assignmentStudentStatus
                            if (assignmentStudentStatus != null) {

                                Log.i(TAG,"currentTime $currentTime")
                                Log.i(TAG,"endTimeRaw $endTimeRaw")
                                Log.i(TAG,"startTimeRaw $startTimeRaw")



                                assignmentAnswerText?.setText(assignmentStudentStatus.aSSIGNMENTDETAILS)
                                assignmentAnswerText?.setSelection(assignmentAnswerText?.text!!.length)

                                if (Utils.checkDateTime(currentTime,endTimeRaw, "Before")
                                    || Utils.checkDateTime(currentTime,endTimeRaw, "Equal")) {
//                                if (endTimeRaw > currentTime || endTimeRaw.compareTo(currentTime) == 0) {
                                    if (assignmentStudentStatus.aSSIGNMENTSTUTUS == 2) {

                                        constraintResult?.visibility = View.VISIBLE
                                        constraintLayoutUpload?.visibility = View.GONE
                                        buttonTakeTest?.visibility = View.GONE
                                        Glide.with(this)
                                            .load(R.drawable.ic_icon_complete)
                                            .into(imageViewStatus!!)
                                        textViewStatus?.text = "Corrected"
                                        assignmentAnswerText?.isEnabled = false
                                        if (assignmentStudentStatus.aSSIGNMENTMARK != null) {
                                            textViewTotMarks?.text =
                                                "Total Marks : ${assignmentStudentStatus.aSSIGNMENTMARK} / ${assignmentDetails.aSSIGNMENTOUTOFFMARK}"
                                        }
                                        if (assignmentStudentStatus.aSSIGNMENTREPLY != "null") {
                                            textViewComment?.text =
                                                "Teacher Comments : ${assignmentStudentStatus.aSSIGNMENTREPLY}"
                                        } else {
                                            textViewComment?.text = "No Reply"
                                        }

                                        if (!assignmentStudentStatus.aSSIGNMENTSUBMITDATE.isNullOrBlank()) {
                                            val date1: Array<String> =
                                                assignmentStudentStatus.aSSIGNMENTSUBMITDATE.split("T".toRegex())
                                                    .toTypedArray()
                                            val ddddd: Long =
                                                Utils.longconversion(date1[0] + " " + date1[1])
                                            textViewDate?.text = "Assignment Submitted On  : ${
                                                Utils.formattedDateTime(ddddd)
                                            }"
                                        }

                                        noDeleteImage = 2

                                    }else{
                                        constraintResult?.visibility = View.GONE
                                        constraintLayoutUpload?.visibility = View.VISIBLE
                                        buttonTakeTest?.visibility = View.VISIBLE
                                        Glide.with(this)
                                            .load(R.drawable.ic_assign_play)
                                            .into(imageViewStatus!!)
                                        textViewStatus?.text = "Resume Assignment"
                                        assignmentAnswerText?.isEnabled = true
                                        noDeleteImage = 1
                                    }
                                }else{

                                    constraintResult?.visibility = View.VISIBLE
                                    constraintLayoutUpload?.visibility = View.GONE
                                    buttonTakeTest?.visibility = View.GONE
                                    Glide.with(this)
                                        .load(R.drawable.ic_icon_complete)
                                        .into(imageViewStatus!!)
                                    textViewStatus?.text = "Submitted"
                                    assignmentAnswerText?.isEnabled = false
                                    if (assignmentStudentStatus.aSSIGNMENTMARK != null) {
                                        textViewTotMarks?.text =
                                            "Total Marks : ${assignmentStudentStatus.aSSIGNMENTMARK} / ${assignmentDetails.aSSIGNMENTOUTOFFMARK}"
                                    }
                                    if (assignmentStudentStatus.aSSIGNMENTREPLY != "null") {
                                        textViewComment?.text =
                                            "Teacher Comments : ${assignmentStudentStatus.aSSIGNMENTREPLY}"
                                    } else {
                                        textViewComment?.text = "No Reply"
                                    }

                                    if (!assignmentStudentStatus.aSSIGNMENTSUBMITDATE.isNullOrBlank()) {
                                        val date1: Array<String> =
                                            assignmentStudentStatus.aSSIGNMENTSUBMITDATE.split("T".toRegex())
                                                .toTypedArray()
                                        val ddddd: Long =
                                            Utils.longconversion(date1[0] + " " + date1[1])
                                        textViewDate?.text = "Assignment Submitted On  : ${
                                            Utils.formattedDateTime(ddddd)
                                        }"
                                    }

                                    noDeleteImage = 2
                                }
                            } else {
                                if (Utils.checkDateTime(currentTime,endTimeRaw, "Before")
                                    || Utils.checkDateTime(currentTime,endTimeRaw, "Equal")) {

                                    constraintResult?.visibility = View.GONE
                                    constraintLayoutUpload?.visibility = View.VISIBLE
                                    buttonTakeTest?.visibility = View.VISIBLE
                                    Glide.with(this)
                                        .load(R.drawable.ic_assign_play)
                                        .into(imageViewStatus!!)
                                    textViewStatus?.text = "Start Assignment"
                                    assignmentAnswerText?.isEnabled = true
                                    noDeleteImage = 1
                                } else {
                                    constraintResult?.visibility = View.GONE
                                    constraintLayoutUpload?.visibility = View.GONE
                                    buttonTakeTest?.visibility = View.GONE
                                    Glide.with(this)
                                        .load(R.drawable.ic_icon_complete)
                                        .into(imageViewStatus!!)
                                    textViewStatus?.text = "Completed"
                                    assignmentAnswerText?.isEnabled = false
                                    noDeleteImage = 2
                                }
                            }

                            if (response.assignmentFileList.isNotEmpty()) {
                                textViewNoFilesStudent?.visibility = View.GONE
                                recyclerViewStudentAttach?.visibility = View.VISIBLE
                                Log.i(TAG,"response.assignmentFileList ${response.assignmentFileList}")
                                for (i in response.assignmentFileList.indices) {
                                    Log.i(TAG,"aSSIGNMENTFILE ${response.assignmentFileList[i].aSSIGNMENTFILE}")
                                    jsonArrayList.add(
                                        AssignmentFileLocal(
                                            response.assignmentFileList[i].aSSIGNMENTFILE,
                                            response.assignmentFileList[i].aSSIGNMENTFILEID,
                                            response.assignmentFileList[i].aSSIGNMENTSUBMITID,
                                            "Json",
                                            noDeleteImage
                                        )
                                    )
                                }
                                Log.i(TAG,"jsonArrayList $jsonArrayList")
                                fileNameList.addAll(jsonArrayList)
                                studentAttachAdapter =
                                    StudentAttachAdapter(this, jsonArrayList, this, TAG)
                                recyclerViewStudentAttach?.adapter = studentAttachAdapter

                            } else {
                                textViewNoFilesStudent?.visibility = View.VISIBLE
                                recyclerViewStudentAttach?.visibility = View.GONE
                            }

                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Status.ERROR ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "Status.ERROR ${Status.LOADING}")

                        }
                    }
                }
            })

    }

    class StudentAttachAdapter(
        var assignmentClickListener: AssignmentClickListener,
        var assignmentFileList: ArrayList<AssignmentFileLocal>,
        var context: Context,
        var TAG: String
    ) : RecyclerView.Adapter<StudentAttachAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView = view.findViewById(R.id.imageView)
            var imageViewOther: ImageView = view.findViewById(R.id.imageViewOther)
            var imageViewDelete: ImageView = view.findViewById(R.id.imageViewDelete)

            var perProgressBar : CircularProgressIndicator = view.findViewById(R.id.perProgressBar)
            var textViewProgress : TextView = view.findViewById(R.id.textViewProgress)
            var textViewTitle  : TextView = view.findViewById(R.id.textViewTitle)


        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.assignment_student_attach_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.i(TAG,"aSSIGNMENTFILE ${assignmentFileList[position].aSSIGNMENTFILE}")
            if (assignmentFileList[position].fileType == "Json") {
                holder.textViewTitle.visibility  =  View.GONE
                holder.perProgressBar.visibility  =  View.GONE
                holder.textViewProgress.visibility  =  View.GONE

                val path: String = assignmentFileList[position].aSSIGNMENTFILE
                Log.i(TAG,"path $path")
                val mFile = File(path)
                if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                    || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")
                ) {
                    // Word document
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(File(path))
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
                        .load(File(path))
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
                        .load(File(path))
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
                        .load(File(path))
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
                        .load(Global.event_url + "/AssignmentFile/" + path)
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
                        .load(File(path))
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
                        .load(File(path))
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
                        .load(File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_video_library)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                }
            }

            else if (assignmentFileList[position].fileType == "Local") {
                val path: String = assignmentFileList[position].aSSIGNMENTFILE
                Log.i(TAG, "path $path")
                val mFile = FileUtils.getReadablePathFromUri(context, path.toUri())
                Log.i(TAG, "mFile $mFile")

                assignmentFileList[position].fileType = "Uploaded"
                holder.perProgressBar.visibility  =  View.VISIBLE
                holder.textViewProgress.visibility  =  View.VISIBLE//,materialList[position].fILENAME
                assignmentClickListener.onFileUploadProgress(position,mFile!!,holder.textViewTitle, holder.perProgressBar,holder.textViewProgress)

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
                        .load(assignmentFileList[position].aSSIGNMENTFILE)
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
                        .contains(".AAC") || mFile.toString().contains(".WMA")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_voice)
                        .into(holder.imageViewOther)
                } else {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_video_library)
                        .into(holder.imageViewOther)
                }
            }

            else if (assignmentFileList[position].fileType == "Uploaded") {
                holder.textViewTitle.visibility  =  View.VISIBLE
                holder.textViewTitle.text = "Uploaded"
                holder.perProgressBar.visibility  =  View.GONE
                holder.textViewProgress.visibility  =  View.GONE

                val path: String = assignmentFileList[position].aSSIGNMENTFILE
                val mFile = File(path)
                if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                    || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")
                ) {
                    // Word document
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(File(path))
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
                        .load(File(path))
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
                        .load(File(path))
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
                        .load(File(path))
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
                        .load(Global.event_url + "/AssignmentFile/" + path)
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
                        .load(File(path))
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
                        .load(File(path))
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
                    try {
                        val thumb = ThumbnailUtils.createVideoThumbnail(
                            Global.event_url + "/AssignmentFile/" + path,
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )
                        holder.imageViewOther.setImageBitmap(thumb)
                    } catch (e: java.lang.Exception) {
                        Log.i("TAG", "Exception $e")
                    }
//                    Glide.with(context)
//                        .load(File(path))
//                        .apply(
//                            RequestOptions.centerCropTransform()
//                                .dontAnimate() //   .override(imageSize, imageSize)
//                                .placeholder(R.drawable.ic_file_video)
//                        )
//                        .thumbnail(0.5f)
//                        .into(holder.imageViewOther)
                }
            }





            holder.imageView.setOnClickListener {
                Global.albumImageList = ArrayList<CustomImageModel>()

                if (assignmentFileList[position].fileType == "Json") {
                    Global.albumImageList.add(
                        CustomImageModel(
                            Global.event_url + "/AssignmentFile/"
                                    + assignmentFileList[position].aSSIGNMENTFILE
                        )
                    )
                }else if (assignmentFileList[position].fileType == "Local"){
                    Global.albumImageList.add(
                        CustomImageModel(
                            assignmentFileList[position].aSSIGNMENTFILE
                        )
                    )
                }
                assignmentClickListener.onViewClick()
            }


            if (assignmentFileList[position].noDeleteImage == 2) {
                holder.imageViewDelete.visibility = View.GONE
            } else {
                holder.imageViewDelete.visibility = View.VISIBLE
            }
            holder.imageViewDelete.setOnClickListener {
                assignmentClickListener.onDeleteClick(position, assignmentFileList[position])
            }
        }

        override fun getItemCount(): Int {
            return assignmentFileList.size
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    class StaffAttachAdapter(
        var assignmentClickListener: AssignmentClickListener,
        var assignmentDetailActivity: AssignmentDetailActivity,
        var assignmentAttachmentList: List<AssignmentDetailsModel.AssignmentAttachment>,
        var context: Context,var TAG : String
    ) : RecyclerView.Adapter<StaffAttachAdapter.ViewHolder>() {
        var downLoadPos = 0
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView = view.findViewById(R.id.imageView)
            var imageViewOther: ImageView = view.findViewById(R.id.imageViewOther)
            var imageViewVideo : ImageView = view.findViewById(R.id.imageViewVideo)
            var imageViewMore : ImageView = view.findViewById(R.id.imageViewMore)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.assignment_item_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val path: String = assignmentAttachmentList[position].aSSIGNMENTFILE
            val mFile = File(path)
            if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")) {
                // Word document
                holder.imageViewOther.visibility = View.VISIBLE
                holder.imageViewVideo.visibility = View.GONE
                holder.imageView.visibility = View.GONE
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //  .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_word)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewOther)
            } else if (mFile.toString().contains(".pdf") ||
                mFile.toString().contains(".PDF")) {
                holder.imageViewOther.visibility = View.VISIBLE
                holder.imageViewVideo.visibility = View.GONE
                holder.imageView.visibility = View.GONE
                // PDF file
                Glide.with(context)
                    .load(File(path))
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
                holder.imageViewVideo.visibility = View.GONE
                holder.imageView.visibility = View.GONE
                Glide.with(context)
                    .load(File(path))
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
                holder.imageViewVideo.visibility = View.GONE
                holder.imageView.visibility = View.GONE
                // Excel file
                Glide.with(context)
                    .load(File(path))
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
                holder.imageViewVideo.visibility = View.GONE
                holder.imageView.visibility = View.VISIBLE
                Glide.with(context)
                    .load(Global.event_url + "/AssignmentFile/" + path)
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
                holder.imageViewVideo.visibility = View.GONE
                holder.imageView.visibility = View.GONE
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_text)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewOther)
            }
            else if (mFile.toString().contains(".mp3") || mFile.toString()
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
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() // .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_voice)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewOther)
            }
            else {
                holder.imageViewOther.visibility = View.GONE
                holder.imageViewVideo.visibility = View.VISIBLE
                holder.imageView.visibility = View.GONE
//                Glide.with(context)
//                    .load(File(path))
//                    .apply(
//                        RequestOptions.centerCropTransform()
//                            .dontAnimate() //   .override(imageSize, imageSize)
//                            .placeholder(R.drawable.ic_file_video)
//                    )
//                    .thumbnail(0.5f)
//                    .into(holder.imageViewOther)
//                Glide.with(context)
//                    // .load(java.io.File(path))
//                    .load(Global.event_url + "/AssignmentFile/" + path)
//                    .apply(
//                        RequestOptions.centerCropTransform()
//                            .dontAnimate() //   .override(imageSize, imageSize)
//                            .placeholder(R.drawable.ic_video_library)
//                    )
//                    .thumbnail(0.5f)
//                    .into(holder.imageViewVideo)
                Log.i(TAG,"video ${Global.event_url + "/AssignmentFile/" + path}")
                try {
                    val thumb = ThumbnailUtils.createVideoThumbnail(
                        Global.event_url + "/AssignmentFile/" + path,
                        MediaStore.Images.Thumbnails.MINI_KIND
                    )
                    holder.imageViewOther.setImageBitmap(thumb)
                } catch (e: java.lang.Exception) {
                    Log.i("TAG", "Exception $e")
                }
            }


            holder.imageViewMore.setOnClickListener {
                val popupMenu = PopupMenu(context, holder.imageViewMore)
                popupMenu.inflate(R.menu.video_adapter_menu)
                popupMenu.menu.findItem(R.id.menu_edit).icon = context.resources.getDrawable(R.drawable.ic_icon_edit)
                popupMenu.menu.findItem(R.id.menu_report).icon = context.resources.getDrawable(R.drawable.ic_icon_about_gray)
                popupMenu.menu.findItem(R.id.menu_video).icon = context.resources.getDrawable(R.drawable.ic_icon_close)
                popupMenu.menu.findItem(R.id.menu_offline_video).icon = context.resources.getDrawable(R.drawable.ic_icon_delete_gray)
                popupMenu.menu.findItem(R.id.menu_download).icon = context.resources.getDrawable(R.drawable.ic_icon_download)
                popupMenu.menu.findItem(R.id.menu_open).icon = context.resources.getDrawable(R.drawable.ic_icon_download)

                popupMenu.menu.findItem(R.id.menu_edit).isVisible = false
                popupMenu.menu.findItem(R.id.menu_report).isVisible = false
                popupMenu.menu.findItem(R.id.menu_video).isVisible = false
                popupMenu.menu.findItem(R.id.menu_offline_video).isVisible = false


                val file = File(Utils.getRootDirPath(context)!!)
                if (!file.exists()) {
                    file.mkdirs()
                } else {
                    Log.i(TAG, "Already existing")
                }

                val check = File(file.path +"/catch/parent/Assignment/"+ assignmentAttachmentList[position].aSSIGNMENTFILE)
                Log.i(TAG,"check $check")
                if (!check.isFile) {
                    popupMenu.menu.findItem(R.id.menu_download).isVisible = true
                    popupMenu.menu.findItem(R.id.menu_open).isVisible = false
                }else{
                    popupMenu.menu.findItem(R.id.menu_download).isVisible = false
                    popupMenu.menu.findItem(R.id.menu_open).isVisible = true
                }

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_download -> {
//                            materialDetailsListener.onUpdateClickListener(materialList[position])
                            if(assignmentDetailActivity.requestPermission()) {
//                                holder.constraintDownload.visibility = View.VISIBLE
//                                holder.textViewPercentage.visibility = View.VISIBLE
                                if (StatusD.RUNNING === PRDownloader.getStatus(position)) {
                                    PRDownloader.pause(position)
                                }

                                if (StatusD.PAUSED === PRDownloader.getStatus(position)) {
                                    PRDownloader.resume(position)
                                }
                                downLoadPos = position

                                var fileUrl = Global.event_url+"/AssignmentFile/" + assignmentAttachmentList[position].aSSIGNMENTFILE
                                var fileLocation = Utils.getRootDirPath(context) +"/catch/parent/Assignment/"
                                //var fileLocation = Environment.getExternalStorageDirectory().absolutePath + "/Passdaily/File/"
                                var fileName = assignmentAttachmentList[position].aSSIGNMENTFILE
                                Log.i(TAG, "fileUrl $fileUrl")
                                Log.i(TAG, "fileLocation $fileLocation")
                                Log.i(TAG, "fileName $fileName")

                                downLoadPos = PRDownloader.download(
                                    fileUrl, fileLocation, fileName
                                )
                                    .build()
                                    .setOnStartOrResumeListener {
                                        //  holder.imageViewClose.visibility = View.VISIBLE
                                    }
                                    .setOnPauseListener {
                                        //   holder.imageViewClose.visibility = View.VISIBLE
                                    }
                                    .setOnCancelListener {
                                        //  holder.imageViewClose.visibility = View.VISIBLE
                                        downLoadPos = 0
                                        //    holder.constraintDownload.visibility = View.GONE
                                        //   holder.textViewPercentage.text ="Loading... "
                                        //  holder.imageViewClose.visibility = View.GONE
                                        Utils.getSnackBar4K(context, "Download Cancelled", assignmentDetailActivity.constraintFirstLayout!!)
                                        PRDownloader.cancel(downLoadPos)

                                    }
                                    .setOnProgressListener { progress ->
                                        val progressPercent: Long =
                                            progress.currentBytes * 100 / progress.totalBytes
//                                        holder.textViewPercentage.text = Utils.getProgressDisplayLine(
//                                            progress.currentBytes,
//                                            progress.totalBytes
//                                        )
                                    }
                                    .start(object : OnDownloadListener {
                                        override fun onDownloadComplete() {
//                                            holder.constraintDownload.visibility = View.GONE
//                                            holder.textViewPercentage.visibility = View.GONE
                                            Utils.getSnackBarGreen(context, "Download Completed", assignmentDetailActivity.constraintFirstLayout!!)
                                        }

                                        override fun onError(error: Error) {
                                            Log.i(TAG, "Error $error")
                                            Utils.getSnackBar4K(context, "$error", assignmentDetailActivity.constraintFirstLayout!!)
//                                            Toast.makeText(
//                                                context,
//                                                "Some Error occured",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                            holder.constraintDownload.visibility = View.GONE
//                                            holder.textViewPercentage.visibility = View.GONE
                                        }
                                    })

                            }
                            true
                        }
                        R.id.menu_open -> {
                            if(assignmentDetailActivity.requestPermission()) {
                                assignmentClickListener.onOpenFileClick(assignmentAttachmentList[position].aSSIGNMENTFILE)
                            }
                            true
                        }
                        else -> false
                    }

                }
                popupMenu.show()
            }


//            holder.imageView.setOnClickListener {
//                Global.albumImageList = ArrayList<CustomImageModel>()
//                Global.albumImageList.add(
//                    CustomImageModel(
//                        Global.event_url + "/AssignmentFile/"
//                                + assignmentAttachmentList[position].aSSIGNMENTFILE
//                    )
//                )
//                assignmentClickListener.onViewClick()
//            }
//
//            holder.imageViewVideo.setOnClickListener {
//                val intent = Intent(context, Video_Activity::class.java)
//                intent.putExtra("ALBUM_TITLE", "")
//                intent.putExtra("ALBUM_FILE", Global.event_url + "/AssignmentFile/" +
//                        assignmentAttachmentList[position].aSSIGNMENTFILE)
//                context.startActivity(intent)
//
//            }

        }

        override fun getItemCount(): Int {
            return assignmentAttachmentList.size
        }

    }


    ///permission Part
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
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
                                AssignmentFileLocal(
                                    imageUri.toString(),
                                    0,
                                    0,
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
                        AssignmentFileLocal(
                            imageUri.toString(),
                            0,
                            0,
                            "Local",
                            noDeleteImage
                        )
                    )
                }
                textViewNoFilesStudent?.visibility = View.GONE
                recyclerViewStudentAttach?.visibility = View.VISIBLE
                studentAttachAdapter =
                    StudentAttachAdapter(this, fileNameList, this@AssignmentDetailActivity, TAG)
                recyclerViewStudentAttach?.adapter = studentAttachAdapter

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


    override fun onDeleteClick(position: Int, assignmentFileList: AssignmentFileLocal) {
        if (assignmentFileList.fileType == "Json") {
            fileNameList.removeAt(position)
            jsonArrayList.removeAt(position)
            //OnlineExam/AssignmentFileDelete?AssignmentFileId=
            deleteAssignmentFile(assignmentFileList)
        } else {
            fileNameList.removeAt(position)
        }
        studentAttachAdapter.notifyDataSetChanged()

        if (fileNameList.size == 0) {
            textViewNoFilesStudent?.visibility = View.VISIBLE
            recyclerViewStudentAttach?.visibility = View.GONE
        }
    }

    private fun deleteAssignmentFile(assignmentFileList: AssignmentFileLocal) {

        assignmentDetailsViewModel.getDeleteAssignmentFile(assignmentFileList.aSSIGNMENTFILEID)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data
                            Log.i(TAG, "deleted Success fully $response")
                        }
                        Status.ERROR -> {
                            Log.i("TAG", "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i("TAG", "resource ${resource.status}")
                            Log.i("TAG", "message ${resource.message}")
                        }
                    }
                }
            })

    }

    override fun onFileUploadClick(position: Int, assignmentFileList: AssignmentFileLocal) {

//        pb!!.setMessage("Uploading  : ${position + 1} / ${fileNameList.size}")
//        if (fileNameList[position].fileType == "Local") {
//            var selectedFilePath =
//                FileUtils.getReadablePathFromUri(this, assignmentFileList.aSSIGNMENTFILE.toUri())
//            Log.i(TAG, "position  $position")
//            val i = Log.i(TAG, "fileName  $selectedFilePath")
//
//            var imagenPerfil = if (selectedFilePath.toString().contains(".jpg") || selectedFilePath.toString()
//                        .contains(".jpeg")
//                    || selectedFilePath.toString().contains(".png") || selectedFilePath.toString()
//                        .contains(".JPG") || selectedFilePath.toString().contains(".JPEG")
//                    || selectedFilePath.toString().contains(".PNG")
//                ) {
//                    val file1 = File(selectedFilePath!!)
//                    val requestFile: RequestBody =
//                        file1.asRequestBody("image/*".toMediaTypeOrNull())
//                    MultipartBody.Part.createFormData(
//                        "STUDY_METERIAL_FILE",
//                        file1.name,
//                        requestFile
//                    )
//                } else {
//                    val requestFile: RequestBody = RequestBody.create(
//                        "multipart/form-data".toMediaTypeOrNull(),
//                        selectedFilePath!!
//                    )
//                    // MultipartBody.Part is used to send also the actual file name
//                    MultipartBody.Part.createFormData(
//                        "STUDY_METERIAL_FILE",
//                        selectedFilePath,
//                        requestFile
//                    )
//                }
//
//
//            val apiInterface = RetrofitClient.create().fileUploadAssignment(
//                SERVER_URL,
//                imagenPerfil
//            )
//            apiInterface.enqueue(object : Callback<FileResultModel> {
//                override fun onResponse(
//                    call: Call<FileResultModel>,
//                    resource: Response<FileResultModel>
//                ) {
//                    val response = resource.body()
//                    if (resource.isSuccessful) {
//                        Log.i(TAG, "response  $response")
//                        fileNames.add(response!!.dETAILS)
//                    }
//                    if ((position + 1) == fileNameList.size) {
//                        pb?.dismiss()
//                        val arraylist = java.lang.String.join(",", fileNames)
//                        Log.i(TAG, "str  $arraylist")
//                        submitFile(arraylist)
//                    }
//
//                }
//
//                override fun onFailure(call: Call<FileResultModel>, t: Throwable) {
//                    Log.i(TAG, "Throwable  $t")
//                }
//            })
//        } else {
//            Log.i(TAG, "str  ${fileNameList[position].aSSIGNMENTFILE}")
//        }
//
//
//        if ((position + 1) == fileNameList.size) {
//            pb?.dismiss()
//            if (fileNames.size != 0) {
//                val arraylist = java.lang.String.join(",", fileNames)
//                Log.i(TAG, "str  $arraylist")
//                submitFile(arraylist)
//            }else{
//                submitFile("")
//            }
//        }

    }


    override fun onFileUploadProgress(
        position: Int,
        fILEPATHName: String,
        textViewTitle: TextView,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView
    ) {
        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(TAG,"selectedFilePath $fILEPATHName");
        filesToUpload[0] = File(fILEPATHName)
        Log.i(TAG,"filesToUpload $filesToUpload");

        showProgress("Uploading media ...",perProgressBar,textViewProgress)
        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload, "",object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                hideProgress(perProgressBar,textViewProgress)
                Log.i(TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {
                hideProgress(perProgressBar,textViewProgress)
                for (i in responses.indices) {
                    Log.i(TAG,"responses ${responses[i]}")
                    //val str = responses[i]
                    textViewProgress.visibility = View.GONE
                    perProgressBar.visibility = View.GONE
                    textViewTitle.visibility = View.VISIBLE
                    //   Log.i(TAG, "RESPONSE $i ${responses[i]}")
                    //submitFile(responses[i],fILETITLE,position)
                    // if ((position + 1) == fileNameList.size) {
                    fileNameList[position].aSSIGNMENTFILE = responses[i]

                    //arrayListItems += responses[i]+","
                    // }

                }
            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                updateProgress(totalpercent, "Uploading file $filenumber", "", perProgressBar,textViewProgress)
                Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })


    }

    fun submitFile(arraylist: String) {

        // postParam.put("ACCADEMIC_ID",student2.accademic_id);
        //        postParam.put("CLASS_ID", student2.class_id);
        //        postParam.put("STUDENT_ID", student2.stu_id);
        //        postParam.put("ASSIGNMENT_ID",String.valueOf(ASSIGNMENT_ID1));
        //
        //        postParam.put("ASSIGNMENT_DETAILS",input_assignment_details.getText().toString());
        //        postParam.put("ASSIGNMENT_FILE",details);

        val url = "OnlineExam/AssignmentSubmit"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("ACCADEMIC_ID", ACADEMICID)
            jsonObject.put("CLASS_ID", CLASSID)
            jsonObject.put("STUDENT_ID", STUDENTID)
            jsonObject.put("ASSIGNMENT_ID", ASSIGNMENT_ID)
            jsonObject.put("ASSIGNMENT_DETAILS", assignmentAnswerText?.text.toString())
            jsonObject.put("ASSIGNMENT_FILE", arraylist)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG, "jsonObject $jsonObject")
//        val mediaType = "application/json; charset=utf-8".toMediaType()
//        val requestBody = jsonObject.toString().toRequestBody(mediaType)

        val requestBody = jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        assignmentDetailsViewModel.getCommonPostFun(url, requestBody)
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
                                    Utils.getSnackBarGreen(
                                        this,
                                        "Assignment Submitted Successfully",
                                        constraintFirstLayout!!
                                    )
                                    arrayListItems = ""
                                    dummyFileName = ArrayList<String>()
                                    initMethod()
                                    // START_QUESTION_TIME = END_QUESTION_TIME
                                }
                                Utils.resultFun(response) == "UPDATED" -> {
                                    Utils.getSnackBarGreen(
                                        this,
                                        "Assignment Updated Successfully",
                                        constraintFirstLayout!!
                                    )
                                    dummyFileName = ArrayList<String>()
                                    initMethod()
                                    // START_QUESTION_TIME = END_QUESTION_TIME
                                }
                                else -> {
                                    Utils.getSnackBar4K(
                                        this,
                                        "Failed While submitting this Assignment",
                                        constraintFirstLayout!!
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

    override fun onViewClick() {
        val extra = Bundle()
        extra.putInt("position", 0)
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val newFragment = SlideshowDialogFragment.newInstance()
        newFragment.arguments = extra
        newFragment.show(ft, newFragment.TAG)
    }

    override fun onOpenFileClick(fILEPATHName: String) {
        Log.i(TAG, "fILEPATHName $fILEPATHName")

        // Create URI
        var dwload =
            File(Utils.getRootDirPath(this)!!+"/catch/parent/Assignment/")

        if (!dwload.exists()) {
            dwload.mkdirs()
        } else {
            //  Toast.makeText(getActivity(), "Already existing", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Already existing")
        }

        val mFile = File("$dwload/$fILEPATHName")
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
            Utils.getSnackBar4K(this,"File format doesn't support",constraintFirstLayout!!)
        }
    }

    fun updateProgress(
        progress: Int,
        title: String?,
        msg: String?,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView
    ) {
        Log.i(TAG,"updateProgress $progress")
//        perProgressBar.setTitle(title)
//        perProgressBar.setMessage(msg)
        textViewProgress.text = "$progress %"
        perProgressBar.progress = progress
    }

    fun showProgress(str: String?, perProgressBar: CircularProgressIndicator, textViewProgress: TextView) {
        Log.i(TAG,"showProgress $str")
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

    override fun onStop() {
        super.onStop()
        Global.albumImageList =  ArrayList<CustomImageModel>()
    }
}


class AssignmentFileLocal(
    var aSSIGNMENTFILE: String, var aSSIGNMENTFILEID: Int,
    var aSSIGNMENTSUBMITID: Int, var fileType: String, var noDeleteImage: Int
)


interface AssignmentClickListener {
    fun onDeleteClick(
        position: Int,
        assignmentFileList: AssignmentFileLocal
    )

    fun onFileUploadClick(
        position: Int,
        assignmentFileList: AssignmentFileLocal
    )

    fun onViewClick()
    fun onOpenFileClick(fILEPATHName: String)

    fun onFileUploadProgress(
        position: Int, fILEPATHName: String,
        textViewTitle: TextView,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView)
}
