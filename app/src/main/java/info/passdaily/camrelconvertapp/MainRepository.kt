package info.passdaily.camrelconvertapp

import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import okhttp3.RequestBody

class MainRepository(val apiHelper: ApiClient) {

    ///login page
    suspend fun getDashBoardNew(studentId: Int) = apiHelper.getDashBoardNew(studentId)

    ////logout page
    suspend fun getLogOutUser(pLoginID1: Int) = apiHelper.getLogOutUser(pLoginID1)
    //block
    suspend fun getBlockStatus(accademicId : Int,studentId: Int) = apiHelper.getBlockStatus(accademicId,studentId)
    ///dashboard
    suspend fun getChildrenDetailsNew(ParentId: Int) = apiHelper.getChildrenDetailsNew(ParentId)
    suspend fun getLoginDetails(username: String,password: String,schoolCode : String) = apiHelper.getLoginDetails(username,password,schoolCode)

    //register
    suspend fun getRegistrationDetails(mobileNumber: String,schoolCode : String) = apiHelper.getRegistrationDetails(mobileNumber,schoolCode)

    //staff Account Create
    suspend fun getStaffAccountCreate(mobileNumber: String,schoolCode : String) = apiHelper.getStaffAccountCreate(mobileNumber,schoolCode)

    ///
    suspend fun getFaqItems(studentId : Int, aboutType : Int) = apiHelper.getFaqItems(studentId,aboutType)

    suspend fun getContactUsItems(studentId : Int) = apiHelper.getContactUsItems(studentId)

    ///
    suspend fun getAbsents(studentId: Int,academicId: Int) = apiHelper.getAbsents(studentId,academicId)

    suspend fun getAccademic(academicId: Int) = apiHelper.getAccademic(academicId)

    ////study material
    suspend fun getStudyMaterialListNew(academicId: Int,classId : Int,studentId: Int,subjectId : Int)
    = apiHelper.getStudyMaterialListNew(academicId,classId,studentId,subjectId)

    suspend fun getMaterialList(materialId : Int)
            = apiHelper.getMaterialList(materialId)

    /////////////////
    /////get Parent EventListAll
    suspend fun getParentEventListAll(academicId: Int,classId : Int,studentId: Int)
            = apiHelper.getParentEventListAll(academicId,classId,studentId)

    //////////get Event List All
    suspend fun getManageEvent(academicId: Int,eventType:Int,adminId : Int,schoolId:Int) = apiHelper.getManageEvent(academicId,
        eventType,adminId,schoolId)

    ///Delete Event from list
    suspend fun deleteEventItem(eventId : Int,adminId :Int,schoolId:Int) = apiHelper.deleteEventItem(eventId,adminId,schoolId)


    suspend fun getSubjectModelNew(classId : Int,studentId: Int) = apiHelper.getSubjectModelNew(classId,studentId)

    suspend fun getJoinLiveNew(academicId: Int,classId : Int,subjectId: Int,studentId : Int)
            = apiHelper.getJoinLiveNew(academicId,classId,subjectId,studentId)

    suspend fun getMeetingAttend(zLIVECLASSID : Int,aCCADEMICID: Int
                                 ,STUDENTID : Int, mStatus : String) = apiHelper.getMeetingAttend(zLIVECLASSID,aCCADEMICID, STUDENTID,mStatus)

    suspend fun getAssignmentListNew(academicId: Int,classId : Int,studentId: Int,subjectId : Int)
            = apiHelper.getAssignmentListNew(academicId,classId,studentId,subjectId)

    suspend fun getAssignmentDetails(academicId: Int,classId : Int,studentId: Int,assignmentId : Int)
            = apiHelper.getAssignmentDetails(academicId,classId,studentId,assignmentId)


    suspend fun getObjectiveListNew(academicId: Int,classId : Int,subjectId: Int,studentId : Int)
            = apiHelper.getObjectiveListNew(academicId,classId,subjectId,studentId)

    suspend fun getDescriptiveListNew(academicId: Int,classId : Int,subjectId: Int,studentId : Int)
            = apiHelper.getDescriptiveListNew(academicId,classId,subjectId,studentId)

    suspend fun getActivityListNew(studentId: Int,academicId : Int)
            = apiHelper.getActivityListNew(academicId,studentId)

    suspend fun getHolidayListNew(studentId: Int,academicId : Int)
            = apiHelper.getHolidayListNew(academicId,studentId)

    suspend fun getLibraryNew(studentId : Int)
    = apiHelper.getLibraryNew(studentId)

    suspend fun getInboxDetailsNew(jsonObject: RequestBody) = apiHelper.getInboxDetailsNew(jsonObject)

    suspend fun getInboxReadStatus(studentId: Int,vIRTUALMAILSENTID : Int) =
        apiHelper.getInboxReadStatus(studentId,vIRTUALMAILSENTID)

    ///post
    suspend fun getEnquiryListNew(accademicRe: RequestBody?)
            = apiHelper.getEnquiryListNew(accademicRe)

