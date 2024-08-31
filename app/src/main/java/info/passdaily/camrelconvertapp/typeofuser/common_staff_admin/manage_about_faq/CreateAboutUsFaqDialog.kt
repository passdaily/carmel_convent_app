package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_about_faq

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
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreateEnquiryBinding
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class CreateAboutUsFaqDialog : DialogFragment {

    lateinit var aboutUsTabListener: AboutUsTabListener

    companion object {
        var TAG = "CreateAboutUsFaqDialog"
    }
    var type = 0
    var titleStr = ""

    private var _binding: DialogCreateEnquiryBinding? = null
    private val binding get() = _binding!!


    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    var toolbar : Toolbar? = null
    var constraintLeave : ConstraintLayout? = null

    var editTextTitle : TextInputEditText? =null
    var editTextDesc : TextInputEditText? =null

    var buttonSubmit : AppCompatButton? =null

    var successMessage = ""
    var failedMessage = ""
    var existingMessage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleWhite)
    }

    constructor(aboutUsTabListener: AboutUsTabListener,
                type : Int){
        this.aboutUsTabListener = aboutUsTabListener
        this.type = type }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        _binding = DialogCreateEnquiryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (type == 1){
            titleStr = "Add About Us"
            successMessage = "About Us Added Successfully"
            failedMessage = "About Us Adding Failed"
            existingMessage = "About Us Details Already Exist"
        }else if (type == 2){
            titleStr = "Add FAQ"
            successMessage = "FAQ Added Successfully"
            failedMessage = "FAQ Adding Failed"
            existingMessage = "FAQ Details Already Exist"
        }

        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_black)
        toolbar?.title = titleStr
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.black))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }
        editTextTitle  = binding.editTextTitle
        editTextDesc  = binding.editTextDesc
        constraintLeave = binding.constraintLeave

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.text = requireActivity().resources.getString(R.string.create)
        buttonSubmit?.setOnClickListener {
            if(editTextTitle?.text.toString().isNotEmpty() &&
                editTextDesc?.text.toString().isNotEmpty()){
                val url = "AboutFaq/AddAboutFaq"
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("ABT_FAQ_TITLE", editTextTitle?.text.toString())
                    jsonObject.put("ABT_FAQ_DESCRIPTION", editTextDesc?.text.toString())
                    jsonObject.put("ABT_FAQ_CREATEDBY", adminId)
                    jsonObject.put("ABT_FAQ_TYPE", type.toString())
                    jsonObject.put("SCHOOL_ID", schoolId)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.i(BottomSheetUpdate.TAG,"jsonObject $jsonObject")

                val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                aboutUsTabListener.onUpdateClick(url,submitItems,
                    successMessage,failedMessage,existingMessage)
                cancelFrg()
            }else{
                aboutUsTabListener.onShowMessage("Don't leave fields empty")
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



}