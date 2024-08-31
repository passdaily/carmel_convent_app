package info.passdaily.camrelconvertapp.services.client_manager


import info.passdaily.camrelconvertapp.services.retrofit.ApiInterface
import okhttp3.RequestBody


class ApiClient( private val apiServices  : ApiInterface) {
    ///dashboard
    suspend fun getChildrenDetailsNew(ParentId: Int) = apiServices.childrenDetailsNew(ParentId)
    suspend fun getDashBoardNew(studentId: Int) = apiServices.dashboardItemsNew(studentId)

    ////logout page
    suspend fun getLogOutUser(pLoginID1: Int) = apiServices.logOutUser(pLoginID1)

    //block
    suspend fun getBlockStatus(accademicId : Int,studentId: Int) = apiServices.blockStatus(accademicId,studentId)

    //loginpage
    suspend fun getLoginDetails(username: String,password: String,schoolCode : String) = apiServices.loginPageNew(username,password,schoolCode)
    //register
    suspend fun getRegistrationDetails(mobileNumber: String,schoolCode: String) = apiServices.parentAccountCreate(mobileNumber,schoolCode)

    //staff Account Create
    suspend fun getStaffAccountCreate(mobileNumber: String,schoolCode : String) = apiServices.staffAccountCreate(mobileNumber,schoolCode)

    suspend fun getFaqItems(studentId : Int, aboutType : Int) = apiServices.faqitems(studentId,aboutType)

    suspend fun getContactUsItems(studentId : Int) = apiServices.contactUsItems(studentId)

    suspend fun getAbsents(studentId: Int,academicId: Int) = apiServices.getAbsentsNew(studentId,academicId)
    suspend fun getAccademic(academicId: Int) = apiServices.accademicNew(academicId)


    //////////study Material
    suspend fun getStudyMaterialListNew(academicId: Int,classId : Int,studentId: Int,subjectId : Int)
    = apiServices.getStudyMaterialListNew(academicId,classId,studentId,subjectId)

    suspend fun getMaterialList(materialId : Int)
            = apiServices.materialList(materialId)

    /////get Parent EventListAll
    suspend fun getParentEventListAll(academicId: Int,classId : Int,studentId: Int)
            = apiServices.parentEventListAll(academicId,classId,studentId)
    //////////get Event List All
    suspend fun getManageEvent(academicId: Int,eventType:Int,adminId : Int,schoolId:Int) = apiServices.manageEvent(academicId,
        eventType,adminId,schoolId)

    ///Delete Event from list
    suspend fun deleteEventItem(eventId : Int,adminId :Int,schoolId:Int) = apiServices.deleteEventItem(eventId,adminId,schoolId)

    suspend fun getSubjectModelNew(classId : Int,studentId: Int) = apiServices.subjectModelNew(classId,studentId)

    suspend fun getJoinLiveNew(academicId: Int,classId : Int,subjectId: Int,studentId : Int)
            = apiServices.joinLiveNew(academicId,classId,subjectId,studentId)

    suspend fun getMeetingAttend(zLIVECLASSID : Int,aCCADEMICID: Int
                                 ,STUDENTID : Int, mStatus : String) = apiServices.meetingAttend(zLIVECLASSID,aCCADEMICID, STUDENTID,mStatus)

    suspend fun getAssignmentListNew(academicId: Int,classId : Int,studentId: Int,subjectId : Int)
            = apiServices.getAssignmentListNew(academicId,classId,studentId,subjectId)

    suspend fun getAssignmentDetails(academicId: Int,classId : Int,studentId: Int,assignmentId : Int)
            = apiServices.assignmentDetails(academicId,classId,studentId,assignmentId)

    suspend fun getObjectiveListNew(academicId: Int,classId : Int,subjectId: Int,studentId : Int)
            = apiServices.getObjectiveListNew(academicId,classId,subjectId,studentId)

    suspend fun getDescriptiveListNew(academicId: Int,classId : Int,subjectId: Int,studentId : Int)
            = apiServices.getDescriptiveListNew(academicId,classId,subjectId,studentId)

    suspend fun getActivityListNew(studentId: Int,academicId : Int)
            = apiServices.activityList(academicId,studentId,0,0)

    suspend fun getHolidayListNew(studentId: Int,academicId : Int)
            = apiServices.holidayList(academicId,studentId,0)

    suspend fun getLibraryNew(studentId: Int) = apiServices.getLibraryNew(studentId)

    suspend fun getInboxDetailsNew(jsonObject: RequestBody) = apiServices.notificationNew(jsonObject)

    suspend fun getInboxReadStatus(studentId: Int,vIRTUALMAILSENTID : Int) =
        apiServices.inboxReadStatus(studentId,vIRTUALMAILSENTID)

    ///post
    suspend fun getEnquiryListNew(accademicRe: RequestBody?)
            = apiServices.enquiryListNew(accademicRe)

