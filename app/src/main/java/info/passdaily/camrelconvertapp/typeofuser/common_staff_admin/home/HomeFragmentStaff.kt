package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.home


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
//import com.github.mikephil.charting.components.AxisBase
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hadiidbouk.charts.BarData
import com.hadiidbouk.charts.ChartProgressBar
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentStaffHomeBinding
import info.passdaily.camrelconvertapp.landingpage.firstpage.FirstScreenActivity
import info.passdaily.camrelconvertapp.lib.ClickableViewPager
import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
//import info.passdaily.camrelconvertapp.lib.video.Video_Activity
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.calander.CalenderStaffActivity
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.descriptive_exam.DescriptiveExamStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.exam_topper.ExamTopperFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card.ProgressCardCBSEFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card_msps.ProgressCardMspHsFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.gallery.GalleryStaffFragment
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.gallery.GalleryViewActivity
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.inbox.InboxStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.AssignmentStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_event.ManageStaffEventDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.mark_absent.MarkAbsentActivity
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.mark_absent.MarkMultiAbsentActivity
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.ObjectiveExamStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.online_video.OnlineVideoStaffFragment
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.online_video.OnlineVideoStaffActivity
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.StudyMaterialStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.update_result.PublishResultFragment
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoomGoLive.ZoomGoLiveActivity
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.staff_punch_attendance.StaffAttendanceActivity
import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.YouTubeIframePlayer
import info.passdaily.camrelconvertapp.typeofuser.parent.study_material.StudyMaterialInit
//import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.YouTubePlayerActivity
import java.util.*


@Suppress("DEPRECATION")
class HomeFragmentStaff : Fragment(),HomeClickListener {
    val DELAY_MS: Long = 500
    var TAG = "StaffHomeFragment"
    private var _binding: FragmentStaffHomeBinding? = null
    private val binding get() = _binding!!

    var toolBarClickListener : ToolBarClickListener? = null

    private lateinit var staffHomeViewModel: StaffHomeViewModel

    var shimmerViewContainer : ShimmerFrameLayout? =null
    var nestedScroll : ConstraintLayout? =null

    private lateinit var localDBHelper : LocalDBHelper
    var listArrayGroup = intArrayOf(
        R.drawable.ic_bottom_mark_absent, R.drawable.ic_bottom_go_live,
        R.drawable.ic_bottom_punch_attendance, R.drawable.ic_bottom_online_video,
        R.drawable.ic_bottom_study_material, R.drawable.ic_bottom_home_assignment,
        R.drawable.ic_bottom_objective_exam, R.drawable.ic_bottom_descriptive_exam,
        R.drawable.ic_bottom_progress_card, R.drawable.ic_bottom_result,
        R.drawable.ic_bottom_exam_topper, R.drawable.ic_bottom_current_period
    )


//    var listArrayColor = intArrayOf(
//        R.color.list_array_light_1, R.color.list_array_light_2,
//        R.color.color100_bio, R.color.color100_hindi,
//        R.color.light_purple_100, R.color.orange_light500,
//        R.color.light_pink_100, R.color.light_color_rect_04,
//        R.color.light_aqua_100, R.color.light_yellow_100,
//        R.color.light_purple_200, R.color.light_blue_200
//    )


    var listArrayFront = intArrayOf(
        R.color.dark_blue_100, R.color.color_physics,
        R.color.color_bio, R.color.color_hindi,
        R.color.col_rect_01, R.color.col_rect_02,
        R.color.color_pink_500, R.color.col_rect_04,
        R.color.color_aqua, R.color.color_yellow_500,
        R.color.color_maths, R.color.color_arabic
    )

    //    public int[] ListArrayimage = {R.drawable.pd_item1,
    //            R.drawable.pd_item2, R.drawable.pd_item3,
    //            R.drawable.pd_item4, R.drawable.pd_item5,
    //            R.drawable.pd_item6, R.drawable.pd_item7,
    //            R.drawable.pd_item8, R.drawable.pd_item9,
    //            R.drawable.pd_item10, R.drawable.pd_item11,
    //            R.drawable.pd_item12};
    var listNames = arrayOf(
        "Take Attendance",
        "Go Live", "Staff Attendance",
        "Online Video Class", "Study Material",
        "Homework", "Objective Exam",
        "Descriptive Exam", "Progress Report",
        "Publish Result", "Exam Topper", "Current Period"
    )

