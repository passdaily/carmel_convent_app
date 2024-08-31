package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class GpsLocationModel(
    @SerializedName("GpsLocation")
    val gpsLocation: ArrayList<GpsLocation>
){
    @Keep
    data class GpsLocation(
        @SerializedName("ACC")
        val aCC: String,
        @SerializedName("ACC2")
        val aCC2: String,
        @SerializedName("CELLID")
        val cELLID: String,
        @SerializedName("LAC")
        val lAC: String,
        @SerializedName("LATITUDE")
        val lATITUDE: String,
        @SerializedName("LOCATION_DATE")
        val lOCATIONDATE: String,
        @SerializedName("LOCATION_ID")
        val lOCATIONID: Int,
        @SerializedName("LONGITUDE")
        val lONGITUDE: String,
        @SerializedName("MCC")
        val mCC: String,
        @SerializedName("MNC")
        val mNC: String,
        @SerializedName("ROTATION")
        val rOTATION: String,
        @SerializedName("SIM_NUMBER")
        val sIMNUMBER: String,
        @SerializedName("SPEED")
        val sPEED: String,
        @SerializedName("TERMINAL_ID")
        val tERMINALID: String,
        @SerializedName("TRACK_DATE")
        val tRACKDATE: String
    )
}