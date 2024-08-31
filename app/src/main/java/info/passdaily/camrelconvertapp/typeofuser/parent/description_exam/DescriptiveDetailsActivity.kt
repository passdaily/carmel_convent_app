package info.passdaily.camrelconvertapp.typeofuser.parent.description_exam

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityDescriptiveDetailsBinding
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper

@Suppress("DEPRECATION")
class DescriptiveDetailsActivity : AppCompatActivity() {

    var TAG = "DescriptiveDetailsActivity"
    private lateinit var binding: ActivityDescriptiveDetailsBinding

    var EXAM_ATTEMPT_ID: Int = 0
    var IS_AUTO_ENDED: Int = 0
    var IS_SUBMITTED: Int = 0
    var TOTAL_QUESTION:String? = null
    var EXAM_DURATION:Long? = null
    var START_TIME:String? = null 
    var END_TIME:String? = null
    var TIME_NOW:String? = null 
    var PAUSED_COUNT:Int = 0
    var STATUS:String? = null
    var TOTAL_OUTOFF_MARK:String? = null
    var TOTAL_ANSWERED_QUESTIONS:String? = null
    var ATTEMPTED_ON: String? = null
    var ALLOWED_PAUSE:Int = 0
    var EXAM_ATTEMPT_STATUS:String? = null
    var EXAM_NAME: String? = null
    var EXAM_DESCRIPTION:String? = null
    var SUBJECT_NAME = ""
    var EXAM_ID:Int = 0

    ///
    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0

    var toolbar : Toolbar? =null

    var textViewExamName : TextView? = null
    var textViewDesc : TextView? =null
    var textViewDuration :  TextView? =null
    var textViewTotalMark  :  TextView? =null

    var textViewTotQues  :  TextView? =null
    var textViewPause  :  TextView? =null
    var textViewStart  :  TextView? =null
    var textViewEnd  :  TextView? =null
    var textViewAttempted : TextView? =null
    var textViewPauseCount : TextView? =null
    var textAnsweredQuestion : TextView? =null
    var textViewStatus : TextView? =null

    var buttonTakeTest : AppCompatButton? =null
    var buttonResumeTest : AppCompatButton? =null
    var buttonViewResult : AppCompatButton? =null
    var buttonNoInternet : AppCompatButton? =null

    var textViewExamStatus : TextView? =null
    var imageViewExamStatus : ImageView? = null
    var textViewSubjectName  : TextView? =null

    var currentDate = ""

    lateinit var descriptiveDetailViewModel: DescriptiveDetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_descriptive_details)

      //  descriptiveDetailViewModel = ViewModelProvider(this).get(DescriptiveDetailViewModel::class.java)
        var studentDBHelper = StudentDBHelper(this)
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID

        descriptiveDetailViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[DescriptiveDetailViewModel::class.java]

        binding = ActivityDescriptiveDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Descriptive Exam Details"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //   toolbar.setTitle("About-Us");
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }

        textViewSubjectName = binding.textViewSubjectName
        textViewExamName  = binding.textViewExamName
        textViewDesc  = binding.textViewDesc
        textViewDuration  = binding.textViewDuration
        textViewTotalMark  = binding.textViewTotalMark

        textViewTotQues  = binding.textViewTotQues
        textViewPause  = binding.textViewPause
        textViewStart  = binding.textViewStart
        textViewEnd  = binding.textViewEnd

        textViewAttempted  = binding.textViewAttempted
        textViewPauseCount  = binding.textViewPauseCount
        textAnsweredQuestion  = binding.textAnsweredQuestion
        textViewStatus  = binding.textViewStatus

        //
        buttonTakeTest  = binding.buttonTakeTest
        buttonResumeTest  = binding.buttonResumeTest
        buttonViewResult  = binding.buttonViewResult
        buttonNoInternet  = binding.buttonNoInternet

        //
        textViewExamStatus = binding.textViewExamStatus
        imageViewExamStatus = binding.imageViewExamStatus

