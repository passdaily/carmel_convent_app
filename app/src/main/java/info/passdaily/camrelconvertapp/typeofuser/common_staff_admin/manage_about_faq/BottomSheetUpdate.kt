package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_about_faq

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetAboutusFaqBinding
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateAlbumBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class BottomSheetUpdate : BottomSheetDialogFragment {


    var type = 0
    private var _binding: BottomSheetAboutusFaqBinding? = null
    private val binding get() = _binding!!
    lateinit var aboutUsTabListener: AboutUsTabListener


    var editTextTitle : TextInputEditText? =null
    var editDescription : TextInputEditText? =null

    lateinit var aboutus: AboutusFaqListModel.Aboutus

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    var successMessage = ""
    var failedMessage = ""
    var existingMessage = ""

    constructor()

    constructor(aboutUsTabListener: AboutUsTabListener, aboutus: AboutusFaqListModel.Aboutus,
                type : Int){
        this.aboutUsTabListener = aboutUsTabListener
        this.aboutus = aboutus
        this.type = type

//        this.gMEMBERID = gMEMBERID
//        this.gROUPNAME = gROUPNAME
//        this.gMEMBERNUMBER = gMEMBERNUMBER
//        this.aCCADEMICID = aCCADEMICID
//        this.gROUPID = gROUPID
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

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        _binding = BottomSheetAboutusFaqBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        editTextTitle  = binding.editTextTitle
        editDescription  = binding.editTextDesc

        editTextTitle?.setText(aboutus.aBTFAQTITLE)
        editDescription?.setText(aboutus.aBTFAQDESCRIPTION)

        if (type == 1){
            binding.textViewTitle.text = "Update About Us"
            successMessage = "About Us Updated Successfully"
            failedMessage = "About Us Updation Failed"
            existingMessage = "About Us Details Already Exist"
        }else if (type == 2){
            binding.textViewTitle.text = "Update FAQ"
            successMessage = "FAQ Updated Successfully"
            failedMessage = "FAQ Updation Failed"
            existingMessage = "FAQ Details Already Exist"
        }



        binding.buttonSubmit.setOnClickListener {
            if(editTextTitle?.text.toString().isNotEmpty() &&
                editDescription?.text.toString().isNotEmpty()){
                val url = "AboutFaq/UpdateAboutFaq"
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("ABT_FAQ_TITLE", editTextTitle?.text.toString())
                        jsonObject.put("ABT_FAQ_DESCRIPTION", editDescription?.text.toString())
                        jsonObject.put("ABT_FAQ_CREATEDBY", adminId)
                        jsonObject.put("ABT_FAQ_TYPE", type.toString())
                        jsonObject.put("ABT_FAQ_ID", aboutus.aBTFAQID)
                        jsonObject.put("SCHOOL_ID", schoolId)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i(TAG,"jsonObject $jsonObject")

                    val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    aboutUsTabListener.onUpdateClick(url,submitItems,
                        successMessage,failedMessage,existingMessage)
            }else{
                aboutUsTabListener.onShowMessage("Don't leave fields empty")
            }
        }

        binding.textDeleteIcon.setOnClickListener {
            aboutUsTabListener.onDeleteItem(aboutus.aBTFAQID)
        }

    }



    companion object {
        var TAG = "BottomSheetFragment"
    }
}