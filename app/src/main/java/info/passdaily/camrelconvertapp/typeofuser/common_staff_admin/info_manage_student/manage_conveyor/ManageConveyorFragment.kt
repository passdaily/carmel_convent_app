package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.manage_conveyor

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
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentManageConveyorBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.CreateAssignmentDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.CreateObjectiveExam
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.RequestBody

@Suppress("DEPRECATION")
class ManageConveyorFragment : Fragment(), ConveyorListener {

    var TAG = "ManageStaffFragment"
    private lateinit var manageConveyorViewModel: ManageConveyorViewModel
    private var _binding: FragmentManageConveyorBinding? = null
    private val binding get() = _binding!!

    // http://demostaff.passdaily.in/ElixirApi/Group/GroupAdd

    var isWorking = false
    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var cLASSID = 0

    lateinit var bottomSheetUpdateConveyor: BottomSheetUpdateConveyor

    lateinit var mAdapter: ConveyorAdapter

    var getClassList = ArrayList<GetYearClassExamModel.Class>()

    var getConveyorList = ArrayList<ManageConveyorListModel.Conveyor>()

    var constraintLayoutContent: ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo: RecyclerView? = null

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null
    var toolBarClickListener: ToolBarClickListener? = null

    var editTextCode: TextInputEditText? = null
    var editTextName: TextInputEditText? = null
    var editTextMobile: TextInputEditText? = null



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
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Manage Conveyor")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)

        manageConveyorViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ManageConveyorViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentManageConveyorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        constraintLayoutContent = binding.constraintLayoutContent
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        shimmerViewContainer = binding.shimmerViewContainer

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())

        spinnerClass = binding.spinnerClass

        spinnerClass?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                cLASSID = getClassList[position].cLASSID
                getConveyorDetails(cLASSID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        initFunction()

        binding.fab.setOnClickListener {
            val dialog1 = DialogCreateConveyor(this)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, DialogCreateConveyor.TAG)
        }

        bottomSheetUpdateConveyor = BottomSheetUpdateConveyor()

    }

    fun initFunction(){

        manageConveyorViewModel.getYearClassExam(adminId, schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getClassList = response.classList as ArrayList<GetYearClassExamModel.Class>
                            var classX = Array(getClassList.size){""}
                            for (i in getClassList.indices) {
                                classX[i] = getClassList[i].cLASSNAME
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


    fun getConveyorDetails(cLASSID: Int) {

        manageConveyorViewModel.getConveyorDetailsList(cLASSID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWorking = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getConveyorList = response.conveyorList as ArrayList<ManageConveyorListModel.Conveyor>
                            if (getConveyorList.isNotEmpty()) {
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {

                                    mAdapter = ConveyorAdapter(
                                        this,
                                        getConveyorList,
                                        requireActivity(), TAG
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
                            getConveyorList = ArrayList<ManageConveyorListModel.Conveyor>()
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


    class ConveyorAdapter(
        var conveyorListener: ConveyorListener,
        var conveyorList: ArrayList<ManageConveyorListModel.Conveyor>,
        var context: Context, var TAG: String
    ) : RecyclerView.Adapter<ConveyorAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textStudentName: TextView = view.findViewById(R.id.textStudentName)
            var textConveyorName: TextView = view.findViewById(R.id.textConveyorName)
            var textMobileNumber: TextView = view.findViewById(R.id.textMobileNumber)
            var textStatus: TextView = view.findViewById(R.id.textStatus)
            var textUpdated : TextView = view.findViewById(R.id.textUpdated)

        //    var shapeImageView: ShapeableImageView = view.findViewById(R.id.shapeImageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.conveyor_list_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textStudentName.text = conveyorList[position].sTUDENTFNAME
            holder.textMobileNumber.text = "Mobile : ${conveyorList[position].cONVEYORSMOBILE}"
            holder.textConveyorName.text = "Driver : ${conveyorList[position].cONVEYORSNAME}"

            holder.textStatus.text = conveyorList[position].cONVEYORSVEHICLENO

            holder.itemView.setOnClickListener {
                conveyorListener.onUpdateClicker(conveyorList,position)
            }

            holder.textUpdated.isVisible = false
            if(conveyorList[position].updated){
                holder.textUpdated.isVisible = true
            }
        }

        override fun getItemCount(): Int {
            return conveyorList.size
        }



    }

    override fun onShowMessageClicker(message: String) {
        Log.i(TAG,"onCreateClick ")
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)
        initFunction()
    }

    override fun onErrorMessageClicker(message: String) {
        Log.i(TAG,"onCreateClick ")
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun onSubmitDetails(
        url: String,
        accademicRe: RequestBody?,
        position: Int,
        CONVEYORS_NAME: String,
        CONVEYORS_MOBILE: String,
        CONVEYORS_VEHICLE_NO: String
    ) {
        manageConveyorViewModel.getCommonPostFun(url, accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG, "resource ${resource.message}")
                    Log.i(TAG, "errorBody ${resource.data?.errorBody()}")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "1" -> {
                                    getConveyorList[position].updated = true
                                    getConveyorList[position].cONVEYORSNAME = CONVEYORS_NAME
                                    getConveyorList[position].cONVEYORSMOBILE = CONVEYORS_MOBILE
                                    getConveyorList[position].cONVEYORSVEHICLENO = CONVEYORS_VEHICLE_NO
                                    mAdapter.notifyItemChanged(position)
                                    bottomSheetUpdateConveyor.dismiss() //to hide it
                                    Utils.getSnackBarGreen(requireActivity(),"Conveyor Updated Successfully",constraintLayoutContent!!)
                                    if ((position + 1) == getConveyorList.size) {
                                        Utils.getSnackBar4K(requireActivity(),"Conveyor list ends here",constraintLayoutContent!!)
                                    } else {
                                        onUpdateClicker(getConveyorList,position+1)
                                    }
                                }
                                Utils.resultFun(response) == "-1" -> {
                                    onErrorMessageClicker("Conveyor Already Exist")
                                }
                                Utils.resultFun(response) == "0" -> {
                                    onErrorMessageClicker("Conveyor updation Failed")
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            onErrorMessageClicker("Please try again after sometime")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(CreateAssignmentDialog.TAG, "loading")
                        }
                    }
                }
            })
    }

    override fun onDeleteClicker(conveyorList: ManageConveyorListModel.Conveyor) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want confirm delete this\nconveyor?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                deleteFunction(conveyorList.cONVEYORSID)
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

    override fun onUpdateClicker(
        conveyorList: ArrayList<ManageConveyorListModel.Conveyor>,
        position: Int
    ) {
        bottomSheetUpdateConveyor = BottomSheetUpdateConveyor(this,conveyorList,position)
        bottomSheetUpdateConveyor.show(requireActivity().supportFragmentManager, "TAG")
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


    fun deleteFunction(gROUPID: Int) {
        manageConveyorViewModel.getConveyorDelete("ManageConveyor/ConveyorDelete",gROUPID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Conveyor Deleted Successfully", constraintLayoutContent!!)
                                    bottomSheetUpdateConveyor.dismiss()
                                    initFunction()
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Conveyor Deletion Failed", constraintLayoutContent!!)
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


interface ConveyorListener{
    fun onShowMessageClicker(message: String)
    fun onErrorMessageClicker(message: String)
    fun onSubmitDetails(
        url: String,
        accademicRe: RequestBody?,
        position: Int,
        CONVEYORS_NAME: String,
        CONVEYORS_MOBILE: String,
        CONVEYORS_VEHICLE_NO: String
    )
    fun onDeleteClicker(conveyorList: ManageConveyorListModel.Conveyor)
    fun onUpdateClicker(conveyorList: ArrayList<ManageConveyorListModel.Conveyor>, position: Int)
}
