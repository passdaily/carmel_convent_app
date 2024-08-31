package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.online_video

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentAttendedTabBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification.NotificationSentTabFragment
import java.util.ArrayList


class YoutubeViewedTabFragment(
    var videoViewedListener: VideoViewedListener,
    var youtubeLogList: ArrayList<YoutubeReportModel.YoutubeLog>
) : Fragment() {

    var TAG = "YoutubeViewedTabFragment"
    private var _binding: FragmentAttendedTabBinding? = null
    private val binding get() = _binding!!

    var recyclerView : RecyclerView? = null

    lateinit var mAdapter: YoutubeViewedAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
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
        if(isAdded) {
            mAdapter = YoutubeViewedAdapter(
                videoViewedListener,
                youtubeLogList,
                requireActivity(),
                TAG
            )
        }
        recyclerView?.adapter = mAdapter


        if(youtubeLogList.isNotEmpty()){
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

    class YoutubeViewedAdapter(var videoViewedListener: VideoViewedListener,
                               var youtubeLogList: ArrayList<YoutubeReportModel.YoutubeLog>,
                               var context: Context, var TAG: String)
            : RecyclerView.Adapter<YoutubeViewedAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewName: TextView = view.findViewById(R.id.textViewName)
            var textFirstView: TextView = view.findViewById(R.id.textFirstView)
            var textRecentView : TextView = view.findViewById(R.id.textRecentView)

            var cardViewClicked : CardView = view.findViewById(R.id.cardViewClicked)
            var textViewClick : TextView = view.findViewById(R.id.textViewClick)

            var buttonReport : AppCompatButton = view.findViewById(R.id.buttonReport)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.youtube_viewed_tab_adapter, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewName.text = youtubeLogList[position].sTUDENTNAME

            holder.textViewClick.text = "Clicked : ${youtubeLogList[position].lOGCOUNT}"

            if(!youtubeLogList[position].fIRSTDATE.isNullOrBlank()) {
                val date: Array<String> =
                    youtubeLogList[position].fIRSTDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                holder.textFirstView.text = Utils.formattedDateTime(dddd)
            }

            if(!youtubeLogList[position].rECENTDATE.isNullOrBlank()) {
                val date: Array<String> =
                    youtubeLogList[position].rECENTDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                holder.textRecentView.text = Utils.formattedDateTime(dddd)
            }

            holder.buttonReport.setOnClickListener {
                videoViewedListener.onLogsClicker(youtubeLogList[position])
            }
        }

        override fun getItemCount(): Int {
            return youtubeLogList.size
        }

    }

}