    var icon_layer = intArrayOf(
        R.drawable.pd_purple_button,
        R.drawable.pd_yellow_button,
        R.drawable.pd_green_button,
        R.drawable.pd_pink_button,
//        R.drawable.pd_skyblue_button
    )
    var icon_name = arrayOf("Inbox", /*"Notification",*/ "Study", "Gallery", "Calendar")
    var icons = intArrayOf(
        R.drawable.ic_top_button_inbox,
        // R.drawable.ic_pd_notification,
        R.drawable.ic_top_button_study,
        R.drawable.ic_top_button_gallery,
        R.drawable.ic_top_button_calendar
    )

    var reportColor = intArrayOf(
        R.color.pd_report_color1,
        R.color.pd_report_color2,
        R.color.pd_report_color3,
        R.color.pd_report_color6,
//        R.color.pd_report_color4,
//        R.color.pd_report_color5,
        R.color.pd_report_color7
    )
    var reportName = arrayOf("Attendance", "Student", "Staff", "Conveyor", "PTA")
    var reportImages = intArrayOf(
        R.drawable.ic_report_attendance,
        R.drawable.ic_report_student,
        R.drawable.ic_report_staff,
        R.drawable.ic_report_conveyor,
//        R.drawable.report_img4,
//        R.drawable.report_img3,
        R.drawable.ic_report_pta
    )
    var adminId = 0
    var recyclerViewReport: RecyclerView? = null

    var springDotsIndicator: SpringDotsIndicator? = null
    var viewPager: ClickableViewPager? = null
    var viewPagerMessage: ClickableViewPager? = null
    var currentPage = 0
    var currentPage1 = 0

    //  private lateinit var barChart: BarChart
    private lateinit var chartProgressBar : ChartProgressBar

    private var absentList = ArrayList<Score>()

    var dataList =  ArrayList<BarData>()

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

    //#1 Defining a BottomSheetBehavior instance
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.screenState = "landingpage"
        toolBarClickListener?.setToolbarName("Dashboard")

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID

        staffHomeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StaffHomeViewModel::class.java]
        // Inflate the layout for this fragment
        _binding = FragmentStaffHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemList: MutableList<CollapsingRecyclerViewItem> = ArrayList()
        for (i in icons.indices) {
            itemList.add(CollapsingRecyclerViewItem(icons[i], icon_name[i], icon_layer[i], 0))
        }

        binding.recyclerViewButton.adapter = ListExampleAdapter(this, itemList, requireActivity())


        val imageView = binding.bottomSheet.imageViewGif//GlideDrawableImageViewTarget
//        val imageViewTarget = GlideI(imageView)
//        Glide.with(this).load("https://media.giphy.com/media/bly80fXqiKVQ1Lk0cY/giphy.gif")
//            .into(imageView)
        Glide.with(this)
            .load(R.raw.swipe_up_gif)
            .into(imageView);

        shimmerViewContainer = binding.shimmerViewContainer
        nestedScroll = binding.constraintLayout29
//        shimmerViewContainer?.visibility =View.VISIBLE
//        nestedScroll?.visibility =View.GONE

        recyclerViewReport = binding.recyclerViewReport
        springDotsIndicator = binding.springDotsIndicator
        springDotsIndicator?.visibility = View.GONE
        viewPager = binding.viewPager
        viewPagerMessage = binding.viewPagerMessage

        chartProgressBar = binding.chartProgressBar

//        barChart =  binding.barChart
//
//        barChart.axisLeft?.setDrawGridLines(false)
//        val xAxis: XAxis = barChart.xAxis
//        xAxis.setDrawGridLines(false)
//        xAxis.setDrawAxisLine(false)
//        //remove right y-axis
//        barChart.axisRight.isEnabled = false
//
//        //remove legend
//        barChart.legend.isEnabled = false
//        //remove description label
//        barChart.description.isEnabled = false
//        //add animation
//        barChart.animateY(1000)
//        // to draw label on xAxis
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.valueFormatter = MyAxisFormatter()
//        xAxis.granularity = 1f; // only intervals of 1 day
//        xAxis.setDrawLabels(true)
//        xAxis.textSize = 12f;

        val display: Display = requireActivity().windowManager.defaultDisplay
        val width: Int = display.width // deprecated
        val height: Int = display.height


        val scroller = binding.nestedScroll

