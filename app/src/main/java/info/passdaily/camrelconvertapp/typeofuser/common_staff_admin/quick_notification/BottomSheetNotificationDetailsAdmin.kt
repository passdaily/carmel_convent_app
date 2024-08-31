package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetNotificationDetailsAdminBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.ShowMoreTextView
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification.send_to_notification.SendNotificationToClassDivisionDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification.send_to_notification.SendNotificationToClassWiseDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification.send_to_notification.SendNotificationToParentDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification.send_to_notification.SendNotificationToStaffDialog


@Suppress("DEPRECATION")
class BottomSheetNotificationDetailsAdmin : BottomSheetDialogFragment {

    private var _binding: BottomSheetNotificationDetailsAdminBinding? = null
    private val binding get() = _binding!!

    var aCCADEMICID = 0

    lateinit var notificationTabClicker : NotificationTabClicker



    lateinit var notificationList: NotificationStaffModel.Inbox

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    var textViewTitle : TextView? = null
    var textViewDesc : ShowMoreTextView? = null

    var buttonParent : CardView? = null
    var buttonSendToStaff : CardView? = null
    var buttonClassDivision  : CardView? = null

    var buttonClassWise  : CardView? = null
    var buttonAllParents  : CardView? = null
    var buttonAllStaff : CardView? = null

    var textViewDate : TextView? = null


    constructor()

    constructor(notificationTabClicker: NotificationTabClicker,notificationList: NotificationStaffModel.Inbox){
        this.notificationTabClicker = notificationTabClicker
        this.notificationList = notificationList
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
        adminId = user[0].ADMIN_ROLE
        schoolId = user[0].SCHOOL_ID
        _binding = BottomSheetNotificationDetailsAdminBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        textViewTitle  = binding.textViewTitle
        textViewDesc  = binding.textViewDesc
        textViewDate = binding.textViewDate

        buttonParent = binding.sentToParentButton
        buttonSendToStaff = binding.buttonSendToStaff
        buttonClassDivision = binding.buttonClassDivision
        buttonClassWise = binding.buttonClassWise
        buttonAllParents = binding.buttonAllParents
        buttonAllStaff  = binding.buttonAllStaff

        textViewTitle?.text = notificationList.vIRTUALMAILTITLE
        textViewDesc?.text = notificationList.vIRTUALMAILCONTENT
        textViewDesc?.apply {
            setShowingLine(8)
            setShowMoreTextColor(resources.getColor(R.color.green_400))
            setShowLessTextColor(resources.getColor(R.color.gray_400))
        }


        buttonParent?.setOnClickListener {
            val dialog1 = SendNotificationToParentDialog(notificationList,notificationTabClicker)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendNotificationToParentDialog.TAG)
        }

        buttonSendToStaff?.setOnClickListener {
            val dialog1 = SendNotificationToStaffDialog(notificationList,notificationTabClicker)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendNotificationToStaffDialog.TAG)
        }

        buttonClassWise?.setOnClickListener {
            val dialog1 = SendNotificationToClassWiseDialog(notificationList,notificationTabClicker)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendNotificationToClassWiseDialog.TAG)
        }

        buttonClassDivision?.setOnClickListener {
            val dialog1 = SendNotificationToClassDivisionDialog(notificationList,notificationTabClicker)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendNotificationToClassDivisionDialog.TAG)
        }

        buttonAllParents?.setOnClickListener {
            //Virtual/VirtualToAllParents?MailId
            val paramsMap: HashMap<String?, Int> = LinkedHashMap()
            paramsMap["MailId"] = notificationList.vIRTUALMAILID
            paramsMap["AdminId"] = adminId
            paramsMap["SchoolId"] = schoolId
            sendFunction("Are you sure want to send All Parents?","Virtual/VirtualToAllParents",paramsMap)
        }

        // http://meridianstaff.passdaily.in/ElixirApi/Virtual/VirtualToAllStaff?AdminId=2&MailId=7&SchoolId=1
        buttonAllStaff?.setOnClickListener {
            //Virtual/VirtualToAllParents?MailId
            val paramsMap: HashMap<String?, Int> = LinkedHashMap()
            paramsMap["AdminId"] = adminId
            paramsMap["MailId"] = notificationList.vIRTUALMAILID
            paramsMap["SchoolId"] = schoolId
            sendFunction("Are you sure want to send All Staff?","Virtual/VirtualToAllStaff",paramsMap)
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


    fun sendFunction(message : String,url : String,paramsMap : HashMap<String?, Int>){
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                //AllStaffs/MessageToAllStaffs?MessageId=
                notificationTabClicker.sendToAllParentStaffPTAConveyor(url,paramsMap)
            }
            .setNegativeButton(
                "No"
            ) { dialog, _ -> //  Action for 'NO' Button
                dialog.cancel()
            }
        //Creating dialog box
        val alert = builder.create()
        //Setting the title manually
        alert.setTitle("Send Message")
        alert.show()
        val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonbackground.setTextColor(Color.BLACK)
        val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonbackground1.setTextColor(Color.BLACK)
    }


    companion object {
        var TAG = "BottomSheetFragment"
    }
}