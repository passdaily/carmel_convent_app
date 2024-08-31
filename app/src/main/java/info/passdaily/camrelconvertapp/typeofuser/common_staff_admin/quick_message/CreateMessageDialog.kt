package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_message

import android.content.Context
import android.os.Bundle
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
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreateEnquiryBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
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

@Suppress("DEPRECATION")
class CreateMessageDialog : DialogFragment {

    lateinit var messageClickListener: MessageClickListener

    companion object {
        var TAG = "CreateNotificationDialog"
    }

    private var _binding: DialogCreateEnquiryBinding? = null
    private val binding get() = _binding!!

    private lateinit var quickMessageViewModel: QuickMessageViewModel

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0

    var toolbar : Toolbar? = null
    var constraintLeave : ConstraintLayout? = null

    var editTextTitle : TextInputEditText? =null
    var editTextDesc : TextInputEditText? =null

    var buttonSubmit : AppCompatButton? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleWhite)
    }

    constructor(messageClickListener: MessageClickListener) {
        this.messageClickListener = messageClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        quickMessageViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[QuickMessageViewModel::class.java]

        _binding = DialogCreateEnquiryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_black)
        toolbar?.title = "Create Message"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.black))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }
        editTextTitle  = binding.editTextTitle
        editTextDesc  = binding.editTextDesc
        constraintLeave = binding.constraintLeave

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.text = requireActivity().resources.getString(R.string.create_message)
        buttonSubmit?.setOnClickListener {
            if(quickMessageViewModel.validateField(editTextTitle!!,"Title field cannot be empty",requireActivity(),constraintLeave!!) &&
                quickMessageViewModel.validateField(editTextDesc!!,"Description field cannot be empty",requireActivity(),constraintLeave!!)){
          // postParam.put("MESSAGE_TITLE",title);
                //        postParam.put("MESSAGE_CONTENT", Text_Msg);
                //        postParam.put("MESSAGE_CREATED_BY",  Global.Admin_id);
                //        postParam.put("MESSAGE_STATUS", "1");
                var url = "Message/MessageCreate"

                val jsonObject = JSONObject()
                try {
                    jsonObject.put("MESSAGE_TITLE", editTextTitle?.text.toString())
                    jsonObject.put("MESSAGE_CONTENT", editTextDesc?.text.toString())
                    jsonObject.put("MESSAGE_CREATED_BY", adminId)
                    jsonObject.put("MESSAGE_STATUS", "1")

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                quickMessageViewModel.getCommonPostFun(url,accademicRe)
                    .observe(requireActivity(), Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    val response = resource.data?.body()!!
                                    progressStop()
                                    when {
                                        Utils.resultFun(response) == "SUCCESS" -> {
                                            messageClickListener.onCreateClick("Message created successfully")
                                            cancelFrg()
                                        }
                                        Utils.resultFun(response) == "EXIST" -> {
                                            Utils.getSnackBar4K(requireActivity(), "Message Already Exist", constraintLeave!!)
                                        }//ERROR
                                        Utils.resultFun(response) == "ERROR" -> {
                                            Utils.getSnackBar4K(requireActivity(), "Message Creation Failed", constraintLeave!!)
                                        }
                                    }
                                }
                                Status.ERROR -> {
                                    progressStop()
                                    Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLeave!!)
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
        val constraintLeave = binding.constraintLeave
        constraintLeave.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLeave.windowToken, 0)
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
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }


}