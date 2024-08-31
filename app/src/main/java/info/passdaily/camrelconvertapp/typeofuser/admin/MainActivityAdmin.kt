package info.passdaily.camrelconvertapp.typeofuser.admin

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.BoringLayout.make
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityMainAdminBinding
import info.passdaily.camrelconvertapp.landingpage.firstpage.FirstScreenActivity
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.academic_management.AcademicManagementFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.calander.CalenderStaffActivity
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.descriptive_exam.DescriptiveExamStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.descriptive_exam.desc_exam_question.DescriptiveQuestionFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.enquiry.EnquiryFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.exam_grade.*
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.exam_topper.ExamTopperFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register.*
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register_msps.MarkRegisterMspHsFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.mark_register_msps.MarkRegisterMspsLpUpFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progrees_card_new.*
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card.*
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card_msps.ProgressCardMspHsFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.progress_card_msps.ProgressCardMspLpUpFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center.send_progress_report.SendProgressFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center_madrasa.exam_grade.ExamGradeMadrasaFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center_madrasa.exam_topper.ExamTopperMadrasaFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center_madrasa.mark_register.MarkRegisterMadrasaFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center_madrasa.progress_card.ProgressCardMadrasaFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.exam_center_madrasa.send_progress.SendProgressMadrasaFragment
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.gallery.GalleryViewActivity
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.home.HomeFragmentStaff
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.inbox.InboxStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.manage_conveyor.ManageConveyorFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.manage_guardian.ManageGuardianFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.promote_selected_student.PromoteSelectedStudentFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.promote_student.PromoteStudentFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.student_class_reallocation.StudentClassReallocation
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.student_info.StudentInfoFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.leave.LeaveFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_about_faq.ManageAboutFaqFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album.ManageAlbumFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album.upload_image.UploadImageFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album.upload_video.UploadVideoFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.AssignmentStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.ManageGroupFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.delete_student.DeleteStudentGroupFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.public_member.ManagePublicMemberFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.student_to_group.StudentToGroupFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_pta.ManagePtaFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_staff.ManageStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.mark_absent.MarkAbsentActivity
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.ObjectiveExamStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.object_exam_question.ObjectQuestionFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.online_video.OnlineVideoStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_message.QuickMessageFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_notification.QuickNotificationFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_regional_message.QuickRegionalMessageFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_voice.QuickVoiceMessageFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.StudentClassWiseFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.absent_list.AbsentListFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.attendance_summary.AttendanceSummaryFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.remarks.RemarkRegisterFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks.student_remark_layout.StudentRemarkFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.StudyMaterialStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.subject_chapter.SubChapterFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.track.TrackStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.update_result.PublishResultFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.update_result.UpdateResultFragment
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoomGoLive.ZoomGoLiveActivity
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_current_meeting.CurrentMeetingFragment
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_current_meeting.ZoomPrivateMeetingActivity
//import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_join_live_class.ZoomJoinLiveClassActivity
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_live_class_report.LiveClassReportFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_scheduled.ZoomScheduledFragment
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.admin_staff_punch_attendance.AdminStaffAttendanceActivity
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.gallery.GalleryStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.leave.staff_leave.LeaveLetterFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_event.ManageStaffEventDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.mark_absent.MarkMultiAbsentActivity


//import info.passdaily.teach_daily_app.typeofuser.testing_area.tele_phone.AutoDetectSMSActivity
//import info.passdaily.teach_daily_app.typeofuser.testing_area.tele_phone.SimDetectionFragment

class MainActivityAdmin : AppCompatActivity(),ToolBarClickListener {

