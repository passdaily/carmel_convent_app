package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progrees_card_new

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Point
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import com.google.android.material.imageview.ShapeableImageView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentProgressCardNewBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card.ProgressCardViewModel
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener


@Suppress("DEPRECATION")
class ProgressCardCbseNewFragment(var title : String) : Fragment() {

    private lateinit var progressCardViewModel: ProgressCardViewModel
    private var _binding: FragmentProgressCardNewBinding? = null
    private val binding get() = _binding!!

    var adminId = 0
    var schoolId = 0
    var TAG = "ProgressCardCbseNewFragment"

    var aCCADEMICID = 0
    var cLASSID = 0
    var eXAMID = 0
    var toolBarClickListener: ToolBarClickListener? = null
    var cLASSNAME = ""

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getExam = ArrayList<GetYearClassExamModel.Exam>()


    var markList = ArrayList<ProgressCardCbseModel.Mark>()
    var staffList = ArrayList<ProgressCardCbseModel.Staff>()
    var studentList = ArrayList<ProgressCardCbseModel.Student>()
    var subjectList = ArrayList<ProgressCardCbseModel.Subject>()


    var updatedStudentList = ArrayList<ProgressCardCbseModel.Student>()

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null
    var spinnerExam: AppCompatSpinner? = null


    var constraintLayout : ConstraintLayout? = null


    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null
    
    var isWork = false

    var recyclerViewVideo : RecyclerView? = null

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
        toolBarClickListener?.setToolbarName(title)
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

