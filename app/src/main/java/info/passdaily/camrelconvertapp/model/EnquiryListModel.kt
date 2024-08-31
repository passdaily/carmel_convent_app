package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class EnquiryListModel(
    @SerializedName("EnquiryList")
    val enquiryList: List<Enquiry>
){
    @Keep
    data class Enquiry(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("QUERY_DESCRIPTION")
        val qUERYDESCRIPTION: String,
        @SerializedName("QUERY_ID")
        val qUERYID: Int,
        @SerializedName("QUERY_REPLY")
        val qUERYREPLY: String,
        @SerializedName("QUERY_REPLYED_BY")
        val qUERYREPLYEDBY: Int,
        @SerializedName("QUERY_REPLYED_DATE")
        val qUERYREPLYEDDATE: String,
        @SerializedName("QUERY_STATUS")
        val qUERYSTATUS: Int,
        @SerializedName("QUERY_SUBJECT")
        val qUERYSUBJECT: String,
        @SerializedName("QUERY_SUBMITTED_DATE")
        val qUERYSUBMITTEDDATE: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int,
        @SerializedName("STUDENT_ROLL_NUMBER")
        val sTUDENTROLLNUMBER: Int
    )
}