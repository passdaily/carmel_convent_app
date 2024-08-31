package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentAssignDetailsTabBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.ShowMoreTextView
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.downloader.Error
import info.passdaily.camrelconvertapp.services.downloader.OnDownloadListener
import info.passdaily.camrelconvertapp.services.downloader.PRDownloader
import info.passdaily.camrelconvertapp.services.downloader.StatusD
import info.passdaily.camrelconvertapp.typeofuser.parent.assignment.AssignmentDetailActivity
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image.SlideshowDialogFragment
import java.io.File

@Suppress("DEPRECATION")
@SuppressLint("SetTextI18n")
class AssignDetailsTabFragment(var assignmentDetails: AssignmentDetailsStaffModel.AssignmentDetails,
                               var submittedDetails: ArrayList<AssignmentDetailsStaffModel.SubmittedDetail>,
                               var staffAttachmentList: ArrayList<AssignmentDetailsStaffModel.StaffAttachment>) : Fragment(),
    AssignmentClickListener {

    var TAG = "AssignDetailsTabFragment"
    private var _binding: FragmentAssignDetailsTabBinding? = null
    private val binding get() = _binding!!

    var constraintFirstLayout : ConstraintLayout? = null
    var textViewTitle: TextView? = null
    var textViewClass: TextView? = null
    var textSubject: TextView? = null
    var textStartDate: TextView? = null
    var textEndDate: TextView? = null
    var textOutOffMark: TextView? = null
    var textTotalSubmission: TextView? = null
    var textDescription: ShowMoreTextView? = null

    var recyclerViewStaffList: RecyclerView? = null

    var recyclerViewList : RecyclerView? = null

    private var readPermission = false
    private var writePermission = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentAssignDetailsTabBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraintFirstLayout = binding.constraintFirstLayout
        textViewTitle = binding.textViewTitle
        textViewClass = binding.textViewClass
        textSubject = binding.textSubject
        textStartDate = binding.textStartDate
        textEndDate = binding.textEndDate
        textOutOffMark = binding.textOutOffMark
        textTotalSubmission = binding.textTotalSubmission
        textDescription = binding.textDescription
        recyclerViewStaffList = binding.recyclerViewStaffList
        recyclerViewStaffList?.layoutManager = GridLayoutManager(requireActivity(), 4)

        recyclerViewList = binding.recyclerViewList
        recyclerViewList?.layoutManager = LinearLayoutManager(requireActivity())

        textViewTitle?.text = assignmentDetails.aSSIGNMENTNAME
        textViewClass?.text = "Class : ${assignmentDetails.cLASSNAME}"
        textSubject?.text = "Subject : ${assignmentDetails.sUBJECTNAME}"


        if (!assignmentDetails.sTARTDATE.isNullOrBlank()) {
            val date: Array<String> =
                assignmentDetails.sTARTDATE.split("T".toRegex()).toTypedArray()
            val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
            textStartDate?.text = "Start Date : ${Utils.formattedDateWords(dddd)}"
        }
        if (!assignmentDetails.eNDDATE.isNullOrBlank()) {
            val date: Array<String> = assignmentDetails.eNDDATE.split("T".toRegex()).toTypedArray()
            val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
            textEndDate?.text = "End Date : ${Utils.formattedDateWords(dddd)}"
        }

        textOutOffMark?.text = "Out Of Marks : ${assignmentDetails.aSSIGNMENTOUTOFFMARK}"
        textTotalSubmission?.text = "Total Submission : ${assignmentDetails.tOTALSUBMIT}"

        textDescription?.text = assignmentDetails.aSSIGNMENTDESCRIPTION
        textDescription?.apply {
            setShowingLine(5)
            setShowMoreTextColor(resources.getColor(R.color.green_300))
            setShowLessTextColor(resources.getColor(R.color.green_300))
        }

        recyclerViewStaffList?.adapter = StaffAttachmentListAdapter(this,
            this@AssignDetailsTabFragment,
            staffAttachmentList,requireActivity(),TAG)


        recyclerViewList?.adapter = SubmissionAdapter(this,
            submittedDetails,requireActivity())


        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }

    }

    class StaffAttachmentListAdapter(
        var assignmentClickListener: AssignmentClickListener,
        var assignDetailsTabFragment: AssignDetailsTabFragment,
        var staffAttachmentList: ArrayList<AssignmentDetailsStaffModel.StaffAttachment>,
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
            } else {
                holder.imageViewOther.visibility = View.VISIBLE
                holder.imageView.visibility = View.GONE
                Glide.with(context)
                    // .load(java.io.File(path))
                    .load(Global.event_url + "/AssignmentFile/" + path)
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
                                        Utils.getSnackBar4K(context, "Download Cancelled", assignDetailsTabFragment.constraintFirstLayout!!)
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
                                            Utils.getSnackBarGreen(context, "Download Completed", assignDetailsTabFragment.constraintFirstLayout!!)
                                        }

                                        override fun onError(error: Error) {
                                            Log.i(TAG, "Error $error")
                                            holder.progressDialog.dismiss()
                                            Utils.getSnackBar4K(context, "$error", assignDetailsTabFragment.constraintFirstLayout!!)
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
            Utils.getSnackBar4K(requireActivity(),"File format doesn't support",constraintFirstLayout!!)
        }
    }

    override fun onReportClick(submittedDetail: AssignmentDetailsStaffModel.SubmittedDetail) {
        val dialog1 = StudentSubmitReportDialog(this,submittedDetail)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.show(transaction, StudentSubmitReportDialog.TAG)

    }


    class SubmissionAdapter(
        var assignmentClickListener: AssignmentClickListener,
        var submittedDetails: ArrayList<AssignmentDetailsStaffModel.SubmittedDetail>,
        var context: Context)
        : RecyclerView.Adapter<SubmissionAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textStudent: TextView = view.findViewById(R.id.textStudent)
            var textSubmitDate: TextView = view.findViewById(R.id.textSubmitDate)
            var textTotalFiles: TextView = view.findViewById(R.id.textTotalFiles)
            var buttonReport : AppCompatButton = view.findViewById(R.id.buttonReport)
        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.submission_student_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.textStudent.text = submittedDetails[position].sTUDENTNAME
            if (!submittedDetails[position].aSSIGNMENTSUBMITDATE.isNullOrBlank()) {
                val date: Array<String> =
                    submittedDetails[position].aSSIGNMENTSUBMITDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                holder.textSubmitDate.text = "Submitted Date : ${Utils.formattedDateWords(dddd)}"
            }
            holder.textTotalFiles.text = "Total File : ${submittedDetails[position].tOTALFILES}"

            holder.buttonReport.setOnClickListener {
                assignmentClickListener.onReportClick(submittedDetails[position])
            }
        }

        override fun getItemCount(): Int {
            return submittedDetails.size
        }

    }


}

interface AssignmentClickListener {
    fun onViewClick()
    fun onOpenFileClick(fILEPATHName: String)
    fun onReportClick(submittedDetail: AssignmentDetailsStaffModel.SubmittedDetail)
}
