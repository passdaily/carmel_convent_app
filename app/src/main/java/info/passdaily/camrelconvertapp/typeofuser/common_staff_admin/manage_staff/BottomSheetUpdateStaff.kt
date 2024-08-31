package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_staff

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
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateStaffBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.permission.sdk29AndUp
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.FileList
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class BottomSheetUpdateStaff : BottomSheetDialogFragment {

    private lateinit var staffViewModel: StaffViewModel


    var imageSelection = ""

    var fileNameList = ArrayList<FileList>()

    private var _binding: BottomSheetUpdateStaffBinding? = null
    private val binding get() = _binding!!

    lateinit var staffDetailsListener: StaffDetailsListener

    lateinit var studentsList: StudentsInfoListModel.Students

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var readPermission = false
    private var writePermission = false


    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var adminRole = 0
    var schoolId = 0

    var toolbar : Toolbar? = null
    var constraintLeave : ConstraintLayout? = null

    var buttonSubmit : AppCompatButton? =null

    var shapeImageView : ShapeableImageView? =null

    var dobDate = ""

    var gender = arrayOf("Select Gender", "Male", "Female", "other")
    var typeStr ="-1"

    var type = arrayOf("Unpublished", "Published")
    var typeStatus =""

    var editStaffCode : TextInputEditText? = null
    var editStaffName : TextInputEditText? = null
    var spinnerGender  : AppCompatSpinner? = null
    var editMobileNumber : TextInputEditText? = null
    var editEmailId : TextInputEditText? = null
    var textJoinDate  : TextInputEditText? = null
    var textEditDob  : TextInputEditText? = null


    var editJoinExperience : TextInputEditText? = null
    var editJoinDesignation : TextInputEditText? = null
    var editFatherName  : TextInputEditText? = null
    var editMotherName  : TextInputEditText? = null
    var editNationality   : TextInputEditText? = null
    var editMotherTongue   : TextInputEditText? = null
    var editReligion  : TextInputEditText? = null
    var editCaste  : TextInputEditText? = null
    var editFatherOccupation : TextInputEditText? = null
    var editCurrentAddress : TextInputEditText? = null
    var editPermanentAddress : TextInputEditText? = null

    var spinnerStatus  : AppCompatSpinner? = null

    lateinit var staffDetails: ArrayList<ManageStaffListModel.Staff>

    var typStr = -1

    var position = 0

    var pb: ProgressDialog? = null


    var typGenderStr = -1
    constructor()

    constructor(
        staffDetailsListener: StaffDetailsListener,
        staffDetails: ArrayList<ManageStaffListModel.Staff>,
        position:Int) {
        this.staffDetailsListener = staffDetailsListener
        this.staffDetails = staffDetails
        this.position = position
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        staffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StaffViewModel::class.java]

        _binding = BottomSheetUpdateStaffBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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

        shapeImageView = binding.shapeImageView
        val imageViewPlus = binding.imageViewPlus

        constraintLeave  = binding.constraintLeave

        editStaffCode  = binding.editStaffCode
        editStaffName  = binding.editStaffName
        spinnerGender  = binding.spinnerGender
        editMobileNumber  = binding.editMobileNumber
        editEmailId  = binding.editEmailId
        textJoinDate  = binding.textJoinDate
        textEditDob  = binding.textEditDob

        textJoinDate?.inputType = InputType.TYPE_NULL
        textJoinDate?.keyListener = null
        textEditDob?.inputType = InputType.TYPE_NULL
        textEditDob?.keyListener = null

        editJoinExperience  = binding.editJoinExperience
        editJoinDesignation  = binding.editJoinDesignation
        editFatherName  = binding.editFatherName
        editMotherName  = binding.editMotherName
        editNationality  = binding.editNationality
        editMotherTongue  = binding.editMotherTongue
        editReligion  = binding.editReligion
        editCaste  = binding.editCaste
        editFatherOccupation  = binding.editFatherOccupation
        editCurrentAddress  = binding.editCurrentAddress
        editPermanentAddress  = binding.editPermanentAddress
        spinnerStatus  = binding.spinnerStatus

        editStaffCode?.setText(staffDetails[position].sTAFFCODE)
        editStaffName?.setText(staffDetails[position].sTAFFFNAME)
        editMobileNumber?.setText(staffDetails[position].sTAFFPHONENUMBER)
        editEmailId?.setText(staffDetails[position].sTAFFEMAILID)
        editJoinExperience?.setText(staffDetails[position].sTAFFJOBEXPERIENCE)
        editJoinDesignation?.setText(staffDetails[position].sTAFFJOBDESCRIPTION)

        editFatherName?.setText(staffDetails[position].sTAFFFATHERNAME)
        editMotherName?.setText(staffDetails[position].sTAFFMOTHERNAME)
        editNationality?.setText(staffDetails[position].sTAFFNATIONALITY)
        editMotherTongue?.setText(staffDetails[position].sTAFFMOTHERTONGUE)
        editReligion?.setText(staffDetails[position].sTAFFRELIGION)
        editCaste?.setText(staffDetails[position].sTAFFCASTE)

        editFatherOccupation?.setText(staffDetails[position].sTAFFFATHEROCCUPATION)
        editCurrentAddress?.setText(staffDetails[position].sTAFFCADDRESS)
        editPermanentAddress?.setText(staffDetails[position].sTAFFPADDRESS)



        if (staffDetails[position].sTAFFIMAGE.isNotEmpty()) {
            Glide.with(requireActivity()).load(Global.event_url + "/Photos/StaffImage/" + staffDetails[position].sTAFFIMAGE)
                .into(shapeImageView!!)
            // profile_str=Global.event_url+"/Photos/StaffImage"+STAFF_IMAGE;
        }

        var genStr = ""
        typGenderStr = staffDetails[position].sTAFFGENDER
        genStr = when (typGenderStr) {
            0 -> {
                "Male~Female~Other"
            }
            1 -> {
                "Female~Male~Other"
            }
            else -> {
                "Other~Male~Female"
            }
        }
        gender = genStr.split("~").toTypedArray()
        //    spinnerGender?.isEnabled = false
        val genderSpi= ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, gender)
        genderSpi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender?.adapter = genderSpi
        spinnerGender?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                if(gender[position] == "Male"){
                    typeStr = "0"
                }else if(gender[position] == "Female"){
                    typeStr = "1"
                }else if(gender[position] == "Other"){
                    typeStr = "2"
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        //spinnerStatus

        var str = ""
        typStr = staffDetails[position].sTAFFSTATUS
        str = if (typStr == 0) {
            "UnPublished~Publish"
        } else {
            "Publish~UnPublished"
        }
        type = str.split("~").toTypedArray()
        val spiStatus = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, type)
        spiStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus?.adapter = spiStatus
        spinnerStatus?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                if (type[position] == "UnPublished") {
                    typeStatus = "0"
                } else if (type[position] == "Publish") {
                    typeStatus = "1"
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val dateJoin: Array<String> = staffDetails[position].sTAFFJOINDATE.split("T").toTypedArray()
        // val joinDate = dateJoin[0].split("-").toTypedArray()
        textJoinDate?.setText(Utils.dateformat(dateJoin[0]))

        textJoinDate?.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(textJoinDate?.windowToken, 0)
            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    var fromStr = "$year-$s-$dayOfMonth"
                    dobDate ="$dayOfMonth/$s/$year"
                    textJoinDate?.setText(Utils.dateformat(fromStr))

                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("Join Date")
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }


        val dateDob: Array<String> = staffDetails[position].sTAFFDOB.split("T").toTypedArray()
        //val dobDateFor = dateDob[0].split("-").toTypedArray()
        textEditDob?.setText(Utils.dateformat(dateDob[0]))

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
            mDatePicker3.setTitle("Dob Date")
            mDatePicker3.datePicker.maxDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
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



        val submitButton = binding.buttonSubmit
        submitButton.setOnClickListener {

            if(staffViewModel.validateField(editStaffCode!!,"Enter Staff Code",requireActivity(),constraintLeave!!)
                && staffViewModel.validateField(editStaffName!!,"Enter Staff Name",requireActivity(),constraintLeave!!)
                && staffViewModel.validateFieldStr(typeStr,"Select Gender",requireActivity(),constraintLeave!!)
                && staffViewModel.validateField(editMobileNumber!!,"Enter Mobile Number",requireActivity(),constraintLeave!!)
//                && staffViewModel.validateField(editEmailId!!,"Enter Email ID",requireActivity(),constraintLeave!!)
//                && staffViewModel.validateField(textJoinDate!!,"Select Join Date",requireActivity(),constraintLeave!!)
                && staffViewModel.validateField(textEditDob!!,"Select Date Of Birth",requireActivity(),constraintLeave!!)
            ) {



                if(fileNameList.isNotEmpty()){
                  //  pb?.show()
                    progressStart()
                    onFileUploadClick(imageSelection)
                }else{
                   // pb?.show()
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("STAFF_ID", staffDetails[position].sTAFFID)
                        jsonObject.put("STAFF_CODE", editStaffCode?.text.toString())
                        jsonObject.put("STAFF_JOINDATE", Utils.parseDateToDDMMYYYY(
                            "MMM dd, yyyy","MM/dd/yyyy", textJoinDate?.text.toString()))
                        jsonObject.put("STAFF_FNAME", editStaffName?.text.toString())
                        jsonObject.put("STAFF_GENDER", typeStr)
                        jsonObject.put("STAFF_DOB", Utils.parseDateToDDMMYYYY(
                            "MMM dd, yyyy","MM/dd/yyyy", textEditDob?.text.toString()))
                        jsonObject.put("STAFF_NATIONALITY", editNationality?.text.toString())
                        jsonObject.put("STAFF_MOTHERTONGUE", editMotherTongue?.text.toString())
                        jsonObject.put("STAFF_RELIGION", editReligion?.text.toString())
                        jsonObject.put("STAFF_CASTE", editCaste?.text.toString())
                        jsonObject.put("STAFF_FATHER_NAME", editFatherName?.text.toString())
                        jsonObject.put("STAFF_MOTHER_NAME", editMotherName?.text.toString())
                        jsonObject.put("STAFF_FATHER_OCCUPATION", editFatherOccupation?.text.toString())
                        jsonObject.put("STAFF_JOB_EXPERIENCE", editJoinExperience?.text.toString())
                        jsonObject.put("STAFF_JOB_DESCRIPTION", editJoinDesignation?.text.toString())
                        jsonObject.put("STAFF_CADDRESS", editCurrentAddress?.text.toString())
                        jsonObject.put("STAFF_PADDRESS", editPermanentAddress?.text.toString())
                        jsonObject.put("STAFF_PHONE_NUMBER", editMobileNumber?.text.toString())
                        jsonObject.put("STAFF_EMAIL_ID", editEmailId?.text.toString())
                        jsonObject.put("STAFF_IMAGE", staffDetails[position].sTAFFIMAGE)
                        jsonObject.put("STAFF_STATUS", typeStatus)
                        jsonObject.put("SCHOOL_ID", schoolId)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i(TAG, "jsonObject $jsonObject")
                    val accademicRe = jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


                    staffDetailsListener.onSubmitListener(
                        "Staff/UpdateStaff",
                        accademicRe,
                        position,
                        editStaffCode?.text.toString(),
                        Utils.parseDateToDDMMYYYY("MMM dd, yyyy","yyyy-MM-dd", textJoinDate?.text.toString())!! +"T00:00:00",
                        editStaffName?.text.toString(),
                        typeStr,
                        Utils.parseDateToDDMMYYYY("MMM dd, yyyy","yyyy-MM-dd", textEditDob?.text.toString())!! +"T00:00:00",
                        editNationality?.text.toString(),
                        editMotherTongue?.text.toString(),
                        editReligion?.text.toString(),
                        editCaste?.text.toString(),
                        editFatherName?.text.toString(),
                        editMotherName?.text.toString(),
                        editFatherOccupation?.text.toString(),
                        editJoinExperience?.text.toString(),
                        editJoinDesignation?.text.toString(),
                        editCurrentAddress?.text.toString(),
                        editPermanentAddress?.text.toString(),
                        editMobileNumber?.text.toString(),
                        editEmailId?.text.toString(),
                        staffDetails[position].sTAFFIMAGE,
                        typeStatus
                    )
                }

            }
        }

        val constraintLeave = binding.constraintLeave
        constraintLeave.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLeave.windowToken, 0)
        }

        binding.textDeleteIcon.setOnClickListener {
            staffDetailsListener.onDeleteClicker(staffDetails[position])
        }

    }
    fun onFileUploadClick(selectedFilePath: String) {

        var SERVER_URL = "StaffUpload/UploadFiles"

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
               // pb?.dismiss()
                progressStop()
                //   hideProgress(perProgressBar,textViewProgress)
                Log.i(TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {
                //  hideProgress(perProgressBar,textViewProgress)
                progressStop()
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("STAFF_ID", staffDetails[position].sTAFFID)
                    jsonObject.put("STAFF_CODE", editStaffCode?.text.toString())
                    jsonObject.put("STAFF_JOINDATE", Utils.parseDateToDDMMYYYY("MMM dd, yyyy","MM/dd/yyyy", textJoinDate?.text.toString()))
                    jsonObject.put("STAFF_FNAME", editStaffName?.text.toString())
                    jsonObject.put("STAFF_GENDER", typeStr)
                    jsonObject.put("STAFF_DOB", Utils.parseDateToDDMMYYYY("MMM dd, yyyy","MM/dd/yyyy", textEditDob?.text.toString()))
                    jsonObject.put("STAFF_NATIONALITY", editNationality?.text.toString())
                    jsonObject.put("STAFF_MOTHERTONGUE", editMotherTongue?.text.toString())
                    jsonObject.put("STAFF_RELIGION", editReligion?.text.toString())
                    jsonObject.put("STAFF_CASTE", editCaste?.text.toString())
                    jsonObject.put("STAFF_FATHER_NAME", editFatherName?.text.toString())
                    jsonObject.put("STAFF_MOTHER_NAME", editMotherName?.text.toString())
                    jsonObject.put("STAFF_FATHER_OCCUPATION", editFatherOccupation?.text.toString())
                    jsonObject.put("STAFF_JOB_EXPERIENCE", editJoinExperience?.text.toString())
                    jsonObject.put("STAFF_JOB_DESCRIPTION", editJoinDesignation?.text.toString())
                    jsonObject.put("STAFF_CADDRESS", editCurrentAddress?.text.toString())
                    jsonObject.put("STAFF_PADDRESS", editPermanentAddress?.text.toString())
                    jsonObject.put("STAFF_PHONE_NUMBER", editMobileNumber?.text.toString())
                    jsonObject.put("STAFF_EMAIL_ID", editEmailId?.text.toString())
                    jsonObject.put("STAFF_IMAGE", responses[0])
                    jsonObject.put("STAFF_STATUS", typeStatus)
                    jsonObject.put("SCHOOL_ID", schoolId)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.i(TAG, "jsonObject $jsonObject")
                val accademicRe = jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

                staffDetailsListener.onSubmitListener("Staff/UpdateStaff",accademicRe,position,
                    editStaffCode?.text.toString(),
                    Utils.parseDateToDDMMYYYY("MMM dd, yyyy","yyyy-MM-dd", textJoinDate?.text.toString())!! +"T00:00:00",
                    editStaffName?.text.toString(),
                    typeStr,
                    Utils.parseDateToDDMMYYYY("MMM dd, yyyy","yyyy-MM-dd", textEditDob?.text.toString())!! +"T00:00:00",
                    editNationality?.text.toString(),
                    editMotherTongue?.text.toString(),
                    editReligion?.text.toString(),
                    editCaste?.text.toString(),
                    editFatherName?.text.toString(),
                    editMotherName?.text.toString(),
                    editFatherOccupation?.text.toString(),
                    editJoinExperience?.text.toString(),
                    editJoinDesignation?.text.toString(),
                    editCurrentAddress?.text.toString(),
                    editPermanentAddress?.text.toString(),
                    editMobileNumber?.text.toString(),
                    editEmailId?.text.toString(),
                    responses[0],
                    typeStatus
                    )
                // submitFile(responses[0])
            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                //  updateProgress(totalpercent, "Uploading file $filenumber", "", perProgressBar,textViewProgress)
                Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })


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


//    private fun submitFile(uploadedFileName: String) {
//
//        var url = "Staff/UpdateStaff"
//
//        val jsonObject = JSONObject()
//        try {
//            jsonObject.put("STAFF_ID", staffDetails[position].sTAFFID)
//            jsonObject.put("STAFF_CODE", editStaffCode?.text.toString())
//            jsonObject.put("STAFF_JOINDATE", Utils.parseDateToDDMMYYYY(
//                "MMM dd, yyyy","MM/dd/yyyy", textJoinDate?.text.toString()))
//            jsonObject.put("STAFF_FNAME", editStaffName?.text.toString())
//            jsonObject.put("STAFF_GENDER", typeStr)
//            jsonObject.put("STAFF_DOB", Utils.parseDateToDDMMYYYY(
//                "MMM dd, yyyy","MM/dd/yyyy", textEditDob?.text.toString()))
//            jsonObject.put("STAFF_NATIONALITY", editNationality?.text.toString())
//            jsonObject.put("STAFF_MOTHERTONGUE", editMotherTongue?.text.toString())
//            jsonObject.put("STAFF_RELIGION", editReligion?.text.toString())
//            jsonObject.put("STAFF_CASTE", editCaste?.text.toString())
//            jsonObject.put("STAFF_FATHER_NAME", editFatherName?.text.toString())
//            jsonObject.put("STAFF_MOTHER_NAME", editMotherName?.text.toString())
//            jsonObject.put("STAFF_FATHER_OCCUPATION", editFatherOccupation?.text.toString())
//            jsonObject.put("STAFF_JOB_EXPERIENCE", editJoinExperience?.text.toString())
//            jsonObject.put("STAFF_JOB_DESCRIPTION", editJoinDesignation?.text.toString())
//            jsonObject.put("STAFF_CADDRESS", editCurrentAddress?.text.toString())
//            jsonObject.put("STAFF_PADDRESS", editPermanentAddress?.text.toString())
//            jsonObject.put("STAFF_PHONE_NUMBER", editMobileNumber?.text.toString())
//            jsonObject.put("STAFF_EMAIL_ID", editEmailId?.text.toString())
//            jsonObject.put("STAFF_IMAGE", uploadedFileName)
//            jsonObject.put("STAFF_STATUS", typeStatus)
//            jsonObject.put("SCHOOL_ID", schoolId)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        Log.i(TAG, "jsonObject $jsonObject")
//        val accademicRe =
//            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
//        staffViewModel.getCommonPostFun(url, accademicRe)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    Log.i(TAG, "resource ${resource.message}")
//                    Log.i(TAG, "errorBody ${resource.data?.errorBody()}")
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                        //    progressStop()
//                            pb?.dismiss()
//                            when {
//                                Utils.resultFun(response) == "1" -> {
//                                    staffDetailsListener.onShowMessageClicker("Staff Details Updated Successfully")
//                                    dialog?.dismiss()
//                                }
//                                Utils.resultFun(response) == "-1" -> {
//                                    staffDetailsListener.onErrorMessageClicker("Staff Details Already Exist")
//                                }
//                                else -> {
//                                    staffDetailsListener.onErrorMessageClicker("Staff updation Failed, Please Contact Support")
//                                }
//                            }
//                        }
//                        Status.ERROR -> {
//                            pb?.dismiss()
//                          //  progressStop()
//                            staffDetailsListener.onErrorMessageClicker("Please try again after sometime")
//                        }
//                        Status.LOADING -> {
//                          //  progressStart()
//                            Log.i(CreateAssignmentDialog.TAG, "loading")
//                        }
//                    }
//                }
//            })
//
//    }

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

    companion object {
        var TAG = "BottomSheetFragment"
    }
}