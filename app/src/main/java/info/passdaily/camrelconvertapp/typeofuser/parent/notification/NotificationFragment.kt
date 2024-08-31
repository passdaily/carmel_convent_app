package info.passdaily.camrelconvertapp.typeofuser.parent.notification

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.camera.core.processing.SurfaceProcessorNode.In
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DateItemBinding
import info.passdaily.camrelconvertapp.databinding.FragmentNotificationBinding
import info.passdaily.camrelconvertapp.databinding.NotificationAdapterBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.downloader.Error
import info.passdaily.camrelconvertapp.services.downloader.OnDownloadListener
import info.passdaily.camrelconvertapp.services.downloader.PRDownloader
import info.passdaily.camrelconvertapp.services.downloader.StatusD
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.AssignDetailsTabFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.AssignmentClickListener
import info.passdaily.camrelconvertapp.typeofuser.parent.MainActivityParent
import kotlinx.coroutines.awaitAll
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.File


@Suppress("DEPRECATION")
class NotificationFragment : Fragment(),InboxClickListener,NotificationClickListener{

    lateinit var bindingNotification : FragmentNotificationBinding

    var TAG = "NotificationFragment"
    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var recyclerView :  RecyclerView? =null
    var shimmerViewContainer: ShimmerFrameLayout? = null
    private lateinit var notificationViewModel: NotificationViewModel
    val consolidatedList = ArrayList<ListItem>()
    var  staffAttachmentList = ArrayList<InboxDetailsNewModel.FileDetail>()

    lateinit var mAdapter : NotificationAdapter

    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null

    var inboxList = ArrayList<InboxDetailsModel.InboxDetail>()


    private var readPermission = false
    private var writePermission = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG, "onAttach ")

    }

    var recyclerViewStaffList: RecyclerView? = null
    var textViewNoFilesStudent: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.currentPage = 14
        Global.screenState = "landingpage"

        notificationViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[NotificationViewModel::class.java]

        // Inflate the layout for this fragment
      ///  notificationViewModel = ViewModelProvider(this)[TrackViewModel::class.java]
       // return inflater.inflate(R.layout.fragment_library, container, false)

        // Inflate the layout for this fragment
        bindingNotification = FragmentNotificationBinding.inflate(inflater, container, false)

