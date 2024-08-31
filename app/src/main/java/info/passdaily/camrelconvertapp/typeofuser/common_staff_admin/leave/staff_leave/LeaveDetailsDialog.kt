package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.leave.staff_leave

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogStaffLeaveDetailsBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.AssignmentDetailsStaffModel
import info.passdaily.camrelconvertapp.model.LeaveStaffDetailsModel
import info.passdaily.camrelconvertapp.model.LeaveStaffListModel
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
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.leave.LeaveViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image.SlideshowDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.ArrayList

class LeaveDetailsDialog : DialogFragment,LeaveDetailsClickListener {

    companion object {
        var TAG = "LeaveDetailsDialog"
    }
    lateinit  var leaveList: LeaveStaffListModel.Leave
    lateinit var staffDetailsListener: StaffDetailsListener

    private var readPermission = false
    private var writePermission = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var leaveViewModel: LeaveViewModel

    private var _binding: DialogStaffLeaveDetailsBinding? = null
    private val binding get() = _binding!!

    var toolbar : Toolbar? = null

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var adminRole = 0

    var textViewStaffName : TextView? = null
    var textViewMobile : TextView? = null
    var textViewApproveDate  : TextView? = null

    var imageViewCall : ImageView? = null

    var textViewFromDate : TextView? = null
    var textViewEndDate : TextView? = null
    var textViewTitle : TextView? = null
    var textViewDescription : TextView? = null

    var recyclerViewFiles :RecyclerView? = null
    var textViewNoFilesStudent : TextView? = null

    var editTextComments :TextInputEditText? = null