    suspend fun getLeaveListNew(accademicRe: RequestBody?)
            = apiServices.leaveListNew(accademicRe)


    suspend fun getCommonPostFun(url : String,accademicRe: RequestBody?)
            = apiServices.commonPostFun(url,accademicRe)



    suspend fun getDeleteAssignmentFile(assignmentFileId: Int)
            = apiServices.deleteAssignmentFile(assignmentFileId)

    suspend fun getDeleteEnquiryFun(url : String, enquiryId : Int)
            = apiServices.deleteEnquiryFun(url,enquiryId)

    suspend fun getDeleteLeaveFun(url : String,leaveId: Int)
            = apiServices.deleteLeaveFun(url,leaveId)

    suspend fun getSubmitAndNext(submitAndNextItems: RequestBody?)
            = apiServices.submitAndNext(submitAndNextItems)

    suspend fun getDeleteOption(optionItems: RequestBody?)
            = apiServices.deleteOption(optionItems)

    suspend fun getSaveAndExit(saveExitItems: RequestBody?)
            = apiServices.saveAndExit(saveExitItems)

    suspend fun getDoAutoEnd(doAutoEndItems: RequestBody?)
            = apiServices.doAutoEnd(doAutoEndItems)

    suspend fun getFinishExam(finishExamItems: RequestBody?)
            = apiServices.finishExam(finishExamItems)
    ////
    suspend fun getObjectiveDetailsNew(AssignmentFileId: Int,OExamId : Int)
            = apiServices.objectiveDetailsNew(AssignmentFileId,OExamId)

    suspend fun getQuestionListNew(OexamId: Int,OexamAttemptId : Int)
            = apiServices.questionListForStart(OexamId,OexamAttemptId)

//    suspend fun getOnlineExamStatus(academicId: Int,classId : Int,studentId : Int ,oExamId : Int)
//            = apiServices.onlineExamStatus(academicId,classId,studentId,oExamId)

    suspend fun getOptionsList(QuestionId: Int,OexamAttemptId : Int)
            = apiServices.optionsList(QuestionId,OexamAttemptId)

    suspend fun getObjectiveExamResult(academicId: Int,studentId: Int,oExamId: Int,OexamAttemptId : Int)
            = apiServices.objectiveExamResult(academicId,studentId,oExamId,OexamAttemptId)


    suspend fun getDQuestionListNew(examId: Int,examAttemptId : Int)
            = apiServices.questionDListForStart(examId,examAttemptId)

    suspend fun getTakeAnswer(examId: Int,examAttemptId : Int,questionId : Int)
            = apiServices.takeAnswer(examId,examAttemptId,questionId)

    suspend fun getDeleteFile(path: String,deleteFile: RequestBody?)
            = apiServices.deleteFileMethod(path,deleteFile)

    suspend fun getSubmitAnswerDExam(submitItems: RequestBody?)
            = apiServices.submitAnswerDExam(submitItems)

    suspend fun getFinishDExam(finishExamItems: RequestBody?)
            = apiServices.finishDExam(finishExamItems)

    suspend fun getDoAutoEndDExam(doAutoEndItems: RequestBody?)
            = apiServices.doAutoEndDExam(doAutoEndItems)


    suspend fun getSaveAndExitDExam(saveExitItems: RequestBody?)
            = apiServices.saveAndExitDExam(saveExitItems)

    suspend fun getDescriptiveExamResult(academicId: Int,studentId: Int,oExamId: Int,OexamAttemptId : Int)
            = apiServices.descriptiveExamResult(academicId,studentId,oExamId, OexamAttemptId)

    suspend fun getDescriptiveDetailsNew(studentId: Int,ExamId : Int)
            = apiServices.descriptiveListNew(studentId,ExamId)
    /////zoom
    suspend fun getZoomScheduleList(academicId: Int,classId : Int,studentId: Int)
            = apiServices.zoomScheduleList(academicId,classId,studentId)

    suspend fun getOnlineVideo(academicId: Int,classId : Int,studentId: Int,subjectId : Int)
            = apiServices.onlineVideoList(academicId,classId,studentId,subjectId)

    suspend fun getSubjects(classId : Int,studentId: Int)
            = apiServices.subjectList(classId,studentId)

    suspend fun getChapter(accademicId : Int, classId: Int,subjectId : Int)
            = apiServices.chapterList(accademicId,classId,subjectId)


    suspend fun getCommonAlbumCategory(url : String,accademicId : Int)
            = apiServices.commonAlbumCategory(url,accademicId)

    suspend fun getGalleryImgVidList(url : String,accademicId : Int)
            = apiServices.galleryImgVidList(url,accademicId)


    suspend fun getTrackMap(dummy : Int) = apiServices.trackMap(dummy)

