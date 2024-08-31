//package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.online_video
//
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.content.Context
//import android.content.DialogInterface
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Color
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.view.*
//import android.widget.*
//import androidx.activity.result.ActivityResultLauncher
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.AppCompatSpinner
//import androidx.appcompat.widget.PopupMenu
//import androidx.appcompat.widget.Toolbar
//import androidx.cardview.widget.CardView
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.core.content.ContextCompat
//import androidx.core.graphics.drawable.DrawableCompat
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.facebook.shimmer.ShimmerFrameLayout
//import info.passdaily.camrelconvertapp.R
//import info.passdaily.camrelconvertapp.databinding.ActivityOnlineVideoStaffBinding
//import info.passdaily.camrelconvertapp.lib.video.Video_Activity
//import info.passdaily.camrelconvertapp.model.*
//import info.passdaily.camrelconvertapp.services.Global
//import info.passdaily.camrelconvertapp.services.Status
//import info.passdaily.camrelconvertapp.services.Utils
//import info.passdaily.camrelconvertapp.services.ViewModelFactory
//import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
//import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
//import info.passdaily.camrelconvertapp.services.downloader.Error
//import info.passdaily.camrelconvertapp.services.downloader.OnDownloadListener
//import info.passdaily.camrelconvertapp.services.downloader.PRDownloader
//import info.passdaily.camrelconvertapp.services.downloader.StatusD
//import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
//import info.passdaily.camrelconvertapp.services.localDB.parent.OfflineStoreDbHelper
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.AssignmentDetailStaffActivity
//import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.VideoClickListner
//import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.YouTubePlayerActivity
//import java.io.File
//import java.util.*
//
//@Suppress("DEPRECATION")
//class OnlineVideoStaffActivity : AppCompatActivity(),VideoClickListner {
//
//
//    var TAG = "OnlineVideoStaffActivity"
//
//    private lateinit var binding: ActivityOnlineVideoStaffBinding
//
//    private lateinit var onlineVideoStaffViewModel: OnlineVideoStaffViewModel
//
//
//    var getYears = ArrayList<GetYearClassExamModel.Year>()
//    var getClass = ArrayList<GetYearClassExamModel.Class>()
//    var getSubject = ArrayList<SubjectsModel.Subject>()
//    var getChapter = ArrayList<ChaptersListStaffModel.Chapters>()
//
//    var getVideoList = ArrayList<YoutubeListStaffModel.Youtube>()
//
//    var aCCADEMICID = 0
//    var cLASSID = 0
//    var sUBJECTID = 0
//    var cHAPTERID = 0
//
//    private lateinit var localDBHelper : LocalDBHelper
//    var adminId = 0
//    var schoolId = 0
//    var getYearAndClassUrl = "Mark/MarkEntryPageLoad?AdminId="
//    var getSubjectUrl ="Mark/GetSubjectByClass?ClassId=&AdminId="
//    var getChapterUrl ="OnlineVideo/ChaptersById?AccademicId=&ClassId=&SubjectId="
//    var spinnerAcademic : AppCompatSpinner? = null
//    var spinnerClass : AppCompatSpinner? = null
//    var spinnerSubject : AppCompatSpinner? = null
//    var spinnerChapter : AppCompatSpinner? = null
//
//    var recyclerViewVideo : RecyclerView? = null
//
//    var toolbar: Toolbar? = null
//
//    var constraintLayoutContent : ConstraintLayout? = null
//    var constraintEmpty: ConstraintLayout? = null
//    var imageViewEmpty: ImageView? = null
//    var textEmpty: TextView? = null
//    var shimmerViewContainer: ShimmerFrameLayout? = null
//
//    private var readPermission = false
//    private var writePermission = false
//
//    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        localDBHelper = LocalDBHelper(this)
//        var user = localDBHelper.viewUser()
//        adminId = user[0].ADMIN_ID
//        schoolId = user[0].SCHOOL_ID
//        // Inflate the layout for this fragment
//        onlineVideoStaffViewModel = ViewModelProviders.of(
//            this,
//            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
//        )[OnlineVideoStaffViewModel::class.java]
//
//        binding = ActivityOnlineVideoStaffBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        toolbar = binding.toolbar
//        if (toolbar != null) {
//            setSupportActionBar(toolbar)
//            supportActionBar!!.title = "Manage Video Details"
//            // Customize the back button
//            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
//            supportActionBar!!.setDisplayShowTitleEnabled(true)
//            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
//                onBackPressed()
//            })
//        }
//
//        constraintLayoutContent = binding.constraintLayoutContent
//        constraintEmpty = binding.constraintEmpty
//        imageViewEmpty = binding.imageViewEmpty
//        textEmpty = binding.textEmpty
//        Glide.with(this)
//            .load(R.drawable.ic_empty_progress_report)
//            .into(imageViewEmpty!!)
//        shimmerViewContainer = binding.shimmerViewContainer
//
//        constraintLayoutContent?.visibility = View.GONE
//        shimmerViewContainer?.visibility = View.VISIBLE
//
//        spinnerAcademic = binding.spinnerAcademic
//        spinnerClass = binding.spinnerClass
//        spinnerSubject = binding.spinnerSubject
//        spinnerChapter = binding.spinnerChapter
//
//        recyclerViewVideo = binding.recyclerViewVideo
//        recyclerViewVideo?.layoutManager = LinearLayoutManager(this)
//
//
//        spinnerAcademic?.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long) {
//                aCCADEMICID = getYears[position].aCCADEMICID
//            }
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }
//
//        spinnerClass?.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long) {
//                cLASSID = getClass[position].cLASSID
//                getSubjectList(cLASSID)
//            }
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }
//
//        spinnerSubject?.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long) {
//                sUBJECTID = getSubject[position].sUBJECTID
//                getChapterList(aCCADEMICID,cLASSID,sUBJECTID,schoolId)
//            }
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }
//
//        spinnerChapter?.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long) {
//                cHAPTERID = getChapter[position].cHAPTERID
//                getVideoList(aCCADEMICID,cLASSID,sUBJECTID,cHAPTERID)
//
//            }
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }
//
//        initFunction()
//
//        permissionsLauncher =
//            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//                readPermission =
//                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
//                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
//                    ?: writePermission
//
//            }
//
//        var fab = binding.fab
//        fab.visibility = View.VISIBLE
//        fab.setOnClickListener {
//            val dialog1 = CreateYoutubeVideo(this)
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
//            dialog1.show(transaction, CreateYoutubeVideo.TAG)
//
//        }
//
//        Utils.setStatusBarColor(this)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.video_main, menu)
//        return true
//    }
//
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        if (id == R.id.action_offline) {
//            val log = Intent(applicationContext, OfflineStoreStaffActivity::class.java)
//            startActivity(log)
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    private fun initFunction() {
//        onlineVideoStaffViewModel.getYearClassExam(adminId)
//            .observe(this, Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//
//                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
//                            var years = Array(getYears.size){""}
//                            for (i in getYears.indices) {
//                                years[i] = getYears[i].aCCADEMICTIME
//                            }
//                            if (spinnerAcademic != null) {
//                                val adapter = ArrayAdapter(
//                                    this,
//                                    android.R.layout.simple_spinner_dropdown_item,
//                                    years
//                                )
//                                spinnerAcademic?.adapter = adapter
//                            }
//
//                            getClass = response.classList as ArrayList<GetYearClassExamModel.Class>
//                            var classX = Array(getClass.size){""}
//                            for (i in getClass.indices) {
//                                classX[i] = getClass[i].cLASSNAME
//                            }
//                            if (spinnerClass != null) {
//                                val adapter = ArrayAdapter(
//                                    this,
//                                    android.R.layout.simple_spinner_dropdown_item,
//                                    classX
//                                )
//                                spinnerClass?.adapter = adapter
//                            }
//                            Log.i(TAG,"initFunction SUCCESS")
//                            constraintLayoutContent?.visibility = View.VISIBLE
//                            shimmerViewContainer?.visibility = View.GONE
//                        }
//                        Status.ERROR -> {
//                            Log.i(TAG,"initFunction ERROR")
//                        }
//                        Status.LOADING -> {
//                            Log.i(TAG,"initFunction LOADING")
//                        }
//                    }
//                }
//            })
//    }
//
//    fun getSubjectList(cLASSID : Int){
//        onlineVideoStaffViewModel.getSubjectStaff(cLASSID,adminId)
//            .observe(this, Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
////                            constraintLayoutContent?.visibility = View.VISIBLE
////                            shimmerViewContainer?.visibility = View.GONE
//                            getSubject = response.subjects as ArrayList<SubjectsModel.Subject>
//                            var subject = Array(getSubject.size){""}
//                            if(subject.isNotEmpty()){
//                                for (i in getSubject.indices) {
//                                    subject[i] = getSubject[i].sUBJECTNAME
//                                }
////                                constraintEmpty?.visibility = View.GONE
////                                recyclerViewVideo?.visibility = View.VISIBLE
////                            }else {
////                                constraintEmpty?.visibility = View.VISIBLE
////                                recyclerViewVideo?.visibility = View.GONE
////                                Glide.with(this)
////                                    .load(R.drawable.ic_empty_progress_report)
////                                    .into(imageViewEmpty!!)
////                                textEmpty?.text =  resources.getString(R.string.no_results)
//                            }
//                            if (spinnerSubject != null) {
//                                val adapter = ArrayAdapter(
//                                    this,
//                                    android.R.layout.simple_spinner_dropdown_item,
//                                    subject
//                                )
//                                spinnerSubject?.adapter = adapter
//                            }
//
//                            Log.i(TAG,"getSubjectList SUCCESS")
//                        }
//                        Status.ERROR -> {
////                            constraintLayoutContent?.visibility = View.VISIBLE
////                            constraintEmpty?.visibility = View.VISIBLE
////                            recyclerViewVideo?.visibility = View.GONE
////                            shimmerViewContainer?.visibility = View.GONE
////
////                            Glide.with(this)
////                                .load(R.drawable.ic_no_internet)
////                                .into(imageViewEmpty!!)
////                            textEmpty?.text =  resources.getString(R.string.no_internet)
//                            Log.i(TAG,"getSubjectList ERROR")
//                        }
//                        Status.LOADING -> {
//                            getSubject = ArrayList<SubjectsModel.Subject>()
//                            getChapter = ArrayList<ChaptersListStaffModel.Chapters>()
//                            getVideoList = ArrayList<YoutubeListStaffModel.Youtube>()
//                            recyclerViewVideo?.adapter = VideoListAdapter(this,getVideoList,this,TAG)
////                            constraintLayoutContent?.visibility = View.GONE
////                            shimmerViewContainer?.visibility = View.VISIBLE
////                            Glide.with(this)
////                                .load(R.drawable.ic_empty_progress_report)
////                                .into(imageViewEmpty!!)
////
////                            textEmpty?.text =  resources.getString(R.string.loading)
//                            Log.i(TAG,"getSubjectList LOADING")
//                        }
//                    }
//                }
//            })
//    }
//
//    fun getChapterList(aCCADEMICID : Int,cLASSID : Int,sUBJECTID : Int,schoolId :Int){
//
//        onlineVideoStaffViewModel.getChapterStaff(aCCADEMICID,cLASSID,sUBJECTID,schoolId)
//            .observe(this, Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            getChapter = response.chaptersList as ArrayList<ChaptersListStaffModel.Chapters>
//                            constraintLayoutContent?.visibility = View.VISIBLE
//                            shimmerViewContainer?.visibility = View.GONE
//
//                            var chapter = Array(getChapter.size){""}
//                            if(chapter.isNotEmpty()) {
//                                for (i in getChapter.indices) { chapter[i] = getChapter[i].cHAPTERNAME }
//
//                                recyclerViewVideo?.visibility = View.VISIBLE
//                                constraintEmpty?.visibility = View.GONE
//                            }else {
//                                recyclerViewVideo?.visibility = View.GONE
//                                constraintEmpty?.visibility = View.VISIBLE
//                                Glide.with(this)
//                                    .load(R.drawable.ic_empty_progress_report)
//                                    .into(imageViewEmpty!!)
//                                textEmpty?.text =  resources.getString(R.string.no_results)
//                            }
//
//                            if (spinnerChapter != null) {
//                                val adapter = ArrayAdapter(
//                                    this,
//                                    android.R.layout.simple_spinner_dropdown_item,
//                                    chapter
//                                )
//                                spinnerChapter?.adapter = adapter
//                            }
//
//                            Log.i(TAG,"getChapterList SUCCESS")
//                        }
//                        Status.ERROR -> {
//                            constraintLayoutContent?.visibility = View.VISIBLE
//                            constraintEmpty?.visibility = View.VISIBLE
//                            recyclerViewVideo?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.GONE
//
//                            Glide.with(this)
//                                .load(R.drawable.ic_no_internet)
//                                .into(imageViewEmpty!!)
//                            textEmpty?.text =  resources.getString(R.string.no_internet)
//                            Log.i(TAG,"getChapterList ERROR")
//                        }
//                        Status.LOADING -> {
//                            getChapter = ArrayList<ChaptersListStaffModel.Chapters>()
//                            getVideoList = ArrayList<YoutubeListStaffModel.Youtube>()
//                            recyclerViewVideo?.adapter = VideoListAdapter(this,getVideoList,this,TAG)
//                            constraintLayoutContent?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.VISIBLE
//                            Glide.with(this)
//                                .load(R.drawable.ic_empty_progress_report)
//                                .into(imageViewEmpty!!)
//
//                            textEmpty?.text =  resources.getString(R.string.loading)
//                            Log.i(TAG,"getChapterList LOADING")
//                        }
//                    }
//                }
//            })
//    }
//
//    fun getVideoList(aCCADEMICID : Int,cLASSID : Int,sUBJECTID : Int,cHAPTERID: Int){
//
//        onlineVideoStaffViewModel.getVideoListStaff(aCCADEMICID,cLASSID,sUBJECTID,cHAPTERID)
//            .observe(this, Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            constraintLayoutContent?.visibility = View.VISIBLE
//                            shimmerViewContainer?.visibility = View.GONE
//
//                            getVideoList = response.youtubeList
//                            if(getVideoList.isNotEmpty()){
//                                constraintEmpty?.visibility = View.GONE
//                                recyclerViewVideo?.visibility = View.VISIBLE
//
//                                recyclerViewVideo?.adapter = VideoListAdapter(this,getVideoList,this,TAG)
//                            }else {
//                                constraintEmpty?.visibility = View.VISIBLE
//                                recyclerViewVideo?.visibility = View.GONE
//                                Glide.with(this)
//                                    .load(R.drawable.ic_empty_progress_report)
//                                    .into(imageViewEmpty!!)
//                                textEmpty?.text =  resources.getString(R.string.no_results)
//                            }
//
//                            Log.i(TAG,"getVideoList SUCCESS")
//                        }
//                        Status.ERROR -> {
//                            constraintLayoutContent?.visibility = View.VISIBLE
//                            constraintEmpty?.visibility = View.VISIBLE
//                            recyclerViewVideo?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.GONE
//
//                            Glide.with(this)
//                                .load(R.drawable.ic_no_internet)
//                                .into(imageViewEmpty!!)
//                            textEmpty?.text =  resources.getString(R.string.no_internet)
//                            Log.i(TAG,"getVideoList ERROR")
//                        }
//                        Status.LOADING -> {
//                            getVideoList = ArrayList<YoutubeListStaffModel.Youtube>()
//                            constraintLayoutContent?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.VISIBLE
//                            Glide.with(this)
//                                .load(R.drawable.ic_empty_progress_report)
//                                .into(imageViewEmpty!!)
//
//                            textEmpty?.text =  resources.getString(R.string.loading)
//                            Log.i(TAG,"getVideoList LOADING")
//                        }
//                    }
//                }
//            })
//
//    }
//
//
//
//    class VideoListAdapter(
//        var videoClickListner: VideoClickListner,
//        var youtubeList: ArrayList<YoutubeListStaffModel.Youtube>,
//        var mContext: Context,
//        var TAG : String
//    ) :
//        RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {
//        var downLoadPos = 0
//        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
//            var textViewDesc: TextView = view.findViewById(R.id.textViewDesc)
//            var textViewClass: TextView = view.findViewById(R.id.textViewClass)
//            var textViewSub: TextView = view.findViewById(R.id.textViewSub)
//            var textViewStatus: TextView = view.findViewById(R.id.textViewStatus)
//            var imageSubject: ImageView = view.findViewById(R.id.imageView)
//
//            var playYoutubeButton : CardView = view.findViewById(R.id.playYoutubeButton)
//            var buttonDownload : CardView = view.findViewById(R.id.buttonDownload)
//            var offlineButton : CardView = view.findViewById(R.id.offlineButton)
//
//            var imageViewMore : ImageView = view.findViewById(R.id.imageViewMore)
//
//            var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
//            var imageViewClose: ImageView = view.findViewById(R.id.imageViewClose)
//            var textDownloadProgress : TextView = view.findViewById(R.id.textDownloadProgress)
//            var cardViewProgress : CardView = view.findViewById(R.id.cardViewProgress)
//            var constraintStatus : ConstraintLayout = view.findViewById(R.id.constraintStatus)
//            var cardViewStatus: CardView = view.findViewById(R.id.cardViewStatus)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val itemView = LayoutInflater.from(parent.context)
//                .inflate(R.layout.youtubestaff_adapter, parent, false)
//            return ViewHolder(itemView)
//        }
//
//        @SuppressLint("SetTextI18n", "NotifyDataSetChanged", "UseCompatLoadingForDrawables")
//        override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
//
//            holder.textViewTitle.text = youtubeList[position].yOUTUBETITLE
//            holder.textViewDesc.text = youtubeList[position].yOUTUBEDESCRIPTION
//            holder.textViewClass.text = "Class : ${youtubeList[position].cLASSNAME}"
//            holder.textViewSub.text = youtubeList[position].sUBJECTNAME
//
//            if(!youtubeList[position].yOUTUBEDATE.isNullOrBlank()) {
//                val date: Array<String> =
//                    youtubeList[position].yOUTUBEDATE.split("T".toRegex()).toTypedArray()
//                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
//                holder.textViewStatus.text = Utils.formattedDateTime(dddd)
//            }else{
//                holder.textViewStatus.text = "No Date"
//            }
//
//            if (youtubeList[position].oRGINALFILE.isNotEmpty() && youtubeList[position].oRGINALFILE != "0") {
//                val file = File(Utils.getRootDirPath(mContext)!!)
//                if (!file.exists()) {
//                    file.mkdirs()
//                } else {
//                    Log.i(TAG, "Already existing")
//                }
//                val check = File(file.path +"/catch/data/staff/encryp/pics/"+ youtubeList[position].oRGINALFILE)
//                if (!check.isFile) {
//                    DrawableCompat.setTint(holder.imageViewMore.drawable, ContextCompat.getColor(mContext.applicationContext, R.color.gray_400))
//                    holder.offlineButton.visibility = View.GONE
//                    holder.buttonDownload.visibility = View.VISIBLE
//                }else{
//                    DrawableCompat.setTint(holder.imageViewMore.drawable, ContextCompat.getColor(mContext.applicationContext, R.color.black))
//                    holder.offlineButton.visibility = View.VISIBLE
//                    holder.buttonDownload.visibility = View.GONE
//                }
//            }else{
//                DrawableCompat.setTint(holder.imageViewMore.drawable, ContextCompat.getColor(mContext.applicationContext, R.color.gray_400))
//                holder.offlineButton.visibility = View.GONE
//                holder.buttonDownload.visibility = View.GONE
//            }
//
//            //
//            if (youtubeList[position].yOUTUBELINK != "0") {
//                holder.playYoutubeButton.visibility = View.VISIBLE
//            } else {
//                holder.playYoutubeButton.visibility = View.GONE
//            }
//
//            holder.playYoutubeButton.setOnClickListener(View.OnClickListener {
//                val split2 = youtubeList[position].yOUTUBELINK.split("=").toTypedArray()
//                val intent = Intent(mContext, YouTubePlayerActivity::class.java)
//                intent.putExtra("youTubeLink", split2[1])
//                intent.putExtra("YOUTUBE_ID", youtubeList[position].yOUTUBEID)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent)
//            })
//
//            holder.offlineButton.setOnClickListener {
//                val intent = Intent(mContext, Video_Activity::class.java)
//                intent.putExtra("ALBUM_TITLE", "")
//                intent.putExtra("ALBUM_FILE", Utils.getRootDirPath(mContext) +"/catch/data/staff/encryp/pics/"
//                        + youtubeList[position].oRGINALFILE)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent)
//
//            }
//
//            holder.buttonDownload.setOnClickListener {
//
//                if(videoClickListner.requestPermission()) {
//                    holder.constraintStatus.setBackgroundResource(R.color.trans);
//                    holder.cardViewStatus.setCardBackgroundColor(mContext.resources.getColor(R.color.gray_200));
//                    holder.buttonDownload.setCardBackgroundColor(mContext.resources.getColor(R.color.gray_200));
//                    holder.playYoutubeButton.setCardBackgroundColor(mContext.resources.getColor(R.color.gray_200));
//                    holder.playYoutubeButton.isEnabled = false
//                    holder.buttonDownload.isEnabled = false
//                    holder.cardViewProgress.visibility = View.VISIBLE
//
//                    if (StatusD.RUNNING === PRDownloader.getStatus(position)) {
//                        PRDownloader.pause(position)
//                    }
//
//                    if (StatusD.PAUSED === PRDownloader.getStatus(position)) {
//                        PRDownloader.resume(position)
//                    }
//                    downLoadPos = position
//                    var fileUrl = Global.event_url+"/YoutubeOGFile/"+ youtubeList[position].oRGINALFILE
//                    var fileLocation = Utils.getRootDirPath(mContext) +"/catch/data/staff/encryp/pics/"
//
//                    var fileName = youtubeList[position].oRGINALFILE
//                    Log.i(TAG, "fileUrl $fileUrl")
//                    Log.i(TAG, "fileLocation $fileLocation")
//                    Log.i(TAG, "fileName $fileName")
//
//                    downLoadPos = PRDownloader.download(
//                        fileUrl, fileLocation, fileName
//                    )
//                        .build()
//                        .setOnStartOrResumeListener {
//                            holder.imageViewClose.visibility = View.VISIBLE
//                        }
//                        .setOnPauseListener {
//                            holder.imageViewClose.visibility = View.VISIBLE
//                        }
//                        .setOnCancelListener {
//                            holder.imageViewClose.visibility = View.VISIBLE
//                            downLoadPos = 0
////                            holder.progressBar.visibility = View.GONE
//                            holder.textDownloadProgress.text ="Loading... "
////                            holder.imageViewClose.visibility = View.GONE
//                            PRDownloader.cancel(downLoadPos)
//
//                        }
//                        .setOnProgressListener { progress ->
//                            val progressPercent: Long =
//                                progress.currentBytes * 100 / progress.totalBytes
//                            holder.textDownloadProgress.text = Utils.getProgressDisplayLine(
//                                progress.currentBytes,
//                                progress.totalBytes
//                            )
//                        }
//                        .start(object : OnDownloadListener {
//                            override fun onDownloadComplete() {
//                                holder.constraintStatus.setBackgroundResource(R.color.white);
//                                holder.cardViewStatus.setCardBackgroundColor(mContext.resources.getColor(R.color.white));
//                                holder.buttonDownload.setCardBackgroundColor(mContext.resources.getColor(R.color.white));
//                                holder.playYoutubeButton.setCardBackgroundColor(mContext.resources.getColor(R.color.white));
//                                holder.playYoutubeButton.isEnabled = true
//                                holder.buttonDownload.isEnabled = true
//                                holder.cardViewProgress.visibility = View.GONE
//                                holder.offlineButton.visibility = View.VISIBLE
//                                holder.buttonDownload.visibility = View.GONE
//
//
//                                val offlineStoreDbHelper = OfflineStoreDbHelper(mContext)
//                                offlineStoreDbHelper.insert(
//                                    OfflineStoreDbHelper.OfflineModel( youtubeList[position].yOUTUBEID, Global.studentName
//                                        , youtubeList[position].cLASSNAME, youtubeList[position].sUBJECTNAME
//                                        ,youtubeList[position].cHAPTERNAME,youtubeList[position].yOUTUBETITLE,
//                                        youtubeList[position].yOUTUBEDESCRIPTION
//                                        ,youtubeList[position].oRGINALFILE,youtubeList[position].sUBJECTNAME))
//
//                                    val intent = Intent(mContext, Video_Activity::class.java)
//                                    intent.putExtra("ALBUM_TITLE", "")
//                                    intent.putExtra("ALBUM_FILE", Utils.getRootDirPath(mContext) +"/catch/data/staff/encryp/pics/"
//                                            + youtubeList[position].oRGINALFILE)
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    mContext.startActivity(intent)
//
//                            }
//                            override fun onError(error: Error) {
//                                Log.i(TAG, "Error $error")
//                                Toast.makeText(
//                                    mContext,
//                                    "Some Error occured",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                holder.constraintStatus.setBackgroundResource(R.color.white);
//                                holder.cardViewStatus.setCardBackgroundColor(mContext.resources.getColor(R.color.white));
//                                holder.buttonDownload.setCardBackgroundColor(mContext.resources.getColor(R.color.white));
//                                holder.playYoutubeButton.setCardBackgroundColor(mContext.resources.getColor(R.color.white));
//                                holder.playYoutubeButton.isEnabled = true
//                                holder.buttonDownload.isEnabled = true
//                                holder.cardViewProgress.visibility = View.GONE
//                                holder.offlineButton.visibility = View.GONE
//                                holder.buttonDownload.visibility = View.VISIBLE
//                            }
//                        })
//                }
//
//                holder.imageViewClose.setOnClickListener {
//                    holder.constraintStatus.setBackgroundResource(R.color.white);
//                    holder.cardViewStatus.setCardBackgroundColor(mContext.resources.getColor(R.color.white));
//                    holder.buttonDownload.setCardBackgroundColor(mContext.resources.getColor(R.color.white));
//                    holder.playYoutubeButton.setCardBackgroundColor(mContext.resources.getColor(R.color.white));
//                    holder.playYoutubeButton.isEnabled = true
//                    holder.buttonDownload.isEnabled = true
//                    holder.cardViewProgress.visibility = View.GONE
//                    holder.offlineButton.visibility = View.GONE
//                    holder.buttonDownload.visibility = View.VISIBLE
//                    PRDownloader.cancel(downLoadPos)
//                }
//            }
//
//            when (youtubeList[position].sUBJECTNAME) {
//                "English" -> {
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_english)
//                        .into(holder.imageSubject)
//                }
//                "Chemistry" -> {
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_chemistry)
//                        .into(holder.imageSubject)
//                }
//                "Biology" -> {
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_biology)
//                        .into(holder.imageSubject)
//                }
//                "Maths" -> {
//
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_maths)
//                        .into(holder.imageSubject)
//                }
//                "Hindi" -> {
//
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_hindi)
//                        .into(holder.imageSubject)
//                }
//                "Physics" -> {
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_physics)
//                        .into(holder.imageSubject)
//                }
//                "Malayalam" -> {
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_malayalam)
//                        .into(holder.imageSubject)
//                }
//                "Arabic" -> {
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_arabic)
//                        .into(holder.imageSubject)
//                }
//                "Accountancy" -> {
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_accountancy)
//                        .into(holder.imageSubject)
//                }
//                "Social Science" -> {
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_social)
//                        .into(holder.imageSubject)
//                }
//                "Economics" -> {
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_economics)
//                        .into(holder.imageSubject)
//                }
//                "BasicScience" -> {
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_biology)
//                        .into(holder.imageSubject)
//                }
//                "Computer" -> {
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_computer)
//                        .into(holder.imageSubject)
//                }
//                "General" -> {
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_sub2_computer)
//                        .into(holder.imageSubject)
//                }
//            }
//
//
//            holder.imageViewMore.setOnClickListener(View.OnClickListener {
//                val popupMenu = PopupMenu(mContext, holder.imageViewMore)
//                popupMenu.inflate(R.menu.video_adapter_menu)
//                popupMenu.menu.findItem(R.id.menu_edit).icon = mContext.resources.getDrawable(R.drawable.ic_icon_edit)
//                popupMenu.menu.findItem(R.id.menu_report).icon = mContext.resources.getDrawable(R.drawable.ic_icon_about_gray)
//                popupMenu.menu.findItem(R.id.menu_video).icon = mContext.resources.getDrawable(R.drawable.ic_icon_close)
//                popupMenu.menu.findItem(R.id.menu_offline_video).icon = mContext.resources.getDrawable(R.drawable.ic_icon_delete_gray)
//                popupMenu.menu.findItem(R.id.menu_download).icon = mContext.resources.getDrawable(R.drawable.ic_icon_download)
//                popupMenu.menu.findItem(R.id.menu_open).icon = mContext.resources.getDrawable(R.drawable.ic_icon_download)
//                popupMenu.menu.findItem(R.id.menu_open).isVisible = false
//                popupMenu.menu.findItem(R.id.menu_download).isVisible = false
//                popupMenu.menu.findItem(R.id.menu_open).isVisible = false
//                if (youtubeList[position].oRGINALFILE != "0") {
//                    val file = File(Utils.getRootDirPath(mContext)!!)
//                    if (!file.exists()) {
//                        file.mkdirs()
//                    } else {
//                        Log.i(TAG, "Already existing")
//                    }
//                    val check = File(file.path +"/catch/data/staff/encryp/pics/"+ youtubeList[position].oRGINALFILE)
//                    popupMenu.menu.findItem(R.id.menu_offline_video).isVisible = check.isFile
//
//                }
//                popupMenu.setOnMenuItemClickListener { menuItem ->
//                    when (menuItem.itemId) {
//                        R.id.menu_edit -> {
//                            videoClickListner.onUpdateClick(youtubeList[position])
//                            true
//                        }
//                        R.id.menu_offline_video -> {
//                            val builder = AlertDialog.Builder(mContext)
//                            builder.setMessage("Are you sure want to Delete Offline Video?")
//                                .setCancelable(false)
//                                .setPositiveButton("Yes") { _, _ ->
//
//                                    val file = File(Utils.getRootDirPath(mContext) + "/catch/data/staff/encryp/pics/"
//                                            + youtubeList[position].oRGINALFILE)
//                                    if (file.exists()) {
//                                        val delete = file.delete()
//                                    }
//                                    val repo1 = OfflineStoreDbHelper(mContext)
////                        val productList1: ArrayList<OfflineStoreDbHelper.OfflineModel> = repo1.viewOfflineVideo()
//                                    repo1.deleteOfflineItem(youtubeList[position].yOUTUBEID)
//                                    // updateList(arraylist);
//                                  //  youtubeList.removeAt(position)
//                                    notifyItemChanged(position)
//                                }
//                                .setNegativeButton(
//                                    "No"
//                                ) { dialog, _ -> //  Action for 'NO' Button
//                                    dialog.cancel()
//                                }
//
//
//                            //Creating dialog box
//                            val alert = builder.create()
//                            //Setting the title manually
//                            alert.setTitle("Delete")
//                            alert.show()
//                            val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
//                            buttonbackground.setTextColor(Color.BLACK)
//                            val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
//                            buttonbackground1.setTextColor(Color.BLACK)
//                            true
//                        }
//                        R.id.menu_report -> {
//                            val intent = Intent(mContext, VideoReportActivity::class.java)
//                            intent.putExtra("YOUTUBE_ID", youtubeList[position].yOUTUBEID)
//                            intent.putExtra("ACCADEMIC_ID", youtubeList[position].aCCADEMICID)
//                            intent.putExtra("CLASS_ID", youtubeList[position].cLASSID)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            mContext.startActivity(intent)
//                            true
//                        }
//                        R.id.menu_video -> {
//                            val builder = AlertDialog.Builder(mContext)
//                            builder.setMessage("Are you sure want to Delete Video?")
//                                .setCancelable(false)
//                                .setPositiveButton("Yes") { _, _ ->
//                                    videoClickListner.onDeleteClick("OnlineVideo/VideoDelete",youtubeList[position].yOUTUBEID)
//                                }
//                                .setNegativeButton(
//                                    "No"
//                                ) { dialog, _ -> //  Action for 'NO' Button
//                                    dialog.cancel()
//                                }
//                            //Creating dialog box
//                            val alert = builder.create()
//                            //Setting the title manually
//                            alert.setTitle("Delete")
//                            alert.show()
//                            val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
//                            buttonbackground.setTextColor(Color.BLACK)
//                            val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
//                            buttonbackground1.setTextColor(Color.BLACK)
//                            true
//                        }
//                        else -> false
//                    }
//                }
//                popupMenu.show()
//            })
//
//
//        }
//
//        override fun getItemCount(): Int {
//            return youtubeList.size
//        }
//
//    }
//
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        Log.i(TAG,"onBackPressed")
//    }
//
//    override fun onCreateClick(message: String) {
//        Utils.getSnackBarGreen(this,message,constraintLayoutContent!!)
//        initFunction()
//    }
//
//    override fun onUpdateClick(youtubeList: YoutubeListStaffModel.Youtube) {
//        val dialog1 = UpdateYoutubeVideo(this,youtubeList)
//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
//        dialog1.show(transaction, UpdateYoutubeVideo.TAG)
//    }
//
//    override fun onDeleteClick(url : String,yOUTUBEID : Int) {
//        val paramsMap: HashMap<String?, Int> = HashMap()
//        paramsMap["YoutubeId"] = yOUTUBEID
//        onlineVideoStaffViewModel.getCommonGetFuntion(url,paramsMap)
//            .observe(this, Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            when {
//                                Utils.resultFun(response) == "0" -> {
//                                    Utils.getSnackBarGreen(this, "Video Deleted Successfully", constraintLayoutContent!!)
//                                    initFunction()
//                                }
//                                else -> {
//                                    Utils.getSnackBar4K(this, "Failed while Youtube Video Creation", constraintLayoutContent!!)
//                                }
//                            }
//                        }
//                        Status.ERROR -> {
//                            Utils.getSnackBar4K(this, "Please try again after sometime", constraintLayoutContent!!)
//                        }
//                        Status.LOADING -> {
//                            Log.i(TAG,"loading")
//                        }
//                    }
//                }
//            })
//    }
//
//    override fun requestPermission(): Boolean {
//        val hasReadPermission = ContextCompat.checkSelfPermission(
//            this@OnlineVideoStaffActivity,
//            android.Manifest.permission.READ_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED
//
//        val hasWritePermission = ContextCompat.checkSelfPermission(
//            this@OnlineVideoStaffActivity,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED
//
//        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
//
//        readPermission = hasReadPermission
//        writePermission = hasWritePermission || minSdk29
//
//        val permissions = readPermission && writePermission
//
//        val permissionToRequests = mutableListOf<String>()
//        if (!writePermission) {
//            permissionToRequests.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
//
//        if (!readPermission) {
//            permissionToRequests.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
//
//        if (permissionToRequests.isNotEmpty()) {
//            permissionsLauncher.launch(permissionToRequests.toTypedArray())
//        }
//
//        return permissions
//    }
//}