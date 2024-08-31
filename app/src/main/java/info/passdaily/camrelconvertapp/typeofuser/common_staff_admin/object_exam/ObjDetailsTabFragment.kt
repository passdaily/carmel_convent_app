package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentObjDetailsTabBinding
import info.passdaily.camrelconvertapp.lib.result_progress.ColorfulRingProgressView
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.typeofuser.parent.objective_exam.OExamPreViewActivity

@Suppress("DEPRECATION")
class ObjDetailsTabFragment(var onlineExamDetails: ArrayList<ObjExamDetailsStaffModel.OnlineExamDetail>,
                            var onlineExamAttendees: ArrayList<ObjExamDetailsStaffModel.OnlineExamAttendee>) : Fragment(), ExamAttendedListener{


    var TAG = "ObjDetailsTabFragment"
    private var _binding: FragmentObjDetailsTabBinding? = null
    private val binding get() = _binding!!
    private lateinit var objectiveExamStaffViewModel: ObjectiveExamStaffViewModel
    var oEXAMID = 0
    var STATUS = 0

    var textTitle : TextView? = null
    var textViewClass : TextView? = null
    var textSubject  : TextView? = null
    var textDesc : TextView? = null
    var textDuration  : TextView? = null
    var textViewPause : TextView? = null
    var textStartDate : TextView? = null
    var textEndDate : TextView? = null
    var recyclerView  : RecyclerView? = null

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmptyTop: ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var publishConstraint : ConstraintLayout? = null
    var textViewPublish: TextView? = null

    var imageViewBg : ImageView? = null
    var textViewStatus: TextView? = null

    lateinit var mAdapter : ExamAttendedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        objectiveExamStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ObjectiveExamStaffViewModel::class.java]


        // Inflate the layout for this fragment
        _binding = FragmentObjDetailsTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textTitle = binding.textTitle
        textViewClass = binding.textViewClass
        textDesc = binding.textDesc
        textSubject = binding.textSubject
        textDuration = binding.textDuration
        textViewPause = binding.textViewPause
        textStartDate = binding.textStartDate
        textEndDate = binding.textEndDate

        constraintEmptyTop = binding.constraintEmptyTop
        constraintLayoutContent = binding.constraintLayoutContent

        for(i in onlineExamDetails.indices){
            constraintEmptyTop?.visibility = View.GONE
            constraintLayoutContent?.visibility = View.VISIBLE
            textTitle?.setBackgroundColor(resources.getColor(R.color.white))
            textViewClass?.setBackgroundColor(resources.getColor(R.color.white))
            textSubject?.setBackgroundColor(resources.getColor(R.color.white))
            textDesc?.setBackgroundColor(resources.getColor(R.color.white))
            textDuration?.setBackgroundColor(resources.getColor(R.color.white))
            textViewPause?.setBackgroundColor(resources.getColor(R.color.white))
            textStartDate?.setBackgroundColor(resources.getColor(R.color.white))
            textEndDate?.setBackgroundColor(resources.getColor(R.color.white))


            textTitle?.text = onlineExamDetails[i].oEXAMNAME
            textViewClass?.text =onlineExamDetails[i].cLASSNAME
            textSubject?.text ="Subject : ${onlineExamDetails[i].sUBJECTNAME}"
            textDesc?.text = onlineExamDetails[i].oEXAMDESCRIPTION
            textDuration?.text ="${onlineExamDetails[i].oEXAMDURATION} Minutes"
            textViewPause?.text ="${onlineExamDetails[i].aLLOWEDPAUSE} Allowed Pause"

            if(!onlineExamDetails[i].sTARTTIME.isNullOrBlank()) {
                val date: Array<String> = onlineExamDetails[i].sTARTTIME.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                textStartDate?.text = "Start : ${Utils.formattedDateWords(dddd)}"
            }

            if(!onlineExamDetails[i].eNDTIME.isNullOrBlank()) {
                val date: Array<String> = onlineExamDetails[i].eNDTIME.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                textEndDate?.text = "End : ${Utils.formattedDateWords(dddd)}"
            }
            oEXAMID = onlineExamDetails[i].oEXAMID
            STATUS = onlineExamDetails[i].sTATUS
        }

        textViewPublish = binding.textViewPublish
        publishConstraint = binding.publishConstraint

        imageViewBg  = binding.imageViewBg
        textViewStatus  = binding.textViewStatus
