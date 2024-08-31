//package info.passdaily.camrelconvertapp.services.zoomsdk;
//
//import android.content.Context;
//
//import us.zoom.sdk.JoinMeetingOptions;
//import us.zoom.sdk.JoinMeetingParams;
//import us.zoom.sdk.MeetingService;
//import us.zoom.sdk.ZoomSDK;
//
//public class JoinMeetingHelperJava {
//    public final int joinMeeting(Context context, ZoomSDK zoomSDK, String meetingNo, String password, String displayName) {
//        MeetingService meetingService = zoomSDK.getMeetingService();
//        if (meetingService == null) {
//            return 0;
//        }
//        JoinMeetingOptions joinMeetingOptions = new JoinMeetingOptions();
//        JoinMeetingParams joinMeetingParams = new JoinMeetingParams();
//        joinMeetingParams.meetingNo = meetingNo;
//        joinMeetingParams.password = password;
//        joinMeetingParams.displayName = displayName;
//        return meetingService.joinMeetingWithParams(context, joinMeetingParams, joinMeetingOptions);
//    }
//}
