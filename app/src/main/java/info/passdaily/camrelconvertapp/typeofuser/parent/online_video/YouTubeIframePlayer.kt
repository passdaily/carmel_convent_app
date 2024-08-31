package info.passdaily.camrelconvertapp.typeofuser.parent.online_video

//import android.os.Bundle
//import android.view.View
//import android.webkit.WebChromeClient
//import android.webkit.WebSettings
//import android.webkit.WebView
//import androidx.appcompat.app.AppCompatActivity
//import info.passdaily.camrelconvertapp.databinding.ActivityIframeYoutubePlayerBinding
//
//class YouTubeIframePlayer : AppCompatActivity() {
//
//    private lateinit var binding: ActivityIframeYoutubePlayerBinding
//
//    private var youTubeLink = ""
//    private var yOUTUBEID = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
////        setContentView(R.layout.activity_main)
//
//        val extras = intent.extras
//        if (extras != null) {
//            youTubeLink = extras.getString("youTubeLink")!!
//            yOUTUBEID = extras.getInt("YOUTUBE_ID")
//            //  intent.putExtra("OEXAM_DURATION",OEXAM_DURATION)
//        }
//        binding = ActivityIframeYoutubePlayerBinding.inflate(layoutInflater)
////        setContentView(binding.root)
//
//        val webView: WebView = binding.webView
//
//        val videoUrl = "https://www.youtube.com/embed/$youTubeLink"
//
//        val embedString = "<iframe  src=\"$videoUrl\" frameborder=\"0\" allowfullscreen></iframe>"
//        webView.webChromeClient = WebChromeClient()
//        val webSettings = webView.settings
//        webSettings.javaScriptEnabled = true
//        webView.settings.loadWithOverviewMode = true
//
//        var text = "<html><body style=\"text-align:justify;\">"
//        text += embedString
//        text += "</body></html>"
//        webView.loadData(text, "text/html", "utf-8")
//
//        // Enable JavaScript (required for YouTube player)
//       // webView.settings.javaScriptEnabled = true
//
////        val webSettings: WebSettings = webView.settings
////        webSettings.javaScriptEnabled = true
//
//     //   webView.loadUrl("https://www.youtube.com/embed/$youTubeLink")
//
//        // Load the YouTube video using iframe
////        webView.loadUrl("https://www.youtube.com/embed/$youTubeLink")
////    }
//        // Load the YouTube video using iframe
////        val iframeCode = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/$youTubeLink\" frameborder=\"0\" allowfullscreen></iframe>"
////        webView.loadData(iframeCode, "text/html", "utf-8")
////
////        // Handle page navigation within WebView
////        webView.webViewClient = object : WebViewClient() {
////            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
////                view?.loadUrl(url!!)
////                return true
////            }
////        }
//    }
//}





//import android.os.Bundle
//import com.google.android.youtube.player.YouTubeBaseActivity
//import com.google.android.youtube.player.YouTubeInitializationResult
//import com.google.android.youtube.player.YouTubePlayer
//import com.google.android.youtube.player.YouTubePlayerView
//import info.passdaily.camrelconvertapp.R
//
//
//class YouTubeIframePlayer : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {
//    private var youTubePlayerView: YouTubePlayerView? = null
//    //private val videoId = "YOUR_VIDEO_ID" // Replace with your video ID
//
//    private var youTubeLink = ""
//    private var yOUTUBEID = 0
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_youtube_player)
//
//        val extras = intent.extras
//        if (extras != null) {
//            youTubeLink = extras.getString("youTubeLink")!!
//            yOUTUBEID = extras.getInt("YOUTUBE_ID")
//            //  intent.putExtra("OEXAM_DURATION",OEXAM_DURATION)
//        }
//
//        youTubePlayerView = findViewById(R.id.youtubePlayerView)
//        youTubePlayerView?.initialize(resources.getString(R.string.google_maps_key), this)
//    }
//
//    override fun onInitializationSuccess(
//        provider: YouTubePlayer.Provider, player: YouTubePlayer, wasRestored: Boolean
//    ) {
//        if (!wasRestored) {
//            // Set the device size for the player
//            player.setFullscreen(false) // Set to true for fullscreen
//            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
//            player.loadVideo(youTubeLink)
//        }
//    }
//
//    override fun onInitializationFailure(
//        provider: YouTubePlayer.Provider, errorReason: YouTubeInitializationResult
//    ) {
//        // Handle initialization failure
//    }
//}

import android.app.Activity
import android.app.ProgressDialog
import android.content.pm.ActivityInfo
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import info.passdaily.camrelconvertapp.databinding.ActivityIframeYoutubePlayerBinding


