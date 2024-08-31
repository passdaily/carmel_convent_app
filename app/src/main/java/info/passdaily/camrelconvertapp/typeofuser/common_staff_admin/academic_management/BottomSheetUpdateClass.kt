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
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateClassBinding
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
class BottomSheetUpdateClass : BottomSheetDialogFragment {


    private lateinit var academicManagementViewModel: AcademicManagementViewModel
    private var _binding: BottomSheetUpdateClassBinding? = null
    private val binding get() = _binding!!
    lateinit var academicClickListener: AcademicClickListener

    var typStr = -1
    var sUBJECTCATID = 0


    var getSubjectCategory = ArrayList<SubCategoryListModel.Subjectcategory>()


    var spinnerSubject : AppCompatSpinner? = null
    var editTextTitle : TextInputEditText? =null
    var editTextDesc : TextInputEditText? =null
    var spinnerStatus : AppCompatSpinner? = null
    var buttonSubmit : AppCompatButton? =null

    var type = arrayOf("UnPublished", "Publish")
    var typeStr ="-1"

    lateinit  var classList: ClassListModel.Class

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    constructor()

    constructor(academicClickListener: AcademicClickListener, classList: ClassListModel.Class){
        this.academicClickListener = academicClickListener
        this.classList = classList

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

        _binding = BottomSheetUpdateClassBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        editTextTitle  = binding.editClassName
        editTextDesc = binding.editClassDescription
        spinnerStatus = binding.spinnerStatus

        editTextTitle?.setText(classList.cLASSNAME)
        editTextDesc?.setText(classList.cLASSDESCRIPTION)

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
        typStr = classList.cLASSSTATUS
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
            if(editTextTitle?.text.toString().isNotEmpty()){
                    if(editTextDesc?.text.toString().isNotEmpty()){
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("CLASS_ID", classList.cLASSID)
                        jsonObject.put("CLASS_NAME", editTextTitle?.text.toString())
                        jsonObject.put("CLASS_DESCRIPTION",  editTextDesc?.text.toString())
                        jsonObject.put("CLASS_STATUS", typeStr)
                        jsonObject.put("SCHOOL_ID", schoolId)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i(TAG,"jsonObject $jsonObject")

                    val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    academicClickListener.onCreateClick("ClassEdit/UpdateClass",submitItems,
                        "Updated successfully","Updation Failed", "Class Already Exist")

                    }else{
                        academicClickListener.onMessageListener("Enter Class Description")
                    }
            }else{
                academicClickListener.onMessageListener("Enter Class Name")
            }
        }

        binding.textDeleteIcon.setOnClickListener {
            //ClassEdit/DeleteClass?ClassId="
            // + feedlist.get(position).get("CLASS_ID");
            val paramsMap: HashMap<String?, Int> = HashMap()
            paramsMap["CLASS_ID"] = classList.cLASSID
            academicClickListener.onDeleteFunction("ClassEdit/DeleteClass",paramsMap)
        }

    }


    companion object {
        var TAG = "BottomSheetFragment"
    }
}