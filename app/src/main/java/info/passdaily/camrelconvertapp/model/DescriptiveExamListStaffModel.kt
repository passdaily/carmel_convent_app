package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class DescriptiveExamListStaffModel(
    @SerializedName("OnlineExamList")
    val desOnlineExamList: List<OnlineExam>
){
    @Keep
    data class OnlineExam(
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
        @SerializedName("CREATED_BY")
        val cREATEDBY: String,
        @SerializedName("CREATED_DATE")
        val cREATEDDATE: String,
        @SerializedName("END_TIME")
        val eNDTIME: String,
        @SerializedName("EXAM_DESCRIPTION")
        val eXAMDESCRIPTION: String,
        @SerializedName("EXAM_DURATION")
        val eXAMDURATION: Int,
        @SerializedName("EXAM_ID")
        val eXAMID: Int,
        @SerializedName("EXAM_KEY")
        val eXAMKEY: Any,
        @SerializedName("EXAM_NAME")
        val eXAMNAME: String,
        @SerializedName("MODIFIED_DATE")
        val mODIFIEDDATE: String,
        @SerializedName("START_TIME")
        val sTARTTIME: String,
        @SerializedName("STATUS")
        val sTATUS: Int,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String?,
        @SerializedName("TOTAL_QUESTION")
        val tOTALQUESTION: Int,
        @SerializedName("TOTAL_QUESTION_MARK")
        val tOTALQUESTIONMARK: Any
    )
}