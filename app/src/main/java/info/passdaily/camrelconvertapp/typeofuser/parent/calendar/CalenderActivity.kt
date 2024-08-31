package info.passdaily.camrelconvertapp.typeofuser.parent.calendar

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityCalenderBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import info.passdaily.camrelconvertapp.typeofuser.parent.MainActivityParent
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
@Suppress("DEPRECATION")
class CalenderActivity : AppCompatActivity() {
    var TAG = "PD_NewCalender"
    var compactCalendarView: CompactCalendarView? = null
    var textViewMonth: TextView? = null
    var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    var appBarLayout: AppBarLayout? = null
    var expandedImage: ImageView? = null
    var imageViewBottom: ImageView? = null
    var imageViewPreview: ImageView? = null
    var imageViewNext: ImageView? = null
    private val appBarExpanded = true
    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0

    var mainClass: Int = 0

    private lateinit var binding: ActivityCalenderBinding

    private lateinit var calendarViewModel: CalendarViewModel

    var events: MutableList<Event>? = null
    var events2: MutableList<Event>? = null
    var milliseconds: Long = 0
    var holidayCalendarList: ArrayList<HolidayCalendarList> = ArrayList()
    var activityCalendarList: ArrayList<ActivityCalendarList> = ArrayList()
    var getListFeed: ArrayList<Event> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var studentDBHelper = StudentDBHelper(this)
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID

        calendarViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[CalendarViewModel::class.java]

        binding = ActivityCalenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { // perform whatever you want on back arrow click
            onBackPressed()
        }

        collapsingToolbarLayout = binding.collapsingToolbarLayout
        imageViewPreview = binding.imageViewPreview
        imageViewNext = binding.imageViewNext
        expandedImage = binding.expandedImage
        imageViewBottom = binding.imageViewBottom
        compactCalendarView = binding.compactCalendarView
        textViewMonth = binding.textViewMonth
        compactCalendarView?.setFirstDayOfWeek(Calendar.SUNDAY)
        val date = Calendar.getInstance().time
        @SuppressLint("SimpleDateFormat") val dateFormatMMM: DateFormat = SimpleDateFormat("MMMM")
        @SuppressLint("SimpleDateFormat") val dateFormatYYYY: DateFormat = SimpleDateFormat("yyyy")
        val strMonth = dateFormatMMM.format(date)
        textViewMonth?.text = strMonth + " " + dateFormatYYYY.format(date)
        setBackgroundImg(strMonth)

        // events has size 2 with the 2 events inserted previously
        events = ArrayList<Event>()
        events2 = ArrayList<Event>()

        methodActivityDays()
        methodHolidayDays()


