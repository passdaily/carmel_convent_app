package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.academic_management

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateSubjectBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class BottomSheetUpdateSubjet : BottomSheetDialogFragment {


    private lateinit var academicManagementViewModel: AcademicManagementViewModel
    private var _binding: BottomSheetUpdateSubjectBinding? = null
    private val binding get() = _binding!!
    lateinit var academicClickListener: AcademicClickListener

    var typStr = -1
    var typGStr = -1
    var sUBJECTCATID = 0



    var getSubjectCategory = ArrayList<SubCategoryListModel.Subjectcategory>()


    var spinnerSubject : AppCompatSpinner? = null
    var editTextTitle : TextInputEditText? =null
    var editTextDesc : TextInputEditText? =null
    var editSubjectCode : TextInputEditText? =null
    var spinnerStatus : AppCompatSpinner? = null
    var buttonSubmit : AppCompatButton? =null

    var type = arrayOf("UnPublished", "Publish")
    var typeStr ="-1"

    lateinit  var subjectList : SubjectListModel.Subject

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    constructor()

    constructor(academicClickListener: AcademicClickListener, subjectList : SubjectListModel.Subject){
        this.academicClickListener = academicClickListener
        this.subjectList = subjectList

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


        academicManagementViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[AcademicManagementViewModel::class.java]

        _binding = BottomSheetUpdateSubjectBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        spinnerSubject = binding.spinnerSubject
        editTextTitle  = binding.editTextTitle
        editTextDesc  = binding.editTextDesc
        editSubjectCode = binding.editSubjectCode
        spinnerStatus = binding.spinnerStatus

        binding.editTextTitle.setText(subjectList.sUBJECTNAME)
        binding.editTextDesc.setText(subjectList.sUBJECTDESCRIPTION)
        binding.editSubjectCode.setText(subjectList.sUBJECTCODE)

        spinnerSubject?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                sUBJECTCATID = getSubjectCategory[position].sUBJECTCATID
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        var str = ""
        typStr = subjectList.sUBJECTSTATUS
        str = if (typStr == 0) {
            "UnPublished~Publish"
        } else {
            "Publish~UnPublished"
        }
        type = str.split("~").toTypedArray()
        val status= ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, type)
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus?.adapter = status
        spinnerStatus?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                if(type[position] == "Publish"){
                    typeStr = "1"
                }else if(type[position] == "UnPublished"){
                    typeStr = "0"
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.buttonSubmit.setOnClickListener {
            if(binding.editTextTitle.text.toString().isNotEmpty()){
                if(binding.editTextDesc.text.toString().isNotEmpty()){
                    if(binding.editSubjectCode.text.toString().isNotEmpty()){
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("SUBJECT_ID", subjectList.sUBJECTID)
                        jsonObject.put("SUBJECT_CAT_ID", sUBJECTCATID)
                        jsonObject.put("SUBJECT_NAME", editTextTitle?.text.toString())
                        jsonObject.put("SUBJECT_CODE",  editSubjectCode?.text.toString())
                        jsonObject.put("SUBJECT_DESCRIPTION",  editTextDesc?.text.toString())
                        jsonObject.put("SUBJECT_STATUS", typeStr)
                        jsonObject.put("SCHOOL_ID", schoolId)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i(TAG,"jsonObject $jsonObject")

                    val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    academicClickListener.onCreateClick("SubjectEdit/SubjectUpdate",submitItems,
                        "Updated successfully","Updation Failed", "Subject Already Exist")

                    }else{
                        academicClickListener.onMessageListener("Enter Subject Code")
                    }
                }else{
                    academicClickListener.onMessageListener("Enter Subject Description")
                }
            }else{
                academicClickListener.onMessageListener("Enter Subject Name")
            }
        }

        binding.textDeleteIcon.setOnClickListener {
         //"SubjectEdit/SubjectDelete?SubjectId="
         //+feedlist.get(fpostion).get("SUBJECT_ID");

            val paramsMap: HashMap<String?, Int> = HashMap()
            paramsMap["SUBJECT_ID"] = subjectList.sUBJECTID
            academicClickListener.onDeleteFunction("SubjectEdit/SubjectDelete",paramsMap)
        }

        getSubjectList()


    }

    fun getSubjectList(){
        academicManagementViewModel.getSubjectCategoryList(0, schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            getSubjectCategory = response.subjectCategory as ArrayList<SubCategoryListModel.Subjectcategory>
                            var subject = Array(getSubjectCategory.size){""}
                            if(subject.isNotEmpty()){
                                for (i in getSubjectCategory.indices) {
                                    subject[i] = getSubjectCategory[i].sUBJECTCATNAME
                                    if (subjectList.sUBJECTCATID == getSubjectCategory[i].sUBJECTCATID) {
                                        sUBJECTCATID = i
                                    }
                                }
                            }
                            if (spinnerSubject != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    subject
                                )
                                spinnerSubject?.adapter = adapter
                            }
                            spinnerSubject?.setSelection(sUBJECTCATID)


                            Log.i(CreateSubjectDialog.TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(CreateSubjectDialog.TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {

                            Log.i(CreateSubjectDialog.TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    companion object {
        var TAG = "BottomSheetFragment"
    }
}