package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class VehichlesModel(
    @SerializedName("Vehichles")
    val vehichles: ArrayList<Vehichle>
){
    @Keep
    data class Vehichle(
        @SerializedName("DRIVER_MOBILE")
        val dRIVERMOBILE: String?,
        @SerializedName("DRIVER_NAME")
        val dRIVERNAME: String?,
        @SerializedName("EXE_NAME")
        val eXENAME: String?,
        @SerializedName("IP_ADDRESS")
        val iPADDRESS: String?,
        @SerializedName("PORT_NUMBER")
        val pORTNUMBER: String?,
        @SerializedName("SIM_NUMBER")
        val sIMNUMBER: String?,
        @SerializedName("TERMINAL_ID")
        val tERMINALID: String?,
        @SerializedName("VEHICLE_ID")
        val vEHICLEID: Int,
        @SerializedName("VEHICLE_NAME")
        val vEHICLENAME: String?,
        @SerializedName("VEHICLE_REG_NO")
        val vEHICLEREGNO: String?,
        @SerializedName("VEHICLE_ROOT")
        val vEHICLEROOT: String?,
        @SerializedName("VEHICLE_STATUS")
        val vEHICLESTATUS: Int
    )
}