        compactCalendarView?.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                var milliseconds: Long = 0
                @SuppressLint("SimpleDateFormat") val dateFormat: DateFormat =
                    SimpleDateFormat("dd-MM-yyyy")
                val strDate = dateFormat.format(dateClicked!!)
                try {
                    val d = dateFormat.parse(strDate)
                    milliseconds = d!!.time
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                getListFeed = ArrayList<Event>()
                for (i in events!!.indices) {
                    if (milliseconds == events!![i].timeInMillis) {
                        getListFeed.add(events!![i])
                    }
                }
                for (i in events2!!.indices) {
                    if (milliseconds == events2!![i].timeInMillis) {
                        getListFeed.add(events2!![i])
                    }
                }
                if (getListFeed.size != 0) {
                    val bottomSheet = BottomSheetFragment(getListFeed)
                    bottomSheet.show(supportFragmentManager, "TAG")
                } else {
                    Toast.makeText(this@CalenderActivity, "No Events", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                @SuppressLint("SimpleDateFormat") val dateFormat: DateFormat =
                    SimpleDateFormat("yyyy-MMM-dd hh:mm:ss")
                @SuppressLint("SimpleDateFormat") val dateFormatMMM: DateFormat =
                    SimpleDateFormat("MMMM")
                @SuppressLint("SimpleDateFormat") val dateFormatYYYY: DateFormat =
                    SimpleDateFormat("yyyy")
                val strDate = dateFormat.format(firstDayOfNewMonth)
                val strMonth = dateFormatMMM.format(firstDayOfNewMonth)
                textViewMonth?.text = strMonth + " " + dateFormatYYYY.format(firstDayOfNewMonth)
                // Toast.makeText(PD_NewCalender.this,"firstDayOfNewMonth "+strDate , Toast.LENGTH_SHORT).show();
                setBackgroundImg(strMonth)
                //compactCalendarCurrentDayBackgroundColor
                //compactCalendarCurrentSelectedDayBackgroundColor
            }
        })
        imageViewNext?.setOnClickListener { compactCalendarView?.scrollRight() }
        imageViewPreview?.setOnClickListener { compactCalendarView?.scrollLeft() }
        mainClass = 1
    }

    override fun onBackPressed() {
        if(mainClass == 1){
            Global.currentPage = 1
            startActivity(Intent(this, MainActivityParent::class.java))
            finish()
        }else{
            super.onBackPressed()
        }
    }

    fun setBackgroundImg(strMonth: String) {
        when (strMonth) {
            "March", "April", "May" -> {
                expandedImage!!.setImageResource(R.drawable.cal_backimage3)
                imageViewBottom!!.setImageResource(R.drawable.cal_bottom3)
                compactCalendarView?.setCurrentDayBackgroundColor(Color.parseColor("#18B962")) //18B962
                compactCalendarView?.setCurrentSelectedDayBackgroundColor(Color.parseColor("#CC4519")) //CC4519
                getToolbarColor(R.drawable.cal_backimage3)
            }
            "June", "July", "August" -> {
                expandedImage!!.setImageResource(R.drawable.cal_backimage2)
                imageViewBottom!!.setImageResource(R.drawable.cal_bottom2)
                compactCalendarView?.setCurrentDayBackgroundColor(Color.parseColor("#18B962"))
                compactCalendarView?.setCurrentSelectedDayBackgroundColor(Color.parseColor("#00BFE6")) //00BFE6
                getToolbarColor(R.drawable.cal_backimage2)
            }
            "September", "October", "November" -> {
                expandedImage!!.setImageResource(R.drawable.cal_backimage4)
                imageViewBottom!!.setImageResource(R.drawable.cal_bottom4)
                compactCalendarView?.setCurrentDayBackgroundColor(Color.parseColor("#18B962"))
                compactCalendarView?.setCurrentSelectedDayBackgroundColor(Color.parseColor("#FFDA00")) //FFDA00
                getToolbarColor(R.drawable.cal_backimage4)
            }
            "December", "January", "February" -> {
                expandedImage!!.setImageResource(R.drawable.cal_backimage5)
                imageViewBottom!!.setImageResource(R.drawable.cal_bottom5)
                compactCalendarView?.setCurrentDayBackgroundColor(Color.parseColor("#18B962"))
                compactCalendarView?.setCurrentSelectedDayBackgroundColor(Color.parseColor("#00BFE6")) //00BFE6
                getToolbarColor(R.drawable.cal_backimage5)
            }
        }
    }

    private fun getToolbarColor(image: Int) {
        val bitmap: Bitmap = BitmapFactory.decodeResource(applicationContext.resources, image)
        Palette.generateAsync(
            bitmap
        ) { palette ->
            val vibrant = palette!!.vibrantSwatch
            val mutedColor = palette.vibrantSwatch!!.rgb
            if (vibrant != null) {
                // If we have a vibrant color
                // update the title TextView
                collapsingToolbarLayout!!.setBackgroundColor(mutedColor)
                //  mutedColor = palette.getMutedColor(R.attr.colorPrimary);
                //   collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(mutedColor));
                collapsingToolbarLayout!!.setContentScrimColor(palette.getLightMutedColor(mutedColor))
                window!!.statusBarColor = palette.getDarkMutedColor(mutedColor)
            }
        }
    }

    private fun methodActivityDays() {

        calendarViewModel.getActivityList(STUDENTID, ACADEMICID)
            .observe(this, Observer{
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            for(i in response.activityList.indices ) {

                                val date1: Array<String> =
                                    response.activityList[i].aCTIVITYDATE.split("T".toRegex())
                                        .toTypedArray()
                                milliseconds = 0
                                @SuppressLint("SimpleDateFormat") val f =
                                    SimpleDateFormat("dd-MMM-yyyy")
                                try {
                                    val d = f.parse(Utils.parseDateToddMMyyyy(date1[0])!!)
                                    milliseconds = d!!.time
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
                                var title: String = if (response.activityList[i].aCTIVITYDESCRIPTION == "") {
                                    " "
                                } else {
                                    response.activityList[i].aCTIVITYDESCRIPTION
                                }
                                val ev1 = Event(
                                    Color.parseColor("#EB381C"), milliseconds, Utils.dateformat(
                                        date1[0]
                                    ).toString() + "~" + "ACTIVITY_DAY~" + title
                                )
                                events!!.add(ev1)
                                activityCalendarList.add(response)
                            }
                            compactCalendarView?.addEvents(events)
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })

    }

    ///TODO: Holiday json list
    private fun methodHolidayDays() {

        calendarViewModel.getHolidayList(STUDENTID, ACADEMICID)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            for (i in response.holidayList.indices) {

                                val date1: Array<String> =
                                    response.holidayList[i].wORKINGDATE.split("T".toRegex())
                                        .toTypedArray()
                                milliseconds = 0
                                @SuppressLint("SimpleDateFormat") val f =
                                    SimpleDateFormat("dd-MMM-yyyy")
                                try {
                                    val d = f.parse(Utils.parseDateToddMMyyyy(date1[0])!!)
                                    milliseconds = d!!.time
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
                                var title: String =
                                    if (response.holidayList[i].wORKINGDESCRIPTION == "") {
                                        " "
                                    } else {
                                        response.holidayList[i].wORKINGDESCRIPTION
                                    }
                                val ev1 = Event(
                                    Color.parseColor("#038654"), milliseconds, Utils.dateformat(
                                        date1[0]
                                    ).toString() + "~" + "WORKING_DAY~" + title
                                )
                                events2!!.add(ev1)
                                holidayCalendarList.add(response)
                            }
                            compactCalendarView?.addEvents(events2)
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })


    }
}