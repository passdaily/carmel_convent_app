package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.manage_conveyor

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateConveyorBinding
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

@Suppress("DEPRECATION")
class BottomSheetUpdateConveyor : BottomSheetDialogFragment {

    private lateinit var manageConveyorViewModel: ManageConveyorViewModel

    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var schoolId = 0

    private var _binding: BottomSheetUpdateConveyorBinding? = null
    private val binding get() = _binding!!

    lateinit var conveyorListener: ConveyorListener

    lateinit var studentsList: StudentsInfoListModel.Students

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var readPermission = false
    private var writePermission = false


    var toolbar : Toolbar? = null
    var constraintLeave : ConstraintLayout? = null

    var buttonSubmit : AppCompatButton? =null

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getStudents  = ArrayList<GetStudentsListModel.Students>()

    var aCCADEMICID = 8
    var cLASSID = 0
    var sTUDENTID = 0
    var editPtaName : TextInputEditText? = null
    var spinnerAccedamic  : AppCompatSpinner? = null
    var spinnerClass  : AppCompatSpinner? = null
    var spinnerStudent  : AppCompatSpinner? = null


    var editDriverName : TextInputEditText? = null
    var editMobileNumber : TextInputEditText? = null
    var editVehicleNo : TextInputEditText? = null
    var editDriverAddress  : TextInputEditText? = null

    var editPtaAddress : TextInputEditText? = null
    var spinnerStatus  : AppCompatSpinner? = null

    lateinit var conveyorList: ArrayList<ManageConveyorListModel.Conveyor>

    var getClass = ArrayList<GetYearClassExamModel.Class>()

    var isWorking = false
    var position = 0

    var typGenderStr = -1
    constructor()

