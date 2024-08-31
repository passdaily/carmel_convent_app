package info.passdaily.camrelconvertapp.firebase

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentFCMHelper
import info.passdaily.camrelconvertapp.typeofuser.admin.MainActivityAdmin
import info.passdaily.camrelconvertapp.typeofuser.parent.MainActivityParent
import info.passdaily.camrelconvertapp.typeofuser.staff.MainActivityStaff


@Suppress("DEPRECATION")
class MyFirebaseMessagingService :  FirebaseMessagingService() {

    private var TAG = "FirebaseMessagingService"
    private lateinit var notificationManager: NotificationManager
    private val ADMIN_CHANNEL_ID = "ServiceNow"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, token)
    }
    //fnx8LkELThWh-7MPMBt6N0:APA91bEA5yxlZj_akNX_4IkDecUS8eEq1lY0bjLBoURXGtib7q34jH4OegKEuuZyWC5W_sUw0Cga7RjEZ9-dXpQdn_8zfYVkgKLAVkot_nNJ1qhebspnXDI7Yt_iotqu3Ab6Je7KQMWH

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
    //    super.onMessageReceived(remoteMessage)
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.i(TAG, "From: ${remoteMessage.from}")
//
//        // Check if the message contains a data payload.
//        remoteMessage.data.let {
//            Log.i(TAG, "Message data payload: ${remoteMessage.data.entries}")
//            Log.i(TAG, "Message data payload: ${remoteMessage.rawData}")
//            Log.i(TAG, "Message data payload: ${remoteMessage.from}")
//            Log.i(TAG, "Message data payload: ${remoteMessage.messageId}")
//            Log.i(TAG, "Message data payload: ${remoteMessage.messageType}")
//            Log.i(TAG, "Message data payload: ${remoteMessage.notification?.title}")
//            Log.i(TAG, "Message data payload: ${remoteMessage.notification?.body}")
//        }

      //  showNotificationMessage(applicationContext,remoteMessage.notification?.title,remoteMessage.notification?.body,"","")

//        Log.i(TAG, "notification data payload : " + remoteMessage.notification.toString())
//
//        Log.i(TAG, "notification body : " + remoteMessage.notification!!.body)
//        Log.i(TAG, "notification title : " + remoteMessage.notification!!.title)
//        Log.i(TAG, "notification imageUrl : " + remoteMessage.notification!!.imageUrl)
//        Log.i(TAG, "notification icon : " + remoteMessage.notification!!.icon)
//
//        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

