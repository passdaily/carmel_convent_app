package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentAttendedTabBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_message.BottomSheetMessageDetailsAdmin

import okhttp3.RequestBody
import java.util.ArrayList

@Suppress("DEPRECATION")
class NotificationTabFragment(
    var notificationClickListener :NotificationClickListener) : Fragment(),NotificationTabClicker {

    var TAG = "NotificationTabFragment"
    private var _binding: FragmentAttendedTabBinding? = null
    private val binding get() = _binding!!

    private lateinit var quickNotificationViewModel: QuickNotificationViewModel

    var adminRole = 0
    private lateinit var localDBHelper : LocalDBHelper

    lateinit var bottomSheetDetails : BottomSheetNotificationDetails
    lateinit var bottomSheetNotificationDetailsAdmin : BottomSheetNotificationDetailsAdmin

    lateinit var bottomSheetUpdate : BottomSheetUpdateNotification

    var recyclerView : RecyclerView? = null

    lateinit var mAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminRole = user[0].ADMIN_ROLE

        // Inflate the layout for this fragment
        quickNotificationViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[QuickNotificationViewModel::class.java]
        // Inflate the layout for this fragment
        _binding = FragmentAttendedTabBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var constraintEmpty = binding.constraintEmpty
        var imageViewEmpty = binding.imageViewEmpty
        var textEmpty = binding.textEmpty
        var shimmerViewContainer = binding.shimmerViewContainer


        recyclerView = binding.recyclerView
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        mAdapter = NotificationAdapter(
            this,
            Global.notificationList,
            requireActivity(),
            TAG
        )
        recyclerView?.adapter = mAdapter


        if(Global.notificationList.isNotEmpty()){
            recyclerView?.visibility = View.VISIBLE
            constraintEmpty.visibility = View.GONE

        }else{
            recyclerView?.visibility = View.GONE
            constraintEmpty.visibility = View.VISIBLE
            Glide.with(this)
                .load(R.drawable.ic_empty_progress_report)
                .into(imageViewEmpty)

            textEmpty.text =  resources.getString(R.string.no_results)
        }


        bottomSheetDetails = BottomSheetNotificationDetails()
        bottomSheetUpdate = BottomSheetUpdateNotification()
        bottomSheetNotificationDetailsAdmin = BottomSheetNotificationDetailsAdmin()
    }


    class NotificationAdapter(var notificationTabClicker : NotificationTabClicker,
                              var notificationSentList: ArrayList<NotificationStaffModel.Inbox>,
                              var context: Context, var TAG: String)
        : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewTitle: TextView = view.findViewById(R.id.textViewClass)
            var textViewDate: TextView = view.findViewById(R.id.textViewDate)
            var textViewDesc: TextView = view.findViewById(R.id.textViewTitle)
            var textCreatedBy :TextView = view.findViewById(R.id.textView121)
            var textTotalFiles :TextView = view.findViewById(R.id.textView120)

            var imageViewMore : ImageView = view.findViewById(R.id.imageViewMore)

