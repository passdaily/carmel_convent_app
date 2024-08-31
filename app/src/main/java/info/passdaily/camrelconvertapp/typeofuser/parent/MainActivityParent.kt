package info.passdaily.camrelconvertapp.typeofuser.parent

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityMainBinding
import info.passdaily.camrelconvertapp.landingpage.firstpage.FirstScreenActivity
import info.passdaily.camrelconvertapp.lib.BottomMenuHelper
import info.passdaily.camrelconvertapp.lib.CountDrawable
import info.passdaily.camrelconvertapp.lib.CustomAdapter
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.lib.spinner.StudentModel
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentFCMHelper
import info.passdaily.camrelconvertapp.typeofuser.parent.absent.AbsentFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.annual_report.AnnualReportFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.assignment.AssignmentList
import info.passdaily.camrelconvertapp.typeofuser.parent.calendar.CalenderActivity
import info.passdaily.camrelconvertapp.typeofuser.parent.conveyor.ConveyorFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.description_exam.DescriptiveExam
import info.passdaily.camrelconvertapp.typeofuser.parent.fees.FeesDetailFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.fees.FeesInitFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.GalleryInitFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.home.HomeFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.leave_enquires.EnquiryFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.leave_enquires.LeaveFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.library.LibraryFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.map.TrackFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.notification.NotificationFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.objective_exam.ObjectiveExam
import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.OfflineStoreActivity
import info.passdaily.camrelconvertapp.typeofuser.parent.online_video.OnlineVideoFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.progress_report.ProgressReportFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.objective_video_exam.ObjectiveVideoExamList
//import info.passdaily.parentapp.typeofuser.parent.record_video_exam.VideoCaptureActivity
import info.passdaily.camrelconvertapp.typeofuser.parent.study_material.StudyMaterialInit
//import info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout.JoinLiveInit
//import info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout.LiveScheduledFragment
//import info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout.ZoomLiveActivity
import kotlinx.coroutines.DelicateCoroutinesApi


@Suppress("DEPRECATION")
class MainActivityParent : AppCompatActivity() {

    var TAG = "MainActivityParent"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
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
    private lateinit var binding: ActivityMainBinding

    private lateinit var mainActivityParentViewModel: MainActivityParentViewModel

    var studentModel = ArrayList<StudentModel>()
    lateinit var childrenModel : List<ChildrensModel.Children>

    var spinnerToolBar: Spinner? = null
    var textColorName: TextView? = null
    var pLoginID  = 0

    var doubleBackToExitPressedOnce = false


    var shimmerViewContainer : ShimmerFrameLayout? =null

    var drawerLayout: DrawerLayout? =null

    lateinit var bottomNavigation : BottomNavigationView

    var icon : LayerDrawable? = null
    private lateinit var localDBHelper: LocalDBHelper

    var shapeImageView : ShapeableImageView? = null
    var textUserName : TextView? = null
    var textCompanyName: TextView? = null


    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localDBHelper = LocalDBHelper(this)
        var user = localDBHelper.viewUser()
        pLoginID = user[0].PLOGIN_ID


        mainActivityParentViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[MainActivityParentViewModel::class.java]

    //    mainActivityParentViewModel = ViewModelProvider(this).get(MainActivityParentViewModel::class.java)


       // mainActivityParentViewModel.getChildrenDetails(pLoginID)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        Utils.setStatusBarColor(this)
        spinnerToolBar = binding.appBarMain.spinnerToolBar

        shimmerViewContainer = binding.appBarMain.contentMain.shimmerViewContainer

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val header: View = navView.getHeaderView(0)
        var imageViewClose = header.findViewById(R.id.imageViewClose) as ImageView
        shapeImageView = header.findViewById(R.id.shapeImageView)
        textUserName = header.findViewById(R.id.textUserName)
        textCompanyName = header.findViewById(R.id.textCompanyName)

        Glide.with(this)
            .load(resources.getDrawable(R.drawable.child_icon))
            .into(shapeImageView as ImageView)

