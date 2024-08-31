//package info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout
//
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.content.DialogInterface
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.bumptech.glide.Glide
//import com.google.android.material.textfield.TextInputEditText
//import info.passdaily.camrelconvertapp.R
//import info.passdaily.camrelconvertapp.databinding.ActivityZoomLiveBinding
//import info.passdaily.camrelconvertapp.services.Global
//import info.passdaily.camrelconvertapp.services.Utils
//import info.passdaily.camrelconvertapp.services.initsdk.AuthConstants
//import info.passdaily.camrelconvertapp.services.initsdk.InitAuthSDKCallback
//import info.passdaily.camrelconvertapp.services.initsdk.InitAuthSDKHelper
//import info.passdaily.camrelconvertapp.services.initsdk.UserLoginCallback
//import info.passdaily.camrelconvertapp.services.localDB.parent.StudentFCMHelper
//import info.passdaily.camrelconvertapp.typeofuser.parent.MainActivityParent
//import us.zoom.sdk.*
//
//
//@Suppress("DEPRECATION", "SetTextI18n")
//class ZoomLiveActivity : AppCompatActivity(), InitAuthSDKCallback, MeetingServiceListener,
//    UserLoginCallback.ZoomDemoAuthenticationListener, View.OnClickListener {
//    var TAG = "ZoomLiveActivity"
//
//    private lateinit var binding: ActivityZoomLiveBinding
//
//    var imageViewLive : ImageView? = null
//    var liveClass: Int = 0
//    var mainClass: Int = 0
//
//    var mZoomSDK: ZoomSDK? = null
//
//    var meetingService: MeetingService? = null
//
//    var nameEditText : TextInputEditText? = null
//    var meetingIdEditText : TextInputEditText? = null
//    var meetingPasswordEditText : TextInputEditText? = null
//
//    var zOOMMEETINGID =  ""
//    var zOOMMEETINGPASSWORD = ""
//
//    // intent.putExtra("zOOMMEETINGID", zoomMeetingList[position].zMEETINGID)
//    //                intent.putExtra("zOOMMEETINGPASSWORD", zoomMeetingList[position].mEETINGPASSWORD)
//
//
//    override fun onZoomIdentityExpired() {
//        Log.i(TAG, "onZoomIdentityExpired ")
//    }
//
//    override fun onZoomAuthIdentityExpired() {
//        Log.i(TAG, "onZoomAuthIdentityExpired")
//    }
//
//
//    @SuppressLint("SetTextI18n")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//
//        var studentFCMHelper = StudentFCMHelper(this)
//        var studentFcm = studentFCMHelper.getProductByStudent(Global.studentId.toString())
//
//        var STUDENT_NAME = studentFcm.STUDENT_NAME
//        val CLASS_NAME = studentFcm.CLASS_NAME
//
//        val extras = intent.extras
//        if (extras != null) {
//            liveClass = extras.getInt("liveClass")
//            mainClass = extras.getInt("mainClass")
//            zOOMMEETINGID = extras.getString("zOOMMEETINGID")!!
//            zOOMMEETINGPASSWORD = extras.getString("zOOMMEETINGPASSWORD")!!
//        }
////        setContentView(R.layout.activity_zoom_live)
//        binding = ActivityZoomLiveBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        mZoomSDK = ZoomSDK.getInstance()
//        if (savedInstanceState == null) {
//            val params = ZoomSDKInitParams()
//            params.appKey = Credentials.SDK_KEY // TODO: Retrieve your SDK key and enter it here
//
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
////        val meetingService2: MeetingService = meetingService!!
//        if (meetingService != null) {
//            meetingService?.addListener(this)
//            Log.i(TAG, "not null $meetingService")
//        }
//
//        nameEditText = binding.nameEditText
//        meetingIdEditText = binding.meetingIdEditText
//        meetingPasswordEditText = binding.meetingPasswordEditText
//
//        meetingIdEditText?.setText(zOOMMEETINGID)
//        meetingIdEditText?.isEnabled = true
////        if(zOOMMEETINGID == "0"){
////            meetingIdEditText?.setText("")
////            meetingIdEditText?.isEnabled = false
////        }
//
//        meetingPasswordEditText?.setText(zOOMMEETINGPASSWORD)
//        meetingPasswordEditText?.isEnabled = true
////        if(zOOMMEETINGPASSWORD == "0"){
////            meetingPasswordEditText?.setText("")
////            meetingIdEditText?.isEnabled = false
////        }
//        nameEditText?.setText(STUDENT_NAME)
//      //  if(nameEditText?.text.toString().isNotEmpty()){
//            nameEditText?.isEnabled = false
//      //  }
//
//        imageViewLive = binding.imageViewLive
//
//        if(liveClass == 1){
//            binding.textToolbarTitle.text = "Live Scheduled List"
//            Glide.with(this)
//                .load(R.drawable.screen_image_live1)
//                .into(imageViewLive!!)
//        }else if(liveClass == 0){
//            binding.textToolbarTitle.text = "Live Class"
//            Glide.with(this)
//                .load(R.drawable.screen_image_live2)
//                .into(imageViewLive!!)
//        }
//
//        binding.imageBackPress.setOnClickListener {
//            if(mainClass == 1){
//                Global.currentPage = 1
//                startActivity(Intent(this, MainActivityParent::class.java))
//               // finish()
//            }else{
//                super.onBackPressed()
//            }
//        }
//
//        binding.buttonLive.setOnClickListener(this)
//
//        Utils.setStatusBarColor(this)
//    }
//
//
//    override fun onClick(v: View?) {
//        if (nameEditText?.text.toString().isNotEmpty() && meetingIdEditText?.text.toString().isNotEmpty()
//            && meetingPasswordEditText?.text.toString().isNotEmpty()) {
//            try {
//                if (!checkPermission()) {
//                    requestPermission()
//                } else if (!mZoomSDK?.isInitialized!!) {
//                    Log.i(TAG, "Init SDK First")
//                    InitAuthSDKHelper.getInstance().initSDK(this, this)
//                } else {
//                    if (ZoomSDK.getInstance().meetingSettingsHelper.isCustomizedMeetingUIEnabled) {
//                        ZoomSDK.getInstance().smsService.enableZoomAuthRealNameMeetingUIShown(
//                            false
//                        )
//                    } else {
//                        ZoomSDK.getInstance().smsService.enableZoomAuthRealNameMeetingUIShown(
//                            true
//                        )
//                    }
//                    val meetingID: String = meetingIdEditText?.text.toString()
//                    val name: String = nameEditText?.text.toString()
//                    val meetingPassword: String = meetingPasswordEditText?.text.toString()
//                    val joinMeetingParams = JoinMeetingParams()
//                    joinMeetingParams.meetingNo = meetingID
//                    joinMeetingParams.displayName = name
//
//
//                    JoinMeetingHelper().joinMeeting(
//                        this,
//                        ZoomSDK.getInstance(), meetingID, meetingPassword, name
//                    )
//                }
//            } catch (e: Exception) {
//                Log.i(TAG, "Unexpected Exception $e");
//            }
//        } else {
//            Toast.makeText(this, "Give values to all the fields", Toast.LENGTH_SHORT)
//                .show()
//        }
//    }
//
//
//    override fun onBackPressed() {
//        if(mainClass == 1){
//            Global.currentPage = 1
//            startActivity(Intent(this, MainActivityParent::class.java))
//            finish()
//        }else{
//            super.onBackPressed()
//        }
//    }
//
//
//
//    override fun onZoomSDKInitializeResult(pos: Int, pos2: Int) {
//        if (pos != 0) {
//            Log.i(TAG, "Failed to initialize Zoom SDK. Error: $pos internalErrorCode= $pos2")
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
//    override fun onZoomSDKLoginResult(result: Long) {
//        Log.i(TAG, "onZoomSDKLoginResult $result")
//    }
//
//    override fun onZoomSDKLogoutResult(result: Long) {
//        Log.i(TAG, "onZoomSDKLoginResult $result")
//    }
//
//
//
//    override fun onMeetingStatusChanged(meetingStatus: MeetingStatus?, pos: Int, p2: Int) {
//        if (meetingStatus == MeetingStatus.MEETING_STATUS_FAILED && pos == 4) {
//            Toast.makeText(this, "Version of ZoomSDK is too low!", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onMeetingParameterNotification(p0: MeetingParameter?) {
//        Log.i(TAG, "onMeetingParameterNotification $p0")
//    }
//
////    override fun onMeetingParameterNotification(result: MeetingParameter?) {
////        Log.i(TAG, "onMeetingParameterNotification $result")
////    }
//
//
//    ////////////////permission Part
//    private fun checkPermission(): Boolean {
//        return ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") == 0
//    }
//
//    /* access modifiers changed from: private */
//    private fun requestPermission() {
//        ActivityCompat.requestPermissions(this, arrayOf("android.permission.CAMERA"), 200)
//    }
//
//    override fun onRequestPermissionsResult(i: Int, vararg strArr: String?, iArr: IntArray) {
//        super.onRequestPermissionsResult(i, strArr, iArr)
//        if (i == 200) {
//            if (iArr.isEmpty() || iArr[0] != 0) {
//                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
//                if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0
//                ) {
//                    showMessageOKCancel(
//                        "You need to allow access permissions"
//                    ) { _, _ ->
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                            requestPermission()
//                        }
//                    }
//                    return
//                }
//                return
//            }
//            Toast.makeText(applicationContext, "Permission Granted.", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun showMessageOKCancel(str: String, onClickListener: DialogInterface.OnClickListener) {
//        AlertDialog.Builder(this)
//            .setMessage(str).setPositiveButton("OK", onClickListener)
//            .setNegativeButton("Cancel", null as DialogInterface.OnClickListener?).create().show()
//    }
//
//}