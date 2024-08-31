package info.passdaily.camrelconvertapp.typeofuser.parent.objective_exam

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityOexamPreviewBinding
import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
import info.passdaily.camrelconvertapp.lib.video.Video_Activity
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image.SlideshowDialogFragment
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION", "SetTextI18n")
@SuppressLint("NotifyDataSetChanged")
class OExamPreViewActivity : AppCompatActivity(),NumberClickListener {
    var TAG = "OExamPreViewActivity"

    var image_path = Global.event_url + "/OExamFile/"

    var answerKeyPath = Global.event_url + "/OExamFile/AnswerKeyFile/"

    private lateinit var binding: ActivityOexamPreviewBinding

    private lateinit var oExamAreaViewModel: OExamAreaViewModel

    var toolbar: Toolbar? = null
    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var onlyForZeroValue = 0

    //http://demo.passdaily.in/OExamFile/AnswerKeyFile/7ea748ce-e742-4149-98d9-a2e5fd5a9a86.jpg
    var OEXAM_ID = 0
    var OEXAM_ATTEMPT_ID = 0
    var STUDENT_NAME = ""
    var CLASS_NAME = ""

    var drawerToggle: ActionBarDrawerToggle? = null
    var drawerLayout: DrawerLayout? = null
    var recyclerViewNumberList: RecyclerView? = null

    var constraintFirstLayout: ConstraintLayout? = null

    var imageViewCloseNavBar: ImageView? = null

    var imageLeftArrow: ConstraintLayout? = null
    var imageRightArrow: ConstraintLayout? = null

    var textViewQuestion: TextView? = null
    var textViewQuestionNo: TextView? = null

    var itemPosition = 0

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

    var questionList = ArrayList<QuestionListModel.Question>()

    var recyclerRadioGroup: RecyclerView? = null

    var textStudentName: TextView? = null
    var textClassName: TextView? = null

    var optionArrayList = ArrayList<OptionNew>()

    var responseOption = ArrayList<ObjectiveOptionList.Option>()

    var responseAttemptedOption = ArrayList<ObjectiveOptionList.AttemptedOption>()

    var aNSWEREDOPTIONID: Int = 0
    var cORRECTOPTIONID : Int = 0

    var recyclerViewQuestionList: RecyclerView? = null

    var textRightWrong : TextView? =null

    lateinit var adapter: OExamAreaActivity.NumberAdapter
    lateinit var questionListAdapter: QuestionListAdapter

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

        oExamAreaViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[OExamAreaViewModel::class.java]

        val extras = intent.extras
        if (extras != null) {
            OEXAM_ID = extras.getInt("OEXAM_ID")
            OEXAM_ATTEMPT_ID = extras.getInt("OEXAM_ATTEMPT_ID")
            STUDENT_NAME = extras.getString("STUDENT_NAME")!!
            CLASS_NAME = extras.getString("CLASS_NAME")!!
        }

        var studentDBHelper = StudentDBHelper(this)
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID

        binding = ActivityOexamPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.appBarExam.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Objective Exam Preview"
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