        imageViewClose.setOnClickListener {
            drawerLayout?.closeDrawer(GravityCompat.START);
        }
        initFunction(pLoginID)


        bottomNavigation = binding.appBarMain.bottomNavigation
        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_home-> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, HomeFragment())
                        .commit()
                }
                R.id.navigation_inbox->{
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, NotificationFragment())
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
                    fragmentTransaction.replace(R.id.nav_host_fragment, TrackFragment())
                        .commit()
                }

            }
            true
        }

        spinnerToolBar?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // showToast(getCountries()[position] + " selected")
               // progressStop()
                shimmerViewContainer?.visibility = View.GONE
                Global.studentId = childrenModel[position].sTUDENTID
                val studentId = childrenModel[position].sTUDENTID

                Global.studentName = childrenModel[position].sTUDENTFNAME
                Global.className = childrenModel[position].cLASSNAME

                textUserName?.text = childrenModel[position].sTUDENTFNAME
                textCompanyName?.text = resources.getString(R.string.school_name)

                Log.i(TAG,"image ${Global.student_image_url+childrenModel[position].sTUDENTIMAGE}")
                Glide.with(this@MainActivityParent)
                    .load(Global.student_image_url+childrenModel[position].sTUDENTIMAGE)
                    .into(shapeImageView as ImageView)

                shapeImageView?.setOnClickListener {
                    val dialog = Dialog(this@MainActivityParent)
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
                    Glide.with(this@MainActivityParent)
                        .load(Global.student_image_url+childrenModel[position].sTUDENTIMAGE)
                        .into(imageViewProfile)

                    dialog.show()
                }

                ///  mainActivityParentViewModel.getDashBoard(childrenModel[position].sTUDENTID)
                val studentDBHelper = StudentDBHelper(this@MainActivityParent)
                var student = studentDBHelper.getStudentById(studentId)
                Log.i(TAG,"getStudentById "+student.STUDENT_ID)
                student_Info_Fun(studentId,student.STUDENT_ROLL_NO)
                blockFunction(student.ACCADEMIC_ID,student.STUDENT_ID)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }



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

        // Initialize the action bar drawer toggle instance
        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.appBarMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close,
        ){
            override fun onDrawerClosed(view:View){
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
                    fragmentTransaction.replace(R.id.nav_host_fragment, HomeFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                R.id.nav_notification -> {


                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, NotificationFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                R.id.nav_zoom -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, LiveScheduledFragment())
////            .addToBackStack("home").commit()
//                        .commit()
                    true
                }
                R.id.nav_live_class -> {
//                    val intent = Intent(applicationContext, ZoomLiveActivity::class.java)
//                    intent.putExtra("liveClass", 0)
//                    intent.putExtra("mainClass", 1)
//                    intent.putExtra("zOOMMEETINGID", "")
//                    intent.putExtra("zOOMMEETINGPASSWORD", "")
//                    startActivity(intent)
                    true
                }

                R.id.nav_online_video -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, OnlineVideoFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                R.id.nav_study_material -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, StudyMaterialInit())
//            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                R.id.nav_join_live -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, JoinLiveInit())
////            .addToBackStack("home").commit()
//                        .commit()
                    true
                }
                R.id.nav_assignment_list -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, AssignmentList())
                        //            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                R.id.nav_descriptive_exam -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, DescriptiveExam())
                        //            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                R.id.nav_objective_exam -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ObjectiveExam())
                        //            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                R.id.nav_absent -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, AbsentFragment())
                        //            .addToBackStack("home").commit()
                        .commit()
                    true
                }
                R.id.nav_library -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, LibraryFragment()).commit()
                    true
                }

                R.id.nav_leave -> {


                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, LeaveFragment()).commit()
                    true
                }
                R.id.nav_enquiry -> {


                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, EnquiryFragment()).commit()
                    true
                }
                R.id.nav_gallery -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, GalleryInitFragment()).commit()
                    true
                }

                R.id.nav_calendar -> {
                    val log = Intent(applicationContext, CalenderActivity::class.java)
                    startActivity(log)
                    true
                }

                R.id.nav_track -> {


                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, TrackFragment()).commit()
                    true
                }

                R.id.nav_fees -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, FeesInitFragment()
                    ).commit()
                    true
                }

                R.id.nav_progress -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressReportFragment("Progress Report")).commit()
                    true
                }
                R.id.nav_progress_cbse -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressReportFragment("Progress Report CBSE")).commit()
                    true
                }
