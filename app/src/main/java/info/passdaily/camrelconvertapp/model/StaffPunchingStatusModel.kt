package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class StaffPunchingStatusModel(
    @SerializedName("PunchingAction")
    var punchingAction: PunchingAction,
    @SerializedName("PunchingDetails")
    var punchingDetails: List<PunchingDetail>,
    @SerializedName("PunchingHistory")
    var punchingHistory: ArrayList<PunchingHistory>,
    @SerializedName("PunchingStatus")
    var punchingStatus: List<PunchingStatu>
) {
    @Keep
    data class PunchingAction(
        @SerializedName("Action")
        var action: String,
        @SerializedName("LATTITUDE")
        var lATTITUDE: String?,
        @SerializedName("LONGITUDE")
        var lONGITUDE: String?,
        @SerializedName("Message")
        var message: String,
        @SerializedName("PUNCH_DISTANCE")
        var pUNCHDISTANCE: Int,
        @SerializedName("STAFF_FNAME")
        var sTAFFFNAME: String,
        @SerializedName("STAFF_IMAGE")
        var sTAFFIMAGE: String,
        @SerializedName("STAFF_IN_TIME")
        var sTAFFINTIME: String?,
        @SerializedName("STAFF_OUT_TIME")
        var sTAFFOUTTIME: String?,
        @SerializedName("TIME_NOW")
        var tIMENOW: String?,
        @SerializedName("WORK_END_TIME")
        var wORKENDTIME: String?,
        @SerializedName("WORK_START_TIME")
        var wORKSTARTTIME: String?
    )

    @Keep
    data class PunchingDetail(
        @SerializedName("ACCADEMIC_ID")
        var aCCADEMICID: Int,
        @SerializedName("ADMIN_ID")
        var aDMINID: Int,
        @SerializedName("DURATION")
        var dURATION: String,
        @SerializedName("PUNCH_IN_COMMENT_BY_ADMIN")
        var pUNCHINCOMMENTBYADMIN: String,
        @SerializedName("PUNCH_IN_DBRAND")
        var pUNCHINDBRAND: String,
        @SerializedName("PUNCH_IN_DID")
        var pUNCHINDID: String,
        @SerializedName("PUNCH_IN_DMODEL")
        var pUNCHINDMODEL: String,
        @SerializedName("PUNCHING_ID")
        var pUNCHINGID: Int,
        @SerializedName("PUNCHING_STATUS")
        var pUNCHINGSTATUS: Int,
        @SerializedName("PUNCH_IN_LATE_REASON")
        var pUNCHINLATEREASON: String,
        @SerializedName("PUNCH_IN_LATTITTUDE")
        var pUNCHINLATTITTUDE: String,
        @SerializedName("PUNCH_IN_LONGITTUDE")
        var pUNCHINLONGITTUDE: String,
        @SerializedName("PUNCH_IN_OUTRANGE_REASON")
        var pUNCHINOUTRANGEREASON: String,
        @SerializedName("PUNCH_IN_SELFIE")
        var pUNCHINSELFIE: String,
        @SerializedName("PUNCH_IN_STATUS")
        var pUNCHINSTATUS: Int,
        @SerializedName("PUNCH_IN_TIME")
        var pUNCHINTIME: String?,
        @SerializedName("PUNCH_OUT_COMMENT_BY_ADMIN")
        var pUNCHOUTCOMMENTBYADMIN: String,
        @SerializedName("PUNCH_OUT_DBRAND")
        var pUNCHOUTDBRAND: String,
        @SerializedName("PUNCH_OUT_DID")
        var pUNCHOUTDID: String,
        @SerializedName("PUNCH_OUT_DMODEL")
        var pUNCHOUTDMODEL: String,
        @SerializedName("PUNCH_OUT_LATE_REASON")
        var pUNCHOUTLATEREASON: String,
        @SerializedName("PUNCH_OUT_LATTITTUDE")
        var pUNCHOUTLATTITTUDE: String,
        @SerializedName("PUNCH_OUT_LONGITTUDE")
        var pUNCHOUTLONGITTUDE: String,
        @SerializedName("PUNCH_OUT_OUTRANGE_REASON")
        var pUNCHOUTOUTRANGEREASON: String,
        @SerializedName("PUNCH_OUT_SELFIE")
        var pUNCHOUTSELFIE: String,
        @SerializedName("PUNCH_OUT_STATUS")
        var pUNCHOUTSTATUS: Int,
        @SerializedName("PUNCH_OUT_TIME")
        var pUNCHOUTTIME: String,
        @SerializedName("SCHOOL_ID")
        var sCHOOLID: Int,
        @SerializedName("STAFF_ATTENDANCE_ID")
        var sTAFFATTENDANCEID: Int,
        @SerializedName("STAFF_ID")
        var sTAFFID: String
    )

    @Keep
    data class PunchingHistory(
        @SerializedName("ACCADEMIC_ID")
        var aCCADEMICID: Int,
        @SerializedName("ADMIN_ID")
        var aDMINID: Int,
        @SerializedName("DURATION")
        var dURATION: String,
        @SerializedName("PUNCH_IN_COMMENT_BY_ADMIN")
        var pUNCHINCOMMENTBYADMIN: String,
        @SerializedName("PUNCH_IN_DBRAND")
        var pUNCHINDBRAND: String,
        @SerializedName("PUNCH_IN_DID")
        var pUNCHINDID: String,
        @SerializedName("PUNCH_IN_DMODEL")
        var pUNCHINDMODEL: String,
        @SerializedName("PUNCHING_ID")
        var pUNCHINGID: Int,
        @SerializedName("PUNCHING_STATUS")
        var pUNCHINGSTATUS: Int,
        @SerializedName("PUNCH_IN_LATE_REASON")
        var pUNCHINLATEREASON: String,
        @SerializedName("PUNCH_IN_LATTITTUDE")
        var pUNCHINLATTITTUDE: String,
        @SerializedName("PUNCH_IN_LONGITTUDE")
        var pUNCHINLONGITTUDE: String,
        @SerializedName("PUNCH_IN_OUTRANGE_REASON")
        var pUNCHINOUTRANGEREASON: String,
        @SerializedName("PUNCH_IN_SELFIE")
        var pUNCHINSELFIE: String,
        @SerializedName("PUNCH_IN_STATUS")
        var pUNCHINSTATUS: Int,
        @SerializedName("PUNCH_IN_TIME")
        var pUNCHINTIME: String,
        @SerializedName("PUNCH_OUT_COMMENT_BY_ADMIN")
        var pUNCHOUTCOMMENTBYADMIN: String,
        @SerializedName("PUNCH_OUT_DBRAND")
        var pUNCHOUTDBRAND: String,
        @SerializedName("PUNCH_OUT_DID")
        var pUNCHOUTDID: String,
        @SerializedName("PUNCH_OUT_DMODEL")
        var pUNCHOUTDMODEL: String,
        @SerializedName("PUNCH_OUT_LATE_REASON")
        var pUNCHOUTLATEREASON: String,
        @SerializedName("PUNCH_OUT_LATTITTUDE")
        var pUNCHOUTLATTITTUDE: String,
        @SerializedName("PUNCH_OUT_LONGITTUDE")
        var pUNCHOUTLONGITTUDE: String,
        @SerializedName("PUNCH_OUT_OUTRANGE_REASON")
        var pUNCHOUTOUTRANGEREASON: String,
        @SerializedName("PUNCH_OUT_SELFIE")
        var pUNCHOUTSELFIE: String,
        @SerializedName("PUNCH_OUT_STATUS")
        var pUNCHOUTSTATUS: Int,
        @SerializedName("PUNCH_OUT_TIME")
        var pUNCHOUTTIME: String,
        @SerializedName("SCHOOL_ID")
        var sCHOOLID: Int,
        @SerializedName("STAFF_ATTENDANCE_ID")
        var sTAFFATTENDANCEID: Int,
        @SerializedName("STAFF_ID")
        var sTAFFID: String
    )

    @Keep
    data class PunchingStatu(
        @SerializedName("ACCADEMIC_ID")
        var aCCADEMICID: Int,
        @SerializedName("ADMIN_COMMENTS")
        var aDMINCOMMENTS: String,
        @SerializedName("ADMIN_ID")
        var aDMINID: Int,
        @SerializedName("ATTENDANCE_DATE")
        var aTTENDANCEDATE: String,
        @SerializedName("ATTENDANCE_STATUS")
        var aTTENDANCESTATUS: Int,
        @SerializedName("SCHOOL_ID")
        var sCHOOLID: Int,
        @SerializedName("STAFF_ATTENDANCE_ID")
        var sTAFFATTENDANCEID: Int,
        @SerializedName("STAFF_ATTENDANCE_STATUS")
        var sTAFFATTENDANCESTATUS: Int
    )
}