//        scroller.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
////            Log.i(TAG, "scrollY $scrollY")
////            Log.i(TAG, "oldScrollY $oldScrollY")
//            Log.i(TAG, "actual scrollY $scrollY")
//            Log.i(TAG, "height $height")
//
//            Log.i(TAG, "screen_size ${resources.getString(R.string.screen_size)}")
//
//            Log.i(TAG, "actual scrollY $scrollY")
//
//            if (scrollY in 940..1050) {
//                Log.i(TAG, "Visibility Gone $scrollY")
//            }
//
//            if (scrollY in 1050 downTo 1000) {
//                Log.i(TAG, "Visibility Visible $scrollY")
//            }
//
//            if (scrollY > oldScrollY) {
//               // Log.i(TAG, "oldScrollY $oldScrollY")
//            }
//            if (scrollY < oldScrollY) {
//                Log.i(TAG, "Scroll UP")
//            }
//            if (scrollY == 0) {
//                Log.i(TAG, "TOP SCROLL")
//            }
//            if (scrollY == v.measuredHeight - v.getChildAt(0).measuredHeight) {
//                Log.i(TAG, "BOTTOM SCROLL")
//            }
////            Log.i(TAG, "v.measuredHeight ${v.measuredHeight}")
////            Log.i(TAG, "v.measuredHeight - v.getChildAt(0).measuredHeight ${v.measuredHeight - v.getChildAt(0).measuredHeight}")
////            Log.i(TAG, "v.getChildAt(0).measuredHeight ${v.getChildAt(0).measuredHeight}")
//        })



        //#2 Initializing the BottomSheetBehavior
        var bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetDashboard)

        //#3 Listening to State Changes of BottomSheet
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                buttonBottomSheetPersistent.text = when (newState) {
//                    BottomSheetBehavior.STATE_EXPANDED -> "Close Persistent Bottom Sheet"
//                    BottomSheetBehavior.STATE_COLLAPSED -> "Open Persistent Bottom Sheet"
//                    else -> "Persistent Bottom Sheet"
//                }
            }
        })

        //#4 Changing the BottomSheet State on ButtonClick
        binding.bottomSheet.bottomSheetButton.setOnClickListener {
            val state =
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_COLLAPSED
                else
                    BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.state = state
        }


        val recyclerViewList = binding.recyclerViewList
        recyclerViewList.layoutManager = GridLayoutManager(requireActivity(), 3)

        val bottomRecyclerView  = binding.bottomSheet.recyclerViewList
        bottomRecyclerView.layoutManager = GridLayoutManager(requireActivity(), 3)

        val recyclerViewItemArrayList: MutableList<CollapsingRecyclerViewItem> = ArrayList()
        for (i in listArrayGroup.indices) {                                                               //listagroup Array
            recyclerViewItemArrayList.add(
                CollapsingRecyclerViewItem(
                    listArrayGroup[i], listNames[i],
                    listArrayFront[i], 0
                )
            )
        }
        val mListArrayAdapter =
            ListArrayAdapter(this,recyclerViewItemArrayList, requireActivity())
        recyclerViewList.adapter = mListArrayAdapter
        bottomRecyclerView.adapter = mListArrayAdapter

        val animFadeIn: Animation =
            AnimationUtils.loadAnimation(requireActivity().applicationContext, R.anim.fade_in)
        val animFadeOut: Animation =
            AnimationUtils.loadAnimation(requireActivity().applicationContext, R.anim.fade_out)

        val scrollBounds = Rect()
        scroller.getHitRect(scrollBounds)
        scroller.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (binding.linearayout1.getLocalVisibleRect(scrollBounds)) {
//                if (!binding.linearayout1.getLocalVisibleRect(scrollBounds)
//                    || scrollBounds.height() < binding.constraintLayout9.height
//                ) {
//                    Log.i(TAG, "BTN APPEAR PARCIALY")
                binding.bottomSheet.bottomSheetDashboard.visibility =View.VISIBLE
//                  //  binding.bottomSheet.bottomSheetDashboard.startAnimation(animFadeOut);
//                } else {
//                    Log.i(TAG, "BTN APPEAR FULLY!!!")
//                    binding.bottomSheet.bottomSheetDashboard.visibility =View.VISIBLE
//                  //  binding.bottomSheet.bottomSheetDashboard.startAnimation(animFadeIn);
//                }
            } else  if (binding.linearLayout4.getLocalVisibleRect(scrollBounds)) {
                binding.bottomSheet.bottomSheetDashboard.visibility =View.VISIBLE
//recyclerViewReport
            }else  if (binding.recyclerViewReport.getLocalVisibleRect(scrollBounds)) {
                binding.bottomSheet.bottomSheetDashboard.visibility = View.VISIBLE
            } else {
                binding.bottomSheet.bottomSheetDashboard.visibility =View.INVISIBLE
                Log.i(TAG, "No")
            }
        })

        binding.constraintLayoutDownload.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, StudyMaterialStaffFragment()).commit()
        }

        var textViewAll = binding.textViewAll

        textViewAll.setOnClickListener {
            val dialog1 = ManageStaffEventDialog()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, ManageStaffEventDialog.TAG)
        }


        initFunction()
    }

    private fun initFunction() {
        staffHomeViewModel.getDashBoardStaff(adminId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            var yOUTUBELINK = ""
                            var profileDetails = response.profileDetails
                            for(i in profileDetails.indices) {
                                toolBarClickListener?.setDrawerImage(profileDetails[i].sTAFFIMAGE!!,
                                    profileDetails[i].sTAFFFNAME,profileDetails[i].aDMINROLENAME)
                                yOUTUBELINK = profileDetails[i].yOUTUBELINK!!
                            }

                            binding.constraintYouTube.setOnClickListener {
                                if(yOUTUBELINK.isNotEmpty()) {
                                    Toast.makeText(requireActivity(), "Opening YouTube", Toast.LENGTH_SHORT).show()
                                    var intent = Intent(Intent.ACTION_VIEW, Uri.parse(yOUTUBELINK));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setPackage("com.google.android.youtube");
                                    startActivity(intent)
                                }
                            }


                            if (response.tiles.aDMINSTATUS == 1) {
                                val attendance = "Attendance \n${response.tiles.tOTALATTENDANCETAKEN} / " +
                                        "${response.tiles.tOTALCLASS}"
                                val student = "Student \n${response.tiles.tOTALSTUDENT}"
                                val staff = "Staff \n${response.tiles.tOTALSTAFF}"
                                val pta = "PTA \n${response.tiles.tOTALPTA}"
                                val leave = "Leave \n${response.tiles.tOTALLEAVENOTE}"
                                val enquiry = "Enquiry \n${response.tiles.tOTALENQUIRY}"
                                val conveyors = "Conveyors \n${response.tiles.tOTALCONVEYORS}"
                                // val reportName = arrayOf(attendance, student, staff, pta, leave, enquiry, conveyors)

                                val reportCount = arrayOf( response.tiles.tOTALATTENDANCETAKEN,
                                    response.tiles.tOTALSTUDENT, response.tiles.tOTALSTAFF,
                                    response.tiles.tOTALCONVEYORS,  response.tiles.tOTALPTA)
                                val report: MutableList<CollapsingRecyclerViewItem> = ArrayList()
                                for (i in reportName.indices) {
                                    report.add(
                                        CollapsingRecyclerViewItem(
                                            reportImages[i],
                                            reportName[i],
                                            reportCount[i],
                                            0
                                        )
                                    )
                                }
                                // recyclerViewReport.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
                                recyclerViewReport!!.layoutManager = GridLayoutManager(
                                    recyclerViewReport!!.context,
                                    1,
                                    GridLayoutManager.HORIZONTAL,
                                    false
                                )
                                val spacingR =
                                    resources.getDimensionPixelSize(R.dimen.text_size_03)
                                // final int spacing = getResources().getDimensionPixelSize(R.dimen.zm_ui_kit_normal_radius) / 2; // apply spacing

                                recyclerViewReport!!.setPadding(0, spacingR, 1, spacingR)
                                recyclerViewReport!!.clipToPadding = false
                                recyclerViewReport!!.clipChildren = false
                                recyclerViewReport!!.addItemDecoration(object : ItemDecoration() {
                                    override fun getItemOffsets(
                                        outRect: Rect, view: View,
                                        parent: RecyclerView, state: RecyclerView.State
                                    ) {
                                        outRect[spacingR, spacingR, spacingR] = spacingR
                                    }
                                })

                                if (isAdded) {
                                    recyclerViewReport!!.adapter = ReportRecyclerViewDataAdapter(
                                        this,
                                        report,
                                        requireActivity(),
                                        TAG
                                    )
                                }

                            }
                            else{
                                localDBHelper.deleteUserID(adminId)
                                localDBHelper.deleteData(requireActivity())
                                val log = Intent(requireActivity(), FirstScreenActivity::class.java)
                                startActivity(log)
                                requireActivity().finish()
                            }

                            val inboxList = response.inboxList
                            try {
                                val mAdapter = InboxListPager(requireActivity(), inboxList)
                                if (viewPagerMessage != null) {
                                    viewPagerMessage!!.adapter = mAdapter
                                }

                                /*After setting the adapter use the timer */
                                val handler1 = Handler()
                                val Update1 = Runnable {
                                    if (currentPage1 == inboxList.size) {
                                        currentPage1 = 0
                                    }
                                    viewPagerMessage!!.setCurrentItem(currentPage1++, true)
                                }
                                try {
                                    val timer1 = Timer() // This will create a new Thread
                                    timer1.schedule(object : TimerTask() {
                                        // task to be scheduled
                                        override fun run() {
                                            handler1.post(Update1)
                                        }
                                    }, DELAY_MS, 5000)
                                } catch (e: Exception) {
                                    Log.i(TAG, "Exception $e")
                                }
                            } catch (e: Exception) { Log.i(TAG, "Exception $e") }

                            val eventList = response.eventList
                            for(i in eventList.indices){
                                Global.academicId = eventList[i].aCCADEMICID
                            }
                            try {
                                val mCustomPagerAdapter = CustomPagerAdapter(requireActivity(), eventList,TAG)
                                if (viewPager != null && springDotsIndicator != null) {
                                    Log.i(TAG, "coming")
                                    viewPager!!.adapter = mCustomPagerAdapter
                                    springDotsIndicator!!.setViewPager(viewPager!!)
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
                                    }, DELAY_MS, 12000)
                                } catch (e: java.lang.Exception) {
                                    handler.removeCallbacks(Update)
                                }
                            } catch (e: Exception) { Log.i(TAG, "Exception $e") }


                            dataList =  ArrayList<BarData>()

                            dataList.add(BarData("01", 3.4f, "3.4"))//€

                            dataList.add(BarData("02", 8f, "8"))

                            dataList.add(BarData("03", 1.8f, "1.8"))

                            dataList.add(BarData("04", 7.3f, "7.3"))

                            dataList.add(BarData("05", 6.2f, "6.2"))

                            dataList.add(BarData("06", 3.3f, "3.3"))

                            dataList.add(BarData("07", 5.3f, "5.3"))

                            dataList.add(BarData("08", 9f, "9"))

                            dataList.add(BarData("09", 2f, "2"))

                            dataList.add(BarData("10", 3.3f, "3.3"))

                            dataList.add(BarData("11", 8f, "8"))

                            dataList.add(BarData("12", 1.8f, "1.8"))

                            dataList.add(BarData("13", 7.3f, "7.3"))

                            dataList.add(BarData("14", 6.2f, "6.2"))