@Suppress("DEPRECATION")
class YouTubeIframePlayer : ComponentActivity() {
    private lateinit var binding: ActivityIframeYoutubePlayerBinding

    private var progressBar: ProgressDialog? = null
//    var youTubePlayerView: YouTubePlayerView? = null
//
    private var youTubeLink = ""
    private var yOUTUBEID = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressBar = ProgressDialog(this)
        progressBar?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressBar?.setCancelable(false)

    val extras = intent.extras
    if (extras != null) {
        youTubeLink = extras.getString("youTubeLink")!!
        yOUTUBEID = extras.getInt("YOUTUBE_ID")
        //  intent.putExtra("OEXAM_DURATION",OEXAM_DURATION)
    }



        // get device dimensions
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels

    setContent {
        Column {
            YoutubeVideoPlayer(videoId = youTubeLink,width = width,height = height)
        }
    }

//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

//        binding = ActivityIframeYoutubePlayerBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        youTubePlayerView = binding.youtubePlayerView
//        lifecycle.addObserver(youTubePlayerView!!)
//
//        youTubePlayerView!!.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//            override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
//              //  val videoId: String = getVideoId()
//                youTubePlayer.loadVideo(youTubeLink, 0F)
//            }
//        })
    }


    @Composable
    fun YoutubeVideoPlayer(videoId: String,width : Int,height :Int) {
        val webView = WebView(LocalContext.current).apply {
            settings.javaScriptEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = false
            //webViewClient = MyBrowser(this@YouTubeIframePlayer,progressBar!!)
            webViewClient = WebViewClient()
        }

        val htmlData = getHTMLData(videoId,width,height)

        Column(Modifier.fillMaxSize()) {

            AndroidView(factory = { webView }) { view ->
                view.loadDataWithBaseURL(
                    "https://www.youtube.com",
                    htmlData,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
//            Button(onClick = {
//                webView.loadUrl("javascript:playVideo();")
//            }) {
//                Text(text = "Play Video")
//            }
//            Button(onClick = {
//                webView.loadUrl("javascript:pauseVideo();")
//            }) {
//                Text(text = "Pause Video")
//            }
//            Button(onClick = {
//                webView.loadUrl("javascript:seekTo(60);")
//            }) {
//                Text(text = "Seek Video")
//            }
        }
    }


    fun getHTMLData(videoId: String, width: Int, height: Int): String {
        return """
        <html>

            <body style="margin:0px;padding:0px;">
               <div id="player">
           </div>
                <script>
                    var player;
                    function onYouTubeIframeAPIReady() {
                        player = new YT.Player('player', {
                            
                             videoId: '$videoId',

                            playerVars: {
                       
                                'autoplay': 1,
                                'controls': 0,
                                'fs':1,
                            },
                            events: {
                                'onReady': onPlayerReady
                            }
                        });
                    }
                    function onPlayerReady(event) {
                     player.playVideo();
                        // Player is ready
                    }
                    function seekTo(time) {
                        player.seekTo(time, true);
                    }
                      function playVideo() {
                        player.playVideo();
                    }
                    function pauseVideo() {
                        player.pauseVideo();
                    }
                </script>
                <script src="https://www.youtube.com/iframe_api"></script>
            </body>
        </html>
    """.trimIndent()
//        return ("<html>"
//                + "<head>"
//                + "</head>"
//                + "<body style=\"border: 0; padding: 0\">"
//                + "<iframe "
//                + "type=\"text/html\" "
//                + "class=\"youtube-player\" "
//                + "width= 100%\""
//                + "\" "
//                + "height= 95%\""
//                + "\" "
//                + "src=\"http://www.youtube.com/v/"
//                + videoId
//                + "?controls=0&showinfo=0&showsearch=0&modestbranding=0" +
//                "&autoplay=1&fs=1&vq=hd720\" " + "frameborder=\"0\"></iframe>"
//                + "</body>"
//                + "</html>")
    }



}



private class MyBrowser(var activity: Activity,var myprogressBar : ProgressDialog) : WebViewClient() {

    var TAG = "MyBrowser"



    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        Log.d(TAG, "shouldOverrideUrlLoading: loading ")
        //myprogressBar.show()
        view.loadUrl(url)
        return true
    }

    override fun onPageStarted(view: WebView?, url: String, favicon: Bitmap?) {
        Log.d(TAG, "onPageStarted: started")
        //myprogressBar.show()
        /*
        here you have to include the your keywords instead of tags [hardcoded string]
     */ //if (url.contains("tags")) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//        } else {
//            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        }
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView, url: String) {
      //  myprogressBar.dismiss()
        Log.d(TAG, "onPageFinished: finished")
        super.onPageFinished(view, url)
    }
}