    var TAG = "MainActivityAdmin"


//    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
//    private val notificationManager: NotificationManager by lazy {
//        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    }
//
//    companion object {
//        const val CHANNEL_ID = "ServiceNow"
//    }

    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your
            // app.
            //sendNotification(this)
        } else {
            // Explain to the user that the feature is unavailable because the
            // features requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainAdminBinding

    var doubleBackToExitPressedOnce = false

    var drawerLayout: DrawerLayout? =null
    var toolbar : Toolbar? =null
    var icon : LayerDrawable? = null

    var shapeImageView :ShapeableImageView? = null
    var textUserName : TextView? = null
    var textViewRole  : TextView? = null
    private lateinit var localDBHelper: LocalDBHelper

    lateinit var bottomNavigation : BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localDBHelper = LocalDBHelper(this)
//        var user = localDBHelper.viewUser()
//        var teacherName = user[0].STAFF_NAME
//        var teacherImage = user[0].STAFF_IMAGE


        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbar = binding.appBarMain.toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        Utils.setStatusBarColor(this)

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        //   val navController = findNavController(R.id.nav_host_fragment)
        //   supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_navigation_icon);
        supportActionBar?.setDisplayHomeAsUpEnabled(false);
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
//            ), drawerLayout
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)

        val header: View = navView.getHeaderView(0)
        var imageViewClose = header.findViewById(R.id.imageViewClose) as ImageView
        shapeImageView = header.findViewById(R.id.shapeImageView) as ShapeableImageView
        textViewRole = header.findViewById(R.id.textViewRole) as TextView
        textUserName = header.findViewById(R.id.textUserName) as TextView
        var textCompanyName = header.findViewById(R.id.textCompanyName) as TextView

//        Log.i(TAG,"image ${Global.staff_image_url+teacherImage}")
//        Glide.with(this@MainActivityAdmin)
//            .load(Global.staff_image_url+teacherImage)
//            .into(shapeImageView as ImageView)

        textUserName?.text = "Loading.."
        textCompanyName.text =  resources.getString(R.string.school_name)

//        shapeImageView.setOnClickListener {
//            val dialog = Dialog(this@MainActivityAdmin)
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.setCancelable(true)
//            dialog.setContentView(R.layout.dialog_profile_view)
//            val lp = WindowManager.LayoutParams()
//            lp.copyFrom(dialog.window!!.attributes)
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//            lp.gravity = Gravity.CENTER
//            dialog.window!!.attributes = lp
//
//            var imageViewProfile = dialog.findViewById<ImageView>(R.id.imageViewProfile)
//            Glide.with(this@MainActivityAdmin)
//                .load(Global.staff_image_url+teacherImage)
//                .into(imageViewProfile)
//
//            dialog.show()
//        }

        imageViewClose.setOnClickListener {
            drawerLayout?.closeDrawer(GravityCompat.START);
        }

        bottomNavigation = binding.appBarMain.bottomNavigation
        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_home-> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, HomeFragmentStaff())
                        .commit()
                }
                R.id.navigation_inbox->{
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, InboxStaffFragment())
                        .commit()
                }
                R.id.navigation_enquiry->{
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, EnquiryFragment())
                        .commit()
                }
                R.id.navigation_leave->{
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, LeaveFragment())
                        .commit()
                }
                R.id.navigation_map->{

                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, TrackStaffFragment())
                        .commit()
                }

            }
            true
        }

        // Initialize the action bar drawer toggle instance
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.appBarMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close,
        ){
            override fun onDrawerClosed(view: View){
                super.onDrawerClosed(view)
                //toast("Drawer closed")
            }

            override fun onDrawerOpened(drawerView: View){
                super.onDrawerOpened(drawerView)
                //toast("Drawer opened")
            }
        }
        drawerToggle.toolbarNavigationClickListener =
            View.OnClickListener { drawerLayout!!.openDrawer(GravityCompat.START) }
        drawerToggle.setHomeAsUpIndicator(R.drawable.ic_navigation_icon)
        drawerToggle.isDrawerIndicatorEnabled = false
        drawerLayout!!.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navView.setNavigationItemSelectedListener{
            drawerLayout!!.closeDrawer(GravityCompat.START)
            true
            when (it.itemId) {
                R.id.nav_home -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, HomeFragmentStaff())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                //
                R.id.nav_inbox -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, InboxStaffFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                //nav_admin_attendance_marking
                R.id.nav_admin_attendance_marking -> {
                    val log = Intent(applicationContext, AdminStaffAttendanceActivity::class.java)
                    startActivity(log)
                    true
                }

                R.id.nav_zoom_take_live -> {
//                    val log = Intent(applicationContext, ZoomGoLiveActivity::class.java)
//                    startActivity(log)
                    true
                }

                R.id.nav_zoom_join_live -> {
//                    val log = Intent(applicationContext, ZoomJoinLiveClassActivity::class.java)
//                    startActivity(log)
                    true
                }

                //nav_zoom_current_scheduled

                R.id.nav_zoom_current_scheduled -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, CurrentMeetingFragment())
