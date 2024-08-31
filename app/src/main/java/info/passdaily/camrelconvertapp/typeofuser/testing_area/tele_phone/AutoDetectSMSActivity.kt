//package info.passdaily.teach_daily_app.typeofuser.testing_area.tele_phone
//
//import android.Manifest
//import android.content.IntentFilter
//import android.content.pm.PackageManager
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.app.ActivityCompat
//import com.google.android.gms.auth.api.Auth
//import com.google.android.gms.auth.api.credentials.HintRequest
//import com.google.android.gms.auth.api.phone.SmsRetriever
//import com.google.android.material.textfield.TextInputEditText
//import info.passdaily.teach_daily_app.R
//import info.passdaily.teach_daily_app.databinding.ActivityAutoDetectSmsactivityBinding
//import info.passdaily.teach_daily_app.landingpage.SmsBroadcastReceiver
//import info.passdaily.teach_daily_app.services.Utils
//import info.passdaily.teach_daily_app.typeofuser.common_staff_admin.home.HomeFragmentStaff
//import java.util.regex.Matcher
//import java.util.regex.Pattern
//
//
//class AutoDetectSMSActivity : AppCompatActivity(),SmsBroadcastReceiver.OTPReceiveListener {
//
//    var smsBroadcastReceiver: SmsBroadcastReceiver? = null
//    private val REQ_USER_CONSENT = 200
//
//    private val PERMISSION_REQUEST_CODE = 1
//
//    private var smsReceiver: SmsBroadcastReceiver? = null
//
//    private lateinit var binding: ActivityAutoDetectSmsactivityBinding
//
//    var TAG = "AutoDetectSMSActivity"
//    var toolbar: Toolbar? = null
//
//    var passwordEditField : TextInputEditText? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityAutoDetectSmsactivityBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        toolbar = binding.toolbar
//        if (toolbar != null) {
//            setSupportActionBar(toolbar)
//            supportActionBar!!.title = "OTP Verification"
//            // Customize the back button
//            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
//            supportActionBar!!.setDisplayShowTitleEnabled(true)
//            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
//                onBackPressed()
//            })
//        }
//
////        passwordEditField = findViewById(R.id.passwordEditField)
////
////        val appSignatureHashHelper = AppSignatureHashHelper(this)
////
////        // This code requires one time to get Hash keys do comment and share key
////
////        // This code requires one time to get Hash keys do comment and share key
////        Log.i(TAG, "HashKey: " + appSignatureHashHelper.appSignatures[0])
////
////        startSMSListener()
//
//       // startSmsUserConsent()
//
//
//
//        if (savedInstanceState == null) {
//            var fragmentManager = supportFragmentManager
//            var fragmentTransaction = fragmentManager.beginTransaction()
//            fragmentTransaction.replace(R.id.sim_host_fragment, SimDetectionFragment())
////            .addToBackStack("home").commit()
//                .commit()
//        }
//
//        Utils.setStatusBarColor(this)
//    }
//
////    private fun startSmsUserConsent() {
////        val client = SmsRetriever.getClient(this)
////        //We can add sender phone number or leave it blank
////        // I'm adding null here
////        client.startSmsUserConsent(null).addOnSuccessListener {
//////            Toast.makeText(
//////                applicationContext,
//////                "On Success",
//////                Toast.LENGTH_LONG
//////            ).show()
////        }.addOnFailureListener {
//////            Toast.makeText(applicationContext, "On OnFailure", Toast.LENGTH_LONG).show()
////        }
////    }
//
////    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
////        super.onActivityResult(requestCode, resultCode, data)
////        if (requestCode == REQ_USER_CONSENT) {
////            if (resultCode == Activity.RESULT_OK && data != null) {
////                //That gives all message to us.
////                // We need to get the code from inside with regex
////                val message: String = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)!!
//////                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
//////                textViewMessage.setText(
//////                    String.format(
//////                        "%s - %s",
//////                        getString(R.string.received_message),
//////                        message
//////                    )
//////                )
////                passwordEditField?.setText(getOtpFromMessage(message))
////            }
////        }
////    }
//
//   fun getOtpFromMessage(message: String?) : String{
//        // This will match any 6 digit number in the message
//        //val pattern: Pattern = Pattern.compile("(|^)\\d{6}")
//        var otp = ""
//        val pattern = Pattern.compile("[^\\d]*[\\d]+[^\\d]+([\\d]+)+[^\\d]+([\\d]+)")
//       // val pattern = Pattern.compile("(|^)\\d{6}")
//        val matcher: Matcher = pattern.matcher(message!!)
//        if (matcher.find()) {
//            //otpText.setText(matcher.group(0))
//           // loginParentViewModel.sendData(matcher.group(1));
//           // Log.i(TAG,"Otp here ${matcher.matches()}")
//            otp = matcher.group(2)!!
////            Log.i(TAG,"Otp here ${matcher.group(0)}")
////            Log.i(TAG,"Otp here ${matcher.group(1)}")
////            Log.i(TAG,"Otp here ${matcher.group(2)}")
//        }
//        return otp
//    }
//
//    private fun registerBroadcastReceiver() {
////        smsBroadcastReceiver = SmsBroadcastReceiver()
////        smsBroadcastReceiver?.smsBroadcastReceiverListener =
////            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
////                override fun onSuccess(intent: Intent?) {
////                    startActivityForResult(intent, REQ_USER_CONSENT)
////                }
////
////                override fun onFailure() {}
////            }
////        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
////        registerReceiver(smsBroadcastReceiver, intentFilter)
//    }
//
//    override fun onStart() {
//        super.onStart()
//    //    registerBroadcastReceiver()
//    }
//
//    override fun onStop() {
//        super.onStop()
//      //  unregisterReceiver(smsBroadcastReceiver)
//    }
//
//    override fun onOTPReceived(otp: String?) {
//        passwordEditField?.setText(getOtpFromMessage(otp))
//
//        if (smsReceiver != null) {
//          //  Log.i(TAG,"not onOTPReceived ")
//            unregisterReceiver(smsReceiver);
//            smsReceiver = null;
//        }
//    }
//
//    override fun onOTPTimeOut() {
//        Log.i(TAG,"onOTPTimeOut")
//        if (smsReceiver != null) {
//            Log.i(TAG,"not onOTPTimeOut ")
//            unregisterReceiver(smsReceiver);
//        }
//    }
//
//    override fun onOTPReceivedError(error: String?) {
//        Log.i(TAG,"onOTPReceivedError $error")
//    }
//
//
//    private fun startSMSListener() {
//        try {
//            smsReceiver = SmsBroadcastReceiver()
//            smsReceiver?.setOTPListener(this)
//            val intentFilter = IntentFilter()
//            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
//            this.registerReceiver(smsReceiver, intentFilter)
//            val client = SmsRetriever.getClient(this)
//            val task = client.startSmsRetriever()
//            task.addOnSuccessListener {
//                // API successfully started
//                Log.i(TAG,"addOnSuccessListener")
//            }
//            task.addOnFailureListener {
//                // Fail to start API
//                Log.i(TAG,"addOnFailureListener")
//            }
//        } catch (e: Exception) {
//            Log.i(TAG,"Exception $e")
//            e.printStackTrace()
//        }
//    }
//
////    private fun requestPermission() {
////        ActivityCompat.requestPermissions(this, arrayOf(
////            Manifest.permission.READ_PHONE_NUMBERS,
////            Manifest.permission.READ_PHONE_STATE), PERMISSION_REQUEST_CODE)
////    }
////
////    private fun checkPermission(): Boolean {
////        return if (Build.VERSION.SDK_INT >= 23) {
////            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED
////                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
////
//////            val result: Int = ContextCompat.checkSelfPermission(requireActivity(), permission)
//////            result == PERMISSION_GRANTED
////        } else {
////            true
////        }
////    }
//
//}