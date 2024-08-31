package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_live_class_report

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentAttendedTabBinding
import info.passdaily.camrelconvertapp.databinding.FragmentObjUnattendedTabBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.ObjUnAttendedTAbFragment
import java.util.ArrayList

class AttendedTabFragment(var meetingAttendedList: ArrayList<ZoomMeetingAttendedListModel.MeetingAttended>) : Fragment() {

    var TAG = "AttendedTabFragment"
    private var _binding: FragmentAttendedTabBinding? = null
    private val binding get() = _binding!!

    var recyclerView : RecyclerView? = null

    lateinit var mAdapter: AttendedAdapter

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
        mAdapter = AttendedAdapter(
            meetingAttendedList,
            requireActivity(),
            TAG
        )
        recyclerView?.adapter = mAdapter


        if(meetingAttendedList.isNotEmpty()){
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

    class AttendedAdapter(var meetingAttendedList: ArrayList<ZoomMeetingAttendedListModel.MeetingAttended>,
                          var context: Context, var TAG: String)
        : RecyclerView.Adapter<AttendedAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textStudentName: TextView = view.findViewById(R.id.textStudentName)
            var textJoinDate: TextView = view.findViewById(R.id.textJoinDate)
            var textViewClass: TextView = view.findViewById(R.id.textViewClass)
            var textTimeDuration: TextView = view.findViewById(R.id.textTimeDuration)
        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): AttendedAdapter.ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.attended_adapter, parent, false)
            )
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: AttendedAdapter.ViewHolder, position: Int) {
           holder.textStudentName.text = meetingAttendedList[position].sTUDENTNAME
            if(!meetingAttendedList[position].zOOMSTARTDATE.isNullOrBlank()) {
                val date: Array<String> = meetingAttendedList[position].zOOMSTARTDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                holder.textJoinDate.text = "Join Date : ${Utils.formattedDateTimeInbox(dddd)}"
            }else{
                holder.textJoinDate.text = "Join Date : "
            }
            holder.textViewClass.text = "Class : ${meetingAttendedList[position].cLASSNAME}"
            holder.textTimeDuration.text = "Time Duration : ${meetingAttendedList[position].zATTENTTOTALTIME} Minutes"
        }

        override fun getItemCount(): Int {
            return meetingAttendedList.size
        }

    }


}