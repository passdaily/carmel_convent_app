package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_scheduled

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
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreateZoomScheduledBinding
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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class UpdateZoomScheduled : DialogFragment {

    lateinit var zoomScheduledListener: ZoomScheduledListener

    companion object {
        var TAG = "UpdateZoomScheduled"
    }

    private var _binding: DialogCreateZoomScheduledBinding? = null
    private val binding get() = _binding!!


    private lateinit var zoomScheduledViewModel: ZoomScheduledViewModel

    lateinit var zoomList: ZoomScheduleStaffModel.Zoom


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
    var textStartTime : TextInputEditText? = null
    var spinnerStatus : AppCompatSpinner? = null

    var editTextMeetingName : TextInputEditText? =null
    var editTextMeetingId : TextInputEditText? =null

    var editTextMeetingPassword : TextInputEditText? =null
    var editTextMeetingLink : TextInputEditText? =null


    var fromStr = ""
    var toStr = ""

    var startDate = ""
    var endDate = ""

    var startTime = ""
    var buttonSubmit : AppCompatButton? =null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()
    var getChapter = ArrayList<ChaptersListStaffModel.Chapters>()
    private lateinit var localDBHelper : LocalDBHelper

    var type = arrayOf("Unpublished", "Published")
    var typStr = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleUpdate)
    }

    constructor(zoomScheduledListener: ZoomScheduledListener, zoomList: ZoomScheduleStaffModel.Zoom) {
        this.zoomList = zoomList
        this.zoomScheduledListener = zoomScheduledListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        zoomScheduledViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ZoomScheduledViewModel::class.java]

        _binding = DialogCreateZoomScheduledBinding.inflate(inflater, container, false)
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
        toolbar?.title = "Update Zoom Scheduled"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.white))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass
        spinnerStatus  = binding.spinnerStatus
        textStartDate = binding.textStartDate
        textStartTime = binding.textStartTime


        editTextMeetingName  = binding.editTextMeetingName
        editTextMeetingId  = binding.editTextMeetingId
        editTextMeetingPassword = binding.editTextMeetingPassword
        editTextMeetingLink  = binding.editTextMeetingLink

        editTextMeetingName?.setText(zoomList.mEETINGTITLE)
        editTextMeetingId?.setText(zoomList.mEETINGID)
        editTextMeetingPassword?.setText(zoomList.mEETINGPASSWORD)
        editTextMeetingLink?.setText(zoomList.mEETINGLINK)


        textStartDate?.inputType = InputType.TYPE_NULL
        textStartDate?.keyListener = null
        textStartTime?.inputType = InputType.TYPE_NULL
        textStartTime?.keyListener = null

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

//2020-05-07T17:32:02.917
        val dateFrom: Array<String> = zoomList.mEETINGSTATRDATE.split("T").toTypedArray()
        val startDate = dateFrom[0].split("-").toTypedArray()
        textStartDate?.setText(Utils.dateformat(dateFrom[0]))
        //   var startDate : String = textStartDate?.text.toString()

        textStartDate?.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(textStartDate?.windowToken, 0)

            val mYear = startDate[0].toInt()
            val mMonth = (startDate[1].toInt()-1)
            val mDay  =startDate[2].toInt()
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    fromStr = "$year-$s-$dayOfMonth"
//                    startDate ="$s/$dayOfMonth/$year"
                    textStartDate?.setText(Utils.dateformat(fromStr))
                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("Start Date")
            mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }



        Log.i(TAG,"zoomList.mEETINGSTATRDATE ${zoomList.mEETINGSTATRDATE}")
        val timeFrom: Array<String> = zoomList.mEETINGSTATRDATE.split("T").toTypedArray()
        val dddd: Long = Utils.longconversion(timeFrom[0] + " " + timeFrom[1])
        val updateTime = Utils.formateTimeMap(dddd)
        textStartTime?.setText(updateTime)

        textStartTime?.setOnClickListener{ // Get Current Time
            val inputFormat: DateFormat = SimpleDateFormat("hh:mm aa", Locale.US)
            val outputFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.US)
            val inputTime = outputFormat.format(inputFormat.parse(updateTime))

            var inputHours = inputTime.substring(0, 2)
            var inputMinutes = inputTime.substring(3, 5)

