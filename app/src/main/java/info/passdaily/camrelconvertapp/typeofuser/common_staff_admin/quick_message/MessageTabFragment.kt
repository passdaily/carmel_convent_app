package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_message

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

import okhttp3.RequestBody
import java.util.ArrayList

@Suppress("DEPRECATION")
class MessageTabFragment(
    var messageClickListener :MessageClickListener) : Fragment(),MessageTabClicker {

    var TAG = "NotificationTabFragment"
    private var _binding: FragmentAttendedTabBinding? = null
    private val binding get() = _binding!!

    private lateinit var quickMessageViewModel: QuickMessageViewModel

    var adminRole = 0
    private lateinit var localDBHelper : LocalDBHelper

    lateinit var bottomSheetDetails : BottomSheetMessageDetails
    lateinit var bottomSheetMessageDetailsAdmin : BottomSheetMessageDetailsAdmin
//
    lateinit var bottomSheetUpdateMessage : BottomSheetUpdateMessage

    var recyclerView : RecyclerView? = null

    lateinit var mAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminRole = user[0].ADMIN_ROLE

        // Inflate the layout for this fragment
        quickMessageViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[QuickMessageViewModel::class.java]
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
        mAdapter = MessageAdapter(
            this,
            Global.messageList,
            requireActivity(),
            TAG
        )
        recyclerView?.adapter = mAdapter


        if(Global.messageList.isNotEmpty()){
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

//
        bottomSheetDetails = BottomSheetMessageDetails()
        bottomSheetUpdateMessage = BottomSheetUpdateMessage()
        bottomSheetMessageDetailsAdmin = BottomSheetMessageDetailsAdmin()
    }


    class MessageAdapter(var messageTabClicker : MessageTabClicker,
                         var messageList: ArrayList<MessageListModel.Message>,
                         var context: Context, var TAG: String)
        : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewTitle: TextView = view.findViewById(R.id.textViewClass)
            var textViewDate: TextView = view.findViewById(R.id.textViewDate)
            var textViewDesc: TextView = view.findViewById(R.id.textViewTitle)

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
            holder.textViewTitle.text = messageList[position].mESSAGETITLE

            if(!messageList[position].mESSAGEDATE.isNullOrBlank()) {
                val date: Array<String> =
                    messageList[position].mESSAGEDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                holder.textViewDate.text = Utils.formattedDate(dddd)
            }
            holder.textViewDesc.text = messageList[position].mESSAGECONTENT

            holder.textViewDesc.setOnClickListener {
                messageTabClicker.onDetailClicker(messageList[position])
            }
            holder.textViewTitle.setOnClickListener {
                messageTabClicker.onDetailClicker(messageList[position])
            }


            holder.imageViewMore.setOnClickListener(View.OnClickListener {
                val popupMenu = PopupMenu(context, holder.imageViewMore)
                popupMenu.inflate(R.menu.video_adapter_menu)
                popupMenu.menu.findItem(R.id.menu_edit).icon = context.resources.getDrawable(R.drawable.ic_icon_edit)
                popupMenu.menu.findItem(R.id.menu_report).icon = context.resources.getDrawable(R.drawable.ic_icon_about_gray)
                popupMenu.menu.findItem(R.id.menu_video).icon = context.resources.getDrawable(R.drawable.ic_icon_close)
                popupMenu.menu.findItem(R.id.menu_offline_video).icon = context.resources.getDrawable(R.drawable.ic_icon_delete_gray)
                popupMenu.menu.findItem(R.id.menu_download).icon = context.resources.getDrawable(R.drawable.ic_icon_download)
                popupMenu.menu.findItem(R.id.menu_open).icon = context.resources.getDrawable(R.drawable.ic_icon_download)
                popupMenu.menu.findItem(R.id.menu_open).isVisible = false
                popupMenu.menu.findItem(R.id.menu_download).isVisible = false
                popupMenu.menu.findItem(R.id.menu_report).isVisible = false
                popupMenu.menu.findItem(R.id.menu_offline_video).isVisible = false
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            messageTabClicker.onUpdateClickListener(messageList[position])
                            true
                        }
                        R.id.menu_video -> {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Are you sure want to delete template?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { _, _ ->
                                    //MessageEdit/MessageDropById?MessageId=
                                    messageTabClicker.onDeleteClickListener("MessageEdit/MessageDropById",messageList[position].mESSAGEID)
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
            return messageList.size
        }

    }
    override fun onCloseBottomSheet(message: String) {
        if(adminRole == 3){
            bottomSheetDetails.dismiss()
        }else if(adminRole == 1){
            bottomSheetMessageDetailsAdmin.dismiss()
        }
    }

    override fun onSuccessMessage(message: String) {
        messageClickListener.onCreateClick(message)
    }
    override fun onFailedMessage(message: String) {
        messageClickListener.onFailedMessage(message)
    }

    override fun onDetailClicker(messageList: MessageListModel.Message) {

        if(adminRole == 3){
            bottomSheetDetails = BottomSheetMessageDetails(this,messageList)
            bottomSheetDetails.show(requireActivity().supportFragmentManager, "TAG")
        }else if(adminRole == 1){
            bottomSheetMessageDetailsAdmin = BottomSheetMessageDetailsAdmin(this,messageList)
            bottomSheetMessageDetailsAdmin.show(requireActivity().supportFragmentManager, "TAG")
        }


    }



    override fun onUpdateClickListener(messageList: MessageListModel.Message) {

        bottomSheetUpdateMessage = BottomSheetUpdateMessage(this,messageList)
        bottomSheetUpdateMessage.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onDeleteClickListener(url: String, mESSAGEID: Int) {
        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["MessageId"] = mESSAGEID
        quickMessageViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    messageClickListener.onCreateClick("Deleted Successfully")
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
        quickMessageViewModel.getCommonPostFun(url,submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "1" -> {
                                    messageClickListener.onCreateClick(successMessage)
                                    bottomSheetUpdateMessage.dismiss()
                                }
                                Utils.resultFun(response) == "0" -> {
                                    messageClickListener.onFailedMessage(failedMessage)
                                }
                                else -> {
                                    messageClickListener.onFailedMessage(existingMessage)
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


    override  fun sendToAllParentStaffPTAConveyor(url : String, paramsMap : HashMap<String?, Int>){

        quickMessageViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(requireActivity(),"Message send successfully",binding.constraintLayout)
                                    bottomSheetMessageDetailsAdmin.dismiss()
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
}

interface MessageTabClicker{
    fun onCloseBottomSheet(message: String)
    fun onSuccessMessage(message: String)
    fun onFailedMessage(message: String)
    fun onDetailClicker(messageList: MessageListModel.Message)

    fun sendToAllParentStaffPTAConveyor(url : String, paramsMap : HashMap<String?, Int>)


    fun onUpdateClickListener(messageList: MessageListModel.Message)
    fun onDeleteClickListener(url: String,mESSAGEID : Int)
    fun onSubmitUpdateClick(url: String, submitItems: RequestBody, successMessage : String, failedMessage : String,existingMessage : String)
}