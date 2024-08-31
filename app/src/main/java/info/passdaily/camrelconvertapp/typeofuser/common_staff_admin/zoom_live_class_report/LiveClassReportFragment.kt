package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_live_class_report

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentLiveClassReportBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.AssignmentDetailStaffActivity
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.CreateObjectiveExam
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.*


@Suppress("DEPRECATION")
class LiveClassReportFragment : Fragment(),LiveClassReportListener {

    var TAG = "LiveClassReportFragment"
    private var _binding: FragmentLiveClassReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var localDBHelper : LocalDBHelper
    var toolBarClickListener : ToolBarClickListener? = null

    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0
    var adminId = 0
    var schoolId = 0

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null
    var spinnerSubject: AppCompatSpinner? = null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    lateinit var liveClassReportAdapter : LiveClassReportAdapter

    var recyclerViewVideo : RecyclerView? = null

    var textViewDate: TextInputEditText? = null
    var dateString : String?= null

    private lateinit var liveClassReportViewModel: LiveClassReportViewModel

    var getMeetingList = ArrayList<ZoomLiveClassReportModel.Meeting>()

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
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Live Class Report")

        // Inflate the layout for this fragment
        liveClassReportViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[LiveClassReportViewModel::class.java]


        // Inflate the layout for this fragment
        _binding = FragmentLiveClassReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraintLayoutContent = binding.constraintLayoutContent
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_state_live)
            .into(imageViewEmpty!!)
        shimmerViewContainer = binding.shimmerViewContainer

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass
        spinnerSubject = binding.spinnerSubject

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())

        textViewDate = binding.textViewDate
        textViewDate?.inputType = InputType.TYPE_NULL
        textViewDate?.keyListener = null

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
                getSubjectList(cLASSID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        spinnerSubject?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                sUBJECTID = getSubject[position].sUBJECTID
                getLiveClassReport(aCCADEMICID,cLASSID,sUBJECTID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        textViewDate?.setOnClickListener{
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(textViewDate?.windowToken, 0)
            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    val fromStr = "$year-$s-$dayOfMonth"
                    textViewDate?.setText(Utils.dateformat(fromStr))
                    getLiveClassReport(aCCADEMICID,cLASSID,sUBJECTID)
                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("Select Date")
            mDatePicker3.show()
            mDatePicker3.setButton(DialogInterface.BUTTON_NEGATIVE, "Clear Date") { _, _ ->
                textViewDate?.setText("")
                getLiveClassReport(aCCADEMICID,cLASSID,sUBJECTID)
            }
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }
        initFunction()
    }

    private fun getLiveClassReport(aCCADEMICID: Int, cLASSID: Int, sUBJECTID: Int) {
        // hashMap.put("ACCADEMIC_ID", this.s_aid);
        //        hashMap.put("CLASS_ID", this.scid);
        //        hashMap.put("SUBJECT_ID", this.ssid);
        //        hashMap.put("ADMIN_ID", Global.Admin_id);
        //        hashMap.put("DATE", this.str_new_start_date);

        dateString = if(Utils.dateformatYYYYMMdd(textViewDate?.text.toString()) != null){
            Utils.dateformatYYYYMMdd(textViewDate?.text.toString())
        }else{
            ""
        }

        val url = "Accademic/MeetingList"
        val jsonObject = JSONObject()
        try {

            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
            jsonObject.put("CLASS_ID", cLASSID)
            jsonObject.put("SUBJECT_ID", sUBJECTID)
            jsonObject.put("ADMIN_ID", adminId)
            jsonObject.put("DATE",dateString)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(CreateObjectiveExam.TAG,"jsonObject $jsonObject")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        liveClassReportViewModel.getLiveClassReport(url,accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getMeetingList= response.meetingList as ArrayList<ZoomLiveClassReportModel.Meeting>
                            if(getMeetingList.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    liveClassReportAdapter = LiveClassReportAdapter(
                                        this,
                                        getMeetingList,
                                        requireActivity(),
                                        TAG
                                    )
                                    recyclerViewVideo!!.adapter = liveClassReportAdapter

                                }
                            }else{
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_state_live)
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
                            getMeetingList = ArrayList<ZoomLiveClassReportModel.Meeting>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_state_live)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })

    }

    class LiveClassReportAdapter(var liveClassReportListener: LiveClassReportListener,
                                 var meetingList: ArrayList<ZoomLiveClassReportModel.Meeting>,
                                 var mContext: Context, var TAG: String)
        : RecyclerView.Adapter<LiveClassReportAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewSubject: TextView = view.findViewById(R.id.textViewSubject)
            var textStartDate: TextView = view.findViewById(R.id.textStartDate)
            var textEndDate: TextView = view.findViewById(R.id.textEndDate)
            var textClassAndTakenBy: TextView = view.findViewById(R.id.textClassAndTakenBy)

            var textTotStudent: TextView = view.findViewById(R.id.textTotStudent)
            var textAttended: TextView = view.findViewById(R.id.textAttended)

            var reportConstraint : ConstraintLayout = view.findViewById(R.id.reportConstraint)
            var endLiveConstraint : ConstraintLayout = view.findViewById(R.id.endLiveConstraint)

            var shapeImageView: ShapeableImageView = view.findViewById(R.id.shapeImageView)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.live_class_report_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.textViewSubject.text = meetingList[position].sUBJECTNAME
            holder.textClassAndTakenBy.text = "Class : ${meetingList[position].cLASSNAME},  Taken by ${meetingList[position].zOOMCREATEDBY} Teacher"


            if(!meetingList[position].zOOMSTARTDATE.isNullOrBlank()) {
                val date: Array<String> =
                    meetingList[position].zOOMSTARTDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                holder.textStartDate.text = "Start : ${Utils.formattedDateWords(dddd)}"
            }else{
                holder.textStartDate.text = "Start : "
            }

            if(!meetingList[position].zOOMENDDATE.isNullOrBlank()) {
                val date: Array<String> = meetingList[position].zOOMENDDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                holder.textEndDate.text = "End : ${Utils.formattedDateWords(dddd)}"
            }else{
                holder.textEndDate.text = "End : "
            }

            holder.textTotStudent.text = "Total Students : ${meetingList[position].tOTALSTUDENT}"
            holder.textAttended.text = "Attended students : ${meetingList[position].tOTALATTENDSTUDENT}"

            if (meetingList[position].zOOMMEETINGSTATUS != 2) {
                holder.endLiveConstraint.visibility = View.VISIBLE //0
            } else {
                holder.endLiveConstraint.visibility = View.GONE //8
            }

            when (meetingList[position].sUBJECTNAME) {
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

            holder.endLiveConstraint.setOnClickListener {
                liveClassReportListener.onEndLiveClickListener("Accademic/EndLiveMeeting",meetingList[position],position)
            }

            holder.reportConstraint.setOnClickListener {
                liveClassReportListener.onReportClick(meetingList[position])
            }

        }

        override fun getItemCount(): Int {
            return meetingList.size
        }

    }


    private fun initFunction() {
        liveClassReportViewModel.getYearClassExam(adminId, schoolId)
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

    fun getSubjectList(cLASSID : Int){
        liveClassReportViewModel.getSubjectStaff(cLASSID,adminId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getSubject = response.subjects as ArrayList<SubjectsModel.Subject>
                            var subject = Array(getSubject.size){""}
                            if(subject.isNotEmpty()){
                                for (i in getSubject.indices) {
                                    subject[i] = getSubject[i].sUBJECTNAME
                                }

                            }
                            if (spinnerSubject != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    subject
                                )
                                spinnerSubject?.adapter = adapter
                            }

                            Log.i(CreateObjectiveExam.TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(CreateObjectiveExam.TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            getSubject = ArrayList<SubjectsModel.Subject>()
                            Log.i(CreateObjectiveExam.TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    override fun onReportClick(meetingList: ZoomLiveClassReportModel.Meeting) {
        val intent = Intent(context, ZoomMeetingAttendedActivity::class.java)
        intent.putExtra("Z_LIVE_CLASS_ID", meetingList.zLIVECLASSID)
        intent.putExtra("ACCADEMIC_ID", meetingList.aCCADEMICID)
        intent.putExtra("CLASS_ID", meetingList.cLASSID)
        startActivity(intent)
    }

    override fun onEndLiveClickListener(url : String,
                                        meetingList: ZoomLiveClassReportModel.Meeting,
                                        position : Int) {
      //Accademic/EndLiveMeeting?ZLiveClassId=

        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["ZLiveClassId"] = meetingList.zLIVECLASSID
        liveClassReportViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Meeting Ended Successfully", constraintLayoutContent!!)
                                    initFunction()
                                }
                                Utils.resultFun(response) == "ERROR" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Meeting Ending Failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }

}

interface LiveClassReportListener{
    fun onReportClick(meetingList: ZoomLiveClassReportModel.Meeting)

    fun onEndLiveClickListener(url : String, meetingList: ZoomLiveClassReportModel.Meeting, position : Int)

}