////            .addToBackStack("home").commit()
//                        .commit()
                    true
                }

                R.id.nav_zoom_private_meeting -> {
//                    val log = Intent(applicationContext, ZoomPrivateMeetingActivity::class.java)
//                    startActivity(log)
                    true
                }

                //
                R.id.nav_zoom_scheduled -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ZoomScheduledFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                R.id.nav_manage_subject_chapter -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, SubChapterFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                //nav_update_res
                R.id.nav_update_res -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, UpdateResultFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                //nav_pub_res

                R.id.nav_pub_res -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, PublishResultFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                ///todo : progress Card
//                //nav_progress_card_lpup
//                R.id.nav_progress_card_lpup -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardLpUpFragment())
//                        .commit()
//                    true
//                }




//                //nav_progress_card_kg_viii
//                R.id.nav_progress_card_kg_viii -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardKGtoVIIIFragment())
//                        .commit()
//                    true
//                }


//                //nav_progress_card_kg_viii_new
//                R.id.nav_progress_card_kg_viii_new -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardKgToVIIINewFragment("New Progress Card (KG-VIII)"))
//                        .commit()
//                    true
//                }

//                //nav_progress_card_ix_xii_new                todo :New
//                R.id.nav_progress_card_ix_xii_new -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardKgToVIIINewFragment("New Progress Card (IX-XII)"))
//                        .commit()
//                    true
//                }


//                //nav_progress_card_ix_xii
//                R.id.nav_progress_card_ix_xii -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardIXtoXIIFragment())
//                        .commit()
//                    true
//                }

                //nav_progress_card_cbse
                R.id.nav_progress_card_cbse -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardCBSEFragment())
                        .commit()
                    true
                }

                //nav_progress_card_cbse_new
                R.id.nav_progress_card_cbse_new -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardCbseNewFragment("New Progress Card CBSE"))
                        .commit()
                    true
                }

//                //nav_progress_card_ce
//                R.id.nav_progress_card_ce -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardCEFragment())
//                        .commit()
//                    true
//                }

//                //nav_progress_card_ce_new
//                R.id.nav_progress_card_ce_new -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardCeNewFragment("New Progress Card CE"))
//                        .commit()
//                    true
//                }


//                //nav_progrescard_msp_lpup
//                R.id.nav_progrescard_msp_lpup -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardMspLpUpFragment())
//                        .commit()
//                    true
//                }

//                //nav_progrescard_msp_lpup_new
//                R.id.nav_progrescard_msp_lpup_new -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardMspLpNewFragment("New Progress Card Msp LP/UP"))
//                        .commit()
//                    true
//                }

//                //nav_progrescard_msp_hs
//                R.id.nav_progrescard_msp_hs -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardMspHsFragment())
//                        .commit()
//                    true
//                }

//                //nav_progrescard_msp_hs_new
//                R.id.nav_progrescard_msp_hs_new -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardMspHsNewFragment("New Progress Card Msp LP/UP"))
//                        .commit()
//                    true
//                }

                ///todo : Mark register
//                //nav_mark_reg_lpup
//                R.id.nav_mark_reg_lpup -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, MarkRegisterLpUpFragment())
//                        .commit()
//                    true
//                }

//                //nav_mark_regup
//                R.id.nav_mark_reg_kg_iv -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, MarkRegisterKgToIVFragment())
//                        .commit()
//                    true
//                }

