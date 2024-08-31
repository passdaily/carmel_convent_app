package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.public_member

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdatePublicMemberBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.GroupViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class BottomSheetUpdatePublicMember : BottomSheetDialogFragment {


    private lateinit var groupViewModel: GroupViewModel
    private var _binding: BottomSheetUpdatePublicMemberBinding? = null
    private val binding get() = _binding!!
    lateinit var publicMemberListener: PublicMemberListener

    var aCCADEMICID = 0
    var gROUPID = 0

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getGroupList = ArrayList<GroupListModel.Group>()

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null

    var editTextTitle: TextInputEditText? = null
    var gMEMBERID = 0
    var gROUPNAME = ""
    var gMEMBERNUMBER = ""

    lateinit var publicMemberList: PublicMembersModel.PublicMember

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    constructor()

    constructor(publicMemberListener: PublicMemberListener,
                publicMemberList : PublicMembersModel.PublicMember){
//                gMEMBERID : Int, gROUPNAME: String,
//                gMEMBERNUMBER : String,
//                aCCADEMICID: Int, gROUPID: Int){
        this.publicMemberListener = publicMemberListener
        this.publicMemberList = publicMemberList

//        this.gMEMBERID = gMEMBERID
//        this.gROUPNAME = gROUPNAME
//        this.gMEMBERNUMBER = gMEMBERNUMBER
//        this.aCCADEMICID = aCCADEMICID
//        this.gROUPID = gROUPID
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

        _binding = BottomSheetUpdatePublicMemberBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass

        binding.editTextMember.setText(publicMemberList.gMEMBERNAME)
        binding.editTextMobile.setText(publicMemberList.gMEMBERNUMBER)

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
            if(binding.editTextMember.text.toString().isNotEmpty() &&
                binding.editTextMobile.text.toString().isNotEmpty()){
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("SCHOOL_ID", schoolId)
                        jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
                        jsonObject.put("GROUP_ID", gROUPID)
                        jsonObject.put("GMEMBER_NAME", binding.editTextMember.text.toString())
                        jsonObject.put("GMEMBER_NUMBER", binding.editTextMobile.text.toString())
                        jsonObject.put("GMEMBER_ID", publicMemberList.gMEMBERID)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i(TAG,"jsonObject $jsonObject")

                    val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    publicMemberListener.onCreateClick("Group/PublicMemberEditById",submitItems,
                        "Member Updated Successfully","Member Updation Failed", "Member Already Existing")
            }else{
                publicMemberListener.onShowMessage("Don't leave fields empty")
            }
        }

        binding.textDeleteIcon.setOnClickListener {
            publicMemberListener.onDeleteItem(publicMemberList.gMEMBERID,publicMemberList.gMEMBERNAME)
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

                            getYears = response.years as java.util.ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size){""}
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                                if (publicMemberList.aCCADEMICID == getYears[i].aCCADEMICID) {
                                    aCCADEMICID = i
                                }
                            }
                            if (spinnerAcademic != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademic?.adapter = adapter
                            }
                            spinnerAcademic?.setSelection(aCCADEMICID)


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
        // /////Public member
        //    //http://demostaff.passdaily.in/ElixirApi/PublicGroup/GroupListGet?AccademicId=0
        groupViewModel.getGroupListForStudentDelete("PublicGroup/GroupListGet",aCCADEMICID,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getGroupList = response.groupList as ArrayList<GroupListModel.Group>
                            var group = Array(getGroupList.size){""}
                            for (i in getGroupList.indices) {
                                group[i] = getGroupList[i].gROUPNAME!!
                                if (publicMemberList.gROUPID == getGroupList[i].gROUPID) {
                                    gROUPID = i
                                }
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    group
                                )
                                spinnerClass?.adapter = adapter
                            }
                            spinnerClass?.setSelection(gROUPID)

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