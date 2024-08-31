//package info.passdaily.camrelconvertapp.typeofuser.parent.online_video
//
//import android.os.Bundle
//import android.os.Handler
//import android.util.Log
//import android.view.View
//import android.view.WindowManager
//import android.widget.ImageButton
//import android.widget.SeekBar
//import android.widget.SeekBar.OnSeekBarChangeListener
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.constraintlayout.widget.ConstraintLayout
//import com.google.android.youtube.player.YouTubeInitializationResult
//import com.google.android.youtube.player.YouTubePlayer
//import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener
//import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener
//import com.google.android.youtube.player.YouTubePlayerFragment
//import info.passdaily.camrelconvertapp.R
//import info.passdaily.camrelconvertapp.databinding.ActivityYoutubePlayerBinding
//
//
//@Suppress("DEPRECATION")
//class YouTubePlayerActivity : AppCompatActivity(), YouTubePlayer.OnInitializedListener ,View.OnClickListener {
//
//    var TAG = "YouTubePlayerActivity"
//    private var playerFragment: YouTubePlayerFragment? = null
//
//    private lateinit var binding: ActivityYoutubePlayerBinding
//
//    private var YouTubeKey = ""  //AIzaSyDZgZNuFiaqcTWwDz1Fw_0OoIJEIjMLXro
//
//    private var mPlayer: YouTubePlayer? = null
//
//    var lengthPlayed: Long = 0
//
//    var videoControl : ConstraintLayout? = null
//    var playVideo : ImageButton? = null
//
//    var mPlayTimeTextView : TextView? =null
//
//    var videoSeekbar : SeekBar? = null
//
//    private var mHandler: Handler? = null
//
//    private var youTubeLink = ""
//    private var yOUTUBEID = 0
//
//    var play_pause = 0
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        val extras = intent.extras
//        if (extras != null) {
//            youTubeLink = extras.getString("youTubeLink")!!
//            yOUTUBEID = extras.getInt("YOUTUBE_ID")
//            //  intent.putExtra("OEXAM_DURATION",OEXAM_DURATION)
//        }
//
//        binding = ActivityYoutubePlayerBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        YouTubeKey = resources.getString(R.string.google_maps_key)
//        playerFragment = fragmentManager.findFragmentById(R.id.youtube_player_fragment) as YouTubePlayerFragment
//
//        playerFragment?.initialize(YouTubeKey, this)
//
//        videoControl = binding.videoControl
//
//        playVideo = binding.playVideo
//        playVideo?.setOnClickListener(this)
//        // findViewById(R.id.pause_video).setOnClickListener(this);
//
//
//        // findViewById(R.id.pause_video).setOnClickListener(this);
//        mPlayTimeTextView = binding.playTime
//        videoSeekbar = binding.videoSeekbar
//        videoSeekbar?.setOnSeekBarChangeListener(mVideoSeekBarChangeListener)
//        mHandler = Handler()
//
//
//    }
//
//
//    override fun onInitializationSuccess(
//        provider: YouTubePlayer.Provider?,
//        youTubePlayer: YouTubePlayer?,
//        wasRestored: Boolean
//    ) {
//        if (null == youTubePlayer) return
//        mPlayer = youTubePlayer
//        displayCurrentTime()
//        if (!wasRestored) {
//            mPlayer?.loadVideo(youTubeLink)
//        } else {
//            mPlayer?.play()
//        }
//
//
//        mPlayer?.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS)
//
//        videoControl?.visibility = View.VISIBLE
//        mPlayer?.setShowFullscreenButton(true)
//
//        mPlayer?.setPlayerStateChangeListener(playerStateChangeListener)
//
//        mPlayer?.setPlaybackEventListener(playbackEventListener)
//    }
//
//    override fun onInitializationFailure(
//        provider: YouTubePlayer.Provider?,
//        youTubeInitResult: YouTubeInitializationResult?
//    ) {
//        Log.i(TAG, "YouTubeInitializationResult Failed")
//    }
//
//
//
//    private val playerStateChangeListener: PlayerStateChangeListener =
//        object : PlayerStateChangeListener {
//            override fun onLoading() {
//                Log.i(TAG, "onLoading")
//            }
//
//            override fun onLoaded(s: String) {
//                Log.i(TAG, "onLoaded")
//            }
//
//            override fun onAdStarted() {
//                Log.i(TAG, "onAdStarted")
//            }
//
//            override fun onVideoStarted() {
//                displayCurrentTime()
//                // mSeekBar.setMax(mPlayer.getDurationMillis()/1000);
//                Log.i(TAG, "onVideoStarted")
//            }
//
//            override fun onVideoEnded() {
//                Log.i(TAG, "onVideoEnded")
//            }
//
//            override fun onError(errorReason: YouTubePlayer.ErrorReason) {
//                Log.i(TAG, "onError $errorReason")
//                play_pause = 1
//                playVideo?.setImageResource(R.drawable.ic_exam_play)
//           //     playVideo?.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@YouTubePlayerActivity,R.color.white));
//            }
//        }
//
//    private val playbackEventListener: PlaybackEventListener = object : PlaybackEventListener {
//        override fun onBuffering(arg0: Boolean) {
//            Log.i(TAG, "onBuffering")
//        }
//
//        override fun onPaused() {
//            mHandler!!.removeCallbacks(runnable)
//            Log.i(TAG, "paused")
//            //            play_pause = 1;
////            play_video.setImageResource(R.drawable.ic_play_arrow_blue_24dp);
//        }
//
//        override fun onPlaying() {
//            mHandler!!.postDelayed(runnable, 100)
//            displayCurrentTime()
//            Log.i(TAG, "onPlaying")
//           // PlayLog_method()
//        }
//
//        override fun onSeekTo(arg0: Int) {
//            mHandler!!.postDelayed(runnable, 100)
//            Log.i(TAG, "onSeekTo")
//        }
//
//        override fun onStopped() {
//            mHandler!!.removeCallbacks(runnable)
//            Log.i(TAG, "onStopped")
//        }
//    }
//
//
//    private var mVideoSeekBarChangeListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
//        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//            // //Only when the user change the progress
//            if (fromUser) {
//                lengthPlayed = (mPlayer?.durationMillis!! * progress / 100).toLong()
//                mPlayer?.seekToMillis(lengthPlayed.toInt())
//            }
//
//            //   seekBar.setProgress(progress);
//        }
//
//        override fun onStartTrackingTouch(seekBar: SeekBar) {}
//        override fun onStopTrackingTouch(seekBar: SeekBar) {}
//    }
//
//    override fun onClick(v: View?) {
//        if (play_pause == 0) {
//            if (mPlayer != null && mPlayer?.isPlaying!!) {
//                mPlayer?.pause()
//                play_pause = 1
//                playVideo?.setImageResource(R.drawable.ic_exam_play)
//           //    playVideo?.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white));
//            }
//        } else {
//            if (mPlayer != null && !mPlayer?.isPlaying!!) {
//                mPlayer?.play()
//                play_pause = 0
//                playVideo?.setImageResource(R.drawable.ic_exam_pause)
//           //     playVideo?.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.white));
//            }
//        }
//    }
//
//
//    private fun displayCurrentTime() {
//        if (null == mPlayer) return
//        val formattedTime = formatTime(mPlayer!!.durationMillis - mPlayer!!.currentTimeMillis)
//        mPlayTimeTextView!!.text = formattedTime
//    }
//
//    private fun formatTime(millis: Int): String {
//        val seconds = millis / 1000
//        val minutes = seconds / 60
//        val hours = minutes / 60
//        return (if (hours == 0) "" else "$hours : ") + String.format(
//            "%02d:%02d",
//            minutes % 60,
//            seconds % 60
//        )
//    }
//
//
//    private val runnable: Runnable = object : Runnable {
//        override fun run() {
//            displayCurrentTime()
//            val progress = mPlayer!!.currentTimeMillis * 100 / mPlayer!!.durationMillis
//            videoSeekbar?.progress = progress
//            mHandler!!.postDelayed(this, 100)
//        }
//    }
//
//    override fun onDestroy() {
//        if (mPlayer != null) {
//            mPlayer!!.release()
//        }
//        super.onDestroy()
//    }
//
//}