package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.enquiry

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentAttendedTabBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.StudentSubmitReportDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class EnquiryTabFragment(var stateType : Int,var enquiryListener :EnquiryListener) : Fragment(),EnquiryTabClicker {
    var TAG = "ApprovedTabFragment"
    private var _binding: FragmentAttendedTabBinding? = null
    private val binding get() = _binding!!

    private lateinit var enquiryViewModel: EnquiryViewModel

    var recyclerView : RecyclerView? = null

    lateinit var mAdapter: EnquiryListAdapter

    var sTUDENTGUARDIANNAME = ""
    var sTUDENTGUARDIANNUMBER = ""

    var enquiryList = ArrayList<EnquiryListStaffModel.Enquiry>()

//    var mContext : Context? = null
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if(mContext ==null){
//            mContext = context.applicationContext
//        }
//        try {
//            leaveListener = context as LeaveListener
//        }catch(e : Exception){
//            Log.i(AssignmentStaffFragment.TAG,"Exception $e")
//        }
//
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        enquiryViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[EnquiryViewModel::class.java]
        // Inflate the layout for this fragment
        _binding = FragmentAttendedTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var constraintEmpty = binding.constraintEmpty
        var imageViewEmpty = binding.imageViewEmpty

        var textEmpty = binding.textEmpty
        var shimmerViewContainer = binding.shimmerViewContainer


        recyclerView = binding.recyclerView
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())

//        for(i in leaveDetailList.indices){
            when (stateType) {
                0 -> {
                    enquiryList = Global.pendingEnquiryList
                }
                1 -> {
                    enquiryList = Global.repliedEnquiryList
                }
            }
//        }


        if(enquiryList.isNotEmpty()){
            recyclerView?.visibility = View.VISIBLE
            constraintEmpty.visibility = View.GONE
            mAdapter = EnquiryListAdapter(
                this,
                enquiryList,
                requireActivity(),
                TAG
            )
            recyclerView?.adapter = mAdapter
        }else{
            recyclerView?.visibility = View.GONE
            constraintEmpty.visibility = View.VISIBLE
            Glide.with(this)
                .load(R.drawable.ic_empty_progress_report)
                .into(imageViewEmpty)

            textEmpty.text =  resources.getString(R.string.no_results)
        }
    }


    class EnquiryListAdapter(
        var enquiryTabClicker : EnquiryTabClicker,
        var enquiryList: ArrayList<EnquiryListStaffModel.Enquiry>,
        var context: Context, var TAG: String)
        : RecyclerView.Adapter<EnquiryListAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewStudent : TextView = view.findViewById(R.id.textViewStudent)
            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
            var textViewDate: TextView = view.findViewById(R.id.textViewDate)
            var textViewDesc: TextView = view.findViewById(R.id.textViewDesc)
//            var cardViewStatus : CardView = view.findViewById(R.id.cardViewStatus)
            var imageViewBg  : ImageView = view.findViewById(R.id.imageViewBg)
            var textViewStatus : TextView = view.findViewById(R.id.textViewStatus)

        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.leave_tab_adapter, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewStudent.text = enquiryList[position].sTUDENTFNAME
            holder.textViewTitle.text = enquiryList[position].qUERYSUBJECT

            if(!enquiryList[position].qUERYSUBMITTEDDATE.isNullOrBlank()) {
                val date: Array<String> =
                    enquiryList[position].qUERYSUBMITTEDDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                holder.textViewDate.text = Utils.formattedDateTime(dddd)
            }
            holder.textViewDesc.text = enquiryList[position].qUERYDESCRIPTION

            when (enquiryList[position].cardColor) {
                0 -> {
                    Glide.with(context)
                        .load(context.resources.getDrawable(R.drawable.ic_pending))
                        .into(holder.imageViewBg)
                }
                1 -> {
                    Glide.with(context)
                        .load(context.resources.getDrawable(R.drawable.ic_approved))
                        .into(holder.imageViewBg)
                }
            }


          //  holder.cardViewStatus.setCardBackgroundColor(enquiryList[position].cardColor)
            holder.textViewStatus.setTextColor(enquiryList[position].textColor)
            holder.textViewStatus.text = enquiryList[position].stateName

            holder.itemView.setOnClickListener {
                enquiryTabClicker.onDetailClicker(enquiryList[position])
            }

