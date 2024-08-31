package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentProgressCardLpUpBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener


@Suppress("DEPRECATION")
class ProgressCardIXtoXIIFragment : Fragment() {

    private lateinit var progressCardViewModel: ProgressCardViewModel
    private var _binding: FragmentProgressCardLpUpBinding? = null
    private val binding get() = _binding!!

    var adminId = 0
    var  schoolId = 0
    var TAG = "ProgressCardLpUpFragment"

    var aCCADEMICID = 0
    var cLASSID = 0
    var eXAMID = 0
    var toolBarClickListener: ToolBarClickListener? = null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getExam = ArrayList<GetYearClassExamModel.Exam>()


    var markList = ArrayList<ProgressCardLpUpModel.Mark>()
    var staffList = ArrayList<ProgressCardLpUpModel.Staff>()
    var studentList = ArrayList<ProgressCardLpUpModel.Student>()
    var subjectList = ArrayList<ProgressCardLpUpModel.Subject>()

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null
    var spinnerExam: AppCompatSpinner? = null

    var cLASSNAME = ""
    var constraintLayout : ConstraintLayout? = null

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null

    var frameLayout : FrameLayout? = null

    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null
    var isWork = false

    private lateinit var localDBHelper: LocalDBHelper
    var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext
        }
        try {
            toolBarClickListener = context as ToolBarClickListener
        } catch (e: Exception) {
            Log.i(TAG, "Exception $e")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Progress Card IX-XII")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        // Inflate the layout for this fragment
        progressCardViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ProgressCardViewModel::class.java]

        _binding = FragmentProgressCardLpUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        constraintLayout  = binding.constraintLayout
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        shimmerViewContainer = binding.shimmerViewContainer

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass
        spinnerExam = binding.spinnerExam


        frameLayout = binding.frameLayout
        viewPager = binding.pager
        tabLayout = binding.tabLayout

        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
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
                view: View, position: Int, id: Long
            ) {
                cLASSID = getClass[position].cLASSID
                cLASSNAME  = getClass[position].cLASSNAME
                if(isWork){
                    progressFun()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        spinnerExam?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                eXAMID = getExam[position].eXAMID
                progressFun()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        initFunction()
    }

    private fun initFunction() {
        progressCardViewModel.getYearClassExam(adminId, schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size) { "" }
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
                            var classX = Array(getClass.size) { "" }
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


                            getExam = response.exams as ArrayList<GetYearClassExamModel.Exam>
                            var examX = Array(getExam.size) { "" }
                            for (i in getExam.indices) {
                                examX[i] = getExam[i].eXAMNAME
                            }
                            if (spinnerExam != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    examX
                                )
                                spinnerExam?.adapter = adapter
                            }

                            Log.i(TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }

    private fun progressFun() {

        progressCardViewModel.getProgressReportHS(aCCADEMICID, cLASSID, eXAMID, adminId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            isWork = true
                            if(response.markList.isNotEmpty()){
                                getTabResult()

                            }else{
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)
                                textEmpty?.text =  resources.getString(R.string.no_results)
                                shimmerViewContainer?.visibility = View.GONE
                                frameLayout?.visibility = View.GONE
                                viewPager?.visibility = View.GONE
                            }


                            Log.i(TAG, "progressFun SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            frameLayout?.visibility = View.GONE
                            viewPager?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)

                            Log.i(TAG, "progressFun ERROR")
                        }
                        Status.LOADING -> {
                            markList = ArrayList<ProgressCardLpUpModel.Mark>()
                            staffList = ArrayList<ProgressCardLpUpModel.Staff>()
                            studentList = ArrayList<ProgressCardLpUpModel.Student>()
                            subjectList = ArrayList<ProgressCardLpUpModel.Subject>()

                            constraintEmpty?.visibility = View.GONE
                            frameLayout?.visibility = View.GONE
                            viewPager?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE

                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG, "progressFun LOADING")
                        }
                    }
                }
            })
    }


    private fun getTabResult() {

        Global.aCCADEMICID = aCCADEMICID
        Global.cLASSID = cLASSID
        Global.eXAMID = eXAMID
        Global.adminId = adminId
        Global.cLASSNAME = cLASSNAME

        val adapter = Global.MyPagerAdapter(childFragmentManager)
        adapter.addFragment(
            ProgressCardIXToXIITAb(
//                aCCADEMICID,
//                cLASSID,
//                eXAMID,
//                adminId,
//                cLASSNAME
            ),
            resources.getString(R.string.progress_details)
        )
        // adapter.addFragment(new DMKOfficial(), "Tweets");
        shimmerViewContainer?.visibility = View.GONE
        constraintEmpty?.visibility = View.GONE
        frameLayout?.visibility = View.VISIBLE
        viewPager?.visibility = View.VISIBLE

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