//                //nav_mark_reghs
//                R.id.nav_mark_reg_v_viii -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, MarkRegisterVtoVIIIFragment())
//                        .commit()
//                    true
//                }
//                //nav_mark_reg
//                R.id.nav_mark_reg_ix_xii -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, MarkRegisterIXtoXIIFragment())
//                        .commit()
//                    true
//                }

//                //nav_mark_reg_ce
//                R.id.nav_mark_reg_ce -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, MarkRegisterCEFragment())
//                        .commit()
//                    true
//                }
                ///nav_mark_reg_cbse
                R.id.nav_mark_reg_cbse -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, MarkRegisterCBSEFragment())
                        .commit()
                    true
                }

//                ///nav_mark_reg_msps_lpup
//                R.id.nav_mark_reg_msps_lpup -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, MarkRegisterMspsLpUpFragment())
//                        .commit()
//                    true
//                }
//
//                //nav_mark_reg_msps_hs
//                R.id.nav_mark_reg_msps_hs -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, MarkRegisterMspHsFragment())
//                        .commit()
//                    true
//                }

                ///todo : exam topper
//                //nav_exam_topper
//                R.id.nav_exam_topper -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        ExamTopperFragment("Mark/ExamTopper", 1,"Exam Topper"))
//                        .commit()
//                    true
//                }

                //nav_examtopper_cbse
                R.id.nav_examtopper_cbse -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment,
                        ExamTopperFragment("MarkLnvn/ExamTopper", 2,"Exam Topper Cbse"))
                        .commit()
                    true
                }

//                //nav_examtopper_ce
//                R.id.nav_examtopper_ce -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        ExamTopperFragment("MarkDon/ExamTopper", 3,"Exam Topper CE"))
//                        .commit()
//                    true
//                }

//                //nav_examtopper_msp
//                R.id.nav_examtopper_msp -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        ExamTopperFragment("MarkMspHs/ExamTopper", 3,"Exam Topper Msp"))
//                        .commit()
//                    true
//                }

                ///   ///todo : exam Grade
//                //nav_exam_grade
//                R.id.nav_exam_grade -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        ExamGradeFragment("Mark/ExamGrade","Exam Grade")
//                    ).commit()
//                    true
//                }

                //nav_exam_grade_cbse
                R.id.nav_exam_grade_cbse -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment,
                        ExamGradeCbseFragment("MarkLnvn/ExamGrade","Exam Grade CBSE")
                    ).commit()
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ExamGradeTableView()
//                    ).commit()
                    true
                }

//                ///nav_exam_grade_ce
//                R.id.nav_exam_grade_ce -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        ExamGradeCeFragment("MarkDon/ExamGrade","Exam Grade CE")
//                    ).commit()
//                    true
//                }

//                //nav_exam_grade_msp_hs
//                R.id.nav_exam_grade_msp_hs -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        ExamGradeMspFragment("MarkMspHs/ExamGrade","Exam Grade MSPS")
//                    ).commit()
//                    true
//                }

                ///todo : Send progress report
//                //nav_send_progress_rp_lpup
//                R.id.nav_send_progress_rp_lpup -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        SendProgressFragment("MarkMsesLpUp/SendMarks","Send Progress Report Lp/Up")
//                    ).commit()
//                    true
//                }

//                ///nav_send_progress_rp
//                R.id.nav_send_progress_rp -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        SendProgressFragment("Mark/SendMarks","Send Progress Report")
//                    ).commit()
//                    true
//                }


                ///nav_send_progress_rp_cbse
                R.id.nav_send_progress_rp_cbse -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment,
                        SendProgressFragment("MarkLnvn/SendMarks","Send Progress Report CBSE")
                    ).commit()
                    true
                }

//                //nav_send_progress_rp_ce
//                R.id.nav_send_progress_rp_ce -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        SendProgressFragment("MarkDon/SendMarks","Send Progress Report CE")
//                    ).commit()
//                    true
//                }

