package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class SubjectsModel(
    @SerializedName("Subjects")
    val subjects: List<Subject>
){
    @Keep
    data class Subject(
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("SUBJECT_STATUS")
        val sUBJECTSTATUS: Int
    )
}