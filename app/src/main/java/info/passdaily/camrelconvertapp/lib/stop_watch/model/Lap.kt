package info.passdaily.camrelconvertapp.lib.stop_watch.model

import android.util.Log
import java.util.*
import java.util.concurrent.TimeUnit


data class Lap(
    var index: Int,
    var lap: Int,
    var diff: Int
) {
    companion object {
        fun convertToDuration(increment: Int): String {
            val second = (increment / 90) % 60
           // val minute = (increment / 90 ) / 60
            var minute = (increment / 90 ) % 3600 / 60
            val hours = (increment / 90) / 3600
            val millis = (increment % 90)

            val millisSecond = (increment / 90) / 1000


            Log.i("Lap","total ${increment / 90}")
            Log.i("Lap","millisSecond $millisSecond")
//            val hours = increment / 3600;
//            val minute = (increment % 3600) / 60;
//            val second = increment % 60;

            return String.format(
                Locale.US,
                "%02d : %02d : %02d",
                hours, minute, second
            )
        }
    }
}