package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.manage_guardian

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateGuardianBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.BottomSheetCreateGroup
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class BottomSheetUpdateGuardian : BottomSheetDialogFragment {

    private lateinit var guardianViewModel: GuardianViewModel

    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var staffId = 0

    private var _binding: BottomSheetUpdateGuardianBinding? = null
    private val binding get() = _binding!!

    lateinit var guardianListener: GuardianListener

    lateinit var studentsList: StudentsInfoListModel.Students

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var readPermission = false
    private var writePermission = false


    var toolbar : Toolbar? = null
    var constraintLeave : ConstraintLayout? = null

    var buttonSubmit : AppCompatButton? =null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getStudents  = ArrayList<GetStudentsListModel.Students>()

    var aCCADEMICID = 8
    var cLASSID = 0
    var sTUDENTID = 0
    var editPtaName : TextInputEditText? = null
    var spinnerAccedamic  : AppCompatSpinner? = null
    var spinnerClass  : AppCompatSpinner? = null
    var spinnerStudent  : AppCompatSpinner? = null
    var spinnerGender     : AppCompatSpinner? = null
    var editDobText    : TextInputEditText? = null

    var editStudentName : TextInputEditText? = null
    var editAdmissionNumber : TextInputEditText? = null
    var editGuardianName : TextInputEditText? = null
    var editGuardianNumber  : TextInputEditText? = null

    var editPtaAddress : TextInputEditText? = null
    var spinnerStatus  : AppCompatSpinner? = null

    lateinit var guardianList: ArrayList<GuardianListModel.Student>

    var getClass = ArrayList<GetYearClassExamModel.Class>()

    var isWorking = false

    var genderType = ""
    var dobDate = ""

    var position = 0

    var typGenderStr = -1
    constructor()

    constructor(guardianListener: GuardianListener,guardianList: ArrayList<GuardianListModel.Student>, position: Int) {
        this.guardianListener = guardianListener
        this.guardianList = guardianList
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

        guardianViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[GuardianViewModel::class.java]

        _binding = BottomSheetUpdateGuardianBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        staffId = user[0].STAFF_ID
        constraintLeave  = binding.constraintLeave



        editStudentName  = binding.editStudentName
        editAdmissionNumber  = binding.editAdmissionNumber
        editGuardianName = binding.editGuardianName
        editGuardianNumber = binding.editGuardianNumber
        spinnerGender   = binding.spinnerGender
        editDobText   = binding.editDobText

        editStudentName?.setText(guardianList[position].sTUDENTFNAME)
        editAdmissionNumber?.setText(guardianList[position].aDMISSIONNUMBER)
        editGuardianName?.setText(guardianList[position].sTUDENTGUARDIANNAME)

        editGuardianNumber?.setText(guardianList[position].sTUDENTGUARDIANNUMBER)

        editDobText!!.inputType = InputType.TYPE_NULL
        editDobText!!.keyListener = null

        var gender = when (guardianList[position].sTUDENTGENDER) {
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
        if(!guardianList[position].sTUDENTDOB.isNullOrBlank()) {
            val dateFrom: Array<String> = guardianList[position].sTUDENTDOB.split("T").toTypedArray()
            editDobText!!.setText(Utils.dateformat(dateFrom[0]))
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

        binding.constraintLayoutPreview.setOnClickListener {
            //var pos = position--
            guardianListener.onPreviewNextListener(position,"No Previous Student",0
            )
        }

        binding.constraintLayoutNext.setOnClickListener {
            // var pos = position++
            guardianListener.onPreviewNextListener(position,"No Next Student",1
            )
        }


        val submitButton = binding.buttonSubmit
        submitButton.setOnClickListener {

            if(guardianViewModel.validateField(editStudentName!!,
                    "Give Student Name",requireActivity(), binding.constraintLeave)
                && guardianViewModel.validateField(editAdmissionNumber!!,
                    "Give Admission Number",requireActivity(), binding.constraintLeave)
                && guardianViewModel.validateField(editGuardianName!!,
                    "Give Guardian Name",requireActivity(), binding.constraintLeave)
                && guardianViewModel.validateField(editGuardianNumber!!,
                    "Give Guardian Mobile Number",requireActivity(), binding.constraintLeave)
            ){
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("STUDENT_ID", guardianList[position].sTUDENTID)
                    jsonObject.put("ADMISSION_NUMBER", editAdmissionNumber?.text.toString())
                    jsonObject.put("STUDENT_FNAME", editStudentName?.text.toString())
                    jsonObject.put("STUDENT_GUARDIAN_NAME", editGuardianName?.text.toString())
                    jsonObject.put("STUDENT_GUARDIAN_NUMBER", editGuardianNumber?.text.toString())
                    jsonObject.put("STUDENT_DOB", Utils.parseDateToDDMMYYYY("MMM dd, yyyy","MM/dd/yyyy", editDobText!!.text.toString()))
                    jsonObject.put("STUDENT_GENDER",genderType)
                    jsonObject.put("ADMIN_ID", adminId)
                    jsonObject.put("STAFF_ID", staffId)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.i(TAG,"jsonObject $jsonObject")
                val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                guardianListener.onSubmitDetails("StudentEdit/StudentQuickEdit",submitItems,position,
                    editAdmissionNumber?.text.toString(),editStudentName?.text.toString(),
                    editGuardianName?.text.toString(),editGuardianNumber?.text.toString(),
                    Utils.parseDateToDDMMYYYY("MMM dd, yyyy","yyyy-MM-dd", editDobText!!.text.toString())+"T00:00:00",
                    genderType
                )
            }

//            if (editStudentName?.text.toString().isNotEmpty()) {
//                if (editAdmissionNumber?.text.toString().isNotEmpty()) {
//                    if (editGuardianName?.text.toString().isNotEmpty()) {
//                        if(editGuardianNumber?.text.toString().isNotEmpty()) {
//
//
//
//                        } else {
//                            guardianListener.onErrorMessageClicker("Enter Guardian Mobile Number")
//                        }
//                    } else {
//                        guardianListener.onErrorMessageClicker("Enter Guardian Name")
//                    }
//                } else {
//                    guardianListener.onErrorMessageClicker("Enter Admission Number")
//                }
//            } else {
//                guardianListener.onErrorMessageClicker("Give Student Name")
//
//            }

        }

    }


    companion object {
        var TAG = "BottomSheetFragment"
    }
}