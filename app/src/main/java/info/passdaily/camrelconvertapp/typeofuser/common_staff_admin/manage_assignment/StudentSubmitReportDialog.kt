package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogStudentSubmitReportBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.downloader.Error
import info.passdaily.camrelconvertapp.services.downloader.OnDownloadListener
import info.passdaily.camrelconvertapp.services.downloader.PRDownloader
import info.passdaily.camrelconvertapp.services.downloader.StatusD
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File

@SuppressLint("SetTextI18n")
@Suppress("DEPRECATION")
class StudentSubmitReportDialog : DialogFragment {

    lateinit var assignmentClickListener: AssignmentClickListener
    private var _binding: DialogStudentSubmitReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var assignmentStaffViewModel: AssignmentStaffViewModel

    var toolbar: Toolbar? = null

    companion object {
        var TAG = "StudentSubmitReportDialog"
    }

    lateinit var submittedDetail: AssignmentDetailsStaffModel.SubmittedDetail

    var constraintFirstLayout : ConstraintLayout? = null
    var textStudentName : TextView? = null
    var textViewSubject : TextView? = null
    var textViewClass : TextView? = null
    var textViewTitle : TextView? = null
    var textViewDesc : ShowMoreTextView? = null
    var textViewDate : TextView? = null
    var textOutOffMark : TextView? = null

    var recyclerView : RecyclerView? = null

    var editTextTitle : TextInputEditText? = null
    var editTextComment : TextInputEditText? = null

    var buttonClose : AppCompatButton? = null
    var buttonSubmit : AppCompatButton? = null

    var studentAttachmentList = ArrayList<StudentSubmissionDetailsModel.StudentAttachment>()

    var outOffMarks = 0
    var enterMark = 0

