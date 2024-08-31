//package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_current_meeting
//
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.AppCompatButton
//import androidx.appcompat.widget.Toolbar
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProviders
//import info.passdaily.camrelconvertapp.R
//import info.passdaily.camrelconvertapp.databinding.ActivityPrivateMeetingBinding
//import info.passdaily.camrelconvertapp.model.ZakAccessTokenModel
//import info.passdaily.camrelconvertapp.model.ZoomGoLiveDetailsModel
//import info.passdaily.camrelconvertapp.model.ZoomSdkUserModel
//import info.passdaily.camrelconvertapp.services.Status
//import info.passdaily.camrelconvertapp.services.Utils
//import info.passdaily.camrelconvertapp.services.ViewModelFactory
//import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
//import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
//import info.passdaily.camrelconvertapp.services.initsdk.ZoomMeetingUISettingHelper
//import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
//import info.passdaily.camrelconvertapp.services.retrofit.RetrofitClientZoom
//import info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout.Credentials
//import info.passdaily.camrelconvertapp.services.zoomsdk.MeetingHostHelperJava
//import info.passdaily.camrelconvertapp.services.zoomsdk.MeetingHostHelperJava.MeetingStatusListener
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoomGoLive.ZoomGoLiveViewModel
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import us.zoom.sdk.*
//
//@Suppress("DEPRECATION")
//class ZoomPrivateMeetingActivity : AppCompatActivity(), ZoomSDKInitializeListener,
//    ZoomSDKAuthenticationListener, MeetingServiceListener {
//    var TAG = "ZoomPrivateMeetingActivity"
////    var ZoomLoginDetails_url: String =
////        Global.url + "Accademic/LiveClassDetailsGet?AdminId=" + Global.Admin_id
//
//    private lateinit var localDBHelper : LocalDBHelper
//    var adminId = 0
//
//    private lateinit var binding: ActivityPrivateMeetingBinding
//    var zoomSDK: ZoomSDK? = null
//    var inMeetingService: InMeetingService? = null
//    var instantMeetingOptions: InstantMeetingOptions? = null
//    private lateinit var zoomGoLiveViewModel: ZoomGoLiveViewModel
//
//    var meetingService: MeetingService? = null
//
//    var zoomLoginKeyDetails = ArrayList<ZoomGoLiveDetailsModel.ZoomLoginKeyDetail>()
//    var email: String? = null
//    var password: String? = null
//    var buttonSubmit: AppCompatButton? = null
//    var meetingNumber = ""
//    var meetingPassword = ""
//    var meetingUrl = ""
//
//    var zakAccessToken : String? = null
//
//    var getUserId : String? = null
//    var getUserName : String? = null
//    var getUserType   = 0
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        localDBHelper = LocalDBHelper(this)
//        var user = localDBHelper.viewUser()
//        adminId = user[0].ADMIN_ID
//
//
//        zoomGoLiveViewModel = ViewModelProviders.of(
//            this,
//            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
//        )[ZoomGoLiveViewModel::class.java]
//
//        binding = ActivityPrivateMeetingBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        var toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        toolbar = binding.toolbar
//        if (toolbar != null) {
//            setSupportActionBar(toolbar)
//            supportActionBar!!.title = "Private Meeting"
//            supportActionBar!!.setDisplayShowTitleEnabled(true)
//            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//            //   toolbar.setTitle("About-Us");
//            toolbar.setNavigationOnClickListener { // perform whatever you want on back arrow click
//                onBackPressed()
//            }
//        }
//        buttonSubmit = binding.buttonSubmit
//        buttonSubmit?.setOnClickListener {
//
//            getZakToken("Bearer ${Credentials.JWT_TOKEN}",Credentials.SDK_EMAIL)
//
//            meetingService = ZoomSDK.getInstance().meetingService
//            meetingService?.addListener(this)
////            MeetingHostHelperJava(
////                this@ZoomPrivateMeetingActivity,
////                ZoomSDK.getInstance(),
////                object : MeetingStatusListener {
////                    override fun onMeetingRunning() {
////                        ZoomSDK.getInstance().inMeetingService.currentMeetingID
////                        meetingNumber =
////                            ZoomSDK.getInstance().inMeetingService.currentMeetingNumber.toString()
////                        ZoomSDK.getInstance().inMeetingService.currentMeetingTopic
////                        meetingPassword = ZoomSDK.getInstance().inMeetingService.meetingPassword
////                        meetingUrl = ZoomSDK.getInstance().inMeetingService.currentMeetingUrl
////                        //Toast.makeText(ZoomTest.this, "ID: ${idPwd.first} PWD: ${idPwd.second}", Toast.LENGTH_SHORT).show();
////                        Log.i(TAG, "ID:  PWD: $meetingNumber $meetingPassword")
////                        // startMeeting();
////                    }
////
////                    override fun onMeetingFailed() {
////                        Toast.makeText(
////                            this@ZoomPrivateMeetingActivity,
////                            "Could not host a meeting.",
////                            Toast.LENGTH_SHORT
////                        ).show()
////                    }
////                }).createInstantMeeting()
//        }
//        intiSdk()
//
//
//        Utils.setStatusBarColor(this)
//    }
//
//
//    private fun initFunction() {
//        zoomGoLiveViewModel.getLiveClassDetails(adminId)
//            .observe(this, Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            val zoomLoginDetails = response.zoomLoginDetails
//                            for (i in zoomLoginDetails.indices) {
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
//                           // intiSdk()
//
//                            getZoomSdkUser("Bearer ${Credentials.JWT_TOKEN}",Credentials.SDK_EMAIL)
//                        }
//                        Status.ERROR -> {
//                            Log.i(TAG, "Status.ERROR ${Status.ERROR}")
//                        }
//                        Status.LOADING -> {
//                            Log.i(TAG, "Status.LOADING ${Status.LOADING}")
//                        }
//                    }
//                }
//            })
//    }
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
//        zoomSDK?.initialize(this, listener, params)
//        initFunction()
////        zoomSDK?.initialize(this, Credentials.SDK_KEY, Credentials.SDK_SECRET, this)
////        val zoomSDKInitParams = ZoomSDKInitParams()
////        zoomSDKInitParams.appKey = Credentials.SDK_KEY
////        zoomSDKInitParams.appSecret = Credentials.SDK_SECRET
////        zoomSDKInitParams.domain = Credentials.SDK_DOMAIN
////        ZoomSDK.getInstance().initialize(this, this, zoomSDKInitParams)
////        ZoomSDK.getInstance().addAuthenticationListener(this)
//    }
//
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
//
//    fun startMeeting() {
//
//        val startParams = StartMeetingParamsWithoutLogin().apply {
//            userId = Credentials.SDK_EMAIL
//            userType = getUserType
//            displayName = getUserName;
//            zoomAccessToken = zakAccessToken
//            //   meetingNo = ZoomSDK.getInstance().inMeetingService.currentMeetingNumber.toString()
//        }
//
//
//        val result = meetingService?.startMeetingWithParams(this, startParams, StartMeetingOptions())
//        Log.i(TAG, "result $result")
//        if (result == MeetingError.MEETING_ERROR_SUCCESS) {
//            // The SDK will attempt to join the meeting.
//            Log.i(TAG, "start Meeting ${meetingService?.currentMeetingUrl}")
//        }
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
//
//
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
//    override fun onZoomSDKLoginResult(result: Long) {}
//    override fun onZoomSDKLogoutResult(result: Long) {}
//    override fun onZoomIdentityExpired() {}
//    override fun onMeetingStatusChanged(meetingStatus: MeetingStatus, i: Int, i1: Int) {}
//
//    override fun onMeetingParameterNotification(p0: MeetingParameter?) {
//        Log.i(TAG, "onMeetingParameterNotification")
//    }
//
//    override fun onZoomSDKInitializeResult(i: Int, i1: Int) {
//        Log.i(TAG, "SDK Initialized!")
//        doLoginToZoom()
//    }
//
//    private fun doLoginToZoom() {
//       // ZoomSDK.getInstance().isLoggedIn
//       // ZoomSDK.getInstance().loginWithZoom(email, password)
//    }
//
//    override fun onZoomAuthIdentityExpired() {}
//
//
//}