//                R.id.nav_progress_lpup -> {
//                    var fragmentManager = supportFragmentManager
//                    var fragmentTransaction = fragmentManager.beginTransaction()
//                    fragmentTransaction.replace(R.id.nav_host_fragment, ProgressReportFragment("Progress Report Lp/Up")).commit()
//                    true
//                }

                //nav_annual_report
                R.id.nav_annual_report -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, AnnualReportFragment()).commit()
                    true
                }

                R.id.nav_conveyor -> {
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ConveyorFragment()).commit()
                    true
                }
                R.id.nav_offline-> {
                    val log = Intent(applicationContext, OfflineStoreActivity::class.java)
                    startActivity(log)
                    true
                }

                //nav_video_exam
                R.id.nav_video_exam-> {
//                    val log = Intent(applicationContext, ObjectiveVideoExamActivity::class.java)
//                    startActivity(log)
//                    true
                    var fragmentManager = supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment, ObjectiveVideoExamList()).commit()
                    true
                }

                R.id.nav_logout -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Do you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ ->

                            //   Event product = new Event();
                            val user1 = localDBHelper.viewUser()
                            val pLoginID1 = user1[0].PLOGIN_ID
                            JSON_LOGOUT(pLoginID1)
                            localDBHelper.deleteUserID(pLoginID1)
                            localDBHelper.deleteData(this@MainActivityParent)
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
                            localDBHelper.deleteData(this@MainActivityParent)
                            // clearData(this@MainActivityParent)
                            JSON_LOGOUT(pLoginID)
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
                    true
                }
                else -> false
            }

        }


        if (savedInstanceState == null) {
            var fragmentManager = supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, HomeFragment())
//            .addToBackStack("home").commit()
                .commit()
        }

        checkNotificationPermission()
        onNewIntent(intent)
    }

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


    private fun student_Info_Fun(studentId : Int,STUDENT_ROLL_NO : Int) {

        mainActivityParentViewModel.getDashBoard(studentId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            val studentDBHelper = StudentDBHelper(this)
//                            var student = studentDBHelper.getStudentById(studentId)

                            //  Global.studentId = productList1.get(0).get("id");
                            Log.i(TAG, "student.stu_roll_no $STUDENT_ROLL_NO")

                            Global.inboxcount = response.tileDetails.tOTALINBOX
                            setInboxcount()

                            if (STUDENT_ROLL_NO == 0) {
                                studentDBHelper.insertUser(
                                    StudentDBHelper.StudentModel(
                                        response.academicDetails.cLASSID,
                                        studentId,
                                        response.academicDetails.sTUDENTROLLNUMBER,
                                        response.academicDetails.aCCADEMICID,
                                        response.academicDetails.aCCADEMICTIME!!
                                    )
                                )
                            } else {
                                studentDBHelper.updateUser(
                                    StudentDBHelper.StudentModel(
                                        response.academicDetails.cLASSID,
                                        studentId,
                                        response.academicDetails.sTUDENTROLLNUMBER,
                                        response.academicDetails.aCCADEMICID,
                                        response.academicDetails.aCCADEMICTIME!!
                                    )
                                )
                            }

                            val no: Int = Global.currentPage
                            switchLayout(no)
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                            retry(studentId,STUDENT_ROLL_NO)
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })

    }

    private fun JSON_LOGOUT(pLoginID1: Int) {
        mainActivityParentViewModel.getLogOutUser(pLoginID1)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            Log.i(TAG, "t $response")
                        }
                        Status.ERROR -> {
                            Log.i("TAG", "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i("TAG", "resource ${resource.status}")
                            Log.i("TAG", "message ${resource.message}")
                        }
                    }
                }
            })


