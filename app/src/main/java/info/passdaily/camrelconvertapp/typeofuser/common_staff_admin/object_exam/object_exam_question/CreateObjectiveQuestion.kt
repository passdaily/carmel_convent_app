package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.object_exam_question

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
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
import info.passdaily.camrelconvertapp.databinding.DialogCreateObjectiveQuestionBinding
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
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.ObjQuestionTabListener
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.ObjectiveExamStaffViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

@Suppress("DEPRECATION")
class CreateObjectiveQuestion : DialogFragment {

    lateinit var objQuestionTabListener: ObjQuestionTabListener

    companion object {
        var TAG = "CreateObjectiveQuestion"
    }

    private var _binding: DialogCreateObjectiveQuestionBinding? = null
    private val binding get() = _binding!!


    private lateinit var objectiveExamStaffViewModel: ObjectiveExamStaffViewModel

    var SERVER_URL = "OnlineExam/UploadFiles"
    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0
    var oEXAMID = 0
    var qUESTIONTYPEID = 0
    var adminId = 0
    var schoolId = 0
    var adminRole = 0
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
    var editTextOption1: TextInputEditText? = null
    var editTextOption2: TextInputEditText? = null
    var editTextOption3: TextInputEditText? = null
    var editTextOption4: TextInputEditText? = null


    var fromStr = ""
    var toStr = ""

    var startDate = ""
    var endDate = ""
    var buttonSubmit: AppCompatButton? = null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()
    var getObjectiveExam = ArrayList<ObjExamListStaffModel.OnlineExam>()

    var questionType = ArrayList<QuestionTypeModel.QuestionType>()

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var readPermission = false
    private var writePermission = false

    private lateinit var localDBHelper: LocalDBHelper

    var type = arrayOf("Unpublished", "Published")

    var startTime = ""
    var endTime = ""
    var rightOption = ""

    var radioGroup: RadioGroup? = null
    var radioButton1: RadioButton? = null
    var radioButton2: RadioButton? = null
    var radioButton3: RadioButton? = null
    var radioButton4: RadioButton? = null

    var constraintLayoutUpload: ConstraintLayout? = null
    var uploadTypeImage: ImageView? = null
    var uploadTypeVoice: ImageView? = null
    var uploadImage : ImageView? = null
    var imageViewClose : CardView? = null

    var imagePath = ""
    var messageForQuestionType = ""

    var pb: ProgressDialog? = null

    var maxCount = 1
    var maxCountSelection = 1
    var fileNameList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    constructor(
        objQuestionTabListener: ObjQuestionTabListener,
        aCCADEMICID: Int,
        cLASSID: Int,
        sUBJECTID: Int,
        oEXAMID: Int
    ) {
        this.objQuestionTabListener = objQuestionTabListener
        this.aCCADEMICID = aCCADEMICID
        this.cLASSID = cLASSID
        this.sUBJECTID = sUBJECTID
        this.oEXAMID = oEXAMID
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        objectiveExamStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ObjectiveExamStaffViewModel::class.java]

        _binding = DialogCreateObjectiveQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pb = ProgressDialog(requireActivity())
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Uploading...")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE
        schoolId = user[0].SCHOOL_ID

