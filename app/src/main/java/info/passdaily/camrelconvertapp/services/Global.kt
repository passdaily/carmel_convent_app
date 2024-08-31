package info.passdaily.camrelconvertapp.services

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.exam_grade.GradeCommonModel

class Global {
    companion object {
        ///New url for files upload and download //http://vtime.passdaily.in

        ///old url for files upload and download //http://carmelkhopoli.passdaily.in
//carmel.passdaily.in/

        var student_image_url = "http://carmel.passdaily.in/StudentImages/"
        var staff_image_url = "http://carmel.passdaily.in/Photos/StaffImage/"

        var event_url = "http://carmel.passdaily.in"
        //http://teachdaily.in/ba9b8405-5468-415f-8f64-e14786b1ed95IMG_1652361489497.jpg
        var image_url = "http://carmel.passdaily.in/Album_File"
        var screenState = "landingpage"
        var study_url = "http://carmel.passdaily.in/StudyMeterials/"

        var voice_url = "http://carmel.passdaily.in/voice%20mail/"

        var currentPage = 1
        var studentId = 0
        var studentName = ""
        var className = ""
        var subjectId = -1

        var academicId = 0

        var fcmToken =""
        var inboxcount = 0

        var ALBUM_URL = ""
        var ALBUM_TITLE = ""
        var ALBUM_CATEGORY_NAME = ""

        var outOffRangeReason = ""
        var lateReason = ""
        var user_default_latitude = 0.0
        var user_default_longitude = 0.0
        var WORK_PLACE_LATTITUDE  = 0.0
        var WORK_PLACE_LONGITUDE = 0.0
        var workPunchInTime = ""
        var punchingId = 0
        var successMessage =""
        var errorMessage= ""
        var punchStatus = ""

        ////admin Attendance punch
        var punchSTAFFID = 0
        var punchACCADEMICID =0

        //        var subjectName = ""
        var subjectImage = 0
//        var assignmentSubjectId = 0
        var descExamId = 0
        var objExamId = 0

        var dOnlineExamResult = ArrayList<OnlineDExamResultModel.DOnlineExamResult>()

        var albumImageList =  ArrayList<CustomImageModel>()

        var getGradeMark = ArrayList<ArrayList<GradeCommonModel.GradeList>>()


        var blockDetailsModel = ArrayList<BlockDetailsModel.BlockDetail>()
        var blockStatus = false

        ///todo : notification list
        var notificationSentList = java.util.ArrayList<NotificationSentStaffModel.InboxSent>()

        ///todo : leave staff list sort
        var progressLeaveList = ArrayList<LeaveDetailsStaffModel.LeaveDetail>()
        var approvedLeaveList = ArrayList<LeaveDetailsStaffModel.LeaveDetail>()
        var rejectedLeaveList = ArrayList<LeaveDetailsStaffModel.LeaveDetail>()
        /////

        ///todo :enquiry staff list sort
        var pendingEnquiryList  = ArrayList<EnquiryListStaffModel.Enquiry>()
        var repliedEnquiryList  = ArrayList<EnquiryListStaffModel.Enquiry>()
        //


        ///todo : notification array for reload
        var notificationList = ArrayList<NotificationStaffModel.Inbox>()

        //todo : Message List for reload
        var messageList = ArrayList<MessageListModel.Message>()
        var messageDeliveryList = ArrayList<MessageDeliveryListModel.SmsDelivery>()

        //todo : voice Message List for reload
        var voiceMessageList = ArrayList<VoiceMessageListModel.Voice>()

        ////todo : About us and faq
        var aboutUsFaqList = java.util.ArrayList<AboutusFaqListModel.Aboutus>()
        var faqAboutUsList = java.util.ArrayList<AboutusFaqListModel.Aboutus>()


        ////todo : Academic Management
//        var getSubjectList = ArrayList<SubjectListModel.Subject>()
//        var getSubjectCategory = ArrayList<SubCategoryListModel.Subjectcategory>()
//        var getClassList = ArrayList<ClassListModel.Class>()
//        var getAcaddemic = ArrayList<AcademicListModel.AccademicDetail>()


        ////todo : Progress card
        var aCCADEMICID = 0
        var cLASSID = 0
        var eXAMID = 0
        var adminId = 0
        var cLASSNAME = ""
//
//        @SuppressLint("SimpleDateFormat")
//        fun dateformat(time: String?): String? {
//            val inputPattern = "yyyy-MM-dd"
//            val outputPattern = "MMM d, yyyy"
//            val inputFormat = SimpleDateFormat(inputPattern)
//            val outputFormat = SimpleDateFormat(outputPattern)
//            var date: Date? = null
//            var str: String? = null
//            try {
//                date = inputFormat.parse(time!!)
//                str = outputFormat.format(date!!)
//            } catch (e: ParseException) {
//                e.printStackTrace()
//            }
//            return str
//        }


    }

    @Suppress("DEPRECATION")
    class MyPagerAdapter(fragmentManager: FragmentManager?) :
        FragmentPagerAdapter(fragmentManager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentTitleList.size
        }

        fun addFragmentTitle(title: String) {
            mFragmentTitleList.add(title)
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getItemPosition(`object`: Any): Int {
            // POSITION_NONE makes it possible to reload the PagerAdapter
            return POSITION_NONE
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }
}