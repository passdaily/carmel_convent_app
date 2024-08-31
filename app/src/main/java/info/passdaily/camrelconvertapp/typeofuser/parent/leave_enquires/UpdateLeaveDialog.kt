package info.passdaily.camrelconvertapp.typeofuser.parent.leave_enquires

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
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreateLeaveBinding
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Suppress("DEPRECATION")
class UpdateLeaveDialog : DialogFragment {

    lateinit var leaveClickListener: LeaveClickListener

    companion object {
        var TAG = "UpdateLeaveDialog"
    }

    private var _binding: DialogCreateLeaveBinding? = null
    private val binding get() = _binding!!

    private lateinit var leaveEnquiryViewModel: LeaveEnquiryViewModel

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var fromStr = ""
    var toStr = ""

    var fromDateCompare = ""
    var toDateCompare = ""

    var toolbar : Toolbar? = null

    var leaveFromDate : TextInputEditText? =null
    var leaveEndDate : TextInputEditText? =null
    var editTextTitle : TextInputEditText? =null
    var editTextDesc : TextInputEditText? =null

    var buttonSubmit : AppCompatButton? =null

    var constraintLeave : ConstraintLayout? = null

    var fromDate = ""
    var toDate = ""
    var title = ""
    var description = ""
    var leaveId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    constructor(
        leaveClickListener: LeaveClickListener,
        fromDate: String,
        toDate: String,
        title: String,
        description: String,
        leaveId : Int
    ) {
        this.leaveClickListener = leaveClickListener
        this.fromDate = fromDate
        this.toDate = toDate
        this.title = title
        this.description = description
        this.leaveId = leaveId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        leaveEnquiryViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[LeaveEnquiryViewModel::class.java]

        _binding = DialogCreateLeaveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getProductById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID
        STUDENT_ROLL_NO = student.STUDENT_ROLL_NO

        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_white)
        toolbar?.title = "Update Leave"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.white))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }
        editTextTitle  = binding.editTextTitle
        editTextDesc  = binding.editTextDesc
        leaveFromDate = binding.leaveFromDate
        leaveEndDate = binding.leaveEndDate

        editTextTitle?.setText(title)
        editTextDesc?.setText(description)
        val dateFrom: Array<String> = fromDate.split("T").toTypedArray()
        val fromItems = dateFrom[0].split("-").toTypedArray()
        leaveFromDate?.setText(Utils.dateformat(dateFrom[0]))

        val dateTo: Array<String> = toDate.split("T").toTypedArray()
        val toItems = dateTo[0].split("-").toTypedArray()
        leaveEndDate?.setText(Utils.dateformat(dateTo[0]))

    // same var  =  coming value
        fromStr = dateFrom[0] ///
        toStr = dateTo[0] ///


        leaveFromDate?.inputType = InputType.TYPE_NULL
        leaveFromDate?.keyListener = null
        leaveEndDate?.inputType = InputType.TYPE_NULL
        leaveEndDate?.keyListener = null

        constraintLeave  = binding.constraintLeave

        //        et_leave_fromdate.requestFocus();
        leaveFromDate?.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(leaveFromDate?.windowToken, 0)

            //Calendar mcurrentDate1= Calendar.getInstance();
            //Calendar mcurrentDate1= Calendar.getInstance();

            val mYear = fromItems[0].toInt()
            val mMonth = (fromItems[1].toInt()-1)
            val mDay = fromItems[2].toInt()
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    fromStr = "$year-$s-$dayOfMonth"
                    if(toStr.isNotEmpty()) {
                        if (Utils.checkDatesAfter(fromStr, toStr)) {
                            leaveFromDate?.setText(Utils.dateformat(fromStr))
                        }else{
                            Toast.makeText(activity, "Give Valid Date", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        leaveFromDate?.setText(Utils.dateformat(fromStr))
                    }


                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("From Date")
            mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }


        leaveEndDate?.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(leaveEndDate?.windowToken, 0)

            val mYear = toItems[0].toInt()
            val mMonth = (toItems[1].toInt()-1)
            val mDay = toItems[2].toInt()
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    toStr = "$year-$s-$dayOfMonth"
                    if(fromStr.isNotEmpty()) {
                        if (Utils.checkDatesAfter(fromStr, toStr)) {
                            leaveEndDate?.setText(Utils.dateformat(toStr))
                        }else{
                            Toast.makeText(activity, "Give Valid Date", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(activity, "Give Start Date", Toast.LENGTH_SHORT).show()
                    }
                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("To Date")
            mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);


//            val buttonbackground: Button = mDatePicker3.getButton(DialogInterface.BUTTON_NEGATIVE)
//            buttonbackground.setTextColor(Color.BLACK)
//
//            val buttonbackground1: Button = mDatePicker3.getButton(DialogInterface.BUTTON_POSITIVE)
//            buttonbackground1.setTextColor(requireActivity().resources.getColor(R.color.green_400))
        }

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.setOnClickListener {
            if(leaveEnquiryViewModel.validateField(editTextTitle!!,"Title field cannot be empty",requireActivity(),constraintLeave!!) &&
                leaveEnquiryViewModel.validateField(editTextDesc!!,"Description field cannot be empty",requireActivity(),constraintLeave!!) &&
                leaveEnquiryViewModel.validateField(leaveFromDate!!,"Give Start Date",requireActivity(),constraintLeave!!) &&
                leaveEnquiryViewModel.validateField(leaveEndDate!!,"Give End Date",requireActivity(),constraintLeave!!)){



//                String update_api=Global.url+"LeaveUpdate/LeaveRequestUpdate";
//
//                Map<String, String> postParam = new HashMap<String, String>();
//                postParam.put("ACCADEMIC_ID", leavelist.get(position).get("ACCADEMIC_ID"));
//                postParam.put("CLASS_ID", leavelist.get(position).get("CLASS_ID"));
//                postParam.put("STUDENT_ID", leavelist.get(position).get("STUDENT_ID"));
//                postParam.put("STUDENT_ROLL_NUMBER", leavelist.get(position).get("STUDENT_ROLL_NUMBER"));
//                postParam.put("LEAVE_SUBJECT", subject);
//                postParam.put("LEAVE_DESCRIPTION", description);
//                postParam.put("LEAVE_FROM_DATE",fromdate);
//                postParam.put("LEAVE_TO_DATE",todate);
//                postParam.put("LEAVE_ID", leavelist.get(position).get("LEAVE_ID"));
                val url = "LeaveUpdate/LeaveRequestUpdate"

                val jsonObject = JSONObject()
                try {
                    jsonObject.put("ACCADEMIC_ID", ACADEMICID)
                    jsonObject.put("CLASS_ID", CLASSID)
                    jsonObject.put("STUDENT_ID", STUDENTID)
                    jsonObject.put("STUDENT_ROLL_NUMBER", STUDENT_ROLL_NO)
                    jsonObject.put("LEAVE_SUBJECT", editTextTitle?.text.toString())
                    jsonObject.put("LEAVE_DESCRIPTION", editTextDesc?.text.toString())
                    jsonObject.put("LEAVE_FROM_DATE", Utils.datedifference(leaveFromDate?.text.toString()))
                    jsonObject.put("LEAVE_TO_DATE", Utils.datedifference(leaveEndDate?.text.toString()))
                    jsonObject.put("LEAVE_ID", leaveId)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                leaveEnquiryViewModel.getCommonPostFun(url,accademicRe)
                    .observe(requireActivity(), Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    cancelFrg()
                                    leaveClickListener.onCreateClick("Leave Updated")
                                }
                                Status.ERROR -> {
                                    Toast.makeText(
                                        activity,
                                        "Please try again after sometime",
                                        Toast.LENGTH_SHORT
                                    ).show()
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

    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }


    }



}