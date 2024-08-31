package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ZoomScheduleStaffModel(
    @SerializedName("ZoomList")
    val zoomList: List<Zoom>
){
    @Keep
    data class Zoom(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CREATED_BY")
        val cREATEDBY: Int,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("MEETING_ID")
        val mEETINGID: String,
        @SerializedName("MEETING_LINK")
        val mEETINGLINK: String,
        @SerializedName("MEETING_PASSWORD")
        val mEETINGPASSWORD: String,
        @SerializedName("MEETING_STATR_DATE")
        val mEETINGSTATRDATE: String,
        @SerializedName("MEETING_STATUS")
        val mEETINGSTATUS: Int,
        @SerializedName("MEETING_TITLE")
        val mEETINGTITLE: String,
        @SerializedName("Z_MEETING_ID")
        val zMEETINGID: Int
    )
}