    var buttonRejected : AppCompatButton? = null
    var buttonApproved : AppCompatButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    constructor()
    constructor(
        staffDetailsListener: StaffDetailsListener,
        leaveList: LeaveStaffListModel.Leave
    ) {
        this.staffDetailsListener = staffDetailsListener
        this.leaveList = leaveList
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
        adminRole = user[0].ADMIN_ROLE
        schoolId = user[0].SCHOOL_ID

        // Inflate the layout for this fragment
        leaveViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[LeaveViewModel::class.java]

        _binding = DialogStaffLeaveDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_white)
        toolbar?.title = "Staff Leave Details"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.white))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }

        textViewStaffName = binding.textViewStaffName
        textViewMobile = binding.textViewMobile
        textViewApproveDate = binding.textViewApproveDate
        imageViewCall = binding.imageViewCall
        textViewFromDate = binding.textViewFromDate
        textViewEndDate =binding.textViewEndDate
        textViewTitle = binding.textViewTitle
        textViewDescription = binding.textViewDescription
        textViewNoFilesStudent = binding.textViewNoFilesStudent
        recyclerViewFiles = binding.recyclerViewFiles
        recyclerViewFiles?.layoutManager = GridLayoutManager(requireActivity(), 4)

        editTextComments = binding.editTextComments
        buttonRejected  = binding.buttonRejected
        buttonApproved  = binding.buttonApproved

        imageViewCall!!.setOnClickListener {
            try {
                var phone = leaveList.sTAFFPHONENUMBER
                val intent = Intent("android.intent.action.DIAL")
                intent.data = Uri.parse("tel:$phone")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                requireActivity().startActivity(intent)
            } catch (e: Exception) {
                Log.i(TAG, "Exception $e")
            }
        }

        if (leaveList.sTAFFIMAGE != "") {
            Glide.with(requireActivity()).load(
                Global.event_url + "/Photos/StaffImage/" + leaveList.sTAFFIMAGE
            ) //STAFF_IMAGE -> http://demo.passdaily.in/Photos/StaffImageA0D181192F902C6AE338BEDF36FC3251.jpg
                //STAFF_IMAGE -> 1A07304FC14301B29E49B4DA301B0EA5.png
                .apply(
                    RequestOptions.centerCropTransform()
                        .dontAnimate()
                        .placeholder(R.drawable.round_account_button_with_user_inside)
                )
                .thumbnail(0.5f)
                .into(binding.shapeImageView)
        }

        binding.shapeImageView.setOnClickListener {

            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_profile_view)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            dialog.window!!.attributes = lp

            var imageViewProfile = dialog.findViewById<ImageView>(R.id.imageViewProfile)
            Glide.with(requireActivity())
                .load(Global.staff_image_url+leaveList.sTAFFIMAGE)
                .into(imageViewProfile)

            dialog.show()
        }


        textViewStaffName!!.text = leaveList.sTAFFFNAME
        textViewMobile!!.text = leaveList.sTAFFPHONENUMBER



        if(!leaveList.lEAVEFROMDATE.isNullOrBlank()) {
            val date: Array<String> =
                leaveList.lEAVEFROMDATE.split("T".toRegex()).toTypedArray()
          //  val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
//                holder.textStartDate.text = "Start Date : ${Utils.formattedDateWords(dddd)}"
//                holder.textStartTime.text = "${Utils.formattedTime(dddd)}"
            textViewFromDate!!.text = "From : ${Utils.dateformat(date[0])}"
        }

        if(!leaveList.lEAVETODATE.isNullOrBlank()) {
            val date: Array<String> =
                leaveList.lEAVETODATE.split("T".toRegex()).toTypedArray()
         //   val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
//                holder.textStartDate.text = "Start Date : ${Utils.formattedDateWords(dddd)}"
//                holder.textStartTime.text = "${Utils.formattedTime(dddd)}"
            textViewEndDate!!.text = "To : ${Utils.dateformat(date[0])}"
        }

        textViewTitle!!.text = leaveList.lEAVESUBJECT
        textViewDescription!!.text = leaveList.lEAVEDESCRIPTION
        editTextComments?.setText(leaveList.lEAVEAPPROVEDREASON)

        if(!leaveList.lEAVESUBMITTEDDATE.isNullOrBlank()) {
            val date: Array<String> =
                leaveList.lEAVESUBMITTEDDATE.split("T".toRegex()).toTypedArray()
            //    val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
//                holder.textStartDate.text = "Start Date : ${Utils.formattedDateWords(dddd)}"
//                holder.textStartTime.text = "${Utils.formattedTime(dddd)}"
            textViewApproveDate!!.text = "Applied On : ${Utils.dateformat(date[0])}"
        }

        if(leaveList.lEAVESTATUS == 1 || leaveList.lEAVESTATUS == 3){
            editTextComments!!.isEnabled = false
            buttonRejected!!.visibility = View.GONE
            buttonApproved!!.visibility = View.GONE

            var query = leaveList.lEAVESTATUS
            when (query) {
//                0 -> {
//                    textViewApproveDate!!.text = "Waiting for Reply"
//                    textViewApproveDate!!.setTextColor(requireActivity().resources.getColor(R.color.blue_400))
//                    textViewApproveDate!!.background = resources.getDrawable(R.drawable.bg_square_gray)
//                }
                1 -> {
                    binding.textViewApproveStatus.text = "Leave Approved"
                    binding.textViewApproveStatus.setTextColor(requireActivity().resources.getColor(R.color.fresh_green_200))
                    binding.textViewApproveStatus.background = resources.getDrawable(R.drawable.bg_text_round_green)
                }
                3 -> {
                    binding.textViewApproveStatus.text = "Leave Rejected"
                    binding.textViewApproveStatus.setTextColor(requireActivity().resources.getColor(R.color.fresh_red_200))
                    binding.textViewApproveStatus.background = resources.getDrawable(R.drawable.bg_text_round_red)
                }
            }
       //     textViewApproveDate!!.text = "Applied On : ${Utils.dateformat(date[0])}"

//            buttonRejected!!.setBackgroundResource(R.drawable.round_gray500)
//            buttonRejected!!.setTextAppearance(
//                requireActivity(),
//                R.style.RoundedCornerButtonGray500
//            )
//            buttonRejected!!.text = requireActivity().resources.getString(R.string.reject)
//
//         //   buttonApproved!!.setBackgroundResource(R.drawable.round_fresh)
//            buttonApproved!!.setTextAppearance(
//                requireActivity(),
//                R.style.RoundedCornerButtonGreenFresh
//            )
//            buttonApproved!!.text = requireActivity().resources.getString(R.string.approved)
        }else{
            binding.textViewApproveStatus.text = "Waiting for Reply"
            binding.textViewApproveStatus.setTextColor(requireActivity().resources.getColor(R.color.blue_400))
            binding.textViewApproveStatus.background = resources.getDrawable(R.drawable.bg_text_round_blue)

        }
//        else if(leaveList.lEAVESTATUS == 3){
//            editTextComments!!.isEnabled = false
//            buttonRejected!!.visibility = View.GONE
//            buttonApproved!!.visibility = View.GONE

         //   buttonRejected!!.setBackgroundResource(R.drawable.round_red_fresh)