    suspend fun getLeaveListNew(accademicRe: RequestBody?)
            = apiHelper.getLeaveListNew(accademicRe)


    suspend fun getCommonPostFun(url : String,accademicRe: RequestBody?)
            = apiHelper.getCommonPostFun(url,accademicRe)


    suspend fun getDeleteAssignmentFile(assignmentFileId: Int)
            = apiHelper.getDeleteAssignmentFile(assignmentFileId)

    suspend fun getDeleteEnquiryFun(url : String,enquiryId : Int)
            = apiHelper.getDeleteEnquiryFun(url,enquiryId)

    suspend fun getDeleteLeaveFun(url : String,leaveId: Int)
            = apiHelper.getDeleteLeaveFun(url,leaveId)

    suspend fun getSubmitAndNext(submitAndNextItems: RequestBody?)
            = apiHelper.getSubmitAndNext(submitAndNextItems)

    suspend fun getDeleteOption(optionItems: RequestBody?)
            = apiHelper.getDeleteOption(optionItems)

    suspend fun getSaveAndExit(saveExitItems: RequestBody?)
            = apiHelper.getSaveAndExit(saveExitItems)

    suspend fun getDoAutoEnd(doAutoEndItems: RequestBody?)
            = apiHelper.getDoAutoEnd(doAutoEndItems)

    suspend fun getFinishExam(finishExamItems: RequestBody?)
            = apiHelper.getFinishExam(finishExamItems)

    /////////////////
    suspend fun getObjectiveDetailsNew(studentId: Int,OExamId : Int)
            = apiHelper.getObjectiveDetailsNew(studentId,OExamId)

    suspend fun getQuestionListNew(OexamId: Int,OexamAttemptId : Int)
            = apiHelper.getQuestionListNew(OexamId,OexamAttemptId)

//    suspend fun getOnlineExamStatus(academicId: Int,classId : Int,studentId : Int ,oExamId : Int)
//            = apiHelper.getOnlineExamStatus(academicId,classId,studentId,oExamId)

    suspend fun getOptionsList(QuestionId: Int,OexamAttemptId : Int)
            = apiHelper.getOptionsList(QuestionId,OexamAttemptId)

    suspend fun getObjectiveExamResult(academicId: Int,studentId: Int,oExamId: Int,OexamAttemptId : Int)
            = apiHelper.getObjectiveExamResult(academicId,studentId,oExamId,OexamAttemptId)


    suspend fun getDQuestionListNew(examId: Int,examAttemptId : Int)
            = apiHelper.getDQuestionListNew(examId,examAttemptId)

//    suspend fun getTakeAnswer(examId: Int,examAttemptId : Int,questionId : Int)
//            = apiHelper.getTakeAnswer(examId,examAttemptId,questionId)

    suspend fun getDeleteFile(path: String,deleteFile: RequestBody?)
            = apiHelper.getDeleteFile(path,deleteFile)

    suspend fun getSubmitAnswerDExam(submitItems: RequestBody?)
            = apiHelper.getSubmitAnswerDExam(submitItems)

    suspend fun getFinishDExam(finishExamItems: RequestBody?)
            = apiHelper.getFinishDExam(finishExamItems)

    suspend fun getDoAutoEndDExam(doAutoEndItems: RequestBody?)
            = apiHelper.getDoAutoEndDExam(doAutoEndItems)

    suspend fun getTakeAnswer(dExamId: Int,dExamAttemptId: Int,questionId: Int)
            = apiHelper.getTakeAnswer(dExamId,dExamAttemptId,questionId)


    suspend fun getSaveAndExitDExam(saveExitItems: RequestBody?)
            = apiHelper.getSaveAndExitDExam(saveExitItems)

    suspend fun getDescriptiveExamResult(academicId: Int,studentId: Int,dExamId: Int,dExamAttemptId : Int)
            = apiHelper.getDescriptiveExamResult(academicId,studentId,dExamId, dExamAttemptId)
    ///////

    suspend fun getDescriptiveDetailsNew(studentId: Int,ExamId : Int)
            = apiHelper.getDescriptiveDetailsNew(studentId,ExamId)

    /////zoom
    suspend fun getZoomScheduleList(academicId: Int,classId : Int,studentId: Int)
            = apiHelper.getZoomScheduleList(academicId,classId,studentId)

    suspend fun getOnlineVideo(academicId: Int,classId : Int,studentId: Int,subjectId : Int)
            = apiHelper.getOnlineVideo(academicId,classId,studentId,subjectId)

    suspend fun getSubjects(classId : Int,studentId: Int)
            = apiHelper.getSubjects(classId,studentId)

    suspend fun getChapter(accademicId : Int, classId: Int,subjectId : Int)
            = apiHelper.getChapter(accademicId,classId,subjectId)

    suspend fun getCommonAlbumCategory(url : String,accademicId : Int)
            = apiHelper.getCommonAlbumCategory(url,accademicId)

