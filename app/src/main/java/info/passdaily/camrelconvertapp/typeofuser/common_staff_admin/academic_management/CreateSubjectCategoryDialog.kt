package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.academic_management

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreateSubCategoryBinding
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
class CreateSubjectCategoryDialog : DialogFragment {

    lateinit var academicClickListener: AcademicClickListener

    companion object {
        var TAG = "CreateSubjectCategoryDialog"
    }

    private var _binding: DialogCreateSubCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var academicManagementViewModel: AcademicManagementViewModel

    var type = arrayOf("Unpublished", "Published")
    var typeStr =""
    var sUBJECTCATID = 0

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    var toolbar : Toolbar? = null
    var constraintLeave : ConstraintLayout? = null

    var editTextTitle : TextInputEditText? =null

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

        _binding = DialogCreateSubCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //        pb = new ProgressDialog(getActivity());
//        pb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pb.setIndeterminate(true);
        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_black)
        toolbar?.title = "Create Subject Category"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.black))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }
        editTextTitle  = binding.editTextTitle

        constraintLeave = binding.constraintLeave



        buttonSubmit = binding.buttonSubmit
       // buttonSubmit?.text = requireActivity().resources.getString(R.string.create_notification)
        buttonSubmit?.setOnClickListener {
            if(academicManagementViewModel.validateField(editTextTitle!!,"Title field cannot be empty",requireActivity(),constraintLeave!!)){
          // postParam.put("VIRTUAL_MAIL_TITLE",noti_title);
                //        postParam.put("VIRTUAL_MAIL_CONTENT", noti_desc);
                //        postParam.put("VIRTUAL_MAIL_STATUS", "1");
                //        postParam.put("VIRTUAL_MAIL_CREATED_BY",  Global.Admin_id);
                    var url = "SubjectCategory/AddSubjectCategory"

                val jsonObject = JSONObject()
                try {
                    jsonObject.put("SUBJECT_CAT_NAME", editTextTitle?.text.toString())
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
                                           // Utils.getSnackBarGreen(requireActivity(), "Subject Category created successfully",constraintLeave!!)
                                            cancelFrg()
                                            academicClickListener.onSuccessMessage("Subject Category created successfully")
                                        }
                                        Utils.resultFun(response) == "0" -> {
                                            Utils.getSnackBar4K(requireActivity(), "Subject Category Already Exist", constraintLeave!!)
                                        }
                                        Utils.resultFun(response) == "-1" -> {
                                            Utils.getSnackBar4K(requireActivity(), "Subject Category Creation Failed", constraintLeave!!)
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