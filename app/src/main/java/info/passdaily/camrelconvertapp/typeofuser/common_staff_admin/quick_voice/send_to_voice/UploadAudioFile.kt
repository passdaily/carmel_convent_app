package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_voice.send_to_voice

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder
import cafe.adriel.androidaudiorecorder.model.AudioChannel
//import cafe.adriel.androidaudiorecorder.AudioRefresh
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate
import cafe.adriel.androidaudiorecorder.model.AudioSource
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.retrofit.RetrofitClientStaff
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.descriptive_exam.desc_exam_question.UpdateDescriptiveQuestion
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.UpdateAssignmentDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_voice.BottomSheetVoiceMessageDetailsAdmin
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_voice.QuickVoiceMessageViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.FileList
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
@SuppressLint("Registered")
class UploadAudioFile : AppCompatActivity(),AudioUpdateListener /*, AudioRefresh*/ {
    var TAG= "UploadAudioFile"
    var toolbar: Toolbar? = null
    //    var viewPager: ViewPager? = null
//    var tabLayout: TabLayout? = null
    var constraintLayout: ConstraintLayout? = null
    var SERVER_URL = "VoiceUpload/UploadFiles"
    var recyclerViewAudio: RecyclerView? = null
    var currentPage = 0
    private lateinit var quickVoiceMessageViewModel: QuickVoiceMessageViewModel

    lateinit var bottomSheetSelectionAudio : BottomSheetSelectionAudio

    private val REQUEST_RECORD_AUDIO = 0
    private val AUDIO_FILE_PATH = ""
    private var saveMenuItem: MenuItem? = null

    lateinit var audioUpdateListener: AudioUpdateListener

    var pb: ProgressDialog? = null

    var mediaPlayer: MediaPlayer? = null
    var mSeekBar: SeekBar? = null
    var seekHandler = Handler()
    var run: Runnable? = null
    var playPauseImage: ImageView? = null
    var songDuration: TextView? = null

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var createAudioFab: FloatingActionButton? = null
    var chooseMusicFab:FloatingActionButton? = null

    var mAddFab: ExtendedFloatingActionButton? = null

    var createAudioFabText: TextView? = null
    var chooseMusicFabText:  TextView? = null

    var isAllFabsVisible: Boolean? = null

    var dialog : Dialog? = null

    private lateinit var localDBHelper: LocalDBHelper
    var adminId = 0
    var adminRole = 0

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private var readPermission = false
    private var writePermission = false

    var fab: FloatingActionButton? = null
    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.activity_audio_recorder)

        quickVoiceMessageViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[QuickVoiceMessageViewModel::class.java]

        localDBHelper = LocalDBHelper(this)
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE

        pb = ProgressDialog(this)
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Loading...")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)

        mAddFab = findViewById(R.id.add_fab)
        createAudioFab = findViewById(R.id.add_alarm_fab)
        chooseMusicFab = findViewById(R.id.add_person_fab)
        createAudioFabText = findViewById(R.id.add_alarm_action_text)
        chooseMusicFabText = findViewById(R.id.add_person_action_text)


        // ActivityCompat.requestPermissions(this, permissions, REQUEST_WRITE_PERMISSION)

        //   audioUpdateListener = FragmentInstance1()

        toolbar = findViewById(R.id.toolbar)
        // setSupportActionBar(toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Audio List"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }
