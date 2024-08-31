package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_event

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.Observer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.PopupMenu
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
import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
//import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
import info.passdaily.camrelconvertapp.lib.video.Video_Activity

import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_about_faq.CreateAboutUsFaqDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.AssignmentDetailStaffActivity
import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.YouTubeIframePlayer
//import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.YouTubePlayerActivity
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class ManageStaffEventDialog : DialogFragment(),EventPClickListener{

    companion object {
        var TAG = "ManageStaffEventDialog"
    }

    private lateinit var manageEventViewModel: ManageEventViewModel
    private var _binding: FragmentManageEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var schoolId = 0

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var toolbar: Toolbar? = null
    var getYears = ArrayList<AcademicListModel.AccademicDetail>()
    var aCCADEMICID = 0

    var constraintLayoutContent: ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo: RecyclerView? = null

    var spinnerAcademic: AppCompatSpinner? = null

    var typeStr = "-1"

    var isChecked = false

    var type = arrayOf("Select Event", "Image Event", "Video Event", "Youtube Event")

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

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID


        manageEventViewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ManageEventViewModel::class.java]

        _binding = FragmentManageEventBinding.inflate(inflater, container, false)
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


        spinnerAcademic = binding.spinnerAcademic
        initFunction()

        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                aCCADEMICID = getYears[position].aCCADEMICID
                if(isChecked) {
                    getEventList()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }



        var spinnerType = binding.spinnerType
        val status =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, type)
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = status
        spinnerType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                when (position) {
                    0 -> { typeStr = "-1" }
                    1 -> { typeStr = "1" }
                    2 -> { typeStr = "2" }
                    3 -> { typeStr = "3" }
                }
                if(isChecked) {
                    getEventList()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.buttonSubmit.visibility = View.GONE
        binding.fab.visibility = View.VISIBLE

        binding.fab.setOnClickListener {

            val dialog1 = CreateEventDialog(this)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, CreateEventDialog.TAG)
        }




        val constraintLayoutContent = binding.constraintLayoutContent
        constraintLayoutContent.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLayoutContent.windowToken, 0)
        }

        Utils.setStatusBarColor(requireActivity())

        initFunction()
        getEventList()
    }

    private fun initFunction() {
        manageEventViewModel.getAcademicList(0, schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getYears =
                                response.accademicDetails as ArrayList<AcademicListModel.AccademicDetail>
                            var years = Array(getYears.size) { "" }
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                            }
                            if (spinnerAcademic != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademic?.adapter = adapter
                            }
                            Log.i(TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }

    fun getEventList() {

        manageEventViewModel.getManageEvents(aCCADEMICID, typeStr.toInt(),adminId,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            isChecked = true
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
           // var textViewDelete : TextView = view.findViewById(R.id.textViewDelete)
            var imageViewMore : ImageView = view.findViewById(R.id.imageViewMore)
            var textViewDesc : TextView = view.findViewById(R.id.textViewDesc)
            var textViewDate  : TextView = view.findViewById(R.id.textViewDate)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.manage_event_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {


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

            holder.imageViewEvent.setOnClickListener {
                eventClickListener.onViewEvent(eventList[position])
            }

//            holder.imageViewMore.setOnClickListener {
//               // eventClickListener.onViewEvent(eventList[position])
//
//            }

            holder.imageViewMore.setOnClickListener(View.OnClickListener {
                val popupMenu = PopupMenu(context, holder.imageViewMore)
                popupMenu.inflate(R.menu.video_adapter_menu)
                popupMenu.menu.findItem(R.id.menu_edit).icon = context.resources.getDrawable(R.drawable.ic_icon_edit)
                popupMenu.menu.findItem(R.id.menu_report).icon = context.resources.getDrawable(R.drawable.ic_icon_about_gray)
                popupMenu.menu.findItem(R.id.menu_video).icon = context.resources.getDrawable(R.drawable.ic_icon_close)
                popupMenu.menu.findItem(R.id.menu_offline_video).icon = context.resources.getDrawable(R.drawable.ic_icon_delete_gray)
                popupMenu.menu.findItem(R.id.menu_download).icon = context.resources.getDrawable(R.drawable.ic_icon_download)
                popupMenu.menu.findItem(R.id.menu_open).icon = context.resources.getDrawable(R.drawable.ic_icon_download)

                popupMenu.menu.findItem(R.id.menu_report).isVisible = false
                popupMenu.menu.findItem(R.id.menu_open).isVisible = false
                popupMenu.menu.findItem(R.id.menu_download).isVisible = false
                popupMenu.menu.findItem(R.id.menu_offline_video).isVisible = false
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            eventClickListener.onUpdateEvent(eventList[position])
                            true
                        }
                        R.id.menu_video -> {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Are you sure want to delete event?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { _, _ ->
                                    //Assignment/AssignmentDelete?AssignmentId=
                                    eventClickListener.onDeleteEvent(eventList[position])
                                }
                                .setNegativeButton(
                                    "No"
                                ) { dialog, _ -> //  Action for 'NO' Button
                                    dialog.cancel()
                                }
                            //Creating dialog box
                            val alert = builder.create()
                            //Setting the title manually
                            alert.setTitle("Delete")
                            alert.show()
                            val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                            buttonbackground.setTextColor(Color.BLACK)
                            val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                            buttonbackground1.setTextColor(Color.BLACK)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            })

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

    override fun onUpdateEvent(event: EventListModel.Event) {
        val dialog1 = UpdateEventDialog(this,event)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.show(transaction, UpdateEventDialog.TAG)
    }

    override fun onDeleteEvent(event: EventListModel.Event) {
        manageEventViewModel.deleteEventItem(event.eVENTID,adminId,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(),"Deleted Successfully",binding.constraintLayoutContent)
                                    getEventList()
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(),"Deletion Failed",binding.constraintLayoutContent)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    override fun onShowMessage(message: String) {
        getEventList()
    }

    fun deleteFunction(eVENTID: Int) {
        manageEventViewModel.deleteEventItem(eVENTID,adminId,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(),"Deleted Successfully",binding.constraintLayoutContent)
                                    getEventList()
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(),"Deletion Failed",binding.constraintLayoutContent)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    private fun progressStart() {
        val dialog1 = ProgressBarDialog()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.isCancelable = false
        dialog1.show(transaction, ProgressBarDialog.TAG)
    }

    fun progressStop() {
        val fragment: ProgressBarDialog? =
            requireActivity().supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
        if (fragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }

}

interface EventPClickListener {
    fun onViewEvent(event : EventListModel.Event)
    fun onUpdateEvent(event : EventListModel.Event)
    fun onDeleteEvent(event : EventListModel.Event)
    fun onShowMessage(message : String)
}