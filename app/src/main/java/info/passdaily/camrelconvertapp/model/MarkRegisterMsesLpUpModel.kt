package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class MarkRegisterMsesLpUpModel(
    @SerializedName("Marks")
    val marksList: List<Mark>
){
    @Keep
    data class Mark(
        @SerializedName("ACCADEMIC_ID")           ///STUDENT_ID: 533,
        val aCCADEMICID: Int,                           ///STUDENT_ROLL_NUMBER: 41,
        @SerializedName("ACCADEMIC_TIME")          ///STUDENT_FNAME: "ABHILASH O K ",
        val aCCADEMICTIME: String,                      ///CLASS_ID: 1,
        @SerializedName("CLASS_ID")               ///CLASS_NAME: "X-A",
        val cLASSID: Int,                               ///ACCADEMIC_ID: 8,
        @SerializedName("CLASS_NAME")             ///ACCADEMIC_TIME: "2021-2022",
        val cLASSNAME: String,                           ///PASS_MARK_THEORY: "N",
        @SerializedName("EXAM_ID")                  ///OUTOFF_MARK_THEORY: "N",
        val eXAMID: Int,                                  ///PASS_MARK_INTERNAL: "N",
        @SerializedName("GRADE_CE")                 ///OUTOFF_MARK_INTERNAL: "N",
        var gRADECE: String?,                            ///TOTAL_MARK_THEORY: "N",
        @SerializedName("GRADE_TE")                 ///TOTAL_MARK_INTERNAL: "N",
        var gRADETE: String?,                           ///TOTAL_GRADE: "",
        @SerializedName("OUTOFF_MARK_INTERNAL")      ///EXAM_ID: 0,
        val oUTOFFMARKINTERNAL: String,                  ///GRADE_TE: null,
        @SerializedName("OUTOFF_MARK_THEORY")        ///GRADE_CE: null,
        val oUTOFFMARKTHEORY: String,
        @SerializedName("PASS_MARK_INTERNAL")
        val pASSMARKINTERNAL: String,
        @SerializedName("PASS_MARK_THEORY")
        val pASSMARKTHEORY: String,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,
        @SerializedName("TOTAL_GRADE")
        val tOTALGRADE: String,
        @SerializedName("TOTAL_MARK_INTERNAL")
        var tOTALMARKINTERNAL: String,
        @SerializedName("TOTAL_MARK_THEORY")
        var tOTALMARKTHEORY: String,

        var updated : Boolean = false
    )
}