        _binding = FragmentProgressCardNewBinding.inflate(inflater, container, false)
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


        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())



        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                aCCADEMICID = getYears[position].aCCADEMICID
                if(isWork){
                    progressFun()
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
        progressCardViewModel.getYearClassExam(adminId, schoolId )
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

        progressCardViewModel.getProgressReportCBSE(aCCADEMICID, cLASSID, eXAMID, adminId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            isWork = true
                            markList = response.markList as ArrayList<ProgressCardCbseModel.Mark>
                            staffList = response.staffList as ArrayList<ProgressCardCbseModel.Staff>
                            studentList = response.studentList as ArrayList<ProgressCardCbseModel.Student>
                            subjectList = response.subjectList as ArrayList<ProgressCardCbseModel.Subject>

                            if(response.markList.isNotEmpty()){
                                for(stu in studentList.indices){
//                                    Log.i(TAG,"studentList[stu] sTUDENTFNAME ${studentList[stu].sTUDENTFNAME}")
//                                    Log.i(TAG,"studentList[stu] sTUDENTROLLNUMBER ${studentList[stu].sTUDENTROLLNUMBER}")
//                                    Log.i(TAG,"studentList[stu] className $cLASSNAME")
//                                    Log.i(TAG,"studentList[stu] sTUDENTGUARDIANNAME ${studentList[stu].sTUDENTGUARDIANNAME}")
//                                    Log.i(TAG,"studentList[stu] sTUDENTGUARDIANNUMBER ${studentList[stu].sTUDENTGUARDIANNUMBER}")
                                    var totalMarks = 0
                                    var gradeA = 0
                                    var gradeB = 0

                                    var tempMarkList = ArrayList<ProgressCardCbseModel.Mark>()
                                    for(mark in markList.indices) {

                                        if(studentList[stu].sTUDENTROLLNUMBER == markList[mark].sTUDENTROLLNUMBER){
//
                                            if(markList[mark].tOTALMARK.toInt() == -1){
                                                markList[mark].tOTALMARK = 0f
                                            }
                                            totalMarks += markList[mark].tOTALMARK.toInt()

                                            if(markList[mark].mARKGRADE == "A"){
                                                gradeA ++
                                            }
                                            if(markList[mark].mARKGRADE == "B"){
                                                gradeB ++
                                            }

                                            markList[mark].pASSSTATUS =  markList[mark].tOTALMARK >= markList[mark].pASSMARK


                                            // val percent = 100 * TOTAL_MARK / oUTOFFMARK
                                            markList[mark].pERCENTAGE = 100 * markList[mark].tOTALMARK.toInt() / markList[mark].oUTOFFMARK.toInt()

                                            for(sub in subjectList.indices){
                                                if(subjectList[sub].sUBJECTID == markList[mark].sUBJECTID){
                                                    markList[mark].sUBJECTNAME = subjectList[sub].sUBJECTNAME
                                                    markList[mark].sUBJECTICON = subjectList[sub].sUBJECTICON
                                                    markList[mark].sUBJECTWISEPASS = subjectList[sub].sUBJECTWISEPASS
                                                    markList[mark].tOTALATTEND = subjectList[sub].tOTALATTEND

                                                }
                                            }
                                            Log.i(TAG,"markList ${markList[mark]}")
                                            tempMarkList.add(markList[mark])
                                        }

                                    }


                                    updatedStudentList.add(ProgressCardCbseModel.Student(
                                        studentList[stu].aCCADEMICID,
                                        studentList[stu].aDMISSIONNUMBER,
                                        studentList[stu].cLASSID,
                                        studentList[stu].sTUDENTFNAME,
                                        studentList[stu].sTUDENTGUARDIANNAME,
                                        studentList[stu].sTUDENTGUARDIANNUMBER,
                                        studentList[stu].sTUDENTID,
                                        studentList[stu].sTUDENTROLLNUMBER,
                                        cLASSNAME,
                                        tempMarkList))

                                    //  Log.i(TAG,"passText $passText")

                                    Log.i(TAG,"totalMarks $totalMarks")
                                    Log.i(TAG,"gradeA $gradeA")
                                    Log.i(TAG,"gradeB $gradeB")

                                }


                                recyclerViewVideo?.visibility = View.VISIBLE
                                shimmerViewContainer?.visibility = View.GONE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewVideo!!.adapter =
                                        ProgressCardAdapter(
                                            updatedStudentList,
                                            requireActivity(),
                                            TAG
                                        )
                                }
                            }else{
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)
                                textEmpty?.text =  resources.getString(R.string.no_results)
                                shimmerViewContainer?.visibility = View.GONE
                                recyclerViewVideo?.visibility = View.GONE
                            }


                            Log.i(TAG, "progressFun SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewVideo?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)

                            Log.i(TAG, "progressFun ERROR")
                        }
                        Status.LOADING -> {
                            markList = ArrayList<ProgressCardCbseModel.Mark>()
                            staffList = ArrayList<ProgressCardCbseModel.Staff>()
                            studentList = ArrayList<ProgressCardCbseModel.Student>()
                            subjectList = ArrayList<ProgressCardCbseModel.Subject>()
                            updatedStudentList = ArrayList<ProgressCardCbseModel.Student>()

                            constraintEmpty?.visibility = View.GONE
                            recyclerViewVideo?.visibility = View.GONE
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




    class ProgressCardAdapter(
        var updateList: ArrayList<ProgressCardCbseModel.Student>,
        var context: Context,
        var TAG: String) : RecyclerView.Adapter<ProgressCardAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewClass: TextView = view.findViewById(R.id.textViewClass)
            var textViewName: TextView = view.findViewById(R.id.textViewName)
            var recyclerView : RecyclerView = view.findViewById(R.id.recyclerView)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.progress_card_new_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.textViewName.text = updateList[position].sTUDENTFNAME

            holder.textViewClass.text = "Class : ${updateList[position].cLASSNAME}"

            holder.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            holder.recyclerView.adapter = SubjectAdapter(updateList[position].markList,context)


        }

        override fun getItemCount(): Int {
            return updateList.size
        }

        class SubjectAdapter(
            var subjectsList: ArrayList<ProgressCardCbseModel.Mark>,
            var context: Context
        ) :
            RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {
            var index = 0

            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                var textViewSubject: TextView = view.findViewById(R.id.textViewSubject)
                var cardViewSubject: CardView = view.findViewById(R.id.cardViewSubject)
                var shapeImageView : ShapeableImageView =  view.findViewById(R.id.shapeImageView)
                var textViewMark : TextView  = view.findViewById(R.id.textViewMark)
                var textViewTotal  : TextView  = view.findViewById(R.id.textViewTotal)
                var constraintCard  : ConstraintLayout  = view.findViewById(R.id.constraintCard)
                var progressBar : ProgressBar?  = view.findViewById(R.id.progressBar)

            }

            override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
