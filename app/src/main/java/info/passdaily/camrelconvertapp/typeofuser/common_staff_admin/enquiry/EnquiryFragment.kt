package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.enquiry

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentLeaveStaffBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.CreateObjectiveExam
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import java.util.ArrayList

@Suppress("DEPRECATION")
class EnquiryFragment : Fragment(),EnquiryListener {

    var TAG = "EnquiryFragment"
    private lateinit var enquiryViewModel : EnquiryViewModel
    //Attendance/EnquiryListGet?ClassId="+scid + "&AccademicId=" + s_aid + "&SubmitDate=0
    private var _binding: FragmentLeaveStaffBinding? = null
    private val binding get() = _binding!!
    var staffId = 0
    var adminId = 0
    var schoolId = 0

    var aCCADEMICID = 0
    var cLASSID = 0

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null

    private lateinit var localDBHelper : LocalDBHelper
    var toolBarClickListener : ToolBarClickListener? = null

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var constraintLayoutContent : ConstraintLayout? = null

    var currentItem = 0

    var shimmerViewContainer: ShimmerFrameLayout? = null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()

    var  enquiryList  = ArrayList<EnquiryListStaffModel.Enquiry>()



    var fab : FloatingActionButton? = null

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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Enquiry")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        staffId = user[0].STAFF_ID
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        // Inflate the layout for this fragment
        enquiryViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[EnquiryViewModel::class.java]

        _binding = FragmentLeaveStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        constraintLayoutContent = binding.constraintLayoutContent
        shimmerViewContainer = binding.shimmerViewContainer
//        tabLayout?.visibility = View.GONE
//        viewPager?.visibility = View.GONE
//        shimmerViewContainer?.visibility = View.VISIBLE

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass


        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                aCCADEMICID = getYears[position].aCCADEMICID
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        spinnerClass?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                cLASSID = getClass[position].cLASSID
                getLeaveList(aCCADEMICID,cLASSID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        viewPager = binding.pager
        tabLayout = binding.tabLayout

        fab = binding.fab
        fab?.visibility = View.GONE

        initFunction()

    }

    private fun getLeaveList(aCCADEMICID: Int, cLASSID: Int) {

        enquiryViewModel.getEnquiryListGet(cLASSID,aCCADEMICID,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
//                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            enquiryList = response.enquiryList as ArrayList<EnquiryListStaffModel.Enquiry>

                            for(i in response.enquiryList.indices){
                                when (enquiryList[i].qUERYSTATUS) {
                                    0 -> {
                                        enquiryList[i].cardColor = 0//pending
                                        enquiryList[i].textColor = resources.getColor(R.color.orange_500)
                                        enquiryList[i].stateName = resources.getString(R.string.pending)
                                        Global.pendingEnquiryList.add(enquiryList[i])
                                    }
                                    1 -> {
                                        enquiryList[i].cardColor = 1//replied
                                        enquiryList[i].textColor = resources.getColor(R.color.fresh_green_200)
                                        enquiryList[i].stateName = resources.getString(R.string.replied)
                                        Global.repliedEnquiryList.add(enquiryList[i])
                                    }
                                }
                            }
                            Log.i(TAG,"repliedEnquiryList ${Global.repliedEnquiryList}")
                            Log.i(TAG,"pendingEnquiryList ${Global.pendingEnquiryList}")

                            getTabResult()

//                            if(leaveDetails.isNotEmpty()){
//                                viewPager?.visibility = View.VISIBLE
//                                constraintEmpty?.visibility = View.GONE
//                                getTabResult()
//                            }else{
//                                viewPager?.visibility = View.GONE
//                                constraintEmpty?.visibility = View.VISIBLE
//                                Glide.with(this)
//                                    .load(R.drawable.ic_empty_progress_report)
//                                    .into(imageViewEmpty!!)
//
//                                textEmpty?.text =  resources.getString(R.string.no_results)
//                            }

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            getTabResult()
//                            constraintEmpty?.visibility = View.VISIBLE
//                            viewPager?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.GONE
//
//                            Glide.with(this)
//                                .load(R.drawable.ic_no_internet)
//                                .into(imageViewEmpty!!)
//                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
//                            viewPager?.visibility = View.GONE
//                            constraintEmpty?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.VISIBLE
//                            leaveDetails = ArrayList<LeaveDetailsStaffModel.LeaveDetail>()
//                            Glide.with(this)
//                                .load(R.drawable.ic_empty_progress_report)
//                                .into(imageViewEmpty!!)
//
//                            textEmpty?.text =  resources.getString(R.string.loading)
                            tabLayout?.visibility = View.GONE
                            viewPager?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            enquiryList  = ArrayList<EnquiryListStaffModel.Enquiry>()
                            Global.pendingEnquiryList = ArrayList<EnquiryListStaffModel.Enquiry>()
                            Global.repliedEnquiryList =ArrayList<EnquiryListStaffModel.Enquiry>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })

    }

    private fun initFunction() {
        enquiryViewModel.getYearClassExam(adminId,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size){""}
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                            }
                            if (spinnerAcademic != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademic?.adapter = adapter
                            }

                            getClass = response.classList as ArrayList<GetYearClassExamModel.Class>
                            var classX = Array(getClass.size){""}
                            for (i in getClass.indices) {
                                classX[i] = getClass[i].cLASSNAME
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    classX
                                )
                                spinnerClass?.adapter = adapter
                            }
                            Log.i(CreateObjectiveExam.TAG,"initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(CreateObjectiveExam.TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(CreateObjectiveExam.TAG,"initFunction LOADING")
                        }
                    }
                }
            })
    }

    private fun getTabResult() {

        val adapter = Global.MyPagerAdapter(childFragmentManager)
        adapter.addFragment(
            EnquiryTabFragment(0,this),
            resources.getString(R.string.enquiry_tab)
        )
        adapter.addFragment(
            EnquiryTabFragment(1,this),
            resources.getString(R.string.replied)
        )

        // adapter.addFragment(new DMKOfficial(), "Tweets");
        tabLayout?.visibility = View.VISIBLE
        viewPager?.visibility = View.VISIBLE
        shimmerViewContainer?.visibility = View.GONE
        viewPager?.adapter = adapter
        viewPager?.currentItem = currentItem
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

    override fun onReloadClick() {
        Log.i(TAG,"onReloadClick")
    }

    override fun onMessageClick(message: String, type: Int, currentPage: Int) {
        when(type){
            1 ->{
                currentItem = currentPage
                getLeaveList(aCCADEMICID,cLASSID)
                Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)
            }
            0 ->{
                Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        toolBarClickListener = null
        mContext = null
        Log.i(TAG,"onDestroy")
    }

}

interface EnquiryListener{
    fun onReloadClick()
    fun onMessageClick(message: String, type: Int, currentPage: Int)
}