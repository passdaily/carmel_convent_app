package info.passdaily.camrelconvertapp.typeofuser.parent.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentHomeBinding
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.ContactUsViewModel
import info.passdaily.camrelconvertapp.lib.ClickableViewPager
import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
//import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
import info.passdaily.camrelconvertapp.lib.video.Video_Activity
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.home.HomeFragmentStaff
import info.passdaily.camrelconvertapp.typeofuser.parent.absent.AbsentFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.assignment.AssignmentList
import info.passdaily.camrelconvertapp.typeofuser.parent.calendar.CalenderActivity
import info.passdaily.camrelconvertapp.typeofuser.parent.description_exam.DescriptiveExam
import info.passdaily.camrelconvertapp.typeofuser.parent.event.ManageEventDialog
import info.passdaily.camrelconvertapp.typeofuser.parent.fees.FeesInitFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.GalleryInitFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.library.LibraryFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.map.TrackFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.notification.NotificationFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.objective_exam.ObjectiveExam
import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.OnlineVideoFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.YouTubeIframePlayer
//import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.YouTubePlayerActivity
import info.passdaily.camrelconvertapp.typeofuser.parent.study_material.StudyMaterialInit
//import info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout.JoinLiveInit
//import info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout.LiveScheduledFragment
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@Suppress("DEPRECATION")
class HomeFragment : Fragment() {
    val DELAY_MS: Long = 500

    var TAG = "HomeFragment";
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    var youTubeLink = ""
    // val imageList: ArrayList<String> = ArrayList()

    var viewPager: ClickableViewPager? = null
    var dotsIndicator: SpringDotsIndicator? = null
    var currentPage = 0
    var currentPageSms = 0

    var viewPagerSms: ViewPager? = null
    var dotsIndicatorSms: DotsIndicator? = null

    var recyclerViewSub: RecyclerView? = null
    var recyclerViewVideo: RecyclerView? = null
    var TOTAL_ABSENTS = 0
    var TOTAL_ASSIGNMENT = 0
    var TOTAL_ENQUIRY = 0
    var TOTAL_INBOX = 0
    var TOTAL_LEAVENOTES = 0
    var TOTAL_DESCRIPTIVE = 0
    var TOTAL_LIVE_CLASS = 0
    var TOTAL_NOTICEBOARD = 0
    var TOTAL_OBJECTIVE = 0
    var TOTAL_STUDYMATERIAL = 0
    var TOTAL_VIDEOS = 0

    var textVideoCount: TextView? = null
    var textMaterialCount: TextView? = null
    var textObjectiveCount: TextView? = null
    var textDescriptiveCount: TextView? = null

    var shimmerViewContainer : ShimmerFrameLayout? =null
     var nestedScroll : ConstraintLayout? =null

    // This property is only valid between onCreateView and
    // onDestroyView.

    var calenderButton: ImageView? = null
    var galleryButton : ImageView? = null
    var libraryButton : ImageView? = null
    var trackButton  : ImageView? = null

    var scheduledConstraintLayout : ConstraintLayout? = null
    var joinConstraintLayout : ConstraintLayout? = null
    var absentConstraintLayout : ConstraintLayout? = null
    var assignmentConstraintLayout : ConstraintLayout? = null


    var videoConstraintLayout  : ConstraintLayout? = null
    var materialConstraintLayout  : ConstraintLayout? = null
    var oExamConstraintLayout : ConstraintLayout? = null
    var descConstraintLayout  : ConstraintLayout? = null

    var mContext : Context? =null

    private lateinit var contactViewModel: ContactUsViewModel

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
        contactViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[ContactUsViewModel::class.java]
//        homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Global.currentPage = 1
        Global.screenState = "homePage"

//        homeViewModel.getDashBoard(Global.studentId)
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        shimmerViewContainer = binding.shimmerViewContainer
        nestedScroll = binding.nestedScroll
        shimmerViewContainer?.visibility =View.VISIBLE
        nestedScroll?.visibility =View.GONE

