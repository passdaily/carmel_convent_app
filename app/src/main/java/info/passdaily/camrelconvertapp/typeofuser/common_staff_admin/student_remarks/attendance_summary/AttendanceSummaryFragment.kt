package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.attendance_summary

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentAttendanceSummeryBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.StudentRemarkViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.description_exam.ItemClickListener
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import java.util.*

@Suppress("DEPRECATION")
class AttendanceSummaryFragment : Fragment(),SummeryItemClickListener {

    var TAG = "AttendanceSummaryFragment"
    private lateinit var studentRemarkViewModel: StudentRemarkViewModel
    private var _binding: FragmentAttendanceSummeryBinding? = null
    private val binding get() = _binding!!


    var isWorking= false
    // http://demostaff.passdaily.in/ElixirApi/Group/GroupAdd
    var aCCADEMICID = 0
    var aCCADEMICYEAR = ""
    var cLASSID = 0
    var eXAMID = 0

    var fromStr = ""
    var toStr = ""

    var startDate = ""
    var endDate = ""
    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    var classMonth = arrayOf(
        "JAN",
        "FEB",
        "MAR",
        "APRIL",
        "MAY",
        "JUNE",
        "JULY",
        "AUG",
        "SEP",
        "OCT",
        "NOV",
        "DEC"
    )
    var month = 0

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getExam = ArrayList<GetYearClassExamModel.Exam>()

    var recyclerViewVideo: RecyclerView? = null
    var tabLayout : RecyclerView? = null
    var viewPager: ViewPager? = null

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null


    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var textStartDate: TextInputEditText? = null
    var textEndDate : TextInputEditText? = null

    var toolBarClickListener : ToolBarClickListener? = null

    var getAttendanceSummary = ArrayList<AttendanceSummaryModel.AttendanceSummary>()
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
        toolBarClickListener?.setToolbarName("Attendance Summery")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        studentRemarkViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StudentRemarkViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentAttendanceSummeryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraintLayoutContent = binding.constraintLayoutContent
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        shimmerViewContainer = binding.shimmerViewContainer

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass


        binding.accedemicText.text = requireActivity().resources.getText(R.string.select_year)
        binding.classText.text = requireActivity().resources.getText(R.string.select_class)

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())





        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                aCCADEMICID = getYears[position].aCCADEMICID
                aCCADEMICYEAR = getYears[position].aCCADEMICTIME
                if(isWorking){
                    onClick(month)
                }

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
                onClick(month)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val mcurrentDate = Calendar.getInstance()
        month = mcurrentDate[Calendar.MONTH]

        initFunction()

        tabLayout = binding.tabLayout
        tabLayout?.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false)

        tabLayout?.adapter = AttendanceSummeryClassAdapter(this,
        classMonth,requireActivity(),month)

        tabLayout?.scrollToPosition(month)


       // getTabResult()
    }


    private fun initFunction() {
        studentRemarkViewModel.getYearClassExam(adminId, schoolId )
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
                            var classes = Array(getClass.size){""}
                            for (i in getClass.indices) {
                                classes[i] = getClass[i].cLASSNAME
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    classes
                                )
                                spinnerClass?.adapter = adapter
                            }

                            Log.i(TAG,"initFunction SUCCESS")

                        }
                        Status.ERROR -> {
                            Log.i(TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"initFunction LOADING")
                        }
                    }
                }
            })
    }






    class AttendanceSummeryClassAdapter(
        val itemClickListener: SummeryItemClickListener,
        var subjects: Array<String>,
        var context: Context,
        var month :Int
    ) :
        RecyclerView.Adapter<AttendanceSummeryClassAdapter.ViewHolder>() {
        var index = month

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textSubject: TextView = view.findViewById(R.id.textAssignmentName)
            var cardView: CardView = view.findViewById(R.id.cardView)
            var imageViewIcon: ImageView = view.findViewById(R.id.imageViewIcon)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.attandance_summery_class_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textSubject.text = subjects[position]
            holder.cardView.setOnClickListener {
                itemClickListener.onClick(position)
                index = position;
                notifyDataSetChanged()
            }

            if (index == position) {
                holder.textSubject.setTextColor(context.resources.getColor(R.color.black))
            } else {
                holder.textSubject.setTextColor(context.resources.getColor(R.color.gray_600))
            }
        }

        override fun getItemCount(): Int {
            return subjects.size
        }

    }

    override fun onClick(month: Int) {
        studentRemarkViewModel.getAttendanceSummeryDetail(cLASSID,(month+1),aCCADEMICID,aCCADEMICYEAR)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWorking = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!

                            getAttendanceSummary = response.attendanceSummary as ArrayList<AttendanceSummaryModel.AttendanceSummary>
                            if(getAttendanceSummary.isNotEmpty()){
                                binding.textWorkingDays.setBackgroundColor(resources.getColor(R.color.white))
                                binding.textAttendanceTaken.setBackgroundColor(resources.getColor(R.color.white))
                                binding.textWorkingDays.text = getAttendanceSummary[0].tOTALWORKING.toString()
                                binding.textAttendanceTaken.text = getAttendanceSummary[0].aTTENDANCETAKEN.toString()

                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewVideo!!.adapter =
                                        AttendanceSummaryAdapter(
                                            getAttendanceSummary,
                                            requireActivity()
                                        )

                                }
                            }else{
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }
                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewVideo?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerViewVideo?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getAttendanceSummary = ArrayList<AttendanceSummaryModel.AttendanceSummary>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }


    class AttendanceSummaryAdapter(
        var getAttendanceSummary: ArrayList<AttendanceSummaryModel.AttendanceSummary>,
        var context: Context)
        : RecyclerView.Adapter<AttendanceSummaryAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textGroupName: TextView = view.findViewById(R.id.textGroupName)
            var textTotalPresent : TextView = view.findViewById(R.id.textTotalPresent)
            var textAbsent : TextView = view.findViewById(R.id.textAbsent)
            var textPercentage: TextView = view.findViewById(R.id.textPercentage)
            var textStatus: TextView = view.findViewById(R.id.textStatus)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.attendance_summery_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textGroupName.text = getAttendanceSummary[position].sTUDENTFNAME

            holder.textStatus.text = "Roll.No : ${getAttendanceSummary[position].sTUDENTROLLNUMBER}"
            holder.textTotalPresent.text = "Total Present : ${getAttendanceSummary[position].tOTALPRESENT}"
            holder.textAbsent.text = "Total Absent : ${getAttendanceSummary[position].tOTALABSENT}"
            holder.textPercentage.text = "Per : ${getAttendanceSummary[position].aTTENDANCEPERCENTAGE}%"
        }

        override fun getItemCount(): Int {
            return getAttendanceSummary.size
        }

    }
}

interface SummeryItemClickListener {
    fun onClick(month: Int)
}



