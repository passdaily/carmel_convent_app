package cafe.adriel.androidaudiorecorder;

import static android.Manifest.permission.RECORD_AUDIO;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;
import omrecorder.AudioChunk;
import omrecorder.PullTransport;
import omrecorder.Recorder;

public class AudioRecorderActivity extends AppCompatActivity
        implements PullTransport.OnAudioChunkPulledListener, MediaPlayer.OnCompletionListener {

    String TAG = "AudioRecorderActivity";

    // AudioRefresh audioRefresh;

    String fileName = null;

    private String filePath;
    private AudioSource source;
    private AudioChannel channel;
    private AudioSampleRate sampleRate;
    private int color;
    private boolean autoStart;
    private boolean keepDisplayOn;

    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;

    private MediaPlayer player;
    private Recorder recorder;
    private VisualizerHandler visualizerHandler;


    private MediaRecorder mediaRecorder;

    private Timer timer;
    private MenuItem saveMenuItem;
    private int recorderSecondsElapsed;
    private int playerSecondsElapsed;
    private boolean isRecording;

    private RelativeLayout contentLayout;
    private GLAudioVisualizationView visualizerView;
    private TextView statusView;
    private TextView timerView;
    private ImageButton restartView;
    private ImageButton recordView;
    private ImageButton playView;

    //int idName=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aar_activity_audio_recorder);

//        try {
//            this.audioRefresh = (AudioRefresh) this.getApplicationContext();
//        } catch (Exception e) {
//            Log.i(TAG, "Exception " + e);
//        }

        if(savedInstanceState != null) {
            filePath = savedInstanceState.getString(AndroidAudioRecorder.EXTRA_FILE_PATH);
            source = (AudioSource) savedInstanceState.getSerializable(AndroidAudioRecorder.EXTRA_SOURCE);
            channel = (AudioChannel) savedInstanceState.getSerializable(AndroidAudioRecorder.EXTRA_CHANNEL);
            sampleRate = (AudioSampleRate) savedInstanceState.getSerializable(AndroidAudioRecorder.EXTRA_SAMPLE_RATE);
            color = savedInstanceState.getInt(AndroidAudioRecorder.EXTRA_COLOR);
            autoStart = savedInstanceState.getBoolean(AndroidAudioRecorder.EXTRA_AUTO_START);
            keepDisplayOn = savedInstanceState.getBoolean(AndroidAudioRecorder.EXTRA_KEEP_DISPLAY_ON);
        } else {
            filePath = getIntent().getStringExtra(AndroidAudioRecorder.EXTRA_FILE_PATH);
            source = (AudioSource) getIntent().getSerializableExtra(AndroidAudioRecorder.EXTRA_SOURCE);
            channel = (AudioChannel) getIntent().getSerializableExtra(AndroidAudioRecorder.EXTRA_CHANNEL);
            sampleRate = (AudioSampleRate) getIntent().getSerializableExtra(AndroidAudioRecorder.EXTRA_SAMPLE_RATE);
            color = getIntent().getIntExtra(AndroidAudioRecorder.EXTRA_COLOR, Color.BLACK);
            autoStart = getIntent().getBooleanExtra(AndroidAudioRecorder.EXTRA_AUTO_START, false);
            keepDisplayOn = getIntent().getBooleanExtra(AndroidAudioRecorder.EXTRA_KEEP_DISPLAY_ON, false);
        }

        if(keepDisplayOn){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(Util.getDarkerColor(color)));
            getSupportActionBar().setHomeAsUpIndicator(
                    ContextCompat.getDrawable(this, R.drawable.aar_ic_clear));
        }

        visualizerView = new GLAudioVisualizationView.Builder(this)
                .setLayersCount(1)
                .setWavesCount(6)
                .setWavesHeight(R.dimen.aar_wave_height)
                .setWavesFooterHeight(R.dimen.aar_footer_height)
                .setBubblesPerLayer(20)
                .setBubblesSize(R.dimen.aar_bubble_size)
                .setBubblesRandomizeSize(true)
                .setBackgroundColor(Util.getBrightColor(color))
                .setLayerColors(new int[]{color})
                .build();

        contentLayout = (RelativeLayout) findViewById(R.id.content);
        statusView = (TextView) findViewById(R.id.status);
        timerView = (TextView) findViewById(R.id.timer);
        restartView = (ImageButton) findViewById(R.id.restart);
        recordView = (ImageButton) findViewById(R.id.record);
        playView = (ImageButton) findViewById(R.id.play);

        contentLayout.setBackgroundColor(Util.getDarkerColor(color));
        contentLayout.addView(visualizerView, 0);
        restartView.setVisibility(View.INVISIBLE);
        playView.setVisibility(View.INVISIBLE);

        if(Util.isBrightColor(color)) {
            ContextCompat.getDrawable(this, R.drawable.aar_ic_clear)
                    .setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            ContextCompat.getDrawable(this, R.drawable.aar_ic_check)
                    .setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            statusView.setTextColor(Color.BLACK);
            timerView.setTextColor(Color.BLACK);
            restartView.setColorFilter(Color.BLACK);
            recordView.setColorFilter(Color.BLACK);
            playView.setColorFilter(Color.BLACK);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(autoStart && !isRecording){
            toggleRecording(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            visualizerView.onResume();
        } catch (Exception e){ }
    }

    @Override
    protected void onPause() {
        try {
            restartRecording(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            visualizerView.onPause();
        } catch (Exception e){ }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            restartRecording(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setResult(RESULT_CANCELED);
        try {
            visualizerView.release();
        } catch (Exception e){ }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(AndroidAudioRecorder.EXTRA_FILE_PATH, filePath);
        outState.putInt(AndroidAudioRecorder.EXTRA_COLOR, color);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.aar_audio_recorder, menu);
        saveMenuItem = menu.findItem(R.id.action_save);
        saveMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.aar_ic_check));
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
            // audioRefresh.onRefreshListener();
        } else if (i == R.id.action_save) {
            try {
//                AudioUpdateListener audioUpdateListener = new InterfaceImp();
//                audioUpdateListener.onCompletion();
                selectAudio();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAudioChunkPulled(AudioChunk audioChunk) {
        float amplitude = isRecording ? (float) audioChunk.maxAmplitude() : 0f;
        visualizerHandler.onDataReceived(amplitude);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopPlaying();
    }

    private void selectAudio() throws IOException {
        stopRecording();
        setResult(RESULT_OK);
        finish();
    }

    public void toggleRecording(View v) {
        stopPlaying();
        Util.wait(100, new Runnable() {
            @Override
            public void run() {

                if (isRecording) {
                    pauseRecording();
                } else {
                    if(checkPermissions()) {
                        resumeRecording();
                    }else{RequestPermissions();}
                }
                //  isRecording = !isRecording;
            }

        });
    }

    public void togglePlaying(View v){
        pauseRecording();
        Util.wait(100, new Runnable() {
            @Override
            public void run() {
                if(isPlaying()){
                    stopPlaying();
                } else {
                    startPlaying();
                }
            }
        });
    }

    public void restartRecording(View v) throws IOException {
        if(isRecording) {
            stopRecording();
        } else if(isPlaying()) {
            stopPlaying();
        } else {
            visualizerHandler = new VisualizerHandler();
            visualizerView.linkTo(visualizerHandler);
            visualizerView.release();
            if(visualizerHandler != null) {
                visualizerHandler.stop();
            }
        }
        saveMenuItem.setVisible(false);
        statusView.setVisibility(View.INVISIBLE);
        restartView.setVisibility(View.INVISIBLE);
        playView.setVisibility(View.INVISIBLE);
        recordView.setImageResource(R.drawable.aar_ic_rec);
        timerView.setText("00:00:00");
        recorderSecondsElapsed = 0;
        playerSecondsElapsed = 0;
    }

    private void resumeRecording() {
        isRecording = true;
        saveMenuItem.setVisible(false);
        statusView.setText(R.string.aar_recording);
        statusView.setVisibility(View.VISIBLE);
        restartView.setVisibility(View.INVISIBLE);
        playView.setVisibility(View.INVISIBLE);
        recordView.setImageResource(R.drawable.aar_ic_pause);
        playView.setImageResource(R.drawable.aar_ic_play);

        visualizerHandler = new VisualizerHandler();
        visualizerView.linkTo(visualizerHandler);
        // idName++;

//        File fname =new File (Utils.Companion.getRootDirPath(this)+"/Passdaily/");
//
//        if (!fname.exists()){
//            fname.mkdirs();
//        } else{
//            Log.i(TAG,"Already existing");
//        }
        fileName = Util.getRootDirPath(this)+"/Passdaily/Audio/";

        File outFile = new File(fileName);

        if (!outFile.exists()){
            outFile.mkdirs();
        } else{
            Log.i(TAG,"Already existing");
        }
        String currentDateAndTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        //  long tsLong = System.currentTimeMillis()/1000;
        // String ts = Long.toString(System.currentTimeMillis()/1000);


        fileName +=  "AudioRecord_"+currentDateAndTime+".mp3";

        Log.i(TAG,"Audio- "+fileName);

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);////change THREE_GPP TO MPEG_4   //DEFAULT
        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);/// change  AMR_NB to AAC    // DEFAULT
        //    recorder.setAudioChannels(AudioFormat.CHANNEL_IN_MONO); //we wrote  //AudioFormat.CHANNEL_IN_MONO
        mediaRecorder.setAudioChannels(1); //we wrote  //MONO
        mediaRecorder.setAudioSamplingRate(44100);//// if not specified, defaults to 8kHz, if specified 44.1 or 48 kHz, lots of noise  // 44100 kHz
        mediaRecorder.setAudioEncodingBitRate(64000);////we wrote //  /64kbps ///20kbps //96kbps

        try {

            mediaRecorder.prepare();
            //change with setDataSource(Context,Uri);
            if (mediaRecorder != null) {
                mediaRecorder.start();
            }
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
            Log.i(TAG,"IllegalArgumentException "+e);
            Log.i(TAG,"IllegalStateException "+e);
            Log.i(TAG,"IOException "+e);
        }

//        try {
//            mediaRecorder.prepare();
//            // recorder.start();
//            //   recorder.start();
//        } catch (IOException e) {
//            Log.i(TAG,"IOException "+e);
//            Log.i(TAG,"prepare() failed");
//        }
//
//        if (mediaRecorder != null) {
//            mediaRecorder.start();
//        }

//        if(recorder == null) {
//            timerView.setText("00:00:00");
//
//            recorder = OmRecorder.wav(
//                    new PullTransport.Default(Util.getMic(source, channel, sampleRate), AudioRecorderActivity.this),
//                    new File(filePath));
//        }
//        recorder.resumeRecording();


//        if(recorder == null) {
//            timerView.setText("00:00:00");
//
//            recorder = new MediaRecorder();
//            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);////change THREE_GPP TO MPEG_4   //DEFAULT
//            recorder.setOutputFile(filePath);
//            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);/// change  AMR_NB to AAC    // DEFAULT
//            //    recorder.setAudioChannels(AudioFormat.CHANNEL_IN_MONO); //we wrote  //AudioFormat.CHANNEL_IN_MONO
//            recorder.setAudioChannels(1); //we wrote  //MONO
//            recorder.setAudioSamplingRate(44100);//// if not specified, defaults to 8kHz, if specified 44.1 or 48 kHz, lots of noise  // 44100 kHz
//            recorder.setAudioEncodingBitRate(64000);////we wrot
//
////            recorder = OmRecorder.wav(
////                    new PullTransport.Default(Util.getMic(source, channel, sampleRate), AudioRecorderActivity.this),
////                    new File(filePath));
//        }
//        //   recorder.resumeRecording();
//        try {
//            recorder.prepare();
//        } catch (IOException e) {
//            Log.i("TAG","prepare() failed");
//        }
//
//        if (recorder != null)
//            recorder.resume();

        startTimer();
    }


    public boolean checkPermissions() {


        //  int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return /*result == PackageManager.PERMISSION_GRANTED && */result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO /*, WRITE_EXTERNAL_STORAGE*/}, REQUEST_AUDIO_PERMISSION_CODE);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG,"requestCode "+requestCode);
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    /*boolean permissionToStore = grantResults[1] ==  PackageManager.PERMISSION_GRANTED;*/
                    Log.i(TAG,"permissionToRecord "+permissionToRecord);
                    //   Log.i(TAG,"permissionToStore "+permissionToStore);
                    if (permissionToRecord /*&& permissionToStore*/) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void pauseRecording() {
        isRecording = false;
        if(!isFinishing()) {
            saveMenuItem.setVisible(true);
        }
        statusView.setText(R.string.aar_paused);
        statusView.setVisibility(View.VISIBLE);
        restartView.setVisibility(View.VISIBLE);
        playView.setVisibility(View.VISIBLE);
        recordView.setImageResource(R.drawable.aar_ic_rec);
        playView.setImageResource(R.drawable.aar_ic_play);

        visualizerView.release();
        if(visualizerHandler != null) {
            visualizerHandler.stop();
        }

        try {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }catch(Exception e){
            Log.i(TAG,"IOException "+e);
            // Toast.makeText(getActivity(),"Retry..", Toast.LENGTH_LONG).show();
        }

        if (recorder != null) {
            recorder.pauseRecording();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                recorder.pause();
//            }

        }

        stopTimer();
    }

    private void stopRecording() throws IOException {
        visualizerView.release();
        if(visualizerHandler != null) {
            visualizerHandler.stop();
        }

        recorderSecondsElapsed = 0;
        if (recorder != null) {
            recorder.stopRecording();
//            recorder.stop();

            recorder = null;
        }

        stopTimer();
    }

    private void startPlaying(){
        try {
            stopRecording();
            player = new MediaPlayer();
            player.setDataSource(fileName);
            player.prepare();
            player.start();

            visualizerView.linkTo(DbmHandler.Factory.newVisualizerHandler(this, player));
            visualizerView.post(new Runnable() {
                @Override
                public void run() {
                    player.setOnCompletionListener(AudioRecorderActivity.this);
                }
            });

            timerView.setText("00:00:00");
            statusView.setText(R.string.aar_playing);
            statusView.setVisibility(View.VISIBLE);
            playView.setImageResource(R.drawable.aar_ic_stop);

            playerSecondsElapsed = 0;
            startTimer();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void stopPlaying(){
        statusView.setText("");
        statusView.setVisibility(View.INVISIBLE);
        playView.setImageResource(R.drawable.aar_ic_play);

        visualizerView.release();
        if(visualizerHandler != null) {
            visualizerHandler.stop();
        }

        if(player != null){
            try {
                player.stop();
                player.reset();
            } catch (Exception e){ }
        }

        stopTimer();
    }

    private boolean isPlaying(){
        try {
            return player != null && player.isPlaying() && !isRecording;
        } catch (Exception e){
            return false;
        }
    }

    private void startTimer(){
        stopTimer();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        }, 0, 1000);
    }

    private void stopTimer(){
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    private void updateTimer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isRecording) {
                    recorderSecondsElapsed++;
                    timerView.setText(Util.formatSeconds(recorderSecondsElapsed));
                } else if(isPlaying()){
                    playerSecondsElapsed++;
                    timerView.setText(Util.formatSeconds(playerSecondsElapsed));
                }
            }
        });
    }
}
