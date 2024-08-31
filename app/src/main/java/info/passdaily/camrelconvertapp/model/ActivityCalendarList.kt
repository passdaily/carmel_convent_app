package info.passdaily.camrelconvertapp.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ActivityCalendarList(
    @SerializedName("ActivityList")
    val activityList: ArrayList<ActivityList>
) {
    @Keep
    data class ActivityList(
        @SerializedName("ACTIVITY_ID")
        val aCTIVITYID: Int,
        @SerializedName("ACTIVITY_DAY")
        val aCTIVITYDAY: Int,
        @SerializedName("ACTIVITY_MONTH")
        val aCTIVITYMONTH: Int,
        @SerializedName("ACTIVITY_YEAR")
        val aCTIVITYYEAR: Int,
        @SerializedName("ACTIVITY_DESCRIPTION")
        val aCTIVITYDESCRIPTION: String,
        @SerializedName("ACTIVITY_DATE")
        val aCTIVITYDATE: String,
        @SerializedName("ACTIVITY_STATUS")
        val ACTIVITY_STATUS: Any
    )
}