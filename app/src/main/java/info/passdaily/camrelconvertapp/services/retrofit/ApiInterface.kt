package info.passdaily.camrelconvertapp.services.retrofit

import info.passdaily.camrelconvertapp.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ApiInterface {

    //http://staff.teachdaily.in/ElixirApi/Teacher/ValidateTeacherLogin?UserName=demo&Password=123456&SchoolCode=883735
    companion object {
        //ParenApp Json :  carmelapp.passdaily.in
        //
        //StaffApp Json :  carmelstaff.passdaily.in

        //meridianapp.passdaily.in
        var BASE_URL = "http://carmelapp.passdaily.in/PassDailyParentsApi/"
        var BASE_URLS = "http://carmelstaff.passdaily.in/ElixirApi/"//http://vtimestaff.passdaily.in
        var BASE_URL_MADRASA = "http://madrasastaff.passdaily.in/ElixirApi/"
//        //staff
//        //

        var ZOOM_BASE_URL = "https://api.zoom.us/v2/"
    }

    //////Zoom Zak Token
    //https://api.zoom.us/v2/users/office@passdailypvtltd.com/token?type=zak
    @GET("users/{userEmail}/token")
    fun getZakToken(@Header("Authorization") authHeader: String,
                    @Path(value = "userEmail", encoded = true) userEmail: String,
                    @Query("type") type: String): Call<ZakAccessTokenModel>

    //https://api.zoom.us/v2/users/office@passdailypvtltd.com

    @GET("users/{userEmail}/")
    fun getZoomSdkUser(@Header("Authorization") authHeader: String,
                       @Path(value = "userEmail", encoded = true) userEmail: String): Call<ZoomSdkUserModel>

    @GET("meetings/{meetingId}/")
    fun getMeetingDetails(@Header("Authorization") authHeader: String,
                          @Path(value = "meetingId", encoded = true) meetingId: String): Call<ZoomMeetingDetailsModel>



    //data['fcmToken'
    @Headers("Content-Type:application/json")
    @POST("{path}")
    fun zoomStartEndStatus(
        @Path("path") path : String?,
        @Body accademicRe: String?,
    ): Call<String>



    @GET
    fun userName(@Url url: String?): Response<ResponseBody?>?

    ///new
    //General/ContactUs?StudentId=0
    @GET("Dashboard/Details")
    suspend fun dashboardItemsNew(
        @Query("studentId") StudentId: Int,
        //category
    ): Response<DashBoardModel>

    //http://demoapp.passdaily.in/PassDailyParentsApi/Login/GetChildrenDetails?parentId=1
    @GET("Login/GetChildrenDetails")
    suspend fun childrenDetailsNew(
        @Query("parentId") ParentId: Int,
        //category
    ): Response<ChildrensModel>


    //http://demoapp.passdaily.in/PassDailyParentsApi/Attendance/AttendanceReportGet?STUDENT_ID=9&ACCADEMIC_ID=7
    @GET("Attendance/AttendanceReportGet")
    suspend fun getAbsentsNew(
        @Query("STUDENT_ID") StudentId: Int,
        @Query("ACCADEMIC_ID") aCCADEMIC_ID: Int,
        //category
    ): Response<AttendanceReportModel>

    //http://demoapp.passdaily.in/PassDailyParentsApi/Accademic/AccademicYearGet?AccademicId=0
    @GET("Accademic/AccademicYearGet")
    suspend fun accademicNew(
        @Query("AccademicId") AccademicId: Int,
        //category
    ): Response<AccademicYearsModel>


    //StudyMaterial/StudyMaterialList
    @GET("StudyMaterial/StudyMaterialList")
    suspend fun getStudyMaterialListNew(
        @Query("AccademicId") Username: Int,
        @Query("ClassId") ClassId: Int,
        @Query("StudentId") StudentId: Int,
        @Query("SubjectId") ChapterId: Int
        //category
    ): Response<StudyMaterialModel>


    @GET("StudyMaterial/FileList")
    suspend fun materialList(
        @Query("StudyMeterialId") materialId: Int,
        //category
    ): Response<MaterialListModel>

    //OnlineExam/GetSubjectByClass?
    @GET("OnlineExam/GetSubjectByClass")
    suspend fun subjectModelNew(
        @Query("ClassId") ClassId: Int,
        @Query("StudentId") StudentId: Int
        //category
    ): Response<SubjectsModel>


    //OnlineExam/LiveClassList?AccademicId=8&ClassId=12&SubjectId=-1&StudentId=928
    @GET("OnlineExam/LiveClassList")
    suspend fun joinLiveNew(
        @Query("AccademicId") Username: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SubjectId") SubjectId: Int,
        @Query("StudentId") StudentId: Int
        //category
    ): Response<LiveClassListModel>

    @GET("OnlineExam/MeetingAttend")
    suspend fun meetingAttend(
        @Query("ZLiveClassId") ZLiveClassId: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("StudentId") StudentId: Int,
        @Query("MeetingStatus") MeetingStatus: String

        //category
    ): Response<LiveClassListModel>

    //OnlineExam/AssignmentList
    @GET("OnlineExam/AssignmentList")
    suspend fun getAssignmentListNew(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("StudentId") StudentId: Int,
        @Query("SubjectId") ChapterId: Int
        //category
    ): Response<AssignmentListModel>


    @GET("OnlineExam/AssignmentStatusByStudentNew")
    suspend fun assignmentDetails(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("StudentId") StudentId: Int,
        @Query("AssignmentId") AssignmentId: Int,
        //category
    ): Response<AssignmentDetailsModel>

    // http://demoapp.passdaily.in/PassDailyParentsApi/OnlineExam/OnlineExamList?AccademicId=7&ClassId=2&SubjectId=-1&StudentId=1
    @GET("OnlineExam/OnlineExamList")
    suspend fun getObjectiveListNew(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SubjectId") SubjectId: Int,
        @Query("StudentId") StudentId: Int
        //category
    ): Response<ObjectiveExamModel>

    @GET("OnlineDExam/OnlineDExamList")
    suspend fun getDescriptiveListNew(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SubjectId") SubjectId: Int,
        @Query("StudentId") StudentId: Int
        //category
    ): Response<DescriptiveExamModel>



    //http://demoapp.passdaily.in/PassDailyParentsApi/
    // Dashboard/EventListAll?AccademicId=7&ClassId=10&StudentId=1020
    @GET("Dashboard/EventListAll")
    suspend fun parentEventListAll(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("StudentId") StudentId: Int
        //category
    ): Response<EventListModel>


    //http://demostaff.passdaily.in/ElixirApi/StaffDashboard/EventListAll?AdminId=5
    //meridianstaff.passdaily.in//ElixirApi/Event/EventListNew?AccademicId=10&EventType=-1&AdminId=1&SchoolId=1
    @GET("Event/EventListNew")
    suspend fun manageEvent(
        @Query("AccademicId") AccademicId: Int,
        @Query("EventType") EventType: Int,
        @Query("AdminId") AdminId: Int,
        @Query("SchoolId") SchoolId: Int,

        ): Response<EventListModel>

    @GET("Event/EventDropById")
    suspend fun deleteEventItem(
        @Query("EventId") EventId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("SchoolId") SchoolId: Int

    ): Response<String>


    @Headers("Content-Type:application/json")
    @POST("EnquiryList/EnquiryList")
    suspend fun enquiryListNew(
        @Body accademicRe: RequestBody?,
    ): Response<EnquiryListModel>

    @Headers("Content-Type:application/json")
    @POST("LeaveList/LeaveList")
    suspend fun leaveListNew(
        @Body accademicRe: RequestBody?,
    ): Response<LeaveDetailsModel>

    //Enquiry/EnquiryRequest

    @Headers("Content-Type:application/json")
    @POST("{path}")
    suspend fun commonPostFun(
        @Path("path") path : String?,
        @Body accademicRe: RequestBody?,
    ): Response<String>


    @Headers("Content-Type:application/json")
    @POST("OnlineExam/AssignmentSubmit")
    suspend fun assignmentSubmitFun(
        @Body accademicRe: RequestBody?,
    ): Response<String>



    //http://demoapp.passdaily.in/PassDailyParentsApi/OnlineExam/OnlineExamDetailsById?StudentId=1&OexamId=101
    @GET("OnlineExam/OnlineExamDetailsById")
    suspend fun objectiveDetailsNew(
        @Query("StudentId") StudentId: Int,
        @Query("OexamId") OexamId: Int,
        //category
    ): Response<ObjectiveDetailModel>

    //http://demoapp.passdaily.in/PassDailyParentsApi/Library/LibraryDetailsGet?StudentId=87
    @GET("Library/LibraryDetailsGet")
    suspend fun getLibraryNew(
        @Query("StudentId") StudentId: Int,
        //category
    ): Response<LibraryModel>

    //String url_login = Global.url+"Login/ValidateLogin?Username=";
    // http://parent.teachdaily.in/PassDailyParentsApi/Login/ValidateLogin?Username=7558966668&Password=844512&SchoolCode=883735
    @GET("Login/ValidateLogin")
    suspend fun loginPageNew(
        @Query("Username") Username: String,
        @Query("Password") Password: String,
        @Query("SchoolCode") SchoolCode: String,
        //category
    ): Response<LoginModel>

  // Accademic/GetHolidayListById?StudentId=2563&AccademicId=5&Dummy=0
    @GET("Accademic/GetHolidayListById")
    //"&AccademicId=6" + "&Dummy=0" + "&Dummy2=0"
    suspend fun holidayList(
        @Query("StudentId") StudentId: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("Dummy") Dummy: Int,
    ): Response<HolidayCalendarList>


    // Accademic/GetActivityListById?StudentId=2563&AccademicId=5&Dummy=0
    @GET("Accademic/GetActivityListById")
    suspend fun activityList(
        @Query("StudentId") StudentId: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("Dummy") Dummy: Int,
        @Query("Dummy2") Dummy2: Int
    ): Response<ActivityCalendarList>


    // @Multipart
    @Headers("Content-Type:application/json")
    @POST("Inboxnew/AppInboxGet")
    suspend fun notificationNew(
        @Body accademicRe: RequestBody?,
    ): Response<InboxDetailsModel>

    //Inbox/AppInboxReadById?
    @GET("Inbox/AppInboxReadById")
    suspend fun inboxReadStatus(
        @Query("StudentId") StudentId: Int,
        @Query("InboxId") InboxId: Int
        //category
    ): Response<String>

    // http://demoapp.passdaily.in/PassDailyParentsApi/OnlineDExam/OnlineDExamDetailsById?StudentId=1&ExamId=62
    @GET("OnlineDExam/OnlineDExamDetailsById")
    suspend fun descriptiveListNew(
        @Query("StudentId") StudentId: Int,
        @Query("ExamId") ExamId: Int
        //category
    ): Response<DescriptiveDetailModel>





//    ///////////Objective Exams
///// //http://demoapp.passdaily.in/PassDailyParentsApi/OnlineExam/OnlineExamStatusByStudent?AccademicId=6&ClassId=1&StudentId=5&OexamId=1
//    @GET("OnlineExam/OnlineExamStatusByStudent")
//    suspend fun onlineExamStatus(
//        @Query("AccademicId") AccademicId: Int,
//        @Query("ClassId") ClassId: Int,
//        @Query("StudentId") StudentId: Int,
//        @Query("OexamId") OexamId: Int
//        //category
//    ): Response<ObjectiveOnlineExamStatus>

    //http://demoapp.passdaily.in/PassDailyParentsApi/OnlineExam/QuestionListForStart?OexamId=1&OexamAttemptId=1
    @GET("OnlineExam/QuestionListForStart")
    suspend fun questionListForStart(
        @Query("OexamId") OexamId: Int,
        @Query("OexamAttemptId") OexamAttemptId: Int
        //category
    ): Response<QuestionListModel>

    //OnlineExam/OptionsList?QuestionId="+QUESTION_ID1+"&OexamAttemptId="+OEXAM_ATTEMPT_ID;
    @GET("OnlineExam/OptionsList")
    suspend fun optionsList(
        @Query("QuestionId") QuestionId: Int,
        @Query("OexamAttemptId") OexamAttemptId: Int
        //category
    ): Response<ObjectiveOptionList>

    //http://demoapp.passdaily.in/PassDailyParentsApi/OnlineExam/OnlineExamResultNew?
    // AccademicId=8&StudentId=901&OexamId=20&OexamAttemptId=33
    @GET("OnlineExam/OnlineExamResultNew")
    suspend fun objectiveExamResult(
        @Query("AccademicId") AccademicId: Int,
        @Query("StudentId") StudentId: Int,
        @Query("OexamId") OexamId: Int,
        @Query("OexamAttemptId") OexamAttemptId: Int
        //category
    ): Response<ObjectiveExamResultModel>


    @Headers("Content-Type:application/json")
    @POST("OnlineExam/SubmitOption")
    suspend fun submitAndNext(
        @Body accademicRe: RequestBody?,
    ): Response<String>


    @Headers("Content-Type:application/json")
    @POST("OnlineExam/DeleteOption")
    suspend fun deleteOption(
        @Body accademicRe: RequestBody?,
    ): Response<String>


    @Headers("Content-Type:application/json")
    @POST("OnlineExam/PauseExam")
    suspend fun saveAndExit(
        @Body accademicRe: RequestBody?,
    ): Response<String>


    @Headers("Content-Type:application/json")
    @POST("OnlineExam/DoAutoEnd")
    suspend fun doAutoEnd(
        @Body accademicRe: RequestBody?,
    ): Response<String>

    @Headers("Content-Type:application/json")
    @POST("OnlineExam/FinishExam")
    suspend fun finishExam(
        @Body accademicRe: RequestBody?,
    ): Response<String>


    @GET("OnlineExam/OnlineExamStatusByStudent")
    fun getOnlineExamStatus(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("StudentId") StudentId: Int,
        @Query("OexamId") OexamId: Int
        //category
    ): Call<ObjectiveOnlineExamStatus>




    //////////////// todo :Objectives exams


    ////descriptive Exam
    //http://demoapp.passdaily.in/PassDailyParentsApi/OnlineDExam/DQuestionListForStart?ExamId=1&ExamAttemptId=8
    @GET("OnlineDExam/DQuestionListForStart")
    suspend fun questionDListForStart(
        @Query("ExamId") dExamId: Int,
        @Query("ExamAttemptId") dExamAttemptId: Int
        //category
    ): Response<QuestionDListModel>


    //http://demoapp.passdaily.in/PassDailyParentsApi/OnlineDExam/TakeAnswer?ExamId=2&ExamAttemptId=8&QuestionId=18
    @GET("OnlineDExam/TakeAnswer")
    suspend fun takeAnswer(
        @Query("ExamId") dExamId: Int,
        @Query("ExamAttemptId") dExamAttemptId: Int,
        @Query("QuestionId") questionId: Int
        //category
    ): Response<TakeAnswerModel>


    @Headers("Content-Type:application/json")
    @POST("{path}")
    suspend fun deleteFileMethod(
        @Path("path") path : String?,
        @Body accademicRe: RequestBody?,
    ): Response<String>

    @GET("OnlineDExam/TakeAnswer")
    fun takeAnswerNew(
        @Query("ExamId") examId: Int,
        @Query("ExamAttemptId") dExamAttemptId: Int,
        @Query("QuestionId") questionId: Int
        //category
    ): Call<String>


    @GET("OnlineExam/AssignmentFileDelete")
    suspend fun deleteAssignmentFile(
        @Query("AssignmentFileId") AssignmentFileId: Int,
        //category
    ): Response<String>


    @Multipart
    // @FormUrlEncoded
    @POST("{path}")
    fun fileUpload(
        @Path("path") path : String?,
        @Part files: List<MultipartBody.Part>,
    ): Call<FileResultModel>

    @Multipart
    // @FormUrlEncoded
    @POST("{path}")
    fun fileUploadAssignment(
        @Path("path") path : String?,
        @Part files: MultipartBody.Part,
    ): Call<FileResultModel>

    @Multipart
    // @FormUrlEncoded
    @POST("{path}")
    fun fileUploadVoice(
        @Path("path") path : String?,
        @Part files: MultipartBody.Part,
    ): Call<FileVoiceResultModel>

//OnlineDExam/SubmitAnswer
    @Headers("Content-Type:application/json")
    @POST("OnlineDExam/SubmitAnswer")
    suspend fun submitAnswerDExam(
        @Body accademicRe: RequestBody?,
    ): Response<String>

    //OnlineDExam/FinishDExam
    @Headers("Content-Type:application/json")
    @POST("OnlineDExam/FinishDExam")
    suspend fun finishDExam(
        @Body accademicRe: RequestBody?,
    ): Response<String>

    //OnlineDExam/DoAutoEndDExam
    @Headers("Content-Type:application/json")
    @POST("OnlineDExam/DoAutoEndDExam")
    suspend fun doAutoEndDExam(
        @Body accademicRe: RequestBody?,
    ): Response<String>


    //OnlineDExam/DoAutoEndDExam
    @Headers("Content-Type:application/json")
    @POST("OnlineDExam/PauseDExam")
    suspend fun saveAndExitDExam(
        @Body accademicRe: RequestBody?,
    ): Response<String>

    //http://demostaff.passdaily.in/ElixirApi/OnlineExam/OptionList?QuetionId=30
    @GET("OnlineExam/OptionList")
    suspend fun objectiveOptionList(
        @Query("QuetionId") QuetionId: Int
        //category
    ): Response<OptionListStaffModel>



    ////OnlineDExam/DQuestionList?AccademicId=8&ClassId=1&SubjectId=9&ExamId=4  //todo: Descriptive Question
    @GET("OnlineDExam/DQuestionList")
    suspend fun descQuestionList(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SubjectId") SubjectId: Int,
        @Query("ExamId") ExamId: Int
        //category
    ): Response<QuestionDescriptiveListModel>


    ///http://demoapp.passdaily.in/PassDailyParentsApi/OnlineDExam/OnlineDExamStatusByStudent?AccademicId=7&ClassId=2&StudentId=19&ExamId=2
    @GET("OnlineDExam/OnlineDExamStatusByStudent")
    fun getOnlineDExamStatus(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("StudentId") StudentId: Int,
        @Query("ExamId") dExamId: Int
        //category
    ): Call<DescriptiveOnlineExamStatus>


    //OnlineDExam/DOnlineExamResult?AccademicId=8&StudentId=928&ExamId=19&ExamAttemptId=14
    @GET("OnlineDExam/DOnlineExamResult")
    suspend fun descriptiveExamResult(
        @Query("AccademicId") AccademicId: Int,
        @Query("StudentId") StudentId: Int,
        @Query("ExamId") dExamId: Int,
        @Query("ExamAttemptId") dExamAttemptId: Int
        //category
    ): Response<OnlineDExamResultModel>




    ///////////////////////////////////////////////////


    ///////////zoom list
    //http://demoapp.passdaily.in/PassDailyParentsApi/OnlineExam/MeetingList?AccademicId=7&ClassId=2&StudentId=1
    @GET("OnlineExam/MeetingList")
    suspend fun zoomScheduleList(
        @Query("AccademicId") Username: Int,
        @Query("ClassId") ClassId: Int,
        @Query("StudentId") StudentId: Int,
        //category
    ): Response<ZoomScheduleListModel>



    // http://demoapp.passdaily.in/PassDailyParentsApi/OnlineExam/YoutubeList?AccademicId=7&ClassId=2&SubjectId=-1&ChapterId=-1
    @GET("OnlineExam/YoutubeList")
    suspend fun onlineVideoList(
        @Query("AccademicId") Username: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SubjectId") SubjectId: Int,
        @Query("ChapterId") ChapterId: Int
        //category
    ): Response<YoutubeListModel>


    //OnlineExam/GetSubjectByClass?
    @GET("OnlineExam/GetSubjectByClass")
    suspend fun subjectList(
        @Query("ClassId") ClassId: Int,
        @Query("StudentId") StudentId: Int
        //category
    ): Response<SubjectsModel>


    @GET("OnlineExam/ChapterList")
    suspend fun chapterList(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SubjectId") SubjectId: Int
        //category
    ): Response<ChapterListModel>


    @GET("{path}")
    suspend fun deleteEnquiryFun(
        @Path("path") path : String?,
        @Query("Enquiry_Id") enquiryId: Int
        //category
    ): Response<String>


    @GET("{path}")
    suspend fun deleteLeaveFun(
        @Path("path") path : String?,
        @Query("LeaveId") leaveId: Int
        //category
    ): Response<String>


    /////Gallery
    @GET("{path}")
    suspend fun commonAlbumCategory(
        @Path("path") path : String?,
        @Query("AccademicId") AccademicId: Int,
        //category
    ): Response<AlbumModel>


    @GET("{path}")
    suspend fun galleryImgVidList(
        @Path("path") path : String?,
        @Query("AlbumCategoryId") AccademicId: Int,
        //category
    ): Response<GalleryImageModel>


    //////http://app.nsrtrading.in/ElixirApi/Gps/GetAllVehichles?Dummy=0
    @GET("Gps/GetAllVehichles")
    suspend fun trackMap(
        @Query("Dummy") dummy: Int,
        //category
    ): Response<VehichlesModel>

    //http://demoapp.passdaily.in/PassDailyParentsApi/Gps/GetLocation?VehichleId=1&TerminalId=352503090699858&SimNumber=7025016669
    @GET("Gps/GetLocation")
    suspend fun gpsLocation(
        @Query("VehichleId") VehichleId: Int,
        @Query("TerminalId") TerminalId: String,
        @Query("SimNumber") SimNumber: String
        //category
    ): Response<GpsLocationModel>


    //http://demoapp.passdaily.in/PassDailyParentsApi/Fees/FeesPaidListGet?ClassId=1&AccademicId=8&StudentId=533&StudentRollNo=1
    @GET("Fees/FeesPaidListGet")
    suspend fun feesDetails(
        @Query("ClassId") ClassId: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("StudentId") StudentId: Int,
        @Query("StudentRollNo") StudentRollNo: Int
        //category
    ): Response<FeesDetailsModel>


    ////Exam/ExamNameGet?ExamId=0
    @GET("Exam/ExamNameGet")
    suspend fun examDetailsList(
        @Query("ExamId") ExamId: Int,
        //category
    ): Response<ExamDetailsModel>


    //http://demoapp.passdaily.in/PassDailyParentsApi/Result/AnnualResultGet?STUDENT_ID=533&ACCADEMIC_ID=8
    @GET("Exam/ExamMarkDetailsGet")
    suspend fun annualResultGet(
        @Query("STUDENT_ID") STUDENT_ID: Int,
        @Query("ACCADEMIC_ID") AccademicId: Int
        //category
    ): Response<AnnualResultModel>



    //Global.url+"Exam/ExamMarkDetailsGet?AccademicId="+student22.accademic_id+"&ClassId="+student22.class_id+"&ExamId=";
    //= pos+"&StudentRollNo="+student22.stu_roll_no;
    //http://demoapp.passdaily.in/PassDailyParentsApi/Exam/ExamMarkDetailsGet?AccademicId=8&ClassId=12&ExamId=1&StudentRollNo=1
    @GET("Exam/ExamMarkDetailsGet")
    suspend fun examMarkDetails(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("StudentRollNo") StudentRollNo: Int,
        //StudentId=1364
        @Query("StudentId") StudentId: Int,
        //category
    ): Response<ProgressMarkDetailsModel>

    //http://demoapp.passdaily.in/PassDailyParentsApi/Conveyor/ConveyorGet?StudentId=351&ClassId=17
    @GET("Conveyor/ConveyorGet")
    suspend fun conveyorGet(
        @Query("StudentId") StudentId: Int,
        @Query("ClassId") ClassId: Int
        //category
    ): Response<ConveyorModel>

    // //OnlineExam/BlockStatus?AccademicId="+student2.accademic_id+"&StudentId="

    @GET("OnlineExam/BlockStatus")
    suspend fun blockStatus(
        @Query("AccademicId") AccademicId: Int,
        @Query("StudentId") StudentId: Int
        //category
    ): Response<BlockDetailsModel>


    //////register
    // http://parent.teachdaily.in/PassDailyParentsApi/Login/ParentAccountCreate?MobileNumber=7558966668&SchoolCode=883735
    @GET("Login/ParentAccountCreate")
    suspend fun parentAccountCreate(
        @Query("MobileNumber") MobileNumber: String,
        @Query("SchoolCode") SchoolCode: String
        //category
    ): Response<String>

    ///http://staff.teachdaily.in/ElixirApi/Teacher/ForgotPassword?MobileNumber=7558966668&SchoolCode=883735
    @GET("Teacher/ForgotPassword")
    suspend fun staffAccountCreate(
        @Query("MobileNumber") MobileNumber: String,
        @Query("SchoolCode") SchoolCode: String
        //category
    ): Response<String>


    //    @get:GET("General/AboutUs?StudentId=0&AboutType=")
//    val explore: Call<ExploreModel>
    @GET("General/AboutUs")
    suspend fun faqitems(
        @Query("StudentId") StudentId: Int,
        @Query("AboutType") AboutType: Int,
        //category
    ): Response<AboutusListModel>

    //General/ContactUs?StudentId=0
    @GET("General/ContactUs")
    suspend fun contactUsItems(
        @Query("StudentId") StudentId: Int,
        //category
    ): Response<ContactusListModel>



    //   String delte=Global.url+"Login/Logout?parentId="+position;
    @GET("Login/Logout")
    suspend fun logOutUser(
        @Query("parentId") ParentId: Int,
        //category
    ): Response<String>

    //////

    //    url_login = Global.url+"Teacher/ValidateTeacherLogin?UserName="+input_fullname.getText().toString()
    //                                +"&PassWord="+input_password.getText().toString();
    @GET("Teacher/ValidateTeacherLogin")
    suspend fun loginPageStaff(
        @Query("Username") Username: String,
        @Query("Password") Password: String,
        @Query("SchoolCode") SchoolCode: String,
        //SchoolCode=883735
        //category
    ): Response<LoginStaffModel>

    //http://demostaff.passdaily.in/ElixirApi/StaffDashboard/DashboardGet?AdminId=2
    @GET("StaffDashboard/DashboardGet")
    suspend fun dashBoardStaff(
        @Query("AdminId") AdminId: Int
        //category
    ): Response<DashboardStaffModel>

    ///http://demostaff.passdaily.in/ElixirApi/Accademic/LiveClassDetailsGet?AdminId=2
    @GET("Accademic/LiveClassDetailsGet")
    suspend fun liveClassDetails(
        @Query("AdminId") AdminId: Int
        //category
    ): Response<ZoomGoLiveDetailsModel>

    ///http://demostaff.passdaily.in/ElixirApi/Accademic/ClassDetailsGet?AdminId=2&AccademicId=7
   // @GET("Accademic/LiveClassDetailsGet")
    @GET("Accademic/ClassDetailsGet")
    suspend fun classDetails(
        @Query("AdminId") AdminId: Int,
        @Query("AccademicId") AccademicId: Int
        //category
    ): Response<ClassSubjectDetailModel>

    //Accademic/StartLiveMeeting

    @Headers("Content-Type:application/json")
    @POST("Accademic/StartLiveMeeting")
    suspend fun startLiveMeeting(
        @Body accademicRe: RequestBody?,
    ): Response<EnquiryListModel>


    ///http://demostaff.passdaily.in/ElixirApi/Mark/MarkEntryPageLoad?AdminId=2
    @GET("Mark/MarkEntryPageLoad")
    suspend fun yearClassExam(
        @Query("AdminId") AdminId: Int,
        @Query("SchoolId") SchoolId: Int
        //category
    ): Response<GetYearClassExamModel>

    //http://demostaff.passdaily.in/ElixirApi/Mark/GetSubjectByClass?ClassId=1&AdminId=2
    @GET("Mark/GetSubjectByClass")
    suspend fun subjectStaff(
        @Query("ClassId") ClassId: Int,
        @Query("AdminId") StudentId: Int
        //category
    ): Response<SubjectsModel>

    @Headers("Content-Type:application/json")
    @POST("Mark/GetSubjectByClassFoHomewok")
    suspend fun postSubjectStaff(
        @Body accademicRe: RequestBody?,
    ): Response<SubjectsModel>

    //http://demostaff.passdaily.in/ElixirApi/OnlineExam/QuestionTypeList?AdminId=1
    @GET("OnlineExam/QuestionTypeList")
    suspend fun questionTypeStaff(
        @Query("AdminId") StudentId: Int
        //category
    ): Response<QuestionTypeModel>

    //http://demostaff.passdaily.in/ElixirApi/OnlineVideo/ChaptersById?AccademicId=8&ClassId=1&SubjectId=9

    @GET("OnlineVideo/ChaptersList")
    suspend fun chaptersListStaff(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SubjectId") SubjectId: Int,
        @Query("SchoolId") SchoolId: Int,
        //SchoolId=1
        //category
    ): Response<ChaptersListStaffModel>

    //OnlineVideo/VideoList?AccademicId=" + s_aid + "&ClassId=" + scid + "&SubjectId=" + ssid + "&ChapterId=" + schapterid;

    @GET("OnlineVideo/VideoList")
    suspend fun videoListStaff(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SubjectId") SubjectId: Int,
        @Query("ChapterId") ChapterId: Int
        //category
    ): Response<YoutubeListStaffModel>

    ///OnlineVideo/YoutubeViewReport?YoutubeId=
    @GET("OnlineVideo/YoutubeViewReport")
    suspend fun youtubeViewReport(
        @Query("YoutubeId") YoutubeId: Int,
        //category
    ): Response<YoutubeReportModel>

    //http://demostaff.passdaily.in/ElixirApi/OnlineVideo/UnPlayedList?AccademicId=7&ClassId=2&YoutubeId=1
    @GET("OnlineVideo/UnPlayedList")
    suspend fun unPlayedVideoList(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("YoutubeId") YoutubeId: Int
        //category
    ): Response<UnAttendedListModel>


    //Youtube Full Log
    //http://demostaff.passdaily.in/ElixirApi/OnlineVideo/YoutubeFullLog?YoutubeLogId=3
    @GET("OnlineVideo/YoutubeFullLog")
    suspend fun youtubeFullLog(
        @Query("YoutubeLogId") YoutubeLogId: Int,
        //category
    ): Response<YoutubeFullLogsModel>

    //////


//    //OnlineVideo/VideoDelete?YoutubeId=
//    @GET("{path}")
//    suspend fun deleteYoutubeVideoFun(
//        @Path("path") path : String?,
//        @Query("YoutubeId") YoutubeId: Int
//        //category
//    ): Response<String>

    ////OnlineExam/OnlineExamList?AccademicId=" +
    //                        s_aid + "&ClassId=" +  scid + "&SubjectId=" +  ssid
//OnlineExam/OnlineExamList?AccademicId=8&ClassId=1&SubjectId=9
    @GET("OnlineExam/OnlineExamList")
    suspend fun onlineExamListStaff(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SubjectId") SubjectId: Int,//SchoolId
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<ObjExamListStaffModel>

    //http://demostaff.passdaily.in/ElixirApi/OnlineExam/QuestionOptionList?AccademicId=8&ClassId=12&SubjectId=7&OexamId=15
    @GET("OnlineExam/QuestionOptionList")
    suspend fun objQuestionOptionListStaff(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SubjectId") SubjectId: Int,
        @Query("OexamId") OexamId: Int,
        //category
    ): Response<QuestionOptionsListModel>


    //http://demostaff.passdaily.in/ElixirApi/OnlineExam/OnlineExamResult?OexamId=2
    @GET("OnlineExam/OnlineExamResult")
    suspend fun onlineExamResultStaff(
        @Query("OexamId") OexamId: Int
        //category
    ): Response<ObjExamDetailsStaffModel>


    //http://demostaff.passdaily.in/ElixirApi/OnlineExam/UnAttendedList?AccademicId=8&ClassId=12&OexamId=2

    @GET("OnlineExam/UnAttendedList")
    suspend fun unAttendedList(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("OexamId") OexamId: Int
        //category
    ): Response<UnAttendedListModel>


    //OnlineExam/OnlineExamPublish?OexamId=";
    @GET("{path}")
    suspend fun commonGetFuntion(
        @Path("path") path : String?,
        @QueryMap paramsMap :HashMap<String?, Int>
        //category
    ): Response<String>

//    //OnlineExam/OnlineExamPublish?OexamId=";
//    @GET("OnlineExam/AllowOnceMore")
//    suspend fun allowOnceMore(
//        @Query("OexamAttemptId") OexamAttemptId: Int
//        //category
//    ): Response<String>


    //http://demostaff.passdaily.in/ElixirApi/StudyMaterial/StudyMaterialGet?AccademicId=8&ClassId=1&SubjectId=9
    @GET("StudyMaterial/StudyMaterialGet")
    suspend fun studyMaterialList(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SubjectId") SubjectId: Int,
        @Query("SchoolId") SchoolId: Int,//SchoolId=1
        //category
    ): Response<MaterialListStaffModel>

    //StudyMaterial/StudyMaterialView?MaterialId=
    @GET("StudyMaterial/StudyMaterialView")
    suspend fun studyMaterialDetails(
        @Query("MaterialId") MaterialId: Int,
        //category
    ): Response<StudyMaterialDetailModel>

    //http://demostaff.passdaily.in/ElixirApi/StudyMaterial/FileDrop?FileId=25
    // &FileName=631eaf06-0fd0-4366-8c2b-0c00b92e6243IMG_1653298719262.jpg
    @GET("{path}")
    suspend fun deleteFiles(
        @Path("path") path : String?,
        @Query("FileId") FileId: Int,
        @Query("FileName") FileName: String
        //category
    ): Response<String>

    ///Assignment list Staff
    ///http://demostaff.passdaily.in/ElixirApi/Assignment/AssignmentList?AccademicId=8&ClassId=1&SubjectId=9
    @GET("Assignment/AssignmentList")
    suspend fun assignmentListStaff(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SubjectId") SubjectId: Int,
        @Query("SchoolId") SchoolId: Int
        //category
    ): Response<AssignmentListStaffModel>

    ///Assignment Details Staff
    //http://demostaff.passdaily.in/ElixirApi/Assignment/AssignmentSubmissionList?AssignmentId=2
    @GET("Assignment/AssignmentSubmissionList")
    suspend fun assignmentSubmissionList(
        @Query("AssignmentId") AssignmentId: Int,
        //category
    ): Response<AssignmentDetailsStaffModel>

    //http://demostaff.passdaily.in/ElixirApi/Assignment/UnSubmittedList?AccademicId=7&ClassId=1&AssignmentId=4
    @GET("Assignment/UnSubmittedList")
    suspend fun unAttendedAssignment(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("AssignmentId") AssignmentId: Int
        //category
    ): Response<UnAttendedListModel>

    //Assignment/AssignmentVerify?AccademicId=
    @GET("Assignment/AssignmentVerify")
    suspend fun studentSubmissionDetail(
        @Query("AccademicId") AccademicId: Int,
        @Query("StudentId") StudentId: Int,
        @Query("AssignmentId") AssignmentId: Int,
        @Query("AssignmentSubmitId") AssignmentSubmitId: Int
        //category
    ): Response<StudentSubmissionDetailsModel>


    //    //http://demostaff.passdaily.in/ElixirApi/Assignment/AssignmentGetById?AssignmentId=17
    @GET("Assignment/AssignmentGetById")
    suspend fun assignmentGetById(
        @Query("AssignmentId") AssignmentId: Int,
        //category
    ): Response<AssignmentDetailsGetByIdModel>



    ////
    @GET("Accademic/GetHolidayListById")
    //"&AccademicId=6" + "&Dummy=0" + "&Dummy2=0"
    suspend fun holidayListStaff(
        @Query("StudentId") StudentId: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("Dummy") Dummy: Int,
    ): Response<HolidayCalendarList>


    // Accademic/GetActivityListById?StudentId=2563&AccademicId=5&Dummy=0
    @GET("Accademic/GetActivityListById")
    suspend fun activityListStaff(
        @Query("StudentId") StudentId: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("Dummy") Dummy: Int,
        @Query("Dummy2") Dummy2: Int
    ): Response<ActivityCalendarList>


    @Headers("Content-Type:application/json")
    @POST("{path}")
    suspend fun liveClassReport(
        @Path("path") path : String?,
        @Body accademicRe: RequestBody?,
    ): Response<ZoomLiveClassReportModel>


    // http://demostaff.passdaily.in/ElixirApi/Accademic/MeetingAttendedReport?ZLiveClassId=17
    @GET("Accademic/MeetingAttendedReport")
    suspend fun meetingAttendedReport(
        @Query("ZLiveClassId") zLiveClassId: Int,

    ): Response<ZoomMeetingAttendedListModel>

    //http://demostaff.passdaily.in/ElixirApi/Accademic/MeetingUnAttendedReport?AccademicId=8&ClassId=2&ZLiveClassId=17
    @GET("Accademic/MeetingUnAttendedReport")
    suspend fun unAttendedZoomReport(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ZLiveClassId") zLiveClassId: Int
        //category
    ): Response<UnAttendedListModel>


    ///Staff Inbox
    ////http://demostaff.passdaily.in/ElixirApi/StaffInbox/StaffInboxGetById?AdminId=2&StaffId=351
    @GET("StaffInbox/StaffInboxGetById")
    suspend fun inboxListStaff(
        @Query("AdminId") AccademicId: Int,
        @Query("StaffId") ClassId: Int,
        //category
    ): Response<InboxListStaffModel>

    //StaffInbox/StaffInboxReadById?InboxId=242&AdminId=2&StaffId=351
    @GET("StaffInbox/StaffInboxReadById")
    suspend fun staffInboxReadById(
        @Query("InboxId") InboxId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("StaffId") ClassId: Int,
        //category
    ): Response<String>


    ///Staff Inbox
    ////http://demostaff.passdaily.in/ElixirApi/Inbox/InboxMessageGet?Title=0&Date=0&StaffId=1
    @GET("Inbox/InboxMessageGet")
    suspend fun notificationStaff(
        @Query("Title") Title: Int,
        @Query("Date") Date: Int,
        @Query("StaffId") StaffId: Int,
        //category
    ): Response<NotificationStaffModel>

    ///http://demostaff.passdaily.in/ElixirApi/Inbox/InboxSentDetailsGet?Title=0&Type=0&From=0&To=0&StaffId=2
    @GET("Inbox/InboxSentDetailsGet")
    suspend fun notificationSentDetails(
        @Query("Title") Title: Int,
        @Query("Type") Type: Int,
        @Query("From") From: Int,
        @Query("To") To: Int,
        @Query("StaffId") StaffId: Int,
        //category
    ): Response<NotificationSentStaffModel>


    ///Attendance/LeaveListGet?AccademicId=&ClassId=0&FromDate=0&Todate=0";
    @GET("Attendance/LeaveListGet")
    suspend fun leaveListGet(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("FromDate") FromDate: Int,
        @Query("Todate") Todate: Int,

        //category
    ): Response<LeaveDetailsStaffModel>


    ///http://demostaff.passdaily.in/ElixirApi/Attendance/EnquiryListGet?ClassId=12&AccademicId=8&SubmitDate=0
    @GET("Attendance/EnquiryListGet")
    suspend fun enquiryListGet(
        @Query("ClassId") ClassId: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("SubmitDate") SubmitDate: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<EnquiryListStaffModel>


    ///http://demostaff.passdaily.in/ElixirApi/Attendance/EnquiryGetById?QueryId=25
    @GET("Attendance/EnquiryGetById")
    suspend fun enquiryGetById(
        @Query("QueryId") QueryId: Int
        //category
    ): Response<EnquiryStaffModel>


    @Headers("Content-Type:application/json")
    @POST("{path}")
    suspend fun zoomScheduledStaff(
        @Path("path") path : String?,
        @Body accademicRe: RequestBody?,
    ): Response<ZoomScheduleStaffModel>

    //zoomScheduledReport
    //http://demostaff.passdaily.in/ElixirApi/OnlineVideo/ZoomReport?ZmeetingId=1
    @GET("OnlineVideo/ZoomReport")
    suspend fun zoomScheduledReport(
        @Query("ZmeetingId") zMEETINGID: Int
        //category
    ): Response<ZoomScheduleReportListModel>

    //Attendance/GetStudentsForAttendance?ClassId=37&AccademicId=8&Date=2022/07/25&SchoolId=1
    @GET("Attendance/GetStudentsForAttendance")
    suspend fun markAbsentsList(
        @Query("ClassId") ClassId: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("Date") Date: String,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<MarkAbsentModel>

    //Attendance/MarkPresent?AttendanceId=&StudentId=
    @GET("Attendance/MarkPresent")
    suspend fun markPresent(
        @Query("AttendanceId") AttendanceId: Int,
        @Query("StudentId") StudentId: Int,
        //category
    ): Response<String>

    @GET("Attendance/MarkFullPresent")
    suspend fun markFullPresent(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("AbsentDate") AbsentDate: String,
        @Query("AdminId") AdminId: Int,
        @Query("Dummy") Dummy: Int
        //category
    ): Response<String>


    ///Student Full details List
    //http://demostaff.passdaily.in/ElixirApi/Message/GetParentListForSendingMessage?AccademicId=8&ClassId=12
    @GET("Message/GetParentListForSendingMessage")
    suspend fun studentList(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<StudentListModel>


    //http://app.nsrtrading.in/ElixirApi/Conveyor/ConveyorsListGet?AccademicId=4
    @GET("Conveyor/ConveyorsListGet")
    suspend fun conveyorList(
        @Query("AccademicId") AccademicId: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<ConveyorsListModel>


    //http://demostaff.passdaily.in/ElixirApi/Pta/PtaListGet?AccademicId=8
    @GET("Pta/PtaListGet")
    suspend fun ptaListGet(
        @Query("AccademicId") AccademicId: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<PtaListModel>

    //http://staff.teachdaily.in/ElixirApi/Staff/StaffListGet?AccademicId=8
    @GET("Staff/StaffListGet")
    suspend fun staffListGet(
        @Query("AccademicId") AccademicId: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<StaffListModel>

    // http://meridianstaff.passdaily.in/ElixirApi/Staff/PunchingStatusByAdmin?AdminId=1&SchoolId=1&StaffId=14
    @GET("Staff/PunchingStatusByAdmin")
    suspend fun punchingStatusByAdmin(
        @Query("AdminId") AccademicId: Int,
        @Query("SchoolId") SchoolId: Int,
        @Query("StaffId") StaffId: Int,
        //category
    ): Response<AdminPunchingOperationModel>

    //http://demostaff.passdaily.in/ElixirApi/BulkMessage/GetClassBySuperAdmin?AccademicId=7
    @GET("BulkMessage/GetClassBySuperAdmin")
    suspend fun classBySuperAdmin(
        @Query("AccademicId") AccademicId: Int,
        //category
    ): Response<GetClassBySuperAdminModel>

    //http://demostaff.passdaily.in/ElixirApi/BulkMessage/ClassSectionBySuperAdmin?AccademicId=7&Dummy=0
    @GET("BulkMessage/ClassSectionBySuperAdmin")
    suspend fun classSectionBySuperAdmin(
        @Query("AccademicId") AccademicId: Int,
        @Query("Dummy") Dummy: Int,
        //category
    ): Response<GetClassBySuperAdminModel>

    //http://demostaff.passdaily.in/ElixirApi/Class/GetClassByTeacher?AccademicId=8&AdminId=2
    @GET("Class/GetClassByTeacher")
    suspend fun classByTeacher(
        @Query("AccademicId") AccademicId: Int,
        @Query("AdminId") AdminId: Int,
        //category
    ): Response<GetClassBySuperAdminModel>

    ////todo : Mark registers /////////////////////////////////////////////////////////////

    ///Mark Register MSPS LPUP
////http://demostaff.passdaily.in/ElixirApi/MarkMsesLpUp/SearchMarksDon?AccademicId=8&ClassId=17&ExamId=29&SubjectId=1
    @GET("MarkMsesLpUp/SearchMarksDon")
    suspend fun markRegisterLpUp(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("SubjectId") SubjectId: Int
        //category
    ): Response<MarkRegisterMsesLpUpModel>


      /////Mark Register KG-XII
    ///http://demostaff.passdaily.in/ElixirApi/Mark/SendMarks?AccademicId=8&ClassId=12&ExamId=1&SubjectId=1
    @GET("Mark/SearchMarks")
    suspend fun markRegisterKgIV(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("SubjectId") SubjectId: Int
        //category
    ): Response<MarkRegisterKGToIVModel>

    /////Mark Register CE
    //MarkDon/SearchMarksDon?AccademicId=8&ClassId=1&ExamId=1&SubjectId=1
    @GET("MarkDon/SearchMarksDon")
    suspend fun markRegisterCE(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("SubjectId") SubjectId: Int
        //category
    ): Response<MarkRegisterMsesLpUpModel>

    ///Mark Register CBSE
    //http://demostaff.passdaily.in/ElixirApi/MarkLnvn/SearchMarksLnvn?AccademicId=8&ClassId=12&ExamId=1&SubjectId=3
    @GET("MarkLnvn/SearchMarksLnvn")
    suspend fun markRegisterCBSE(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("SubjectId") SubjectId: Int
        //category
    ): Response<MarkRegisterKGToIVModel>


    //MarkMspLpUp/SearchMarksMsp?AccademicId=8&ClassId=12&ExamId=1&SubjectId=9
    @GET("{path}")
    suspend fun markRegisterMsps(
        @Path("path") path : String?,
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("SubjectId") SubjectId: Int
        //category
    ): Response<MarkRegisterMspsLpUpModel>

    ////////////////Todo : Progress Report card//////////////////////////////////////////////////
//MarkDon/ProgressReportDon?AccademicId=8&ClassId=17&ExamId=29&AdminId=1&Dummy=0
    @GET("MarkDon/ProgressReportDon")
    suspend fun progressReportLpUp(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("Dummy") Dummy: Int
        //category
    ): Response<ProgressCardLpUpModel>


    //http://demostaff.passdaily.in/ElixirApi/Mark/ProgressReportHs?AccademicId=8&ClassId=1&ExamId=1&AdminId=1&Dummy=0
    @GET("Mark/ProgressReportHs")
    suspend fun progressReportHs(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("Dummy") Dummy: Int
        //category
    ): Response<ProgressCardKgToXiiModel>


    // MarkLnvn/ProgressReportLnvn?AccademicId=8&ClassId=12&ExamId=1&AdminId=1&Dummy=0
    @GET("MarkLnvn/ProgressReportLnvn")
    suspend fun progressReportCBSE(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("Dummy") Dummy: Int
        //category
    ): Response<ProgressCardCbseModel>


    //MarkMspHs/ProgressReportMspHs?AccademicId=8&ClassId=12&ExamId=1&AdminId=1&Dummy=0
    @GET("{path}")
    suspend fun progressReportMsp(
        @Path("path") path : String?,
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("Dummy") Dummy: Int
        //category
    ): Response<ProgressCardMspHsModel>



    ////Descriptive Exam Staff'
    //http://demostaff.passdaily.in/ElixirApi/OnlineDExam/OnlineDExamList?AccademicId=8&ClassId=1&SubjectId=8
    @GET("OnlineDExam/OnlineDExamList")
    suspend fun descriptiveOnlineExamList(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SubjectId") SubjectId: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<DescriptiveExamListStaffModel>

    /////descriptive Result model Staff
    ///http://demostaff.passdaily.in/ElixirApi/OnlineDExam/OnlineDExamResult?ExamId=1
    @GET("OnlineDExam/OnlineDExamResult")
    suspend fun descriptiveExamResult(
        @Query("ExamId") ExamId: Int,
        //category
    ): Response<DescriptiveExamStaffResultModel>

    //http://demostaff.passdaily.in/ElixirApi/OnlineDExam/GoToCorrectionList?AccademicId=7&StudentId=1&ExamId=1&ExamAttemptId=18
    @GET("OnlineDExam/GoToCorrectionList")
    suspend fun descGiveMarkPreviewList(
        @Query("AccademicId") AccademicId: Int,
        @Query("StudentId") StudentId: Int,
        @Query("ExamId") dExamId: Int,
        @Query("ExamAttemptId") dExamAttemptId: Int
        //category
    ): Response<DescGiveMarkPreviewModel>

    @GET("OnlineDExam/GiveMark")
    suspend fun giveMarks(
        @Query("AdminId") AccademicId: Int,
        @Query("ExamAttemptDId") dExamAttemptId: Int,
        @Query("Mark") mark: String
        //category
    ): Response<String>

    //OnlineDExam/RemoveTeacherCommentFile?ExamAttemptDId=
    @GET("{path}")
    suspend fun deleteCommentsFile(
        @Path("path") path : String?,
        @Query("ExamAttemptDId") dExamAttemptId: Int,
    ): Response<String>



    // String Answer_url = Global.url + "OnlineDExam/TakeAnswer?ExamId=" + EXAM_ID + "&ExamAttemptId="
    //                + EXAM_ATTEMPT_ID + "&QuestionId=" + QUESTION_ID;

    @GET("OnlineDExam/TakeAnswer")
    suspend fun takeAnswerDExam(
        @Query("ExamId") examId: Int,
        @Query("ExamAttemptId") examAttemptId: Int,
        @Query("QuestionId") questionId: Int
        //category
    ): Response<TakeAnswerStaffModel>

//{"Answer":{"QUESTION_ID":1,"EXAM_ATTEMPT_D_ID":null,"QUESTION_ID_ATTEMPT":null,"ANSWER":null,"ANSWER_FILE_1":null
//     ,"ANSWER_FILE_2":null,"ANSWER_FILE_3":null,"ANSWER_MARK":-1}}

    //OnlineDExam/UnAttendedListD?AccademicId="+ACCADEMIC_ID+"&ClassId="+CLASS_ID+"&ExamId="
    @GET("OnlineDExam/UnAttendedListD")
    suspend fun unAttendedDList(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int
        //category
    ): Response<UnAttendedListModel>





    //Mark/ExamTopper?AccademicId=8&ClassId=12&ExamId=1&AdminId=1&Dummy1=0&Dummy2=0
    //MarkLnvn/ExamTopper?AccademicId=8&ClassId=12&ExamId=1&AdminId=1&Dummy1=0&Dummy2=0
    //MarkDon/ExamTopper?AccademicId=8&ClassId=12&ExamId=1&AdminId=1&Dummy1=0&Dummy2=0
    //MarkMspHs/ExamTopper?AccademicId=8&ClassId=12&ExamId=1&AdminId=1&Dummy1=0&Dummy2=0
    @GET("{path}")
    suspend fun examTopperResponse(
        @Path("path") path : String?,
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("Dummy1") Dummy: Int,
        @Query("Dummy2") Dummy2: Int
        //category
    ): Response<ExamTopperResponseModel>


    //Mark/ExamGrade?AccademicId=6&ClassId=1&ExamId=1&AdminId=1&Dummy1=0&Dummy2=0&Dummy3=0&Dummy4=0
    //MarkLnvn/ExamGrade?AccademicId=8&ClassId=12&ExamId=1&AdminId=1&Dummy1=0&Dummy2=0&Dummy3=0&Dummy4=0
    //MarkDon/ExamGrade?AccademicId=8&ClassId=12&ExamId=1&AdminId=1&Dummy1=0&Dummy2=0&Dummy3=0&Dummy4=0
    //MarkMspHs/ExamGrade?AccademicId=8&ClassId=12&ExamId=2&AdminId=1&Dummy1=0&Dummy2=0&Dummy3=0&Dummy4=0

    @GET("{path}")
    suspend fun examGradeCbseResponse(
        @Path("path") path : String?,
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("Dummy1") Dummy1: Int,
        @Query("Dummy2") Dummy2: Int,
        @Query("Dummy3") Dummy3: Int,
        @Query("Dummy4") Dummy4: Int
        //category
    ): Response<ExamGradeCBSEModel>

    @GET("{path}")
    suspend fun examGradeResponse(
        @Path("path") path : String?,
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("Dummy1") Dummy1: Int,
        @Query("Dummy2") Dummy2: Int,
        @Query("Dummy3") Dummy3: Int,
        @Query("Dummy4") Dummy4: Int
        //category
    ): Response<ExamGradeStateModel>

    @GET("{path}")
    suspend fun examGradeCeResponse(
        @Path("path") path : String?,
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("Dummy1") Dummy1: Int,
        @Query("Dummy2") Dummy2: Int,
        @Query("Dummy3") Dummy3: Int,
        @Query("Dummy4") Dummy4: Int
        //category
    ): Response<ExamGradeCeModel>


    @GET("{path}")
    suspend fun examGradeMspResponse(
        @Path("path") path : String?,
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("Dummy1") Dummy1: Int,
        @Query("Dummy2") Dummy2: Int,
        @Query("Dummy3") Dummy3: Int,
        @Query("Dummy4") Dummy4: Int
        //category
    ): Response<ExamGradeMspModel>


    ///Send Progress Report
    @GET("{path}")
    suspend fun sendProgressReport(
        @Path("path") path : String?,
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("ExamId") ExamId: Int,
        //category
    ): Response<String>


    /////Group Creation

//http://meridianstaff.passdaily.in/ElixirApi/Group/GroupList
    @Headers("Content-Type:application/json")
    @POST("Group/GroupList")
    suspend fun groupList(
        @Body accademicRe: RequestBody?,
    ): Response<GroupListModel>


    @GET("Group/GroupDropById")
    suspend fun deleteGroupItem(
        @Query("GroupId") GroupId: Int,
        //category
    ): Response<String>


    ///delete studentin group

    //http://demostaff.passdaily.in/ElixirApi/Group/GroupListGet?AccademicId=0
    @GET("{path}")
    suspend fun getGroupListForStudentDelete(
        @Path("path") path : String?,
        @Query("AccademicId") AccademicId: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<GroupListModel>

    ///http://demostaff.passdaily.in/ElixirApi/Group/GroupStudentList?AccademicId=8&GroupId=1
    @GET("Group/GroupStudentList")
    suspend fun groupStudentList(
        @Query("AccademicId") AccademicId: Int,
        @Query("GroupId") GroupId: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<GroupStudentModel>

    ///Group/GroupStudentDropById?GmemberId=


    @GET("{path}")
    suspend fun deleteGroupStudentItem(
        @Path("path") path : String?,
        @Query("GmemberId") GmemberId: Int,
        //category
    ): Response<String>


    ///http://demostaff.passdaily.in/ElixirApi/AnnualResult/GetResult?AccademicId=8&ClassId=12

    @GET("AnnualResult/GetResult")
    suspend fun updateResultList(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        //category
    ): Response<ResultListModel>


    //  Result_send = Global.url+"AnnualResult/SendResult?AccId=" + s_aid + "&ClsId=" + scid;
    @GET("AnnualResult/SendResult")
    suspend fun publishResult(
        @Query("AccId") AccademicId: Int,
        @Query("ClsId") ClassId: Int,
        //category
    ): Response<String>


//////////////public member List
    //http://demostaff.passdaily.in/ElixirApi/Group/PublicMemberList?AccademicId=8&GroupId=3
    @GET("Group/PublicMemberList")
    suspend fun publicMember(
        @Query("AccademicId") AccademicId: Int,
        @Query("GroupId") GroupId: Int,
        //category
    ): Response<PublicMembersModel>

    //http://demostaff.passdaily.in/ElixirApi/PublicGroup/PublicMemberList?AccademicId=8&GroupId=3
    @GET("PublicGroup/PublicMemberList")
    suspend fun publicGroupMember(
        @Query("AccademicId") AccademicId: Int,
        @Query("GroupId") GroupId: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<PublicMembersModel>

    // URL_MESSAGE=Global.url+"BulkMessage/SendMessageClassWise?AccademicId="+s_aid+
   //"&ClassId=" + scid +"&MessageId="+Global.message_id+ "&AdminId="+Global.Admin_id;
    @GET("{path}")
    suspend fun sendMessageClassWise(
        @Path("path") path : String?,
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("MessageId") MessageId: Int,
        @Query("AdminId") AdminId: Int,
        //category
    ): Response<String>

    //    URL_MESSAGE=Global.url+"BulkMessage/SendClassSectionWise?AccademicId="+s_aid+
    //"&ClassName=" + classfeed.get(posit).get("CLASS_NAME") +"&MessageId="+Global.message_id+
    // "&AdminId="+Global.Admin_id+"&Dummy=0";
    @GET("{path}")
    suspend fun sendClassSectionWise(
        @Path("path") path : String?,
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("MessageId") MessageId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("Dummy") Dummy: Int
        //category
    ): Response<String>

    //  URL_MESSAGE= Global.url + "Virtual/SendVirtualClassWise?AccademicId=" + s_aid+
    //"&ClassName=" + sname + "&MailId=" +Global.mail_id + "&AdminId=" + Global.Admin_id + "&Dummy=0";
    //http://carmelstaff.passdaily.in/ElixirApi/Virtual/SendVirtualClassWise?
    // ClassId=38&AccademicId=10&MailId=15&AdminId=7&SchoolId=1
    @GET("Virtual/SendVirtualClassWise")
    suspend fun sendVirtualClassWise(
        @Query("ClassId") ClassId: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("MailId") MessageId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<String>


    //  GET_Message = Global.url+"Send/SendVirtualMailToParents?ClassId="+scid+
    //  "&StudentId="+feedlist4.get(position).get("STUDENT_ID")
    //                +"&AdminId="+ Global.Admin_id+"&MailId="+Global.mail_id;
    @GET("Send/SendVirtualMailToParents")
    suspend fun sendVirtualMailToParents(
        @Query("ClassId") ClassId: Int,
        @Query("StudentId") StudentId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("MailId") MessageId: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<String>


    // URL_MESSAGE=Global.url+"Virtual/SendVirtualClassSectionWise?ClassId="+scid+
    //"&AccademicId=" + s_aid +"&MailId="+ Global.mail_id+ "&AdminId="+Global.Admin_id;
    //http://carmelstaff.passdaily.in/ElixirApi/Virtual/SendVirtualClassSectionWise?
//    // AccademicId=10&ClassName=Test%20Class&MailId=15&AdminId=7&Dummy=0&SchoolId=1
    @GET("Virtual/SendVirtualClassSectionWise")
    suspend fun sendVirtualClassSectionWise(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassName") ClassName: String,
        @Query("MailId") MessageId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("Dummy") Dummy: Int,
        @Query("SchoolId") SchoolId: Int,
        //schoolId
        //category
    ): Response<String>

    /////////////////message send class staff side
    // GET_class_url=Global.url+"Send/SendMessageClassWise?ClassId="+scid+"&AccademicId="+s_aid+"&MessageId="+
    //Global.message_id+"&AdminId="+Global.Admin_id+"&dummy=0";

    @GET("Send/SendMessageClassWise")
    suspend fun sendMessageClassByTeacher(
        @Query("ClassId") ClassId: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("MessageId") MessageId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("dummy") dummy: Int,
        //category
    ): Response<String>


    /////////////notification send to class Staff side
    // GET_class_url=Global.url+"Virtual/SendVirtualClassWise?ClassId="+scid+"&AccademicId="+s_aid
   //+"&MailId="+Global.mail_id+"&AdminId="+Global.Admin_id;
    @GET("Virtual/SendVirtualClassWise")
    suspend fun sendNotificationClassByTeacher(
        @Query("ClassId") ClassId: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("MailId") MailId: Int,
        @Query("AdminId") AdminId: Int,
        //category
    ): Response<String>


    /////Student Remarks

    //http://demostaff.passdaily.in/ElixirApi/Details/GetDetails?ClassId=12&AccademicId=8
    @GET("Details/GetDetails")
    suspend fun studentClasswise(
        @Query("ClassId") ClassId: Int,
        @Query("AccademicId") AccademicId: Int,
        //category
    ): Response<StudentsClasswiseDetailsModel>


    //http://demostaff.passdaily.in/ElixirApi/Marks/GetStudentsRemarks?AccademicId=8&ClassId=12&ExamId=1

    @GET("Marks/GetStudentsRemarks")
    suspend fun remarkRegister(
        @Query("AccademicId") ClassId: Int,
        @Query("ClassId") AccademicId: Int,
        @Query("ExamId") ExamId: Int,
        //category
    ): Response<RemarkRegisterListModel>

    //absent list
    ///Details/GetAbsentees?ClassId=12&AccademicId=8&From=01/Mar/2022&To=15/Sep/2022&Dummy=0
    @GET("Details/GetAbsentees")
    suspend fun absenteesList(
        @Query("ClassId") AccademicId: Int,
        @Query("AccademicId") ClassId: Int,
        @Query("From") From: String,
        @Query("To") To: String,
        @Query("Dummy") Dummy: Int,
        //category
    ): Response<AbsenteesListModel>


    //Details/GetSummary?ClassId=12&Month=9&AccademicId=8&AccademicYear=2021-2022

    @GET("Details/GetSummary")
    suspend fun attendanceSummeryDetail(
        @Query("ClassId") ClassId: Int,
        @Query("Month") Month: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("AccademicYear") AccademicYear: String,
        //category
    ): Response<AttendanceSummaryModel>


    //http://demostaff.passdaily.in/ElixirApi/Attendance/GetStudentsForAttendance?ClassId=12&AccademicId=8&Date=2022-09-12
    @GET("Attendance/GetStudentsForAttendance")
    suspend fun studentDetails(
        @Query("ClassId") ClassId: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("Date") Date: String,
        //category
    ): Response<MarkAbsentModel>



    /////manage ALbum

    ////http://demostaff.passdaily.in/ElixirApi/Album/GetAlbumCategoryList?AccademicId=8&AlbumType=0
    //
    @GET("Album/GetAlbumCategoryList")
    suspend fun manageAlbumList(
        @Query("AccademicId") AccademicId: Int,
        @Query("AlbumType") AlbumType: Int,
        //category
    ): Response<AlbumCategoryModel>


    //Teacher/AlbumCategoryDelete?AlbumCatId=
    @GET("{path}")
    suspend fun albumCategoryDelete(
        @Path("path") path : String?,
        @Query("AlbumCatId") AlbumCatId: Int,
        //category
    ): Response<String>

///http://demostaff.passdaily.in/ElixirApi/Image/ImageCategoryList?AlbumCatId=0
    @GET("{path}")
    suspend fun imageCategory(
        @Path("path") path : String?,
        @Query("AlbumCatId") AlbumCatId: Int,
        //category
    ): Response<ImageCategoryModel>

    //http://demostaff.passdaily.in/ElixirApi/Virtual/ImageGetAll?AlbumCatId=1
///http://demostaff.passdaily.in/ElixirApi/AlbumVideo/VideoGetAll?AlbumCatId=2
    @GET("{path}")
    suspend fun galleryImageVideo(
        @Path("path") path : String?,
        @Query("AlbumCatId") AlbumCatId: Int,
        //category
    ): Response<GalleryImageVideoModel>
//
    /////Manage About us and FAQ

    //http://demostaff.passdaily.in//ElixirApi/AboutFaq/AboutFaqList?AdminId=5

    @GET("AboutFaq/AboutFaqList")
    suspend fun aboutUsFaq(
        @Query("AdminId") AdminId: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<AboutusFaqListModel>


    //AboutFaq/DeleteAboutFaq?AboutFaqId=

    @GET("{path}")
    suspend fun aboutUsFaqDelete(
        @Path("path") path : String?,
        @Query("AboutFaqId") AboutFaqId: Int,
        //category
    ): Response<String>


    ///Student Remark
    //http://vstaff.passdaily.in/ElixirApi/Remarks/RemarksGet?AccademicId=5&ClassId=5
    @GET("Remarks/RemarksGet")
    suspend fun studentRemark(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        //category
    ): Response<StudentRemarksModel>


    ////Student info
//http://app.nsrtrading.in/ElixirApi/StudentList/StudentGet
    @Headers("Content-Type:application/json")
    @POST("StudentList/StudentGet")
    suspend fun studentInfo(
        @Body accademicRe: RequestBody?,
    ): Response<StudentsInfoListModel>


    //StudentList/StudentDropById?StudentId="+student_id+"&AccademicId="
    @GET("{path}")
    suspend fun studentInfoDelete(
        @Path("path") path : String?,
        @Query("StudentId") StudentId: Int,
        @Query("AccademicId") AccademicId: Int,
        //category
    ): Response<String>


    //Staff/StaffList
    @Headers("Content-Type:application/json")
    @POST("Staff/StaffList")
    suspend fun staffDetailsList(
        @Body accademicRe: RequestBody?,
    ): Response<ManageStaffListModel>


    //Staff/DeleteStaff?StaffId=

    @GET("{path}")
    suspend fun deleteStaff(
        @Path("path") path : String?,
        @Query("StaffId") StaffId: Int,
        //category
    ): Response<String>


    //////////////pta
    //Staff/StaffList
    @Headers("Content-Type:application/json")
    @POST("Pta/PtaList")
    suspend fun ptaDetailList(
        @Body accademicRe: RequestBody?,
    ): Response<ManagePtaListModel>


    @GET("{path}")
    suspend fun deletePta(
        @Path("path") path : String?,
        @Query("PtaMemberId") PtaMemberId: Int,
        //category
    ): Response<String>

    ///Manage Conveyor List
    /////http://demostaff.passdaily.in/ElixirApi/StudentList/ConveyorList?ClassId=1&ConveyorName=0&Dummy=0
    @GET("StudentList/ConveyorList")
    suspend fun conveyorDetailsList(
        @Query("ClassId") ClassId: Int,
        @Query("ConveyorName") ConveyorName: String,
        @Query("Dummy") Dummy: Int,
        //category
    ): Response<ManageConveyorListModel>


    ///Student Full details List
    //http://demostaff.passdaily.in/ElixirApi/StudentEdit/StudentListForConveyor?AccademicId=8&ClassId=9
    @GET("StudentEdit/StudentListForConveyor")
    suspend fun getStudentList(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        //category
    ): Response<GetStudentsListModel>


    /////////ManageConveyor/ConveyorDelete?ConveyorId=
    @GET("{path}")
    suspend fun conveyorDelete(
        @Path("path") path : String?,
        @Query("ConveyorId") ConveyorId: Int,
        //category
    ): Response<String>


//    /////get guardian
    //http://demostaff.passdaily.in/ElixirApi/StudentSet/StudentQuickUpdate?ClassId=1&AccademicId=8
    @GET("StudentSet/StudentQuickUpdate")
    suspend fun guardianList(
        @Query("ClassId") ClassId: Int,
        @Query("AccademicId") AccademicId: Int,
        //category
    ): Response<GuardianListModel>


    //Academic Management
    ////Subject list
    @Headers("Content-Type:application/json")
    @POST("SubjectList/SubjectList")
    suspend fun subjectDetailList(
        @Body accademicRe: RequestBody?,
    ): Response<SubjectListModel>


    //http://demostaff.passdaily.in/ElixirApi/SubjectCategory/SubjectCatList?SubjectCatName=0
    @GET("SubjectCategory/SubjectCatList")
    suspend fun subjectCategory(
        @Query("SubjectCatName") SubjectCatName: Int,
        @Query("SchoolId") SchoolId: Int,
        //&SchoolId=1
        //category
    ): Response<SubCategoryListModel>

    //http://demostaff.passdaily.in/ElixirApi/ManageClass/ClassList?ClassName=0&ClassStatus=0
    @GET("ManageClass/ClassList")
    suspend fun classList(
        @Query("ClassName") ClassName: Int,
        @Query("ClassStatus") ClassStatus: Int,
        //category
    ): Response<ClassListModel>

    //    http://demostaff.passdaily.in/ElixirApi/Message/GetClassListByTeacher?AdminId=2
    @GET("Message/GetClassListByTeacher")
    suspend fun getClassList(
        @Query("AdminId") AdminId: Int
        //category
    ): Response<ClassListModel>


    //http://demostaff.passdaily.in/ElixirApi/Accademic/AccademicYearGet?AccademicId=0&SchoolId=1
    @GET("Accademic/AccademicYearGet")
    suspend fun academicList(
        @Query("AccademicId") AccademicId: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<AcademicListModel>


    ////promote Student

    //  Get_promote_studentURL=Global.url+"ManageStudents/PromoteStudents?
    //  AccademicIdFrom="+froms_aid+"&ClassIdFrom="+fromscid+
// "&AccademicIdTo="+tos_aid+"&ClassIdTo="+toscid;
    @GET("ManageStudents/PromoteStudents")
    suspend fun promoteStudent(
        @Query("AccademicIdFrom") AccademicIdFrom: Int,
        @Query("ClassIdFrom") ClassIdFrom: Int,
        @Query("AccademicIdTo") AccademicIdTo: Int,
        @Query("ClassIdTo") ClassIdTo: Int,
        //category
    ): Response<String>

    //////Promote Selected Student
    //http://demostaff.passdaily.in/ElixirApi/Promote/StudentsForPromote?AccademicId=8&ClassId=1
    @GET("Promote/StudentsForPromote")
    suspend fun studentsPromoteList(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("SchoolId") SchoolId: Int,

        //category
    ): Response<PromoteStudentListModel>

    //   GET_Student_Promote_URL=Global.url+"Promote/PromoteStudents?StudentId="+feedlist5.get(i).get("STUDENT_ID")+
    //                "&StudentRollNo="+feedlist5.get(i).get("STUDENT_ROLL_NUMBER") +"&AccademicId="+ps_aid+"&ClassId="+pscid;
    @GET("Promote/PromoteStudents")
    suspend fun sendStudentsPromote(
        @Query("StudentId") StudentId: Int,
        @Query("StudentRollNo") StudentRollNo: Int,
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        //category
    ): Response<String>

    ///http://demostaff.passdaily.in/ElixirApi/StudentSet/StudentsForReallocation?AccademicId=8&ClassId=1&Gender=-1&RollNo=-1
    @GET("Promote/StudentsForPromote")
    suspend fun studentReallocationList(
        @Query("AccademicId") AccademicId: Int,
        @Query("ClassId") ClassId: Int,
        @Query("Gender") Gender: Int,
        @Query("RollNo") RollNo: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<ReallocationStudentListModel>

    //"StudentSet/RollNumberUpdate?StudAccademicId="+"&RollNumber="+edit.getText().toString();

    @GET("StudentSet/RollNumberUpdate")
    suspend fun rollNumberUpdate(
        @Query("StudAccademicId") StudAccademicId: Int,
        @Query("RollNumber") RollNumber: String,
        //category
    ): Response<String>


    ///http://demostaff.passdaily.in/ElixirApi/Message/MessageGet?StaffId=2&Title=0&Date=0
    @GET("Message/MessageGet")
    suspend fun messageListStaff(
        @Query("StaffId") StaffId: Int,
        @Query("Title") Title: Int,
        @Query("Date") Date: Int,
        //category
    ): Response<MessageListModel>

    //http://demostaff.passdaily.in/ElixirApi/Message/MessageDeliveryReportGet?Date=0&ClassId=0&MobileNo=0&StaffId=2
    @GET("Message/MessageDeliveryReportGet")
    suspend fun messageDeliveryListStaff(
        @Query("Date") Date: Int,
        @Query("ClassId") ClassId: Int,
        @Query("MobileNo") MobileNo: Int,
        @Query("StaffId") StaffId: Int,
        //category
    ): Response<MessageDeliveryListModel>

    /////////////Regional Message List
    //http://demostaff.passdaily.in/ElixirApi/MessageUnicode/MessageGet?StaffId=1&Title=0&Date=0
    @GET("MessageUnicode/MessageGet")
    suspend fun regionalMessageListStaff(
        @Query("StaffId") StaffId: Int,
        @Query("Title") Title: Int,
        @Query("Date") Date: Int,
        //category
    ): Response<MessageListModel>

    ///http://demostaff.passdaily.in/ElixirApi/Message/MessageDeliveryReportGet?Date=0&ClassId=0&MobileNo=0&StaffId=1
    @GET("Message/MessageDeliveryReportGet")
    suspend fun regionalMessageDeliveryListStaff(
        @Query("Date") Date: Int,
        @Query("ClassId") ClassId: Int,
        @Query("MobileNo") MobileNo: Int,
        @Query("StaffId") StaffId: Int,
        //category
    ): Response<MessageDeliveryListModel>


    /////Voice Message Template List
//http://demostaff.passdaily.in/ElixirApi/Voice/VoiceGet?StaffId=1&Title=0&Date=0
    @GET("Voice/VoiceGet")
    suspend fun voiceMessageListStaff(
        @Query("StaffId") StaffId: Int,
        @Query("Title") Title: Int,
        @Query("Date") Date: Int,
        //category
    ): Response<VoiceMessageListModel>

    ///Voice Message Delivery List
    ///http://demostaff.passdaily.in/ElixirApi/Voice/VoiceDeliveryReportGet?Date=0&ClassId=0&MobileNo=0&StaffId=1
    @GET("Voice/VoiceDeliveryReportGet")
    suspend fun voiceMessageDeliveryListStaff(
        @Query("Date") Date: Int,
        @Query("ClassId") ClassId: Int,
        @Query("MobileNo") MobileNo: Int,
        @Query("StaffId") StaffId: Int,
        //category
    ): Response<MessageDeliveryListModel>

    //http://demoapp.passdaily.in/PassDailyParentsApi/Gps/GetAllVehichles?Dummy=0
    @GET("Gps/GetAllVehichles")
    suspend fun vehicleList(
        @Query("Dummy") Dummy: Int,
        //category
    ): Response<VehichlesModel>


    //http://demostaff.passdaily.in/ElixirApi/Accademic/MeetingListByAdmin?AccademicId=12&adminId=1

    @GET("Accademic/MeetingListByAdmin")
    suspend fun meetingListByAdmin(
        @Query("AccademicId") AccademicId: Int,
        @Query("adminId") adminId: Int,
        //category
    ): Response<CurrentMeetingListModel>

    //////punching staff attendance
    //http://staff.teachdaily.in//ElixirApi/Staff/PunchingStatus?AdminId=1&SchoolId=1
    @GET("Staff/PunchingStatus")
    suspend fun punchAttendance(
        @Query("AdminId") AdminId: Int,
        @Query("SchoolId") SchoolId: Int,
        //category
    ): Response<StaffPunchingStatusModel>

    ///http://staff.teachdaily.in//ElixirApi/Staff/PunchIn



    ///feb 28 2024
    //http://meridianstaff.passdaily.in//ElixirApi/StaffInbox/StaffInboxViewById?InboxId=716&AdminId=1&StaffId=1
    @GET("StaffInbox/StaffInboxViewById")
    suspend fun staffInboxViewById(
        @Query("InboxId") InboxId: Int,
        @Query("AdminId") AdminId: Int,
        @Query("StaffId") StaffId: Int,
        //category
    ): Response<InboxDetailsStaffNewModel>

    ///http://localhost:17842/ElixirApi/Inbox/InboxGetByIdNew?VirtualMailId=98
    @GET("Inbox/InboxGetByIdNew")
    suspend fun notificationUpdate(
        @Query("VirtualMailId") virtualMailId: Int,
        //category
    ): Response<NotificationUpdateModel>


    //http://meridianapp.passdaily.in/PassDailyParentsApi/Inbox/AppInboxGetByIdNew?Inbox_Id=10515&StudentId=1734
    @GET("Inbox/AppInboxGetByIdNew")
    suspend fun inboxGetByDetails(
        @Query("Inbox_Id") inboxId: Int,
        @Query("StudentId") studentId: Int
        //category
    ): Response<InboxDetailsNewModel>

    //Staff/LeaveViewById?LeaveId=5
    @GET("Staff/LeaveViewById")
    suspend fun leaveDetails(
        @Query("LeaveId") LeaveId: Int,
        //category
    ): Response<LeaveStaffDetailsModel>


    //Staff/StaffLeaveList
    @Headers("Content-Type:application/json")
    @POST("Staff/StaffLeaveList")
    suspend fun staffLeaveLetterList(
        @Body accademicRe: RequestBody?,
    ): Response<LeaveStaffListModel>


}