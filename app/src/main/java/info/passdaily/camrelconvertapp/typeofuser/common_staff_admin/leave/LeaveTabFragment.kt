package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.leave

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
import java.util.ArrayList

@Suppress("DEPRECATION")
class LeaveTabFragment(var stateType : Int,var leaveListener :LeaveListener) : Fragment(),LeaveTabClicker {
    var TAG = "ApprovedTabFragment"
    private var _binding: FragmentAttendedTabBinding? = null
    private val binding get() = _binding!!

    private lateinit var leaveViewModel: LeaveViewModel

    var recyclerView : RecyclerView? = null

    lateinit var mAdapter: LeaveListAdapter

    var leaveDetailList = ArrayList<LeaveDetailsStaffModel.LeaveDetail>()

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

        leaveViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[LeaveViewModel::class.java]
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
                    leaveDetailList = Global.progressLeaveList
                }
                1 -> {
                    leaveDetailList = Global.approvedLeaveList
                }
                3 -> {
                    leaveDetailList = Global.rejectedLeaveList
                }
            }
//        }


        if(leaveDetailList.isNotEmpty()){
            recyclerView?.visibility = View.VISIBLE
            constraintEmpty.visibility = View.GONE
            mAdapter = LeaveListAdapter(
                this,
                leaveDetailList,
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


    class LeaveListAdapter(
        var leaveTabClicker : LeaveTabClicker,
        var leaveDetailList: ArrayList<LeaveDetailsStaffModel.LeaveDetail>,
        var context: Context, var TAG: String)
        : RecyclerView.Adapter<LeaveListAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewStudent : TextView = view.findViewById(R.id.textViewStudent)
            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
            var textViewDate: TextView = view.findViewById(R.id.textViewDate)
            var textViewDesc: TextView = view.findViewById(R.id.textViewDesc)
          //  var cardViewStatus : CardView = view.findViewById(R.id.cardViewStatus)
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
            holder.textViewStudent.text = leaveDetailList[position].sTUDENTFNAME
            holder.textViewTitle.text = leaveDetailList[position].lEAVESUBJECT

            if(!leaveDetailList[position].lEAVESUBMITTEDDATE.isNullOrBlank()) {
                val date: Array<String> =
                    leaveDetailList[position].lEAVESUBMITTEDDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                holder.textViewDate.text = Utils.formattedDateTime(dddd)
            }
            holder.textViewDesc.text = leaveDetailList[position].lEAVEDESCRIPTION

         //   holder.cardViewStatus.setCardBackgroundColor(leaveDetailList[position].cardColor)

            when (leaveDetailList[position].cardColor) {
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
                3 -> {
                    Glide.with(context)
                        .load(context.resources.getDrawable(R.drawable.ic_rejected))
                        .into(holder.imageViewBg)
                }
            }


            holder.textViewStatus.setTextColor(leaveDetailList[position].textColor)
            holder.textViewStatus.text = leaveDetailList[position].stateName

            holder.itemView.setOnClickListener {
                leaveTabClicker.onDetailClicker(leaveDetailList[position])
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
            return leaveDetailList.size
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onDetailClicker(leaveDetail: LeaveDetailsStaffModel.LeaveDetail) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialogview_leave_tab)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.attributes = lp

        var textViewState  = dialog.findViewById<TextView>(R.id.textViewState)
        var textViewStudent = dialog.findViewById<TextView>(R.id.textViewStudent)
        var textViewTitle  = dialog.findViewById<TextView>(R.id.textViewTitle)
        var textViewDesc = dialog.findViewById<TextView>(R.id.textViewDesc)
        var textViewRollNo = dialog.findViewById<TextView>(R.id.textViewRollNo)
        var textViewStart = dialog.findViewById<TextView>(R.id.textViewStart)
        var textViewEnd = dialog.findViewById<TextView>(R.id.textViewEnd)
        var approveButton = dialog.findViewById<ConstraintLayout>(R.id.approveButton)
        var rejectedButton = dialog.findViewById<ConstraintLayout>(R.id.rejectedButton)
        var imageViewClose  = dialog.findViewById<ImageView>(R.id.imageViewClose)
        var imageViewBg  = dialog.findViewById<ImageView>(R.id.imageViewBg)


        when (leaveDetail.cardColor) {
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
            3 -> {
                Glide.with(requireActivity())
                    .load(resources.getDrawable(R.drawable.ic_rejected))
                    .into(imageViewBg)
            }
        }



        when (stateType) {
            0 -> {
                approveButton.visibility = View.VISIBLE
                rejectedButton.visibility = View.VISIBLE
            }
        }

        textViewState.text = leaveDetail.stateName
        textViewState.setTextColor(leaveDetail.textColor)

        textViewStudent.text = leaveDetail.sTUDENTFNAME
        textViewTitle.text = leaveDetail.lEAVESUBJECT
        textViewRollNo.text = "Roll.No : ${leaveDetail.sTUDENTROLLNUMBER}"
        textViewDesc.text = leaveDetail.lEAVEDESCRIPTION


        if(!leaveDetail.lEAVEFROMDATE.isNullOrBlank()) {
            val date: Array<String> =
                leaveDetail.lEAVEFROMDATE.split("T".toRegex()).toTypedArray()
            val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
            textViewStart.text = Utils.formattedDateTimeInbox(dddd)
        }

        if(!leaveDetail.lEAVETODATE.isNullOrBlank()) {
            val date: Array<String> =
                leaveDetail.lEAVETODATE.split("T".toRegex()).toTypedArray()
            val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
            textViewEnd.text = Utils.formattedDateTimeInbox(dddd)
        }

        imageViewClose.setOnClickListener {  dialog.dismiss() }

        approveButton.setOnClickListener { approveRejectFun("Leave/LeaveAprove",leaveDetail,
            "Leave Approved","Leave Approval failed",0)
//            leaveListener.onMessageClick("Leave Approved",1)
            dialog.dismiss()
        }

        rejectedButton.setOnClickListener { approveRejectFun("Class/LeaveReject",leaveDetail,
            "Leave Rejected","Unapproval process failed",2)
            dialog.dismiss()
        }

        dialog.show()
    }

    fun approveRejectFun(url : String, leaveDetail: LeaveDetailsStaffModel.LeaveDetail, messageSuccess: String, messageFailed: String,
                         currentPage : Int) {

        //        String Approve_url=Global.url+"Leave/LeaveAprove";
        //
        //        Map <String, String> postParam = new HashMap <String, String>();
        //        postParam.put("LEAVE_ID",feedlist4.get(position).get("LEAVE_ID"));
        //        postParam.put("LEAVE_SUBMITTED_DATE", feedlist4.get(position).get("CLASS_ID"));
        //        postParam.put("LEAVE_APPROVED_BY", feedlist4.get(position).get("LEAVE_APPROVED_BY"));
        //        postParam.put("STUDENT_GUARDIAN_NAME", feedlist4.get(position).get("STUDENT_ROLL_NUMBER"));
        //        postParam.put("STUDENT_GUARDIAN_NUMBER", feedlist4.get(position).get("STUDENT_ID"));
      //  val url = "Leave/LeaveAprove"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("LEAVE_ID", leaveDetail.lEAVEID)
            jsonObject.put("LEAVE_SUBMITTED_DATE", leaveDetail.lEAVESUBMITTEDDATE)
            jsonObject.put("LEAVE_APPROVED_BY", leaveDetail.lEAVEAPPROVEDBY)
            jsonObject.put("STUDENT_GUARDIAN_NAME", leaveDetail.sTUDENTGUARDIANNAME)
            jsonObject.put("STUDENT_GUARDIAN_NUMBER", leaveDetail.sTUDENTGUARDIANNUMBER)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(StudentSubmitReportDialog.TAG,"jsonObject $jsonObject")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        leaveViewModel.getCommonPostFun(url,accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "1" -> {
                                    leaveListener.onMessageClick(messageSuccess,1,currentPage)
                                }
                                Utils.resultFun(response) == "0" -> {
                                    leaveListener.onMessageClick(messageFailed, 0, currentPage)
                                }
                            }
                        }
                        Status.ERROR -> {
                            leaveListener.onMessageClick(
                                "Please try again after sometime",
                                0,
                                currentPage
                            )
                        }
                        Status.LOADING -> {
                            Log.i(StudentSubmitReportDialog.TAG,"loading")
                        }
                    }
                }
            })
    }

}

interface LeaveTabClicker{
    fun onDetailClicker(leaveDetail: LeaveDetailsStaffModel.LeaveDetail)
}