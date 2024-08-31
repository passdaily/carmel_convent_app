package info.passdaily.camrelconvertapp.landingpage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status


@Suppress("DEPRECATION")
class SmsBroadcastReceiver : BroadcastReceiver() {

    lateinit  var smsBroadcastReceiverListener: SmsBroadcastReceiverListener
    var TAG = "SmsBroadcastReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "smsBroadcastReceiverListener")
        if (intent!!.action === SmsRetriever.SMS_RETRIEVED_ACTION) {

            val extras: Bundle? = intent!!.extras
            val smsRetrieverStatus: Status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status
            when (smsRetrieverStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val messageIntent: Intent =
                        extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT)!!
                    smsBroadcastReceiverListener.onSuccess(messageIntent)
                    Log.i(TAG, "smsBroadcastReceiverListener")
                }
                CommonStatusCodes.TIMEOUT -> smsBroadcastReceiverListener.onFailure()
            }
        }


    }
}
interface SmsBroadcastReceiverListener {
    fun onSuccess(otpMsg: Intent?)
    fun onFailure()
}


//@Suppress("DEPRECATION")
//class SmsBroadcastReceiver : BroadcastReceiver() {
//
//    var TAG= "SmsBroadcastReceiver"
//
//    private var otpListener: OTPReceiveListener? = null
//
//    /**
//     * @param otpListener
//     */
//    fun setOTPListener(otpListener: OTPReceiveListener?) {
//        this.otpListener = otpListener
//    }
//
//    /**
//     * @param context
//     * @param intent
//     */
//    override fun onReceive(context: Context?, intent: Intent) {
//        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
//            val extras: Bundle = intent.extras!!
//            val status: Status = extras.get(SmsRetriever.EXTRA_STATUS) as Status
//            when (status.statusCode) {
//                CommonStatusCodes.SUCCESS -> {
//
//                    //This is the full message
//                    try {
//                        val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
//                        if (otpListener != null) {
//                            otpListener?.onOTPReceived(message)
//                        }
//                    }catch(e: NullPointerException){
//                        Log.i(TAG,"NullPointerException $e")
//                        otpListener?.onOTPReceivedError("Some error while message receiving")
//                    }
//
//                    /*<#> Your ExampleApp code is: 123ABC78
//                    FA+9qCX9VSu*/
//
//                    //Extract the OTP code and send to the listener
//
//                }
//                CommonStatusCodes.TIMEOUT ->                     // Waiting for SMS timed out (5 minutes)
//                    if (otpListener != null) {
//                        otpListener?.onOTPTimeOut()
//                    }
//                CommonStatusCodes.API_NOT_CONNECTED -> if (otpListener != null) {
//                    otpListener?.onOTPReceivedError("API NOT CONNECTED")
//                }
//                CommonStatusCodes.NETWORK_ERROR -> if (otpListener != null) {
//                    otpListener?.onOTPReceivedError("NETWORK ERROR")
//                }
//                CommonStatusCodes.ERROR -> if (otpListener != null) {
//                    otpListener?.onOTPReceivedError("SOME THING WENT WRONG")
//                }
//            }
//        }
//    }
//
//    /**
//     *
//     */
//
//    open interface OTPReceiveListener {
//        fun onOTPReceived(otp: String?)
//        fun onOTPTimeOut()
//        fun onOTPReceivedError(error: String?)
//    }
//
////    var smsBroadcastReceiverListener: SmsBroadcastReceiverListener? = null
////    override fun onReceive(context: Context?, intent: Intent) {
////        if (intent.action === SmsRetriever.SMS_RETRIEVED_ACTION) {
////            val extras = intent.extras
////            val smsRetrieverStatus = extras!![SmsRetriever.EXTRA_STATUS] as Status?
////            when (smsRetrieverStatus!!.statusCode) {
////                CommonStatusCodes.SUCCESS -> {
////                    val messageIntent =
////                        extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
////                    smsBroadcastReceiverListener!!.onSuccess(messageIntent)
////                }
////                CommonStatusCodes.TIMEOUT -> smsBroadcastReceiverListener!!.onFailure()
////            }
////        }
////    }
////
////    interface SmsBroadcastReceiverListener {
////        fun onSuccess(intent: Intent?)
////        fun onFailure()
////    }
//
//}