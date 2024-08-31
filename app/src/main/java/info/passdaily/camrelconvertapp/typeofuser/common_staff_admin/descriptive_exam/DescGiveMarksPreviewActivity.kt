package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.descriptive_exam

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
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
import com.devlomi.record_view.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityDescGiveMarksPreviewBinding
import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.lib.video.Video_Activity
import info.passdaily.camrelconvertapp.model.CustomImageModel
import info.passdaily.camrelconvertapp.model.DescGiveMarkPreviewModel
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.permission.sdk29AndUp
import info.passdaily.camrelconvertapp.services.retrofit.RetrofitClientStaff
import info.passdaily.camrelconvertapp.typeofuser.parent.description_exam.FileName
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image.SlideshowDialogFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class DescGiveMarksPreviewActivity : AppCompatActivity(),NumberDescStaffClickListener {

    var TAG= "DescGiveMarksPreviewActivity"

    var Qimage_path = Global.event_url + "/DExamFile/Question/"

    var Aimage_path = Global.event_url + "/DExamFile/Answer/"
    ///DExamFile/

    var SERVER_URL = "DComment/UploadFiles"

    var Cimage_path = Global.event_url + "/DExamFile/"

    private lateinit var binding: ActivityDescGiveMarksPreviewBinding

    private lateinit var descriptiveExamStaffViewModel: DescriptiveExamStaffViewModel

    var toolbar: Toolbar? = null

    var EXAM_ID = 0
    var EXAM_ATTEMPT_ID = 0
    var STUDENT_NAME =""
    var EXAM_NAME = ""
    var aCCADEMICID = 0
    var sTUDENTID = 0
    var cLASSNAME = ""
    var EXAM_ATTEMPT_ID_ANSWER = 0

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

    var questionAttemptedList  = ArrayList<DescGiveMarkPreviewModel.StudentQuestionAttempted>()

    //var answerDetails = ArrayList<TakeAnswerStaffModel>()

    var recordButton: RecordButton? = null
    var recordView: RecordView? = null

    var itemPosition = 0

    lateinit var adapter: NumberAdapter
    lateinit var questionListAdapter: QuestionListAdapter

    var drawerToggle: ActionBarDrawerToggle? = null
    var drawerLayout: DrawerLayout? = null
    var recyclerViewNumberList: RecyclerView? = null

    var constraintFirstLayout: ConstraintLayout? = null

    var textViewQuestion : TextView? = null
    var textViewQuestionNo : TextView? = null
    var textMarksForQuestion : TextView? = null

    var imageLeftArrow: ConstraintLayout? = null
    var imageRightArrow: ConstraintLayout? = null

    var answerEditText: EditText? = null

    var ANSWER_FILE1 = ""
    var ANSWER_FILE2 = ""
    var ANSWER_FILE3 = ""

    var ANSWER = ""

    var answerMark = 0

    var recorder: MediaRecorder? = null

    var recyclerViewFiles : RecyclerView? = null

    var fileNameList = ArrayList<FileName>()

    var fileName= ""
    var file_name=""

    var questionMark = 0

    var buttonMark : AppCompatButton? = null
    var editTextMark : TextInputEditText? = null
    var editTextComments : TextInputEditText? = null
    var textClearComments : TextView? = null

    var imageViewGetImage : ImageView? = null
    var imageViewClose  : ImageView? = null


    var cardViewAudioComment : CardView? = null
    var playPauseRelativeCmd : RelativeLayout? = null
    var playPauseImageCmd : ImageView? = null
    var mSeekBarCmd : SeekBar? = null
    var seekHandlerCmd = Handler()
    var runCmd: Runnable? = null
    var songDurationCmd : TextView? = null
    var imageAudioClose : ImageView? = null

    var mediaPlayerComment : MediaPlayer? = null

    var deleteFileFromServer = 0
    var deleteCommentsFromServer = 0

    var cOMMENTTYPE = ""

    var imagePathUpload = ""
    var commentUplodedFile = ""

    var constraintLayoutCamera : ConstraintLayout? = null
    var constraintLayoutAudio : ConstraintLayout? = null
    var cardViewRecorderLayout : CardView? = null

    lateinit var fileAdapter : FileAdapter

    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var adminRole = 0

    /////Navigation bar question list
    var recyclerViewQuestionList: RecyclerView? = null
    var imageViewCloseNavBar: ImageView? = null


    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var readPermission = false
    private var writePermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localDBHelper = LocalDBHelper(this)
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE

        descriptiveExamStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[DescriptiveExamStaffViewModel::class.java]

        val extras = intent.extras
        if (extras != null) {

            //  intent.putExtra("aCCADEMICID", onlineExamAttendee.aCCADEMICID)
            //        intent.putExtra("eXAMID", onlineExamAttendee.eXAMID)
            //        intent.putExtra("eXAMNAME", onlineExamAttendee.eXAMNAME)
            //        intent.putExtra("STUDENT_NAME", onlineExamAttendee.sTUDENTNAME)
            //        intent.putExtra("EXAM_ID", onlineExamAttendee.eXAMID)
            //        intent.putExtra("EXAM_ATTEMPT_ID", onlineExamAttendee.eXAMATTEMPTID)
            aCCADEMICID = extras.getInt("aCCADEMICID")
            EXAM_ID = extras.getInt("eXAMID")
            EXAM_ATTEMPT_ID = extras.getInt("EXAM_ATTEMPT_ID")
            STUDENT_NAME = extras.getString("STUDENT_NAME")!!
            sTUDENTID = extras.getInt("sTUDENTID")
            cLASSNAME = extras.getString("cLASSNAME")!!
            EXAM_NAME = extras.getString("eXAMNAME")!!
        }

//        setContentView(R.layout.activity_desc_give_marks_preview)
        binding = ActivityDescGiveMarksPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.appBarExam.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            //  supportActionBar!!.title = EXAM_NAME
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //   toolbar.setTitle("About-Us");
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }

        var collapsingToolbar = binding.appBarExam.collapsingToolbar
        collapsingToolbar.isTitleEnabled = false;
        setSupportActionBar(toolbar);
        toolbar?.title = EXAM_NAME;

        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow); // your drawable

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

        textViewQuestion = binding.appBarExam.textViewQuestion
        textViewQuestionNo = binding.appBarExam.textViewQuestionNo
        textMarksForQuestion  = binding.appBarExam.textMarksForQuestion

        answerEditText = binding.appBarExam.answerEditText
        answerEditText?.isEnabled = false

        var textStudentName = binding.appBarExam.textStudentName
        textStudentName.text = STUDENT_NAME
        var textClassName  = binding.appBarExam.textClassName
        textClassName.text = cLASSNAME


        buttonMark = binding.appBarExam.buttonMark
        editTextMark  = binding.appBarExam.editTextMark
        editTextComments = binding.appBarExam.editTextComments
        textClearComments = binding.appBarExam.textClearComments
        constraintLayoutCamera = binding.appBarExam.constraintLayoutCamera
        constraintLayoutAudio = binding.appBarExam.constraintLayoutAudio
        cardViewRecorderLayout = binding.appBarExam.cardViewRecorderLayout

        /////////
        imageViewGetImage = binding.appBarExam.imageViewGetImage /////Comment Image uploaded already
        imageViewClose = binding.appBarExam.imageViewClose /////Comment ImageView Close or delete


        ////////
        cardViewAudioComment = binding.appBarExam.cardViewAudioComment /////comment Audio uploaded already
        playPauseRelativeCmd = binding.appBarExam.playPauseComment /////comment relative act like button play and pause
        playPauseImageCmd  = binding.appBarExam.playPauseImageCmd /////comment image for changing play and pause icon
        mSeekBarCmd = binding.appBarExam.mSeekBarCmd /////comment seekbar for audio running
        songDurationCmd = binding.appBarExam.songDurationCmd /////comment audio duration text
        imageAudioClose = binding.appBarExam.imageAudioClose /////Comment Audio Close or delete

        mediaPlayerComment = MediaPlayer()



        imageViewQuestion = binding.appBarExam.imageViewQuestion
        cardViewAudioQuestion = binding.appBarExam.cardViewAudioQuestion
        imageViewVideoQuestion = binding.appBarExam.imageViewVideoQuestion
        playPauseImageView = binding.appBarExam.playPauseImageView
        mSeekBar = binding.appBarExam.mSeekBar
        songDuration = binding.appBarExam.songDuration

        recordView = binding.appBarExam.recordView
        recordButton = binding.appBarExam.recordButton


        ////relative Layout for click event
        playPauseRelative = binding.appBarExam.playPauseRelative

        mediaPlayer = MediaPlayer()

        recyclerViewNumberList = binding.appBarExam.recyclerViewNumberList
        recyclerViewNumberList?.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        recyclerViewQuestionList = binding.navListLayout.recyclerViewQuestionList
        recyclerViewQuestionList?.layoutManager = LinearLayoutManager(this)

        recyclerViewFiles = binding.appBarExam.recyclerViewFiles
        recyclerViewFiles?.layoutManager = GridLayoutManager(this,3)

        imageLeftArrow = binding.appBarExam.imageLeftArrow
        imageRightArrow = binding.appBarExam.imageRightArror

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
                onNumberClick(itemPosition, questionAttemptedList[itemPosition], 3, "")
            }
        }

        imageRightArrow?.setOnClickListener {
            if ((itemPosition + 1) == questionAttemptedList.size) {
                Log.i(TAG, "No Skip Question")
                Utils.getSnackBar4K(this, "No Question after this", constraintFirstLayout!!)
            } else {
                ///radioButton selection item id
                itemPosition++
                onNumberClick(itemPosition, questionAttemptedList[itemPosition], 3, "")
            }
        }


        textClearComments?.setOnClickListener {
            if(deleteCommentsFromServer == 1){
                if (editTextComments?.text.toString().isNotEmpty()) {
                    deleteFileFromServer("OnlineDExam/RemoveTeacherComment",EXAM_ATTEMPT_ID_ANSWER,"text")
                    editTextComments?.setText("")
                }
            }else if(deleteCommentsFromServer == 0){
                editTextComments?.setText("")
            }

        }


        imageViewClose?.setOnClickListener {
            imageViewClose?.visibility = View.GONE
            imageViewGetImage?.visibility = View.GONE
            ///display audio VISIBLE
            cardViewAudioComment?.visibility = View.GONE
            imageAudioClose?.visibility = View.GONE
            ////upload layouts gone
            constraintLayoutCamera?.visibility = View.VISIBLE  ///////Image Upload Field
            constraintLayoutAudio?.visibility = View.VISIBLE  ///////Audio Upload Field
            cardViewRecorderLayout?.visibility = View.VISIBLE  ///////Audio Upload Field

            if(deleteFileFromServer == 1){
                //EXAM_ATTEMPT_ID_ANSWER
                deleteFileFromServer("OnlineDExam/RemoveTeacherCommentFile",EXAM_ATTEMPT_ID_ANSWER,"File")
                commentUplodedFile = ""
            }else if(deleteFileFromServer == 0){
                imagePathUpload = ""
            }
        }

        imageAudioClose?.setOnClickListener {
            if (mediaPlayerComment != null && mediaPlayerComment!!.isPlaying) {
                mediaPlayerComment!!.reset()
            }
            imageViewClose?.visibility = View.GONE
            imageViewGetImage?.visibility = View.GONE
            ///display audio VISIBLE
            cardViewAudioComment?.visibility = View.GONE
            imageAudioClose?.visibility = View.GONE
            ////upload layouts gone
            constraintLayoutCamera?.visibility = View.VISIBLE  ///////Image Upload Field
            constraintLayoutAudio?.visibility = View.VISIBLE  ///////Audio Upload Field
            cardViewRecorderLayout?.visibility = View.VISIBLE  ///////Audio Upload Field

            if(deleteFileFromServer == 1){
                //EXAM_ATTEMPT_ID_ANSWER
                deleteFileFromServer("OnlineDExam/RemoveTeacherCommentFile",EXAM_ATTEMPT_ID_ANSWER,"File")
                commentUplodedFile = ""
            }else if(deleteFileFromServer == 0){
                imagePathUpload = ""
            }
        }


        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }

        constraintLayoutCamera?.setOnClickListener {
            if (requestPermission()) {
                val imageCollection = sdk29AndUp {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val intent = Intent(Intent.ACTION_PICK, imageCollection)
                //  intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "image/*"
                startForResult.launch(intent)
            }
        }

        constraintLayoutAudio?.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_GET_CONTENT); // or ACTION_OPEN_DOCUMENT //ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "audio/*"
            startForResult.launch(intent)

        }


        initFunction()

        Utils.setStatusBarColor(this)
        if(checkRecordPermissions()) {
            recordView()
        }else if(!checkRecordPermissions()){
            requestRecordPermissions()
        }



        buttonMark?.setOnClickListener {

            if(deleteFileFromServer == 1){
                if(descriptiveExamStaffViewModel.validateField(editTextMark!!, "Give Mark for the question",this,constraintFirstLayout!!)
                    && descriptiveExamStaffViewModel.validateMark(editTextMark!!.text.toString().toInt(),questionMark
                        , "Given Mark is greater then question mark",this,constraintFirstLayout!!)){
                    progressStart()
                    submitFile(commentUplodedFile)
                    Log.i(TAG,"cOMMENTTYPE $cOMMENTTYPE")
                    Log.i(TAG,"commentUplodedFile $commentUplodedFile")
                }

            }else  if(deleteFileFromServer == 0){
                if(descriptiveExamStaffViewModel.validateField(editTextMark!!, "Give Mark for the question",this,constraintFirstLayout!!)
                    && descriptiveExamStaffViewModel.validateMark(editTextMark!!.text.toString().toInt(),questionMark
                        , "Given Mark is greater then question mark",this,constraintFirstLayout!!)){
                    progressStart()
                    if(imagePathUpload.isNotEmpty()){
                        Log.i(TAG,"imagePathUpload $imagePathUpload")
                        onFileUploadClick(imagePathUpload)
                    }else{
                        cOMMENTTYPE = "-1"
                        submitFile(commentUplodedFile)
                    }
                }
            }


        }
    }
    fun checkRecordPermissions(): Boolean {
        val result1 = ContextCompat.checkSelfPermission(
            applicationContext, RECORD_AUDIO
        )
        return result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        val permissionToRequests = mutableListOf<String>()
        permissionToRequests.add(RECORD_AUDIO)
        if (permissionToRequests.isNotEmpty()) {
            permissionsLauncher.launch(permissionToRequests.toTypedArray())
        }
//
//        ActivityCompat.requestPermissions(
//            this@DescGiveMarksPreviewActivity,
//            arrayOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE))
    }


    private fun recordView() {
        recordButton?.setRecordView(recordView)
        recordButton?.setOnRecordClickListener(OnRecordClickListener { })
        recordView?.cancelBounds = 8.0f
        recordView?.setSmallMicColor(Color.parseColor("#c2185b"))
        recordView?.setLessThanSecondAllowed(false)
        recordView?.setSlideToCancelText("Slide To Cancel")
        recordView?.setCustomSounds(0, R.raw.record_finished, 0)

        recordView?.setOnRecordListener(object : OnRecordListener {
            override fun onLessThanSecond() {
                Log.i(TAG, "onLessThanSecond")
            }
            override fun onStart() {
                Log.i(TAG, "onStart")
                // Toast.makeText(MainActivity.this, "OnStartRecord", Toast.LENGTH_SHORT).show();
                if (recorder == null) {
                    try {
                        fileName = Utils.getRootDirPath(this@DescGiveMarksPreviewActivity) + "/Record/Audio/"
                        val outFile = File(fileName)
                        if (!outFile.exists()) {
                            outFile.mkdirs()
                        } else {
                            Log.i(TAG, "Already existing")
                        }
                        val time = System.currentTimeMillis()
                        fileName += "AUD_$time.mp3"
                        file_name = "AUD_$time.mp3"
                        Log.i(TAG, "Audio- $fileName")
                        recorder = MediaRecorder()
                        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
                        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) ////change THREE_GPP TO MPEG_4   //DEFAULT
                        recorder!!.setOutputFile(fileName)
                        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC) /// change  AMR_NB to AAC    // DEFAULT
                        //    recorder.setAudioChannels(AudioFormat.CHANNEL_IN_MONO); //we wrote  //AudioFormat.CHANNEL_IN_MONO
                        recorder!!.setAudioChannels(1) //we wrote  //MONO
                        recorder!!.setAudioSamplingRate(44100) //// if not specified, defaults to 8kHz, if specified 44.1 or 48 kHz, lots of noise  // 44100 kHz
                        recorder!!.setAudioEncodingBitRate(64000) ////we wrote //  /64kbps ///20kbps //96kbps
                        //           }
                        try {
                            recorder!!.prepare()
                            // recorder.start();
                            //   recorder.start();
                        } catch (e: IOException) {
                            Log.e(TAG, "prepare() failed")
                        }
                        recorder!!.start()
                    } catch (e: RuntimeException) {
                        e.printStackTrace()
                    }
                    catch (n : NullPointerException){
                        n.printStackTrace()
                        Log.e(TAG, "NullPointerException $n")
                    }
                }
            }

            override fun onCancel() {
                val file = File(fileName)
                if (!file.exists()) {
                    return
                }
                if (file.delete()) {
                    val printStream = System.out
                    printStream.println("file Deleted :$fileName")
                    return
                }
                val printStream2 = System.out
                printStream2.println("file not Deleted :$fileName")
            }

            override fun onFinish(recordTime: Long, limitReached: Boolean) {
                val humanTimeText: String = FileUtils.getHumanTimeText(recordTime)
                Toast.makeText(this@DescGiveMarksPreviewActivity,
                    "onFinishRecord - Recorded Time is: $humanTimeText", Toast.LENGTH_SHORT
                ).show()
                try {
                    recorder!!.stop()
                    recorder!!.reset()
                    recorder!!.release()
                    recorder = null
                    val unused = recorder
                } catch (unused2: Exception) {
                    Toast.makeText(this@DescGiveMarksPreviewActivity, "Retry..", Toast.LENGTH_SHORT).show()
                }
//                constraintCommentImage_Audio.setVisibility(View.VISIBLE)
//                relativeCommentImageView.setVisibility(View.GONE)
//                constraintAlready.setVisibility(View.GONE)
//                constraintUpload.setVisibility(View.GONE)
//                cardViewRecorderLayout.setVisibility(View.GONE)
//                cardVoiceAudio_Recorded.setVisibility(View.VISIBLE)
//                constraintRecordedFromPhone.setVisibility(View.VISIBLE)
//                UploadStr = fileName
//                delete_from_server = 0
//                COMMENT_TYPE = "2"
//                val textView: TextView = textCommentDurationRec
//                textView.text = "00:00 | $humanTimeText"

                imageViewClose?.visibility = View.GONE
                imageViewGetImage?.visibility = View.GONE
                ///display audio VISIBLE
                cardViewAudioComment?.visibility = View.VISIBLE
                imageAudioClose?.visibility = View.VISIBLE
                ////upload layouts gone
                constraintLayoutCamera?.visibility = View.GONE  ///////Image Upload Field
                constraintLayoutAudio?.visibility = View.GONE  ///////Audio Upload Field
                cardViewRecorderLayout?.visibility = View.GONE  ///////Audio Upload Field
                cOMMENTTYPE = "2"
                imagePathUpload = fileName
                audioPlayerComment(fileName)

            }

        })
        recordView?.setOnBasketAnimationEndListener(OnBasketAnimationEnd { })


    }

    private fun initFunction() {
        descriptiveExamStaffViewModel.getDescGiveMarkPreviewList(aCCADEMICID,sTUDENTID,EXAM_ID,EXAM_ATTEMPT_ID)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            questionAttemptedList = response.studentQuestionAttemptedList as ArrayList<DescGiveMarkPreviewModel.StudentQuestionAttempted>

                            if(questionAttemptedList.isNotEmpty()){

                                textViewQuestionNo?.text = "Question 1"

                                textViewQuestion?.text = questionAttemptedList[0].qUESTIONTITLE
//                                if(!questionAttemptedList[0].aNSWERMARK.isNullOrEmpty()){
//                                    textMarksForQuestion?.text = "${questionAttemptedList[0].aNSWERMARK} / ${questionAttemptedList[0].qUESTIONMARK} Marks"
//                                }else{
//                                    textMarksForQuestion?.text = "No Mark / ${questionAttemptedList[0].qUESTIONMARK} Marks"
//                                }


                                questionTypeFun(questionAttemptedList[0])

                                adapter = NumberAdapter(
                                    this,
                                    questionAttemptedList,
                                    this@DescGiveMarksPreviewActivity
                                )
                                recyclerViewNumberList?.adapter = adapter

                                questionListAdapter = QuestionListAdapter(
                                    this,
                                    questionAttemptedList,
                                    this@DescGiveMarksPreviewActivity
                                )
                                recyclerViewQuestionList?.adapter = questionListAdapter
                            }else{

                            }

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {

                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            questionAttemptedList =
                                ArrayList<DescGiveMarkPreviewModel.StudentQuestionAttempted>()
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })

    }

    private fun questionTypeFun(questionArrayList: DescGiveMarkPreviewModel.StudentQuestionAttempted) {

        progressStart()
        imageViewClose?.visibility = View.GONE
        imageViewGetImage?.visibility = View.GONE
        ///display audio gone
        cardViewAudioComment?.visibility = View.GONE
        imageAudioClose?.visibility = View.GONE
        ////upload layouts gone
        constraintLayoutCamera?.visibility = View.GONE  ///////Image Upload Field
        constraintLayoutAudio?.visibility = View.GONE  ///////Audio Upload Field
        cardViewRecorderLayout?.visibility = View.GONE  ///////Audio Upload Field
        getTakeAnswer(questionArrayList)

        //mediaPlayer = MediaPlayer()

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
                    val intent = Intent(this@DescGiveMarksPreviewActivity, ExoPlayerActivity::class.java)
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

    private fun getTakeAnswer(questionArrayList: DescGiveMarkPreviewModel.StudentQuestionAttempted) {

        answerEditText?.setText("")
        editTextMark?.setText("")
        editTextComments?.setText("")
        ANSWER = ""
        ANSWER_FILE1 = ""
        ANSWER_FILE2 = ""
        ANSWER_FILE3 = ""
        fileNameList = ArrayList<FileName>()

        if (mediaPlayerComment != null && mediaPlayerComment!!.isPlaying) {
            mediaPlayerComment!!.reset()
        }

        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.reset()
        }
        Log.i(TAG,
            "http://demostaff.passdaily.in/ElixirApi/OnlineDExam/TakeAnswer?ExamId=$EXAM_ID" +
                    "&ExamAttemptId=$EXAM_ATTEMPT_ID&QuestionId=${questionArrayList.qUESTIONID}")

        val apiInterface = RetrofitClientStaff.create().takeAnswerNew(
            EXAM_ID,EXAM_ATTEMPT_ID ,questionArrayList.qUESTIONID)
        apiInterface.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>, response: Response<String>
            ) {
                if (response.isSuccessful) {
                    try{
                        ////{
                        //"Answer": {
                        //"QUESTION_ID": 1,
                        //"EXAM_ATTEMPT_D_ID": 21,
                        //"QUESTION_ID_ATTEMPT": 1,
                        //"ANSWER": "test description exam area",
                        //"ANSWER_FILE_1": "4892bdcb-0f58-4ea0-a96b-c72e5b07095dIMG_1653298719262.jpg",
                        //"ANSWER_FILE_2": "d3f581b1-2a13-424b-8ef4-8d6ee5b9e303IMG_1655121580464.jpg",
                        //"ANSWER_FILE_3": "null",
                        //"ANSWER_MARK": 3,
                        //"TEACHER_COMMENT": "Overwrite both onCreateDialog and onCreateView. Store view created at onCreateDialog in a member variable, and let onCreateView return that member variable.\r\n\r\n",
                        //"COMMENT_TYPE": -1,
                        //"COMMENT_FILE": ""
                        //}
                        //}
                        var result = response.body()
                        Log.i(TAG, "Response result $result")
                        val jObject = JSONObject(result!!)
                        val answer = jObject.getJSONObject("Answer")
                        var eXAMATTEMPTDID = answer.getString("EXAM_ATTEMPT_D_ID")
                        ANSWER = answer.getString("ANSWER")
                        ANSWER_FILE1 = answer.getString("ANSWER_FILE_1")
                        ANSWER_FILE2 = answer.getString("ANSWER_FILE_2")
                        ANSWER_FILE3 = answer.getString("ANSWER_FILE_3")
                        answerMark = answer.getInt("ANSWER_MARK")
                        var tEACHERCOMMENT = answer.getString("TEACHER_COMMENT")
                        cOMMENTTYPE = answer.getString("COMMENT_TYPE")
                        var cOMMENTFILE = answer.getString("COMMENT_FILE")



                        if (ANSWER != "null") {
                            answerEditText?.setText(ANSWER)
                        }else{
                            answerEditText?.setText("No Answer")
                        }


                        if(answerMark != 0 && answerMark != -1){
                            textMarksForQuestion?.text = "$answerMark / ${questionArrayList.qUESTIONMARK} Marks"
                        }else{
                            textMarksForQuestion?.text = "No Mark / ${questionArrayList.qUESTIONMARK} Marks"
                        }

                        if(answerMark != 0 && answerMark != -1) {
                            editTextMark?.setText(answerMark.toString())
                        }else{
                            editTextMark?.setText("")
                        }

                        questionMark = questionArrayList.qUESTIONMARK

                        if (tEACHERCOMMENT == "null") {
                            tEACHERCOMMENT = ""
                        }else{
                            deleteCommentsFromServer = 1
                        }
                        editTextComments?.setText(tEACHERCOMMENT.toString())
                        editTextComments?.setSelection(editTextComments?.text!!.length)


                        if(eXAMATTEMPTDID != "null"){
                            EXAM_ATTEMPT_ID_ANSWER = eXAMATTEMPTDID.toString().toInt()
                            editTextComments?.isEnabled = true  ///////comment EditText
                            textClearComments?.isEnabled = true ////button Comments
                            buttonMark?.isEnabled = true ///button Mark
                            editTextMark?.isEnabled = true  ///////Add Mark EditText
                            constraintLayoutCamera?.visibility = View.VISIBLE  ///////Image Upload Field
                            constraintLayoutAudio?.visibility = View.VISIBLE  ///////Audio Upload Field
                            cardViewRecorderLayout?.visibility = View.VISIBLE  ///////Audio Upload Field
                        }else{
                            editTextComments?.isEnabled = false  ///////comment EditText
                            textClearComments?.isEnabled = false ////button
                            buttonMark?.isEnabled = false ///button
                            editTextMark?.isEnabled = false  ///////Add Mark EditText
                            constraintLayoutCamera?.visibility = View.GONE  ///////Image Upload Field
                            constraintLayoutAudio?.visibility = View.GONE  ///////Audio Upload Field
                            cardViewRecorderLayout?.visibility = View.GONE  ///////Audio Upload Field
                        }


                        when (cOMMENTTYPE) {
                            "1" -> {
                                //display image visible
                                imageViewClose?.visibility = View.VISIBLE
                                imageViewGetImage?.visibility = View.VISIBLE
                                ///display audio gone
                                cardViewAudioComment?.visibility = View.GONE
                                imageAudioClose?.visibility = View.GONE
                                ////upload layouts gone
                                constraintLayoutCamera?.visibility = View.GONE  ///////Image Upload Field
                                constraintLayoutAudio?.visibility = View.GONE  ///////Audio Upload Field
                                cardViewRecorderLayout?.visibility = View.GONE  ///////Audio Upload Field
                                //teacher comments and mark given area
                                editTextComments?.isEnabled = true  ///////comment EditText
                                textClearComments?.isEnabled = true ////button Comments
                                buttonMark?.isEnabled = true ///button Mark
                                editTextMark?.isEnabled = true  ///////Add Mark EditText
                                Glide.with(this@DescGiveMarksPreviewActivity)
                                    .load(Cimage_path + cOMMENTFILE)
                                    .apply(
                                        RequestOptions.centerCropTransform()
                                            .dontAnimate() //   .override(imageSize, imageSize)
                                            .placeholder(R.drawable.image_icon)
                                    )
                                    .thumbnail(0.5f)
                                    .into(imageViewGetImage!!)
                                //                            delete_from_server = 1
                                //                            UploadStr = COMMENT_FILE

                                deleteFileFromServer = 1
                                commentUplodedFile = cOMMENTFILE
                            }
                            "2" -> {
                                //display image GONE
                                imageViewClose?.visibility = View.GONE
                                imageViewGetImage?.visibility = View.GONE
                                ///display audio VISIBLE
                                cardViewAudioComment?.visibility = View.VISIBLE
                                imageAudioClose?.visibility = View.VISIBLE
                                ////upload layouts gone
                                constraintLayoutCamera?.visibility = View.GONE  ///////Image Upload Field
                                constraintLayoutAudio?.visibility = View.GONE  ///////Audio Upload Field
                                cardViewRecorderLayout?.visibility = View.GONE  ///////Audio Upload Field
                                //teacher comments and mark given area
                                editTextComments?.isEnabled = true  ///////comment EditText
                                textClearComments?.isEnabled = true ////button Comments
                                buttonMark?.isEnabled = true ///button Mark
                                editTextMark?.isEnabled = true  ///////Add Mark EditText

                                deleteFileFromServer = 1
                                commentUplodedFile = cOMMENTFILE

                                audioPlayerComment(Cimage_path + cOMMENTFILE)
                            }
                            "-1", "null" -> {
                                //display image GONE
                                imageViewClose?.visibility = View.GONE
                                imageViewGetImage?.visibility = View.GONE
                                ///display audio VISIBLE
                                cardViewAudioComment?.visibility = View.GONE
                                imageAudioClose?.visibility = View.GONE
                                ////upload layouts gone
                                constraintLayoutCamera?.visibility = View.VISIBLE  ///////Image Upload Field
                                constraintLayoutAudio?.visibility = View.VISIBLE  ///////Audio Upload Field
                                cardViewRecorderLayout?.visibility = View.VISIBLE  ///////Audio Upload Field
                                //teacher comments and mark given area
                                editTextComments?.isEnabled = true  ///////comment EditText
                                textClearComments?.isEnabled = true ////button Comments
                                buttonMark?.isEnabled = true ///button Mark
                                editTextMark?.isEnabled = true  ///////Add Mark EditTextc
                                deleteFileFromServer = 0
                            }
                        }
                        if(eXAMATTEMPTDID == "null"){
                            editTextComments?.isEnabled = false  ///////comment EditText
                            textClearComments?.isEnabled = false ////button
                            buttonMark?.isEnabled = false ///button
                            editTextMark?.isEnabled = false  ///////Add Mark EditText
                            constraintLayoutCamera?.visibility = View.GONE  ///////Image Upload Field
                            constraintLayoutAudio?.visibility = View.GONE  ///////Audio Upload Field
                            cardViewRecorderLayout?.visibility = View.GONE  ///////Audio Upload Field
                        }

                        ANSWER_FILE1 = questionArrayList.aNSWERFILE1.toString()
                        if (ANSWER_FILE1 != "null" && ANSWER_FILE1 != "") {
                            //  fileNameList.add(FileName(qUESTIONID, ANSWER_FILE1,answerFileUrl1,"Json"))
                            fileNameList.add(FileName(questionArrayList.qUESTIONID, ANSWER_FILE1,"/DExamFile/Answer/","Json"))
                        }
                        ANSWER_FILE2 = questionArrayList.aNSWERFILE2.toString()
                        if (ANSWER_FILE2 != "null" && ANSWER_FILE2 != "") {
                            //  fileNameList.add(FileName(qUESTIONID, ANSWER_FILE2,answerFileUrl2,"Json"))
                            fileNameList.add(FileName(questionArrayList.qUESTIONID, ANSWER_FILE2,"/DExamFile/Answer/","Json"))
                        }
                        ANSWER_FILE3 = questionArrayList.aNSWERFILE3.toString()
                        if (ANSWER_FILE3 != "null" && ANSWER_FILE3 != "") {
                            //   fileNameList.add(FileName(qUESTIONID, ANSWER_FILE3,answerFileUrl3,"Json"))
                            fileNameList.add(FileName(questionArrayList.qUESTIONID, ANSWER_FILE3,"/DExamFile/Answer/","Json"))
                        }

                        fileAdapter = FileAdapter(
                            this@DescGiveMarksPreviewActivity, fileNameList, this@DescGiveMarksPreviewActivity, Aimage_path
                        )

                        recyclerViewFiles?.adapter = fileAdapter

                        progressStop()

                    } catch (error : JSONException) {
                        progressStop()
                        Log.i(TAG, "JSONException $error")
                    }

                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                progressStop()
                Log.i(TAG, "Throwable $t")
            }
        })



