package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ZakAccessTokenModel(
    @SerializedName("token")
    var token: String
)