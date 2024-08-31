package info.passdaily.camrelconvertapp.typeofuser.parent.event

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentManageEventBinding
import info.passdaily.camrelconvertapp.databinding.FragmentParentEventsBinding
import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
//import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
import info.passdaily.camrelconvertapp.lib.video.Video_Activity
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.YouTubeIframePlayer
//import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.YouTubePlayerActivity
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class ManageEventDialog : DialogFragment(),EventPClickListener{

    companion object {
        var TAG = "ManageEventDialog"
    }

    private lateinit var manageEventViewModel: ManageEventParentViewModel
    private var _binding: FragmentParentEventsBinding? = null
    private val binding get() = _binding!!

    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var toolbar: Toolbar? = null


    var constraintLayoutContent: ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo: RecyclerView? = null

    var spinnerAcademic: AppCompatSpinner? = null

    var getEventList = ArrayList<EventListModel.Event>()

    lateinit var mAdapter : EventAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getProductById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID


        manageEventViewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[ManageEventParentViewModel::class.java]

        _binding = FragmentParentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = binding.toolbar
//        if (toolbar != null) {
//            setSupportActionBar(toolbar)
//            supportActionBar!!.title = "Create Assignment"
//            // Customize the back button
//            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
//            supportActionBar!!.setDisplayShowTitleEnabled(true)
//            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
//                onBackPressed()
//            })
//        }

        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_white)
        toolbar?.title = "Manage Events"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.white))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }

        constraintLayoutContent = binding.constraintLayoutContent
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        shimmerViewContainer = binding.shimmerViewContainer


        recyclerViewVideo = binding.recyclerViewEvent
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())


//        spinnerAcademic?.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long
//            ) {
//                aCCADEMICID = getYears[position].aCCADEMICID
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }



//        initFunction()


        binding.buttonSubmit.visibility = View.GONE


        getEventList()


        val constraintLayoutContent = binding.constraintLayoutContent
        constraintLayoutContent.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLayoutContent.windowToken, 0)
        }

        //    Utils.setStatusBarColor(requireActivity())
    }

    fun getEventList() {

        manageEventViewModel.getManageEventsParent(ACADEMICID,CLASSID,STUDENTID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {

                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getEventList = response.eventList
                            if (getEventList.isNotEmpty()) {
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    mAdapter = EventAdapter(
                                        this,
                                        getEventList,
                                        requireActivity(), TAG
                                    )
                                    recyclerViewVideo!!.adapter = mAdapter
                                }
                            } else {
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text = resources.getString(R.string.no_results)
                            }
                            Log.i(TAG, "getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewVideo?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text = resources.getString(R.string.no_internet)
                            Log.i(TAG, "getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerViewVideo?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getEventList = ArrayList<EventListModel.Event>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)

                            textEmpty?.text = resources.getString(R.string.loading)
                            Log.i(TAG, "getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    class EventAdapter(
        var eventClickListener: EventPClickListener,
        var eventList: ArrayList<EventListModel.Event>,
        var context: Context,var TAG : String
    ) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageViewEvent: ImageView = view.findViewById(R.id.imageViewEvent)
            var textViewTitle : TextView = view.findViewById(R.id.textViewTitle)
            var textViewDelete : TextView = view.findViewById(R.id.textViewDelete)
            var textViewDesc : TextView = view.findViewById(R.id.textViewDesc)
            var textViewDate  : TextView = view.findViewById(R.id.textViewDate)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.parent_event_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.textViewDelete.visibility = View.GONE
            holder.textViewTitle.text = eventList[position].eVENTTITLE

            holder.textViewDesc.text = eventList[position].eVENTDESCRIPTION


            if(!eventList[position].eVENTDATE.isNullOrBlank()) {
                val date: Array<String> = eventList[position].eVENTDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                holder.textViewDate.text = Utils.formattedDateWords(dddd)
            }

            Glide.with(context)
                .load(Global.event_url + "/EventFile/" + eventList[position].eVENTFILE)
                .into(holder.imageViewEvent)

//            Glide.with(context)
//                .load(Global.event_url+"/EventFile/"+eventList[position].eVENTFILE)
//                .apply(
//                    RequestOptions.fitCenterTransform()
//                        .dontAnimate() //   .override(imageSize, imageSize)
//                        .placeholder(R.drawable.ic_image_view)
//                )
//                .thumbnail(0.5f)
//                .into(holder.imageViewEvent)

            holder.itemView.setOnClickListener {
                eventClickListener.onViewEvent(eventList[position])
            }

        }

        override fun getItemCount(): Int {
            return eventList.size
        }

    }



    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }
    }

    override fun onViewEvent(event: EventListModel.Event) {
        Log.i(TAG,"event ${event.eVENTLINKFILE}")
        if(event.eVENTTYPE == 2){
            val intent = Intent(requireActivity(), ExoPlayerActivity::class.java)
            intent.putExtra("ALBUM_TITLE", "")
            intent.putExtra("ALBUM_FILE", Global.event_url+"/EventFile/"+event.eVENTLINKFILE)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
        }
        else if(event.eVENTTYPE == 3){
            val split2 = event.eVENTLINKFILE.split("=").toTypedArray()
            val intent = Intent(requireActivity(), YouTubeIframePlayer::class.java)
            intent.putExtra("youTubeLink", split2[1])
            intent.putExtra("YOUTUBE_ID", event.eVENTID)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent)
        }
    }

}

interface EventPClickListener {
    fun onViewEvent(event : EventListModel.Event)
}