//        descriptiveExamStaffViewModel.getTakeAnswerDExam(EXAM_ID,EXAM_ATTEMPT_ID,questionArrayList.qUESTIONID)
//            .observe(this, Observer {
//                it?.let { resource ->
//                    Log.i(TAG,"resource $resource")
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//
//                            var answerDetails = response.answer
//                            Log.i(TAG,"answerDetails $answerDetails")
//
//                            ANSWER = answerDetails.aNSWER!!
//                            if (!ANSWER.isNullOrEmpty()) {
//                                answerEditText?.setText(ANSWER)
//                            }else{
//                                answerEditText?.setText("No Answer")
//                            }
//                            answerMark = answerDetails.aNSWERMARK
//
//                            if(answerMark != 0 && answerMark != -1){
//                                textMarksForQuestion?.text = "$answerMark / ${questionArrayList.qUESTIONMARK} Marks"
//                                editTextMark?.setText(answerMark.toString())
//                            }else{
//                                textMarksForQuestion?.text = "No Mark / ${questionArrayList.qUESTIONMARK} Marks"
//                                editTextMark?.setText(answerMark.toString())
//                            }
//
//                            questionMark = questionArrayList.qUESTIONMARK
//
//
//                            var tEACHERCOMMENT = answerDetails.tEACHERCOMMENT
//                            if (!tEACHERCOMMENT.isNullOrEmpty()) {
//                                tEACHERCOMMENT = "test"
//                            }
//                            editTextComments?.setText(tEACHERCOMMENT.toString())
//                            editTextComments?.setSelection(editTextComments?.text!!.length)
//
//
//                            if(answerDetails.eXAMATTEMPTDID != null){
//                                EXAM_ATTEMPT_ID_ANSWER = answerDetails.eXAMATTEMPTDID.toString().toInt()
//                                editTextComments?.isEnabled = true  ///////comment EditText
//                                textClearComments?.isEnabled = true ////button Comments
//                                buttonMark?.isEnabled = true ///button Mark
//                                editTextMark?.isEnabled = true  ///////Add Mark EditText
//                                constraintLayoutCamera?.isEnabled = true  ///////Image Upload Field
//                                constraintLayoutAudio?.isEnabled = true  ///////Audio Upload Field
//                                cardViewRecorderLayout?.isEnabled = true  ///////Audio Upload Field
//                            }else{
//                                editTextComments?.isEnabled = false  ///////comment EditText
//                                textClearComments?.isEnabled = false ////button
//                                buttonMark?.isEnabled = false ///button
//                                editTextMark?.isEnabled = false  ///////Add Mark EditText
//                                constraintLayoutCamera?.isEnabled = false  ///////Image Upload Field
//                                constraintLayoutAudio?.isEnabled = false  ///////Audio Upload Field
//                                cardViewRecorderLayout?.isEnabled = false  ///////Audio Upload Field
//                            }
//
//
//
//                            ANSWER_FILE1 = questionArrayList.aNSWERFILE1.toString()
//                            if (ANSWER_FILE1 != "null" && ANSWER_FILE1 != "") {
//                                //  fileNameList.add(FileName(qUESTIONID, ANSWER_FILE1,answerFileUrl1,"Json"))
//                                fileNameList.add(FileName(questionArrayList.qUESTIONID, ANSWER_FILE1,"/DExamFile/Answer/","Json"))
//                            }
//                            ANSWER_FILE2 = questionArrayList.aNSWERFILE2.toString()
//                            if (ANSWER_FILE2 != "null" && ANSWER_FILE2 != "") {
//                                //  fileNameList.add(FileName(qUESTIONID, ANSWER_FILE2,answerFileUrl2,"Json"))
//                                fileNameList.add(FileName(questionArrayList.qUESTIONID, ANSWER_FILE2,"/DExamFile/Answer/","Json"))
//                            }
//                            ANSWER_FILE3 = questionArrayList.aNSWERFILE3.toString()
//                            if (ANSWER_FILE3 != "null" && ANSWER_FILE3 != "") {
//                                //   fileNameList.add(FileName(qUESTIONID, ANSWER_FILE3,answerFileUrl3,"Json"))
//                                fileNameList.add(FileName(questionArrayList.qUESTIONID, ANSWER_FILE3,"/DExamFile/Answer/","Json"))
//                            }
//
//                            fileAdapter = FileAdapter(
//                                this@DescGiveMarksPreviewActivity, fileNameList, this@DescGiveMarksPreviewActivity, Aimage_path
//                            )
//
//                            recyclerViewFiles?.adapter = fileAdapter

