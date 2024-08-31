package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.object_exam_question

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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogUpdateObjectiveQuestionBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
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
class UpdateObjectiveQuestion : DialogFragment,ObjectOptionListener {

    lateinit var objQuestionTabListener: ObjQuestionTabListener

    companion object {
        var TAG = "UpdateObjectiveQuestion"
    }

    private var _binding: DialogUpdateObjectiveQuestionBinding? = null
    private val binding get() = _binding!!


    private lateinit var objectiveExamStaffViewModel: ObjectiveExamStaffViewModel

    var SERVER_URL = "OnlineExam/UploadFiles"
    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0
    var oEXAMID = 0
    var qUESTIONTYPEID = 0
    var adminId = 0
    var adminRole = 0
    var schoolId = 0
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

    var pb: ProgressDialog? = null

//    var editTextOption1: TextInputEditText? = null
//    var editTextOption2: TextInputEditText? = null
//    var editTextOption3: TextInputEditText? = null
//    var editTextOption4: TextInputEditText? = null


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

    var getOptionList = ArrayList<OptionListStaffModel.Option>()

    lateinit  var mAdapter : OptionListAdapter

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var readPermission = false
    private var writePermission = false

    private lateinit var localDBHelper: LocalDBHelper

    var type = arrayOf("Unpublished", "Published")

    var startTime = ""
    var endTime = ""
    var rightOption = ""

//    var radioGroup: RadioGroup? = null
//    var radioButton1: RadioButton? = null
//    var radioButton2: RadioButton? = null
//    var radioButton3: RadioButton? = null
//    var radioButton4: RadioButton? = null

    var constraintLayoutUpload: ConstraintLayout? = null
    var uploadTypeImage: ImageView? = null
    var uploadTypeVoice: ImageView? = null
    var uploadImage: ImageView? = null
    var imageViewClose: CardView? = null

    var imagePath = ""
    var messageForQuestionType = ""

  //  var uploadToken = -1

    var maxCount = 1
    var maxCountSelection = 1
    var fileNameList = ArrayList<String>()

    var fabAddOption : ExtendedFloatingActionButton? = null

  // lateinit var optionsList : OptionListStaffModel.Option

    var recyclerViewOption : RecyclerView? = null

    var questionList : QuestionOptionsListModel.Question
    var optionList = ArrayList<QuestionOptionsListModel.Option>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleWhite)
    }

    constructor(
        objQuestionTabListener: ObjQuestionTabListener,
        questionList: QuestionOptionsListModel.Question,
        optionList: ArrayList<QuestionOptionsListModel.Option>,
        aCCADEMICID: Int,
        cLASSID: Int,
        sUBJECTID: Int
    ) {
        this.objQuestionTabListener = objQuestionTabListener
        this.questionList = questionList
        this.optionList = optionList
        this.aCCADEMICID = aCCADEMICID
        this.cLASSID = cLASSID
        this.sUBJECTID = sUBJECTID
       // this.optionsList = sUBJECTID
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

        _binding = DialogUpdateObjectiveQuestionBinding.inflate(inflater, container, false)
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
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_black)
        toolbar?.title = "Update Objective Question"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.black))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass
        spinnerSubject = binding.spinnerSubject
        spinnerExam = binding.spinnerExam
        spinnerQuestionType = binding.spinnerQuestionType

        editTextTitle = binding.editTextTitle
        fabAddOption = binding.fabAddOption
        recyclerViewOption = binding.recyclerViewOption
        recyclerViewOption?.layoutManager = LinearLayoutManager(requireActivity())

        constraintLayoutUpload = binding.constraintLayoutUpload
        uploadTypeImage = binding.uploadTypeImage
        uploadImage = binding.uploadImage
        imageViewClose = binding.imageViewClose

        editTextTitle?.setText(questionList.qUESTIONTITLE)

