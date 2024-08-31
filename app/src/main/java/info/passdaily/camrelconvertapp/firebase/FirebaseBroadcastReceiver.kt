package info.passdaily.camrelconvertapp.firebase

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import info.passdaily.camrelconvertapp.R

@Suppress("DEPRECATION")
class FirebaseBroadcastReceiver : BroadcastReceiver() {

    val TAG = "FirebaseBroadcastReceiver"

    override fun onReceive(context: Context, intent: Intent) {

        Log.i(TAG,"WakefulBroadcastReceiver ")

//        try {
//            var uri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.school_bell)
//            // var uri = Uri.parse("https://www.myinstants.com/media/sounds/beeper_emergency_call.mp3")
//            var r = RingtoneManager.getRingtone(context, uri)
//            r.play()
//        }catch (e : Exception){
//            Log.i(TAG,"Exception $e")
//        }

        Log.i(TAG,"intent ${intent.extras!!}")

//        val i = Intent(context, ReceiveDataActivity::class.java)
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        i.putExtra("message", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
//        context.startActivity(i)

//        val dataBundle = intent.extras
//        if (dataBundle != null)
//            for (key in dataBundle.keySet()) {
//                Log.i(TAG, "dataBundle: " + key + " : " + dataBundle.get(key))
//            }
        //val remoteMessage = RemoteMessage(dataBundle)
    }
}