    suspend fun getGpsLocation(VEHICLE_ID : Int,TERMINAL_ID: String,SIM_NUMBER: String)
    = apiServices.gpsLocation(VEHICLE_ID,TERMINAL_ID,SIM_NUMBER)

    suspend fun getFeesDetails(CLASSID : Int,ACADEMICID: Int,STUDENTID: Int,STUDENT_ROLL_NO : Int)
            = apiServices.feesDetails(CLASSID,ACADEMICID,STUDENTID,STUDENT_ROLL_NO)

    suspend fun getExamList(examId : Int) = apiServices.examDetailsList(examId)

    suspend fun getExamMarkDetails(
        ACADEMICID: Int,
        CLASSID: Int,
        EXAMID: Int,
        STUDENT_ROLL_NO: Int,
        STUDENT_ID: Int
    )
            = apiServices.examMarkDetails(CLASSID,ACADEMICID,EXAMID,STUDENT_ROLL_NO,STUDENT_ID)

    suspend fun getAnnualReport(STUDENT_ID : Int,ACADEMICID : Int)
            = apiServices.annualResultGet(STUDENT_ID,ACADEMICID)

    suspend fun getConveyorGet(StudentId : Int,ClassId : Int)
            = apiServices.conveyorGet(StudentId,ClassId)



    //////////////////Staff URL Requests
    suspend fun getLoginDetailStaff(username: String,password: String,schoolCode : String) = apiServices.loginPageStaff(username,password,schoolCode)

    suspend fun getDashBoardStaff(adminId: Int) = apiServices.dashBoardStaff(adminId)
    //
    suspend fun getLiveClassDetails(adminId: Int) = apiServices.liveClassDetails(adminId)

    suspend fun getClassDetails(adminId: Int,accademicId :Int) = apiServices.classDetails(adminId,accademicId)

    //getMeetingListByAdmin(accademicId,adminId))
    suspend fun getMeetingListByAdmin(accademicId: Int,adminId :Int) = apiServices.meetingListByAdmin(accademicId,adminId)

    ///post
    suspend fun getStartLiveMeeting(accademicRe: RequestBody?)
            = apiServices.startLiveMeeting(accademicRe)

    ////Online Video
    suspend fun getYearClassExam(adminId: Int,schoolId: Int) = apiServices.yearClassExam(adminId,schoolId)

    // getSubjectStaff
    suspend fun getSubjectStaff(classId : Int,adminId: Int) = apiServices.subjectStaff(classId,adminId)

    // Assignment getSubjectStaff Post method
    suspend fun postSubjectStaff(accademicRe: RequestBody?) = apiServices.postSubjectStaff(accademicRe)

    ///question Type Staff
    suspend fun getQuestionTypeStaff(adminId: Int) = apiServices.questionTypeStaff(adminId)

    //get Object Option List
    suspend fun getObjectOptionList(questionId: Int) = apiServices.objectiveOptionList(questionId)



    // getChapterStaff
    suspend fun getChapterStaff(aCCADEMICID : Int,cLASSID : Int,sUBJECTID : Int,sCHOOLID : Int) =
        apiServices.chaptersListStaff(aCCADEMICID,cLASSID,sUBJECTID,sCHOOLID)

    // getVideoListStaff
    suspend fun getVideoListStaff(aCCADEMICID : Int,cLASSID : Int,sUBJECTID : Int,chapter : Int) =
        apiServices.videoListStaff(aCCADEMICID,cLASSID,sUBJECTID,chapter)

    // youtube View Report
    suspend fun getYoutubeReport(YoutubeId : Int) =
        apiServices.youtubeViewReport(YoutubeId)

    //unPlayedVideoList
    suspend fun getUnPlayedVideoList(accademicId : Int,classId : Int,YoutubeId : Int) =
        apiServices.unPlayedVideoList(accademicId,classId,YoutubeId)

    // youtube Full Log List
    suspend fun getYoutubeFullLog(YoutubeLogId : Int) =
        apiServices.youtubeFullLog(YoutubeLogId)

//    suspend fun getDeleteYoutubeVideoFun(url : String,youtubeId: Int)
//            = apiServices.deleteYoutubeVideoFun(url,youtubeId)

    /////objective Exam
    suspend fun getOnlineExamListStaff(
        aCCADEMICID: Int,
        cLASSID: Int,
        sUBJECTID: Int,
        schoolId: Int
    ) =
        apiServices.onlineExamListStaff(aCCADEMICID,cLASSID,sUBJECTID,schoolId)

    /////objective Question List
    suspend fun getObjQuestionOptionListStaff(aCCADEMICID : Int,cLASSID : Int,sUBJECTID : Int,oEXAMID : Int) =
        apiServices.objQuestionOptionListStaff(aCCADEMICID,cLASSID,sUBJECTID,oEXAMID)

