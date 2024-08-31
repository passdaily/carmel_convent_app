package info.passdaily.camrelconvertapp.firebase

import android.app.*
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.typeofuser.admin.MainActivityAdmin
import info.passdaily.camrelconvertapp.typeofuser.parent.MainActivityParent
import info.passdaily.camrelconvertapp.typeofuser.staff.MainActivityStaff
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat

class NotificationUtils(private val mContext: Context) {


    val CHANNEL_ID: String = "general_channel_new"


    @RequiresApi(Build.VERSION_CODES.P)
    fun showNotificationMessage( context: Context,title: String, message: String?,timeStamp : String?,json : String?,role : Int) {
        // Check for empty push message
        if (TextUtils.isEmpty(message)) return
        val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // notification icon
        val icon = R.mipmap.ic_launcher_carmel
        val channelId = "channel-05"
        val channelName = "Channel Name5"
        val importance = NotificationManager.IMPORTANCE_HIGH
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )
            notificationManager.createNotificationChannel(mChannel)
        }
        val mBuilder = NotificationCompat.Builder(
            mContext, channelId
        )
       // val alarmSound = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.school_bell)
        //Log.i(TAG,"alarmSound $alarmSound")

        try {
            var uri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.school_bell)
           // var uri = Uri.parse("https://www.myinstants.com/media/sounds/beeper_emergency_call.mp3")
            var r = RingtoneManager.getRingtone(context, uri)
            r.play()
        }catch (e : Exception){
            Log.i(TAG,"Exception $e")
        }

//        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
//            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//            .setUsage(AudioAttributes.USAGE_ALARM)
//            .build()
      //  val alarmSound = Uri.parse("android.resource://" + getApplicationContext()?.packageName + "/" + R.raw.notification_sound)



        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.addLine(message)
       // showSmallNotification(context,mBuilder, icon, title, message,timeStamp, alarmSound)

        val uniqueInt = (System.currentTimeMillis() and 0xfffffff).toInt()


        when (role) {
            5 -> {
                val notificationIntent = Intent(mContext, MainActivityAdmin::class.java)
                notificationIntent.putExtra("NotificationMessage", json)
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

                var pendingNotificationIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        mContext,
                        uniqueInt, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);
                } else {
                    PendingIntent.getActivity(
                        mContext,
                        uniqueInt, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationChannel = NotificationChannel(
                        channelId,
                        "My Notifications",
                        NotificationManager.IMPORTANCE_MAX
                    )

                    // Configure the notification channel.
                    notificationChannel.description = "Channel description"
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.RED
                    notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                    notificationChannel.enableVibration(true)
                    notificationManager.createNotificationChannel(notificationChannel)
                }


                val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)

                notificationBuilder.setAutoCancel(true)
                    //  .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingNotificationIntent)
                    .setSmallIcon(R.drawable.cam_logo)
                    .setTicker(context.resources.getString(R.string.app_name)) //     .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentInfo("Info")

                notificationManager.notify( /*notification id*/1, notificationBuilder.build())
            }
            1 -> {
                val notificationIntent = Intent(mContext, MainActivityAdmin::class.java)
                notificationIntent.putExtra("NotificationMessage", json)
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

                var pendingNotificationIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        mContext,
                        uniqueInt, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);
                } else {
                    PendingIntent.getActivity(
                        mContext,
                        uniqueInt, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationChannel = NotificationChannel(
                        channelId,
                        "My Notifications",
                        NotificationManager.IMPORTANCE_MAX
                    )

                    // Configure the notification channel.
                    notificationChannel.description = "Channel description"
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.RED
                    notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                    notificationChannel.enableVibration(true)
                    notificationManager.createNotificationChannel(notificationChannel)
                }


                val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)

                notificationBuilder.setAutoCancel(true)
                    //  .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingNotificationIntent)
                    .setSmallIcon(R.drawable.cam_logo)
                    .setTicker(context.resources.getString(R.string.app_name)) //     .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentInfo("Info")

                notificationManager.notify( /*notification id*/1, notificationBuilder.build())
            }
            3 -> {

                val notificationIntent = Intent(mContext, MainActivityStaff::class.java)
                notificationIntent.putExtra("NotificationMessage", json)
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                var pendingNotificationIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        mContext,
                        uniqueInt, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);
                } else {
                    PendingIntent.getActivity(
                        mContext,
                        uniqueInt, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationChannel = NotificationChannel(
                        channelId,
                        "My Notifications",
                        NotificationManager.IMPORTANCE_MAX
                    )

                    // Configure the notification channel.
                    notificationChannel.description = "Channel description"
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.RED
                    notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                    notificationChannel.enableVibration(true)
                    notificationManager.createNotificationChannel(notificationChannel)
                }
                val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
                notificationBuilder.setAutoCancel(true)
                    // .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingNotificationIntent)
                    .setSmallIcon(R.drawable.cam_logo)
                    .setTicker(context.resources.getString(R.string.app_name)) //     .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentInfo("Info")

                notificationManager.notify( /*notification id*/1, notificationBuilder.build())
            }
            4 -> {

                val notificationIntent = Intent(mContext, MainActivityParent::class.java)
                notificationIntent.putExtra("NotificationMessage", json)
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                var pendingNotificationIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        mContext,
                        uniqueInt, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);
                } else {
                    PendingIntent.getActivity(
                        mContext,
                        uniqueInt, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationChannel = NotificationChannel(
                        channelId,
                        "My Notifications",
                        NotificationManager.IMPORTANCE_MAX
                    )

                    // Configure the notification channel.
                    notificationChannel.description = "Channel description"
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.RED
                    notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                    notificationChannel.enableVibration(true)
                    notificationManager.createNotificationChannel(notificationChannel)
                }
                val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
                notificationBuilder.setAutoCancel(true)
                    // .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingNotificationIntent)
                    .setSmallIcon(R.drawable.cam_logo)
                    .setTicker(context.resources.getString(R.string.app_name)) //     .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentInfo("Info")

                notificationManager.notify( /*notification id*/1, notificationBuilder.build())
            }
        }

