package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_live_class_report

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
import info.passdaily.camrelconvertapp.databinding.ActivityObjExamDetailsBinding
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
class ZoomMeetingAttendedActivity : AppCompatActivity() {

    var TAG = "ZoomMeetingAttendedActivity"
    private lateinit var binding: ActivityObjExamDetailsBinding

    var Z_LIVE_CLASS_ID = 0
    var ACCADEMIC_ID = 0
    var CLASS_ID = 0

    var toolbar: Toolbar? = null
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var constraintLayoutContent : ConstraintLayout? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null
    var unAttendedListModel = ArrayList<UnAttendedListModel.UnAttended>()

    var meetingAttendedList = ArrayList<ZoomMeetingAttendedListModel.MeetingAttended>()

    private lateinit var liveClassReportViewModel: LiveClassReportViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        if (extras != null) {
            Z_LIVE_CLASS_ID = extras.getInt("Z_LIVE_CLASS_ID")
            ACCADEMIC_ID = extras.getInt("ACCADEMIC_ID")
            CLASS_ID = extras.getInt("CLASS_ID")
        }

        liveClassReportViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[LiveClassReportViewModel::class.java]

        binding = ActivityObjExamDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Meeting Report"
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

        initFunction(Z_LIVE_CLASS_ID)
        Utils.setStatusBarColor(this)
    }
    private fun initFunction(zLiveClassId : Int) {
        liveClassReportViewModel.getMeetingAttendedReport(zLiveClassId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            meetingAttendedList = response.meetingAttendedList as ArrayList<ZoomMeetingAttendedListModel.MeetingAttended>
                            getUnAttendedList(ACCADEMIC_ID,CLASS_ID,zLiveClassId)

                            Log.i(TAG,"getMeetingAttendedReport SUCCESS")
                        }
                        Status.ERROR -> {
                            getTabResult()
                            Log.i(TAG,"getMeetingAttendedReport ERROR")
                        }
                        Status.LOADING -> {
                            meetingAttendedList = ArrayList<ZoomMeetingAttendedListModel.MeetingAttended>()
                            Log.i(TAG,"getMeetingAttendedReport LOADING")
                        }
                    }
                }
            })

    }

    private fun getUnAttendedList(accademicId: Int, classId: Int, zLiveClassId: Int) {

        liveClassReportViewModel.getUnAttendedZoomReport(accademicId,classId,zLiveClassId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            unAttendedListModel = response.unAttendedList as ArrayList<UnAttendedListModel.UnAttended>
                            getTabResult()

                            Log.i(TAG,"getUnAttendedList SUCCESS")
                        }
                        Status.ERROR -> {
                            getTabResult()
                            Log.i(TAG,"getUnAttendedList ERROR")
                        }
                        Status.LOADING -> {
//                            submittedDetails = ArrayList<AssignmentDetailsStaffModel.SubmittedDetail>()
//                            staffAttachmentList = ArrayList<AssignmentDetailsStaffModel.StaffAttachment>()
                            Log.i(TAG,"getUnAttendedList LOADING")
                        }
                    }
                }
            })

    }

    private fun getTabResult() {

        val adapter = Global.MyPagerAdapter(supportFragmentManager)
        adapter.addFragment(
            AttendedTabFragment(meetingAttendedList),
            resources.getString(R.string.attended)
        )
        adapter.addFragment(
            ObjUnAttendedTAbFragment(unAttendedListModel),
            resources.getString(R.string.unattended)
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
}