    suspend fun getGalleryImgVidList(url : String,accademicId : Int)
            = apiHelper.getGalleryImgVidList(url,accademicId)

    suspend fun getTrackMap(dummy : Int) = apiHelper.getTrackMap(dummy)

    suspend fun getGpsLocation(VEHICLE_ID : Int,TERMINAL_ID: String,SIM_NUMBER: String)
            = apiHelper.getGpsLocation(VEHICLE_ID,TERMINAL_ID,SIM_NUMBER)


    suspend fun getFeesDetails(CLASSID : Int,ACADEMICID: Int,STUDENTID: Int,STUDENT_ROLL_NO : Int)
            = apiHelper.getFeesDetails(CLASSID,ACADEMICID,STUDENTID,STUDENT_ROLL_NO)

    ///
    suspend fun getExamList(examId : Int) = apiHelper.getExamList(examId)

    suspend fun getExamMarkDetails(
        ACADEMICID: Int,
        CLASSID: Int,
        EXAMID: Int,
        STUDENT_ROLL_NO: Int,
        STUDENT_ID: Int
    )
            = apiHelper.getExamMarkDetails(CLASSID,ACADEMICID,EXAMID,STUDENT_ROLL_NO,STUDENT_ID)


    suspend fun getAnnualReport(STUDENT_ID : Int,ACADEMICID : Int)
            = apiHelper.getAnnualReport(STUDENT_ID,ACADEMICID)

    suspend fun getConveyorGet(StudentId : Int,ClassId : Int)
            = apiHelper.getConveyorGet(StudentId,ClassId)



    //////////////////Staff URL Requests
    suspend fun getLoginDetailStaff(username: String,password: String, schoolCode : String) = apiHelper.getLoginDetailStaff(username,password,schoolCode)

    suspend fun getDashBoardStaff(adminId: Int) = apiHelper.getDashBoardStaff(adminId)

    suspend fun getLiveClassDetails(adminId: Int) = apiHelper.getLiveClassDetails(adminId)

    suspend fun getClassDetails(adminId: Int,accademicId :Int) = apiHelper.getClassDetails(adminId,accademicId)

    //getMeetingListByAdmin(accademicId,adminId))
    suspend fun getMeetingListByAdmin(accademicId: Int,adminId :Int) = apiHelper.getMeetingListByAdmin(accademicId,adminId)

    ///post
    suspend fun getStartLiveMeeting(accademicRe: RequestBody?)
            = apiHelper.getStartLiveMeeting(accademicRe)

    ////Online Video
    suspend fun getYearClassExam(adminId: Int,schoolId: Int) = apiHelper.getYearClassExam(adminId,schoolId)

    // getSubjectStaff
    suspend fun getSubjectStaff(classId : Int,adminId: Int) = apiHelper.getSubjectStaff(classId,adminId)

    // Assignment getSubjectStaff Post method
    suspend fun postSubjectStaff(accademicRe: RequestBody?) = apiHelper.postSubjectStaff(accademicRe)

    ///question Type Staff
    suspend fun getQuestionTypeStaff(adminId: Int) = apiHelper.getQuestionTypeStaff(adminId)

    //get Object Option List
    suspend fun getObjectOptionList(questionId: Int) = apiHelper.getObjectOptionList(questionId)

    // getChapterStaff
    suspend fun getChapterStaff(aCCADEMICID : Int,cLASSID : Int,sUBJECTID : Int,sCHOOLID : Int) =
        apiHelper.getChapterStaff(aCCADEMICID,cLASSID,sUBJECTID,sCHOOLID)

    // getVideoListStaff
    suspend fun getVideoListStaff(aCCADEMICID : Int,cLASSID : Int,sUBJECTID : Int,chapter : Int) =
        apiHelper.getVideoListStaff(aCCADEMICID,cLASSID,sUBJECTID,chapter)

    // youtube View Report
    suspend fun getYoutubeReport(YoutubeId : Int) =
        apiHelper.getYoutubeReport(YoutubeId)

    //unPlayedVideoList
    suspend fun getUnPlayedVideoList(accademicId : Int,classId : Int,YoutubeId : Int) =
        apiHelper.getUnPlayedVideoList(accademicId,classId,YoutubeId)

    // youtube Full Log List
    suspend fun getYoutubeFullLog(YoutubeLogId : Int) =
        apiHelper.getYoutubeFullLog(YoutubeLogId)

//    suspend fun getDeleteYoutubeVideoFun(url : String,youtubeId: Int)
//            = apiHelper.getDeleteYoutubeVideoFun(url,youtubeId)

    /////objective Exam
    suspend fun getOnlineExamListStaff(
        aCCADEMICID: Int,
        cLASSID: Int,
        sUBJECTID: Int,
        schoolId: Int) =
        apiHelper.getOnlineExamListStaff(aCCADEMICID,cLASSID,sUBJECTID,schoolId)

