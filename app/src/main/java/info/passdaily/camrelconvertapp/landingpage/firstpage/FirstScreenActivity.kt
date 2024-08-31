package info.passdaily.camrelconvertapp.landingpage.firstpage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.landingpage.SmsBroadcastReceiver
import info.passdaily.camrelconvertapp.landingpage.SmsBroadcastReceiverListener
//import info.passdaily.parentapp.landingpage.SmsBroadcastReceiverListener
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.LoginParentViewModel
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import java.util.regex.Matcher
import java.util.regex.Pattern


@Suppress("DEPRECATION")
class FirstScreenActivity : AppCompatActivity() /*,SmsBroadcastReceiver.OTPReceiveListener*/ {


    var doubleBackToExitPressedOnce: Boolean = false

    var TAG = "FirstScreenActivity"

    private val REQ_USER_CONSENT = 200

    private lateinit var loginParentViewModel: LoginParentViewModel

    lateinit var smsBroadcastReceiver: SmsBroadcastReceiver

    // private val smsBroadcastReceiver by lazy { SmsBroadcastReceiver() }

    private val appPermission =
        arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_MMS)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_screen)


        loginParentViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[LoginParentViewModel::class.java]

        loginParentViewModel.init();
        loginParentViewModel.sendData("");

        if (savedInstanceState == null) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer,
                FirstScreenFragment()
            ).commit()
        }

        setStatusBarColor()
        startSmsUserConsent();
    }

    private fun requestSmsPermission() {
        val smspermission = Manifest.permission.RECEIVE_SMS
        val grant = ContextCompat.checkSelfPermission(this, smspermission)
        // to check if read SMS permission is granted or not
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permissionList = arrayOfNulls<String>(1)
            permissionList[0] = smspermission
            ActivityCompat.requestPermissions(this, permissionList, 1)
        }
    }



    private fun startSmsUserConsent() {
        val client = SmsRetriever.getClient(this)
        //We can add sender phone number or leave it blank
        // I'm adding null here
        client.startSmsUserConsent(null).addOnSuccessListener {
            Log.i(TAG, "On Success ")

        }.addOnFailureListener {
            Log.i(TAG, "On OnFailure ")
        }
    }

    private fun setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = window
            //  window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#D5D8DC")
        }
    }

    override fun onBackPressed() {
        when (Global.screenState) {
            "landingpage" -> {
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frameContainer, FirstScreenFragment()).commit()
            }
            "LoginFragment" -> {
                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.frameContainer, SelectUserFragment()).commit()
            }
            "homePage" -> {
                if (!doubleBackToExitPressedOnce) {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(this, "click back again to exit.", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed(
                        { doubleBackToExitPressedOnce = false },
                        2000
                    )
                } else {
                    super.onBackPressed()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
//                textViewMessage.setText(
//                    String.format(
//                        "%s - %s",
//                        getString(R.string.received_message),
//                        message
//                    )
//                )
                getOtpFromMessage(message)
            }
        }
    }

    private fun getOtpFromMessage(message: String?) {
        // This will match any 6 digit number in the message
        //val pattern: Pattern = Pattern.compile("(|^)\\d{6}")
        val pattern = Pattern.compile("[^\\d]*[\\d]+[^\\d]+([\\d]+)")
        val matcher: Matcher = pattern.matcher(message!!)
        if (matcher.find()) {
            //otpText.setText(matcher.group(0))
            loginParentViewModel.sendData(matcher.group(1));
            //Log.i(TAG,"Otp here ${matcher.group(0)}")
            Log.i(TAG, "Otp here ${matcher.group(1)}")
        }
    }

    //
//
    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    startActivityForResult(intent, REQ_USER_CONSENT)
                }
                override fun onFailure() {}
            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }


    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }

}