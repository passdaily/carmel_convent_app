package info.passdaily.camrelconvertapp.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient

import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.ContactUsViewModel
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.FaqViewModel
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.LoginParentViewModel
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.LoginStaffViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.academic_management.AcademicManagementViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.attendance_marking.AttendanceMakingViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.calander.CalendarViewStaffModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.descriptive_exam.DescriptiveExamStaffViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.enquiry.EnquiryViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.exam_grade.ExamGradeViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.exam_topper.ExamTopperViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.MainActivityParentViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.absent.AbsentViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.annual_report.AnnualReportViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.assignment.AssignmentDetailsViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.assignment.AssignmentViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.calendar.CalendarViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.conveyor.ConveyorViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.description_exam.DExamAreaViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.description_exam.DescriptiveDetailViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.description_exam.DescriptiveExamViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.fees.FeesDetailViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image.GalleryViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.home.HomeViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.leave_enquires.LeaveEnquiryViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.library.LibraryViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.map.TrackViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.notification.NotificationViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.objective_exam.OExamAreaViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.objective_exam.ObjectiveDetailViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.objective_exam.ObjectiveExamListViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.OnlineVideoViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.progress_report.ProgressViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.study_material.StudyInitViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout.JoinLiveInitViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout.LiveScheduledViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.home.StaffHomeViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.inbox.InboxStaffViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.leave.LeaveViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.AssignmentStaffViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.mark_absent.MarkAbsentViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register.MarkRegisterViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.ObjectiveExamStaffViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.online_video.OnlineVideoStaffViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card.ProgressCardViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.send_progress_report.SendProgressViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.gallery.GalleryStaffViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_about_faq.ManageAboutFaqViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album.ManageAlbumViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.manage_conveyor.ManageConveyorViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_event.ManageEventViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.GroupViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.manage_guardian.GuardianViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.promote_student.PromoteStudentViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_pta.PtaViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_staff.StaffViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification.QuickNotificationViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.student_info.StudentInfoViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_message.QuickMessageViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_regional_message.QuickRegionalMessageViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_voice.QuickVoiceMessageViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.staff_punch_attendance.StaffPunchViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.StudentRemarkViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.StudyMaterialStaffViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.subject_chapter.SubChapterViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.track.TrackStaffViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.update_result.UpdateResultViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoomGoLive.ZoomGoLiveViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_live_class_report.LiveClassReportViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_scheduled.ZoomScheduledViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.event.ManageEventParentViewModel

