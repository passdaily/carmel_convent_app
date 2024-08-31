package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.admin_staff_punch_attendance

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityAdminStaffAttendanceBinding
import info.passdaily.camrelconvertapp.databinding.ActivityMainStaffBinding
import info.passdaily.camrelconvertapp.databinding.ActivityStaffAttendanceBinding
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.ContactUsViewModel
import info.passdaily.camrelconvertapp.lib.seek_circle.SeekCircle
import info.passdaily.camrelconvertapp.lib.stop_watch.model.Lap
import info.passdaily.camrelconvertapp.model.GetYearClassExamModel
import info.passdaily.camrelconvertapp.model.StaffListModel
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.home.HomeFragmentStaff
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.staff_punch_attendance.StaffPunchViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.io.IOException
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class AdminStaffAttendanceActivity : AppCompatActivity(){

    private lateinit var staffPunchViewModel: StaffPunchViewModel
    var TAG = "StaffAttendanceActivity"
    private lateinit var binding: ActivityAdminStaffAttendanceBinding

    var doubleBackToExitPressedOnce = false

    var toolbar: Toolbar? = null
    var adminId = 0
    var schoolId = 0
    private lateinit var localDBHelper: LocalDBHelper

    var aCCADEMICID = 0
    var sTAFFID = 0
    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getStaff = ArrayList<StaffListModel.Staff>()

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      ///  setContentView(R.layout.activity_staff_attendance)
        binding = ActivityAdminStaffAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        localDBHelper = LocalDBHelper(this)
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        // Inflate the layout for this fragment
        staffPunchViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StaffPunchViewModel::class.java]


        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = "Admin Staff Marking"
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }

        Global.punchACCADEMICID = 0
        Global.punchSTAFFID = 0
        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass


        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                Global.punchACCADEMICID = getYears[position].aCCADEMICID
                getStaffList()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        spinnerClass?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                Global.punchSTAFFID = getStaff[position].sTAFFID
              //  getPunchingStatusByAdmin()
                // getPunchStaffAttendance()
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_staff_host_fragment, AdminStaffAttendanceFragment(0,""))
//            .addToBackStack("home").commit()
                    .commit()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        if (savedInstanceState == null) {
            var fragmentManager = supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_staff_host_fragment, AdminStaffAttendanceFragment(0,""))
//            .addToBackStack("home").commit()
                .commit()
        }

        initFunction()
        Utils.setStatusBarColor(this)
    }


    override fun onBackPressed() {
        // super.onBackPressed()
        when (Global.screenState) {
            "staffAttendance" -> {
                var fragmentManager = supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_staff_host_fragment, AdminStaffAttendanceFragment(0,"")).commit()
            }
            "landingpage" -> {
//                if (!doubleBackToExitPressedOnce) {
//                    doubleBackToExitPressedOnce = true
//                    Toast.makeText(this, "click back again to exit.", Toast.LENGTH_SHORT).show()
//                    Handler(Looper.getMainLooper()).postDelayed(
//                        { doubleBackToExitPressedOnce = false },
//                        2000
//                    )
//                } else {
//                    Global.currentPage = 1
                    super.onBackPressed()
//                }
            }

        }
    }


    private fun initFunction() {
        staffPunchViewModel.getYearClassExam(adminId,schoolId)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size) { "" }
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                            }
                            if (spinnerAcademic != null) {
                                val adapter = ArrayAdapter(
                                    this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademic?.adapter = adapter
                            }

                            Log.i(TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }

    private fun getStaffList() {
        staffPunchViewModel.getStaffListStaff(Global.punchACCADEMICID,schoolId)
            .observe(this, Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getStaff = response.staffList as ArrayList<StaffListModel.Staff>
                            var years = Array(getStaff.size) { "" }
                            for (i in getStaff.indices) {
                                years[i] = getStaff[i].sTAFFFNAME
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerClass?.adapter = adapter
                            }


                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {

                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {

                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

}