//        Utils.setStatusBarColor(this)
        Utils.setStatusBarColor(this)
        initMethod()
    }

    @SuppressLint("SetTextI18n")
    private fun initMethod() {

        descriptiveDetailViewModel.getDescriptiveDetails(STUDENTID, Global.descExamId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            var startText = ""
                            var endText = ""
                            var startDateCalculation = ""
                            var endDateCalculation = ""
                            val response = resource.data?.body()
                            if (response != null) {
                                for (i in response.descDetailExam.indices) {
                                    textViewSubjectName?.text = response.descDetailExam[i].sUBJECTNAME
                                    textViewExamName?.text = response.descDetailExam[i].eXAMNAME
                                    textViewDesc?.text = response.descDetailExam[i].eXAMDESCRIPTION

                                    textViewDuration?.text =
                                        "${response.descDetailExam[i].eXAMDURATION} Minutes"
                                    textViewTotQues?.text =
                                        "${response.descDetailExam[i].tOTALQUESTION} Questions"
                                    textViewTotalMark?.text =
                                        "${response.descDetailExam[i].tOTALOUTOFFMARK} Marks"
                                    textViewPause?.text =
                                        "${response.descDetailExam[i].aLLOWEDPAUSE} Times"

                                    if (!response.descDetailExam[i].sTARTTIME.isNullOrBlank()) {
                                        val date: Array<String> =
                                            response.descDetailExam[i].sTARTTIME.split("T".toRegex())
                                                .toTypedArray()
                                        startDateCalculation = date[0] + " " + date[1];
                                        val dddd: Long = Utils.longconversion(startDateCalculation)
                                        startText = Utils.formattedDate(dddd)
                                    }
                                    if (!response.descDetailExam[i].eNDTIME.isNullOrBlank()) {
                                        val date1: Array<String> =
                                            response.descDetailExam[i].eNDTIME.split("T".toRegex())
                                                .toTypedArray()
                                        endDateCalculation = date1[0] + " " + date1[1]
                                        val ddddd: Long = Utils.longconversion(endDateCalculation)
                                        endText = Utils.formattedDate(ddddd)
                                    }
                                    textViewStart?.text = startText
                                    textViewEnd?.text = endText

                                    EXAM_ID =  response.descDetailExam[i].eXAMID
                                    EXAM_NAME =  response.descDetailExam[i].eXAMNAME
                                    SUBJECT_NAME =  response.descDetailExam[i].sUBJECTNAME
                                    EXAM_DURATION =  response.descDetailExam[i].eXAMDURATION
                                    PAUSED_COUNT = response.descDetailExam[i].pAUSEDCOUNT
                                    TIME_NOW = response.descDetailExam[i].tIMENOW
                                    ATTEMPTED_ON = response.descDetailExam[i].aTTEMPTEDON
                                    ALLOWED_PAUSE = response.descDetailExam[i].aLLOWEDPAUSE
                                    EXAM_ATTEMPT_ID = response.descDetailExam[i].eXAMATTEMPTID
                                    IS_AUTO_ENDED = response.descDetailExam[i].iSAUTOENDED
                                    IS_SUBMITTED = response.descDetailExam[i].iSSUBMITTED


                                    if (response.descDetailExam[i].aTTEMPTEDON.isNullOrEmpty()) {
                                        textViewAttempted?.text = "Not Yet"
                                        textViewAttempted?.setTextColor(Color.parseColor("#FF3D00"))
                                    } else {
                                        val dt12 = ATTEMPTED_ON!!.split("T").toTypedArray()
                                        val dtstr12: Long =
                                            Utils.longconversion(dt12[0] + " " + dt12[1])
                                        val datetime12: String = Utils.formattedDateWords(dtstr12)
                                        textViewAttempted?.text = datetime12
                                        textViewAttempted?.setTextColor(Color.parseColor("#FF000000"))
                                    }

                                    textViewPauseCount?.text =
                                        response.descDetailExam[i].pAUSEDCOUNT.toString()
                                    textAnsweredQuestion?.text =
                                        response.descDetailExam[i].tOTALANSWEREDQUESTIONS.toString()
                                    textViewStatus?.text =
                                        response.descDetailExam[i].tOTALANSWEREDQUESTIONS.toString()


//                            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//                            try {
//                                Date date = new Date();
//                                String current_date = dateformat.format(date); // this date & time you can store in database as a string
//                                Log.i(TAG,"Current Date Time : " + current_date);
                                    val currDate = TIME_NOW!!.split("T").toTypedArray()
                                    currentDate = currDate[0] + " " + currDate[1]
                                    val curr_date: Long =
                                        Utils.longconversion(currDate[0] + " " + currDate[1])
                                    val curr_datetime: String = Utils.formattedDate(curr_date)
                                    Log.i(TAG, "curr_datetime : $curr_datetime")


                                    if (PAUSED_COUNT >= ALLOWED_PAUSE) {
                                        textViewStatus?.text = "Completed"
                                        textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
                                        textViewExamStatus?.text = "Exam Completed"
                                        Glide.with(this)
                                            .load(R.drawable.ic_descriptive_finish_test)
                                            .into(imageViewExamStatus!!)
                                        buttonTakeTest?.visibility = View.GONE
                                        buttonResumeTest?.visibility = View.GONE
                                        buttonViewResult?.visibility = View.VISIBLE
                                    } else {
                                        if (endDateCalculation == currentDate || endDateCalculation > currentDate) {
                                            if (EXAM_ATTEMPT_ID == -1 && IS_AUTO_ENDED == -1 && IS_SUBMITTED == -1) {
                                                textViewStatus?.text = "On Progress"
                                                textViewStatus?.setTextColor(Color.parseColor("#1e88e5"))
                                                buttonTakeTest?.visibility = View.VISIBLE
                                                buttonResumeTest?.visibility = View.GONE
                                                buttonViewResult?.visibility = View.GONE
                                                //
                                                textViewExamStatus?.text = "Take Test"
                                                Glide.with(this)
                                                    .load(R.drawable.ic_descriptive_take_test)
                                                    .into(imageViewExamStatus!!)

                                                buttonTakeTest?.setOnClickListener(View.OnClickListener {
                                                    val intent = Intent(this@DescriptiveDetailsActivity,
                                                        DExamAreaActivity::class.java)
                                                    intent.putExtra("SUBJECT_NAME", SUBJECT_NAME)
                                                    intent.putExtra("EXAM_ID", EXAM_ID)
                                                    intent.putExtra("EXAM_DURATION", EXAM_DURATION)
                                                    intent.putExtra("EXAM_NAME", EXAM_NAME)
                                                    intent.putExtra("ALLOWED_PAUSE", ALLOWED_PAUSE)
                                                    intent.putExtra("PAUSED_COUNT", PAUSED_COUNT)
                                                    intent.putExtra("TIME_NOW", TIME_NOW)
                                                    startActivity(intent)
                                                    finish()
                                                })
                                                //"OEXAM_ATTEMPT_ID":>0,"IS_AUTO_ENDED":0,"IS_SUBMITTED":0,
//This particular student already started the exam but not finished, So you can give Button Like RESUME TEST
                                            } else if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == 0 && IS_SUBMITTED == 0) {
                                                textViewStatus?.text = "On Progress"
                                                textViewStatus?.setTextColor(Color.parseColor("#1e88e5"))
                                                buttonTakeTest?.visibility = View.GONE
                                                buttonResumeTest?.visibility = View.VISIBLE
                                                buttonViewResult?.visibility = View.GONE
                                                textViewExamStatus?.text = "Resume Test"
                                                Glide.with(this)
                                                    .load(R.drawable.ic_decriptive_resume_test)
                                                    .into(imageViewExamStatus!!)
                                                buttonResumeTest?.setOnClickListener(View.OnClickListener {
                                                    val intent = Intent(this@DescriptiveDetailsActivity,
                                                        DExamAreaActivity::class.java)
                                                    intent.putExtra("SUBJECT_NAME", SUBJECT_NAME)
                                                    intent.putExtra("EXAM_ID", EXAM_ID)
                                                    intent.putExtra("EXAM_DURATION", EXAM_DURATION)
                                                    intent.putExtra("EXAM_NAME", EXAM_NAME)
                                                    intent.putExtra("ALLOWED_PAUSE", ALLOWED_PAUSE)
                                                    intent.putExtra("PAUSED_COUNT", PAUSED_COUNT)
                                                    intent.putExtra("TIME_NOW", TIME_NOW)
                                                    startActivity(intent)
                                                    finish()
                                                })
                                            } else if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == -1 && IS_SUBMITTED == 0) {
                                                textViewStatus?.text = "Completed"
                                                textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
                                                buttonTakeTest?.visibility = View.GONE
                                                buttonResumeTest?.visibility = View.GONE
                                                buttonViewResult?.visibility = View.VISIBLE
                                                textViewExamStatus?.text = "Exam Completed"
                                                Glide.with(this)
                                                    .load(R.drawable.ic_descriptive_finish_test)
                                                    .into(imageViewExamStatus!!)
                                            } else if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == 0 && IS_SUBMITTED == 1) {
                                                textViewStatus?.text = "Completed"
                                                textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
                                                buttonTakeTest?.visibility = View.GONE
                                                buttonResumeTest?.visibility = View.GONE
                                                buttonViewResult?.visibility = View.VISIBLE
                                                textViewExamStatus?.text = "Exam Completed"
                                                Glide.with(this)
                                                    .load(R.drawable.ic_descriptive_finish_test)
                                                    .into(imageViewExamStatus!!)
                                            } else if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == 1 && IS_SUBMITTED == 1) {
                                                textViewStatus?.text = "Completed"
                                                textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
                                                buttonTakeTest?.visibility = View.GONE
                                                buttonResumeTest?.visibility = View.GONE
                                                buttonViewResult?.visibility = View.VISIBLE
                                                textViewExamStatus?.text = "Exam Completed"
                                                Glide.with(this)
                                                    .load(R.drawable.ic_descriptive_finish_test)
                                                    .into(imageViewExamStatus!!)
                                            }
                                        } else {
                                            if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == 1 && IS_SUBMITTED == 0) {
                                                textViewStatus?.text = "Completed"
                                                textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
                                                buttonTakeTest?.visibility = View.GONE
                                                buttonResumeTest?.visibility = View.GONE
                                                buttonViewResult?.visibility = View.VISIBLE
                                                textViewExamStatus?.text = "Exam Completed"
                                                Glide.with(this)
                                                    .load(R.drawable.ic_descriptive_finish_test)
                                                    .into(imageViewExamStatus!!)
                                            } else if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == 0 && IS_SUBMITTED == 1) {
                                                textViewStatus?.text = "Completed"
                                                textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
                                                buttonTakeTest?.visibility = View.GONE
                                                buttonResumeTest?.visibility = View.GONE
                                                buttonViewResult?.visibility = View.VISIBLE
                                                textViewExamStatus?.text = "Exam Completed"
                                                Glide.with(this)
                                                    .load(R.drawable.ic_descriptive_finish_test)
                                                    .into(imageViewExamStatus!!)
                                            } else if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == 1 && IS_SUBMITTED == 1) {
                                                textViewStatus?.text = "Completed"
                                                textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
                                                buttonTakeTest?.visibility = View.GONE
                                                buttonResumeTest?.visibility = View.GONE
                                                buttonViewResult?.visibility = View.VISIBLE
                                                textViewExamStatus?.text = "Exam Completed"
                                                Glide.with(this)
                                                    .load(R.drawable.ic_descriptive_finish_test)
                                                    .into(imageViewExamStatus!!)
                                            } else {
                                                textViewStatus?.text = "Time Expired"
                                                textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
                                                buttonTakeTest?.visibility = View.GONE
                                                buttonResumeTest?.visibility = View.GONE
                                                buttonViewResult?.visibility = View.VISIBLE
                                                textViewExamStatus?.text = "Time Expired"
                                                Glide.with(this)
                                                    .load(R.drawable.ic_descriptive_finish_test)
                                                    .into(imageViewExamStatus!!)
                                            }
                                        }
                                    }


                                    buttonViewResult?.setOnClickListener{
                                        val intent = Intent(
                                            this@DescriptiveDetailsActivity,
                                            DExamResultActivity::class.java
                                        )
                                        intent.putExtra("EXAM_ID", EXAM_ID)
                                        intent.putExtra("EXAM_ATTEMPT_ID", EXAM_ATTEMPT_ID)
                                        intent.putExtra("ATTEMPTED_ON", ATTEMPTED_ON)
                                        startActivity(intent)
                                        // finish();
                                    }
                                }
                            }
                        }
                        Status.ERROR -> {
                            textViewExamStatus?.text = resources.getString(R.string.no_internet)
                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewExamStatus!!)

                            textViewSubjectName?.text = ""
                            textViewSubjectName?.setBackgroundColor(resources.getColor(R.color.gray_100))
                            textViewExamName?.text = ""
                            textViewExamName?.setBackgroundColor(resources.getColor(R.color.gray_100))
                            textViewDesc?.text =""
                            textViewDesc?.setBackgroundColor(resources.getColor(R.color.gray_100))
                            buttonNoInternet?.visibility =View.VISIBLE

                            textViewDuration?.text = ""
                            textViewTotQues?.text = ""
                            textViewTotalMark?.text =""
                            textViewPause?.text = ""
                            textViewStart?.text = ""
                            textViewEnd?.text = ""

                            textViewDuration?.setBackgroundColor(resources.getColor(R.color.gray_100))
                            textViewTotQues?.setBackgroundColor(resources.getColor(R.color.gray_100))
                            textViewTotalMark?.setBackgroundColor(resources.getColor(R.color.gray_100))
                            textViewPause?.setBackgroundColor(resources.getColor(R.color.gray_100))
                            textViewStart?.setBackgroundColor(resources.getColor(R.color.gray_100))
                            textViewEnd?.setBackgroundColor(resources.getColor(R.color.gray_100))


                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })




//        descriptiveDetailViewModel.getDescriptiveDetails(STUDENTID, Global.descExamId)
//        descriptiveDetailViewModel.getDescriptiveDetailsObservable()
//            .observe(this) {
//                var startText = ""
//                var endText = ""
//                var startDateCalculation = ""
//                var endDateCalculation = ""
//                if (it != null) {
//                    for (i in it.descDetailExam.indices) {
//                        textViewSubjectName?.text = it.descDetailExam[i].sUBJECTNAME
//                        textViewExamName?.text = it.descDetailExam[i].eXAMNAME
//                        textViewDesc?.text = it.descDetailExam[i].eXAMDESCRIPTION
//
//                        textViewDuration?.text = "${it.descDetailExam[i].eXAMDURATION} Minutes"
//                        textViewTotQues?.text = "${it.descDetailExam[i].tOTALQUESTION} Questions"
//                        textViewTotalMark?.text = "${it.descDetailExam[i].tOTALOUTOFFMARK} Marks"
//                        textViewPause?.text = "${it.descDetailExam[i].aLLOWEDPAUSE} Times"
//
//                        if (!it.descDetailExam[i].sTARTTIME.isNullOrBlank()) {
//                            val date: Array<String> =
//                                it.descDetailExam[i].sTARTTIME.split("T".toRegex()).toTypedArray()
//                            startDateCalculation = date[0] + " " + date[1];
//                            val dddd: Long = Utils.longconversion(startDateCalculation)
//                            startText = Utils.formattedDate(dddd)
//                        }
//                        if (!it.descDetailExam[i].eNDTIME.isNullOrBlank()) {
//                            val date1: Array<String> =
//                                it.descDetailExam[i].eNDTIME.split("T".toRegex()).toTypedArray()
//                            endDateCalculation = date1[0] + " " + date1[1]
//                            val ddddd: Long = Utils.longconversion(endDateCalculation)
//                            endText = Utils.formattedDate(ddddd)
//                        }
//                        textViewStart?.text = startText
//                        textViewEnd?.text = endText
//
//                        PAUSED_COUNT = it.descDetailExam[i].pAUSEDCOUNT
//                        TIME_NOW = it.descDetailExam[i].tIMENOW
//                        ATTEMPTED_ON = it.descDetailExam[i].aTTEMPTEDON
//                        ALLOWED_PAUSE = it.descDetailExam[i].aLLOWEDPAUSE
//                        EXAM_ATTEMPT_ID = it.descDetailExam[i].eXAMATTEMPTID
//                        IS_AUTO_ENDED = it.descDetailExam[i].iSAUTOENDED
//                        IS_SUBMITTED = it.descDetailExam[i].iSSUBMITTED
//
//
//                        if (it.descDetailExam[i].aTTEMPTEDON.isNullOrEmpty()) {
//                            textViewAttempted?.text = "Not Yet"
//                            textViewAttempted?.setTextColor(Color.parseColor("#FF3D00"))
//                        } else {
//                            val dt12 = ATTEMPTED_ON!!.split("T").toTypedArray()
//                            val dtstr12: Long = Utils.longconversion(dt12[0] + " " + dt12[1])
//                            val datetime12: String = Utils.formattedDateWords(dtstr12)
//                            textViewAttempted?.text = datetime12
//                            textViewAttempted?.setTextColor(Color.parseColor("#FF000000"))
//                        }
//
//                        textViewPauseCount?.text = it.descDetailExam[i].pAUSEDCOUNT.toString()
//                        textAnsweredQuestion?.text =
//                            it.descDetailExam[i].tOTALANSWEREDQUESTIONS.toString()
//                        textViewStatus?.text =
//                            it.descDetailExam[i].tOTALANSWEREDQUESTIONS.toString()
//
//
////                            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
////                            try {
////                                Date date = new Date();
////                                String current_date = dateformat.format(date); // this date & time you can store in database as a string
////                                Log.i(TAG,"Current Date Time : " + current_date);
//                        val currDate = TIME_NOW!!.split("T").toTypedArray()
//                        currentDate = currDate[0] + " " + currDate[1]
//                        val curr_date: Long = Utils.longconversion(currDate[0] + " " + currDate[1])
//                        val curr_datetime: String = Utils.formattedDate(curr_date)
//                        Log.i(TAG, "curr_datetime : $curr_datetime")
//
//
//                        if (PAUSED_COUNT >= ALLOWED_PAUSE) {
//                            textViewStatus?.text = "Completed"
//                            textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
//                            textViewExamStatus?.text = "Exam Completed"
//                            Glide.with(this)
//                                .load(R.drawable.ic_descriptive_finish_test)
//                                .into(imageViewExamStatus!!)
//                            buttonTakeTest?.visibility = View.GONE
//                            buttonResumeTest?.visibility = View.GONE
//                            buttonViewResult?.visibility = View.VISIBLE
//                        } else {
//                            if (endDateCalculation == currentDate || endDateCalculation > currentDate) {
//                                if (EXAM_ATTEMPT_ID == -1 && IS_AUTO_ENDED == -1 && IS_SUBMITTED == -1) {
//                                    textViewStatus?.text = "On Progress"
//                                    textViewStatus?.setTextColor(Color.parseColor("#1e88e5"))
//                                    buttonTakeTest?.visibility = View.VISIBLE
//                                    buttonResumeTest?.visibility = View.GONE
//                                    buttonViewResult?.visibility = View.GONE
//                                    //
//                                    textViewExamStatus?.text = "Take Test"
//                                    Glide.with(this)
//                                        .load(R.drawable.ic_descriptive_take_test)
//                                        .into(imageViewExamStatus!!)
//
//                                    buttonTakeTest?.setOnClickListener(View.OnClickListener {
////                                        val intent = Intent(
////                                            this@Online_DescrptiveListClick_Page,
////                                            Online_DescriptiveExamPage::class.java)
////                                        intent.putExtra("SUBJECT_NAME", SUBJECT_NAME)
////                                        intent.putExtra("EXAM_ID", EXAM_ID)
////                                        intent.putExtra("EXAM_DURATION", EXAM_DURATION)
////                                        intent.putExtra("EXAM_NAME", EXAM_NAME)
////                                        intent.putExtra("ALLOWED_PAUSE", ALLOWED_PAUSE)
////                                        intent.putExtra("PAUSED_COUNT", PAUSED_COUNT)
////                                        intent.putExtra("TIME_NOW", TIME_NOW)
////                                        startActivity(intent)
////                                        finish()
//                                    })
//                                    //"OEXAM_ATTEMPT_ID":>0,"IS_AUTO_ENDED":0,"IS_SUBMITTED":0,
////This particular student already started the exam but not finished, So you can give Button Like RESUME TEST
//                                } else if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == 0 && IS_SUBMITTED == 0) {
//                                    textViewStatus?.text = "On Progress"
//                                    textViewStatus?.setTextColor(Color.parseColor("#1e88e5"))
//                                    buttonTakeTest?.visibility = View.GONE
//                                    buttonResumeTest?.visibility = View.VISIBLE
//                                    buttonViewResult?.visibility = View.GONE
//                                    textViewExamStatus?.text = "Resume Test"
//                                    Glide.with(this)
//                                        .load(R.drawable.ic_decriptive_resume_test)
//                                        .into(imageViewExamStatus!!)
//                                    buttonResumeTest?.setOnClickListener(View.OnClickListener {
////                                        val intent = Intent(
////                                            this@Online_DescrptiveListClick_Page,
////                                            Online_DescriptiveExamPage::class.java
////                                        )
////                                        intent.putExtra("SUBJECT_NAME", SUBJECT_NAME)
////                                        intent.putExtra("EXAM_ID", EXAM_ID)
////                                        intent.putExtra("EXAM_DURATION", EXAM_DURATION)
////                                        intent.putExtra("EXAM_NAME", EXAM_NAME)
////                                        intent.putExtra("ALLOWED_PAUSE", ALLOWED_PAUSE)
////                                        intent.putExtra("PAUSED_COUNT", PAUSED_COUNT)
////                                        intent.putExtra("TIME_NOW", TIME_NOW)
////                                        startActivity(intent)
////                                        finish()
//                                    })
//                                } else if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == -1 && IS_SUBMITTED == 0) {
//                                    textViewStatus?.text = "Completed"
//                                    textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
//                                    buttonTakeTest?.visibility = View.GONE
//                                    buttonResumeTest?.visibility = View.GONE
//                                    buttonViewResult?.visibility = View.VISIBLE
//                                    textViewExamStatus?.text = "Exam Completed"
//                                    Glide.with(this)
//                                        .load(R.drawable.ic_descriptive_finish_test)
//                                        .into(imageViewExamStatus!!)
//                                } else if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == 0 && IS_SUBMITTED == 1) {
//                                    textViewStatus?.text = "Completed"
//                                    textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
//                                    buttonTakeTest?.visibility = View.GONE
//                                    buttonResumeTest?.visibility = View.GONE
//                                    buttonViewResult?.visibility = View.VISIBLE
//                                    textViewExamStatus?.text = "Exam Completed"
//                                    Glide.with(this)
//                                        .load(R.drawable.ic_descriptive_finish_test)
//                                        .into(imageViewExamStatus!!)
//                                } else if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == 1 && IS_SUBMITTED == 1) {
//                                    textViewStatus?.text = "Completed"
//                                    textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
//                                    buttonTakeTest?.visibility = View.GONE
//                                    buttonResumeTest?.visibility = View.GONE
//                                    buttonViewResult?.visibility = View.VISIBLE
//                                    textViewExamStatus?.text = "Exam Completed"
//                                    Glide.with(this)
//                                        .load(R.drawable.ic_descriptive_finish_test)
//                                        .into(imageViewExamStatus!!)
//                                }
//                            } else {
//                                if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == 1 && IS_SUBMITTED == 0) {
//                                    textViewStatus?.text = "Completed"
//                                    textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
//                                    buttonTakeTest?.visibility = View.GONE
//                                    buttonResumeTest?.visibility = View.GONE
//                                    buttonViewResult?.visibility = View.VISIBLE
//                                    textViewExamStatus?.text = "Exam Completed"
//                                    Glide.with(this)
//                                        .load(R.drawable.ic_descriptive_finish_test)
//                                        .into(imageViewExamStatus!!)
//                                } else if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == 0 && IS_SUBMITTED == 1) {
//                                    textViewStatus?.text = "Completed"
//                                    textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
//                                    buttonTakeTest?.visibility = View.GONE
//                                    buttonResumeTest?.visibility = View.GONE
//                                    buttonViewResult?.visibility = View.VISIBLE
//                                    textViewExamStatus?.text = "Exam Completed"
//                                    Glide.with(this)
//                                        .load(R.drawable.ic_descriptive_finish_test)
//                                        .into(imageViewExamStatus!!)
//                                } else if (EXAM_ATTEMPT_ID > 0 && IS_AUTO_ENDED == 1 && IS_SUBMITTED == 1) {
//                                    textViewStatus?.text = "Completed"
//                                    textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
//                                    buttonTakeTest?.visibility = View.GONE
//                                    buttonResumeTest?.visibility = View.GONE
//                                    buttonViewResult?.visibility = View.VISIBLE
//                                    textViewExamStatus?.text = "Exam Completed"
//                                    Glide.with(this)
//                                        .load(R.drawable.ic_descriptive_finish_test)
//                                        .into(imageViewExamStatus!!)
//                                } else {
//                                    textViewStatus?.text = "Time Expired"
//                                    textViewStatus?.setTextColor(Color.parseColor("#EB381C"))
//                                    buttonTakeTest?.visibility = View.GONE
//                                    buttonResumeTest?.visibility = View.GONE
//                                    buttonViewResult?.visibility = View.VISIBLE
//                                    textViewExamStatus?.text = "Time Expired"
//                                    Glide.with(this)
//                                        .load(R.drawable.ic_descriptive_finish_test)
//                                        .into(imageViewExamStatus!!)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
    }
}