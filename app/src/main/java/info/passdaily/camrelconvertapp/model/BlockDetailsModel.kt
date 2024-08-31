package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class BlockDetailsModel(
    @SerializedName("BlockDetails")
    val blockDetails: ArrayList<BlockDetail>
){

    @Keep
    data class BlockDetail(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("BLOCK_DATE")
        val bLOCKDATE: String,
        @SerializedName("BLOCK_MESSAGE")
        val bLOCKMESSAGE: String,
        @SerializedName("BLOCK_STATUS")
        val bLOCKSTATUS: Int,
        @SerializedName("STUDENT_BLOCK_ID")
        val sTUDENTBLOCKID: Int,
        @SerializedName("STUDENT_ID")
        val sTUDENTID: Int
    )
}