package info.passdaily.camrelconvertapp.typeofuser.parent.objective_video_exam

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.provider.MediaStore
import androidx.lifecycle.Observer
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityOexamVideoExamBinding
import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
import info.passdaily.camrelconvertapp.lib.video.Video_Activity
import info.passdaily.camrelconvertapp.model.CustomImageModel
import info.passdaily.camrelconvertapp.model.ObjectiveOnlineExamStatus
import info.passdaily.camrelconvertapp.model.ObjectiveOptionList
import info.passdaily.camrelconvertapp.model.QuestionListModel
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import info.passdaily.camrelconvertapp.services.retrofit.RetrofitClient
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image.SlideshowDialogFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.objective_exam.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@SuppressLint("RestrictedApi, ClickableViewAccessibility")
class ObjectiveVideoExamActivity : AppCompatActivity(),NumberClickListener, FinishClickListener {
    private lateinit var binding: ActivityOexamVideoExamBinding
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var cameraExecutor: ExecutorService

    var image_path = Global.event_url + "/OExamFile/"

    var onlyForZeroValue = 0

    var toolbar: Toolbar? = null
//    var chronometer : Chronometer? = null

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0

    var drawerToggle: ActionBarDrawerToggle? = null
    var drawerLayout: DrawerLayout? = null
    var recyclerViewNumberList: RecyclerView? = null

    var constraintFirstLayout: ConstraintLayout? = null

    /////Navigation bar question list
    var recyclerViewQuestionList: RecyclerView? = null
    var imageViewCloseNavBar: ImageView? = null
    var textViewAnswered: TextView? = null
    var textViewTotQuestion: TextView? = null
    var textViewNotAnswered: TextView? = null

    var textStudentName: TextView? = null
    var textClassName: TextView? = null

    var imageLeftArrow: ConstraintLayout? = null
    var imageRightArrow: ConstraintLayout? = null

    var textViewQuestion: TextView? = null
    var textViewQuestionNo: TextView? = null
    var textMarksForQuestion: TextView? = null

    var textTimerHours: TextView? = null
    var textTimerMinutes: TextView? = null
    var textTimerSeconds: TextView? = null

    var imageViewQuestion: ImageView? = null
    var cardViewAudioQuestion: CardView? = null
    var imageViewVideoQuestion: ImageView? = null
    var mediaPlayer: MediaPlayer? = null
    var mSeekBar: SeekBar? = null
    var seekHandler = Handler()
    var run: Runnable? = null
    var playPauseImageView: ImageView? = null
    var songDuration: TextView? = null
    var playPauseRelative: RelativeLayout? = null

    var submitButton: AppCompatButton? = null
    var finishButton: AppCompatButton? = null
    var textClearAnswer : TextView? = null

    var imageViewPause: ConstraintLayout? = null

    var radioGroup: RadioGroup? = null

    var itemPosition = 0
    var selectedItemId = false

    var OEXAM_ATTEMPT_ID: Int = 0
    var IS_AUTO_ENDED: Int = 0
    var IS_SUBMITTED: Int = 0
    var STUDENT_NAME: String? = null
    var CLASS_NAME: String? = null
    var OEXAM_DURATION: Long = 0
    var START_TIME: String? = null
    var END_TIME: String? = null
    var TIME_NOW: String? = null
    var PAUSED_COUNT: Int = 0
    var STATUS: String? = null
    var TOTAL_OUTOFF_MARK: String? = null
    var TOTAL_ANSWERED_QUESTIONS: String? = null
    var ATTEMPTED_ON: String? = null
    var ALLOWED_PAUSE: Int = 0
    var OEXAM_ATTEMPT_STATUS: String? = null
    var OEXAM_NAME: String? = null
    var OEXAM_DESCRIPTION: String? = null
    var SUBJECT_NAME = ""
    var OEXAM_ID: Int = 18
    var RESULT: String? = null
    var ELAPSED_TIME: Int = 0
    var JSON_ERROR: String? = null

    var END_QUESTION_TIME = 0

    var GET_START_TIME_WHEN_ACTIVITY_STARTS = 0

    var tOTALQUESTIONS = 0
    var aNSWEREDQUESTIONS = 0
    var TO_BE_ANSWERED: Int = 0

    var textViewQuestionNumber : TextView? = null

    var aNSWEREDOPTIONID: Int = 0

    private lateinit var oExamAreaViewModel: OExamAreaViewModel

    private var responseOption = ArrayList<ObjectiveOptionList.Option>()

    var responseAttemptedOption = ArrayList<ObjectiveOptionList.AttemptedOption>()

    var questionList = ArrayList<QuestionListModel.Question>()

    private var mTimerRunning = false
    private var mStartTimeInMillis: Long = 0
    private var mTimeLeftInMillis: Long = 0
    private var mEndTime: Long = 0
    private var mCountDownTimer: CountDownTimer? = null