//        for(i in optionList.indices){
//            var isChecked = false
//            if(optionList[i].iSRIGHTOPTION == 1){
//                isChecked = true
//            }
//            Log.i(TAG,"i $i")
//            when (i) {
//                0 -> {
//                    editTextOption1?.setText(optionList[i].oPTIONTEXT)
//                    radioButton1!!.isEnabled = true
//                    radioButton1!!.isChecked = isChecked
//                    radioButton1!!.text = optionList[i].oPTIONTEXT
//                }
//                1 -> {
//                    editTextOption2?.setText(optionList[i].oPTIONTEXT)
//                    radioButton2!!.isEnabled = true
//                    radioButton2!!.isChecked = isChecked
//                    radioButton2!!.text = optionList[i].oPTIONTEXT
//                }
//                2 -> {
//                    editTextOption3?.setText(optionList[i].oPTIONTEXT)
//                    radioButton3!!.isEnabled = true
//                    radioButton3!!.isChecked = isChecked
//                    radioButton3!!.text = optionList[i].oPTIONTEXT
//
//                }
//                3 -> {
//                    editTextOption4?.setText(optionList[i].oPTIONTEXT)
//                    radioButton4!!.isEnabled = true
//                    radioButton4!!.isChecked = isChecked
//                    radioButton4!!.text = optionList[i].oPTIONTEXT
//                }
//            }
//        }

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
                            Toast.makeText(
                                requireActivity(),
                                "Select $maxCountSelection ",
                                Toast.LENGTH_SHORT
                            ).show()

                            val imageCollection = sdk29AndUp {
                                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            val intent = Intent(Intent.ACTION_PICK, imageCollection)
                            intent.putExtra(Intent.CATEGORY_OPENABLE, true)
                            intent.type = "image/*"
                            startForResult.launch(intent)
                        } else {
                            Utils.getSnackBar4K(
                                requireActivity(),
                                "Maximum Count Reached",
                                constraintLayoutContent!!
                            )
                        }
                    }
                }
                3 -> {
                    if (requestPermission()) {
                        mimeTypeFun("audio/*", Intent.ACTION_GET_CONTENT)
                    }
                }
                4 -> {
                    if (requestPermission()) {
                        mimeTypeFun("video/*", Intent.ACTION_PICK)
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


        if(questionList.qUESTIONCONTENT.isNotEmpty()) {

            constraintLayoutUpload?.visibility = View.VISIBLE
            uploadTypeImage?.visibility = View.GONE
            uploadImage?.visibility = View.VISIBLE
            imageViewClose?.visibility = View.VISIBLE
          //  uploadToken = 1
            imagePath = questionList.qUESTIONCONTENT

            val path: String = questionList.qUESTIONCONTENT
            val mFile = File(path)
            if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                || mFile.toString().contains(".png") || mFile.toString()
                    .contains(".JPG") || mFile.toString().contains(".JPEG")
                || mFile.toString().contains(".PNG")
            ) {

                // JPG file
                Glide.with(requireActivity())
                    .load(Global.event_url + "/OExamFile/" + path)
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_gallery)
                    )
                    .thumbnail(0.5f)
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
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() // .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_voice)
                    )
                    .thumbnail(0.5f)
                    .into(uploadImage!!)
            } else {
                Glide.with(requireActivity())
                    .load(Global.event_url + "/OExamFile/" + path)
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_video_library)
                    )
                    .priority(Priority.HIGH)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .thumbnail(0.5f)
                    .into(uploadImage!!)
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
        buttonSubmit?.setBackgroundResource(R.drawable.round_green400)
        buttonSubmit?.setTextAppearance(
            requireActivity(),
            R.style.RoundedCornerButtonOrange400
        )
        buttonSubmit?.text = requireActivity().resources.getString(R.string.update)
        buttonSubmit?.setOnClickListener {
            if (qUESTIONTYPEID == 1) {
                if (objectiveExamStaffViewModel.validateField(editTextTitle!!,
                        "Give Question", requireActivity(), constraintLayoutContent!!)) {
                    submitFile("")
                }
            } else {
                if (objectiveExamStaffViewModel.validateField(
                        editTextTitle!!, "Give Question", requireActivity(), constraintLayoutContent!!
                    ) && objectiveExamStaffViewModel.validateFieldStr(imagePath,
                        messageForQuestionType, requireActivity(), constraintLayoutContent!!)) {
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
        getOptionList()


        Utils.setStatusBarColor(requireActivity())
    }


    fun onFileUploadClick(imagePathUri: String) {

//        if(uploadToken == 1){
//            submitFile(imagePath)
//        }else  if(uploadToken == 0){
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
                  //  Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
                }
            })
