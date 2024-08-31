package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class GetYearClassExamModel(
    @SerializedName("Class")
    val classList: List<Class>,
    @SerializedName("Exams")
    val exams: List<Exam>,
    @SerializedName("Years")
    val years: List<Year>
){

    @Keep
    data class Exam(
        @SerializedName("EXAM_DESCRIPTION")
        val eXAMDESCRIPTION: String,
        @SerializedName("EXAM_ID")
        val eXAMID: Int,
        @SerializedName("EXAM_NAME")
        val eXAMNAME: String,
        @SerializedName("EXAM_STATUS")
        val eXAMSTATUS: Int
    )

    @Keep
    data class Class(
        @SerializedName("CLASS_DESCRIPTION")
        val cLASSDESCRIPTION: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CLASS_STATUS")
        val cLASSSTATUS: Int
    )

    @Keep
    data class Year(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_STATUS")
        val aCCADEMICSTATUS: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String
    )
}