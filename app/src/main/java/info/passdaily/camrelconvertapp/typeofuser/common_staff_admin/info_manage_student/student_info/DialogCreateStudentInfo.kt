package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.student_info

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreateStudentInfoBinding
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
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_staff.DialogCreateStaff
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.FileList
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

@Suppress("DEPRECATION")
class DialogCreateStudentInfo : DialogFragment {

    lateinit var studentInfoListener: StudentInfoListener


    companion object {
        var TAG = "DialogCreateStudentInfo"
    }

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var readPermission = false
    private var writePermission = false

    private var _binding: DialogCreateStudentInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var studentInfoViewModel: StudentInfoViewModel

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()

    var STUDENT_ROLL_NO = 0   //P04439750.

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var staffId = 0
    var schoolId = 0
    var toolbar : Toolbar? = null
    var constraintLeave : ConstraintLayout? = null

    var gender = arrayOf("Select Gender", "Male", "Female", "other")
    var typeStr ="-1"
    var isWorking= false

    var shapeImageView : ShapeableImageView? =null

    var imageSelection = ""

    var imageReponce = ""

    var adminRole = 0


    var fileNameList = ArrayList<FileList>()

    var aCCADEMICID = 0
    var cLASSID = 0

    var textEditDob  : TextInputEditText? =null
    var editAdmissionNumber : TextInputEditText? =null
    var editStudentName  : TextInputEditText? =null
    var editGuardianName : TextInputEditText? =null
    var editGuardianNumber : TextInputEditText? =null

    var spinnerAcademic : AppCompatSpinner? =null
    var spinnerClass : AppCompatSpinner? =null
    var spinnerGender : AppCompatSpinner? =null

    var buttonSubmit : AppCompatButton? =null