//            var cardViewParent: CardView = view.findViewById(R.id.cardViewParent)
//            var cardViewClass: CardView = view.findViewById(R.id.cardViewClass)

        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.notification_tab_adapter, parent, false)
            )
        }

        @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewTitle.text = notificationSentList[position].vIRTUALMAILTITLE

            if(!notificationSentList[position].vIRTUALMAILDATE.isNullOrBlank()) {
                val date: Array<String> =
                    notificationSentList[position].vIRTUALMAILDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                holder.textViewDate.text = Utils.formattedDate(dddd)
            }
            holder.textViewDesc.text = notificationSentList[position].vIRTUALMAILCONTENT

            holder.textViewDesc.setOnClickListener {
                notificationTabClicker.onDetailClicker(notificationSentList[position])
            }
            holder.textViewTitle.setOnClickListener {
                notificationTabClicker.onDetailClicker(notificationSentList[position])
            }

            holder.textCreatedBy.text = notificationSentList[position].createdStaff

            holder.textTotalFiles.isVisible = false
            if(notificationSentList[position].fileCount > 0){
                holder.textTotalFiles.isVisible = true
                holder.textTotalFiles.text = "File Count ${notificationSentList[position].fileCount}"
            }

            holder.imageViewMore.setOnClickListener(View.OnClickListener {
                val popupMenu = PopupMenu(context, holder.imageViewMore)
                popupMenu.inflate(R.menu.notification_menu)
                popupMenu.menu.findItem(R.id.menu_manage_file).icon = context.resources.getDrawable(R.drawable.ic_icon_edit)
                popupMenu.menu.findItem(R.id.menu_edit).icon = context.resources.getDrawable(R.drawable.ic_icon_about_gray)
                popupMenu.menu.findItem(R.id.menu_delete).icon = context.resources.getDrawable(R.drawable.ic_icon_close)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_manage_file -> {
                            notificationTabClicker.onManageFileListener(notificationSentList[position])
                            true
                        }
                        R.id.menu_edit -> {
                            notificationTabClicker.onUpdateClickListener(notificationSentList[position])
                            true
                        }
                        R.id.menu_delete -> {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Are you sure want to delete template?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { _, _ ->
                                    //InboxEdit/InboxDropById?VirtualMailId=
                                    notificationTabClicker.onDeleteClickListener("InboxEdit/InboxDropById",notificationSentList[position].vIRTUALMAILID)
                                }
                                .setNegativeButton(
                                    "No"
                                ) { dialog, _ -> //  Action for 'NO' Button
                                    dialog.cancel()
                                }
                            //Creating dialog box
                            val alert = builder.create()
                            //Setting the title manually
                            alert.setTitle("Delete")
                            alert.show()
                            val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                            buttonbackground.setTextColor(Color.BLACK)
                            val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                            buttonbackground1.setTextColor(Color.BLACK)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            })
        }

        override fun getItemCount(): Int {
            return notificationSentList.size
        }

    }
    override fun onSuccessMessage(message: String) {
        notificationClickListener.onCreateClick(message)
    }
    override fun onFailedMessage(message: String) {
        notificationClickListener.onFailedMessage(message)
    }

    override fun onDetailClicker(notificationList: NotificationStaffModel.Inbox) {
//        val dialog = Dialog(requireActivity())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
//        dialog.setContentView(R.layout.dialogview_notification_tab)
//        val lp = WindowManager.LayoutParams()
//        lp.copyFrom(dialog.window!!.attributes)
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//        lp.gravity = Gravity.CENTER
//        dialog.window!!.attributes = lp
//
//        var sentDate = ""
//        var textViewTitle = dialog.findViewById<TextView>(R.id.textViewTitle)
//        var textViewDesc = dialog.findViewById<ShowMoreTextView>(R.id.textViewDesc)
//        var textViewDate = dialog.findViewById<TextView>(R.id.textViewDate)
//        var imageViewClose  = dialog.findViewById<ImageView>(R.id.imageViewClose)
//
//
//        if(!notificationList.vIRTUALMAILDATE.isNullOrBlank()) {
//            val date1: Array<String> = notificationList.vIRTUALMAILDATE.split("T".toRegex()).toTypedArray()
//            val sendStr = Utils.longconversion(date1[0] + " " + date1[1])
//            sentDate = Utils.formattedDateTime(sendStr)
//        }
//        textViewDate.text = sentDate
//        textViewTitle.text = notificationList.vIRTUALMAILTITLE
////        textViewDesc.text = notificationSentList.vIRTUALMAILCONTENT
//
//        textViewDesc?.text = notificationList.vIRTUALMAILCONTENT
//        textViewDesc?.apply {
//            setShowingLine(5)
//            setShowMoreTextColor(resources.getColor(R.color.green_300))
//            setShowLessTextColor(resources.getColor(R.color.green_300))
//        }
//
//        imageViewClose.setOnClickListener {
//
//            dialog.dismiss()
//        }
//
//        dialog.show()
        if(adminRole == 3){
            bottomSheetDetails = BottomSheetNotificationDetails(this,notificationList)
            bottomSheetDetails.show(requireActivity().supportFragmentManager, "TAG")
        }else if(adminRole == 1){
            bottomSheetNotificationDetailsAdmin = BottomSheetNotificationDetailsAdmin(this,notificationList)
            bottomSheetNotificationDetailsAdmin.show(requireActivity().supportFragmentManager, "TAG")
        }


    }

    override fun onSendButtonClicker(notificationList: NotificationStaffModel.Inbox) {

    }


    override fun onUpdateClickListener(notificationList: NotificationStaffModel.Inbox) {
        val dialog1 = UpdateNotificationDialog(this,notificationList)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.show(transaction, UpdateNotificationDialog.TAG)

//        bottomSheetUpdate = BottomSheetUpdateNotification(this,notificationList)
//        bottomSheetUpdate.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onDeleteClickListener(url: String, vIRTUALMAILID: Int) {
        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["VirtualMailId"] = vIRTUALMAILID
        quickNotificationViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    notificationClickListener.onCreateClick("Deleted Successfully")
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Deletion Failed", binding.constraintLayout)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", binding.constraintLayout)
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }

    override fun onSubmitUpdateClick(
        url: String,
        submitItems: RequestBody,
        successMessage: String,
        failedMessage: String,
        existingMessage : String
    ) {
        quickNotificationViewModel.getCommonPostFun(url,submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "1" -> {
                                    notificationClickListener.onCreateClick(successMessage)
                                    bottomSheetUpdate.dismiss()
                                }
                                Utils.resultFun(response) == "0" -> {
                                    notificationClickListener.onFailedMessage(failedMessage)
                                }
                                Utils.resultFun(response) == "-1" -> {
                                    notificationClickListener.onFailedMessage(existingMessage)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
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

    override fun onCloseBottomSheet(message: String) {
        if(adminRole == 3){
            bottomSheetDetails.dismiss()
        }else if(adminRole == 1){
            bottomSheetNotificationDetailsAdmin.dismiss()
        }
    }

    override  fun sendToAllParentStaffPTAConveyor(url : String, paramsMap : HashMap<String?, Int>){

        quickNotificationViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(requireActivity(),"Notification send successfully",binding.constraintLayout)
                                    bottomSheetNotificationDetailsAdmin.dismiss()
                                }
                                Utils.resultFun(response) == "FAILED" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Failed while Sending message", binding.constraintLayout)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", binding.constraintLayout)
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(BottomSheetMessageDetailsAdmin.TAG,"loading")
                        }
                    }
                }
            })
    }

    override fun onManageFileListener(notificationList: NotificationStaffModel.Inbox) {
        val dialog1 = UpdateNotificationFileDialog(this,notificationList)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.show(transaction, UpdateNotificationFileDialog.TAG)
    }
}

interface NotificationTabClicker{
    fun onCloseBottomSheet(message: String)
    fun onSuccessMessage(message: String)
    fun onFailedMessage(message: String)
    fun onDetailClicker(notificationList: NotificationStaffModel.Inbox)

    fun onSendButtonClicker(notificationList: NotificationStaffModel.Inbox)

    fun sendToAllParentStaffPTAConveyor(url : String, paramsMap : HashMap<String?, Int>)

    fun onManageFileListener(notificationList: NotificationStaffModel.Inbox)
    fun onUpdateClickListener(notificationList: NotificationStaffModel.Inbox)
    fun onDeleteClickListener(url: String,vIRTUALMAILID : Int)
    fun onSubmitUpdateClick(url: String, submitItems: RequestBody, successMessage : String, failedMessage : String,existingMessage : String)
}