//
                            dataList.add(BarData("15", 3.3f, "3.3"))
//
//                            dataList.add(BarData("16", 8f, "8"))
//
//                            dataList.add(BarData("17", 1.8f, "1.8"))
//
//                            dataList.add(BarData("18", 7.3f, "7.3"))
////
//                            dataList.add(BarData("19", 6.2f, "6.2"))
//
//                            dataList.add(BarData("20", 3.3f, "3.3"))
//
//                            dataList.add(BarData("21", 8f, "8"))
//
//                            dataList.add(BarData("22", 1.8f, "1.8"))
//
//                            dataList.add(BarData("23", 7.3f, "7.3"))
////
//                            dataList.add(BarData("24", 6.2f, "6.2"))
//
//                            dataList.add(BarData("25", 3.3f, "3.3"))
//
//                            dataList.add(BarData("26", 8f, "8"))
//
//                            dataList.add(BarData("27", 1.8f, "1.8"))
//
//                            dataList.add(BarData("28", 7.3f, "7.3"))
////
//                            dataList.add(BarData("29", 6.2f, "6.2"))
//
//                            dataList.add(BarData("30", 3.3f, "3.3"))


                            if(dataList.size >= 31){
                                binding.constraintBarChart.layoutParams.width = 3000
                            } else if(dataList.size >= 25){
                                binding.constraintBarChart.layoutParams.width = 2600
                            } else if(dataList.size >= 20){
                                binding.constraintBarChart.layoutParams.width = 2000
                            }else if(dataList.size >= 15){
                                binding.constraintBarChart.layoutParams.width = 1600
                            }else if(dataList.size >= 10){
                                binding.constraintBarChart.layoutParams.width = 1000
                            }


                            chartProgressBar.setDataList(dataList)
                            chartProgressBar.build()


