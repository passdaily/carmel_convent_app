package info.passdaily.camrelconvertapp.typeofuser.parent.online_video

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentOnlineVideoBinding
import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
import info.passdaily.camrelconvertapp.lib.video.Video_Activity
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
import info.passdaily.camrelconvertapp.services.localDB.parent.OfflineStoreDbHelper
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import java.io.File


@Suppress("DEPRECATION")
class OnlineVideoFragment : Fragment(),VideoClickListner {

    var TAG = "OnlineVideoFragment"
    private lateinit var onlineVideoViewModel: OnlineVideoViewModel
    private var _binding: FragmentOnlineVideoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var recyclerViewLive: RecyclerView? = null

    var subjectSpinner: AppCompatSpinner? = null
    var chapterSpinner: AppCompatSpinner? = null
    var chapterListModel = ArrayList<ChapterListModel.Chapter>()
    var subjectListModel = ArrayList<SubjectsModel.Subject>()

    var aCCADEMIC_ID = 0
    var cLASS_ID = 0
    var STUDENT_ID = 0
    var subjectId = 0
    var chapterId = 0

    var constraintEmpty: ConstraintLayout? =null
    var imageViewEmpty: ImageView? =null
    var textEmpty : TextView? =null

    var mContext : Context? =null

    private var readPermission = false
    private var writePermission = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG,"onAttach ")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.currentPage = 2
        Global.screenState = "landingpage"
        val studentDBHelper = StudentDBHelper(requireActivity())
        var student2 = studentDBHelper.getStudentById(Global.studentId)
       // var student2 = studentDBHelper.getCountry(Global.studentId)
        Log.i(TAG, "student " + student2.STUDENT_ID)
        aCCADEMIC_ID = student2.ACCADEMIC_ID
        cLASS_ID = student2.CLASS_ID

        STUDENT_ID = student2.STUDENT_ID

        Log.i(TAG, "Global.studentId " + Global.studentId)
        Log.i(TAG, "STUDENT_ID " + student2.STUDENT_ID)
        Log.i(TAG, "ACCADEMIC_ID " + student2.ACCADEMIC_ID)
        Log.i(TAG, "STUDENT_ROLL_NO " + student2.STUDENT_ROLL_NO)
        Log.i(TAG, "CLASS_ID " + student2.CLASS_ID)

//        for(i in studentDBHelper.viewUser().indices){
//            Log.i(TAG,"viewUser STUDENT_ID "+studentDBHelper.viewUser()[i].STUDENT_ID)
//            Log.i(TAG,"viewUser ACCADEMIC_ID "+studentDBHelper.viewUser()[i].ACCADEMIC_ID)
//            Log.i(TAG,"viewUser CLASS_ID "+studentDBHelper.viewUser()[i].CLASS_ID)
//        }


        onlineVideoViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[OnlineVideoViewModel::class.java]

