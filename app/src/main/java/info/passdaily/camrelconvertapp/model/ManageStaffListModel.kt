package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ManageStaffListModel(
    @SerializedName("StaffList")
    var staffList: List<Staff>
){
    @Keep
    data class Staff(
        @SerializedName("STAFF_BIRTHPLACE")
        var sTAFFBIRTHPLACE: String,
        @SerializedName("STAFF_BLOODGROUP")
        var sTAFFBLOODGROUP: String,
        @SerializedName("STAFF_CADDRESS")
        var sTAFFCADDRESS: String,
        @SerializedName("STAFF_CASTE")
        var sTAFFCASTE: String,
        @SerializedName("STAFF_CAST_TYPE")
        var sTAFFCASTTYPE: String,
        @SerializedName("STAFF_CATEGORY")
        var sTAFFCATEGORY: Int,
        @SerializedName("STAFF_CODE")
        var sTAFFCODE: String,
        @SerializedName("STAFF_DOB")
        var sTAFFDOB: String,
        @SerializedName("STAFF_EMAIL_ID")
        var sTAFFEMAILID: String,
        @SerializedName("STAFF_FATHER_NAME")
        var sTAFFFATHERNAME: String,
        @SerializedName("STAFF_FATHER_OCCUPATION")
        var sTAFFFATHEROCCUPATION: String,
        @SerializedName("STAFF_FNAME")
        var sTAFFFNAME: String,
        @SerializedName("STAFF_GENDER")
        var sTAFFGENDER: Int,
        @SerializedName("STAFF_ID")
        var sTAFFID: Int,
        @SerializedName("STAFF_IMAGE")
        var sTAFFIMAGE: String,
        @SerializedName("STAFF_JOB_DESCRIPTION")
        var sTAFFJOBDESCRIPTION: String,
        @SerializedName("STAFF_JOB_EXPERIENCE")
        var sTAFFJOBEXPERIENCE: String,
        @SerializedName("STAFF_JOB_ROLE")
        var sTAFFJOBROLE: String,
        @SerializedName("STAFF_JOINDATE")
        var sTAFFJOINDATE: String,
        @SerializedName("STAFF_LNAME")
        var sTAFFLNAME: String,
        @SerializedName("STAFF_MNAME")
        var sTAFFMNAME: String,
        @SerializedName("STAFF_MOTHER_NAME")
        var sTAFFMOTHERNAME: String,
        @SerializedName("STAFF_MOTHERTONGUE")
        var sTAFFMOTHERTONGUE: String,
        @SerializedName("STAFF_NATIONALITY")
        var sTAFFNATIONALITY: String,
        @SerializedName("STAFF_PADDRESS")
        var sTAFFPADDRESS: String,
        @SerializedName("STAFF_PHONE_NUMBER")
        var sTAFFPHONENUMBER: String,
        @SerializedName("STAFF_RELIGION")
        var sTAFFRELIGION: String,
        @SerializedName("STAFF_STATUS")
        var sTAFFSTATUS: Int,

        var updated : Boolean = false
    )
}