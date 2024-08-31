package info.passdaily.camrelconvertapp.typeofuser.parent.description_exam

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.*
import android.webkit.URLUtil
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
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityDexamPreviewBinding
import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
import info.passdaily.camrelconvertapp.lib.video.Video_Activity
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.calculateDuration
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.CreateAssignmentDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.FileList
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image.SlideshowDialogFragment
import java.io.IOException
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION", "SetTextI18n")
@SuppressLint("NotifyDataSetChanged")
class DExamPreviewActivity : AppCompatActivity(), NumberDPClickListener {
    var TAG = "DExamPreviewActivity"

    var Qimage_path = Global.event_url + "/DExamFile/Question/"

    ////http://demo.passdaily.in/DExamFile/Question/fd93916c-0db2-49c4-b9ce-aa56f7fd504a.jpg
    var Aimage_path = Global.event_url + "/DExamFile/Answer/"

    var answerKeyPath = Global.event_url + "/DExamFile/"

    private lateinit var dExamAreaViewModel: DExamAreaViewModel
    private lateinit var binding: ActivityDexamPreviewBinding

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


    var textStudentName: TextView? = null
    var textClassName: TextView? = null

    var imageLeftArrow: ConstraintLayout? = null
    var imageRightArrow: ConstraintLayout? = null

    var textViewQuestion: TextView? = null
    var textViewQuestionNo: TextView? = null
    var textMarksForQuestion: TextView? = null



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

    var STUDENT_NAME =""
    var answerFileUrl1 ="OnlineDExam/DeleteFile1"
    var answerFileUrl2 ="OnlineDExam/DeleteFile2"
    var answerFileUrl3 ="OnlineDExam/DeleteFile3"

    var EXAM_NAME = ""
    var ALLOWED_PAUSE = 0
    var PAUSED_COUNT = 0
    var TIME_NOW = ""
    var SUBJECT_NAME = ""

    var CLASS_NAME = ""

    var GET_START_TIME_WHEN_ACTIVITY_STARTS = 0
    var END_QUESTION_TIME = 0

    var recyclerViewFiles : RecyclerView? = null

    var tOTALQUESTIONS = 0
    var aNSWEREDQUESTIONS = 0
    var TO_BE_ANSWERED: Int = 0
    var questionList = ArrayList<QuestionDListModel.Question>()

    var fileNameList = ArrayList<FileName>()

    var tempFileNameList = ArrayList<FileName>()

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

    var mediaPlayerAnswer: MediaPlayer? = null
    var textViewAnswerKey : TextView? = null
    var imageViewAnswer : ImageView? = null
    var cardViewAudioAnswer  : CardView? = null
    var mSeekBarAns : SeekBar? = null
    var songDurationAns : TextView? = null
    var imageViewVideoAnswer : ImageView? = null
    var playPauseRelativeAns : RelativeLayout? = null
    var playPauseImageViewAns : ImageView? = null
    var seekHandlerAnswer = Handler()
    var runAns: Runnable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dexam_area)

        dExamAreaViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[DExamAreaViewModel::class.java]

        val extras = intent.extras
        if (extras != null) {
            EXAM_ID = extras.getInt("EXAM_ID")
            EXAM_ATTEMPT_ID = extras.getInt("EXAM_ATTEMPT_ID")
            STUDENT_NAME = extras.getString("STUDENT_NAME")!!
            EXAM_NAME = extras.getString("eXAMNAME")!!
        }


        binding = ActivityDexamPreviewBinding.inflate(layoutInflater)
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
            supportActionBar!!.title = "PreView"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //   toolbar.setTitle("About-Us");
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
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

        recyclerViewNumberList = binding.appBarExam.recyclerViewNumberList
        recyclerViewNumberList?.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        imageLeftArrow = binding.appBarExam.imageLeftArrow
        imageRightArrow = binding.appBarExam.imageRightArror


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

        //////answer key
        mediaPlayerAnswer = MediaPlayer()
        textViewAnswerKey = binding.appBarExam.textViewAnswerKey
        imageViewAnswer = binding.appBarExam.imageViewAnswer
        cardViewAudioAnswer = binding.appBarExam.cardViewAudioAnswer
        mSeekBarAns = binding.appBarExam.mSeekBarAns
        songDurationAns = binding.appBarExam.songDurationAns
        imageViewVideoAnswer = binding.appBarExam.imageViewVideoAnswer
        playPauseRelativeAns = binding.appBarExam.playPauseRelativeAns
        playPauseImageViewAns = binding.appBarExam.playPauseImageViewAns

        textViewQuestion = binding.appBarExam.textViewQuestion
        textViewQuestionNo = binding.appBarExam.textViewQuestionNo
        textMarksForQuestion = binding.appBarExam.textMarksForQuestion


        recyclerViewFiles = binding.appBarExam.recyclerViewFiles
        recyclerViewFiles?.layoutManager = GridLayoutManager(this,3)

        textStudentName = binding.appBarExam.textStudentName
        textClassName = binding.appBarExam.textClassName
        textStudentName?.text = Global.studentName
        textClassName?.text = Global.className
        ///Nav item
        recyclerViewQuestionList = binding.navListLayout.recyclerViewQuestionList
        recyclerViewQuestionList?.layoutManager = LinearLayoutManager(this)