//        viewPager = findViewById(R.id.viewpager)
//        tabLayout = findViewById(R.id.tabs)


        constraintLayout = findViewById(R.id.constraintLayout)

        constraintEmpty = findViewById(R.id.constraintEmpty)
        imageViewEmpty = findViewById(R.id.imageViewEmpty)
        textEmpty = findViewById(R.id.textEmpty)
        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        shimmerViewContainer = findViewById(R.id.shimmerViewContainer)

        recyclerViewAudio = findViewById(R.id.recyclerViewAudio)

        val layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd = true
        recyclerViewAudio?.layoutManager = layoutManager

        //recyclerViewAudio?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, true)
        //   viewTab()

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

                readPermission = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermission


            }


        addList()

        createAudioFab?.visibility = View.GONE
        chooseMusicFab?.visibility = View.GONE
        createAudioFabText?.visibility = View.GONE
        chooseMusicFabText?.visibility = View.GONE

        isAllFabsVisible = false
        mAddFab?.shrink()

        mAddFab?.setOnClickListener{
            isAllFabsVisible = if (!isAllFabsVisible!!) {
                createAudioFab?.show()
                chooseMusicFab?.show()
                createAudioFabText?.visibility = View.VISIBLE
                chooseMusicFabText?.visibility = View.VISIBLE
                mAddFab?.extend()
                true
            } else {
                createAudioFab?.hide()
                chooseMusicFab?.hide()
                createAudioFabText?.visibility = View.GONE
                chooseMusicFabText?.visibility = View.GONE
                mAddFab?.shrink()
                false
            }
        }

        chooseMusicFab?.setOnClickListener{
            if (requestPermission()) {
                val intent =
                    Intent(Intent.ACTION_GET_CONTENT); // or ACTION_OPEN_DOCUMENT //ACTION_GET_CONTENT
                // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "audio/*";
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startForResult.launch(intent)
            }


            createAudioFab?.hide()
            chooseMusicFab?.hide()
            createAudioFabText?.visibility = View.GONE
            chooseMusicFabText?.visibility = View.GONE
            isAllFabsVisible = false
            mAddFab?.shrink()
        }

        createAudioFab?.setOnClickListener {
            if (requestPermission()) {
                AndroidAudioRecorder.with(this) // Required
                    .setFilePath(AUDIO_FILE_PATH)
                    .setColor(ContextCompat.getColor(this, R.color.white))
                    .setRequestCode(REQUEST_RECORD_AUDIO) // Optional
                    .setSource(AudioSource.MIC)
                    .setChannel(AudioChannel.STEREO)
                    .setSampleRate(AudioSampleRate.HZ_48000)
                    .setAutoStart(false)
                    .setKeepDisplayOn(true) // Start recording
                    .record()
            }

            createAudioFab?.hide()
            chooseMusicFab?.hide()
            createAudioFabText?.visibility = View.GONE
            chooseMusicFabText?.visibility = View.GONE
            isAllFabsVisible = false
            mAddFab?.shrink()
        }


