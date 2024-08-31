package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.online_video

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityVideoReportBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.ObjUnAttendedTAbFragment
import java.util.ArrayList

@Suppress("DEPRECATION")
class VideoReportActivity : AppCompatActivity(),VideoViewedListener {

    var TAG = "VideoReportActivity"
    private lateinit var binding: ActivityVideoReportBinding

    private lateinit var onlineVideoStaffViewModel: OnlineVideoStaffViewModel

    var unAttendedListModel = ArrayList<UnAttendedListModel.UnAttended>()
    var YOUTUBE_ID = 0
    var ACCADEMIC_ID = 0
    var CLASS_ID = 0

    var toolbar: Toolbar? = null
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var youtubeDetails = ArrayList<YoutubeReportModel.YoutubeDetail>()
    var youtubeLogList = ArrayList<YoutubeReportModel.YoutubeLog>()

    ///full Log
    var youtubeFullLogs = ArrayList<YoutubeFullLogsModel.YoutubeFullLog>()

    var constraintLayoutContent : ConstraintLayout? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_report)

        val extras = intent.extras
        if (extras != null) {
            YOUTUBE_ID = extras.getInt("YOUTUBE_ID")
            ACCADEMIC_ID = extras.getInt("ACCADEMIC_ID")
            CLASS_ID = extras.getInt("CLASS_ID")
        }


        onlineVideoStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[OnlineVideoStaffViewModel::class.java]

        binding = ActivityVideoReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Online Video Report"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }

        constraintLayoutContent = binding.constraintLayoutContent
        shimmerViewContainer= binding.shimmerViewContainer
        constraintLayoutContent?.visibility = View.GONE
        shimmerViewContainer?.visibility = View.VISIBLE


        viewPager = binding.pager
        tabLayout = binding.tabLayout

        initFunction(YOUTUBE_ID)
        Utils.setStatusBarColor(this)
    }

    @SuppressLint("SetTextI18n")
    private fun initFunction(youtubeId: Int) {

        onlineVideoStaffViewModel.getYoutubeReport(youtubeId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            youtubeDetails = response.youtubeDetails as ArrayList<YoutubeReportModel.YoutubeDetail>
                            for(position in youtubeDetails.indices){
                                binding.textViewTitle.text = youtubeDetails[position].yOUTUBETITLE
                                binding.textSubject.text = "Subject : ${youtubeDetails[position].sUBJECTNAME}"

                                binding.textViewDesc.text = youtubeDetails[position].yOUTUBEDESCRIPTION
                                binding.textViewDesc.apply {9
                                    setShowingLine(2)
                                    setShowMoreTextColor(resources.getColor(R.color.green_300))
                                    setShowLessTextColor(resources.getColor(R.color.green_300))
                                }

                                if(!youtubeDetails[position].yOUTUBEDATE.isNullOrBlank()) {
                                    val date: Array<String> =
                                        youtubeDetails[position].yOUTUBEDATE.split("T".toRegex()).toTypedArray()
                                    val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                                    binding.textViewDate.text = Utils.formattedDateTime(dddd)
                                }


                            }

                            youtubeLogList = response.youtubeLogList as ArrayList<YoutubeReportModel.YoutubeLog>
                            getUnPlayedVideoList(ACCADEMIC_ID,CLASS_ID,YOUTUBE_ID)

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            getTabResult()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            youtubeDetails = ArrayList<YoutubeReportModel.YoutubeDetail>()
                            youtubeLogList = ArrayList<YoutubeReportModel.YoutubeLog>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })


    }



    private fun getUnPlayedVideoList(accademicId: Int, classId: Int, youtubeId: Int) {

        onlineVideoStaffViewModel.getUnPlayedVideoList(accademicId,classId,youtubeId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            unAttendedListModel = response.unAttendedList as ArrayList<UnAttendedListModel.UnAttended>
                            getTabResult()

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            getTabResult()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
//                            submittedDetails = ArrayList<AssignmentDetailsStaffModel.SubmittedDetail>()
//                            staffAttachmentList = ArrayList<AssignmentDetailsStaffModel.StaffAttachment>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })

    }


    private fun getTabResult() {

        val adapter = Global.MyPagerAdapter(supportFragmentManager)
        adapter.addFragment(
            YoutubeViewedTabFragment( this,youtubeLogList),
            resources.getString(R.string.viewed)
        )
        adapter.addFragment(
            ObjUnAttendedTAbFragment(unAttendedListModel),
            resources.getString(R.string.unplayed)
        )
        // adapter.addFragment(new DMKOfficial(), "Tweets");
        constraintLayoutContent?.visibility = View.VISIBLE
        shimmerViewContainer?.visibility = View.GONE
        viewPager?.adapter = adapter
        viewPager?.currentItem = 0
        tabLayout?.setupWithViewPager(viewPager)

        viewPager?.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                tabLayout
            )
        )
        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }

    override fun onLogsClicker(youtubeLogList: YoutubeReportModel.YoutubeLog) {
       Log.i(TAG,"youtubeLogList $youtubeLogList")

        onlineVideoStaffViewModel.getYoutubeFullLog(youtubeLogList.yOUTUBELOGID)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            youtubeFullLogs = response.youtubeFullLogs as ArrayList<YoutubeFullLogsModel.YoutubeFullLog>

                            if (youtubeFullLogs.size != 0) {
                                val bottomSheet = BottomSheetYoutubeFullLog(youtubeLogList.sTUDENTNAME,youtubeFullLogs)
                                bottomSheet.show(supportFragmentManager, "TAG")
                            } else {
                                Utils.getSnackBarGreen(this,"No Log Found",constraintLayoutContent!!)
                            }
                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            getTabResult()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            youtubeFullLogs = ArrayList<YoutubeFullLogsModel.YoutubeFullLog>()
//                            staffAttachmentList = ArrayList<AssignmentDetailsStaffModel.StaffAttachment>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })


    }
}


interface VideoViewedListener{
    fun onLogsClicker(youtubeLogList: YoutubeReportModel.YoutubeLog)
}