        recyclerViewNumberList = binding.appBarExam.recyclerViewNumberList
        recyclerViewNumberList?.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )

        imageLeftArrow = binding.appBarExam.imageLeftArrow
        imageRightArrow = binding.appBarExam.imageRightArror


        imageViewCloseNavBar = binding.navListLayout.imageViewCloseNavBar
        imageViewCloseNavBar?.setOnClickListener {
            drawerLayout?.closeDrawer(GravityCompat.END)

        }

        textViewQuestion = binding.appBarExam.textViewQuestion
        textViewQuestionNo = binding.appBarExam.textViewQuestionNo
        mediaPlayer = MediaPlayer()

        imageViewQuestion = binding.appBarExam.imageViewQuestion
        cardViewAudioQuestion = binding.appBarExam.cardViewAudioQuestion
        imageViewVideoQuestion = binding.appBarExam.imageViewVideoQuestion
        playPauseImageView = binding.appBarExam.playPauseImageView
        mSeekBar = binding.appBarExam.mSeekBar
        songDuration = binding.appBarExam.songDuration

        ////relative Layout for click event
        playPauseRelative = binding.appBarExam.playPauseRelative

        mediaPlayer = MediaPlayer()
        recyclerRadioGroup = binding.appBarExam.recyclerRadioGroup
        recyclerRadioGroup?.layoutManager = LinearLayoutManager(this)

        textRightWrong = binding.appBarExam.textRightWrong

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


        textStudentName = binding.appBarExam.textStudentName
        textClassName = binding.appBarExam.textClassName
        textStudentName?.text = STUDENT_NAME
        textClassName?.text = CLASS_NAME

        ///Nav item
        recyclerViewQuestionList = binding.navListLayout.recyclerViewQuestionList
        recyclerViewQuestionList?.layoutManager = LinearLayoutManager(this)

        imageLeftArrow?.setOnClickListener {
            if (itemPosition <= 0) {
                Utils.getSnackBar4K(this,"No Question before this",constraintFirstLayout!!)
            } else {
                ///radioButton selection item id
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
                itemPosition++
                onNumberClick(itemPosition, questionList[itemPosition], 3,"")
            }
        }


        getTotalQuestions(OEXAM_ID,OEXAM_ATTEMPT_ID)

        Utils.setStatusBarColor(this)
    }



    fun getTotalQuestions(OEXAM_ID: Int,OEXAM_ATTEMPT_ID: Int) {
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

                                questionTypeFun(questionList[0])

                                textViewAnswerKey!!.visibility = View.VISIBLE
                                if(!questionList[0].aNSWERKEY!!.isNullOrEmpty() &&
                                    questionList[0].aNSWERKEY != "There Is No Teacher Comment"){
                                    var s = "<font><b>Answer Key :  </b></font>"
                                    //String s = "<font><b>Teacher Comments : </b></font>";
                                    textViewAnswerKey?.text = Html.fromHtml(s + questionList[0].aNSWERKEY)
                                } else {
                                    var s = "<font><b>There Is No Answer Key</b></font>"
                                    textViewAnswerKey!!.text =  Html.fromHtml(s)
                                }

                                if(!questionList[0].aNSWERKEYFILETYPE.toString().isNullOrEmpty()
                                    && questionList[0].aNSWERKEYFILETYPE != 0){
                                    answerTypeFun(questionList[0])
                                }else{
                                    imageViewAnswer?.visibility = View.GONE
                                    cardViewAudioAnswer?.visibility = View.GONE
                                    imageViewVideoAnswer?.visibility = View.GONE
                                }

                                getOptionsList(questionList[0].qUESTIONID, OEXAM_ATTEMPT_ID)
                                adapter =
                                    OExamAreaActivity.NumberAdapter(
                                        this,
                                        questionList,
                                        applicationContext
                                    )
                                recyclerViewNumberList?.adapter = adapter

                                questionListAdapter =
                                    QuestionListAdapter(this, questionList, applicationContext)
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
                    val intent = Intent(this@OExamPreViewActivity, ExoPlayerActivity::class.java)
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


    private fun answerTypeFun(questionArrayList: QuestionListModel.Question) {

        if (mediaPlayerAnswer != null && mediaPlayerAnswer!!.isPlaying) {
            mediaPlayerAnswer!!.reset()
        }
        //  else { mediaPlayer?.reset() }

        imageViewAnswer?.setOnClickListener {
            Global.albumImageList = ArrayList<CustomImageModel>()
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
                                            mediaPlayerAnswer!!.duration
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
                    val intent = Intent(this@OExamPreViewActivity, ExoPlayerActivity::class.java)
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

        if(!questionArrayList.aNSWERKEY.isNullOrEmpty() &&
            questionArrayList.aNSWERKEY != "There Is No Teacher Comment"){
            var s = "<font><b>Answer Key :  </b></font>"
            //String s = "<font><b>Teacher Comments : </b></font>";
            textViewAnswerKey?.text = Html.fromHtml(s + questionArrayList.aNSWERKEY)
        } else {
            var s = "<font><b>There Is No Answer Key</b></font>"
            textViewAnswerKey!!.text =  Html.fromHtml(s)
        }

        if(!questionArrayList.aNSWERKEYFILETYPE.toString().isNullOrEmpty()
            && questionArrayList.aNSWERKEYFILETYPE != 0){
            answerTypeFun(questionArrayList)
        }else{
            imageViewAnswer?.visibility = View.GONE
            cardViewAudioAnswer?.visibility = View.GONE
            imageViewVideoAnswer?.visibility = View.GONE
        }

        getOptionsList(questionArrayList.qUESTIONID, OEXAM_ATTEMPT_ID)
        Log.i(TAG, "before load")


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



    fun getOptionsList(qUESTIONID: Int, oEXAMATTEMPTDID: Int) {


//        for (int i=0; i< radio_group.getChildCount(); i++){
//            radio_group.removeViewAt(i);
//        }
        responseOption = ArrayList<ObjectiveOptionList.Option>()
        optionArrayList = ArrayList<OptionNew>()
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
                            Log.i(TAG,"responseOption $responseOption")
                            Log.i(TAG,"responseAttemptedOption $responseAttemptedOption")

                            if (!responseAttemptedOption.isNullOrEmpty()) {
                                for (ii in responseAttemptedOption.indices) {
                                    aNSWEREDOPTIONID = responseAttemptedOption[ii].aNSWEREDOPTIONID
                                    cORRECTOPTIONID = responseAttemptedOption[ii].cORRECTOPTIONID
//                                    if (responseAttemptedOption[ii].cORRECTOPTIONID != null) {
//                                        cORRECTOPTIONID = 0
//                                    }
                                    Log.i(TAG,"aNSWEREDOPTIONID ${responseAttemptedOption[ii].aNSWEREDOPTIONID}")
                                    Log.i(TAG,"cORRECTOPTIONID ${responseAttemptedOption[ii].cORRECTOPTIONID}")
                                }

                                for (i in responseOption.indices) {
                                    Log.i(TAG,"oPTIONID ${responseOption[i].oPTIONID}")
                                    Log.i(TAG,"aNSWEREDOPTIONID $aNSWEREDOPTIONID")
                                    Log.i(TAG,"cORRECTOPTIONID $cORRECTOPTIONID")
                                    val oPTIONID = responseOption[i].oPTIONID
                                    val oPTIONTEXT = responseOption[i].oPTIONTEXT
                                    val iSRIGHTOPTION =  responseOption[i].iSRIGHTOPTION

                                    if (iSRIGHTOPTION == 1) {
                                        optionArrayList.add(OptionNew(oPTIONTEXT,1, oPTIONID))

                                    }else if (iSRIGHTOPTION == 0 && oPTIONID == aNSWEREDOPTIONID) {
                                        optionArrayList.add(OptionNew(oPTIONTEXT,2, oPTIONID))

                                    }else if (iSRIGHTOPTION == 0 && oPTIONID != aNSWEREDOPTIONID) {
                                        optionArrayList.add(OptionNew(oPTIONTEXT,0, oPTIONID))

                                    }

                                    if(oPTIONID == aNSWEREDOPTIONID && oPTIONID == cORRECTOPTIONID){
                                        textRightWrong?.text = "Correct"
                                        textRightWrong?.setTextColor(resources.getColor(R.color.fresh_green_200))
                                    }else if(oPTIONID == aNSWEREDOPTIONID){
                                        textRightWrong?.text = "Wrong"
                                        textRightWrong?.setTextColor(resources.getColor(R.color.fresh_red_200))
                                    }


                                }
                            } else {

                                for (i in responseOption.indices) {
                                    optionArrayList.add(OptionNew(responseOption[i].oPTIONTEXT,responseOption[i].iSRIGHTOPTION,
                                        responseOption[i].oPTIONID))
                               }

                                textRightWrong?.text = "Not Answered"
                                textRightWrong?.setTextColor(resources.getColor(R.color.color_chemistry))

                            }
                            Log.i(TAG,"optionArrayList $optionArrayList")
                            recyclerRadioGroup?.adapter = RadioAdapter(optionArrayList,this@OExamPreViewActivity)
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

    class RadioAdapter(var responseOption: ArrayList<OptionNew>,var context : Context)
        : RecyclerView.Adapter<RadioAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textOption: TextView = view.findViewById(R.id.textOption)
            var imageView: ImageView = view.findViewById(R.id.imageView46)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.radio_item, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
           holder.textOption.text = responseOption[position].oPTIONTEXT
            when (responseOption[position].iSRIGHTOPTION) {
                1 -> {
                    Glide.with(context)
                        .load(context.resources.getDrawable(R.drawable.ic_radio_item_correct))
                        .thumbnail(0.5f)
                        .into(holder.imageView)
                }
                2 -> {
                    Glide.with(context)
                        .load(context.resources.getDrawable(R.drawable.ic_radio_item_wrong))
                        .thumbnail(0.5f)
                        .into(holder.imageView)

                }
                0 -> {
                    Glide.with(context)
                        .load(context.resources.getDrawable(R.drawable.ic_radio_item_empty))
                        .thumbnail(0.5f)
                        .into(holder.imageView)
                }
            }

        }

        override fun getItemCount(): Int {
            return responseOption.size
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


            holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.blue_100))

            holder.textViewNumber.text = (position + 1).toString()
            holder.textViewQuestion.text = mList[position].qUESTIONTITLE

            holder.constraint.setOnClickListener {
                numberClickListener.onNumberClick(position, mList[position], 3,"")
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

//    private fun getSnackBar(message: String) {
//        val snackbar = Snackbar
//            .make(constraintFirstLayout!!, message, Snackbar.LENGTH_SHORT)
//        val view: View = snackbar.getView()
//        val params = view.layoutParams as FrameLayout.LayoutParams
//        params.gravity = Gravity.TOP
//        view.layoutParams = params
//        snackbar.view.setPadding(0, 90, 0, 0)
//        snackbar.show()
//    }



}
class OptionNew(val oPTIONTEXT: String,val iSRIGHTOPTION: Int, val oPTIONID: Int)