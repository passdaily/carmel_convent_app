package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class MarkAbsentModel(
    @SerializedName("StudentDetails")
    val studentDetails: List<StudentDetail>
){

    @Keep
    data class StudentDetail(
        @SerializedName("ABSENTS")
        val aBSENTS: Int,
        @SerializedName("ATTENDANCE_ID")
        val aTTENDANCEID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_SECTION")
        val cLASSSECTION: String?,
        @SerializedName("SESSION_ID")
        val sESSIONID: String?,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String?,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_IMAGE")
        val sTUDENTIMAGE: String?,
        @SerializedName("STUDENT_LNAME")
        val sTUDENTLNAME: String?,
        @SerializedName("STUDENT_MNAME")
        val sTUDENTMNAME: String?,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: String?,
        @SerializedName("STUDENT_STATUS")
        val sTUDENTSTATUS: Int,

        var selectedValue : Boolean = false,
        var titleName : String,
        var titleMessage : String
    )
}