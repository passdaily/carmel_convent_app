package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateGroupBinding
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class BottomSheetUpdateGroup : BottomSheetDialogFragment {


    private lateinit var groupViewModel: GroupViewModel
    private var _binding: BottomSheetUpdateGroupBinding? = null
    private val binding get() = _binding!!
    lateinit var groupListener: GroupListener

    var typStr = -1
    var typGStr = -1

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null

    var editTextTitle: TextInputEditText? = null

    var group_type = arrayOf("Student Group","Public Group")
    var type = arrayOf("Unpublished", "Published")
    var groupTypeStr ="-1"
    var typeStr ="-1"

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var gROUPID = 0
    var gROUPNAME = ""
    var gROUPTYPE = 0
    var gROUPSTATUS = 0
    constructor()

    constructor(groupListener: GroupListener,gROUPID : Int, gROUPNAME: String, gROUPTYPE: Int, gROUPSTATUS: Int){
        this.groupListener = groupListener
        this.gROUPID = gROUPID
        this.gROUPNAME = gROUPNAME
        this.gROUPTYPE = gROUPTYPE
        this.gROUPSTATUS = gROUPSTATUS
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

        _binding = BottomSheetUpdateGroupBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass

        binding.editTextTitle.setText(gROUPNAME)

        var strG = ""
        typGStr = gROUPTYPE
        strG = if (typGStr == 1) {
            "Student Group~Public Group"
        } else {
            "Public Group~Student Group"
        }
        group_type = strG.split("~").toTypedArray()
        val groupTypeAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, group_type)
        groupTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAcademic?.adapter = groupTypeAdapter
        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {

                if(group_type[position] == "Student Group"){
                    groupTypeStr = "1"
                }else if(group_type[position] == "Public Group"){
                    groupTypeStr = "2"
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        var str = ""
        typStr = gROUPSTATUS
        str = if (typStr == 0) {
            "Unpublished~Published"
        } else {
            "Published~Unpublished"
        }
        type = str.split("~").toTypedArray()
        val status= ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, type)
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClass?.adapter = status
        spinnerClass?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                if (type[position] == "Unpublished") {
                    typeStr = "0"
                } else if (type[position] == "Published") {
                    typeStr = "1"
                }

//                if (position == 0) {
//                    typeStr = position.toString()
//                } else if (position == 1) {
//                    typeStr = position.toString()
//                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.buttonSubmit.setOnClickListener {
            if(binding.editTextTitle.text.toString().isNotEmpty()){
                if(groupTypeStr != "-1"){
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("SCHOOL_ID", schoolId)
                        jsonObject.put("GROUP_NAME", binding.editTextTitle.text.toString())
                        jsonObject.put("GROUP_TYPE", groupTypeStr)
                        jsonObject.put("GROUP_STATUS", typeStr)
                        jsonObject.put("GROUP_ID", gROUPID)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i(BottomSheetCreateGroup.TAG,"jsonObject $jsonObject")

                    val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    groupListener.onCreateClick("Group/GroupEditById",submitItems,"Group Updated successfully","Group Updation Failed")


                }else{
                    groupListener.onShowMessage("Select Group type")
                }
            }else{
                groupListener.onShowMessage("Group Name Can't be empty")
            }
        }

        binding.textDeleteIcon.setOnClickListener {
            groupListener.onDeleteItem(gROUPID)
        }


    }

    companion object {
        var TAG = "BottomSheetFragment"
    }
}