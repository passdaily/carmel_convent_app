package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetNotificationDetailsBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.ShowMoreTextView
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification.send_to_notification.SendNotificationToClassByTeacherDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification.send_to_notification.SendNotificationToParentDialog

@Suppress("DEPRECATION")
class BottomSheetNotificationDetails : BottomSheetDialogFragment {

    private var _binding: BottomSheetNotificationDetailsBinding? = null
    private val binding get() = _binding!!

    var aCCADEMICID = 0

    lateinit var notificationTabClicker : NotificationTabClicker



    lateinit var notificationList: NotificationStaffModel.Inbox

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0

    var textViewTitle : TextView? = null
    var textViewDesc : ShowMoreTextView? = null

    var sentToParentButton : CardView? = null
    var sendToClassButton  : CardView? = null

    var textViewDate : TextView? = null

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

        _binding = BottomSheetNotificationDetailsBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        textViewTitle  = binding.textViewTitle
        textViewDesc  = binding.textViewDesc
        textViewDate = binding.textViewDate

        sentToParentButton = binding.sentToParentButton
        sendToClassButton = binding.sendToClassButton

        textViewTitle?.text = notificationList.vIRTUALMAILTITLE
        textViewDesc?.text = notificationList.vIRTUALMAILCONTENT
        textViewDesc?.apply {
            setShowingLine(8)
            setShowMoreTextColor(resources.getColor(R.color.green_300))
            setShowLessTextColor(resources.getColor(R.color.green_300))
        }


        sentToParentButton?.setOnClickListener {
            val dialog1 = SendNotificationToParentDialog(notificationList,notificationTabClicker)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendNotificationToParentDialog.TAG)
        }
        sendToClassButton?.setOnClickListener {
            val dialog1 = SendNotificationToClassByTeacherDialog(notificationList,notificationTabClicker)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendNotificationToClassByTeacherDialog.TAG)
        }

        if(!notificationList.vIRTUALMAILDATE.isNullOrBlank()) {
            val date1: Array<String> = notificationList.vIRTUALMAILDATE.split("T".toRegex()).toTypedArray()
            val sendStr = Utils.longconversion(date1[0] + " " + date1[1])
            textViewDate?.text = Utils.formattedDateTime(sendStr)
        }

        binding.imageViewClose.setOnClickListener {
            dialog?.dismiss()
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