    /////objective Question List
    suspend fun getObjQuestionOptionListStaff(aCCADEMICID : Int,cLASSID : Int,sUBJECTID : Int,oEXAMID : Int) =
        apiHelper.getObjQuestionOptionListStaff(aCCADEMICID,cLASSID,sUBJECTID,oEXAMID)

    suspend fun getOnlineExamResultStaff(OexamId : Int) = apiHelper.getOnlineExamResultStaff(OexamId)

    suspend fun getUnattendedList(aCCADEMICID : Int,cLASSID : Int,OexamId : Int)
            = apiHelper.getUnattendedList(aCCADEMICID,cLASSID,OexamId)

    //   "OnlineExam/OnlineExamPublish?OexamId=" + oEXAMID
    suspend fun getCommonGetFuntion(url : String,map : HashMap<String?, Int>) = apiHelper.getCommonGetFuntion(url,map)


    /////Descriptive Exam
    suspend fun getDescOnlineExamList(aCCADEMICID: Int, cLASSID: Int, sUBJECTID: Int, schoolId: Int) =
        apiHelper.getDescOnlineExamList(aCCADEMICID,cLASSID,sUBJECTID,schoolId)

    suspend fun getDescriptiveExamResult(examId : Int) = apiHelper.getDescriptiveExamResult(examId)

    suspend fun getUnattendedDList(aCCADEMICID : Int,cLASSID : Int,ExamId : Int)
            = apiHelper.getUnattendedDList(aCCADEMICID,cLASSID,ExamId)

    suspend fun getDescQuestionList(aCCADEMICID : Int,cLASSID : Int,sUBJECTID : Int,ExamId : Int)
            = apiHelper.getDescQuestionList(aCCADEMICID,cLASSID,sUBJECTID,ExamId)

    suspend fun getDescGiveMarkPreviewList(aCCADEMICID : Int,cLASSID : Int,dExamId : Int,dExamAttendId : Int)
            = apiHelper.getDescGiveMarkPreviewList(aCCADEMICID,cLASSID,dExamId,dExamAttendId)

    suspend fun giveMarks(adminId : Int,dExamAttendId : Int,mark : String) = apiHelper.giveMarks(adminId,dExamAttendId,mark)

    ///delete Comments files
    suspend fun DeleteCommentsFile(path : String,dExamAttendId : Int) = apiHelper.DeleteCommentsFile(path,dExamAttendId)



    ////take Answer Staff
    suspend fun getTakeAnswerDExam(examId: Int,examAttemptId : Int,questionId : Int)
            = apiHelper.getTakeAnswerDExam(examId,examAttemptId,questionId)

//    //  OnlineExam/AllowOnceMore?OexamAttemptId=
//    suspend fun getAllowOnceMore(OexamAttemptId : Int) = apiHelper.getAllowOnceMore(OexamAttemptId)

    //Study Material List
    suspend fun getMaterialListStaff(aCCADEMICID : Int,cLASSID : Int,sUBJECTID : Int,sCHOOLID :Int) =
        apiHelper.getMaterialListStaff(aCCADEMICID,cLASSID,sUBJECTID,sCHOOLID)

    //studyMaterialView
    suspend fun getStudyMaterialDetails(MaterialId : Int) = apiHelper.getStudyMaterialDetails(MaterialId)

    suspend fun getDeleteFiles(url : String,fileId : Int,fileName : String)
            = apiHelper.getDeleteFiles(url,fileId,fileName)


    //assignment list staff
    suspend fun getAssignmentListStaff(
        aCCADEMICID: Int,
        cLASSID: Int,
        sUBJECTID: Int,
        schoolId: Int
    )
            = apiHelper.getAssignmentListStaff(aCCADEMICID,cLASSID,sUBJECTID,schoolId)

    //getAssignmentSubmissionList
    suspend fun getAssignmentSubmissionList(AssignmentId : Int) = apiHelper.getAssignmentSubmissionList(AssignmentId)


    suspend fun getUnAttendedAssignment(aCCADEMICID : Int,cLASSID : Int,assignmentId : Int)
            = apiHelper.getUnAttendedAssignment(aCCADEMICID,cLASSID,assignmentId)

    suspend fun getStudentSubmissionDetail(accademicId : Int,studentId : Int,assignmentId : Int,assignmentSubmitId : Int)
            = apiHelper.getStudentSubmissionDetail(accademicId,studentId,assignmentId,assignmentSubmitId)

    //getAssignmentSubmissionList
    suspend fun getAssignmentGetById(AssignmentId : Int) = apiHelper.getAssignmentGetById(AssignmentId)

    suspend fun getActivityListStaff(adminId: Int,academicId : Int)
            = apiHelper.getActivityListStaff(adminId,academicId)

    suspend fun getHolidayListStaff(adminId: Int,academicId : Int)
            = apiHelper.getHolidayListStaff(adminId,academicId)

    ///Live Class Report

    suspend fun getLiveClassReport(url : String,accademicRe: RequestBody?)
            = apiHelper.getLiveClassReport(url,accademicRe)

