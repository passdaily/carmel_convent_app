package info.passdaily.camrelconvertapp.services

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.snackbar.Snackbar
import info.passdaily.camrelconvertapp.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.roundToInt


@Suppress("DEPRECATION")
@SuppressLint("ResourceType")
class Utils {
    @SuppressLint("SimpleDateFormat")
    companion object {
        var file1: File? = null
        var reqFile: RequestBody? = null

        private lateinit var outputDirectory: File

        fun generateTransparentColor(color: Int, alpha: Double?): Int {
            val defaultAlpha = 255 // (0 - Invisible / 255 - Max visibility)
            val colorAlpha = alpha?.times(defaultAlpha)?.roundToInt() ?: defaultAlpha
            return ColorUtils.setAlphaComponent(color, colorAlpha)
        }


        private const val maxHeight = 1280.0f
        private const val maxWidth = 1280.0f

        fun removeLastChar(s: String): String{
//returns the string after removing the last character
            return s.substring(0, s.length - 1)
        }



        fun isOnline(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
            return false
        }

        @SuppressLint("SimpleDateFormat")
        fun dateformatAudioyyyyMMddHHmmss(time: String?): String? {
            val inputPattern = "yyyyMMddHHmmss"
            val outputPattern = "MMM dd, yyyy hh:mm a"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time!!)
                str = outputFormat.format(date!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }

        @SuppressLint("SimpleDateFormat")
        fun parseDateToddMMMyyyy(time: String?): String? {
            val inputPattern = "yyyy-MM-dd"
            val outputPattern = "dd/MMM/yyyy"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = ""
            try {
                date = inputFormat.parse(time!!)
                str = outputFormat.format(date!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }

        fun datedifference(time: String?): String? {
            val inputPattern = "MMM d, yyyy"
            val outputPattern = "yyyy-MM-dd"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time!!)
                str = outputFormat.format(date!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }

        fun parseDateToMMMDDYYYY(time: String?): String? {
            val inputPattern = "MMM dd,yyyy"
            val outputPattern = "MM/dd/yyyy"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }


        fun parseDateToDDMMYYYY(inputPattern : String,outputPattern : String,time: String?): String? {
//            val inputPattern = "yyyy-MM-dd"
//            val outputPattern = "dd/MM/yyyy"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }


        //2023-10-14T14:36:57.59
        @SuppressLint("SimpleDateFormat")
        fun parseDateTime(time: String?): String? {
            val inputPattern = "HH:mm:ss"
            val outputPattern = "hh:mm aa"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = ""
            try {
                date = inputFormat.parse(time!!)
                str = outputFormat.format(date!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }


        fun parseDateToDDMMMYYYY(time: String?): String? {
            val inputPattern = "MMM dd,yyyy"
            val outputPattern = "dd/MMM/yyyy"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }


        @SuppressLint("SimpleDateFormat")
        fun dateformat(time: String?): String? {
            val inputPattern = "yyyy-MM-dd"
            val outputPattern = "MMM dd, yyyy"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time!!)
                str = outputFormat.format(date!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }


        fun formateTimeMap(time: Long): String? {
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = time
            val now = Calendar.getInstance()
            val timeFormatString = "hh:mm aa"
            val dateTimeFormatString = "hh:mm aa"
            val HOURS = (60 * 60 * 60).toLong()
            return DateFormat.format("hh:mm aa", smsTime).toString()
        }

        @SuppressLint("SimpleDateFormat")
        fun dateformatYYYYMMdd(time: String?): String? {
            val inputPattern = "MMM dd, yyyy"
            val outputPattern = "yyyy/MM/dd"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(time!!)
                str = outputFormat.format(date!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }


        fun longconversion(datetime: String?): Long {
            var milliseconds: Long = 0
            try {
                val cal = Calendar.getInstance()
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                cal.time = sdf.parse(datetime)
                milliseconds = cal.timeInMillis
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return milliseconds
        }

        fun longConversionTime(datetime: String?): Long {
            var milliseconds: Long = 0
            try {
                val cal = Calendar.getInstance()
                val sdf = SimpleDateFormat("HH:mm:ss")
                cal.time = sdf.parse(datetime)
                milliseconds = cal.timeInMillis
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return milliseconds
        }


        ///Only Date
        fun formattedDate(smsTimeInMilis: Long): String {
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = smsTimeInMilis
            val now = Calendar.getInstance()
            val timeFormatString = "h:mm aa"
            val dateTimeFormatString = "dd / MM / yyyy"
            val HOURS = (60 * 60 * 60).toLong()
            return if (now[Calendar.DATE] == smsTime[Calendar.DATE] && now[Calendar.MONTH] == smsTime[Calendar.MONTH]
                && now[Calendar.YEAR] == smsTime[Calendar.YEAR]
            ) {
                "Today, " + DateFormat.format(timeFormatString, smsTime)
            } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
                //            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else {
                DateFormat.format("dd / MM / yyyy", smsTime).toString()
            }
        }

        ///only Time
        fun formattedTime(smsTimeInMilis: Long): String {
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = smsTimeInMilis
            val now = Calendar.getInstance()
            val timeFormatString = "hh:mm aa"
            val dateTimeFormatString = "hh:mm aa"
            val HOURS = (60 * 60 * 60).toLong()
            return if (now[Calendar.DATE] == smsTime[Calendar.DATE] && now[Calendar.MONTH] == smsTime[Calendar.MONTH]
                && now[Calendar.YEAR] == smsTime[Calendar.YEAR]
            ) {
                DateFormat.format("hh:mm aa", smsTime).toString()
            } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
                //            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else {
                DateFormat.format("hh:mm aa", smsTime).toString()
            }
        }


        ///only Time
        fun formattedTimehmma(smsTimeInMilis: Long): String {
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = smsTimeInMilis
            val now = Calendar.getInstance()
            val timeFormatString = "h:mm a"
            val dateTimeFormatString = "h:mm a"
            val HOURS = (60 * 60 * 60).toLong()
            return if (now[Calendar.DATE] == smsTime[Calendar.DATE] && now[Calendar.MONTH] == smsTime[Calendar.MONTH]
                && now[Calendar.YEAR] == smsTime[Calendar.YEAR]
            ) {
                DateFormat.format("h:mm a", smsTime).toString()
            } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
                //            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else {
                DateFormat.format("h:mm a", smsTime).toString()
            }
        }


        ///only Time
        fun formattedTimeHHmmss(smsTimeInMilis: Long): String {
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = smsTimeInMilis
            val now = Calendar.getInstance()
            val timeFormatString = "HH:mm:ss"
            val dateTimeFormatString = "HH:mm:ss"
            val HOURS = (60 * 60 * 60).toLong()
            return if (now[Calendar.DATE] == smsTime[Calendar.DATE] && now[Calendar.MONTH] == smsTime[Calendar.MONTH]
                && now[Calendar.YEAR] == smsTime[Calendar.YEAR]
            ) {
                DateFormat.format("hh:mm:ss", smsTime).toString()
            } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
                //            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else {
                DateFormat.format("HH:mm:ss", smsTime).toString()
            }
        }

        ///////////Both date and Time
        fun formattedDateTime(smsTimeInMilis: Long): String {
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = smsTimeInMilis
            val now = Calendar.getInstance()
            val timeFormatString = "h:mm aa"
            val dateTimeFormatString = "dd, MMM yyyy, hh:mm aa"
            val HOURS = (60 * 60 * 60).toLong()
            return if (now[Calendar.DATE] == smsTime[Calendar.DATE] && now[Calendar.MONTH] == smsTime[Calendar.MONTH]
                && now[Calendar.YEAR] == smsTime[Calendar.YEAR]
            ) {
                "Today, " + DateFormat.format(timeFormatString, smsTime)
            } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
                //            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else {
                DateFormat.format("dd, MMM yyyy, hh:mm aa", smsTime).toString()
            }
        }

        ///////////Both date and Time
        fun formattedDateTime1(smsTimeInMilis: Long): String {
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = smsTimeInMilis
            val now = Calendar.getInstance()
            val timeFormatString = "h:mm aa"
            val dateTimeFormatString = "dd, MMM yyyy, hh:mm a"
            val HOURS = (60 * 60 * 60).toLong()
            return if (now[Calendar.DATE] == smsTime[Calendar.DATE] && now[Calendar.MONTH] == smsTime[Calendar.MONTH]
                && now[Calendar.YEAR] == smsTime[Calendar.YEAR]
            ) {
                DateFormat.format("dd, MMM yyyy, hh:mm a", smsTime).toString()
            } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
                //            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
                DateFormat.format("dd, MMM yyyy, hh:mm a", smsTime).toString()
            } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
                DateFormat.format("dd, MMM yyyy, hh:mm a", smsTime).toString()
            } else {
                DateFormat.format("dd, MMM yyyy, hh:mm a", smsTime).toString()
            }
        }

        /////For Inbox notification
        fun longconversionInbox(datetime: String?): Long {
            var milliseconds: Long = 0
            try {
                val cal = Calendar.getInstance()
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                cal.time = sdf.parse(datetime)
                milliseconds = cal.timeInMillis
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return milliseconds
        }

        //
        ///////////For Inbox notification
        fun formattedDateTimedd(smsTimeInMilis: Long): String {
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = smsTimeInMilis
            val now = Calendar.getInstance()
            val timeFormatString = "h:mm aa"
            val dateTimeFormatString = "dd / MMM / yyyy"
            val HOURS = (60 * 60 * 60).toLong()
            return if (now[Calendar.DATE] == smsTime[Calendar.DATE] && now[Calendar.MONTH] == smsTime[Calendar.MONTH]
                && now[Calendar.YEAR] == smsTime[Calendar.YEAR]
            ) {
                return "Today"
            } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
                return "Yesterday"
                //   DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else {
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            }
        }


        ///////////For Inbox notification
        fun formattedDateTimeInbox(smsTimeInMilis: Long): String {
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = smsTimeInMilis
            val now = Calendar.getInstance()
            val timeFormatString = "h:mm aa"
            val dateTimeFormatString = "dd / MMM / yyyy"
            val HOURS = (60 * 60 * 60).toLong()
            return if (now[Calendar.DATE] == smsTime[Calendar.DATE] && now[Calendar.MONTH] == smsTime[Calendar.MONTH]
                && now[Calendar.YEAR] == smsTime[Calendar.YEAR]
            ) {
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
                return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
                //   DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else {
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            }
        }


        ///Only Date in Words
        fun formattedDateWords(smsTimeInMilis: Long): String {
            val smsTime = Calendar.getInstance()
            smsTime.timeInMillis = smsTimeInMilis
            val now = Calendar.getInstance()
            val timeFormatString = "h:mm aa"
            val dateTimeFormatString = "MMM dd, yyyy"
            val HOURS = (60 * 60 * 60).toLong()
            return if (now[Calendar.DATE] == smsTime[Calendar.DATE] && now[Calendar.MONTH] == smsTime[Calendar.MONTH]
                && now[Calendar.YEAR] == smsTime[Calendar.YEAR]
            ) {
                DateFormat.format("MMM dd, yyyy", smsTime).toString()
            } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
                //            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
                DateFormat.format(dateTimeFormatString, smsTime).toString()
            } else {
                DateFormat.format("MMM dd, yyyy", smsTime).toString()
            }
        }


        /////validation                 todo
        fun validateFieldIsEmpty(value: String?): Boolean {
            return TextUtils.isEmpty(value)
        }

        fun validateMobile(value: String?): Boolean {
            return value?.length != 10
        }






        fun parseDateToddMMyyyy(time: String?): String? {
            val inputPattern = "yyyy-MM-dd"
            val outputPattern = "dd-MMM-yyyy"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var date: Date? = null
            var str: String? = ""
            try {
                date = inputFormat.parse(time)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }


        fun setStatusBarColor(activity: Activity) {
            val window = activity.window
            //  window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#FFFFFF")
        }

        fun setStatusBarColorZoom(activity: Activity) {
            val window = activity.window
            //  window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#EBF2F9");
        }

        fun calculateDuration(duration: Int): String? {
            var finalDuration = ""
            val minutes = TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
            val seconds = TimeUnit.MILLISECONDS.toSeconds(duration.toLong())
            if (minutes == 0L) {
                finalDuration = "0 : $seconds"
            } else {
                if (seconds >= 60) {
                    val sec = seconds - minutes * 60
                    finalDuration = "$minutes : $sec"
                }
            }
            return finalDuration
        }

        fun resultFun(response: String): String {
            var rESULT = ""
            try {
                val jsonObj = JSONObject(response)
                rESULT = jsonObj.getString("RESULT")
            } catch (err: JSONException) {
                Log.d("Error", err.toString());
            }
            return rESULT

        }
        fun resultUpFun(response: String): String {
            var rESULT = ""
            try {
                val jsonObj = JSONObject(response)
                rESULT = jsonObj.getString("Result")
            } catch (err: JSONException) {
                Log.d("Error", err.toString());
            }
            return rESULT

        }


        fun timeConversion(totalSeconds: Int): String? {
            val MINUTES_IN_AN_HOUR = 60
            val SECONDS_IN_A_MINUTE = 60
            val seconds = totalSeconds % SECONDS_IN_A_MINUTE
            val totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE
            val minutes = totalMinutes % MINUTES_IN_AN_HOUR
            val hours = totalMinutes / MINUTES_IN_AN_HOUR
            val HH = (if (hours < 10) "0" else "") + hours
            val MM = (if (minutes < 10) "0" else "") + minutes
            val SS = (if (seconds < 10) "0" else "") + seconds
            return "$HH:$MM:$SS"
        }


        fun getSnackBar4K(context: Context, message: String, constraintLayout: ConstraintLayout?) {
            val snackBar = Snackbar.make(constraintLayout!!, message, Snackbar.LENGTH_SHORT)
                .setDuration(3000)
            val view = snackBar.view
            val params = view.layoutParams as CoordinatorLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            val textView =
                view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            view.setBackgroundColor(context.resources.getColor(R.color.red_300))
            textView.setTextColor(Color.WHITE)

            val typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
            textView.typeface = Typeface.create(typeface, Typeface.NORMAL)
            textView.textSize = 16f


            snackBar.show()
        }


        fun getSnackBarGreen(
            context: Context,
            message: String,
            constraintLayout: ConstraintLayout?
        ) {
            val snackBar = Snackbar.make(constraintLayout!!, message, Snackbar.LENGTH_SHORT)
                .setDuration(4000)
            val view = snackBar.view
            val params = view.layoutParams as CoordinatorLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            val textView =
                view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            view.setBackgroundColor(context.resources.getColor(R.color.green_300))
            textView.setTextColor(Color.WHITE)

            val typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
            textView.typeface = Typeface.create(typeface, Typeface.NORMAL)
            textView.textSize = 16f


            snackBar.show()
        }


        fun selectedImageConversion(
            imageName: String?,
            contentURI: Uri,
            context: Context
        ): MultipartBody.Part {
            val result: String?
            val cursor = context.contentResolver.query(contentURI, null, null, null, null)
            cursor?.moveToFirst()
            val idx: Int? = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor?.getString(idx!!)
            cursor?.close()
            /////////////
            file1 = File(result!!)
            reqFile = file1!!.asRequestBody("image/*".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData(
                imageName!!,
                file1?.name,
                reqFile!!
            )
        }

        fun compressImage(imagePath: String?,context : Context): String {
            var scaledBitmap: Bitmap? = null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            var bmp = BitmapFactory.decodeFile(imagePath, options)
            var actualHeight : Float = options.outHeight.toFloat()
            var actualWidth : Float  = options.outWidth.toFloat()
            var imgRatio = actualWidth / actualHeight
            val maxRatio: Float = maxWidth / maxHeight
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                when {
                    imgRatio < maxRatio -> {
                        imgRatio = maxHeight / actualHeight
                        actualWidth = (imgRatio * actualWidth)
                        actualHeight = maxHeight
                    }
                    imgRatio > maxRatio -> {
                        imgRatio =
                            maxWidth / actualWidth
                        actualHeight = (imgRatio * actualHeight)
                        actualWidth = maxWidth
                    }
                    else -> {
                        actualHeight = maxHeight
                        actualWidth = maxWidth
                    }
                }
            }
            options.inSampleSize = calculateInSampleSize(options, actualWidth.toInt(), actualHeight.toInt())
            options.inJustDecodeBounds = false
            options.inDither = false
            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)
            try {
                bmp = BitmapFactory.decodeFile(imagePath, options)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth.toInt(), actualHeight.toInt(), Bitmap.Config.RGB_565)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            val ratioX = actualWidth / options.outWidth.toFloat()
            val ratioY = actualHeight / options.outHeight.toFloat()
            val middleX = actualWidth / 2.0f
            val middleY = actualHeight / 2.0f
            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
            val canvas = Canvas(scaledBitmap!!)
            canvas.setMatrix(scaleMatrix)
            canvas.drawBitmap(
                bmp!!,
                middleX - bmp.width / 2,
                middleY - bmp.height / 2,
                Paint(Paint.FILTER_BITMAP_FLAG)
            )
            bmp.recycle()
            val exif: ExifInterface
            try {
                exif = ExifInterface(imagePath!!)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
                val matrix = Matrix()
                Log.i("Utils","orientation $orientation")
                when (orientation) {
                    6 -> {
                        matrix.postRotate(90f)
                    }
                    3 -> {
                        matrix.postRotate(180f)
                    }
                    8 -> {
                        matrix.postRotate(270f)
                    }
                    7 -> {
                        matrix.postRotate(270f)
                    }
                }
                scaledBitmap = Bitmap.createBitmap(
                    scaledBitmap,
                    0,
                    0,
                    scaledBitmap.width,
                    scaledBitmap.height,
                    matrix,
                    true
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            var out: FileOutputStream? = null
            val filepath = getFilename(context)
            try {
                out = FileOutputStream(filepath)

                //write the compressed bitmap at the destination specified by filename.
                scaledBitmap!!.compress(Bitmap.CompressFormat.WEBP, 80, out)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
            return filepath
        }

        fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
                val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }
            val totalPixels = (width * height).toFloat()
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
            return inSampleSize
        }

        fun getFilename(context: Context): String {
//            val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
//                    + "/Teachdaily/File/Pack")

            outputDirectory = getOutputDirectory(context)

            if(!outputDirectory.exists()) {
                outputDirectory = getOutputDirectory(context)
            }

            // Create the storage directory if it does not exist
//            if (!mediaStorageDir.exists()) {
//                mediaStorageDir.mkdirs()
//            }
            val mImageName =
                "IMG_" + System.currentTimeMillis().toString() + ".jpg"
            return outputDirectory.absolutePath + "/" + mImageName
        }


        fun getOutputDirectory(context: Context): File {
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, context.resources.getString(R.string.file_name)+"/compress/pack/").apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else context.filesDir
        }

        fun checkDatesAfter(fromDate: String, toDate: String): Boolean {
            var dfDate = SimpleDateFormat("yyyy-MM-dd")
            var b = false
            when {
                dfDate.parse(fromDate)!!.before(dfDate.parse(toDate)) -> {
                    b = true
                }
                dfDate.parse(fromDate)!! == dfDate.parse(toDate) -> {
                    b = true;//If two dates are equal
                }
                dfDate.parse(fromDate)!!.after(dfDate.parse(toDate)) -> {
                    b = false;//If two dates are equal
                }
            }
            return b
        }


        fun checkDateTime(fromDate: String, toDate: String, note: String): Boolean {
            var dfDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var b = false
            if (note == "After") {
                when {
                    dfDate.parse(fromDate)!!.after(dfDate.parse(toDate)) -> {
                        b = true
                    }
                }
            }
            if (note == "Before") {
                when {
                    dfDate.parse(fromDate)!!.before(dfDate.parse(toDate)) -> {
                        b = true
                    }
                }
            }
            if (note == "Equal") {
                when {
                    dfDate.parse(fromDate)!! == dfDate.parse(toDate) -> {
                        b = true;//If two dates are equal
                    }
                }
            }
            return b
        }


        fun checkOnlyTime(fromDate: String, toDate: String, note: String): Boolean {
            var dfDate = SimpleDateFormat("h:mm a")
            var b = false
            if (note == "After") {
                when {
                    dfDate.parse(fromDate)!!.after(dfDate.parse(toDate)) -> {
                        b = true
                    }
                }
            }
            if (note == "Before") {
                when {
                    dfDate.parse(fromDate)!!.before(dfDate.parse(toDate)) -> {
                        b = true
                    }
                }
            }
            if (note == "Equal") {
                when {
                    dfDate.parse(fromDate)!! == dfDate.parse(toDate) -> {
                        b = true;//If two dates are equal
                    }
                }
            }
            return b
        }



        //	public static String compressImage(String imagePath) {
        //		final float maxHeight = 1280.0f;
        //		final float maxWidth = 1280.0f;
        //		Bitmap scaledBitmap = null;
        //
        //		BitmapFactory.Options options = new BitmapFactory.Options();
        //		options.inJustDecodeBounds = true;
        //		Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        //
        //		int actualHeight = options.outHeight;
        //		int actualWidth = options.outWidth;
        //
        //		float imgRatio = (float) actualWidth / (float) actualHeight;
        //		float maxRatio = maxWidth / maxHeight;
        //
        //		if (actualHeight > maxHeight || actualWidth > maxWidth) {
        //			if (imgRatio < maxRatio) {
        //				imgRatio = maxHeight / actualHeight;
        //				actualWidth = (int) (imgRatio * actualWidth);
        //				actualHeight = (int) maxHeight;
        //			} else if (imgRatio > maxRatio) {
        //				imgRatio = maxWidth / actualWidth;
        //				actualHeight = (int) (imgRatio * actualHeight);
        //				actualWidth = (int) maxWidth;
        //			} else {
        //				actualHeight = (int) maxHeight;
        //				actualWidth = (int) maxWidth;
        //
        //			}
        //		}
        //
        //		options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        //		options.inJustDecodeBounds = false;
        //		options.inDither = false;
        //		options.inPurgeable = true;
        //		options.inInputShareable = true;
        //		options.inTempStorage = new byte[16 * 1024];
        //
        //		try {
        //			bmp = BitmapFactory.decodeFile(imagePath, options);
        //		} catch (OutOfMemoryError exception) {
        //			exception.printStackTrace();
        //
        //		}
        //		try {
        //			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        //		} catch (OutOfMemoryError exception) {
        //			exception.printStackTrace();
        //		}
        //
        //		float ratioX = actualWidth / (float) options.outWidth;
        //		float ratioY = actualHeight / (float) options.outHeight;
        //		float middleX = actualWidth / 2.0f;
        //		float middleY = actualHeight / 2.0f;
        //
        //		Matrix scaleMatrix = new Matrix();
        //		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        //
        //		Canvas canvas = new Canvas(scaledBitmap);
        //		canvas.setMatrix(scaleMatrix);
        //		canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        //
        //		if(bmp!=null)
        //		{
        //			bmp.recycle();
        //		}
        //
        //		ExifInterface exif;
        //		try {
        //			exif = new ExifInterface(imagePath);
        //			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        //			Matrix matrix = new Matrix();
        //			if (orientation == 6) {
        //				matrix.postRotate(90);
        //			} else if (orientation == 3) {
        //				matrix.postRotate(180);
        //			} else if (orientation == 8) {
        //				matrix.postRotate(270);
        //			}
        //			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        //		} catch (IOException e) {
        //			e.printStackTrace();
        //		}
        //		FileOutputStream out = null;
        //		String filepath = getFilename();
        //		try {
        //			out = new FileOutputStream(filepath);
        //
        //			//write the compressed bitmap at the destination specified by filename.
        //			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        //
        //		} catch (FileNotFoundException e) {
        //			e.printStackTrace();
        //		}
        //
        //		return filepath;
        //	}
        //
        //	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //		final int height = options.outHeight;
        //		final int width = options.outWidth;
        //		int inSampleSize = 1;
        //
        //		if (height > reqHeight || width > reqWidth) {
        //			final int heightRatio = Math.round((float) height / (float) reqHeight);
        //			final int widthRatio = Math.round((float) width / (float) reqWidth);
        //			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        //		}
        //		final float totalPixels = width * height;
        //		final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        //
        //		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        //			inSampleSize++;
        //		}
        //
        //		return inSampleSize;
        //	}
        //
        //	public static String getFilename() {
        //		File mediaStorageDir = new File(Environment.getExternalStorageDirectory().toString() +
        //				"/Passdaily/File/Pack");
        //
        //		// Create the storage directory if it does not exist
        //		if (! mediaStorageDir.exists()){
        //			mediaStorageDir.mkdirs();
        //		}
        //
        //		String mImageName="IMG_"+ String.valueOf(System.currentTimeMillis()) +".jpg";
        //		String uriString = (mediaStorageDir.getAbsolutePath() + "/"+ mImageName);;
        //		return uriString;
        //
        //	}

        fun getRootDirPath(context: Context): String? {
            return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                val file: File =
                    ContextCompat.getExternalFilesDirs(context.applicationContext, null)[0]
                file.absolutePath
            } else {
                context.applicationContext.filesDir.absolutePath
            }
        }

