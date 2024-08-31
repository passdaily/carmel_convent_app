package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.student_remark_layout

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Html
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
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetStudentRemarkBinding
import info.passdaily.camrelconvertapp.databinding.FragmentManageGroupBinding
import info.passdaily.camrelconvertapp.databinding.FragmentRemarkRegisterBinding
import info.passdaily.camrelconvertapp.databinding.FragmentStudentRemarkBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register.BottomMarkRegisterCBSE
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register.MarkRegisterLpUpFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register_msps.BottomMarkRegisterMspsLpUp
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.GroupViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.CreateObjectiveExam
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.StudentRemarkViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.remarks.BottomMarkRemarkRegister
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class StudentRemarkFragment : Fragment(),StuRemarkRegisterClicker {

    var TAG = "StudentRemarkFragment"
    private lateinit var studentRemarkViewModel: StudentRemarkViewModel
    private var _binding: FragmentStudentRemarkBinding? = null
    private val binding get() = _binding!!

    lateinit var bottomMarkRemark: BottomMarkStudentRemark
    var isWorking= false
    // http://demostaff.passdaily.in/ElixirApi/Group/GroupAdd
    var aCCADEMICID = 0
    var cLASSID = 0
    var eXAMID = 0


    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()

    lateinit var mAdapter : StudentRemarkAdapter

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var spinnerExam : AppCompatSpinner? = null
    var toolBarClickListener : ToolBarClickListener? = null

    var getStudentRemark = ArrayList<StudentRemarksModel.Remarks>()
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
        toolBarClickListener?.setToolbarName("Student Remark")
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
        _binding = FragmentStudentRemarkBinding.inflate(inflater, container, false)
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


        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())


        binding.accedemicText.text = requireActivity().resources.getText(R.string.select_year)
        binding.classText.text = requireActivity().resources.getText(R.string.select_class)

        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                aCCADEMICID = getYears[position].aCCADEMICID
                if(isWorking){
                    getFinalList()
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
                getFinalList()

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        initFunction()
        bottomMarkRemark = BottomMarkStudentRemark()
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


    fun getFinalList(){
        studentRemarkViewModel.getStudentRemark(aCCADEMICID,cLASSID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWorking = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!

                            getStudentRemark = response.remarksList as ArrayList<StudentRemarksModel.Remarks>
                            if(getStudentRemark.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    mAdapter =  StudentRemarkAdapter(
                                        this,
                                        getStudentRemark,
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
                            getStudentRemark = ArrayList<StudentRemarksModel.Remarks>()
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


    class StudentRemarkAdapter(
        var remarkRegisterClicker : StuRemarkRegisterClicker,
        var getStudentRemark: ArrayList<StudentRemarksModel.Remarks>,
        var context: Context)
        : RecyclerView.Adapter<StudentRemarkAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textStudentName: TextView = view.findViewById(R.id.textStudentName)
            var textClassName: TextView = view.findViewById(R.id.textClassName)
            var textRollNo : TextView = view.findViewById(R.id.textRollNo)

            var editTextWork : TextView = view.findViewById(R.id.editTextWork)

            var textUpdated  : TextView = view.findViewById(R.id.textUpdated)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.student_remark_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textStudentName.text = getStudentRemark[position].sTUDENTFNAME
            holder.textRollNo.text = "Admission No : ${getStudentRemark[position].aDMISSIONNUMBER}"
            holder.textClassName.text = "Roll.No : ${getStudentRemark[position].sTUDENTROLLNUMBER}"

            if (getStudentRemark[position].sTUDENTEXTRA != null) {
                holder.editTextWork.text = getStudentRemark[position].sTUDENTEXTRA.toString()
            }


            holder.itemView.setOnClickListener {
                remarkRegisterClicker.onMarkDetails(getStudentRemark,position)
            }

            if(getStudentRemark[position].update){
                holder.textUpdated.isVisible = true
            }
        }

        override fun getItemCount(): Int {
           return getStudentRemark.size
        }

    }

    override fun onMessageClick(message: String) {
        Log.i(TAG,"onCreateClick")
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun onMarkDetails(remark: ArrayList<StudentRemarksModel.Remarks>, position: Int) {
        bottomMarkRemark = BottomMarkStudentRemark(remark,position,this)
        bottomMarkRemark.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onSubmitClickListener(
        textRemark: String,
        remark: StudentRemarksModel.Remarks,
        position: Int) {

        val url = "Remarks/RemarksAdd"
        val jsonObject = JSONObject()
        try {


            jsonObject.put("STUDENT_FNAME", remark.sTUDENTFNAME)
            jsonObject.put("STUDENT_ID", remark.sTUDENTID)
            jsonObject.put("ADMISSION_NUMBER", remark.aDMISSIONNUMBER)
            jsonObject.put("STUDENT_EXTRA",textRemark)

            // postParam.put("STUDENT_FNAME",STUDENT_FNAME);
            //        postParam.put("STUDENT_EXTRA", STUDENT_EXTRA);
            //        postParam.put("ADMISSION_NUMBER",  ADMISSION_NUMBER);
            //        postParam.put("STUDENT_ID",STUDENT_ID );


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        studentRemarkViewModel.getCommonPostFun(url,accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    getStudentRemark[position].update = true
                                    getStudentRemark[position].sTUDENTEXTRA = textRemark
                                    mAdapter.notifyItemChanged(position)
                                    bottomMarkRemark.dismiss();//to hide it
                                  //  bottomMarkRemarkRegister.dismiss();//to hide it
                                    Utils.getSnackBarGreen(requireActivity(), "Remarks Updated successfully", constraintLayoutContent!!)
                                    if ((position + 1) == getStudentRemark.size) {
                                        onMessageClick("Student Ends Here")
                                    } else {
                                        onMarkDetails(getStudentRemark,position+1)
                                    }
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Remarks Updation Failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(CreateObjectiveExam.TAG,"loading")
                        }
                    }
                }
            })
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
}

interface StuRemarkRegisterClicker{
    fun onMessageClick(message: String)

    fun onMarkDetails(remark: ArrayList<StudentRemarksModel.Remarks>, position: Int)

    fun onSubmitClickListener(textRemark: String,
                              remark : StudentRemarksModel.Remarks,
                              position :Int)

}


