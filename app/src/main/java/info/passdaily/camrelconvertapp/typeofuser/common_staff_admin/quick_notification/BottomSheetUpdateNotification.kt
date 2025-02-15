package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetNotificationDetailsBinding
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateAlbumBinding
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateNotificationBinding
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdatePublicMemberBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.ShowMoreTextView
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album.BottomSheetUpdateAlbum
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.GroupViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.public_member.BottomSheetUpdatePublicMember
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class BottomSheetUpdateNotification : BottomSheetDialogFragment {

    private var _binding: BottomSheetUpdateNotificationBinding? = null
    private val binding get() = _binding!!

    var aCCADEMICID = 0

    lateinit var notificationTabClicker : NotificationTabClicker

    lateinit var notificationList: NotificationStaffModel.Inbox

    private lateinit var quickNotificationViewModel: QuickNotificationViewModel

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0

    var textViewTitle : TextView? = null
    var textViewDesc : ShowMoreTextView? = null

    var textViewDate : TextView? = null

    var editTextTitle : TextInputEditText? =null
    var editDescription : TextInputEditText? =null

    constructor()

    constructor(notificationTabClicker: NotificationTabClicker,notificationList: NotificationStaffModel.Inbox){
        this.notificationTabClicker = notificationTabClicker
        this.notificationList = notificationList

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

        quickNotificationViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[QuickNotificationViewModel::class.java]

        _binding = BottomSheetUpdateNotificationBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        editTextTitle  = binding.editTextTitle
        editDescription  = binding.editDescription

        editTextTitle?.setText(notificationList.vIRTUALMAILTITLE)
        editDescription?.setText(notificationList.vIRTUALMAILCONTENT)


        binding.buttonSubmit.setOnClickListener {
//            if(quickNotificationViewModel.validateField(editTextTitle!!,"Title field cannot be empty",requireActivity(),constraintLeave!!) &&
//                quickNotificationViewModel.validateField(editDescription!!,"Description field cannot be empty",requireActivity(),constraintLeave!!)){
//                //     String reply_url=Global.url+"InboxEdit/InboxSetById";
//                //
//                //        Map <String, String> postParam = new HashMap <String, String>();
//                //        postParam.put("VIRTUAL_MAIL_ID", messageid);
//                var url = "InboxEdit/InboxSetById"
//
//                val jsonObject = JSONObject()
//                try {
//                    jsonObject.put("VIRTUAL_MAIL_ID", notificationList.vIRTUALMAILID)
//                    jsonObject.put("VIRTUAL_MAIL_TITLE", editTextTitle?.text.toString())
//                    jsonObject.put("VIRTUAL_MAIL_CONTENT", editDescription?.text.toString())
//                    jsonObject.put("VIRTUAL_MAIL_STATUS", "1")
//                    jsonObject.put("VIRTUAL_MAIL_CREATED_BY", adminId)
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            }

            if(binding.editTextTitle.text.toString().isNotEmpty() &&
                binding.editDescription.text.toString().isNotEmpty()){
                val url = "InboxEdit/InboxSetById"
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("VIRTUAL_MAIL_ID", notificationList.vIRTUALMAILID)
                    jsonObject.put("VIRTUAL_MAIL_TITLE", editTextTitle?.text.toString())
                    jsonObject.put("VIRTUAL_MAIL_CONTENT", editDescription?.text.toString())
                    jsonObject.put("VIRTUAL_MAIL_STATUS", "1")
                    jsonObject.put("VIRTUAL_MAIL_CREATED_BY", adminId)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.i(TAG,"jsonObject $jsonObject")

                val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                notificationTabClicker.onSubmitUpdateClick(url,submitItems,
                    "Notification Updated Successfully","Notification Updation Failed",
                    "Notification Already existing")
            }else{
                notificationTabClicker.onFailedMessage("Don't leave fields empty")
            }
        }


//
//        binding.buttonSubmit.setOnClickListener {
//            if(binding.editTextTitle.text.toString().isNotEmpty() &&
//                binding.editDescription.text.toString().isNotEmpty()){
//                val url = "Teacher/AlbumCategoryEdit"
//                    val jsonObject = JSONObject()
//                    try {
//                        jsonObject.put("ALBUM_CATEGORY_NAME",binding.editTextTitle.text.toString())
//                        jsonObject.put("ALBUM_CATEGORY_DISCRIPTION", binding.editDescription.text.toString())
//                        jsonObject.put("ALBUM_CATEGORY_TYPE", albumCategory.aLBUMCATEGORYTYPE)
//                        jsonObject.put("ACCADEMIC_ID", albumCategory.aCCADEMICID)
//                        jsonObject.put("ALBUM_CATEGORY_CREATED", albumCategory.aLBUMCATEGORYCREATED)
//                        jsonObject.put("ALBUM_CATEGORY_ID", albumCategory.aLBUMCATEGORYID)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                    Log.i(TAG,"jsonObject $jsonObject")
//
//                    val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
//                    albumListener.onUpdateClick(url,submitItems,
//                        "Album Updated Successfully","Album Updation Failed")
//            }else{
//                albumListener.onShowMessage("Don't leave fields empty")
//            }
//        }
//

    }



    companion object {
        var TAG = "BottomSheetFragment"
    }
}