//        permissionsLauncher =
//            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//                readPermission =
//                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
//                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
//                    ?: writePermission
//
//            }

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
                onNumberClick(itemPosition, Global.dOnlineExamResult[itemPosition], 3, "")
            }
        }

        imageRightArrow?.setOnClickListener {
            if ((itemPosition + 1) == questionList.size) {
                Log.i(TAG, "No Skip Question")
                Utils.getSnackBar4K(this, "No Question after this", constraintFirstLayout!!)
            } else {
                ///radioButton selection item id
                itemPosition++
                onNumberClick(itemPosition, Global.dOnlineExamResult[itemPosition], 3, "")
            }
        }

        ////////////////////////////
        getTotalQuestions(EXAM_ATTEMPT_ID)


      //  Utils.setStatusBarColor(this)
        Utils.setStatusBarColor(this)

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


    private fun getTotalQuestions(EXAM_ATTEMPT_ID: Int) {


        textViewQuestionNo?.text = "Question 1"
        textViewQuestion?.text = Global.dOnlineExamResult[0].qUESTIONTITLE
//
        textMarksForQuestion?.text = Global.dOnlineExamResult[0].qUESTIONMARK.toString()

        textViewAnswerKey!!.visibility = View.VISIBLE

        if(!Global.dOnlineExamResult[0].aNSWERKEY!!.isNullOrEmpty() &&
            Global.dOnlineExamResult[0].aNSWERKEY != "There Is No Teacher Comment"){
            var s = "<font><b>Teacher Comments :  </b></font>"
            //String s = "<font><b>Teacher Comments : </b></font>";
            textViewAnswerKey?.text = Html.fromHtml(s + Global.dOnlineExamResult[0].aNSWERKEY)
        } else {
            textViewAnswerKey!!.text =  Html.fromHtml(Global.dOnlineExamResult[0].aNSWERKEY)
        }

        if(!Global.dOnlineExamResult[0].aNSWERKEYFILETYPE.toString().isNullOrEmpty()
            && Global.dOnlineExamResult[0].aNSWERKEYFILETYPE != 0){
            answerTypeFun(Global.dOnlineExamResult[0])
        }else{
            imageViewAnswer?.visibility = View.GONE
            cardViewAudioAnswer?.visibility = View.GONE
            imageViewVideoAnswer?.visibility = View.GONE
        }



        getTakeAnswer(Global.dOnlineExamResult[0])

        adapter =
            NumberAdapter(
                this,
                Global.dOnlineExamResult,
                applicationContext
            )
        recyclerViewNumberList?.adapter = adapter

        questionListAdapter =
            QuestionListAdapter(
                this,
                Global.dOnlineExamResult,
                applicationContext
            )
        recyclerViewQuestionList?.adapter = questionListAdapter


    }

    private fun getTakeAnswer(dOnlineExamResult : OnlineDExamResultModel.DOnlineExamResult) {

        answerEditText?.setText("")
        QUESTION_ID = 0
        ANSWER = ""
        ANSWER_FILE1 = ""
        ANSWER_FILE2 = ""
        ANSWER_FILE3 = ""
        fileNameList = ArrayList<FileName>()
        tempFileNameList = ArrayList<FileName>()

        QUESTION_ID =  dOnlineExamResult.qUESTIONID
        ANSWER = dOnlineExamResult.aNSWER.toString()
        if (ANSWER != "null") {
            answerEditText?.setText(ANSWER)
        }
        ANSWER_FILE1 = dOnlineExamResult.aNSWERFILE1.toString()
        if (ANSWER_FILE1 != "null" && ANSWER_FILE1 != "") {
            //  fileNameList.add(FileName(qUESTIONID, ANSWER_FILE1,answerFileUrl1,"Json"))
            tempFileNameList.add(FileName(dOnlineExamResult.qUESTIONID, ANSWER_FILE1,answerFileUrl1,"Json"))
        }
        ANSWER_FILE2 = dOnlineExamResult.aNSWERFILE2.toString()
        if (ANSWER_FILE2 != "null" && ANSWER_FILE2 != "") {
            //  fileNameList.add(FileName(qUESTIONID, ANSWER_FILE2,answerFileUrl2,"Json"))
            tempFileNameList.add(FileName(dOnlineExamResult.qUESTIONID, ANSWER_FILE2,answerFileUrl2,"Json"))
        }
        ANSWER_FILE3 = dOnlineExamResult.aNSWERFILE3.toString()
        if (ANSWER_FILE3 != "null" && ANSWER_FILE3 != "") {
            //   fileNameList.add(FileName(qUESTIONID, ANSWER_FILE3,answerFileUrl3,"Json"))
            tempFileNameList.add(FileName(dOnlineExamResult.qUESTIONID, ANSWER_FILE3,answerFileUrl3,"Json"))
        }

        fileAdapter = FileAdapter(
            this@DExamPreviewActivity,tempFileNameList,this@DExamPreviewActivity,Aimage_path)

        recyclerViewFiles?.adapter = fileAdapter
        Log.i(TAG,"tempFileNameList $tempFileNameList")

    }


