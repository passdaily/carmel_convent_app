package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.ArrayList

@Suppress("DEPRECATION")
class ObjExamDetailsActivity : AppCompatActivity() {

    var TAG = "ObjExamDetailsActivity"
    private lateinit var binding: ActivityObjExamDetailsBinding
    private lateinit var objectiveExamStaffViewModel: ObjectiveExamStaffViewModel

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var toolbar: Toolbar? = null
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null

    var ACCADEMIC_ID = 0
    var CLASS_ID = 0
    var EXAM_ID = 0
    var SUBJECT_ID = 0
    var STATUS = -1

    var onlineExamAttendees = ArrayList<ObjExamDetailsStaffModel.OnlineExamAttendee>()
    var onlineExamDetails = ArrayList<ObjExamDetailsStaffModel.OnlineExamDetail>()

    var unAttendedListModel = ArrayList<UnAttendedListModel.UnAttended>()

    var getQuestionOptionList = ArrayList<QuestionOptionsListModel.Question>()

    var getOptionList= ArrayList<QuestionOptionsListModel.Option>()

    var constraintLayoutContent : ConstraintLayout? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localDBHelper = LocalDBHelper(this)
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        // Inflate the layout for this fragment

        val extras = intent.extras
        if (extras != null) {
            EXAM_ID = extras.getInt("EXAM_ID")
            ACCADEMIC_ID = extras.getInt("ACCADEMIC_ID")
            CLASS_ID = extras.getInt("CLASS_ID")
            SUBJECT_ID = extras.getInt("SUBJECT_ID")
            STATUS  = extras.getInt("STATUS")
        }

        objectiveExamStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ObjectiveExamStaffViewModel::class.java]


        binding = ActivityObjExamDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Objective Exam Details"
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

//        GlobalScope.launch(Dispatchers.Main) {
//
//
//        }
        initFunction(EXAM_ID)
        Utils.setStatusBarColor(this)


    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
    private fun initFunction(EXAM_ID : Int) {
        objectiveExamStaffViewModel.getOnlineExamResultStaff(EXAM_ID)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            onlineExamAttendees = response.onlineExamAttendees as ArrayList<ObjExamDetailsStaffModel.OnlineExamAttendee>
                            onlineExamDetails = response.onlineExamDetails as ArrayList<ObjExamDetailsStaffModel.OnlineExamDetail>

                            getQuestions()
                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            getTabResult()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            onlineExamDetails = ArrayList<ObjExamDetailsStaffModel.OnlineExamDetail>()
                            onlineExamAttendees = ArrayList<ObjExamDetailsStaffModel.OnlineExamAttendee>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })

    }


    //OnlineExam/UnAttendedList?AccademicId=8&ClassId=12&OexamId=2
    private fun getQuestions() {
        objectiveExamStaffViewModel.getObjQuestionOptionListStaff(ACCADEMIC_ID,CLASS_ID,SUBJECT_ID,EXAM_ID)
            .observe(this, Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            getQuestionOptionList = response.questionList as ArrayList<QuestionOptionsListModel.Question>
                            getOptionList= response.optionList as ArrayList<QuestionOptionsListModel.Option>
                            getUnAttendedList(ACCADEMIC_ID,CLASS_ID,EXAM_ID)
                            Log.i(TAG,"getQuestions SUCCESS")
                        }
                        Status.ERROR -> {
                            getTabResult()
                            Log.i(TAG,"getQuestions ERROR")
                        }
                        Status.LOADING -> {
                            getQuestionOptionList = ArrayList<QuestionOptionsListModel.Question>()
                            Log.i(TAG,"getQuestions LOADING")
                        }
                    }
                }
            })

    }

    //OnlineExam/UnAttendedList?AccademicId=8&ClassId=12&OexamId=2
    private fun getUnAttendedList(ACCADEMIC_ID: Int, CLASS_ID: Int, EXAM_ID: Int) {
        objectiveExamStaffViewModel.getUnattendedList(ACCADEMIC_ID,CLASS_ID,EXAM_ID)
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
                            unAttendedListModel = ArrayList<UnAttendedListModel.UnAttended>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })

    }



    private fun getTabResult() {

        val adapter = Global.MyPagerAdapter(supportFragmentManager)

        adapter.addFragment(
            ObjQuestionTabFragment(getQuestionOptionList,getOptionList,ACCADEMIC_ID,CLASS_ID,SUBJECT_ID,EXAM_ID),
            resources.getString(R.string.question)
        )
        adapter.addFragment(
            ObjDetailsTabFragment(onlineExamDetails,onlineExamAttendees),
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
        viewPager?.currentItem = 1
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