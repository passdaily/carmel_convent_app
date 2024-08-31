package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center_madrasa.exam_topper

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentObjectiveExamListBinding
import info.passdaily.camrelconvertapp.lib.result_progress.ColorfulRingProgressView
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerMadrasa
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.exam_topper.ExamTopperViewModel
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener



@Suppress("DEPRECATION")
class ExamTopperMadrasaFragment(var url: String, var type: Int, var title : String) : Fragment() {

    var TAG = "ExamTopperFragment"
    private lateinit var examTopperViewModel: ExamTopperViewModel

    private var _binding: FragmentObjectiveExamListBinding? = null
    private val binding get() = _binding!!
    var isWork = false
    var aCCADEMICID = 0
    var cLASSID = 0
    var eXAMID = 0
    var toolBarClickListener : ToolBarClickListener? = null

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getExam = ArrayList<GetYearClassExamModel.Exam>()

    var getExamTopper = ArrayList<ExamTopperResponseModel.ExamTopper>()


    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var spinnerSubject : AppCompatSpinner? = null

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null

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
        toolBarClickListener?.setToolbarName(title)

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        examTopperViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerMadrasa.services))
        )[ExamTopperViewModel::class.java]

        _binding = FragmentObjectiveExamListBinding.inflate(inflater, container, false)
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
        spinnerSubject = binding.spinnerSubject

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
                if(isWork){
                    getExamTopper(aCCADEMICID,cLASSID,eXAMID)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        binding.subHintText.text = requireActivity().resources.getString(R.string.select_exam)
        spinnerSubject?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                eXAMID = getExam[position].eXAMID
                getExamTopper(aCCADEMICID,cLASSID,eXAMID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        initFunction()

        binding.fab.visibility = View.GONE
    }

    private fun getExamTopper(aCCADEMICID: Int, cLASSID: Int, eXAMID: Int) {

        examTopperViewModel.getExamTopperResponse(url,aCCADEMICID,cLASSID,eXAMID,adminId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getExamTopper = response.examTopper as ArrayList<ExamTopperResponseModel.ExamTopper>

                            if(getExamTopper.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if(type == 1 || type == 2){
                                    if (isAdded) {
                                        recyclerViewVideo!!.adapter =
                                            ExamTopperAdapter(
                                                getExamTopper,
                                                requireActivity(),
                                                type
                                            )
                                    }
                                }else if(type == 3){
                                    if (isAdded) {
                                        recyclerViewVideo!!.adapter =
                                            ExamTopperCEAdapter(
                                                getExamTopper,
                                                requireActivity(),
                                                TAG
                                            )
                                    }
                                }

                            }else{
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }

                            Log.i(TAG,"initFunction SUCCESS")
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
                            getExamTopper = ArrayList<ExamTopperResponseModel.ExamTopper>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"initFunction LOADING")
                        }
                    }
                }
            })
    }


    private fun initFunction() {
        examTopperViewModel.getYearClassExam(adminId, schoolId )
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            isWork = true
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
                            if (spinnerSubject != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    examX
                                )
                                spinnerSubject?.adapter = adapter
                            }

                            Log.i(TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            if(isAdded) {
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text = resources.getString(R.string.no_internet)
                            }
                            Log.i(TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            getExamTopper = ArrayList<ExamTopperResponseModel.ExamTopper>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"initFunction LOADING")
                        }
                    }
                }
            })
    }

    class ExamTopperAdapter(var examTopper: ArrayList<ExamTopperResponseModel.ExamTopper>,
                            var context: Context, var type: Int) :
        RecyclerView.Adapter<ExamTopperAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewName: TextView = view.findViewById(R.id.textViewName)
            var textViewRollNo: TextView = view.findViewById(R.id.textViewRollNo)
            var textViewMark : TextView = view.findViewById(R.id.textViewMark)
            var textViewPercentage : TextView = view.findViewById(R.id.textViewPercentage)
            var colorProgress : ColorfulRingProgressView = view.findViewById(R.id.colorProgress)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamTopperAdapter.ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.exam_topper_state_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {


            when (type) {
                1 -> {
                    holder.textViewName.text = examTopper[position].sTUDENTFNAME
                    holder.textViewRollNo.text ="Roll.No : ${examTopper[position].sTUDENTROLLNUMBER}"
                    holder.textViewMark.text = "Mark : ${examTopper[position].tOTALMARK.toString().toFloat().toInt()} / ${examTopper[position].tOTALOUTMARK.toString().toFloat().toInt()}"
                    holder.textViewPercentage.text = "${examTopper[position].tOTALPERCENTAGE.toString().toFloat().toInt()}%"

        //            val parseFloat: Float = examTopper[position].tOTALMARK.toString()
        //                .toFloat() / examTopper[position].tOTALOUTMARK.toString()
        //                .toFloat() * 100.0f
                    holder.colorProgress.percent = examTopper[position].tOTALPERCENTAGE.toString().toFloat()
                }
                2 -> {
                    holder.textViewName.text = examTopper[position].sTUDENTFNAME
                    holder.textViewRollNo.text ="Roll.No : ${examTopper[position].sTUDENTROLLNUMBER}"
                    holder.textViewMark.text = "Mark : ${examTopper[position].tOTALMARK.toString().toFloat()} / ${examTopper[position].tOTALOUTMARK.toString().toFloat()}"
                    holder.textViewPercentage.text = "${examTopper[position].tOTALPERCENTAGE.toString().toFloat()}%"

        //            val parseFloat: Float = examTopper[position].tOTALMARK.toString()
        //                .toFloat() / examTopper[position].tOTALOUTMARK.toString()
        //                .toFloat() * 100.0f
                    holder.colorProgress.percent = examTopper[position].tOTALPERCENTAGE.toString().toFloat()
                }
            }
        }

        override fun getItemCount(): Int {
            return examTopper.size
        }

    }