    suspend fun getOnlineExamResultStaff(OexamId : Int) = apiServices.onlineExamResultStaff(OexamId)

    suspend fun getUnattendedList(aCCADEMICID : Int,cLASSID : Int,OexamId : Int)
    = apiServices.unAttendedList(aCCADEMICID,cLASSID,OexamId)

    //   "OnlineExam/OnlineExamPublish?OexamId=" + oEXAMID
    suspend fun getCommonGetFuntion(url : String,map : HashMap<String?, Int>) = apiServices.commonGetFuntion(url,map)

//    //  OnlineExam/AllowOnceMore?OexamAttemptId=
//    suspend fun getAllowOnceMore(OexamAttemptId : Int) = apiServices.allowOnceMore(OexamAttemptId)


    /////Descriptive Exam
    suspend fun getDescOnlineExamList(aCCADEMICID: Int, cLASSID: Int, sUBJECTID: Int, schoolId: Int) =
        apiServices.descriptiveOnlineExamList(aCCADEMICID,cLASSID,sUBJECTID,schoolId)

    suspend fun getDescriptiveExamResult(examId : Int) = apiServices.descriptiveExamResult(examId)

    suspend fun getUnattendedDList(aCCADEMICID : Int,cLASSID : Int,ExamId : Int)
            = apiServices.unAttendedDList(aCCADEMICID,cLASSID,ExamId)

    suspend fun getDescQuestionList(aCCADEMICID : Int,cLASSID : Int,sUBJECTID : Int,ExamId : Int)
            = apiServices.descQuestionList(aCCADEMICID,cLASSID,sUBJECTID,ExamId)

    suspend fun getDescGiveMarkPreviewList(aCCADEMICID : Int,cLASSID : Int,dExamId : Int,dExamAttendId : Int)
            = apiServices.descGiveMarkPreviewList(aCCADEMICID,cLASSID,dExamId,dExamAttendId)

    suspend fun giveMarks(adminId : Int,dExamAttendId : Int,mark : String) = apiServices.giveMarks(adminId,dExamAttendId,mark)

    ///delete Comments files
    suspend fun DeleteCommentsFile(path : String,dExamAttendId : Int) = apiServices.deleteCommentsFile(path,dExamAttendId)



    ////take Answer Staff
    suspend fun getTakeAnswerDExam(examId: Int,examAttemptId : Int,questionId : Int)
            = apiServices.takeAnswerDExam(examId,examAttemptId,questionId)

    //Study Material List
    suspend fun getMaterialListStaff(aCCADEMICID : Int,cLASSID : Int,sUBJECTID : Int,sCHOOLID :Int) =
        apiServices.studyMaterialList(aCCADEMICID,cLASSID,sUBJECTID,sCHOOLID)

    //studyMaterialView
    suspend fun getStudyMaterialDetails(MaterialId : Int) = apiServices.studyMaterialDetails(MaterialId)

    suspend fun getDeleteFiles(url : String,fileId : Int,fileName : String)
            = apiServices.deleteFiles(url,fileId,fileName)

    //assignment list staff
    suspend fun getAssignmentListStaff(
        aCCADEMICID: Int,
        cLASSID: Int,
        sUBJECTID: Int,
        schoolId: Int
    )
            = apiServices.assignmentListStaff(aCCADEMICID,cLASSID,sUBJECTID,schoolId)

    //getAssignmentSubmissionList
    suspend fun getAssignmentSubmissionList(AssignmentId : Int) = apiServices.assignmentSubmissionList(AssignmentId)

    suspend fun getUnAttendedAssignment(aCCADEMICID : Int,cLASSID : Int,assignmentId : Int)
            = apiServices.unAttendedAssignment(aCCADEMICID,cLASSID,assignmentId)

    suspend fun getStudentSubmissionDetail(accademicId : Int,studentId : Int,assignmentId : Int,assignmentSubmitId : Int)
            = apiServices.studentSubmissionDetail(accademicId,studentId,assignmentId,assignmentSubmitId)

    //getAssignmentSubmissionList
    suspend fun getAssignmentGetById(AssignmentId : Int) = apiServices.assignmentGetById(AssignmentId)


    suspend fun getActivityListStaff(adminId: Int,academicId : Int)
            = apiServices.activityListStaff(adminId,academicId,0,0)

    suspend fun getHolidayListStaff(adminId: Int,academicId : Int)
            = apiServices.holidayListStaff(adminId,academicId,0)

    ///Live Class Report

    suspend fun getLiveClassReport(url : String,accademicRe: RequestBody?)
            = apiServices.liveClassReport(url,accademicRe)

    //  suspend fun meetingAttendedReport(
    //        @Query("ZLiveClassId") zLiveClassId: Int,

    suspend fun getMeetingAttendedReport(zLiveClassId : Int) = apiServices.meetingAttendedReport(zLiveClassId)

