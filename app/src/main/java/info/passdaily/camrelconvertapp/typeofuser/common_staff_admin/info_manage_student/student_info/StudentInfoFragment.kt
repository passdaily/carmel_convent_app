package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.student_info

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.imageview.ShapeableImageView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentStudentInfoBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class StudentInfoFragment : Fragment(),StudentInfoListener {

    var TAG = "StudentInfoFragment"
    private lateinit var studentInfoViewModel: StudentInfoViewModel
    private var _binding: FragmentStudentInfoBinding? = null
    private val binding get() = _binding!!
    lateinit var bottomSheetStudentInfo : BottomSheetStudentInfo

    // http://demostaff.passdaily.in/ElixirApi/Group/GroupAdd

    var gender = arrayOf("Select Gender", "Male", "Female", "other")
    var typeStr ="-1"
    var isWorking= false
    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var getStudentsList = ArrayList<StudentsInfoListModel.Students>()

    lateinit var mAdapter : StudentInfoAdapter

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var spinnerGender  : AppCompatSpinner? = null
    var toolBarClickListener : ToolBarClickListener? = null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()

    var aCCADEMICID = 0
    var cLASSID = 0

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
        toolBarClickListener?.setToolbarName("Student Parent Info")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID
        studentInfoViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StudentInfoViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentStudentInfoBinding.inflate(inflater, container, false)
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
        spinnerGender = binding.spinnerGender

        initFunction()

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())


        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                aCCADEMICID = getYears[position].aCCADEMICID
                if(isWorking) {
                    getStudentInfo()
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
                getStudentInfo()

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val status= ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, gender)
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender?.adapter = status
        spinnerGender?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                if(isWorking) {
                    when (position) {
                        0 -> { typeStr = "-1"}
                        1 -> { typeStr = "0" }
                        2 -> { typeStr = "1" }
                        3 -> { typeStr = "2" }
                    }
                    getStudentInfo()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.fab.setOnClickListener {
            val dialog1 = DialogCreateStudentInfo(this)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, DialogCreateStudentInfo.TAG)
        }

        bottomSheetStudentInfo = BottomSheetStudentInfo()

    }


    fun initFunction(){
        studentInfoViewModel.getYearClassExam(adminId, schoolId )
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


    fun getStudentInfo(){
        val jsonObject = JSONObject()
        try {
            jsonObject.put("STUDENT_FNAME", "")
            jsonObject.put("ADMISSION_NUMBER", "")
            jsonObject.put("STUDENT_FATHER_NAME", "")
            jsonObject.put("CLASS_ID", cLASSID)
            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
            jsonObject.put("STUDENT_GENDER", typeStr)
            jsonObject.put("STUDENT_GUARDIAN_NUMBER", "")
            jsonObject.put("SCHOOL_ID", schoolId)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")

        val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


        studentInfoViewModel.getStudentInfo(submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource ${resource.data?.code()}")
                    Log.i(TAG,"resource ${resource.message}")
                    Log.i(TAG,"resource ${resource.status}")

                    if(resource.data?.code() == 500){
                        constraintEmpty?.visibility = View.VISIBLE
                        recyclerViewVideo?.visibility = View.GONE
                        shimmerViewContainer?.visibility = View.GONE
                        Glide.with(this)
                            .load(R.drawable.ic_empty_progress_report)
                            .into(imageViewEmpty!!)

                        textEmpty?.text =   resources.getString(R.string.no_results)
                    }


                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWorking = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getStudentsList= response.studentsList as ArrayList<StudentsInfoListModel.Students>
                            if(getStudentsList.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {

                                    mAdapter =  StudentInfoAdapter(
                                        this,
                                        getStudentsList,
                                        requireActivity()
                                    )

                                    recyclerViewVideo!!.adapter = mAdapter
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
                            getStudentsList = ArrayList<StudentsInfoListModel.Students>()
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

    class StudentInfoAdapter(var studentInfoListener: StudentInfoListener,
                             var studentsList: ArrayList<StudentsInfoListModel.Students>,
                             var context: Context) :
        RecyclerView.Adapter<StudentInfoAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textStudentName: TextView = view.findViewById(R.id.textStudentName)
            var textClassName: TextView = view.findViewById(R.id.textClassName)
            var textParentName : TextView = view.findViewById(R.id.textParentName)
            var textContactNumber : TextView = view.findViewById(R.id.textContactNumber)
            var textGender : TextView = view.findViewById(R.id.textGender)
            var textUpdated : TextView = view.findViewById(R.id.textUpdated)
            var shapeImageView : ShapeableImageView = view.findViewById(R.id.shapeImageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.student_info_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textStudentName.text = studentsList[position].sTUDENTFNAME
            holder.textClassName.text = "Class : ${studentsList[position].cLASSNAME}"
            holder.textParentName.text = "Parent : ${studentsList[position].sTUDENTGUARDIANNAME}"
            holder.textContactNumber.text = "Mobile : ${studentsList[position].sTUDENTGUARDIANNUMBER}"
            when (studentsList[position].sTUDENTGENDER) {
                0 -> {
                    holder.textGender.text = "Gender : Male"
                }
                1 -> {
                    holder.textGender.text = "Gender : Female"
                }
                else -> {
                    holder.textGender.text = "Gender : Other"
                }
            }
            holder.itemView.setOnClickListener {
                studentInfoListener.onUpdateClick(studentsList,position)
            }

            if (studentsList[position].sTUDENTIMAGE != null) {
                Glide.with(context).load(
                    Global.student_image_url + studentsList[position].sTUDENTIMAGE!!
                ) //STAFF_IMAGE -> http://demo.passdaily.in/Photos/StaffImageA0D181192F902C6AE338BEDF36FC3251.jpg
                    //STAFF_IMAGE -> 1A07304FC14301B29E49B4DA301B0EA5.png
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate()
                            .placeholder(R.drawable.round_account_button_with_user_inside)
                    )
                    .thumbnail(0.5f)
                    .into(holder.shapeImageView)
            }

            holder.textUpdated.isVisible = false
            if(studentsList[position].updated){
                holder.textUpdated.isVisible = true
            }
        }

        override fun getItemCount(): Int {
            return studentsList.size
        }

    }


    private fun progressStart() {
        val dialog1 = ProgressBarDialog()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.isCancelable = false
        dialog1.show(transaction, ProgressBarDialog.TAG)
    }

    fun progressStop() {
        val fragment: ProgressBarDialog? =
            requireActivity().supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
        if (fragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }

    override fun onUpdateClick(students:ArrayList<StudentsInfoListModel.Students>,position :Int) {
        bottomSheetStudentInfo = BottomSheetStudentInfo(this,students,position)
        bottomSheetStudentInfo.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onSubmitListener(
        url: String,
        jsonObject: RequestBody,
        position: Int,
        ADMISSION_NUMBER: String,
        ADMISSION_DATE: String?,

        STUDENT_FNAME: String,
        STUDENT_GENDER: String,
        STUDENT_DOB: String?,
        STUDENT_BLOODGROUP: String,
        STUDENT_BIRTHPLACE: String,
        STUDENT_BIRTHPLACE_TALUK: String,
        STUDENT_BIRTHPLACE_DISTRICT: String,
        STUDENT_BIRTHPLACE_STATE: String,

        STUDENT_NATIONALITY: String,
        STUDENT_MOTHERTONGUE: String,
        STUDENT_RELIGION: String,
        STUDENT_CASTE: String,
        STUDENT_CAST_TYPE: String,
        STUDENT_LAST_STUDIED: String,
        STUDENT_SSLC_REG_NO: String,
        STUDENT_AADHAR_NUMBER: String,
        STUDENT_PADDRESS: String,
        STUDENT_CADDRESS: String,

        STUDENT_FATHER_NAME: String,
        STUDENT_GUARDIAN_NUMBER: String,
        STUDENT_FATHER_QUALIFICATION: String,
        STUDENT_FATHER_OCCUPATION: String,
        STUDENT_EMAIL_ID: String,

        STUDENT_MOTHER_NAME: String,
        STUDENT_PHONE_NUMBER: String,
        STUDENT_MOTHER_QUALIFICATION: String,
        STUDENT_MOTHER_OCCUPATION: String,

        sTUDENTIMAGE: String
    ) {

        studentInfoViewModel.getCommonPostFun(url,jsonObject)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    getStudentsList[position].updated = true
                                    //onSuccessMessage("Student Created")
                                    getStudentsList[position].aDMISSIONNUMBER = ADMISSION_NUMBER
                                    getStudentsList[position].aDMISSIONDATE = ADMISSION_DATE!!

                                    getStudentsList[position].sTUDENTFNAME  = STUDENT_FNAME
                                    getStudentsList[position].sTUDENTGENDER = STUDENT_GENDER.toInt()
                                    getStudentsList[position].sTUDENTDOB = STUDENT_DOB!!
                                    getStudentsList[position].sTUDENTBLOODGROUP = STUDENT_BLOODGROUP
                                    getStudentsList[position].sTUDENTBIRTHPLACE = STUDENT_BIRTHPLACE
                                    getStudentsList[position].sTUDENTBIRTHPLACETALUK = STUDENT_BIRTHPLACE_TALUK
                                    getStudentsList[position].sTUDENTBIRTHPLACEDISTRICT = STUDENT_BIRTHPLACE_DISTRICT
                                    getStudentsList[position].sTUDENTBIRTHPLACESTATE = STUDENT_BIRTHPLACE_STATE

                                    getStudentsList[position].sTUDENTNATIONALITY = STUDENT_NATIONALITY
                                    getStudentsList[position].sTUDENTMOTHERTONGUE = STUDENT_MOTHERTONGUE
                                    getStudentsList[position].sTUDENTRELIGION = STUDENT_RELIGION
                                    getStudentsList[position].sTUDENTCASTE = STUDENT_CASTE
                                    getStudentsList[position].sTUDENTCASTTYPE = STUDENT_CAST_TYPE
                                    getStudentsList[position].sTUDENTLASTSTUDIED = STUDENT_LAST_STUDIED
                                    getStudentsList[position].sTUDENTSSLCREGNO = STUDENT_SSLC_REG_NO
                                    getStudentsList[position].sTUDENTAADHARNUMBER = STUDENT_AADHAR_NUMBER
                                    getStudentsList[position].sTUDENTPADDRESS = STUDENT_PADDRESS
                                    getStudentsList[position].sTUDENTCADDRESS = STUDENT_CADDRESS

                                    getStudentsList[position].sTUDENTFATHERNAME = STUDENT_FATHER_NAME
                                    getStudentsList[position].sTUDENTGUARDIANNUMBER = STUDENT_GUARDIAN_NUMBER
                                    getStudentsList[position].sTUDENTFATHERQUALIFICATION = STUDENT_FATHER_QUALIFICATION
                                    getStudentsList[position].sTUDENTFATHEROCCUPATION = STUDENT_FATHER_OCCUPATION
                                    getStudentsList[position].sTUDENTEMAILID = STUDENT_EMAIL_ID

                                    getStudentsList[position].sTUDENTMOTHERNAME = STUDENT_MOTHER_NAME
                                    getStudentsList[position].sTUDENTPHONENUMBER = STUDENT_PHONE_NUMBER
                                    getStudentsList[position].sTUDENTMOTHERQUALIFICATION = STUDENT_MOTHER_QUALIFICATION
                                    getStudentsList[position].sTUDENTMOTHEROCCUPATION = STUDENT_MOTHER_OCCUPATION

                                    getStudentsList[position].sTUDENTIMAGE = sTUDENTIMAGE

                                    mAdapter.notifyItemChanged(position)
                                    bottomSheetStudentInfo.dismiss() //to hide it
                                    Utils.getSnackBarGreen(requireActivity(),"Student Details Updated Successfully",constraintLayoutContent!!)
                                    if ((position + 1) == getStudentsList.size) {
                                        Utils.getSnackBar4K(requireActivity(),"Student list ends here",constraintLayoutContent!!)
                                    } else {
                                        onUpdateClick(getStudentsList,position+1)
                                    }
                                    progressStop()
                                }
                                Utils.resultFun(response) == "ERROR" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Student is already existing", constraintLayoutContent)
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Failed While Creating student", constraintLayoutContent)

                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent)
                        }
                        Status.LOADING -> {
                             progressStart()
                            Log.i(DialogCreateStudentInfo.TAG,"loading")
                        }
                    }
                }
            })
    }

    override fun onPreviewNextListener(position: Int,message: String,indexMove:Int) {
      //  mAdapter.notifyItemChanged(position)
        bottomSheetStudentInfo.dismiss() //to hide it
        if(indexMove == 0){  ///preview
            if (position <= 0) {
                Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
            } else {
                onUpdateClick(getStudentsList,position-1)
            }
        }else if(indexMove == 1){  ///next
            if ((position + 1) == getStudentsList.size) {
            Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
        } else {
            onUpdateClick(getStudentsList,position+1)
        }

        }
        Log.i(TAG,"position $position")

    }

    override fun onSuccessMessage(message : String){
        Utils.getSnackBarGreen(requireActivity(), message, constraintLayoutContent!!)
//        Utils.getSnackBarGreen(requireActivity(),message,binding.constraintLayoutContent)
        getStudentInfo()
    }
    override fun onDeleteClick(studentId: Int,sTUDACCADEMICID :Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want confirm delete this\nstudent?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                onDeleteClickListener("", studentId,aCCADEMICID)
            }
            .setNegativeButton(
                "No"
            ) { dialog, _ -> //  Action for 'NO' Button
                dialog.cancel()
            }
        //Creating dialog box
        val alert = builder.create()
        //Setting the title manually
        alert.setTitle("Delete")
        alert.show()
        val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonbackground.setTextColor(Color.BLACK)
        val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonbackground1.setTextColor(Color.BLACK)
    }


    fun onDeleteClickListener(url: String, studentId: Int,acedamicId : Int){
        studentInfoViewModel.getStudentInfoDelete("StudentList/StudentDropById",studentId,acedamicId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Student Deleted Successfully", constraintLayoutContent!!)
                                    bottomSheetStudentInfo.dismiss()
                                    getStudentInfo()
                                }
                                Utils.resultFun(response) == "1" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Student Deletion Failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }
}