//        }




    }

    private fun submitFile(fileName: String) {

        val url = "OnlineExam/UpdateQuetion"
        val jsonObject = JSONObject()
        try {
            //    postParam.put("ADMIN_ID",Global.Admin_id);
            //        postParam.put("OEXAM_ID",s_eid);
            //        postParam.put("QUESTION_TYPE_ID", s_qid);
            //        postParam.put("QUESTION_TITLE", input_question.getText().toString());
            //        postParam.put("QUESTION_CONTENT", filename1);
            //        postParam.put("QUESTION_ID", QUESTION_ID);
            jsonObject.put("ADMIN_ID", adminId)
            jsonObject.put("OEXAM_ID", oEXAMID)
            jsonObject.put("QUESTION_TYPE_ID", qUESTIONTYPEID)
            jsonObject.put("QUESTION_TITLE", editTextTitle?.text.toString())
            jsonObject.put("QUESTION_CONTENT", fileName)
            jsonObject.put("QUESTION_ID", questionList.qUESTIONID);

//            jsonObject.put("OPTION1_TEXT", editTextOption1?.text.toString())
//            jsonObject.put("OPTION2_TEXT", editTextOption2?.text.toString())
//            jsonObject.put("OPTION3_TEXT", editTextOption3?.text.toString())
//            jsonObject.put("OPTION4_TEXT", editTextOption4?.text.toString())
//            jsonObject.put("RIGHT_OPTION_TEXT", rightOption)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG, "jsonObject $jsonObject")
        val accademicRe =
            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        objectiveExamStaffViewModel.getCommonPostFun(url, accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(
                                        requireActivity(),
                                        "Question Updated Successfully",
                                        constraintLayoutContent!!
                                    )
                                    objQuestionTabListener.onCreateClick("Question Updated Successfully")
                                    cancelFrg()
                                }
                                Utils.resultFun(response) == "EXIST" -> {
                                    Utils.getSnackBar4K(
                                        requireActivity(),
                                        "Question Already Exists",
                                        constraintLayoutContent!!
                                    )
                                }
                                Utils.resultFun(response) == "FAIL" -> {
                                    Utils.getSnackBar4K(
                                        requireActivity(),
                                        "Failed While Updating Question",
                                        constraintLayoutContent!!
                                    )
                                }
                                else -> {
                                    Utils.getSnackBar4K(
                                        requireActivity(),
                                        "Failed While Adding Question and Options",
                                        constraintLayoutContent!!
                                    )
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(
                                requireActivity(),
                                "Please try again after sometime",
                                constraintLayoutContent!!
                            )
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(CreateDescriptiveExam.TAG, "loading")
                        }
                    }
                }
            })
    }

    fun mimeTypeFun(mimeTypes: String, actionPick: String) {
        if (fileNameList.size < maxCount) {
            maxCountSelection = maxCount - fileNameList.size
            Toast.makeText(requireActivity(), "Select $maxCountSelection ", Toast.LENGTH_SHORT).show()

            val intent = Intent(actionPick); // or ACTION_OPEN_DOCUMENT //ACTION_GET_CONTENT
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
                if (fileNameList.size >= 1) {
                    constraintLayoutUpload?.visibility = View.VISIBLE
                    imageViewClose?.visibility = View.VISIBLE
                    uploadImage?.visibility = View.VISIBLE
                    uploadTypeImage?.visibility = View.GONE
                    //uploadToken = 0
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
                    } else if (mFile.toString().contains(".mp3") || mFile.toString()
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
                    } else {
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
                } else {
                    constraintLayoutUpload?.visibility = View.VISIBLE
                    imageViewClose?.visibility = View.GONE
                    uploadImage?.visibility = View.GONE
                    uploadTypeImage?.visibility = View.VISIBLE
                    Utils.getSnackBar4K(
                        requireActivity(),
                        "Selected More then one",
                        constraintLayoutContent!!
                    )
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
                                    if (questionList.oEXAMID == getObjectiveExam[i].oEXAMID) {
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
                            var qUESTIONTYPE_ID = 0
                            questionType =
                                response.questionType as ArrayList<QuestionTypeModel.QuestionType>
                            var question = Array(questionType.size) { "" }
                            if (question.isNotEmpty()) {
                                for (i in questionType.indices) {
                                    question[i] = questionType[i].qUESTIONTYPENAME
                                    if (questionList.qUESTIONTYPEID == questionType[i].qUESTIONTYPEID) {
                                        qUESTIONTYPE_ID = i
                                    }
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
                            spinnerQuestionType?.setSelection(qUESTIONTYPE_ID)

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

    fun getOptionList() {
        objectiveExamStaffViewModel.getObjectOptionList(questionList.qUESTIONID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            getOptionList= response.optionList as ArrayList<OptionListStaffModel.Option>
                            if (isAdded) {
                                mAdapter = OptionListAdapter(this,getOptionList,requireActivity())
                                recyclerViewOption!!.adapter = mAdapter
                            }

                            //  Log.i(TAG,"Feedlist_dialog "+Feedlist_dialog.size());
                            if (getOptionList.size < 4) {
                                fabAddOption?.visibility = View.VISIBLE
                            } else {
                                fabAddOption?.visibility = View.GONE
                            }

                            fabAddOption?.setOnClickListener {
                                var optionsList = OptionListStaffModel.Option(
                                    0,0,"","",questionList.qUESTIONID,true)
                                getOptionList.add(optionsList)
                                mAdapter.notifyItemInserted(getOptionList.size - 1)

                                if (getOptionList.size < 4) {
                                    fabAddOption?.visibility = View.VISIBLE
                                } else {
                                    fabAddOption?.visibility = View.GONE
                                }

                            }

                            Log.i(TAG,"getOptionList SUCCESS $getOptionList")
                        }
                        Status.ERROR -> {

                            Log.i(TAG,"getOptionList ERROR")
                        }
                        Status.LOADING -> {
                            getOptionList = ArrayList<OptionListStaffModel.Option>()
                            Log.i(TAG,"getOptionList LOADING")
                        }
                    }
                }
            })

    }


    class OptionListAdapter(var objectOptionListener : ObjectOptionListener,
                            var getOptionList: ArrayList<OptionListStaffModel.Option>,
                            var context : Context) :
        RecyclerView.Adapter<OptionListAdapter.ViewHolder>() {
        var selectedPosition = -1

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var checkBox: CheckBox = view.findViewById(R.id.checkBox)
            var editTextOption: EditText = view.findViewById(R.id.editTextOption)
            var textViewOption: TextView = view.findViewById(R.id.textViewOption)
            var imageViewEdit: ImageView = view.findViewById(R.id.imageViewEdit)
            var imageViewDelete: ImageView = view.findViewById(R.id.imageViewDelete)
            var imageViewSubmit: ImageView = view.findViewById(R.id.imageViewSubmit)
            var imageViewClose : ImageView = view.findViewById(R.id.imageViewClose)
        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.option_list_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("ResourceAsColor")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.checkBox.isChecked = position == selectedPosition
           // holder.checkBox.visibility = View.VISIBLE
            holder.textViewOption.text = getOptionList[position].oPTIONTEXT
            holder.imageViewClose.visibility = View.GONE
            holder.checkBox.visibility = View.VISIBLE
            holder.imageViewEdit.visibility = View.VISIBLE
            holder.imageViewDelete.visibility = View.VISIBLE
            holder.imageViewSubmit.visibility = View.GONE

            if(getOptionList[position].isNewCheck){
                holder.checkBox.buttonTintList = getColorStateList(context,R.color.gray_200)
                holder.imageViewEdit.visibility = View.VISIBLE
                holder.imageViewDelete.visibility = View.VISIBLE
                holder.imageViewSubmit.visibility = View.GONE
                holder.textViewOption.setTextColor(context.resources.getColor(R.color.gray_200))
                holder.textViewOption.text = "Enter Text Here"
            }

            var isChecked = false
            if(getOptionList[position].iSRIGHTOPTION == 1){
                isChecked = true
                holder.textViewOption.setTextColor(context.resources.getColor(R.color.green_600))
            }

            holder.checkBox.setOnClickListener {

                if (position == selectedPosition) {
                    holder.checkBox.isChecked = false
                    selectedPosition = -1
                    // Log.i(TAG,"selected_position "+selected_position);
                    // Log.i(TAG,"position "+position);
                } else {
                    selectedPosition = position
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Are you Sure want to make this option as right option ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ ->
                            for (i in getOptionList.indices) {
                                if (i == position) {
                                    val jsonObject = JSONObject()
                                    try {
                                        jsonObject.put("QUESTION_ID", getOptionList[i].qUESTIONID)
                                        jsonObject.put("OPTION_TEXT",  getOptionList[i].oPTIONTEXT)
                                        jsonObject.put("IS_RIGHT_OPTION", 1)
                                        jsonObject.put("OPTION_ID", getOptionList[i].oPTIONID)

                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                    objectOptionListener.onRightOptionClicker(
                                        "OnlineExam/UpdateOption", jsonObject, i,
                                        "Option Updated Successfully"
                                    )
                                } else {
                                    val jsonObject = JSONObject()
                                    try {
                                        jsonObject.put("QUESTION_ID", getOptionList[i].qUESTIONID)
                                        jsonObject.put("OPTION_TEXT", getOptionList[i].oPTIONTEXT)
                                        jsonObject.put("IS_RIGHT_OPTION", 0)
                                        jsonObject.put("OPTION_ID", getOptionList[i].oPTIONID)

                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                    objectOptionListener.onRightOptionClicker(
                                        "OnlineExam/UpdateOption", jsonObject, i,
                                        "Option Updated Successfully"
                                    )
                                }
                            }
                        }
                        .setNegativeButton(
                            "No"
                        ) { dialog, _ -> //  Act
                            objectOptionListener.onOptionChangeNoClicker()// ion for 'NO' Button
                            dialog.cancel()
                        }
                    //Creating dialog box
                    val alert = builder.create()
                    //Setting the title manually
                    alert.setTitle("Change Option")
                    alert.show()
                    val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                    buttonbackground.setTextColor(Color.BLACK)
                    val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                    buttonbackground1.setTextColor(Color.BLACK)

                }

            }


            holder.checkBox.isChecked = isChecked

            holder.imageViewEdit.setOnClickListener {
                holder.textViewOption.visibility = View.GONE
                holder.editTextOption.visibility = View.VISIBLE
                holder.editTextOption.setText(getOptionList[position].oPTIONTEXT)
                holder.imageViewEdit.visibility = View.GONE
                holder.imageViewSubmit.visibility = View.VISIBLE
                holder.imageViewDelete.visibility = View.VISIBLE
                holder.checkBox.visibility = View.GONE
                holder.imageViewClose.visibility = View.VISIBLE
            }

            holder.imageViewSubmit.setOnClickListener {
                if(getOptionList[position].isNewCheck){
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("QUESTION_ID", getOptionList[position].qUESTIONID)
                        jsonObject.put("OPTION_TEXT", holder.editTextOption.text.toString())
                        jsonObject.put("IS_RIGHT_OPTION", getOptionList[position].iSRIGHTOPTION)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    objectOptionListener.onOptionEditClicker("OnlineExam/AddOption",jsonObject,position,
                        "Option Added Successfully")
                }else{
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("QUESTION_ID", getOptionList[position].qUESTIONID)
                        jsonObject.put("OPTION_TEXT", holder.editTextOption.text.toString())
                        jsonObject.put("IS_RIGHT_OPTION", getOptionList[position].iSRIGHTOPTION)
                        jsonObject.put("OPTION_ID", getOptionList[position].oPTIONID)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    objectOptionListener.onOptionEditClicker("OnlineExam/UpdateOption",jsonObject,position,
                        "Option Updated Successfully")
                }
            }

            holder.imageViewDelete.setOnClickListener {
                objectOptionListener.onOptionDeleteClicker(position,getOptionList[position].isNewCheck,
                    getOptionList[position].oPTIONID)
            }

            holder.imageViewClose.setOnClickListener {
                holder.checkBox.visibility = View.VISIBLE
                holder.imageViewClose.visibility = View.GONE
                holder.textViewOption.visibility = View.VISIBLE
                holder.editTextOption.visibility = View.GONE
                holder.imageViewEdit.visibility = View.VISIBLE
                holder.imageViewSubmit.visibility = View.GONE
                holder.imageViewDelete.visibility = View.VISIBLE
            }

        }

        override fun getItemCount(): Int {
            return getOptionList.size
        }

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
    override fun onRightOptionClicker(url: String, jsonObject: JSONObject, position: Int,successMessage : String) {

        Log.i(CreateDescriptiveExam.TAG,"jsonObject $jsonObject")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        objectiveExamStaffViewModel.getCommonPostFun(url,accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(requireActivity(),successMessage,constraintLayoutContent!!)
                                 //   getOptionList[position].isNewCheck = false
                                 //   mAdapter.notifyItemChanged(position)
                                   // mAdapter.notifyDataSetChanged()
                                    //  mAdapter1.notifyDataSetChanged();
                                    if (position + 1 == getOptionList.size) {
                                        getOptionList = ArrayList<OptionListStaffModel.Option>()
                                        getOptionList()
                                        objQuestionTabListener.onCreateClick("Option Added Successfully")
                                    }
                                }
                                Utils.resultFun(response) == "EXIST" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Option Already Exist", constraintLayoutContent!!)
                                }
                                Utils.resultFun(response) == "FAIL" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Failed While Adding Option", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {

                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {

                            Log.i(CreateDescriptiveExam.TAG,"loading")
                        }
                    }
                }
            })
    }
    override fun onOptionEditClicker(url: String, jsonObject: JSONObject, position: Int,successMessage : String) {

        Log.i(CreateDescriptiveExam.TAG,"jsonObject $jsonObject")
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
                                    Utils.getSnackBarGreen(requireActivity(),successMessage,constraintLayoutContent!!)
                                    //   getOptionList[position].isNewCheck = false
                                    //   mAdapter.notifyItemChanged(position)
                                    // mAdapter.notifyDataSetChanged()
                                    //  mAdapter1.notifyDataSetChanged();
                                    getOptionList = ArrayList<OptionListStaffModel.Option>()
                                    getOptionList()
                                    objQuestionTabListener.onCreateClick("Option Added Successfully")


                                }
                                Utils.resultFun(response) == "EXIST" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Option Already Exist", constraintLayoutContent!!)
                                }
                                Utils.resultFun(response) == "FAIL" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Failed While Adding Option", constraintLayoutContent!!)
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
    override fun onOptionChangeNoClicker(){
        mAdapter.notifyDataSetChanged()
    }
    override fun onOptionDeleteClicker(position: Int,isNewCheck : Boolean,oPTIONID : Int ) {

        if(isNewCheck){
            fabAddOption?.visibility = View.VISIBLE
            getOptionList.removeAt(position)
            mAdapter.notifyDataSetChanged()
            if (getOptionList.size < 4) {
                fabAddOption?.visibility = View.VISIBLE
            } else {
                fabAddOption?.visibility = View.GONE
            }
        }else{
            val paramsMap: HashMap<String?, Int> = HashMap()
            paramsMap["OptionId"] = oPTIONID
            objectiveExamStaffViewModel.getCommonGetFuntion("OnlineExam/OptionDelete",paramsMap)
                .observe(requireActivity(), Observer {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                val response = resource.data?.body()!!
                                when {
                                    Utils.resultFun(response) == "DELETED" -> {
                                        //  Utils.getSnackBarGreen(requireActivity(), "Deleted Successfully", constraintLayoutContent!!)
                                        getOptionList.removeAt(position)
                                        mAdapter.notifyDataSetChanged()
                                        objQuestionTabListener.onCreateClick("Deleted Successfully")
                                        if (getOptionList.size < 4) {
                                            fabAddOption?.visibility = View.VISIBLE
                                        } else {
                                            fabAddOption?.visibility = View.GONE
                                        }
                                    }
                                    Utils.resultFun(response) == "FAIL" -> {
                                        Utils.getSnackBar4K(requireActivity(), "Failed while delete", constraintLayoutContent!!)
                                    }
                                }
                            }
                            Status.ERROR -> {
                                Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                            }
                            Status.LOADING -> {
                                Log.i(TAG,"loading")
                            }
                        }
                    }
                })
        }
    }
}


interface ObjectOptionListener{
    fun onRightOptionClicker(url: String, jsonObject: JSONObject, position: Int,successMessage : String)
    fun onOptionEditClicker(url: String, jsonObject: JSONObject, position: Int,successMessage : String)
    fun onOptionDeleteClicker(position: Int,isNewCheck : Boolean,oPTIONID : Int)
    fun onOptionChangeNoClicker()
}