//
//                            Log.i(TAG,"getSubjectList SUCCESS")
//                        }
//                        Status.ERROR -> {
//
//                            Log.i(TAG,"getSubjectList ERROR")
//                        }
//                        Status.LOADING -> {
//
//                            Log.i(TAG,"getSubjectList LOADING")
//                        }
//                    }
//                }
//            })


//        answerEditText?.setText("")
//        ANSWER = ""
//        ANSWER_FILE1 = ""
//        ANSWER_FILE2 = ""
//        ANSWER_FILE3 = ""
//        fileNameList = ArrayList<FileName>()
//
//        ANSWER = questionArrayList.aNSWER.toString()
//        if (ANSWER != "null") {
//            answerEditText?.setText(ANSWER)
//        }
//        questionMark = questionArrayList.qUESTIONMARK
//
//        if(questionArrayList.eXAMATTEMPTDID != null){
//            EXAM_ATTEMPT_ID_ANSWER = questionArrayList.eXAMATTEMPTDID!!.toInt()
//        }
//
//
//        editTextMark!!.setText(questionArrayList.aNSWERMARK)
//
//        ANSWER_FILE1 = questionArrayList.aNSWERFILE1.toString()
//        if (ANSWER_FILE1 != "null" && ANSWER_FILE1 != "") {
//            //  fileNameList.add(FileName(qUESTIONID, ANSWER_FILE1,answerFileUrl1,"Json"))
//            fileNameList.add(FileName(questionArrayList.qUESTIONID, ANSWER_FILE1,"/DExamFile/Answer/","Json"))
//        }
//        ANSWER_FILE2 = questionArrayList.aNSWERFILE2.toString()
//        if (ANSWER_FILE2 != "null" && ANSWER_FILE2 != "") {
//            //  fileNameList.add(FileName(qUESTIONID, ANSWER_FILE2,answerFileUrl2,"Json"))
//            fileNameList.add(FileName(questionArrayList.qUESTIONID, ANSWER_FILE2,"/DExamFile/Answer/","Json"))
//        }
//        ANSWER_FILE3 = questionArrayList.aNSWERFILE3.toString()
//        if (ANSWER_FILE3 != "null" && ANSWER_FILE3 != "") {
//            //   fileNameList.add(FileName(qUESTIONID, ANSWER_FILE3,answerFileUrl3,"Json"))
//            fileNameList.add(FileName(questionArrayList.qUESTIONID, ANSWER_FILE3,"/DExamFile/Answer/","Json"))
//        }
//
//        fileAdapter = FileAdapter(
//            this@DescGiveMarksPreviewActivity, fileNameList, this@DescGiveMarksPreviewActivity, Aimage_path
//        )
//
//        recyclerViewFiles?.adapter = fileAdapter


    }

    //http://demostaff.passdaily.in/ElixirApi/OnlineDExam/GiveMark?AdminId=1&ExamAttemptId=1021&Mark=4
    private fun submitFile(fileName : String) {
        /// HashMap<String, String> hashMap = new HashMap<String, String>();
        //        hashMap.put("ADMIN_ID", Global.Admin_id);
        //        hashMap.put("EXAM_ATTEMPT_D_ID", EXAM_ATTEMPT_D_ID1);
        //        hashMap.put("ANSWER_MARK", editAddMark.getText().toString());
        //        hashMap.put("TEACHER_COMMENT", editAddComments.getText().toString());
        //        hashMap.put("COMMENT_TYPE", COMMENT_TYPE);
        //        hashMap.put("COMMENT_FILE", str);
        val url = "OnlineDExam/GiveMarkNew"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("ADMIN_ID", adminId)
            jsonObject.put("EXAM_ATTEMPT_D_ID", EXAM_ATTEMPT_ID_ANSWER)
            jsonObject.put("ANSWER_MARK", editTextMark?.text.toString())
            jsonObject.put("TEACHER_COMMENT", editTextComments?.text.toString())
            jsonObject.put("COMMENT_TYPE", cOMMENTTYPE)
            jsonObject.put("COMMENT_FILE", fileName)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        descriptiveExamStaffViewModel.getCommonPostFun(url,accademicRe)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "OK" -> {
                                    Utils.getSnackBarGreen(this,"Comments Added Successfully",constraintFirstLayout!!)
                                    //   descQuestionListener.onCreateClick("")
                                    //    cancelFrg()
                                    itemPosition++
                                    onNumberClick(itemPosition, questionAttemptedList[itemPosition], 3, "")
                                }
                                Utils.resultFun(response) == "FAIL" -> {
                                    Utils.getSnackBar4K(this, "Failed while adding comments", constraintFirstLayout!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(this, "Please try again after sometime", constraintFirstLayout!!)
                        }
                        Status.LOADING -> {
                            //   progressStart()
                            Log.i(CreateDescriptiveExam.TAG,"loading")
                        }
                    }
                }
            })


    }


    fun onFileUploadClick(selectedFilePath: String) {

        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(TAG,"selectedFilePath $selectedFilePath");
        filesToUpload[0] = File(selectedFilePath)
        Log.i(TAG,"filesToUpload $filesToUpload");

        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload, "",object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                Log.i(TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {

                commentUplodedFile = responses[0]
                Log.i(TAG,"commentUplodedFile $commentUplodedFile")
                submitFile(commentUplodedFile)

            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })



//        Log.i(TAG, "fileName  $selectedFilePath")
//
//        var imagenPerfil = if (selectedFilePath.toString().contains(".jpg") || selectedFilePath.toString()
//                .contains(".jpeg")
//            || selectedFilePath.toString().contains(".png") || selectedFilePath.toString()
//                .contains(".JPG") || selectedFilePath.toString().contains(".JPEG")
//            || selectedFilePath.toString().contains(".PNG")
//        ) {
//            val file1 = File(selectedFilePath)
//            val requestFile: RequestBody =
//                file1.asRequestBody("image/*".toMediaTypeOrNull())
//            MultipartBody.Part.createFormData(
//                "STUDY_METERIAL_FILE",
//                file1.name,
//                requestFile
//            )
//        } else {
//            val file1 = File(selectedFilePath)
//            val requestFile: RequestBody = RequestBody.create(
//                "multipart/form-data".toMediaTypeOrNull(),
//                selectedFilePath
//            )
//            // MultipartBody.Part is used to send also the actual file name
//            MultipartBody.Part.createFormData(
//                "STUDY_METERIAL_FILE",
//                selectedFilePath,
//                requestFile
//            )
//        }
//
//
//        val apiInterface = RetrofitClientStaff.create().fileUploadAssignment(
//            SERVER_URL,
//            imagenPerfil
//        )
//        apiInterface.enqueue(object : Callback<FileResultModel> {
//            override fun onResponse(
//                call: Call<FileResultModel>,
//                resource: Response<FileResultModel>
//            ) {
//                val response = resource.body()
//                if (resource.isSuccessful) {
//                    Log.i(TAG, "response  $response")
//                    //  fileNames.add(response!!.dETAILS)
//                    commentUplodedFile = response?.dETAILS!!
//                    submitFile(commentUplodedFile)
//                }
//
//            }
//
//            override fun onFailure(call: Call<FileResultModel>, t: Throwable) {
//                Log.i(CreateDescriptiveQuestion.TAG, "Throwable  $t")
//            }
//        })


    }



    private fun deleteFileFromServer(path : String,eXAMATTEMPT_D_ID : Int,type : String) {
        descriptiveExamStaffViewModel.deleteCommentsFile(path,eXAMATTEMPT_D_ID)
            .observe(this, Observer {
                it?.let { resource ->
                    Log.i(TAG,"giveMarks $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(resource.data.isSuccessful){
                                Log.i(TAG,"deleteCommentsFile isSuccessful")
                                if(type == "text"){
                                    deleteCommentsFromServer = 0
                                }else if(type == "File"){
                                    deleteFileFromServer = 0
                                }
                            }else{
                                Utils.getSnackBar4K(this, "Failed while deleting", constraintFirstLayout!!)
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"deleteCommentsFile ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"deleteCommentsFile LOADING")
                        }
                    }
                }
            })

    }



    private fun audioPlayerComment(audioPath : String){

        mediaPlayerComment = MediaPlayer()
        if (mediaPlayerComment != null && mediaPlayerComment!!.isPlaying) {
            mediaPlayerComment!!.reset()
        }

        //MediaPlayer.create(context, R.raw.sample_media)
        mediaPlayerComment!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayerComment!!.setDataSource(audioPath)
            mediaPlayerComment!!.prepare() // might take long for buffering.
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (ex: IllegalStateException) {
            ex.printStackTrace()
        }

        mSeekBarCmd!!.max = mediaPlayerComment!!.duration
        //   mSeekBar.setTag(position);
        //   mSeekBar.setTag(position);
        songDurationCmd?.text =
            "0 : 0" + " | " + calculateDuration(mediaPlayerComment!!.duration)
        mSeekBarCmd!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                if (mediaPlayerComment != null && fromUser) {
                    mediaPlayerComment!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })


        playPauseRelativeCmd?.setOnClickListener(View.OnClickListener {
            if (!mediaPlayerComment!!.isPlaying) {
                mediaPlayerComment!!.start()
                playPauseImageCmd?.setImageResource(R.drawable.ic_exam_pause)
                runCmd = Runnable {
                    // Updateing SeekBar every 100 miliseconds
                    mSeekBarCmd!!.progress = mediaPlayerComment!!.currentPosition
                    seekHandlerCmd.postDelayed(runCmd!!, 100)
                    //For Showing time of audio(inside runnable)
                    val miliSeconds = mediaPlayerComment!!.currentPosition
                    if (miliSeconds != 0) {
                        //if audio is playing, showing current time;
                        val minutes = TimeUnit.MILLISECONDS.toMinutes(miliSeconds.toLong())
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(miliSeconds.toLong())
                        if (minutes == 0L) {
                            songDurationCmd?.text =
                                "0 : $seconds | " + calculateDuration(
                                    mediaPlayerComment!!.duration
                                )
                        } else {
                            if (seconds >= 60) {
                                val sec = seconds - minutes * 60
                                songDurationCmd?.text =
                                    "$minutes : $sec | " + calculateDuration(
                                        mediaPlayerComment!!.duration
                                    )
                            }
                        }
                    } else {
                        //Displaying total time if audio not playing
                        val totalTime = mediaPlayerComment!!.duration
                        val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime.toLong())
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime.toLong())
                        if (minutes == 0L) {
                            songDurationCmd?.text = "0 : $seconds"
                        } else {
                            if (seconds >= 60) {
                                val sec = seconds - minutes * 60
                                songDurationCmd?.text = "$minutes : $sec"
                            }
                        }
                    }
                }
                runCmd!!.run()
            } else {
                mediaPlayerComment!!.pause()
                playPauseImageCmd?.setImageResource(R.drawable.ic_exam_play)
            }
        })

        mediaPlayerComment!!.setOnCompletionListener {
            playPauseImageCmd?.setImageResource(R.drawable.ic_exam_play)
            mediaPlayerComment!!.pause()
        }
    }


    class NumberAdapter(
        var numberClickListener: NumberDescStaffClickListener,
        var mList: ArrayList<DescGiveMarkPreviewModel.StudentQuestionAttempted>,
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
        var numberClickListener: NumberDescStaffClickListener,
        var mList: ArrayList<DescGiveMarkPreviewModel.StudentQuestionAttempted>,
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

            if (!mList[position].aNSWER.isNullOrEmpty()) {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.green_400))
                holder.textViewNumber.setTextColor(context.resources.getColor(R.color.white))
            } else {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.color_chemistry))
                holder.textViewNumber.setTextColor(context.resources.getColor(R.color.white))
            }

            holder.textViewNumber.text = (position + 1).toString()
            holder.textViewQuestion.text = mList[position].qUESTIONTITLE

            holder.constraint.setOnClickListener {
                numberClickListener.onNumberClick(position, mList[position], 3, "")
                index = position
                notifyDataSetChanged()
            }

            if (index == position) {
                holder.constraint.setBackgroundColor(context.resources.getColor(R.color.gray_100))
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


    /////////////////////////////////////////////////////file list
    class FileAdapter(var numberClickListener: NumberDescStaffClickListener,
                      var fileNameList: ArrayList<FileName>,
                      var context: Context,
                      var Aimage_path : String
    ) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageViewFile: ImageView = view.findViewById(R.id.imageViewFile)
            var imageViewClose: ImageView = view.findViewById(R.id.imageViewClose)
            var buttonUpload : AppCompatButton = view.findViewById(R.id.buttonUpload)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.file_name_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.imageViewClose.visibility = View.GONE

            if(fileNameList[position].type == "Json") {
                holder.buttonUpload.visibility = View.GONE
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
                holder.buttonUpload.visibility = View.GONE
                Glide.with(context)
                    .load(fileNameList[position].fileName)
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
                numberClickListener.onViewImagePreview()
            }
        }

        override fun getItemCount(): Int {
            return  fileNameList.size
        }

    }

    override fun onNumberClick(
        position: Int,
        questionAttemptedList: DescGiveMarkPreviewModel.StudentQuestionAttempted,
        type: Int,
        lastValue: String
    ) {
        drawerLayout?.closeDrawer(GravityCompat.END)
        itemPosition = position
        Log.i(TAG, "id $position")

        textViewQuestionNo?.text = "Question ${position + 1}"
        textViewQuestion?.text = questionAttemptedList.qUESTIONTITLE
//        if(!questionAttemptedList.aNSWERMARK.isNullOrEmpty()){
//            textMarksForQuestion?.text = "${questionAttemptedList.aNSWERMARK} / ${questionAttemptedList.qUESTIONMARK} Marks"
//        }else{
//            textMarksForQuestion?.text = "No Mark / ${questionAttemptedList.qUESTIONMARK} Marks"
//        }

        questionTypeFun(questionAttemptedList)


        getQuestionPaletteLoad(type,position)
    }

    override fun onViewImagePreview() {
        val extra = Bundle()
        extra.putInt("position", 0)
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val newFragment = SlideshowDialogFragment.newInstance()
        newFragment.arguments = extra
        newFragment.show(ft, newFragment.TAG)
    }

    private fun getQuestionPaletteLoad(type: Int,position : Int) {


        questionListAdapter =
            QuestionListAdapter(
                this,
                questionAttemptedList,
                applicationContext
            )
        recyclerViewQuestionList?.adapter = questionListAdapter

//                            if (lastValue == "lastValue") {
//                                val bottomSheet = DescExamFinishSheetFragment(this,tOTALQUESTIONS, aNSWEREDQUESTIONS, TO_BE_ANSWERED,1)
//                                bottomSheet.isCancelable = false
//                                bottomSheet.show(supportFragmentManager, "TAG")
//                            }

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
    var startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data
                Log.i(TAG, "data $data")
                var imagePath = ""
                if (data?.data != null) {
                    val imageUri: Uri? = data.data
                    imagePath = imageUri!!.toString()
                }
                imagePathUpload = FileUtils.getReadablePathFromUri(this, imagePath.toUri())!!
                Log.i(TAG,"imagePathUpload $imagePathUpload")

                // val mFile = FileUtils.getReadablePathFromUri(this, imagePath.toUri())

                if (imagePathUpload.toString().contains(".jpg") || imagePathUpload.toString().contains(".jpeg")
                    || imagePathUpload.toString().contains(".png") || imagePathUpload.toString()
                        .contains(".JPG") || imagePathUpload.toString().contains(".JPEG")
                    || imagePathUpload.toString().contains(".PNG")
                ) {
                    cOMMENTTYPE = "1"
                    imageViewClose?.visibility = View.VISIBLE
                    imageViewGetImage?.visibility = View.VISIBLE
                    ///display audio gone
                    cardViewAudioComment?.visibility = View.GONE
                    imageAudioClose?.visibility = View.GONE
                    ////upload layouts gone
                    constraintLayoutCamera?.visibility = View.GONE  ///////Image Upload Field
                    constraintLayoutAudio?.visibility = View.GONE  ///////Audio Upload Field
                    cardViewRecorderLayout?.visibility = View.GONE  ///////Audio Upload Field

                    // JPG file
                    Glide.with(this)
                        .load(imagePath)
                        .into(imageViewGetImage!!)
                }else if (imagePathUpload.toString().contains(".mp3") || imagePathUpload.toString()
                        .contains(".wav") || imagePathUpload.toString().contains(".ogg")
                    || imagePathUpload.toString().contains(".m4a") || imagePathUpload.toString()
                        .contains(".aac") || imagePathUpload.toString().contains(".wma") ||
                    imagePathUpload.toString().contains(".MP3") || imagePathUpload.toString()
                        .contains(".WAV") || imagePathUpload.toString().contains(".OGG")
                    || imagePathUpload.toString().contains(".M4A") || imagePathUpload.toString()
                        .contains(".AAC") || imagePathUpload.toString().contains(".WMA")
                ) {
                    cOMMENTTYPE = "2"
                    //display image GONE
                    imageViewClose?.visibility = View.GONE
                    imageViewGetImage?.visibility = View.GONE
                    ///display audio VISIBLE
                    cardViewAudioComment?.visibility = View.VISIBLE
                    imageAudioClose?.visibility = View.VISIBLE
                    ////upload layouts gone
                    constraintLayoutCamera?.visibility = View.GONE  ///////Image Upload Field
                    constraintLayoutAudio?.visibility = View.GONE  ///////Audio Upload Field
                    cardViewRecorderLayout?.visibility = View.GONE  ///////Audio Upload Field

                    audioPlayerComment(imagePathUpload)
                }

            }

        }

    fun requestPermission(): Boolean {

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


    private fun progressStart() {
        val dialog1 = ProgressBarDialog()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.isCancelable = false
        dialog1.show(transaction, ProgressBarDialog.TAG)
    }

    fun progressStop() {
        val fragment: ProgressBarDialog? =
            supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
        if (fragment != null) { supportFragmentManager.beginTransaction().remove(fragment)
            .commitAllowingStateLoss()
        }
    }
}

