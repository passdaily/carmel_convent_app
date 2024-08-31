//package info.passdaily.parentapp.lib.video_theme
//
//import android.os.Bundle
//import android.view.View
//import androidx.activity.compose.setContent
//import androidx.activity.ComponentActivity
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalView
//import androidx.compose.ui.unit.dp
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.WindowInsetsControllerCompat
//import com.google.android.exoplayer2.MediaItem
////import com.imherrera.videoplayer.VideoPlayer
////import com.imherrera.videoplayer.rememberVideoPlayerState
////import com.imherrera.videoplayer.VideoPlayerControl
//import info.passdaily.parentapp.services.Global
//
//class VideoPlayerActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
////        var bundle :Bundle ?= intent.extras
////        ALBUM_URL = bundle?.getString("ALBUM_URL")!! // 1
////        ALBUM_TITLE = bundle.getString("aLBUMTITLE")!! // 1
////        setContentView(R.layout.activity_video_player)
//        setContent {
//
//            ComposePlayerSampleTheme {
//                val playerState = rememberVideoPlayerState()
//
//                VideoPlayer(
//                    modifier = Modifier
//                        .background(Color.Black)
//                        .adaptiveSize(playerState.isFullscreen.value, LocalView.current),
//                    playerState = playerState,
//                ) {
//                    /**
//                     * Use default control or implement your own
//                     * */
//                    VideoPlayerControl(
//                        state = playerState,
//                        title = Global.ALBUM_TITLE,
//                        subtitle = "Category : ${Global.ALBUM_CATEGORY_NAME}",
//                    )
//                }
//
//                LaunchedEffect(Unit) {
//                    playerState.player.setMediaItem(MediaItem.fromUri(Global.ALBUM_URL))
//                  // playerState.player.setMediaItem(MediaItem.fromUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"))
//                    playerState.player.prepare()
//                    playerState.player.playWhenReady = true
//                }
//            }
//        }
//    }
//}
//
//private fun Modifier.adaptiveSize(fullscreen: Boolean, view: View): Modifier {
//    return if (fullscreen) {
//        hideSystemBars(view)
//        fillMaxSize()
//    } else {
//        hideSystemBars(view)
//        fillMaxSize()
////        showSystemBars(view)
////        fillMaxWidth().height(250.dp)
//    }
//}
//
//
//private fun hideSystemBars(view: View) {
//    val windowInsetsController = ViewCompat.getWindowInsetsController(view) ?: return
//    // Configure the behavior of the hidden system bars
//    windowInsetsController.systemBarsBehavior =
//        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//    // Hide both the status bar and the navigation bar
//    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
//}
//
//private fun showSystemBars(view: View) {
//    val windowInsetsController = ViewCompat.getWindowInsetsController(view) ?: return
//    // Show both the status bar and the navigation bar
//    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
//}