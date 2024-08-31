package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ZoomSdkUserModel(
    @SerializedName("account_id")
    var accountId: String,
    @SerializedName("account_number")
    var accountNumber: Long,
    @SerializedName("cluster")
    var cluster: String,
    @SerializedName("cms_user_id")
    var cmsUserId: String,
    @SerializedName("created_at")
    var createdAt: String,
    @SerializedName("dept")
    var dept: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("first_name")
    var firstName: String,
    @SerializedName("group_ids")
    var groupIds: List<Any>,
    @SerializedName("id")
    var id: String,
    @SerializedName("im_group_ids")
    var imGroupIds: List<Any>,
    @SerializedName("jid")
    var jid: String,
    @SerializedName("job_title")
    var jobTitle: String,
    @SerializedName("language")
    var language: String,
    @SerializedName("last_client_version")
    var lastClientVersion: String,
    @SerializedName("last_login_time")
    var lastLoginTime: String,
    @SerializedName("last_name")
    var lastName: String,
    @SerializedName("location")
    var location: String,
    @SerializedName("login_types")
    var loginTypes: List<Int>,
    @SerializedName("personal_meeting_url")
    var personalMeetingUrl: String,
    @SerializedName("phone_country")
    var phoneCountry: String,
    @SerializedName("phone_number")
    var phoneNumber: String,
    @SerializedName("pmi")
    var pmi: Long,
    @SerializedName("role_id")
    var roleId: String,
    @SerializedName("role_name")
    var roleName: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("timezone")
    var timezone: String,
    @SerializedName("type")
    var type: Int,
    @SerializedName("use_pmi")
    var usePmi: Boolean,
    @SerializedName("user_created_at")
    var userCreatedAt: String,
    @SerializedName("verified")
    var verified: Int
)