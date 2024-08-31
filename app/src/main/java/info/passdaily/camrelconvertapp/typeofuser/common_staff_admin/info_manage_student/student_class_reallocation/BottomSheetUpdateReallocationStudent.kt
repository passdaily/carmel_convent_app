package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.student_class_reallocation

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import info.passdaily.camrelconvertapp.R
import android.view.LayoutInflater
import android.view.ViewGroup
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.databinding.BottomSheetSendStudentBinding
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdateReallocationBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.promote_student.PromoteStudentViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.GroupViewModel
import java.util.ArrayList

@Suppress("DEPRECATION")
class BottomSheetUpdateReallocationStudent : BottomSheetDialogFragment {


    private lateinit var promoteStudentViewModel: PromoteStudentViewModel
    private var _binding: BottomSheetUpdateReallocationBinding? = null
    private val binding get() = _binding!!
    lateinit var studentReallocationListener: StudentReallocationListener

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()

    var aCCADEMICID = 0
    var cLASSID = 0

    var textCurrentRollNo : TextView? = null
    var textStudentName : TextView? = null
    var textClassName : TextView? = null
    var textAcademicYear : TextView? = null

    lateinit var getStudentList : ArrayList<ReallocationStudentListModel.Student>
    var position = 0
    var selectedValues = ArrayList<Int>()

    var editTextTitle: TextInputEditText? = null

    var group_type = arrayOf("Select Group Type", "Student Group","Public Group")
    var type = arrayOf("UnPublished", "Publish")
    var groupTypeStr ="-1"
    var typeStr ="-1"

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0

    constructor()
    constructor(studentReallocationListener: StudentReallocationListener,
                getStudentList : ArrayList<ReallocationStudentListModel.Student>,position :Int){
        this.studentReallocationListener = studentReallocationListener
        this.getStudentList = getStudentList
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


        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID

        promoteStudentViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[PromoteStudentViewModel::class.java]

        _binding = BottomSheetUpdateReallocationBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        textCurrentRollNo = binding.textCurrentRollNo
        textStudentName = binding.textStudentName
        textClassName = binding.textClassName
        textAcademicYear = binding.textAcademicYear

        textCurrentRollNo?.text = getStudentList[position].sTUDENTROLLNUMBER.toString()
        textStudentName?.text = getStudentList[position].sTUDENTFNAME
        textClassName?.text = getStudentList[position].cLASSNAME
        textAcademicYear?.text = getStudentList[position].aCCADEMICTIME


        binding.buttonSubmit.setOnClickListener {

            // "StudentSet/RollNumberUpdate?StudAccademicId="
            //+"&RollNumber="+edit.getText().toString();
            if(binding.editTextTitle.text.toString().isNotEmpty()) {
                studentReallocationListener.onSubmitClick(
                    getStudentList[position].sTUDACCADEMICID, binding.editTextTitle.text.toString(),position)
            }else{
                studentReallocationListener.onErrorMessage("Enter Admission Number")
            }
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


        binding.textDeleteIcon.setOnClickListener {
            studentReallocationListener.onDeleteClick(getStudentList[position].sTUDENTID)
        }


    }





    companion object {
        var TAG = "BottomSheetFragment"
    }
}