//                //nav_send_progress_rp_cete
//                R.id.nav_send_progress_rp_cete -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        SendProgressFragment("MarkMspLpUp/SendMarks","Send Progress Report Msp Lp/Up")
//                    ).commit()
//                    true
//                }

//                //nav_send_progress_rp_cete2
//                R.id.nav_send_progress_rp_cete2 -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        SendProgressFragment("MarkMspHs/SendMarks","Send Progress Report Msp Hs")
//                    ).commit()
//                    true
//                }

                ////Todo : Exam Center Madrasa

                //nav_mark_reg_madrasa
                //nav_progrescard_madrasa
                //nav_examtopper_madrasa
                //nav_exam_grade_madrasa
                //nav_send_progress_rp_madrasa

//                R.id.nav_progrescard_madrasa -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardMadrasaFragment())
//                        .commit()
//                    true
//                }
//
//                R.id.nav_progrescard_madrasa_new -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressCardMadrasaNewFragment("New Progress Card Madrasa"))
//                        .commit()
//                    true
//                }
//
//                R.id.nav_mark_reg_madrasa -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, MarkRegisterMadrasaFragment())
//                        .commit()
//                    true
//                }
//
//                R.id.nav_examtopper_madrasa -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        ExamTopperMadrasaFragment("Mark/ExamTopper", 1,"Exam Topper Madrasa"))
//                        .commit()
//                    true
//                }
//
//                R.id.nav_exam_grade_madrasa -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        ExamGradeMadrasaFragment("Mark/ExamGrade","Exam Grade Madrasa"))
//                        .commit()
//                    true
//                }
//
//                R.id.nav_send_progress_rp_madrasa -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment,
//                        SendProgressMadrasaFragment("Mark/SendMarks","Send Progress Report"))
//                        .commit()
//                    true
//                }

                ////Todo : Exam Center Madrasa




                ///nav_group_list
                R.id.nav_group_list -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ManageGroupFragment()
                    ).commit()
                    true
                }
                //nav_addstudent_group
                R.id.nav_addstudent_group -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, StudentToGroupFragment()
                    ).commit()
                    true
                }

                //nav_del_studentgroup
                R.id.nav_del_studentgroup -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, DeleteStudentGroupFragment()
                    ).commit()
                    true
                }


                //nav_public_group_list
                R.id.nav_public_group_list -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ManagePublicMemberFragment()
                    ).commit()
                    true
                }

                R.id.nav_manage_youtube_class -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, OnlineVideoStaffFragment())
//            .addToBackStack("home").commit()
                        .commit()
//                    val log = Intent(applicationContext, OnlineVideoStaffActivity::class.java)
//                    startActivity(log)
                    true
                }

                R.id.nav_manage_exam -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ObjectiveExamStaffFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

//                R.id.nav_manage_question -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ObjectQuestionFragment())
////            .addToBackStack("home").commit()
//                        .commit()
//                    true
//                }

                //nav_manage_desc_exam
                R.id.nav_manage_desc_exam -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, DescriptiveExamStaffFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

//                //nav_manage_desc_question
//                R.id.nav_manage_desc_question -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, DescriptiveQuestionFragment())
////            .addToBackStack("home").commit()
//                        .commit()
//                    true
//                }

                R.id.nav_manage_studymaterial -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, StudyMaterialStaffFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                R.id.nav_manage_assigment -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, AssignmentStaffFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                //nav_zoom_class_report
                R.id.nav_zoom_class_report -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, LiveClassReportFragment())
                        .commit()
                    true
                }

//                //nav_msg_send
//                R.id.nav_msg_send -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, QuickMessageFragment())
//                        .commit()
//                    true
//                }

                //nav_msg_sendvoice
                R.id.nav_msg_sendvoice -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, QuickVoiceMessageFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

//                //nav_msg_sendreg
//                R.id.nav_msg_sendreg -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, QuickRegionalMessageFragment())
//                        .commit()
//                    true
//                }

                R.id.nav_newmail -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, QuickNotificationFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                //nav_mark_absent
                R.id.nav_mark_absent -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, AttendanceMakingFragment())