//                            absentList = ArrayList<Score>()
//
//                            absentList.add(Score("01",5.toFloat()))
//                            absentList.add(Score("02",6.toFloat()))
//                            absentList.add(Score("03",4.toFloat()))
//                            absentList.add(Score("04",7.toFloat()))
//                            absentList.add(Score("05",2.toFloat()))
//                            absentList.add(Score("06",8.toFloat()))
//                            absentList.add(Score("07",4.toFloat()))
//                            absentList.add(Score("08",6.toFloat()))
//                            absentList.add(Score("09",1.toFloat()))
//                            absentList.add(Score("10",9.toFloat()))
//                            absentList.add(Score("11",8.toFloat()))
//                            absentList.add(Score("12",4.toFloat()))
//                            absentList.add(Score("13",6.toFloat()))
//                            absentList.add(Score("14",1.toFloat()))
//                            absentList.add(Score("15",9.toFloat()))
//                            absentList.add(Score("16",8.toFloat()))
//                            absentList.add(Score("17",4.toFloat()))
//                            absentList.add(Score("18",6.toFloat()))
//                            absentList.add(Score("19",1.toFloat()))
//                            absentList.add(Score("20",9.toFloat()))
//
//
//                            //now draw bar chart with dynamic data
//                            val entries: ArrayList<BarEntry> = ArrayList()
//                            //you can replace this data object with  your custom object
//                            for (i in absentList.indices) {
//                                val score = absentList[i]
//                                Log.i(TAG, "i.toFloat() ${i.toFloat()}")
//                                Log.i(TAG, "score ${score.score}")
//                                entries.add(BarEntry(i.toFloat(), score.score))
//                            }
//                            val barDataSet = BarDataSet(entries, "")
//                            if (isAdded) { //////checking context of activity is null
//                                barDataSet.setColors(mContext!!.resources.getColor(R.color.color_bio))
//                            }
//                            val data2 = BarData(barDataSet)
//                            barChart.data = data2
//                            barChart.invalidate()