interface NumberDescStaffClickListener {
    fun onNumberClick(
        position: Int,
        questionAttemptedList: DescGiveMarkPreviewModel.StudentQuestionAttempted,
        type: Int,
        lastValue: String
    )

    fun onViewImagePreview()
}



//        descriptiveExamStaffViewModel.giveMarks(adminId ,EXAM_ATTEMPT_ID_ANSWER,marks)
//            .observe(this, Observer {
//                it?.let { resource ->
//                   // Log.i(TAG,"giveMarks $resource")
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            if(resource.data.isSuccessful){
//                            //    Utils.getSnackBarGreen(this, "Mark Added", constraintFirstLayout!!)
//                                if ((itemPosition + 1) == questionAttemptedList.size) {
//                                    Log.i(TAG, "No Skip Question")
//                                    Utils.getSnackBar4K(this, "No Question after this", constraintFirstLayout!!)
//                                } else {
//                                    ///radioButton selection item id
//                                    itemPosition++
//                                    onNumberClick(itemPosition, questionAttemptedList[itemPosition], 3, "")
//                                }
//                            }else{
//                                Utils.getSnackBar4K(this, "Failed while adding mark", constraintFirstLayout!!)
//                            }
//
//                            Log.i(TAG,"giveMarks SUCCESS")
//                        }
//                        Status.ERROR -> {
//                            Log.i(TAG,"giveMarks ERROR")
//                        }
//                        Status.LOADING -> {
//                            Log.i(TAG,"getSubgiveMarksjectList LOADING")
//                        }
//                    }
//                }
//            })