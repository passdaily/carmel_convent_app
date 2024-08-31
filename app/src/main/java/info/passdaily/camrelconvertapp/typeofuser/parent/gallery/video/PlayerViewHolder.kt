package info.passdaily.camrelconvertapp.typeofuser.parent.gallery.video

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.RequestManager
import android.widget.TextView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.video.VideoActivity

class PlayerViewHolder(private val parent: View) : ViewHolder(
    parent
) {
    /**
     * below view have public modifier because
     * we have access PlayerViewHolder inside the ExoPlayerRecyclerView
     */
    @JvmField
    var mediaContainer: FrameLayout = itemView.findViewById(R.id.mediaContainer)

    @JvmField
    var mediaCoverImage: ImageView = itemView.findViewById(R.id.ivMediaCoverImage)

    @JvmField
    var volumeControl: ImageView = itemView.findViewById(R.id.ivVolumeControl)

    @JvmField
    var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

    @JvmField
    var imageViewPlay: ImageView = itemView.findViewById(R.id.imageViewPlay)

    @JvmField
    var requestManager: RequestManager? = null
    fun onBind(mediaObject: VideoActivity.VidModel, requestManager: RequestManager?) {
        this.requestManager = requestManager
        parent.tag = this
        this.requestManager!!.load(R.drawable.artboard)
            .into(mediaCoverImage)
    }

}