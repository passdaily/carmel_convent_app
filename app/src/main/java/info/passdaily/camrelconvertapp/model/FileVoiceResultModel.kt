package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class FileVoiceResultModel(
    @SerializedName("RESULT")
    val rESULT: String,
    @SerializedName("VOICE_MAIL_FILE")
    val vOICEMAILFILE: String,
    @SerializedName("TEMPLATE_ID")
    val tEMPLATEID: String,

)
/// "RESULT": "SUCCESS",
////                                "VOICE_MAIL_FILE": "042d6f03-53c1-4aec-809f-47729f4fd621A3974F345EE938CEC09EB76A169EEE11.mp3",
////                                "TEMPLATE_ID": "2019121913120912304179268100.mp3"