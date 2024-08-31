package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.promote_selected_student

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentManageGroupBinding
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
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.student_to_group.BottomSheetSendStudent
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener

@Suppress("DEPRECATION")
class PromoteSelectedStudentFragment : Fragment(),PromoteSelectedStudentListener {

    var TAG = "ManageGuardianFragment"
    private lateinit var promoteStudentViewModel: PromoteStudentViewModel
    private var _binding: FragmentManageGroupBinding? = null
    private val binding get() = _binding!!

    lateinit var bottomSheetPromoteStudent  : BottomSheetPromoteStudent

    // http://demostaff.passdaily.in/ElixirApi/Group/GroupAdd

    var aCCADEMICID = 0
    var cLASSID = 0


    var isWorking= false
    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()

    var getStudentList = ArrayList<PromoteStudentListModel.Student>()

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var toolBarClickListener : ToolBarClickListener? = null

    var selectedValues = ArrayList<Int>()

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
        toolBarClickListener?.setToolbarName("Promote Selected Student")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        promoteStudentViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[PromoteStudentViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentManageGroupBinding.inflate(inflater, container, false)
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

        binding.accedemicText.text = requireActivity().resources.getText(R.string.select_year)
        binding.classText.text = requireActivity().resources.getText(R.string.select_class)

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())


        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                aCCADEMICID = getYears[position].aCCADEMICID
                if(isWorking){
                    getGuardianList()
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
                getGuardianList()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        initFunction()

        binding.fab.visibility = View.GONE

        binding.buttonSubmit.visibility = View.GONE
        binding.buttonSubmit.text = resources.getString(R.string.promote_student)
        binding.buttonSubmit.setOnClickListener {
            Log.i(TAG,"selectedValues $selectedValues")
            bottomSheetPromoteStudent = BottomSheetPromoteStudent(this,selectedValues,getStudentList)
            bottomSheetPromoteStudent.show(requireActivity().supportFragmentManager, "TAG")
        }
        bottomSheetPromoteStudent = BottomSheetPromoteStudent()

    }

    private fun getGuardianList() {
        promoteStudentViewModel.getStudentsPromoteList(aCCADEMICID,cLASSID, schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWorking = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getStudentList = response.studentList as ArrayList<PromoteStudentListModel.Student>
                            if (getStudentList.isNotEmpty()) {
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {

                                    recyclerViewVideo!!.adapter = PromoteSelectedAdapter(
                                        this,
                                        getStudentList,
                                        requireActivity()
                                    )
                                }
                            } else {
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text = resources.getString(R.string.no_results)
                            }
                            Log.i(TAG, "getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewVideo?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text = resources.getString(R.string.no_internet)
                            Log.i(TAG, "getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerViewVideo?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getStudentList = ArrayList<PromoteStudentListModel.Student>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)

                            textEmpty?.text = resources.getString(R.string.loading)
                            Log.i(TAG, "getSubjectList LOADING")
                        }
                    }
                }
            })
    }


    private fun initFunction() {
        promoteStudentViewModel.getYearClassExam(adminId,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
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

                            getClass = response.classList as java.util.ArrayList<GetYearClassExamModel.Class>
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


    class PromoteSelectedAdapter(
        var promoteSelectedStudentListener: PromoteSelectedStudentListener,
        var studentList: ArrayList<PromoteStudentListModel.Student>,
        var context: Context)
        : RecyclerView.Adapter<PromoteSelectedAdapter.ViewHolder>() {
        var mylist = ArrayList<Int>()

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textStudentName: TextView = view.findViewById(R.id.textStudentName)
            var textAcademicYear: TextView = view.findViewById(R.id.textAcademicYear)
            var imageViewCheck : ImageView = view.findViewById(R.id.imageViewCheck)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.promote_selected_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textStudentName.text = studentList[position].sTUDENTFNAME

            holder.textAcademicYear.text = studentList[position].aCCADEMICTIME

            if (studentList[position].selectedValue) {
                // viewHolder.checkBox.setChecked(true);
                holder.imageViewCheck.setImageResource(R.drawable.ic_checked_black)
                mylist.add(position)

            } else {
                //viewHolder.checkBox.setChecked(false);
                holder.imageViewCheck.setImageResource(R.drawable.ic_check_gray)
                mylist.remove(position)
            }

            promoteSelectedStudentListener.onShowMessage(mylist)

            holder.itemView.setOnClickListener {
                studentList[position].selectedValue = !studentList[position].selectedValue
                notifyItemChanged(position)
            }


        }

        override fun getItemCount(): Int {
           return studentList.size
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }


    }

    override fun onShowMessage(selectedValue: ArrayList<Int>) {
        this.selectedValues = selectedValue
        if(selectedValues.isNotEmpty()){
            binding.buttonSubmit.visibility = View.VISIBLE
        }else{
            binding.buttonSubmit.visibility = View.GONE
        }
    }


    override fun onErrorMessageClicker(message: String) {
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
    }


    override fun onSendPromotionList(aCCADEMICIDTO: Int, cLASSIDTO: Int) {
        //Create json array for filter
        if (selectedValues.size > 0) {
            var count = 0
            progressStart()
            for (i in selectedValues.indices) {
                count++
                submitStudentListJson(getStudentList[i],aCCADEMICIDTO,cLASSIDTO,count)
            }
        } else {
            Utils.getSnackBar4K(requireActivity(),"Select atleast one student",binding.constraintLayoutContent)
        }
    }


    fun submitStudentListJson(
        studentList: PromoteStudentListModel.Student,
        aCCADEMICIDTO: Int,
        cLASSIDTO: Int,
        position : Int
    ) {
        promoteStudentViewModel.sendStudentsPromote(studentList.sTUDENTID,
            studentList.sTUDENTROLLNUMBER,
            aCCADEMICIDTO,cLASSIDTO)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            bottomSheetPromoteStudent.dismiss()
                           // progressStop()
                            when {
                                Utils.resultFun(response) == "INSERTED" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Students Promoted Successfully", constraintLayoutContent!!)
//                                    initFunction()
                                }
                                Utils.resultFun(response) == "UPDATED" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Students Promotion Updated", constraintLayoutContent!!)
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Students Promotion Failed", constraintLayoutContent!!)
                                }
                            }
                            Log.i(TAG,"selectedValues ${selectedValues.size}")
                            Log.i(TAG,"position ${position + 1}")
                            if (position + 1 == selectedValues.size) {
                                progressStop()
                                getGuardianList()
                            }
                            Log.i(TAG, "getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                           // progressStop()
                            Log.i(TAG, "getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                          //  progressStart()
                            Log.i(TAG, "getSubjectList LOADING")
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

interface PromoteSelectedStudentListener{
    fun onShowMessage(selectedValue: ArrayList<Int>)
    fun onErrorMessageClicker(message: String)
    fun onSendPromotionList(aCCADEMICIDTO: Int, cLASSIDTO: Int)

}