package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ExamTopperResponseModel(
    @SerializedName("ExamTopper")
    val examTopper: List<ExamTopper>
) {
    @Keep
    data class ExamTopper(
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String?,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,
        @SerializedName("TOTAL_MARK")
        val tOTALMARK: String?,
        @SerializedName("TOTAL_OUT_MARK")
        val tOTALOUTMARK: String?,
        @SerializedName("TOTAL_PERCENTAGE")
        val tOTALPERCENTAGE: String?
    )
}