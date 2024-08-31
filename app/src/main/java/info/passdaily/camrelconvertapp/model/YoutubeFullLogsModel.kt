package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class YoutubeFullLogsModel(
    @SerializedName("YoutubeFullLogs")
    val youtubeFullLogs: List<YoutubeFullLog>
){
    @Keep
    data class YoutubeFullLog(
        @SerializedName("LOG_DATE")
        val lOGDATE: String,
        @SerializedName("YOUTUBE_LOG_ID")
        val yOUTUBELOGID: Int,
        @SerializedName("YOUTUBE_LOG_S_ID")
        val yOUTUBELOGSID: Int
    )
}