    suspend fun getMeetingAttendedReport(zLiveClassId : Int) = apiHelper.getMeetingAttendedReport(zLiveClassId)

    suspend fun getUnAttendedZoomReport(aCCADEMICID : Int,cLASSID : Int,zLiveClassId : Int)
            = apiHelper.getUnAttendedZoomReport(aCCADEMICID,cLASSID,zLiveClassId)

    suspend fun getInboxStaff(adminId : Int,staffId : Int)
            = apiHelper.getInboxStaff(adminId,staffId)

    suspend fun getInboxReadById(inboxId : Int,adminId : Int,staffId : Int)
            = apiHelper.getInboxReadById(inboxId,adminId,staffId)


    suspend fun getNotificationStaff(staffId : Int)
            = apiHelper.getNotificationStaff(staffId)

    suspend fun getNotificationSentDetails(staffId : Int)
            = apiHelper.getNotificationSentDetails(staffId)

    suspend fun getLeaveListGet(accademicId : Int,classId : Int)
            = apiHelper.getLeaveListGet(accademicId,classId)

    //getEnquiryListGet
    suspend fun getEnquiryListGet(classId: Int, accademicId: Int, schoolId: Int)
            = apiHelper.getEnquiryListGet(classId,accademicId,schoolId)

    //getEnquiryIdGet
    suspend fun getEnquiryGetById(qUERYID : Int)
            = apiHelper.getEnquiryGetById(qUERYID)

    /////Zoom scheduled List
    suspend fun getZoomScheduledStaff(url : String,accademicRe: RequestBody?)
            = apiHelper.getZoomScheduledStaff(url,accademicRe)

    /////zoom scheduled Report
    suspend fun getZoomScheduledReport(zMEETINGID: Int)  = apiHelper.getZoomScheduledReport(zMEETINGID)

    ////mark absent
    suspend fun getMarkAbsentStaff(cLASSID: Int,aCCADEMICID : Int,date: String,schoolId: Int)
            = apiHelper.getMarkAbsentStaff(cLASSID,aCCADEMICID,date,schoolId)

    ////mark Present
    suspend fun getMarkPresentStaff(attendanceId: Int,studentId : Int)
            = apiHelper.getMarkPresentStaff(attendanceId,studentId)

    ////mark Full Present
    suspend fun getMarkFullPresent(accademicId: Int,classId : Int,absentDate : String,adminId : Int)
            = apiHelper.getMarkFullPresent(accademicId,classId,absentDate,adminId)


    ////Student List for Staff
    suspend fun getStudentListStaff(accademicId: Int, classId: Int, schoolId: Int)
            = apiHelper.getStudentListStaff(accademicId,classId,schoolId)

    ////Conveyor List for Staff
    suspend fun getConveyorListStaff(accademicId: Int, schoolId: Int)
            = apiHelper.getConveyorListStaff(accademicId,schoolId)

    ////Staff List
    suspend fun getStaffListStaff(accademicId: Int, schoolId: Int)
            = apiHelper.getStaffListStaff(accademicId,schoolId)

    //class By Super Admin
    suspend fun getClassBySuperAdmin(accademicId: Int)
            = apiHelper.getClassBySuperAdmin(accademicId)

    //    ////get Punching Status By Admin
    suspend fun getPunchingStatusByAdmin(adminId:Int,schoolId:Int,sTAFFID:Int)
            = apiHelper.getPunchingStatusByAdmin(adminId,schoolId,sTAFFID)


    //class Section By Super Admin
    suspend fun getClassSectionBySuperAdmin(accademicId: Int)
            = apiHelper.getClassSectionBySuperAdmin(accademicId)

    //get Class By Teacher
    suspend fun getClassByTeacher(accademicId: Int,adminId : Int)
            = apiHelper.getClassByTeacher(accademicId,adminId)

    ////PTA List
    suspend fun getPtaListGet(accademicId: Int, schoolId: Int)
            = apiHelper.getPtaListGet(accademicId,schoolId)

    ///Mark Register LP/UP
    suspend fun getMarkRegisterLpUp(accademicId: Int,classId : Int,examId : Int,subjectId : Int)
            = apiHelper.getMarkRegisterLpUp(accademicId,classId,examId,subjectId)

    ///Mark Register KG/IV, V-VIII, IX-XII
    suspend fun getMarkRegisterKgIV(accademicId: Int,classId : Int,examId : Int,subjectId : Int)
            = apiHelper.getMarkRegisterKgIV(accademicId,classId,examId,subjectId)

    ///Mark Register CE
    suspend fun getMarkRegisterCE(accademicId: Int,classId : Int,examId : Int,subjectId : Int)
            = apiHelper.getMarkRegisterCE(accademicId,classId,examId,subjectId)

    ///Mark Register CBSE
    suspend fun getMarkRegisterCBSE(accademicId: Int,classId : Int,examId : Int,subjectId : Int)
            = apiHelper.getMarkRegisterCBSE(accademicId,classId,examId,subjectId)

