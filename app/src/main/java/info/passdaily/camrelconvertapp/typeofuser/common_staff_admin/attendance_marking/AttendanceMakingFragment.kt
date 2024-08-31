package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.attendance_marking

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentAttendanceMakingBinding
import info.passdaily.camrelconvertapp.databinding.FragmentObjectiveExamListBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class AttendanceMakingFragment : Fragment(),AttendanceListener {

    var TAG = "AttandanceMakingFragment"
    private lateinit var attendanceMakingViewModel: AttendanceMakingViewModel
    private var _binding: FragmentAttendanceMakingBinding? = null
    private val binding get() = _binding!!

    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0


    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()

    var getStudentDetail = ArrayList<StudentDetailsModel.StudentDetail>()

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var spinnerSubject : AppCompatSpinner? = null

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var textStartDate: TextInputEditText? = null

    var recyclerViewVideo : RecyclerView? = null

    var toolBarClickListener : ToolBarClickListener? = null
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
        toolBarClickListener?.setToolbarName("Attendance Marking")
        // Inflate the layout for this fragment
      //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId =  user[0].SCHOOL_ID
        // Inflate the layout for this fragment
        attendanceMakingViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[AttendanceMakingViewModel::class.java]

        _binding = FragmentAttendanceMakingBinding.inflate(inflater, container, false)
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

        textStartDate = binding.textStartDate
        textStartDate?.inputType = InputType.TYPE_NULL
        textStartDate?.keyListener = null

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())


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
                getAttendanceMarking(cLASSID,aCCADEMICID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        initFunction()


    }


    private fun initFunction() {
        attendanceMakingViewModel.getYearClassExam(adminId, schoolId)
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


    private fun getAttendanceMarking(cLASSID: Int, aCCADEMICID: Int) {

        @SuppressLint("SimpleDateFormat") val date = SimpleDateFormat("yyyy/MM/dd")
            .format(Date())
        attendanceMakingViewModel.getStudentDetails(cLASSID,aCCADEMICID,date)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getStudentDetail= response.studentDetails as ArrayList<StudentDetailsModel.StudentDetail>
                            if(getStudentDetail.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewVideo!!.adapter =
                                        AttendanceMakingAdapter(
                                            this,
                                            getStudentDetail,
                                            requireActivity(),
                                            TAG
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
                            getStudentDetail = ArrayList<StudentDetailsModel.StudentDetail>()
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

    class AttendanceMakingAdapter(
        var attendanceListener : AttendanceListener,
        var getStudentDetail: ArrayList<StudentDetailsModel.StudentDetail>,
        var context: Context, var TAG : String)
        : RecyclerView.Adapter<AttendanceMakingAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textName: TextView = view.findViewById(R.id.textName)
            var textGuardianName: TextView = view.findViewById(R.id.textGuardianName)
            var imageViewCheck : ImageView = view.findViewById(R.id.imageViewCheck)
            var cardViewButton : CardView = view.findViewById(R.id.cardViewButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.attendance_making_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textName.text = getStudentDetail[position].sTUDENTFNAME

            holder.textGuardianName.text =
                "Roll.No : ${getStudentDetail[position].sTUDENTROLLNUMBER}"

            if(getStudentDetail[position].aBSENTS == 0){
                holder.imageViewCheck.visibility = View.VISIBLE
                holder.cardViewButton.visibility = View.GONE
                holder.imageViewCheck.setImageResource(R.drawable.ic_check_gray)
            }else{
                holder.imageViewCheck.visibility = View.GONE
                holder.cardViewButton.visibility = View.VISIBLE
            }
            holder.imageViewCheck.setOnClickListener {
                holder.imageViewCheck.setImageResource(R.drawable.ic_checked_black)
                attendanceListener.onMarkPresent(getStudentDetail[position])
            }

            holder.cardViewButton.setOnClickListener {
                attendanceListener.onMarkAbsent(getStudentDetail[position])
            }
        }

        override fun getItemCount(): Int {
            return getStudentDetail.size
        }

    }

    override fun onMarkPresent(getStudentDetail: StudentDetailsModel.StudentDetail) {
       Log.i(TAG,"getStudentDetail ${getStudentDetail}")
    }

    override fun onMarkAbsent(getStudentDetail: StudentDetailsModel.StudentDetail) {
        Log.i(TAG,"getStudentDetail ${getStudentDetail}")
    }


}

interface AttendanceListener {
    fun onMarkPresent(getStudentDetail: StudentDetailsModel.StudentDetail)
    fun onMarkAbsent(getStudentDetail: StudentDetailsModel.StudentDetail)
}
