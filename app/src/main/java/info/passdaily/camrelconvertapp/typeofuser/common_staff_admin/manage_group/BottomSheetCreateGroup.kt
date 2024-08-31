package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import info.passdaily.camrelconvertapp.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.ViewModelProviders
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.databinding.BottomSheetCreateGroupBinding
import info.passdaily.camrelconvertapp.databinding.BottomSheetForLogBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register.MarkRegisterCBSEClicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

@Suppress("DEPRECATION")
class BottomSheetCreateGroup : BottomSheetDialogFragment {

    private lateinit var groupViewModel: GroupViewModel
    private var _binding: BottomSheetCreateGroupBinding? = null
    private val binding get() = _binding!!
    lateinit var groupListener: GroupListener

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null

    var editTextTitle: TextInputEditText? = null

    var group_type = arrayOf("Select Group Type", "Student Group","Public Group")
    var type = arrayOf("Unpublished", "Published")
    var groupTypeStr ="-1"
    var typeStr ="-1"

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    constructor()
    constructor(groupListener: GroupListener){
        this.groupListener = groupListener
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

        _binding = BottomSheetCreateGroupBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass


        val groupTypeAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, group_type)
        groupTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAcademic?.adapter = groupTypeAdapter
        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                    when (position) {
                        0 -> { groupTypeStr = "-1" }
                        1 -> { groupTypeStr = position.toString() }
                        2 -> { groupTypeStr = position.toString() }
                    }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val status= ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, type)
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClass?.adapter = status
        spinnerClass?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
//                if (position == 0) {
//                    typeStr = position.toString()
//                } else if (position == 1) {
//                    typeStr = position.toString()
//                }
                if (type[position] == "Unpublished") {
                    typeStr = "0"
                } else if (type[position] == "Published") {
                    typeStr = "1"
                }

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
                        jsonObject.put("GROUP_TYPE", groupTypeStr)
                        jsonObject.put("GROUP_NAME", binding.editTextTitle.text.toString())
                        jsonObject.put("GROUP_TYPE", groupTypeStr)
                        jsonObject.put("GROUP_STATUS", typeStr)
                        jsonObject.put("CREATED_BY", adminId)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i(TAG,"jsonObject $jsonObject")

                    val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    groupListener.onCreateClick("Group/GroupAdd",submitItems,"Group created successfully","Group Creation Failed")


                }else{
                    groupListener.onShowMessage("Select Group type")
                }
            }else{
                groupListener.onShowMessage("Group Name Can't be empty")
            }
        }


    }

    companion object {
        var TAG = "BottomSheetFragment"
    }
}