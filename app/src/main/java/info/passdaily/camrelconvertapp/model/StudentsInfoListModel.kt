package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class StudentsInfoListModel(
    @SerializedName("StudentsList")
    var studentsList: List<Students>
){
    @Keep
    data class Students(
        @SerializedName("ADMISSION_DATE")
        var aDMISSIONDATE: String?,
        @SerializedName("ADMISSION_NUMBER")
        var aDMISSIONNUMBER: String?,
        @SerializedName("CLASS_DESCRIPTION")
        var cLASSDESCRIPTION: Any,
        @SerializedName("CLASS_ID")
        var cLASSID: Int,
        @SerializedName("CLASS_NAME")
        var cLASSNAME: String?,
        @SerializedName("CLASS_SECTION")
        var cLASSSECTION: String?,
        @SerializedName("SESSION_ID")
        var sESSIONID: Any,
        @SerializedName("STUD_ACCADEMIC_ID")
        var sTUDACCADEMICID: Int,
        @SerializedName("STUDENT_BIRTHPLACE")
        var sTUDENTBIRTHPLACE: String?,
        @SerializedName("STUDENT_BLOODGROUP")
        var sTUDENTBLOODGROUP: String?,
        @SerializedName("STUDENT_CADDRESS")
        var sTUDENTCADDRESS: String?,
        @SerializedName("STUDENT_CASTE")
        var sTUDENTCASTE: String?,
        @SerializedName("STUDENT_CAST_TYPE")
        var sTUDENTCASTTYPE: String?,
        @SerializedName("STUDENT_DOB")
        var sTUDENTDOB: String?,
        @SerializedName("STUDENT_EMAIL_ID")
        var sTUDENTEMAILID: String?,
        @SerializedName("STUDENT_FATHER_NAME")
        var sTUDENTFATHERNAME: String?,
        @SerializedName("STUDENT_FATHER_OCCUPATION")
        var sTUDENTFATHEROCCUPATION: String?,
        @SerializedName("STUDENT_FATHER_QUALIFICATION")
        var sTUDENTFATHERQUALIFICATION: String?,
        @SerializedName("STUDENT_FNAME")
        var sTUDENTFNAME: String?,
        @SerializedName("STUDENT_GENDER")
        var sTUDENTGENDER: Int,
        @SerializedName("STUDENT_GUARDIAN_NAME")
        var sTUDENTGUARDIANNAME: String?,
        @SerializedName("STUDENT_GUARDIAN_NUMBER")
        var sTUDENTGUARDIANNUMBER: String?,
        @SerializedName("STUDENT_ID")
        var sTUDENTID: Int,
        @SerializedName("STUDENT_IMAGE")
        var sTUDENTIMAGE: String?,
        @SerializedName("STUDENT_LAST_STUDIED")
        var sTUDENTLASTSTUDIED: String?,
        @SerializedName("STUDENT_LNAME")
        var sTUDENTLNAME: String?,
        @SerializedName("STUDENT_MNAME")
        var sTUDENTMNAME: String?,
        @SerializedName("STUDENT_MOTHER_NAME")
        var sTUDENTMOTHERNAME: String?,
        @SerializedName("STUDENT_MOTHER_OCCUPATION")
        var sTUDENTMOTHEROCCUPATION: String?,
        @SerializedName("STUDENT_MOTHER_QUALIFICATION")
        var sTUDENTMOTHERQUALIFICATION: String?,
        @SerializedName("STUDENT_MOTHERTONGUE")
        var sTUDENTMOTHERTONGUE: String?,
        @SerializedName("STUDENT_NATIONALITY")
        var sTUDENTNATIONALITY: String?,
        @SerializedName("STUDENT_PADDRESS")
        var sTUDENTPADDRESS: String?,
        //
        @SerializedName("STUDENT_PHONE_NUMBER")
        var sTUDENTPHONENUMBER: String?,
        @SerializedName("STUDENT_RELIGION")
        var sTUDENTRELIGION: String?,
        @SerializedName("STUDENT_ROLL_NUMBER")
        var sTUDENTROLLNUMBER: String?,
        @SerializedName("STUDENT_STATUS")
        var sTUDENTSTATUS: Int,
        @SerializedName("STUDENT_BIRTHPLACE_TALUK")
        var sTUDENTBIRTHPLACETALUK: String?,
        @SerializedName("STUDENT_BIRTHPLACE_DISTRICT")
        var sTUDENTBIRTHPLACEDISTRICT: String?,
        @SerializedName("STUDENT_BIRTHPLACE_STATE")
        var sTUDENTBIRTHPLACESTATE: String?,
        @SerializedName("STUDENT_SSLC_REG_NO")
        var sTUDENTSSLCREGNO: String?,
        @SerializedName("STUDENT_AADHAR_NUMBER")
        var sTUDENTAADHARNUMBER: String?,


        //  "STUDENT_BIRTHPLACE_TALUK": "",
        //            "STUDENT_BIRTHPLACE_DISTRICT": "",
        //            "STUDENT_BIRTHPLACE_STATE": "",
        //            "STUDENT_SSLC_REG_NO": "",
        //            "STUDENT_AADHAR_NUMBER": ""

        var updated : Boolean = false
    )
}