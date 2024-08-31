package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_staff

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentManageStaffBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.CreateAssignmentDialog
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class ManageStaffFragment : Fragment(),StaffDetailsListener {

    var TAG = "ManageStaffFragment"
    private lateinit var staffViewModel: StaffViewModel
    private var _binding: FragmentManageStaffBinding? = null
    private val binding get() = _binding!!

    // http://demostaff.passdaily.in/ElixirApi/Group/GroupAdd

    var isWorking= false
    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    lateinit var bottomSheetUpdateStaff : BottomSheetUpdateStaff

    lateinit var mAdapter : StaffAdapter

    var getStaffList = ArrayList<ManageStaffListModel.Staff>()

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var toolBarClickListener : ToolBarClickListener? = null

    var editTextCode : TextInputEditText? = null
    var editTextName : TextInputEditText? = null
    var editTextMobile : TextInputEditText? = null


    var mContext : Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(mContext ==null){
            mContext = context.applicationContext
        }
        try {
            toolBarClickListener = context as ToolBarClickListener
        }catch(e : Exception){
            Log.i(TAG,"Exception $e")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Manage Staff")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        staffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StaffViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentManageStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraintLayoutContent = binding.constraintLayoutContent
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        shimmerViewContainer = binding.shimmerViewContainer

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())

//        editTextCode = binding.editTextCode
//        editTextName = binding.editTextName
        editTextMobile = binding.editTextMobile


        editTextMobile?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(getStaffList.size != 0){
                    mAdapter.getFilter().filter(s)
                }
            }
        })

        initFunction()

        binding.fab.setOnClickListener {
            val dialog1 = DialogCreateStaff(this)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, DialogCreateStaff.TAG)
        }

        bottomSheetUpdateStaff = BottomSheetUpdateStaff()
    }

    fun initFunction(){

        val jsonObject = JSONObject()
        try {
            jsonObject.put("STAFF_CODE", "")
            jsonObject.put("STAFF_FNAME", "")
            jsonObject.put("STAFF_PHONE_NUMBER", "")//"SCHOOL_ID":1
            jsonObject.put("SCHOOL_ID", schoolId)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")

        val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


        staffViewModel.getStaffList(submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWorking = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getStaffList= response.staffList as ArrayList<ManageStaffListModel.Staff>
                            if(getStaffList.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {

                                    mAdapter = StaffAdapter(
                                        this,
                                        getStaffList,
                                        getStaffList,
                                        requireActivity(),TAG)
                                    recyclerViewVideo!!.adapter = mAdapter
//                                        StaffAdapter(
//                                            this,
//                                            getStaffList,
//                                            getStaffList,
//                                            requireActivity(),TAG)
                                }
                            }else{
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }
                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewVideo?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerViewVideo?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getStaffList = ArrayList<ManageStaffListModel.Staff>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    class StaffAdapter(
        var staffDetailsListener: StaffDetailsListener,
        var staffListModel: ArrayList<ManageStaffListModel.Staff>,
        var staffList: ArrayList<ManageStaffListModel.Staff>,
        var context: Context, var TAG : String)
        : RecyclerView.Adapter<StaffAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewStaff: TextView = view.findViewById(R.id.textViewStaff)
            var textViewEmail: TextView = view.findViewById(R.id.textViewEmail)
            var textMobileNumber: TextView = view.findViewById(R.id.textMobileNumber)
            var textViewCode : TextView = view.findViewById(R.id.textViewCode)
            var textUpdated : TextView = view.findViewById(R.id.textUpdated)
            var shapeImageView : ShapeableImageView = view.findViewById(R.id.shapeImageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.staff_list_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewStaff.text =  staffListModel[position].sTAFFFNAME
            holder.textMobileNumber.text =  "Mobile : ${staffListModel[position].sTAFFPHONENUMBER}"
            holder.textViewEmail.text =  "Email : ${staffListModel[position].sTAFFEMAILID}"

            holder.textViewCode.text =  "Code : ${staffListModel[position].sTAFFCODE}"

            if (staffListModel[position].sTAFFIMAGE != "") {
                Glide.with(context).load(
                    Global.event_url + "/Photos/StaffImage/" + staffListModel[position].sTAFFIMAGE
                ) //STAFF_IMAGE -> http://demo.passdaily.in/Photos/StaffImageA0D181192F902C6AE338BEDF36FC3251.jpg
                    //STAFF_IMAGE -> 1A07304FC14301B29E49B4DA301B0EA5.png
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate()
                            .placeholder(R.drawable.round_account_button_with_user_inside)
                    )
                    .thumbnail(0.5f)
                    .into(holder.shapeImageView)
            }

            holder.itemView.setOnClickListener {
                staffDetailsListener.onUpdateClicker(staffListModel,position)
            }

            Log.i(TAG,"update ${staffListModel[position].updated}")
            Log.i(TAG,"sTAFFFNAME ${staffListModel[position].sTAFFFNAME}")
            holder.textUpdated.isVisible = false
            if(staffListModel[position].updated){
                holder.textUpdated.isVisible = true
            }
        }

        override fun getItemCount(): Int {
           return staffListModel.size
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

        fun getFilter(): Filter {
            return object : Filter() {
                /* access modifiers changed from: protected */
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val charSequence2 = charSequence.toString()
                    Log.i(TAG, "charString $charSequence2")
                    staffListModel = staffList
                    if (charSequence2.isEmpty()) {
                        //  getSalesDetailsModel = getSalesDetails
                    } else {
                        val arrayList: ArrayList<ManageStaffListModel.Staff> = ArrayList()
                        for (unAttended in staffListModel) {
                            //  Log.i(TAG, "row $unAttended")

                            if (unAttended.sTAFFPHONENUMBER.contains(charSequence2)
                                || unAttended.sTAFFCODE.lowercase().contains(charSequence2.lowercase())
                                || unAttended.sTAFFFNAME.lowercase().contains(charSequence2.lowercase())
                                || unAttended.sTAFFLNAME.lowercase().contains(charSequence2.lowercase())
                                || unAttended.sTAFFMNAME.lowercase().contains(charSequence2.lowercase())
                            ) {
                                arrayList.add(unAttended)
                            }
                        }
                        staffListModel = arrayList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = staffListModel
                    Log.i(TAG, "filterResults $filterResults")
                    return filterResults
                }

                /* access modifiers changed from: protected */
                @SuppressLint("NotifyDataSetChanged")
                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {
                    staffListModel = filterResults.values as ArrayList<ManageStaffListModel.Staff>
                    notifyDataSetChanged()
                }
            }
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

    override fun onShowMessageClicker(message: String) {
        Log.i(TAG,"onCreateClick ")
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)
        initFunction()
    }


    override fun onErrorMessageClicker(message: String) {
        Log.i(TAG,"onCreateClick ")
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)

    }

    override fun onSubmitListener(
        url: String,
        jsonObject: RequestBody,
        position: Int,

        STAFF_CODE: String,
        STAFF_JOINDATE: String,
        STAFF_FNAME: String,
        STAFF_GENDER: String,
        STAFF_DOB: String,
        STAFF_NATIONALITY: String,
        STAFF_MOTHERTONGUE: String,
        STAFF_RELIGION: String,
        STAFF_CASTE: String,
        STAFF_FATHER_NAME: String,
        STAFF_MOTHER_NAME: String,
        STAFF_FATHER_OCCUPATION: String,
        STAFF_JOB_EXPERIENCE: String,
        STAFF_JOB_DESCRIPTION: String,
        STAFF_CADDRESS: String,
        STAFF_PADDRESS: String,
        STAFF_PHONE_NUMBER: String,
        STAFF_EMAIL_ID: String,
        STAFF_IMAGE: String,
        STAFF_STATUS: String
    ) {
        staffViewModel.getCommonPostFun(url, jsonObject)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG, "resource ${resource.message}")
                    Log.i(TAG, "errorBody ${resource.data?.errorBody()}")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "1" -> {
                                    getStaffList[position].updated = true
                                    getStaffList[position].sTAFFCODE =  STAFF_CODE
                                    getStaffList[position].sTAFFJOINDATE =  STAFF_JOINDATE
                                    getStaffList[position].sTAFFFNAME =  STAFF_FNAME
                                    getStaffList[position].sTAFFGENDER =  STAFF_GENDER.toInt()
                                    getStaffList[position].sTAFFDOB =  STAFF_DOB
                                    getStaffList[position].sTAFFNATIONALITY =  STAFF_NATIONALITY
                                    getStaffList[position].sTAFFMOTHERTONGUE =  STAFF_MOTHERTONGUE
                                    getStaffList[position].sTAFFRELIGION =  STAFF_RELIGION
                                    getStaffList[position].sTAFFCASTE =  STAFF_CASTE
                                    getStaffList[position].sTAFFFATHERNAME =  STAFF_FATHER_NAME
                                    getStaffList[position].sTAFFMOTHERNAME =  STAFF_MOTHER_NAME
                                    getStaffList[position].sTAFFFATHEROCCUPATION =  STAFF_FATHER_OCCUPATION
                                    getStaffList[position].sTAFFJOBEXPERIENCE =  STAFF_JOB_EXPERIENCE
                                    getStaffList[position].sTAFFJOBDESCRIPTION =  STAFF_JOB_DESCRIPTION
                                    getStaffList[position].sTAFFCADDRESS =  STAFF_CADDRESS
                                    getStaffList[position].sTAFFPADDRESS =  STAFF_PADDRESS
                                    getStaffList[position].sTAFFPHONENUMBER =  STAFF_PHONE_NUMBER
                                    getStaffList[position].sTAFFEMAILID =  STAFF_EMAIL_ID
                                    getStaffList[position].sTAFFIMAGE =  STAFF_IMAGE
                                    getStaffList[position].sTAFFSTATUS =  STAFF_STATUS.toInt()

                                    Log.i(TAG,"getStaffList ${getStaffList[position]}")
                                    mAdapter.notifyItemChanged(position)
                                    bottomSheetUpdateStaff.dismiss() //to hide it
                                    Utils.getSnackBarGreen(requireActivity(),"Staff Details Updated Successfully",constraintLayoutContent!!)
                                    if ((position + 1) == getStaffList.size) {
                                        Utils.getSnackBar4K(requireActivity(),"Staff list ends here",constraintLayoutContent!!)
                                    } else {
                                        onUpdateClicker(getStaffList,position+1)
                                    }
                                }
                                Utils.resultFun(response) == "-1" -> {
                                    Utils.getSnackBar4K(requireActivity(),"Staff Details Already Exist",constraintLayoutContent!!)
                                   // staffDetailsListener.onErrorMessageClicker("Staff Details Already Exist")
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(),"Staff updation Failed, Please Contact Support",constraintLayoutContent!!)
                                  //  staffDetailsListener.onErrorMessageClicker("Staff updation Failed, Please Contact Support")
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(requireActivity(),"Please try again after sometime",constraintLayoutContent!!)
                            //staffDetailsListener.onErrorMessageClicker("Please try again after sometime")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(CreateAssignmentDialog.TAG, "loading")
                        }
                    }
                }
            })
    }

    override fun onUpdateClicker(staffDetails: ArrayList<ManageStaffListModel.Staff>, position: Int) {
//        val dialog1 = DialogUpdateStaff(this,staffDetails)
//        val transaction = requireActivity().supportFragmentManager.beginTransaction()
//        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
//        dialog1.show(transaction, DialogUpdateStaff.TAG)
        bottomSheetUpdateStaff = BottomSheetUpdateStaff(this,staffDetails,position)
        bottomSheetUpdateStaff.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onDeleteClicker(staffDetails: ManageStaffListModel.Staff) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want conform delete staff from the list?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                deleteFunction(staffDetails.sTAFFID)
            }
            .setNegativeButton(
                "No"
            ) { dialog, _ -> //  Action for 'NO' Button
                dialog.cancel()
            }
        //Creating dialog box
        val alert = builder.create()
        //Setting the title manually
        alert.setTitle("Delete")
        alert.show()
        val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonbackground.setTextColor(Color.BLACK)
        val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonbackground1.setTextColor(Color.BLACK)

    }


    fun deleteFunction(sTAFFID: Int) {
        staffViewModel.getDeleteStaff("Staff/DeleteStaff",sTAFFID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Staff Deleted Successfully", constraintLayoutContent!!)
                                    bottomSheetUpdateStaff.dismiss()
                                    initFunction()
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Staff Deletion Failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }


}
interface StaffDetailsListener{
    fun onShowMessageClicker(message: String)
    fun onErrorMessageClicker(message: String)
    fun onSubmitListener(
        url: String,
        jsonObject: RequestBody,
        position: Int,
        STAFF_CODE: String,
        STAFF_JOINDATE: String,
        STAFF_FNAME: String,
        STAFF_GENDER: String,
        STAFF_DOB: String,
        STAFF_NATIONALITY: String,
        STAFF_MOTHERTONGUE: String,
        STAFF_RELIGION: String,
        STAFF_CASTE: String,
        STAFF_FATHER_NAME: String,
        STAFF_MOTHER_NAME: String,
        STAFF_FATHER_OCCUPATION: String,
        STAFF_JOB_EXPERIENCE: String,
        STAFF_JOB_DESCRIPTION: String,
        STAFF_CADDRESS: String,
        STAFF_PADDRESS: String,
        STAFF_PHONE_NUMBER: String,
        STAFF_EMAIL_ID: String,
        STAFF_IMAGE: String,
        STAFF_STATUS: String)
    fun onDeleteClicker(staffDetails: ManageStaffListModel.Staff)
    fun onUpdateClicker(staffDetails: ArrayList<ManageStaffListModel.Staff>, position: Int)
}
