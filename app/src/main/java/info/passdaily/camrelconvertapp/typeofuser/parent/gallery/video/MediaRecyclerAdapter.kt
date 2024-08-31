package info.passdaily.camrelconvertapp.typeofuser.parent.gallery.video

import com.bumptech.glide.RequestManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import info.passdaily.camrelconvertapp.R
import java.util.ArrayList

class MediaRecyclerAdapter(
    private val mediaObjects: ArrayList<VideoActivity.VidModel>,
    private val requestManager: RequestManager
) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return PlayerViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.video_card_adapter, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        (viewHolder as PlayerViewHolder).onBind(mediaObjects[i], requestManager)
    }

    override fun getItemCount(): Int {
        return mediaObjects.size
    }
}