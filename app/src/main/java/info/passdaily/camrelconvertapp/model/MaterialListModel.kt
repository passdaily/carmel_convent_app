package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class MaterialListModel(
    @SerializedName("FilesDetails")
    val filesDetails: ArrayList<FilesDetail>,
    @SerializedName("StudyMaterialDetails")
    val studyMaterialDetails: List<StudyMaterialDetail>
){
    @Keep
    data class StudyMaterialDetail(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("STUDY_METERIAL_DATE")
        val sTUDYMETERIALDATE: String,
        @SerializedName("STUDY_METERIAL_DESCRIPTION")
        val sTUDYMETERIALDESCRIPTION: String,
        @SerializedName("STUDY_METERIAL_FILE")
        val sTUDYMETERIALFILE: Any,
        @SerializedName("STUDY_METERIAL_ID")
        val sTUDYMETERIALID: Int,
        @SerializedName("STUDY_METERIAL_STATUS")
        val sTUDYMETERIALSTATUS: Int,
        @SerializedName("STUDY_METERIAL_TITLE")
        val sTUDYMETERIALTITLE: String,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: Any,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("TOTAL_FILE")
        val tOTALFILE: Any
    )

    @Keep
    data class FilesDetail(
        @SerializedName("FILE_ID")
        val fILEID: Int,
        @SerializedName("FILE_NAME")
        val fILENAME: String,
        @SerializedName("FILE_TITLE")
        val fILETITLE: String,
        @SerializedName("STUDY_METERIAL_ID")
        val sTUDYMETERIALID: Int
    )
}