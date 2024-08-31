package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.student_class_reallocation

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
import com.facebook.shimmer.ShimmerFrameLayout
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
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.promote_student.PromoteStudentViewModel
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener

@Suppress("DEPRECATION")
class StudentClassReallocation : Fragment(),StudentReallocationListener {

    var TAG = "StudentInfoFragment"
    private lateinit var promoteStudentViewModel: PromoteStudentViewModel
    private var _binding: FragmentStudentInfoBinding? = null
    private val binding get() = _binding!!
    lateinit var bottomSheetUpdateReallocationStudent : BottomSheetUpdateReallocationStudent

    // http://demostaff.passdaily.in/ElixirApi/Group/GroupAdd

    var gender = arrayOf("Select Gender", "Male", "Female", "other")
    var typeStr ="-1"
    var rollNo = "-1"
    var isWorking= false
    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    var getStudentsList = ArrayList<ReallocationStudentListModel.Student>()

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null

    lateinit var mAdapter : StudentInfoAdapter

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
        toolBarClickListener?.setToolbarName("Student Class Reallocation")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId =  user[0].SCHOOL_ID

        promoteStudentViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[PromoteStudentViewModel::class.java]

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
        binding.fab.visibility= View.GONE

        bottomSheetUpdateReallocationStudent = BottomSheetUpdateReallocationStudent()


        initFunction()
    }


    fun initFunction(){
        promoteStudentViewModel.getYearClassExam(adminId,schoolId)
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
        promoteStudentViewModel.getStudentReallocationList(aCCADEMICID,cLASSID,typeStr.toInt(), rollNo.toInt(), schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWorking = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getStudentsList= response.studentList as ArrayList<ReallocationStudentListModel.Student>
                            if(getStudentsList.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    mAdapter = StudentInfoAdapter(
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
                            getStudentsList = ArrayList<ReallocationStudentListModel.Student>()
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

    class StudentInfoAdapter(var studentInfoListener: StudentReallocationListener,
                             var studentsList: ArrayList<ReallocationStudentListModel.Student>,
                             context: Context) :
        RecyclerView.Adapter<StudentInfoAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textGroupName: TextView = view.findViewById(R.id.textGroupName)
            var textGroupType: TextView = view.findViewById(R.id.textGroupType)
            var textStatus : TextView = view.findViewById(R.id.textStatus)
            var textUpdated : TextView = view.findViewById(R.id.textUpdated)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.absentees_list_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textGroupName.text = studentsList[position].sTUDENTFNAME
            holder.textStatus.text = "Class : ${studentsList[position].cLASSNAME}"
            holder.textGroupType.text = "Roll No : ${studentsList[position].sTUDENTROLLNUMBER}"

            holder.itemView.setOnClickListener {
                studentInfoListener.onUpdateClick(studentsList,position)
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

    override fun onUpdateClick(
        students: ArrayList<ReallocationStudentListModel.Student>,
        position: Int
    ) {
        bottomSheetUpdateReallocationStudent = BottomSheetUpdateReallocationStudent(this,students,position)
        bottomSheetUpdateReallocationStudent.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onSubmitClick(sTUDACCADEMICID: Int, rollNumber: String,position: Int) {
        promoteStudentViewModel.getRollNumberUpdate(sTUDACCADEMICID,rollNumber)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            Log.i(TAG,"response $response")
                            when {
                                Utils.resultFun(response) == "success" -> {
//                                    Utils.getSnackBarGreen(requireActivity(), "Roll Number Updated Successfully", constraintLayoutContent!!)
//                                    initFunction()
                                    getStudentsList[position].updated = true
                                    getStudentsList[position].sTUDENTROLLNUMBER = rollNumber.toInt()
                                    mAdapter.notifyItemChanged(position)
                                    bottomSheetUpdateReallocationStudent.dismiss() //to hide it
                                    Utils.getSnackBarGreen(requireActivity(),"Roll Number Updated Successfully",constraintLayoutContent!!)
                                    if ((position + 1) == getStudentsList.size) {
                                        Utils.getSnackBar4K(requireActivity(),"Student list ends here",constraintLayoutContent!!)
                                    } else {
                                        onUpdateClick(getStudentsList,position+1)
                                    }
                                }
                               else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Roll Number Updation Failed", constraintLayoutContent!!)
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

    override fun onSuccessMessage(message : String){
        Utils.getSnackBarGreen(requireActivity(), message, constraintLayoutContent!!)
        getStudentInfo()
    }

    override fun onErrorMessage(message : String){
        Utils.getSnackBar4K(requireActivity(), message, constraintLayoutContent!!)
    }
    override fun onDeleteClick(sTUDACCADEMICID :Int) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want confirm delete this\nstudent?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                onDeleteClickListener(sTUDACCADEMICID)
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


    fun onDeleteClickListener(sTUDACCADEMICID : Int){
        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["STUD_ACCADEMIC_ID"] = sTUDACCADEMICID
        // GET_Delete_url=Global.url+"StudentSet/StudentDelete?StudAccademicId="+feedlist4.get(fpostion).get("STUD_ACCADEMIC_ID");
        promoteStudentViewModel.getCommonGetFuntion("StudentSet/StudentDelete",paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Student Deleted Successfully", constraintLayoutContent!!)
                                    bottomSheetUpdateReallocationStudent.dismiss()
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

interface StudentReallocationListener{
    fun onSuccessMessage(message : String)
    fun onErrorMessage(message : String)
    fun onUpdateClick(students: ArrayList<ReallocationStudentListModel.Student>, position: Int)
    fun onSubmitClick(sTUDACCADEMICID: Int,rollNumber :String,position: Int)
    fun onDeleteClick(sTUDACCADEMICID: Int)
}