//        when (role) {
//            1 -> {
//                val notificationIntent = Intent(mContext, MainActivityAdmin::class.java)
//                notificationIntent.putExtra("NotificationMessage", json)
//                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                val pendingNotificationIntent = PendingIntent.getActivity(
//                    mContext,
//                    uniqueInt,
//                    notificationIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT
//                )
//                // val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.school_bell)
//                // Log.i(TAG,"soundUri $soundUri")
//                val notification: Notification
//                mBuilder.setSmallIcon(icon).setTicker(title)
//                    .setAutoCancel(true)
//                    .setContentTitle(title)
//                    .setContentIntent(pendingNotificationIntent)
//                    // .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                    .setStyle(inboxStyle)
//                    .setSmallIcon(R.drawable.cam_logo)
//                    .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
//                    .setContentText(message)
//                    // .setSound(soundUri)
//                    .setPriority(Notification.PRIORITY_HIGH)
//                    .build().also { notification = it }
//                notificationManager.notify(0, notification)
//                // playNotificationSound(mContext)
//            }
//
////            5 -> {
////                val notificationIntent = Intent(mContext, MainActivityAdmin::class.java)
////                notificationIntent.putExtra("NotificationMessage", json)
////                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
////                val pendingNotificationIntent = PendingIntent.getActivity(
////                    mContext,
////                    uniqueInt,
////                    notificationIntent,
////                    PendingIntent.FLAG_UPDATE_CURRENT
////                )
////                // val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.school_bell)
////                // Log.i(TAG,"soundUri $soundUri")
////                val notification: Notification
////                mBuilder.setSmallIcon(icon).setTicker(title)
////                    .setAutoCancel(true)
////                    .setContentTitle(title)
////                    .setContentIntent(pendingNotificationIntent)
////                    // .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
////                    .setStyle(inboxStyle)
////                    .setSmallIcon(R.drawable.cam_logo)
////                    .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
////                    .setContentText(message)
////                    // .setSound(soundUri)
////                    .setPriority(Notification.PRIORITY_HIGH)
////                    .build().also { notification = it }
////                notificationManager.notify(0, notification)
////                // playNotificationSound(mContext)
////            }
//            3 -> {
//                val notificationIntent = Intent(mContext, MainActivityStaff::class.java)
//                notificationIntent.putExtra("NotificationMessage", json)
//                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                val pendingNotificationIntent = PendingIntent.getActivity(
//                    mContext,
//                    uniqueInt,
//                    notificationIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT
//                )
//
//                // val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.school_bell)
//                // Log.i(TAG,"soundUri $soundUri")
//                val notification: Notification
//                mBuilder.setSmallIcon(icon).setTicker(title)
//                    .setAutoCancel(true)
//                    .setContentTitle(title)
//                    .setContentIntent(pendingNotificationIntent)
//                    // .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                    .setStyle(inboxStyle)
//                    .setSmallIcon(R.drawable.cam_logo)
//                    .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
//                    .setContentText(message)
//                    // .setSound(soundUri)
//                    .setPriority(Notification.PRIORITY_HIGH)
//                    .build().also { notification = it }
//                notificationManager.notify(0, notification)
//                // playNotificationSound(mContext)
//            }
//            4 -> {
//                val notificationIntent = Intent(mContext, MainActivityParent::class.java)
//                notificationIntent.putExtra("NotificationMessage", json)
//                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                val pendingNotificationIntent = PendingIntent.getActivity(
//                    mContext,
//                    uniqueInt,
//                    notificationIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT
//                )
//                // val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.school_bell)
//                // Log.i(TAG,"soundUri $soundUri")
//                val notification: Notification
//                mBuilder.setSmallIcon(icon).setTicker(title)
//                    .setAutoCancel(true)
//                    .setContentTitle(title)
//                    .setContentIntent(pendingNotificationIntent)
//                    // .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                    .setStyle(inboxStyle)
//                    .setSmallIcon(R.drawable.cam_logo)
//                    .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
//                    .setContentText(message)
//                    // .setSound(soundUri)
//                    .setPriority(Notification.PRIORITY_HIGH)
//                    .build().also { notification = it }
//                notificationManager.notify(0, notification)
//                // playNotificationSound(mContext)
//            }
//        }



    }

    private fun showSmallNotification(
        context : Context,
        mBuilder: NotificationCompat.Builder,
        icon: Int, title: String, message: String?,timeStamp: String?, json: Uri
    ) {
        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.addLine(message)

        val uniqueInt = (System.currentTimeMillis() and 0xfffffff).toInt()
        val notificationIntent = Intent(mContext, MainActivityParent::class.java)
           notificationIntent.putExtra("NotificationMessage", json);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingNotificationIntent = PendingIntent.getActivity(
            mContext,
            uniqueInt,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


       // val soundUri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.school_bell)
        val notification: Notification = mBuilder.setSmallIcon(icon).setTicker(title)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentIntent(pendingNotificationIntent)
           // .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setStyle(inboxStyle)
            .setSmallIcon(R.drawable.cam_logo)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    mContext.resources,
                    icon
                )
            ) //     .setContentText(message)
           // .setSound(soundUri)
            .setContentTitle(title) //the "title" value you sent in your notification
            .setContentText(message) //ditto
            .setPriority(Notification.PRIORITY_MAX)
            .build()
        val notificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)


    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    fun getBitmapFromURL(strURL: String?): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    // Playing notification sound
    fun playNotificationSound(mContext :Context) {
        try {
//            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            val r = RingtoneManager.getRingtone(mContext, alarmSound)
//            r.play()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }



            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
            val channelName  = "General Channel"
            val channelDesc = "channel Desc"
            val important = NotificationManagerCompat.IMPORTANCE_HIGH
            val channel  = NotificationChannelCompat.Builder(CHANNEL_ID,important).apply {
                setName(channelName)
                setDescription(channelDesc)

                setSound(Uri.parse("android.resource://" + mContext.packageName+ "/" + R.raw.school_bell),
//                Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${mContext.packageName}/raw/message"),
                    Notification.AUDIO_ATTRIBUTES_DEFAULT
                )
            }

            NotificationManagerCompat.from(mContext).createNotificationChannel(channel.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "FirebaseMessagingService"

        /**
         * Method checks if the app is in background or not
         */
        fun isAppIsInBackground(context: Context): Boolean {
            var isInBackground = true
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                val runningProcesses = am.runningAppProcesses
                for (processInfo in runningProcesses) {
                    if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (activeProcess in processInfo.pkgList) {
                            if (activeProcess == context.packageName) {
                                isInBackground = false
                            }
                        }
                    }
                }
            } else {
                val taskInfo = am.getRunningTasks(1)
                val componentInfo = taskInfo[0].topActivity
                if (componentInfo!!.packageName == context.packageName) {
                    isInBackground = false
                }
            }
            return isInBackground
        }

        // Clears notification tray messages
        fun clearNotifications(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        }

        fun getTimeMilliSec(timeStamp: String?): Long {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            try {
                val date = format.parse(timeStamp)
                return date.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return 0
        }
    }



}