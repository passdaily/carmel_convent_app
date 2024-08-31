package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class LeaveStaffListModel(
    @SerializedName("LeaveList")
    var leaveList: List<Leave>
) {
    @Keep
    data class Leave(
        @SerializedName("ACCADEMIC_ID")
        var aCCADEMICID: Int,
        @SerializedName("APPROVED_BY_NAME")
        var aPPROVEDBYNAME: String,
        @SerializedName("LEAVE_APPROVED_BY")
        var lEAVEAPPROVEDBY: String,
        @SerializedName("LEAVE_APPROVED_DATE")
        var lEAVEAPPROVEDDATE: String,
        @SerializedName("LEAVE_APPROVED_REASON")
        var lEAVEAPPROVEDREASON: String,
        @SerializedName("LEAVE_DESCRIPTION")
        var lEAVEDESCRIPTION: String,
        @SerializedName("LEAVE_FROM_DATE")
        var lEAVEFROMDATE: String,
        @SerializedName("LEAVE_ID")
        var lEAVEID: Int,
        @SerializedName("LEAVE_STATUS")
        var lEAVESTATUS: Int,
        @SerializedName("LEAVE_SUBJECT")
        var lEAVESUBJECT: String,
        @SerializedName("LEAVE_SUBMITTED_DATE")
        var lEAVESUBMITTEDDATE: String,
        @SerializedName("LEAVE_TO_DATE")
        var lEAVETODATE: String,
        @SerializedName("SCHOOL_ID")
        var sCHOOLID: Int,
        @SerializedName("STAFF_FNAME")
        var sTAFFFNAME: String,
        @SerializedName("STAFF_ID")
        var sTAFFID: Int,
        @SerializedName("STAFF_IMAGE")
        var sTAFFIMAGE: String,
        @SerializedName("STAFF_PHONE_NUMBER")
        var sTAFFPHONENUMBER: String,
        @SerializedName("TOTAL_FILE")
        var tOTALFILE: Int,
        ///LEAVE_OPERATION
        @SerializedName("LEAVE_OPERATION")
        var lEAVEOPERATION: String,
        @SerializedName("LEAVE_STATUS_TEXT")
        var lEAVESTATUSTEXT: String,

        //LEAVE_STATUS_TEXT
    )
}