package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.descriptive_exam.desc_exam_question

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
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreateDescriptiveQuestionBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.FileUtils
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.permission.sdk29AndUp
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.descriptive_exam.CreateDescriptiveExam
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.descriptive_exam.DescQuestionTabListener
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.descriptive_exam.DescriptiveExamStaffViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

@Suppress("DEPRECATION")
class CreateDescriptiveQuestion : DialogFragment {

    lateinit var descQuestionListener: DescQuestionTabListener

    companion object {
        var TAG = "CreateDescriptiveQuestion"
    }

    private var _binding: DialogCreateDescriptiveQuestionBinding? = null
    private val binding get() = _binding!!


    private lateinit var descriptiveExamStaffViewModel: DescriptiveExamStaffViewModel

    var SERVER_URL = "OnlineDExam/UploadFiles"
    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0
    var eXAMID = 0
    var qUESTIONTYPEID = 0
    var typeStr = ""

    var toolbar: Toolbar? = null
    var constraintLayoutContent: ConstraintLayout? = null

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null
    var spinnerSubject: AppCompatSpinner? = null
    var spinnerExam: AppCompatSpinner? = null
    var spinnerQuestionType: AppCompatSpinner? = null


    var spinnerStatus: AppCompatSpinner? = null

    var textStartTime: TextInputEditText? = null
    var textEndTime: TextInputEditText? = null

    var editTextTitle: TextInputEditText? = null
    var editTextMarks: TextInputEditText? = null

    var buttonSubmit: AppCompatButton? = null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()
    var getDescriptiveExam = ArrayList<DescriptiveExamListStaffModel.OnlineExam>()

    var questionType = ArrayList<QuestionTypeModel.QuestionType>()

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var readPermission = false
    private var writePermission = false


    var constraintLayoutUpload: ConstraintLayout? = null
    var uploadTypeImage: ImageView? = null
    var uploadTypeVoice: ImageView? = null
    var uploadImage : ImageView? = null
    var imageViewClose : CardView? = null

    var imagePath = ""
    var messageForQuestionType = ""

    var maxCount = 1
    var maxCountSelection = 1
    var fileNameList = ArrayList<String>()

    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var adminRole = 0
    var schoolId = 0

