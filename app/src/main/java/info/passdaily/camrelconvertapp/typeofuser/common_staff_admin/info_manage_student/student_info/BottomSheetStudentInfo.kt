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
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetStudentInfoBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.FileUtils
import info.passdaily.camrelconvertapp.services.Global
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
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class BottomSheetStudentInfo : BottomSheetDialogFragment {
    var TAG = "BottomSheetStudentInfo"

    private var _binding: BottomSheetStudentInfoBinding? = null
    private val binding get() = _binding!!
    lateinit var studentInfoListener: StudentInfoListener

    lateinit var  studentsList: ArrayList<StudentsInfoListModel.Students>



    var dobDate = ""
    var admissionDate = ""

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var staffId = 0
    var adminRole = 0
    var schoolId = 0
    var bloodGrouptype = ""
    var genderType = ""

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var readPermission = false
    private var writePermission = false


    var editStudentName  : TextInputEditText? = null
    var editAdmissionNumber   : TextInputEditText? = null
    var editAdmissionDate   : TextInputEditText? = null
    var editDobText    : TextInputEditText? = null
    // var editClassName   : TextInputEditText? = null
    var spinnerGender     : AppCompatSpinner? = null
    var editBloodGroup    : TextInputEditText? = null
    //  var spinnerBlood    : AppCompatSpinner? = null
    var editFatherName    : TextInputEditText? = null
    var editMotherName   : TextInputEditText? = null
    var editMobileNumber   : TextInputEditText? = null
    var editMotherNumber  : TextInputEditText? = null
    var editBirthPlace    : TextInputEditText? = null
    var editBirthPlaceTaluk : TextInputEditText? = null
    var editBirthPlaceDistrict: TextInputEditText? = null
    var editBirthPlaceState : TextInputEditText? = null
    var editNationality  : TextInputEditText? = null
    var editMotherTongue   : TextInputEditText? = null
    var editCaste   : TextInputEditText? = null
    var editCasteType : TextInputEditText? = null
    var editReligion  : TextInputEditText? = null
    var editLastStudied  : TextInputEditText? = null
    var editPermanentAddress  : TextInputEditText? = null
    var editCurrentAddress   : TextInputEditText? = null
    var editSsclRegNumber : TextInputEditText? = null
    var editAadharNumber : TextInputEditText? = null
    var editFatherOccupation   : TextInputEditText? = null
    var editFatherQualification  : TextInputEditText? = null
    var editMotherOccupation   : TextInputEditText? = null
    var editMotherQualification  : TextInputEditText? = null
    //    var editGuardianName   : TextInputEditText? = null
//    var editGuardianNumber   : TextInputEditText? = null
    var editEmailId   : TextInputEditText? = null

    var constraintLayoutPreview : ConstraintLayout? = null
    var constraintLayoutNext : ConstraintLayout? = null

    var imageSelection = ""

    var fileNameList = ArrayList<FileList>()

    var shapeImageView : ShapeableImageView? =null
    var position = 0

    var pb: ProgressDialog? = null

    constructor()

    private lateinit var studentInfoViewModel: StudentInfoViewModel

    constructor(
        studentInfoListener: StudentInfoListener, studentsList: ArrayList<StudentsInfoListModel.Students>,
        position:Int) {
        this.studentInfoListener = studentInfoListener
        this.studentsList = studentsList
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

        studentInfoViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StudentInfoViewModel::class.java]

        _binding = BottomSheetStudentInfoBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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
        staffId= user[0].STAFF_ID

        constraintLayoutPreview = binding.constraintLayoutPreview        //1
        constraintLayoutNext  = binding.constraintLayoutNext        //1



        editAdmissionNumber = binding.editAdmissionNumber        //1
        editAdmissionDate = binding.editAdmissionDate
        editStudentName   = binding.editStudentName
        editDobText   = binding.editDobText

        // editClassName  = binding.editClassName                     //5
        spinnerGender   = binding.spinnerGender
        editBloodGroup = binding.editBloodGroup
        // spinnerBlood   = binding.spinnerBlood
        editBirthPlace  = binding.editBirthPlace

        editBirthPlaceTaluk = binding.editBirthPlaceTaluk
        editBirthPlaceDistrict = binding.editBirthPlaceDistrict        //10
        editBirthPlaceState = binding.editBirthPlaceState
        editNationality = binding.editNationality

        editMotherTongue = binding.editMotherTongue
        editCaste = binding.editCaste
        editCasteType = binding.editCasteType                        //15
        editReligion = binding.editReligion

        editLastStudied = binding.editLastStudied
        editPermanentAddress = binding.editPermanentAddress
        editCurrentAddress = binding.editCurrentAddress
        editSsclRegNumber = binding.editSsclRegNumber                 //20

        editAadharNumber = binding.editAadharNumber
        editFatherName  = binding.editFatherName
        editMobileNumber  = binding.editMobileNumber
        editFatherOccupation = binding.editFatherOccupation

        editFatherQualification = binding.editFatherQualification     //25
        editEmailId = binding.editEmailId
        editMotherName  = binding.editMotherName
        editMotherNumber  = binding.editMotherNumber

        editMotherOccupation = binding.editMotherOccupation
        editMotherQualification = binding.editMotherQualification      //30

//        editGuardianName  = binding.editGuardianName
//        editGuardianNumber = binding.editGuardianNumber


        shapeImageView = binding.shapeImageView
        val imageViewPlus = binding.imageViewPlus

        if (studentsList[position].sTUDENTIMAGE != null) {
            Glide.with(requireActivity()).load(Global.student_image_url + studentsList[position].sTUDENTIMAGE!!)
                .into(shapeImageView!!)
            // profile_str=Global.event_url+"/Photos/StaffImage"+STAFF_IMAGE;
        }


        editAdmissionDate!!.inputType = InputType.TYPE_NULL
        editAdmissionDate!!.keyListener = null

        editDobText!!.inputType = InputType.TYPE_NULL
        editDobText!!.keyListener = null

        editStudentName!!.setText(studentsList[position].sTUDENTFNAME)
        editAdmissionNumber!!.setText(studentsList[position].aDMISSIONNUMBER)
        // editRollNumber.setText(studentsList.sTUDENTROLLNUMBER)
//        editClassName!!.setText(studentsList[position].cLASSNAME)
        binding.textViewClass.text = studentsList[position].cLASSNAME!!

//        when (studentsList.sTUDENTGENDER) {
//            0 -> {
//                editGender.setText("Male")
//            }
//            1 -> {
//                editGender.setText("Female")
//            }
//            else -> {
//                editGender.setText("Other")
//            }
//        }
        var gender = when (studentsList[position].sTUDENTGENDER) {
            0 -> {
                "Male" + "~" + "Female" + "~" + "Other"
            }
            1 -> {
                "Female" + "~" + "Male" + "~" +"Other"
            }
            2 -> {
                "Other" + "~" + "Male" + "~" + "Female"
            }
            else -> { "Male" + "~" + "Female" + "~" + "Other"}
        }
        var gender_ = gender.split("~".toRegex()).toTypedArray()

        val gender_Sta= ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, gender_)
        gender_Sta.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender!!.adapter = gender_Sta
        spinnerGender!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                genderType = gender_[position]
                if(gender_[position] == "Male"){
                    genderType = "0"
                }else if(gender_[position] == "Female"){
                    genderType = "1"
                }else if(gender_[position] == "Other"){
                    genderType = "2"
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        var blood = ""
        when (studentsList[position].sTUDENTBLOODGROUP) {
            "O+" -> {
                blood = "O-" + "~" + "B+" + "~" + "B-" + "~" + "A+" + "~" + "A-" + "~" + "AB+" + "~" + "AB-" + "~" + "A1B-"
            }
            "O-" -> {
                blood = "O+" + "~" + "B+" + "~" + "B-" + "~" + "A+" + "~" + "A-" + "~" + "AB+" + "~" + "AB-" + "~" + "A1B-"
            }
            "B+" -> {
                blood = "O+" + "~" + "O-" + "~" +"B-" + "~" + "A+" + "~" + "A-" + "~" + "AB+" + "~" + "AB-" + "~" + "A1B-"
            }
            "B-" -> {
                blood = "O+" + "~" + "O-" + "~" + "B+" + "~" + "A+" + "~" + "A-" + "~" + "AB+" + "~" + "AB-" + "~" + "A1B-"
            }
            "A+" -> {
                blood = "O+" + "~" + "O-" + "~" + "B+" + "~" + "B-" + "~" +"A-" + "~" + "AB+" + "~" + "AB-" + "~" + "A1B-"
            }
            "A-" -> {
                blood = "O+" + "~" + "O-" + "~" + "B+" + "~" + "B-" + "~" + "A+" + "~" +"AB+" + "~" + "AB-" + "~" + "A1B-"
            }
            "AB+" -> {
                blood = "O+" + "~" + "O-" + "~" + "B+" + "~" + "B-" + "~" + "A+" + "~" + "A-" + "~" + "AB-" + "~" + "A1B-"
            }
            "AB-" -> {
                blood = "O+" + "~" + "O-" + "~" + "B+" + "~" + "B-" + "~" + "A+" + "~" + "A-" + "~" + "AB+" + "~" + "A1B-"
            }
            "A1B-" -> {
                blood = "O+" + "~" + "O-" + "~" + "B+" + "~" + "B-" + "~" + "A+" + "~" + "A-" + "~" + "AB+" + "~" + "AB-"
            }
            else -> {
                blood = "O+" + "~" + "O-" + "~" + "B+" + "~" + "B-" + "~" + "A+" + "~" + "A-" + "~" + "AB+" + "~" + "AB-" + "~" + "A1B-"
            }
        }
        var bgrp = blood.split("~".toRegex()).toTypedArray()


//        val status= ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, bgrp)
//        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerBlood!!.adapter = status
//        spinnerBlood!!.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long) {
//                 bloodGrouptype = bgrp[position]
//
//            }
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }

        editBloodGroup?.setText(studentsList[position].sTUDENTBLOODGROUP)

        editBirthPlace!!.setText(studentsList[position].sTUDENTBIRTHPLACE)
        editBirthPlaceTaluk!!.setText(studentsList[position].sTUDENTBIRTHPLACETALUK)
        editBirthPlaceDistrict!!.setText(studentsList[position].sTUDENTBIRTHPLACEDISTRICT)
        editBirthPlaceState!!.setText(studentsList[position].sTUDENTBIRTHPLACESTATE)

        editNationality!!.setText(studentsList[position].sTUDENTNATIONALITY)
        editMotherTongue!!.setText(studentsList[position].sTUDENTMOTHERTONGUE)
        editCaste!!.setText(studentsList[position].sTUDENTCASTE)
        editCasteType!!.setText(studentsList[position].sTUDENTCASTTYPE)
        editReligion!!.setText(studentsList[position].sTUDENTRELIGION)
        editLastStudied!!.setText(studentsList[position].sTUDENTLASTSTUDIED)

        editPermanentAddress!!.setText(studentsList[position].sTUDENTPADDRESS)
        editCurrentAddress!!.setText(studentsList[position].sTUDENTCADDRESS)
        editSsclRegNumber!!.setText(studentsList[position].sTUDENTSSLCREGNO)
        editAadharNumber!!.setText(studentsList[position].sTUDENTAADHARNUMBER)

        editFatherName!!.setText(studentsList[position].sTUDENTFATHERNAME)
        editMobileNumber!!.setText(studentsList[position].sTUDENTGUARDIANNUMBER)
        editFatherOccupation!!.setText(studentsList[position].sTUDENTFATHEROCCUPATION)
        editFatherQualification!!.setText(studentsList[position].sTUDENTFATHERQUALIFICATION)

        editMotherName!!.setText(studentsList[position].sTUDENTMOTHERNAME)
        editMotherNumber!!.setText(studentsList[position].sTUDENTPHONENUMBER)
        editMotherOccupation!!.setText(studentsList[position].sTUDENTMOTHEROCCUPATION)
        editMotherQualification!!.setText(studentsList[position].sTUDENTMOTHERQUALIFICATION)