//        bindingNotification.txtMessage.text="Sample Text"

        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getProductById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID
        STUDENT_ROLL_NO = student.STUDENT_ROLL_NO

      //  var textViewTitle = view.findViewById(R.id.textView32) as TextView
        bindingNotification.textViewTitle.text = "Notification"
        //constraintEmpty = view.findViewById(R.id.constraintEmpty)
       // imageViewEmpty = view.findViewById(R.id.imageViewEmpty)
      //  textEmpty = view.findViewById(R.id.textEmpty)
        constraintEmpty = bindingNotification.constraintEmpty
        imageViewEmpty = bindingNotification.imageViewEmpty
        shimmerViewContainer = bindingNotification.shimmerViewContainer
        textEmpty = bindingNotification.textEmpty
        if (isAdded) {
            Glide.with(mContext!!)
                .load(R.drawable.ic_empty_state_notification)
                .into(imageViewEmpty!!)
        }
        bindingNotification.textEmpty.text = "No Notification"

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }



        val jsonObject = JSONObject()
        try {
            jsonObject.put("ACCADEMIC_ID", ACADEMICID)
            jsonObject.put("CLASS_ID", CLASSID)
            jsonObject.put("STUDENT_ID", STUDENTID)
            jsonObject.put("VIRTUAL_MAIL_SENT_DATE", "")
            jsonObject.put("TODate", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

//        notificationViewModel.getInboxDetails(jsonObject)

       // recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView = bindingNotification.recyclerView
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())


        intiFunction(accademicRe)


        return bindingNotification.root
    }


    private fun intiFunction(accademicRe : RequestBody) {

        notificationViewModel.getInboxDetails(accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if (response.inboxDetails.isNotEmpty()) {
                                inboxList = response.inboxDetails
                                shimmerViewContainer?.visibility = View.GONE
                                constraintEmpty?.visibility = View.GONE
                                recyclerView?.visibility = View.VISIBLE
                                var sendDate = dateCompare(inboxList[0].vIRTUALMAILSENTDATE)
                                consolidatedList.add(
                                    DateItem(
                                        Utils.formattedDateTimedd(sendDate), 0, 0, "", 0, "",
                                        "", 0, 0, "", 0,
                                        "", 0, "", 0,0
                                    )
                                )
                                for (ii in inboxList.indices) {
                                    if (sendDate == dateCompare(inboxList[ii].vIRTUALMAILSENTDATE)) {
                                        consolidatedList.add(
                                            GeneralItem(
                                                inboxList[ii].aCCADEMICID,
                                                inboxList[ii].cLASSID,
                                                "",
                                                inboxList[ii].sTUDENTID,
                                                inboxList[ii].vIRTUALMAILCONTENT,
                                                "",
                                                inboxList[ii].vIRTUALMAILREADSTATUS,
                                                inboxList[ii].vIRTUALMAILSENTBY,
                                                inboxList[ii].vIRTUALMAILSENTDATE,
                                                inboxList[ii].vIRTUALMAILSENTID,
                                                inboxList[ii].vIRTUALMAILSENTSTATUS,
                                                inboxList[ii].vIRTUALMAILSENTSTUDENTID,
                                                inboxList[ii].vIRTUALMAILTITLE,
                                                inboxList[ii].vIRTUALMAILTYPE,
                                                inboxList[ii].fILECOUNT
                                            )
                                        )
                                    } else {
                                        sendDate = dateCompare(inboxList[ii].vIRTUALMAILSENTDATE)
                                        consolidatedList.add(
                                            DateItem(
                                                Utils.formattedDateTimedd(sendDate), 0, 0, "",
                                                0, "", "", 0,
                                                0, "", 0, "",
                                                0, "", 0,0
                                            )
                                        )
                                        consolidatedList.add(
                                            GeneralItem(
                                                inboxList[ii].aCCADEMICID,
                                                inboxList[ii].cLASSID,
                                               "",
                                                inboxList[ii].sTUDENTID,
                                                inboxList[ii].vIRTUALMAILCONTENT,
                                                "",
                                                inboxList[ii].vIRTUALMAILREADSTATUS,
                                                inboxList[ii].vIRTUALMAILSENTBY,
                                                inboxList[ii].vIRTUALMAILSENTDATE,
                                                inboxList[ii].vIRTUALMAILSENTID,
                                                inboxList[ii].vIRTUALMAILSENTSTATUS,
                                                inboxList[ii].vIRTUALMAILSENTSTUDENTID,
                                                inboxList[ii].vIRTUALMAILTITLE,
                                                inboxList[ii].vIRTUALMAILTYPE,
                                                inboxList[ii].fILECOUNT
                                            )
                                        )
                                        //      break
                                    }
                                    //  }
                                }
                                Log.i(TAG,"end here ")
                                if (isAdded) {
                                    Log.i(TAG,"consolidatedList $consolidatedList")
                                    mAdapter =  NotificationAdapter(this,consolidatedList, mContext!!)
                                    recyclerView?.adapter = mAdapter

                                }
                            }else{
                                if (isAdded) {
                                    Glide.with(mContext!!)
                                        .load(R.drawable.ic_empty_state_notification)
                                        .into(imageViewEmpty!!)
                                }
                                textEmpty?.text = requireActivity().resources.getString(R.string.no_results)
                            }

                        }
                        Status.ERROR -> {
                            shimmerViewContainer?.visibility = View.GONE
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerView?.visibility = View.GONE

                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_no_internet)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text = requireActivity().resources.getString(R.string.no_internet)

                            Log.i(TAG,"ERROR ")
                        }
                        Status.LOADING -> {
                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_empty_state_notification)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text =  requireActivity().resources.getString(R.string.loading)
                            shimmerViewContainer?.visibility = View.VISIBLE
                            recyclerView?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            Log.i(TAG,"LOADING ")
                        }
                    }
                }
            })
    }




    class NotificationAdapter(var inboxClickListener: InboxClickListener,
                              var inboxDetails: ArrayList<ListItem>, var mContext: Context)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var inboxdate  = ""