    var dobDate = ""
    var pb: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleWhite)
    }

    constructor(studentInfoListener: StudentInfoListener) {
        this.studentInfoListener = studentInfoListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        studentInfoViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StudentInfoViewModel::class.java]

        _binding = DialogCreateStudentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pb = ProgressDialog(requireActivity())
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Loading...")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE
        schoolId = user[0].SCHOOL_ID
        staffId =  user[0].STAFF_ID
        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_black)
        toolbar?.title = "Create Student Info"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.black))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }
        spinnerAcademic  = binding.spinnerAcademic
        spinnerClass  = binding.spinnerClass
        spinnerGender  = binding.spinnerGender

        textEditDob   = binding.textEditDob
        textEditDob?.inputType = InputType.TYPE_NULL
        textEditDob?.keyListener = null

        editAdmissionNumber = binding.editAdmissionNumber
        editStudentName   = binding.editStudentName
        editGuardianName  = binding.editGuardianName
        editGuardianNumber = binding.editGuardianNumber

        constraintLeave = binding.constraintLeave

        shapeImageView = binding.shapeImageView
        val imageViewPlus = binding.imageViewPlus

        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
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
                view: View, position: Int, id: Long) {
                cLASSID = getClass[position].cLASSID

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val status= ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, gender)
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender?.adapter = status
        spinnerGender?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                    when (position) {
                        0 -> { typeStr = "-1"}
                        1 -> { typeStr = "0" }
                        2 -> { typeStr = "1" }
                        3 -> { typeStr = "2" }
                    }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }

        imageViewPlus.setOnClickListener {
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


        textEditDob?.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(textEditDob?.windowToken, 0)
            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    var fromStr = "$year-$s-$dayOfMonth"
                    dobDate ="$dayOfMonth/$s/$year"
                    textEditDob?.setText(Utils.dateformat(fromStr))

                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("Start Date")
            mDatePicker3.datePicker.maxDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.setOnClickListener {
            if(
            //   studentInfoViewModel.validateField(textEditDob!!,"Enter Date Of Birth",requireActivity(),constraintLeave!!) &&
                studentInfoViewModel.validateField(editAdmissionNumber!!,"Enter Admission Number",requireActivity(),constraintLeave!!)
                && studentInfoViewModel.validateField(editStudentName!!,"Enter Student Name",requireActivity(),constraintLeave!!)
                && studentInfoViewModel.validateFieldStr(typeStr,"Select Gender",requireActivity(),constraintLeave!!)
                && studentInfoViewModel.validateField(editGuardianName!!,"Enter Guardian Name",requireActivity(),constraintLeave!!)
                && studentInfoViewModel.validateField(editGuardianNumber!!,"Enter Guardian Number",requireActivity(),constraintLeave!!)
            ){
                if(fileNameList.isNotEmpty()){
                    progressStart()
                    onFileUploadClick(imageSelection)
                }else{
                    progressStart()
                    submitStudent("")
                }
            }
        }
        val constraintLeave = binding.constraintLeave
        constraintLeave.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLeave.windowToken, 0)
        }

        var buttonCancel = binding.buttonCancel
        buttonCancel.setOnClickListener {   cancelFrg() }

        initFunction()
    }


    ///permission Part
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data
                Log.i(DialogCreateStaff.TAG, "data $data")

                //If single image selected
                if (data?.data != null) {
                    val imageUri: Uri? = data.data
                    fileNameList.add(
                        FileList(
                            0,
                            imageUri.toString(),
                            "",
                            "Local",
                            0
                        )
                    )
                    imageSelection = FileUtils.getReadablePathFromUri(requireActivity(), imageUri!!)!!
                    Glide.with(requireActivity())
                        .load(imageUri)
                        .into(shapeImageView!!)
                }

            }

        }


    fun initFunction(){
        studentInfoViewModel.getYearClassExam(adminId, schoolId )
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size){""}
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                            }
                            if (spinnerAcademic != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademic?.adapter = adapter
                            }


                            getClass = response.classList as ArrayList<GetYearClassExamModel.Class>
                            var classes = Array(getClass.size){""}
                            for (i in getClass.indices) {
                                classes[i] = getClass[i].cLASSNAME
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    classes
                                )
                                spinnerClass?.adapter = adapter
                            }



                            Log.i(TAG,"initFunction SUCCESS")

                        }
                        Status.ERROR -> {
                            Log.i(TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"initFunction LOADING")
                        }
                    }
                }
            })

    }


    fun onFileUploadClick(selectedFilePath: String) {

        //http://meridianstaff.passdaily.in/ElixirApi/StudentList/FileUpload
        var SERVER_URL = "StudentList/UploadFiles"

        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(TAG,"selectedFilePath $selectedFilePath");
        filesToUpload[0] = File(selectedFilePath)
        Log.i(TAG,"filesToUpload $filesToUpload");

        //  showProgress("Uploading media ...",perProgressBar,textViewProgress)
        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload,"", object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                progressStop()
                //   hideProgress(perProgressBar,textViewProgress)
                Log.i(TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {
                //  hideProgress(perProgressBar,textViewProgress)
                Log.i(TAG,"responses ${responses[0]}")
                submitStudent(responses[0])

            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                //  updateProgress(totalpercent, "Uploading file $filenumber", "", perProgressBar,textViewProgress)
                Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })
    }

    fun submitStudent(filename :String){
        // postParam.put("VIRTUAL_MAIL_TITLE",noti_title);
        //        postParam.put("VIRTUAL_MAIL_CONTENT", noti_desc);
        //        postParam.put("VIRTUAL_MAIL_STATUS", "1");
        //        postParam.put("VIRTUAL_MAIL_CREATED_BY",  Global.Admin_id);
        var dob = ""
        if(textEditDob!!.text.toString().isNotEmpty()){
            dob = Utils.parseDateToDDMMYYYY("MMM dd, yyyy","MM/dd/yyyy", textEditDob!!.text.toString())!!
        }

        var url = "ManageStudents/AddNewStudent"

        val jsonObject = JSONObject()
        try {
            jsonObject.put("ADMISSION_NUMBER", editAdmissionNumber?.text.toString())
            jsonObject.put("CLASS_ID", cLASSID)
            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
            jsonObject.put("STUDENT_GENDER", typeStr)
            jsonObject.put("STUDENT_DOB",  dob)
            jsonObject.put("STUDENT_FNAME", editStudentName?.text.toString())
            jsonObject.put("STUDENT_GUARDIAN_NAME", editGuardianName?.text.toString())
            jsonObject.put("STUDENT_IMAGE", filename)
            jsonObject.put("STUDENT_GUARDIAN_NUMBER", editGuardianNumber?.text.toString())
            jsonObject.put("SCHOOL_ID", schoolId)
            jsonObject.put("ADMIN_ID", adminId)
            jsonObject.put("STAFF_ID", staffId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject ${jsonObject.toString()}")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        studentInfoViewModel.getCommonPostFun(url,accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    studentInfoListener.onSuccessMessage("Student Created")
                                   // Utils.getSnackBarGreen(requireActivity(), "Student Created", constraintLeave!!)
                                    cancelFrg()
                                }
                                Utils.resultFun(response) == "ERROR" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Student is already existing", constraintLeave!!)
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Failed While Creating student", constraintLeave!!)

                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLeave!!)
                        }
                        Status.LOADING -> {
                           // progressStart()
                            Log.i(TAG,"loading")
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
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
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