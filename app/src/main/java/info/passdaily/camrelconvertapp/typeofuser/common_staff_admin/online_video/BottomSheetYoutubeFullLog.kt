package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.online_video

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import info.passdaily.camrelconvertapp.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import android.widget.TextView
import com.github.sundeepk.compactcalendarview.domain.Event
import info.passdaily.camrelconvertapp.databinding.BottomSheetForLogBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Utils
import java.util.ArrayList

@Suppress("DEPRECATION")
class BottomSheetYoutubeFullLog : BottomSheetDialogFragment {
    var sTUDENTNAME = ""
    var youtubeFullLogs = ArrayList<YoutubeFullLogsModel.YoutubeFullLog>()
    var recyclerView: RecyclerView? = null

    private var _binding: BottomSheetForLogBinding? = null
    private val binding get() = _binding!!


    constructor(sTUDENTNAME : String,youtubeFullLogs: ArrayList<YoutubeFullLogsModel.YoutubeFullLog>) {
        this.sTUDENTNAME = sTUDENTNAME
        this.youtubeFullLogs = youtubeFullLogs
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

        _binding = BottomSheetForLogBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        hashMapArrayList= new ArrayList <>();
        binding.textViewTitle.text = sTUDENTNAME

        recyclerView = binding.recyclerView
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        Log.i(TAG, "getList $youtubeFullLogs")

        recyclerView?.adapter = ListFeedAdapter(youtubeFullLogs,requireActivity())

    }

    private inner class ListFeedAdapter(
        var youtubeFullLogs: ArrayList<YoutubeFullLogsModel.YoutubeFullLog>,
        var context: Context
    ) :
        RecyclerView.Adapter<ListFeedAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.bottomsheet_log_adapter, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.i(TAG, "getListFeed " + youtubeFullLogs[position].lOGDATE)

            if(!youtubeFullLogs[position].lOGDATE.isNullOrBlank()) {
                val date: Array<String> =
                    youtubeFullLogs[position].lOGDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                holder.textViewDate.text = Utils.formattedDateWords(dddd)
                holder.textViewTime.text = Utils.formattedTime(dddd)
            }

        }

        override fun getItemCount(): Int {
            return youtubeFullLogs.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
            var textViewTime: TextView = itemView.findViewById(R.id.textViewTime)

        }
    }

    companion object {
        var TAG = "BottomSheetFragment"
    }
}