    private var readPermission = false
    private var writePermission = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var studentSubmittedDetails: StudentSubmissionDetailsModel.StudentSubmittedDetails
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleWhite)
    }

    constructor(
        assignmentClickListener: AssignmentClickListener,
        submittedDetail: AssignmentDetailsStaffModel.SubmittedDetail
    ) {
        this.assignmentClickListener = assignmentClickListener
        this.submittedDetail = submittedDetail
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        assignmentStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[AssignmentStaffViewModel::class.java]

        _binding = DialogStudentSubmitReportBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_black)
        toolbar?.title = "Report Submitted Student"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.black))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }

        constraintFirstLayout  = binding.constraintLayoutContent
        textStudentName = binding.textStudentName
        textViewSubject = binding.textViewSubject
        textViewClass = binding.textViewClass
        textViewTitle = binding.textViewTitle
        textViewDesc = binding.textViewDesc

        textViewDate= binding.textViewDate

        textOutOffMark= binding.textOutOffMark
        recyclerView = binding.recyclerView
        recyclerView?.layoutManager = GridLayoutManager(requireActivity(), 4)

        editTextTitle = binding.editTextTitle
        editTextComment = binding.editTextComment

        buttonClose = binding.buttonClose
        buttonClose?.setOnClickListener {
            cancelFrg()
        }
        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.setOnClickListener {
            enterMark = if(editTextTitle?.text.toString().isNotEmpty()){
                editTextTitle?.text.toString().toInt()
            }else{
                0
            }
            Log.i(TAG,"outOffMarks $outOffMarks")
            Log.i(TAG,"editTextTitle ${editTextTitle?.text.toString()}")
            if(assignmentStaffViewModel.validateField(editTextTitle!!,
                    "Give Mark field cannot be empty",requireActivity(),binding.constraintLayoutContent)
                && assignmentStaffViewModel.validateField(editTextComment!!,
                    "Give comment field cannot be empty",requireActivity(),binding.constraintLayoutContent)
                && assignmentStaffViewModel.validateMark(enterMark,outOffMarks,
            "Given Mark is greater then out of mark",requireActivity(),binding.constraintLayoutContent)
             ){

                val builder = AlertDialog.Builder(context)
                builder.setMessage("Give comments ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->
                        verifiedFun()
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, _ -> //  Action for 'NO' Button
                        dialog.cancel()
                    }
                //Creating dialog box
                val alert = builder.create()
                //Setting the title manually
                alert.setTitle("Confirm")
                alert.show()
                val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                buttonbackground.setTextColor(Color.BLACK)
                val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                buttonbackground1.setTextColor(Color.BLACK)
            }
        }


        val constraintLayoutContent = binding.constraintLayoutContent
        constraintLayoutContent.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLayoutContent.windowToken, 0)
        }

        initFunction()

        Utils.setStatusBarColor(requireActivity())

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }
    }

    private fun verifiedFun() {
        // hashMap.put("ASSIGNMENT_SUBMIT_ID", ASSIGNMENT_SUBMIT_ID);
        //        hashMap.put("ASSIGNMENT_MARK", input_enter_mark.getText().toString());
        //        hashMap.put("ASSIGNMENT_REPLY", input_comments.getText().toString());
        val url = "Assignment/AssignmentVerified"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("ASSIGNMENT_SUBMIT_ID", studentSubmittedDetails.aSSIGNMENTSUBMITID)
            jsonObject.put("ASSIGNMENT_MARK", editTextTitle?.text.toString())
            jsonObject.put("ASSIGNMENT_REPLY", editTextComment?.text.toString())

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        assignmentStaffViewModel.getCommonPostFun(url,accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Assignment Verified Successfully", binding.constraintLayoutContent)
                                }
                                Utils.resultFun(response) == "FAIL" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Assignment Verification Failed", binding.constraintLayoutContent)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", binding.constraintLayoutContent)
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })

    }


    private fun initFunction() {
        assignmentStaffViewModel.getStudentSubmissionDetail(submittedDetail.aCCADEMICID,
            submittedDetail.sTUDENTID
            ,submittedDetail.aSSIGNMENTID,
            submittedDetail.aSSIGNMENTSUBMITID)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            studentSubmittedDetails = response.studentSubmittedDetails
                            textStudentName?.text = studentSubmittedDetails.sTUDENTNAME
                            textViewSubject?.text = "Subject : ${studentSubmittedDetails.sUBJECTNAME}"
                            textViewClass?.text = "Class : ${studentSubmittedDetails.cLASSNAME}"
                            textViewTitle?.text = studentSubmittedDetails.aSSIGNMENTNAME
                            textViewDesc?.text = studentSubmittedDetails.aSSIGNMENTDESCRIPTION
                            textViewDesc?.apply {
                                setShowingLine(5)
                                setShowMoreTextColor(resources.getColor(R.color.green_300))
                                setShowLessTextColor(resources.getColor(R.color.green_300))
                            }

                            outOffMarks = studentSubmittedDetails.aSSIGNMENTOUTOFFMARK
                            textOutOffMark?.text  = "Out Off Mark : ${studentSubmittedDetails.aSSIGNMENTOUTOFFMARK}"
                            if(studentSubmittedDetails.aSSIGNMENTMARK != 0){
                                editTextTitle?.setText(studentSubmittedDetails.aSSIGNMENTMARK.toString())
                            }
                            editTextComment?.setText(studentSubmittedDetails.aSSIGNMENTREPLY)

                            if (!studentSubmittedDetails.aSSIGNMENTSUBMITDATE.isNullOrBlank()) {
                                val date: Array<String> =
                                    studentSubmittedDetails.aSSIGNMENTSUBMITDATE.split("T".toRegex()).toTypedArray()
                                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                                textViewDate?.text = "Submitted Date : ${Utils.formattedDateWords(dddd)}"
                            }else{
                                textViewDate?.text = "Submitted Date : Not Yet"
                            }
                            studentAttachmentList = response.studentAttachmentList as ArrayList<StudentSubmissionDetailsModel.StudentAttachment>

                            recyclerView?.adapter =
                                StudentAttachmentListAdapter(
                                    assignmentClickListener,
                                    this@StudentSubmitReportDialog,
                                    studentAttachmentList, requireActivity()
                                )

                            binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
                                val rb = group.findViewById<View>(checkedId) as RadioButton
                                editTextComment?.setText(rb.text.toString())
                            }

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            studentAttachmentList = ArrayList<StudentSubmissionDetailsModel.StudentAttachment>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    class StudentAttachmentListAdapter(
        var assignmentClickListener: AssignmentClickListener,
        var studentSubmitReportDialog: StudentSubmitReportDialog,
        var staffAttachmentList: ArrayList<StudentSubmissionDetailsModel.StudentAttachment>,
        var context: Context)
        : RecyclerView.Adapter<StudentAttachmentListAdapter.ViewHolder>() {
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
            val path: String = staffAttachmentList[position].aSSIGNMENTFILE
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
                    .load(Global.event_url + "/AssignmentFile/" + path)
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
            }
            else {
//                holder.imageViewOther.visibility = View.VISIBLE
//                holder.imageView.visibility = View.GONE
//                Glide.with(context)
//                    .load(File(path))
//                    .apply(
//                        RequestOptions.centerCropTransform()
//                            .dontAnimate() //   .override(imageSize, imageSize)
//                            .placeholder(R.drawable.ic_file_video)
//                    )
//                    .thumbnail(0.5f)
//                    .into(holder.imageViewOther)


                try {
                    val thumb = ThumbnailUtils.createVideoThumbnail(
                        Global.event_url + "/AssignmentFile/" + path,
                        MediaStore.Images.Thumbnails.MINI_KIND
                    )
                    holder.imageViewOther.setImageBitmap(thumb)
                } catch (e: java.lang.Exception) {
                    Log.i("TAG", "Exception $e")
                }
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

                val check = File(file.path +"/catch/staff/Assignment/"+ staffAttachmentList[position].aSSIGNMENTFILE)
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
                            if(studentSubmitReportDialog.requestPermission()) {
//                                holder.constraintDownload.visibility = View.VISIBLE
//                                holder.textViewPercentage.visibility = View.VISIBLE
                                if (StatusD.RUNNING === PRDownloader.getStatus(position)) {
                                    PRDownloader.pause(position)
                                }

                                if (StatusD.PAUSED === PRDownloader.getStatus(position)) {
                                    PRDownloader.resume(position)
                                }
                                downLoadPos = position

                                var fileUrl = Global.event_url+"/AssignmentFile/" + staffAttachmentList[position].aSSIGNMENTFILE
                                var fileLocation = Utils.getRootDirPath(context) +"/catch/staff/Assignment/"
                                //var fileLocation = Environment.getExternalStorageDirectory().absolutePath + "/Passdaily/File/"
                                var fileName = staffAttachmentList[position].aSSIGNMENTFILE
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
                                        Utils.getSnackBar4K(context, "Download Cancelled", studentSubmitReportDialog.constraintFirstLayout!!)
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
                                            Utils.getSnackBarGreen(context, "Download Completed", studentSubmitReportDialog.constraintFirstLayout!!)

                                        }

                                        override fun onError(error: Error) {
                                            Log.i(TAG, "Error $error")
                                            holder.progressDialog.dismiss()
                                            Utils.getSnackBar4K(context, "$error", studentSubmitReportDialog.constraintFirstLayout!!)
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
                            if(studentSubmitReportDialog.requestPermission()) {
                                assignmentClickListener.onOpenFileClick(staffAttachmentList[position].aSSIGNMENTFILE)
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

    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }
    }
}