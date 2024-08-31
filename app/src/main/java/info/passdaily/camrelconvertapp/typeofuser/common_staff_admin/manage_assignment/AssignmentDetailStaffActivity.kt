package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment

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
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album.BottomSheetUpdateAlbum
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.ObjDetailsTabFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.ObjUnAttendedTAbFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.ObjectiveExamStaffViewModel
import java.util.ArrayList

@Suppress("DEPRECATION")
class AssignmentDetailStaffActivity : AppCompatActivity() {

    var TAG = "AssignmentDetailStaffActivity"
    private lateinit var binding: ActivityObjExamDetailsBinding

    var ASSIGNMENT_ID = 0
    var ACCADEMIC_ID = 0
    var CLASS_ID = 0

    var toolbar: Toolbar? = null
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null

    var constraintLayoutContent : ConstraintLayout? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null
    var unAttendedListModel = ArrayList<UnAttendedListModel.UnAttended>()
    private lateinit var assignmentDetails: AssignmentDetailsStaffModel.AssignmentDetails
    var staffAttachmentList = ArrayList<AssignmentDetailsStaffModel.StaffAttachment>()
    var submittedDetails = ArrayList<AssignmentDetailsStaffModel.SubmittedDetail>()



    private lateinit var assignmentStaffViewModel: AssignmentStaffViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        if (extras != null) {
            ASSIGNMENT_ID = extras.getInt("ASSIGNMENT_ID")
            ACCADEMIC_ID = extras.getInt("ACCADEMIC_ID")
            CLASS_ID = extras.getInt("CLASS_ID")
        }

        assignmentStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[AssignmentStaffViewModel::class.java]

        binding = ActivityObjExamDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Assignment Details"
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

        initFunction(ASSIGNMENT_ID)
        Utils.setStatusBarColor(this)
    }
    private fun initFunction(AssignmentId : Int) {
        assignmentStaffViewModel.getAssignmentSubmissionList(AssignmentId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            assignmentDetails = response.assignmentDetails
                            submittedDetails = response.submittedDetails as ArrayList<AssignmentDetailsStaffModel.SubmittedDetail>
                            staffAttachmentList = response.staffAttachmentList as ArrayList<AssignmentDetailsStaffModel.StaffAttachment>
                            getUnAttendedList(ACCADEMIC_ID,CLASS_ID,ASSIGNMENT_ID)

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            getTabResult()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            submittedDetails = ArrayList<AssignmentDetailsStaffModel.SubmittedDetail>()
                            staffAttachmentList = ArrayList<AssignmentDetailsStaffModel.StaffAttachment>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })

    }

    private fun getUnAttendedList(accademicId: Int, classId: Int, assignmentId: Int) {

        assignmentStaffViewModel.getUnAttendedAssignment(accademicId,classId,assignmentId)
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
            AssignDetailsTabFragment(assignmentDetails,submittedDetails,staffAttachmentList),
            resources.getString(R.string.details)
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