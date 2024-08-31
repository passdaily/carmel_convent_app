package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class StudentListModel(
    @SerializedName("ParentList")
    val parentList: List<Parent>
){

    @Keep
    data class Parent(
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ADMISSION_NUMBER")
        val aDMISSIONNUMBER: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_GUARDIAN_NAME")
        val sTUDENTGUARDIANNAME: String,
        @SerializedName("STUDENT_GUARDIAN_NUMBER")
        val sTUDENTGUARDIANNUMBER: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int,

        var isChecked : Boolean =  false
    )
}