//        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
//            var textViewDate: TextView = view.findViewById(R.id.textViewDate)
//            var textViewDesc: TextView = view.findViewById(R.id.textViewDesc)
//        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            val itemView = LayoutInflater.from(parent.context)
//                .inflate(R.layout.notification_adapter, parent, false)
//            return ViewHolder(itemView)
            val layoutInflater = LayoutInflater.from(parent.context)
            return when (viewType) {
                ListItem.TYPE_DATE ->
                    DateViewHolder(DateItemBinding.inflate(layoutInflater,parent,false))
                else ->
                    GeneralViewHolder(NotificationAdapterBinding.inflate(layoutInflater,parent,false))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder.itemViewType) {
                ListItem.TYPE_DATE -> (holder as DateViewHolder).bind( position,
                    item = inboxDetails[position] as DateItem,
                )
                ListItem.TYPE_GENERAL -> (holder as GeneralViewHolder).bind( position,
                    item = inboxDetails[position] as GeneralItem
                )
            }
        }

        override fun getItemCount(): Int {
            return inboxDetails.size
        }
        override fun getItemViewType(position: Int): Int {
            return inboxDetails[position].type
        }


        inner class DateViewHolder(val binding: DateItemBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(position: Int,item: DateItem) {
                binding.txtDate.text = item.dATEFORMAT
            }
        }

        inner class GeneralViewHolder(val binding: NotificationAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(position: Int,item: GeneralItem) {
//                var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
//                var textViewDate: TextView = view.findViewById(R.id.textViewDate)
//                var textViewDesc: TextView = view.findViewById(R.id.textViewDesc)

                binding.textTotalFiles.isVisible = false
                if(item.fILECOUNT > 0){
                    binding.textTotalFiles.isVisible = true
                    binding.textTotalFiles.text = "File Count ${item.fILECOUNT}"
                }

                if (!item.vIRTUALMAILSENTDATE.isNullOrBlank()) {
                    val date1: Array<String> =
                        item.vIRTUALMAILSENTDATE.split("T".toRegex()).toTypedArray()
                    val ddddd: Long = Utils.longconversion(date1[0] + " " + date1[1])
                    inboxdate = Utils.formattedDateTimeInbox(ddddd)
                }
                binding.textViewDate.text = inboxdate
                binding.textViewTitle.text = item.vIRTUALMAILTITLE
                binding.textViewDesc.text = item.vIRTUALMAILCONTENT

                val inboxCount = item.vIRTUALMAILREADSTATUS.toString()
                if (inboxCount == "0" && !inboxCount.isNullOrBlank()) {
                    val typeface = ResourcesCompat.getFont(mContext, R.font.poppins_bold)
                    binding.textViewDate.typeface = typeface
                    binding.textViewTitle.typeface = typeface
                    binding.textViewDesc.typeface = typeface
                }else if (inboxCount == "1" && !inboxCount.isNullOrBlank()) {
                    val typefaceD = ResourcesCompat.getFont(mContext, R.font.poppins_regular)
                    binding.textViewDate.typeface = typefaceD
                    binding.textViewDesc.typeface = typefaceD
                    val typeface = ResourcesCompat.getFont(mContext, R.font.poppins_medium)
                    binding.textViewTitle.typeface = typeface

                }

                itemView.setOnClickListener {
                    inboxClickListener.onViewClick(item,position)
                }

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewClick(item: GeneralItem, position: Int){
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialogview_inbox)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.attributes = lp

        var sentDate = ""
        var textViewTitle = dialog.findViewById<TextView>(R.id.textViewTitle)
        var textViewDesc = dialog.findViewById<TextView>(R.id.textViewDesc)
        var textViewDate = dialog.findViewById<TextView>(R.id.textViewDate)
        var imageViewClose  = dialog.findViewById<ImageView>(R.id.imageViewClose)

     //   var recyclerViewStaffList   = dialog.findViewById<ImageView>(R.id.recyclerViewStaffList)
        recyclerViewStaffList = dialog.findViewById<RecyclerView>(R.id.recyclerViewStaffList)
        recyclerViewStaffList?.layoutManager = GridLayoutManager(requireActivity(), 4)
        textViewNoFilesStudent = dialog.findViewById<TextView>(R.id.textViewNoFilesStudent)

        initFunction(item.vIRTUALMAILSENTID,STUDENTID);



        inboxReadStaus(Global.studentId,item.vIRTUALMAILSENTID)
        if(!item.vIRTUALMAILSENTDATE.isNullOrBlank()) {
            val date1: Array<String> = item.vIRTUALMAILSENTDATE.split("T".toRegex()).toTypedArray()
            val sendStr = Utils.longconversion(date1[0] + " " + date1[1])
            sentDate = Utils.formattedDateTime(sendStr)
        }
        textViewDate.text = sentDate
        textViewTitle.text = item.vIRTUALMAILTITLE
        textViewDesc.text = item.vIRTUALMAILCONTENT

        imageViewClose.setOnClickListener {


            if (item.vIRTUALMAILREADSTATUS == 0) {
                val total: Int = Integer.valueOf(Global.inboxcount) - 1
                Log.i(TAG, "Total$total")
                Global.inboxcount = total
                (activity as MainActivityParent?)!!.setInboxcount()

                //inboxlist = new ArrayList<HashMap<String, String>>();
                item.vIRTUALMAILREADSTATUS = 1
                mAdapter.notifyDataSetChanged()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun initFunction(vIRTUALMAILSENTID : Int,sTUDENTID :Int) {

        notificationViewModel.getInboxGetByDetails(vIRTUALMAILSENTID,sTUDENTID)
            .observe(this, Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            staffAttachmentList = response.fileDetails as ArrayList<InboxDetailsNewModel.FileDetail>

                            if(staffAttachmentList.isNotEmpty()){
                                recyclerViewStaffList?.visibility = View.VISIBLE
                                textViewNoFilesStudent?.visibility = View.GONE

                                recyclerViewStaffList?.adapter = StaffAttachmentListAdapter(
                                    this,
                                    this@NotificationFragment,
                                    staffAttachmentList, requireActivity(), TAG
                                )
                            }else{
                                recyclerViewStaffList?.visibility = View.GONE
                                textViewNoFilesStudent?.visibility = View.VISIBLE
                            }

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            staffAttachmentList = ArrayList<InboxDetailsNewModel.FileDetail>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })

    }


    fun inboxReadStaus(studentId: Int, vIRTUALMAILSENTID: Int){
        notificationViewModel.getInboxReadStatus(studentId, vIRTUALMAILSENTID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(Utils.resultFun(response) == "SUCCESS"){
                                Log.i(TAG, "read")
                            }else if(Utils.resultFun(response) == "FAILED"){
                                Log.i(TAG, "FAILED")
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })
    }



    class GeneralItem(
        val aCCADEMICID: Int,
        val cLASSID: Int,
        val cLASSSECTION: String,
        val sTUDENTID: Int,
        val vIRTUALMAILCONTENT: String,
        val vIRTUALMAILREADDATE: String,
        var vIRTUALMAILREADSTATUS: Int,
        val vIRTUALMAILSENTBY: Int,
        val vIRTUALMAILSENTDATE: String,
        val vIRTUALMAILSENTID: Int,
        val vIRTUALMAILSENTSTATUS: Any,
        val vIRTUALMAILSENTSTUDENTID: Int,
        val vIRTUALMAILTITLE: String,
        val vIRTUALMAILTYPE: Int,
        val fILECOUNT: Int,
    ) : ListItem(TYPE_GENERAL)

    class DateItem(
        val dATEFORMAT :String,
        val aCCADEMICID: Int,
        val cLASSID: Int,
        val cLASSSECTION: String,
        val sTUDENTID: Int,
        val vIRTUALMAILCONTENT: String,
        val vIRTUALMAILREADDATE: String,
        val vIRTUALMAILREADSTATUS: Int,
        val vIRTUALMAILSENTBY: Int,
        val vIRTUALMAILSENTDATE: String,
        val vIRTUALMAILSENTID: Int,
        val vIRTUALMAILSENTSTATUS: Any,
        val vIRTUALMAILSENTSTUDENTID: Int,
        val vIRTUALMAILTITLE: String,
        val vIRTUALMAILTYPE: Int,
        val fILECOUNT: Int,
    ) : ListItem(TYPE_DATE)


    fun dateCompare(vIRTUALMAILSENTDATE: String): Long {
        val date1: Array<String> = vIRTUALMAILSENTDATE.split("T".toRegex()).toTypedArray()
        return Utils.longconversionInbox(date1[0])
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
        Log.i(TAG,"onDetach ")
    }

    override fun onDestroy() {
        super.onDestroy()
        mContext = null
        Log.i(TAG,"onDestroy ")
    }




    class StaffAttachmentListAdapter(
        var assignmentClickListener: NotificationClickListener,
        var assignDetailsTabFragment: NotificationFragment,
        var staffAttachmentList: ArrayList<InboxDetailsNewModel.FileDetail>,
        var context: Context, var TAG: String )
        : RecyclerView.Adapter<StaffAttachmentListAdapter.ViewHolder>() {
        var downLoadPos = 0
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView = view.findViewById(R.id.imageView)
            var imageViewOther: ImageView = view.findViewById(R.id.imageViewOther)
            var imageViewMore: ImageView = view.findViewById(R.id.imageViewMore)
            var progressDialog = ProgressDialog(context)
        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.assignment_item_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val path: String = staffAttachmentList[position].fILENAME
            val mFile = File(path)
            if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")
            ) {
                // Word document
                holder.imageViewOther.visibility = View.VISIBLE
                holder.imageView.visibility = View.GONE
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //  .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_word)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewOther)
            } else if (mFile.toString().contains(".pdf") ||
                mFile.toString().contains(".PDF")
            ) {
                holder.imageViewOther.visibility = View.VISIBLE
                holder.imageView.visibility = View.GONE
                // PDF file
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //     .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_pdf)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewOther)
            } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")
            ) {
                // Powerpoint file
                holder.imageViewOther.visibility = View.VISIBLE
                holder.imageView.visibility = View.GONE
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_power_point)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewOther)
            } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
            ) {
                holder.imageViewOther.visibility = View.VISIBLE
                holder.imageView.visibility = View.GONE
                // Excel file
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //  .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_excel)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewOther)
            } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                || mFile.toString().contains(".png") || mFile.toString()
                    .contains(".JPG") || mFile.toString().contains(".JPEG")
                || mFile.toString().contains(".PNG")
            ) {
                // JPG file
                holder.imageViewOther.visibility = View.GONE
                holder.imageView.visibility = View.VISIBLE
                Glide.with(context)
                    .load(Global.event_url + "/Upload/VMailFile/" + path)
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_gallery)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageView)
            } else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                // Text file
                holder.imageViewOther.visibility = View.VISIBLE
                holder.imageView.visibility = View.GONE
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_text)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewOther)
            } else if (mFile.toString().contains(".mp3") || mFile.toString()
                    .contains(".wav") || mFile.toString().contains(".ogg")
                || mFile.toString().contains(".m4a") || mFile.toString()
                    .contains(".aac") || mFile.toString().contains(".wma") ||
                mFile.toString().contains(".MP3") || mFile.toString()
                    .contains(".WAV") || mFile.toString().contains(".OGG")
                || mFile.toString().contains(".M4A") || mFile.toString()
                    .contains(".AAC") || mFile.toString().contains(".WMA")
            ) {
                holder.imageViewOther.visibility = View.VISIBLE
                holder.imageView.visibility = View.GONE
                Glide.with(context)
                    .load(File(path))
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() // .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_voice)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewOther)
            } else {
                holder.imageViewOther.visibility = View.VISIBLE
                holder.imageView.visibility = View.GONE
                Glide.with(context)
                    // .load(java.io.File(path))
                    .load(Global.event_url + "/Upload/VMailFile/" + path)
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_video_library)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewOther)
//                Glide.with(context)
//                    .load(File(path))
//                    .apply(
//                        RequestOptions.centerCropTransform()
//                            .dontAnimate() //   .override(imageSize, imageSize)
//                            .placeholder(R.drawable.ic_file_video)
//                    )
//                    .thumbnail(0.5f)
//                    .into(holder.imageViewOther)
            }

            holder.imageViewMore.setOnClickListener {

                val popupMenu = PopupMenu(context, holder.imageViewMore)
                popupMenu.inflate(R.menu.video_adapter_menu)
                popupMenu.menu.findItem(R.id.menu_edit).icon = context.resources.getDrawable(R.drawable.ic_icon_edit)
                popupMenu.menu.findItem(R.id.menu_report).icon = context.resources.getDrawable(R.drawable.ic_icon_about_gray)
                popupMenu.menu.findItem(R.id.menu_video).icon = context.resources.getDrawable(R.drawable.ic_icon_close)
                popupMenu.menu.findItem(R.id.menu_offline_video).icon = context.resources.getDrawable(R.drawable.ic_icon_delete_gray)
                popupMenu.menu.findItem(R.id.menu_download).icon = context.resources.getDrawable(R.drawable.ic_icon_download)
                popupMenu.menu.findItem(R.id.menu_open).icon = context.resources.getDrawable(R.drawable.ic_icon_download)

                popupMenu.menu.findItem(R.id.menu_edit).isVisible = false
                popupMenu.menu.findItem(R.id.menu_report).isVisible = false
                popupMenu.menu.findItem(R.id.menu_video).isVisible = false
                popupMenu.menu.findItem(R.id.menu_offline_video).isVisible = false


                val file = File(Utils.getRootDirPath(context)!!)
                if (!file.exists()) {
                    file.mkdirs()
                } else {
                    Log.i(TAG, "Already existing")
                }

                val check = File(file.path +"/catch/staff/Assignment/"+ staffAttachmentList[position].fILENAME)
                Log.i(TAG,"check $check")
                if (!check.isFile) {
                    popupMenu.menu.findItem(R.id.menu_download).isVisible = true
                    popupMenu.menu.findItem(R.id.menu_open).isVisible = false
                }else{
                    popupMenu.menu.findItem(R.id.menu_download).isVisible = false
                    popupMenu.menu.findItem(R.id.menu_open).isVisible = true
                }

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_download -> {
//                            materialDetailsListener.onUpdateClickListener(materialList[position])
                            if(assignDetailsTabFragment.requestPermission()) {
//                                holder.constraintDownload.visibility = View.VISIBLE
//                                holder.textViewPercentage.visibility = View.VISIBLE
                                if (StatusD.RUNNING === PRDownloader.getStatus(position)) {
                                    PRDownloader.pause(position)
                                }

                                if (StatusD.PAUSED === PRDownloader.getStatus(position)) {
                                    PRDownloader.resume(position)
                                }
                                downLoadPos = position

                                var fileUrl = Global.event_url+"/Upload/VMailFile/" + staffAttachmentList[position].fILENAME
                                var fileLocation = Utils.getRootDirPath(context) +"/catch/staff/Assignment/"
                                //var fileLocation = Environment.getExternalStorageDirectory().absolutePath + "/Passdaily/File/"
                                var fileName = staffAttachmentList[position].fILENAME
                                Log.i(TAG, "fileUrl $fileUrl")
                                Log.i(TAG, "fileLocation $fileLocation")
                                Log.i(TAG, "fileName $fileName")

                                downLoadPos = PRDownloader.download(
                                    fileUrl, fileLocation, fileName
                                )
                                    .build()
                                    .setOnStartOrResumeListener {
                                        //  holder.imageViewClose.visibility = View.VISIBLE
                                    }
                                    .setOnPauseListener {
                                        //   holder.imageViewClose.visibility = View.VISIBLE
                                    }
                                    .setOnCancelListener {
                                        //  holder.imageViewClose.visibility = View.VISIBLE
                                        downLoadPos = 0
                                        //    holder.constraintDownload.visibility = View.GONE
                                        //   holder.textViewPercentage.text ="Loading... "
                                        //  holder.imageViewClose.visibility = View.GONE
                                        holder.progressDialog.dismiss()
                                        Toast.makeText(context,"Download Cancelled",Toast.LENGTH_LONG).show()
                                       // Utils.getSnackBar4K(context, "Download Cancelled", assignDetailsTabFragment.constraintFirstLayout!!)
                                        PRDownloader.cancel(downLoadPos)

                                    }
                                    .setOnProgressListener { progress ->
                                        val progressPercent: Long =
                                            progress.currentBytes * 100 / progress.totalBytes
//                                        holder.textViewPercentage.text = Utils.getProgressDisplayLine(
//                                            progress.currentBytes,
//                                            progress.totalBytes
//                                        )
                                        holder.progressDialog.setMessage("Downloading : ${Utils.getProgressDisplayLine(
                                            progress.currentBytes, progress.totalBytes)}")
                                        holder.progressDialog.show()
                                    }
                                    .start(object : OnDownloadListener {
                                        override fun onDownloadComplete() {
//                                            holder.constraintDownload.visibility = View.GONE
//                                            holder.textViewPercentage.visibility = View.GONE
                                            holder.progressDialog.dismiss()
                                            Toast.makeText(context,"Download Completed",Toast.LENGTH_LONG).show()
                                         //   Utils.getSnackBarGreen(context, "Download Completed", assignDetailsTabFragment.constraintFirstLayout!!)
                                        }

                                        override fun onError(error: Error) {
                                            Log.i(TAG, "Error $error")
                                            holder.progressDialog.dismiss()
                                            Toast.makeText(context,"Error While Download",Toast.LENGTH_LONG).show()
                                           // Utils.getSnackBar4K(context, "$error", assignDetailsTabFragment.constraintFirstLayout!!)
//                                            Toast.makeText(
//                                                context,
//                                                "Some Error occured",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                            holder.constraintDownload.visibility = View.GONE
//                                            holder.textViewPercentage.visibility = View.GONE
                                        }
                                    })

                            }
                            true
                        }
                        R.id.menu_open -> {
                            if(assignDetailsTabFragment.requestPermission()) {
                                assignmentClickListener.onOpenFileClick(staffAttachmentList[position].fILENAME)
                            }
                            true
                        }
                        else -> false
                    }

                }
                popupMenu.show()

            }


