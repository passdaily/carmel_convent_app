package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class StudyMaterialModel(
    @SerializedName("StudyMaterials")
    val studyMaterials: List<StudyMaterial>
){
    @Keep
    data class StudyMaterial(
        @SerializedName("ACCADEMIC_ID")
        val aCCADEMICID: Int,
        @SerializedName("ACCADEMIC_TIME")
        val aCCADEMICTIME: String?,
        @SerializedName("CLASS_ID")
        val cLASSID: Int,
        @SerializedName("CLASS_NAME")
        val cLASSNAME: String?,
        @SerializedName("STUDY_METERIAL_DATE")
        val sTUDYMETERIALDATE: String?,
        @SerializedName("STUDY_METERIAL_DESCRIPTION")
        val sTUDYMETERIALDESCRIPTION: String?,
        @SerializedName("STUDY_METERIAL_FILE")
        val sTUDYMETERIALFILE: String?,
        @SerializedName("STUDY_METERIAL_ID")
        val sTUDYMETERIALID: Int,
        @SerializedName("STUDY_METERIAL_STATUS")
        val sTUDYMETERIALSTATUS: Int,
        @SerializedName("STUDY_METERIAL_TITLE")
        val sTUDYMETERIALTITLE: String?,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String?,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String?,
        @SerializedName("TOTAL_FILE")
        val tOTALFILE: Int
    )

    //STUDY_METERIAL_ID: 69,
    //ACCADEMIC_ID: 8,
    //CLASS_ID: 1,
    //STUDY_METERIAL_TITLE: "Neural control",
    //STUDY_METERIAL_DESCRIPTION: "coordination",
    //STUDY_METERIAL_FILE: null,
    //STUDY_METERIAL_STATUS: 1,
    //CLASS_NAME: "X-A",
    //ADMIN_ID: null,
    //STUDY_METERIAL_DATE: "2022-05-31T12:54:42.127",
    //SUBJECT_ID: 9,
    //ACCADEMIC_TIME: "2021-2022",
    //SUBJECT_NAME: "Biology",
    //CREATED_BY: "GAFOOR",
    //TOTAL_FILE: 0,
}