package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.inbox

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
import info.passdaily.camrelconvertapp.databinding.FragmentAttendedTabBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.downloader.Error
import info.passdaily.camrelconvertapp.services.downloader.OnDownloadListener
import info.passdaily.camrelconvertapp.services.downloader.PRDownloader
import info.passdaily.camrelconvertapp.services.downloader.StatusD
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.parent.MainActivityParent
import info.passdaily.camrelconvertapp.typeofuser.parent.notification.NotificationClickListener
import info.passdaily.camrelconvertapp.typeofuser.parent.notification.NotificationFragment
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import java.io.File


@Suppress("DEPRECATION")
class InboxStaffFragment : Fragment(),InboxStaffClickListener,NotificationStaffClickListener {

    var TAG = "InboxStaffFragment"
    var toolBarClickListener : ToolBarClickListener? = null

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var staffId = 0
    var recyclerView : RecyclerView? = null

    private var _binding: FragmentAttendedTabBinding? = null
    private val binding get() = _binding!!
    var inboxList = ArrayList<InboxListStaffModel.Inbox>()

    private lateinit var inboxStaffViewModel: InboxStaffViewModel
    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null


    private var readPermission = false
    private var writePermission = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>


    var  staffAttachmentList = ArrayList<InboxDetailsStaffNewModel.FileDetail>()

    var recyclerViewStaffList: RecyclerView? = null
    var textViewNoFilesStudent: TextView? = null

