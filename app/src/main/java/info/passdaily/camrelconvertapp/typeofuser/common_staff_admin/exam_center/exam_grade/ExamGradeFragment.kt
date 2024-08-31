package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.exam_grade

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.imageview.ShapeableImageView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentObjectiveExamListBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener

@Suppress("DEPRECATION")
class ExamGradeFragment(var url : String,var title : String) : Fragment() {


    var TAG = "ExamGradeFragment"
    private lateinit var examGradeViewModel: ExamGradeViewModel

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

    var getGradeCommonModel = ArrayList<GradeCommonModel>()


    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var spinnerSubject : AppCompatSpinner? = null

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null

    var relativeLayout :RelativeLayout? = null

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
        // Inflate the layout for this fragment
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName(title)

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        examGradeViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ExamGradeViewModel::class.java]

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
        recyclerViewVideo?.visibility = View.GONE

        relativeLayout = binding.relativeLayout
        relativeLayout?.visibility = View.VISIBLE


        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                aCCADEMICID = getYears[position].aCCADEMICID
                if(isWork){
                    getExamGrade(aCCADEMICID,cLASSID,eXAMID)
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
                if(isWork){
                    getExamGrade(aCCADEMICID,cLASSID,eXAMID)
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
                getExamGrade(aCCADEMICID,cLASSID,eXAMID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        initFunction()

        binding.fab.visibility = View.GONE
    }

    private fun getExamGrade(aCCADEMICID: Int, cLASSID: Int, eXAMID: Int) {
        examGradeViewModel.getExamGradeResponse(
            url,aCCADEMICID,cLASSID,eXAMID,adminId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWork = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            var getExamGradeCbse = response.examGrade as ArrayList<ExamGradeStateModel.ExamGrade>

                            if(getExamGradeCbse.isNotEmpty()){
                                //  recyclerViewVideo?.visibility = View.VISIBLE
                                relativeLayout?.removeAllViews();
                                constraintEmpty?.visibility = View.GONE
                                relativeLayout?.visibility = View.VISIBLE

                                var header = arrayOf<String>()
                                var namesList = header.toMutableList()
                                namesList.add("SUBJECT")

                                for(i in getExamGradeCbse.indices){
                                    var tempGrade: ArrayList<GradeCommonModel.GradeList> = ArrayList()
                                    tempGrade.add(GradeCommonModel.GradeList(" A ",getExamGradeCbse[i].gRADEA))
                                    tempGrade.add(GradeCommonModel.GradeList("A+",getExamGradeCbse[i].gRADEAPLUS))
                                    tempGrade.add(GradeCommonModel.GradeList(" B ",getExamGradeCbse[i].gRADEB))
                                    tempGrade.add(GradeCommonModel.GradeList("B+",getExamGradeCbse[i].gRADEBPLUS))
                                    tempGrade.add(GradeCommonModel.GradeList(" C ",getExamGradeCbse[i].gRADEC))
                                    tempGrade.add(GradeCommonModel.GradeList("C+",getExamGradeCbse[i].gRADECPLUS))
                                    tempGrade.add(GradeCommonModel.GradeList(" D ",getExamGradeCbse[i].gRADED))
                                    tempGrade.add(GradeCommonModel.GradeList("D+",getExamGradeCbse[i].gRADEDPLUS))
                                    tempGrade.add(GradeCommonModel.GradeList(" E ",getExamGradeCbse[i].gRADEE))

//                                    getGradeCommonModel.add(GradeCommonModel(getExamGradeCbse[i].oUTOFFMARK, getExamGradeCbse[i].pASSMARK,
//                                        0,0,
//                                        " ", getExamGradeCbse[i].sUBJECTCODE,
//                                        getExamGradeCbse[i].sUBJECTID, getExamGradeCbse[i].sUBJECTNAME,
//                                        0.0, getExamGradeCbse[i].tOTALATTEND,tempGrade))

                                    Global.getGradeMark.add(tempGrade)
                                    namesList.add(getExamGradeCbse[i].sUBJECTNAME)
                               }

                                header = namesList.toTypedArray()
                                relativeLayout?.addView(ExamGradeTableView(requireActivity(),header))
//                                if (isAdded) {
//                                    recyclerViewVideo!!.adapter = ExamGradeAdapter(getGradeCommonModel, requireActivity())
//                                }


                            }else{
                                relativeLayout?.visibility = View.GONE
                                //recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }

                            Log.i(TAG,"initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            relativeLayout?.visibility = View.GONE
                            constraintEmpty?.visibility = View.VISIBLE
                           // recyclerViewVideo?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                           // recyclerViewVideo?.visibility = View.GONE
                            relativeLayout?.visibility = View.GONE
                            relativeLayout?.removeAllViews();
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getGradeCommonModel = ArrayList<GradeCommonModel>()
                            Global.getGradeMark = ArrayList<ArrayList<GradeCommonModel.GradeList>>()
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

    class ExamGradeAdapter(var examGradeCbse: ArrayList<GradeCommonModel>, var mContext: Context) :
        RecyclerView.Adapter<ExamGradeAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textSubjectName: TextView = view.findViewById(R.id.textSubjectName)
            var textViewStaff: TextView = view.findViewById(R.id.textViewStaff)
            var textPassMark : TextView = view.findViewById(R.id.textPassMark)
            var textTotalMarks : TextView = view.findViewById(R.id.textTotalMarks)
            var textTotalAttended : TextView = view.findViewById(R.id.textTotalAttended)
//            var textViewA1 : TextView = view.findViewById(R.id.textViewA1)
//            var textViewA2 : TextView = view.findViewById(R.id.textViewA2)
//            var textViewB1 : TextView = view.findViewById(R.id.textViewB1)
//            var textViewB2 : TextView = view.findViewById(R.id.textViewB2)
//            var textViewC1 : TextView = view.findViewById(R.id.textViewC1)
//            var textViewC2 : TextView = view.findViewById(R.id.textViewC2)
//            var textViewD : TextView = view.findViewById(R.id.textViewD)
//            var textViewE : TextView = view.findViewById(R.id.textViewE)

            var recyclerView : RecyclerView = view.findViewById(R.id.recyclerView)
            var shapeImageView : ShapeableImageView = view.findViewById(R.id.shapeImageView)

        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ExamGradeAdapter.ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.exam_grade_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ExamGradeAdapter.ViewHolder, position: Int) {
//            when (examGradeCbse[position].sUBJECTNAME) {
//                "English" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_english)
//                        .into(holder.shapeImageView)
//                }
//                "Chemistry" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_chemistry)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_chemistry)
//                        .into(holder.shapeImageView)
//                }
//                "Biology" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_bio)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_biology)
//                        .into(holder.shapeImageView)
//                }
//                "Maths" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_maths)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_maths)
//                        .into(holder.shapeImageView)
//                }
//                "Hindi" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_maths)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_hindi)
//                        .into(holder.shapeImageView)
//                }
//                "Physics" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_physics)
//                        .into(holder.shapeImageView)
//                }
//                "Malayalam" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_malayalam)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_malayalam)
//                        .into(holder.shapeImageView)
//                }
//                "Arabic" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_arabic)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_arabic)
//                        .into(holder.shapeImageView)
//                }
//                "Accountancy" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_accounts)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_accountancy)
//                        .into(holder.shapeImageView)
//                }
//                "Social Science" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_social)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_social)
//                        .into(holder.shapeImageView)
//                }
//                "Economics" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_econonics)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_economics)
//                        .into(holder.shapeImageView)
//                }
//                "BasicScience" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_bio)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_biology)
//                        .into(holder.shapeImageView)
//                }
//                "Computer" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_computer)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_computer)
//                        .into(holder.shapeImageView)
//                }
//                "General" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_computer)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_computer)
//                        .into(holder.shapeImageView)
//                }
//            }

            when (examGradeCbse[position].sUBJECTNAME) {
                "English" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_english_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl

                    Glide.with(mContext)
                        .load(R.drawable.ic_study_english)
                        .into(holder.shapeImageView)
                }
                "Chemistry" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_chemistry_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_chemistry)
                        .into(holder.shapeImageView)
                }
                "Biology" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_bio_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.shapeImageView)
                }
                "Maths" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_maths_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_maths)
                        .into(holder.shapeImageView)
                }
                "Hindi" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_hindi_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_hindi)
                        .into(holder.shapeImageView)
                }
                "Physics" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_physics_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_physics)
                        .into(holder.shapeImageView)
                }
                "Malayalam" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_malayalam_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_malayalam)
                        .into(holder.shapeImageView)
                }
                "Arabic" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_arabic_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_arabic)
                        .into(holder.shapeImageView)
                }
                "Accountancy" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_accounts_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_accountancy)
                        .into(holder.shapeImageView)
                }
                "Social Science" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_social_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_social)
                        .into(holder.shapeImageView)
                }
                "Economics" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_economics_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_economics)
                        .into(holder.shapeImageView)
                }
                "BasicScience" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_bio_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.shapeImageView)
                }
                "Computer" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_computer_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_computer)
                        .into(holder.shapeImageView)
                }
                "General" -> {
                    val colorInt: Int = mContext.resources.getColor(R.color.color_computer_light)
                    val csl = ColorStateList.valueOf(colorInt)
                    holder.shapeImageView.strokeColor = csl
                    Glide.with(mContext)
                        .load(R.drawable.ic_study_computer)
                        .into(holder.shapeImageView)
                }
            }


            holder.textSubjectName.text = examGradeCbse[position].sUBJECTNAME
            holder.textViewStaff.text = "Staff Name : ${examGradeCbse[position].sTAFFFNAME}"
            holder.textPassMark.text = "Passmark : ${examGradeCbse[position].pASSMARK} / ${examGradeCbse[position].oUTOFFMARK}"
          //  holder.textTotalMarks.text = "Total Mark : ${examGradeCbse[position].sUBJECTTOTALMARK}"
            holder.textTotalAttended.text = "Total Attended : ${examGradeCbse[position].tOTALATTEND}"

            holder.recyclerView.layoutManager = LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false)
            holder.recyclerView.adapter = GradeCommonAdapter(
                examGradeCbse[position].gradeList,
                mContext
            )