//                            progressStop()
                            shimmerViewContainer?.visibility =View.GONE
                            nestedScroll?.visibility =View.VISIBLE
                            binding.bottomSheet.bottomSheetDashboard.visibility =View.VISIBLE
                        }
                        Status.ERROR -> {
                            // progressStop()
                            shimmerViewContainer?.visibility =View.GONE
                            nestedScroll?.visibility =View.VISIBLE
                            binding.bottomSheet.bottomSheetDashboard.visibility =View.VISIBLE
                            Log.i(TAG, "resource ${resource.status}")
                        }
                        Status.LOADING -> {
                            shimmerViewContainer?.visibility =View.VISIBLE
                            nestedScroll?.visibility =View.GONE
                            binding.bottomSheet.bottomSheetDashboard.visibility =View.GONE
                            //progressStart()
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })
    }


    private class ListArrayAdapter(
        var homeClickListener: HomeClickListener,
        var recyclerViewItemArrayList: List<CollapsingRecyclerViewItem>,
        var context: Context
    ) :
        RecyclerView.Adapter<ListArrayAdapter.ViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.pd_arraylist, viewGroup, false)
            )
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
            //    viewHolder.imageViewImage.setImageResource(recyclerViewItemArrayList[i].icon_front)
            viewHolder.textViewTitle.text = recyclerViewItemArrayList[i].name


            Glide.with(context)
                .load(recyclerViewItemArrayList[i].icon)
                .into(viewHolder.imageViewIcon)
            viewHolder.cardViewItem.setCardBackgroundColor(context.resources.getColor(recyclerViewItemArrayList[i].icon_layout))
            val generatedColor = Utils.generateTransparentColor(context.resources.getColor(recyclerViewItemArrayList[i].icon_layout), 0.07)
            viewHolder.cardViewBackground.setCardBackgroundColor(generatedColor)