    ///Mark Register Msps Lp/Up and Hs
    suspend fun getMarkRegisterMsps(url : String,accademicId: Int,classId : Int,examId : Int,subjectId : Int)
            = apiHelper.getMarkRegisterMsps(url,accademicId,classId,examId,subjectId)


    //progressReportLpUp
    suspend fun getProgressReportLpUp(accademicId: Int,classId : Int,examId : Int,adminId : Int)
            = apiHelper.getProgressReportLpUp(accademicId,classId,examId,adminId)

    //progressReport KG- XII
    suspend fun getProgressReportHS(accademicId: Int,classId : Int,examId : Int,adminId : Int)
            = apiHelper.getProgressReportHS(accademicId,classId,examId,adminId)

    //progressReport CBSE
    suspend fun getProgressReportCBSE(accademicId: Int,classId : Int,examId : Int,adminId : Int)
            = apiHelper.getProgressReportCBSE(accademicId,classId,examId,adminId)

    ///progressReport Msp Hs
    suspend fun getProgressReportMsp(url: String,accademicId: Int,classId : Int,examId : Int,adminId : Int)
            = apiHelper.getProgressReportMsp(url,accademicId,classId,examId,adminId)

    ///Exam Topper
    suspend fun getExamTopperResponse(url : String,academicId : Int,classId : Int,examId : Int,adminId: Int)
            = apiHelper.getExamTopperResponse(url,academicId,classId,examId,adminId)

    //    getExamGradeCbseResponse
    suspend fun getExamGradeCbseResponse(url : String,academicId : Int,classId : Int,examId : Int,adminId: Int)
            = apiHelper.getExamGradeCbseResponse(url,academicId,classId,examId,adminId)

    ///getExamGradeResponse
    suspend fun getExamGradeResponse(url : String,academicId : Int,classId : Int,examId : Int,adminId: Int)
            = apiHelper.getExamGradeResponse(url,academicId,classId,examId,adminId)

    ///getExamGradeCeResponse
    suspend fun getExamGradeCeResponse(url : String,academicId : Int,classId : Int,examId : Int,adminId: Int)
            = apiHelper.getExamGradeCeResponse(url,academicId,classId,examId,adminId)

    ///getExamGradeMspResponse
    suspend fun getExamGradeMspResponse(url : String,academicId : Int,classId : Int,examId : Int,adminId: Int)
            = apiHelper.getExamGradeMspResponse(url,academicId,classId,examId,adminId)


    ////Send Progress Report
    suspend fun getSendProgressReport(url: String, accademicId: Int, classId: Int, examId: Int)
            = apiHelper.getSendProgressReport(url,accademicId,classId,examId)


    ///manage Group List
    suspend fun getGroupList(groupList: RequestBody?)
            = apiHelper.getGroupList(groupList)

    ///Group Item delete
    suspend fun getDeleteGroupItem(groupId: Int)
            = apiHelper.getDeleteGroupItem(groupId)

    //getGroupList
    suspend fun getGroupListForStudentDelete(url : String,aCCADEMICID: Int,schoolId :Int)
            = apiHelper.getGroupListForStudentDelete(url,aCCADEMICID, schoolId)


    ///get group Student List
    suspend fun getGroupStudentList(aCCADEMICID: Int,gROUPID: Int,schoolId :Int)
            = apiHelper.getGroupStudentList(aCCADEMICID,gROUPID,schoolId)


    //deleteGroupStudentItem
    suspend fun getDeleteGroupStudentItem(url : String,gMemberId: Int)
            = apiHelper.getDeleteGroupStudentItem(url,gMemberId)


    //update Result
    suspend fun getUpdateResultList(aCCADEMICID: Int,cLASSID: Int)
            = apiHelper.getUpdateResultList(aCCADEMICID,cLASSID)


    //Publish Result
    suspend fun getPublishResult(aCCADEMICID: Int,cLASSID: Int)
            = apiHelper.getPublishResult(aCCADEMICID,cLASSID)

    //public member
    suspend fun getPublicMember(aCCADEMICID: Int,gROUPID: Int)
            = apiHelper.getPublicMember(aCCADEMICID,gROUPID)

    //get Public Group Member
    suspend fun getPublicGroupMember(aCCADEMICID: Int,gROUPID: Int,schoolId :Int)
            = apiHelper.getPublicGroupMember(aCCADEMICID,gROUPID,schoolId)

    //sendMessageClassWise
    suspend fun getSendMessageClassWise(url : String,aCCADEMICID: Int,cLASSID: Int,messageId : Int, adminId: Int)
            = apiHelper.getSendMessageClassWise(url,aCCADEMICID,cLASSID,messageId,adminId)

    //get Send Class Section Wise
    suspend fun getSendClassSectionWise(url : String,aCCADEMICID: Int,cLASSID: Int,messageId : Int, adminId: Int)
            = apiHelper.getSendClassSectionWise(url,aCCADEMICID,cLASSID,messageId,adminId)