        textVideoCount = binding.textVideoCount
        textMaterialCount = binding.textMaterialCount
        textObjectiveCount = binding.textObjectiveCount
        textDescriptiveCount = binding.textDescriptiveCount

        viewPager = binding.viewPager
        dotsIndicator = binding.springDotsIndicator
        dotsIndicator?.visibility = View.GONE

        viewPagerSms = binding.viewPagerSms
        dotsIndicatorSms = binding.dotsIndicatorSms
        dotsIndicatorSms?.visibility = View.GONE

        recyclerViewSub = binding.recyclerViewSub
        recyclerViewSub?.layoutManager = LinearLayoutManager(requireActivity(),
            LinearLayoutManager.HORIZONTAL, false
        )
        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())

        calenderButton = binding.calenderButton
        galleryButton = binding.galleryButton
        libraryButton = binding.libraryButton
        trackButton = binding.trackButton


        initView()


        calenderButton?.setOnClickListener {
            val log = Intent(requireActivity(), CalenderActivity::class.java)
            startActivity(log)
        }

        galleryButton?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, GalleryInitFragment()).commit()
        }

        libraryButton?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, LibraryFragment()).commit()
        }

        trackButton?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, TrackFragment()).commit()
        }

        scheduledConstraintLayout = binding.scheduledConstraintLayout
        joinConstraintLayout = binding.joinConstraintLayout
        absentConstraintLayout = binding.absentConstraintLayout
        assignmentConstraintLayout = binding.assignmentConstraintLayout

        scheduledConstraintLayout?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, FeesInitFragment()).commit()
        }
        joinConstraintLayout?.setOnClickListener {
//            var fragmentManager = requireActivity().supportFragmentManager
//            var fragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.nav_host_fragment, JoinLiveInit()).commit()
        }
        absentConstraintLayout?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, AbsentFragment()).commit()
        }
        assignmentConstraintLayout?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, AssignmentList()).commit()
        }

        videoConstraintLayout = binding.videoConstraintLayout
        materialConstraintLayout = binding.materialConstraintLayout
        oExamConstraintLayout = binding.oExamConstraintLayout
        descConstraintLayout = binding.descConstraintLayout

        videoConstraintLayout?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, OnlineVideoFragment()).commit()
        }
        materialConstraintLayout?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, StudyMaterialInit()).commit()
        }
        oExamConstraintLayout?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, ObjectiveExam()).commit()
        }

        descConstraintLayout?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, DescriptiveExam()).commit()
        }

        binding.constraintLayoutDownload.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, StudyMaterialInit()).commit()
        }

        var textViewAll = binding.textViewAll

        textViewAll.setOnClickListener {
            val dialog1 = ManageEventDialog()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, ManageEventDialog.TAG)
        }
        initItems()

        requireActivity().window.statusBarColor = requireActivity().resources.getColor(R.color.light_blue100)
    }


    private fun initView() {
        val studentId = 0
        contactViewModel.getContactUsItems(studentId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            for(contactUs in response.contactusList) {
                                Global.WORK_PLACE_LATTITUDE = contactUs.lATTITUDE.toDouble()
                                Global.WORK_PLACE_LONGITUDE = contactUs.lONGITUDE.toDouble()
                            }
                        }
                        Status.ERROR -> {
                            Log.i("TAG", "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i("TAG", "resource ${resource.status}")
                            Log.i("TAG", "message ${resource.message}")
                        }
                    }
                }
            })
    }

    private fun initItems() {
//        homeViewModel.getDashBoardObservable()
//            .observe(requireActivity(), {
//                if (it != null) {
//
//                    if(isAdded) {
//                        recyclerViewSub?.adapter =
//                            TimeTableAdapter(it.periodDetails, mContext!!)
//                        recyclerViewVideo?.adapter = VideoAdapter(it.videoDetails,  mContext!!)
//
//                    Log.i(TAG, "PERIOD_NAME " + it.periodDetails)
//
//                    Log.i(TAG, "videoDetails " + it.videoDetails)
//                    Log.i(TAG, "eventDetails " + it.eventDetails)
//                    Log.i(TAG, "inboxDetails " + it.inboxDetails)
////                    for(i in it.eventDetails){
////                        imageList.add(i.eVENTFILE)
////                    }
//                    viewPager?.adapter = CustomPagerAdapter(requireActivity(), it.eventDetails)
//                    dotsIndicator?.setViewPager(viewPager!!)
////
//                    viewPagerSms?.adapter = InboxAdapter(requireActivity(), it.inboxDetails)
//                    dotsIndicatorSms?.setViewPager(viewPagerSms!!)
//                    }
//
//                    TOTAL_ABSENTS = it.tileDetails.tOTALABSENTS
//                    TOTAL_ASSIGNMENT = it.tileDetails.tOTALASSIGNMENT
//                    TOTAL_DESCRIPTIVE = it.tileDetails.tOTALDESCRIPTIVE
//                    TOTAL_ENQUIRY = it.tileDetails.tOTALENQUIRY
//                    TOTAL_INBOX = it.tileDetails.tOTALINBOX
//                    TOTAL_LIVE_CLASS = it.tileDetails.tOTALLIVECLASS
//                    TOTAL_VIDEOS = it.tileDetails.tOTALVIDEOS
//                    TOTAL_OBJECTIVE = it.tileDetails.tOTALOBJECTIVE
//                    TOTAL_LEAVENOTES = it.tileDetails.tOTALLEAVENOTES
//                    TOTAL_NOTICEBOARD = it.tileDetails.tOTALNOTICEBOARD
//                    TOTAL_STUDYMATERIAL = it.tileDetails.tOTALSTUDYMATERIAL
//
//                    textVideoCount?.text = it.tileDetails.tOTALVIDEOS.toString()
//                    textMaterialCount?.text = it.tileDetails.tOTALSTUDYMATERIAL.toString()
//                    textDescriptiveCount?.text = it.tileDetails.tOTALDESCRIPTIVE.toString()
//                    textObjectiveCount?.text = it.tileDetails.tOTALOBJECTIVE.toString()
//
//                    shimmerViewContainer?.visibility =View.GONE
//                    nestedScroll?.visibility =View.VISIBLE
//
//                }
//            })

        homeViewModel.getDashBoard(Global.studentId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            binding.constraintYouTube.setOnClickListener {
                                if(response.academicDetails.yOUTUBELINK!!.isNotEmpty()) {
                                    youTubeLink = response.academicDetails.yOUTUBELINK
                                    Toast.makeText(requireActivity(), "Opening YouTube", Toast.LENGTH_SHORT).show()
                                    var intent = Intent(Intent.ACTION_VIEW, Uri.parse(youTubeLink));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setPackage("com.google.android.youtube");
                                    startActivity(intent)
                                }
                            }

                            if(isAdded) {
                                recyclerViewSub?.adapter =
                                    TimeTableAdapter(response.periodDetails, mContext!!)
                                recyclerViewVideo?.adapter = VideoAdapter(response.videoDetails,  mContext!!)

                                Log.i(TAG, "PERIOD_NAME " + response.periodDetails)

                                Log.i(TAG, "videoDetails " + response.videoDetails)
                                Log.i(TAG, "eventDetails " + response.eventDetails)
                                Log.i(TAG, "inboxDetails " + response.inboxDetails)
//                    for(i in it.eventDetails){
//                        imageList.add(i.eVENTFILE)
//                    }
//                                viewPager?.adapter = CustomPagerAdapter(requireActivity(), response.eventDetails)
//                                dotsIndicator?.setViewPager(viewPager!!)
//

                                val eventList = response.eventDetails
                                for(i in eventList.indices){
                                    Global.academicId = eventList[i].aCCADEMICID
                                }
                                try {
                                    val mCustomPagerAdapter = CustomPagerAdapter(
                                        requireActivity(),
                                        eventList,
                                        TAG
                                    )
                                    if (viewPager != null && dotsIndicator != null) {
                                        Log.i(TAG, "coming")
                                        viewPager!!.adapter = mCustomPagerAdapter
                                        dotsIndicator!!.setViewPager(viewPager!!)
                                    }
                                    /*After setting the adapter use the timer */
                                    /*After setting the adapter use the timer */
                                    val handler = Handler()
                                    val Update = Runnable {
                                        if (currentPage == eventList.size - 1) {
                                            currentPage = 0
                                        }
                                        viewPager!!.setCurrentItem(currentPage++, true)
                                    }

                                    try {
                                        val timer = Timer() // This will create a new Thread
                                        timer.schedule(object : TimerTask() {
                                            // task to be scheduled
                                            override fun run() {
                                                handler.post(Update)
                                            }
                                        }, DELAY_MS, 5000)
                                    } catch (e: java.lang.Exception) {
                                        handler.removeCallbacks(Update)
                                    }
                                } catch (e: Exception) { Log.i(TAG, "Exception $e") }





                                val inboxLis = response.inboxDetails
                                try {
                                    val mCustomPagerAdapter = InboxAdapter(
                                        requireActivity(),
                                        inboxLis,
                                    )
                                    if (viewPagerSms != null && dotsIndicatorSms != null) {
                                        Log.i(TAG, "coming")
                                        viewPagerSms!!.adapter = mCustomPagerAdapter
                                        dotsIndicatorSms!!.setViewPager(viewPagerSms!!)
                                    }
                                    /*After setting the adapter use the timer */
                                    /*After setting the adapter use the timer */
                                    val handler = Handler()
                                    val Update = Runnable {
                                        if (currentPageSms == eventList.size - 1) {
                                            currentPageSms = 0
                                        }
                                        viewPagerSms!!.setCurrentItem(currentPageSms++, true)
                                    }

                                    try {
                                        val timer = Timer() // This will create a new Thread
                                        timer.schedule(object : TimerTask() {
                                            // task to be scheduled
                                            override fun run() {
                                                handler.post(Update)
                                            }
                                        }, DELAY_MS, 5000)
                                    } catch (e: java.lang.Exception) {
                                        handler.removeCallbacks(Update)
                                    }
                                } catch (e: Exception) { Log.i(TAG, "Exception $e") }


//                                viewPagerSms?.adapter = InboxAdapter(requireActivity(), response.inboxDetails)
//                                dotsIndicatorSms?.setViewPager(viewPagerSms!!)
                            }

                            TOTAL_ABSENTS = response.tileDetails.tOTALABSENTS
                            TOTAL_ASSIGNMENT = response.tileDetails.tOTALASSIGNMENT
                            TOTAL_DESCRIPTIVE = response.tileDetails.tOTALDESCRIPTIVE
                            TOTAL_ENQUIRY = response.tileDetails.tOTALENQUIRY
                            TOTAL_INBOX = response.tileDetails.tOTALINBOX
                            TOTAL_LIVE_CLASS = response.tileDetails.tOTALLIVECLASS
                            TOTAL_VIDEOS = response.tileDetails.tOTALVIDEOS
                            TOTAL_OBJECTIVE = response.tileDetails.tOTALOBJECTIVE
                            TOTAL_LEAVENOTES = response.tileDetails.tOTALLEAVENOTES
                            TOTAL_NOTICEBOARD = response.tileDetails.tOTALNOTICEBOARD
                            TOTAL_STUDYMATERIAL = response.tileDetails.tOTALSTUDYMATERIAL

                            binding.textViewSheduled.text = TOTAL_LIVE_CLASS.toString()
                            binding.textViewLive.text = TOTAL_LIVE_CLASS.toString()
                            binding.textViewAbsent.text = TOTAL_ABSENTS.toString()
                            binding.textViewAssignment.text = TOTAL_ASSIGNMENT.toString()

                            textVideoCount?.text = TOTAL_VIDEOS.toString()
                            textMaterialCount?.text = TOTAL_STUDYMATERIAL.toString()
                            textDescriptiveCount?.text = TOTAL_DESCRIPTIVE.toString()
                            textObjectiveCount?.text = TOTAL_OBJECTIVE.toString()

                            shimmerViewContainer?.visibility =View.GONE
                            nestedScroll?.visibility =View.VISIBLE
                        }
                        Status.ERROR -> {
                            shimmerViewContainer?.visibility =View.GONE
                            nestedScroll?.visibility =View.VISIBLE
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


//    class CustomPagerAdapter(context: FragmentActivity, resources: List<DashBoardModel.EventDetail>) :
//        PagerAdapter() {
//        private val mContext: FragmentActivity = context
//        var mLayoutInflater: LayoutInflater =
//            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        private val mResources: List<DashBoardModel.EventDetail> = resources
//        override fun instantiateItem(container: ViewGroup, position: Int): Any {
//            val itemView: View =
//                mLayoutInflater.inflate(R.layout.pageradapter, container, false)
//            val imageView: ImageView =
//                itemView.findViewById<View>(R.id.imageView) as ImageView
//            //  imageView.setImageResource(mResources.get(position))
//            Log.i(
//                "CustomPagerAdapter ",
//                "image " + Global.event_url + "/EventFile/" + mResources[position]
//            )
//            Glide.with(mContext)
//                .load(Global.event_url + "/EventFile/" + mResources[position].eVENTFILE)
//                .into(imageView)
//
//
//            itemView.setOnClickListener {
//                if(mResources[position].eVENTTYPE == 2){
////                    val intent = Intent(mContext, Video_Activity::class.java)
////                    intent.putExtra("ALBUM_TITLE", "")
////                    intent.putExtra("ALBUM_FILE", Global.event_url+"/EventFile/"+mResources[position].eVENTLINKFILE)
////                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    mContext.startActivity(intent)
//                    val intent: Intent = Intent(mContext, ExoPlayerActivity::class.java)
//                    intent.putExtra("ALBUM_TITLE", "")
//                    intent.putExtra("ALBUM_FILE", Global.event_url+"/EventFile/"+mResources[position].eVENTLINKFILE)
////                    intent.putExtra("YOUTUBE_LINK", Global.event_url+"/EventFile/"+mResources[position].eVENTLINKFILE)
////                    intent.putExtra("EVENT_TYPE", mResources[position].eVENTTYPE)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    mContext.startActivity(intent)
//                }
//                else if(mResources[position].eVENTTYPE == 3){
//                    val split2 = mResources[position].eVENTLINKFILE.split("=").toTypedArray()
//                    val intent = Intent(mContext, YouTubePlayerActivity::class.java)
//                    intent.putExtra("youTubeLink", split2[1])
//                    intent.putExtra("YOUTUBE_ID", mResources[position].eVENTID)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    mContext.startActivity(intent)
//                }
//            }
//
//            container.addView(itemView)
//            return itemView
//        }
//
//        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
//            collection.removeView(view as View)
//        }
//
//        override fun getCount(): Int {
//            return mResources.size
//        }
//
//        override fun isViewFromObject(view: View, `object`: Any): Boolean {
//            return view === `object`
//        }
//
//    }


    class CustomPagerAdapter(
        var context: Activity,
        var mResources: List<DashBoardModel.EventDetail>,
        var TAG : String
    ) :
        PagerAdapter() {
        var mLayoutInflater = context.applicationContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        override fun getCount(): Int {
            return mResources.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj as ConstraintLayout
        }

        override fun instantiateItem(viewGroup: ViewGroup, position: Int): Any {
            val inflate: View = mLayoutInflater.inflate(R.layout.pd_bannerlayout, viewGroup, false)
            val with = Glide.with(context)
            with.load(Global.event_url + "/EventFile/" + mResources[position].eVENTFILE).into(
                (inflate.findViewById<View>(R.id.imageView) as ImageView)
            )
            viewGroup.addView(inflate)

            inflate.setOnClickListener {
                if(mResources[position].eVENTTYPE == 2){
//                    val intent = Intent(mContext, Video_Activity::class.java)
//                    intent.putExtra("ALBUM_TITLE", "")
//                    intent.putExtra("ALBUM_FILE", Global.event_url+"/EventFile/"+mResources[position].eVENTLINKFILE)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    mContext.startActivity(intent)
                    val intent: Intent = Intent(context, ExoPlayerActivity::class.java)
                    intent.putExtra("ALBUM_TITLE", "")
                    intent.putExtra("ALBUM_FILE", Global.event_url+"/EventFile/"+mResources[position].eVENTLINKFILE)
//                    intent.putExtra("YOUTUBE_LINK", Global.event_url+"/EventFile/"+mResources[position].eVENTLINKFILE)
//                    intent.putExtra("EVENT_TYPE", mResources[position].eVENTTYPE)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent)
                }
                else if(mResources[position].eVENTTYPE == 3){
                    val split2 = mResources[position].eVENTLINKFILE!!.split("=").toTypedArray()
                    val intent = Intent(context, YouTubeIframePlayer::class.java)
                    intent.putExtra("youTubeLink", split2[1])
                    intent.putExtra("YOUTUBE_ID", mResources[position].eVENTID)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent)
                }
            }
            return inflate
        }

        override fun destroyItem(viewGroup: ViewGroup, i: Int, obj: Any) {
            viewGroup.removeView(obj as ConstraintLayout)
        }
    }



    class InboxAdapter(context: FragmentActivity, inboxDetails: List<DashBoardModel.InboxDetail>) :
        PagerAdapter() {
        private val mContext: FragmentActivity = context
        var mLayoutInflater: LayoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        private val mInboxDetails: List<DashBoardModel.InboxDetail> = inboxDetails
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView: View =
                mLayoutInflater.inflate(R.layout.inbox_adapter, container, false)
            val textDate: TextView = itemView.findViewById<View>(R.id.textDate) as TextView
            val textDesc: TextView = itemView.findViewById<View>(R.id.textDesc) as TextView


//                    String st_dattttt=Global.FormateTimeMapttt(datestr1);
//                    String[] time1 = st_dattttt.split(":");
//                    String mhour=time1[0];
//                    String mminute=time1[1];

            // start_get_time=Global.FormateTimeMap0(datestr1);
            //FormateTimeMap0
            val date1: Array<String> =
                mInboxDetails[position].vIRTUALMAILSENTDATE!!.split("T".toRegex()).toTypedArray()
            val datetime: String = Utils.parseDateToddMMMyyyy(date1[0]).toString()
            //end_date.setText(datetme1)

            textDate.text = datetime
            textDesc.text = mInboxDetails[position].vIRTUALMAILCONTENT
//            Log.i("CustomPagerAdapter ","image "+Global.event_url+"/EventFile/"+mInboxDetails[position])
            container.addView(itemView)
            return itemView
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int {
            return mInboxDetails.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

    }


    class TimeTableAdapter(
        var storeModel: List<DashBoardModel.PeriodDetail>, var context: Context
    ) : RecyclerView.Adapter<TimeTableAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewSubject: TextView = view.findViewById(R.id.textViewSubject)
            var textPeriodName: TextView = view.findViewById(R.id.textPeriodName)
            var imageSubject: ImageView = view.findViewById(R.id.imageSubject)
        }

        @NonNull
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.dashboard_period_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewSubject.text = storeModel[position].sUBJECTNAME
            when (storeModel[position].pERIODNAME) {
                "1" -> {
                    holder.textPeriodName.text = "First Period"
                }
                "2" -> {
                    holder.textPeriodName.text = "Second Period"
                }
                "3" -> {
                    holder.textPeriodName.text = "Third Period"
                }
                "4" -> {
                    holder.textPeriodName.text = "Fourth Period"
                }
                "5" -> {
                    holder.textPeriodName.text = "Fifth Period"
                }
                "6" -> {
                    holder.textPeriodName.text = "Sixth Period"
                }
                "7" -> {
                    holder.textPeriodName.text = "Seventh Period"
                }
                "8" -> {
                    holder.textPeriodName.text = "Eighth Period"
                }
                "9" -> {
                    holder.textPeriodName.text = "Ninth Period"
                }
                "10" -> {
                    holder.textPeriodName.text = "Tenth Period"
                }
            }

            when (storeModel[position].sUBJECTICON) {
                "English.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_english)
                        .into(holder.imageSubject)
                }
                "Chemistry.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_chemistry)
                        .into(holder.imageSubject)
                }
                "Biology.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.imageSubject)
                }
                "Maths.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_maths)
                        .into(holder.imageSubject)
                }
                "Hindi.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_hindi)
                        .into(holder.imageSubject)
                }
                "Physics.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_physics)
                        .into(holder.imageSubject)
                }
                "Malayalam.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_malayalam)
                        .into(holder.imageSubject)
                }
                "Arabic.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_arabic)
                        .into(holder.imageSubject)
                }
                "Accountancy.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_accountancy)
                        .into(holder.imageSubject)
                }
                "Social.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_social)
                        .into(holder.imageSubject)
                }
                "Economics.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_economics)
                        .into(holder.imageSubject)
                }
                "BasicScience.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.imageSubject)
                }
                "Computer.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_computer)
                        .into(holder.imageSubject)
                }
                "General.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_computer)
                        .into(holder.imageSubject)
                }
            }
        }

        override fun getItemCount(): Int {
            return storeModel.size
        }
    }


    class VideoAdapter(var videoDetails: List<DashBoardModel.VideoDetail>, var context: Context) :
        RecyclerView.Adapter<VideoAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewSubject: TextView = view.findViewById(R.id.textView38)
            var textPeriodName: TextView = view.findViewById(R.id.textView37)
            var imageSubject: ImageView = view.findViewById(R.id.imageView23)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.dashboard_video_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewSubject.text = videoDetails[position].sUBJECTNAME
            holder.textPeriodName.text =
                "Total Video : " + videoDetails[position].tOTALVIDEOS.toString()

            when (videoDetails[position].sUBJECTICON) {
                "English.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_english)
                        .into(holder.imageSubject)
                }
                "Chemistry.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_chemistry)
                        .into(holder.imageSubject)
                }
                "Biology.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.imageSubject)
                }
                "Maths.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_maths)
                        .into(holder.imageSubject)
                }
                "Hindi.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_hindi)
                        .into(holder.imageSubject)
                }
                "Physics.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_physics)
                        .into(holder.imageSubject)
                }
                "Malayalam.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_malayalam)
                        .into(holder.imageSubject)
                }
                "Arabic.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_arabic)
                        .into(holder.imageSubject)
                }
                "Accountancy.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_accountancy)
                        .into(holder.imageSubject)
                }
                "Social.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_social)
                        .into(holder.imageSubject)
                }
                "Economics.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_economics)
                        .into(holder.imageSubject)
                }
                "BasicScience.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.imageSubject)
                }
                "Computer.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_computer)
                        .into(holder.imageSubject)
                }
                "General.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_study_computer)
                        .into(holder.imageSubject)
                }
            }
        }

        override fun getItemCount(): Int {
            return videoDetails.size
        }


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

}