        fun getProgressDisplayLine(currentBytes: Long, totalBytes: Long): String? {
            return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(
                totalBytes
            )
        }

        private fun getBytesToMBString(bytes: Long): String? {
            return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00))
        }

        fun getFileFromUri(contentResolver: ContentResolver, uri: Uri, directory: File): File {
            val file =
                File.createTempFile("suffix", "prefix", directory)
            file.outputStream().use {
                contentResolver.openInputStream(uri)?.copyTo(it)
            }

            return file
        }


        //	public static void deleteCache(Context context) {
        //		try {
        //			File dir = context.getCacheDir();
        //			deleteDir(dir);
        //		} catch (Exception e) {}
        //	}
        //	public static boolean deleteDir(File dir) {
        //		if (dir != null && dir.isDirectory()) {
        //			String[] children = dir.list();
        //			for (int i = 0; i < children.length; i++) {
        //				boolean success = deleteDir(new File(dir, children[i]));
        //				if (!success) {
        //					return false;
        //				}
        //			}
        //			return dir.delete();
        //		} else if(dir!= null && dir.isFile()) {
        //			return dir.delete();
        //		} else {
        //			return false;
        //		}
        //	}
        fun getFileName(filename: String?, context: Context): File {
            val direct =
                File(Environment.getExternalStorageDirectory().toString() + "Passdaily/encryp/")
            if (!direct.exists()) {
                if (!direct.mkdir()) {
                    Log.i("ERROR", "Cannot create a directory!")
                } else {
                    direct.mkdirs()
                }
            }
//            val folder = File(context.filesDir.absolutePath + File.separator + "/encryp/")
//            if (!folder.exists()) {
//                if (!folder.mkdir()) {
//                    Log.i("ERROR", "Cannot create a directory!")
//                } else {
//                    folder.mkdirs()
//                }
//            }
            Log.i("mkdir", "folder ${File(direct, filename!!)}")
            return File(direct, filename)
        }


        fun hideFocusListener(view: View, context: Context) {
            val key = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            key?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }

        fun hideFocusListener(activity: Activity) {
            val key = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            key?.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }


        fun formatSeconds(i: Int): String? {
            val str: String
            val i2 = i % 3600
            val i3 = i2 % 60
            val floor = Math.floor((i2 / 60).toDouble()).toInt()
            val floor2 = Math.floor((i / 3600).toDouble()).toInt()
            val sb = StringBuilder()
            var str2 = "0"
            sb.append(if (floor2 < 10) str2 else "")
            sb.append(floor2)
            val sb2 = sb.toString()
            val sb3 = StringBuilder()
            str = if (floor < 10) {
                str2
            } else {
                ""
            }
            sb3.append(str)
            sb3.append(floor)
            val sb4 = sb3.toString()
            val sb5 = StringBuilder()
            if (i3 >= 10) {
                str2 = ""
            }
            sb5.append(str2)
            sb5.append(i3)
            val sb6 = sb5.toString()
            return "$sb2:$sb4:$sb6"
        }


        private const val SECOND_MILLIS = 1000
        private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
        private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
        private const val DAY_MILLIS = 24 * HOUR_MILLIS


        fun getTimeAgo(time: Long, ctx: Context?): String? {
            var time = time
            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000
            }
            val now: Long = System.currentTimeMillis()
            if (time > now || time <= 0) {
                return null
            }

            // TODO: localize
            val diff = now - time
            return when {
                diff < MINUTE_MILLIS -> {
                    "Now"
                }
                diff < 2 * MINUTE_MILLIS -> {
                    "a minute ago"
                }
                diff < 50 * MINUTE_MILLIS -> {
                    "$diff / $MINUTE_MILLIS minutes ago"
                }
                diff < 90 * MINUTE_MILLIS -> {
                    "an hour ago"
                }
                diff < 24 * HOUR_MILLIS -> {
                    "$diff / $HOUR_MILLIS hours ago"
                }
//                diff < 48 * HOUR_MILLIS -> {
//                    "yesterday"
//                }
                else -> {
                    val smsTime = Calendar.getInstance()
                    smsTime.timeInMillis = time
                    DateFormat.format("MMM dd, yyyy", smsTime).toString()
                }
            }
        }


        fun isValidInputPoint(input: String?): Boolean {
           // val inputPattern = "^\\d+(\\.\\d)?\\d*\$"
            val inputPattern = "^\\d+(\\.\\d)?\\d*\$|(a|A)(b|B)(s|S)+\$|(n|N)(i|I)(l|L)"
                //"/^\\d*\\.?\\d*\$/"
                //"/^(0|[1-9]\\d*)(\\.\\d+)?\$/"
              //  "^\\d{0,3}(\\.\\d{1,2})+$|^[1-9]{2,3}+$|^[0-9]+$|(a|A)(b|B)(s|S)+$|(a|A)(b|B)+$|(n|N)(i|I)(l|L)"

            //^\d{0,2}(\.\d{1,2})?$
            val pattern = Pattern.compile(inputPattern)
            val matcher = pattern.matcher(input)
            return matcher.matches()
        }


        internal class DecimalDigitsInputFilter(digitsBeforeZero: Int, digitsAfterZero: Int) :
            InputFilter {
            private val mPattern: Pattern
            override fun filter(
                source: CharSequence,
                start: Int,
                end: Int,
                dest: Spanned,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val matcher: Matcher = mPattern.matcher(dest)
                return if (!matcher.matches()) "" else null
            }

            init {
                mPattern =
                    Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
            }
        }


        fun getPathFromUri(context: Context, uri: Uri): String? {
            val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                    }

                    // TODO handle non-primary volumes
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                    return getDataColumn(context, contentUri, null, null)
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":").toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(
                        split[1]
                    )
                    return getDataColumn(context, contentUri, selection, selectionArgs)
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {

                // Return the remote address
                return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                    context,
                    uri,
                    null,
                    null
                )
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }
            return null
        }

        fun getDataColumn(
            context: Context, uri: Uri?, selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(
                column
            )
            try {
                cursor = context.contentResolver.query(
                    uri!!, projection, selection, selectionArgs,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    val index: Int = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }


        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority
        }


    } //////////// object function end here

}/// class end here

fun calculateDuration(duration: Int): String {
    var finalDuration = ""
    val minutes = TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
    val seconds = TimeUnit.MILLISECONDS.toSeconds(duration.toLong())
    if (minutes == 0L) {
        finalDuration = "0 : $seconds"
    } else {
        if (seconds >= 60) {
            val sec = seconds - minutes * 60
            finalDuration = "$minutes : $sec"
        }
    }
    return finalDuration
}