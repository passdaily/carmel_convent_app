package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.remarks

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
import info.passdaily.camrelconvertapp.databinding.FragmentManageGroupBinding
import info.passdaily.camrelconvertapp.databinding.FragmentRemarkRegisterBinding
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
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class RemarkRegisterFragment : Fragment(),RemarkRegisterClicker {

    var TAG = "RemarkRegisterFragment"
    private lateinit var studentRemarkViewModel: StudentRemarkViewModel
    private var _binding: FragmentRemarkRegisterBinding? = null
    private val binding get() = _binding!!

    lateinit var bottomSheet : BottomMarkRemarkRegister

    var isWorking= false
    // http://demostaff.passdaily.in/ElixirApi/Group/GroupAdd
    var aCCADEMICID = 0
    var cLASSID = 0
    var eXAMID = 0


    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var  schoolId = 0

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getExam = ArrayList<GetYearClassExamModel.Exam>()

    lateinit var mAdapter : RemarkRegisterAdapter

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

    var getRemarkRegister = ArrayList<RemarkRegisterListModel.Remarks>()
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
        toolBarClickListener?.setToolbarName("Remark Register")
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
        _binding = FragmentRemarkRegisterBinding.inflate(inflater, container, false)
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
        spinnerExam = binding.spinnerExam


        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())


        binding.accedemicText.text = requireActivity().resources.getText(R.string.select_year)
        binding.classText.text = requireActivity().resources.getText(R.string.select_class)
        binding.textExamName.text = requireActivity().resources.getText(R.string.select_exam)

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
                if(isWorking){
                    getFinalList()
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
                view: View, position: Int, id: Long) {
                eXAMID = getClass[position].cLASSID
                getFinalList()

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        initFunction()

        binding.fab.visibility = View.GONE
        bottomSheet = BottomMarkRemarkRegister()
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

                            getExam = response.exams as ArrayList<GetYearClassExamModel.Exam>
                            var exams = Array(getExam.size){""}
                            for (i in getExam.indices) {
                                exams[i] = getExam[i].eXAMNAME
                            }
                            if (spinnerExam != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    exams
                                )
                                spinnerExam?.adapter = adapter
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
        studentRemarkViewModel.getRemarkRegister(aCCADEMICID,cLASSID,eXAMID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWorking = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!

                            getRemarkRegister = response.remarksList as ArrayList<RemarkRegisterListModel.Remarks>
                            if(getRemarkRegister.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    mAdapter =  RemarkRegisterAdapter(
                                        this,
                                        getRemarkRegister,
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
                            getRemarkRegister = ArrayList<RemarkRegisterListModel.Remarks>()
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


    class RemarkRegisterAdapter(
        var remarkRegisterClicker : RemarkRegisterClicker,
        var getRemarkRegister: ArrayList<RemarkRegisterListModel.Remarks>,
        context: Context)
        : RecyclerView.Adapter<RemarkRegisterAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textStudentName: TextView = view.findViewById(R.id.textStudentName)
            var textClassName: TextView = view.findViewById(R.id.textClassName)
            var textRollNo : TextView = view.findViewById(R.id.textRollNo)


            var editTextWork  : TextView = view.findViewById(R.id.editTextWork)
            var editTextArt  : TextView = view.findViewById(R.id.editTextArt)
            var editTextHealth  : TextView = view.findViewById(R.id.editTextHealth)
            var editTextDiscipline  : TextView = view.findViewById(R.id.editTextDiscipline)

            var textUpdated  : TextView = view.findViewById(R.id.textUpdated)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.remark_register_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textStudentName.text = getRemarkRegister[position].sTUDENTFNAME
            holder.textClassName.text = "Class : ${getRemarkRegister[position].cLASSNAME}"
            holder.textRollNo.text = "Roll.No : ${getRemarkRegister[position].sTUDENTROLLNUMBER}"

            if (getRemarkRegister[position].rEMARKCOLOUMN1 != "N") {
                holder.editTextWork.text = getRemarkRegister[position].rEMARKCOLOUMN1
            }

            if (getRemarkRegister[position].rEMARKCOLOUMN2 != "N") {
                holder.editTextArt.text = getRemarkRegister[position].rEMARKCOLOUMN2
            }

            if (getRemarkRegister[position].rEMARKCOLOUMN3 != "N") {
                holder.editTextHealth.text = getRemarkRegister[position].rEMARKCOLOUMN3
            }

            if (getRemarkRegister[position].rEMARKCOLOUMN4 != "N") {
                holder.editTextDiscipline.text = getRemarkRegister[position].rEMARKCOLOUMN4
            }

            holder.itemView.setOnClickListener {
                remarkRegisterClicker.onMarkDetails(getRemarkRegister,position)
            }

            if(getRemarkRegister[position].update){
                holder.textUpdated.isVisible = true
            }
        }

        override fun getItemCount(): Int {
           return getRemarkRegister.size
        }

    }

    override fun onMessageClick(message: String) {
        Log.i(TAG,"onCreateClick")
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun onMarkDetails(remark: ArrayList<RemarkRegisterListModel.Remarks>, position: Int) {
        bottomSheet = BottomMarkRemarkRegister(remark,position,this)
        bottomSheet.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onSubmitClickListener(
        textMarksWork: String,
        textMarksArt: String,
        textMarksHealth: String,
        textMarksDiscipline: String,
        remark: RemarkRegisterListModel.Remarks,
        position: Int) {


        //    postParam.put("ACCADEMIC_ID",s_aid);
        //        postParam.put("CLASS_ID", scid);
        //        postParam.put("EXAM_ID",  s_eid);
        //        postParam.put("STUDENT_ID",STUDENT_ID );
        //        postParam.put("STUDENT_ROLL_NUMBER",STUDENT_ID);
        //        postParam.put("ADMIN_ID", Global.Admin_id);
        //        postParam.put("REMARK_COLOUMN_1", edit1 );
        //        postParam.put("REMARK_COLOUMN_2",edit2 );
        //        postParam.put("REMARK_COLOUMN_3",  edit3);
        //        postParam.put("REMARK_COLOUMN_4",edit4 );

        val url = "Marks/StudentRemarksAdd"
        val jsonObject = JSONObject()
        try {

            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
            jsonObject.put("CLASS_ID", cLASSID)
            jsonObject.put("EXAM_ID", eXAMID)
            jsonObject.put("STUDENT_ID", remark.sTUDENTID)
            jsonObject.put("STUDENT_ROLL_NUMBER", remark.sTUDENTROLLNUMBER)
            jsonObject.put("ADMIN_ID",adminId)

            jsonObject.put("REMARK_COLOUMN_1", textMarksWork)
            jsonObject.put("REMARK_COLOUMN_2", textMarksArt)
            jsonObject.put("REMARK_COLOUMN_3", textMarksHealth)
            jsonObject.put("REMARK_COLOUMN_4", textMarksDiscipline)


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
                                    getRemarkRegister[position].update = true
                                    getRemarkRegister[position].rEMARKCOLOUMN1 = textMarksWork
                                    getRemarkRegister[position].rEMARKCOLOUMN2 = textMarksArt
                                    getRemarkRegister[position].rEMARKCOLOUMN3 = textMarksHealth
                                    getRemarkRegister[position].rEMARKCOLOUMN4 = textMarksDiscipline
                                    mAdapter.notifyItemChanged(position)
                                    bottomSheet.dismiss();//to hide it
                                    Utils.getSnackBarGreen(requireActivity(), "Remarks Updated successfully", constraintLayoutContent!!)
                                    if ((position + 1) == getRemarkRegister.size) {
                                        onMessageClick("Student Ends Here")
                                    } else {
                                        onMarkDetails(getRemarkRegister,position+1)
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

interface RemarkRegisterClicker{
    fun onMessageClick(message: String)

    fun onMarkDetails(remark: ArrayList<RemarkRegisterListModel.Remarks>, position: Int)

    fun onSubmitClickListener(textMarksWork: String,
                              textMarksArt: String,
                              textMarksHealth : String,
                              textMarksDiscipline : String,
                              remark : RemarkRegisterListModel.Remarks,
                              position :Int)

}