//        val apiInterface = RetrofitClient.create().logOutUser( pLoginID1)
//        apiInterface.enqueue(object : Callback<String> {
//            @SuppressLint("SetTextI18n")
//            override fun onResponse(
//                call: Call<String>, response: Response<String>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "t ${response.body()}")
//                }
//            }
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                Log.i(TAG, "t $t")
//            }
//        })

    }

    private fun switchLayout(PageNumber : Int) {

        Log.i(TAG,"PageNumber $PageNumber")
        when (PageNumber) {
            1 -> {


                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, HomeFragment(), "Home")
    //            .addToBackStack("home").commit()
                    .commit()
            }
            2 -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, OnlineVideoFragment())
    //            .addToBackStack("home").commit()
                    .commit()
            }
            3 -> {
//                var fragmentManager = supportFragmentManager
//                var fragmentTransaction = fragmentManager.beginTransaction()
//                fragmentTransaction.replace(R.id.nav_host_fragment, LiveScheduledFragment())
//    //            .addToBackStack("home").commit()
//                    .commit()
            }
            4 -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, StudyMaterialInit())
    //            .addToBackStack("home").commit()
                    .commit()
            }
            5 -> {
//                var fragmentManager = supportFragmentManager
//                var fragmentTransaction = fragmentManager.beginTransaction()
//                fragmentTransaction.replace(R.id.nav_host_fragment, JoinLiveInit())
//    //            .addToBackStack("home").commit()
//                    .commit()
            }

            6 -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, AssignmentList())
                    //            .addToBackStack("home").commit()
                    .commit()
            }
            7 -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, DescriptiveExam())
                    //            .addToBackStack("home").commit()
                    .commit()
            }
            8 -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, ObjectiveExam())
                    //            .addToBackStack("home").commit()
                    .commit()
            }
            9 -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, AbsentFragment())
                    //            .addToBackStack("home").commit()
                    .commit()
            }
            10 -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, LibraryFragment())
                    //            .addToBackStack("home").commit()
                    .commit()
            }
            11 -> {


                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, LeaveFragment())
                    //            .addToBackStack("home").commit()
                    .commit()
            }
            12 -> {


                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, EnquiryFragment())
                    //            .addToBackStack("home").commit()
                    .commit()
            }

            13 -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, GalleryInitFragment())
                    //            .addToBackStack("home").commit()
                    .commit()
            }
            14 -> {


                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, NotificationFragment())
                    //            .addToBackStack("home").commit()
                    .commit()
            }
            15 -> {


                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, TrackFragment())
                    //            .addToBackStack("home").commit()
                    .commit()
            }

            16 -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, FeesInitFragment()
                )
                    //            .addToBackStack("home").commit()
                    .commit()
            }

            17 -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, ProgressReportFragment("Progress Report"))
                    .commit()
            }

            18 -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, AnnualReportFragment())
                    .commit()
            }

            19 -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, ConveyorFragment())
                    .commit()
            }
        }

    }

    private fun initFunction(pLoginID: Int) {

        mainActivityParentViewModel.getChildrenDetails(pLoginID)
            .observe(this, Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            val studentFCMHelper = StudentFCMHelper(this)
                            var  productList2 = studentFCMHelper.viewFcmUser()
                            for(i in productList2){
                                val ok = studentFCMHelper.deleteUserID(i.STUDENT_ID)
                                Log.i(TAG,"ok $ok")
                            }
                            childrenModel = response.childrens
                            shimmerViewContainer?.visibility = View.GONE
                           // progressStop()
                            if(childrenModel.isNotEmpty()){
                                //      studentModel = ArrayList<StudentModel>()
                                for (i in response.childrens.indices) {
                                    var color = StudentModel(
                                        i,
                                        response.childrens[i].sTUDENTID,
                                        response.childrens[i].sTUDENTFNAME,
                                        response.childrens[i].cLASSNAME
                                    )
                                    studentModel.add(color)
                                    Log.i(TAG,"image ${Global.student_image_url+response.childrens[i].sTUDENTIMAGE}")
                                    studentFCMHelper.insertUser(
                                        StudentFCMHelper.StudentModel(response.childrens[i].sTUDENTID,
                                            response.childrens[i].sTUDENTIMAGE,
                                            response.childrens[i].cLASSID,response.childrens[i].cLASSNAME,
                                            response.childrens[i].sTUDENTFNAME,response.childrens[i].cLASSSECTION))
                                }
                                spinnerToolBar?.adapter = CustomAdapter(this, response.childrens)
                            }else{
                                val repo1 = LocalDBHelper(this)
                                val user1 = repo1.viewUser()
                                val pLoginID1 = user1[0].PLOGIN_ID
                                JSON_LOGOUT(pLoginID1)
                                repo1.deleteUserID(pLoginID1)
                                localDBHelper.deleteData(this@MainActivityParent)
                                val log = Intent(applicationContext, FirstScreenActivity::class.java)
                                startActivity(log)
                                finish()
                            }

                            Log.i(TAG,"studentFCMHelper ${studentFCMHelper.viewFcmUser()}")
                        }
                        Status.ERROR -> {
                            shimmerViewContainer?.visibility = View.GONE
                            //progressStop()
                            Log.i(TAG, "Error ${resource.message}")
                            retry()

                        }
                        Status.LOADING -> {
                            shimmerViewContainer?.visibility = View.VISIBLE
                         //   progressStart()
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })

//        mainActivityParentViewModel.getChildrenDetailsObservable()
//            .observe(this, {
//                if (it != null) {
//                    childrenModel = it.childrens
//                    spinnerToolBar?.adapter =
//                        CustomAdapter(this, it.childrens)
//
//              //      studentModel = ArrayList<StudentModel>()
//                    for (i in it.childrens.indices) {
//                        var color = StudentModel(
//                            i,
//                            it.childrens[i].sTUDENTID,
//                            it.childrens[i].sTUDENTFNAME,
//                            it.childrens[i].cLASSNAME
//                        )
//                        studentModel.add(color)
//                    }
//
//                }
//            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val menuItem: MenuItem = menu.findItem(R.id.action_notification)
        icon = menuItem.icon as LayerDrawable
        val actionRefresh = menu.findItem(R.id.action_refresh) as MenuItem
        val rotation = AnimationUtils.loadAnimation(applicationContext, R.anim.clockwise_refresh)

        actionRefresh.setActionView(R.layout.refresh_action_view).actionView?.setOnClickListener { view -> //  rotation.setRepeatCount(Animation.INFINITE);
                view.startAnimation(rotation)
                //  progressStart();
                val studentDBHelper = StudentDBHelper(this@MainActivityParent)
                var student = studentDBHelper.getStudentById(Global.studentId)
                Log.i(TAG, "getStudentById " + student.STUDENT_ID)
                student_Info_Fun(Global.studentId, student.STUDENT_ROLL_NO)
            }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_notification) {

            var fragmentManager = supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, NotificationFragment()).commit()
            return true
        }else if (id == R.id.action_logout) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Do you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    //   Event product = new Event();
                    val user1 = localDBHelper.viewUser()
                    val pLoginID1 = user1[0].PLOGIN_ID
                    JSON_LOGOUT(pLoginID1)
                    localDBHelper.deleteUserID(pLoginID1)
                    localDBHelper.deleteData(this@MainActivityParent)
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
                    val user1 = localDBHelper.viewUser()
                    val pLoginID1 = user1[0].PLOGIN_ID
                    JSON_LOGOUT(pLoginID1)
                    localDBHelper.deleteData(this@MainActivityParent)
                    // clearData(this@MainActivityParent)
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


    fun setCount(context: Context?, count: String?, icon: LayerDrawable, show: Boolean) {

        var badge: CountDrawable

        // Reuse drawable if possible
        val reuse = icon.findDrawableByLayerId(R.id.ic_badge)
        badge = if (reuse != null && reuse is CountDrawable) {
            reuse
        } else {
            CountDrawable(context!!)
        }
        badge.setCount(count!!)
        icon.mutate()

        val transparentDrawable: Drawable = ColorDrawable(Color.TRANSPARENT)
        if(show){
            icon.setDrawableByLayerId(R.id.ic_badge, badge)}

        else{ icon.setDrawableByLayerId(R.id.ic_badge, transparentDrawable)}
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
       // super.onBackPressed()
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        }
        when (Global.screenState) {
            "landingpage" -> {

                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_host_fragment, HomeFragment()).commit()
            }
            "homePage" -> {
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


//        else {
//            if(Global.screenstate == "dashboard"){
//                var fragmentManager = supportFragmentManager
//                var fragmentTransaction = fragmentManager.beginTransaction()
//                fragmentTransaction.replace(R.id.nav_host_fragment, HomeFragment()).commit()
//
//            }
//        }
//
//        if (doubleBackToExitPressedOnce) {
//            val intent = Intent(Intent.ACTION_MAIN)
//            intent.addCategory(Intent.CATEGORY_HOME)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
//        }
//
//        doubleBackToExitPressedOnce = true
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
//
//        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 1700)
    }

    private fun retry(){
        val builder = AlertDialog.Builder(this)
            .setTitle("No Internet")
            .setMessage("There is no internet connection here.")
            .setCancelable(false)
            .setPositiveButton("Retry") { dialog, which ->
               // progressStart()
                shimmerViewContainer?.visibility = View.VISIBLE
                initFunction(pLoginID)
                dialog.dismiss()
            }
            .setNegativeButton(
                "Offline Videos"
            ) { dialog, id -> //  Action for 'NO' Button
                val log = Intent(applicationContext, OfflineStoreActivity::class.java)
                startActivity(log)
                dialog.dismiss()
            }
        //  builder.show()
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.black))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.green_300))
            //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
        }
        dialog.show()
    }

    private fun retry(studentId: Int ,STUDENT_ROLL_NO: Int){
        val builder = AlertDialog.Builder(this)
            .setTitle("No Internet")
            .setMessage("There is no internet connection here.")
            .setCancelable(false)
            .setPositiveButton("Retry") { dialog, which ->
                student_Info_Fun(studentId,STUDENT_ROLL_NO)
                dialog.dismiss()
            }
            .setNegativeButton(
                "Offline Videos"
            ) { dialog, id -> //  Action for 'NO' Button
                val log = Intent(applicationContext, OfflineStoreActivity::class.java)
                startActivity(log)
                dialog.dismiss()
            }
        //  builder.show()
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.black))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.green_300))
            //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
        }
        dialog.show()
    }

    fun progressStart() {
        val dialog1 = ProgressBarDialog()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.isCancelable = false
        dialog1.show(transaction, ProgressBarDialog.TAG)
    }

    fun progressStop() {
        val fragment: ProgressBarDialog? =
            supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }


    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent) {
      //  Global.currentPage = 14



        val extras = intent.extras
        if (extras != null) {
            Log.i(TAG, "Coming-$extras")
            Log.i(TAG, "Coming1-" + extras.getString("NotificationMessage"))
            if (extras.containsKey("NotificationMessage")) {

//                val inte = Intent(this, ReceiveDataActivity::class.java)
//                inte.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                inte.putExtra("message", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
//                startActivity(inte)

                // extract the extra-data in the Notification
                val msg = extras.getString("NotificationMessage")
                val newTre = msg!!.split(",".toRegex()).toTypedArray()
                Log.i(TAG, "Coming2-" + newTre[0])
                val repo = StudentFCMHelper(applicationContext)

                when {
                    newTre[0] == "1" -> {
                        Global.currentPage = 14
                        Global.studentId = newTre[1].toInt()
                        val fragment: Fragment = NotificationFragment()
                        val frgManager = supportFragmentManager
                        frgManager.beginTransaction().replace(R.id.nav_host_fragment, fragment)
                            .commitAllowingStateLoss()
                    }
                    newTre[0] == "2" -> {
                        Global.currentPage = 14
                        val product = repo.getProductByClassId(newTre[2])
                        Global.studentId = product.STUDENT_ID
                        val fragment: Fragment = NotificationFragment()
                        val frgManager = supportFragmentManager
                        frgManager.beginTransaction().replace(R.id.nav_host_fragment, fragment)
                            .commitAllowingStateLoss()
                    }
                    newTre[0] == "3" -> {
                        Log.i(TAG, "Student ID" + newTre[3])
                        Global.currentPage = 14
                        val product1 = repo.getProductByClassSec(newTre[3])
                        Global.studentId = product1.STUDENT_ID
                        Log.i(TAG, "Student ID" + product1.STUDENT_ID)
                        val fragment: Fragment = NotificationFragment()
                        val frgManager = supportFragmentManager
                        frgManager.beginTransaction().replace(R.id.nav_host_fragment, fragment)
                            .commitAllowingStateLoss()
                    }
                    newTre[0] == "4" -> {
                        Global.currentPage = 14
                        val repo1 = StudentFCMHelper(this)
                        var productList2 = ArrayList<StudentFCMHelper.StudentModel>()
                        productList2 = repo1.viewFcmUser()
                        Global.studentId = productList2[0].STUDENT_ID
                        Log.i(TAG, "Student ID" + Global.studentId)
                        val fragment: Fragment = NotificationFragment()
                        val frgManager = supportFragmentManager
                        frgManager.beginTransaction().replace(R.id.nav_host_fragment, fragment)
                            .commitAllowingStateLoss()
                    }
                    else -> {
                        Global.currentPage = 14
                        val repo1 = StudentFCMHelper(this)
                        var productList2 = ArrayList<StudentFCMHelper.StudentModel>()
                        productList2 = repo1.viewFcmUser()
                        Global.studentId = productList2[0].STUDENT_ID
                        Log.i(TAG, "Student ID" + Global.studentId)
                        val fragment: Fragment = NotificationFragment()
                        val frgManager = supportFragmentManager
                        frgManager.beginTransaction().replace(R.id.nav_host_fragment, fragment)
                            .commitAllowingStateLoss()
                    }
                }
            }
        }
    }

    fun setInboxcount() {
        Log.i(TAG, "Total-Maino- " + Global.inboxcount)
        val total = Integer.valueOf(Global.inboxcount)
        if (total > 0) {
            var textLength = total.toString()
            if(total > 99){
                textLength = "99+"
            }
            setCount(this@MainActivityParent,textLength, icon!!,true)
            Log.i(TAG, "Total-Main1- $total")
            BottomMenuHelper.removeBadge(bottomNavigation, R.id.navigation_inbox)
            BottomMenuHelper.showBadge(this@MainActivityParent,
                bottomNavigation, R.id.navigation_inbox, textLength)
        } else {
            setCount(this@MainActivityParent,"", icon!!,false)
            // setCount(this@MainActivityParent,"", icon!!)
            Log.i(TAG, "Total-Main2- $total")
            BottomMenuHelper.removeBadge(bottomNavigation, R.id.navigation_inbox)
        }
    }



    private fun blockFunction(accademicId: Int, studentId: Int) {
        Global.blockDetailsModel  = ArrayList<BlockDetailsModel.BlockDetail>()
        mainActivityParentViewModel.getBlockStatus(accademicId,studentId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if(response.blockDetails.isNotEmpty()){
                                Global.blockDetailsModel = response.blockDetails
                                Global.blockStatus = true
                            }else{
                                Global.blockStatus = false
                            }

                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })
    }
}

