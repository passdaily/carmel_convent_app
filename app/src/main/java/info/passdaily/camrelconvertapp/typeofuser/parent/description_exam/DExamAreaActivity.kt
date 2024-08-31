package info.passdaily.camrelconvertapp.typeofuser.parent.description_exam

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.progressindicator.CircularProgressIndicator
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityDexamAreaBinding
import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.lib.video.Video_Activity
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.FileUtils
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import info.passdaily.camrelconvertapp.services.permission.sdk29AndUp
import info.passdaily.camrelconvertapp.services.retrofit.RetrofitClient
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.CreateAssignmentDialog
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image.SlideshowDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@Suppress("DEPRECATION", "SetTextI18n")
@SuppressLint("NotifyDataSetChanged")
class DExamAreaActivity : AppCompatActivity(), NumberDClickListener,FinishClickListener {
    var TAG = "DExamAreaActivity"

    var Qimage_path = Global.event_url + "/DExamFile/Question/"

    ////http://demo.passdaily.in/DExamFile/Question/fd93916c-0db2-49c4-b9ce-aa56f7fd504a.jpg
    var Aimage_path = Global.event_url + "/DExamFile/Answer/"

    private lateinit var dExamAreaViewModel: DExamAreaViewModel
    private lateinit var binding: ActivityDexamAreaBinding

    var toolbar: Toolbar? = null
    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var onlyForZeroValue = 0


    private var mTimerRunning = false
    private var mStartTimeInMillis: Long = 0
    private var mTimeLeftInMillis: Long = 0
    private var mEndTime: Long = 0
    private var mCountDownTimer: CountDownTimer? = null

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
    var textClearAnswer: TextView? = null

    var imageViewPause: ConstraintLayout? = null

    //http://demoapp.passdaily.in/PassDailyParentsApi/OnlineDExam/SubmitAnswer
    var QUESTION_ID = 0
    var QUESTION_TYPE_ID = ""
    var EXAM_ID = 0
    var RESULT = ""
    var EXAM_ATTEMPT_ID = 0
    var EXAM_DURATION: Long = 0
    var ELAPSED_TIME = 0
    var JSON_ERROR = ""
    var QUESTION_CONTENT = ""
    var ANSWER = ""

    var ANSWER_FILE1 = ""
    var ANSWER_FILE2 = ""
    var ANSWER_FILE3 = ""

    var answerFileUrl1 ="OnlineDExam/DeleteFile1"
    var answerFileUrl2 ="OnlineDExam/DeleteFile2"
    var answerFileUrl3 ="OnlineDExam/DeleteFile3"

    var EXAM_NAME = ""
    var ALLOWED_PAUSE = 0
    var PAUSED_COUNT = 0
    var TIME_NOW = ""
    var SUBJECT_NAME = ""

    var GET_START_TIME_WHEN_ACTIVITY_STARTS = 0
    var END_QUESTION_TIME = 0

    var recyclerViewFiles : RecyclerView? = null

    var tOTALQUESTIONS = 0
    var aNSWEREDQUESTIONS = 0
    var TO_BE_ANSWERED: Int = 0
    var questionList = ArrayList<QuestionDListModel.Question>()

    var fileNameList = ArrayList<FileName>()

    var tempFileNameList = ArrayList<FileName>()

    var constraintLayoutUpload : ConstraintLayout? = null


    var answerEditText: EditText? = null

    var itemPosition = 0

    lateinit var adapter: NumberAdapter
    lateinit var questionListAdapter: QuestionListAdapter

    lateinit var fileAdapter : FileAdapter

    private var readPermission = false
    private var writePermission = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    var maxCount = 3
    var maxCountSelection = 3

    var pb: ProgressDialog? = null

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var adminRole = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dexam_area)

        localDBHelper = LocalDBHelper(this)
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE

        dExamAreaViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[DExamAreaViewModel::class.java]

        val extras = intent.extras
        if (extras != null) {
            SUBJECT_NAME = extras.getString("SUBJECT_NAME")!!
            EXAM_ID = extras.getInt("EXAM_ID")
            EXAM_DURATION = extras.getLong("EXAM_DURATION")
            EXAM_NAME = extras.getString("EXAM_NAME")!!
            ALLOWED_PAUSE = extras.getInt("ALLOWED_PAUSE")
            PAUSED_COUNT = extras.getInt("PAUSED_COUNT")
            TIME_NOW = extras.getString("TIME_NOW")!!
        }

        var studentDBHelper = StudentDBHelper(this)
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID

        binding = ActivityDexamAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)


