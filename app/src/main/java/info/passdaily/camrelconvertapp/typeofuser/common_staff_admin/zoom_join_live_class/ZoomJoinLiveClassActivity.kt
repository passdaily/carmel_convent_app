//package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_join_live_class
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.AppCompatButton
//import androidx.appcompat.widget.Toolbar
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.ViewModelProviders
//import com.google.android.material.textfield.TextInputEditText
//import info.passdaily.camrelconvertapp.databinding.ActivityZoomJoinLiveClassBinding
//import info.passdaily.camrelconvertapp.services.Utils
//import info.passdaily.camrelconvertapp.services.ViewModelFactory
//import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
//import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
//import info.passdaily.camrelconvertapp.services.initsdk.InitAuthSDKCallback
//import info.passdaily.camrelconvertapp.services.initsdk.InitAuthSDKHelper
//import info.passdaily.camrelconvertapp.services.initsdk.UserLoginCallback
//import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
//import info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout.Credentials
//import info.passdaily.camrelconvertapp.services.zoomsdk.JoinMeetingHelperJava
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoomGoLive.ZoomGoLiveViewModel
//import us.zoom.sdk.*
//
//@Suppress("DEPRECATION")
//class ZoomJoinLiveClassActivity : AppCompatActivity(), InitAuthSDKCallback, MeetingServiceListener,
//    UserLoginCallback.ZoomDemoAuthenticationListener, View.OnClickListener{
//
//    var TAG = "ZoomJoinLiveClassActivity"
//
//    private lateinit var binding: ActivityZoomJoinLiveClassBinding
//
//    private lateinit var zoomGoLiveViewModel: ZoomGoLiveViewModel
//
//    private lateinit var localDBHelper: LocalDBHelper
//    var adminId = 0
//
//    var mZoomSDK: ZoomSDK? = null
//    var meetingService: MeetingService? = null
//
//    //   JoinMeetingParams params;
//    var editJoinNumber: TextInputEditText? = null
//    var editJoinName: TextInputEditText? = null
//    var editJoinPassword: TextInputEditText? = null
//
//    var constraintLayout : ConstraintLayout? = null
//    var joinButton: AppCompatButton? = null
//
//    var ZoomClassList = "0"
//    var MEETING_ID = ""
//    var MEETING_PASSWORD = ""
//    var Z_MEETING_ID = ""
//
//    // TODO Change it to your APP Key
//    val SDK_KEY = "I2I3TPAxkrFEs657QqAzp4TEyvUIPutNSkHs"
//    //I2I3TPAxkrFEs657QqAzp4TEyvUIPutNSkHs
//
//    //I2I3TPAxkrFEs657QqAzp4TEyvUIPutNSkHs
//    // TODO Change it to your APP Secret
//    val SDK_SECRET = "UWDSUF33H1wSUVEt7jdTcjgUb4br5BwRN54n"
//    //UWDSUF33H1wSUVEt7jdTcjgUb4br5BwRN54n
//
//    companion object {
//        private const val CAMERA_PERMISSION_CODE = 100
//        private const val STORAGE_PERMISSION_CODE = 101
//    }
//
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
//        binding = ActivityZoomJoinLiveClassBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        toolbar = binding.toolbar
//        if (toolbar != null) {
//            setSupportActionBar(toolbar)
//            supportActionBar!!.title = "Join Live Class"
//            // Customize the back button
////            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
//            supportActionBar!!.setDisplayShowTitleEnabled(true)
//            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//            //   toolbar.setTitle("About-Us");
//            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
//                onBackPressed()
//            })
//        }
//
//        val extras = intent.extras
//        if (extras != null) {
//            //ZoomClassList = extras.getString("ZoomClassList");
//            //feedlist.get(position).get("MEETING_ID")
//            MEETING_ID = extras.getString("MEETING_ID")!!
//            MEETING_PASSWORD = extras.getString("MEETING_PASSWORD")!!
//            Z_MEETING_ID = extras.getString("Z_MEETING_ID")!!
//        }
//
//        Log.i(TAG, "MEETING_ID $MEETING_ID")
//        Log.i(TAG, "MEETING_PASSWORD $MEETING_ID")
//
//        constraintLayout = binding.constraintLayout
//        editJoinNumber = binding.editJoinNumber
//        editJoinName = binding.editJoinName
//        editJoinPassword = binding.editJoinPassword
//        editJoinNumber?.setText(MEETING_ID)
//
//        joinButton = binding.joinButton
//
//
//        mZoomSDK = ZoomSDK.getInstance()
//        if (savedInstanceState == null) {
//            val params = ZoomSDKInitParams()
//            params.appKey = Credentials.SDK_KEY // TODO: Retrieve your SDK key and enter it here
//            params.jwtToken = Credentials.JWT_TOKEN // Todo :Pass in your Meeting SDK JWT
//            params.appSecret = Credentials.SDK_SECRET // TODO: Retrieve your SDK secret and enter it here
//            params.domain = Credentials.SDK_DOMAIN
//            params.enableLog = true
//
//            val listener: ZoomSDKInitializeListener = object : ZoomSDKInitializeListener {
//                /**
//                 * @param errorCode [us.zoom.sdk.ZoomError.ZOOM_ERROR_SUCCESS] if the SDK has been initialized successfully.
//                 */
//                override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) {
//
//                }
//                override fun onZoomAuthIdentityExpired() {
//
//                }
//            }
//            mZoomSDK?.initialize(this, listener, params)
//        }
//        meetingService = mZoomSDK?.meetingService
//        if (meetingService != null) {
//            meetingService!!.addListener(this)
//            Log.i(TAG, "currentMeetingUrl${meetingService?.currentMeetingUrl}")
//            Log.i(TAG, "currentRtcMeetingNumber${meetingService?.currentRtcMeetingNumber}")
//            Log.i(TAG, "meetingStatus${meetingService?.currentRtcMeetingID}")
//            Log.i(TAG, "meetingStatus${meetingService?.meetingStatus}")
//        }
//
//        joinButton?.setOnClickListener(this)
//
//        Utils.setStatusBarColorZoom(this)
//
//        checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)
//    }
//
//    override fun onZoomSDKInitializeResult(i: Int, i1: Int) {
//        if (i != 0) {
//            val str = TAG
//            Log.i(str, "Failed to initialize Zoom SDK. Error: $i internalErrorCode= $i1")
//            return
//        }
//        ZoomSDK.getInstance().meetingSettingsHelper.enable720p(true)
//        ZoomSDK.getInstance().meetingSettingsHelper.enableShowMyMeetingElapseTime(true)
//        ZoomSDK.getInstance().meetingSettingsHelper.setVideoOnWhenMyShare(true)
//        ZoomSDK.getInstance().meetingService.addListener(this)
//        Log.i(TAG, "Initialize Zoom SDK successfully.")
//        if (mZoomSDK!!.tryAutoLoginZoom() == 0) {
//            UserLoginCallback.getInstance().addListener(this)
//        }
//    }
//
//
//    override fun onZoomSDKLoginResult(result: Long) {
//        Log.i(TAG,"onZoomSDKLoginResult")
//    }
//
//    override fun onZoomSDKLogoutResult(result: Long) {
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
//    override fun onMeetingStatusChanged(p0: MeetingStatus?, p1: Int, p2: Int) {
//        Log.i(TAG,"onMeetingStatusChanged")
//    }
//
//
//
//    override fun onMeetingParameterNotification(p0: MeetingParameter?) {
//        Log.i(TAG,"onMeetingParameterNotification")
//    }
//
//    override fun onClick(view: View?) {
//        Log.i(TAG, "onClick")
//        if(zoomGoLiveViewModel.validateField(editJoinNumber!!,"Enter meeting id or number",this,constraintLayout!!) &&
//            zoomGoLiveViewModel.validateField(editJoinPassword!!,"Enter meeting password",this,constraintLayout!!)
//            && zoomGoLiveViewModel.validateField(editJoinName!!,"Enter User Name",this,constraintLayout!!))
//        {
//            try {
//                if (!mZoomSDK!!.isInitialized) {
//                    Log.i(TAG, "Init SDK First")
//                    InitAuthSDKHelper.getInstance().initSDK(this, this)
//                } else {
//                    if (ZoomSDK.getInstance().meetingSettingsHelper.isCustomizedMeetingUIEnabled) {
//                        ZoomSDK.getInstance().smsService.enableZoomAuthRealNameMeetingUIShown(false)
//                    } else {
//                        ZoomSDK.getInstance().smsService.enableZoomAuthRealNameMeetingUIShown(true)
//                    }
//                    val meetingNo: String = editJoinNumber?.text.toString()
//                    val password: String = editJoinPassword?.text.toString()
//                    val displayName: String = editJoinName?.text.toString()
//                    val joinMeetingParams = JoinMeetingParams()
//                    joinMeetingParams.meetingNo = meetingNo
//                    //                    joinMeetingParams.password = password;
//                    joinMeetingParams.displayName = displayName
//                    JoinMeetingHelperJava().joinMeeting(
//                        this,
//                        ZoomSDK.getInstance(),
//                        meetingNo,
//                        password,
//                        displayName
//                    )
//                }
//            } catch (e: Exception) {
//                Log.i(TAG, "Unexpected Exception $e")
//            }
//        }
//    }
//
//
//    /* access modifiers changed from: protected */
//    override fun onDestroy() {
//        super.onDestroy()
//        UserLoginCallback.getInstance().removeListener(this)
//        Log.i(TAG, "onDestroy")
//        val meetingService2 = meetingService
//        meetingService2?.removeListener(this)
//        InitAuthSDKHelper.getInstance().reset()
//    }
//
//    // Function to check and request permission.
//    private fun checkPermission(permission: String, requestCode: Int) {
//        if (ContextCompat.checkSelfPermission(this@ZoomJoinLiveClassActivity, permission) == PackageManager.PERMISSION_DENIED) {
//
//            // Requesting the permission
//            ActivityCompat.requestPermissions(this@ZoomJoinLiveClassActivity, arrayOf(permission), requestCode)
//        } else {
//            Toast.makeText(this@ZoomJoinLiveClassActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // This function is called when the user accepts or decline the permission.
//    // Request Code is used to check which permission called this function.
//    // This request code is provided when the user is prompt for permission.
//    override fun onRequestPermissionsResult(requestCode: Int,
//                                            permissions: Array<String>,
//                                            grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this@ZoomJoinLiveClassActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this@ZoomJoinLiveClassActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
//            }
//        } else if (requestCode == STORAGE_PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this@ZoomJoinLiveClassActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this@ZoomJoinLiveClassActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//}