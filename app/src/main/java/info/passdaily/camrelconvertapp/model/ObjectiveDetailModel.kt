package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ObjectiveDetailModel(
    @SerializedName("OnlineExamList")
    val objectiveModel: List<ObjectiveModel>
){
    @Keep
    data class ObjectiveModel(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("ALLOWED_PAUSE")
        val aLLOWEDPAUSE: Int,
        @SerializedName("ANSWERED_QUESTIONS")
        val aNSWEREDQUESTIONS: Int,
        @SerializedName("ATTEMPTED_ON")
        val aTTEMPTEDON: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("END_TIME")
        val eNDTIME: String,
        @SerializedName("IS_AUTO_ENDED")
        val iSAUTOENDED: Int,
        @SerializedName("IS_SUBMITTED")
        val iSSUBMITTED: Int,
        @SerializedName("MODIFIED_DATE")
        val mODIFIEDDATE: String,
        @SerializedName("OEXAM_ATTEMPT_ID")
        val oEXAMATTEMPTID: Int,
        @SerializedName("OEXAM_DESCRIPTION")
        val oEXAMDESCRIPTION: String,
        @SerializedName("OEXAM_DURATION")
        val oEXAMDURATION: Long,
        @SerializedName("OEXAM_ID")
        val oEXAMID: Int,
        @SerializedName("OEXAM_KEY")
        val oEXAMKEY: String,
        @SerializedName("OEXAM_NAME")
        val oEXAMNAME: String,
        @SerializedName("PAUSED_COUNT")
        val pAUSEDCOUNT: Int,
        @SerializedName("START_TIME")
        val sTARTTIME: String,
        @SerializedName("STATUS")
        val sTATUS: Int,
        @SerializedName("STUDENT_NAME")
        val sTUDENTNAME: String,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("TIME_NOW")
        val tIMENOW: String,
        @SerializedName("TOTAL_QUESTION")
        val tOTALQUESTION: Int
    )
}