//package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.mark_absent
//
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.content.Context
//import android.content.DialogInterface
//import android.graphics.Color
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.appcompat.widget.AppCompatSpinner
//import androidx.cardview.widget.CardView
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager.widget.ViewPager
//import com.bumptech.glide.Glide
//import com.facebook.shimmer.ShimmerFrameLayout
//import com.google.android.material.tabs.TabLayout
//import com.google.android.material.textfield.TextInputEditText
//import info.passdaily.camrelconvertapp.R
//import info.passdaily.camrelconvertapp.databinding.FragmentMarkAbsentBinding
//import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
//import info.passdaily.camrelconvertapp.model.*
//import info.passdaily.camrelconvertapp.services.Global
//import info.passdaily.camrelconvertapp.services.Status
//import info.passdaily.camrelconvertapp.services.Utils
//import info.passdaily.camrelconvertapp.services.ViewModelFactory
//import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
//import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
//import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.AssignmentStaffFragment
//import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.json.JSONException
//import org.json.JSONObject
//import java.text.SimpleDateFormat
//import java.util.*
//
//@Suppress("DEPRECATION")
//class MarkAbsentFragment : Fragment(), MarkAbsentListener {
//
//    var TAG = "MarkAbsentFragment"
//    private lateinit var markAbsentViewModel: MarkAbsentViewModel
//    private var _binding: FragmentMarkAbsentBinding? = null
//    private val binding get() = _binding!!
//
//
//    var toolBarClickListener: ToolBarClickListener? = null
//
//    private lateinit var localDBHelper: LocalDBHelper
//    var adminId = 0
//    var getYears = ArrayList<GetYearClassExamModel.Year>()
//    var getClass = ArrayList<GetYearClassExamModel.Class>()
//    var getSubject = ArrayList<SubjectsModel.Subject>()
//
//    var getStudentDetails = ArrayList<MarkAbsentModel.StudentDetail>()
//    lateinit var mAdapter : MarkAbsentAdapter
//
//    var spinnerAcademic: AppCompatSpinner? = null
//    var spinnerClass: AppCompatSpinner? = null
//    var editTextDate: TextInputEditText? = null
//
//
//    var constraintLayoutContent: ConstraintLayout? = null
//    var shimmerViewContainer: ShimmerFrameLayout? = null
//
//    var recyclerViewVideo: RecyclerView? = null
//
//
//        var aCCADEMICID = 0
//        var cLASSID = 0
//        var fromStr = ""
//        var startDate = ""
//        var typeStatus = 0
//
//
//    var constraintEmpty: ConstraintLayout? = null
//    var imageViewEmpty: ImageView? = null
//    var textEmpty: TextView? = null
//
//    var cardViewPublish : CardView? = null
//    var cardViewUnPublish : CardView? = null
//
//    var textPublish : TextView? = null
//    var textUnPublish : TextView? = null
//
//    var  date = ""
//
//
//    var mContext: Context? = null
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (mContext == null) {
//            mContext = context.applicationContext
//        }
//        try {
//            toolBarClickListener = context as ToolBarClickListener
//        } catch (e: Exception) {
//            Log.i(TAG, "Exception $e")
//        }
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        Global.screenState = "staffhomepage"
//        toolBarClickListener?.setToolbarName("Mark Absents")
//        // Inflate the layout for this fragment
//        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
//        localDBHelper = LocalDBHelper(requireActivity())
//        var user = localDBHelper.viewUser()
//        adminId = user[0].ADMIN_ID
//        // Inflate the layout for this fragment
//        markAbsentViewModel = ViewModelProviders.of(
//            this,
//            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
//        )[MarkAbsentViewModel::class.java]
//
//        _binding = FragmentMarkAbsentBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        constraintLayoutContent = binding.constraintLayoutContent
//        constraintEmpty = binding.constraintEmpty
//        imageViewEmpty = binding.imageViewEmpty
//        textEmpty = binding.textEmpty
//        Glide.with(this)
//            .load(R.drawable.ic_empty_progress_report)
//            .into(imageViewEmpty!!)
//        shimmerViewContainer = binding.shimmerViewContainer
//
//        spinnerAcademic = binding.spinnerAcademic
//        spinnerClass = binding.spinnerClass
//
//
//         cardViewPublish = binding.cardViewPublish
//         cardViewUnPublish = binding.cardViewUnPublish
//
//        textPublish = binding.textPublish
//        textUnPublish = binding.textUnPublish
//
//        recyclerViewVideo = binding.recyclerViewVideo
//        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())
//
//        spinnerAcademic?.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long
//            ) {
//                aCCADEMICID = getYears[position].aCCADEMICID
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }
//
//        spinnerClass?.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long
//            ) {
//                cLASSID = getClass[position].cLASSID
//                getMarkAbsentStaff()
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }
//
//
//
//
//        cardViewPublish?.setOnClickListener {
//            cardViewPublish?.setCardBackgroundColor(requireActivity().resources.getColor(R.color.white))
//            textPublish?.setTextColor(requireActivity().resources.getColor(R.color.black))
//            cardViewUnPublish?.setCardBackgroundColor(requireActivity().resources.getColor(R.color.gray_100))
//            textUnPublish?.setTextColor(requireActivity().resources.getColor(R.color.gray_600))
//
//            typeStatus = 0
//            getMarkAbsentStaff()
//        }
//
//        cardViewUnPublish?.setOnClickListener {
//            cardViewPublish?.setCardBackgroundColor(requireActivity().resources.getColor(R.color.gray_100))
//            textPublish?.setTextColor(requireActivity().resources.getColor(R.color.gray_600))
//            cardViewUnPublish?.setCardBackgroundColor(requireActivity().resources.getColor(R.color.white))
//            textUnPublish?.setTextColor(requireActivity().resources.getColor(R.color.black))
//            typeStatus = 1
//            getMarkAbsentStaff()
//        }
//
//
//        val fab = binding.fab
//        fab.setOnClickListener {
////            val dialog1 = CreateZoomScheduled(this)
////            val transaction = requireActivity().supportFragmentManager.beginTransaction()
////            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
////            dialog1.show(transaction, CreateZoomScheduled.TAG)
//        }
//        date = SimpleDateFormat("yyyy/MM/dd").format(Date())
//        initFunction()
//    }
//
//
//    private fun initFunction() {
//        markAbsentViewModel.getYearClassExam(adminId)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//
//                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
//                            var years = Array(getYears.size) { "" }
//                            for (i in getYears.indices) {
//                                years[i] = getYears[i].aCCADEMICTIME
//                            }
//                            if (spinnerAcademic != null) {
//                                val adapter = ArrayAdapter(
//                                    requireActivity(),
//                                    android.R.layout.simple_spinner_dropdown_item,
//                                    years
//                                )
//                                spinnerAcademic?.adapter = adapter
//                            }
//
//                            getClass = response.classList as ArrayList<GetYearClassExamModel.Class>
//                            var classX = Array(getClass.size) { "" }
//                            for (i in getClass.indices) {
//                                classX[i] = getClass[i].cLASSNAME
//                            }
//                            if (spinnerClass != null) {
//                                val adapter = ArrayAdapter(
//                                    requireActivity(),
//                                    android.R.layout.simple_spinner_dropdown_item,
//                                    classX
//                                )
//                                spinnerClass?.adapter = adapter
//                            }
//                            Log.i(TAG, "initFunction SUCCESS")
//                        }
//                        Status.ERROR -> {
//                            Log.i(TAG, "initFunction ERROR")
//                        }
//                        Status.LOADING -> {
//                            Log.i(TAG, "initFunction LOADING")
//                        }
//                    }
//                }
//            })
//    }
//
//    @SuppressLint("SimpleDateFormat")
//    private fun getMarkAbsentStaff() {
//
//        Log.i(TAG,"typeStatus $typeStatus")
//        Log.i(TAG,"date $date")
//        Log.i(TAG,"cLASSID $cLASSID")
//        Log.i(TAG,"aCCADEMICID $aCCADEMICID")
//
//        markAbsentViewModel.getMarkAbsentStaff(cLASSID,aCCADEMICID,date)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            shimmerViewContainer?.visibility = View.GONE
//                            val response = resource.data?.body()!!
//                            var getStudentDetail = response.studentDetails as ArrayList<MarkAbsentModel.StudentDetail>
//
//                            if(typeStatus == 0) {
//                                for (i in getStudentDetail.indices) {
//                                    if (typeStatus == getStudentDetail[i].aBSENTS) {
//                                        getStudentDetails.add(getStudentDetail[i])
//                                    }
//                                }
//                            }else{
//                                for (i in getStudentDetail.indices) {
//                                    if (getStudentDetail[i].sTUDENTROLLNUMBER.toString().toFloat().toInt() == getStudentDetail[i].aBSENTS) {
//                                        getStudentDetails.add(getStudentDetail[i])
//                                    }
//                                }
//                            }
//
//                            if(getStudentDetails.isNotEmpty()){
//                                recyclerViewVideo?.visibility = View.VISIBLE
//                                constraintEmpty?.visibility = View.GONE
//                                if (isAdded) {
//                                    mAdapter =   MarkAbsentAdapter(
//                                        this,
//                                        getStudentDetails,
//                                        requireActivity(),
//                                        typeStatus,
//                                        TAG
//                                    )
//                                    recyclerViewVideo!!.adapter = mAdapter
//
//                                }
//                            }else{
//                                recyclerViewVideo?.visibility = View.GONE
//                                constraintEmpty?.visibility = View.VISIBLE
//                                Glide.with(this)
//                                    .load(R.drawable.ic_empty_progress_report)
//                                    .into(imageViewEmpty!!)
//
//                                textEmpty?.text =  resources.getString(R.string.no_results)
//                            }
//
//                            Log.i(AssignmentStaffFragment.TAG,"getSubjectList SUCCESS")
//                        }
//                        Status.ERROR -> {
//                            constraintEmpty?.visibility = View.VISIBLE
//                            recyclerViewVideo?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.GONE
//
//                            Glide.with(this)
//                                .load(R.drawable.ic_no_internet)
//                                .into(imageViewEmpty!!)
//                            textEmpty?.text =  resources.getString(R.string.no_internet)
//                            Log.i(AssignmentStaffFragment.TAG,"getSubjectList ERROR")
//                        }
//                        Status.LOADING -> {
//                            recyclerViewVideo?.visibility = View.GONE
//                            constraintEmpty?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.VISIBLE
//                            getStudentDetails = ArrayList<MarkAbsentModel.StudentDetail>()
//                            Glide.with(this)
//                                .load(R.drawable.ic_empty_progress_report)
//                                .into(imageViewEmpty!!)
//
//                            textEmpty?.text =  resources.getString(R.string.loading)
//                            Log.i(AssignmentStaffFragment.TAG,"getSubjectList LOADING")
//                        }
//                    }
//                }
//            })
//
//    }
//
//    class MarkAbsentAdapter(var markAbsentListener: MarkAbsentListener,
//                            var studentDetails: ArrayList<MarkAbsentModel.StudentDetail>,
//                            var context: Context,var typeStatus : Int,var TAG: String) : RecyclerView.Adapter<MarkAbsentAdapter.ViewHolder>() {
//        var titleName = ""
//        var titleMessage = ""
//        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
//            var textViewRollNo: TextView = view.findViewById(R.id.textViewRollNo)
//            var imageViewCheck : ImageView = view.findViewById(R.id.imageViewCheck)
//        }
//        override fun onCreateViewHolder(
//            parent: ViewGroup,
//            viewType: Int
//        ): ViewHolder {
//            val itemView = LayoutInflater.from(parent.context)
//                .inflate(R.layout.mark_absent_adapter, parent, false)
//            return ViewHolder(itemView)
//        }
//
//        @SuppressLint("SetTextI18n")
//        override fun onBindViewHolder(holder: MarkAbsentAdapter.ViewHolder, position: Int) {
//            holder.textViewTitle.text = studentDetails[position].sTUDENTFNAME
//            when {
//                studentDetails[position].sTUDENTROLLNUMBER != null -> {
//                    holder.textViewRollNo.text =
//                        "Roll.No : ${studentDetails[position].sTUDENTROLLNUMBER.toString().toFloat().toInt()}"
//                }
//                else -> {
//                    holder.textViewRollNo.text = "Roll.No : "
//                }
//            }
//
//
//            if (studentDetails[position].selectedValue) {
//                // viewHolder.checkBox.setChecked(true);
//                holder.imageViewCheck.setImageResource(R.drawable.ic_checked_black)
//                if(typeStatus == 1){
//                    titleName = "Mark Present"
//                    titleMessage = "Mark ${studentDetails[position].sTUDENTFNAME} as present"
//                }else{
//                    titleName = "Mark Absent"
//                    titleMessage = "Mark ${studentDetails[position].sTUDENTFNAME} as Absent"
//                }
//                markAbsentListener.onDialogClickListener(position,studentDetails[position],titleName,titleMessage,typeStatus)
//            } else {
//                //viewHolder.checkBox.setChecked(false);
//                holder.imageViewCheck.setImageResource(R.drawable.ic_check_gray)
//            }
//
//            holder.itemView.setOnClickListener {
//                studentDetails[position].selectedValue = !studentDetails[position].selectedValue
//                notifyItemChanged(position)
//
//            }
//        }
//
//        override fun getItemCount(): Int {
//            return studentDetails.size
//        }
//
//    }
//
////    override fun onCreateClick(message: String) {
////        Log.i(TAG,"onCreateClick")
////        getMarkAbsentStaff()
////        Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)
////    }
//
//    override fun onDialogClickListener(
//        position: Int,
//        studentDetails: MarkAbsentModel.StudentDetail,
//        titleName: String,
//        titleMessage: String,
//        typeStatus: Int
//    ) {
//        val builder = AlertDialog.Builder(context)
//        builder.setMessage(titleMessage)
//            .setCancelable(false)
//            .setPositiveButton("Yes") { _, _ ->
//                if(typeStatus == 1){
//                    markPresentStaff(studentDetails.aTTENDANCEID,studentDetails.sTUDENTID)
//                }else {
//                    markAbesentFun(studentDetails)
//                }
//            }
//            .setNegativeButton(
//                "No"
//            ) { dialog, _ -> //  Action for 'NO' Button
//                dialog.cancel()
//                getStudentDetails[position].selectedValue = false
//                mAdapter.notifyItemChanged(position)
//            }
//        //Creating dialog box
//        val alert = builder.create()
//        //Setting the title manually
//        alert.setTitle(titleName)
//        alert.show()
//        val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
//        buttonbackground.setTextColor(Color.BLACK)
//        val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
//        buttonbackground1.setTextColor(Color.BLACK)
//        Log.i(TAG,"studentDetails $studentDetails")
//    }
//
//    private fun markPresentStaff(aTTENDANCEID: Int, sTUDENTID: Int) {
//
//        markAbsentViewModel.getMarkPresentStaff(aTTENDANCEID,sTUDENTID)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            progressStop()
//                            when {
//                                Utils.resultFun(response) == "DELETED" -> {
//                                    Utils.getSnackBarGreen(requireActivity(), "Successfully Marked Present", constraintLayoutContent!!)
//                                    getMarkAbsentStaff()
//                                }
//                                Utils.resultFun(response) == "FAILED" -> {
//                                    Utils.getSnackBar4K(requireActivity(), "Marking Present Failed", constraintLayoutContent!!)
//                                }
//                            }
//                        }
//                        Status.ERROR -> {
//                            progressStop()
//                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
//                        }
//                        Status.LOADING -> {
//                            progressStart()
//                            Log.i(TAG,"loading")
//                        }
//                    }
//                }
//            })
//    }
//
//    private fun markAbesentFun(studentDetails: MarkAbsentModel.StudentDetail) {
//
//        var url = "Details/MarkAbsent"
//        var sTUDENTROLLNUMBER = when {
//            studentDetails.sTUDENTROLLNUMBER != null -> {
//                studentDetails.sTUDENTROLLNUMBER.toString().toFloat().toInt() }
//            else -> { 0 }
//        }
//        val jsonObject = JSONObject()
//        try {
//            jsonObject.put("STUDENT_ROLLNO", sTUDENTROLLNUMBER)
//            jsonObject.put("CLASS_ID", cLASSID)
//            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
//            jsonObject.put("ATTENDANCE_MARKEDBY", adminId)
//            jsonObject.put("ABSENT_DATE", date)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//        val academicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
//        markAbsentViewModel.getCommonPostFun(url,academicRe)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            Log.i(TAG,"response $response")
//                            progressStop()
//                            when {
//                                Utils.resultFun(response) == "SUCCESS" -> {
//                                    Utils.getSnackBarGreen(requireActivity(), "Absent Marked", constraintLayoutContent!!)
//                                    getMarkAbsentStaff()
//                                }
//                                Utils.resultFun(response) == "UPDATED" -> {
//                                    Utils.getSnackBar4K(requireActivity(), "Already Absent Marked", constraintLayoutContent!!)
//                                }
//                                Utils.resultFun(response) == "NOT_EXIST" -> {
//                                    Utils.getSnackBar4K(requireActivity(), "Student not Found", constraintLayoutContent!!)
//                                }
//                                Utils.resultFun(response) == "FAILED" -> {
//                                    Utils.getSnackBar4K(requireActivity(), "Marking Absent failed", constraintLayoutContent!!)
//                                }
//                            }
//                        }
//                        Status.ERROR -> {
//                            progressStop()
//                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
//                            Log.i(TAG,"ERROR")
//                        }
//                        Status.LOADING -> {
//                            progressStart()
//                            Log.i(TAG,"loading")
//                        }
//                    }
//                }
//            })
//    }
//
////    override fun onDeleteClickListener(url: String, zMeetingId: Int) {
////        Log.i(TAG,"onDeleteClickListener")
////    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        toolBarClickListener = null
//        mContext = null
//        Log.i(TAG,"onDestroy")
//    }
//
//    private fun progressStart() {
//        val dialog1 = ProgressBarDialog()
//        val transaction = requireActivity().supportFragmentManager.beginTransaction()
//        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
//        dialog1.isCancelable = false
//        dialog1.show(transaction, ProgressBarDialog.TAG)
//    }
//
//    fun progressStop() {
//        val fragment: ProgressBarDialog? =
//            requireActivity().supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
//        if (fragment != null) {
//            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
//        }
//    }
//
//}
//
////interface MarkAbsentListener {
//////    fun onCreateClick(message: String)
////
////    fun onDialogClickListener(
////        position: Int,
////        studentDetails: MarkAbsentModel.StudentDetail,
////        titleName: String,
////        titleMessage: String,
////        typeStatus: Int
////    )
////
//////    fun onDeleteClickListener(url: String, zMeetingId: Int)
////}