package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class LeaveStaffDetailsModel(
    @SerializedName("FilesList")
    var filesList: List<Files>,
    @SerializedName("LeaveDetails")
    var leaveDetails: LeaveDetails
) {
    @Keep
    data class Files(
        @SerializedName("CREATED_DATE")
        var cREATEDDATE: String,
        @SerializedName("FILE_ID")
        var fILEID: Int,
        @SerializedName("FILE_NAME")
        var fILENAME: String,
        @SerializedName("FILE_STATUS")
        var fILESTATUS: Int,
        @SerializedName("LEAVE_ID")
        var lEAVEID: Int
    )

    @Keep
    data class LeaveDetails(
        @SerializedName("ACCADEMIC_ID")
        var aCCADEMICID: Int,
        @SerializedName("APPROVED_BY_NAME")
        var aPPROVEDBYNAME: Any,
        @SerializedName("LEAVE_APPROVED_BY")
        var lEAVEAPPROVEDBY: Any,
        @SerializedName("LEAVE_APPROVED_DATE")
        var lEAVEAPPROVEDDATE: Any,
        @SerializedName("LEAVE_APPROVED_REASON")
        var lEAVEAPPROVEDREASON: Any,
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
        var sCHOOLID: Any,
        @SerializedName("STAFF_FNAME")
        var sTAFFFNAME: String,
        @SerializedName("STAFF_ID")
        var sTAFFID: Int,
        @SerializedName("STAFF_IMAGE")
        var sTAFFIMAGE: String,
        @SerializedName("STAFF_PHONE_NUMBER")
        var sTAFFPHONENUMBER: String,
        @SerializedName("TOTAL_FILE")
        var tOTALFILE: Any
    )
}