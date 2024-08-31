package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ConveyorsListModel(
    @SerializedName("ConveyorsList")
    val conveyorsList: List<Conveyors>
){
    @Keep
    data class Conveyors(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
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
        val sTUDENTID: Int,

        var isChecked :  Boolean = false
    )
}