//        fab = findViewById(R.id.fab)
//        fab?.visibility = View.VISIBLE
//        fab?.setOnClickListener {
//            if (requestPermission()) {
//                AndroidAudioRecorder.with(this) // Required
//                    .setFilePath(AUDIO_FILE_PATH)
//                    .setColor(ContextCompat.getColor(this, R.color.white))
//                    .setRequestCode(REQUEST_RECORD_AUDIO) // Optional
//                    .setSource(AudioSource.MIC)
//                    .setChannel(AudioChannel.STEREO)
//                    .setSampleRate(AudioSampleRate.HZ_48000)
//                    .setAutoStart(false)
//                    .setKeepDisplayOn(true) // Start recording
//                    .record()
//            }
//        }
        Utils.setStatusBarColor(this)

        bottomSheetSelectionAudio = BottomSheetSelectionAudio()
    }


    ///permission Part
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == RESULT_OK) {
                val data: Intent? = it!!.data
                Log.i(TAG, "data $data")

                //If single image selected
                if (data!!.data != null) {
                    val imageUri: Uri? = data.data
                    Log.i(TAG,"imageUri")
                    val mFile = FileUtils.getReadablePathFromUri(this,imageUri!!)
                    Log.i(TAG,"mFile $mFile")
                    bottomSheetSelectionAudio = BottomSheetSelectionAudio(this,mFile)
                    bottomSheetSelectionAudio.show(supportFragmentManager, "TAG")
                }

            }

        }

    fun addList() {
        val file = File(Utils.getRootDirPath(this)!!)
        if (!file.exists()) {
            file.mkdirs()
        } else {
            Log.i(TAG, "Already existing")
        }
        val directory = File(file.path +"/Passdaily/Audio/")
        shimmerViewContainer?.visibility = View.GONE
        if (directory.exists()) {
            val files: Array<File> = directory.listFiles()!!
            if (files.isNotEmpty()) {
                recyclerViewAudio?.visibility = View.VISIBLE
                constraintEmpty?.visibility = View.GONE
                recyclerViewAudio?.adapter = UploadAudioListAdapter(this, files)

            } else {
                recyclerViewAudio?.visibility = View.GONE
                constraintEmpty?.visibility = View.VISIBLE
                Glide.with(this)
                    .load(R.drawable.ic_empty_progress_report)
                    .into(imageViewEmpty!!)

                textEmpty?.text = resources.getString(R.string.no_results)
            }
        }else{
            recyclerViewAudio?.visibility = View.GONE
            constraintEmpty?.visibility = View.VISIBLE
            Glide.with(this)
                .load(R.drawable.ic_empty_progress_report)
                .into(imageViewEmpty!!)

            textEmpty?.text = resources.getString(R.string.no_results)
        }

    }

    class UploadAudioListAdapter(
        var audioUpdateListener: AudioUpdateListener, var feedlist: Array<File>,
    ) : RecyclerView.Adapter<UploadAudioListAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val v =
                LayoutInflater.from(parent.context).inflate(R.layout.audio_adapter, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.textViewTime.text = feedlist[position].name

            var timeSplit =feedlist[position].name.split(".").toTypedArray()
            var getDate = timeSplit[0].split("_").toTypedArray()
            holder.textViewTitle.text =  Utils.dateformatAudioyyyyMMddHHmmss(getDate[1])

            holder.itemView.setOnClickListener {
                audioUpdateListener.onCompletion(feedlist[position],Utils.dateformatAudioyyyyMMddHHmmss(getDate[1]))
            }
        }

        override fun getItemCount(): Int {
            return feedlist.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textViewTime: TextView
            var textViewTitle: TextView

            init {
                textViewTime = itemView.findViewById<View>(R.id.textViewTime) as TextView
                textViewTitle = itemView.findViewById<View>(R.id.textViewTitle) as TextView
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

    class AudioClass(var audioTitle: String, var dateTime: String, var audioPath: String)


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.main_audio, menu)
        val actionRefresh = menu.findItem(R.id.action_refresh) as MenuItem
        val rotation = AnimationUtils.loadAnimation(applicationContext, R.anim.clockwise_refresh)

        actionRefresh.setActionView(R.layout.refresh_action_half_view).actionView
            ?.setOnClickListener { view -> //  rotation.setRepeatCount(Animation.INFINITE);
                view.startAnimation(rotation)
//                currentPage = viewPager?.currentItem!!
                addList()
                Utils.getSnackBarGreen(this, "New Audio Updated", constraintLayout)
                // audioUpdateListener.onCompletion()
                //  progressStart();
            }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId

        return super.onOptionsItemSelected(item)
    }

    private class UploadAudioAdapter(
        var audioUpdateListener: AudioUpdateListener,
        var feedlist: ArrayList<AudioClass>,
        fragmentInstance1: Activity?
    ) : RecyclerView.Adapter<UploadAudioAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val v =
                LayoutInflater.from(parent.context).inflate(R.layout.audio_adapter, parent, false)
            return ViewHolder(v)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewTitle.text = feedlist[position].audioTitle
            holder.textViewTime.text = feedlist[position].dateTime

            holder.itemView.setOnClickListener {
                //  audioUpdateListener.onCompletion(feedlist[position])
            }
        }

        override fun getItemCount(): Int {
            return feedlist.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textViewTime: TextView
            var textViewTitle: TextView

            init {
                textViewTime = itemView.findViewById<View>(R.id.textViewTime) as TextView
                textViewTitle = itemView.findViewById<View>(R.id.textViewTitle) as TextView
            }
        }
    }

    companion object {
        private const val REQUEST_WRITE_PERMISSION = 786
    }

    override fun onCompletion(audioClass: File, dateTime: String?) {
        dialog = Dialog(this)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.dialog_audio)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog?.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog?.window!!.attributes = lp

        var textViewTitle = dialog?.findViewById<View>(R.id.textViewTitle) as TextView
        var textViewDate = dialog?.findViewById<View>(R.id.textViewDate) as TextView
        var imageViewClose = dialog?.findViewById<ImageView>(R.id.imageViewClose)

        var editTextTitle = dialog?.findViewById<View>(R.id.editTextTitle) as TextInputEditText

        playPauseImage = dialog?.findViewById<View>(R.id.imageViewPlay) as ImageView
        mSeekBar = dialog?.findViewById<View>(R.id.mSeekBar) as SeekBar
        songDuration = dialog?.findViewById<View>(R.id.textSongDuration) as TextView
        mediaPlayer = MediaPlayer()

        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer!!.setDataSource(audioClass.absolutePath)
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


        textViewTitle.text = "Audio Path \n${audioClass.name}"
        textViewDate.text = dateTime

        imageViewClose?.setOnClickListener {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                mediaPlayer!!.reset()
            }
            dialog?.dismiss()
        }

        var fab = dialog?.findViewById<View>(R.id.fab) as ExtendedFloatingActionButton
        fab.setOnClickListener {
            if(editTextTitle.text.toString().isNotEmpty()){
                //  pb?.show()
                onFileUpload(audioClass.absolutePath,editTextTitle.text.toString())
            }else{
                Toast.makeText(this,"Give Voice Title Before Upload",Toast.LENGTH_SHORT).show()
            }

        }


        dialog?.show()
    }

    override fun onCloseListener() {
        bottomSheetSelectionAudio.dismiss()
    }




    override fun onFileUpload(selectedFilePath: String,editTextTitle: String) {

        //  var selectedFilePath = FileUtils.getReadablePathFromUri(requireActivity(), audioPath.toUri())


        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(TAG,"selectedFilePath $selectedFilePath");
        filesToUpload[0] = File(selectedFilePath)
        Log.i(TAG,"filesToUpload $filesToUpload");

        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload,"voice", object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                pb?.dismiss()
                Log.i(TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {
                pb?.dismiss()
//                var imagePath = responses[0]
                Log.i(TAG,"responses ${responses[0]}")
                var fileName = responses[0].split("~").toTypedArray()
                submitFile(fileName[0],fileName[1],editTextTitle)

            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                pb?.show()
                pb?.setMessage("Uploading : $totalpercent / 100")
                //     Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })
    }


    private fun submitFile(vOICEMAILFILE: String, tEMPLATEID: String, editTextTitle : String) {


        val url = "Voice/VoiceAdd"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("VOICE_MAIL_TITLE", editTextTitle)
            jsonObject.put("VOICE_MAIL_FILE", vOICEMAILFILE)
            jsonObject.put("TEMPLATE_ID", tEMPLATEID)
            jsonObject.put("VOICE_MAIL_CREATED_BY",adminId)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG, "jsonObject $jsonObject")
        val accademicRe =
            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        quickVoiceMessageViewModel.getCommonPostFun(url, accademicRe)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            pb?.dismiss()
                            dialog?.dismiss()
                            bottomSheetSelectionAudio.dismiss()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(this,"Voice Message Added",constraintLayout)

                                }
                                Utils.resultFun(response) == "FAILED" -> {
                                    Utils.getSnackBarGreen(this,"Failed While Adding Voice Message",constraintLayout)
                                    //Toast.makeText(this,"Failed While Uploading Audio",Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Utils.getSnackBarGreen(this,"Error while Uploading",constraintLayout)
                                    //   Toast.makeText(this,"Error while Uploading",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(this,"Please try again after sometime",Toast.LENGTH_SHORT).show()
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "loading")
                        }
                    }
                }
            })

    }
}

