package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class RemarkRegisterListModel(
    @SerializedName("RemarksList")
    val remarksList: List<Remarks>
){
    @Keep
    data class Remarks(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ADMIN_ID")
        val aDMINID: Any,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("EXAM_ID")
        val eXAMID: Int,
        @SerializedName("MARK_REMARK_ID")
        val mARKREMARKID: Int,
        @SerializedName("REMARK_COLOUMN_1")
        var rEMARKCOLOUMN1: String,
        @SerializedName("REMARK_COLOUMN_2")
        var rEMARKCOLOUMN2: String,
        @SerializedName("REMARK_COLOUMN_3")
        var rEMARKCOLOUMN3: String,
        @SerializedName("REMARK_COLOUMN_4")
        var rEMARKCOLOUMN4: String,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,

        var update : Boolean = false
    )
}