    //get Send Virtual Class Wise
    suspend fun getSendVirtualClassWise(classId: Int,accademicId: Int,mailId: Int,adminId : Int,schoolId:Int)
            = apiHelper.getSendVirtualClassWise(classId,accademicId,mailId,adminId,schoolId)


    //get Send Virtual Mail To Parents
    suspend fun getSendVirtualMailToParents(classId: Int,studentId: Int,adminId: Int,mailId : Int,schoolId: Int)
            = apiHelper.getSendVirtualMailToParents(classId,studentId,adminId,mailId,schoolId)

    //get Send Virtual Class Section Wise
    suspend fun getSendVirtualClassSectionWise(aCCADEMICID: Int,className: String,mailId : Int, adminId: Int,schoolId: Int)
    // (classId: Int,accademicId: Int,mailId: Int,adminId : Int,schoolId:Int)
            = apiHelper.getSendVirtualClassSectionWise(aCCADEMICID,className,mailId,adminId,schoolId)
    // (accademicId,classId,,mailId,adminId,schoolId)


    ///send Message Class By Teacher     //////staff Side
    suspend fun getSendMessageClassByTeacher(classId: Int,accademicId: Int,messageId: Int,adminId : Int)
            = apiHelper.getSendMessageClassByTeacher(classId,accademicId,messageId,adminId)

    ///send Message Class By Teacher     //////staff Side
    suspend fun getSendNotificationClassByTeacher(classId: Int,accademicId: Int,mailId: Int,adminId : Int)
            = apiHelper.getSendNotificationClassByTeacher(classId,accademicId,mailId,adminId)

    ///Remarks
    //studentClasswise
    suspend fun getStudentClasswise(cLASSID: Int,aCCADEMICID: Int)
            = apiHelper.getStudentClasswise(cLASSID,aCCADEMICID)


    //remark Register
    suspend fun getRemarkRegister(aCCADEMICID: Int,cLASSID: Int,eXAMID: Int)
            = apiHelper.getRemarkRegister(aCCADEMICID,cLASSID,eXAMID)


    //Absentees List
    suspend fun getAbsenteesList(cLASSID: Int,aCCADEMICID: Int,fromDate: String,toDate : String)
            = apiHelper.getAbsenteesList(cLASSID,aCCADEMICID,fromDate,toDate)

    suspend fun getAttendanceSummeryDetail(cLASSID: Int,mONTH: Int,aCCADEMICID: Int,aCCADEMICYEAR : String)
            = apiHelper.getAttendanceSummeryDetail(cLASSID,mONTH,aCCADEMICID,aCCADEMICYEAR)

    //getStudentDetails
    suspend fun getStudentDetails(cLASSID: Int,aCCADEMICID: Int,Date : String)
            = apiHelper.getStudentDetails(cLASSID,aCCADEMICID,Date)

    //getManageAlbumList
    suspend fun getManageAlbumList(aCCADEMICID: Int,aLBUMYYPE: Int)
            = apiHelper.getManageAlbumList(aCCADEMICID,aLBUMYYPE)

    //getImageCategory
    suspend fun getImageCategory(url : String,albumCatId: Int)
            = apiHelper.getImageCategory(url,albumCatId)

    //get Gallery Image Video
    suspend fun getGalleryImageVideo(url : String,albumCatId: Int)
            = apiHelper.getGalleryImageVideo(url,albumCatId)

    //getAlbumCategoryDelete
    suspend fun getAlbumCategoryDelete(url: String,AlbumCatId: Int)
            = apiHelper.getAlbumCategoryDelete(url,AlbumCatId)


    //getAboutUsFaq
    suspend fun getAboutUsFaq(AdminId: Int, schoolId: Int)
            = apiHelper.getAboutUsFaq(AdminId,schoolId)

    //getAboutUsFaqDelete
    suspend fun getAboutUsFaqDelete(url: String,AboutFaqId: Int)
            = apiHelper.getAboutUsFaqDelete(url,AboutFaqId)

    ///Student Remark
    suspend fun getStudentRemark(aCCADEMICID: Int,cLASSID: Int)
            = apiHelper.getStudentRemark(aCCADEMICID,cLASSID)


    //studentInfo
    suspend fun getStudentInfo(studentList: RequestBody?)
            = apiHelper.getStudentInfo(studentList)

    //getStudentInfoDelete
    suspend fun getStudentInfoDelete(url : String, studentId: Int,accademicId :Int)
            = apiHelper.getStudentInfoDelete(url,studentId,accademicId)


    //manage Staff List
    suspend fun getStaffList(staffList: RequestBody?)
       = apiHelper.getStaffList(staffList)

    //deleteStaff
    suspend fun getDeleteStaff(url: String,staffId: Int)
            = apiHelper.getDeleteStaff(url,staffId)

    //getPtaDetailList
    suspend fun getPtaDetailList(studentList: RequestBody?)
            = apiHelper.getPtaDetailList(studentList)

    //deletePta
    suspend fun getDeletePta(url: String,PtaMemberId: Int)
            = apiHelper.getDeletePta(url,PtaMemberId)