    suspend fun getUnAttendedZoomReport(aCCADEMICID : Int,cLASSID : Int,zLiveClassId : Int)
            = apiServices.unAttendedZoomReport(aCCADEMICID,cLASSID,zLiveClassId)

    suspend fun getInboxStaff(adminId : Int,staffId : Int)
            = apiServices.inboxListStaff(adminId,staffId)

    suspend fun getInboxReadById(inboxId : Int,adminId : Int,staffId : Int)
            = apiServices.staffInboxReadById(inboxId,adminId,staffId)

    suspend fun getNotificationStaff(staffId : Int)
            = apiServices.notificationStaff(0,0,staffId)

    suspend fun getNotificationSentDetails(staffId : Int)
            = apiServices.notificationSentDetails(0,0,0,0,staffId)


    suspend fun getLeaveListGet(accademicId : Int,classId : Int)
            = apiServices.leaveListGet(accademicId,classId,0,0)

    //getEnquiryListGet
    suspend fun getEnquiryListGet(classId: Int, accademicId: Int, schoolId: Int)
            = apiServices.enquiryListGet(classId,accademicId,0,schoolId)

    //getEnquiryIdGet
    suspend fun getEnquiryGetById(qUERYID : Int)
            = apiServices.enquiryGetById(qUERYID)

    /////Zoom scheduled List
    suspend fun getZoomScheduledStaff(url : String,accademicRe: RequestBody?)
            = apiServices.zoomScheduledStaff(url,accademicRe)

    /////zoom scheduled Report
    suspend fun getZoomScheduledReport(zMEETINGID: Int)  = apiServices.zoomScheduledReport(zMEETINGID)

    ////mark absent
    suspend fun getMarkAbsentStaff(cLASSID: Int,aCCADEMICID : Int,date: String,schoolId: Int)
            = apiServices.markAbsentsList(cLASSID,aCCADEMICID,date,schoolId)

    ////mark Present
    suspend fun getMarkPresentStaff(attendanceId: Int,studentId : Int)
            = apiServices.markPresent(attendanceId,studentId)


    ////mark Full Present
    suspend fun getMarkFullPresent(accademicId: Int,classId : Int,absentDate : String,adminId : Int)
            = apiServices.markFullPresent(accademicId,classId,absentDate,adminId,0)

    ////Student List for Staff
    suspend fun getStudentListStaff(accademicId: Int,classId : Int, schoolId: Int)
            = apiServices.studentList(accademicId,classId,schoolId)

    ////Conveyor List for Staff
    suspend fun getConveyorListStaff(accademicId: Int, schoolId: Int)
            = apiServices.conveyorList(accademicId,schoolId)

    ////Staff List
    suspend fun getStaffListStaff(accademicId: Int, schoolId: Int)
            = apiServices.staffListGet(accademicId,schoolId)

    //    ////get Punching Status By Admin
    suspend fun getPunchingStatusByAdmin(adminId:Int,schoolId:Int,sTAFFID:Int)
            = apiServices.punchingStatusByAdmin(adminId,schoolId,sTAFFID)


    //class By Super Admin
    suspend fun getClassBySuperAdmin(accademicId: Int)
            = apiServices.classBySuperAdmin(accademicId)

    //class Section By Super Admin
    suspend fun getClassSectionBySuperAdmin(accademicId: Int)
            = apiServices.classSectionBySuperAdmin(accademicId,0)

    //get Class By Teacher
    suspend fun getClassByTeacher(accademicId: Int,adminId : Int)
            = apiServices.classByTeacher(accademicId,adminId)

    ////PTA List
    suspend fun getPtaListGet(accademicId: Int, schoolId: Int)
            = apiServices.ptaListGet(accademicId,schoolId)

    ///Mark Register LP/UP
    suspend fun getMarkRegisterLpUp(accademicId: Int,classId : Int,examId : Int,subjectId : Int)
            = apiServices.markRegisterLpUp(accademicId,classId,examId,subjectId)

    ///Mark Register KG/IV, V-VIII, IX-XII
    suspend fun getMarkRegisterKgIV(accademicId: Int,classId : Int,examId : Int,subjectId : Int)
            = apiServices.markRegisterKgIV(accademicId,classId,examId,subjectId)

    ///Mark Register CE
    suspend fun getMarkRegisterCE(accademicId: Int,classId : Int,examId : Int,subjectId : Int)
            = apiServices.markRegisterCE(accademicId,classId,examId,subjectId)

    ///Mark Register CBSE
    suspend fun getMarkRegisterCBSE(accademicId: Int,classId : Int,examId : Int,subjectId : Int)
            = apiServices.markRegisterCBSE(accademicId,classId,examId,subjectId)


