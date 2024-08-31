package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.track

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentAttendedTabBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.parent.MainActivityParent
import info.passdaily.camrelconvertapp.typeofuser.parent.notification.NotificationFragment
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener


@Suppress("DEPRECATION")
class TrackStaffFragment : Fragment(),TrackStaffClickListener {

    var TAG = "TrackStaffFragment"
    var toolBarClickListener : ToolBarClickListener? = null

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var staffId = 0
    var recyclerView : RecyclerView? = null

    private var _binding: FragmentAttendedTabBinding? = null
    private val binding get() = _binding!!
    var vehicleList = ArrayList<VehichlesModel.Vehichle>()

    private lateinit var trackStaffViewModel: TrackStaffViewModel
    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    lateinit var mAdapter : TrackAdapter
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
        toolBarClickListener?.setToolbarName("Track Vehicle")

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        staffId = user[0].STAFF_ID

        // Inflate the layout for this fragment
        trackStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[TrackStaffViewModel::class.java]
        // Inflate the layout for this fragment
        _binding = FragmentAttendedTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        shimmerViewContainer = binding.shimmerViewContainer

        recyclerView = binding.recyclerView
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())

        initFunction()
    }


    private fun initFunction() {
        trackStaffViewModel.getVehicleList(0)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            vehicleList = response.vehichles as ArrayList<VehichlesModel.Vehichle>

                            if(vehicleList.isNotEmpty()){
                                recyclerView?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    mAdapter =  TrackAdapter(
                                        this,
                                        vehicleList,
                                        requireActivity(),
                                        TAG
                                    )
                                    recyclerView?.adapter = mAdapter
                                }
                            }else{
                                recyclerView?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_state_notification)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }
                            Log.i(TAG,"getMeetingAttendedReport SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerView?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getMeetingAttendedReport ERROR")
                        }
                        Status.LOADING -> {
                            recyclerView?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            vehicleList = ArrayList<VehichlesModel.Vehichle>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_state_notification)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"getMeetingAttendedReport LOADING")
                        }
                    }
                }
            })

    }

    class TrackAdapter(var trackStaffClickListener: TrackStaffClickListener,
                       var vehicleList: ArrayList<VehichlesModel.Vehichle>,
                       var mContext: Context, var TAG: String) : RecyclerView.Adapter<TrackAdapter.ViewHolder>() {
        var firstLetter = ""
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textBusNo: TextView = view.findViewById(R.id.textBusNo)
            var textBusName: TextView = view.findViewById(R.id.textBusName)
            var textBusRoute: TextView = view.findViewById(R.id.textBusRoute)

        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.track_staff_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textBusNo.text = vehicleList[position].vEHICLEREGNO?.replace("null","")
            holder.textBusName.text = vehicleList[position].vEHICLENAME?.replace("null","")
            holder.textBusRoute.text = vehicleList[position].vEHICLEROOT?.replace("null","")

            holder.itemView.setOnClickListener {
                val intent  = Intent(mContext, MapActivityTest::class.java)
                intent.putExtra("VEHICLE_NAME", vehicleList[position].vEHICLENAME)
                intent.putExtra("TERMINAL_ID", vehicleList[position].tERMINALID)
                intent.putExtra("SIM_NUMBER", vehicleList[position].sIMNUMBER)
                intent.putExtra("VEHICLE_ID", vehicleList[position].vEHICLEID.toString())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent)

            }

        }

        override fun getItemCount(): Int {
           return vehicleList.size
        }

    }


}
interface TrackStaffClickListener {

}