package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.manage_conveyor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreateConveyorBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.CreateAssignmentDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Suppress("DEPRECATION")
class DialogCreateConveyor : DialogFragment {

    private lateinit var manageConveyorViewModel: ManageConveyorViewModel

    companion object {
        var TAG = "DialogCreatePta"
    }

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()

    var getStudents  = ArrayList<GetStudentsListModel.Students>()

    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var schoolId = 0

    private var _binding: DialogCreateConveyorBinding? = null
    private val binding get() = _binding!!

    lateinit var conveyorListener: ConveyorListener



    var toolbar : Toolbar? = null
    var constraintLayoutContent : ConstraintLayout? = null

    var buttonSubmit : AppCompatButton? =null



    var type = arrayOf("Unpublished", "Published")
    var typeStatus =""

    var typePtaRole =""

    var aCCADEMICID = 0
    var cLASSID = 0
    var sTUDENTID = 0

    var isWorking = false

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null
    var spinnerStudent : AppCompatSpinner? = null

    var editDriverName : TextInputEditText? = null
    var editVehicleNo : TextInputEditText? = null
    var editMobileNumber  : TextInputEditText? = null
    var editCurrentAddress : TextInputEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleWhite)
    }

    constructor(conveyorListener: ConveyorListener) {
        this.conveyorListener = conveyorListener

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        manageConveyorViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ManageConveyorViewModel::class.java]

        _binding = DialogCreateConveyorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_black)
        toolbar?.title = "Add Conveyor"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.black))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }


        constraintLayoutContent  = binding.constraintLayoutContent

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass

        spinnerStudent = binding.spinnerStudent

        editDriverName = binding.editDriverName
        editVehicleNo = binding.editVehicleNo
        editMobileNumber  = binding.editMobileNumber
        editCurrentAddress = binding.editCurrentAddress

        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                aCCADEMICID = getYears[position].aCCADEMICID
                if(isWorking){
                    getStudentList(aCCADEMICID,cLASSID)
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
                getStudentList(aCCADEMICID,cLASSID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        spinnerStudent?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                sTUDENTID = getStudents[position].sTUDENTID
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.buttonSubmit.setOnClickListener {
            if(manageConveyorViewModel.validateField(editDriverName!!,"Enter Driver Name",requireActivity(),constraintLayoutContent!!)
                && manageConveyorViewModel.validateField(editVehicleNo!!,"Enter Vehicle Number",requireActivity(),constraintLayoutContent!!)
                && manageConveyorViewModel.validateField(editMobileNumber!!,"Enter Driver's Mobile Number",requireActivity(),constraintLayoutContent!!)) {

                submitFile()

            }
        }

        initFunction()
    }



    private fun initFunction() {
        manageConveyorViewModel.getYearClassExam(adminId, schoolId )
            .observe(this, Observer {
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

    private fun getStudentList(aCCADEMICID : Int,cLASSID: Int) {
        manageConveyorViewModel.getStudentList(aCCADEMICID,cLASSID)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            isWorking = true
                            getStudents = response.studentsList as ArrayList<GetStudentsListModel.Students>
                            var years = Array(getStudents.size){""}
                            for (i in getStudents.indices) {
                                years[i] = getStudents[i].sTUDENTFNAME
                            }
                            if (spinnerStudent != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerStudent?.adapter = adapter
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


    private fun submitFile() {

        var url = "ManageConveyor/ConveyorAdd"

        val jsonObject = JSONObject()
        try {
            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
            jsonObject.put("CLASS_ID", cLASSID)
            jsonObject.put("STUDENT_ID", sTUDENTID)
            jsonObject.put("CONVEYORS_VEHICLE_NO", editVehicleNo?.text.toString())
            jsonObject.put("CONVEYORS_NAME", editDriverName?.text.toString())
            jsonObject.put("CONVEYORS_MOBILE", editMobileNumber?.text.toString())
            jsonObject.put("CONVEYORS_ADDRESS", editCurrentAddress?.text.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG, "jsonObject $jsonObject")
        val accademicRe =
            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
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
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    conveyorListener.onShowMessageClicker("Conveyor Added Successfully")
                                    cancelFrg()
                                }
                                Utils.resultFun(response) == "EXIST" -> {
                                    conveyorListener.onErrorMessageClicker("Conveyor Already Exist")
                                }
                                Utils.resultFun(response) == "FAILED" -> {
                                    conveyorListener.onErrorMessageClicker("Conveyor Adding Failed")
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            conveyorListener.onErrorMessageClicker("Please try again after sometime")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(CreateAssignmentDialog.TAG, "loading")
                        }
                    }
                }
            })

    }


    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
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


}