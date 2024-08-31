package info.passdaily.camrelconvertapp.typeofuser.parent.absent

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.StudyMaterialFragmentBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper



@Suppress("DEPRECATION")
class AbsentFragment : Fragment(), ItemClickListener {
    var TAG = "AbsentFragment"

    private var _binding: StudyMaterialFragmentBinding? = null
    private val binding get() = _binding!!

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0

    var recyclerViewAbsentList: RecyclerView? = null
    var recyclerView: RecyclerView? = null

    var accedemicList: ArrayList<AccademicYearsModel.AccademicYear> =
        ArrayList<AccademicYearsModel.AccademicYear>()

    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null

    var shimmerViewContainer : ShimmerFrameLayout? =null

    private lateinit var absentViewModel: AbsentViewModel

    var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG, "onAttach ")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.currentPage = 9
        Global.screenState = "landingpage"
        // Inflate the layout for this fragment
//        absentViewModel = ViewModelProvider(this)[AbsentViewModel::class.java]
        absentViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[AbsentViewModel::class.java]

        _binding = StudyMaterialFragmentBinding.inflate(inflater, container, false)
        return binding.root
     //   return inflater.inflate(R.layout.study_material_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID

        var textViewTitle = binding.textView32
        textViewTitle.text = "Absent"

        recyclerView = binding.tabLayout
        recyclerView?.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        //absentViewModel.getAccademic(0)

        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty


        recyclerViewAbsentList = binding.recyclerView
        recyclerViewAbsentList?.layoutManager = LinearLayoutManager(requireActivity())

        shimmerViewContainer = binding.shimmerViewContainer

        intiFunction()
        getAbsentListFun(-1)
    }

    private fun getAbsentListFun(i: Int) {

//        absentViewModel.getAbsents(
//            STUDENTID,
//            ACADEMICID
//        )
//        absentViewModel.getAbsentObservable()
//            .observe(requireActivity(), {
//
//                if (it != null) {
//                    if (it.attendanceReport.isNotEmpty()) {
//                        constraintEmpty?.visibility = View.GONE
//                        recyclerViewAbsentList?.visibility = View.VISIBLE
//                        if (isAdded) {
//                            recyclerViewAbsentList?.adapter =
//                                AttendanceAdapter(it.attendanceReport, mContext!!)
//                        }
//                    } else {
//                        constraintEmpty?.visibility = View.VISIBLE
//                        recyclerViewAbsentList?.visibility = View.GONE
//                    }
//                }
//            })

        absentViewModel.getAbsentsNew(STUDENTID, ACADEMICID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                          val response = resource.data?.body()!!

                            if(response.attendanceReport.isNotEmpty()){
                                constraintEmpty?.visibility = View.GONE
                                recyclerViewAbsentList?.visibility = View.VISIBLE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewAbsentList?.adapter =
                                        AttendanceAdapter(response.attendanceReport, mContext!!)
                                }
                            }else{
                                constraintEmpty?.visibility = View.VISIBLE
                                recyclerViewAbsentList?.visibility = View.GONE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    Glide.with(mContext!!)
                                        .load(R.drawable.ic_empty_state_absent)
                                        .into(imageViewEmpty!!)
                                }
                                textEmpty?.text = requireActivity().resources.getString(R.string.no_results)

                            }
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewAbsentList?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_no_internet)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text = requireActivity().resources.getString(R.string.no_internet_str)

                            Log.i(TAG, "Status.ERROR ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_empty_state_absent)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text = requireActivity().resources.getString(R.string.loading)
                            shimmerViewContainer?.visibility = View.VISIBLE
                            recyclerViewAbsentList?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE

                        }
                    }
                }
            })

        
    }


    fun intiFunction() {

        absentViewModel.getAccademicNew(ACADEMICID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if (isAdded) {
                                recyclerView?.adapter = AccademicAdapter(this, response.accademicYears  as ArrayList<AccademicYearsModel.AccademicYear>, mContext!!)
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })

//        absentViewModel.getAccademicObservable().observe(requireActivity(), {
//            if (it != null) {
//                accedemicList = it.accademicYears
//                if (isAdded) {
//                    recyclerView?.adapter = AccademicAdapter(this, accedemicList, mContext!!)
//                }
//            }
//        })
    }

    class AccademicAdapter(
        val itemClickListener: ItemClickListener,
        var accedemicList: ArrayList<AccademicYearsModel.AccademicYear>,
        var context: Context
    ) : RecyclerView.Adapter<AccademicAdapter.ViewHolder>() {
        var index = 0

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textSubject: TextView = view.findViewById(R.id.textAssignmentName)
            var cardView: CardView = view.findViewById(R.id.cardView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.subjet_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textSubject.text = accedemicList[position].aCCADEMICTIME
            holder.cardView.setOnClickListener {
                itemClickListener.onClick(accedemicList[position].aCCADEMICID)
                index = position;
                notifyDataSetChanged()
            }

            if (index == position) {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.green_400))
                holder.textSubject.setTextColor(Color.parseColor("#FFFFFFFF"))
            } else {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.white))
                holder.textSubject.setTextColor(context.resources.getColor(R.color.green_400))
            }
        }

        override fun getItemCount(): Int {
            return accedemicList.size
        }
    }

    class AttendanceAdapter(
        var attendanceReport: List<AttendanceReportModel.AttendanceReport>,
        var mContext: Context
    ) :
        RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {
        var startDate = ""

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewName: TextView = view.findViewById(R.id.textViewName)
            var textViewRollNo: TextView = view.findViewById(R.id.textViewRollNo)
            var textViewDate: TextView = view.findViewById(R.id.textViewStatus)
            val textViewClass : TextView = view.findViewById(R.id.textViewClass)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.absent_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewName.text = attendanceReport[position].sTUDENTNAME
            holder.textViewRollNo.text = "Roll.No : " + attendanceReport[position].sTUDENTROLLNUMBER

            if (!attendanceReport[position].aBSENTDATE.isNullOrBlank()) {
                val date: Array<String> =
                    attendanceReport[position].aBSENTDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                startDate = Utils.formattedDateWords(dddd)
            }
            holder.textViewDate.text = startDate
            holder.textViewClass.text  = "Class : " +  attendanceReport[position].cLASSNAME

        }

        override fun getItemCount(): Int {
            return attendanceReport.size
        }

    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
        Log.i(TAG, "onDetach ")
    }

    override fun onDestroy() {
        super.onDestroy()
        mContext = null
        _binding = null
        Log.i(TAG, "onDestroy ")
    }

    override fun onClick(id: Int) {
        absentViewModel.getAbsentsNew(STUDENTID, id)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(response.attendanceReport.isNotEmpty()){
                                constraintEmpty?.visibility = View.GONE
                                recyclerViewAbsentList?.visibility = View.VISIBLE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewAbsentList?.adapter =
                                        AttendanceAdapter(response.attendanceReport, mContext!!)
                                }
                            }else{
                                constraintEmpty?.visibility = View.VISIBLE
                                recyclerViewAbsentList?.visibility = View.GONE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    Glide.with(mContext!!)
                                        .load(R.drawable.ic_empty_state_absent)
                                        .into(imageViewEmpty!!)
                                }
                                textEmpty?.text = requireActivity().resources.getString(R.string.no_results)

                            }
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewAbsentList?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_no_internet)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text = requireActivity().resources.getString(R.string.no_internet_str)

                            Log.i(TAG, "Status.ERROR ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            shimmerViewContainer?.visibility = View.VISIBLE
                            recyclerViewAbsentList?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE

                        }
                    }
                }
            })
    }


    fun onRetry(name : String?,message: String?){

        MaterialAlertDialogBuilder(requireActivity(),R.style.AlertDialogTheme)
            .setTitle(name)
            .setCancelable(false)
            .setMessage(message)
            .setPositiveButton(
                "RETRY"
            ) { dialogInterface, _ ->

                intiFunction()

                dialogInterface.dismiss()
            }
            .show()

    }






}

interface ItemClickListener {
    fun onClick(id: Int)
}