    ///Mark Register Msps Lp/Up
    suspend fun getMarkRegisterMsps(url : String,accademicId: Int,classId : Int,examId : Int,subjectId : Int)
            = apiServices.markRegisterMsps(url,accademicId,classId,examId,subjectId)

    //progressReportLpUp
    suspend fun getProgressReportLpUp(accademicId: Int,classId : Int,examId : Int,adminId : Int)
            = apiServices.progressReportLpUp(accademicId,classId,examId,adminId,0)

    //progressReport KG- XII
    suspend fun getProgressReportHS(accademicId: Int,classId : Int,examId : Int,adminId : Int)
            = apiServices.progressReportHs(accademicId,classId,examId,adminId,0)


    //progressReport CBSE
    suspend fun getProgressReportCBSE(accademicId: Int,classId : Int,examId : Int,adminId : Int)
            = apiServices.progressReportCBSE(accademicId,classId,examId,adminId,0)

    ///progressReport Msp Hs
    suspend fun getProgressReportMsp(url: String,accademicId: Int,classId : Int,examId : Int,adminId : Int)
            = apiServices.progressReportMsp(url,accademicId,classId,examId,adminId,0)


    /////Exam Topper
    suspend fun getExamTopperResponse(url : String,academicId : Int,classId : Int,examId : Int,adminId: Int)
            = apiServices.examTopperResponse(url,academicId,classId,examId,adminId,0,0)

    //    getExamGradeCbseResponse
    suspend fun getExamGradeCbseResponse(url : String,academicId : Int,classId : Int,examId : Int,adminId: Int)
            = apiServices.examGradeCbseResponse(url,academicId,classId,examId,adminId,0,0,0,0)

    ///getExamGradeResponse
    suspend fun getExamGradeResponse(url : String,academicId : Int,classId : Int,examId : Int,adminId: Int)
            = apiServices.examGradeResponse(url,academicId,classId,examId,adminId,0,0,0,0)

    ///getExamGradeCeResponse
    suspend fun getExamGradeCeResponse(url : String,academicId : Int,classId : Int,examId : Int,adminId: Int)
            = apiServices.examGradeCeResponse(url,academicId,classId,examId,adminId,0,0,0,0)


    ///getExamGradeCeResponse
    suspend fun getExamGradeMspResponse(url : String,academicId : Int,classId : Int,examId : Int,adminId: Int)
            = apiServices.examGradeMspResponse(url,academicId,classId,examId,adminId,0,0,0,0)


    ////Send Progress Report
    suspend fun getSendProgressReport(url: String, accademicId: Int, classId: Int, examId: Int)
            = apiServices.sendProgressReport(url,accademicId,classId,examId)

    ///manage Group List
    suspend fun getGroupList(groupList: RequestBody?)
            = apiServices.groupList(groupList)

    ///Group Item delete
    suspend fun getDeleteGroupItem(groupId: Int)
            = apiServices.deleteGroupItem(groupId)

    //getGroupList
    suspend fun getGroupListForStudentDelete(url : String,aCCADEMICID: Int,schoolId :Int)
            = apiServices.getGroupListForStudentDelete(url,aCCADEMICID,schoolId)

    suspend fun getGroupStudentList(aCCADEMICID: Int,gROUPID: Int,schoolId :Int)
            = apiServices.groupStudentList(aCCADEMICID,gROUPID,schoolId)

    //deleteGroupStudentItem
    suspend fun getDeleteGroupStudentItem(url : String,gMemberId: Int)
            = apiServices.deleteGroupStudentItem(url,gMemberId)


    //update Result
    suspend fun getUpdateResultList(aCCADEMICID: Int,cLASSID: Int)
            = apiServices.updateResultList(aCCADEMICID,cLASSID)

    //Publish Result
    suspend fun getPublishResult(aCCADEMICID: Int,cLASSID: Int)
            = apiServices.publishResult(aCCADEMICID,cLASSID)


    //public member
    suspend fun getPublicMember(aCCADEMICID: Int,gROUPID: Int)
            = apiServices.publicMember(aCCADEMICID,gROUPID)

    //get Public Group Member
    suspend fun getPublicGroupMember(aCCADEMICID: Int,gROUPID: Int,schoolId :Int)
            = apiServices.publicGroupMember(aCCADEMICID,gROUPID,schoolId)

    //sendMessageClassWise
    suspend fun getSendMessageClassWise(url : String,aCCADEMICID: Int,cLASSID: Int,messageId : Int, adminId: Int)
            = apiServices.sendMessageClassWise(url,aCCADEMICID,cLASSID,messageId,adminId)

    //get Send Class Section Wise
    suspend fun getSendClassSectionWise(url : String,aCCADEMICID: Int,cLASSID: Int,messageId : Int, adminId: Int)
            = apiServices.sendClassSectionWise(url,aCCADEMICID,cLASSID,messageId,adminId,0)