//            holder.imageView.setOnClickListener {
//                Global.albumImageList = ArrayList<CustomImageModel>()
//                Global.albumImageList.add(
//                    CustomImageModel(
//                        Global.event_url + "/AssignmentFile/"
//                                + staffAttachmentList[position].aSSIGNMENTFILE
//                    )
//                )
//                assignmentClickListener.onViewClick()
//            }
        }

        override fun getItemCount(): Int {
            return staffAttachmentList.size
        }

    }

    fun requestPermission(): Boolean {

        var hasReadPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        }else {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }


        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermission = hasReadPermission
        writePermission = hasWritePermission || minSdk29

        val permissions = readPermission && writePermission


        val permissionToRequests = mutableListOf<String>()
        if (!writePermission) {
            permissionToRequests.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!readPermission) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_IMAGES)
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_VIDEO)
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_AUDIO)
            }else {
                permissionToRequests.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionToRequests.isNotEmpty()) {
            permissionsLauncher.launch(permissionToRequests.toTypedArray())
        }

        return permissions
    }


    override fun onOpenFileClick(fILEPATHName: String) {
        Log.i(TAG, "fILEPATHName $fILEPATHName")

        // Create URI
        var dwload =
            File(Utils.getRootDirPath(requireActivity())!!+"/catch/staff/Assignment/")

        if (!dwload.exists()) {
            dwload.mkdirs()
        } else {
            //  Toast.makeText(getActivity(), "Already existing", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Already existing")
        }

        val mFile = File("$dwload/$fILEPATHName")
        var pdfURI = FileProvider.getUriForFile(requireActivity(), requireActivity().packageName + ".provider", mFile)
        Log.i(TAG, "file $mFile")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = pdfURI
        try {
            if (mFile.toString().contains(".doc") || mFile.toString().contains(".DOC")) {
                // Word document
                intent.setDataAndType(pdfURI, "application/msword")
            } else if (mFile.toString().contains(".pdf") || mFile.toString().contains(".PDF")) {
                // PDF file
                intent.setDataAndType(pdfURI, "application/pdf")
            } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")
            ) {
                // Powerpoint file
                intent.setDataAndType(pdfURI, "application/vnd.ms-powerpoint")
            } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
            ) {
                // Excel file
                intent.setDataAndType(pdfURI, "application/vnd.ms-excel")
            } else if (mFile.toString().contains(".docx") || mFile.toString().contains(".DOCX")) {
                // docx file
                intent.setDataAndType(
                    pdfURI,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                || mFile.toString().contains(".png") || mFile.toString()
                    .contains(".JPG") || mFile.toString().contains(".JPEG")
                || mFile.toString().contains(".PNG")
            ) {
                // JPG file
                intent.setDataAndType(pdfURI, "image/*")
            } else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                // Text file
                intent.setDataAndType(pdfURI, "text/plain")
            } else if (mFile.toString().contains(".mp3") || mFile.toString()
                    .contains(".wav") || mFile.toString().contains(".ogg")
                || mFile.toString().contains(".m4a") || mFile.toString()
                    .contains(".aac") || mFile.toString().contains(".wma") ||
                mFile.toString().contains(".MP3") || mFile.toString()
                    .contains(".WAV") || mFile.toString().contains(".OGG")
                || mFile.toString().contains(".M4A") || mFile.toString()
                    .contains(".AAC") || mFile.toString().contains(".WMA")
            ) {
                intent.setDataAndType(pdfURI, "audio/*")

            } else if (mFile.toString().contains(".MP4") || mFile.toString().contains(".MOV")
                || mFile.toString().contains(".WMV") || mFile.toString()
                    .contains(".AVI") || mFile.toString().contains(".AVCHD") || mFile.toString()
                    .contains(".FLV")
                || mFile.toString().contains(".F4V")
                || mFile.toString().contains(".SWF")
                || mFile.toString().contains(".WEBM")
                || mFile.toString().contains(".HTML5")
                || mFile.toString().contains(".MKV")
            ) {
                // JPG file
                intent.setDataAndType(pdfURI, "video/*")
            }
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(intent)
        }catch(e : Exception){
            Log.i(TAG, "exception $e");
            Toast.makeText(context,"File format doesn't support",Toast.LENGTH_LONG).show()
//            Utils.getSnackBar4K(requireActivity(),"File format doesn't support",constraintFirstLayout!!)
        }
    }


}


interface InboxClickListener {

    fun onViewClick(item: NotificationFragment.GeneralItem, position: Int)
}

interface NotificationClickListener {

    fun onOpenFileClick(fILEPATHName: String)
}


