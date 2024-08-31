//package info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.graphics.Color
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.appcompat.widget.AppCompatButton
//import androidx.cardview.widget.CardView
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import androidx.viewpager.widget.ViewPager
//import com.bumptech.glide.Glide
//import com.facebook.shimmer.ShimmerFrameLayout
//import com.google.android.material.tabs.TabLayout
//import info.passdaily.camrelconvertapp.R
//import info.passdaily.camrelconvertapp.databinding.StudyMaterialFragmentBinding
//import info.passdaily.camrelconvertapp.model.*
//import info.passdaily.camrelconvertapp.services.Global
//import info.passdaily.camrelconvertapp.services.Global.Companion.subjectId
//import info.passdaily.camrelconvertapp.services.Status
//import info.passdaily.camrelconvertapp.services.Utils
//import info.passdaily.camrelconvertapp.services.ViewModelFactory
//import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
//import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
//import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
//import us.zoom.sdk.*
//
//
//@Suppress("DEPRECATION")
//@SuppressLint("NotifyDataSetChanged")
//class JoinLiveInit : Fragment(),ItemClickListener, MeetingServiceListener, ZoomSDKInitializeListener
//, ZoomSDKAuthenticationListener, InMeetingServiceListener {
//    var nameTest = "new"
//
//    var TAG = "JoinLiveInit"
//    //subjetc_url= Global.url+"OnlineExam/GetSubjectByClass?ClassId="+student2.class_id+"&StudentId="+student2.stu_id;
//    private var _binding: StudyMaterialFragmentBinding? = null
//    private val binding get() = _binding!!
//
//    var zoomSDK: ZoomSDK? = null
//
//    // zoomListUrl = Global.url + "OnlineExam/LiveClassList?AccademicId=" + student2.accademic_id + "&ClassId=" + student2.class_id + "&SubjectId=" + ssid + "
//    var tabLayout: TabLayout? = null
//    var viewPager: ViewPager? = null
//    var recyclerViewMaterialList: RecyclerView? = null
//    var recyclerView: RecyclerView? = null
//    private lateinit var viewModel: JoinLiveInitViewModel
//    var STUDENTID = 0
//    var CLASSID = 0
//    var ACADEMICID = 0
//
//    var constraintEmpty: ConstraintLayout? =null
//    var imageViewEmpty: ImageView? =null
//    var textEmpty : TextView? =null
//
//    var textViewName : TextView? =null
//    var mContext : Context? =null
//
//    var shimmerViewContainer : ShimmerFrameLayout? =null
//    var zLIVECLASSID = 0
//    var aCCADEMICID = 0
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
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        Global.currentPage = 5
//        Global.screenState = "landingpage"
//        viewModel = ViewModelProviders.of(
//            this,
//            ViewModelFactory(ApiClient(NetworkLayer.services))
//        )[JoinLiveInitViewModel::class.java]
//
//        _binding = StudyMaterialFragmentBinding.inflate(inflater, container, false)
//        return binding.root
//      //  return inflater.inflate(R.layout.study_material_fragment, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        var studentDBHelper = StudentDBHelper(requireActivity())
//        var student = studentDBHelper.getStudentById(Global.studentId)
//        STUDENTID = student.STUDENT_ID
//        ACADEMICID = student.ACCADEMIC_ID
//        CLASSID = student.CLASS_ID
//
//        textViewName = binding.textView32
//        textViewName?.text = "Join Live"
//
//        ///load subjects
//        recyclerView = binding.tabLayout
//        recyclerView?.layoutManager =
//            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
//      //  viewModel.getSubjects(student.CLASS_ID, student.STUDENT_ID)
//        constraintEmpty = binding.constraintEmpty
//        imageViewEmpty = binding.imageViewEmpty
//        textEmpty = binding.textEmpty
//        Glide.with(requireActivity())
//            .load(R.drawable.ic_empty_icon_zoom)
//            .into(imageViewEmpty!!)
//        textEmpty?.text = "No List Founded"
//
//
//        Log.i(TAG, "STUDENT_ID " + student.STUDENT_ID)
//        Log.i(TAG, "ACCADEMIC_ID " + student.ACCADEMIC_ID)
//        Log.i(TAG, "CLASS_ID " + student.CLASS_ID)
//        Log.i(TAG, "subjectID $subjectId")
//
////AccademicId=7&ClassId=2&StudentId=1
//
///////////////load study Material list
//        recyclerViewMaterialList = binding.recyclerView
//        recyclerViewMaterialList?.layoutManager = LinearLayoutManager(requireActivity())
//
//        shimmerViewContainer = binding.shimmerViewContainer
//
//        intiFunction()
//        onClick(-1)
//        intiSdk()
//    }
//
//    private fun intiFunction() {
//        viewModel.getSubjects(CLASSID, STUDENTID)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            if (isAdded) {
//                                recyclerView?.adapter = SubjectAdapter(
//                                    this,
//                                    response.subjects as ArrayList<SubjectsModel.Subject>,
//                                    mContext!!
//                                )
//                            }
//                        }
//                        Status.ERROR -> {
//                            Log.i(TAG, "Error ${Status.ERROR}")
////                            onRetry(
////                                "",""
////                                resources.getString(R.string.no_internet),
////                                resources.getString(R.string.internet_error_message)
////                            )
//                        }
//                        Status.LOADING -> {
//                            Log.i(TAG, "resource ${resource.status}")
//                            Log.i(TAG, "message ${resource.message}")
//                        }
//                    }
//                }
//            })
//    }
//
//
//    class SubjectAdapter(
//        val itemClickListener: ItemClickListener,
//        var subjects: ArrayList<SubjectsModel.Subject>,
//        var context: Context
//    ) :
//        RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {
//        var index = 0
//
//        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var textSubject: TextView = view.findViewById(R.id.textAssignmentName)
//            var cardView: CardView = view.findViewById(R.id.cardView)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val itemView = LayoutInflater.from(parent.context)
//                .inflate(R.layout.subjet_adapter, parent, false)
//            return ViewHolder(itemView)
//        }
//
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.textSubject.text = subjects[position].sUBJECTNAME
//            holder.cardView.setOnClickListener {
//                itemClickListener.onClick(subjects[position].sUBJECTID)
//                index = position;
//                notifyDataSetChanged()
//            }
//
//            if (index == position) {
//                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.green_400))
//                holder.textSubject.setTextColor(Color.parseColor("#FFFFFFFF"))
//            } else {
//                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.white))
//                holder.textSubject.setTextColor(Color.parseColor("#18b962"))
//            }
//        }
//
//        override fun getItemCount(): Int {
//            return subjects.size
//        }
//
//    }
//
//    /////////////////////get StudyMaterial
//    class JoinLiveAdapter(var itemClickListener: ItemClickListener, var liveClassList: List<LiveClassListModel.LiveClass>, var context: Context) :
//        RecyclerView.Adapter<JoinLiveAdapter.ViewHolder>() {
//
//
//        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var cardView9: CardView = view.findViewById(R.id.cardViewStatus)
//            var textSubjectName: TextView = view.findViewById(R.id.textSubjectName)
//            var imageSubject: ImageView = view.findViewById(R.id.imageViewSubject)
//
//
//            var textstartDate: TextView = view.findViewById(R.id.textAssignmentName)
//            var textEndDate: TextView = view.findViewById(R.id.textEndDate)
//            var textClassName: TextView = view.findViewById(R.id.textClassName)
//            var textStudent: TextView = view.findViewById(R.id.textStudent)
//
//            var buttonJoinLive: AppCompatButton = view.findViewById(R.id.buttonJoinLive)
//
//
//        }
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val itemView = LayoutInflater.from(parent.context)
//                .inflate(R.layout.live_list_adapter, parent, false)
//            return ViewHolder(itemView)
//        }
//        @SuppressLint("SetTextI18n")
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
////            holder.textstartDate.text ="Started : "+ liveClassList[position].zOOMSTARTDATE
////            holder.textEndDate.text ="Ended  : "+ liveClassList[position].zOOMENDDATE
//            holder.textClassName.text ="Created : "+liveClassList[position].cREATEDBY
//            holder.textStudent.text ="Total Student : "+liveClassList[position].tOTALSTUDENT
//
//            if(!liveClassList[position].zOOMSTARTDATE.isNullOrBlank()) {
//                val date: Array<String> =
//                    liveClassList[position].zOOMSTARTDATE.split("T".toRegex()).toTypedArray()
//                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
//                holder.textstartDate.text = "Starts : " + Utils.formattedTime(dddd)
//            }else{
//                holder.textstartDate.text = "Starts : "
//            }
//            if(!liveClassList[position].zOOMENDDATE.isNullOrBlank()) {
//                val date1: Array<String> =
//                    liveClassList[position].zOOMENDDATE.split("T".toRegex()).toTypedArray()
//                val ddddd: Long = Utils.longconversion(date1[0] + " " + date1[1])
//                holder.textEndDate.text = "Ended : " + Utils.formattedTime(ddddd)
//            } else{
//                holder.textEndDate.text = "Ended : "
//            }
//
//            holder.textSubjectName.text =liveClassList[position].sUBJECTNAME
//            when (liveClassList[position].sUBJECTICON) {
//                "English.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_english))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_english))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_english)
//                        .into(holder.imageSubject)
//                }
//                "Chemistry.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_chemistry))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_chemistry))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_chemistry)
//                        .into(holder.imageSubject)
//                }
//                "Biology.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_bio))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_bio))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_biology)
//                        .into(holder.imageSubject)
//                }
//                "Maths.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_maths))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_maths))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_maths)
//                        .into(holder.imageSubject)
//                }
//                "Hindi.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_hindi))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_hindi))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_hindi)
//                        .into(holder.imageSubject)
//                }
//                "Physics.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_physics))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_physics))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_physics)
//                        .into(holder.imageSubject)
//                }
//                "Malayalam.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_malayalam))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_malayalam))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_malayalam)
//                        .into(holder.imageSubject)
//                }
//                "Arabic.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_arabic))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_arabic))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_arabic)
//                        .into(holder.imageSubject)
//                }
//                "Accountancy.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_accounts))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_accounts))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_accountancy)
//                        .into(holder.imageSubject)
//                }
//                "Social.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_social))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_social))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_social)
//                        .into(holder.imageSubject)
//                }
//                "Economics.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_econonics))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_econonics))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_economics)
//                        .into(holder.imageSubject)
//                }
//                "BasicScience.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_bio))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_bio))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_biology)
//                        .into(holder.imageSubject)
//                }
//                "Computer.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_computer))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_computer))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_computer)
//                        .into(holder.imageSubject)
//                }
//
//                "General.png" -> {
//                    holder.cardView9.setCardBackgroundColor(context.resources.getColor(R.color.color100_computer))
//                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_computer))
//                    Glide.with(context)
//                        .load(R.drawable.ic_study_computer)
//                        .into(holder.imageSubject)
//                }
//            }
//
//
//            if (liveClassList[position].zOOMMEETINGID  != "0"
//                && liveClassList[position].zOOMMEETINGPASSWORD  != "0"
//                && liveClassList[position].zOOMMEETINGSTATUS  == 6) {
//                holder.buttonJoinLive.setBackgroundResource(R.drawable.round_green400)
//                holder.buttonJoinLive.setTextAppearance(context, R.style.RoundedCornerButtonGreen400)
//                holder.buttonJoinLive.isEnabled = true
//                holder.buttonJoinLive.text = "Join Live Class"
//            }else  if (liveClassList[position].zOOMMEETINGID  != "0"
//                && liveClassList[position].zOOMMEETINGPASSWORD  != "0"
//                && liveClassList[position].zOOMMEETINGSTATUS  == 2) {
//                holder.buttonJoinLive.setBackgroundResource(R.drawable.round_gray500)
//                holder.buttonJoinLive.setTextAppearance(context, R.style.RoundedCornerButtonGray500)
//                holder.buttonJoinLive.isEnabled = false
//                holder.buttonJoinLive.text = "This Class Ended"
//            }else  if (liveClassList[position].zOOMMEETINGID  == "0"
//                && liveClassList[position].zOOMMEETINGPASSWORD  == "0"
//                && liveClassList[position].zOOMMEETINGSTATUS  == 0) {
//                holder.buttonJoinLive.setBackgroundResource(R.drawable.round_gray500)
//                holder.buttonJoinLive.setTextAppearance(context, R.style.RoundedCornerButtonGray500)
//                holder.buttonJoinLive.isEnabled = false
//                holder.buttonJoinLive.text = "Class Not Started"
//            }
//
//
//            holder.buttonJoinLive.setOnClickListener {
////                val intent  = Intent(context, ZoomLiveActivity::class.java)
////                intent.putExtra("liveClass", 0)
////                intent.putExtra("mainClass", 0)
////                intent.putExtra("zOOMMEETINGID", liveClassList[position].zOOMMEETINGID)
////                intent.putExtra("zOOMMEETINGPASSWORD", liveClassList[position].zOOMMEETINGPASSWORD)
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                context.startActivity(intent)
//
//                itemClickListener.onMeetingAttend(liveClassList[position].zLIVECLASSID,
//                    liveClassList[position].aCCADEMICID)
//
//                var NO_BUTTON_PARTICIPANTS = 0
//                var MEETING_DETAILS = false
//                var INVITE_OPTION = false
//                var UNMUTE_AUDIO = false
//
//                //"MEETING_DETAILS":1,"PARTICIPANT_DETAILS":0,"INVITE_OPTION":1,"UNMUTE_AUDIO":0},
//                if (liveClassList[position].pARTICIPANTDETAILS == 1) {
//                    NO_BUTTON_PARTICIPANTS = 8
//                }
//
//                if (liveClassList[position].mEETINGDETAILS == 1) {
//                    MEETING_DETAILS = true
//                }
//
//                if (liveClassList[position].iNVITEOPTION == 1) {
//                    INVITE_OPTION = true
//                }
//
//                if (liveClassList[position].uNMUTEAUDIO == 1) {
//                    UNMUTE_AUDIO = true
//                }
//
//                ////////////////////////condition here
//                if (liveClassList[position].pARTICIPANTDETAILS == 0 && liveClassList[position].mEETINGDETAILS == 0
//                    && liveClassList[position].iNVITEOPTION == 0 && liveClassList[position].uNMUTEAUDIO == 0) {
//
//                    JoinMeetingHelper().joinMeeting(
//                        context, ZoomSDK.getInstance(),
//                        liveClassList[position].zOOMMEETINGID,
//                        liveClassList[position].zOOMMEETINGPASSWORD,
//                        Global.studentName
//                    )
//                } else {
//                    JoinMeetingHelper().joinMeeting(
//                        context,
//                        ZoomSDK.getInstance(),
//                        liveClassList[position].zOOMMEETINGID,
//                        liveClassList[position].zOOMMEETINGPASSWORD,
//                        Global.studentName,
//                        MEETING_DETAILS,
//                        NO_BUTTON_PARTICIPANTS,
//                        UNMUTE_AUDIO,
//                        INVITE_OPTION
//                    )
//                }
//            }
//        }
//
//        override fun getItemCount(): Int {
//            return liveClassList.size
//        }
//
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
////        _binding = null
//    }
//
//    override fun onClick(id: Int) {
//      Log.i(TAG, "onClick callback $id")
//        viewModel.getLiveClassList(ACADEMICID,
//            CLASSID,
//            id,
//            STUDENTID)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//
//                            if(response.liveClassList.isNotEmpty()){
//                                constraintEmpty?.visibility = View.GONE
//                                recyclerViewMaterialList?.visibility = View.VISIBLE
//                                shimmerViewContainer?.visibility = View.GONE
//                                if (isAdded) {
//                                    recyclerViewMaterialList?.adapter =
//
//                                        JoinLiveAdapter( this,
//                                            response.liveClassList, mContext!!
//                                        )
//                                }
//                            }else{
//                                constraintEmpty?.visibility = View.VISIBLE
//                                recyclerViewMaterialList?.visibility = View.GONE
//                                shimmerViewContainer?.visibility = View.GONE
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
//                            recyclerViewMaterialList?.visibility = View.GONE
//                            shimmerViewContainer?.visibility = View.GONE
//
//                            if (isAdded) {
//                                Glide.with(mContext!!)
//                                    .load(R.drawable.ic_no_internet)
//                                    .into(imageViewEmpty!!)
//                            }
//                            textEmpty?.text =requireActivity().resources.getString(R.string.no_internet)
//
//                            Log.i(TAG, "Status.ERROR ${Status.ERROR}")
//                        }
//                        Status.LOADING -> {
//                            if (isAdded) {
//                                Glide.with(mContext!!)
//                                    .load(R.drawable.ic_empty_state_live)
//                                    .into(imageViewEmpty!!)
//                            }
//                            textEmpty?.text =  requireActivity().resources.getString(R.string.loading)
//                            shimmerViewContainer?.visibility = View.VISIBLE
//                            recyclerViewMaterialList?.visibility = View.GONE
//                            constraintEmpty?.visibility = View.GONE
//
//                        }
//                    }
//                }
//            })
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
//        _binding =  null
//        Log.i(TAG,"onDestroy ")
//    }
//
//
//    private fun intiSdk() {
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
////        zoomSDK?.initialize(requireActivity(), Credentials.SDK_KEY, Credentials.SDK_SECRET, this)
////        val zoomSDKInitParams = ZoomSDKInitParams()
////        zoomSDKInitParams.appKey = Credentials.SDK_KEY
////        zoomSDKInitParams.appSecret = Credentials.SDK_SECRET
////        zoomSDKInitParams.domain = Credentials.SDK_DOMAIN
////        ZoomSDK.getInstance().initialize(requireActivity(), this, zoomSDKInitParams)
////        ZoomSDK.getInstance().addAuthenticationListener(this)
//    }
//
//
//    override fun onMeetingStatusChanged(meetingStatus: MeetingStatus?, p1: Int, p2: Int) {
//        if (meetingStatus == MeetingStatus.MEETING_STATUS_CONNECTING) {
//            meetingAttend(zLIVECLASSID,aCCADEMICID,"MEETING_STATUS_CONNECTING")
//        }
//        if (meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING) {
//            meetingAttend(zLIVECLASSID,aCCADEMICID,"MEETING_STATUS_INMEETING")
//        }
//        if (meetingStatus == MeetingStatus.MEETING_STATUS_DISCONNECTING) {
//            meetingAttend(zLIVECLASSID,aCCADEMICID,"MEETING_STATUS_DISCONNECTING")
//        }
//        if (meetingStatus == MeetingStatus.MEETING_STATUS_FAILED) {
//            meetingAttend(zLIVECLASSID,aCCADEMICID,"MEETING_STATUS_FAILED")
//        }
//    }
//
//    override fun onMeetingParameterNotification(p0: MeetingParameter?) {
//        TODO("Not yet implemented")
//    }
//
////    override fun onMeetingParameterNotification(response: MeetingParameter?) {
////        Log.i(TAG, "onMeetingParameterNotification $response")
////    }
//
//
//    override fun onMeetingAttend(zLIVECLASSID : Int,aCCADEMICID : Int) {
//        this.zLIVECLASSID = zLIVECLASSID
//        this.aCCADEMICID = aCCADEMICID
//        meetingAttend(zLIVECLASSID,aCCADEMICID,"6")
//    }
//
//
//
//    private fun meetingAttend(zLIVECLASSID: Int, aCCADEMICID: Int, mStatus: String) {
//        viewModel.getMeetingAttend(zLIVECLASSID,aCCADEMICID, STUDENTID,mStatus)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            Log.i(TAG, "RESULT $response")
//                        }
//                        Status.ERROR -> {
//                            Log.i(TAG, "Error ${Status.ERROR}")
//                        }
//                        Status.LOADING -> {
//                            Log.i(TAG, "resource ${resource.status}")
//                            Log.i(TAG, "message ${resource.message}")
//                        }
//                    }
//                }
//            })
//    }
//
//    override fun onZoomSDKInitializeResult(p0: Int, p1: Int) {
//        Log.i(TAG,"onZoomSDKInitializeResult")
//    }
//
//
//    override fun onZoomSDKLoginResult(p0: Long) {
//        Log.i(TAG,"onZoomSDKLoginResult")
//    }
//
//    override fun onZoomSDKLogoutResult(p0: Long) {
//        Log.i(TAG,"onZoomSDKLogoutResult")
//    }
//
//    override fun onZoomIdentityExpired() {
//        Log.i(TAG,"onZoomIdentityExpired")
//    }
//
//    override fun onZoomAuthIdentityExpired() {
//        Log.i(TAG,"onZoomAuthIdentityExpired")
//    }
//
//
//    override fun onMeetingNeedPasswordOrDisplayName(
//        p0: Boolean,
//        p1: Boolean,
//        p2: InMeetingEventHandler?
//    ) {
//        Log.i(TAG,"onMeetingNeedPasswordOrDisplayName")
//    }
//
//    override fun onWebinarNeedRegister(p0: String?) {
//        TODO("Not yet implemented")
//    }
//
////    override fun onWebinarNeedRegister() {
////        Log.i(TAG,"onWebinarNeedRegister")
////    }
//
////    override fun onWebinarNeedRegister(response: String?) {
////        Log.i(TAG, "onWebinarNeedRegister $response")
////    }
//
//
//    override fun onJoinWebinarNeedUserNameAndEmail(p0: InMeetingEventHandler?) {
//        Log.i(TAG,"onJoinWebinarNeedUserNameAndEmail")
//    }
//
//    override fun onMeetingNeedCloseOtherMeeting(p0: InMeetingEventHandler?) {
//        TODO("Not yet implemented")
//    }
//
////    override fun onMeetingNeedColseOtherMeeting(p0: InMeetingEventHandler?) {
////        Log.i(TAG,"onMeetingNeedColseOtherMeeting")
////    }
//
////    override fun onMeetingNeedCloseOtherMeeting(response: InMeetingEventHandler?) {
////        Log.i(TAG, "onMeetingNeedCloseOtherMeeting $response")
////    }
//
//
//    override fun onMeetingFail(p0: Int, p1: Int) {
//        Log.i(TAG,"onMeetingFail")
//    }
//
//    override fun onMeetingLeaveComplete(p0: Long) {
//        Log.i(TAG,"onMeetingLeaveComplete")
//    }
//
//    override fun onMeetingUserJoin(p0: MutableList<Long>?) {
//        Log.i(TAG,"onMeetingUserJoin")
//    }
//
//    override fun onMeetingUserLeave(p0: MutableList<Long>?) {
//        Log.i(TAG,"onMeetingUserLeave")
//    }
//
//    override fun onMeetingUserUpdated(p0: Long) {
//        Log.i(TAG,"onMeetingUserUpdated")
//    }
//
//    override fun onMeetingHostChanged(p0: Long) {
//        Log.i(TAG,"onMeetingHostChanged")
//    }
//
//    override fun onMeetingCoHostChanged(p0: Long) {
//        Log.i(TAG,"onMeetingCoHostChanged")
//    }
//
//    override fun onMeetingCoHostChange(p0: Long, p1: Boolean) {
//        TODO("Not yet implemented")
//    }
//
////    override fun onMeetingCoHostChange(response: Long, p1: Boolean) {
////        Log.i(TAG, "onMeetingCoHostChange $response")
////    }
//
//    override fun onActiveVideoUserChanged(p0: Long) {
//        Log.i(TAG,"onActiveVideoUserChanged")
//    }
//
//    override fun onActiveSpeakerVideoUserChanged(p0: Long) {
//        Log.i(TAG,"onActiveSpeakerVideoUserChanged")
//    }
//
//    override fun onHostVideoOrderUpdated(p0: MutableList<Long>?) {
//        TODO("Not yet implemented")
//    }
//
//    override fun onFollowHostVideoOrderChanged(p0: Boolean) {
//        TODO("Not yet implemented")
//    }
//
////    override fun onHostVideoOrderUpdated(response: MutableList<Long>?) {
////        Log.i(TAG, "onHostVideoOrderUpdated $response")
////    }
//
////    override fun onFollowHostVideoOrderChanged(response: Boolean) {
////        Log.i(TAG, "onFollowHostVideoOrderChanged $response")
////    }
//
//    override fun onSpotlightVideoChanged(p0: Boolean) {
//        Log.i(TAG,"onSpotlightVideoChanged")
//    }
//
//    override fun onSpotlightVideoChanged(p0: MutableList<Long>?) {
//        Log.i(TAG,"onSpotlightVideoChanged")
//    }
//
////    override fun onUserVideoStatusChanged(p0: Long) {
////        Log.i(TAG,"onUserVideoStatusChanged")
////    }
//
////    override fun onSpotlightVideoChanged(response: MutableList<Long>?) {
////        Log.i(TAG, "onSpotlightVideoChanged $response")
////    }
//
//
//    override fun onUserVideoStatusChanged(p0: Long, p1: InMeetingServiceListener.VideoStatus?) {
//        Log.i(TAG,"onUserVideoStatusChanged")
//    }
//
//    override fun onUserNetworkQualityChanged(p0: Long) {
//        Log.i(TAG,"onUserNetworkQualityChanged")
//    }
//
//    override fun onSinkMeetingVideoQualityChanged(p0: VideoQuality?, p1: Long) {
//        Log.i(TAG,"onSinkMeetingVideoQualityChanged")
//    }
//
////    override fun onSinkMeetingVideoQualityChanged(response: VideoQuality?, p1: Long) {
////        Log.i(TAG, "onSinkMeetingVideoQualityChanged $response")
////    }
//
//    override fun onMicrophoneStatusError(p0: InMeetingAudioController.MobileRTCMicrophoneError?) {
//        Log.i(TAG,"onMicrophoneStatusError")
//    }
//
////    override fun onUserAudioStatusChanged(p0: Long) {
////        Log.i(TAG,"onUserAudioStatusChanged")
////    }
//
//
//    override fun onUserAudioStatusChanged(p0: Long, p1: InMeetingServiceListener.AudioStatus?) {
//        Log.i(TAG,"onUserAudioStatusChanged")
//    }
//
//    override fun onHostAskUnMute(p0: Long) {
//        Log.i(TAG,"onHostAskUnMute")
//    }
//
//    override fun onHostAskStartVideo(p0: Long) {
//        Log.i(TAG,"onHostAskStartVideo")
//    }
//
//    override fun onUserAudioTypeChanged(p0: Long) {
//        Log.i(TAG,"onUserAudioTypeChanged")
//    }
//
//    override fun onMyAudioSourceTypeChanged(p0: Int) {
//        Log.i(TAG,"onMyAudioSourceTypeChanged")
//    }
//
//    override fun onLowOrRaiseHandStatusChanged(p0: Long, p1: Boolean) {
//        Log.i(TAG,"onLowOrRaiseHandStatusChanged")
//    }
//
////    override fun onMeetingSecureKeyNotification(p0: ByteArray?) {
////        Log.i(TAG,"onMeetingSecureKeyNotification")
////    }
//
//
//    override fun onChatMessageReceived(p0: InMeetingChatMessage?) {
//        Log.i(TAG,"onChatMessageReceived")
//    }
//
//    override fun onChatMsgDeleteNotification(p0: String?, p1: ChatMessageDeleteType?) {
//        Log.i(TAG,"onChatMsgDeleteNotification")
//    }
//
//    override fun onShareMeetingChatStatusChanged(p0: Boolean) {
//        Log.i(TAG,"onShareMeetingChatStatusChanged")
//    }
//
////    override fun onChatMsgDeleteNotification(p0: String?, p1: ChatMessageDeleteType?) {
////        Log.i(TAG,"onChatMsgDeleteNotification")
////    }
////
////    override fun onShareMeetingChatStatusChanged(p0: Boolean) {
////        Log.i(TAG,"onShareMeetingChatStatusChanged")
////    }
//
//    override fun onSilentModeChanged(p0: Boolean) {
//        Log.i(TAG,"onSilentModeChanged")
//    }
//
//    override fun onFreeMeetingReminder(p0: Boolean, p1: Boolean, p2: Boolean) {
//        Log.i(TAG,"onFreeMeetingReminder")
//    }
//
//    override fun onMeetingActiveVideo(p0: Long) {
//        Log.i(TAG,"onMeetingActiveVideo")
//    }
//
//    override fun onSinkAttendeeChatPriviledgeChanged(p0: Int) {
//        Log.i(TAG,"onSinkAttendeeChatPriviledgeChanged")
//    }
//
//    override fun onSinkAllowAttendeeChatNotification(p0: Int) {
//        Log.i(TAG,"onSinkAllowAttendeeChatNotification")
//    }
//
//
//
//    override fun onSinkPanelistChatPrivilegeChanged(p0: InMeetingChatController.MobileRTCWebinarPanelistChatPrivilege?) {
//        Log.i(TAG,"onSinkPanelistChatPrivilegeChanged")
//    }
//
//    override fun onUserNameChanged(p0: Long, p1: String?) {
//        Log.i(TAG,"onUserNameChanged")
//    }
//
//
//    override fun onUserNamesChanged(p0: MutableList<Long>?) {
//        Log.i(TAG,"onUserNamesChanged")
//    }
//
//    override fun onFreeMeetingNeedToUpgrade(p0: FreeMeetingNeedUpgradeType?, p1: String?) {
//        Log.i(TAG,"onFreeMeetingNeedToUpgrade")
//    }
//
//    override fun onFreeMeetingUpgradeToGiftFreeTrialStart() {
//        Log.i(TAG,"onFreeMeetingUpgradeToGiftFreeTrialStart")
//    }
//
//    override fun onFreeMeetingUpgradeToGiftFreeTrialStop() {
//        Log.i(TAG,"onFreeMeetingUpgradeToGiftFreeTrialStop")
//    }
//
//    override fun onFreeMeetingUpgradeToProMeeting() {
//        Log.i(TAG,"onFreeMeetingUpgradeToProMeeting")
//    }
//
//
//
//
//    override fun onClosedCaptionReceived(p0: String?, p1: Long) {
//        Log.i(TAG,"onClosedCaptionReceived")
//    }
//
//    override fun onRecordingStatus(p0: InMeetingServiceListener.RecordingStatus?) {
//        Log.i(TAG,"onRecordingStatus")
//    }
//
//    override fun onLocalRecordingStatus(p0: Long, p1: InMeetingServiceListener.RecordingStatus?) {
//        Log.i(TAG,"onLocalRecordingStatus")
//    }
//
//    override fun onInvalidReclaimHostkey() {
//        Log.i(TAG,"onInvalidReclaimHostkey")
//    }
//
//    override fun onPermissionRequested(p0: Array<out String>?) {
//        Log.i(TAG,"onPermissionRequested")
//    }
//
//    override fun onAllHandsLowered() {
//        Log.i(TAG,"onAllHandsLowered")
//    }
//
//    override fun onLocalVideoOrderUpdated(p0: MutableList<Long>?) {
//        Log.i(TAG,"onLocalVideoOrderUpdated")
//    }
//
//
//}
//
//
//
//interface ItemClickListener{
//    fun onClick(id: Int)
//
//    fun onMeetingAttend(zLIVECLASSID : Int,aCCADEMICID : Int)
//}