class ViewModelFactory (private val apiHelper: ApiClient) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainActivityParentViewModel::class.java) -> {
                MainActivityParentViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(LoginParentViewModel::class.java) -> {
                LoginParentViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(AbsentViewModel::class.java) -> {
                AbsentViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(StudyInitViewModel::class.java) -> {
                StudyInitViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(JoinLiveInitViewModel::class.java) -> {
                JoinLiveInitViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(AssignmentViewModel::class.java) -> {
                AssignmentViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(AssignmentDetailsViewModel::class.java) -> {
                AssignmentDetailsViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(ObjectiveExamListViewModel::class.java) -> {
                ObjectiveExamListViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(DescriptiveExamViewModel::class.java) -> {
                DescriptiveExamViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(LeaveEnquiryViewModel::class.java) -> {
                LeaveEnquiryViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(ObjectiveDetailViewModel::class.java) -> {
                ObjectiveDetailViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(DescriptiveDetailViewModel::class.java) -> {
                DescriptiveDetailViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(LibraryViewModel::class.java) -> {
                LibraryViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(CalendarViewModel::class.java) -> {
                CalendarViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> {
                NotificationViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(OExamAreaViewModel::class.java) -> {
                OExamAreaViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(DExamAreaViewModel::class.java) -> {
                DExamAreaViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(OnlineVideoViewModel::class.java) -> {
                OnlineVideoViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(LiveScheduledViewModel::class.java) -> {
                LiveScheduledViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(GalleryViewModel::class.java) -> {
                GalleryViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(TrackViewModel::class.java) -> {
                TrackViewModel(MainRepository(apiHelper)) as T
            }


            ////manage Event Staff
            modelClass.isAssignableFrom(ManageEventViewModel::class.java) -> {
                ManageEventViewModel(MainRepository(apiHelper)) as T
            }

            //Manage Event Parent
            modelClass.isAssignableFrom(ManageEventParentViewModel::class.java) -> {
                ManageEventParentViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(FeesDetailViewModel::class.java) -> {
                FeesDetailViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(ProgressViewModel::class.java) -> {
                ProgressViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(AnnualReportViewModel::class.java) -> {
                AnnualReportViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(ConveyorViewModel::class.java) -> {
                ConveyorViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(FaqViewModel::class.java) -> {
                FaqViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(ContactUsViewModel::class.java) -> {
                ContactUsViewModel(MainRepository(apiHelper)) as T
            }

            ///staff
            modelClass.isAssignableFrom(LoginStaffViewModel::class.java) -> {
                LoginStaffViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(StaffHomeViewModel::class.java) -> {
                StaffHomeViewModel(MainRepository(apiHelper)) as T
            }
            //ZoomGoLiveViewModel
            modelClass.isAssignableFrom(ZoomGoLiveViewModel::class.java) -> {
                ZoomGoLiveViewModel(MainRepository(apiHelper)) as T
            }
            //onlineVideo
            modelClass.isAssignableFrom(OnlineVideoStaffViewModel::class.java) -> {
                OnlineVideoStaffViewModel(MainRepository(apiHelper)) as T
            }
            //Objective Exam
            modelClass.isAssignableFrom(ObjectiveExamStaffViewModel::class.java) -> {
                ObjectiveExamStaffViewModel(MainRepository(apiHelper)) as T
            }
            //Descriptive Exam
            modelClass.isAssignableFrom(DescriptiveExamStaffViewModel::class.java) -> {
                DescriptiveExamStaffViewModel(MainRepository(apiHelper)) as T
            }

            //SubChapterViewModel/
            modelClass.isAssignableFrom(SubChapterViewModel::class.java) -> {
                SubChapterViewModel(MainRepository(apiHelper)) as T
            }
            //StudyMaterialStaffViewModel
            modelClass.isAssignableFrom(StudyMaterialStaffViewModel::class.java) -> {
                StudyMaterialStaffViewModel(MainRepository(apiHelper)) as T
            }
            //StudyMaterialStaffViewModel
            modelClass.isAssignableFrom(AssignmentStaffViewModel::class.java) -> {
                AssignmentStaffViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(AssignmentStaffViewModel::class.java) -> {
                AssignmentStaffViewModel(MainRepository(apiHelper)) as T
            }
            //
            modelClass.isAssignableFrom(CalendarViewStaffModel::class.java) -> {
                CalendarViewStaffModel(MainRepository(apiHelper)) as T
            }
            //LiveClassReportViewModel
            modelClass.isAssignableFrom(LiveClassReportViewModel::class.java) -> {
                LiveClassReportViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(InboxStaffViewModel::class.java) -> {
                InboxStaffViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(QuickNotificationViewModel::class.java) -> {
                QuickNotificationViewModel(MainRepository(apiHelper)) as T
            }

            modelClass.isAssignableFrom(LeaveViewModel::class.java) -> {
                LeaveViewModel(MainRepository(apiHelper)) as T
            }
            modelClass.isAssignableFrom(EnquiryViewModel::class.java) -> {
                EnquiryViewModel(MainRepository(apiHelper)) as T
            }
            ////Zoom Scheduled List
            modelClass.isAssignableFrom(ZoomScheduledViewModel::class.java) -> {
                ZoomScheduledViewModel(MainRepository(apiHelper)) as T
            }
            ///
            modelClass.isAssignableFrom(MarkAbsentViewModel::class.java) -> {
                MarkAbsentViewModel(MainRepository(apiHelper)) as T
            }
            ///MarkRegisterViewModel
            modelClass.isAssignableFrom(MarkRegisterViewModel::class.java) -> {
                MarkRegisterViewModel(MainRepository(apiHelper)) as T
            }
            //progressCardViewModel
            modelClass.isAssignableFrom(ProgressCardViewModel::class.java) -> {
                ProgressCardViewModel(MainRepository(apiHelper)) as T
            }
            //ExamTopperViewModel
            modelClass.isAssignableFrom(ExamTopperViewModel::class.java) -> {
                ExamTopperViewModel(MainRepository(apiHelper)) as T
            }
            //ExamGradeViewModel
            modelClass.isAssignableFrom(ExamGradeViewModel::class.java) -> {
                ExamGradeViewModel(MainRepository(apiHelper)) as T
            }
            //SendProgressViewModel
            modelClass.isAssignableFrom(SendProgressViewModel::class.java) -> {
                SendProgressViewModel(MainRepository(apiHelper)) as T
            }

            //SendProgressViewModel
            modelClass.isAssignableFrom(GroupViewModel::class.java) -> {
                GroupViewModel(MainRepository(apiHelper)) as T
            }

            //UpdateResultViewModel
            modelClass.isAssignableFrom(UpdateResultViewModel::class.java) -> {
                UpdateResultViewModel(MainRepository(apiHelper)) as T
            }
            ////Student Remarks
            modelClass.isAssignableFrom(StudentRemarkViewModel::class.java) -> {
                StudentRemarkViewModel(MainRepository(apiHelper)) as T
            }

            //attendanceMakingViewModel
            modelClass.isAssignableFrom(AttendanceMakingViewModel::class.java) -> {
                AttendanceMakingViewModel(MainRepository(apiHelper)) as T
            }

            //attendanceMakingViewModel
            modelClass.isAssignableFrom(ManageAlbumViewModel::class.java) -> {
                ManageAlbumViewModel(MainRepository(apiHelper)) as T
            }

            //manageAboutFaqViewModel
            modelClass.isAssignableFrom(ManageAboutFaqViewModel::class.java) -> {
                ManageAboutFaqViewModel(MainRepository(apiHelper)) as T
            }
            //StudentInfoViewModel
            modelClass.isAssignableFrom(StudentInfoViewModel::class.java) -> {
                StudentInfoViewModel(MainRepository(apiHelper)) as T
            }

            //StaffViewModel
            modelClass.isAssignableFrom(StaffViewModel::class.java) -> {
                StaffViewModel(MainRepository(apiHelper)) as T
            }

            //PtaViewModel
            modelClass.isAssignableFrom(PtaViewModel::class.java) -> {
                PtaViewModel(MainRepository(apiHelper)) as T
            }

            //PtaViewModel
            modelClass.isAssignableFrom(ManageConveyorViewModel::class.java) -> {
                ManageConveyorViewModel(MainRepository(apiHelper)) as T
            }

            ///GuardianViewModel
            modelClass.isAssignableFrom(GuardianViewModel::class.java) -> {
                GuardianViewModel(MainRepository(apiHelper)) as T
            }

            ///ManageEventViewModel
            modelClass.isAssignableFrom(ManageEventViewModel::class.java) -> {
                ManageEventViewModel(MainRepository(apiHelper)) as T
            }

            ///AcademicManagementViewModel
            modelClass.isAssignableFrom(AcademicManagementViewModel::class.java) -> {
                AcademicManagementViewModel(MainRepository(apiHelper)) as T
            }

            //promoteStudentViewModel
            modelClass.isAssignableFrom(PromoteStudentViewModel::class.java) -> {
                PromoteStudentViewModel(MainRepository(apiHelper)) as T
            }

            //QuickMessageViewModel
            modelClass.isAssignableFrom(QuickMessageViewModel::class.java) -> {
                QuickMessageViewModel(MainRepository(apiHelper)) as T
            }
            ///QuickRegionalMessageViewModel
            modelClass.isAssignableFrom(QuickRegionalMessageViewModel::class.java) -> {
                QuickRegionalMessageViewModel(MainRepository(apiHelper)) as T
            }

            ///QuickVoiceMessageViewModel
            modelClass.isAssignableFrom(QuickVoiceMessageViewModel::class.java) -> {
                QuickVoiceMessageViewModel(MainRepository(apiHelper)) as T
            }

            //////////Gallery View Staff
            modelClass.isAssignableFrom(GalleryStaffViewModel::class.java) -> {
                GalleryStaffViewModel(MainRepository(apiHelper)) as T
            }

            //////////Track View Staff
            modelClass.isAssignableFrom(TrackStaffViewModel::class.java) -> {
                TrackStaffViewModel(MainRepository(apiHelper)) as T
            }
            ///Staff Punch Attendance
            modelClass.isAssignableFrom(StaffPunchViewModel::class.java) -> {
                StaffPunchViewModel(MainRepository(apiHelper)) as T
            }

            else -> {throw IllegalArgumentException("Unknown class name")}
        }
    }


}