package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class MaterialListStaffModel(
    @SerializedName("MaterialList")
    val materialList: List<Material>
){
    @Keep
    data class Material(                                 ///////"STUDY_METERIAL_ID": 2,
        @SerializedName("ACCADEMIC_ID")                                 ///////"ACCADEMIC_ID": 10,
        val aCCADEMICID: Int,                                 ///////"CLASS_ID": 26,
        @SerializedName("ACCADEMIC_TIME")                                 ///////"STUDY_METERIAL_TITLE": "History of computer",
        val aCCADEMICTIME: String?,                                 ///////"STUDY_METERIAL_DESCRIPTION": "History of computer",
        @SerializedName("ADMIN_ID")                                 ///////"STUDY_METERIAL_FILE": null,
        val aDMINID: String?,                                 ///////"STUDY_METERIAL_STATUS": 1,
        @SerializedName("CLASS_ID")                                 ///////"CLASS_NAME": "Test Class",
        val cLASSID: Int,                                 ///////"ADMIN_ID": null,
        @SerializedName("CLASS_NAME")                                 ///////"STUDY_METERIAL_DATE": "2023-08-23T16:48:13.383",
        val cLASSNAME: String?,                                 ///////"SUBJECT_ID": 1,
        @SerializedName("CREATED_BY")                                 ///////"ACCADEMIC_TIME": "2023-2024",
        val cREATEDBY: String?,                                 ///////"SUBJECT_NAME": "Computer",
        @SerializedName("STUDY_METERIAL_DATE")                                 ///////"CREATED_BY": "ANVER S",
        val sTUDYMETERIALDATE: String?,                                 ///////"TOTAL_FILE": 1,
        @SerializedName("STUDY_METERIAL_DESCRIPTION")                                 ///////"SCHOOL_ID": 1,
        val sTUDYMETERIALDESCRIPTION: String?,                                 ///////"SUBJECT_ICON": null
        @SerializedName("STUDY_METERIAL_FILE")                                 ///////
        val sTUDYMETERIALFILE: String?,                                 ///////
        @SerializedName("STUDY_METERIAL_ID")                                 ///////
        val sTUDYMETERIALID: Int,                                 ///////
        @SerializedName("STUDY_METERIAL_STATUS")
        val sTUDYMETERIALSTATUS: Int,
        @SerializedName("STUDY_METERIAL_TITLE")
        val sTUDYMETERIALTITLE: String?,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String?,
        @SerializedName("SCHOOL_ID")
        val sCHOOLID: Int,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String?,
        @SerializedName("TOTAL_FILE")
        val tOTALFILE: Int
    )
}