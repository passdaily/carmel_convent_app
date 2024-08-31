package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ExamDetailsModel(
    @SerializedName("ExamDetails")
    val examDetails: ArrayList<ExamDetail>
){
    @Keep
    data class ExamDetail(
        @SerializedName("EXAM_DESCRIPTION")
        val eXAMDESCRIPTION: String,
        @SerializedName("EXAM_ID")
        val eXAMID: Int,
        @SerializedName("EXAM_NAME")
        val eXAMNAME: String,
        @SerializedName("EXAM_STATUS")
        val eXAMSTATUS: Int
    )
}