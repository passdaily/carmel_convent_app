//package info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout
//
//import android.content.Context
//import us.zoom.sdk.ZoomSDK
//import us.zoom.sdk.MeetingService
//import us.zoom.sdk.JoinMeetingOptions
//import us.zoom.sdk.JoinMeetingParams
//import us.zoom.sdk.MeetingViewsOptions
//
//class JoinMeetingHelper {
//    fun joinMeeting(context: Context?, zoomSDK: ZoomSDK, meetingNo: String?, password: String?, displayName: String?
//    ): Int {
//        val meetingService = zoomSDK.meetingService ?: return 0
//        val joinMeetingOptions = JoinMeetingOptions()
//        joinMeetingOptions.no_share = true
//        val joinMeetingParams = JoinMeetingParams()
//        joinMeetingParams.displayName = displayName
//        joinMeetingParams.meetingNo = meetingNo
//        joinMeetingParams.password = password
//        return meetingService.joinMeetingWithParams(context, joinMeetingParams, joinMeetingOptions)
//    }
//    fun joinMeeting(
//        context: Context?,
//        zoomSDK: ZoomSDK,
//        meetingNo: String?,
//        password: String?,
//        displayName: String?,
//        MEETING_DETAILS: Boolean,
//        NO_BUTTON_PARTICIPANTS: Int,
//        UNMUTE_AUDIO: Boolean,
//        INVITE_OPTION: Boolean
//    ): Int {
//        val meetingService = zoomSDK.meetingService ?: return 0
//        val joinMeetingOptions = JoinMeetingOptions()
//        joinMeetingOptions.no_driving_mode = MEETING_DETAILS
//        joinMeetingOptions.no_invite = INVITE_OPTION
//        joinMeetingOptions.no_share = true
//        joinMeetingOptions.no_audio = UNMUTE_AUDIO
//        joinMeetingOptions.no_disconnect_audio = true
//        joinMeetingOptions.no_unmute_confirm_dialog = true
//        if (MEETING_DETAILS && NO_BUTTON_PARTICIPANTS != 0) {
//            joinMeetingOptions.meeting_views_options = (MeetingViewsOptions.NO_TEXT_PASSWORD +
//                    MeetingViewsOptions.NO_BUTTON_PARTICIPANTS
//                    + MeetingViewsOptions.NO_TEXT_MEETING_ID)
//        } else if (MEETING_DETAILS) {
//            joinMeetingOptions.meeting_views_options = (MeetingViewsOptions.NO_TEXT_PASSWORD
//                    + MeetingViewsOptions.NO_TEXT_MEETING_ID)
//        } else if (NO_BUTTON_PARTICIPANTS != 0) {
//            joinMeetingOptions.meeting_views_options = NO_BUTTON_PARTICIPANTS
//        }
//        val joinMeetingParams = JoinMeetingParams()
//        joinMeetingParams.displayName = displayName
//        joinMeetingParams.meetingNo = meetingNo
//        joinMeetingParams.password = password
//        return meetingService.joinMeetingWithParams(context, joinMeetingParams, joinMeetingOptions)
//    }
//}