//        onlineVideoViewModel = ViewModelProvider(this)[OnlineVideoViewModel::class.java]
//        onlineVideoViewModel.getSubjects(student.CLASS_ID, Global.studentId)


        _binding = FragmentOnlineVideoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        subjectSpinner = binding.subjectSpinner
        chapterSpinner = binding.chapterSpinner

        recyclerViewLive = binding.recyclerViewLive
        recyclerViewLive?.layoutManager = LinearLayoutManager(requireActivity())


        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(requireActivity())
            .load(R.drawable.ic_empty_state_video)
            .into(imageViewEmpty!!)
        textEmpty?.text = "No Video"

        intiFunction()

        subjectSpinner?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                subjectId = subjectListModel[position].sUBJECTID
                intiChapter(subjectId)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        chapterSpinner?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                chapterId = chapterListModel[position].cHAPTERID
                initMeth(subjectId,chapterId)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }

        return root
    }


    private fun initMeth(subjectId :  Int ,chapterId : Int) {
        onlineVideoViewModel.getOnlineVideo( aCCADEMIC_ID, cLASS_ID, subjectId, chapterId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            if(response.youtubeList.isNotEmpty()){
                                constraintEmpty?.visibility = View.GONE
                                recyclerViewLive?.visibility = View.VISIBLE
                                if(isAdded) {
                                    recyclerViewLive?.adapter =
                                        OnlineVideoListAdapter(
                                            this,
                                            response.youtubeList,
                                            mContext!!,
                                            TAG
                                        )
                                }
                            }else{
                                constraintEmpty?.visibility = View.VISIBLE
                                recyclerViewLive?.visibility = View.GONE

                                if (isAdded) {
                                    Glide.with(mContext!!)
                                        .load(R.drawable.ic_empty_state_video)
                                        .into(imageViewEmpty!!)
                                }
                                textEmpty?.text =   requireActivity().resources.getString(R.string.no_results)
                            }

                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewLive?.visibility = View.GONE

                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_no_internet)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text =   requireActivity().resources.getString(R.string.no_internet)

                            Log.i(TAG, "Status.ERROR ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_empty_state_video)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text =  requireActivity().resources.getString(R.string.loading)
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewLive?.visibility = View.GONE



                        }
                    }
                }
            })
    }

    private fun intiFunction() {

        subjectListModel = ArrayList<SubjectsModel.Subject>()
        onlineVideoViewModel.getSubjects(cLASS_ID, STUDENT_ID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!


                            subjectListModel = response.subjects as ArrayList<SubjectsModel.Subject>
                            var subject = Array(subjectListModel.size){""}
                            for (i in subjectListModel.indices) {
                                subject[i] = subjectListModel[i].sUBJECTNAME
                            }
                            if (subjectSpinner != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    subject
                                )
                                subjectSpinner?.adapter = adapter
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Status.ERROR ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "Status.LOADING ${Status.LOADING}")
                        }
                    }
                }
            })
    }

    private fun intiChapter(subjectId: Int) {
        chapterListModel = ArrayList<ChapterListModel.Chapter>()
        onlineVideoViewModel.getChapter(aCCADEMIC_ID,cLASS_ID, subjectId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            chapterListModel = response.chapterList
                            val chapter: ArrayList<String> = ArrayList<String>()
                            for (i in chapterListModel.indices) {
                                chapter.add(chapterListModel[i].cHAPTERNAME)
                            }
                            if (chapterSpinner != null) {
                                if(isAdded) {
                                    val adapter = ArrayAdapter(
                                        mContext!!,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        chapter
                                    )
                                    chapterSpinner?.adapter = adapter
                                }
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Status.ERROR ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "Status.LOADING ${Status.LOADING}")
                        }
                    }
                }
            })

    }


    class OnlineVideoListAdapter(
        var videoClickListner: VideoClickListner,
        var onlineVideo: List<YoutubeListModel.Youtube>,
        var context: Context,
        var TAG : String
    ) : RecyclerView.Adapter<OnlineVideoListAdapter.ViewHolder>() {

        var downLoadPos = 0
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
            var textViewDesc: TextView = view.findViewById(R.id.textViewDesc)

            //textViewDate
            var textViewClass: TextView = view.findViewById(R.id.textViewClass)
            var textViewSub: TextView = view.findViewById(R.id.textViewSub)
            var textViewDate: TextView = view.findViewById(R.id.textViewStatus)
            var cardViewStatus: CardView = view.findViewById(R.id.cardViewStatus)
            var imageSubject: ImageView = view.findViewById(R.id.imageView)

            var cardViewProgress : CardView = view.findViewById(R.id.cardViewProgress)

            var imageViewMore: ImageView = view.findViewById(R.id.imageViewMore)

            var buttonDownload : AppCompatButton = view.findViewById(R.id.buttonDownload)
            var buttonYouTube: AppCompatButton = view.findViewById(R.id.buttonYouTube)
            var offlineButton : AppCompatButton = view.findViewById(R.id.offlineButton)

            var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
            var imageViewClose: ImageView = view.findViewById(R.id.imageViewClose)
            var textDownloadProgress : TextView = view.findViewById(R.id.textDownloadProgress)

            var constraintStatus : ConstraintLayout = view.findViewById(R.id.constraintStatus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.online_video_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

            when (onlineVideo[position].sUBJECTICON) {
                "English.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_english)
                        .into(holder.imageSubject)
                }
                "Chemistry.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_chemistry)
                        .into(holder.imageSubject)
                }
                "Biology.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_biology)
                        .into(holder.imageSubject)
                }
                "Maths.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_maths)
                        .into(holder.imageSubject)
                }
                "Hindi.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_hindi)
                        .into(holder.imageSubject)
                }
                "Physics.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_physics)
                        .into(holder.imageSubject)
                }
                "Malayalam.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_malayalam)
                        .into(holder.imageSubject)
                }
                "Arabic.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_arabic)
                        .into(holder.imageSubject)
                }
                "Accountancy.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_accountancy)
                        .into(holder.imageSubject)
                }
                "Social.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_social)
                        .into(holder.imageSubject)
                }
                "Economics.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_economics)
                        .into(holder.imageSubject)
                }
                "BasicScience.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_biology)
                        .into(holder.imageSubject)
                }
                "Computer.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_computer)
                        .into(holder.imageSubject)
                }
                "General.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_vid_sub_computer)
                        .into(holder.imageSubject)
                }
            }
            holder.cardViewStatus.setCardBackgroundColor(Color.parseColor("#FFFFFF"));


            holder.textViewTitle.text = onlineVideo[position].yOUTUBETITLE
            holder.textViewDesc.text = onlineVideo[position].yOUTUBEDESCRIPTION

            holder.textViewSub.text = onlineVideo[position].sUBJECTNAME
            holder.textViewClass.text = onlineVideo[position].cLASSNAME
          //  holder.textViewDesc.text = onlineVideo[position].yOUTUBEDESCRIPTION

            if(!onlineVideo[position].yOUTUBEDATE.isNullOrBlank()) {
                val date: Array<String> =
                    onlineVideo[position].yOUTUBEDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                holder.textViewDate.text = Utils.formattedDate(dddd)
            }else{
                holder.textViewDate.text = "No Date"
            }


            if (onlineVideo[position].oRGINALFILE.isNotEmpty() && onlineVideo[position].oRGINALFILE != "0") {
                val file = File(Utils.getRootDirPath(context)!!)
                if (!file.exists()) {
                    file.mkdirs()
                } else {
                    Log.i(TAG, "Already existing")
                }
                val check = File(file.path +"/catch/data/encryp/pics/"+ onlineVideo[position].oRGINALFILE)
                if (!check.isFile) {
                    holder.imageViewMore.isEnabled = false
                    DrawableCompat.setTint(holder.imageViewMore.drawable, ContextCompat.getColor(context.applicationContext, R.color.gray_400))
                    holder.offlineButton.visibility = View.GONE
                    holder.buttonDownload.visibility = View.VISIBLE
                }else{
                    holder.imageViewMore.isEnabled = true
                    DrawableCompat.setTint(holder.imageViewMore.drawable, ContextCompat.getColor(context.applicationContext, R.color.black))
                    holder.offlineButton.visibility = View.VISIBLE
                    holder.buttonDownload.visibility = View.GONE
                }
            }else{
                holder.imageViewMore.isEnabled = false
                DrawableCompat.setTint(holder.imageViewMore.drawable, ContextCompat.getColor(context.applicationContext, R.color.gray_400))
                holder.offlineButton.visibility = View.GONE
                holder.buttonDownload.visibility = View.GONE
            }

            //
            if (onlineVideo[position].yOUTUBELINK != "0") {
                holder.buttonYouTube.visibility = View.VISIBLE
            } else {
                holder.buttonYouTube.visibility = View.GONE
            }

            holder.buttonYouTube.setOnClickListener {
                if(!Global.blockStatus) {
                    val vid = onlineVideo[position].yOUTUBELINK.split("=").toTypedArray()
                    //                    startActivity(new Intent(getActivity(),YouTube_SingleVideo.class));
//                    val intent = Intent(context, YouTubePlayerActivity::class.java)
//                    intent.putExtra("youTubeLink", vid[1])
//                    intent.putExtra("YOUTUBE_ID", onlineVideo[position].yOUTUBEID)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent)

                    val intent = Intent(context, YouTubeIframePlayer::class.java)
                    intent.putExtra("youTubeLink", vid[1])
                    intent.putExtra("YOUTUBE_ID", onlineVideo[position].yOUTUBEID)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent)
                }
            }

            holder.offlineButton.setOnClickListener {
                if(!Global.blockStatus) {
                    val intent = Intent(context, ExoPlayerActivity::class.java)
                    intent.putExtra("ALBUM_TITLE", "")
                    intent.putExtra("ALBUM_FILE", Utils.getRootDirPath(context) +"/catch/data/encryp/pics/"
                            + onlineVideo[position].oRGINALFILE)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent)
                }
            }


            Log.i(TAG," youtubeId ${onlineVideo[position].yOUTUBEID} original ${onlineVideo[position].oRGINALFILE}")

            holder.buttonDownload.setOnClickListener {

                if(videoClickListner.requestPermission()) {
                    holder.constraintStatus.setBackgroundResource(R.color.trans);
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.gray_200));
                    holder.cardViewProgress.visibility = View.VISIBLE

                    if (StatusD.RUNNING === PRDownloader.getStatus(position)) {
                        PRDownloader.pause(position)
                    }

                    if (StatusD.PAUSED === PRDownloader.getStatus(position)) {
                        PRDownloader.resume(position)
                    }
                    downLoadPos = position
                    var fileUrl = Global.event_url+"/YoutubeOGFile/"+ onlineVideo[position].oRGINALFILE
                    var fileLocation = Utils.getRootDirPath(context) +"/catch/data/encryp/pics/"

                    var fileName = onlineVideo[position].oRGINALFILE
                    Log.i(TAG, "fileUrl $fileUrl")
                    Log.i(TAG, "fileLocation $fileLocation")
                    Log.i(TAG, "fileName $fileName")

                    downLoadPos = PRDownloader.download(
                        fileUrl, fileLocation, fileName
                    )
                        .build()
                        .setOnStartOrResumeListener {
                            holder.imageViewClose.visibility = View.VISIBLE
                        }
                        .setOnPauseListener {
                            holder.imageViewClose.visibility = View.VISIBLE
                        }
                        .setOnCancelListener {
                            holder.imageViewClose.visibility = View.VISIBLE
                            downLoadPos = 0
//                            holder.progressBar.visibility = View.GONE
//                            holder.textDownloadProgress.visibility = View.GONE
//                            holder.imageViewClose.visibility = View.GONE
                            PRDownloader.cancel(downLoadPos)

                        }
                        .setOnProgressListener { progress ->
                            val progressPercent: Long =
                                progress.currentBytes * 100 / progress.totalBytes
                            holder.textDownloadProgress.text = Utils.getProgressDisplayLine(
                                progress.currentBytes,
                                progress.totalBytes
                            )
                        }
                        .start(object : OnDownloadListener {
                            override fun onDownloadComplete() {
                                holder.constraintStatus.setBackgroundResource(R.color.white);
                                holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.white));
                                holder.cardViewProgress.visibility = View.GONE
                                holder.offlineButton.visibility = View.VISIBLE
                                holder.buttonDownload.visibility = View.GONE


                                val offlineStoreDbHelper = OfflineStoreDbHelper(context)
                                offlineStoreDbHelper.insert(
                                    OfflineStoreDbHelper.OfflineModel( onlineVideo[position].yOUTUBEID, Global.studentName
                                        , onlineVideo[position].cLASSNAME, onlineVideo[position].sUBJECTNAME
                                        ,onlineVideo[position].cHAPTERNAME,onlineVideo[position].yOUTUBETITLE,
                                        onlineVideo[position].yOUTUBEDESCRIPTION
                                        ,onlineVideo[position].oRGINALFILE,onlineVideo[position].sUBJECTICON))

                                if(!Global.blockStatus) {
                                    val intent = Intent(context, ExoPlayerActivity::class.java)
                                    intent.putExtra("ALBUM_TITLE", "")
                                    intent.putExtra("ALBUM_FILE", Utils.getRootDirPath(context) +"/catch/data/encryp/pics/"
                                            + onlineVideo[position].oRGINALFILE)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent)
                                }

                            }
                            override fun onError(error: Error) {
                                Log.i(TAG, "Error $error")
                                Toast.makeText(
                                    context,
                                    "Some Error occured",
                                    Toast.LENGTH_SHORT
                                ).show()
                                holder.constraintStatus.setBackgroundResource(R.color.white);
                                holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.white));
                                holder.cardViewProgress.visibility = View.GONE
                                holder.offlineButton.visibility = View.GONE
                                holder.buttonDownload.visibility = View.VISIBLE
                            }
                        })
                }

                holder.imageViewClose.setOnClickListener {
                    holder.constraintStatus.setBackgroundResource(R.color.white);
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.white));
                    holder.cardViewProgress.visibility = View.GONE
                    holder.offlineButton.visibility = View.GONE
                    holder.buttonDownload.visibility = View.VISIBLE
                    PRDownloader.cancel(downLoadPos)
                }

            }
        }

        override fun getItemCount(): Int {
            return onlineVideo.size
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    override fun onCreateClick(message: String) {
        Log.i(TAG,"onCreateClick ")
    }

    override fun onUpdateClick(youtubeList: YoutubeListStaffModel.Youtube) {

        Log.i(TAG,"onUpdateClick ")
    }
    override fun onDeleteClick(url : String,yOUTUBEID : Int) {
        Log.i(TAG,"onDeleteClick ")
    }

    override fun requestPermission(): Boolean {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

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
            permissionToRequests.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionToRequests.isNotEmpty()) {
            permissionsLauncher.launch(permissionToRequests.toTypedArray())
        }

        return permissions
    }




}

interface VideoClickListner {

    fun onCreateClick(message : String)
    fun onUpdateClick(youtubeList: YoutubeListStaffModel.Youtube)
    fun onDeleteClick(url : String,yOUTUBEID : Int)
    fun requestPermission() : Boolean
}