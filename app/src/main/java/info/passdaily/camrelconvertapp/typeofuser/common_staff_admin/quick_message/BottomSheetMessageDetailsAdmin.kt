package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_message

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
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetMessageDetailsAdminBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.ShowMoreTextView
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_message.send_to.*

@Suppress("DEPRECATION")
class BottomSheetMessageDetailsAdmin : BottomSheetDialogFragment {

    private var _binding: BottomSheetMessageDetailsAdminBinding? = null
    private val binding get() = _binding!!

    var aCCADEMICID = 0

    lateinit var messageTabClicker : MessageTabClicker

    private lateinit var quickMessageViewModel: QuickMessageViewModel

    lateinit var messageList: MessageListModel.Message

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0

    var textViewTitle : TextView? = null
    var textViewDesc : ShowMoreTextView? = null

    var buttonParent : CardView? = null
    var buttonStaff  : CardView? = null
    var buttonGroup   : CardView? = null
    var buttonPublicGroup   : CardView? = null
    var buttonPTA   : CardView? = null
    var buttonConveyor  : CardView? = null
    var buttonClassDivision  : CardView? = null
    var buttonPublicGroupWise : CardView? = null
    var buttonGroupWise  : CardView? = null
    var buttonClassWise  : CardView? = null
    var buttonAllParents  : CardView? = null
    var buttonAllStaff  : CardView? = null
    var buttonAllConveyor : CardView? = null
    var buttonAllPta  : CardView? = null

    var textViewDate : TextView? = null

    constructor()

    constructor(messageTabClicker: MessageTabClicker,messageList: MessageListModel.Message){
        this.messageTabClicker = messageTabClicker
        this.messageList = messageList

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

        quickMessageViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[QuickMessageViewModel::class.java]

        _binding = BottomSheetMessageDetailsAdminBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        textViewTitle  = binding.textViewTitle
        textViewDesc  = binding.textViewDesc
        textViewDate = binding.textViewDate

        buttonParent = binding.buttonParent
        buttonStaff = binding.buttonStaff
        buttonGroup  = binding.buttonGroup
        buttonPublicGroup = binding.buttonPublicGroup
        buttonPTA = binding.buttonPTA
        buttonConveyor = binding.buttonConveyor
        buttonClassDivision = binding.buttonClassDivision
        buttonPublicGroupWise = binding.buttonPublicGroupWise
        buttonGroupWise = binding.buttonGroupWise
        buttonClassWise = binding.buttonClassWise
        buttonAllParents = binding.buttonAllParents
        buttonAllStaff = binding.buttonAllStaff
        buttonAllConveyor = binding.buttonAllConveyor
        buttonAllPta = binding.buttonAllPta

        textViewTitle?.text = messageList.mESSAGETITLE
        textViewDesc?.text = messageList.mESSAGECONTENT
        textViewDesc?.apply {
            setShowingLine(8)
            setShowMoreTextColor(resources.getColor(R.color.green_300))
            setShowLessTextColor(resources.getColor(R.color.green_300))
        }


        buttonParent?.setOnClickListener {
            val dialog1 = SendToParentDialog(messageList,messageTabClicker,"Send/SendMessageToParents")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToParentDialog.TAG)
        }

        buttonStaff?.setOnClickListener {
            val dialog1 = SendToStaffDialog(messageList,messageTabClicker,"Staff/SendMessageToStaffs")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToStaffDialog.TAG)
        }
        buttonGroup?.setOnClickListener {
            val dialog1 = SendToGroupDialog(messageList,messageTabClicker,"Group/SendMessageToGroup")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToGroupDialog.TAG)
        }

        buttonPublicGroup?.setOnClickListener {
            val dialog1 = SendToPublicGroupDialog(messageList,messageTabClicker,"PublicGroup/SendMessageToPublicGroup")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToPublicGroupDialog.TAG)
        }

        buttonPTA?.setOnClickListener {
            val dialog1 = SendToPTADialog(messageList,messageTabClicker,"Pta/SendMessageToPta")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToPTADialog.TAG)
        }

        buttonConveyor?.setOnClickListener {
            val dialog1 = SendToConveyorDialog(messageList,messageTabClicker,"Conveyor/SendMessageToConveyor")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToConveyorDialog.TAG)
        }


        buttonClassDivision?.setOnClickListener {
            val dialog1 = SendToClassDivisionDialog(messageList,messageTabClicker,"BulkMessage/SendMessageClassWise")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToClassDivisionDialog.TAG)
        }


        buttonClassWise?.setOnClickListener {
            val dialog1 = SendToClassWiseDialog(messageList,messageTabClicker,"BulkMessage/SendClassSectionWise")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToClassWiseDialog.TAG)
        }

        buttonGroupWise?.setOnClickListener {
            val dialog1 = SendToGroupWiseDialog(messageList,messageTabClicker,"GroupWise/MessageToGroupWise")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToGroupWiseDialog.TAG)
        }

        buttonPublicGroupWise?.setOnClickListener {
            val dialog1 = SendToPublicGroupWiseDialog(messageList,messageTabClicker,"PublicGroupWise/MessageToPublicGroupWise")
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, SendToPublicGroupWiseDialog.TAG)
        }

        buttonAllParents?.setOnClickListener {
            val paramsMap: HashMap<String?, Int> = LinkedHashMap()
            paramsMap["MessageId"] = messageList.mESSAGEID
            paramsMap["AdminId"] = adminId
           sendFunction("Are you Sure want to send All Parents?","AllParents/MessageToAllParents",paramsMap)
        }

        buttonAllStaff?.setOnClickListener {
            val paramsMap: HashMap<String?, Int> = LinkedHashMap()
            paramsMap["MessageId"] = messageList.mESSAGEID
            paramsMap["AdminId"] = adminId
            sendFunction("Are you Sure want to send All Staff?","AllStaffs/MessageToAllStaffs",paramsMap)
        }

        buttonAllConveyor?.setOnClickListener {
            val paramsMap: HashMap<String?, Int> = LinkedHashMap()
            paramsMap["MessageId"] = messageList.mESSAGEID
            paramsMap["AdminId"] = adminId
            sendFunction("Are you Sure want to send All Conveyor?","Conveyor/MessageToAllConveyors",paramsMap)
        }

        buttonAllPta?.setOnClickListener {
            val paramsMap: HashMap<String?, Int> = LinkedHashMap()
            paramsMap["MessageId"] = messageList.mESSAGEID
            paramsMap["AdminId"] = adminId
            sendFunction("Are you Sure want to send All PTA?","Pta/MessageToAllPta",paramsMap)
        }

        if(!messageList.mESSAGEDATE.isNullOrBlank()) {
            val date1: Array<String> = messageList.mESSAGEDATE.split("T".toRegex()).toTypedArray()
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
                messageTabClicker.sendToAllParentStaffPTAConveyor(url,paramsMap)
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