//package info.passdaily.camrelconvertapp.services.zoomsdk
//
//import android.content.Context
//
//import us.zoom.sdk.*
//
//class MeetingHostHelperJava(
//    private val context: Context,
//    private val zoomSDK: ZoomSDK, /* access modifiers changed from: private */
//    private val meetingStatusListener: MeetingStatusListener
//) {
//    private val meetingService: MeetingService? = zoomSDK.meetingService
//
//    interface MeetingStatusListener {
//        fun onMeetingFailed()
//        fun onMeetingRunning()
//    }
//
//    fun createInstantMeeting(): Int {
//        if (meetingService == null) {
//            return -1
//        }
//        val instantMeetingOptions = InstantMeetingOptions()
////        meetingService.addListener(MeetingServiceListener { meetingStatus, i, i2 ->
////            if (meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING) {
////                meetingStatusListener.onMeetingRunning()
////            } else if (meetingStatus == MeetingStatus.MEETING_STATUS_FAILED) {
////                meetingStatusListener.onMeetingFailed()
////            }
////        })
//        return meetingService.startInstantMeeting(context, instantMeetingOptions)
//    }
//
//}