//                val itemView = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.progress_card_list_subject_adapter, parent, false)
//                return ViewHolder(itemView)

                val inflate: View = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.progress_card_list_subject_adapter, viewGroup, false)
                inflate.layoutParams.width =
                    (screenWidth - context.resources.getDimension(R.dimen.screen_width_cbse).toInt()) / 2
                return ViewHolder(inflate)
            }

            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun onBindViewHolder(holder: ViewHolder, position: Int) {

                holder.textViewSubject.text = subjectsList[position].sUBJECTNAME


                if(subjectsList[position].pASSSTATUS){
                    holder.textViewMark.text = Html.fromHtml(String.format(
                            "Mark : ${subjectsList[position].tOTALMARK} / ${subjectsList[position].oUTOFFMARK}" +
                            "<br>Grade : ${subjectsList[position].mARKGRADE}"+"<font color='#04B557'><br>Pass</font>"))//<br>//&nbsp;&nbsp;&nbsp;
                }else{
                    holder.textViewMark.text = Html.fromHtml(String.format(
                            "Mark : ${subjectsList[position].tOTALMARK} / ${subjectsList[position].oUTOFFMARK}" +
                            "<br>Grade : ${subjectsList[position].mARKGRADE}"+"<font color='#FF2222'><br>Fail</font>"))
                }



                holder.textViewTotal.text = "${subjectsList[position].tOTALMARK} %"

                holder.progressBar?.progress = subjectsList[position].pERCENTAGE

                when (subjectsList[position].sUBJECTICON) {
                    "English.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_english), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_english)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        //  holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_english))
                        Glide.with(context)
                            .load(R.drawable.ic_study_english)
                            .into(holder.shapeImageView)
                    }
                    "Chemistry.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_chemistry), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_chemistry)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        // holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_chemistry))
                        Glide.with(context)
                            .load(R.drawable.ic_study_chemistry)
                            .into(holder.shapeImageView)
                    }
                    "Biology.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_bio), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_bio)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        //   holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_bio))
                        Glide.with(context)
                            .load(R.drawable.ic_study_biology)
                            .into(holder.shapeImageView)
                    }
                    "Maths.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_maths), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_maths)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        //   holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_maths))
                        Glide.with(context)
                            .load(R.drawable.ic_study_maths)
                            .into(holder.shapeImageView)
                    }
                    "Hindi.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_hindi), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_hindi)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        //   holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_hindi))
                        Glide.with(context)
                            .load(R.drawable.ic_study_hindi)
                            .into(holder.shapeImageView)
                    }
                    "Physics.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_physics), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_physics)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        //  holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_physics))
                        Glide.with(context)
                            .load(R.drawable.ic_study_physics)
                            .into(holder.shapeImageView)
                    }
                    "Malayalam.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_malayalam), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_malayalam)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        //    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_malayalam))
                        Glide.with(context)
                            .load(R.drawable.ic_study_malayalam)
                            .into(holder.shapeImageView)
                    }
                    "Arabic.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_arabic), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_arabic)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        //    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_arabic))
                        Glide.with(context)
                            .load(R.drawable.ic_study_arabic)
                            .into(holder.shapeImageView)
                    }
//                    "Islamic" -> {
//                        holder.cardViewSubject.setCardBackgroundColor(context.resources.getColor(R.color.color_arabic_light))
//
//                        val colorInt: Int = context.resources.getColor(R.color.color_arabic_light)
////                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
//                        val csl = ColorStateList.valueOf(colorInt)
//                        holder.shapeImageView.strokeColor = csl
//                        //    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_arabic))
//                        Glide.with(context)
//                            .load(R.drawable.ic_study_arabic)
//                            .into(holder.shapeImageView)
//                    }
                    "Accountancy.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_accounts), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_accounts)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        //    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_accounts))
                        Glide.with(context)
                            .load(R.drawable.ic_study_accountancy)
                            .into(holder.shapeImageView)
                    }
                    "Social.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_social), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_social)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        //    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_social))
                        Glide.with(context)
                            .load(R.drawable.ic_study_social)
                            .into(holder.shapeImageView)
                    }
                    "Economics.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_econonics), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_econonics)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        //   holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_econonics))
                        Glide.with(context)
                            .load(R.drawable.ic_study_economics)
                            .into(holder.shapeImageView)
                    }
                    "BasicScience.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_bio), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_bio)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        //   holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_bio))
                        Glide.with(context)
                            .load(R.drawable.ic_study_biology)
                            .into(holder.shapeImageView)
                    }
                    "Computer.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_computer), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_computer)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        //   holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_computer))
                        Glide.with(context)
                            .load(R.drawable.ic_study_computer)
                            .into(holder.shapeImageView)
                    }
//                    "I T" -> {
//                        holder.cardViewSubject.setCardBackgroundColor(context.resources.getColor(R.color.color_computer_light))
//
//                        val colorInt: Int = context.resources.getColor(R.color.color_computer_light)
////                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
//                        val csl = ColorStateList.valueOf(colorInt)
//                        holder.shapeImageView.strokeColor = csl
//                        //   holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_computer))
//                        Glide.with(context)
//                            .load(R.drawable.ic_study_computer)
//                            .into(holder.shapeImageView)
//                    }
                    "General.png" -> {
                        val generatedColor = Utils.generateTransparentColor(context.resources.getColor(R.color.color_general), 0.06)
                        holder.constraintCard.setBackgroundColor(generatedColor)

                        val colorInt: Int = context.resources.getColor(R.color.color100_general)
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
                        val csl = ColorStateList.valueOf(colorInt)
                        holder.shapeImageView.strokeColor = csl
                        //  holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_computer))
                        Glide.with(context)
                            .load(R.drawable.ic_study_general)
                            .into(holder.shapeImageView)
                    }
                }

//
            }

            override fun getItemCount(): Int {
                return subjectsList.size
            }

            override fun getItemViewType(position: Int): Int {
                return position
            }

            val screenWidth: Int
                get() {
                    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    val display = wm.defaultDisplay
                    val size = Point()
                    display.getSize(size)
                    return size.x
                }

        }


    }






}