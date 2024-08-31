//package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_current_meeting
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.res.ColorStateList
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.appcompat.widget.AppCompatButton
//import androidx.appcompat.widget.AppCompatSpinner
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.facebook.shimmer.ShimmerFrameLayout
//import com.google.android.material.imageview.ShapeableImageView
//import com.google.android.material.textfield.TextInputEditText
//import info.passdaily.camrelconvertapp.R
//import info.passdaily.camrelconvertapp.databinding.FragmentCurrentMeetingBinding
//import info.passdaily.camrelconvertapp.databinding.FragmentLiveClassReportBinding
//import info.passdaily.camrelconvertapp.model.*
//import info.passdaily.camrelconvertapp.services.Global
//import info.passdaily.camrelconvertapp.services.Status
//import info.passdaily.camrelconvertapp.services.Utils
//import info.passdaily.camrelconvertapp.services.ViewModelFactory
//import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
//import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
//import info.passdaily.camrelconvertapp.services.initsdk.InitAuthSDKCallback
//import info.passdaily.camrelconvertapp.services.initsdk.UserLoginCallback
//import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
//import info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout.Credentials
//import info.passdaily.camrelconvertapp.services.zoomsdk.JoinMeetingHelperJava
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_scheduled.ZoomScheduledViewModel
//import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
//import us.zoom.sdk.*
//import java.util.*
//
//
//@Suppress("DEPRECATION")
//class CurrentMeetingFragment : Fragment(), InitAuthSDKCallback, MeetingServiceListener,
//    UserLoginCallback.ZoomDemoAuthenticationListener, View.OnClickListener,
//    ZoomSDKAuthenticationListener,LiveClassMeetingListener {
//
//    var TAG = "CurrentMeetingFragment"
//    private var _binding: FragmentCurrentMeetingBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var localDBHelper : LocalDBHelper
//    var toolBarClickListener : ToolBarClickListener? = null
//    var zoomSDK: ZoomSDK? = null
//
//    var aCCADEMICID = 0
//    var cLASSID = 0
//    var sUBJECTID = 0
//    var adminId = 0
//
//    var spinnerAcademic: AppCompatSpinner? = null
//
//    var getYears = ArrayList<GetYearClassExamModel.Year>()
//
//    var constraintLayoutContent : ConstraintLayout? = null
//    var constraintEmpty: ConstraintLayout? = null
//    var imageViewEmpty: ImageView? = null
//    var textEmpty: TextView? = null
//    var shimmerViewContainer: ShimmerFrameLayout? = null
//
//    var email: String? = null
//    var password: String? = null
//
//    var recyclerViewVideo : RecyclerView? = null
//
//    var textViewDate: TextInputEditText? = null
//
//    private lateinit var zoomScheduledViewModel: ZoomScheduledViewModel
//
//    var getMeetingList = ArrayList<CurrentMeetingListModel.Meeting>()
//
//    var mContext : Context? = null
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if(mContext ==null){
//            mContext = context.applicationContext
//        }
//        try {
//            toolBarClickListener = context as ToolBarClickListener
//        }catch(e : Exception){
//            Log.i(TAG,"Exception $e")
//        }
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        localDBHelper = LocalDBHelper(requireActivity())
//        var user = localDBHelper.viewUser()
//        adminId = user[0].ADMIN_ID
//
//        Global.screenState = "staffhomepage"
//        toolBarClickListener?.setToolbarName("Current Live Class")
//
//        // Inflate the layout for this fragment
//        zoomScheduledViewModel = ViewModelProviders.of(
//            this,
//            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
//        )[ZoomScheduledViewModel::class.java]
//
//
//        // Inflate the layout for this fragment
//        _binding = FragmentCurrentMeetingBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        constraintLayoutContent = binding.constraintLayoutContent
//        constraintEmpty = binding.constraintEmpty
//        imageViewEmpty = binding.imageViewEmpty
//        textEmpty = binding.textEmpty
//        Glide.with(this)
//            .load(R.drawable.ic_empty_state_live)
//            .into(imageViewEmpty!!)
//        shimmerViewContainer = binding.shimmerViewContainer
//
//        spinnerAcademic = binding.spinnerAcademic
//
//        recyclerViewVideo = binding.recyclerViewVideo
//        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())
//
//        spinnerAcademic?.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long
//            ) {
//                aCCADEMICID = getYears[position].aCCADEMICID
//                getMeetingListByAdmin(aCCADEMICID)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }
//        initFunction()
//    }
//
//    private fun initFunction() {
//        zoomScheduledViewModel.getYearClassExam(adminId)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//
//                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
//                            var years = Array(getYears.size) { "" }
//                            for (i in getYears.indices) {
//                                years[i] = getYears[i].aCCADEMICTIME
//                            }
//                            if (spinnerAcademic != null) {
//                                val adapter = ArrayAdapter(
//                                    requireActivity(),
//                                    android.R.layout.simple_spinner_dropdown_item,
//                                    years
//                                )
//                                spinnerAcademic?.adapter = adapter
//                            }
//                            intiSdk()
//                            Log.i(TAG, "initFunction SUCCESS")
//                        }
//                        Status.ERROR -> {
//
//                            Log.i(TAG, "initFunction ERROR")
//                        }
//                        Status.LOADING -> {
//
//                            Log.i(TAG, "initFunction LOADING")
//                        }
//                    }
//                }
//            })
//    }
//
//
//    private fun getMeetingListByAdmin(aCCADEMICID: Int) {
//        zoomScheduledViewModel.getMeetingListByAdmin(aCCADEMICID,adminId)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            shimmerViewContainer?.visibility = View.GONE
//                            val response = resource.data?.body()!!
//                            getMeetingList = response.meetingList as ArrayList<CurrentMeetingListModel.Meeting>
//                            if(getMeetingList.isNotEmpty()){
//                                recyclerViewVideo?.visibility = View.VISIBLE
//                                constraintEmpty?.visibility = View.GONE
//                                if (isAdded) {
//                                    recyclerViewVideo!!.adapter =
//                                        CurrentMeetingAdapter(
//                                            this,
//                                            getMeetingList,
//                                            requireActivity(),
//                                            TAG
//                                        )
//                                }
//                            }else{
//                                recyclerViewVideo?.visibility = View.GONE
//                                constraintEmpty?.visibility = View.VISIBLE
//                                Glide.with(this)
//                                    .load(R.drawable.ic_empty_progress_report)
//                                    .into(imageViewEmpty!!)
//
//                                textEmpty?.text =  resources.getString(R.string.no_results)
//                            }
//
//                            Log.i(TAG,"getSubjectList SUCCESS")
//                        }
//                        Status.ERROR -> {
//                            constraintEmpty?.visibility = View.VISIBLE
//                            recyclerViewVideo?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.GONE
//
//                            Glide.with(this)
//                                .load(R.drawable.ic_no_internet)
//                                .into(imageViewEmpty!!)
//                            textEmpty?.text =  resources.getString(R.string.no_internet)
//                            Log.i(TAG,"getSubjectList ERROR")
//                        }
//                        Status.LOADING -> {
//                            recyclerViewVideo?.visibility = View.GONE
//                            constraintEmpty?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.VISIBLE
//                            getMeetingList = ArrayList<CurrentMeetingListModel.Meeting>()
//                            Glide.with(this)
//                                .load(R.drawable.ic_empty_progress_report)
//                                .into(imageViewEmpty!!)
//
//                            textEmpty?.text =  resources.getString(R.string.loading)
//                            Log.i(TAG,"getSubjectList LOADING")
//                        }
//                    }
//                }
//            })
//
//    }
//
//
//    class CurrentMeetingAdapter(
//        var liveClassMeetingListener : LiveClassMeetingListener,
//        var getMeetingList: ArrayList<CurrentMeetingListModel.Meeting>,
//        var mContext: Context,
//        var TAG: String) : RecyclerView.Adapter<CurrentMeetingAdapter.ViewHolder>() {
//
//        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var textViewSubject: TextView = view.findViewById(R.id.textViewSubject)
//            var textViewClass: TextView = view.findViewById(R.id.textViewClass)
//            var textTotalStudents: TextView = view.findViewById(R.id.textTotalStudents)
//            var textTotalAttended: TextView = view.findViewById(R.id.textTotalAttended)
//            var textMeetingStartTime: TextView = view.findViewById(R.id.textMeetingStartTime)
//            var textClassBy: TextView = view.findViewById(R.id.textClassBy)
//
//            var shapeImageView: ShapeableImageView = view.findViewById(R.id.shapeImageView)
//
//            var imageViewMore  : ImageView = view.findViewById(R.id.imageViewMore)
//
//            var buttonJoinLive  : AppCompatButton = view.findViewById(R.id.buttonJoinLive)
//        }
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val itemView = LayoutInflater.from(parent.context)
//                .inflate(R.layout.current_meeting_adapter, parent, false)
//            return ViewHolder(itemView)
//        }
//
//        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.textViewSubject.text = getMeetingList[position].sUBJECTNAME
//            holder.textViewClass.text = "Class : ${getMeetingList[position].cLASSNAME}"
//
////            holder.textViewClass.text = "Class : ${getMeetingList[position].cLASSNAME}"
//
//            holder.textTotalStudents.text = "Total Students : "+getMeetingList[position].tOTALSTUDENT
//            holder.textTotalAttended.text = "Total Attended Students : "+getMeetingList[position].tOTALATTENDSTUDENT
//
//            if(!getMeetingList[position].zOOMSTARTDATE.isNullOrBlank()) {
//                val date: Array<String> =
//                    getMeetingList[position].zOOMSTARTDATE.split("T".toRegex()).toTypedArray()
//                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
//                holder.textMeetingStartTime.text = "Live Started Time : ${Utils.formattedDateWords(dddd)}"
//            }else{
//                holder.textMeetingStartTime.text = "Live Started Time : "
//            }
//
//            holder.textClassBy.text = "Class Taken by : "+getMeetingList[position].zOOMCREATEDBY
//
//
//            //  holder.textViewClass.setText(feedlist.get(position).getCLASS_NAME());
//
//            //     holder.textTotalStudents.setText("Total Students : "+feedlist.get(position).getTOTAL_STUDENT());
//            //            holder.textTotalAttended.setText("Total Attended Student  : "+feedlist.get(position).getTOTAL_ATTEND_STUDENT());
//            //
//            //            String[] date=feedlist.get(position).getZOOM_START_DATE().split("T");
//            //            long datestr= Global.longconversion1(date[1]);
//            //            String st_dat1=Global.FormateTimeMap0(datestr);
//            //
//            //            holder.textMeetingStartTime.setText("Live Started Time : "+st_dat1);
//            //            holder.textClassBy.setText("Class Taken by "+feedlist.get(position).getZOOM_CREATED_BY());
//
//            when (getMeetingList[position].sUBJECTICON) {
//                "English" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_english)
//                        .into(holder.shapeImageView)
//                }
//                "Chemistry" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_chemistry)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_chemistry)
//                        .into(holder.shapeImageView)
//                }
//                "Biology" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_bio)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_biology)
//                        .into(holder.shapeImageView)
//                }
//                "Maths" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_maths)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_maths)
//                        .into(holder.shapeImageView)
//                }
//                "Hindi" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_maths)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_hindi)
//                        .into(holder.shapeImageView)
//                }
//                "Physics" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_physics)
//                        .into(holder.shapeImageView)
//                }
//                "Malayalam" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_malayalam)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_malayalam)
//                        .into(holder.shapeImageView)
//                }
//                "Arabic" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_arabic)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_arabic)
//                        .into(holder.shapeImageView)
//                }
//                "Accountancy" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_accounts)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_accountancy)
//                        .into(holder.shapeImageView)
//                }
//                "Social Science" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_social)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_social)
//                        .into(holder.shapeImageView)
//                }
//                "Economics" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_econonics)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_economics)
//                        .into(holder.shapeImageView)
//                }
//                "BasicScience" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_bio)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_biology)
//                        .into(holder.shapeImageView)
//                }
//                "Computer" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_computer)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_computer)
//                        .into(holder.shapeImageView)
//                }
//                "General" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color100_computer)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_computer)
//                        .into(holder.shapeImageView)
//                }
//            }
//
//            holder.buttonJoinLive.setOnClickListener(View.OnClickListener {
//                liveClassMeetingListener.onStartLiveClass(getMeetingList[position])
//            })
//
//        }
//
//        override fun getItemCount(): Int {
//            return getMeetingList.size
//        }
//
//    }
//
//
//
//    fun intiSdk() {
//        zoomSDK = ZoomSDK.getInstance()
//        val params = ZoomSDKInitParams()
//        params.appKey = Credentials.SDK_KEY // TODO: Retrieve your SDK key and enter it here
//
//        params.appSecret = Credentials.SDK_SECRET // TODO: Retrieve your SDK secret and enter it here
//        params.domain = Credentials.SDK_DOMAIN
//        params.enableLog = true
//
//        val listener: ZoomSDKInitializeListener = object : ZoomSDKInitializeListener {
//            /**
//             * @param errorCode [us.zoom.sdk.ZoomError.ZOOM_ERROR_SUCCESS] if the SDK has been initialized successfully.
//             */
//            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {
//
//            }
//            override fun onZoomAuthIdentityExpired() {
//
//            }
//        }
//        zoomSDK?.initialize(requireActivity(), listener, params)
//
////        zoomSDK?.initialize(requireActivity(), Credentials.SDK_KEY, Credentials.SDK_SECRET, this)
////        val zoomSDKInitParams = ZoomSDKInitParams()
////        zoomSDKInitParams.appKey = Credentials.SDK_KEY
////        zoomSDKInitParams.appSecret = Credentials.SDK_SECRET
////        zoomSDKInitParams.domain = Credentials.SDK_DOMAIN
////        ZoomSDK.getInstance().initialize(requireActivity(), this, zoomSDKInitParams)
////        ZoomSDK.getInstance().addAuthenticationListener(this)
//    }
//
//    override fun onClick(v: View) {}
//    override fun onZoomSDKLoginResult(result: Long) {}
//    override fun onZoomSDKLogoutResult(result: Long) {}
//    override fun onZoomIdentityExpired() {}
//    override fun onMeetingStatusChanged(meetingStatus: MeetingStatus, i: Int, i1: Int) {}
//    override fun onMeetingParameterNotification(p0: MeetingParameter?) {
//        Log.i(TAG, "onMeetingParameterNotification!")
//    }
//
//    override fun onZoomSDKInitializeResult(i: Int, i1: Int) {
//        Log.i(TAG, "SDK Initialized!")
//
//    }
//
//    private fun doLoginToZoom() {
//       // ZoomSDK.getInstance().loginWithZoom(email, password)
//    }
//
//    override fun onZoomAuthIdentityExpired() {}
//    override fun onStartLiveClass(meeting: CurrentMeetingListModel.Meeting) {
//        JoinMeetingHelperJava().joinMeeting(
//            requireActivity(), ZoomSDK.getInstance(),
//            meeting.zOOMMEETINGID,
//            meeting.zOOMMEETINGPASSWORD, "Admin"
//        )
//    }
//
//}
//
//interface LiveClassMeetingListener{
//
//    fun onStartLiveClass(meeting: CurrentMeetingListModel.Meeting)
//}
//
//