//            if(leaveDetailList[position].lEAVESTATUS == 0 && stateType == 0){
//                holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.orange_light500))
//                holder.textViewStatus.setTextColor(context.resources.getColor(R.color.orange_500))
//                holder.textViewStatus.text = context.resources.getString(R.string.progress)
//            }
//
//            if(leaveDetailList[position].lEAVESTATUS == 1 && stateType == 1){
//                holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.green_light100))
//                holder.textViewStatus.setTextColor(context.resources.getColor(R.color.green_400))
//                holder.textViewStatus.text = context.resources.getString(R.string.approved)
//            }
//
//            if(leaveDetailList[position].lEAVESTATUS == 3 && stateType == 3){
//                holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_physics))
//                holder.textViewStatus.setTextColor(context.resources.getColor(R.color.color_physics))
//                holder.textViewStatus.text = context.resources.getString(R.string.rejected)
//            }

//            when (leaveDetailList[position].lEAVESTATUS) {
//                 0 -> {
//                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.orange_light500))
//                    holder.textViewStatus.setTextColor(context.resources.getColor(R.color.orange_500))
//                    holder.textViewStatus.text = context.resources.getString(R.string.progress)
//                }
//                1 -> {
//                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.green_light100))
//                    holder.textViewStatus.setTextColor(context.resources.getColor(R.color.green_400))
//                    holder.textViewStatus.text = context.resources.getString(R.string.approved)
//                }
//                3 -> {
//                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_physics))
//                    holder.textViewStatus.setTextColor(context.resources.getColor(R.color.color_physics))
//                    holder.textViewStatus.text = context.resources.getString(R.string.rejected)
//                }
//            }


        }

        override fun getItemCount(): Int {
            return enquiryList.size
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onDetailClicker(enquiryList: EnquiryListStaffModel.Enquiry) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialogview_enquiry_tab)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.attributes = lp

        queryIdFunction(enquiryList.qUERYID)

        var textViewState  = dialog.findViewById<TextView>(R.id.textViewState)
        var textViewStudent = dialog.findViewById<TextView>(R.id.textViewStudent)
        var textViewTitle  = dialog.findViewById<TextView>(R.id.textViewTitle)
        var textViewDesc = dialog.findViewById<TextView>(R.id.textViewDesc)
        var textViewRollNo = dialog.findViewById<TextView>(R.id.textViewRollNo)

        var textViewReply = dialog.findViewById<TextView>(R.id.textViewReply)
        var textViewDate = dialog.findViewById<TextView>(R.id.textViewDate)

        var textInputLayout  = dialog.findViewById<TextInputLayout>(R.id.textInputLayout)
        var editTextDesc = dialog.findViewById<TextInputEditText>(R.id.editTextDesc)
        var sendButton = dialog.findViewById<AppCompatButton>(R.id.sendButton)

        var imageViewClose  = dialog.findViewById<ImageView>(R.id.imageViewClose)

        var imageViewBg  = dialog.findViewById<ImageView>(R.id.imageViewBg)

        when (enquiryList.cardColor) {
            0 -> {
                Glide.with(requireActivity())
                    .load(resources.getDrawable(R.drawable.ic_pending))
                    .into(imageViewBg)
            }
            1 -> {
                Glide.with(requireActivity())
                    .load(resources.getDrawable(R.drawable.ic_approved))
                    .into(imageViewBg)
            }
        }


        when (stateType) {
            0 -> {
                textInputLayout.visibility = View.VISIBLE
                sendButton.visibility = View.VISIBLE
                textViewDate.visibility = View.GONE
                textViewReply.visibility = View.GONE
            }
            1 -> {
                textViewDate.visibility = View.VISIBLE
                textViewReply.visibility = View.VISIBLE
                textInputLayout.visibility = View.GONE
                sendButton.visibility = View.GONE
            }
        }

        textViewState.text = enquiryList.stateName
        textViewState.setTextColor(enquiryList.textColor)

        textViewStudent.text = enquiryList.sTUDENTFNAME
        textViewTitle.text = enquiryList.qUERYSUBJECT
        textViewRollNo.text = "Roll.No : ${enquiryList.sTUDENTROLLNUMBER}"
        textViewDesc.text = enquiryList.qUERYDESCRIPTION

        textViewReply.text = enquiryList.qUERYREPLY

        if(!enquiryList.qUERYREPLYEDDATE.isNullOrBlank()) {
            val date: Array<String> =
                enquiryList.qUERYREPLYEDDATE.split("T".toRegex()).toTypedArray()
            val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
            textViewDate.text = Utils.formattedDateTime(dddd)
        }

        sendButton.setOnClickListener {
            replyFun("Attendance/EnquiryReply",enquiryList,
                "Send successfully","Send failed",editTextDesc.text.toString())
            dialog.dismiss()
        }

        imageViewClose.setOnClickListener {  dialog.dismiss() }


        dialog.show()
    }

    private fun queryIdFunction(qUERYID: Int) {
        enquiryViewModel.getEnquiryGetById(qUERYID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            sTUDENTGUARDIANNAME =response.enquiry.sTUDENTGUARDIANNAME
                            sTUDENTGUARDIANNUMBER =response.enquiry.sTUDENTGUARDIANNUMBER
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })
    }

    fun replyFun(url : String,enquiryList: EnquiryListStaffModel.Enquiry
                 ,messageSuccess: String, messageFailed: String,editTextDesc : String) {
        //  String reply_url=Global.url+"Attendance/EnquiryReply";
        //
        //        Map <String, String> postParam = new HashMap <String, String>();
        //        postParam.put("QUERY_ID",QUERY_ID);
        //        postParam.put("QUERY_REPLYED_BY",  Global.Admin_id);
        //        postParam.put("QUERY_REPLY", reply);
        //        postParam.put("STUDENT_GUARDIAN_NAME",STUDENT_GUARDIAN_NAME);
        //        postParam.put("STUDENT_GUARDIAN_NUMBER", STUDENT_GUARDIAN_NUMBER);
      //  val url = "Leave/LeaveAprove"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("QUERY_ID", enquiryList.qUERYID)
            jsonObject.put("QUERY_REPLYED_BY", enquiryList.qUERYSUBMITTEDDATE)
            jsonObject.put("QUERY_REPLY", editTextDesc)
            jsonObject.put("STUDENT_GUARDIAN_NAME", sTUDENTGUARDIANNAME)
            jsonObject.put("STUDENT_GUARDIAN_NUMBER", sTUDENTGUARDIANNUMBER)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(StudentSubmitReportDialog.TAG,"jsonObject $jsonObject")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        enquiryViewModel.getCommonPostFun(url,accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "1" -> {
                                    enquiryListener.onMessageClick(messageSuccess,1,1)
                                }
                                Utils.resultFun(response) == "0" -> {
                                    enquiryListener.onMessageClick(messageFailed, 0,0)
                                }
                            }
                        }
                        Status.ERROR -> {
                            enquiryListener.onMessageClick(
                                "Please try again after sometime",
                                0
                            ,0)
                        }
                        Status.LOADING -> {
                            Log.i(StudentSubmitReportDialog.TAG,"loading")
                        }
                    }
                }
            })
    }

}

interface EnquiryTabClicker{
    fun onDetailClicker(enquiryList: EnquiryListStaffModel.Enquiry)
}