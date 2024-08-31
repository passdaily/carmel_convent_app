package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_scheduled

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogZoomScheduledReportBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff

@SuppressLint("SetTextI18n")
@Suppress("DEPRECATION")
class ZoomScheduleReportDialog : DialogFragment {

    private var _binding: DialogZoomScheduledReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var zoomScheduledViewModel: ZoomScheduledViewModel

    lateinit var zoomScheduledListener: ZoomScheduledListener

    var toolbar: Toolbar? = null

    companion object {
        var TAG = "ZoomScheduleReportDialog"
    }


    lateinit var  zoomList: ZoomScheduleStaffModel.Zoom

    var recyclerView : RecyclerView? = null


    private var  zoomReportList = ArrayList<ZoomScheduleReportListModel.ZoomReport>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleWhite)
    }

    constructor(
        zoomScheduledListener: ZoomScheduledListener,
        zoomList: ZoomScheduleStaffModel.Zoom
    ) {
        this.zoomScheduledListener = zoomScheduledListener
        this.zoomList = zoomList
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        zoomScheduledViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ZoomScheduledViewModel::class.java]

        _binding = DialogZoomScheduledReportBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_black)
        toolbar?.title = "Zoom Scheduled Report"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.black))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }


        recyclerView = binding.recyclerView
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity() )

        initFunction()

        Utils.setStatusBarColor(requireActivity())
    }



    private fun initFunction() {
        zoomScheduledViewModel.getZoomScheduledReport(zoomList.zMEETINGID)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            zoomReportList = response.zoomReportList as ArrayList<ZoomScheduleReportListModel.ZoomReport>

                            if (isAdded) {
                                recyclerView!!.adapter =
                                    ZoomScheduleReportAdapter(
                                        zoomScheduledListener,
                                        zoomReportList,
                                        requireActivity(),
                                        TAG
                                    )
                            }

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            zoomReportList = ArrayList<ZoomScheduleReportListModel.ZoomReport>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    class ZoomScheduleReportAdapter(var zoomScheduledListener: ZoomScheduledListener,
                                    var zoomReportList: ArrayList<ZoomScheduleReportListModel.ZoomReport>,
                                    var context : Context,
                                    var TAG: String) : RecyclerView.Adapter<ZoomScheduleReportAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewName: TextView = view.findViewById(R.id.textViewName)
            var textFirstView: TextView = view.findViewById(R.id.textFirstView)
            var textRecentView : TextView = view.findViewById(R.id.textRecentView)

            var cardViewClicked : CardView = view.findViewById(R.id.cardViewClicked)
            var textViewClick : TextView = view.findViewById(R.id.textViewClick)


        }
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.scheduled_report_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {
            holder.textViewName.text = zoomReportList[position].sTUDENTNAME

            holder.textViewClick.text = "Clicked : ${zoomReportList[position].cLICKCOUNT}"

            if(!zoomReportList[position].mEETINGCLICKDATE.isNullOrBlank()) {
                val date: Array<String> =
                    zoomReportList[position].mEETINGCLICKDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                holder.textFirstView.text = Utils.formattedDateTime(dddd)
            }

            if(!zoomReportList[position].mEETINGRECENTDATE.isNullOrBlank()) {
                val date: Array<String> =
                    zoomReportList[position].mEETINGRECENTDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
                holder.textRecentView.text = Utils.formattedDateTime(dddd)
            }
        }

        override fun getItemCount(): Int {
            return zoomReportList.size
        }

    }


    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }
    }
}