        constraintLayoutContent = binding.constraintLayoutContent

        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_white)
        toolbar?.title = "Create Objective Question"
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
        editTextOption1 = binding.editTextOption1
        editTextOption2 = binding.editTextOption2
        editTextOption3 = binding.editTextOption3
        editTextOption4 = binding.editTextOption4

        radioGroup = binding.radioGroup
        radioButton1 = binding.radioButton1
        radioButton2 = binding.radioButton2
        radioButton3 = binding.radioButton3
        radioButton4 = binding.radioButton4

        constraintLayoutUpload = binding.constraintLayoutUpload
        uploadTypeImage = binding.uploadTypeImage
        uploadImage = binding.uploadImage
        imageViewClose = binding.imageViewClose

        radioButton1?.isEnabled = false
        radioButton2?.isEnabled = false
        radioButton3?.isEnabled = false
        radioButton4?.isEnabled = false

        editTextOption1?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (editTextOption1?.text.toString().isEmpty()) {
                    editTextOption1?.error = "Option Required"
                    radioButton1!!.text = "Option 1"
                    radioButton1!!.isEnabled = false
                } else {
                    radioButton1!!.isEnabled = true
                    editTextOption1?.setError(null)
                    radioButton1!!.text = s
                }
            }
        })

        editTextOption2?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (editTextOption2?.text.toString().isEmpty()) {
                    editTextOption2?.error = "Option Required"
                    radioButton2!!.text = "Option 2"
                    radioButton2!!.isEnabled = false
                } else {
                    radioButton2!!.isEnabled = true
                    editTextOption2?.error = null
                    radioButton2!!.text = s
                }
            }
        })

        editTextOption3?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (editTextOption3?.text.toString().isEmpty()) {
                    editTextOption3?.error = "Option Required"
                    radioButton3!!.text = "Option 3"
                    radioButton3!!.isEnabled = false
                } else {
                    radioButton3!!.isEnabled = true
                    editTextOption3?.error = null
                    radioButton3!!.text = s
                }
            }
        })

        editTextOption4?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (editTextOption4?.text.toString().isEmpty()) {
                    editTextOption4?.error = "Option Required"
                    radioButton4!!.text = "Option 4"
                    radioButton4!!.isEnabled = false
                } else {
                    radioButton4!!.isEnabled = true
                    editTextOption4?.error = null
                    radioButton4!!.text = s
                }
            }
        })

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
                oEXAMID = getObjectiveExam[position].oEXAMID
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

        radioGroup!!.setOnCheckedChangeListener { group, checkedId ->
            val id = group.checkedRadioButtonId
            when (checkedId) {
                R.id.radioButton1 ->                         // do operations specific to this selection
                    rightOption = radioButton1!!.text.toString()
                R.id.radioButton2 ->                         // do operations specific to this selection
                    rightOption = radioButton2!!.text.toString()
                R.id.radioButton3 ->                         // do operations specific to this selection
                    rightOption = radioButton3!!.text.toString()
                R.id.radioButton4 ->                         // do operations specific to this selection
                    rightOption = radioButton4!!.text.toString()
            }

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
                if(objectiveExamStaffViewModel.validateField(editTextTitle!!, "Give Question",requireActivity(),constraintLayoutContent!!)
                    && objectiveExamStaffViewModel.validateField(editTextOption1!!,
                        "Option 1 is Mandatory",requireActivity(),constraintLayoutContent!!)
                    && objectiveExamStaffViewModel.validateField(editTextOption2!!,
                        "Option 2 is Mandatory",requireActivity(),constraintLayoutContent!!)
                    && objectiveExamStaffViewModel.validateFieldStr(rightOption,
                        "Select Right Option",requireActivity(),constraintLayoutContent!!)){

                    submitFile("")
                }
            } else {
                if(objectiveExamStaffViewModel.validateField(editTextTitle!!, "Give Question",requireActivity(),constraintLayoutContent!!)
                    && objectiveExamStaffViewModel.validateField(editTextOption1!!,
                        "Option 1 is Mandatory",requireActivity(),constraintLayoutContent!!)
                    && objectiveExamStaffViewModel.validateField(editTextOption2!!,
                        "Option 2 is Mandatory",requireActivity(),constraintLayoutContent!!)
                    && objectiveExamStaffViewModel.validateFieldStr(rightOption,
                        "Select Right Option",requireActivity(),constraintLayoutContent!!)
                    && objectiveExamStaffViewModel.validateFieldStr(imagePath,
                        messageForQuestionType,requireActivity(),constraintLayoutContent!!)){
                    submitFile(imagePath)
                   // onFileUploadClick(imagePath)
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

//            var selectedFilePath = FileUtils.getReadablePathFromUri(requireActivity(), imagePath.toUri())
//
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


            var selectedFilePath = FileUtils.getReadablePathFromUri(requireActivity(), imagePathUri.toUri())


            val filesToUpload = arrayOfNulls<File>(1)
            // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
            Log.i(TAG,"selectedFilePath $selectedFilePath");
            filesToUpload[0] = File(selectedFilePath!!)
            Log.i(TAG,"filesToUpload $filesToUpload");

            val fileUploader = FileUploader(adminRole)
            fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload, "",object :
                FileUploader.FileUploaderCallback {
                override fun onError() {
                    pb?.dismiss()
                    Log.i(TAG,"onError ")
                }

                override fun onFinish(responses: Array<String>) {
                    pb?.dismiss()
                    imagePath = responses[0]
                    Log.i(TAG,"commentUplodedFile $imagePath")
                   // submitFile(commentUplodedFile)

                }

                override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                    pb?.show()
                    pb?.setMessage("Uploading : $totalpercent / 100")
                 //   Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
                }
            })

    }

    private fun submitFile(fileName: String) {

        val url = "OnlineExam/AddQuetion"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("ADMIN_ID", adminId)
            jsonObject.put("OEXAM_ID", oEXAMID)
            jsonObject.put("QUESTION_TYPE_ID", qUESTIONTYPEID)
            jsonObject.put("QUESTION_TITLE", editTextTitle?.text.toString())
            jsonObject.put("QUESTION_CONTENT", fileName)

            jsonObject.put("OPTION1_TEXT", editTextOption1?.text.toString())
            jsonObject.put("OPTION2_TEXT", editTextOption2?.text.toString())
            jsonObject.put("OPTION3_TEXT", editTextOption3?.text.toString())
            jsonObject.put("OPTION4_TEXT", editTextOption4?.text.toString())
            jsonObject.put("RIGHT_OPTION_TEXT", rightOption)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        objectiveExamStaffViewModel.getCommonPostFun(url,accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(requireActivity(),"Question Created Successfully",constraintLayoutContent!!)
                                    objQuestionTabListener.onCreateClick("Question Created Successfully")
                                    cancelFrg()
                                }
                                Utils.resultFun(response) == "QUESTIONEXIST" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Question Already Existing", constraintLayoutContent!!)
                                }
                                Utils.resultFun(response) == "QUESTIONFAILED" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Failed While Adding Question", constraintLayoutContent!!)
                                }
                                Utils.resultFun(response) == "OPTIONFAILED" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Failed While Adding Options", constraintLayoutContent!!)
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Failed While Adding Question and Options", constraintLayoutContent!!)
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
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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
                    }else if (mFile.toString().contains(".mp3") || mFile.toString()
                            .contains(".wav") || mFile.toString().contains(".ogg")
                        || mFile.toString().contains(".m4a") || mFile.toString()
                            .contains(".aac") || mFile.toString().contains(".wma") ||
                        mFile.toString().contains(".MP3") || mFile.toString()
                            .contains(".WAV") || mFile.toString().contains(".OGG")
                        || mFile.toString().contains(".M4A") || mFile.toString()
                            .contains(".AAC") || mFile.toString().contains(".WMA")
                    ) {
                        Glide.with(requireActivity())
                            .load(R.drawable.ic_file_voice)
                            .into(uploadImage!!)
                    }else {
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
        objectiveExamStaffViewModel.getYearClassExam(adminId,schoolId)
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
        objectiveExamStaffViewModel.getSubjectStaff(cLASSID, adminId)
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
        objectiveExamStaffViewModel.getOnlineExamListStaff(
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
                            var OEXAM_ID = 0
                            getObjectiveExam =
                                response.onlineExamList as ArrayList<ObjExamListStaffModel.OnlineExam>
                            var exam = Array(getObjectiveExam.size) { "" }
                            if (exam.isNotEmpty()) {
                                for (i in getObjectiveExam.indices) {
                                    exam[i] = getObjectiveExam[i].oEXAMNAME
                                    if (oEXAMID == getObjectiveExam[i].oEXAMID) {
                                        OEXAM_ID = i
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
                            spinnerExam?.setSelection(OEXAM_ID)

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
        objectiveExamStaffViewModel.getQuestionTypeStaff(adminId)
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