//        editGuardianName!!.setText(studentsList[position].sTUDENTGUARDIANNAME)
//        editGuardianNumber!!.setText(studentsList[position].sTUDENTGUARDIANNUMBER)
        editEmailId!!.setText(studentsList[position].sTUDENTEMAILID)

        if(studentsList[position].sTUDENTDOB != null) {
            val dateFrom: Array<String> = studentsList[position].sTUDENTDOB!!.split("T").toTypedArray()
            editDobText!!.setText(Utils.dateformat(dateFrom[0]))
        }

        if(studentsList[position].aDMISSIONDATE != null) {
            val dateAdmission: Array<String> = studentsList[position].aDMISSIONDATE!!.split("T").toTypedArray()
            editAdmissionDate!!.setText(Utils.dateformat(dateAdmission[0]))
        }

        editDobText!!.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editDobText!!.windowToken, 0)
            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    var fromStr = "$year-$s-$dayOfMonth"
                    dobDate ="$dayOfMonth/$s/$year"
                    editDobText!!.setText(Utils.dateformat(fromStr))

                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("Start Date")
            mDatePicker3.datePicker.maxDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }


        editAdmissionDate!!.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editAdmissionDate!!.windowToken, 0)
            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    var fromStr = "$year-$s-$dayOfMonth"
                    admissionDate ="$dayOfMonth/$s/$year"
                    editAdmissionDate!!.setText(Utils.dateformat(fromStr))

                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("Start Date")
           // mDatePicker3.datePicker.minDate = Date().time
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

        constraintLayoutPreview?.setOnClickListener {
            //var pos = position--
            studentInfoListener.onPreviewNextListener(position,"No Previous Student",0
            )
        }

        constraintLayoutNext?.setOnClickListener {
            // var pos = position++
            studentInfoListener.onPreviewNextListener(position,"No Next Student",1
            )
        }


        var buttonSubmit = binding.buttonSubmit
        buttonSubmit.setOnClickListener {

            if(
//                studentInfoViewModel.validateFieldBott(editAdmissionDate!!,
//                    "Give Admission Date",requireActivity(), binding.constraintLayout)
//                &&
                studentInfoViewModel.validateFieldBott(editStudentName!!,
                    "Student name is mandatory",requireActivity(), binding.constraintLayout)
//                && studentInfoViewModel.validateFieldBott(editDobText!!,
//                    "Date of Birth is mandatory",requireActivity(), binding.constraintLayout)
                && studentInfoViewModel.validateFieldStrBott(genderType,"Select Gender",requireActivity(),binding.constraintLayout)
                && studentInfoViewModel.validateFieldBott(editMobileNumber!!,
                    "Enter Father/Guardian number",requireActivity(), binding.constraintLayout)) {


                if(fileNameList.isNotEmpty()){
                    //  progressStart()
                    pb?.show()
                    onFileUploadClick(imageSelection)
                }else{
                    var image = ""
                    if (studentsList[position].sTUDENTIMAGE != null) {
                        image = studentsList[position].sTUDENTIMAGE!!
                    }
                    var dob = ""
                    if(editDobText!!.text.toString().isNotEmpty()){
                        dob = Utils.parseDateToDDMMYYYY("MMM dd, yyyy","MM/dd/yyyy", editDobText!!.text.toString())!!
                    }
                    var admissionDate = ""
                    if(editAdmissionDate!!.text.toString().isNotEmpty()){
                        admissionDate = Utils.parseDateToDDMMYYYY("MMM dd, yyyy","MM/dd/yyyy", editAdmissionDate!!.text.toString())!!
                    }

                    val jsonObject = JSONObject()
                    try {

                        jsonObject.put("STUDENT_ID", studentsList[position].sTUDENTID)

                        jsonObject.put("ADMISSION_NUMBER", editAdmissionNumber!!.text.toString())
                        jsonObject.put("ADMISSION_DATE", admissionDate)

                        jsonObject.put("STUDENT_FNAME",  editStudentName!!.text.toString())
                        jsonObject.put("STUDENT_GENDER",genderType)
                        jsonObject.put("STUDENT_DOB", dob)
                        jsonObject.put("STUDENT_BLOODGROUP", editBloodGroup!!.text.toString())
                        jsonObject.put("STUDENT_BIRTHPLACE", editBirthPlace!!.text.toString())
                        jsonObject.put("STUDENT_BIRTHPLACE_TALUK", editBirthPlaceTaluk!!.text.toString())
                        jsonObject.put("STUDENT_BIRTHPLACE_DISTRICT", editBirthPlaceDistrict!!.text.toString())
                        jsonObject.put("STUDENT_BIRTHPLACE_STATE", editBirthPlaceState!!.text.toString())
                        jsonObject.put("STUDENT_NATIONALITY", editNationality!!.text.toString())
                        jsonObject.put("STUDENT_MOTHERTONGUE", editMotherTongue!!.text.toString()) ///10
                        jsonObject.put("STUDENT_RELIGION", editReligion!!.text.toString())
                        jsonObject.put("STUDENT_CASTE", editCaste!!.text.toString())
                        jsonObject.put("STUDENT_CAST_TYPE", editCasteType!!.text.toString())
                        jsonObject.put("STUDENT_LAST_STUDIED", editLastStudied!!.text.toString())
                        jsonObject.put("STUDENT_SSLC_REG_NO", editSsclRegNumber!!.text.toString())
                        jsonObject.put("STUDENT_AADHAR_NUMBER", editAadharNumber!!.text.toString())
                        jsonObject.put("STUDENT_PADDRESS", editPermanentAddress!!.text.toString())
                        jsonObject.put("STUDENT_CADDRESS", editCurrentAddress!!.text.toString())


                        jsonObject.put("STUDENT_FATHER_NAME",editFatherName!!.text.toString())
                        jsonObject.put("STUDENT_GUARDIAN_NUMBER", editMobileNumber!!.text.toString()) //25
                        jsonObject.put("STUDENT_FATHER_QUALIFICATION", editFatherQualification!!.text.toString())
                        jsonObject.put("STUDENT_FATHER_OCCUPATION", editFatherOccupation!!.text.toString())
                        jsonObject.put("STUDENT_EMAIL_ID", editEmailId!!.text.toString()) //

                        jsonObject.put("STUDENT_MOTHER_NAME", editMotherName!!.text.toString())
                        jsonObject.put("STUDENT_PHONE_NUMBER", editMotherNumber!!.text.toString())
                        jsonObject.put("STUDENT_MOTHER_QUALIFICATION", editMotherQualification!!.text.toString()) //20
                        jsonObject.put("STUDENT_MOTHER_OCCUPATION", editMotherOccupation!!.text.toString())

                        jsonObject.put("STUDENT_GUARDIAN_NAME", editFatherName!!.text.toString())

                        jsonObject.put("STUDENT_IMAGE", image)
                        jsonObject.put("SCHOOL_ID", schoolId)
                        jsonObject.put("ADMIN_ID", adminId)
                        jsonObject.put("STAFF_ID", staffId)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i(TAG,"jsonObject $jsonObject")
                    val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

                    studentInfoListener.onSubmitListener(
                        "StudentSet/StudentSetByIdNew",
                        accademicRe,
                        position,

                        editAdmissionNumber!!.text.toString(),
                        Utils.parseDateToDDMMYYYY("MMM dd, yyyy","yyyy-MM-dd", editAdmissionDate!!.text.toString())+"T00:00:00",

                        editStudentName!!.text.toString(),
                        genderType,
                        Utils.parseDateToDDMMYYYY("MMM dd, yyyy","yyyy-MM-dd", editDobText!!.text.toString())+"T00:00:00",
                        editBloodGroup!!.text.toString(),
                        editBirthPlace!!.text.toString(),
                        editBirthPlaceTaluk!!.text.toString(),
                        editBirthPlaceDistrict!!.text.toString(),
                        editBirthPlaceState!!.text.toString(),

                        editNationality!!.text.toString(),
                        editMotherTongue!!.text.toString(),
                        editReligion!!.text.toString(),
                        editCaste!!.text.toString(),
                        editCasteType!!.text.toString(),
                        editLastStudied!!.text.toString(),
                        editSsclRegNumber!!.text.toString(),
                        editAadharNumber!!.text.toString(),
                        editPermanentAddress!!.text.toString(),
                        editCurrentAddress!!.text.toString(),

                        editFatherName!!.text.toString(),
                        editMobileNumber!!.text.toString(),
                        editFatherQualification!!.text.toString(),
                        editFatherOccupation!!.text.toString(),
                        editEmailId!!.text.toString(),

                        editMotherName!!.text.toString(),
                        editMotherNumber!!.text.toString(),
                        editMotherQualification!!.text.toString(),
                        editMotherOccupation!!.text.toString(),
                        image)
                }

            }
        }


        var buttonCancel = binding.buttonCancel
        buttonCancel.setOnClickListener {
            dialog?.dismiss()
        }


//        var textStudentName  = binding.textStudentName
//        var textAdmissionNo  = binding.textAdmissionNo
//        var textRollNumber  = binding.textRollNumber
//        var textViewDob  = binding.textViewDob
//        var textClassName  = binding.textClassName
//        var textGender  = binding.textGender
//        var textBloodGroup  = binding.textBloodGroup
//        var textFatherName  = binding.textFatherName
//        var textMotherName  = binding.textMotherName
//        var textMobileNumber  = binding.textMobileNumber
//
//        textStudentName.text = studentsList.sTUDENTFNAME
//        textAdmissionNo.text = studentsList.aDMISSIONNUMBER
//        textRollNumber.text = studentsList.sTUDENTROLLNUMBER.toString()
//        if(!studentsList.sTUDENTDOB.isNullOrBlank()) {
//            val date: Array<String> =
//                studentsList.sTUDENTDOB.split("T".toRegex()).toTypedArray()
//            val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
//            textViewDob.text = Utils.formattedDate(dddd)
//        }
//        textClassName.text = studentsList.cLASSNAME
//        when (studentsList.sTUDENTGENDER) {
//            0 -> {
//                textGender.text = "Male"
//            }
//            1 -> {
//                textGender.text = "Female"
//            }
//            else -> {
//                textGender.text = "Other"
//            }
//        }
//        textBloodGroup.text = studentsList.sTUDENTBLOODGROUP
//        textFatherName.text = studentsList.sTUDENTFATHERNAME
//        textMotherName.text = studentsList.sTUDENTMOTHERNAME
//        textMobileNumber.text = studentsList.sTUDENTPHONENUMBER
//
        binding.textDeleteIcon.setOnClickListener {
            studentInfoListener.onDeleteClick(studentsList[position].sTUDENTID,studentsList[position].sTUDACCADEMICID)
        }
//
//        binding.buttonSubmit.setOnClickListener {
//            val dialog1 = DialogUpdateStudentInfo(studentInfoListener,studentsList)
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
//            dialog1.show(transaction, DialogUpdateStudentInfo.TAG)
//            dialog?.dismiss()
//        }
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
                //  progressStop()
                pb?.dismiss()
                //   hideProgress(perProgressBar,textViewProgress)
                Log.i(TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {
                //   progressStop()
                pb?.dismiss()
                //  hideProgress(perProgressBar,textViewProgress)
                Log.i(TAG,"responses ${responses[0]}")

                var dob = ""
                if(editDobText!!.text.toString().isNotEmpty()){
                    dob = Utils.parseDateToDDMMYYYY("MMM dd, yyyy","MM/dd/yyyy", editDobText!!.text.toString())!!
                }
                var admissionDate = ""
                if(editAdmissionDate!!.text.toString().isNotEmpty()){
                    admissionDate = Utils.parseDateToDDMMYYYY("MMM dd, yyyy","MM/dd/yyyy", editAdmissionDate!!.text.toString())!!
                }

                val jsonObject = JSONObject()
                try {

                    jsonObject.put("STUDENT_ID", studentsList[position].sTUDENTID)

                    jsonObject.put("ADMISSION_NUMBER", editAdmissionNumber!!.text.toString())
                    jsonObject.put("ADMISSION_DATE", admissionDate)

                    jsonObject.put("STUDENT_FNAME",  editStudentName!!.text.toString())
                    jsonObject.put("STUDENT_GENDER",genderType)
                    jsonObject.put("STUDENT_DOB", dob)
                    jsonObject.put("STUDENT_BLOODGROUP", editBloodGroup!!.text.toString())
                    jsonObject.put("STUDENT_BIRTHPLACE", editBirthPlace!!.text.toString())
                    jsonObject.put("STUDENT_BIRTHPLACE_TALUK", editBirthPlaceTaluk!!.text.toString())
                    jsonObject.put("STUDENT_BIRTHPLACE_DISTRICT", editBirthPlaceDistrict!!.text.toString())
                    jsonObject.put("STUDENT_BIRTHPLACE_STATE", editBirthPlaceState!!.text.toString())

                    jsonObject.put("STUDENT_NATIONALITY", editNationality!!.text.toString())
                    jsonObject.put("STUDENT_MOTHERTONGUE", editMotherTongue!!.text.toString()) ///10
                    jsonObject.put("STUDENT_RELIGION", editReligion!!.text.toString())
                    jsonObject.put("STUDENT_CASTE", editCaste!!.text.toString())
                    jsonObject.put("STUDENT_CAST_TYPE", editCasteType!!.text.toString())
                    jsonObject.put("STUDENT_LAST_STUDIED", editLastStudied!!.text.toString())
                    jsonObject.put("STUDENT_SSLC_REG_NO", editSsclRegNumber!!.text.toString())
                    jsonObject.put("STUDENT_AADHAR_NUMBER", editAadharNumber!!.text.toString())
                    jsonObject.put("STUDENT_PADDRESS", editPermanentAddress!!.text.toString())
                    jsonObject.put("STUDENT_CADDRESS", editCurrentAddress!!.text.toString())


                    jsonObject.put("STUDENT_FATHER_NAME",editFatherName!!.text.toString())
                    jsonObject.put("STUDENT_GUARDIAN_NUMBER", editMobileNumber!!.text.toString()) //25
                    jsonObject.put("STUDENT_FATHER_QUALIFICATION", editFatherQualification!!.text.toString())
                    jsonObject.put("STUDENT_FATHER_OCCUPATION", editFatherOccupation!!.text.toString())
                    jsonObject.put("STUDENT_EMAIL_ID", editEmailId!!.text.toString()) //

                    jsonObject.put("STUDENT_MOTHER_NAME", editMotherName!!.text.toString())
                    jsonObject.put("STUDENT_PHONE_NUMBER", editMotherNumber!!.text.toString())
                    jsonObject.put("STUDENT_MOTHER_QUALIFICATION", editMotherQualification!!.text.toString()) //20
                    jsonObject.put("STUDENT_MOTHER_OCCUPATION", editMotherOccupation!!.text.toString())

                    jsonObject.put("STUDENT_GUARDIAN_NAME", editFatherName!!.text.toString())

                    jsonObject.put("STUDENT_IMAGE", responses[0])
                    jsonObject.put("SCHOOL_ID", schoolId)
                    jsonObject.put("ADMIN_ID", adminId)
                    jsonObject.put("STAFF_ID", staffId)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.i(TAG,"jsonObject $jsonObject")
                val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

                studentInfoListener.onSubmitListener(
                    "StudentSet/StudentSetByIdNew",
                    accademicRe,
                    position,

                    editAdmissionNumber!!.text.toString(),
                    Utils.parseDateToDDMMYYYY("MMM dd, yyyy","yyyy-MM-dd", editAdmissionDate!!.text.toString())+"T00:00:00",

                    editStudentName!!.text.toString(),
                    genderType,
                    Utils.parseDateToDDMMYYYY("MMM dd, yyyy","yyyy-MM-dd", editDobText!!.text.toString())+"T00:00:00",
                    editBloodGroup!!.text.toString(),
                    editBirthPlace!!.text.toString(),
                    editBirthPlaceTaluk!!.text.toString(),
                    editBirthPlaceDistrict!!.text.toString(),
                    editBirthPlaceState!!.text.toString(),

                    editNationality!!.text.toString(),
                    editMotherTongue!!.text.toString(),
                    editReligion!!.text.toString(),
                    editCaste!!.text.toString(),
                    editCasteType!!.text.toString(),
                    editLastStudied!!.text.toString(),
                    editSsclRegNumber!!.text.toString(),
                    editAadharNumber!!.text.toString(),
                    editPermanentAddress!!.text.toString(),
                    editCurrentAddress!!.text.toString(),

                    editFatherName!!.text.toString(),
                    editMobileNumber!!.text.toString(),
                    editFatherQualification!!.text.toString(),
                    editFatherOccupation!!.text.toString(),
                    editEmailId!!.text.toString(),

                    editMotherName!!.text.toString(),
                    editMotherNumber!!.text.toString(),
                    editMotherQualification!!.text.toString(),
                    editMotherOccupation!!.text.toString(),

                    responses[0]
                )


            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                //  updateProgress(totalpercent, "Uploading file $filenumber", "", perProgressBar,textViewProgress)
                Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })
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

    companion object {
        var TAG = "BottomSheetFragment"
    }
}