    //get Send Virtual Class Wise
    suspend fun getSendVirtualClassWise(classId: Int,accademicId: Int,mailId: Int,adminId : Int,schoolId:Int)
            = apiServices.sendVirtualClassWise(classId,accademicId,mailId,adminId,schoolId)


    //get Send Virtual Mail To Parents
    suspend fun getSendVirtualMailToParents(classId: Int,studentId: Int,adminId: Int,mailId : Int,schoolId:Int)
            = apiServices.sendVirtualMailToParents(classId,studentId,adminId,mailId,schoolId)

    //get Send Virtual Class Section Wise
    suspend fun getSendVirtualClassSectionWise(aCCADEMICID: Int,className: String,mailId : Int, adminId: Int,schoolId: Int)
    // (classId: Int,accademicId: Int,mailId: Int,adminId : Int,schoolId:Int)
            = apiServices.sendVirtualClassSectionWise(aCCADEMICID,className,mailId,adminId,0,schoolId)
    // (accademicId,classId,,mailId,adminId,schoolId)

    ///send Message Class By Teacher     //////staff Side
    suspend fun getSendMessageClassByTeacher(classId: Int,accademicId: Int,messageId: Int,adminId : Int)
            = apiServices.sendMessageClassByTeacher(classId,accademicId,messageId,adminId,0)

    ///send Notification Class By Teacher     //////staff Side
    suspend fun getSendNotificationClassByTeacher(classId: Int,accademicId: Int,mailId: Int,adminId : Int)
            = apiServices.sendNotificationClassByTeacher(classId,accademicId,mailId,adminId)

    //studentClasswise
    suspend fun getStudentClasswise(cLASSID: Int,aCCADEMICID: Int)
            = apiServices.studentClasswise(cLASSID,aCCADEMICID)

    //remark Register
    suspend fun getRemarkRegister(aCCADEMICID: Int,cLASSID: Int,eXAMID: Int)
            = apiServices.remarkRegister(aCCADEMICID,cLASSID,eXAMID)

    //Absentees List
    suspend fun getAbsenteesList(cLASSID: Int,aCCADEMICID: Int,fromDate: String,toDate : String)
            = apiServices.absenteesList(cLASSID,aCCADEMICID,fromDate,toDate,0)

    //attendanceSummeryDetail
    suspend fun getAttendanceSummeryDetail(cLASSID: Int,mONTH: Int,aCCADEMICID: Int,aCCADEMICYEAR : String)
            = apiServices.attendanceSummeryDetail(cLASSID,mONTH,aCCADEMICID,aCCADEMICYEAR)

    //getStudentDetails
    suspend fun getStudentDetails(cLASSID: Int,aCCADEMICID: Int,Date : String)
            = apiServices.studentDetails(cLASSID,aCCADEMICID,Date)


    //getManageAlbumList
    suspend fun getManageAlbumList(aCCADEMICID: Int,aLBUMYYPE: Int)
            = apiServices.manageAlbumList(aCCADEMICID,aLBUMYYPE)

    //getImageCategory
    suspend fun getImageCategory(url : String,albumCatId: Int)
            = apiServices.imageCategory(url,albumCatId)

    //get Gallery Image Video
    suspend fun getGalleryImageVideo(url : String,albumCatId: Int)
            = apiServices.galleryImageVideo(url,albumCatId)


    //getAlbumCategoryDelete
    suspend fun getAlbumCategoryDelete(url: String,AlbumCatId: Int)
            = apiServices.albumCategoryDelete(url,AlbumCatId)


    //getAboutUsFaq
    suspend fun getAboutUsFaq(AdminId: Int, schoolId: Int)
            = apiServices.aboutUsFaq(AdminId,schoolId)


    //getAboutUsFaqDelete
    suspend fun getAboutUsFaqDelete(url: String,AboutFaqId: Int)
            = apiServices.aboutUsFaqDelete(url,AboutFaqId)

    ///Student Remark
    suspend fun getStudentRemark(aCCADEMICID: Int,cLASSID: Int)
            = apiServices.studentRemark(aCCADEMICID,cLASSID)

    //studentInfo
    suspend fun getStudentInfo(studentList: RequestBody?)
            = apiServices.studentInfo(studentList)

    suspend fun getStudentInfoDelete(url : String, studentId: Int,accademicId :Int)
            = apiServices.studentInfoDelete(url,studentId,accademicId)


    //manage Staff List
    suspend fun getStaffList(staffList: RequestBody?)
            = apiServices.staffDetailsList(staffList)

    //deleteStaff
    suspend fun getDeleteStaff(url: String,staffId: Int)
            = apiServices.deleteStaff(url,staffId)


    //getPtaDetailList
    suspend fun getPtaDetailList(studentList: RequestBody?)
            = apiServices.ptaDetailList(studentList)

