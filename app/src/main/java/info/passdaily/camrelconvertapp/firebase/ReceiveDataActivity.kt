package info.passdaily.camrelconvertapp.firebase

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityReceiveDataBinding
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.calculateDuration
import java.io.IOException
import java.util.concurrent.TimeUnit


class ReceiveDataActivity : AppCompatActivity() {

    var mediaPlayer: MediaPlayer? = null
    private lateinit var binding: ActivityReceiveDataBinding
    var mSeekBar: SeekBar? = null
    var seekHandler = Handler()
    var run: Runnable? = null

    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiveDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Audio Notification"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }

       // var playPauseRelative = binding.playPauseRelative
        var songDurationTimer = binding.songDurationTimer

        val intent = intent
        val message = intent.getStringExtra("message")

        var playPauseImageView = binding.playPauseImageView
        mSeekBar = binding.mSeekBar
        var songDuration = binding.songDuration

        mediaPlayer = MediaPlayer()

        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer!!.setDataSource(message)
            mediaPlayer!!.prepare() // might take long for buffering.
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (ex: IllegalStateException) {
            ex.printStackTrace()
        }

        mSeekBar!!.max = mediaPlayer!!.duration
        //   mSeekBar.setTag(position);
        //   mSeekBar.setTag(position);
        songDurationTimer.text = "00 : 00"
        songDuration.text = calculateDuration(mediaPlayer!!.duration)
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


        playPauseImageView.setOnClickListener(View.OnClickListener {
            if (!mediaPlayer!!.isPlaying) {
                mediaPlayer!!.start()
                playPauseImageView.setImageResource(R.drawable.ic_exam_pause)
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
                            songDurationTimer.text = "00 : $seconds"
                        } else {
                            if (seconds >= 60) {
                                val sec = seconds - minutes * 60
                                songDurationTimer.text = "$minutes : $sec"
                            }
                        }
                    } else {
                        //Displaying total time if audio not playing
                        val totalTime = mediaPlayer!!.duration
                        val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime.toLong())
                        val seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime.toLong())
                        if (minutes == 0L) {
                            songDurationTimer.text = "00 : $seconds"
                        } else {
                            if (seconds >= 60) {
                                val sec = seconds - minutes * 60
                                songDurationTimer.text = "$minutes : $sec"
                            }
                        }
                    }
                }
                run!!.run()
            } else {
                mediaPlayer!!.pause()
                playPauseImageView.setImageResource(R.drawable.ic_exam_play)
            }
        })

        mediaPlayer!!.setOnCompletionListener {
            playPauseImageView.setImageResource(R.drawable.ic_exam_play)
            mediaPlayer!!.pause()
        }


        Utils.setStatusBarColor(this)
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.reset()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.reset()
        }
    }
}