//                        .commit()
                    val log = Intent(applicationContext, MarkAbsentActivity::class.java)
                    startActivity(log)
                    true
                }
                //nav_mark_multi_absent
                R.id.nav_mark_multi_absent -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, AttendanceMakingFragment())
//                        .commit()
                    val log = Intent(applicationContext, MarkMultiAbsentActivity::class.java)
                    startActivity(log)
                    true
                }

                R.id.nav_leave -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, LeaveFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                //nav_enquiry
                R.id.nav_enquiry -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, EnquiryFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }


                R.id.nav_staff_leave -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, LeaveLetterFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                //nav_newschoolcal
                R.id.nav_newschoolcal -> {
                    val log = Intent(applicationContext, CalenderStaffActivity::class.java)
                    startActivity(log)
                    true
                }

                /////Student Remarks

                //nav_Stud_classwise
                R.id.nav_Stud_classwise -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, StudentClassWiseFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }


                //nav_student_info
                R.id.nav_student_info -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, StudentInfoFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                ///nav_student_guardian
                R.id.nav_student_guardian -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ManageGuardianFragment()).commit()
                    true
                }

                //nav_mpromotestudent
                R.id.nav_mpromotestudent -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, PromoteStudentFragment()).commit()
                    true
                }
                ///nav_student_cls_reallocation
                R.id.nav_student_cls_reallocation -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, StudentClassReallocation()).commit()
                    true
                }

                R.id.nav_mpromoteselect_student -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, PromoteSelectedStudentFragment()).commit()
                    true
                }

                //nav_stud_remarks
                R.id.nav_stud_remarks -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, StudentRemarkFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                //nav_remark_register
                R.id.nav_remark_register -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, RemarkRegisterFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }


                //nav_remark_register
                R.id.nav_event_list -> {
                    val dialog1 = ManageStaffEventDialog()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                    dialog1.show(transaction, ManageStaffEventDialog.TAG)
                    true
                }

                //nav_absente_list
                R.id.nav_absente_list -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, AbsentListFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                //nav_attend_summary
                R.id.nav_attend_summary -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, AttendanceSummaryFragment())
////            .addToBackStack("home").commit()
//                        .commit()
                    true
                }

                //nav_gallery_list
                R.id.nav_gallery_list -> {
//                    val log = Intent(applicationContext, GalleryViewActivity::class.java)
//                    startActivity(log)
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, GalleryStaffFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                //nav_album_ilist
                R.id.nav_album_ilist -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ManageAlbumFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                //nav_album_addi
                R.id.nav_album_addi -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, UploadImageFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                //nav_album_addv
                R.id.nav_album_addv -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, UploadVideoFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                ////nav_manage_staf
                R.id.nav_manage_staff -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ManageStaffFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                ////nav_manage_pta
                R.id.nav_manage_pta -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ManagePtaFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                //nav_mconveyor_list
                R.id.nav_mconveyor_list -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ManageConveyorFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }



                //nav_manage_aboutus
                R.id.nav_manage_aboutus -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ManageAboutFaqFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                //nav_academic_year_list
                R.id.nav_academic_year_list -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, AcademicManagementFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }

                R.id.nav_test_map -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, TrackStaffFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }


//                //nav_share_one
//                R.id.nav_share_one -> {
//                    val log = Intent(applicationContext, SharePageActivity::class.java)
//                    startActivity(log)
//                    true
//                }
//
//                //nav_share_one
//                R.id.nav_share_two -> {
//                    val log = Intent(applicationContext, SharePage2Activity::class.java)
//                    startActivity(log)
//                    true
//                }
//
//                //nav_staff_attendance
//                R.id.nav_staff_attendance -> {
//                    val log = Intent(applicationContext, StaffAttendanceActivity::class.java)
//                    startActivity(log)
//                    true
//                }
//
//                //nav_chart_list
//                R.id.nav_chart_list -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ChartFragment())
////            .addToBackStack("home").commit()
//                        .commit()
//                    true
//                }
//
//                //nav_image_crop
//                R.id.nav_image_crop -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ImageCropFragment())
////            .addToBackStack("home").commit()
//                        .commit()
//                    true
//                }
//
//                R.id.nav_sim_detect -> {
////                    var fragmentManager = supportFragmentManager
////                    var fragmentTransaction = fragmentManager.beginTransaction()
////                    fragmentTransaction.replace(R.id.nav_host_fragment, SimDetectionFragment())
//////            .addToBackStack("home").commit()
////                        .commit()
//                    val log = Intent(applicationContext, AutoDetectSMSActivity::class.java)
//                    startActivity(log)
//                    true
//                }

                R.id.nav_logout -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Do you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ ->
                            val user1 = localDBHelper.viewUser()
                            val adminId = user1[0].ADMIN_ID
                            localDBHelper.deleteUserID(adminId)
                            localDBHelper.deleteData(this@MainActivityAdmin)

                            val log = Intent(applicationContext, FirstScreenActivity::class.java)
                            startActivity(log)
                            finish()
                        }
                        .setNegativeButton(
                            "No"
                        ) { dialog, id -> //  Action for 'NO' Button
                            dialog.cancel()
                        }


                    //Creating dialog box
                    val alert = builder.create()
                    //Setting the title manually
                    alert.setTitle("Logout?")
                    alert.show()
                    val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                    buttonbackground.setTextColor(Color.BLACK)

                    val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                    buttonbackground1.setTextColor(Color.BLACK)
                    true
                }

                R.id.nav_clear_data -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Do you want to clear all the data's stored in this app?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ ->
                            localDBHelper.deleteData(this@MainActivityAdmin)
                            val log = Intent(applicationContext, FirstScreenActivity::class.java)
                            startActivity(log)
                            finish()
                            //clearData(this@MainActivityAdmin)

                        }
                        .setNegativeButton(
                            "No"
                        ) { dialog, _ -> //  Action for 'NO' Button
                            dialog.cancel()
                        }


                    //Creating dialog box
                    val alert = builder.create()
                    //Setting the title manually
                    alert.setTitle("Clear Data")
                    alert.show()
                    val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                    buttonbackground.setTextColor(Color.BLACK)

                    val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                    buttonbackground1.setTextColor(Color.BLACK)
                    true
                }

                else -> false
            }

        }

        if (savedInstanceState == null) {
            var fragmentManager = supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, HomeFragmentStaff())
//            .addToBackStack("home").commit()
                .commit()
        }

//        // Sets up permissions request launcher.
//        requestPermissionLauncher = registerForActivityResult(RequestPermission()) { permissions ->
//            if (permissions) {
//              //  showDummyNotification()
//            } else {
//                Toast.makeText(
//                    this,
//                    "Please grant Notification permission from App Settings",
//                    Snackbar.LENGTH_LONG
//                ).show()
//            }
//        }
//        createNotificationChannel()
//
//        Log.i(TAG,"areNotificationsEnabled ${notificationManager.areNotificationsEnabled()}")
        checkNotificationPermission()

        onNewIntent(intent)
    }

