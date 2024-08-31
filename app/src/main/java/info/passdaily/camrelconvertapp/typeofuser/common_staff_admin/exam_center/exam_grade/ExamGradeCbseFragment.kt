package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.exam_grade

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.imageview.ShapeableImageView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentObjectiveExamListBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card.SampleObject
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card.SampleObject1
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import org.json.JSONException
import java.util.*

@Suppress("DEPRECATION")
class ExamGradeCbseFragment(var url : String,var title : String) : Fragment() {


    var TAG = "ExamGradeCbseFragment"
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

    var tableA: TableLayout? = null
    var tableB: TableLayout? = null
    var tableC: TableLayout? = null
    var tableD: TableLayout? = null


    var horizontalScrollViewB: HorizontalScrollView? = null
    var horizontalScrollViewD: HorizontalScrollView? = null

    var scrollViewC: ScrollView? = null
    var scrollViewD: ScrollView? = null


    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var spinnerSubject : AppCompatSpinner? = null

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null


   // var getGradeMark = ArrayList<ArrayList<GradeCommonModel.GradeList>>()

    var getExamGradeCbse = ArrayList<ExamGradeCBSEModel.ExamGrade>()




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



      //  relativeLayout?.addView(ExamGrade_TableView(requireActivity(),header))

    }

    private fun getExamGrade(aCCADEMICID: Int, cLASSID: Int, eXAMID: Int) {
        examGradeViewModel.getExamGradeCbseResponse(
            url,aCCADEMICID,cLASSID,eXAMID,adminId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWork = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getExamGradeCbse = response.examGrade as ArrayList<ExamGradeCBSEModel.ExamGrade>

                            if(getExamGradeCbse.isNotEmpty()){
                            //    getGradeMark = ArrayList<ArrayList<GradeCommonModel.GradeList>>()
                                Global.getGradeMark = ArrayList<ArrayList<GradeCommonModel.GradeList>>()
                                relativeLayout?.removeAllViews();
                                //   recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                relativeLayout?.visibility = View.VISIBLE

                                var header = arrayOf<String>()
                                var namesList = header.toMutableList()
                                namesList.add("SUBJECT")

                                for(i in getExamGradeCbse.indices){

                                    var tempGrade: ArrayList<GradeCommonModel.GradeList> = ArrayList()
                                    tempGrade.add(GradeCommonModel.GradeList("A1",getExamGradeCbse[i].gRADEA1))
                                    tempGrade.add(GradeCommonModel.GradeList("A2",getExamGradeCbse[i].gRADEA2))
                                    tempGrade.add(GradeCommonModel.GradeList("B1",getExamGradeCbse[i].gRADEB1))
                                    tempGrade.add(GradeCommonModel.GradeList("B2",getExamGradeCbse[i].gRADEB2))
                                    tempGrade.add(GradeCommonModel.GradeList("C1",getExamGradeCbse[i].gRADEC1))
                                    tempGrade.add(GradeCommonModel.GradeList("C2",getExamGradeCbse[i].gRADEC2))
                                    tempGrade.add(GradeCommonModel.GradeList(" D ",getExamGradeCbse[i].gRADED))
                                    tempGrade.add(GradeCommonModel.GradeList(" E ",getExamGradeCbse[i].gRADEE))

//                                    getGradeCommonModel.add(GradeCommonModel(getExamGradeCbse[i].oUTOFFMARK, getExamGradeCbse[i].pASSMARK,
//                                        0,0,
//                                        getExamGradeCbse[i].sTAFFFNAME, getExamGradeCbse[i].sUBJECTCODE,
//                                        getExamGradeCbse[i].sUBJECTID, getExamGradeCbse[i].sUBJECTNAME,
//                                        getExamGradeCbse[i].sUBJECTTOTALMARK, getExamGradeCbse[i].tOTALATTEND,tempGrade))



                                    Global.getGradeMark.add(tempGrade)
                                    namesList.add(getExamGradeCbse[i].sUBJECTNAME)

                               }
                             //   Global.getGradeMark = getGradeMark
                                header = namesList.toTypedArray()
                                relativeLayout?.addView(ExamGradeTableView(requireActivity(),header))



                            }else{
                                relativeLayout?.visibility = View.GONE
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
                            relativeLayout?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            relativeLayout?.visibility = View.GONE
                            relativeLayout?.removeAllViews();
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                          //  getGradeCommonModel = ArrayList<GradeCommonModel>()
                            getExamGradeCbse = ArrayList<ExamGradeCBSEModel.ExamGrade>()
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
            holder.textTotalMarks.text = "Total Mark : ${examGradeCbse[position].sUBJECTTOTALMARK}"
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
        examGradeViewModel.getYearClassExam(adminId,schoolId)
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


//    class ExamGrade_TableView @JvmOverloads constructor(
//        context: Context,
//        var header: Array<String>,
//       // var gradeList: ArrayList<ArrayList<GradeCommonModel.GradeList>>,
//    ) : RelativeLayout(context) {
//        var TAG = "Tablelayout"
//
//       // var header = arrayOf<String>()
//
//        var tableA: TableLayout? = null
//        var tableB: TableLayout? = null
//        var tableC: TableLayout? = null
//        var tableD: TableLayout? = null
//        var tableMG: TableLayout? = null
//        var horizontalScrollViewB: HorizontalScrollView? = null
//        var horizontalScrollViewD: HorizontalScrollView? = null
//        var horizontalScrollViewMG: HorizontalScrollView? = null
//        var scrollViewC: ScrollView? = null
//        var scrollViewD: ScrollView? = null
//        var sampleObjects: List<SampleObject>? = null
//        var sampleObjects1: List<SampleObject1> = ArrayList()
//
//        // int HeaderCellWidth[] = new int[header.length];
//        lateinit var HeaderCellWidth: IntArray
//        lateinit var tableCWidth: IntArray
//
//
//        private fun sampleObjects() : List<SampleObject> {
//            val sampleObjects: List<SampleObject> = ArrayList()
//
//            for (y in Global.getGradeMark.indices) {
//                  var temp = ""
//
//                for (z in Global.getGradeMark[y].indices) {
//                    temp += if(z+1 == Global.getGradeMark[y].size){
//                        Global.getGradeMark[y][z].gRADECOUNT.toString()
//                    }else{
//                        Global.getGradeMark[y][z].gRADECOUNT.toString() + "~"
//                    }
////                    }
//                    Log.i(TAG, "temp $y ${ Global.getGradeMark[y][z].gRADECOUNT}")
//                    //   marks += temp
//                }
//
//                    var taleRowForTableD = TableRow(context)
//                   val info = temp.split("~".toRegex()).toTypedArray()
////                  val array3: ArrayList<String> = ArrayList()
////                  Collections.addAll(array3, *info)
//
//
//                    //for(int x=0 ; x<loopCount; x++){
//                    for (l in info.indices) {
//                        //    TableRow.LayoutParams params = new TableRow.LayoutParams( HeaderCellWidth[x+1],LayoutParams.MATCH_PARENT);
//                        val params = TableRow.LayoutParams(230, LayoutParams.MATCH_PARENT)
//                        params.setMargins(2, 2, 2, 0)
//
//                        // TextView textViewB = this.bodyTextView(info[x]);
//
//                        // TextView textViewB = this.bodyTextView(info[x]);
//                        val textViewB = bodyTextView(info[l])
//                        val typeface = ResourcesCompat.getFont(
//                            context, R.font.poppins_medium
//                        )
//                        textViewB.setTypeface(typeface)
//                        textViewB.setTextSize(
//                            TypedValue.COMPLEX_UNIT_PX,
//                            resources.getDimension(R.dimen.text_size_02)
//                        )
//                        textViewB.setTextColor(resources.getColor(R.color.gray_600))
//                        textViewB.setBackgroundResource(R.drawable.cellshapes_two_white)
//                        taleRowForTableD.addView(textViewB, params)
//                    }
//                    tableD!!.addView(taleRowForTableD)
//
//            }
//
//            return sampleObjects
//        }
//
//
//        init {
////            //MarkLnvn/ProgressReportLnvn?AccademicId=6&ClassId=5&ExamId=1&AdminId=1&Dummy=0
////            GET_MarkList =
////                FileUtils.url + "MarkLnvn/ProgressReportLnvn?AccademicId=" + 8 +
////                        "&ClassId=" + 17 + "&ExamId=" + 1 + "&AdminId=" + 1 + "&Dummy=0"
////            RunGetFunMarkList(GET_MarkList, context)
//
//            HeaderCellWidth = IntArray(header.size)
//            tableCWidth = IntArray(Global.getGradeMark[0].size + 2)
//
//            newTable_init()
//        }
//
//
//        //Table Data Feed
//        fun newTable_init() {
//            initComponents()
//            sampleObjects = sampleObjects()
//            setComponentsId()
//            setScrollViewAndHorizontalScrollViewTag()
//            horizontalScrollViewB!!.addView(tableB)
//            scrollViewC!!.addView(tableC)
//            scrollViewD!!.addView(horizontalScrollViewD)
//            horizontalScrollViewD!!.addView(tableD)
//            addComponentToMainLayout()
//            addTableRowToTableA()
//            addTableRowToTableB()
//            resizeHeaderHeight()
//            tableRowHeaderCellWidth
//            generateTableC_AndTable_B()
//            resizeBodyTableRowHeight()
//        }
//
//        /////initial Components list
//        private fun initComponents() {
//            tableA = TableLayout(context)
//            tableB = TableLayout(context)
//            tableC = TableLayout(context)
//            tableD = TableLayout(context)
//            horizontalScrollViewB = MyHorizontalScrollView(context)
//            horizontalScrollViewD = MyHorizontalScrollView(context)
//            scrollViewC = MyScrollView(context)
//            scrollViewD = MyScrollView(context)
//
//            //Log.i(TAG,"subjectlist "+ this.subjectlist.size());
//        }
//
//        ///named scrollview and Horizontal Tags
//        private fun setScrollViewAndHorizontalScrollViewTag() {
//            horizontalScrollViewB!!.tag = "horizontal scroll view b"
//            horizontalScrollViewD!!.tag = "horizontal scroll view d"
//            scrollViewC!!.tag = "scroll view c"
//            scrollViewD!!.tag = "scroll view d"
//        }
//
//        ////set id for tables
//        @SuppressLint("ResourceType")
//        private fun setComponentsId() {
//            tableA!!.id = 1
//            horizontalScrollViewB!!.id = 2
//            scrollViewC!!.id = 3
//            scrollViewD!!.id = 4
//        }
//
//        private fun addComponentToMainLayout() {
//            val componentB_Params =
//                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//            componentB_Params.addRule(RIGHT_OF, tableA!!.id)
//            val componentC_Params =
//                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//            componentC_Params.addRule(BELOW, tableA!!.id)
//            val componentD_Params =
//                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
//            componentD_Params.addRule(RIGHT_OF, scrollViewC!!.id)
//            componentD_Params.addRule(BELOW, horizontalScrollViewB!!.id)
//            this.addView(tableA)
//            this.addView(horizontalScrollViewB, componentB_Params)
//            this.addView(scrollViewC, componentC_Params)
//            this.addView(scrollViewD, componentD_Params)
//        }
//
//        private fun addTableRowToTableA() {
//            tableA!!.addView(componentATableRow())
//            //   this.tableA.addView(this.componentA_MaxMark_TableRow());
//            //    this.tableA.addView(this.componentA_PassMark_TableRow());
//            //    this.tableA.addView(this.componentA_SubWisePass_TableRow());
//            //   this.tableA.addView(this.componentA_SubWisePassPercentage_TableRow());
//            //   this.tableA.addView(this.componentA_DummyRow_TableRow());
//        }
//
//        private fun addTableRowToTableB() {
//            tableB!!.addView(componentBTableRow())
//            //            this.tableB.addView(this.componentB_MaxMark_TableRow());
////            this.tableB.addView(this.componentB_PassMark_TableRow());
////
////            this.tableB.addView(this.componentB_SubWisePass_TableRow());
////            this.tableB.addView(this.componentB_SubWisePassPercentage_TableRow());
////            this.tableB.addView(this.componentB_DummyRow_TableRow());
//        }
//
//        ///// row 1 header
//        fun componentATableRow(): TableRow {
//            val componentATabele = TableRow(context)
//            val params = TableRow.LayoutParams(330, LayoutParams.MATCH_PARENT)
//            params.setMargins(2, 0, 0, 0)
//            val textView = headerTextView(header[0])
//            val typeface = ResourcesCompat.getFont(
//                context, R.font.poppins_bold
//            )
//            textView.typeface = typeface
//            textView.setTextSize(
//                TypedValue.COMPLEX_UNIT_PX,
//                resources.getDimension(R.dimen.text_size_02)
//            )
//            textView.setTextColor(resources.getColor(R.color.black))
//            textView.setBackgroundResource(R.drawable.cellshapes_white)
//            textView.layoutParams = params
//            componentATabele.setBackgroundColor(resources.getColor(R.color.white))
//            componentATabele.addView(textView)
//            return componentATabele
//        }
//
//        /////row 1 fields
//        fun componentBTableRow(): TableRow {
//            val componentBTableRow = TableRow(context)
//            val headerFieldCount = Global.getGradeMark[0].size
//            val params = TableRow.LayoutParams(230, LayoutParams.MATCH_PARENT)
//            params.setMargins(2, 0, 2, 0)
//
//
//            //may be want to changes here
//            for (i in 0 until headerFieldCount) {
//                //  TextView textView =this.headerTextView(array3.get(i+1).substring(0,3));
//                val textView = headerTextView(Global.getGradeMark[0][i].gRADENAME)
//                val typeface = ResourcesCompat.getFont(
//                    context, R.font.poppins_medium
//                )
//                textView.typeface = typeface
//                textView.setTextSize(
//                    TypedValue.COMPLEX_UNIT_PX,
//                    resources.getDimension(R.dimen.text_size_02)
//                )
//                textView.setTextColor(resources.getColor(R.color.gray_600))
//                textView.layoutParams = params
//                textView.setBackgroundResource(R.drawable.cellshapes_white)
//                textView.setTextColor(Color.BLACK)
//                componentBTableRow.addView(textView)
//            }
//            componentBTableRow.setBackgroundColor(resources.getColor(R.color.white))
//            return componentBTableRow
//        }
//
//        private fun resizeHeaderHeight() {
//            val productNameHeaderTableRow = tableA!!.getChildAt(0) as TableRow
//            val produceInfoTableRow = tableB!!.getChildAt(0) as TableRow
//            val rowAheight = viewHeight(productNameHeaderTableRow)
//            val rowBheight = viewHeight(produceInfoTableRow)
//            val tableRow =
//                if (rowAheight < rowBheight) productNameHeaderTableRow else produceInfoTableRow
//            val finalHeight = if (rowAheight > rowBheight) rowAheight else rowBheight
//            matchLayoutHeight(tableRow, finalHeight)
//        }
//
//        val tableRowHeaderCellWidth: Unit
//            get() {
//                val tableAChildCount = (tableA!!.getChildAt(0) as TableRow).childCount
//                val tableBChildCount = (tableB!!.getChildAt(0) as TableRow).childCount
//                for (i in 0 until tableAChildCount + tableBChildCount) {
//                    if (i == 0) {
//                        HeaderCellWidth[i] =
//                            viewWidth((tableA!!.getChildAt(0) as TableRow).getChildAt(i))
//                    } else {
//                        tableCWidth[i] =
//                            viewWidth((tableB!!.getChildAt(0) as TableRow).getChildAt(i - 1))
//                    }
//                }
//            }
//
//        private fun generateTableC_AndTable_B() {
//            for (i in 1 until header.size) {
//                val tableRowForTableC = tableRowForTableC(header[i])
//                // TableRow tableRowForTableD =this.tableRowForTableD(sampleObject,j);
//                tableC!!.addView(tableRowForTableC)
//            }
//        }
//
//        fun tableRowForTableC(subName: String?): TableRow {
//            // TableRow.LayoutParams params = new TableRow.LayoutParams( HeaderCellWidth[0],LayoutParams.MATCH_PARENT);
//            val params = TableRow.LayoutParams(330, LayoutParams.MATCH_PARENT)
//            params.setMargins(2, 2, 2, 0)
//            val tableRowForTableC = TableRow(context)
//            val textView = bodyTextView(subName)
//            //     TextView textView = this.bodyTextView(header[0]);
//            val typeface = ResourcesCompat.getFont(
//                context, R.font.poppins_bold
//            )
//            textView.typeface = typeface
//            textView.setTextSize(
//                TypedValue.COMPLEX_UNIT_PX,
//                resources.getDimension(R.dimen.text_size_02)
//            )
//            textView.setTextColor(resources.getColor(R.color.gray_600))
//            tableRowForTableC.addView(textView, params)
//            textView.setBackgroundResource(R.drawable.cellshapes_white)
//            return tableRowForTableC
//        }
//
//        // resize body table row height
//        fun resizeBodyTableRowHeight() {
//            val tableC_ChildCount = tableC!!.childCount
//            for (x in 0 until tableC_ChildCount) {
//                val productNameHeaderTableRow = tableC!!.getChildAt(x) as TableRow
//                val productInfoTableRow = tableD!!.getChildAt(x) as TableRow
//                val rowAHeight = viewHeight(productNameHeaderTableRow)
//                val rowBHeight = viewHeight(productInfoTableRow)
//                val tableRow =
//                    if (rowAHeight < rowBHeight) productNameHeaderTableRow else productInfoTableRow
//                val finalHeight = if (rowAHeight > rowBHeight) rowAHeight else rowBHeight
//                matchLayoutHeight(tableRow, finalHeight)
//            }
//        }
//
//        private fun matchLayoutHeight(tableRow: TableRow, finalHeight: Int) {
//            val tableRowChildCount = tableRow.childCount
//            if (tableRow.childCount == 1) {
//                val view = tableRow.getChildAt(0)
//                val params = view.layoutParams as TableRow.LayoutParams
//                params.height = finalHeight - (params.bottomMargin + params.topMargin)
//            }
//            for (i in 0 until tableRowChildCount) {
//                val view = tableRow.getChildAt(i)
//                val params = view.layoutParams as TableRow.LayoutParams
//                if (!isTheHeighestLayout(tableRow, i)) {
//                    params.height = finalHeight - (params.bottomMargin + params.topMargin)
//                    return
//                }
//            }
//        }
//
//        private fun isTheHeighestLayout(tableRow: TableRow, layoutposition: Int): Boolean {
//            val tableRowChildCount = tableRow.childCount
//            var heightViewPosition = -1
//            var viewHeight = 0
//            for (i in 0 until tableRowChildCount) {
//                val view = tableRow.getChildAt(i)
//                val height = viewHeight(view)
//                if (viewHeight < height) {
//                    heightViewPosition = i
//                    viewHeight = height
//                }
//            }
//            return heightViewPosition == layoutposition
//        }
//
//        private fun viewWidth(view: View): Int {
//            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
//            return view.measuredWidth
//        }
//
//        private fun viewHeight(view: View): Int {
//            view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
//            return view.measuredHeight
//        }
//
//        // table cell standard TextView
//        fun bodyTextView(label: String?): TextView {
//            val bodyTextView = TextView(context)
//            bodyTextView.setBackgroundColor(Color.WHITE)
//            bodyTextView.text = label
//            bodyTextView.gravity = Gravity.CENTER
//            bodyTextView.setPadding(10, 10, 10, 10)
//            return bodyTextView
//        }
//
//        // header standard TextView
//        fun headerTextView(label: String?): TextView {
//            val headerTextView = TextView(context)
//            headerTextView.setBackgroundColor(Color.WHITE)
//            headerTextView.text = label
//            headerTextView.gravity = Gravity.CENTER
//            headerTextView.setPadding(15, 15, 15, 15)
//            return headerTextView
//        }
//
//        internal inner class MyHorizontalScrollView(context: Context?) :
//            HorizontalScrollView(context) {
//            override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
//                val tag = this.tag as String
//                if (tag.equals("horizontal scroll view b", ignoreCase = true)) {
//                    horizontalScrollViewD!!.scrollTo(l, 0)
//                } else {
//                    horizontalScrollViewB!!.scrollTo(l, 0)
//                }
//            }
//        }
//
//        // scroll view custom class
//        internal inner class MyScrollView(context: Context?) :
//            ScrollView(context) {
//            override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
//                val tag = this.tag as String
//                if (tag.equals("scroll view c", ignoreCase = true)) {
//                    scrollViewD!!.scrollTo(0, t)
//                } else {
//                    scrollViewC!!.scrollTo(0, t)
//                }
//            }
//        }
//    }
}
