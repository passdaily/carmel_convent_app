package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.student_to_group

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import info.passdaily.camrelconvertapp.R
import android.view.LayoutInflater
import android.view.ViewGroup
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.databinding.BottomSheetSendStudentBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.GroupViewModel
import java.util.ArrayList

@Suppress("DEPRECATION")
class BottomSheetSendStudent : BottomSheetDialogFragment {


    private lateinit var groupViewModel: GroupViewModel
    private var _binding: BottomSheetSendStudentBinding? = null
    private val binding get() = _binding!!
    lateinit var studentListener: StudentListener

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getGroupList = ArrayList<GroupListModel.Group>()

    var aCCADEMICID = 0
    var gROUPID = 0

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null

    var getStudentList = ArrayList<StudentListModel.Parent>()
    var selectedValues = ArrayList<Int>()

    var editTextTitle: TextInputEditText? = null

    var group_type = arrayOf("Select Group Type", "Student Group","Public Group")
    var type = arrayOf("UnPublished", "Publish")
    var groupTypeStr ="-1"
    var typeStr ="-1"

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var   schoolId = 0
    constructor()
    constructor(studentListener: StudentListener,selectedValues : ArrayList<Int>,
                getStudentList : ArrayList<StudentListModel.Parent>){
        this.studentListener = studentListener
        this.getStudentList = getStudentList
        this.selectedValues = selectedValues
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
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        groupViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[GroupViewModel::class.java]

        _binding = BottomSheetSendStudentBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass

        binding.accedemicText.text = requireActivity().resources.getText(R.string.select_year)
        binding.classText.text = requireActivity().resources.getText(R.string.select_group)

        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                aCCADEMICID = getYears[position].aCCADEMICID
                groupListFun(aCCADEMICID)

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
                gROUPID = getGroupList[position].gROUPID
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.buttonSubmit.setOnClickListener {

            studentListener.sendStudentsList(aCCADEMICID,gROUPID)
//            if(binding.editTextTitle.text.toString().isNotEmpty()){
//                if(groupTypeStr != "-1"){
//                    val jsonObject = JSONObject()
//                    try {
//                        jsonObject.put("GROUP_NAME", binding.editTextTitle.text.toString())
//                        jsonObject.put("GROUP_TYPE", groupTypeStr)
//                        jsonObject.put("GROUP_STATUS", typeStr)
//                        jsonObject.put("CREATED_BY", adminId)
//
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }
//                    Log.i(TAG,"jsonObject $jsonObject")
//
//                    val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
//                    groupListener.onCreateClick("Group/GroupAdd",submitItems,"Group created successfully","Group Creation Failed")
//
//
//                }else{
//                    groupListener.onShowMessage("Select Group type")
//                }
//            }else{
//                groupListener.onShowMessage("Group Name Can't be empty")
//            }
        }
        initFunction()

    }


    private fun initFunction() {
        groupViewModel.getYearClassExam(adminId,schoolId)
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


    private fun groupListFun(aCCADEMICID: Int) {
        groupViewModel.getGroupListForStudentDelete("Group/GroupListGet",aCCADEMICID,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getGroupList = response.groupList as ArrayList<GroupListModel.Group>
                            var group = Array(getGroupList.size){""}
                            for (i in getGroupList.indices) {
                                group[i] = getGroupList[i].gROUPNAME!!
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    group
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

    companion object {
        var TAG = "BottomSheetFragment"
    }
}