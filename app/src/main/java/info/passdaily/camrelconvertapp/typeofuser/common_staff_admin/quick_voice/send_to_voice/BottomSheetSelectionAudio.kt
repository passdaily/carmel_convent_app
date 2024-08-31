package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_voice.send_to_voice

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetSelectionAudioBinding
import info.passdaily.camrelconvertapp.model.VoiceMessageListModel
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.calculateDuration
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_voice.QuickVoiceMessageViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_voice.VoiceMessageTabClicker
import java.io.IOException
import java.util.concurrent.TimeUnit

class BottomSheetSelectionAudio : BottomSheetDialogFragment {

    var TAG = "BottomSheetSelectionAudio"

    private var _binding: BottomSheetSelectionAudioBinding? = null
    private val binding get() = _binding!!


    var mediaPlayer: MediaPlayer? = null
    var mSeekBar: SeekBar? = null
    var seekHandler = Handler()
    var run: Runnable? = null
    var playPauseImage: ImageView? = null
    var songDuration: TextView? = null
    lateinit var  audioUpdateListener: AudioUpdateListener
    var audioFile  =""
    constructor()

    constructor(audioUpdateListener: AudioUpdateListener,audioFile : String?){
        this.audioUpdateListener = audioUpdateListener

        this.audioFile = audioFile!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)


        _binding = BottomSheetSelectionAudioBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        var textViewTitle = binding.textViewTitle
        var imageViewClose = binding.imageViewClose

        var editTextTitle = binding.editTextTitle

        playPauseImage = binding.imageViewPlay
        mSeekBar = binding.mSeekBar
        songDuration = binding.textSongDuration
        mediaPlayer = MediaPlayer()

        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer!!.setDataSource(audioFile)
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


        playPauseImage?.setOnClickListener(View.OnClickListener {
            if (!mediaPlayer!!.isPlaying) {
                mediaPlayer!!.start()
                playPauseImage?.setImageResource(R.drawable.ic_exam_pause)
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
                playPauseImage?.setImageResource(R.drawable.ic_exam_play)
            }
        })

        mediaPlayer!!.setOnCompletionListener {
            playPauseImage?.setImageResource(R.drawable.ic_exam_play)
            mediaPlayer!!.pause()
        }


        textViewTitle.text = "Audio Path"/// /n${audioFile}

        imageViewClose.setOnClickListener {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                mediaPlayer!!.reset()
            }
            audioUpdateListener.onCloseListener()
        }

        var fab = binding.fab
        fab.setOnClickListener {
            if(editTextTitle.text.toString().isNotEmpty()){
                ///    uploadFile(audioClass.audioPath,editTextTitle.text.toString())
                audioUpdateListener.onFileUpload(audioFile,editTextTitle.text.toString())
            }else{
                Toast.makeText(requireActivity(),"Give Voice Title Before Upload", Toast.LENGTH_SHORT).show()
            }

        }


    }

    companion object {
        var TAG = "BottomSheetFragment"
    }
}