interface StudentInfoListener{
    fun onPreviewNextListener( position: Int,message: String,indexMove:Int)
    fun onSuccessMessage(message : String)
    fun onUpdateClick(students: ArrayList<StudentsInfoListModel.Students>, position: Int)
    fun onSubmitListener(
        url: String,
        jsonObject: RequestBody,
        position: Int,
        ADMISSION_NUMBER: String,
        ADMISSION_DATE: String?,

        STUDENT_FNAME: String,
        STUDENT_GENDER: String,
        STUDENT_DOB: String?,
        STUDENT_BLOODGROUP: String,
        STUDENT_BIRTHPLACE: String,
        STUDENT_BIRTHPLACE_TALUK: String,
        STUDENT_BIRTHPLACE_DISTRICT: String,
        STUDENT_BIRTHPLACE_STATE: String,

        STUDENT_NATIONALITY: String,
        STUDENT_MOTHERTONGUE: String,
        STUDENT_RELIGION: String,
        STUDENT_CASTE: String,
        STUDENT_CAST_TYPE: String,
        STUDENT_LAST_STUDIED: String,
        STUDENT_SSLC_REG_NO: String,
        STUDENT_AADHAR_NUMBER: String,
        STUDENT_PADDRESS: String,
        STUDENT_CADDRESS: String,

        STUDENT_FATHER_NAME: String,
        STUDENT_GUARDIAN_NUMBER: String,
        STUDENT_FATHER_QUALIFICATION: String,
        STUDENT_FATHER_OCCUPATION: String,
        STUDENT_EMAIL_ID: String,

        STUDENT_MOTHER_NAME: String,
        STUDENT_PHONE_NUMBER: String,
        STUDENT_MOTHER_QUALIFICATION: String,
        STUDENT_MOTHER_OCCUPATION: String,

        sTUDENTIMAGE: String
    )
    fun onDeleteClick(studentId: Int,sTUDACCADEMICID :Int)
}
