package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ProgressMarkDetailsModel(
    @SerializedName("MarkDetails")
    val markDetails: ArrayList<MarkDetail>
){
    @Keep
    data class MarkDetail(
        @SerializedName("ACCADEMIC_ID")                              ////"MARK_ID": 2,
        val aCCADEMICID: Int,                              ////"ACCADEMIC_ID": 10,
        @SerializedName("CLASS_ID")                              ////"CLASS_ID": 26,
        val cLASSID: Int,                              ////"EXAM_ID": 1,
        @SerializedName("EXAM_ID")                              ////"SUBJECT_ID": 1,
        val eXAMID: Int,                              ////"STUDENT_ROLL_NUMBER": 3,
        @SerializedName("MARK_DATE")                              ////"PASS_MARK": "35.00",
        val mARKDATE: String?,                              ////"OUTOFF_MARK": "100.00",
        @SerializedName("MARKED_BY")                              ////"TOTAL_MARK": "100.00",
        val mARKEDBY: Int,                              ////"MARK_DATE": "2023-07-22T12:21:45.49",
        @SerializedName("MARK_GRADE")                              ////"MARKED_BY": 1,
        val mARKGRADE: String?,                              ////"MARK_GRADE": "A+",
        @SerializedName("MARK_ID")                              ////"SUBJECT_TOTALMARK": 0,
        val mARKID: Int,                              ////"SUBJECT_PASSMARK": 0,
        @SerializedName("OUTOFF_MARK")                              ////"SUBJECT_NAME": "Computer",
        val oUTOFFMARK: Int,                              ////"SUBJECT_ICON": "Computer.png"
        @SerializedName("PASS_MARK")                              ////
        val pASSMARK: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String?,
        @SerializedName("SUBJECT_PASSMARK")
        val sUBJECTPASSMARK: Int,
        @SerializedName("SUBJECT_TOTALMARK")
        val sUBJECTTOTALMARK: Int,
        @SerializedName("TOTAL_MARK")
        val tOTALMARK: Int,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String?,
        //
    )
}