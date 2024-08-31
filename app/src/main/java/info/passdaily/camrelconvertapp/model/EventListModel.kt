package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class EventListModel(
    @SerializedName("EventList")
    val eventList: ArrayList<Event>
) {
    @Keep
    data class Event(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("EVENT_DATE")
        val eVENTDATE: String,
        @SerializedName("EVENT_DESCRIPTION")
        val eVENTDESCRIPTION: String,
        @SerializedName("EVENT_FILE")
        val eVENTFILE: String,
        @SerializedName("EVENT_ID")
        val eVENTID: Int,
        @SerializedName("EVENT_LINK_FILE")
        val eVENTLINKFILE: String,
        @SerializedName("EVENT_STATUS")
        val eVENTSTATUS: Int,
        @SerializedName("EVENT_TITLE")
        val eVENTTITLE: String,
        @SerializedName("EVENT_TYPE")
        val eVENTTYPE: Int
    )
}