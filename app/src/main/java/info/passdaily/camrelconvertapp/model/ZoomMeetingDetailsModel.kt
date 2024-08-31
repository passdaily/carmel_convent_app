package info.passdaily.camrelconvertapp.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ZoomMeetingDetailsModel(
    @SerializedName("agenda")
    var agenda: String,
    @SerializedName("assistant_id")
    var assistantId: String,
    @SerializedName("created_at")
    var createdAt: String,
    @SerializedName("encrypted_password")
    var encryptedPassword: String,
    @SerializedName("h323_password")
    var h323Password: String,
    @SerializedName("host_email")
    var hostEmail: String,
    @SerializedName("host_id")
    var hostId: String,
    @SerializedName("id")
    var id: Long,
    @SerializedName("join_url")
    var joinUrl: String,
    @SerializedName("password")
    var password: String,
    @SerializedName("pre_schedule")
    var preSchedule: Boolean,
    @SerializedName("pstn_password")
    var pstnPassword: String,
    @SerializedName("settings")
    var settings: Settings,
    @SerializedName("start_url")
    var startUrl: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("timezone")
    var timezone: String,
    @SerializedName("topic")
    var topic: String,
    @SerializedName("type")
    var type: Int,
    @SerializedName("uuid")
    var uuid: String
) {
    @Keep
    data class Settings(
        @SerializedName("allow_multiple_devices")
        var allowMultipleDevices: Boolean,
        @SerializedName("alternative_host_update_polls")
        var alternativeHostUpdatePolls: Boolean,
        @SerializedName("alternative_hosts")
        var alternativeHosts: String,
        @SerializedName("alternative_hosts_email_notification")
        var alternativeHostsEmailNotification: Boolean,
        @SerializedName("approval_type")
        var approvalType: Int,
        @SerializedName("approved_or_denied_countries_or_regions")
        var approvedOrDeniedCountriesOrRegions: ApprovedOrDeniedCountriesOrRegions,
        @SerializedName("audio")
        var audio: String,
        @SerializedName("auto_recording")
        var autoRecording: String,
        @SerializedName("breakout_room")
        var breakoutRoom: BreakoutRoom,
        @SerializedName("close_registration")
        var closeRegistration: Boolean,
        @SerializedName("cn_meeting")
        var cnMeeting: Boolean,
        @SerializedName("device_testing")
        var deviceTesting: Boolean,
        @SerializedName("email_notification")
        var emailNotification: Boolean,
        @SerializedName("enable_dedicated_group_chat")
        var enableDedicatedGroupChat: Boolean,
        @SerializedName("encryption_type")
        var encryptionType: String,
        @SerializedName("enforce_login")
        var enforceLogin: Boolean,
        @SerializedName("enforce_login_domains")
        var enforceLoginDomains: String,
        @SerializedName("focus_mode")
        var focusMode: Boolean,
        @SerializedName("host_save_video_order")
        var hostSaveVideoOrder: Boolean,
        @SerializedName("host_video")
        var hostVideo: Boolean,
        @SerializedName("in_meeting")
        var inMeeting: Boolean,
        @SerializedName("jbh_time")
        var jbhTime: Int,
        @SerializedName("join_before_host")
        var joinBeforeHost: Boolean,
        @SerializedName("meeting_authentication")
        var meetingAuthentication: Boolean,
        @SerializedName("mute_upon_entry")
        var muteUponEntry: Boolean,
        @SerializedName("participant_video")
        var participantVideo: Boolean,
        @SerializedName("private_meeting")
        var privateMeeting: Boolean,
        @SerializedName("registrants_confirmation_email")
        var registrantsConfirmationEmail: Boolean,
        @SerializedName("registrants_email_notification")
        var registrantsEmailNotification: Boolean,
        @SerializedName("request_permission_to_unmute_participants")
        var requestPermissionToUnmuteParticipants: Boolean,
        @SerializedName("show_share_button")
        var showShareButton: Boolean,
        @SerializedName("use_pmi")
        var usePmi: Boolean,
        @SerializedName("waiting_room")
        var waitingRoom: Boolean,
        @SerializedName("watermark")
        var watermark: Boolean
    ) {
        @Keep
        data class ApprovedOrDeniedCountriesOrRegions(
            @SerializedName("enable")
            var enable: Boolean
        )

        @Keep
        data class BreakoutRoom(
            @SerializedName("enable")
            var enable: Boolean
        )
    }
}