    lateinit var mAdapter : InboxAdapter
    var mContext : Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(mContext ==null){
            mContext = context.applicationContext
        }
        try {
            toolBarClickListener = context as ToolBarClickListener
        }catch(e : Exception){
            Log.i(TAG,"Exception $e")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Inbox")

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        staffId = user[0].STAFF_ID

        // Inflate the layout for this fragment
        inboxStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[InboxStaffViewModel::class.java]
        // Inflate the layout for this fragment
        _binding = FragmentAttendedTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        shimmerViewContainer = binding.shimmerViewContainer

        recyclerView = binding.recyclerView
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())


        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }

        initFunction()
    }


    private fun initFunction() {
        inboxStaffViewModel.getInboxStaff(adminId,staffId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            inboxList = response.inboxList as ArrayList<InboxListStaffModel.Inbox>

                            if(inboxList.isNotEmpty()){
                                recyclerView?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    mAdapter =  InboxAdapter(
                                        this,
                                        inboxList,
                                        requireActivity(),
                                        TAG
                                    )
                                    recyclerView?.adapter = mAdapter
                                }
                            }else{
                                recyclerView?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_state_notification)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }
                            Log.i(TAG,"getMeetingAttendedReport SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerView?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getMeetingAttendedReport ERROR")
                        }
                        Status.LOADING -> {
                            recyclerView?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            inboxList = ArrayList<InboxListStaffModel.Inbox>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_state_notification)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"getMeetingAttendedReport LOADING")
                        }
                    }
                }
            })

    }

    class InboxAdapter(var inboxStaffClickListener: InboxStaffClickListener,
                       var inboxList: ArrayList<InboxListStaffModel.Inbox>,
                       var context: Context, var TAG: String) : RecyclerView.Adapter<InboxAdapter.ViewHolder>() {
        var firstLetter = ""
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewTitle: TextView = view.findViewById(R.id.textViewClass)
            var textViewDate: TextView = view.findViewById(R.id.textViewDate)
            var textViewDesc: TextView = view.findViewById(R.id.textViewTitle)
//            var textDescription: TextView = view.findViewById(R.id.textDescription)
            var textFirstLetter: TextView = view.findViewById(R.id.textFirstLetter)
            var textTotalFiles :TextView = view.findViewById(R.id.textTotalFiles)

        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.inbox_staff_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewTitle.text = inboxList[position].vIRTUALMAILTITLE

            holder.textTotalFiles.isVisible = false
            if(inboxList[position].fILECOUNT > 0){
                holder.textTotalFiles.isVisible = true
                holder.textTotalFiles.text = "File Count ${inboxList[position].fILECOUNT}"
            }

            if(!inboxList[position].vIRTUALMAILSENTDATE.isNullOrBlank()) {
                val date: Array<String> =
                    inboxList[position].vIRTUALMAILSENTDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                holder.textViewDate.text = Utils.formattedDate(dddd)
            }
            holder.textViewDesc.text = inboxList[position].vIRTUALMAILCONTENT

            firstLetter = if(inboxList[position].vIRTUALMAILTITLE.isNotEmpty()){
                 inboxList[position].vIRTUALMAILTITLE.substring(0, 1)
            }else{
                "A"
            }
            holder.textFirstLetter.text = firstLetter

            val inboxCount = inboxList[position].vIRTUALMAILREADSTATUS.toString()
            if (inboxCount == "0" && !inboxCount.isNullOrBlank()) {
                val typeface = ResourcesCompat.getFont(context, R.font.poppins_bold)
                holder.textViewDate.typeface = typeface
                holder.textViewTitle.typeface = typeface
                holder.textViewDesc.typeface = typeface
            }else if (inboxCount == "1" && !inboxCount.isNullOrBlank()) {
                val typefaceD = ResourcesCompat.getFont(context, R.font.poppins_regular)
                holder.textViewDate.typeface = typefaceD
                holder.textViewDesc.typeface = typefaceD
                val typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
                holder.textViewTitle.typeface = typeface

            }

            holder.itemView.setOnClickListener {
                inboxStaffClickListener.onViewClick(inboxList[position],position)
            }
        }

        override fun getItemCount(): Int {
           return inboxList.size
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewClick(inboxList: InboxListStaffModel.Inbox, position: Int) {
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
        inboxReadStatus(adminId,staffId,inboxList.vIRTUALMAILSENTSTAFFID)
        if(!inboxList.vIRTUALMAILSENTDATE.isNullOrBlank()) {
            val date1: Array<String> = inboxList.vIRTUALMAILSENTDATE.split("T".toRegex()).toTypedArray()
            val sendStr = Utils.longconversion(date1[0] + " " + date1[1])
            sentDate = Utils.formattedDateTime(sendStr)
        }
        textViewDate.text = sentDate
        textViewTitle.text = inboxList.vIRTUALMAILTITLE
        textViewDesc.text = inboxList.vIRTUALMAILCONTENT

        recyclerViewStaffList = dialog.findViewById<RecyclerView>(R.id.recyclerViewStaffList)
        recyclerViewStaffList?.layoutManager = GridLayoutManager(requireActivity(), 4)
        textViewNoFilesStudent = dialog.findViewById<TextView>(R.id.textViewNoFilesStudent)

        //?InboxId=716&AdminId=1&StaffId=1
        inboxDetails(inboxList.vIRTUALMAILSENTSTAFFID,adminId,staffId);

        imageViewClose.setOnClickListener {

            if (inboxList.vIRTUALMAILREADSTATUS == 0) {
                val total: Int = Integer.valueOf(Global.inboxcount) - 1
                Log.i(TAG, "Total$total")
              //  Global.inboxcount = total
               // (activity as MainActivityParent?)!!.setInboxcount()

                //inboxlist = new ArrayList<HashMap<String, String>>();
                inboxList.vIRTUALMAILREADSTATUS = 1
                mAdapter.notifyDataSetChanged()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun inboxReadStatus(adminId: Int,staffId: Int, vIRTUALMAILSENTSTAFFID: Int) {
//StaffInbox/StaffInboxReadById?InboxId=
        inboxStaffViewModel.getInboxReadById(vIRTUALMAILSENTSTAFFID,adminId, staffId)
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



    private fun inboxDetails(vIRTUALMAILSENTSTAFFID : Int,adminId: Int,staffId :Int) {

        inboxStaffViewModel.getStaffInboxViewById(vIRTUALMAILSENTSTAFFID,adminId,staffId)
            .observe(this, Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            staffAttachmentList = response.fileDetails as ArrayList<InboxDetailsStaffNewModel.FileDetail>

                            if(staffAttachmentList.isNotEmpty()){
                                recyclerViewStaffList?.visibility = View.VISIBLE
                                textViewNoFilesStudent?.visibility = View.GONE

                                recyclerViewStaffList?.adapter =
                                    StaffAttachmentListAdapter(
                                        this,
                                        this@InboxStaffFragment,
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
                            staffAttachmentList = ArrayList<InboxDetailsStaffNewModel.FileDetail>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })

    }



    class StaffAttachmentListAdapter(
        var assignmentClickListener: NotificationStaffClickListener,
        var assignDetailsTabFragment: InboxStaffFragment,
        var staffAttachmentList: ArrayList<InboxDetailsStaffNewModel.FileDetail>,
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
                                        Toast.makeText(context,"Download Cancelled", Toast.LENGTH_LONG).show()
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
                                            Toast.makeText(context,"Download Completed", Toast.LENGTH_LONG).show()
                                            //   Utils.getSnackBarGreen(context, "Download Completed", assignDetailsTabFragment.constraintFirstLayout!!)
                                        }

                                        override fun onError(error: Error) {
                                            Log.i(TAG, "Error $error")
                                            holder.progressDialog.dismiss()
                                            Toast.makeText(context,"Error While Download", Toast.LENGTH_LONG).show()
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
            Toast.makeText(context,"File format doesn't support", Toast.LENGTH_LONG).show()
//            Utils.getSnackBar4K(requireActivity(),"File format doesn't support",constraintFirstLayout!!)
        }
    }

}
interface InboxStaffClickListener {

    fun onViewClick(inboxList: InboxListStaffModel.Inbox, position: Int)
}


interface NotificationStaffClickListener {

    fun onOpenFileClick(fILEPATHName: String)
}
