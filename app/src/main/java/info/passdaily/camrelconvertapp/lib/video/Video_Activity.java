package info.passdaily.camrelconvertapp.lib.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import info.passdaily.camrelconvertapp.R;
import info.passdaily.camrelconvertapp.typeofuser.parent.MainActivityParent;

public class Video_Activity extends AppCompatActivity implements
        SurfaceHolder.Callback, MediaPlayer.OnPreparedListener,
        VideoControllerView.MediaPlayerControlListener,
        MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnCompletionListener{

    String TAG="videoView";
    String ALBUM_FILE,ALBUM_TITLE,ALBUM_ID;
    SurfaceHolder videoHolder;

    ResizeSurfaceView mVideoSurface;
    MediaPlayer mMediaPlayer;
    VideoControllerView controller;
    int mVideoWidth;
    int mVideoHeight;
    View mContentView;
    private View mLoadingView;
    private boolean mIsComplete;

    FrameLayout fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.testvideoview);


        mVideoSurface = (ResizeSurfaceView) findViewById(R.id.videoSurface);
        mContentView = findViewById(R.id.video_container);
        mLoadingView = findViewById(R.id.loading);
        videoHolder = mVideoSurface.getHolder();
        videoHolder.addCallback(this);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        fm=(FrameLayout)findViewById(R.id.videoSurfaceContainer) ;

        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b != null) {
            ALBUM_TITLE = b.getString("ALBUM_TITLE");
            ALBUM_FILE = b.getString("ALBUM_FILE");

            videoplay(ALBUM_FILE);

        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void videoplay(String video) {

     //   Log.i(TAG,"video2 "+ Global.image_url+"/video/"+video);


        //(FrameLayout) findViewById(R.id.videoSurfaceContainer)
        controller = new VideoControllerView.Builder(this, this)

                .withVideoSurfaceView(mVideoSurface)//to enable toggle display controller view
                .canControlBrightness(true)
                .canControlVolume(true)
                .canSeekVideo(true)
                .exitIcon(R.drawable.video_top_back)
                .pauseIcon(R.drawable.ic_media_pause)
                .playIcon(R.drawable.ic_media_play)
                .withVideoTitle("")
                .shrinkIcon(R.drawable.ic_media_fullscreen_shrink)
                .stretchIcon(R.drawable.ic_media_fullscreen_stretch)
                .build((FrameLayout) findViewById(R.id.videoSurfaceContainer));//layout container that hold video play view

        mLoadingView.setVisibility(View.VISIBLE);

        try {
            Uri pl= Uri.parse(video);
            //Log.i(TAG,"video3 "+Global.image_url+"/video/"+video);

            mMediaPlayer.setDataSource(this,pl );
            // mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                @Override
                public void onPrepared(MediaPlayer playerM){
                    playerM.start();
                    mLoadingView.setVisibility(View.GONE);

                    mIsComplete = false;
                }
            });
            mMediaPlayer.setOnCompletionListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mVideoSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v,MotionEvent event) {
                controller.toggleControllerView();
                return false;
            }
        });

    }



    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            this.finish();
        } else {
            super.onBackPressed();
            /// onDestroy(); //call onStop
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mIsComplete = true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //setup video controller view
        mLoadingView.setVisibility(View.GONE);
        mVideoSurface.setVisibility(View.VISIBLE);
        mMediaPlayer.start();
        mIsComplete = false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp,int width,int height) {
        mVideoHeight = mp.getVideoHeight();
        mVideoWidth = mp.getVideoWidth();
        if (mVideoHeight > 0 && mVideoWidth > 0)
            mVideoSurface.adjustSize(mContentView.getWidth(), mContentView.getHeight(), mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVideoWidth > 0 && mVideoHeight > 0)
            mVideoSurface.adjustSize(getDeviceWidth(this),getDeviceHeight(this),mVideoSurface.getWidth(), mVideoSurface.getHeight());
    }


    public static int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        }
        return mDisplayMetrics.widthPixels;
    }

    public static int getDeviceHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        if (wm != null) {
            wm.getDefaultDisplay().getMetrics(mDisplayMetrics);
        }
        return mDisplayMetrics.heightPixels;
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            mMediaPlayer.setDisplay(holder);
        }
        catch (Exception e){
            e.printStackTrace();
            Intent intent=new Intent(Video_Activity.this, MainActivityParent.class);
            startActivity(intent);
        }
//        mMediaPlayer.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        resetPlayer();
    }
// End SurfaceHolder.Callback

    @Override
    public void start() {
        if(null != mMediaPlayer) {
            mMediaPlayer.start();
            mIsComplete = false;
        }
    }

    @Override
    public void pause() {
        if(null != mMediaPlayer) {
            mMediaPlayer.pause();
        }

    }

    @Override
    public int getCurrentPosition() {
        if(null != mMediaPlayer)
            return mMediaPlayer.getCurrentPosition();
        else
            return 0;
    }

    @Override
    public int getDuration() {
        if(null != mMediaPlayer)
            return mMediaPlayer.getDuration();
        else
            return 0;
    }

    @Override
    public void seekTo(int i) {
        if(null != mMediaPlayer) {
            mMediaPlayer.seekTo(i);
        }
    }

    @Override
    public boolean isPlaying() {
        if(null != mMediaPlayer)
            return mMediaPlayer.isPlaying();
        else
            return false;
    }

    @Override
    public boolean isComplete() {
        return mIsComplete;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean isFullScreen() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    @Override
    public void toggleFullScreen() {

    }

    @Override
    public void exit() {
        resetPlayer();
        finish();
    }


    private void resetPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
