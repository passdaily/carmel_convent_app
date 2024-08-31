package info.passdaily.camrelconvertapp.typeofuser.parent.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentTrackBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.track.MapActivityTest

@Suppress("DEPRECATION")
class TrackFragment : Fragment() {

    var TAG = "TrackFragment"
    lateinit var binding : FragmentTrackBinding

    private lateinit var trackViewModel: TrackViewModel

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var recyclerViewTrack : RecyclerView? = null
    var constraintEmpty: ConstraintLayout? =null
    var imageViewEmpty: ImageView? =null
    var textEmpty : TextView? =null

    var shimmerViewContainer : ShimmerFrameLayout? =null
    var mContext : Context? =null
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG,"onAttach ")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Global.currentPage = 15
        Global.screenState = "landingpage"

        trackViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[TrackViewModel::class.java]

        // Inflate the layout for this fragment
        binding = FragmentTrackBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //        bindingNotification.txtMessage.text="Sample Text"

        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getProductById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID
        STUDENT_ROLL_NO = student.STUDENT_ROLL_NO

        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(requireActivity())
            .load(R.drawable.ic_empty_state_assignment)
            .into(imageViewEmpty!!)
        textEmpty?.text = "No Track Vehicle"

        var textViewTitle = binding.textView32
        textViewTitle.text ="Track Vehicle"

        recyclerViewTrack = binding.recyclerView
        recyclerViewTrack?.layoutManager =  LinearLayoutManager(requireActivity())

        shimmerViewContainer = binding.shimmerViewContainer

        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_track, container, false)
        initTrack()
    }

    private fun initTrack() {
        trackViewModel.getTrackMap(STUDENTID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(response.vehichles.isNotEmpty()){
                                constraintEmpty?.visibility = View.GONE
                                recyclerViewTrack?.visibility = View.VISIBLE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewTrack?.adapter =
                                        TrackAdapter(
                                            response.vehichles,
                                            mContext!!
                                        )
                                }
                            }else{
                                constraintEmpty?.visibility = View.VISIBLE
                                recyclerViewTrack?.visibility = View.GONE
                                shimmerViewContainer?.visibility = View.GONE
                                if (isAdded) {
                                    Glide.with(mContext!!)
                                        .load(R.drawable.ic_empty_state_absent)
                                        .into(imageViewEmpty!!)
                                }
                                textEmpty?.text = requireActivity().resources.getString(R.string.no_results)
                            }
                        }
                        Status.ERROR -> {
                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_no_internet)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text =  requireActivity().resources.getString(R.string.no_internet)
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewTrack?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                        }
                        Status.LOADING -> {
                            if (isAdded) {
                                Glide.with(mContext!!)
                                    .load(R.drawable.ic_empty_state_absent)
                                    .into(imageViewEmpty!!)
                            }
                            textEmpty?.text =  requireActivity().resources.getString(R.string.loading)
                            shimmerViewContainer?.visibility = View.VISIBLE
                            recyclerViewTrack?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                        }
                    }
                }
            })

    }

    class TrackAdapter(var vehichles: ArrayList<VehichlesModel.Vehichle>, var mContext: Context) :
        RecyclerView.Adapter<TrackAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textBusNo: TextView = view.findViewById(R.id.textBusNo)
            var textBusName: TextView = view.findViewById(R.id.textBusName)
            var textBusRoute: TextView = view.findViewById(R.id.textBusRoute)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.track_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textBusNo.text = vehichles[position].vEHICLEREGNO?.replace("null","")
            holder.textBusName.text = vehichles[position].vEHICLENAME?.replace("null","")
            holder.textBusRoute.text = vehichles[position].vEHICLEROOT?.replace("null","")

            holder.itemView.setOnClickListener {
                val intent  = Intent(mContext, MapActivityTest::class.java)
                intent.putExtra("VEHICLE_NAME", vehichles[position].vEHICLENAME)
                intent.putExtra("TERMINAL_ID", vehichles[position].tERMINALID)
                intent.putExtra("SIM_NUMBER", vehichles[position].sIMNUMBER)
                intent.putExtra("VEHICLE_ID", vehichles[position].vEHICLEID.toString())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent)

            }
        }

        override fun getItemCount(): Int {
            return vehichles.size
        }

    }

}