//            val c = Calendar.getInstance()
//            val mHour = c[Calendar.HOUR_OF_DAY]
//            val mMinute = c[Calendar.MINUTE]

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
                    Log.i(TAG, "end_time $startTime")
                }, inputHours.toInt(), inputMinutes.toInt(), false
            )
            timePickerDialog.setTitle("Start Time")
            timePickerDialog.show()
            timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }



        var str = ""
        typStr = zoomList.mEETINGSTATUS
        str = if (typStr == 0) {
            "Unpublished~Published"
        } else {
            "Published~Unpublished"
        }
        type = str.split("~").toTypedArray()
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
                if (type[position] == "Unpublished") {
                    typeStr = "0"
                } else if (type[position] == "Published") {
                    typeStr = "1"
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.setBackgroundResource(R.drawable.round_orage400)
        buttonSubmit?.setTextAppearance(
            requireActivity(),
            R.style.RoundedCornerButtonOrange400
        )
        buttonSubmit?.text = requireActivity().resources.getString(R.string.update)
        buttonSubmit?.setOnClickListener {
            if(zoomScheduledViewModel.validateField(textStartDate!!,
                "Start Date field cannot be empty",requireActivity(),constraintLayoutContent!!)
                && zoomScheduledViewModel.validateField(textStartTime!!,
                    "Time field cannot be empty",requireActivity(),constraintLayoutContent!!)
                && zoomScheduledViewModel.validateField(editTextMeetingName!!,
                    "Meeting Name field cannot be empty",requireActivity(),constraintLayoutContent!!)
                && zoomScheduledViewModel.validateField(editTextMeetingId!!,
                    "Meeting Id field cannot be empty",requireActivity(),constraintLayoutContent!!)
                && zoomScheduledViewModel.validateField(editTextMeetingPassword!!,
                    "Meeting Password field cannot be empty",requireActivity(),constraintLayoutContent!!)
                && zoomScheduledViewModel.validateField(editTextMeetingLink!!,
                    "Meeting Link field cannot be empty",requireActivity(),constraintLayoutContent!!)){
                        //Map <String, String> postParam = new HashMap <String, String>();
                //        postParam.put("ACCADEMIC_ID",s_aid);
                //        postParam.put("CLASS_ID",scid);
                //        postParam.put("MEETING_TITLE",input_meeting_name.getText().toString());
                //        postParam.put("MEETING_ID",input);
                //        postParam.put("MEETING_PASSWORD",input_meeting_password.getText().toString());
                //        postParam.put("MEETING_LINK",input_meeting_link.getText().toString());
                //        // parseDateToddMMyyyyCal1(join_date.getText().toString());
                //        postParam.put("MEETING_STATR_DATE",parseDateToddMMyyyyCal1(join_date.getText().toString()));
                //        postParam.put("CREATED_BY",Global.Admin_id);
                //        postParam.put("MEETING_STATUS",type_str);
                    //   postParam.put("Z_MEETING_ID",Z_MEETING_ID);

                val url = "OnlineVideo/ZoomUpdate"
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
                    jsonObject.put("CLASS_ID", cLASSID)
                    jsonObject.put("MEETING_TITLE", editTextMeetingName?.text.toString())

                    jsonObject.put("MEETING_ID", editTextMeetingId?.text.toString())
                    jsonObject.put("MEETING_PASSWORD", editTextMeetingPassword?.text.toString())

                    jsonObject.put("MEETING_LINK", editTextMeetingLink?.text.toString())
                    jsonObject.put("MEETING_STATR_DATE", "${Utils.parseDateToMMMDDYYYY(textStartDate?.text.toString())} ${textStartTime?.text.toString()}")
                    jsonObject.put("CREATED_BY", adminId)
                    jsonObject.put("MEETING_STATUS", typeStr)
                    jsonObject.put("Z_MEETING_ID", zoomList.zMEETINGID)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.i(TAG,"jsonObject $jsonObject")
                val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                zoomScheduledViewModel.getCommonPostFun(url,accademicRe)
                    .observe(requireActivity(), Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    val response = resource.data?.body()!!
                                    progressStop()
                                    when {
                                        Utils.resultFun(response) == "1" -> {
                                            zoomScheduledListener.onCreateClick("Meeting Scheduled Successfully")
                                            cancelFrg()
                                        }
                                        Utils.resultFun(response) == "0" -> {
                                            Utils.getSnackBar4K(requireActivity(), "Live Class Scheduling Failed", constraintLayoutContent!!)
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
        zoomScheduledViewModel.getYearClassExam(adminId, schoolId )
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            var aCCADEMICID = 0
                            var cLASSID = 0
                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size){""}
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                                if (zoomList.aCCADEMICID == getYears[i].aCCADEMICID) {
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

                            getClass = response.classList as ArrayList<GetYearClassExamModel.Class>
                            var classX = Array(getClass.size){""}
                            for (i in getClass.indices) {
                                classX[i] = getClass[i].cLASSNAME
                                if (zoomList.cLASSID == getClass[i].cLASSID) {
                                    cLASSID = i
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
                            spinnerClass?.setSelection(cLASSID)

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