package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.descriptive_exam

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreateObjExamBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_scheduled.CreateZoomScheduled
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class CreateDescriptiveExam : DialogFragment {

    lateinit var descriptiveExamListener: DescriptiveExamListener

    companion object {
        var TAG = "CreateDescriptiveExam"
    }

    private var _binding: DialogCreateObjExamBinding? = null
    private val binding get() = _binding!!


    private lateinit var descriptiveExamStaffViewModel: DescriptiveExamStaffViewModel


    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0
    var adminId = 0
    var schoolId = 0
    var typeStr =""

    var toolbar : Toolbar? = null
    var constraintLayoutContent : ConstraintLayout? = null

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null
    var spinnerSubject: AppCompatSpinner? = null
    var textStartDate: TextInputEditText? = null
    var textEndDate : TextInputEditText? = null
    var spinnerStatus : AppCompatSpinner? = null

    var textStartTime : TextInputEditText? = null
    var textEndTime : TextInputEditText? = null

    var editTextTitle : TextInputEditText? =null
    var editTextDesc : TextInputEditText? =null

    var editTextDuration  : TextInputEditText? =null
    var editTextAllowPause  : TextInputEditText? =null

    var fromStr = ""
    var toStr = ""

    var startDate = ""
    var endDate = ""
    var buttonSubmit : AppCompatButton? =null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()
    var getChapter = ArrayList<ChaptersListStaffModel.Chapters>()
    private lateinit var localDBHelper : LocalDBHelper

    var type = arrayOf("Unpublished", "Published")

    var startTime = ""
    var endTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    constructor(descriptiveExamListener: DescriptiveExamListener) {
        this.descriptiveExamListener = descriptiveExamListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        descriptiveExamStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[DescriptiveExamStaffViewModel::class.java]

        _binding = DialogCreateObjExamBinding.inflate(inflater, container, false)
        return binding.root
    }

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
        toolbar?.title = "Create Descriptive Exam"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.white))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass
        spinnerSubject = binding.spinnerSubject
        textStartDate = binding.textStartDate
        textEndDate = binding.textEndDate
        textStartTime = binding.textStartTime
        textEndTime = binding.textEndTime
        spinnerStatus  = binding.spinnerStatus

        editTextTitle  = binding.editTextTitle
        editTextDesc  = binding.editTextDesc
        editTextDuration = binding.editTextDuration
        editTextAllowPause  = binding.editTextAllowPause


        textStartDate?.inputType = InputType.TYPE_NULL
        textStartDate?.keyListener = null
        textEndDate?.inputType = InputType.TYPE_NULL
        textEndDate?.keyListener = null

        textStartTime?.inputType = InputType.TYPE_NULL
        textStartTime?.keyListener = null
        textEndTime?.inputType = InputType.TYPE_NULL
        textEndTime?.keyListener = null


        textStartDate!!.isClickable = true;
        textEndDate!!.isClickable = true;
        textStartTime!!.isClickable = true;
        textEndTime!!.isClickable = true;

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
                view: View, position: Int, id: Long) {
                sUBJECTID = getSubject[position].sUBJECTID
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        textStartDate?.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(textStartDate?.windowToken, 0)
            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    fromStr = "$year-$s-$dayOfMonth"
                    startDate ="$s/$dayOfMonth/$year"
                    if(toStr.isNotEmpty()) {
                        if (Utils.checkDatesAfter(fromStr, toStr)) {
                            textStartDate?.setText(Utils.dateformat(fromStr))
                        }else{
                            Toast.makeText(activity, "Give Valid Date", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        textStartDate?.setText(Utils.dateformat(fromStr))
                    }


                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("Start Date")
            mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }

        textEndDate?.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(textEndDate?.windowToken, 0)

            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    toStr = "$year-$s-$dayOfMonth"

                    if(fromStr.isNotEmpty()) {
                        if (Utils.checkDatesAfter(fromStr, toStr)) {
                            textEndDate?.setText(Utils.dateformat(toStr))
                            endDate ="$s/$dayOfMonth/$year"
                        }else{
                            Toast.makeText(activity, "Give Valid Date", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(activity, "Give Start Date", Toast.LENGTH_SHORT).show()
                    }
                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("End Date")
            mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

        }


        textStartTime?.setOnClickListener(View.OnClickListener { // Get Current Time
            val c = Calendar.getInstance()
            val mHour = c[Calendar.HOUR_OF_DAY]
            val mMinute = c[Calendar.MINUTE]

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(requireActivity(),
                { _, hourOfDay, minute ->
                    val isPM = hourOfDay >= 12
                    startTime = String.format(
                        "%02d:%02d %s",
                        if (hourOfDay == 12 || hourOfDay == 0) 12 else hourOfDay % 12,
                        minute,
                        if (isPM) "PM" else "AM"
                    )
                    textStartTime?.setText(
                        String.format(
                            "%02d:%02d %s",
                            if (hourOfDay == 12 || hourOfDay == 0) 12 else hourOfDay % 12,
                            minute,
                            if (isPM) "PM" else "AM"
                        )
                    )
                    // end_time.setText(hourOfDay + ":" + minute +AM_PM);
                    Log.i(CreateZoomScheduled.TAG, "end_time $startTime")
                }, mHour, mMinute, false
            )
            timePickerDialog.setTitle("Start Time")
            timePickerDialog.show()
            timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        })


        textEndTime?.setOnClickListener(View.OnClickListener { // Get Current Time
            val c = Calendar.getInstance()
            val mHour = c[Calendar.HOUR_OF_DAY]
            val mMinute = c[Calendar.MINUTE]

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(requireActivity(),
                { _, hourOfDay, minute ->
                    val isPM = hourOfDay >= 12
                    endTime = String.format(
                        "%02d:%02d %s",
                        if (hourOfDay == 12 || hourOfDay == 0) 12 else hourOfDay % 12,
                        minute,
                        if (isPM) "PM" else "AM"
                    )
                    textEndTime?.setText(
                        String.format(
                            "%02d:%02d %s",
                            if (hourOfDay == 12 || hourOfDay == 0) 12 else hourOfDay % 12,
                            minute,
                            if (isPM) "PM" else "AM"
                        )
                    )
                    // end_time.setText(hourOfDay + ":" + minute +AM_PM);
                    Log.i(CreateZoomScheduled.TAG, "end_time $endTime")
                }, mHour, mMinute, false
            )
            timePickerDialog.setTitle("End Time")
            timePickerDialog.show()
            timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        })


        val status = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, type)
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus?.adapter = status
        spinnerStatus?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    typeStr = position.toString()
                } else if (position == 1) {
                    typeStr = position.toString()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.text = requireActivity().resources.getString(R.string.create_exam)
        buttonSubmit?.setOnClickListener {
            if(descriptiveExamStaffViewModel.validateField(textStartDate!!,
                "Start Date field cannot be empty",requireActivity(),constraintLayoutContent!!)
                && descriptiveExamStaffViewModel.validateField(textEndDate!!,
                    "End Date field cannot be empty",requireActivity(),constraintLayoutContent!!)
                && descriptiveExamStaffViewModel.validateField(textStartTime!!,
                    "Start Time field cannot be empty",requireActivity(),constraintLayoutContent!!)
                && descriptiveExamStaffViewModel.validateField(textEndTime!!,
                    "End Time field cannot be empty",requireActivity(),constraintLayoutContent!!)
                && descriptiveExamStaffViewModel.validateField(editTextDuration!!,
                    "Duration field cannot be empty",requireActivity(),constraintLayoutContent!!)
                && descriptiveExamStaffViewModel.validateField(editTextAllowPause!!,
                    "Please give allowed pauses",requireActivity(),constraintLayoutContent!!)
                && descriptiveExamStaffViewModel.validateField(editTextTitle!!,
                    "Title field cannot be empty",requireActivity(),constraintLayoutContent!!)
                && descriptiveExamStaffViewModel.validateField(editTextDesc!!,
                    "Description field cannot be empty",requireActivity(),constraintLayoutContent!!)){


          //postParam.put("ADMIN_ID",Global.Admin_id);
                //        postParam.put("ACCADEMIC_ID",s_aid);
                //        postParam.put("CLASS_ID", scid);
                //        postParam.put("SUBJECT_ID", ssid);
                //
                //        postParam.put("EXAM_NAME", exam_name);
                //        postParam.put("EXAM_DESCRIPTION", exam_description);
                //
                //        postParam.put("START_TIME", start_date_and_time);
                //        postParam.put("END_TIME", end_date_and_time);
                //
                //        postParam.put("EXAM_DURATION", exam_duration);
                //        postParam.put("ALLOWED_PAUSE", "5");
                //        postParam.put("STATUS", status);
                //http://demostaff.passdaily.in/ElixirApi/OnlineDExam/AddOnlineDExam
                val url = "OnlineDExam/AddOnlineDExam"
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("SCHOOL_ID", schoolId)
                    jsonObject.put("ADMIN_ID", adminId)
                    jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
                    jsonObject.put("CLASS_ID", cLASSID)
                    jsonObject.put("SUBJECT_ID", sUBJECTID)

                    jsonObject.put("EXAM_NAME", editTextTitle?.text.toString())
                    jsonObject.put("EXAM_DESCRIPTION", editTextDesc?.text.toString())

                    jsonObject.put("START_TIME", "$startDate $startTime")
                    jsonObject.put("END_TIME","$endDate $endTime")
                    jsonObject.put("EXAM_DURATION", editTextDuration?.text.toString())
                    jsonObject.put("ALLOWED_PAUSE", editTextAllowPause?.text.toString())
                    jsonObject.put("STATUS", typeStr)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.i(TAG,"jsonObject $jsonObject")
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
                                            descriptiveExamListener.onCreateClick("Exam Created Successfully")
                                            cancelFrg()
                                        }
                                        Utils.resultFun(response) == "EXIST" -> {
                                            Utils.getSnackBar4K(requireActivity(), "Exam Already Exist", constraintLayoutContent!!)
                                        }
                                        else -> {
                                            Utils.getSnackBar4K(requireActivity(), "Exam Creation Failed", constraintLayoutContent!!)
                                        }
                                    }
                                }
                                Status.ERROR -> {
                                    progressStop()
                                    Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                                }
                                Status.LOADING -> {
                                    progressStart()
                                    Log.i(TAG,"loading")
                                }
                            }
                        }
                    })

            }

        }
        val constraintLayoutContent = binding.constraintLayoutContent
        constraintLayoutContent.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLayoutContent.windowToken, 0)
        }


        initFunction()
        Utils.setStatusBarColor(requireActivity())
    }


    private fun initFunction() {
        descriptiveExamStaffViewModel.getYearClassExam(adminId,schoolId)
            .observe(this, Observer {
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
                            var classX = Array(getClass.size){""}
                            for (i in getClass.indices) {
                                classX[i] = getClass[i].cLASSNAME
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    classX
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

    fun getSubjectList(cLASSID : Int){
        descriptiveExamStaffViewModel.getSubjectStaff(cLASSID,adminId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getSubject = response.subjects as ArrayList<SubjectsModel.Subject>
                            var subject = Array(getSubject.size){""}
                            if(subject.isNotEmpty()){
                                for (i in getSubject.indices) {
                                    subject[i] = getSubject[i].sUBJECTNAME
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

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            getSubject = ArrayList<SubjectsModel.Subject>()
                            getChapter = ArrayList<ChaptersListStaffModel.Chapters>()
                            Log.i(TAG,"getSubjectList LOADING")
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
}