    lateinit var adapter: NumberAdapter
    lateinit var questionListAdapter: QuestionListAdapter

   // var viewFinder: PreviewView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        oExamAreaViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[OExamAreaViewModel::class.java]

        var studentDBHelper = StudentDBHelper(this)
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID

        binding = ActivityOexamVideoExamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.appBarExam.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = OEXAM_NAME
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow_white)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //   toolbar.setTitle("About-Us");
//            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
//                onBackPressed()
//            })
        }
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
//        chronometer = binding.chronometer
//      //  viewFinder = viewBinding.viewFinder
//        binding.videoCaptureButton.setOnClickListener {
//            chronometer!!.start()
//            chronometer!!.base = SystemClock.elapsedRealtime()
//            captureVideo() }

        cameraExecutor = Executors.newSingleThreadExecutor()


        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navViewContainer
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        // Initialize the action bar drawer toggle instance
        drawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.appBarExam.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close,
        ) {

        }

        drawerToggle?.isDrawerIndicatorEnabled = false
        drawerLayout?.addDrawerListener(drawerToggle!!)
        drawerToggle?.syncState()

        constraintFirstLayout = binding.appBarExam.constraintFirstLayout


        recyclerViewNumberList = binding.appBarExam.recyclerViewNumberList
        recyclerViewNumberList?.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        imageLeftArrow = binding.appBarExam.imageLeftArrow
        imageRightArrow = binding.appBarExam.imageRightArror

        submitButton = binding.appBarExam.submitButton
        finishButton = binding.appBarExam.finishButton
     //   imageViewPause = binding.appBarExam.imageViewPause

        textClearAnswer = binding.appBarExam.textClearAnswer

        imageViewQuestion = binding.appBarExam.imageViewQuestion
        cardViewAudioQuestion = binding.appBarExam.cardViewAudioQuestion
        imageViewVideoQuestion = binding.appBarExam.imageViewVideoQuestion
        playPauseImageView = binding.appBarExam.playPauseImageView
        mSeekBar = binding.appBarExam.mSeekBar
        songDuration = binding.appBarExam.songDuration

        ////relative Layout for click event
        playPauseRelative = binding.appBarExam.playPauseRelative

        mediaPlayer = MediaPlayer()

        textViewQuestion = binding.appBarExam.textViewQuestion
        textViewQuestionNo = binding.appBarExam.textViewQuestionNo
        textMarksForQuestion = binding.appBarExam.textMarksForQuestion


        textTimerHours = binding.appBarExam.textTimerHours
        textTimerMinutes = binding.appBarExam.textTimerMinutes
        textTimerSeconds = binding.appBarExam.textTimerSeconds


        radioGroup = binding.appBarExam.radioGroup

        textStudentName = binding.appBarExam.textStudentName
        textClassName = binding.appBarExam.textClassName
        textStudentName?.text = Global.studentName
        textClassName?.text = CLASS_NAME
        ///Nav item
        recyclerViewQuestionList = binding.navListLayout.recyclerViewQuestionList
        recyclerViewQuestionList?.layoutManager = LinearLayoutManager(this)

        textViewAnswered = binding.navListLayout.textViewAnswered
        textViewNotAnswered = binding.navListLayout.textViewNotAnswered
        textViewTotQuestion = binding.navListLayout.textViewTotQuestion

        imageViewCloseNavBar = binding.navListLayout.imageViewCloseNavBar
        imageViewCloseNavBar?.setOnClickListener {
            drawerLayout?.closeDrawer(GravityCompat.END)

        }

        imageLeftArrow?.setOnClickListener {
            if (itemPosition <= 0) {
                Utils.getSnackBar4K(this,"No Question before this",constraintFirstLayout!!)
            } else {
                ///radioButton selection item id
                selectedItemId = false
                itemPosition--
                onNumberClick(itemPosition, questionList[itemPosition], 3,"")
            }
        }


        imageRightArrow?.setOnClickListener {
            if ((itemPosition + 1) == questionList.size) {
                Log.i(TAG, "No Skip Question")
                Utils.getSnackBar4K(this,"No Question after this",constraintFirstLayout!!)
            } else {
                ///radioButton selection item id
                selectedItemId = false
                itemPosition++
                onNumberClick(itemPosition, questionList[itemPosition], 3,"")
            }
        }

        submitButton?.setOnClickListener {
            (radioGroup?.checkedRadioButtonId != -1).also { selectedItemId = it }

            if (selectedItemId) { submitAndNextFunction(questionList[itemPosition], elaspeTimeFun()) } else {
                Utils.getSnackBar4K(this,"Please Choose one option",constraintFirstLayout!!)
            }

        }

        finishButton?.setOnClickListener {

           // pauseTimer()
            val bottomSheet = ExamFinishSheetFragment(this,tOTALQUESTIONS, aNSWEREDQUESTIONS, TO_BE_ANSWERED,1)
            bottomSheet.isCancelable = false
            bottomSheet.show(supportFragmentManager, "TAG")

        }


        textClearAnswer?.setOnClickListener {

            (radioGroup?.checkedRadioButtonId != -1).also { selectedItemId = it }

            if (selectedItemId) { clearAnswerMethod(questionList[itemPosition], aNSWEREDOPTIONID) } else {
                Utils.getSnackBar4K(this,"Please Choose one option",constraintFirstLayout!!)
            }

        }

        getOnlineExamStatus(ACADEMICID, CLASSID, STUDENTID, OEXAM_ID)

    }


    private fun clearAnswerMethod(question: QuestionListModel.Question, aNSWEREDOPTIONID: Int) {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("OEXAM_ATTEMPT_ID", question.oEXAMATTEMPTID)
            jsonObject.put("QUESTION_ID", question.qUESTIONID)
            jsonObject.put("ANSWERED_OPTION_ID", aNSWEREDOPTIONID)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")

        val optionItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        oExamAreaViewModel.getDeleteOption(optionItems)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(Utils.resultFun(response) == "SUCCESS"){
                                selectedItemId = false
                                radioGroup?.clearCheck()
                                onNumberClick(itemPosition, questionList[itemPosition], 3,"")
                            }else{
                                Toast.makeText(
                                    this,
                                    "Failed While clearing this answer",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(
                                this,
                                "Please try again after sometime",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })


    }


    private fun submitAndNextFunction(question: QuestionListModel.Question, elapsedTime: Int) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("OEXAM_ATTEMPT_ID", question.oEXAMATTEMPTID)
            jsonObject.put("QUESTION_ID", question.qUESTIONID)
            jsonObject.put("ANSWERED_OPTION_ID", aNSWEREDOPTIONID)
            jsonObject.put("ELAPSED_TIME", elapsedTime)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")
        val submitAndNextItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        oExamAreaViewModel.getSubmitAndNext(submitAndNextItems)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(Utils.resultFun(response) == "SUCCESS"){
                                if ((itemPosition + 1) == questionList.size) {
                                    onNumberClick(itemPosition, questionList[itemPosition], 3,"lastValue")
                                   // pauseTimer()
                                } else {
                                    ///radioButton selection item id
                                    selectedItemId = false
                                    itemPosition++
                                    onNumberClick(itemPosition, questionList[itemPosition], 3,"")
                                }
                                GET_START_TIME_WHEN_ACTIVITY_STARTS = END_QUESTION_TIME
                                // START_QUESTION_TIME = END_QUESTION_TIME
                            }else{
                                Toast.makeText(
                                    this,
                                    "Failed While submitting this answer",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(
                                this,
                                "Please try again after sometime",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })

    }


    private fun getOnlineExamStatus(ACADEMICID: Int, CLASSID: Int, STUDENTID: Int, OEXAM_ID: Int) {

        val apiInterface = RetrofitClient.create().getOnlineExamStatus(
            ACADEMICID,
            CLASSID,
            STUDENTID, OEXAM_ID
        )
        apiInterface.enqueue(object : Callback<ObjectiveOnlineExamStatus> {
            override fun onResponse(
                call: Call<ObjectiveOnlineExamStatus>,
                response: Response<ObjectiveOnlineExamStatus>
            ) {
                if (response.isSuccessful) {
                    RESULT = response.body()!!.rESULT
                    OEXAM_ATTEMPT_ID = response.body()!!.oEXAMATTEMPTID
                    OEXAM_DURATION = response.body()!!.oEXAMDURATION
                    ELAPSED_TIME = response.body()!!.eLAPSEDTIME
                    JSON_ERROR = response.body()!!.jSONERROR

                }
                when (RESULT) {
                    "SUCCESS" -> {

                        Log.i(TAG, "start ELAPSED_TIME $ELAPSED_TIME")
                        mTimeLeftInMillis = OEXAM_DURATION * 60000
                        Log.i(TAG, "millisInput $mTimeLeftInMillis")
//
//
                        val elapsedMillis = ELAPSED_TIME * 1000
                        Log.i(TAG, "minute $elapsedMillis")
                        val millisInputTotalTimeElapsedTime = mTimeLeftInMillis - elapsedMillis
                        setTime(millisInputTotalTimeElapsedTime)

                        /////////////// Start Time
                        val c = Calendar.getInstance()
                        //     System.out.println("Current time => "+c.getTime());
                        val df = java.text.SimpleDateFormat("hh:mm:ss")
                        val formattedDate = df.format(c.time)
                        val tokens = formattedDate.split(":".toRegex()).toTypedArray()
                        val hours1 = tokens[0].toInt()
                        val minutes1 = tokens[1].toInt()
                        val seconds1 = tokens[2].toInt()
                        GET_START_TIME_WHEN_ACTIVITY_STARTS = seconds1 + minutes1 * 60 + hours1 * 3600
                        Log.i(TAG, "Start_here GET_START_TIME_WHEN_ACTIVITY_STARTS $GET_START_TIME_WHEN_ACTIVITY_STARTS")

                        startTimer()

                        getTotalQuestions(OEXAM_ATTEMPT_ID)

                    }"EXAM_FINISHED", "EXAM_TIME_OVER" -> {
                        val bottomSheet = ExamFinishSheetFragment(this@ObjectiveVideoExamActivity,tOTALQUESTIONS, aNSWEREDQUESTIONS, TO_BE_ANSWERED,1)
                        bottomSheet.isCancelable = false
                        bottomSheet.show(supportFragmentManager, "TAG")
                    }
                    else -> {
                        Log.i(TAG, "Failed")
                    }
                }
            }
            override fun onFailure(call: Call<ObjectiveOnlineExamStatus>, t: Throwable) {
                Log.i("ProductFragment", "t $t")
            }
        })


    }

    private fun setTime(milliseconds: Long) {
        mStartTimeInMillis = milliseconds
        resetTimer()
    }

    private fun startTimer() {
//        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountDownText()

            }

            override fun onFinish() {
                mTimerRunning = false
                //  updateWatchInterface();
            }
        }.start()
        mTimerRunning = true
        // updateWatchInterface();
    }

    private fun pauseTimer() {
        mCountDownTimer?.cancel()
        mTimerRunning = false
        //  updateWatchInterface();
    }

    private fun resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis
        updateCountDownText()

    }


    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun updateCountDownText() {
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60
        val minutes = (mTimeLeftInMillis / (1000 * 60) % 60).toInt()
        val hours = (mTimeLeftInMillis / (1000 * 60 * 60) % 24).toInt()

//        val timeLeftFormatted: String
//        if (hours > 0) {
        textTimerHours?.text = String.format(Locale.getDefault(), "%02d", hours)
        textTimerMinutes?.text = String.format(Locale.getDefault(), "%02d", minutes)
        textTimerSeconds?.text = String.format(Locale.getDefault(), "%02d", seconds)

        if (String.format(Locale.getDefault(), "%02d", hours) == "00"
            && String.format(Locale.getDefault(), "%02d", minutes) == "00"
            && String.format(Locale.getDefault(), "%02d", seconds) <= "01") {

            val c = Calendar.getInstance()
            val df = java.text.SimpleDateFormat("hh:mm:ss")
            val formattedDate = df.format(c.time)
            val tokens = formattedDate.split(":".toRegex()).toTypedArray()
            val hours1 = tokens[0].toInt()
            val minutes1 = tokens[1].toInt()
            val seconds1 = tokens[2].toInt()

            val pAUSEDELAPSED: Int = seconds1 + minutes1 * 60 + hours1 * 3600
            Log.i(TAG, "GET_START_TIME_WHEN_ACTIVITY_STARTS_PAUSE $GET_START_TIME_WHEN_ACTIVITY_STARTS")
            Log.i(TAG, "PAUSED_ELAPSED_PAUSE $pAUSEDELAPSED")
            val elapsedTime = pAUSEDELAPSED - GET_START_TIME_WHEN_ACTIVITY_STARTS
            Log.i(TAG, "Elapsed_Time $elapsedTime")

            val jsonObject = JSONObject()
            try {
                jsonObject.put("OEXAM_ID", OEXAM_ID)
                jsonObject.put("OEXAM_ATTEMPT_ID", OEXAM_ATTEMPT_ID)
                jsonObject.put("ELAPSED_TIME", elapsedTime)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Log.i(TAG,"jsonObject $jsonObject")

            val doAutoEndItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


            oExamAreaViewModel.getDoAutoEnd(doAutoEndItems)
                .observe(this, Observer {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                val response = resource.data?.body()!!
                                if(Utils.resultFun(response) == "SUCCESS"){
                                    Log.i(TAG, "Auto end success ")
                                    captureVideo()
                                }else{
                                    Log.i(TAG, "Auto end Failed ")
                                }
                            }
                            Status.ERROR -> {
                                Toast.makeText(
                                    this,
                                    "Please try again after sometime",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            Status.LOADING -> {
                                Log.i(TAG,"loading")
                            }
                        }
                    }
                })
            val bottomSheet = ExamFinishSheetFragment(this,tOTALQUESTIONS, aNSWEREDQUESTIONS, TO_BE_ANSWERED,2)
            bottomSheet.isCancelable = false
            bottomSheet.show(supportFragmentManager, "TAG")
        }

    }


    fun finishExam(title : String){
        val builder = AlertDialog.Builder(this)
        builder.setMessage(title)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->

                captureVideo()
            }
            .setNegativeButton(
                "No"
            ) { dialog, id -> //  Action for 'NO' Button
                dialog.cancel()
            }
        //Creating dialog box
        val alert = builder.create()
        //Setting the title manually
        alert.setTitle("Finish?")
        alert.show()
        val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonbackground.setTextColor(Color.BLACK)

        val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonbackground1.setTextColor(Color.BLACK)
    }

    fun getTotalQuestions(OEXAM_ATTEMPT_ID: Int) {
        oExamAreaViewModel.getQuestionList(
            OEXAM_ID,
            OEXAM_ATTEMPT_ID
        )
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            questionList = response.questionList
                            if (!questionList.isNullOrEmpty()) {
                                textViewQuestionNo?.text = "Question 1"
                                textViewQuestion?.text = questionList[0].qUESTIONTITLE

                                ///radioButton selection item id
                                selectedItemId = false
                                questionTypeFun(questionList[0])
                                getOptionsList(
                                    questionList[0].qUESTIONID,
                                    OEXAM_ATTEMPT_ID
                                )


                                if (questionList[0].tOTALQUESTIONS != null) {
                                    tOTALQUESTIONS =
                                        questionList[0].tOTALQUESTIONS.toString()
                                            .toDouble().roundToInt()
                                }

                                if (questionList[0].aNSWEREDQUESTIONS != null) {
                                    aNSWEREDQUESTIONS =
                                        questionList[0].aNSWEREDQUESTIONS.toString()
                                            .toDouble().roundToInt()
                                    textViewAnswered?.text = aNSWEREDQUESTIONS.toString()
                                } else {
                                    textViewAnswered?.text = onlyForZeroValue.toString()
                                }
                                TO_BE_ANSWERED = tOTALQUESTIONS - aNSWEREDQUESTIONS
                                textViewNotAnswered?.text = TO_BE_ANSWERED.toString()

                                textViewTotQuestion?.text = questionList.size.toString()

                                adapter =
                                    NumberAdapter(
                                        this,
                                        questionList,
                                        applicationContext
                                    )
                                recyclerViewNumberList?.adapter = adapter

                                questionListAdapter =
                                    QuestionListAdapter(
                                        this,
                                        questionList,
                                        applicationContext
                                    )
                                recyclerViewQuestionList?.adapter = questionListAdapter

                            }

                        }
                        Status.ERROR -> {
                            Log.i(TAG, "ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "LOADING")
                        }
                    }
                }
            })
    }

    private fun questionTypeFun(questionArrayList: QuestionListModel.Question) {

        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.reset()
        }
        //  else { mediaPlayer?.reset() }

        imageViewQuestion?.setOnClickListener {
            Global.albumImageList = ArrayList<CustomImageModel>()
            Global.albumImageList.add(
                CustomImageModel(Global.event_url + "/OExamFile/" + questionArrayList.qUESTIONCONTENT)
            )
            val extra = Bundle()
            extra.putInt("position", 0)
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            val newFragment = SlideshowDialogFragment.newInstance()
            newFragment.arguments = extra
            newFragment.show(ft, newFragment.TAG)
        }

        when (questionArrayList.qUESTIONTYPEID) {
            2 -> {
                imageViewQuestion?.visibility = View.VISIBLE
                cardViewAudioQuestion?.visibility = View.GONE
                imageViewVideoQuestion?.visibility = View.GONE
                //  imageView.setImageResource(R.drawable.bird2);
                Glide.with(this)
                    .load(Global.event_url + "/OExamFile/" + questionArrayList.qUESTIONCONTENT)
                    .apply(
                        RequestOptions.fitCenterTransform()
                            .dontAnimate()
                            .placeholder(R.drawable.image_icon)
                    )
                    .thumbnail(0.5f)
                    .into(imageViewQuestion!!)
            }
            3 -> {
                imageViewQuestion?.visibility = View.GONE
                cardViewAudioQuestion?.visibility = View.VISIBLE
                imageViewVideoQuestion?.visibility = View.GONE

                //MediaPlayer.create(context, R.raw.sample_media)
                mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                try {
                    mediaPlayer!!.setDataSource(image_path + questionArrayList.qUESTIONCONTENT)
                    mediaPlayer!!.prepare() // might take long for buffering.
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (ex: IllegalStateException) {
                    ex.printStackTrace()
                }

                mSeekBar!!.max = mediaPlayer!!.duration
                //   mSeekBar.setTag(position);
                //   mSeekBar.setTag(position);
                songDuration?.text =
                    "0 : 0" + " | " + calculateDuration(mediaPlayer!!.duration)
                mSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (mediaPlayer != null && fromUser) {
                            mediaPlayer!!.seekTo(progress)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                })


                playPauseRelative?.setOnClickListener(View.OnClickListener {
                    if (!mediaPlayer!!.isPlaying) {
                        mediaPlayer!!.start()
                        playPauseImageView?.setImageResource(R.drawable.ic_exam_pause)
                        run = Runnable {
                            // Updateing SeekBar every 100 miliseconds
                            mSeekBar!!.progress = mediaPlayer!!.currentPosition
                            seekHandler.postDelayed(run!!, 100)
                            //For Showing time of audio(inside runnable)
                            val miliSeconds = mediaPlayer!!.currentPosition
                            if (miliSeconds != 0) {
                                //if audio is playing, showing current time;
                                val minutes = TimeUnit.MILLISECONDS.toMinutes(miliSeconds.toLong())
                                val seconds = TimeUnit.MILLISECONDS.toSeconds(miliSeconds.toLong())
                                if (minutes == 0L) {
                                    songDuration?.text =
                                        "0 : $seconds | " + calculateDuration(
                                            mediaPlayer!!.duration
                                        )
                                } else {
                                    if (seconds >= 60) {
                                        val sec = seconds - minutes * 60
                                        songDuration?.text =
                                            "$minutes : $sec | " + calculateDuration(
                                                mediaPlayer!!.duration
                                            )
                                    }
                                }
                            } else {
                                //Displaying total time if audio not playing
                                val totalTime = mediaPlayer!!.duration
                                val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime.toLong())
                                val seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime.toLong())
                                if (minutes == 0L) {
                                    songDuration?.text = "0 : $seconds"
                                } else {
                                    if (seconds >= 60) {
                                        val sec = seconds - minutes * 60
                                        songDuration?.text = "$minutes : $sec"
                                    }
                                }
                            }
                        }
                        run!!.run()
                    } else {
                        mediaPlayer!!.pause()
                        playPauseImageView?.setImageResource(R.drawable.ic_exam_play)
                    }
                })

                mediaPlayer!!.setOnCompletionListener {
                    playPauseImageView?.setImageResource(R.drawable.ic_exam_play)
                    mediaPlayer!!.pause()
                }

            }
            4 -> {
                imageViewQuestion?.visibility = View.GONE
                cardViewAudioQuestion?.visibility = View.GONE
                imageViewVideoQuestion?.visibility = View.VISIBLE
                imageViewVideoQuestion?.setOnClickListener {
                    val intent = Intent(this@ObjectiveVideoExamActivity, ExoPlayerActivity::class.java)
                    intent.putExtra("ALBUM_TITLE", "")
                    intent.putExtra("ALBUM_FILE", image_path + questionArrayList.qUESTIONCONTENT)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent)
                }

            }
            else -> {
                imageViewQuestion?.visibility = View.GONE
                cardViewAudioQuestion?.visibility = View.GONE
                imageViewVideoQuestion?.visibility = View.GONE
            }
        }

    }

    fun getOptionsList(qUESTIONID: Int, oEXAMATTEMPTDID: Int) {


//        for (int i=0; i< radio_group.getChildCount(); i++){
//            radio_group.removeViewAt(i);
//        }
        radioGroup?.removeAllViews()
        radioGroup?.clearCheck()
        responseOption = ArrayList<ObjectiveOptionList.Option>()
        responseAttemptedOption = ArrayList<ObjectiveOptionList.AttemptedOption>()
        oExamAreaViewModel.getOptionsList(
            qUESTIONID,
            oEXAMATTEMPTDID
        )
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            responseOption = resource.data?.body()!!.optionList
                            responseAttemptedOption = resource.data.body()!!.attemptedOption

                            val radioButtons = arrayOfNulls<RadioButton>(responseOption.size)
                            val params = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )

                            if (!responseAttemptedOption.isNullOrEmpty()) {
                                for (ii in responseAttemptedOption.indices) {
                                    aNSWEREDOPTIONID = responseAttemptedOption[ii].aNSWEREDOPTIONID
                                }
                                ///radioButton selection item id
                                selectedItemId = true

                                for (i in responseOption.indices) {
                                    radioButtons[i] = RadioButton(this@ObjectiveVideoExamActivity)
                                    radioButtons[i]?.text = responseOption[i].oPTIONTEXT
                                    radioButtons[i]!!.textSize = 16f
                                    radioButtons[i]?.buttonTintList =
                                        ContextCompat.getColorStateList(
                                            this,
                                            R.color.radio_color_code
                                        )
                                    radioButtons[i]!!.id = responseOption[i].oPTIONID
                                    radioButtons[i]!!.tag = responseOption[i]
                                    //  radioButtons[ii].setPadding(8,20,8,8);
                                    if (responseOption[i].oPTIONID == aNSWEREDOPTIONID) { //OPTION_ID
                                        radioButtons[i]!!.isChecked = true
                                        Log.i(TAG, "II $i")
                                    }
                                    // radioButtons[i].setPadding(8,20,8,8);
                                    params.setMargins(8, 40, 8, 8)
                                    radioButtons[i]!!.layoutParams = params
                                    radioGroup?.addView(radioButtons[i])
                                }
                            } else {

                                for (i in responseOption.indices) {
                                    radioButtons[i] = RadioButton(this@ObjectiveVideoExamActivity)
                                    radioButtons[i]?.text = responseOption[i].oPTIONTEXT
                                    radioButtons[i]!!.textSize = 16f
                                    radioButtons[i]?.buttonTintList =
                                        ContextCompat.getColorStateList(
                                            this,
                                            R.color.radio_color_code
                                        )
                                    radioButtons[i]!!.id = responseOption[i].oPTIONID
                                    radioButtons[i]!!.tag = responseOption[i]
                                    // radioButtons[i].setPadding(8,20,8,8);
                                    params.setMargins(8, 40, 8, 8)
                                    radioButtons[i]!!.layoutParams = params
                                    radioGroup?.addView(radioButtons[i])
                                }
                            }

                            radioGroup?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->

                                if (radioGroup?.checkedRadioButtonId == -1) {
                                    Log.i(TAG, "checkedId false")
                                } else {
                                    Log.i(TAG, "checkedId true")
                                    aNSWEREDOPTIONID = radioGroup?.checkedRadioButtonId!!
                                    selectedItemId = true
                                }
                            })

                        }
                        Status.ERROR -> {
                            Log.i(TAG, "ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "LOADING")
                        }
                    }
                }
            })

    }





    override fun onNumberClick(
        position: Int,
        questionArrayList: QuestionListModel.Question,
        type: Int,
        lastValue: String
    ) {
        drawerLayout?.closeDrawer(GravityCompat.END)
        itemPosition = position
        Log.i(TAG, "id $position")

        textViewQuestionNo?.text = "Question ${position + 1}"
        textViewQuestion?.text = questionArrayList.qUESTIONTITLE

        questionTypeFun(questionArrayList)

        getOptionsList(questionArrayList.qUESTIONID, OEXAM_ATTEMPT_ID)
        Log.i(TAG, "before load")
        getQuestionPaletteLoad(questionArrayList.oEXAMID, OEXAM_ATTEMPT_ID,type,position,lastValue)
        ///

    }

    /////
    private fun getQuestionPaletteLoad(oEXAMID: Int, OEXAM_ATTEMPT_ID: Int,type : Int, position: Int,lastValue: String) {

        oExamAreaViewModel.getQuestionList(
            oEXAMID,
            OEXAM_ATTEMPT_ID
        )
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            questionList = response.questionList
                            Log.i(TAG, "inside load")

                            if (questionList[0].tOTALQUESTIONS != null) {
                                tOTALQUESTIONS = questionList[0].tOTALQUESTIONS.toString().toDouble().roundToInt()
                            }
                            if (questionList[0].aNSWEREDQUESTIONS != null) {
                                aNSWEREDQUESTIONS = questionList[0].aNSWEREDQUESTIONS.toString().toDouble().roundToInt()
                                textViewAnswered?.text = aNSWEREDQUESTIONS.toString()
                            } else {
                                textViewAnswered?.text = onlyForZeroValue.toString()
                            }
                            TO_BE_ANSWERED = tOTALQUESTIONS - aNSWEREDQUESTIONS
                            textViewNotAnswered?.text = TO_BE_ANSWERED.toString()

                            questionListAdapter =
                                QuestionListAdapter(
                                    this,
                                    questionList,
                                    applicationContext
                                )
                            recyclerViewQuestionList?.adapter = questionListAdapter

                            if(lastValue =="lastValue"){
                                val bottomSheet = ExamFinishSheetFragment(this,tOTALQUESTIONS, aNSWEREDQUESTIONS, TO_BE_ANSWERED,1)
                                bottomSheet.isCancelable = false
                                bottomSheet.show(supportFragmentManager, "TAG")
                            }

                            when (type) {
                                1 -> {
                                    questionListAdapter.notifyItemRangeChanged(position)
                                    recyclerViewQuestionList?.smoothScrollToPosition(position)
                                }
                                2 -> {
                                    adapter.notifyItemRangeChanged(position)
                                    recyclerViewNumberList?.smoothScrollToPosition(position)
                                }
                                else -> {
                                    questionListAdapter.notifyItemRangeChanged(position)
                                    recyclerViewQuestionList?.smoothScrollToPosition(position)
                                    adapter.notifyItemRangeChanged(position)
                                    recyclerViewNumberList?.smoothScrollToPosition(position)
                                }
                            }

                        }
                        Status.ERROR -> {
                            Log.i(TAG, "ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "LOADING")
                        }
                    }
                }
            })

    }



    class NumberAdapter(
        var numberClickListener: NumberClickListener,
        var mList: List<QuestionListModel.Question>,
        var context: Context
    ) : RecyclerView.Adapter<NumberAdapter.ViewHolder>() {
        var index = 0

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewNumber: TextView = view.findViewById(R.id.textViewNumber)
            var cardView: CardView = view.findViewById(R.id.cardView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.objective_number_list, parent, false)
            return ViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return mList.size
        }

        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewNumber.text = (position + 1).toString()

            holder.cardView.setOnClickListener {
                numberClickListener.onNumberClick(position, mList[position], 1,"")
                index = position
                notifyDataSetChanged()
            }


            if (index == position) {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.green_400))
                holder.textViewNumber.setTextColor(context.resources.getColor(R.color.white))
                holder.cardView.cardElevation = 5f
            } else {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.white))
                holder.textViewNumber.setTextColor(context.resources.getColor(R.color.gray_400))
                holder.cardView.cardElevation = 0f
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun notifyItemRangeChanged(id: Int) {
            index = id
            notifyDataSetChanged()
        }

    }


    class QuestionListAdapter(
        var numberClickListener: NumberClickListener,
        var mList: List<QuestionListModel.Question>,
        var context: Context
    ) : RecyclerView.Adapter<QuestionListAdapter.ViewHolder>() {
        var index = 0

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewNumber: TextView = view.findViewById(R.id.textViewNumber)
            var textViewQuestion: TextView = view.findViewById(R.id.textViewQuestion)
            var constraint: ConstraintLayout = view.findViewById(R.id.constraint)
            var cardView: CardView = view.findViewById(R.id.cardView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.question_list_item, parent, false)
            return ViewHolder(itemView)
        }


        override fun getItemCount(): Int {
            return mList.size
        }

        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            if (mList[position].oEXAMATTEMPTDID != null && mList[position].qUESTIONIDATTEMPT != null
                && mList[position].aNSWEREDOPTIONID != null
            ) {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.green_400))
            } else {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.color_chemistry))
            }

            holder.textViewNumber.text = (position + 1).toString()
            holder.textViewQuestion.text = mList[position].qUESTIONTITLE

            holder.constraint.setOnClickListener {
                numberClickListener.onNumberClick(position, mList[position], 3,"")
                index = position
                notifyDataSetChanged()
            }

            if (index == position) {
                holder.constraint.setBackgroundColor(context.resources.getColor(R.color.gray_150))
            } else {
                holder.constraint.setBackgroundColor(context.resources.getColor(R.color.white))
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        fun notifyItemRangeChanged(id: Int) {
            index = id
            notifyDataSetChanged()
        }

    }



    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return
       // binding.videoCaptureButton.isEnabled = false


        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            return
        }
        // create and start a new recording session
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {
                if (PermissionChecker.checkSelfPermission(
                        this@ObjectiveVideoExamActivity,
                        Manifest.permission.RECORD_AUDIO
                    ) ==
                    PermissionChecker.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                //lambda event listener
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
//                        binding.videoCaptureButton.apply {
//                            text = getString(R.string.stop_capture)
//                            isEnabled = true
//                        }
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
//                            chronometer!!.base = SystemClock.elapsedRealtime()
//                            chronometer!!.stop()
                            finish()
                            val msg =
                                "Video capture succeeded: " + "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                            Log.d(TAG, msg)
                        } else {

                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " + "${recordEvent.error}")
                        }
//                        binding.videoCaptureButton.apply {
//                            text = getString(R.string.start_capture)
//                            isEnabled = true
//                        }
                    }
                }
            }
    }

    private fun startCamera() {

//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener(Runnable {
//            val cameraProvider = cameraProviderFuture.get()
//
//            val preview = Preview.Builder().build()
//            val myPreviewView: PreviewView = viewBinding.viewFinder
//            preview.setSurfaceProvider(myPreviewView.surfaceProvider)
//
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            cameraProvider.bindToLifecycle(this, cameraSelector, preview)
//
//        }, ContextCompat.getMainExecutor(this))


        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val myPreviewView: PreviewView = binding.appBarExam.viewFinder
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(myPreviewView.surfaceProvider)
                }
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.LOWEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    override fun onCancelClick() {
       // TODO("Not yet implemented")
    }

    override fun onFinishClick() {
      //  TODO("Not yet implemented")
        captureVideo()

    }

    override fun onViewResult() {
       // TODO("Not yet implemented")
    }


    @SuppressLint("SimpleDateFormat")
    private fun elaspeTimeFun(): Int {
        val c = Calendar.getInstance()
        val df = java.text.SimpleDateFormat("hh:mm:ss")
        val formattedDate = df.format(c.time)
        val tokens = formattedDate.split(":".toRegex()).toTypedArray()
        val hours = tokens[0].toInt()
        val minutes = tokens[1].toInt()
        val seconds = tokens[2].toInt()
        END_QUESTION_TIME = seconds + minutes * 60 + hours * 3600
        Log.i(TAG, "GET_START_TIME_WHEN_ACTIVITY_STARTS_PAUSE $GET_START_TIME_WHEN_ACTIVITY_STARTS")
        Log.i(TAG, "PAUSED_ELAPSED_PAUSE $END_QUESTION_TIME")
        val elaspsTime = END_QUESTION_TIME - GET_START_TIME_WHEN_ACTIVITY_STARTS
        Log.i(TAG, "elaspsTime $elaspsTime")
        return elaspsTime


    }
}
