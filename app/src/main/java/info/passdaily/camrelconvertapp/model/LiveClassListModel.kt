package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class LiveClassListModel(
    @SerializedName("LiveClassList")
    val liveClassList: List<LiveClass>
){
    @Keep
    data class LiveClass(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CREATED_BY")
        val cREATEDBY: String,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("INVITE_OPTION")
        val iNVITEOPTION: Int,
        @SerializedName("MEETING_DETAILS")
        val mEETINGDETAILS: Int,
        @SerializedName("PARTICIPANT_DETAILS")
        val pARTICIPANTDETAILS: Int,
        @SerializedName("STAFF_ID")
        val sTAFFID: Int,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("SUBJECT_STATUS")
        val sUBJECTSTATUS: Int,
        @SerializedName("TOTAL_STUDENT")
        val tOTALSTUDENT: Int,
        @SerializedName("UNMUTE_AUDIO")
        val uNMUTEAUDIO: Int,
        @SerializedName("UPDATED_DATE")
        val uPDATEDDATE: Any,
        @SerializedName("Z_LIVE_CLASS_ID")
        val zLIVECLASSID: Int,
        @SerializedName("ZOOM_ACCADEMIC_ID")
        val zOOMACCADEMICID: Int,
        @SerializedName("ZOOM_CLASS_ID")
        val zOOMCLASSID: Int,
        @SerializedName("ZOOM_END_DATE")
        val zOOMENDDATE: String,
        @SerializedName("ZOOM_MEETING_ID")
        val zOOMMEETINGID: String,
        @SerializedName("ZOOM_MEETING_LINK")
        val zOOMMEETINGLINK: String,
        @SerializedName("ZOOM_MEETING_PASSWORD")
        val zOOMMEETINGPASSWORD: String,
        @SerializedName("ZOOM_MEETING_STATUS")
        val zOOMMEETINGSTATUS: Int,
        @SerializedName("ZOOM_START_DATE")
        val zOOMSTARTDATE: String,
        @SerializedName("ZOOM_SUBJECT_ID")
        val zOOMSUBJECTID: Int,
        @SerializedName("Z_STATUS")
        val zSTATUS: Int
    )
}