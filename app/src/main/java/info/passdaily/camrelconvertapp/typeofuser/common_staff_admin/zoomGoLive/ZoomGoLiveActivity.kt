//package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoomGoLive
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.DialogInterface
//import android.content.Intent
//import android.content.res.ColorStateList
//import android.graphics.Color
//import android.os.Bundle
//import android.util.Log
//import android.view.*
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.AppCompatButton
//import androidx.appcompat.widget.SwitchCompat
//import androidx.appcompat.widget.Toolbar
//import androidx.cardview.widget.CardView
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.facebook.shimmer.ShimmerFrameLayout
//import com.google.android.material.imageview.ShapeableImageView
//import com.zipow.videobox.view.mm.z
//import info.passdaily.camrelconvertapp.R
//import info.passdaily.camrelconvertapp.databinding.ActivityZoomGoLiveBinding
//import info.passdaily.camrelconvertapp.model.*
//import info.passdaily.camrelconvertapp.services.Status
//import info.passdaily.camrelconvertapp.services.Utils
//import info.passdaily.camrelconvertapp.services.ViewModelFactory
//import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
//import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
//import info.passdaily.camrelconvertapp.services.initsdk.ZoomMeetingUISettingHelper
//import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
//import info.passdaily.camrelconvertapp.services.retrofit.RetrofitClientStaff
//import info.passdaily.camrelconvertapp.services.retrofit.RetrofitClientZoom
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.home.StudentActivity
//import info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout.Credentials
//import org.json.JSONException
//import org.json.JSONObject
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import us.zoom.sdk.*
//
//
//@Suppress("DEPRECATION")
//class ZoomGoLiveActivity : AppCompatActivity(),SelectedClickListener, ZoomSDKInitializeListener,
//    ZoomSDKAuthenticationListener, MeetingServiceListener {
//
//    var TAG = "ZoomGoLiveActivity"
//
//    private lateinit var binding: ActivityZoomGoLiveBinding
//
//    private lateinit var zoomGoLiveViewModel: ZoomGoLiveViewModel
//
//    private lateinit var localDBHelper : LocalDBHelper
//    var recyclerViewLive : RecyclerView? = null
//
//    var accademicListModel = ArrayList<ZoomGoLiveDetailsModel.AccademicDetail>()
//    var classSubjectModel = ArrayList<ClassSubjectDetailModel.ClassSubjectDetail>()
//    var zoomLoginKeyDetails = ArrayList<ZoomGoLiveDetailsModel.ZoomLoginKeyDetail>()
//    var adminId = 0
//
//    var mZoomSDK: ZoomSDK? = null
//    //  var inMeetingService: InMeetingService? = null
//    var instantMeetingOptions: InstantMeetingOptions? = null
//    var meetingService: MeetingService? = null
//    var constraintLayoutOptions : CardView? = null
//
//    var switchMeetingDetails : SwitchCompat? = null
//    var switchParticipants : SwitchCompat? = null
//    var switchUnmuteAudio : SwitchCompat? = null
//    var switchInviteOption : SwitchCompat? = null
//
//
//    var selectedValues = ArrayList<Int>()
//
//    var email = ""
//    var password = ""
//    var meetingClassId = ""
//    var meetingNumber  : String? = null
//    var meetingPassword  : String? = null
//    var meetingUrl : String? = null
//
//    var aCCADEMICID = 0
//
//    var buttonSubmit : AppCompatButton? = null
//    var spinnerAccedamic: Spinner? = null
//
//    val checkedItem = intArrayOf(-1)
//    var listItems = arrayOf("")
//
//    var constraintEmpty: ConstraintLayout? = null
//    var imageViewEmpty: ImageView? = null
//    var textEmpty: TextView? = null
//    var shimmerViewContainer: ShimmerFrameLayout? = null
//
//    var myItemShouldBeEnabled = false
//
//    var zakAccessToken : String? = null
//
//    var getUserId : String? = null
//    var getUserName : String? = null
//    var getUserType   = 0
//
//    var toolbar: Toolbar? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        localDBHelper = LocalDBHelper(this)
//        var user = localDBHelper.viewUser()
//        adminId = user[0].ADMIN_ID
//        // Inflate the layout for this fragment
//        zoomGoLiveViewModel = ViewModelProviders.of(
//            this,
//            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
//        )[ZoomGoLiveViewModel::class.java]
//
//        binding = ActivityZoomGoLiveBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        toolbar = binding.toolbar
//        if (toolbar != null) {
//            setSupportActionBar(toolbar)
//            supportActionBar!!.title = "Live Class"
//            // Customize the back button
//            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
//            supportActionBar!!.setDisplayShowTitleEnabled(true)
//            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//            //   toolbar.setTitle("About-Us");
//            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
//                onBackPressed()
//            })
//        }
//        spinnerAccedamic = binding.spinnerAccedamic
//
//        recyclerViewLive = binding.recyclerViewLive
//        if(resources.getBoolean(R.bool.is_tab)) {
//            recyclerViewLive?.layoutManager = GridLayoutManager(this,2)
//        } else {
//            recyclerViewLive?.layoutManager = LinearLayoutManager(this)
//        }
//
//        constraintEmpty = binding.constraintEmpty
//        imageViewEmpty = binding.imageViewEmpty
//        textEmpty = binding.textEmpty
//        Glide.with(this)
//            .load(R.drawable.ic_empty_state_assignment)
//            .into(imageViewEmpty!!)
//        shimmerViewContainer = binding.shimmerViewContainer
//
//        constraintLayoutOptions = binding.constraintLayoutOptions
//        switchMeetingDetails = binding.switchMeetingDetails
//        switchParticipants = binding.switchParticipants
//        switchUnmuteAudio = binding.switchUnmuteAudio
//        switchInviteOption = binding.switchInviteOption
//
//        buttonSubmit = binding.buttonSubmit
//        spinnerAccedamic?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//
//            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                // showToast(getCountries()[position] + " selected")
//                aCCADEMICID = accademicListModel[position].aCCADEMICID
//                getClassDetails(aCCADEMICID)
//
//            }
//            override fun onNothingSelected(p0: AdapterView<*>?) {}
//        }
//
//        buttonSubmit?.setOnClickListener(View.OnClickListener {
//            finalStartMeeting()
//            Log.i(TAG, "email $email")
//            Log.i(TAG, "password $password")
//        })
//
//        initFunction()
//
//
//        Utils.setStatusBarColor(this)
//    }
//
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.golive_main, menu)
//        return true
//    }
//
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        if (id == R.id.action_license) {
//            // AlertDialog builder instance to build the alert dialog
//            val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this@ZoomGoLiveActivity)
//
//            // title of the alert dialog
//            alertDialog.setTitle("Purchased Private Zoom Login Keys")
//            alertDialog.setSingleChoiceItems(listItems, checkedItem[0],
//                DialogInterface.OnClickListener { _, which ->
//                    checkedItem[0] = which
//                    Log.i(TAG, "listItems[which] " + listItems[which])
//                    email = zoomLoginKeyDetails[which].zUSERNAME
//                    password = zoomLoginKeyDetails[which].zPASSOWRD
//
//                    // dialog.dismiss();
//                })
//
//            alertDialog.setPositiveButton(
//                "Select"
//            ) { dialog, which ->
//                dialog.dismiss()
//                ///Todo : changes
//                intiSdk()
//            }
//
//            // already selected item
//            alertDialog.setNegativeButton(
//                "Clear"
//            ) { _, _ ->
//                checkedItem[0] = -1
//                email = Credentials.SDK_EMAIL
//                password = Credentials.SDK_PASSWORD
//                Log.i(TAG, "Credentials.SDK_EMAIL $email")
//                Log.i(TAG, "Credentials.SDK_PASSWORD $password")
//                intiSdk()
//            }
//            // create and build the AlertDialog instance
//            // with the AlertDialog builder instance
//            val customAlertDialog = alertDialog.create()
//
//            // show the alert dialog when the button is clicked
//            customAlertDialog.show()
//            val buttonbackground: Button = customAlertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
//            buttonbackground.setTextColor(Color.BLACK)
//
//            val buttonbackground1: Button = customAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
//            buttonbackground1.setTextColor(Color.BLACK)
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    private fun intiSdk() {
//        mZoomSDK = ZoomSDK.getInstance()
//        val params = ZoomSDKInitParams()
//        params.appKey = Credentials.SDK_KEY // TODO: Retrieve your SDK key and enter it here
//        params.appSecret = Credentials.SDK_SECRET // TODO: Retrieve your SDK secret and enter it here
//        //   params.jwtToken = Credentials.JWT_TOKEN // Todo :Pass in your Meeting SDK JWT
//        params.domain = Credentials.SDK_DOMAIN
//        // params.jwtToken = Credentials.JWT_CLIENT_ID
//        params.enableLog = true
//
//        val listener: ZoomSDKInitializeListener = object : ZoomSDKInitializeListener {
//            /**
//             * @param errorCode [us.zoom.sdk.ZoomError.ZOOM_ERROR_SUCCESS] if the SDK has been initialized successfully.
//             */
//            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {
//                if (errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
//                    Log.i(TAG,  "Failed to initialize Zoom SDK. Error: $errorCode, internalErrorCode=$internalErrorCode")
//                } else {
////                    Log.i(TAG, "Initialize Zoom SDK successfully.")
////                    Log.i(TAG, "tryAutoLoginZoom ${mZoomSDK?.tryAutoLoginZoom()}")
////                    Log.i(TAG, "ZOOM_API_ERROR_SUCCESS ${ZoomApiError.ZOOM_API_ERROR_SUCCESS}")
////                    Log.i(TAG, "ZOOM_API_INVALID_STATUS ${ZoomApiError.ZOOM_API_INVALID_STATUS}")
////                    Log.i(TAG, "ZOOM_API_ERROR_EMAIL_LOGIN_IS_DISABLED ${ZoomApiError.ZOOM_API_ERROR_EMAIL_LOGIN_IS_DISABLED}")
////                    Log.i(TAG, "ZOOM_API_ERROR_FAILED_NULLPOINTER ${ZoomApiError.ZOOM_API_ERROR_FAILED_NULLPOINTER}")
////                    Log.i(TAG, "ZOOM_API_ERROR_FAILED_WRONGPARAMETERS ${ZoomApiError.ZOOM_API_ERROR_FAILED_WRONGPARAMETERS}")
////                    Log.i(TAG, "ZOOM_API_ERROR_FAILED_CLIENT_INCOMPATIBLE ${ZoomApiError.ZOOM_API_ERROR_FAILED_CLIENT_INCOMPATIBLE}")
//                    if (mZoomSDK?.tryAutoLoginZoom() == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
//                        // UserLoginCallback.getInstance().addListener(this);
////                        showProgressPanel(true);
//                        Log.i(TAG, "ZoomApiError.ZOOM_API_ERROR_SUCCESS ${ZoomApiError.ZOOM_API_ERROR_SUCCESS}")
//                    } else {
//
////                        showProgressPanel(false);
//                    }
//                }
//            }
//            override fun onZoomAuthIdentityExpired() {
//                Log.i(TAG, "onZoomAuthIdentityExpired")
//            }
//        }
//
//        mZoomSDK?.initialize(this, listener, params)
//
////        mZoomSDK?.initialize(this, Credentials.SDK_KEY, Credentials.SDK_SECRET, this)
////        val zoomSDKInitParams = ZoomSDKInitParams()
////        zoomSDKInitParams.appKey = Credentials.SDK_KEY
////        zoomSDKInitParams.appSecret = Credentials.SDK_SECRET
////        zoomSDKInitParams.domain = Credentials.SDK_DOMAIN
////        ZoomSDK.getInstance().initialize(this, this, zoomSDKInitParams)
////        ZoomSDK.getInstance().addAuthenticationListener(this)
//    }
//
//    fun getZakToken(bearer: String,userEmail : String) {
//        Log.i("DashboardDesigner ", "plus $bearer")
//        val apiInterface = RetrofitClientZoom.create().getZakToken(bearer, userEmail, "zak")
//        apiInterface.enqueue(object : Callback<ZakAccessTokenModel> {
//            override fun onResponse(
//                call: Call<ZakAccessTokenModel>, response: Response<ZakAccessTokenModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i("DashboardDesigner", "response " + response.body())
//                    zakAccessToken = response.body()?.token
//                    startMeeting()
//                }
//            }
//
//            override fun onFailure(call: Call<ZakAccessTokenModel>, t: Throwable) {
//                Log.i("DashboardDesigner", "t $t")
//            }
//        })
//
//    }
//
//    fun getZoomSdkUser(bearer: String,userEmail : String) {
//        Log.i(TAG, "plus $bearer")
//        val apiInterface = RetrofitClientZoom.create().getZoomSdkUser(bearer, userEmail)
//        apiInterface.enqueue(object : Callback<ZoomSdkUserModel> {
//            override fun onResponse(
//                call: Call<ZoomSdkUserModel>, response: Response<ZoomSdkUserModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    var result = response.body()
//                    getUserId = result?.id
//                    getUserName = result?.firstName
//                    getUserType  = result?.type!!
//                }
//            }
//
//            override fun onFailure(call: Call<ZoomSdkUserModel>, t: Throwable) {
//                Log.i("DashboardDesigner", "t $t")
//            }
//        })
//
//    }
//
//
//    fun getMeetingDetails(bearer: String,meetingId : String) {
//
//
////        zoomGoLiveViewModel.getLoginDetails(
////            "anveradmin",
////            ".anver$2014"
////        ).observe(this, Observer {
////            it?.let { resource ->
////                when (resource.status) {
////                    Status.SUCCESS -> {
////                        val response = resource.data?.body()!!
////                        Log.i(TAG, "response $response")
////                    }
////                    Status.ERROR -> {
////
////                        Log.i(TAG, "Error ${Status.ERROR}")
////                    }
////                    Status.LOADING -> {
////
////                        Log.i(TAG, "resource ${resource.status}")
////                        Log.i(TAG, "message ${resource.message}")
////                    }
////                }
////
////            }
////        })
//
//        //zoomStartEndStatus
//
//
//
////        val apiInterface = RetrofitClientStaff.create().loginTestPageStaff(
////            "anveradmin",
////            ".anver$2014")
////        apiInterface.enqueue(object : Callback<LoginStaffModel> {
////            override fun onResponse(
////                call: Call<LoginStaffModel>, response: Response<LoginStaffModel>
////            ) {
////                if (response.isSuccessful) {
////                    var result = response.body()
////                    Log.i(TAG, "Response result $result")
////
////                }
////            }
////            override fun onFailure(call: Call<LoginStaffModel>, t: Throwable) {
////                Log.i("DashboardDesigner", "t $t")
////            }
////        })
//
//
////        Log.i(TAG, "plus $bearer")
////        val apiInterface = RetrofitClientZoom.create().getMeetingDetails(bearer, meetingId)
////        apiInterface.enqueue(object : Callback<ZoomMeetingDetailsModel> {
////            override fun onResponse(
////                call: Call<ZoomMeetingDetailsModel>, response: Response<ZoomMeetingDetailsModel>
////            ) {
////                if (response.isSuccessful) {
////                    Log.i(TAG, "response " + response.body())
////                    var result = response.body()
////
////                    meetingUrl = result?.joinUrl
////                    meetingNumber = result?.id.toString()
////                    meetingPassword = result?.password
////
////                    var liveStatus = "Accademic/StartLiveMeeting"
////                    liveStatusMethod(
////                        liveStatus,
////                        "6",
////                        meetingUrl!!,
////                        meetingClassId,
////                        //"MEETING_STATUS"
////                        "ZOOM_MEETING_STATUS"
////                    )
////                }
////            }
////
////            override fun onFailure(call: Call<ZoomMeetingDetailsModel>, t: Throwable) {
////                Log.i("DashboardDesigner", "t $t")
////            }
////        })
//
//    }
//
//    fun startMeeting() {
//
////        MeetingHostHelper(
////            this,
////            ZoomSDK.getInstance(),
////            object : MeetingHostHelper.MeetingStatusListener {
////                override fun onMeetingRunning() {
////
////                }
////
////                override fun onMeetingFailed() {
////                    Toast.makeText(this@ZoomGoLiveActivity, "Could not host a meeting.", Toast.LENGTH_SHORT)
////                        .show()
////                }
////            }).createInstantMeeting()
//
//        val opts = ZoomMeetingUISettingHelper.getMeetingOptions()
//
//        // val params = StartMeetingParamsWithoutLogin()
//
//        val params = StartMeetingParams4NormalUser()
//
//
//        Log.i(TAG, "getUserId ${Credentials.SDK_EMAIL}")
//        Log.i(TAG, "getUserType $getUserType")
//        Log.i(TAG, "getUserName $getUserName")
//        Log.i(TAG, "zakAccessToken $zakAccessToken")
//
//
//
//        val startParams = StartMeetingParamsWithoutLogin().apply {
//            userId = Credentials.SDK_EMAIL
//            userType = getUserType
//            displayName = getUserName;
//            zoomAccessToken = zakAccessToken
//            //   meetingNo = ZoomSDK.getInstance().inMeetingService.currentMeetingNumber.toString()
//        }
//
////        meetingUrl = mZoomSDK?.meetingService?.currentMeetingUrl
////        meetingNumber = mZoomSDK?.inMeetingService?.currentMeetingNumber.toString()
////        meetingPassword = mZoomSDK?.inMeetingService?.meetingPassword
//
////        var liveStatus = "Accademic/StartLiveMeeting"
////        liveStatusMethod(liveStatus, "6", meetingUrl!!, meetingClassId, "ZOOM_MEETING_STATUS")
//        if (switchUnmuteAudio?.isChecked!!) {
//            mZoomSDK?.inMeetingService?.allowParticipantsToUnmuteSelf(false)
//        }
//
//        val result = meetingService?.startMeetingWithParams(this, startParams, StartMeetingOptions())
//        Log.i(TAG, "result $result")
//        if (result == MeetingError.MEETING_ERROR_SUCCESS) {
//            // The SDK will attempt to join the meeting.
//            Log.i(TAG, "start Meeting ${meetingService?.currentMeetingUrl}")
//        }
//
//
/////////todo
//
//
////        Log.i(TAG, "startMeeting")
////        if (mZoomSDK?.isLoggedIn!!) {
////            meetingService = mZoomSDK?.meetingService
////            meetingService!!.addListener(this)
////            instantMeetingOptions = InstantMeetingOptions()
////            inMeetingService = mZoomSDK?.inMeetingService
////            ///new code add here
////            if (switchUnmuteAudio?.isChecked!!) {
////                inMeetingService?.allowParticipantsToUnmuteSelf(false)
////            }
////            //   inMeetingService.isParticipantsUnmuteSelfAllowed();
////            val startInstantMeeting = meetingService!!.startInstantMeeting(this, instantMeetingOptions)
////
////            Log.i(TAG, String.format("[startMeeting] result of startInstantMeeting: %d", startInstantMeeting))
////
////
////            return
////        }
////        Toast.makeText(this, "Please Login First", Toast.LENGTH_SHORT).show()
////        Log.i(TAG, "Please Login First")
//    }
//
//    private fun liveStatusMethod(liveStatus: String, meetingStatusValue: String, meetingUrl: String,
//                                 meetingClassId: String, meetingStatusKey : String) {
//
//        var MEETING_DETAILS = 0
//        if (switchMeetingDetails?.isChecked!!) {
//            MEETING_DETAILS = 1
//        }
//        var PARTICIPANT_DETAILS = 0
//        if (switchParticipants?.isChecked!!) {
//            PARTICIPANT_DETAILS = 1
//        }
//        var UNMUTE_AUDIO = 0
//        if (switchUnmuteAudio?.isChecked!!) {
//            UNMUTE_AUDIO = 1
//        }
//        var INVITE_OPTION = 0
//        if (switchInviteOption?.isChecked!!) {
//            INVITE_OPTION = 1
//        }
//
//        // hashMap.put("ACCADEMIC_ID",  s_aid);
//        //        hashMap.put("ADMIN_ID", Global.Admin_id);
//        //        hashMap.put("ZOOM_MEETING_ID",  meetingNumber);
//        //        hashMap.put("ZOOM_MEETING_PASSWORD",  meetingPassword);
//        //        hashMap.put(str5, str2);
//        //        hashMap.put("ZOOM_MEETING_LINK", str3);
//        //        hashMap.put("CLASS_SUBJECT_ID", str4);
//        //
//        //        hashMap.put("MEETING_DETAILS", String.valueOf(MEETING_DETAILS));
//        //        hashMap.put("PARTICIPANT_DETAILS", String.valueOf(PARTICIPANT_DETAILS));
//        //        hashMap.put("INVITE_OPTION", String.valueOf(INVITE_OPTION));
//        //        hashMap.put("UNMUTE_AUDIO", String.valueOf(UNMUTE_AUDIO));
//
//        val jsonObject = JSONObject()
//        try {
//            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
//            jsonObject.put("ADMIN_ID", adminId)
//            jsonObject.put("ZOOM_MEETING_ID", meetingNumber)
//            jsonObject.put("ZOOM_MEETING_PASSWORD", meetingPassword)
//            jsonObject.put( meetingStatusKey, meetingStatusValue)
//            jsonObject.put("ZOOM_MEETING_LINK", meetingUrl)
//            jsonObject.put("CLASS_SUBJECT_ID", meetingClassId)
//
//            jsonObject.put("MEETING_DETAILS", MEETING_DETAILS)
//            jsonObject.put("PARTICIPANT_DETAILS", PARTICIPANT_DETAILS)
//            jsonObject.put("INVITE_OPTION", INVITE_OPTION)
//            jsonObject.put("UNMUTE_AUDIO", UNMUTE_AUDIO)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        Log.i(TAG,"jsonObject $jsonObject")
//
//
//        val apiInterface = RetrofitClientStaff.create().zoomStartEndStatus(
//            liveStatus,
//            jsonObject.toString())
//        apiInterface.enqueue(object : Callback<String> {
//            override fun onResponse(
//                call: Call<String>, response: Response<String>
//            ) {
//                if (response.isSuccessful) {
//                    var result = response.body()
//                    Log.i(TAG, "Response result $result")
//
//                }
//            }
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                Log.i("DashboardDesigner", "t $t")
//            }
//        })
//
////      //  val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
////        zoomGoLiveViewModel.getCommonPostFun(liveStatus,jsonObject.toString())
////            .observe(this@ZoomGoLiveActivity, Observer {
////                it?.let { resource ->
////                    Log.i(TAG,"resource $resource")
////                    when (resource.status) {
////                        Status.SUCCESS -> {
////                            val response = resource.data?.body()!!
////                            Log.i(TAG,"response $response")
////                        }
////                        Status.ERROR -> {
////                            Toast.makeText(
////                                this@ZoomGoLiveActivity,
////                                "Please try again after sometime",
////                                Toast.LENGTH_SHORT
////                            ).show()
////                        }
////                        Status.LOADING -> {
////                            Log.i(TAG,"loading")
////                        }
////                    }
////                }
////            })
//
//
//    }
//
//    fun finalStartMeeting() {
//        meetingNumber = ""
//        meetingPassword = ""
//        meetingUrl = ""
//        meetingClassId = ""
//        Log.i(TAG, "mylist " + selectedValues.size)
//        for (i in selectedValues.indices) {
//            meetingClassId += classSubjectModel[selectedValues[i]].cLASSSUBJECTID +"~"
//            Log.i(TAG, "meetingClassId $meetingClassId")
//        }
//        meetingService = ZoomSDK.getInstance().meetingService
//        meetingService?.addListener(this)
//
//
////        MeetingHostHelper(
////            this,
////            ZoomSDK.getInstance(),
////            object : MeetingHostHelper.MeetingStatusListener {
////                override fun onMeetingRunning() {
////                    ZoomSDK.getInstance().inMeetingService.currentMeetingID
////                    meetingNumber = ZoomSDK.getInstance().inMeetingService.currentMeetingNumber.toString()
////                    ZoomSDK.getInstance().inMeetingService.currentMeetingTopic
////                    meetingPassword = ZoomSDK.getInstance().inMeetingService.meetingPassword
////                    meetingUrl = ZoomSDK.getInstance().inMeetingService.currentMeetingUrl
////                    //Toast.makeText(ZoomTest.this, "ID: ${idPwd.first} PWD: ${idPwd.second}", Toast.LENGTH_SHORT).show();
////                    Log.i(TAG, "ID:  PWD: $meetingNumber $meetingPassword")
//        getZakToken("Bearer ${Credentials.JWT_TOKEN}",Credentials.SDK_EMAIL)
////                }
////
////                override fun onMeetingFailed() {
////                    Toast.makeText(this@ZoomGoLiveActivity, "Could not host a meeting.", Toast.LENGTH_SHORT)
////                        .show()
////                }
////            }).createInstantMeeting()
//
//
//
//
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        Log.i(TAG, "onBackPressed")
//    }
//
//    private fun initFunction() {
//        zoomGoLiveViewModel.getLiveClassDetails(adminId)
//            .observe(this, Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            myItemShouldBeEnabled = true
////                            shimmerViewContainer?.visibility = View.GONE
////                            constraintEmpty?.visibility = View.GONE
//                            val response = resource.data?.body()!!
//
//                            val accademic: ArrayList<String> = ArrayList<String>()
//                            accademicListModel = response.accademicDetails as ArrayList<ZoomGoLiveDetailsModel.AccademicDetail>
//                            for (i in accademicListModel.indices) {
//                                accademic.add(accademicListModel[i].aCCADEMICTIME)
//                            }
//                            if (spinnerAccedamic != null) {
//                                val adapter = ArrayAdapter(
//                                    this,
//                                    android.R.layout.simple_spinner_dropdown_item,
//                                    accademic
//                                )
//                                spinnerAccedamic?.adapter = adapter
//                            }
//                            val zoomLoginDetails = response.zoomLoginDetails
//                            for (i in zoomLoginDetails.indices) {
//
//                                Credentials.SDK_EMAIL = zoomLoginDetails[i].zUSERNAME
//                                Credentials.SDK_PASSWORD = zoomLoginDetails[i].zPASSOWRD
//
//                                Credentials.SDK_KEY = zoomLoginDetails[i].zAPPKEY
//                                Credentials.SDK_SECRET = zoomLoginDetails[i].zAPPSECRETKEY
//
//                                Credentials.JWT_TOKEN = zoomLoginDetails[i].zJWTTOKEN
//
//                                email = Credentials.SDK_EMAIL
//                                password = Credentials.SDK_PASSWORD
//                            }
//                            zoomLoginKeyDetails = response.zoomLoginKeyDetails as ArrayList<ZoomGoLiveDetailsModel.ZoomLoginKeyDetail>
//                            listItems = Array(zoomLoginKeyDetails.size){""}
//                            for (i in zoomLoginKeyDetails.indices) {
//                                listItems[i] = "${zoomLoginKeyDetails[i].zKEYTITLE} \n ${zoomLoginKeyDetails[i].zUSERNAME}"
//                            }
//
//                            intiSdk()
//                            getZoomSdkUser("Bearer ${Credentials.JWT_TOKEN}",Credentials.SDK_EMAIL)
//                        }
//                        Status.ERROR -> {
//                            myItemShouldBeEnabled = false
////                            constraintEmpty?.visibility = View.VISIBLE
////                            shimmerViewContainer?.visibility = View.GONE
////
////                            Glide.with(this)
////                                .load(R.drawable.ic_no_internet)
////                                .into(imageViewEmpty!!)
////                            textEmpty?.text =  resources.getString(R.string.no_internet)
//                            Log.i(TAG, "Status.ERROR ${Status.ERROR}")
//                        }
//                        Status.LOADING -> {
//                            myItemShouldBeEnabled = false
////                            Glide.with(this)
////                                .load(R.drawable.ic_empty_progress_report)
////                                .into(imageViewEmpty!!)
////
////                            textEmpty?.text =  resources.getString(R.string.loading)
////                            shimmerViewContainer?.visibility = View.VISIBLE
////                            constraintEmpty?.visibility = View.GONE
//                        }
//                    }
//                }
//            })
//    }
//
//    //https://marketplace.zoom.us/docs/sdk/native-sdks/android/build-an-app/implement-features/   todo
//    //https://marketplace.zoom.us/develop/apps/1ZVMIU9ZQHCF0aZ_OehNVw/download  todo
//
//
//    private fun doLoginToZoom() {
//        Log.i(TAG, "email $email")
//        Log.i(TAG, "password $password")
//        ZoomSDK.getInstance().tryAutoLoginZoom()
////        ZoomSDK.getInstance().loginWithZoom(email, password)
//
////        val result = ZoomSDK.getInstance().loginWithZoom(email, password)
////
////        Log.i(TAG, "result $result")
////
////        if (result == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
////            // Request executed, listen for result to start meeting
////            ZoomSDK.getInstance().addAuthenticationListener(this)
////        }
//    }
//
//
//    private fun getClassDetails(aCCADEMICID : Int) {
//        zoomGoLiveViewModel.getClassDetails(adminId,aCCADEMICID)
//            .observe(this, Observer {
//                it?.let { resource ->
//                    Log.i(TAG,"resource $resource")
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            shimmerViewContainer?.visibility = View.GONE
//                           // constraintEmpty?.visibility = View.GONE
//                            val response = resource.data?.body()!!
//                            classSubjectModel = response.classSubjectDetails
//
//                            if(classSubjectModel.isNotEmpty()){
//                                recyclerViewLive?.visibility = View.VISIBLE
//                                constraintEmpty?.visibility = View.GONE
//                                recyclerViewLive?.adapter = ClassSubjectAdapter(this,classSubjectModel,this)
//
//                            }else{
//                                recyclerViewLive?.visibility = View.GONE
//                                constraintEmpty?.visibility = View.VISIBLE
//                                Glide.with(this)
//                                    .load(R.drawable.ic_empty_state_pta)
//                                    .into(imageViewEmpty!!)
//
//                                textEmpty?.text =  resources.getString(R.string.no_results)
//                            }
//                        }
//                        Status.ERROR -> {
//                            constraintEmpty?.visibility = View.VISIBLE
//                            shimmerViewContainer?.visibility = View.GONE
//                            recyclerViewLive?.visibility = View.GONE
//                            Glide.with(this)
//                                .load(R.drawable.ic_no_internet)
//                                .into(imageViewEmpty!!)
//                            textEmpty?.text =  resources.getString(R.string.no_internet)
//                            Log.i(TAG, "resource ${resource.status}")
//                        }
//                        Status.LOADING -> {
//                            shimmerViewContainer?.visibility = View.VISIBLE
//                            constraintEmpty?.visibility = View.GONE
//                            recyclerViewLive?.visibility = View.GONE
//                            classSubjectModel = ArrayList<ClassSubjectDetailModel.ClassSubjectDetail>()
//                            Glide.with(this)
//                                .load(R.drawable.ic_empty_progress_report)
//                                .into(imageViewEmpty!!)
//
//                            textEmpty?.text =  resources.getString(R.string.loading)
//
//                            Log.i(TAG, "resource ${resource.status}")
//                            Log.i(TAG, "message ${resource.message}")
//                        }
//                    }
//                }
//            })
//    }
//
//
//    class ClassSubjectAdapter(
//        var selectedClickListener : SelectedClickListener,
//        var classSubject: ArrayList<ClassSubjectDetailModel.ClassSubjectDetail>,
//        var mContext: Context) :
//        RecyclerView.Adapter<ClassSubjectAdapter.ViewHolder>() {
//
//        var mylist = ArrayList<Int>()
//        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var textSubjectName: TextView = view.findViewById(R.id.textSubjectName)
//            var textMark: TextView = view.findViewById(R.id.textMark)
//            var imageViewCheck : ImageView = view.findViewById(R.id.imageViewCheck)
//            var shapeImageView: ShapeableImageView = view.findViewById(R.id.shapeImageView)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val itemView = LayoutInflater.from(parent.context)
//                .inflate(R.layout.classsubject_adapter, parent, false)
//            return ViewHolder(itemView)
//        }
//
//        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//            holder.textSubjectName.text = classSubject[position].cLASSNAME
//            holder.textMark.text = classSubject[position].sUBJECTNAME
//
//            if (classSubject[position].selectedValue) {
//                // viewHolder.checkBox.setChecked(true);
//                holder.imageViewCheck.setImageResource(R.drawable.ic_checked_black)
//                mylist.add(position)
//
//            } else {
//                //viewHolder.checkBox.setChecked(false);
//                holder.imageViewCheck.setImageResource(R.drawable.ic_check_gray)
//                mylist.remove(position)
//            }
//
//            selectedClickListener.onViewClick(mylist)
//
//            holder.itemView.setOnClickListener {
//                classSubject[position].selectedValue = !classSubject[position].selectedValue
//                notifyItemChanged(position)
//            }
//
//            when (classSubject[position].sUBJECTNAME) {
//                "English" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_english_light)
////                    val colorInt: Int = mContext.resources.getColor(R.color.color100_physics)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_english)
//                        .into(holder.shapeImageView)
//                }
//                "Chemistry" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_chemistry_light)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_chemistry)
//                        .into(holder.shapeImageView)
//                }
//                "Biology" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_bio_light)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_biology)
//                        .into(holder.shapeImageView)
//                }
//                "Maths" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_maths_light)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_maths)
//                        .into(holder.shapeImageView)
//                }
//                "Hindi" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_hindi_light)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_hindi)
//                        .into(holder.shapeImageView)
//                }
//                "Physics" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_physics_light)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_physics)
//                        .into(holder.shapeImageView)
//                }
//                "Malayalam" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_malayalam_light)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_malayalam)
//                        .into(holder.shapeImageView)
//                }
//                "Arabic" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_arabic_light)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_arabic)
//                        .into(holder.shapeImageView)
//                }
//                "Accountancy" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_accounts_light)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_accountancy)
//                        .into(holder.shapeImageView)
//                }
//                "Social Science" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_social_light)
//
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_social)
//                        .into(holder.shapeImageView)
//                }
//                "Economics" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_economics_light)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_economics)
//                        .into(holder.shapeImageView)
//                }
//                "BasicScience" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_bio_light)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_biology)
//                        .into(holder.shapeImageView)
//                }
//                "Computer" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_computer_light)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_computer)
//                        .into(holder.shapeImageView)
//                }
//                "General" -> {
//                    val colorInt: Int = mContext.resources.getColor(R.color.color_computer_light)
//                    val csl = ColorStateList.valueOf(colorInt)
//                    holder.shapeImageView.strokeColor = csl
//                    Glide.with(mContext)
//                        .load(R.drawable.ic_study_computer)
//                        .into(holder.shapeImageView)
//                }
//            }
//
//        }
//
//        override fun getItemCount(): Int {
//            return classSubject.size
//        }
//
//        override fun getItemId(position: Int): Long {
//            return position.toLong()
//        }
//
//        override fun getItemViewType(position: Int): Int {
//            return position
//        }
//
//    }
//
//    override fun onViewClick(selectedValue: ArrayList<Int>) {
//        this.selectedValues = selectedValue
//
//        Log.i(TAG,"selectedValues ${selectedValues.size}")
//        if(selectedValues.isNotEmpty()){
//            buttonSubmit?.setBackgroundResource(R.drawable.round_green600)
//            buttonSubmit?.setTextAppearance(
//                this,
//                R.style.RoundedCornerButtonGreen600
//            )
//            buttonSubmit?.isEnabled = true
//            constraintLayoutOptions?.visibility = View.VISIBLE
//            switchMeetingDetails?.visibility = View.VISIBLE
//            switchParticipants?.visibility = View.VISIBLE
//            switchUnmuteAudio?.visibility = View.VISIBLE
//            switchInviteOption?.visibility = View.VISIBLE
//        }else{
//            buttonSubmit?.setBackgroundResource(R.drawable.round_gray500)
//            buttonSubmit?.setTextAppearance(
//                this,
//                R.style.RoundedCornerButtonGray300
//            )
//            buttonSubmit?.isEnabled = false
//            constraintLayoutOptions?.visibility = View.GONE
//            switchMeetingDetails?.visibility = View.GONE
//            switchParticipants?.visibility = View.GONE
//            switchUnmuteAudio?.visibility = View.GONE
//            switchInviteOption?.visibility = View.GONE
//        }
//
//
//    }
//
//
//    override fun onZoomSDKInitializeResult(p0: Int, p1: Int) {
//        Log.i(TAG, "onZoomSDKInitializeResult $p0 $p1")
//        doLoginToZoom()
//
//    }
//
//    override fun onZoomSDKLoginResult(result: Long) {
//
//        Log.i(TAG, "onZoomSDKLoginResult $result")
//
//        val str = if (result == 0L) "Logged in successfully." else "Username / Password do not match."
//        Log.i(TAG, "onZoomSDKLoginResult $str")
//    }
//
//
//    override fun onMeetingStatusChanged(meetingStatus: MeetingStatus?, i: Int, p2: Int) {
//
//
//        // Log.i(TAG,"meetingStatus $meetingStatus $i $p2")
//
//        if (meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING) {
//
////            Log.i(TAG,"MEETING_STATUS_INMEETING ${ZoomSDK.getInstance().inMeetingService.meetingPassword}")
////            Log.i(TAG,"MEETING_STATUS_INMEETING ${ZoomSDK.getInstance().meetingService.currentMeetingUrl}")
////            Log.i(TAG,"MEETING_STATUS_INMEETING ${ZoomSDK.getInstance().meetingService.currentRtcMeetingNumber}")
////
//            meetingNumber = ZoomSDK.getInstance().inMeetingService.currentMeetingNumber.toString()
//            meetingPassword = ZoomSDK.getInstance().inMeetingService.meetingPassword
//            meetingUrl = ZoomSDK.getInstance().inMeetingService.currentMeetingUrl
//
/////               getMeetingDetails("Bearer ${Credentials.JWT_TOKEN}", meetingNumber!!)
//
//            var liveStatus = "Accademic/StartLiveMeeting"
//            liveStatusMethod(
//                liveStatus,
//                "6",
//                meetingUrl!!,
//                meetingClassId,
//                //"MEETING_STATUS"
//                "ZOOM_MEETING_STATUS"
//            )
//
//
//            Log.i(TAG, "Meeting Connected")
//
//        }
//        if (meetingStatus == MeetingStatus.MEETING_STATUS_DISCONNECTING) {
//            Log.i(TAG, "Meeting Disconnected")
//            var liveStatus = "Accademic/EndMeeting"
//            liveStatusMethod(
//                liveStatus,
//                "MEETING_STATUS_DISCONNECTING",
//                meetingUrl!!,
//                meetingClassId,
//                "MEETING_STATUS"
//            )
//
//            /////refresh while meeting end
//            checkedItem[0] = -1
//            selectedValues.clear()
//            classSubjectModel = ArrayList<ClassSubjectDetailModel.ClassSubjectDetail>()
//            getClassDetails(aCCADEMICID)
//            switchMeetingDetails?.isChecked = false
//            switchParticipants?.isChecked = false
//            switchUnmuteAudio?.isChecked = false
//            switchInviteOption?.isChecked = false
//        }
//        if (meetingStatus == MeetingStatus.MEETING_STATUS_FAILED) {
//            var liveStatus ="Accademic/UpdateMeetingStatus"
//            liveStatusMethod(
//                liveStatus,
//                "MEETING_STATUS_FAILED",
//                meetingUrl!!,
//                meetingClassId,
//                "MEETING_STATUS"
//            )
//        }
//        if (meetingStatus == MeetingStatus.MEETING_STATUS_FAILED && i == 4) {
//            Toast.makeText(this, "Version of ZoomSDK is too low!", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//    override fun onMeetingParameterNotification(meetingParameter: MeetingParameter?) {
//  Log.i(TAG, "onZoomSDKLogoutResultonMeetingParameterNotification ")
//    }
//
//    override fun onZoomSDKLogoutResult(p0: Long) {
//        Log.i(TAG, "onZoomSDKLogoutResult ")
//    }
//
//    override fun onZoomIdentityExpired() {
//        Log.i(TAG, "onZoomIdentityExpired ")
//    }
//
//    override fun onZoomAuthIdentityExpired() {
//        Log.i(TAG, "onZoomAuthIdentityExpired ")
//    }
//
//
//
//    /* access modifiers changed from: protected */
//    override fun onStart() {
//        super.onStart()
//        Log.i(TAG, "onStart")
//    }
//
//    /* access modifiers changed from: protected */
//    override fun onStop() {
//        super.onStop()
//        Log.i(TAG, "onStop")
//    }
//
//    /* access modifiers changed from: protected */
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.i(TAG, "onDestroy")
//    }
//}
//
//
//interface SelectedClickListener {
//
//    fun onViewClick(selectedValue: ArrayList<Int>)
//}