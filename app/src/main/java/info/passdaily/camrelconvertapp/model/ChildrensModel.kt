package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ChildrensModel(
    @SerializedName("Childrens")
    val childrens: List<Children>
){
    data class Children(
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CLASS_SECTION")
        val cLASSSECTION: String,
        @SerializedName("INBOX_COUNT")
        val iNBOXCOUNT: Any,
        @SerializedName("PLOGIN_ID")
        val pLOGINID: Int,
        @SerializedName("PLSTUDENT_ID")
        val pLSTUDENTID: Int,
        @SerializedName("STUDENT_FNAME")
        val sTUDENTFNAME: String,
        @SerializedName("STUDENT_GUARDIAN_NAME")
        val sTUDENTGUARDIANNAME: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_IMAGE")
        val sTUDENTIMAGE: String,
        @SerializedName("STUDENT_LNAME")
        val sTUDENTLNAME: String,
        @SerializedName("STUDENT_MNAME")
        val sTUDENTMNAME: String
    )
}