    constructor(conveyorListener: ConveyorListener,conveyorList: ArrayList<ManageConveyorListModel.Conveyor>, position :Int) {
        this.conveyorListener = conveyorListener
        this.conveyorList = conveyorList
        this.position = position
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
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

        _binding = BottomSheetUpdateConveyorBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        constraintLeave  = binding.constraintLeave

        spinnerClass  = binding.spinnerClass
        spinnerStudent  = binding.spinnerStudent


        editDriverName  = binding.editDriverName
        editMobileNumber  = binding.editMobileNumber
        editVehicleNo = binding.editVehicleNo
        editDriverAddress = binding.editDriverAddress



        editDriverName?.setText(conveyorList[position].cONVEYORSNAME)
        editMobileNumber?.setText(conveyorList[position].cONVEYORSMOBILE)
        editVehicleNo?.setText(conveyorList[position].cONVEYORSVEHICLENO)

        editDriverAddress?.setText(conveyorList[position].cONVEYORSVEHICLENO)


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

        val submitButton = binding.buttonSubmit
        submitButton.setOnClickListener {

            if(manageConveyorViewModel.validateField(editDriverName!!,
                    "Enter Driver's Mobile Number",requireActivity(), binding.constraintLeave)
                && manageConveyorViewModel.validateField(editMobileNumber!!,
                    "Enter Vehicle Number",requireActivity(), binding.constraintLeave)
                && manageConveyorViewModel.validateField(editVehicleNo!!,
                    "Enter Driver Name",requireActivity(), binding.constraintLeave)
            ){
                var url = "ConveyorEdit/ConveyorUpdate"

                val jsonObject = JSONObject()
                try {
                    jsonObject.put("CONVEYORS_ID ", conveyorList[position].cONVEYORSID)
                    jsonObject.put("CLASS_ID", cLASSID)
                    jsonObject.put("STUDENT_ID", conveyorList[position].sTUDENTID)
                    jsonObject.put("CONVEYORS_NAME", editDriverName?.text.toString())
                    jsonObject.put("CONVEYORS_MOBILE", editMobileNumber?.text.toString())
                    jsonObject.put("CONVEYORS_VEHICLE_NO", editVehicleNo?.text.toString())
                    jsonObject.put("CONVEYORS_ADDRESS", editDriverAddress?.text.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.i(TAG, "jsonObject $jsonObject")
                val accademicRe =
                    jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                conveyorListener.onSubmitDetails("ConveyorEdit/ConveyorUpdate",accademicRe,position,
                    editDriverName?.text.toString(),editMobileNumber?.text.toString(),
                    editVehicleNo?.text.toString()
                )
            }


//            if(editDriverName?.text.toString().isNotEmpty()){
//                if(editDriverName?.text.toString().isNotEmpty()){
//                    if(editDriverName?.text.toString().isNotEmpty()){
//                        submitFile()
//                    }else{
//                        conveyorListener.onErrorMessageClicker("Enter Driver's Mobile Number")
//                    }
//                }else{
//                    conveyorListener.onErrorMessageClicker("Enter Vehicle Number")
//                }
//            }else{
//                conveyorListener.onErrorMessageClicker("Enter Driver Name")
//            }

        }

        binding.textDeleteIcon.setOnClickListener {
            conveyorListener.onDeleteClicker(conveyorList[position])
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

                            getClass = response.classList as ArrayList<GetYearClassExamModel.Class>
                            var classX = Array(getClass.size){""}
                            for (i in getClass.indices) {
                                classX[i] = getClass[i].cLASSNAME
                                if (conveyorList[position].cLASSID == getClass[i].cLASSID) {
                                    cLASSID = i
                                }
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    classX
                                )
                                spinnerClass?.adapter = adapter
                            }
                            spinnerClass?.setSelection(cLASSID)
                            Log.i(DialogCreateConveyor.TAG,"initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(DialogCreateConveyor.TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(DialogCreateConveyor.TAG,"initFunction LOADING")
                        }
                    }
                }
            })
    }

    private fun getStudentList(aCCADEMICID : Int,cLASSID: Int) {
        manageConveyorViewModel.getStudentList(Global.academicId,cLASSID)
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

                            Log.i(DialogCreateConveyor.TAG,"initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(DialogCreateConveyor.TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(DialogCreateConveyor.TAG,"initFunction LOADING")
                        }
                    }
                }
            })
    }


    private fun submitFile() {

        var url = "ConveyorEdit/ConveyorUpdate"

        val jsonObject = JSONObject()
        try {
            jsonObject.put("CONVEYORS_ID ", conveyorList[position].cONVEYORSID)
            jsonObject.put("CLASS_ID", cLASSID)
            jsonObject.put("STUDENT_ID", conveyorList[position].sTUDENTID)
            jsonObject.put("CONVEYORS_NAME", editDriverName?.text.toString())
            jsonObject.put("CONVEYORS_MOBILE", editMobileNumber?.text.toString())
            jsonObject.put("CONVEYORS_VEHICLE_NO", editVehicleNo?.text.toString())
            jsonObject.put("CONVEYORS_ADDRESS", editDriverAddress?.text.toString())
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
                                Utils.resultFun(response) == "1" -> {
                                    conveyorListener.onShowMessageClicker("Conveyor Updated Successfully")
                                    dialog?.dismiss()
                                }
                                Utils.resultFun(response) == "-1" -> {
                                    conveyorListener.onErrorMessageClicker("Conveyor Already Exist")
                                }
                                Utils.resultFun(response) == "0" -> {
                                    conveyorListener.onErrorMessageClicker("Conveyor updation Failed")
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


    fun requestPermission(): Boolean {

        val hasReadPermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermission = hasReadPermission
        writePermission = hasWritePermission || minSdk29

        val permissions = readPermission && writePermission

        val permissionToRequests = mutableListOf<String>()
        if (!writePermission) {
            permissionToRequests.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!readPermission) {
            permissionToRequests.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissionToRequests.isNotEmpty()) {
            permissionsLauncher.launch(permissionToRequests.toTypedArray())
        }

        return permissions
    }

    companion object {
        var TAG = "BottomSheetFragment"
    }
}