//            holder.textViewA1.text = examGradeCbse[position].gRADEA1.toString()
//            holder.textViewA2.text = examGradeCbse[position].gRADEA2.toString()
//            holder.textViewB1.text = examGradeCbse[position].gRADEB1.toString()
//            holder.textViewB2.text = examGradeCbse[position].gRADEB2.toString()
//            holder.textViewC1.text = examGradeCbse[position].gRADEC1.toString()
//            holder.textViewC2.text = examGradeCbse[position].gRADEC2.toString()
//            holder.textViewD.text = examGradeCbse[position].gRADED.toString()
//            holder.textViewE.text = examGradeCbse[position].gRADEE.toString()


        }



        override fun getItemCount(): Int {
           return examGradeCbse.size
        }

    }

    class GradeCommonAdapter(var gradeList: ArrayList<GradeCommonModel.GradeList>,var mContext: Context)
        : RecyclerView.Adapter<GradeCommonAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textGradeName: TextView = view.findViewById(R.id.textGradeName)
            var textGradeValue: TextView = view.findViewById(R.id.textGradeValue)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeCommonAdapter.ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.grade_common_adapter, parent, false)
            return ViewHolder(itemView)

//            val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.grade_common_adapter, parent, false)
//            inflate.layoutParams.width = screenWidth / 10
//            return ViewHolder(inflate)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
           holder.textGradeName.text = gradeList[position].gRADENAME
            holder.textGradeValue.text = gradeList[position].gRADECOUNT.toString()
        }

        override fun getItemCount(): Int {
            return gradeList.size
        }

        val screenWidth: Int
            get() {
                val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display = wm.defaultDisplay
                val size = Point()
                display.getSize(size)
                return size.x
            }

    }

    private fun initFunction() {
        examGradeViewModel.getYearClassExam(adminId, schoolId)
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
                            getGradeCommonModel = ArrayList<GradeCommonModel>()
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

}

class GradeCommonModel(

    var oUTOFFMARK: Int,
    var pASSMARK: Int,
    var oUTOFFMARKCE: Int,
    var pASSMARKCE: Int,
    var sTAFFFNAME: String,
    var sUBJECTCODE: String,
    var sUBJECTID: Int,
    var sUBJECTNAME: String,
    var sUBJECTTOTALMARK: Double,
    var tOTALATTEND: Int,
    var gradeList: ArrayList<GradeList>,
){
    class GradeList(
        var gRADENAME: String,
        var gRADECOUNT :Int
    )

}