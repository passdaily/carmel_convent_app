package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ObjExamDetailsStaffModel(
    @SerializedName("OnlineExamAttendees")
    val onlineExamAttendees: List<OnlineExamAttendee>,
    @SerializedName("OnlineExamDetails")
    val onlineExamDetails: List<OnlineExamDetail>
) {
    @Keep
    data class OnlineExamAttendee(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ANSWERED_QUESTIONS")
        val aNSWEREDQUESTIONS: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CORRECT_ANSWER_COUNT")
        val cORRECTANSWERCOUNT: Int,
        @SerializedName("ELAPSED_TIME")
        val eLAPSEDTIME: Int,
        @SerializedName("END_TIME")
        val eNDTIME: String,
        @SerializedName("IS_AUTO_ENDED")
        val iSAUTOENDED: Int,
        @SerializedName("IS_PAUSED")
        val iSPAUSED: Int,
        @SerializedName("IS_SUBMITTED")
        val iSSUBMITTED: Int,
        @SerializedName("OEXAM_ATTEMPT_ID")
        val oEXAMATTEMPTID: Int,
        @SerializedName("OEXAM_DESCRIPTION")
        val oEXAMDESCRIPTION: String,
        @SerializedName("OEXAM_DURATION")
        val oEXAMDURATION: Int,
        @SerializedName("OEXAM_ID")
        val oEXAMID: Int,
        @SerializedName("OEXAM_NAME")
        val oEXAMNAME: String,
        @SerializedName("PAUSED_COUNT")
        val pAUSEDCOUNT: Int,
        @SerializedName("START_TIME")
        val sTARTTIME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_NAME")
        val sTUDENTNAME: String,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("TOTAL_QUESTIONS")
        val tOTALQUESTIONS: Int
    )

    @Keep
    data class OnlineExamDetail(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("ALLOWED_PAUSE")
        val aLLOWEDPAUSE: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("END_TIME")
        val eNDTIME: String,
        @SerializedName("MODIFIED_DATE")
        val mODIFIEDDATE: String,
        @SerializedName("OEXAM_DESCRIPTION")
        val oEXAMDESCRIPTION: String,
        @SerializedName("OEXAM_DURATION")
        val oEXAMDURATION: Int,
        @SerializedName("OEXAM_ID")
        val oEXAMID: Int,
        @SerializedName("OEXAM_KEY")
        val oEXAMKEY: Any,
        @SerializedName("OEXAM_NAME")
        val oEXAMNAME: String,
        @SerializedName("START_TIME")
        val sTARTTIME: String,
        @SerializedName("STATUS")
        val sTATUS: Int,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String
    )
}