//    override fun onRefreshListener() {
//        Log.i(TAG, "onRefreshListener")
//    }
//}

interface AudioUpdateListener {
    fun onCompletion(audioClass: File, dateTime: String?)
    fun onCloseListener()
    fun onFileUpload(selectedFilePath: String, editTextTitle: String) {

    }
}




///old code

//    private inner class MyPagerAdapter(fragmentManager: FragmentManager?) : FragmentPagerAdapter(
//        fragmentManager!!
//    ) {
//        private val mFragmentList: MutableList<Fragment> = ArrayList()
//        private val mFragmentTitleList: MutableList<String> = ArrayList()
//        override fun getItem(position: Int): Fragment {
//            return mFragmentList[position]
//        }
//
//        override fun getCount(): Int {
//            return mFragmentTitleList.size
//        }
//
//        fun addFragment(fragment: Fragment, title: String) {
//            mFragmentList.add(fragment)
//            mFragmentTitleList.add(title)
//        }
//
//        override fun getItemPosition(`object`: Any): Int {
//            // POSITION_NONE makes it possible to reload the PagerAdapter
//            return POSITION_NONE
//        }
//
//        override fun getPageTitle(position: Int): CharSequence? {
//            return mFragmentTitleList[position]
//        }
//    }
//
//    @SuppressLint("ValidFragment")
//    class FragmentInstance1() : Fragment(), AudioUpdateListener {
//        var TAG = "FragmentInstance1"
//        val MEDIA_PATH: String = Environment.getExternalStorageDirectory().path + "/"
//       // val MEDIA_PATH = getRootDirPath(requireActivity())+"/Passdaily/Audio/"
//        var mediaPlayer: MediaPlayer? = null
//        var mSeekBar: SeekBar? = null
//        var seekHandler = Handler()
//        var run: Runnable? = null
//        var playPauseImage: ImageView? = null
//        var songDuration: TextView? = null
//        private lateinit var quickVoiceMessageViewModel: QuickVoiceMessageViewModel
//        var SERVER_URL = "VoiceUpload/UploadFiles"
//        var adminId = 0
//        private lateinit var localDBHelper : LocalDBHelper
//
//        private var songsList: ArrayList<AudioClass>? = null
//        private var mp3Pattern: String? = ""
//        var recyclerviewList: RecyclerView? = null
//
//        var pb: ProgressDialog? = null
//
//        var dialog : Dialog? = null
//
//        //  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd, MMM yyyy, hh:mm aa")
//        @SuppressLint("MissingInflatedId")
//        override fun onCreateView(
//            inflater: LayoutInflater,
//            container: ViewGroup?,
//            savedInstanceState: Bundle?
//        ): View? {
//            val view = inflater.inflate(R.layout.fragment_audio_list, container, false)
//
//            pb = ProgressDialog(requireActivity())
//            pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
//            pb?.setMessage("Loading...")
//            pb?.isIndeterminate = true
//            pb?.setCancelable(false)
//
//            localDBHelper = LocalDBHelper(requireActivity())
//            var user = localDBHelper.viewUser()
//            adminId = user[0].ADMIN_ID
//
//            quickVoiceMessageViewModel = ViewModelProviders.of(
//                this,
//                ViewModelFactory(ApiClient(NetworkLayerStaff.services))
//            )[QuickVoiceMessageViewModel::class.java]
//            mp3Pattern = arguments!!.getString("msg")
//            songsList = ArrayList()
//            recyclerviewList = view.findViewById(R.id.recyclerviewList)
//            val linearLayout = LinearLayoutManager(activity)
//            linearLayout.reverseLayout = true
//            linearLayout.stackFromEnd = true
//            recyclerviewList?.layoutManager = linearLayout
//
////            recyclerview_list.addOnItemTouchListener(new RecyclerItemClickListnr(getContext(),
////                    recyclerview_list, new RecyclerItemClickListnr.ClickListener(){
////                @Override
////                public void onClick(View view,final int position){
////                    Intent intent = new Intent(getContext(),Audio_SelectUpload.class);
////                    intent.putExtra("filepath", songsList.get(position).get("filepath"));
////                    startActivity(intent);
////                    requireActivity().finish();
////                    Log.i("TAG","filepath "+ songsList.get(position).get("filepath"));
////                }
////                @Override
////                public void onLongClick(View view,int position){
////
////                }
////            }));
//
//            addList()
//
//            return view
//        }
//
//        fun getRootDirPath(context: Context): String? {
//            return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
//                val file: File =
//                    ContextCompat.getExternalFilesDirs(context.applicationContext, null)[0]
//                file.absolutePath
//            } else {
//                context.applicationContext.filesDir.absolutePath
//            }
//        }
//
//        fun addList() {
//            val songList = playList
//            if (songList != null) {
//
////             songList.sortedByDescending {
////                    LocalDate.parse(it.dateTime, dateTimeFormatter)
////                }
////                Collections.sort(songList,
////                    Comparator<AudioClass> { o1, o2 ->
////                        o1.dateTime.compareTo(o2.dateTime)
////                    })
//                // Collections.sort(datesList);
//                //songList.sortByDescending{it.dateTime}
//                songList.sortBy(dateTimeStrToLocalDateTime)
//                recyclerviewList?.adapter = UploadAudioAdapter(this, songList, activity)
//            } else {
//                Log.i("TAG", "empty ")
//            }
//        }
//
//        // return songs list array
//        private val playList: ArrayList<AudioClass>?
//            get() {
//                println(MEDIA_PATH)
//                val home = File(MEDIA_PATH)
//                val listFiles = home.listFiles()
//                if (listFiles != null && listFiles.isNotEmpty()) {
//                    for (file in listFiles) {
//                        println(file.absolutePath)
//                        if (file.isDirectory) {
//                            scanDirectory(file)
//                        } else {
//                            addSongToList(file)
//                        }
//                    }
//                }
//                // return songs list array
//                return songsList
//            }
//
//        private fun scanDirectory(directory: File?) {
//            if (directory != null) {
//                val listFiles = directory.listFiles()
//                if (listFiles != null && listFiles.isNotEmpty()) {
//                    for (file in listFiles) {
//                        if (file.isDirectory) {
//                            scanDirectory(file)
//                        } else {
//                            addSongToList(file)
//                        }
//                    }
//                }
//            }
//        }
//
//        val dateTimeStrToLocalDateTime: (AudioClass) -> LocalDateTime = {
//            LocalDateTime.parse(it.dateTime, DateTimeFormatter.ofPattern("dd, MMM yyyy, hh:mm a"))
//        }
//
//        private fun addSongToList(song: File) {
//            if (song.name.endsWith(mp3Pattern!!)) {
////                val songMap = HashMap<String, String>()
////                songMap["filename"] = song.name.substring(0, song.name.length - 4)
////                songMap["filepath"] = song.path
////               // songsList?.sortedBy { song.path.toDate() }
////                songsList?.sortedWith(compareBy({ it.customProperty }))
//
//                // Adding each song to SongList
//                songsList!!.add(
//                    AudioClass(
//                        song.name.substring(0, song.name.length - 4),
//                        formattedDateTime1(File(song.path).lastModified()),
//                        song.path
//                    )
//                )
//            }
//        }
//
//
//        //        companion object {
//        fun newInstance(text: String?): FragmentInstance1 {
//            val f = FragmentInstance1()
//            val b = Bundle()
//            b.putString("msg", text)
//            f.arguments = b
//            return f
//        }
//
//        override fun onCompletion(audioClass: File) {
////            Log.i(TAG, "onCompletion ${audioClass.audioTitle}")
////            Log.i(TAG, "onCompletion ${audioClass.dateTime}")
////            Log.i(TAG, "onCompletion ${audioClass.audioPath}")
//            //  addList()
//
//            dialog = Dialog(requireActivity())
//            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog?.setCancelable(false)
//            dialog?.setContentView(R.layout.dialog_audio)
//            val lp = WindowManager.LayoutParams()
//            lp.copyFrom(dialog?.window?.attributes)
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//            lp.gravity = Gravity.CENTER
//            dialog?.window?.attributes = lp
//
//            var textViewTitle = dialog?.findViewById<View>(R.id.textViewTitle) as TextView
//            var textViewDate = dialog?.findViewById<View>(R.id.textViewDate) as TextView
//            var imageViewClose = dialog?.findViewById<ImageView>(R.id.imageViewClose)
//
//            var editTextTitle = dialog?.findViewById<View>(R.id.editTextTitle) as TextInputEditText
//
//            playPauseImage = dialog?.findViewById<View>(R.id.playPauseImage) as ImageView
//            mSeekBar = dialog?.findViewById<View>(R.id.mSeekBar) as SeekBar
//            songDuration = dialog?.findViewById<View>(R.id.songDuration) as TextView
//            mediaPlayer = MediaPlayer()
//
//            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
//            try {
//                mediaPlayer!!.setDataSource(audioClass.audioPath)
//                mediaPlayer!!.prepare() // might take long for buffering.
//            } catch (e: IOException) {
//                e.printStackTrace()
//            } catch (ex: IllegalStateException) {
//                ex.printStackTrace()
//            }
//
//            mSeekBar!!.max = mediaPlayer!!.duration
//            //   mSeekBar.setTag(position);
//            //   mSeekBar.setTag(position);
//            songDuration?.text =
//                "0 : 0" + " | " + calculateDuration(mediaPlayer!!.duration)
//            mSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//                override fun onProgressChanged(
//                    seekBar: SeekBar,
//                    progress: Int,
//                    fromUser: Boolean
//                ) {
//                    if (mediaPlayer != null && fromUser) {
//                        mediaPlayer!!.seekTo(progress)
//                    }
//                }
//
//                override fun onStartTrackingTouch(seekBar: SeekBar) {}
//                override fun onStopTrackingTouch(seekBar: SeekBar) {}
//            })
//
//
//            playPauseImage?.setOnClickListener(View.OnClickListener {
//                if (!mediaPlayer!!.isPlaying) {
//                    mediaPlayer!!.start()
//                    playPauseImage?.setImageResource(R.drawable.ic_exam_pause)
//                    run = Runnable {
//                        // Updateing SeekBar every 100 miliseconds
//                        mSeekBar!!.progress = mediaPlayer!!.currentPosition
//                        seekHandler.postDelayed(run!!, 100)
//                        //For Showing time of audio(inside runnable)
//                        val miliSeconds = mediaPlayer!!.currentPosition
//                        if (miliSeconds != 0) {
//                            //if audio is playing, showing current time;
//                            val minutes = TimeUnit.MILLISECONDS.toMinutes(miliSeconds.toLong())
//                            val seconds = TimeUnit.MILLISECONDS.toSeconds(miliSeconds.toLong())
//                            if (minutes == 0L) {
//                                songDuration?.text =
//                                    "0 : $seconds | " + calculateDuration(
//                                        mediaPlayer!!.duration
//                                    )
//                            } else {
//                                if (seconds >= 60) {
//                                    val sec = seconds - minutes * 60
//                                    songDuration?.text =
//                                        "$minutes : $sec | " + calculateDuration(
//                                            mediaPlayer!!.duration
//                                        )
//                                }
//                            }
//                        } else {
//                            //Displaying total time if audio not playing
//                            val totalTime = mediaPlayer!!.duration
//                            val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime.toLong())
//                            val seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime.toLong())
//                            if (minutes == 0L) {
//                                songDuration?.text = "0 : $seconds"
//                            } else {
//                                if (seconds >= 60) {
//                                    val sec = seconds - minutes * 60
//                                    songDuration?.text = "$minutes : $sec"
//                                }
//                            }
//                        }
//                    }
//                    run!!.run()
//                } else {
//                    mediaPlayer!!.pause()
//                    playPauseImage?.setImageResource(R.drawable.ic_exam_play)
//                }
//            })
//
//            mediaPlayer!!.setOnCompletionListener {
//                playPauseImage?.setImageResource(R.drawable.ic_exam_play)
//                mediaPlayer!!.pause()
//            }
//
//
//            textViewTitle.text = "Audio Path \n${audioClass.audioPath}"
//            textViewDate.text = audioClass.dateTime
//
//            imageViewClose?.setOnClickListener {
//                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
//                    mediaPlayer!!.reset()
//                }
//                dialog?.dismiss()
//            }
//
//            var fab = dialog?.findViewById<View>(R.id.fab) as ExtendedFloatingActionButton
//            fab.setOnClickListener {
//                if(editTextTitle.text.toString().isNotEmpty()){
//                    pb?.show()
//                   // uploadFile(audioClass.audioPath,editTextTitle.text.toString())
//                }else{
//                    Toast.makeText(requireActivity(),"Give Voice Title Before Upload",Toast.LENGTH_SHORT).show()
//                }
//
//            }
//
//
//            dialog?.show()
//
//        }
//
//        fun uploadFile(audioPath: String,editTextTitle: String) {
//
//            val requestFile: RequestBody =
//                RequestBody.create("multipart/form-data".toMediaTypeOrNull(), audioPath)
//            // MultipartBody.Part is used to send also the actual file name
//            var imagenPerfil =
//                MultipartBody.Part.createFormData("STUDY_METERIAL_FILE", audioPath, requestFile)
//
//            val apiInterface = RetrofitClientStaff.create().fileUploadVoice(
//                SERVER_URL,
//                imagenPerfil
//            )
//            apiInterface.enqueue(object : Callback<FileVoiceResultModel> {
//                override fun onResponse(
//                    call: Call<FileVoiceResultModel>,
//                    resource: Response<FileVoiceResultModel>
//                ) {
//                    val response = resource.body()
//                    if (resource.isSuccessful) {
//                        Log.i(TAG, "response  $response")
//                        submitFile(response!!.vOICEMAILFILE, response.tEMPLATEID,editTextTitle)
//                    }
//                }
//
//                override fun onFailure(call: Call<FileVoiceResultModel>, t: Throwable) {
//                    pb?.dismiss()
//                    Log.i(CreateAssignmentDialog.TAG, "Throwable  $t")
//                }
//            })
//        }
//
//
//        private fun submitFile(vOICEMAILFILE: String, tEMPLATEID: String, editTextTitle : String) {
//
//            //  String url= Global.url+"Voice/VoiceAdd";
//            //
//            //        Map <String, String> postParam = new HashMap <String, String>();
//            //        postParam.put("VOICE_MAIL_TITLE", text);
//            //        postParam.put("VOICE_MAIL_FILE",VOICE_MAIL_FILE);
//            //        postParam.put("TEMPLATE_ID",TEMPLATE_ID);
//            //        postParam.put("VOICE_MAIL_CREATED_BY",Global.Admin_id);
//
//            val url = "Voice/VoiceAdd"
//            val jsonObject = JSONObject()
//            try {
//                jsonObject.put("VOICE_MAIL_TITLE", editTextTitle)
//                jsonObject.put("VOICE_MAIL_FILE", vOICEMAILFILE)
//                jsonObject.put("TEMPLATE_ID", tEMPLATEID)
//                jsonObject.put("VOICE_MAIL_CREATED_BY",adminId)
//
//
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//            Log.i(CreateAssignmentDialog.TAG, "jsonObject $jsonObject")
//            val accademicRe =
//                jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
//            quickVoiceMessageViewModel.getCommonPostFun(url, accademicRe)
//                .observe(requireActivity(), Observer {
//                    it?.let { resource ->
//                        when (resource.status) {
//                            Status.SUCCESS -> {
//                                val response = resource.data?.body()!!
//                                pb?.dismiss()
//                                dialog?.dismiss()
//                                when {
//                                    Utils.resultFun(response) == "SUCCESS" -> {
//                                    }
//                                    Utils.resultFun(response) == "FAILED" -> {
//                                        Toast.makeText(requireActivity(),"Failed While Uploading Audio",Toast.LENGTH_SHORT).show()
//                                    }
//                                    else -> {
//                                        Toast.makeText(requireActivity(),"Error while Uploading",Toast.LENGTH_SHORT).show()
//                                    }
//                                }
//                            }
//                            Status.ERROR -> {
//                                Toast.makeText(requireActivity(),"Please try again after sometime",Toast.LENGTH_SHORT).show()
//                            }
//                            Status.LOADING -> {
//                                Log.i(CreateAssignmentDialog.TAG, "loading")
//                            }
//                        }
//                    }
//                })
//
//        }
//
//
////        }
//    }

//    fun viewTab() {
//        val adapter = MyPagerAdapter(supportFragmentManager)
//
//        adapter.addFragment(FragmentInstance1().newInstance(".mp3"), ".mp3") //
//        adapter.addFragment(FragmentInstance1().newInstance(".wav"), ".wav")
//        adapter.addFragment(FragmentInstance1().newInstance(".m4a"), ".m4a")
//        adapter.addFragment(FragmentInstance1().newInstance(".aac"), ".aac") //OGG
//        adapter.addFragment(FragmentInstance1().newInstance(".wma"), ".wma") //WMA
//        adapter.addFragment(FragmentInstance1().newInstance(".ogg"), ".ogg") //OGG
//
//
//        //Delivery Reports
//        viewPager?.adapter = adapter
//        tabLayout?.setupWithViewPager(viewPager)
//
//        viewPager?.currentItem = currentPage
//
//        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                viewPager?.currentItem = tab.position
//                // currentPage = tab.position
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {}
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//        })
//    }

