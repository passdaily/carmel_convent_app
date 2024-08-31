package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ConveyorModel(
    @SerializedName("Conveyor")
    val conveyor: List<Conveyor>
){
    @Keep
    data class Conveyor(
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CONVEYORS_ADDRESS")
        val cONVEYORSADDRESS: String,
        @SerializedName("CONVEYORS_DATE")
        val cONVEYORSDATE: String,
        @SerializedName("CONVEYORS_ID")
        val cONVEYORSID: Int,
        @SerializedName("CONVEYORS_MOBILE")
        val cONVEYORSMOBILE: String,
        @SerializedName("CONVEYORS_NAME")
        val cONVEYORSNAME: String,
        @SerializedName("CONVEYORS_STATUS")
        val cONVEYORSSTATUS: Int,
        @SerializedName("CONVEYORS_VEHICLE_NO")
        val cONVEYORSVEHICLENO: String,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int
    )
}