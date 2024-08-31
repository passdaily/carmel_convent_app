package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_event

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import androidx.lifecycle.Observer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreateEventBinding
import info.passdaily.camrelconvertapp.databinding.DialogCreateLeaveBinding
import info.passdaily.camrelconvertapp.databinding.DialogCreateStaffLeaveBinding
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.model.AcademicListModel
import info.passdaily.camrelconvertapp.model.EventListModel
import info.passdaily.camrelconvertapp.model.GetYearClassExamModel
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.permission.sdk29AndUp
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.CreateAssignmentDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_pta.DialogCreatePta
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_pta.PTADetailsListener
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_staff.DialogCreateStaff
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.FileList
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

@Suppress("DEPRECATION")
class UpdateEventDialog : DialogFragment {


    companion object {
        var TAG = "UpdateEventDialog"
    }

    private var _binding: DialogCreateEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var manageEventViewModel: ManageEventViewModel

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var fromStr = ""
    var toStr = ""

    var fromDateCompare = ""
    var toDateCompare = ""

   // var type = arrayOf("Select Event", "Image Event", "Video Event", "Youtube Event")

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private var readPermission = false
    private var writePermission = false
    var getYears = ArrayList<AcademicListModel.AccademicDetail>()
    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var adminRole = 0
    var schoolId = 0
    var imageSelection = ""
    var videoSelection = ""
    var initPos = false
    var fileNameList = ArrayList<FileList>()
//    var filevideoNameList = ArrayList<FileList>()

    var toolbar: Toolbar? = null
    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerType: AppCompatSpinner? = null
    var typeStr = "-1"

    var editTextTitle: TextInputEditText? = null
    var editTextDesc: TextInputEditText? = null

    lateinit var eventList : EventListModel.Event

    var constraintVideoUpload: ConstraintLayout? = null
    var textInputLayoutYoutube: TextInputLayout? = null
    var editYoutubeLink: TextInputEditText? = null

    var buttonSubmit: AppCompatButton? = null

    var constraintLeave: ConstraintLayout? = null

    var constraintLayoutUpload: ConstraintLayout? = null
    var constraintImageShow: ConstraintLayout? = null
    var imageViewShow: ImageView? = null
    var textViewDelete: TextView? = null

    ///video
    var constraintVideoShow: ConstraintLayout? = null
    var videoViewShow : ImageView? = null
    var textViewVidDelete : TextView? = null


    lateinit var eventClickListener: EventPClickListener

    var aCCADEMICID = 0

    var pb: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    constructor()
    constructor(eventPClickListener: EventPClickListener,eventList : EventListModel.Event) {
        this.eventClickListener = eventPClickListener
        this.eventList = eventList

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE
        schoolId = user[0].SCHOOL_ID

        manageEventViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ManageEventViewModel::class.java]
        _binding = DialogCreateEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pb = ProgressDialog(requireActivity())
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Loading...")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)
        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_white)
        toolbar?.title = "Update Event"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.white))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }
        constraintLayoutUpload = binding.constraintLayoutUpload
        constraintImageShow = binding.constraintImageShow
        imageViewShow = binding.imageViewShow
        textViewDelete = binding.textViewDelete

        constraintVideoUpload = binding.constraintVideoUpload
        textInputLayoutYoutube = binding.textInputLayoutYoutube
        editYoutubeLink = binding.editYoutubeLink

        constraintVideoShow = binding.constraintVideoShow
        videoViewShow = binding.videoViewShow
        textViewVidDelete = binding.textViewVidDelete

        spinnerAcademic = binding.spinnerAcademic
        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                aCCADEMICID = getYears[position].aCCADEMICID
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        // var type = arrayOf("Select Event", "Image Event", "Video Event", "Youtube Event")
        spinnerType = binding.spinnerType
        var type = when (eventList.eVENTTYPE) {
            1 -> {
                "Image Event" + "~" + "Video Event" + "~" + "Youtube Event"
            }
            2 -> {
                "Video Event" + "~" + "Youtube Event" + "~" +"Image Event"
            }
            3 -> {
                "Youtube Event" + "~" + "Image Event" + "~" + "Video Event"
            }
            else -> { "Image Event" + "~" + "Video Event" + "~" + "Youtube Event"}
        }
        var type_ = type.split("~".toRegex()).toTypedArray()
        val status =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, type_)
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType?.adapter = status
        spinnerType?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                typeStr = eventList.eVENTTYPE.toString()
