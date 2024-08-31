//package info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.appcompat.widget.AppCompatButton
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import info.passdaily.camrelconvertapp.R
//import info.passdaily.camrelconvertapp.databinding.FragmentLiveScheduledBinding
//import info.passdaily.camrelconvertapp.model.*
//import info.passdaily.camrelconvertapp.services.Global
//import info.passdaily.camrelconvertapp.services.Status
//import info.passdaily.camrelconvertapp.services.Utils
//import info.passdaily.camrelconvertapp.services.ViewModelFactory
//import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
//import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
//import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
//
//@Suppress("DEPRECATION")
//class LiveScheduledFragment : Fragment() {
//
//    var TAG ="LiveScheduledFragment"
//
//    private lateinit var liveScheduledViewModel: LiveScheduledViewModel
//    private var _binding: FragmentLiveScheduledBinding? = null
//
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
//    var recyclerViewLive :  RecyclerView? =null
//
//    var STUDENT_ID = 0
//    var ACCADEMIC_ID = 0
//    var CLASS_ID = 0
//
//    var constraintEmpty: ConstraintLayout? =null
//    var imageViewEmpty: ImageView? =null
//    var textEmpty : TextView? =null
//
//    var mContext : Context? =null
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (mContext == null) {
//            mContext = context.applicationContext;
//        }
//        Log.i(TAG,"onAttach ")
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        Global.currentPage = 3
//        Global.screenState = "landingpage"
//        val studentDBHelper = StudentDBHelper(requireActivity())
//        var student = studentDBHelper.getStudentById(Global.studentId)
//
//        STUDENT_ID =   student.STUDENT_ID
//        ACCADEMIC_ID = student.ACCADEMIC_ID
//        CLASS_ID =     student.CLASS_ID
//
//        Log.i(TAG,"STUDENT_ID "+student.STUDENT_ID)
//        Log.i(TAG,"ACCADEMIC_ID "+student.ACCADEMIC_ID)
//        Log.i(TAG,"CLASS_ID "+student.CLASS_ID)
//
//        liveScheduledViewModel = ViewModelProviders.of(
//            this,
//            ViewModelFactory(ApiClient(NetworkLayer.services))
//        )[LiveScheduledViewModel::class.java]
////AccademicId=7&ClassId=2&StudentId=1
// //       liveScheduledViewModel.getZoomSchedule(student.ACCADEMIC_ID,student.CLASS_ID,student.STUDENT_ID)
//
//        _binding = FragmentLiveScheduledBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        constraintEmpty = binding.constraintEmpty
//        imageViewEmpty = binding.imageViewEmpty
//        textEmpty = binding.textEmpty
//        Glide.with(requireActivity())
//            .load(R.drawable.ic_empty_state_live)
//            .into(imageViewEmpty!!)
//        textEmpty?.text = "No Live Classes"
//
//
//
//        recyclerViewLive = binding.recyclerViewLive
//        recyclerViewLive?.layoutManager = LinearLayoutManager(requireActivity())
//        recyclerViewLive?.hasFixedSize()
//
//        initItems()
//      //  val textView: TextView = binding.textGallery
////        liveScheduledViewModel.text.observe(viewLifecycleOwner, Observer {
////            textView.text = it
////        })
//        return root
//    }
//
//
//    private fun initItems() {
////        liveScheduledViewModel.getZoomScheduleObservable()
////            .observe(requireActivity(), {
////                if (it != null) {
////                    if(isAdded) {
////                        recyclerViewLive?.adapter =
////                            ZoomScheduleListAdapter(it.zoomMeetingList, mContext!!)
////                    }
////                }
////            })
//
//
//        liveScheduledViewModel.getZoomScheduleList( ACCADEMIC_ID,
//            CLASS_ID,
//            STUDENT_ID)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//
//                            if(response.zoomMeetingList.isNotEmpty()){
//                                constraintEmpty?.visibility = View.GONE
//                                recyclerViewLive?.visibility = View.VISIBLE
//                                if (isAdded) {
//                                    recyclerViewLive?.adapter =
//                                        ZoomScheduleListAdapter(
//                                            response.zoomMeetingList, mContext!!
//                                        )
//                                }
//                            }else{
//                                constraintEmpty?.visibility = View.VISIBLE
//                                recyclerViewLive?.visibility = View.GONE
//                                if (isAdded) {
//                                    Glide.with(mContext!!)
//                                        .load(R.drawable.ic_empty_state_live)
//                                        .into(imageViewEmpty!!)
//                                }
//                                textEmpty?.text = requireActivity().resources.getString(R.string.no_results)
//
//                            }
//                        }
//                        Status.ERROR -> {
//                            constraintEmpty?.visibility = View.VISIBLE
//                            recyclerViewLive?.visibility = View.GONE
//
//                            if (isAdded) {
//                                Glide.with(mContext!!)
//                                    .load(R.drawable.ic_no_internet)
//                                    .into(imageViewEmpty!!)
//                            }
//                            textEmpty?.text = requireActivity().resources.getString(R.string.no_internet_str)
//
//                            Log.i(TAG, "Status.ERROR ${Status.ERROR}")
//                        }
//                        Status.LOADING -> {
//
//                            if (isAdded) {
//                                Glide.with(mContext!!)
//                                    .load(R.drawable.ic_empty_state_live)
//                                    .into(imageViewEmpty!!)
//                            }
//                            textEmpty?.text =  requireActivity().resources.getString(R.string.loading)
//                            recyclerViewLive?.visibility = View.GONE
//                            constraintEmpty?.visibility = View.GONE
//
//                        }
//                    }
//                }
//            })
//    }
//
//    class ZoomScheduleListAdapter(var zoomMeetingList: List<ZoomScheduleListModel.ZoomMeeting>,
//                                  var context: Context)
//        : RecyclerView.Adapter<ZoomScheduleListAdapter.ViewHolder>() {
//        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var textViewID: TextView = view.findViewById(R.id.textViewID)
//            var textViewPassword: TextView = view.findViewById(R.id.textViewPassword)
//            //textViewDate
//            var textViewDate: TextView = view.findViewById(R.id.textViewStatus)
//
//            var buttonLive : AppCompatButton = view.findViewById(R.id.buttonLive)
//        }
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val itemView = LayoutInflater.from(parent.context)
//                .inflate(R.layout.zoomlist_adapter, parent, false)
//            return ViewHolder(itemView)
//        }
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//            val date: Array<String> =
//                zoomMeetingList[position].mEETINGSTATRDATE.split("T".toRegex()).toTypedArray()
//            val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
//            holder.textViewDate.text = Utils.formattedDate(dddd)
//
//            holder.textViewID.text = zoomMeetingList[position].mEETINGID
//            holder.textViewPassword.text = zoomMeetingList[position].mEETINGPASSWORD
//
//            holder.buttonLive.setOnClickListener {
//                val intent  = Intent(context, ZoomLiveActivity::class.java)
//                intent.putExtra("liveClass", 1)
//                intent.putExtra("mainClass", 0)
//                intent.putExtra("zOOMMEETINGID", zoomMeetingList[position].mEETINGID)
//                intent.putExtra("zOOMMEETINGPASSWORD", zoomMeetingList[position].mEETINGPASSWORD)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent)
//            }
//        }
//
//        override fun getItemCount(): Int {
//          return zoomMeetingList.size
//        }
//
//    }
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//
//    override fun onDetach() {
//        super.onDetach()
//        mContext = null
//        Log.i(TAG,"onDetach ")
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mContext = null
//        Log.i(TAG,"onDestroy ")
//    }
//
//
//
//
//}