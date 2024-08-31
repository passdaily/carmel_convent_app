package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class LeaveDetailsStaffModel(
    @SerializedName("LeaveDetails")
    val leaveDetails: List<LeaveDetail>
){
    @Keep
    data class LeaveDetail(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("LEAVE_APPROVED_BY")
        val lEAVEAPPROVEDBY: Any,
        @SerializedName("LEAVE_APPROVED_DATE")
        val lEAVEAPPROVEDDATE: Any,
        @SerializedName("LEAVE_DESCRIPTION")
        val lEAVEDESCRIPTION: String,
        @SerializedName("LEAVE_FROM_DATE")
        val lEAVEFROMDATE: String,
        @SerializedName("LEAVE_ID")
        val lEAVEID: Int,
        @SerializedName("LEAVE_STATUS")
        val lEAVESTATUS: Int,
        @SerializedName("LEAVE_SUBJECT")
        val lEAVESUBJECT: String,
        @SerializedName("LEAVE_SUBMITTED_DATE")
        val lEAVESUBMITTEDDATE: String,
        @SerializedName("LEAVE_TO_DATE")
        val lEAVETODATE: String,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_GUARDIAN_NAME")
        val sTUDENTGUARDIANNAME: String,
        @SerializedName("STUDENT_GUARDIAN_NUMBER")
        val sTUDENTGUARDIANNUMBER: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,
        var cardColor : Int,
        var textColor : Int,
        var stateName :String
    )
}