//    class ExamTopperCBSEAdapter(var examTopper: ArrayList<ExamTopperResponseModel.ExamTopper>,
//                            var context: Context,var TAG: String) :
//        RecyclerView.Adapter<ExamTopperCBSEAdapter.ViewHolder>() {
//
//        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var textViewName: TextView = view.findViewById(R.id.textViewName)
//            var textViewRollNo: TextView = view.findViewById(R.id.textViewRollNo)
//            var textViewMark : TextView = view.findViewById(R.id.textViewMark)
//            var textViewPercentage : TextView = view.findViewById(R.id.textViewPercentage)
//            var colorProgress : ColorfulRingProgressView = view.findViewById(R.id.colorProgress)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamTopperCBSEAdapter.ViewHolder {
//            val itemView = LayoutInflater.from(parent.context)
//                .inflate(R.layout.exam_topper_state_adapter, parent, false)
//            return ViewHolder(itemView)
//        }
//
//        @SuppressLint("SetTextI18n")
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//            holder.textViewName.text = examTopper[position].sTUDENTFNAME
//            holder.textViewRollNo.text ="Roll.No : ${examTopper[position].sTUDENTROLLNUMBER}"
//            holder.textViewMark.text = "Mark : ${examTopper[position].tOTALMARK.toString().toFloat()} / ${examTopper[position].tOTALOUTMARK.toString().toFloat()}"
//            holder.textViewPercentage.text = "${examTopper[position].tOTALPERCENTAGE.toString().toFloat()}%"
//
////            val parseFloat: Float = examTopper[position].tOTALMARK.toString()
////                .toFloat() / examTopper[position].tOTALOUTMARK.toString()
////                .toFloat() * 100.0f
//            holder.colorProgress.percent = examTopper[position].tOTALPERCENTAGE.toString().toFloat()
//
//
//        }
//
//        override fun getItemCount(): Int {
//            return examTopper.size
//        }
//
//    }


    class ExamTopperCEAdapter(var examTopper: ArrayList<ExamTopperResponseModel.ExamTopper>,
                              var context: Context, var TAG: String) :
        RecyclerView.Adapter<ExamTopperCEAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewName: TextView = view.findViewById(R.id.textViewName)
            var textViewRollNo: TextView = view.findViewById(R.id.textViewRollNo)
            var textViewMark : TextView = view.findViewById(R.id.textViewMark)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamTopperCEAdapter.ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.exam_topper_ce_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.textViewName.text = examTopper[position].sTUDENTFNAME
            holder.textViewRollNo.text ="Roll.No : ${examTopper[position].sTUDENTROLLNUMBER}"
            holder.textViewMark.text = "Mark : ${examTopper[position].tOTALMARK.toString().toFloat().toInt()} " +
                    "/ ${examTopper[position].tOTALOUTMARK.toString().toFloat().toInt()}"

        }

        override fun getItemCount(): Int {
            return examTopper.size
        }

    }


}