//            buttonRejected!!.setTextAppearance(
//                requireActivity(),
//                R.style.RoundedCornerButtonRedFresh
//            )
//            buttonRejected!!.text = requireActivity().resources.getString(R.string.rejected)
//
//
//            buttonApproved!!.setBackgroundResource(R.drawable.round_gray500)
//            buttonApproved!!.setTextAppearance(
//                requireActivity(),
//                R.style.RoundedCornerButtonGray500
//            )
//            buttonApproved!!.text = requireActivity().resources.getString(R.string.approve)
//        }

        buttonApproved!!.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure want to Approve ?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    //Staff/LeaveRequestDelete?LeaveId=100
                    approvedOrRejected(1)
                }
                .setNegativeButton(
                    "No"
                ) { dialog, _ -> //  Action for 'NO' Button
                    dialog.cancel()
                }
            //Creating dialog box
            val alert = builder.create()
            //Setting the title manually
            alert.setTitle("Approval")
            alert.show()
            val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            buttonbackground.setTextColor(Color.BLACK)
            val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            buttonbackground1.setTextColor(Color.BLACK)


        }

        buttonRejected!!.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure want to Reject ?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    //Staff/LeaveRequestDelete?LeaveId=100
                    approvedOrRejected(3)
                }
                .setNegativeButton(
                    "No"
                ) { dialog, _ -> //  Action for 'NO' Button
                    dialog.cancel()
                }
            //Creating dialog box
            val alert = builder.create()
            //Setting the title manually
            alert.setTitle("Reject")
            alert.show()
            val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            buttonbackground.setTextColor(Color.BLACK)
            val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            buttonbackground1.setTextColor(Color.BLACK)

        }


        initFunction()
    }


    private fun approvedOrRejected(status: Int) {
//URL: http://meridianstaff.passdaily.in/ElixirApi/Staff/StaffLeaveAproveReject
//
//This For Approval
//{
//    "LEAVE_ID": 4,
//
//    "LEAVE_STATUS": 1, ----- This value is 1 means this leave details approved
//
//    "LEAVE_APPROVED_REASON":"Too Much Leave Taking Person",
//
//    "LEAVE_APPROVED_BY": 1
//}

        val url = "Staff/StaffLeaveAproveReject"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("LEAVE_ID", leaveList.lEAVEID)
            jsonObject.put("LEAVE_STATUS", status)
            jsonObject.put("LEAVE_APPROVED_REASON", editTextComments?.text.toString())
            jsonObject.put("LEAVE_APPROVED_BY", adminId)
            // "LEAVE_ID":3
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG, "jsonObject $jsonObject")
        val accademicRe =
            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        leaveViewModel.getCommonPostFun(url, accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG, "resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                   // cancelFrg()
                                    buttonRejected!!.isEnabled = false
                                    buttonApproved!!.isEnabled = false

                                    if(status == 1){
                                        buttonRejected!!.setBackgroundResource(R.drawable.round_gray500)
                                        buttonRejected!!.setTextAppearance(
                                            requireActivity(),
                                            R.style.RoundedCornerButtonGray500
                                        )
                                        buttonRejected!!.text = requireActivity().resources.getString(R.string.reject)

                                   //     buttonApproved!!.setBackgroundResource(R.drawable.round_fresh)
                                        buttonApproved!!.setTextAppearance(
                                            requireActivity(),
                                            R.style.RoundedCornerButtonGreenFresh
                                        )
                                        buttonApproved!!.text = requireActivity().resources.getString(R.string.approved)
                                    }else if(status == 3){

                                //        buttonRejected!!.setBackgroundResource(R.drawable.round_red_fresh)
                                        buttonRejected!!.setTextAppearance(
                                            requireActivity(),
                                            R.style.RoundedCornerButtonRedFresh
                                        )
                                        buttonRejected!!.text = requireActivity().resources.getString(R.string.rejected)


                                        buttonApproved!!.setBackgroundResource(R.drawable.round_gray500)
                                        buttonApproved!!.setTextAppearance(
                                            requireActivity(),
                                            R.style.RoundedCornerButtonGray500
                                        )
                                        buttonApproved!!.text = requireActivity().resources.getString(R.string.approve)
                                    }

                                    staffDetailsListener.onCreateClick("")
                                    Utils.getSnackBarGreen(
                                        requireActivity(),
                                        "Leave Request Replied Successfully",
                                        binding.constraintLeave
                                    )
                                }
                                Utils.resultFun(response) == "FAIL" -> {
                                    Utils.getSnackBar4K(
                                        requireActivity(),
                                        "Leave Request Reply Failed",
                                        binding.constraintLeave
                                    )
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(
                                requireActivity(),
                                "Please try again after sometime",
                                binding.constraintLeave
                            )
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG, "loading")
                        }
                    }
                }
            })

    }


    private fun initFunction() {
        leaveViewModel.getLeaveDetails(leaveList.lEAVEID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            var filesList= response.filesList as ArrayList<LeaveStaffDetailsModel.Files>
                            if(filesList.isNotEmpty()) {
                                recyclerViewFiles?.visibility = View.VISIBLE
                                textViewNoFilesStudent?.visibility = View.GONE
                                if (isAdded) {

                                    recyclerViewFiles?.adapter =
                                        StaffLeaveFilesAdapter(
                                            this,
                                            this@LeaveDetailsDialog,
                                            filesList, requireActivity(), TAG
                                        )

                                } else {
                                    recyclerViewFiles?.visibility = View.GONE
                                    textViewNoFilesStudent?.visibility = View.VISIBLE

                                }


                            }
                            Log.i(TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }


    class StaffLeaveFilesAdapter(
        var assignmentClickListener: LeaveDetailsClickListener,
        var leaveDetailsDialog: LeaveDetailsDialog,
        var staffAttachmentList: ArrayList<LeaveStaffDetailsModel.Files>,
        var context: Context, var TAG: String )
        : RecyclerView.Adapter<StaffLeaveFilesAdapter.ViewHolder>() {
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
                    .load(Global.event_url + "/Upload/StaffLeave/" + path)
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
                    .load(Global.event_url + "/Upload/StaffLeave/" + path)
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

                val check = File(file.path +"/catch/staff/Leave/"+ staffAttachmentList[position].fILENAME)
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
                            if(leaveDetailsDialog.requestPermission()) {
//                                holder.constraintDownload.visibility = View.VISIBLE
//                                holder.textViewPercentage.visibility = View.VISIBLE
                                if (StatusD.RUNNING === PRDownloader.getStatus(position)) {
                                    PRDownloader.pause(position)
                                }

                                if (StatusD.PAUSED === PRDownloader.getStatus(position)) {
                                    PRDownloader.resume(position)
                                }
                                downLoadPos = position

                                    //http://stm.passdaily.in/Upload/StaffLeave/qqq.jpg
                                var fileUrl = Global.event_url+"/Upload/StaffLeave/" + staffAttachmentList[position].fILENAME
                                var fileLocation = Utils.getRootDirPath(context) +"/catch/staff/Leave/"
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
                                        Utils.getSnackBar4K(context, "Download Cancelled", leaveDetailsDialog.binding.constraintLeave)
                                        PRDownloader.cancel(downLoadPos)

                                    }
                                    .setOnProgressListener { progress ->
                                        val progressPercent: Long =
                                            progress.currentBytes * 100 / progress.totalBytes
//                                        holder.textViewPercentage.text = Utils.getProgressDisplayLine(
//                                            progress.currentBytes,
//                                            progress.totalBytes
//                                        )
                                        holder.progressDialog.setMessage("Downloading : ${
                                            Utils.getProgressDisplayLine(
                                            progress.currentBytes, progress.totalBytes)}")
                                        holder.progressDialog.show()
                                    }
                                    .start(object : OnDownloadListener {
                                        override fun onDownloadComplete() {
//                                            holder.constraintDownload.visibility = View.GONE
//                                            holder.textViewPercentage.visibility = View.GONE
                                            holder.progressDialog.dismiss()
                                            Utils.getSnackBarGreen(context, "Download Completed",leaveDetailsDialog.binding.constraintLeave)
                                        }

                                        override fun onError(error: Error) {
                                            Log.i(TAG, "Error $error")
                                            holder.progressDialog.dismiss()
                                            Utils.getSnackBar4K(context, "$error", leaveDetailsDialog.binding.constraintLeave)
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
                            if(leaveDetailsDialog.requestPermission()) {
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

    fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(LeaveDetailsDialog.TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }
    }

    override fun onViewClick() {
        val extra = Bundle()
        extra.putInt("position", 0)
        val ft: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        val newFragment = SlideshowDialogFragment.newInstance()
        newFragment.arguments = extra
        newFragment.show(ft, newFragment.TAG)
    }

    override fun onOpenFileClick(fILEPATHName: String) {
        Log.i(TAG, "fILEPATHName $fILEPATHName")

        // Create URI
        var dwload =
            File(Utils.getRootDirPath(requireActivity())!!+"/catch/staff/Leave/")

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
            Utils.getSnackBar4K(requireActivity(),"File format doesn't support",binding.constraintLeave)
        }
    }

    override fun onReportClick(submittedDetail: AssignmentDetailsStaffModel.SubmittedDetail) {
        Log.i(TAG, "submittedDetail")
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

}

interface LeaveDetailsClickListener {
    fun onViewClick()
    fun onOpenFileClick(fILEPATHName: String)
    fun onReportClick(submittedDetail: AssignmentDetailsStaffModel.SubmittedDetail)
}