    //deletePta
    suspend fun getDeletePta(url: String,PtaMemberId: Int)
            = apiServices.deletePta(url,PtaMemberId)


    //conveyorDetailsList
    suspend fun getConveyorDetailsList(ClassId: Int)
            = apiServices.conveyorDetailsList(ClassId,"0",0)

    ////Get StudentList
    suspend fun getStudentList(accedamicId: Int,classId : Int) = apiServices.getStudentList(accedamicId,classId)


    //delete Conveyor
    suspend fun getConveyorDelete(url: String,ConveyorId: Int)
            = apiServices.conveyorDelete(url,ConveyorId)

    /////get guardian
    suspend fun getGuardianList(classId : Int,academicId: Int) = apiServices.guardianList(classId,academicId)


    /////Academic Management
    ///get Subject
    suspend fun getSubjectDetailList(subjectList: RequestBody?)
            = apiServices.subjectDetailList(subjectList)

    ///get Category List
    suspend fun getSubjectCategoryList(subjectCatName : Int,schoolId: Int) = apiServices.subjectCategory(subjectCatName,schoolId)

    ///get Class List
    suspend fun getClassList(className : Int,classStatus : Int) = apiServices.classList(className,classStatus)

    ///get Academic List
    suspend fun getAcademicList(accademicId : Int,schoolId : Int ) = apiServices.academicList(accademicId,schoolId)


    ///get Promote Student
    suspend fun getPromoteStudent(accademicIdFrom: Int,classIdFrom: Int, accademicIdTo: Int,classIdTo: Int) =
        apiServices.promoteStudent(accademicIdFrom,classIdFrom,accademicIdTo,classIdTo)


    ///get Students For Promote
    suspend fun getStudentsPromoteList(accademicId : Int,classId : Int,schoolId: Int)
    = apiServices.studentsPromoteList(accademicId,classId,schoolId)

    ///get Promote Student
    suspend fun sendStudentsPromote(studentId: Int,studentRollNo: Int, accademicId: Int,classId: Int) =
        apiServices.sendStudentsPromote(studentId,studentRollNo,accademicId,classId)

    ///get Student Reallocation List
    suspend fun getStudentReallocationList(accademicId: Int,classId: Int,gender: Int,rollNo: Int,schoolId: Int) =
        apiServices.studentReallocationList(accademicId,classId,gender,rollNo,schoolId)


    ///get Roll Number Update
    suspend fun getRollNumberUpdate(studAccademicId : Int,rollNumber : String)
    = apiServices.rollNumberUpdate(studAccademicId,rollNumber)


    //////get Message List Staff
    suspend fun getMessageListStaff(staffId : Int)
            = apiServices.messageListStaff(staffId,0,0)

    //////get Message Delivery List Staff
    suspend fun getMessageDeliveryListStaff(staffId : Int)
            = apiServices.messageDeliveryListStaff(0,0,0,staffId)


    //////get Regional Message List Staff
    suspend fun getRegionalMessageListStaff(staffId : Int)
            = apiServices.regionalMessageListStaff(staffId,0,0)

    //////get Regional Message Delivery List Staff
    suspend fun getRegionalMessageDeliveryListStaff(staffId : Int)
            = apiServices.regionalMessageDeliveryListStaff(0,0,0,staffId)

    //////get Voice Message List Staff
    suspend fun getVoiceMessageListStaff(staffId : Int)
            = apiServices.voiceMessageListStaff(staffId,0,0)

    //////get Voice Message Delivery List Staff
    suspend fun getVoiceMessageDeliveryListStaff(staffId : Int)
            = apiServices.voiceMessageDeliveryListStaff(0,0,0,staffId)

    ////track list
    suspend fun getVehicleList(dummyId : Int) = apiServices.vehicleList(0)


    ////puncing staff attendance
    suspend fun getPunchAttendance(adminId : Int,schoolId : Int)
            = apiServices.punchAttendance(adminId,schoolId)


    ////get Class List
    suspend fun getClassList(adminId: Int) = apiServices.getClassList(adminId)


    ///28 FEB 2024

    //manage Staff Leave List
    suspend fun getStaffLeaveLetterList(staffList: RequestBody?)
            = apiServices.staffLeaveLetterList(staffList)

    suspend fun getLeaveDetails(leaveId: Int)
            = apiServices.leaveDetails(leaveId)

    suspend fun getStaffInboxViewById(vIRTUALMAILSENTSTAFFID: Int,adminId : Int,staffId: Int)
            = apiServices.staffInboxViewById(vIRTUALMAILSENTSTAFFID,adminId,staffId)

    suspend fun getNotificationUpdate(virtualMailId : Int)
            = apiServices.notificationUpdate(virtualMailId)

    suspend fun getInboxGetByDetails(inboxId: Int,studentId : Int) =
        apiServices.inboxGetByDetails(inboxId,studentId)

}