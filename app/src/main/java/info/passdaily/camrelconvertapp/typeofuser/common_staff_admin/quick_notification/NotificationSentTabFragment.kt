package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentAttendedTabBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_live_class_report.AttendedTabFragment
import java.util.ArrayList

class NotificationSentTabFragment() : Fragment(),NotificationSentTabClicker {

    var TAG = "NotificationSentTabFragment"
    private var _binding: FragmentAttendedTabBinding? = null
    private val binding get() = _binding!!

    var recyclerView : RecyclerView? = null
    var adminRole = 0

    lateinit var mAdapter: NotificationAdapter

    private lateinit var localDBHelper : LocalDBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


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
        mAdapter = NotificationAdapter(
            this,
            Global.notificationSentList,
            requireActivity(),
            TAG
        )
        recyclerView?.adapter = mAdapter


        if(Global.notificationSentList.isNotEmpty()){
            recyclerView?.visibility = View.VISIBLE
            constraintEmpty.visibility = View.GONE

        }else{
            recyclerView?.visibility = View.GONE
            constraintEmpty.visibility = View.VISIBLE
            Glide.with(this)
                .load(R.drawable.ic_empty_progress_report)
                .into(imageViewEmpty)

            textEmpty.text =  resources.getString(R.string.no_results)
        }
    }


    class NotificationAdapter(var notificationSentTabClicker: NotificationSentTabClicker,
                              var notificationSentList: ArrayList<NotificationSentStaffModel.InboxSent>,
                              var context: Context, var TAG: String)
        : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewTitle: TextView = view.findViewById(R.id.textViewClass)
            var textViewDate: TextView = view.findViewById(R.id.textViewDate)
            var textViewDesc: TextView = view.findViewById(R.id.textViewTitle)

        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.notification_sent_tab_adapter, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewTitle.text = notificationSentList[position].vIRTUALMAILTITLE

            if(!notificationSentList[position].vIRTUALMAILSENTDATE.isNullOrBlank()) {
                val date: Array<String> =
                    notificationSentList[position].vIRTUALMAILSENTDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                holder.textViewDate.text = Utils.formattedDate(dddd)
            }
            holder.textViewDesc.text = notificationSentList[position].vIRTUALMAILCONTENT

            holder.itemView.setOnClickListener {
                notificationSentTabClicker.onDetailClicker(notificationSentList[position])
            }
        }

        override fun getItemCount(): Int {
            return notificationSentList.size
        }

    }

    override fun onDetailClicker(notificationSentList: NotificationSentStaffModel.InboxSent) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialogview_inbox)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.attributes = lp

        var sentDate = ""
        var textViewTitle = dialog.findViewById<TextView>(R.id.textViewTitle)
        var textViewDesc = dialog.findViewById<TextView>(R.id.textViewDesc)
        var textViewDate = dialog.findViewById<TextView>(R.id.textViewDate)
        var imageViewClose  = dialog.findViewById<ImageView>(R.id.imageViewClose)

        if(!notificationSentList.vIRTUALMAILSENTDATE.isNullOrBlank()) {
            val date1: Array<String> = notificationSentList.vIRTUALMAILSENTDATE.split("T".toRegex()).toTypedArray()
            val sendStr = Utils.longconversion(date1[0] + " " + date1[1])
            sentDate = Utils.formattedDateTime(sendStr)
        }
        textViewDate.text = sentDate
        textViewTitle.text = notificationSentList.vIRTUALMAILTITLE
        textViewDesc.text = notificationSentList.vIRTUALMAILCONTENT

        imageViewClose.setOnClickListener {


            dialog.dismiss()
        }

        dialog.show()
    }


}

interface NotificationSentTabClicker{
    fun onDetailClicker(notificationSentList: NotificationSentStaffModel.InboxSent)
}