package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class LoginModel(
    @SerializedName("LOGIN_ROLE")
    val lOGINROLE: String,
    @SerializedName("PLOGIN_ID")
    val pLOGINID: Int,
    //SCHOOL_ID
    @SerializedName("SCHOOL_ID")
    val sCHOOLID: Int,
)