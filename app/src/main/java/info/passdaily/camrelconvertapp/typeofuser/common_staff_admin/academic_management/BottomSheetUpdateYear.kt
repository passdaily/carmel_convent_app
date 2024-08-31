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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateSubCategoryBinding
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateSubjectBinding
import info.passdaily.camrelconvertapp.databinding.BottomSheetYearBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class BottomSheetUpdateYear : BottomSheetDialogFragment {


    private lateinit var academicManagementViewModel: AcademicManagementViewModel
    private var _binding: BottomSheetYearBinding? = null
    private val binding get() = _binding!!
    lateinit var academicClickListener: AcademicClickListener

    var typStr = -1

    var type = arrayOf("UnPublished", "Publish")
    var typeStr ="-1"

    var getSubjectCategory = ArrayList<SubCategoryListModel.Subjectcategory>()



    var editTextTitle : TextInputEditText? =null
    var buttonSubmit : AppCompatButton? =null
    var spinnerStatus : AppCompatSpinner? = null

    lateinit  var academicDetail: AcademicListModel.AccademicDetail

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    constructor()

    constructor(academicClickListener: AcademicClickListener, academicDetail: AcademicListModel.AccademicDetail){
        this.academicClickListener = academicClickListener
        this.academicDetail = academicDetail

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

        _binding = BottomSheetYearBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        editTextTitle  = binding.editTextTitle
        spinnerStatus = binding.spinnerStatus
        editTextTitle?.setText(academicDetail.aCCADEMICTIME)


        var str = ""
        typStr = academicDetail.aCCADEMICSTATUS
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
                    val jsonObject = JSONObject()
                    try {
                       // postParam.put("ACCADEMIC_ID", accademic_id);
                        //        postParam.put("ACCADEMIC_TIME", text);
                        //        postParam.put("ACCADEMIC_STATUS", type_str2);
                        jsonObject.put("ACCADEMIC_ID", academicDetail.aCCADEMICID)
                        jsonObject.put("ACCADEMIC_TIME", editTextTitle?.text.toString())
                        jsonObject.put("ACCADEMIC_STATUS", typeStr)
                        jsonObject.put("SCHOOL_ID", schoolId)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i(TAG,"jsonObject $jsonObject")

                    val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    academicClickListener.onCreateClick("AcademicEdit/UpdateYear",submitItems,
                        "Updated successfully","Updation Failed", "Academic Year Already Exist")

            }else{
                academicClickListener.onMessageListener("Enter Subject Name")
            }
        }

        binding.textDeleteIcon.setOnClickListener {
         // "Accademic/DeleteYear?AccademicId=" +
            // feedlist.get(position).get("ACCADEMIC_ID") + "&Dummy=0";
            val paramsMap: HashMap<String?, Int> = HashMap()
            paramsMap["ACCADEMIC_ID"] = academicDetail.aCCADEMICID
            paramsMap["Dummy"] = 0
            academicClickListener.onDeleteFunction("Accademic/DeleteYear",paramsMap)
        }


    }


    companion object {
        var TAG = "BottomSheetFragment"
    }
}