    //conveyorDetailsList
    suspend fun getConveyorDetailsList(ClassId: Int)
            = apiHelper.getConveyorDetailsList(ClassId)


    ////Get StudentList
    suspend fun getStudentList(accedamicId: Int,classId : Int) = apiHelper.getStudentList(accedamicId,classId)


    //delete Conveyor
    suspend fun getConveyorDelete(url: String,ConveyorId: Int)
            = apiHelper.getConveyorDelete(url,ConveyorId)


    /////get guardian
    suspend fun getGuardianList(classId : Int,academicId: Int) = apiHelper.getGuardianList(classId,academicId)


    /////Academic Management
    ///get Subject
    suspend fun getSubjectDetailList(subjectList: RequestBody?)
            = apiHelper.getSubjectDetailList(subjectList)

    ///get Category List
    suspend fun getSubjectCategoryList(subjectCatName : Int,schoolId: Int) = apiHelper.getSubjectCategoryList(subjectCatName,schoolId)

    ///get Class List
    suspend fun getClassList(className : Int,classStatus : Int) = apiHelper.getClassList(className,classStatus)


    ///get Academic List
    suspend fun getAcademicList(accademicId : Int,schoolId: Int) = apiHelper.getAcademicList(accademicId,schoolId)


    ///get Promote Student
    suspend fun getPromoteStudent(accademicIdFrom: Int,classIdFrom: Int, accademicIdTo: Int,classIdTo: Int) =
        apiHelper.getPromoteStudent(accademicIdFrom,classIdFrom,accademicIdTo,classIdTo)

    ///get Students For Promote
    suspend fun getStudentsPromoteList(accademicId : Int,classId : Int,schoolId: Int) =
        apiHelper.getStudentsPromoteList(accademicId,classId,schoolId)



    ///get Promote Student
    suspend fun sendStudentsPromote(studentId: Int,studentRollNo: Int, accademicId: Int,classId: Int) =
        apiHelper.sendStudentsPromote(studentId,studentRollNo,accademicId,classId)


    ///get Student Reallocation List
    suspend fun getStudentReallocationList(accademicId: Int,classId: Int,gender: Int,rollNo: Int,schoolId: Int) =
        apiHelper.getStudentReallocationList(accademicId,classId,gender,rollNo,schoolId)


    ///get Roll Number Update
    suspend fun getRollNumberUpdate(studAccademicId : Int,rollNumber : String)
            = apiHelper.getRollNumberUpdate(studAccademicId,rollNumber)


    //////get Message List Staff
    suspend fun getMessageListStaff(staffId : Int)
            = apiHelper.getMessageListStaff(staffId)

    //////get Message Delivery List Staff
    suspend fun getMessageDeliveryListStaff(staffId : Int)
            = apiHelper.getMessageDeliveryListStaff(staffId)

    //////get Regional Message List Staff
    suspend fun getRegionalMessageDeliveryListStaff(staffId : Int)
            = apiHelper.getRegionalMessageDeliveryListStaff(staffId)

    //////get Regional Message Delivery List Staff
    suspend fun getRegionalMessageListStaff(staffId : Int)
            = apiHelper.getRegionalMessageListStaff(staffId)


    //////get Voice Message List Staff
    suspend fun getVoiceMessageListStaff(staffId : Int)
            = apiHelper.getVoiceMessageListStaff(staffId)

    //////get Voice Message Delivery List Staff
    suspend fun getVoiceMessageDeliveryListStaff(staffId : Int)
            = apiHelper.getVoiceMessageDeliveryListStaff(staffId)

    ////track list
    suspend fun getVehicleList(dummyId : Int) = apiHelper.getVehicleList(dummyId)


    ////puncing staff attendance
    suspend fun getPunchAttendance(adminId : Int,schoolId : Int)
            = apiHelper.getPunchAttendance(adminId,schoolId)


    ////get Class List
    suspend fun getClassList(adminId: Int) = apiHelper.getClassList(adminId)


    ///feb 28 2024

    //manage Staff Leave List
    suspend fun getStaffLeaveLetterList(staffList: RequestBody?)
            = apiHelper.getStaffLeaveLetterList(staffList)

    suspend fun getLeaveDetails(leaveId: Int)
            = apiHelper.getLeaveDetails(leaveId)

    ///getInboxGetByDetails(vIRTUALMAILSENTSTAFFID: Int,adminId : Int,staffId: Int)
    suspend fun getStaffInboxViewById(vIRTUALMAILSENTSTAFFID: Int,adminId : Int,staffId: Int)
            = apiHelper.getStaffInboxViewById(vIRTUALMAILSENTSTAFFID,adminId,staffId)

    suspend fun getNotificationUpdate(virtualMailId : Int)
            = apiHelper.getNotificationUpdate(virtualMailId)

    suspend fun getInboxGetByDetails(inboxId: Int,studentId : Int) =
        apiHelper.getInboxGetByDetails(inboxId,studentId)

}