//        val resultIntent = Intent(applicationContext, MainActivityParent::class.java)
//        resultIntent.putExtra("body", remoteMessage.notification!!.body)
//        resultIntent.putExtra("title", remoteMessage.notification!!.title)
//        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        // check for image attachment
//        showNotificationMessage(applicationContext,
//            remoteMessage.notification!!.body,remoteMessage.notification!!.title)


        
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            ////{Inbox=2,0,9,0, Assignment/Home Work,TEST CLASS1 - Biology - Crop Production and Management is Updated.
                // Please go to Assignment/Home Work option To View}

            Log.i(TAG, "Data Payload: " + remoteMessage.data.toString())
            val strsplit = remoteMessage.data.toString()
            val strsplit1 = strsplit.split("=".toRegex()).toTypedArray()
            Log.i(TAG, "Data Payload1: " + strsplit1[0] + "--" + strsplit1[1])
            val secondstr = strsplit1[1].replace("}", "")
            handleDataMessage(secondstr)
        }


    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun handleDataMessage(json: String) {

        Log.i(TAG, "push json: $json")
        try {
            val newTre: Array<String> = json.split("~".toRegex()).toTypedArray()

            Log.i(TAG,"Json Size ${newTre.size}")
            var localDBHelper = LocalDBHelper(applicationContext)
            var user = localDBHelper.viewUser()
            var adminId = user[0].ADMIN_ID

            Log.i(TAG,"ADMIN_ROLE ${user[0].ADMIN_ROLE}")
            Log.i(TAG,"STAFF_ID ${user[0].STAFF_ID}")
            Log.i(TAG,"PLOGIN_ID ${user[0].PLOGIN_ID}")
            Log.i(TAG,"BASE_URL ${user[0].BASE_URL}")
            Log.i(TAG,"LOGIN_ROLE ${user[0].LOGIN_ROLE}")


            /////Admin role for admin is 5 and notification payload data size is 4
            if(newTre.size == 4 && user[0].ADMIN_ROLE == 5) {
                // check for image attachment
                var staffId = user[0].STAFF_ID
                if (newTre[0] == "1") {
                    Log.i(TAG, "STUDENT_ID $staffId")
                    if (staffId == newTre[1].toInt()) {
                        showNotificationMessage(applicationContext, newTre[2], newTre[3], newTre[3], json,1)
                    }
                }  else if (newTre[0] == "2") {
                    showNotificationMessage(applicationContext, newTre[2], newTre[3], newTre[3], json,1)
                }
            }
            /////Admin role for admin is 1 and notification payload data size is 4
            else if(newTre.size == 4 && user[0].ADMIN_ROLE == 1) {
                // check for image attachment
                var staffId = user[0].STAFF_ID
                if (newTre[0] == "1") {
                    Log.i(TAG, "STUDENT_ID $staffId")
                    if (staffId == newTre[1].toInt()) {
                        showNotificationMessage(applicationContext, newTre[2], newTre[3], newTre[3], json,1)
                    }
                }  else if (newTre[0] == "2") {
                    showNotificationMessage(applicationContext, newTre[2], newTre[3], newTre[3], json,1)
                }
            }
            /////Admin role for staff is 3 and notification payload data size is 4
            else if(newTre.size == 4 && user[0].ADMIN_ROLE == 3) {
                // check for image attachment
                var staffId = user[0].STAFF_ID
                if (newTre[0] == "1") {
                    Log.i(TAG, "STUDENT_ID $staffId")
                    if (staffId == newTre[1].toInt()) {
                        showNotificationMessage(applicationContext, newTre[2], newTre[3], newTre[3], json,3)
                    }
                }  else if (newTre[0] == "2") {
                    showNotificationMessage(applicationContext, newTre[2], newTre[3], newTre[3], json,3)
                }
            }
            /////Admin role for parent is 4 and notification payload data size is 6
            else if(newTre.size ==  6 && user[0].ADMIN_ROLE == 4){
                val repo = StudentFCMHelper(applicationContext)
                Log.i(TAG, "newTre0 ${newTre[0]}")
                Log.i(TAG, "newTre1 ${newTre[1]}")
                if (newTre[0] == "1") {
                    var student = repo.getProductByStudent(newTre[1])
                    Log.i(TAG, "STUDENT_ID ${student.STUDENT_ID}")
                    if (student.STUDENT_ID == newTre[1].toInt()) {
                        showNotificationMessage(applicationContext, newTre[4], newTre[5], newTre[5], json,4) }
                }
                else if (newTre[0] == "2") {
                    Log.i(TAG, "newTre[2] ${newTre[2]}")
                    var student = repo.getProductByClassId(newTre[2])
                    Log.i(TAG, "CLASS_ID ${student.CLASS_ID}")
                    if (student.CLASS_ID == newTre[2].toInt()) {
                        showNotificationMessage(applicationContext, newTre[4], newTre[5], newTre[5], json,4) }
                }
                else if (newTre[0] == "3") {
                    var student = repo.getProductByClassSec(newTre[3])
                    Log.i(TAG, "CLASS_SECTION ${student.CLASS_SECTION}")
                    if (student.CLASS_SECTION == newTre[3]) { showNotificationMessage(applicationContext, newTre[4], newTre[5], newTre[5], json,4) }

                }
                else if (newTre[0] == "4") { showNotificationMessage(applicationContext, newTre[4], newTre[5], newTre[5], json,4) }
            }

        } catch (e: Exception) {
            Log.i(TAG, "Exception: " + e.message)
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    fun setupNotificationChannels() {
        Log.i(TAG, "setupNotificationChannels ");
        val adminChannelName = getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = getString(R.string.notifications_admin_channel_description)

        val adminChannel = NotificationChannel(
            ADMIN_CHANNEL_ID,
            adminChannelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager.createNotificationChannel(adminChannel);

    }


    /**
     * Showing notification with text only
     */
    private fun showNotificationMessage(
        context: Context, title : String?,message : String?,timeStamp : String?,json : String?,role : Int) {

        val notificationUtils = NotificationUtils(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationUtils.showNotificationMessage(context,title!!,message!!,timeStamp!!,json!!,role)
        }
    }
}