/////////////////////////////////////////////////////file list
    class FileAdapter(var numberDClickListener: NumberDPClickListener,
                      var fileNameList: ArrayList<FileName>,
                      var context: Context,
                      var Aimage_path : String)
        : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

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

//////////////////////////////////////////////////////////////////////////////////////////////////////////////


    override fun onBackPressed() {
        super.onBackPressed()
    //    Global.dOnlineExamResult = ArrayList<OnlineDExamResultModel.DOnlineExamResult>()
    }


/////////////////////////////////////////////////////


    override fun onNumberClick(
        position: Int,
        dOnlineExamResult: OnlineDExamResultModel.DOnlineExamResult,
        type: Int,
        lastValue: String
    ) {
        drawerLayout?.closeDrawer(GravityCompat.END)
        itemPosition = position
        Log.i(TAG, "id $position")

        textViewQuestionNo?.text = "Question ${position + 1}"
        textViewQuestion?.text = dOnlineExamResult.qUESTIONTITLE

        questionTypeFun(dOnlineExamResult)

        //
        if(!dOnlineExamResult.aNSWERKEY!!.isNullOrEmpty() &&
            dOnlineExamResult.aNSWERKEY != "There Is No Teacher Comment"){
            var s = "<font><b>Teacher Comments :  </b></font>"
            //String s = "<font><b>Teacher Comments : </b></font>";
            textViewAnswerKey?.text = Html.fromHtml(s + dOnlineExamResult.aNSWERKEY)
        } else {
            textViewAnswerKey!!.text =  Html.fromHtml(dOnlineExamResult.aNSWERKEY)
        }

        if(!dOnlineExamResult.aNSWERKEYFILETYPE.toString().isNullOrEmpty()
            && dOnlineExamResult.aNSWERKEYFILETYPE != 0){
            answerTypeFun(dOnlineExamResult)
        }else{
            imageViewAnswer?.visibility = View.GONE
            cardViewAudioAnswer?.visibility = View.GONE
            imageViewVideoAnswer?.visibility = View.GONE
        }

        getTakeAnswer(dOnlineExamResult)

        Log.i(TAG, "before load")
        getQuestionPaletteLoad(type,position)
    }



    /////
    private fun getQuestionPaletteLoad(type: Int,position : Int) {


        questionListAdapter =
            QuestionListAdapter(
                this,
                Global.dOnlineExamResult,
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

    private fun questionTypeFun(questionArrayList: OnlineDExamResultModel.DOnlineExamResult) {

        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.reset()
        }
        //  else { mediaPlayer?.reset() }
        Log.i(TAG,"file ${Qimage_path + questionArrayList.qUESTIONCONTENT}")


        imageViewQuestion?.setOnClickListener {
            Global.albumImageList = java.util.ArrayList<CustomImageModel>()
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
                var isValid = URLUtil.isValidUrl(Qimage_path + questionArrayList.qUESTIONCONTENT)
                Log.i(TAG,"isValid $isValid")

                //MediaPlayer.create(context, R.raw.sample_media)
                mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                try {
                    mediaPlayer!!.setDataSource(Qimage_path + questionArrayList.qUESTIONCONTENT)
                    mediaPlayer!!.prepare() // might take long for buffering.
                } catch (e: IOException) {
                    Log.i(TAG,"IOException $e")
                    e.printStackTrace()
                } catch (ex: IllegalStateException) {
                    Log.i(TAG,"IllegalStateException $ex")
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
                    val intent = Intent(this@DExamPreviewActivity, ExoPlayerActivity::class.java)
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


    private fun answerTypeFun(questionArrayList: OnlineDExamResultModel.DOnlineExamResult) {

        if (mediaPlayerAnswer != null && mediaPlayerAnswer!!.isPlaying) {
            mediaPlayerAnswer!!.reset()
        }
        //  else { mediaPlayer?.reset() }

        imageViewAnswer?.setOnClickListener {
            Global.albumImageList = java.util.ArrayList<CustomImageModel>()
            Global.albumImageList.add(
                CustomImageModel(answerKeyPath + questionArrayList.aNSWERKEYFILE)
            )
            val extra = Bundle()
            extra.putInt("position", 0)
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            val newFragment = SlideshowDialogFragment.newInstance()
            newFragment.arguments = extra
            newFragment.show(ft, newFragment.TAG)
        }

        when (questionArrayList.aNSWERKEYFILETYPE) {
            1 -> {
                imageViewAnswer?.visibility = View.VISIBLE
                cardViewAudioAnswer?.visibility = View.GONE
                imageViewVideoAnswer?.visibility = View.GONE
                //  imageView.setImageResource(R.drawable.bird2);
                Glide.with(this)
                    .load(answerKeyPath + questionArrayList.aNSWERKEYFILE)
                    .apply(
                        RequestOptions.fitCenterTransform()
                            .dontAnimate()
                            .placeholder(R.drawable.image_icon)
                    )
                    .thumbnail(0.5f)
                    .into(imageViewAnswer!!)
            }
            2 -> {
                imageViewAnswer?.visibility = View.GONE
                cardViewAudioAnswer?.visibility = View.VISIBLE
                imageViewVideoAnswer?.visibility = View.GONE

                //MediaPlayer.create(context, R.raw.sample_media)
                mediaPlayerAnswer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                try {
                    mediaPlayerAnswer!!.setDataSource(answerKeyPath + questionArrayList.aNSWERKEYFILE)
                    mediaPlayerAnswer!!.prepare() // might take long for buffering.
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (ex: IllegalStateException) {
                    ex.printStackTrace()
                }

                mSeekBarAns!!.max = mediaPlayerAnswer!!.duration
                //   mSeekBar.setTag(position);
                //   mSeekBar.setTag(position);
                songDurationAns?.text =
                    "0 : 0" + " | " + calculateDuration(mediaPlayerAnswer!!.duration)
                mSeekBarAns!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        if (mediaPlayerAnswer != null && fromUser) {
                            mediaPlayerAnswer!!.seekTo(progress)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar) {}
                })


                playPauseRelativeAns?.setOnClickListener(View.OnClickListener {
                    if (!mediaPlayerAnswer!!.isPlaying) {
                        mediaPlayerAnswer!!.start()
                        playPauseImageViewAns?.setImageResource(R.drawable.ic_exam_pause)
                        runAns = Runnable {
                            // Updateing SeekBar every 100 miliseconds
                            mSeekBarAns!!.progress = mediaPlayerAnswer!!.currentPosition
                            seekHandlerAnswer.postDelayed(runAns!!, 100)
                            //For Showing time of audio(inside runnable)
                            val miliSeconds = mediaPlayerAnswer!!.currentPosition
                            if (miliSeconds != 0) {
                                //if audio is playing, showing current time;
                                val minutes = TimeUnit.MILLISECONDS.toMinutes(miliSeconds.toLong())
                                val seconds = TimeUnit.MILLISECONDS.toSeconds(miliSeconds.toLong())
                                if (minutes == 0L) {
                                    songDurationAns?.text =
                                        "0 : $seconds | " + calculateDuration(
                                            mediaPlayer!!.duration
                                        )
                                } else {
                                    if (seconds >= 60) {
                                        val sec = seconds - minutes * 60
                                        songDurationAns?.text =
                                            "$minutes : $sec | " + calculateDuration(
                                                mediaPlayerAnswer!!.duration
                                            )
                                    }
                                }
                            } else {
                                //Displaying total time if audio not playing
                                val totalTime = mediaPlayerAnswer!!.duration
                                val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime.toLong())
                                val seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime.toLong())
                                if (minutes == 0L) {
                                    songDurationAns?.text = "0 : $seconds"
                                } else {
                                    if (seconds >= 60) {
                                        val sec = seconds - minutes * 60
                                        songDurationAns?.text = "$minutes : $sec"
                                    }
                                }
                            }
                        }
                        runAns!!.run()
                    } else {
                        mediaPlayerAnswer!!.pause()
                        playPauseImageViewAns?.setImageResource(R.drawable.ic_exam_play)
                    }
                })

                mediaPlayerAnswer!!.setOnCompletionListener {
                    playPauseImageViewAns?.setImageResource(R.drawable.ic_exam_play)
                    mediaPlayerAnswer!!.pause()
                }

            }
            3 -> {
                imageViewAnswer?.visibility = View.GONE
                cardViewAudioAnswer?.visibility = View.GONE
                imageViewVideoAnswer?.visibility = View.VISIBLE
                imageViewVideoAnswer?.setOnClickListener {
                    val intent = Intent(this@DExamPreviewActivity, ExoPlayerActivity::class.java)
                    intent.putExtra("ALBUM_TITLE", "")
                    intent.putExtra("ALBUM_FILE", answerKeyPath + questionArrayList.aNSWERKEYFILE)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent)
                }

            }
            else -> {
                imageViewAnswer?.visibility = View.GONE
                cardViewAudioAnswer?.visibility = View.GONE
                imageViewVideoAnswer?.visibility = View.GONE
            }
        }

    }

    class NumberAdapter(
        var numberClickListener: NumberDPClickListener,
        var mList: ArrayList<OnlineDExamResultModel.DOnlineExamResult>,
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
        var numberClickListener: NumberDPClickListener,
        var mList: List<OnlineDExamResultModel.DOnlineExamResult>,
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

}

interface NumberDPClickListener {
    fun onNumberClick(
        position: Int,
        dOnlineExamResult: OnlineDExamResultModel.DOnlineExamResult,
        type: Int,
        lastValue: String
    )

    fun onViewImagePreview()
}





