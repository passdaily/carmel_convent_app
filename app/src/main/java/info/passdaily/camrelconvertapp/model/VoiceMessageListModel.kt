package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class VoiceMessageListModel(
    @SerializedName("VoiceList")
    val voiceList: List<Voice>
){
    @Keep
    data class Voice(
        @SerializedName("TEMPLATE_ID")
        val tEMPLATEID: String,
        @SerializedName("VOICE_MAIL_CREATED_BY")
        val vOICEMAILCREATEDBY: Int,
        @SerializedName("VOICE_MAIL_DATE")
        val vOICEMAILDATE: String,
        @SerializedName("VOICE_MAIL_FILE")
        val vOICEMAILFILE: String,
        @SerializedName("VOICE_MAIL_ID")
        val vOICEMAILID: Int,
        @SerializedName("VOICE_MAIL_STATUS")
        val vOICEMAILSTATUS: Int,
        @SerializedName("VOICE_MAIL_TITLE")
        val vOICEMAILTITLE: String
    )
}