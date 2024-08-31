package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_pta

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
import info.passdaily.camrelconvertapp.databinding.FragmentManagePtaBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class ManagePtaFragment : Fragment(), PTADetailsListener {

    var TAG = "ManageStaffFragment"
    private lateinit var ptaViewModel: PtaViewModel
    private var _binding: FragmentManagePtaBinding? = null
    private val binding get() = _binding!!

    // http://demostaff.passdaily.in/ElixirApi/Group/GroupAdd

    var isWorking = false
    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var schoolId =0

    lateinit var bottomSheetUpdatePta: BottomSheetUpdatePta

    lateinit var mAdapter: PtaAdapter

    var getPtaList = ArrayList<ManagePtaListModel.Pta>()

    var constraintLayoutContent: ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo: RecyclerView? = null

    var spinnerAcademic: AppCompatSpinner? = null
    var spinnerClass: AppCompatSpinner? = null
    var toolBarClickListener: ToolBarClickListener? = null

    var editTextCode: TextInputEditText? = null
    var editTextName: TextInputEditText? = null
    var editTextMobile: TextInputEditText? = null


    var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext
        }
        try {
            toolBarClickListener = context as ToolBarClickListener
        } catch (e: Exception) {
            Log.i(TAG, "Exception $e")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Manage PTA")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        ptaViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[PtaViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentManagePtaBinding.inflate(inflater, container, false)
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
                if(getPtaList.size != 0){
                    mAdapter.getFilter().filter(s)
                }
            }
        })

        initFunction()

        binding.fab.setOnClickListener {
            val dialog1 = DialogCreatePta(this)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, DialogCreatePta.TAG)
        }

        bottomSheetUpdatePta = BottomSheetUpdatePta()
    }

    fun initFunction() {

        val jsonObject = JSONObject()
        try {
            //PTA_MEMBER_NAME",
            //PTA_MEMBER_MOBILE"
            jsonObject.put("PTA_MEMBER_NAME", "")
            jsonObject.put("PTA_MEMBER_MOBILE", "")
            jsonObject.put("SCHOOL_ID", schoolId)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG, "jsonObject $jsonObject")

        val submitItems =
            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


        ptaViewModel.getPtaList(submitItems)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isWorking = true
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getPtaList = response.ptaList as ArrayList<ManagePtaListModel.Pta>
                            if (getPtaList.isNotEmpty()) {
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {

                                    mAdapter = PtaAdapter(
                                        this,
                                        getPtaList,
                                        getPtaList,
                                        requireActivity(), TAG
                                    )
                                    recyclerViewVideo!!.adapter = mAdapter
//                                        StaffAdapter(
//                                            this,
//                                            getStaffList,
//                                            getStaffList,
//                                            requireActivity(),TAG)
                                }
                            } else {
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text = resources.getString(R.string.no_results)
                            }
                            Log.i(TAG, "getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewVideo?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text = resources.getString(R.string.no_internet)
                            Log.i(TAG, "getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerViewVideo?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getPtaList = ArrayList<ManagePtaListModel.Pta>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)

                            textEmpty?.text = resources.getString(R.string.loading)
                            Log.i(TAG, "getSubjectList LOADING")
                        }
                    }
                }
            })
    }


    class PtaAdapter(
        var ptaDetailsListener: PTADetailsListener,
        var ptaListModel: ArrayList<ManagePtaListModel.Pta>,
        var ptaList: ArrayList<ManagePtaListModel.Pta>,
        var context: Context, var TAG: String
    ) : RecyclerView.Adapter<PtaAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewPTA: TextView = view.findViewById(R.id.textViewPTA)
            var textPtaRole: TextView = view.findViewById(R.id.textPtaRole)
            var textViewEmail: TextView = view.findViewById(R.id.textViewEmail)
            var textMobileNumber: TextView = view.findViewById(R.id.textMobileNumber)
            var textUpdated : TextView = view.findViewById(R.id.textUpdated)
            var shapeImageView: ShapeableImageView = view.findViewById(R.id.shapeImageView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.pta_list_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewPTA.text = ptaListModel[position].pTAMEMBERNAME
            holder.textMobileNumber.text = "Mobile : ${ptaListModel[position].pTAMEMBERMOBILE}"
            holder.textViewEmail.text = "Email : ${ptaListModel[position].pTAMEMBEREMAIL}"


            //demo.passdaily.in/Photos/PtaMemberImage/55CC063C9B7DC388479915D64E2D9D3F.jpg
            if (ptaListModel[position].pTAMEMBERROLE.toString() == "1") {
                holder.textPtaRole.text = "PTA : President"
            } else if (ptaListModel[position].pTAMEMBERROLE.toString() == "2") {
                holder.textPtaRole.text = "PTA : Vice-Precident"
            } else if (ptaListModel[position].pTAMEMBERROLE.toString() == "3") {
                holder.textPtaRole.text = "PTA : Secretary"
            } else if (ptaListModel[position].pTAMEMBERROLE.toString() == "4") {
                holder.textPtaRole.text = "PTA : Join-Secretary"
            } else if (ptaListModel[position].pTAMEMBERROLE.toString() == "5") {
                holder.textPtaRole.text = "PTA : Cashier"
            } else if (ptaListModel[position].pTAMEMBERROLE.toString() == "6") {
                holder.textPtaRole.text = "PTA : Member"
            } else if (ptaListModel[position].pTAMEMBERROLE.toString() == "7") {
                holder.textPtaRole.text = "PTA : Other"
            }

         //   holder.textPtaRole.text = "PTA : ${ptaListModel[position].pTAMEMBERROLE}"

            if (ptaListModel[position].pTAMEMBERIMAGE != "") {
                Glide.with(context).load(
                    Global.event_url + "/Photos/PtaMemberImage/" + ptaListModel[position].pTAMEMBERIMAGE
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
                ptaDetailsListener.onUpdateClicker(ptaListModel,position)
            }


            holder.textUpdated.isVisible = false
            if(ptaListModel[position].updated){
                holder.textUpdated.isVisible = true
            }

        }

        override fun getItemCount(): Int {
            return ptaListModel.size
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
                    ptaListModel = ptaList
                    if (charSequence2.isEmpty()) {
                        //  getSalesDetailsModel = getSalesDetails
                    } else {
                        val arrayList: ArrayList<ManagePtaListModel.Pta> = ArrayList()
                        for (unAttended in ptaListModel) {
                            //  Log.i(TAG, "row $unAttended")

                            if (unAttended.pTAMEMBERMOBILE.contains(charSequence2)
                                || unAttended.pTAMEMBERNAME.lowercase()
                                    .contains(charSequence2.lowercase())
                            ) {
                                arrayList.add(unAttended)
                            }
                        }
                        ptaListModel = arrayList
                    }
                    val filterResults = FilterResults()
                    filterResults.values = ptaListModel
                    Log.i(TAG, "filterResults $filterResults")
                    return filterResults
                }

                /* access modifiers changed from: protected */
                @SuppressLint("NotifyDataSetChanged")
                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {
                    ptaListModel = filterResults.values as ArrayList<ManagePtaListModel.Pta>
                    notifyDataSetChanged()
                }
            }
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

    override fun onSubmitClicker(
        url: String,
        jsonObject: RequestBody,
        position: Int,
        PTA_MEMBER_NAME: String,
        PTA_MEMBER_ROLE: String,
        PTA_MEMBER_MOBILE: String,
        PTA_MEMBER_ADDRESS: String,
        PTA_MEMBER_EMAIL: String,
        PTA_MEMBER_IMAGE: String,
        PTA_MEMBER_STATUS: String
    ) {

        ptaViewModel.getCommonPostFun(url, jsonObject)
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
                                    getPtaList[position].updated = true
                                    getPtaList[position].pTAMEMBERNAME = PTA_MEMBER_NAME
                                    getPtaList[position].pTAMEMBERROLE = PTA_MEMBER_ROLE.toInt()
                                    getPtaList[position].pTAMEMBERMOBILE = PTA_MEMBER_MOBILE
                                    getPtaList[position].pTAMEMBERADDRESS = PTA_MEMBER_ADDRESS
                                    getPtaList[position].pTAMEMBEREMAIL = PTA_MEMBER_EMAIL
                                    getPtaList[position].pTAMEMBERIMAGE = PTA_MEMBER_IMAGE
                                    getPtaList[position].pTAMEMBERSTATUS = PTA_MEMBER_STATUS.toInt()
                                  //  marksList[position].tOTALMARK = textMarks
                                    mAdapter.notifyItemChanged(position)
                                    bottomSheetUpdatePta.dismiss();//to hide it
                                    Utils.getSnackBarGreen(requireActivity(),"PTA Details Updated Successfully",constraintLayoutContent!!)
                                    if ((position + 1) == getPtaList.size) {
                                        Utils.getSnackBar4K(requireActivity(),"PTA list ends here",constraintLayoutContent!!)
                                    } else {
                                        onUpdateClicker(getPtaList,position+1)
                                    }
                                }
                                Utils.resultFun(response) == "-1" -> {
                                    onErrorMessageClicker("PTA Details Already Exist")
                                }
                                else -> {
                                    onErrorMessageClicker("PTA updation Failed, Please Contact Support")
                                }
                            }
                        }
                        Status.ERROR -> {
                               progressStop()
                            onErrorMessageClicker("Please try again after sometime")
                        }
                        Status.LOADING -> {
                               progressStart()
                            Log.i(TAG, "loading")
                        }
                    }
                }
            })
    }

    override fun onDeleteClicker(ptaList: ManagePtaListModel.Pta) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want conform delete PTA from the list?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                deleteFunction(ptaList.pTAMEMBERID)
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

    override fun onUpdateClicker(ptaList: ArrayList<ManagePtaListModel.Pta>, position:Int) {
        bottomSheetUpdatePta = BottomSheetUpdatePta(this,ptaList,position)
        bottomSheetUpdatePta.show(requireActivity().supportFragmentManager, "TAG")
    }

    fun deleteFunction(pTAMEMBERID: Int) {
        ptaViewModel.getDeletePta("Pta/DeletePta",pTAMEMBERID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Staff Deleted Successfully", constraintLayoutContent!!)
                                    bottomSheetUpdatePta.dismiss()
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


interface PTADetailsListener{
    fun onShowMessageClicker(message: String)
    fun onErrorMessageClicker(message: String)
    fun onSubmitClicker(
        url: String,
        jsonObject: RequestBody,
        position: Int,
        PTA_MEMBER_NAME: String,
        PTA_MEMBER_ROLE: String,
        PTA_MEMBER_MOBILE: String,
        PTA_MEMBER_ADDRESS: String,
        PTA_MEMBER_EMAIL: String,
        PTA_MEMBER_IMAGE: String,
        PTA_MEMBER_STATUS: String
    )
    fun onDeleteClicker(ptaList: ManagePtaListModel.Pta)
    fun onUpdateClicker(ptaList: ArrayList<ManagePtaListModel.Pta>, position: Int,)
}
