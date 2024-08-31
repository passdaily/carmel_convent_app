package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class StudyMaterialDetailModel(
    @SerializedName("FileList")
    val fileList: List<File>,
    @SerializedName("MaterialDetails")
    val materialDetails: List<MaterialDetail>
){
    @Keep
    data class File(
        @SerializedName("CLASS_ID")
        val cLASSID: Any,
        @SerializedName("FILE_ID")
        val fILEID: Int,
        @SerializedName("FILE_NAME")
        val fILENAME: String,
        @SerializedName("FILE_TITLE")
        val fILETITLE: String,
        @SerializedName("STUDY_METERIAL_ID")
        val sTUDYMETERIALID: Int
    )

    @Keep
    data class MaterialDetail(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String,
        @SerializedName("ADMIN_ID")
        val aDMINID: Int,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String,
        @SerializedName("CREATED_BY")
        val cREATEDBY: String,
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
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("TOTAL_FILE")
        val tOTALFILE: Any
    )
}