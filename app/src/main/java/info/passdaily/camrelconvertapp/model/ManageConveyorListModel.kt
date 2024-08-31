package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ManageConveyorListModel(
    @SerializedName("ConveyorList")
    var conveyorList: List<Conveyor>
){
    @Keep
    data class Conveyor(
        @SerializedName("CLASS_ID")
        var cLASSID: Int,
        @SerializedName("CLASS_NAME")
        var cLASSNAME: String,
        @SerializedName("CONVEYORS_DATE")
        var cONVEYORSDATE: String,
        @SerializedName("CONVEYORS_ID")
        var cONVEYORSID: Int,
        @SerializedName("CONVEYORS_MOBILE")
        var cONVEYORSMOBILE: String,
        @SerializedName("CONVEYORS_NAME")
        var cONVEYORSNAME: String,
        @SerializedName("CONVEYORS_VEHICLE_NO")
        var cONVEYORSVEHICLENO: String,
        @SerializedName("STUDENT_FNAME")
        var sTUDENTFNAME: String,
        @SerializedName("STUDENT_ID")
        var sTUDENTID: Int,
        @SerializedName("STUDENT_LNAME")
        var sTUDENTLNAME: String,
        var updated : Boolean = false
    )
}