    var pb: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    constructor(
        descQuestionListener: DescQuestionTabListener,
        aCCADEMICID: Int,
        cLASSID: Int,
        sUBJECTID: Int,
        eXAMID: Int
    ) {
        this.descQuestionListener = descQuestionListener
        this.aCCADEMICID = aCCADEMICID
        this.cLASSID = cLASSID
        this.sUBJECTID = sUBJECTID
        this.eXAMID = eXAMID
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        pb = ProgressDialog(requireActivity())
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Uploading...")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE


        descriptiveExamStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[DescriptiveExamStaffViewModel::class.java]

        _binding = DialogCreateDescriptiveQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("IntentReset")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID
        constraintLayoutContent = binding.constraintLayoutContent

        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_white)
        toolbar?.title = "Create Descriptive Question"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.white))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass
        spinnerSubject = binding.spinnerSubject
        spinnerExam = binding.spinnerExam
        spinnerQuestionType = binding.spinnerQuestionType

        editTextTitle = binding.editTextTitle
        editTextMarks = binding.editTextMarks
       

        constraintLayoutUpload = binding.constraintLayoutUpload
        uploadTypeImage = binding.uploadTypeImage
        uploadImage = binding.uploadImage
        imageViewClose = binding.imageViewClose

       

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

        spinnerClass?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                cLASSID = getClass[position].cLASSID
                getSubjectList(cLASSID)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        spinnerSubject?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                sUBJECTID = getSubject[position].sUBJECTID
                getExamQuestion(aCCADEMICID, cLASSID, sUBJECTID)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        spinnerExam?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                eXAMID = getDescriptiveExam[position].eXAMID
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        spinnerQuestionType?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                qUESTIONTYPEID = questionType[position].qUESTIONTYPEID
                when (qUESTIONTYPEID) {
                    1 -> {
                        constraintLayoutUpload?.visibility = View.GONE
                    }
                    2 -> {
                        messageForQuestionType = "Please Select Image file"
                        constraintLayoutUpload?.visibility = View.VISIBLE
                        uploadTypeImage?.visibility = View.VISIBLE
                        uploadTypeImage?.setImageResource(R.drawable.ic_upload_icon)
                    }
                    3 -> {
                        messageForQuestionType = "Please Select audio file"
                        constraintLayoutUpload?.visibility = View.VISIBLE
                        uploadTypeImage?.visibility = View.VISIBLE
                        uploadTypeImage?.setImageResource(R.drawable.ic_audio_img)
                    }
                    4 -> {
                        messageForQuestionType = "Please Select video file"
                        constraintLayoutUpload?.visibility = View.VISIBLE
                        uploadTypeImage?.visibility = View.VISIBLE
                        uploadTypeImage?.setImageResource(R.drawable.ic_upload_icon)
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        uploadTypeImage?.setOnClickListener {
            when (qUESTIONTYPEID) {
                2 -> {
                    if (requestPermission()) {
                        if (fileNameList.size < maxCount) {
                            maxCountSelection = maxCount - fileNameList.size
                            Toast.makeText(requireActivity(), "Select $maxCountSelection ", Toast.LENGTH_SHORT).show()

                            val imageCollection = sdk29AndUp {
                                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            val intent = Intent(Intent.ACTION_PICK, imageCollection)
                            intent.putExtra(Intent.CATEGORY_OPENABLE, true)
                            intent.type = "image/*"
                            startForResult.launch(intent)
                        } else {
                            Utils.getSnackBar4K(requireActivity(), "Maximum Count Reached", constraintLayoutContent!!)
                        }
                    }
                }
                3 -> {
                    if (requestPermission()) {
                        mimeTypeFun("audio/*",Intent.ACTION_GET_CONTENT)
                    }
                }
                4 -> {
                    if (requestPermission()) {
                        mimeTypeFun("video/*",Intent.ACTION_PICK)
                    }
                }
            }
        }
        imageViewClose?.setOnClickListener {
            imagePath = ""
            fileNameList = ArrayList<String>()
            uploadImage?.visibility = View.GONE
            uploadTypeImage?.visibility = View.VISIBLE
            imageViewClose?.visibility = View.GONE
        }

    
        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }



        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.text = requireActivity().resources.getString(R.string.create)
        buttonSubmit?.setOnClickListener {
            if(qUESTIONTYPEID == 1){
                if(descriptiveExamStaffViewModel.validateField(editTextTitle!!,
                        "Give Question",requireActivity(),constraintLayoutContent!!)
                    && descriptiveExamStaffViewModel.validateField(editTextMarks!!,
                        "",requireActivity(),constraintLayoutContent!!)){

                    submitFile("")
                }
            }else {
                if(descriptiveExamStaffViewModel.validateField(editTextTitle!!, "Give Question",requireActivity(),constraintLayoutContent!!)
                    && descriptiveExamStaffViewModel.validateField(editTextMarks!!,
                        "Give Question",requireActivity(),constraintLayoutContent!!)
                    && descriptiveExamStaffViewModel.validateFieldStr(imagePath,
                        messageForQuestionType,requireActivity(),constraintLayoutContent!!)){
                    submitFile(imagePath)
                    //onFileUploadClick(imagePath)
                }
            }
        }
        val constraintLayoutContent = binding.constraintLayoutContent
        constraintLayoutContent.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLayoutContent.windowToken, 0)
        }


        initFunction()
        getQuestionTypeStaff()
        Utils.setStatusBarColor(requireActivity())
    }



    fun onFileUploadClick(imagePathUri: String) {

        var selectedFilePath = FileUtils.getReadablePathFromUri(requireActivity(), imagePathUri.toUri())


        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(TAG,"selectedFilePath $selectedFilePath");
        filesToUpload[0] = File(selectedFilePath!!)
        Log.i(TAG,"filesToUpload $filesToUpload");

        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload,"",object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                pb?.dismiss()
                Log.i(TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {
                pb?.dismiss()
                imagePath = responses[0]
                Log.i(TAG,"commentUplodedFile $imagePath")
                //submitFile(commentUplodedFile)

            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                pb?.show()
                pb?.setMessage("Uploading : $totalpercent / 100")
             //   Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })

//            Log.i(TAG, "fileName  $selectedFilePath")
//
//            var imagenPerfil = if (selectedFilePath.toString().contains(".jpg") || selectedFilePath.toString()
//                    .contains(".jpeg")
//                || selectedFilePath.toString().contains(".png") || selectedFilePath.toString()
//                    .contains(".JPG") || selectedFilePath.toString().contains(".JPEG")
//                || selectedFilePath.toString().contains(".PNG")
//            ) {
//                val file1 = File(selectedFilePath!!)
//                val requestFile: RequestBody =
//                    file1.asRequestBody("image/*".toMediaTypeOrNull())
//                MultipartBody.Part.createFormData(
//                    "STUDY_METERIAL_FILE",
//                    file1.name,
//                    requestFile
//                )
//            } else {
//                val requestFile: RequestBody = RequestBody.create(
//                    "multipart/form-data".toMediaTypeOrNull(),
//                    selectedFilePath!!
//                )
//                // MultipartBody.Part is used to send also the actual file name
//                MultipartBody.Part.createFormData(
//                    "STUDY_METERIAL_FILE",
//                    selectedFilePath,
//                    requestFile
//                )
//            }
//
//
//            val apiInterface = RetrofitClientStaff.create().fileUploadAssignment(
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
//                        //  fileNames.add(response!!.dETAILS)
//                        submitFile(response?.dETAILS!!)
//                    }
//
//                }
//
//                override fun onFailure(call: Call<FileResultModel>, t: Throwable) {
//                    Log.i(TAG, "Throwable  $t")
//                }
//            })


    }

    private fun submitFile(fileName: String) {

        val url = "OnlineDExam/AddDQuetion"
        val jsonObject = JSONObject()
        try {
            //postParam.put("ADMIN_ID",Global.Admin_id);
            //        postParam.put("EXAM_ID",s_eid);
            //        postParam.put("QUESTION_TYPE_ID", s_qid);
            //        postParam.put("QUESTION_TITLE", input_question.getText().toString());
            //        postParam.put("QUESTION_CONTENT", filename1);
            //        postParam.put("QUESTION_MARK", input_question_marks.getText().toString());
            jsonObject.put("ADMIN_ID", adminId)
            jsonObject.put("EXAM_ID", eXAMID)
            jsonObject.put("QUESTION_TYPE_ID", qUESTIONTYPEID)
            jsonObject.put("QUESTION_TITLE", editTextTitle?.text.toString())
            jsonObject.put("QUESTION_CONTENT", fileName)
            jsonObject.put("QUESTION_MARK", editTextMarks?.text.toString())


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(CreateDescriptiveExam.TAG,"jsonObject $jsonObject")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        descriptiveExamStaffViewModel.getCommonPostFun(url,accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(requireActivity(),"Question Created Successfully",constraintLayoutContent!!)
                                    descQuestionListener.onCreateClick("")
                                    cancelFrg()

                                }
                                Utils.resultFun(response) == "-1" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Question Already Question", constraintLayoutContent!!)
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Failed While Adding Options", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(CreateDescriptiveExam.TAG,"loading")
                        }
                    }
                }
            })


    }

    fun mimeTypeFun(mimeTypes: String, actionPick: String) {
        if (fileNameList.size < maxCount) {
            maxCountSelection = maxCount - fileNameList.size
            Toast.makeText(requireActivity(), "Select $maxCountSelection ", Toast.LENGTH_SHORT).show()

            val intent =
                Intent(actionPick); // or ACTION_OPEN_DOCUMENT //ACTION_GET_CONTENT
            intent.putExtra(Intent.CATEGORY_OPENABLE, true)
            intent.type = mimeTypes;
            startForResult.launch(intent)
        } else {
            Utils.getSnackBar4K(requireActivity(), "Maximum Count Reached", constraintLayoutContent!!)
        }

    }
    ///permission Part
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data
                Log.i(TAG, "data $data")

                if (data?.data != null) {
                    val imageUri: Uri? = data.data
                    imagePath = imageUri!!.toString()
                    fileNameList.add(imagePath)
                }
                if(fileNameList.size >= 1){
                    constraintLayoutUpload?.visibility = View.VISIBLE
                    imageViewClose?.visibility = View.VISIBLE
                    uploadImage?.visibility = View.VISIBLE
                    uploadTypeImage?.visibility = View.GONE

                    val mFile = FileUtils.getReadablePathFromUri(requireActivity(), imagePath.toUri())

                    if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                        || mFile.toString().contains(".png") || mFile.toString()
                            .contains(".JPG") || mFile.toString().contains(".JPEG")
                        || mFile.toString().contains(".PNG")
                    ) {
                        // JPG file
                        Glide.with(requireActivity())
                            .load(imagePath)
                            .into(uploadImage!!)
                    }
                    else if (mFile.toString().contains(".mp3") || mFile.toString()
                            .contains(".wav") || mFile.toString().contains(".ogg")
                        || mFile.toString().contains(".m4a") || mFile.toString()
                            .contains(".aac") || mFile.toString().contains(".wma") ||
                        mFile.toString().contains(".MP3") || mFile.toString()
                            .contains(".WAV") || mFile.toString().contains(".OGG")
                        || mFile.toString().contains(".M4A") || mFile.toString()
                            .contains(".AAC") || mFile.toString().contains(".WMA"))
                    {
                        Glide.with(requireActivity())
                            .load(R.drawable.ic_file_voice)
                            .into(uploadImage!!)
                    }
                    else {
//                        Glide.with(requireActivity())
//                            .load(R.drawable.ic_file_video)
//                            .into(uploadImage!!)
                        try {
                            val thumb = ThumbnailUtils.createVideoThumbnail(
                                mFile!!,
                                MediaStore.Images.Thumbnails.MINI_KIND
                            )
                            uploadImage!!.setImageBitmap(thumb)
                        } catch (e: java.lang.Exception) {
                            Log.i("TAG", "Exception $e")
                        }
                    }
                    onFileUploadClick(imagePath)
                }else{
                    constraintLayoutUpload?.visibility = View.VISIBLE
                    imageViewClose?.visibility = View.GONE
                    uploadImage?.visibility = View.GONE
                    uploadTypeImage?.visibility = View.VISIBLE
                    Utils.getSnackBar4K(requireActivity(), "Selected More then one", constraintLayoutContent!!)
                }

            }

        }

    private fun initFunction() {
        descriptiveExamStaffViewModel.getYearClassExam(adminId,schoolId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            var aCCADEMIC_ID = 0
                            var cLASS_ID = 0
                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size) { "" }
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                                if (aCCADEMICID == getYears[i].aCCADEMICID) {
                                    aCCADEMIC_ID = i
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
                            spinnerAcademic?.setSelection(aCCADEMIC_ID)

                            getClass = response.classList as ArrayList<GetYearClassExamModel.Class>
                            var classX = Array(getClass.size) { "" }
                            for (i in getClass.indices) {
                                classX[i] = getClass[i].cLASSNAME
                                if (cLASSID == getClass[i].cLASSID) {
                                    cLASS_ID = i
                                }
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    classX
                                )
                                spinnerClass?.adapter = adapter
                            }
                            spinnerClass?.setSelection(cLASS_ID)

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

    fun getSubjectList(cLASSID: Int) {
        descriptiveExamStaffViewModel.getSubjectStaff(cLASSID, adminId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            var sUBJECT_ID = 0
                            getSubject = response.subjects as ArrayList<SubjectsModel.Subject>
                            var subject = Array(getSubject.size) { "" }
                            if (subject.isNotEmpty()) {
                                for (i in getSubject.indices) {
                                    subject[i] = getSubject[i].sUBJECTNAME
                                    if (sUBJECTID == getSubject[i].sUBJECTID) {
                                        sUBJECT_ID = i
                                    }
                                }

                            }
                            if (spinnerSubject != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    subject
                                )
                                spinnerSubject?.adapter = adapter
                            }
                            spinnerSubject?.setSelection(sUBJECT_ID)

                            Log.i(TAG, "getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            getSubject = ArrayList<SubjectsModel.Subject>()
                            Log.i(TAG, "getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    fun getExamQuestion(aCCADEMICID: Int, cLASSID: Int, sUBJECTID: Int) {
        descriptiveExamStaffViewModel.getDescOnlineExamList(
            aCCADEMICID,
            cLASSID,
            sUBJECTID,
            schoolId
        )
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            var eXAM_ID = 0
                            getDescriptiveExam =
                                response.desOnlineExamList as ArrayList<DescriptiveExamListStaffModel.OnlineExam>
                            var exam = Array(getDescriptiveExam.size) { "" }
                            if (exam.isNotEmpty()) {
                                for (i in getDescriptiveExam.indices) {
                                    exam[i] = getDescriptiveExam[i].eXAMNAME
                                    if (eXAMID == getDescriptiveExam[i].eXAMID) {
                                        eXAM_ID = i
                                    }
                                }
                            }
                            if (spinnerExam != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    exam
                                )
                                spinnerExam?.adapter = adapter
                            }
                            spinnerExam?.setSelection(eXAM_ID)

                            Log.i(TAG, "getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "getSubjectList ERROR")
                        }
                        Status.LOADING -> {
//                            shimmerViewContainer?.visibility = View.VISIBLE
//                            getSubject = ArrayList<SubjectsModel.Subject>()
//                            getObjectiveExam = ArrayList<ObjExamListStaffModel.OnlineExam>()
//                            recyclerViewVideo?.adapter =
//                                ObjectiveExamStaffFragment.ObjectiveExamAdapter(
//                                    this,
//                                    getObjectiveExam,
//                                    requireActivity(),
//                                    TAG
//                                )

                            Log.i(TAG, "getSubjectList LOADING")
                        }
                    }
                }
            })
    }


    fun getQuestionTypeStaff() {
        descriptiveExamStaffViewModel.getQuestionTypeStaff(adminId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            questionType =
                                response.questionType as ArrayList<QuestionTypeModel.QuestionType>
                            var question = Array(questionType.size) { "" }
                            if (question.isNotEmpty()) {
                                for (i in questionType.indices) {
                                    question[i] = questionType[i].qUESTIONTYPENAME
                                }

                            }
                            if (spinnerQuestionType != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    question
                                )
                                spinnerQuestionType?.adapter = adapter
                            }

                            Log.i(TAG, "getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            getSubject = ArrayList<SubjectsModel.Subject>()
                            Log.i(TAG, "getSubjectList LOADING")
                        }
                    }
                }
            })
    }


    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }
    }

    private fun progressStart() {
        val dialog1 = ProgressBarDialog()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.isCancelable = false
        dialog1.show(transaction, ProgressBarDialog.TAG)
    }

    fun progressStop() {
        val fragment: ProgressBarDialog? =
            requireActivity().supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
        if (fragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment)
                .commitAllowingStateLoss()
        }
    }


    fun requestPermission(): Boolean {

        var hasReadPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        }else {
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
            }else {
                permissionToRequests.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionToRequests.isNotEmpty()) {
            permissionsLauncher.launch(permissionToRequests.toTypedArray())
        }

        return permissions
    }
}