package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ObjExamListStaffModel(
    @SerializedName("OnlineExamList")
    val onlineExamList: List<OnlineExam>
){
    @Keep
    data class OnlineExam(                                   //"OEXAM_ID": 38,
        @SerializedName("ACCADEMIC_ID")                                   //"OEXAM_KEY": null,
        val aCCADEMICID: Int,                                   //"ADMIN_ID": 2,
        @SerializedName("ACCADEMIC_TIME")                                   //"ACCADEMIC_ID": 10,
        val aCCADEMICTIME: String,                                   //"CLASS_ID": 21,
        @SerializedName("ADMIN_ID")                                   //"SUBJECT_ID": 11,
        val aDMINID: Int,                                   //"OEXAM_NAME": "TEST ONLINE EXAM",
        @SerializedName("ALLOWED_PAUSE")                                   //"OEXAM_DESCRIPTION": "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
        val aLLOWEDPAUSE: Int,                                   //"OEXAM_DURATION": 30,
        @SerializedName("CLASS_ID")                                   //"START_TIME": "2023-08-24T10:40:00",
        val cLASSID: Int,                                   //"END_TIME": "2024-03-31T12:10:00",
        @SerializedName("CLASS_NAME")                                   //"CREATED_DATE": "2023-08-25T15:17:13.447",
        val cLASSNAME: String,                                   //"MODIFIED_DATE": null,
        @SerializedName("CREATED_DATE")                                   //"ALLOWED_PAUSE": 5,
        val cREATEDDATE: String,                                   //"STATUS": 0,
        @SerializedName("END_TIME")                                   //"CLASS_NAME": "V-A",
        val eNDTIME: String,                                   //"SUBJECT_NAME": "COMMUNICATIVE ENGLISH",
        @SerializedName("MODIFIED_DATE")                                   //"ACCADEMIC_TIME": "2023-2024",
        val mODIFIEDDATE: Any,                                   //"SUBJECT_ICON": "English.png"
        @SerializedName("OEXAM_DESCRIPTION")
        val oEXAMDESCRIPTION: String,
        @SerializedName("OEXAM_DURATION")
        val oEXAMDURATION: Int,
        @SerializedName("OEXAM_ID")
        val oEXAMID: Int,
        @SerializedName("OEXAM_KEY")
        val oEXAMKEY: Any,
        @SerializedName("OEXAM_NAME")
        val oEXAMNAME: String,
        @SerializedName("START_TIME")
        val sTARTTIME: String,
        @SerializedName("STATUS")
        val sTATUS: Int,
        @SerializedName("SUBJECT_ID")
        val sUBJECTID: Int,
        @SerializedName("SUBJECT_NAME")
        val sUBJECTNAME: String,
        @SerializedName("SUBJECT_ICON")
        val sUBJECTICON: String?
    )
}