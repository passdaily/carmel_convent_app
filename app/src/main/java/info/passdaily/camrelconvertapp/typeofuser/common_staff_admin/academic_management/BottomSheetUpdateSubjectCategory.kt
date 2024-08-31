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
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class BottomSheetUpdateSubjectCategory : BottomSheetDialogFragment {


    private lateinit var academicManagementViewModel: AcademicManagementViewModel
    private var _binding: BottomSheetUpdateSubCategoryBinding? = null
    private val binding get() = _binding!!
    lateinit var academicClickListener: AcademicClickListener

    var typStr = -1


    var getSubjectCategory = ArrayList<SubCategoryListModel.Subjectcategory>()



    var editTextTitle : TextInputEditText? =null
    var buttonSubmit : AppCompatButton? =null


    lateinit  var subjectCategory: SubCategoryListModel.Subjectcategory

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    constructor()

    constructor(academicClickListener: AcademicClickListener, subjectCategory: SubCategoryListModel.Subjectcategory){
        this.academicClickListener = academicClickListener
        this.subjectCategory = subjectCategory

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

        _binding = BottomSheetUpdateSubCategoryBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        editTextTitle  = binding.editTextTitle

        editTextTitle?.setText(subjectCategory.sUBJECTCATNAME)


        binding.buttonSubmit.setOnClickListener {
            if(binding.editTextTitle.text.toString().isNotEmpty()){
                    val jsonObject = JSONObject()
                    try {
                        //"SUBJECT_CAT_ID", sub_id);
                        //"SUBJECT_CAT_NAME", sub_name);
                        jsonObject.put("SUBJECT_CAT_ID", subjectCategory.sUBJECTCATID)
                        jsonObject.put("SUBJECT_CAT_NAME", editTextTitle?.text.toString())
                        jsonObject.put("SCHOOL_ID", schoolId)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i(TAG,"jsonObject $jsonObject")

                    val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    academicClickListener.onCreateClick("SubjectCatEdit/UpdateSubjectCategory",submitItems,
                        "Updated successfully","Updation Failed", "Subject Category Already Exist")

            }else{
                academicClickListener.onMessageListener("Enter Subject Name")
            }
        }

        binding.textDeleteIcon.setOnClickListener {
         //   SubjectCatEdit/DeleteSubjectCategory?SubjectCatId="
         //+feedlist.get(position).get("SUBJECT_CAT_ID");
            val paramsMap: HashMap<String?, Int> = HashMap()
            paramsMap["SUBJECT_CAT_ID"] = subjectCategory.sUBJECTCATID
            academicClickListener.onDeleteFunction("SubjectCatEdit/DeleteSubjectCategory",paramsMap)
        }


    }


    companion object {
        var TAG = "BottomSheetFragment"
    }
}