//      //  Log.i(TAG,"ELAPSED_TIME "+ELAPSED_TIME);
//        long millisInput = Long.parseLong(OEXAM_DURATION) * 60000;
//        //Log.i(TAG,"millisInput "+(millisInput-Integer.valueOf(ELAPSED_TIME));
//        setTime(millisInput);

        // Log.i(TAG,"onCreate ");
        pb = ProgressDialog(this)
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Loading...")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)


        toolbar = binding.appBarExam.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = EXAM_NAME
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //   toolbar.setTitle("About-Us");
//            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
//                onBackPressed()
//            })
        }

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
        constraintFirstLayout!!.setOnClickListener {
            hideFocusListener()
        }
        var constraintView = binding.appBarExam.constraintFirstLayout
        constraintView.setOnClickListener {
            hideFocusListener()
        }

        recyclerViewNumberList = binding.appBarExam.recyclerViewNumberList
        recyclerViewNumberList?.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        imageLeftArrow = binding.appBarExam.imageLeftArrow
        imageRightArrow = binding.appBarExam.imageRightArror

        submitButton = binding.appBarExam.submitButton
        finishButton = binding.appBarExam.finishButton
        imageViewPause = binding.appBarExam.imageViewPause

        textClearAnswer = binding.appBarExam.textClearAnswer

        imageViewQuestion = binding.appBarExam.imageViewQuestion
        cardViewAudioQuestion = binding.appBarExam.cardViewAudioQuestion
        imageViewVideoQuestion = binding.appBarExam.imageViewVideoQuestion
        playPauseImageView = binding.appBarExam.playPauseImageView
        mSeekBar = binding.appBarExam.mSeekBar
        songDuration = binding.appBarExam.songDuration



        answerEditText = binding.appBarExam.answerEditText

        ////relative Layout for click event
        playPauseRelative = binding.appBarExam.playPauseRelative

        mediaPlayer = MediaPlayer()

        textViewQuestion = binding.appBarExam.textViewQuestion
        textViewQuestionNo = binding.appBarExam.textViewQuestionNo
        textMarksForQuestion = binding.appBarExam.textMarksForQuestion


        textTimerHours = binding.appBarExam.textTimerHours
        textTimerMinutes = binding.appBarExam.textTimerMinutes
        textTimerSeconds = binding.appBarExam.textTimerSeconds


        recyclerViewFiles = binding.appBarExam.recyclerViewFiles
        recyclerViewFiles?.layoutManager = GridLayoutManager(this,3)

        textStudentName = binding.appBarExam.textStudentName
        textClassName = binding.appBarExam.textClassName
        textStudentName?.text = Global.studentName
        textClassName?.text = Global.className
        ///Nav item
        recyclerViewQuestionList = binding.navListLayout.recyclerViewQuestionList
        recyclerViewQuestionList?.layoutManager = LinearLayoutManager(this)

        textViewAnswered = binding.navListLayout.textViewAnswered
        textViewNotAnswered = binding.navListLayout.textViewNotAnswered
        textViewTotQuestion = binding.navListLayout.textViewTotQuestion


        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }

        constraintLayoutUpload = binding.appBarExam.constraintLayoutUpload
        constraintLayoutUpload?.setOnClickListener {
            Log.i(TAG,"fileNameList ${fileNameList.size}")
            if(requestPermission()) {
                if(fileNameList.size < maxCount) {
                    maxCountSelection = maxCount - fileNameList.size
                    Toast.makeText(this, "Select $maxCountSelection ", Toast.LENGTH_SHORT).show()
                    val imageCollection = sdk29AndUp {
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

                    val intent = Intent(Intent.ACTION_PICK, imageCollection)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.type = "image/*"
                    startForResult.launch(intent)
                }else {
                    Utils.getSnackBar4K(this, "Maximum Count Reached", constraintFirstLayout!!)
                }
            }
        }




        imageViewCloseNavBar = binding.navListLayout.imageViewCloseNavBar
        imageViewCloseNavBar?.setOnClickListener {
            drawerLayout?.closeDrawer(GravityCompat.END)

        }

        imageLeftArrow?.setOnClickListener {
            if (itemPosition <= 0) {
                Utils.getSnackBar4K(this, "No Question before this", constraintFirstLayout!!)
            } else {
                ///radioButton selection item id
                itemPosition--
                onNumberClick(itemPosition, questionList[itemPosition], 3, "")
            }
        }

        imageRightArrow?.setOnClickListener {
            if ((itemPosition + 1) == questionList.size) {
                Log.i(TAG, "No Skip Question")
                Utils.getSnackBar4K(this, "No Question after this", constraintFirstLayout!!)
            } else {
                ///radioButton selection item id
                itemPosition++
                onNumberClick(itemPosition, questionList[itemPosition], 3, "")
            }
        }

        submitButton?.setOnClickListener {

            if(dExamAreaViewModel.validateField(answerEditText!!,constraintFirstLayout!!,this@DExamAreaActivity)
                || (fileNameList.size + tempFileNameList.size) != 0){

//                    if(fileNameList.size != 0) {
//
//                        //    progressStart();
//                        pb = ProgressDialog.show(this, "", "Loading...", true)
//                        Thread {
//                            //creating new thread to handle Http Operations
//                            try {
//                                for (i in fileNameList.indices) {
//                                    if (fileNameList[i].type == "Local") {
//                                        onFileUploadClick(i, fileNameList[i])
//                                        SystemClock.sleep(3000)
//                                    }
//                                }
//                                //Your code goes here
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
//                        }.start()
//                    }else{
                        submitFile()
//                    }
//
            }else{
                Utils.getSnackBar4K(this, "Please answer this question", constraintFirstLayout!!)
            }


        }

        finishButton?.setOnClickListener {

            pauseTimer()
            val bottomSheet = DescExamFinishSheetFragment(this,tOTALQUESTIONS, aNSWEREDQUESTIONS, TO_BE_ANSWERED,1)
            bottomSheet.isCancelable = false
            bottomSheet.show(supportFragmentManager, "TAG")

        }

        imageViewPause?.setOnClickListener {


            if (PAUSED_COUNT >= ALLOWED_PAUSE) {
                Utils.getSnackBar4K(this,"Continue or finish",constraintFirstLayout!!)

            } else {
                if (mTimerRunning) {

                    pauseTimer()
                    val dialog = Dialog(this@DExamAreaActivity)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.obj_exam_pause_dialog)
                    val lp = WindowManager.LayoutParams()
                    lp.copyFrom(dialog.window!!.attributes)
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                    lp.gravity = Gravity.CENTER
                    dialog.window!!.attributes = lp


                    val resumeButton = dialog.findViewById(R.id.resumeButton) as AppCompatButton
                    resumeButton.setOnClickListener {
                        dialog.dismiss()
                        startTimer()
                    }

                    val saveButton = dialog.findViewById(R.id.saveButton) as AppCompatButton
                    saveButton.setOnClickListener {
                        val c = Calendar.getInstance()
                        val df = SimpleDateFormat("hh:mm:ss")
                        val formattedDate = df.format(c.time)
                        val tokens = formattedDate.split(":".toRegex()).toTypedArray()
                        val hours = tokens[0].toInt()
                        val minutes = tokens[1].toInt()
                        val seconds = tokens[2].toInt()

                        val PAUSED_ELAPSED: Int = seconds + minutes * 60 + hours * 3600
                        Log.i(TAG, "GET_START_TIME_WHEN_ACTIVITY_STARTS_PAUSE $GET_START_TIME_WHEN_ACTIVITY_STARTS")
                        Log.i(TAG, "PAUSED_ELAPSED_PAUSE $PAUSED_ELAPSED")
                        val Elapsed_Time = PAUSED_ELAPSED - GET_START_TIME_WHEN_ACTIVITY_STARTS
                        Log.i(TAG, "Elapsed_Time $Elapsed_Time")
                        Log.i(TAG, "questionList ${questionList[itemPosition].eXAMATTEMPTDID}")
                        saveAndExitFunction(EXAM_ATTEMPT_ID, Elapsed_Time)

//                    /////only for test below both need
                        dialog.dismiss()
//                    startTimer()
                    }

                    val finishButton = dialog.findViewById(R.id.finishButton) as AppCompatButton
                    finishButton.setOnClickListener {
                        val bottomSheet = DescExamFinishSheetFragment(this,tOTALQUESTIONS, aNSWEREDQUESTIONS, TO_BE_ANSWERED,1)
                        bottomSheet.isCancelable = false
                        bottomSheet.show(supportFragmentManager, "TAG")
                        dialog.dismiss()
                    }
                    dialog.show()
                } else {
                    startTimer()
                }
            }
        }

        textClearAnswer?.setOnClickListener {

            val urlStr = "OnlineDExam/DeleteAnswer"
            deleteFile(urlStr,EXAM_ATTEMPT_ID,QUESTION_ID,2,1)

        }

        ////////////////////////////
        getOnlineExamStatus(ACADEMICID, CLASSID, STUDENTID, EXAM_ID)


       // Utils.setStatusBarColor(this)
        Utils.setStatusBarColor(this)

    }
    fun hideFocusListener() {
        val view = this.currentFocus

        // if nothing is currently
        // focus then this will protect
        // the app from crash

        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            val manager = getSystemService(
                INPUT_METHOD_SERVICE
            ) as InputMethodManager
            manager
                .hideSoftInputFromWindow(
                    view.windowToken, 0
                )
        }
    }

    private fun getTotalQuestions(EXAM_ATTEMPT_ID: Int) {

        dExamAreaViewModel.getQuestionList(
            EXAM_ID,
            EXAM_ATTEMPT_ID
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

                                textMarksForQuestion?.text = questionList[0].qUESTIONMARK.toString()

                                ///radioButton selection item id
                                questionTypeFun(questionList[0])

                                getTakeAnswer(EXAM_ID, EXAM_ATTEMPT_ID, questionList[0].qUESTIONID)


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

    private fun getTakeAnswer(examId: Int, examAttemptId: Int, qUESTIONID: Int) {

        answerEditText?.setText("")
        QUESTION_ID = 0
        ANSWER = ""
        ANSWER_FILE1 = ""
        ANSWER_FILE2 = ""
        ANSWER_FILE3 = ""
        fileNameList = ArrayList<FileName>()
        tempFileNameList = ArrayList<FileName>()

        dExamAreaViewModel.getTakeAnswer(examId,
            examAttemptId,
            qUESTIONID)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            QUESTION_ID =  response.answer.qUESTIONID
                            ANSWER = response.answer.aNSWER.toString()
                            if (ANSWER != "null") {
                                answerEditText?.setText(ANSWER)
                            }
                            ANSWER_FILE1 = response.answer.aNSWERFILE1.toString()
                            if (ANSWER_FILE1 != "null" && ANSWER_FILE1 != "") {
                                //  fileNameList.add(FileName(qUESTIONID, ANSWER_FILE1,answerFileUrl1,"Json"))
                                tempFileNameList.add(FileName(qUESTIONID, ANSWER_FILE1,answerFileUrl1,"Json"))
                            }
                            ANSWER_FILE2 = response.answer.aNSWERFILE2.toString()
                            if (ANSWER_FILE2 != "null" && ANSWER_FILE2 != "") {
                                //  fileNameList.add(FileName(qUESTIONID, ANSWER_FILE2,answerFileUrl2,"Json"))
                                tempFileNameList.add(FileName(qUESTIONID, ANSWER_FILE2,answerFileUrl2,"Json"))
                            }
                            ANSWER_FILE3 = response.answer.aNSWERFILE3.toString()
                            if (ANSWER_FILE3 != "null" && ANSWER_FILE3 != "") {
                                //   fileNameList.add(FileName(qUESTIONID, ANSWER_FILE3,answerFileUrl3,"Json"))
                                tempFileNameList.add(FileName(qUESTIONID, ANSWER_FILE3,answerFileUrl3,"Json"))
                            }

                            fileNameList.addAll(tempFileNameList)

                            fileAdapter = FileAdapter(
                                this@DExamAreaActivity,tempFileNameList,this@DExamAreaActivity,Aimage_path,TAG)

                            recyclerViewFiles?.adapter = fileAdapter
                            Log.i(TAG,"tempFileNameList $tempFileNameList")

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

//        val apiInterface = RetrofitClient.create().takeAnswerNew(
//            examId,
//            examAttemptId,
//            qUESTIONID
//        )
//        apiInterface.enqueue(object : Callback<TakeAnswerModel> {
//            override fun onResponse(
//                call: Call<TakeAnswerModel>,
//                resource: Response<TakeAnswerModel>
//            ) {
//                if (resource.isSuccessful) {
//
//
//
//                }
//
//            }
//
//            override fun onFailure(call: Call<TakeAnswerModel>, t: Throwable) {
//                Log.i(TAG, "Throwable  $t")
//            }
//        })


    }


/////////////////////////////////////////////////////file list
    class FileAdapter (var numberDClickListener: NumberDClickListener,
                      var fileNameList: ArrayList<FileName>,
                      var context: Context,
                      var Aimage_path : String,var TAG : String)
        : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageViewFile: ImageView = view.findViewById(R.id.imageViewFile)
            var imageViewClose: ImageView = view.findViewById(R.id.imageViewClose)
            var buttonUpload : AppCompatButton = view.findViewById(R.id.buttonUpload)

            var perProgressBar : CircularProgressIndicator = view.findViewById(R.id.perProgressBar)
            var textViewProgress : TextView = view.findViewById(R.id.textViewProgress)
            var textViewTitle  : TextView = view.findViewById(R.id.textViewTitle)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.file_name_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.imageViewClose.setOnClickListener {
                numberDClickListener.onFileJsonClick(position,fileNameList[position])
            }

//            holder.buttonUpload.setOnClickListener {
//                numberDClickListener.onFileUploadClick(position,fileNameList[position])
//            }

            if(fileNameList[position].type == "Json") {
                holder.buttonUpload.visibility = View.GONE
                holder.perProgressBar.visibility  =  View.GONE
                holder.textViewProgress.visibility  =  View.GONE
                Glide.with(context)
                    .load(Aimage_path + fileNameList[position].fileName)
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_gallery)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewFile)
            }else if(fileNameList[position].type =="Local"){
                val path: String = fileNameList[position].fileName
                Log.i(TAG, "path $path")
                val mFile = FileUtils.getReadablePathFromUri(context, path.toUri())
                Log.i(TAG, "mFile $mFile")

                holder.buttonUpload.visibility = View.GONE
                fileNameList[position].type = "Uploaded"

                holder.perProgressBar.visibility  =  View.VISIBLE
                holder.textViewProgress.visibility  =  View.VISIBLE//,materialList[position].fILENAME
                numberDClickListener.onFileUploadProgress(position,mFile!!,fileNameList[position].urlStr,holder.textViewTitle,
                    holder.perProgressBar, holder.textViewProgress)


                Glide.with(context)
                    .load(fileNameList[position].fileName)
                    .into(holder.imageViewFile)
            }else if(fileNameList[position].type == "Uploaded") {
                holder.buttonUpload.visibility = View.GONE
                holder.perProgressBar.visibility  =  View.GONE
                holder.textViewProgress.visibility  =  View.GONE
                Glide.with(context)
                    .load(Aimage_path + fileNameList[position].fileName)
                    .apply(
                        RequestOptions.centerCropTransform()
                            .dontAnimate() //   .override(imageSize, imageSize)
                            .placeholder(R.drawable.ic_file_gallery)
                    )
                    .thumbnail(0.5f)
                    .into(holder.imageViewFile)
            }


            holder.imageViewFile.setOnClickListener {
                Global.albumImageList = ArrayList<CustomImageModel>()
                if(fileNameList[position].type == "Json") {
                    Global.albumImageList.add(
                        CustomImageModel(Aimage_path + fileNameList[position].fileName)
                    )
                }else if(fileNameList[position].type == "Local") {
                    Global.albumImageList.add(
                        CustomImageModel(fileNameList[position].fileName)
                    )
                }

                numberDClickListener.onViewImagePreview()
            }
        }

        override fun getItemCount(): Int {
            return  fileNameList.size
        }

    }

    override fun onViewImagePreview(){
        val extra = Bundle()
        extra.putInt("position", 0)
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val newFragment = SlideshowDialogFragment.newInstance()
        newFragment.arguments = extra
        newFragment.show(ft, newFragment.TAG)
    }
    override fun onFileUploadClick(position: Int, fileName: FileName) {
//        var selectedFilePath = fileName.fileName
//        Log.i(TAG, "position  $position")
//        Log.i(TAG, "fileName  $fileName")
//
////        if (selectedFilePath.contains(".jpg")
////            || selectedFilePath.contains(".jpeg")
////            || selectedFilePath.contains(".png")
////            || selectedFilePath.contains(".JPG")
////            || selectedFilePath.contains(".JPEG")
////            || selectedFilePath.contains(".PNG")) {
////                selectedFilePath = Utils.compressImage(fileName.fileName) // imagePath as a string
////        }
//
//        val descriptionList: MutableList<MultipartBody.Part> = ArrayList()
//        descriptionList.add(Utils.selectedImageConversion("STUDY_METERIAL_FILE",
//            selectedFilePath.toUri(),this))
//
//
//        val apiInterface = RetrofitClient.create().fileUpload(
//            fileName.urlStr,
//            descriptionList
//        )
//        apiInterface.enqueue(object : Callback<FileResultModel> {
//            override fun onResponse(
//                call: Call<FileResultModel>,
//                resource: Response<FileResultModel>
//            ) {
//                val response = resource.body()
//                if (resource.isSuccessful) {
//                    Log.i(TAG, "response  $response")
//                    when (position) {
//                        0 -> { ANSWER_FILE1 = response!!.dETAILS }
//                        1 -> { ANSWER_FILE2 = response!!.dETAILS }
//                        2 -> { ANSWER_FILE3 = response!!.dETAILS }
//                    }
//                }
//                if((position+1) == fileNameList.size){
//                    pb?.dismiss()
//                    submitFile()
//                }
//
//            }
//
//            override fun onFailure(call: Call<FileResultModel>, t: Throwable) {
//                Log.i(TAG, "Throwable  $t")
//            }
//        })
//
//
    }

    override fun onFileUploadProgress(
        position: Int,
        fILEPATHName: String,
        fILEURL : String,
        textViewTitle: TextView,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView
    ) {
        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(TAG,"selectedFilePath $fILEPATHName");
        filesToUpload[0] = File(fILEPATHName)
        Log.i(TAG,"filesToUpload $filesToUpload");

        showProgress("Uploading media ...",perProgressBar,textViewProgress)
        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(fILEURL, "STUDY_METERIAL_FILE", filesToUpload, "",object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                hideProgress(perProgressBar,textViewProgress)
                Log.i(TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {
                hideProgress(perProgressBar,textViewProgress)
                for (i in responses.indices) {
                    //val str = responses[i]
                    textViewProgress.visibility = View.GONE
                    perProgressBar.visibility = View.GONE
                    textViewTitle.visibility = View.VISIBLE
                    //   Log.i(TAG, "RESPONSE $i ${responses[i]}")
                    //submitFile(responses[i],fILETITLE,position)
                    // if ((position + 1) == fileNameList.size) {
                    when (position) {
                        0 -> { ANSWER_FILE1 = responses[i]
                            fileNameList[position].fileName = ANSWER_FILE1
                            fileNameList[position].type = "Uploaded"
                        }
                        1 -> { ANSWER_FILE2 = responses[i]
                                fileNameList[position].fileName = ANSWER_FILE2
                                fileNameList[position].type = "Uploaded"
                        }
                        2 -> { ANSWER_FILE3 = responses[i]
                                fileNameList[position].fileName = ANSWER_FILE3
                                fileNameList[position].type = "Uploaded"
                        }
                    }
                    // }

                }
            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                updateProgress(totalpercent, "Uploading file $filenumber", "", perProgressBar,textViewProgress)
                Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })
    }


    fun submitFile(){


        val jsonObject = JSONObject()
        try {
            jsonObject.put("EXAM_ATTEMPT_ID", EXAM_ATTEMPT_ID)
            jsonObject.put("QUESTION_ID", QUESTION_ID)
            jsonObject.put("ELAPSED_TIME", elaspeTimeFun())
            jsonObject.put("ANSWER", answerEditText?.text.toString())
            jsonObject.put("ANSWER_FILE_1", ANSWER_FILE1)
            jsonObject.put("ANSWER_FILE_2", ANSWER_FILE2)
            jsonObject.put("ANSWER_FILE_3", ANSWER_FILE3)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")

        val submitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


        dExamAreaViewModel.submitAnswerDExam(submitItems)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            val error = resource.data.errorBody()?.string()

                            if(Utils.resultFun(response) == "SUCCESS"){
                                if ((itemPosition + 1) == questionList.size) {
                                    onNumberClick(itemPosition, questionList[itemPosition], 3,"lastValue")
                                    pauseTimer()
                                } else {
                                    ///radioButton selection item id
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

    fun updateProgress(
        progress: Int,
        title: String?,
        msg: String?,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView
    ) {
        Log.i(TAG,"updateProgress $progress")
//        perProgressBar.setTitle(title)
//        perProgressBar.setMessage(msg)
        textViewProgress.text = "$progress %"
        perProgressBar.progress = progress
    }

    fun showProgress(str: String?, perProgressBar: CircularProgressIndicator, textViewProgress: TextView) {
        Log.i(TAG,"showProgress $str")
        try {
            //perProgressBar.setCancelable(false)
            // perProgressBar.setTitle("Please wait")
            //perProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            perProgressBar.max = 100 // Progress Dialog Max Value
            // perProgressBar.setMessage(str)
//            if (perProgressBar.isShowing) perProgressBar.dismiss()
//            perProgressBar.show()
        } catch (e: java.lang.Exception) {
        }
    }

    fun hideProgress(perProgressBar: CircularProgressIndicator, textViewProgress: TextView) {
        try {
            Log.i(TAG,"hideProgress")
            // if (perProgressBar.isShowing) perProgressBar.dismiss()
        } catch (e: java.lang.Exception) {
        }
    }


    override fun onFileJsonClick(
        position: Int,
        fileName: FileName
    ) {
        if(fileName.type == "Json"){
            fileNameList.removeAt(position)
            tempFileNameList.removeAt(position)
            deleteFile(fileName.urlStr,EXAM_ATTEMPT_ID,fileName.qUESTIONID,1,0)
        }else{
            fileNameList.removeAt(position)
            when (position) {
                0 -> { ANSWER_FILE1 = "" }
                1 -> { ANSWER_FILE2 = "" }
                2 -> { ANSWER_FILE3 = "" }
            }
        }
        fileAdapter.notifyDataSetChanged()

    }



    fun deleteFile(path : String, examAttemptId: Int, qUESTIONID: Int,elapsedTime: Int,type : Int) {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("EXAM_ATTEMPT_ID", examAttemptId)
            jsonObject.put("QUESTION_ID", qUESTIONID)
            jsonObject.put("ELAPSED_TIME", elapsedTime)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")

        val deleteFile =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


        dExamAreaViewModel.getDeleteFile(path,deleteFile)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(Utils.resultFun(response) == "SUCCESS"){
                                if(type == 1){
                                    onNumberClick(itemPosition, questionList[itemPosition], 3, "")
                                }
                                Log.i(TAG, "isSuccessful delete")
                            }else{
                                Log.i(TAG, "Failed ")
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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////timer setups

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

    override fun onBackPressed() {
      //  super.onBackPressed()
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
            val df = SimpleDateFormat("hh:mm:ss")
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
                jsonObject.put("EXAM_ID", EXAM_ID)
                jsonObject.put("EXAM_ATTEMPT_ID", EXAM_ATTEMPT_ID)
                jsonObject.put("ELAPSED_TIME", elapsedTime)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Log.i(TAG,"jsonObject $jsonObject")

            val doAutoEndItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


            dExamAreaViewModel.getDoAutoEndDExam(doAutoEndItems)
                .observe(this, Observer {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                val response = resource.data?.body()!!
                                if(Utils.resultFun(response) == "SUCCESS"){
                                    Log.i(TAG, "Auto end success ")
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
            val bottomSheet = DescExamFinishSheetFragment(this,tOTALQUESTIONS, aNSWEREDQUESTIONS, TO_BE_ANSWERED,2)
            bottomSheet.isCancelable = false
            bottomSheet.show(supportFragmentManager, "TAG")
        }

    }
/////////////////////////////////////////////////////


    override fun onNumberClick(
        position: Int,
        questionArrayList: QuestionDListModel.Question,
        type: Int,
        lastValue: String
    ) {
        drawerLayout?.closeDrawer(GravityCompat.END)
        itemPosition = position
        Log.i(TAG, "id $position")

        textViewQuestionNo?.text = "Question ${position + 1}"
        textViewQuestion?.text = questionArrayList.qUESTIONTITLE

        questionTypeFun(questionArrayList)

        getTakeAnswer(EXAM_ID, EXAM_ATTEMPT_ID, questionArrayList.qUESTIONID)

        Log.i(TAG, "before load")
        getQuestionPaletteLoad(questionArrayList.eXAMID, EXAM_ATTEMPT_ID, type, position, lastValue)
    }


    /////
    private fun getQuestionPaletteLoad(
        oEXAMID: Int,
        OEXAM_ATTEMPT_ID: Int,
        type: Int,
        position: Int,
        lastValue: String
    ) {

        dExamAreaViewModel.getQuestionList(
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
                                tOTALQUESTIONS =
                                    questionList[0].tOTALQUESTIONS.toString().toDouble()
                                        .roundToInt()
                            }
                            if (questionList[0].aNSWEREDQUESTIONS != null) {
                                aNSWEREDQUESTIONS =
                                    questionList[0].aNSWEREDQUESTIONS.toString().toDouble()
                                        .roundToInt()
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

                            if (lastValue == "lastValue") {
                                val bottomSheet = DescExamFinishSheetFragment(this,tOTALQUESTIONS, aNSWEREDQUESTIONS, TO_BE_ANSWERED,1)
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


    private fun questionTypeFun(questionArrayList: QuestionDListModel.Question) {

        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.reset()
        }
        //  else { mediaPlayer?.reset() }

        imageViewQuestion?.setOnClickListener {
            Global.albumImageList = ArrayList<CustomImageModel>()
            Global.albumImageList.add(
                CustomImageModel(Qimage_path + questionArrayList.qUESTIONCONTENT)
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
                    .load(Qimage_path + questionArrayList.qUESTIONCONTENT)
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
                    mediaPlayer!!.setDataSource(Qimage_path + questionArrayList.qUESTIONCONTENT)
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
                    val intent = Intent(this@DExamAreaActivity, ExoPlayerActivity::class.java)
                    intent.putExtra("ALBUM_TITLE", "")
                    intent.putExtra("ALBUM_FILE", Qimage_path + questionArrayList.qUESTIONCONTENT)
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


    //    /http://demoapp.passdaily.in/PassDailyParentsApi/OnlineDExam/OnlineDExamStatusByStudent?AccademicId=8&ClassId=12&StudentId=928&ExamId=21
    private fun getOnlineExamStatus(academicid: Int, classid: Int, studentid: Int, examId: Int) {

        val apiInterface = RetrofitClient.create().getOnlineDExamStatus(
            academicid,
            classid,
            studentid, examId
        )
        apiInterface.enqueue(object : Callback<DescriptiveOnlineExamStatus> {
            override fun onResponse(
                call: Call<DescriptiveOnlineExamStatus>,
                response: Response<DescriptiveOnlineExamStatus>
            ) {
                if (response.isSuccessful) {
                    RESULT = response.body()!!.rESULT!!
                    EXAM_ATTEMPT_ID = response.body()!!.oEXAMATTEMPTID
                    EXAM_DURATION = response.body()!!.oEXAMDURATION
                    ELAPSED_TIME = response.body()!!.eLAPSEDTIME
                    JSON_ERROR = response.body()!!.jSONERROR!!

                }
                if (RESULT == "SUCCESS") {

                    val builder = AlertDialog.Builder(this@DExamAreaActivity)
                        .setTitle("Alert")
                        .setMessage("Don't press home button it will be consider as save and exit from exam")
                        .setCancelable(false)
                        .setPositiveButton("Okay Got It") { dialog, which ->


                            getTotalQuestions(EXAM_ATTEMPT_ID)

                            Log.i(TAG, "start ELAPSED_TIME $ELAPSED_TIME")
                            mTimeLeftInMillis = EXAM_DURATION * 60000
                            Log.i(TAG, "millisInput $mTimeLeftInMillis")
//
//
                            val elapsedMillis = ELAPSED_TIME * 1000
                            Log.i(TAG, "minute $elapsedMillis")
                            val millisInputTotalTimeElapsedTime = mTimeLeftInMillis - elapsedMillis
                            setTime(millisInputTotalTimeElapsedTime)

//                    updateCountDownText()
//                    //  updateWatchInterface();
//                    if (mTimerRunning) {
//                        // mEndTime = prefs.getLong("endTime", 0);
//                        mTimeLeftInMillis = mEndTime - System.currentTimeMillis()
//                        if (mTimeLeftInMillis < 0) {
//                            mTimeLeftInMillis = 0
//                            mTimerRunning = false
//                            updateCountDownText()
//                            // updateWatchInterface();
//                        }
//                        Log.i(TAG, "start")
//                    } else {
//                        Log.i(TAG, "start startTimer")
                            startTimer()
//                    }

                            /////////////// Start Time
                            val c = Calendar.getInstance()
                            //     System.out.println("Current time => "+c.getTime());
                            val df = SimpleDateFormat("hh:mm:ss")
                            val formattedDate = df.format(c.time)
                            val tokens = formattedDate.split(":".toRegex()).toTypedArray()
                            val hours1 = tokens[0].toInt()
                            val minutes1 = tokens[1].toInt()
                            val seconds1 = tokens[2].toInt()
                            GET_START_TIME_WHEN_ACTIVITY_STARTS =
                                seconds1 + minutes1 * 60 + hours1 * 3600
                            Log.i(
                                TAG,
                                "Start_here GET_START_TIME_WHEN_ACTIVITY_STARTS $GET_START_TIME_WHEN_ACTIVITY_STARTS"
                            )
                            //  START_QUESTION_TIME = seconds1 + minutes1 * 60 + hours1 * 3600
                            dialog.dismiss()
                        }
                    //  builder.show()
                    val dialog = builder.create()
                    dialog.setOnShowListener {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setTextColor(resources.getColor(R.color.green_300))
                        //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
                    }
                    dialog.show()


                } else if (RESULT == "EXAM_FINISHED" || RESULT == "EXAM_TIME_OVER") {
                    val bottomSheet = DescExamFinishSheetFragment(this@DExamAreaActivity,
                        tOTALQUESTIONS, aNSWEREDQUESTIONS, TO_BE_ANSWERED,1)
                    bottomSheet.isCancelable = false
                    bottomSheet.show(supportFragmentManager, "TAG")
                    Log.i(TAG, "EXAM_FINISHED or EXAM_TIME_OVER")
                } else {
                    Log.i(TAG, "Failed")
                }
            }

            override fun onFailure(call: Call<DescriptiveOnlineExamStatus>, t: Throwable) {
                Log.i(TAG, "Throwable  $t")
            }
        })


    }

    class NumberAdapter(
        var numberClickListener: NumberDClickListener,
        var mList: List<QuestionDListModel.Question>,
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
                numberClickListener.onNumberClick(position, mList[position], 1, "")
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
        var numberClickListener: NumberDClickListener,
        var mList: List<QuestionDListModel.Question>,
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

            if (mList[position].eXAMATTEMPTDID != null && mList[position].qUESTIONIDATTEMPT != null) {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.green_400))
            } else {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.color_chemistry))
            }

            holder.textViewNumber.text = (position + 1).toString()
            holder.textViewQuestion.text = mList[position].qUESTIONTITLE

            holder.constraint.setOnClickListener {
                numberClickListener.onNumberClick(position, mList[position], 3, "")
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.exam_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_nav -> {
                drawerLayout?.openDrawer(GravityCompat.END)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    ///permission Part
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->

            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data

                //If multiple image selected
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0

                    val countPath = count + fileNameList.size
                    if (countPath > 3) {
                        Toast.makeText(this, "You select more then $maxCount", Toast.LENGTH_SHORT).show()
                    }else{
//                        fileNameList.addAll(tempFileNameList)
                        for (i in 0 until count) {
                            val imageUri: Uri? = data.clipData?.getItemAt(i)?.uri
                            fileNameList.add(FileName(QUESTION_ID,imageUri!!.toString(),"OnlineDExam/UploadFiles","Local"))
                            //  loadPaths.add(DefaultProduct(imageUri,i+1))
                        }

                        fileAdapter = FileAdapter(
                            this@DExamAreaActivity,fileNameList,this@DExamAreaActivity,Aimage_path,TAG)

                        recyclerViewFiles?.adapter = fileAdapter
                    }
                    //     imageAdapter.addSelectedImages(selectedPaths)
                }

            }

        }


    fun requestPermission() : Boolean {

        var hasReadPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        }else {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }


        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermission = hasReadPermission
        writePermission = hasWritePermission || minSdk29

        val permissions = readPermission && writePermission


        val permissionToRequests = mutableListOf<String>()
        if (!writePermission) {
            permissionToRequests.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!readPermission) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_IMAGES)
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_VIDEO)
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_AUDIO)
            }else {
                permissionToRequests.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionToRequests.isNotEmpty()) {
            permissionsLauncher.launch(permissionToRequests.toTypedArray())
        }

        return permissions
    }

    @SuppressLint("SimpleDateFormat")
    private fun elaspeTimeFun(): Int {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("hh:mm:ss")
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

    override fun onCancelClick() {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("hh:mm:ss")
        val formattedDate = df.format(c.time)
        val tokens = formattedDate.split(":".toRegex()).toTypedArray()
        val hours = tokens[0].toInt()
        val minutes = tokens[1].toInt()
        val seconds = tokens[2].toInt()
        GET_START_TIME_WHEN_ACTIVITY_STARTS = seconds + minutes * 60 + hours * 3600

        if(!mTimerRunning) {
            startTimer()
        }
    }

    override fun onFinishClick() {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("hh:mm:ss")
        val formattedDate = df.format(c.time)
        val tokens = formattedDate.split(":".toRegex()).toTypedArray()
        val hours = tokens[0].toInt()
        val minutes = tokens[1].toInt()
        val seconds = tokens[2].toInt()

        val PAUSED_ELAPSED: Int = seconds + minutes * 60 + hours * 3600
        Log.i(TAG, "GET_START_TIME_WHEN_ACTIVITY_STARTS_PAUSE $GET_START_TIME_WHEN_ACTIVITY_STARTS")
        Log.i(TAG, "PAUSED_ELAPSED_PAUSE $PAUSED_ELAPSED")
        val elapsedTime = PAUSED_ELAPSED - GET_START_TIME_WHEN_ACTIVITY_STARTS

        val jsonObject = JSONObject()
        try {
            jsonObject.put("EXAM_ATTEMPT_ID", EXAM_ATTEMPT_ID)
            jsonObject.put("ELAPSED_TIME", elapsedTime)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")

        val finishItem =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


        dExamAreaViewModel.getFinishDExam(finishItem)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(Utils.resultFun(response) == "SUCCESS"){
                                Toast.makeText(
                                    this,
                                    "Your Exam is Saved and exit",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }else{
                                Toast.makeText(
                                    this,
                                    "Failed While Save and Exit",
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

    override fun onViewResult() {
        finish()
    }

    private fun saveAndExitFunction(EXAM_ATTEMPT_ID : Int, elapsedTime: Int) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("EXAM_ATTEMPT_ID", EXAM_ATTEMPT_ID)
            jsonObject.put("ELAPSED_TIME", elapsedTime)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")

        val saveExitItems =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())


        dExamAreaViewModel.getSaveAndExitDExam(saveExitItems)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(Utils.resultFun(response) == "SUCCESS"){
                                Toast.makeText(
                                    this,
                                    "Your Exam is Saved and exit",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }else{
                                Toast.makeText(
                                    this,
                                    "Failed While Save and Exit",
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

}


data class FileName(var qUESTIONID : Int,var fileName: String,var  urlStr : String,var type : String)

interface NumberDClickListener {
    fun onNumberClick(
        position: Int,
        questionArrayList: QuestionDListModel.Question,
        type: Int,
        lastValue: String
    )

    fun onFileJsonClick(
        position: Int,
        fileName: FileName
    )

    fun onFileUploadClick(
        position: Int,
        fileName: FileName
    )

    fun onFileUploadProgress(
        position: Int,
        fILEPATHName : String,
//        fILENAME: String,
//        fILETITLE: String,
        fILEURL : String,
        textViewTitle: TextView,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView
    )

    fun onViewImagePreview()

}


interface FinishClickListener{
    fun onCancelClick()
    fun onFinishClick()
    fun onViewResult()
}