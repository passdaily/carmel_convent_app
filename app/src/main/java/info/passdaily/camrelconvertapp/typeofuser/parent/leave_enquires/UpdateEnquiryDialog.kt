package info.passdaily.camrelconvertapp.typeofuser.parent.leave_enquires

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreateEnquiryBinding
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class UpdateEnquiryDialog : DialogFragment {

    lateinit var leaveClickListener: LeaveClickListener

    companion object {
        var TAG = "UpdateEnquiryDialog"
    }

    private var _binding: DialogCreateEnquiryBinding? = null
    private val binding get() = _binding!!

    private lateinit var leaveEnquiryViewModel: LeaveEnquiryViewModel

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var toolbar : Toolbar? = null

    var fromDate = ""
    var toDate = ""
    var title = ""
    var description = ""
    var enquiryId = 0

    var editTextTitle : TextInputEditText? =null
    var editTextDesc : TextInputEditText? =null

    var constraintLeave : ConstraintLayout? = null

    var buttonSubmit : AppCompatButton? =null

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
        enquiryId : Int
    ) {
        this.leaveClickListener = leaveClickListener
        this.fromDate = fromDate
        this.toDate = toDate
        this.title = title
        this.description = description
        this.enquiryId = enquiryId
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

        _binding = DialogCreateEnquiryBinding.inflate(inflater, container, false)
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

        constraintLeave =  binding.constraintLeave

        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_white)
        toolbar?.title = "Update Enquiry"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.white))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }
        editTextTitle  = binding.editTextTitle
        editTextDesc  = binding.editTextDesc
        editTextTitle?.setText(title)
        editTextDesc?.setText(description)

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.setOnClickListener {
            if(leaveEnquiryViewModel.validateField(editTextTitle!!,"Title field cannot be empty",requireActivity(),constraintLeave!!) &&
                leaveEnquiryViewModel.validateField(editTextTitle!!,"Description field cannot be empty",requireActivity(),constraintLeave!!)){

                var url = "EnquiryUpdate/EnquiryRequestUpdate"

                val jsonObject = JSONObject()
                try {
                    jsonObject.put("ACCADEMIC_ID", ACADEMICID)
                    jsonObject.put("CLASS_ID", CLASSID)
                    jsonObject.put("STUDENT_ID", STUDENTID)
                    jsonObject.put("STUDENT_ROLL_NUMBER", STUDENT_ROLL_NO)
                    jsonObject.put("QUERY_SUBJECT", editTextTitle?.text.toString())
                    jsonObject.put("QUERY_DESCRIPTION", editTextDesc?.text.toString())
                    jsonObject.put("QUERY_ID", enquiryId)
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
                                    leaveClickListener.onCreateClick("Enquiry Updated")
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