//                typeStr = gender_[position]
                if(type_[position] == "Image Event" && initPos){
                    typeStr = "1"
                    constraintVideoUpload?.visibility = View.GONE
                    textInputLayoutYoutube?.visibility = View.GONE
                    constraintVideoShow?.visibility = View.GONE
                    videoSelection = ""
                    editYoutubeLink?.setText("")
                }else if(type_[position] == "Video Event" && initPos){
                    typeStr = "2"
                    constraintVideoUpload?.visibility = View.VISIBLE
                    textInputLayoutYoutube?.visibility = View.GONE
                    constraintVideoShow?.visibility = View.GONE
                    videoSelection = ""
                }else if(type_[position] == "Youtube Event" && initPos){
                    typeStr = "3"
                    constraintVideoUpload?.visibility = View.GONE
                    constraintVideoShow?.visibility = View.GONE
                    textInputLayoutYoutube?.visibility = View.VISIBLE
                    editYoutubeLink?.setText("")
                }

                initPos = true
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        editTextTitle = binding.editTextTitle
        editTextDesc = binding.editTextDesc

        editTextTitle?.setText(eventList.eVENTTITLE)
        editTextDesc?.setText(eventList.eVENTDESCRIPTION)


        constraintLeave = binding.constraintLeave

        if(eventList.eVENTFILE.isNotEmpty()) {
            imageSelection = eventList.eVENTFILE
            constraintLayoutUpload?.visibility = View.GONE
            constraintImageShow?.visibility = View.VISIBLE
            Glide.with(requireActivity())
                .load(Global.event_url + "/EventFile/" + eventList.eVENTFILE)
                .into(imageViewShow!!)
        }

        if(eventList.eVENTLINKFILE.isNotEmpty()){
            videoSelection =  eventList.eVENTLINKFILE
            if(eventList.eVENTTYPE == 2){
                constraintVideoUpload?.visibility = View.GONE
                constraintVideoShow?.visibility = View.VISIBLE
                textInputLayoutYoutube?.visibility = View.GONE
                Glide.with(requireActivity())
                    .load(Global.event_url + "/EventFile/" + eventList.eVENTLINKFILE)
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_video_library)
                    )
                    .priority(Priority.HIGH)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .thumbnail(0.5f)
                    .into(videoViewShow!!)
            }else if(eventList.eVENTTYPE == 3){
                constraintVideoUpload?.visibility = View.GONE
                constraintVideoShow?.visibility = View.GONE
                textInputLayoutYoutube?.visibility = View.VISIBLE
                editYoutubeLink?.setText(eventList.eVENTLINKFILE)
            }

        }



        textViewDelete?.setOnClickListener {
            constraintLayoutUpload?.visibility = View.VISIBLE
            constraintImageShow?.visibility = View.GONE
            imageSelection = ""
            fileNameList = ArrayList<FileList>()
        }

        textViewVidDelete?.setOnClickListener {
            constraintVideoUpload?.visibility = View.VISIBLE
            constraintVideoShow?.visibility = View.GONE
            videoSelection = ""
           // filevideoNameList = ArrayList<FileList>()
        }

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission


            }


        constraintVideoUpload?.setOnClickListener {

            if (requestPermission()) {
                //                selectImage()
                val intent = Intent(Intent.ACTION_PICK); // or ACTION_OPEN_DOCUMENT //ACTION_GET_CONTENT
               // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "video/*"
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startForVideoResult.launch(intent)

            }
        }


        binding.constraintLayoutUpload.setOnClickListener {

            if (requestPermission()) {
                //                selectImage()
                val imageCollection = sdk29AndUp {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val intent = Intent(Intent.ACTION_PICK, imageCollection)
                //     intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "image/*"
                startForResult.launch(intent)

            }
        }

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.setOnClickListener {
            if(typeStr == "3"){
                videoSelection = editYoutubeLink?.text.toString()
            }
            if (manageEventViewModel.validateField(
                    editTextTitle!!.text.toString(),
                    "Enter Event Title",
                    requireActivity(),
                    constraintLeave!!
                )
                && manageEventViewModel.validateField(
                    imageSelection,
                    "Upload Thumbnail Image",
                    requireActivity(),
                    constraintLeave!!
                )
                && manageEventViewModel.validateFieldVideo(
                    typeStr,
                    videoSelection,
                    "",
                    requireActivity(),
                    constraintLeave!!
                )
                && manageEventViewModel.validateFieldStr(
                    typeStr,
                    "Select Event type",
                    requireActivity(),
                    constraintLeave!!
                )
            ) {
                if(fileNameList.isNotEmpty()){
                    pb?.show()
                    onFileUploadClick(imageSelection)
                }else{
                    pb?.show()
                    submitFile(imageSelection)
                }

            }

        }

        val constraintLeave = binding.constraintLeave
        constraintLeave.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLeave.windowToken, 0)
        }

        initFunction()

    }

    private fun initFunction() {
        manageEventViewModel.getAcademicList(0, schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getYears = response.accademicDetails as ArrayList<AcademicListModel.AccademicDetail>
                            var years = Array(getYears.size) { "" }
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                                if (eventList.aCCADEMICID == getYears[i].aCCADEMICID) {
                                    aCCADEMICID = i
                                }
                            }
                            if (spinnerAcademic != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademic?.adapter = adapter
                            }
                            spinnerAcademic?.setSelection(aCCADEMICID)


//                            var years = Array(getYears.size) { "" }
//                            for (i in getYears.indices) {
//                                years[i] = getYears[i].aCCADEMICTIME
//                            }
//                            if (spinnerAcademic != null) {
//                                val adapter = ArrayAdapter(
//                                    requireActivity(),
//                                    android.R.layout.simple_spinner_dropdown_item,
//                                    years
//                                )
//                                spinnerAcademic?.adapter = adapter
//                            }
                            Log.i(TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }


    fun onFileUploadClick(selectedFilePath: String) {

        var SERVER_URL = "Event/UploadThumbnail"

        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(TAG, "selectedFilePath $selectedFilePath");
        filesToUpload[0] = File(selectedFilePath)
        Log.i(TAG, "filesToUpload $filesToUpload");

        //  showProgress("Uploading media ...",perProgressBar,textViewProgress)
        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload, "", object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                pb?.dismiss()
                //   hideProgress(perProgressBar,textViewProgress)
                Log.i(TAG, "onError ")
            }

            override fun onFinish(responses: Array<String>) {
                //  hideProgress(perProgressBar,textViewProgress)
                submitFile(responses[0])
            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                //  updateProgress(totalpercent, "Uploading file $filenumber", "", perProgressBar,textViewProgress)
                pb?.setMessage("Uploading..")
                Log.i(TAG, "Progress Status $currentpercent $totalpercent $filenumber")
            }
        })


    }


    fun onVideoUploadClick(selectedFilePath: String) {

        var SERVER_URL = "Event/UploadFiles"

        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(TAG, "selectedFilePath $selectedFilePath");
        filesToUpload[0] = File(selectedFilePath)
        Log.i(TAG, "filesToUpload $filesToUpload");

        //  showProgress("Uploading media ...",perProgressBar,textViewProgress)
        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload, "", object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                pb?.dismiss()
                //   hideProgress(perProgressBar,textViewProgress)
                Log.i(TAG, "onError ")
            }

            override fun onFinish(responses: Array<String>) {
                //  hideProgress(perProgressBar,textViewProgress)
                pb?.dismiss()
                videoSelection = responses[0]
            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                //  updateProgress(totalpercent, "Uploading file $filenumber", "", perProgressBar,textViewProgress)
                pb?.setMessage("Uploading $currentpercent / 100")
                Log.i(TAG, "Progress Status $currentpercent $totalpercent $filenumber")
            }
        })


    }


    private fun submitFile(uploadedFileName: String) {
//        if(typeStr == "3"){
//            videoSelection = editYoutubeLink?.text.toString()
//        }

        var url = "Event/EventSetById"
        //"ADMIN_ID" : 1,
        //"ACCADEMIC_ID" : 10,
        //"EVENT_TITLE" : "Abdul Kalaam Motivational Speech",
        //"EVENT_DESCRIPTION" : "Abdul Kalaam Motivational Speech  For Young People to understand How to Handle Failures",
        //"EVENT_FILE" :"kalaam.jpeg",
        //"EVENT_TYPE" : "2",
        //"EVENT_LINK_FILE" : "kalaamspeech.mp4",
        //"SCHOOL_ID" : 1,
        //"EVENT_ID" : 106
        //}

        val jsonObject = JSONObject()
        try {
            jsonObject.put("ADMIN_ID", adminId)
            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
            jsonObject.put("EVENT_TITLE", editTextTitle?.text.toString())
            jsonObject.put("EVENT_DESCRIPTION", editTextDesc?.text.toString())
            jsonObject.put("EVENT_FILE", uploadedFileName)
            jsonObject.put("EVENT_TYPE", typeStr)
            //EVENT_TYPE
            jsonObject.put("EVENT_LINK_FILE", videoSelection)
            jsonObject.put("SCHOOL_ID", schoolId)
            jsonObject.put("EVENT_ID", eventList.eVENTID)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG, "jsonObject $jsonObject")
        val accademicRe =
            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        manageEventViewModel.getCommonPostFun(url, accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(DialogCreatePta.TAG, "resource ${resource.message}")
                    Log.i(DialogCreatePta.TAG, "errorBody ${resource.data?.errorBody()}")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            pb?.dismiss()
                            //    progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(
                                        requireActivity(),
                                        "Event Created Successfully",
                                        constraintLeave
                                    )
                                    eventClickListener.onShowMessage("")
                                    cancelFrg()
                                }
                                Utils.resultFun(response) == "FAIL" -> {
                                    Utils.getSnackBar4K(
                                        requireActivity(),
                                        "Event Creation Failed",
                                        constraintLeave
                                    )
                                }
                            }
                        }
                        Status.ERROR -> {
                            pb?.dismiss()
                            //   progressStop()
                            Utils.getSnackBar4K(
                                requireActivity(),
                                "Please try again after sometime",
                                constraintLeave
                            )
                        }
                        Status.LOADING -> {
                            //      progressStart()
                            Log.i(CreateAssignmentDialog.TAG, "loading")
                        }
                    }
                }
            })

    }

    ///permission Part
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data
                Log.i(CreateAssignmentDialog.TAG, "data $data")

                if (data?.data != null) {
                    val imageUri: Uri? = data.data
                    imageSelection =
                        FileUtils.getReadablePathFromUri(requireActivity(), imageUri!!)!!
                    fileNameList.add(
                        FileList(
                            0,
                            imageUri.toString(),
                            "",
                            "Local",
                            0
                        )
                    )
                    constraintLayoutUpload?.visibility = View.GONE
                    constraintImageShow?.visibility = View.VISIBLE
                    Glide.with(requireActivity())
                        .load(imageUri)
                        .into(imageViewShow!!)
                }

            }

        }


    private val startForVideoResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data
                Log.i(CreateAssignmentDialog.TAG, "data $data")

                if (data?.data != null) {
                    val imageUri: Uri? = data.data
                    var videoSelection =
                        FileUtils.getReadablePathFromUri(requireActivity(), imageUri!!)!!
//                    filevideoNameList.add(
//                        FileList(
//                            0,
//                            imageUri.toString(),
//                            "",
//                            "Local",
//                            0
//                        )
//                    )
                    constraintVideoUpload?.visibility = View.GONE
                    constraintVideoShow?.visibility = View.VISIBLE
                    textInputLayoutYoutube?.visibility = View.GONE
                    try {
                        val thumb = ThumbnailUtils.createVideoThumbnail(
                            videoSelection,
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )
                        videoViewShow?.setImageBitmap(thumb)
                    } catch (e: java.lang.Exception) {
                        Log.i("TAG", "Exception $e")
                    }
                    pb?.show()
                    onVideoUploadClick(videoSelection)
                }

            }

        }

    fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }


    }

    fun requestPermission(): Boolean {

        var hasReadPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        } else {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }


        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireActivity(),
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
            } else {
                permissionToRequests.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionToRequests.isNotEmpty()) {
            permissionsLauncher.launch(permissionToRequests.toTypedArray())
        }

        return permissions
    }


}