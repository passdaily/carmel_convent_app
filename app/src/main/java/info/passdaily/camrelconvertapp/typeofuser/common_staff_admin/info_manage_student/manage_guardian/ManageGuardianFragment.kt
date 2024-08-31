package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.manage_guardian

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
import okhttp3.RequestBody

@Suppress("DEPRECATION")
class ManageGuardianFragment : Fragment(),GuardianListener {

    var TAG = "ManageGuardianFragment"
    private lateinit var guardianViewModel: GuardianViewModel
    private var _binding: FragmentManageGroupBinding? = null
    private val binding get() = _binding!!

    lateinit var bottomSheetUpdate  : BottomSheetUpdateGuardian

    // http://demostaff.passdaily.in/ElixirApi/Group/GroupAdd

    var aCCADEMICID = 0
    var cLASSID = 0


    var isWorking= false
    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0

    var schoolId = 0
    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()

    var getGuardianList = ArrayList<GuardianListModel.Student>()

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null

    lateinit var mAdapter : GuardianAdapter

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var toolBarClickListener : ToolBarClickListener? = null


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
        toolBarClickListener?.setToolbarName("Student Guardian Info")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        guardianViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[GuardianViewModel::class.java]

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


        bottomSheetUpdate = BottomSheetUpdateGuardian()

    }

    private fun getGuardianList() {
        guardianViewModel.getGuardianList(cLASSID,aCCADEMICID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWorking = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getGuardianList = response.guardianList as ArrayList<GuardianListModel.Student>
                            if (getGuardianList.isNotEmpty()) {
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    mAdapter  = GuardianAdapter(
                                        this,
                                        getGuardianList,
                                        requireActivity()
                                    )
                                    recyclerViewVideo!!.adapter = mAdapter
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
                            getGuardianList = ArrayList<GuardianListModel.Student>()
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
        guardianViewModel.getYearClassExam(adminId, schoolId )
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


    class GuardianAdapter(
        var guardianListener: GuardianListener,
        var guardianList: ArrayList<GuardianListModel.Student>,
        var context: Context)
        : RecyclerView.Adapter<GuardianAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textStudentName: TextView = view.findViewById(R.id.textStudentName)
            var textGuardianName: TextView = view.findViewById(R.id.textGuardianName)
            var textGuardianMobile: TextView = view.findViewById(R.id.textGuardianMobile)
            var textStatus : TextView = view.findViewById(R.id.textStatus)
            var textUpdated : TextView = view.findViewById(R.id.textUpdated)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.guardian_list_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textStudentName.text = guardianList[position].sTUDENTFNAME

            holder.textGuardianName.text = "Guardian Name : ${guardianList[position].sTUDENTGUARDIANNAME}"
            holder.textGuardianMobile.text = "Guardian Number : ${guardianList[position].sTUDENTGUARDIANNUMBER}"
            holder.textStatus.text = "AD : ${guardianList[position].aDMISSIONNUMBER}"


            holder.itemView.setOnClickListener {
                guardianListener.onUpdateItem(guardianList,position)
            }

            holder.textUpdated.isVisible = false
            if(guardianList[position].updated){
                holder.textUpdated.isVisible = true
            }
        }

        override fun getItemCount(): Int {
           return guardianList.size
        }

    }

    override fun onPreviewNextListener(position: Int, message: String, indexMove: Int) {
        //  mAdapter.notifyItemChanged(position)
        bottomSheetUpdate.dismiss() //to hide it
        if(indexMove == 0){  ///preview
            if (position <= 0) {
                Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
            } else {
                onUpdateItem(getGuardianList,position-1)
            }
        }else if(indexMove == 1){  ///next
            if ((position + 1) == getGuardianList.size) {
                Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
            } else {
                onUpdateItem(getGuardianList,position+1)
            }

        }
        Log.i(TAG,"position $position")
    }

    override fun onShowMessage(message: String) {
        Log.i(TAG,"onCreateClick ")
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)
        initFunction()
    }


    override fun onUpdateItem(guardianList: ArrayList<GuardianListModel.Student>, position: Int) {

        bottomSheetUpdate = BottomSheetUpdateGuardian(this,guardianList,position)
        bottomSheetUpdate.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onErrorMessageClicker(message: String) {
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun onSubmitDetails(
        url: String,
        submitItems: RequestBody?,
        position: Int,
        ADMISSION_NUMBER: String,
        STUDENT_FNAME: String,
        STUDENT_GUARDIAN_NAME: String,
        STUDENT_GUARDIAN_NUMBER: String,
        STUDENT_DOB: String,
        STUDENT_GENDER: String
    ) {
        guardianViewModel.getCommonPostFun(url,submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
//                                    onShowMessage("Guardian Updated Successfully")
//                                    bottomSheetUpdate.dismiss()
//                                    initFunction()
                                    getGuardianList[position].updated = true
                                    getGuardianList[position].aDMISSIONNUMBER = ADMISSION_NUMBER
                                    getGuardianList[position].sTUDENTFNAME = STUDENT_FNAME
                                    getGuardianList[position].sTUDENTGUARDIANNAME = STUDENT_GUARDIAN_NAME
                                    getGuardianList[position].sTUDENTGUARDIANNUMBER = STUDENT_GUARDIAN_NUMBER
                                    getGuardianList[position].sTUDENTDOB = STUDENT_DOB
                                    getGuardianList[position].sTUDENTGENDER = STUDENT_GENDER.toInt()
                                    mAdapter.notifyItemChanged(position)
                                    bottomSheetUpdate.dismiss() //to hide it
                                    Utils.getSnackBarGreen(requireActivity(),"Student Details Updated Successfully",constraintLayoutContent!!)
                                    if ((position + 1) == getGuardianList.size) {
                                        Utils.getSnackBar4K(requireActivity(),"Student list ends here",constraintLayoutContent!!)
                                    } else {
                                        onUpdateItem(getGuardianList,position+1)
                                    }
                                }
                                else -> {
                                    onErrorMessageClicker("Guardian Updation Failed")
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

interface GuardianListener{
    fun onPreviewNextListener( position: Int,message: String,indexMove:Int)
    fun onShowMessage(message: String)
    fun onUpdateItem(guardianList: ArrayList<GuardianListModel.Student>, position: Int)
    fun onErrorMessageClicker(message: String)
    fun onSubmitDetails(
        url: String,
        submitItems: RequestBody?,
        position: Int,
        ADMISSION_NUMBER: String,
        STUDENT_FNAME: String,
        STUDENT_GUARDIAN_NAME: String,
        STUDENT_GUARDIAN_NUMBER: String,
        STUDENT_DOB: String,
        STUDENT_GENDER: String
    )

}