//           viewHolder.cardViewBackground.setCardBackgroundColor(context.resources.getColor(R.color.color100_physics))

            viewHolder.itemView.setOnClickListener {
                homeClickListener.onBottomListClicker(i)
            }


        }

        override fun getItemCount(): Int {
            return recyclerViewItemArrayList.size
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageViewIcon: ImageView = view.findViewById<View>(R.id.imageViewIcon) as ImageView
            var textViewTitle: TextView = view.findViewById<View>(R.id.textViewTitle) as TextView

            var cardViewItem : CardView = view.findViewById<View>(R.id.cardViewItem) as CardView
            var cardViewBackground : CardView = view.findViewById<View>(R.id.cardViewBackground) as CardView

        }
    }


    private class ReportRecyclerViewDataAdapter(
        var homeClickListener : HomeClickListener,
        var itemList: List<CollapsingRecyclerViewItem>,
        var context: Context,
        var TAG : String
    ) :
        RecyclerView.Adapter<ReportRecyclerViewDataAdapter.ViewHolder>() {
        override fun getItemViewType(i: Int): Int {
            return i
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
            val inflate: View = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.pd_report_layout, viewGroup, false)
            inflate.layoutParams.width = screenWidth / 3
            return ViewHolder(inflate)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
//            viewHolder.relativeLayout2.setBackgroundResource(itemList[position].icon_layout)
//            viewHolder.relativeLayout2.alpha = 0.9f
            Glide.with(context)
                .load(itemList[position].icon)
                .into(viewHolder.imageViewIcon)
            viewHolder.textViewTitle.text = itemList[position].name
            viewHolder.textViewCount.text = itemList[position].icon_layout.toString()

            viewHolder.itemView.setOnClickListener {

                homeClickListener.onListClicker(position)

            }
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageViewIcon: ImageView = view.findViewById<View>(R.id.imageViewIcon) as ImageView
            // var relativeLayout2: RelativeLayout = view.findViewById<View>(R.id.relativeLayout2) as RelativeLayout
            var textViewTitle: TextView = view.findViewById<View>(R.id.textViewTitle) as TextView
            var textViewCount : TextView = view.findViewById<View>(R.id.textViewCount) as TextView

        }

        val screenWidth: Int
            get() {
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display = wm.defaultDisplay
                val size = Point()
                display.getSize(size)
                return size.x
            }

    }

    private class InboxListPager(
        var context: Context,
        var feedlist: List<DashboardStaffModel.Inbox>
    ) :
        PagerAdapter() {
        var  mLayoutInflater  = context.applicationContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        override fun getCount(): Int {
            return feedlist.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj as ConstraintLayout
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view: View = mLayoutInflater.inflate(R.layout.newinboxlist, container, false)
            val text1 = view.findViewById<View>(R.id.text1) as TextView
            val text2 = view.findViewById<View>(R.id.text2) as TextView
            val text3 = view.findViewById<View>(R.id.text3) as TextView
            text1.text = feedlist[position].vIRTUALMAILTITLE
            text2.text = feedlist[position].vIRTUALMAILCONTENT
            val date = Objects.requireNonNull(feedlist[position].vIRTUALMAILSENTDATE).split("T")
                .toTypedArray()
            val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
            text3.text = Utils.formattedDate(dddd)
            container.addView(view)
            return view
        }

        override fun destroyItem(viewGroup: ViewGroup, i: Int, obj: Any) {
            viewGroup.removeView(obj as ConstraintLayout)
        }
    }


    class CustomPagerAdapter(
        var context: Activity,
        var mResources: List<DashboardStaffModel.Event>,
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
                    val split2 = mResources[position].eVENTLINKFILE.split("=").toTypedArray()
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

    override fun onListClicker(position: Int) {
        when(position){
            0 -> {
                val intent = Intent(context, MarkAbsentActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                requireActivity().startActivity(intent)
            }
            1 -> {
                val intent = Intent(context, StudentActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                requireActivity().startActivity(intent)
            }
            2 -> {
                val intent = Intent(context, StaffActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                requireActivity().startActivity(intent)
            }
            3 ->{
                val intent = Intent(context, ConveyorActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                requireActivity().startActivity(intent)
            }
            4 -> {
                val intent = Intent(context, PtaActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                requireActivity().startActivity(intent)
            }
        }
    }

    override fun onButtonClicker(position: Int) {
        when(position){
            0 -> {
                var fragmentManager =  requireActivity().supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, InboxStaffFragment()).commit()
            }
            1 -> {
                var fragmentManager =  requireActivity().supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, StudyMaterialStaffFragment()).commit()
            }
            2 -> {
//                val intent = Intent(context, GalleryViewActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                requireActivity().startActivity(intent)
                var fragmentManager =  requireActivity().supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, GalleryStaffFragment()).commit()
            }
            ///Gallery Fragment
//            3 -> {
//                var fragmentManager =  requireActivity().supportFragmentManager
//                var fragmentTransaction = fragmentManager.beginTransaction()
//                fragmentTransaction.replace(R.id.nav_host_fragment, StudyMaterialStaffFragment()).commit()
//            }

            3 -> {
                val intent = Intent(context, CalenderStaffActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                requireActivity().startActivity(intent)
//                val log = Intent(requireActivity(), CalenderStaffActivity::class.java)
//                startActivity(log)
            }
        }
    }

    override fun onBottomListClicker(position: Int) {
        when(position) {
            0 -> {
                val log = Intent(requireActivity(), MarkMultiAbsentActivity::class.java)
                startActivity(log)
            }
            1 -> {
//                val log = Intent(requireActivity(), ZoomGoLiveActivity::class.java)
//                startActivity(log)
            }
            2 -> {
                val log = Intent(requireActivity(), StaffAttendanceActivity::class.java)
                startActivity(log)
//                var fragmentManager =  requireActivity().supportFragmentManager
//                var fragmentTransaction = fragmentManager.beginTransaction()
//                fragmentTransaction.replace(R.id.nav_host_fragment, ZoomScheduledFragment()).commit()
            }
            3 -> {
//                val log = Intent(requireActivity(), OnlineVideoStaffActivity::class.java)
//                startActivity(log)

                var fragmentManager =  requireActivity().supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, OnlineVideoStaffFragment()).commit()
            }
            4 -> {
                var fragmentManager =  requireActivity().supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, StudyMaterialStaffFragment()).commit()
            }
            5 -> {
                var fragmentManager =  requireActivity().supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, AssignmentStaffFragment()).commit()
            }
            6 -> {
                var fragmentManager =  requireActivity().supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, ObjectiveExamStaffFragment()).commit()
            }
            7 -> {
                var fragmentManager =  requireActivity().supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, DescriptiveExamStaffFragment()).commit()
            }
            8 -> {
                var fragmentManager =  requireActivity().supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardCBSEFragment()).commit()
            }
            9 -> {
                var fragmentManager =  requireActivity().supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, PublishResultFragment()).commit()
            }

            10 -> {
                var fragmentManager =  requireActivity().supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment,
                    ExamTopperFragment("MarkLnvn/ExamTopper", 2,"Exam Topper Cbse")).commit()
            }
