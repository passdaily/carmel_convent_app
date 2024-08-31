package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.student_info

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
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogUpdateStudentInfoBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Suppress("DEPRECATION")
class DialogUpdateStudentInfo : DialogFragment {

    lateinit var studentInfoListener: StudentInfoListener

    companion object {
        var TAG = "DialogCreateStaff"
    }

    private var _binding: DialogUpdateStudentInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var studentInfoViewModel: StudentInfoViewModel

    lateinit var studentsList: StudentsInfoListModel.Students


    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    var toolbar : Toolbar? = null
    var constraintLeave : ConstraintLayout? = null

    var buttonSubmit : AppCompatButton? =null

    var dobDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleWhite)
    }

    constructor(studentInfoListener: StudentInfoListener,studentsList: StudentsInfoListModel.Students) {
        this.studentInfoListener = studentInfoListener
        this.studentsList = studentsList

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

        _binding = DialogUpdateStudentInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId =  user[0].SCHOOL_ID
        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_black)
        toolbar?.title = "Update Student Info"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.black))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }
        constraintLeave = binding.constraintLeave

        var editStudentName   = binding.editStudentName
        var editAdmissionNumber = binding.editAdmissionNumber
        var editAdmissionDate = binding.editAdmissionDate
      //  var editRollNumber = binding.editRollNumber
        var editDobText   = binding.editDobText
        var editClassName  = binding.editClassName
        var editGender   = binding.editGender
        editGender.isEnabled = false
        var editBloodGroup   = binding.editBloodGroup
        var editFatherName  = binding.editFatherName
        var editMotherName  = binding.editMotherName
        var editMobileNumber  = binding.editMobileNumber
        var editBirthPlace  = binding.editBirthPlace
        var editNationality = binding.editNationality
        var editMotherTongue = binding.editMotherTongue
        var editCaste = binding.editCaste
        var editReligion = binding.editReligion
        var editLastStudied = binding.editLastStudied
        var editPermanentAddress = binding.editPermanentAddress
        var editFatherOccupation = binding.editFatherOccupation
        var editFatherQualification = binding.editFatherQualification
        var editMotherOccupation = binding.editMotherOccupation
        var editMotherQualification = binding.editMotherQualification
        var editGuardianName  = binding.editGuardianName
        var editGuardianNumber = binding.editGuardianNumber
        var editEmailId = binding.editEmailId

        editDobText.inputType = InputType.TYPE_NULL
        editDobText.keyListener = null

        editStudentName.setText(studentsList.sTUDENTFNAME)
        editAdmissionNumber.setText(studentsList.aDMISSIONNUMBER)
       // editRollNumber.setText(studentsList.sTUDENTROLLNUMBER)
        editClassName.setText(studentsList.cLASSNAME)
        when (studentsList.sTUDENTGENDER) {
            0 -> {
                editGender.setText("Male")
            }
            1 -> {
                editGender.setText("Female")
            }
            else -> {
                editGender.setText("Other")
            }
        }

        editBloodGroup.setText(studentsList.sTUDENTBLOODGROUP)
        editFatherName.setText(studentsList.sTUDENTFATHERNAME)
        editMotherName.setText(studentsList.sTUDENTMOTHERNAME)
        editMobileNumber.setText(studentsList.sTUDENTPHONENUMBER)
        editBirthPlace.setText(studentsList.sTUDENTBIRTHPLACE)

        editNationality.setText(studentsList.sTUDENTNATIONALITY)
        editMotherTongue.setText(studentsList.sTUDENTMOTHERTONGUE)
        editCaste.setText(studentsList.sTUDENTCASTE)
        editReligion.setText(studentsList.sTUDENTRELIGION)
        editLastStudied.setText(studentsList.sTUDENTLASTSTUDIED)


        editPermanentAddress.setText(studentsList.sTUDENTPADDRESS)
        editFatherOccupation.setText(studentsList.sTUDENTFATHEROCCUPATION)
        editFatherQualification.setText(studentsList.sTUDENTFATHERQUALIFICATION)
        editMotherOccupation.setText(studentsList.sTUDENTMOTHEROCCUPATION)
        editMotherQualification.setText(studentsList.sTUDENTMOTHERQUALIFICATION)

        editGuardianName.setText(studentsList.sTUDENTGUARDIANNAME)
        editGuardianNumber.setText(studentsList.sTUDENTGUARDIANNUMBER)
        editEmailId.setText(studentsList.sTUDENTEMAILID)

        if(studentsList.sTUDENTDOB != null) {
            val dateFrom: Array<String> = studentsList.sTUDENTDOB!!.split("T").toTypedArray()
            editDobText.setText(Utils.dateformat(dateFrom[0]))
        }

        if(studentsList.aDMISSIONDATE != null) {
            val dateAdmission: Array<String> = studentsList.aDMISSIONDATE!!.split("T").toTypedArray()
            editAdmissionDate.setText(Utils.dateformat(dateAdmission[0]))
        }

        editDobText.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editDobText.windowToken, 0)
            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    var fromStr = "$year-$s-$dayOfMonth"
                    dobDate ="$dayOfMonth/$s/$year"
                    editDobText.setText(Utils.dateformat(fromStr))

                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("Start Date")
            mDatePicker3.datePicker.maxDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }




        editAdmissionDate.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editAdmissionDate.windowToken, 0)
            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    var fromStr = "$year-$s-$dayOfMonth"
                    dobDate ="$dayOfMonth/$s/$year"
                    editAdmissionDate.setText(Utils.dateformat(fromStr))

                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("Start Date")
            mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.setOnClickListener {

            if(studentInfoViewModel.validateField(editAdmissionDate,
                    "Give Admission Date",requireActivity(), binding.constraintLeave)
                && studentInfoViewModel.validateField(editDobText,
                    "Give Date of birth",requireActivity(), binding.constraintLeave)) {


                var url = "StudentSet/StudentSetById"

                val jsonObject = JSONObject()
                try {

                    //{
                    //        "STUDENT_ID" : 3063,
                    //  "ADMISSION_NUMBER" : "ABCDEFG001",
                    //  "ADMISSION_DATE" : "04/25/2018",
                    //  "STUDENT_FNAME" : "Sakeer Hussain",
                    //  "STUDENT_GENDER" : 0,
                    //  "STUDENT_DOB"   : "04/18/1990",
                    //  "STUDENT_BLOODGROUP" : "O+ve",
                    //  "STUDENT_BIRTHPLACE" : "Palakkad",
                    //  "STUDENT_NATIONALITY"  : "Indian",
                    //  "STUDENT_MOTHERTONGUE" : "Malayalam",
                    //  "STUDENT_RELIGION"  : "Islam",
                    //  "STUDENT_CASTE"   : "Mappilla",
                    //  "STUDENT_LAST_STUDIED" :"Vennakkara" ,
                    //  "STUDENT_PADDRESS"   :"Palakkad, Kerala",
                    //  "STUDENT_CADDRESS" : "Palakkad, Kerala",
                    //  "STUDENT_FATHER_NAME" : "Shahul Hameed",
                    //  "STUDENT_FATHER_QUALIFICATION" : "MBA",
                    //  "STUDENT_FATHER_OCCUPATION"  : "Business",
                    //  "STUDENT_MOTHER_NAME"    : "Safiya",
                    //  "STUDENT_MOTHER_QUALIFICATION"  : "10th std",
                    //  "STUDENT_MOTHER_OCCUPATION"  : "House Wife",
                    //  "STUDENT_PHONE_NUMBER"    : "04912523398",
                    //  "STUDENT_GUARDIAN_NAME"   : "Shahul Hameed",
                    //  "STUDENT_EMAIL_ID"    : "anver.khan86@gmail.com",
                    //  "STUDENT_GUARDIAN_NUMBER" : "5556669997"
                    //}

                    jsonObject.put("STUDENT_ID", studentsList.sTUDENTID)
                    jsonObject.put("ADMISSION_NUMBER", editAdmissionNumber.text.toString())
                    jsonObject.put("ADMISSION_DATE", Utils.parseDateToDDMMYYYY("MMM dd, yyyy","MM/dd/yyyy", editAdmissionDate.text.toString()))
                    jsonObject.put("STUDENT_FNAME",  editStudentName.text.toString())
                    jsonObject.put("STUDENT_GENDER", studentsList.sTUDENTGENDER.toString())

                    jsonObject.put("STUDENT_DOB", Utils.parseDateToDDMMYYYY("MMM dd, yyyy","MM/dd/yyyy", editDobText.text.toString()))
                    jsonObject.put("STUDENT_BLOODGROUP", studentsList.sTUDENTGENDER)
                    jsonObject.put("STUDENT_BIRTHPLACE", editBirthPlace.text.toString())
                    jsonObject.put("STUDENT_NATIONALITY", editNationality.text.toString())
                    jsonObject.put("STUDENT_MOTHERTONGUE", editMotherTongue.text.toString()) ///10

                    jsonObject.put("STUDENT_RELIGION", editReligion.text.toString())
                    jsonObject.put("STUDENT_CASTE", editCaste.text.toString())
                    jsonObject.put("STUDENT_LAST_STUDIED", editLastStudied.text.toString())
                    jsonObject.put("STUDENT_PADDRESS", editPermanentAddress.text.toString())
                    jsonObject.put("STUDENT_CADDRESS", studentsList.sTUDENTCADDRESS)

                    jsonObject.put("STUDENT_FATHER_NAME",editFatherName.text.toString())
                    jsonObject.put("STUDENT_FATHER_QUALIFICATION", editFatherQualification.text.toString())
                    jsonObject.put("STUDENT_FATHER_OCCUPATION", editFatherOccupation.text.toString())
                    jsonObject.put("STUDENT_MOTHER_NAME", editMotherName.text.toString())
                    jsonObject.put("STUDENT_MOTHER_QUALIFICATION", editMotherQualification.text.toString()) //20

                    jsonObject.put("STUDENT_MOTHER_OCCUPATION", editMotherOccupation.text.toString())
                    jsonObject.put("STUDENT_PHONE_NUMBER", editMobileNumber.text.toString())
                    jsonObject.put("STUDENT_GUARDIAN_NAME", editGuardianName.text.toString())
                    jsonObject.put("STUDENT_EMAIL_ID", editEmailId.text.toString()) //
                    jsonObject.put("STUDENT_GUARDIAN_NUMBER", editGuardianNumber.text.toString()) //25

                    jsonObject.put("SCHOOL_ID", schoolId)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.i(TAG,"jsonObject $jsonObject")
                val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                studentInfoViewModel.getCommonPostFun(url,accademicRe)
                    .observe(requireActivity(), Observer {
                        it?.let { resource ->
                            Log.i(TAG,"resource $resource")
                            Log.i(TAG,"isSuccessful ${resource.data?.isSuccessful}")
                            Log.i(TAG,"errorBody ${resource.data?.errorBody().toString()}")
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    val response = resource.data?.body()!!
                                   // progressStop()
                                    Log.i(TAG,"isSuccessful ${resource.data.isSuccessful}")
                                    Log.i(TAG,"errorBody ${resource.data.errorBody().toString()}")
                                    Log.i(TAG,"errorBody ${resource.data.errorBody()?.string()}")
                                    when {
                                        Utils.resultFun(response) == "SUCCESS" -> {
                                            studentInfoListener.onSuccessMessage("Student Updated Successfully")
                                            Utils.getSnackBarGreen(requireActivity(), "Student Updated Successfully", constraintLeave!!)
                                            cancelFrg()
                                        }
                                        Utils.resultFun(response) == "ERROR" -> {
                                            Utils.getSnackBar4K(requireActivity(), "Admission Number Already Exist", constraintLeave!!)
                                        }
                                        else -> {
                                            Utils.getSnackBar4K(requireActivity(), "Failed While Creating student", constraintLeave!!)
                                        }
                                    }
                                }
                                Status.ERROR -> {
                                //    progressStop()
                                    Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLeave!!)
                                }
                                Status.LOADING -> {
                                 //   progressStart()
                                    Log.i(TAG,"loading")
                                }
                            }
                        }
                    })
            }
        }


        val constraintLeave = binding.constraintLeave
        constraintLeave.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLeave.windowToken, 0)
        }



        var buttonCancel = binding.buttonCancel
        buttonCancel.setOnClickListener {   cancelFrg() }
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


}