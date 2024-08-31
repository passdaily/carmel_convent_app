package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.academic_management

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreateSubjectBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class CreateSubjectDialog : DialogFragment {

    lateinit var academicClickListener: AcademicClickListener

    companion object {
        var TAG = "CreateSubjectDialog"
    }

    private var _binding: DialogCreateSubjectBinding? = null
    private val binding get() = _binding!!

    private lateinit var academicManagementViewModel: AcademicManagementViewModel

    var type = arrayOf("Unpublished", "Published")
    var typeStr =""
    var sUBJECTCATID = 0

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId =0

    var toolbar : Toolbar? = null
    var constraintLeave : ConstraintLayout? = null

    var spinnerSubject : AppCompatSpinner? = null
    var editTextTitle : TextInputEditText? =null
    var editTextDesc : TextInputEditText? =null
    var editSubjectCode : TextInputEditText? =null
    var spinnerStatus : AppCompatSpinner? = null
    var buttonSubmit : AppCompatButton? =null

    var getSubjectCategory = ArrayList<SubCategoryListModel.Subjectcategory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleWhite)
    }

    constructor(academicClickListener: AcademicClickListener) {
        this.academicClickListener = academicClickListener
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

        _binding = DialogCreateSubjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_black)
        toolbar?.title = "Create Subject"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.black))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }
        spinnerSubject = binding.spinnerSubject
        editTextTitle  = binding.editTextTitle
        editTextDesc  = binding.editTextDesc
        editSubjectCode = binding.editSubjectCode
        spinnerStatus = binding.spinnerStatus

        constraintLeave = binding.constraintLeave

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

        val status = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, type)
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus?.adapter = status
        spinnerStatus?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    typeStr = position.toString()
                } else if (position == 1) {
                    typeStr = position.toString()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        buttonSubmit = binding.buttonSubmit
       // buttonSubmit?.text = requireActivity().resources.getString(R.string.create_notification)
        buttonSubmit?.setOnClickListener {
            if(academicManagementViewModel.validateField(editTextTitle!!,"Title field cannot be empty",requireActivity(),constraintLeave!!)
                && academicManagementViewModel.validateField(editTextDesc!!,"Description field cannot be empty",requireActivity(),constraintLeave!!)
                && academicManagementViewModel.validateField(editSubjectCode!!,"Enter Subject code",requireActivity(),constraintLeave!!)){
          // postParam.put("VIRTUAL_MAIL_TITLE",noti_title);
                //        postParam.put("VIRTUAL_MAIL_CONTENT", noti_desc);
                //        postParam.put("VIRTUAL_MAIL_STATUS", "1");
                //        postParam.put("VIRTUAL_MAIL_CREATED_BY",  Global.Admin_id);
                    var url = "Subject/SubjectAdd"

                val jsonObject = JSONObject()
                try {
                    jsonObject.put("SUBJECT_CAT_ID", sUBJECTCATID)
                    jsonObject.put("SUBJECT_NAME", editTextTitle?.text.toString())
                    jsonObject.put("SUBJECT_CODE",  editSubjectCode?.text.toString())
                    jsonObject.put("SUBJECT_DESCRIPTION",  editTextDesc?.text.toString())
                    jsonObject.put("SUBJECT_STATUS", typeStr)
                    jsonObject.put("SCHOOL_ID", schoolId)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                academicManagementViewModel.getCommonPostFun(url,accademicRe)
                    .observe(requireActivity(), Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    val response = resource.data?.body()!!
                                    progressStop()
                                    when {
                                        Utils.resultFun(response) == "1" -> {
                                           // Utils.getSnackBarGreen(requireActivity(), "Subject created successfully",constraintLeave!!)
                                            cancelFrg()
                                            academicClickListener.onSuccessMessage("Subject created successfully")
                                        }
                                        Utils.resultFun(response) == "0" -> {
                                            Utils.getSnackBar4K(requireActivity(), "Subject Already Exist", constraintLeave!!)
                                        }
                                        else -> {
                                            Utils.getSnackBar4K(requireActivity(), "Subject Creation Failed", constraintLeave!!)
                                        }
                                    }
                                }
                                Status.ERROR -> {
                                    progressStop()
                                    Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLeave!!)
                                }
                                Status.LOADING -> {
                                    progressStart()
                                    Log.i(TAG,"loading")
                                }
                            }
                        }
                    })

            }

        }
        val constraintLeave = binding.constraintLeave
        constraintLeave.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLeave.windowToken, 0)
        }

        getSubjectList()

    }


    fun getSubjectList(){
        academicManagementViewModel.getSubjectCategoryList(0,schoolId)
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


                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {

                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }
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


}