//            11 -> {
//                var fragmentManager =  requireActivity().supportFragmentManager
//                var fragmentTransaction = fragmentManager.beginTransaction()
//                fragmentTransaction.replace(R.id.nav_host_fragment, PublishResultFragment()).commit()
//            }

        }
    }

//    inner class MyAxisFormatter : IndexAxisValueFormatter() {
//        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//            val index = value.toInt()
//            //Log.i(TAG, "getAxisLabel: index $index")
//            return if (index < absentList.size) {
//                absentList[index].name
//            } else {
//                ""
//            }
//        }
//    }

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
data class Score(
    val name: String,
    val score: Float,
)



private class ListExampleAdapter(
    var homeClickListener : HomeClickListener,
    var list: List<CollapsingRecyclerViewItem>,context: Context) : BaseAdapter() {

    private val mInflator: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val vh: ListRowHolder
        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.pd_button_layout, parent, false)
            vh = ListRowHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }

        vh.textTest.text = list[position].name
        vh.imageRelative.setBackgroundResource(list[position].icon_layout)
        vh.buttonImageView.setImageResource(list[position].icon)

        vh.constraintLayout.setOnClickListener {
            homeClickListener.onButtonClicker(position)
        }

        return view
    }



}

private class ListRowHolder(row: View?) {
    val textTest: TextView = row?.findViewById(R.id.textTest) as TextView
    val imageRelative: RelativeLayout = row?.findViewById(R.id.imageRelative) as RelativeLayout
    val buttonImageView: ImageView = row?.findViewById(R.id.buttonImageView) as ImageView
    val constraintLayout : ConstraintLayout = row?.findViewById(R.id.constraintLayout) as ConstraintLayout
}

class CollapsingRecyclerViewItem(
    var icon: Int,
    var name: String,
    var icon_layout: Int,
    var icon_front: Int
)

interface HomeClickListener{
    fun onListClicker(position: Int)

    fun onButtonClicker(position: Int)

    fun onBottomListClicker(position: Int)
}