//    private fun createNotificationChannel() {
//        val channel = NotificationChannel(
//            CHANNEL_ID,
//            "Important Notification Channel",
//            NotificationManager.IMPORTANCE_HIGH,
//        ).apply {
//            description = "This notification contains important announcement, etc."
//        }
//        notificationManager.createNotificationChannel(channel)
//    }

    fun checkNotificationPermission(){
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                Log.i(TAG, "onCreate: PERMISSION GRANTED")
                // sendNotification(this)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                Snackbar.make(
                    findViewById(R.id.parent_layout),
                    "Allow Notification",
                    Snackbar.LENGTH_LONG
                ).setAction("Settings") {
                    // Responds to click on the action
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri: Uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }.show()
            }
            else -> {
                // The registered ActivityResultCallback gets the result of this request
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }





    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent) {
        //  Global.currentPage = 14

        val extras = intent.extras
        if (extras != null) {
            var fragmentManager = supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, InboxStaffFragment())
                .commitAllowingStateLoss()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val menuItem: MenuItem = menu.findItem(R.id.action_notification)
        icon = menuItem.icon as LayerDrawable

        val actionRefresh = menu.findItem(R.id.action_refresh) as MenuItem
        val rotation = AnimationUtils.loadAnimation(applicationContext, R.anim.clockwise_refresh)

        actionRefresh.setActionView(R.layout.refresh_action_view).actionView
            ?.setOnClickListener { view -> //  rotation.setRepeatCount(Animation.INFINITE);
                view.startAnimation(rotation)
                //  progressStart();
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, HomeFragmentStaff())
//            .addToBackStack("home").commit()
                    .commit()
            }


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_notification) {

            var fragmentManager = supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, QuickNotificationFragment())
//            .addToBackStack("home").commit()
                .commit()
            return true
        } else if (id == R.id.action_logout) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Do you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    //   Event product = new Event();
                    val user1 = localDBHelper.viewUser()
                    val adminId = user1[0].ADMIN_ID
                    localDBHelper.deleteUserID(adminId)
                    localDBHelper.deleteData(this@MainActivityAdmin)

                    val log = Intent(applicationContext, FirstScreenActivity::class.java)
                    startActivity(log)
                    finish()
                }
                .setNegativeButton(
                    "No"
                ) { dialog, id -> //  Action for 'NO' Button
                    dialog.cancel()
                }


            //Creating dialog box
            val alert = builder.create()
            //Setting the title manually
            alert.setTitle("Logout?")
            alert.show()
            val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            buttonbackground.setTextColor(Color.BLACK)

            val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            buttonbackground1.setTextColor(Color.BLACK)
            return true
        }
        else if (id == R.id.action_clear) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Do you want to clear all the data's stored in this app?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    localDBHelper.deleteData(this@MainActivityAdmin)
                    //  clearData(this@MainActivityStaff)
                    val log = Intent(applicationContext, FirstScreenActivity::class.java)
                    startActivity(log)
                    finish()
                }
                .setNegativeButton(
                    "No"
                ) { dialog, _ -> //  Action for 'NO' Button
                    dialog.cancel()
                }


            //Creating dialog box
            val alert = builder.create()
            //Setting the title manually
            alert.setTitle("Clear Data")
            alert.show()
            val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            buttonbackground.setTextColor(Color.BLACK)

            val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            buttonbackground1.setTextColor(Color.BLACK)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    override fun onBackPressed() {
        // super.onBackPressed()
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }
        when (Global.screenState) {
            "staffhomepage" -> {

                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, HomeFragmentStaff()).commit()
            }
            "landingpage" -> {
                if (!doubleBackToExitPressedOnce) {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(this, "click back again to exit.", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed(
                        { doubleBackToExitPressedOnce = false },
                        2000
                    )
                } else {
                    Global.currentPage = 1
                    super.onBackPressed()
                }
            }

        }
    }

    override fun setToolbarName(name: String) {
        toolbar?.title = name
    }

    override fun setDrawerImage(staffImage: String, staffName : String,aDMINROLENAME :String) {

        Log.i(TAG,"image ${Global.staff_image_url+staffImage}")
        Glide.with(this@MainActivityAdmin)
            .load(Global.staff_image_url+staffImage)
            .into(shapeImageView as ImageView)

        textUserName?.text = staffName
        textViewRole?.text = aDMINROLENAME

        shapeImageView?.setOnClickListener {
            val dialog = Dialog(this@MainActivityAdmin)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_profile_view)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            dialog.window!!.attributes = lp

            var imageViewProfile = dialog.findViewById<ImageView>(R.id.imageViewProfile)
            Glide.with(this@MainActivityAdmin)
                .load(Global.staff_image_url+staffImage)
                .into(imageViewProfile)

            dialog.show()
        }
    }


}
