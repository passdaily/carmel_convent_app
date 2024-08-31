package info.passdaily.camrelconvertapp.landingpage

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.messaging.FirebaseMessaging
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityObjExamDetailsBinding
import info.passdaily.camrelconvertapp.databinding.ActivitySplashScreenBinding
import info.passdaily.camrelconvertapp.firebase.Config
import info.passdaily.camrelconvertapp.firebase.NotificationUtils
import info.passdaily.camrelconvertapp.landingpage.slide.SlidePage
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.admin.MainActivityAdmin
import info.passdaily.camrelconvertapp.typeofuser.parent.MainActivityParent
import info.passdaily.camrelconvertapp.typeofuser.staff.MainActivityStaff
import java.io.IOException


@Suppress("DEPRECATION")
class SplashScreen : Activity() {

    var TAG= "SplashScreen"
    private val SPLASH_TIME_OUT:Long = 1000
    private var mRegistrationBroadcastReceiver: BroadcastReceiver? = null
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
            /// setContentView(R.layout.activity_splash_screen)

        var constraintLayout  = binding.constraintLayout


        try {
            mRegistrationBroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    // checking for type intent filter
                    FirebaseMessaging.getInstance().subscribeToTopic("ServiceNow")
                        .addOnCompleteListener { task ->
                            var msg = getString(R.string.msg_subscribed)
                            if (!task.isSuccessful) {
                                msg = getString(R.string.msg_subscribe_failed)
                            }
                            Log.i(TAG, msg)
                        }
                }
            }

        } catch (e: NullPointerException) {
            Log.i(TAG, "Print exception$e")
        }


        mHandler = Handler()
        mRunnable = Runnable { dismissSplash() }

        // allow user to click and dismiss the splash screen prematurely

        val rootView = findViewById<View>(android.R.id.content)
        rootView.setOnClickListener { dismissSplash() }
//        Handler(Looper.getMainLooper()).postDelayed({
//            // This method will be executed once the timer is over
//            // Start your app main activity
//
//
//            // close this activity
//        }, SPLASH_TIME_OUT)// 3000 is the delayed time in milliseconds.
//

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isComplete) {
                try {
                    val firebaseToken = it.result.toString()
                    val registrationComplete = Intent(Config.REGISTRATION_COMPLETE)
                    registrationComplete.putExtra("token", firebaseToken)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete)
                    Log.i(TAG, "firebaseToken $firebaseToken")
                } catch (e: IOException) {
                    Log.i(TAG, "IOException $e")
                } catch (error: RuntimeExecutionException) {
                    Utils.getSnackBar4K(this,resources.getString(R.string.no_internet),constraintLayout)
                    Log.i(TAG, "RuntimeExecutionException $error")
                }
            }
        }




        // Notify UI that registration has completed, so the progress indicator can be hidden.

        // Notify UI that registration has completed, so the progress indicator can be hidden.


    }


    override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mRegistrationBroadcastReceiver!!,
            IntentFilter(Config.REGISTRATION_COMPLETE)
        )
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mRegistrationBroadcastReceiver!!,
            IntentFilter(Config.PUSH_NOTIFICATION)
        )

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(applicationContext)
        super.onResume()
        mHandler!!.postDelayed(
            mRunnable!!,
            SPLASH_TIME_OUT
        )
    }



    private fun dismissSplash() {
        val localDBHelper = LocalDBHelper(this)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        var user = localDBHelper.viewUser()
        when (user[0].ADMIN_ROLE) {
            0 -> {
                startActivity(Intent(this, SlidePage::class.java))
                finish()
            }
            1 -> {
                startActivity(Intent(this, MainActivityAdmin::class.java))
                finish()
            }
            5 -> {
                startActivity(Intent(this, MainActivityAdmin::class.java))
                finish()
            }
            3 -> {
                startActivity(Intent(this, MainActivityStaff::class.java))
                finish()
            }
            4 -> {
                startActivity(Intent(this, MainActivityParent::class.java))
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop")
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver!!)
        super.onPause()
        mHandler!!.removeCallbacks(mRunnable!!)
    }

}