//        if(STATUS == 1){
//            publishConstraint?.isEnabled = false
//            textViewPublish?.text = "Published"
//        }else{
//            publishConstraint?.isEnabled = true
//            textViewPublish?.text = "Publish"
//        }
        when (STATUS) {
            0 -> {
                publishConstraint?.isEnabled = true
                publishConstraint?.visibility = View.VISIBLE
                textViewPublish?.text = "Publish"
                Glide.with(requireActivity())
                    .load(resources.getDrawable(R.drawable.ic_rejected))
                    .into(imageViewBg!!)

                textViewStatus?.text = "Unpublished"
                textViewStatus?.setTextColor(resources.getColor(R.color.fresh_red_200))

            }
            1 -> {
                publishConstraint?.visibility = View.GONE
                Glide.with(requireActivity())
                    .load(resources.getDrawable(R.drawable.ic_approved))
                    .into(imageViewBg!!)

                textViewStatus?.text = "Published"
                textViewStatus?.setTextColor(resources.getColor(R.color.fresh_green_200))
            }
        }
        recyclerView = binding.recyclerView
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        if(onlineExamAttendees.isNotEmpty()){
            recyclerView?.visibility = View.VISIBLE
            constraintEmpty?.visibility = View.GONE
            mAdapter = ExamAttendedAdapter(this,onlineExamAttendees,requireActivity(),TAG)
            recyclerView?.adapter = mAdapter
        }else{
            recyclerView?.visibility = View.GONE
            constraintEmpty?.visibility = View.VISIBLE
            Glide.with(this)
                .load(R.drawable.ic_empty_progress_report)
                .into(imageViewEmpty!!)

            textEmpty?.text =  resources.getString(R.string.no_results)
        }


        publishConstraint?.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure to change exam status ?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    publishStatus("OnlineExam/OnlineExamPublish",oEXAMID)
                }
                .setNegativeButton(
                    "No"
                ) { dialog, _ -> //  Action for 'NO' Button
                    dialog.cancel()
                }
            //Creating dialog box
            val alert = builder.create()
            //Setting the title manually
            alert.setTitle("Publish")
            alert.show()
            val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            buttonbackground.setTextColor(Color.BLACK)
            val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            buttonbackground1.setTextColor(Color.BLACK)

        }

    }

    private fun publishStatus(url : String,oEXAMID: Int) {
        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["OexamId"] = oEXAMID
        objectiveExamStaffViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Exam Successfully Published", constraintLayoutContent!!)
                                    textViewPublish?.text = "Published"
                                    publishConstraint?.isEnabled = false
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Exam Publishing Failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }

    class ExamAttendedAdapter(var examAttendedListener: ExamAttendedListener,
                              var onlineExamAttendees: ArrayList<ObjExamDetailsStaffModel.OnlineExamAttendee>,
                              var context: Context,
                              var TAG : String
    ) : RecyclerView.Adapter<ExamAttendedAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
            var textViewMark: TextView = view.findViewById(R.id.textViewMark)
            var textStartDate: TextView = view.findViewById(R.id.textStartDate)
            var textEndDate: TextView = view.findViewById(R.id.textEndDate)
            var textTotalQuestion: TextView = view.findViewById(R.id.textTotalQuestion)
            var textUnanswered: TextView = view.findViewById(R.id.textUnanswered)
            var textTimeTake : TextView = view.findViewById(R.id.textTimeTake)
            var textTotalPause : TextView = view.findViewById(R.id.textTotalPause)
            var colorProgress : ColorfulRingProgressView = view.findViewById(R.id.colorProgress)
            var buttonAllowAgain : AppCompatButton = view.findViewById(R.id.buttonAllowAgain)
            var buttonPreview: AppCompatButton = view.findViewById(R.id.buttonGiveMarks)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.exam_attended_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewTitle.text = onlineExamAttendees[position].sTUDENTNAME
            holder.textViewMark.text = "${onlineExamAttendees[position].cORRECTANSWERCOUNT} / " +
                    "${onlineExamAttendees[position].tOTALQUESTIONS}"

            val parseFloat: Float = onlineExamAttendees[position].cORRECTANSWERCOUNT
                .toFloat() / onlineExamAttendees[position].tOTALQUESTIONS
                .toFloat() * 100.0f
            holder.colorProgress.percent = parseFloat

            holder.textTotalQuestion.text = "Total Questions : ${onlineExamAttendees[position].tOTALQUESTIONS}"
            if(!onlineExamAttendees[position].sTARTTIME.isNullOrBlank()) {
                val date: Array<String> =
                    onlineExamAttendees[position].sTARTTIME.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                holder.textStartDate.text = "Start : ${Utils.formattedDate(dddd)}"
            }else{
                holder.textStartDate.text = "Start : No Date here"
            }

            if(!onlineExamAttendees[position].eNDTIME.isNullOrBlank()) {
                val date: Array<String> = onlineExamAttendees[position].eNDTIME.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                holder.textEndDate.text = "End : ${Utils.formattedDate(dddd)}"
            }else{
                holder.textEndDate.text = "End : No Date here"
            }
            holder.textTotalPause.text = "Total Pause : ${onlineExamAttendees[position].pAUSEDCOUNT}"
            holder.textTimeTake.text = "Time Taken : ${Utils.formatSeconds(onlineExamAttendees[position].eLAPSEDTIME)}"
            val unAnswered = onlineExamAttendees[position].tOTALQUESTIONS - onlineExamAttendees[position].aNSWEREDQUESTIONS
            Log.i(TAG,"unAnswered $unAnswered")
            holder.textUnanswered.text = "Unanswered : $unAnswered"

            if (onlineExamAttendees[position].iSAUTOENDED == 0 && onlineExamAttendees[position].iSSUBMITTED == 0) {
                holder.buttonAllowAgain.isEnabled = false
                holder.buttonAllowAgain.setBackgroundResource(R.drawable.round_light_green600)
                holder.buttonAllowAgain.setTextAppearance(
                    context,
                    R.style.RoundedCornerButtonLightGreen600
                )
            }else{
                holder.buttonAllowAgain.isEnabled = true
                holder.buttonAllowAgain.setBackgroundResource(R.drawable.round_green400)
                holder.buttonAllowAgain.setTextAppearance(
                    context,
                    R.style.RoundedCornerButtonGreen400
                )
            }

            holder.buttonAllowAgain.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Are sure want to allow again?").setCancelable(false)
                    .setPositiveButton(
                        "Yes"
                    ) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        examAttendedListener.onClickListener(
                            onlineExamAttendees[position].oEXAMATTEMPTID,
                            holder
                        )

                    }.setNegativeButton(
                        "No"
                    ) { dialogInterface, _ -> dialogInterface.cancel() }
                val create = builder.create()
                create.setTitle("Allow")
                create.show()
                val buttonbackground: Button = create.getButton(DialogInterface.BUTTON_NEGATIVE)
                buttonbackground.setTextColor(Color.BLACK)
                val buttonbackground1: Button = create.getButton(DialogInterface.BUTTON_POSITIVE)
                buttonbackground1.setTextColor(Color.BLACK)
            }
            holder.buttonPreview.setOnClickListener {

                examAttendedListener.onPreviewClickListener(onlineExamAttendees[position].cLASSNAME,
                    onlineExamAttendees[position].sTUDENTNAME,onlineExamAttendees[position].oEXAMID,
                    onlineExamAttendees[position].oEXAMATTEMPTID)
            }

        }

        override fun getItemCount(): Int {
           return onlineExamAttendees.size
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClickListener(oEXAMATTEMPTID: Int,  holder: ExamAttendedAdapter.ViewHolder) {
//        Allow_Once_Method(
//            viewHolder,
//            feedlist_studentlist.get(i).get("OEXAM_ATTEMPT_ID")
//        )
        //OnlineExam/AllowOnceMore?OexamAttemptId=
        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["OexamAttemptId"] = oEXAMATTEMPTID

        objectiveExamStaffViewModel.getCommonGetFuntion("OnlineExam/AllowOnceMore",paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "OK" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "You allowed to do this Exam one More time", constraintLayoutContent!!)
                                    holder.buttonAllowAgain.isEnabled = false
                                    holder.buttonAllowAgain.setBackgroundResource(R.drawable.round_light_green600)
                                    holder.buttonAllowAgain.setTextAppearance(
                                        context,
                                        R.style.RoundedCornerButtonLightGreen600
                                    )
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Allowed Failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }
    override fun onPreviewClickListener(
        cLASSNAME: String,
        sTUDENTNAME: String,
        oEXAMID: Int,
        oEXAMATTEMPTID: Int
    ) {
        val intent = Intent(requireActivity(), OExamPreViewActivity::class.java)
        intent.putExtra("CLASS_NAME", cLASSNAME)
        intent.putExtra("STUDENT_NAME", sTUDENTNAME)
        intent.putExtra("OEXAM_ID", oEXAMID)
        intent.putExtra("OEXAM_ATTEMPT_ID", oEXAMATTEMPTID)
        startActivity(intent)
    }

}

interface ExamAttendedListener{
    fun onClickListener(
        oEXAMATTEMPTID: Int,
        holder: ObjDetailsTabFragment.ExamAttendedAdapter.ViewHolder
    )

    fun onPreviewClickListener(cLASSNAME: String, sTUDENTNAME: String, oEXAMID: Int, oEXAMATTEMPTID: Int)
}