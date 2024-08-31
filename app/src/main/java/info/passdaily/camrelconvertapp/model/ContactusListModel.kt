package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ContactusListModel(
    @SerializedName("ContactusList")
    val contactusList: List<Contactus>
){
    @Keep
    data class Contactus(
        @SerializedName("CONTACT_US_ADDRESS")
        val cONTACTUSADDRESS: String,
        @SerializedName("CONTACT_US_DATE")
        val cONTACTUSDATE: Any,
        @SerializedName("CONTACT_US_EMAIL")
        val cONTACTUSEMAIL: String,
        @SerializedName("CONTACT_US_ID")
        val cONTACTUSID: Int,
        @SerializedName("CONTACT_US_PHONE")
        val cONTACTUSPHONE: String,
        @SerializedName("CONTACT_US_STATUS")
        val cONTACTUSSTATUS: Int,
        @SerializedName("LATTITUDE")
        val lATTITUDE: String,
        @SerializedName("LONGITUDE")
        val lONGITUDE: String
    )
}