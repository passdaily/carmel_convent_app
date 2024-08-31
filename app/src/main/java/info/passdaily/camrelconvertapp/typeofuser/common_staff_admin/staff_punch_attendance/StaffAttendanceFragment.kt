package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.staff_punch_attendance

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.Context
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
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.*
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.maps.android.SphericalUtil
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentStaffAttendanceBinding
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.ContactUsViewModel
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.lib.seek_circle.SeekCircle
import info.passdaily.camrelconvertapp.lib.stop_watch.model.Lap
import info.passdaily.camrelconvertapp.model.AdminPunchingOperationModel
import info.passdaily.camrelconvertapp.model.StaffPunchingStatusModel
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.admin_staff_punch_attendance.AdminStaffAttendanceFragment
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@OptIn(DelicateCoroutinesApi::class)
@Suppress("DEPRECATION")
class StaffAttendanceFragment(var timerflag: Int, var commentUplodedFile: String) : Fragment(),
    OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener, StaffPunchInterface {


    var TAG = "StaffAttendanceFragment"
    private lateinit var staffPunchViewModel: StaffPunchViewModel
    private var _binding: FragmentStaffAttendanceBinding? = null
    private val binding get() = _binding!!

    var outOffRangeReason = ""
    var lateReason = ""

    var deviceBrand = ""
    var deviceId = ""
    var deviceModel = ""

    var punchingId = 0

    var mFusedLocationClient: FusedLocationProviderClient? = null

    var PERMISSION_ID = 44

    var progressHour = 0

    var adminId = 0
    var schoolId = 0
    private lateinit var localDBHelper: LocalDBHelper

    private var locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    var mapFragment: SupportMapFragment? = null


    ////////////Lat and long taken from work place
    var WORK_PLACE_LATTITUDE = 0.0
    var WORK_PLACE_LONGITUDE = 0.0

    var pUNCHDISTANCE = 0

    var job: Job? = null


    private var running = true
    private var increment = 0
    private var curIncrement = 0

    ////////////Lat and long taken using map dialog if user in out off range
    var user_map_latitude = 0.0
    var user_map_longitude = 0.0

    ////////////Lat and long taken from user default for checking user location status
    var user_default_latitude = 0.0
    var user_default_longitude = 0.0

    var mLastLocation: Location? = null
    var mCurrLocationMarker: Marker? = null

    var mLocationRequest: LocationRequest? = null

    var secondPassR = false
    private val REQUEST_CODE_MULTIPLE_PERMISSIONS = 57

    private var mGeofencingClient: GeofencingClient? = null
    private var mGeofencePendingIntent: PendingIntent? = null

    private var googleMap: GoogleMap? = null
    var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var contactViewModel: ContactUsViewModel

    private var googleApiClient: GoogleApiClient? = null

    private val REQUEST_LOCATION = 1

    private val geofenceList = ArrayList<Geofence>()
    private var geofencePendingIntent: PendingIntent? = null

    var textViewDistance: TextView? = null

    var textComeEarly: TextView? = null

    var textViewRange: TextView? = null

    var textViewStatus: TextView? = null

    var punchInButtonScr: AppCompatButton? = null
    var punchOutButtonScr: AppCompatButton? = null

    var workPunchInTime = ""
    var workStartTime = ""
    var workEndTime = ""
    var timeNow = ""

    var successMessage = ""
    var errorMessage = ""
    var punchStatus = ""

    var distance = 0.0

    var dialog: Dialog? = null

    var constraintText: ConstraintLayout? = null
    var constraintOutOffRange: ConstraintLayout? = null

    //constraintLateEarly
    var constraintLateEarly: ConstraintLayout? = null
    var constraintButtons: ConstraintLayout? = null

    var progressLocation: ProgressBar? = null

    private var timeCountInMilliSeconds = (1 * 60000).toLong()

    private enum class TimerStatus {
        STARTED, STOPPED
    }

    private var timerStatus = TimerStatus.STOPPED

    //https://deepshikhapuri.wordpress.com/2016/11/07/android-countdown-timer-run-in-background/

    var progressBarCircle: SeekCircle? = null
    var progressBarDashed: SeekCircle? = null
    var textViewTimer: TextView? = null
    var imageViewReset: ImageView? = null
    var imageViewStartStop: ImageView? = null
    var countDownTimer: CountDownTimer? = null

    var shapeImageView : ShapeableImageView? = null
    var textViewStaff  : TextView? = null
    var recyclerList : RecyclerView? = null
    var textViewNoReport : TextView? = null

    lateinit var mAdapter : PunchAttendanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.screenState = "landingpage"
        // Inflate the layout for this fragment
        _binding = FragmentStaffAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        // Inflate the layout for this fragment
        staffPunchViewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StaffPunchViewModel::class.java]

        contactViewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[ContactUsViewModel::class.java]


        progressBarDashed = binding.progressBarDashed
        progressBarCircle = binding.progressBarCircle
        textViewTimer = binding.textViewTimer
        punchInButtonScr = binding.punchInButtonScr
        punchOutButtonScr = binding.punchOutButtonScr

        shapeImageView  = binding.bottomSheet.shapeImageView
        textViewStaff  = binding.bottomSheet.textViewStaff
        recyclerList  = binding.bottomSheet.recyclerList
        recyclerList!!.layoutManager = LinearLayoutManager(requireActivity())
        textViewNoReport = binding.bottomSheet.textViewNoReport

        //#2 Initializing the BottomSheetBehavior
        var bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheetDashboard)

        //#3 Listening to State Changes of BottomSheet
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                buttonBottomSheetPersistent.text = when (newState) {
//                    BottomSheetBehavior.STATE_EXPANDED -> "Close Persistent Bottom Sheet"
//                    BottomSheetBehavior.STATE_COLLAPSED -> "Open Persistent Bottom Sheet"
//                    else -> "Persistent Bottom Sheet"
//                }
            }
        })

        //#4 Changing the BottomSheet State on ButtonClick
        binding.bottomSheet.bottomSheetButton.setOnClickListener {
            val state =
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_COLLAPSED
                else
                    BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.state = state
        }




        punchInButtonScr?.setOnClickListener {

            Global.successMessage = "Punch In Successfully"
            Global.errorMessage = "Punch In Process Failed"
            Global.punchStatus = "In"
            if (requestPermissions(requireActivity()) && gpsEnable()) {
                punchInProcessMethod()
            }

//            else{
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//
//                    val builder = AlertDialog.Builder(requireActivity())
//                    // builder.setTitle("App permissions")
//                    builder.setMessage("Click on ok then app will redirect to app settings then please click on Other Permissions and Please Allow All")
//                        .setCancelable(false)
//                        .setPositiveButton("Okay, I got it") { _, _ ->
//                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                            val uri: Uri = Uri.fromParts("package", requireActivity().getPackageName(), null)
//                            intent.data = uri
//                            startActivity(intent)
//                        }
//                        .setNegativeButton(
//                            "No"
//                        ) { dialog, id -> //  Action for 'NO' Button
//                            dialog.cancel()
//                        }
//
//
//                    //Creating dialog box
//                    val alert = builder.create()
//                    //Setting the title manually
//                    alert.setTitle("App permissions")
//                    alert.show()
//                    val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
//                    buttonbackground.setTextColor(Color.BLACK)
//
//                    val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
//                    buttonbackground1.setTextColor(Color.BLACK)
//
//
//                }
//            }
        }

        punchOutButtonScr?.setOnClickListener {
            Global.successMessage = "Punch Out Successfully"
            Global.errorMessage = "Punch Out Process Failed"
            Global.punchStatus = "Out"
            if (requestPermissions(requireActivity()) && gpsEnable()) {
                punchOutProcessMethod()
            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        getPunchStaffAttendance()

        //
        if (requestPermissions(requireActivity())) {
            //  getLastLocation()
        }




        getSystemDetail()

        if (timerflag == 1) {
            onSelfieUploaded(commentUplodedFile)
        }
        //  startCountDownTimer()
        //  animate();

        //  initView()


    }

    fun gpsEnable(): Boolean {
        var isGpsEnable = false
        GpsUtils(requireActivity()).turnGPSOn { isGPSEnable ->
            isGpsEnable = isGPSEnable  // You can get the callback here..
        }
        return isGpsEnable
    }


    fun punchInProcessMethod() {


        timeNowMethod()

        try {


            dialog = Dialog(requireActivity())
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
            dialog?.setContentView(R.layout.dialog_staff_attendance)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog?.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            dialog?.window!!.attributes = lp

            constraintText = dialog?.findViewById(R.id.constraintText) as ConstraintLayout
            constraintOutOffRange =
                dialog?.findViewById(R.id.constraintOutOffRange) as ConstraintLayout
            //constraintLateEarly
            constraintLateEarly = dialog?.findViewById(R.id.constraintLateEarly) as ConstraintLayout
            constraintButtons = dialog?.findViewById(R.id.constraintButtons) as ConstraintLayout

            progressLocation = dialog?.findViewById(R.id.progressLocation) as ProgressBar

            constraintText?.visibility = View.GONE
            constraintOutOffRange?.visibility = View.GONE
            //constraintLateEarly
            constraintLateEarly?.visibility = View.GONE
            constraintButtons?.visibility = View.GONE
            progressLocation?.visibility = View.VISIBLE


//            var editOutOffRange = dialog.findViewById(R.id.editOutOffRange) as TextInputEditText
//            var editTextLateReason = dialog.findViewById(R.id.editTextLateReason) as TextInputEditText
//            var editTextEarlyReason = dialog.findViewById(R.id.editTextEarlyReason) as TextInputEditText
//            //textInputEarlyReason
//            var textInputEarlyReason = dialog.findViewById(R.id.textInputEarlyReason) as TextInputLayout
//            var textInputOutOffRange = dialog.findViewById(R.id.textInputOutOffRange) as TextInputLayout
//            var textInputLateReason = dialog.findViewById(R.id.textInputLateReason) as TextInputLayout
//            val punchInButton = dialog.findViewById(R.id.punchInButton) as AppCompatButton
//            val refreshButton = dialog.findViewById(R.id.refreshButton) as AppCompatButton
//            textInputEarlyReason.visibility = View.GONE


            //   val mFragment: FragmentManager = requireActivity().supportFragmentManager.beginTransaction()

            mapFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            if (requestPermissions(requireActivity())) {
                if (mapFragment != null) {
                    mapFragment!!.getMapAsync(this)
                    Log.i(TAG, "onMapReady")
                }
            }
            if (!Places.isInitialized()) {
                Places.initialize(requireActivity(), resources.getString(R.string.google_maps_key));
            }
//
//            textComeEarly = dialog?.findViewById(R.id.textComeEarly) as TextView
//            textViewDistance = dialog?.findViewById(R.id.textViewDistance) as TextView
//            textViewRange = dialog?.findViewById(R.id.textViewRange) as TextView
//
//            Log.i(TAG,"user_map_latitude $user_map_latitude")
//            Log.i(TAG,"user_map_longitude $user_map_longitude")
//            Log.i(TAG,"WORK_PLACE_LATTITUDE $WORK_PLACE_LATTITUDE")
//            Log.i(TAG,"WORK_PLACE_LONGITUDE $WORK_PLACE_LONGITUDE")
//            Log.i(TAG,"pUNCHDISTANCE $pUNCHDISTANCE")
//
////            val locationA = Location("Company Location")
////            locationA.latitude = WORK_PLACE_LATTITUDE
////            locationA.longitude = WORK_PLACE_LONGITUDE
//            if((user_map_latitude == 0.0 && user_map_longitude == 0.0) ||
//                    (WORK_PLACE_LATTITUDE == 0.0 && WORK_PLACE_LONGITUDE == 0.0)){
//                punchInButton.visibility = View.GONE
//                refreshButton.visibility = View.VISIBLE
//                textInputOutOffRange.visibility = View.GONE
//                textInputLateReason.visibility = View.GONE
//                textViewDistance!!.visibility = View.VISIBLE
//                textViewRange!!.visibility = View.VISIBLE
//                textViewDistance?.setBackgroundColor(resources.getColor(R.color.gray_100))
//                textViewRange?.setBackgroundColor(resources.getColor(R.color.gray_100))
//            }
////            val locationB = Location("Current Location")
////            locationB.latitude = user_map_latitude
////            locationB.longitude = user_map_longitude
////            distance = locationA.distanceTo(locationB).toDouble()
//
//
//
//            val office = LatLng(WORK_PLACE_LATTITUDE, WORK_PLACE_LONGITUDE)
//            val currentLocation = LatLng(user_map_latitude, user_map_longitude)
//            var  sphericalDistance = SphericalUtil.computeDistanceBetween(office, currentLocation);
//            distance =  (sphericalDistance / 1000).toInt()
//            if(distance == 0){ distance = 1 }
//
//
//            Log.i(TAG,"distance $distance")
//
//            Log.i(TAG,"start $wrStartTime")
//            Log.i(TAG,"now $nowTime")
//            Log.i(TAG,"wrStartTime ${Utils.formattedTimehmma(wrStartTime)}")
//            Log.i(TAG,"wrEndTime ${Utils.formattedTimehmma(wrEndTime)}")
//            Log.i(TAG,"nowTime ${Utils.formattedTimehmma(nowTime)}")
//
//
////            val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
////
////            val st = "9:04 AM"
////            val nw = "6:00 PM"
////            val time1 = LocalTime.parse(nw, timeFormatter)
////            val time2 = LocalTime.parse(st, timeFormatter)
////            if (time1.isBefore(time2)) {
////                Log.i(TAG,"time1 before time2")
////            } else if (time1.isAfter(time2)) {
////                Log.i(TAG,"time1 after time2")
////            }
//
//
//            if (Utils.checkOnlyTime(Utils.formattedTimehmma(nowTime),  Utils.formattedTimehmma(wrStartTime), "Before") /*||
//                            Utils.checkOnlyTime(Utils.formattedTimehmma(nowTime), Utils.formattedTimehmma(wrStartTime), "Equal")*/) {
//                textInputOutOffRange.visibility = View.GONE
//                textInputLateReason.visibility = View.GONE
//                textViewDistance!!.visibility = View.GONE
//                textViewRange!!.visibility = View.GONE
//              //  if(Utils.checkOnlyTime(Utils.formattedTimehmma(nowTime), Utils.formattedTimehmma(wrEndTime), "Before")){
//                textComeEarly!!.text = "You are on time"
//                textComeEarly!!.visibility = View.VISIBLE
//
//               if(distance > pUNCHDISTANCE) {
//                   textViewDistance!!.visibility = View.VISIBLE
//                   textViewRange!!.visibility = View.VISIBLE
//
//
//
//                   if((user_map_longitude != 0.0 && user_map_latitude != 0.0) &&
//                       (WORK_PLACE_LATTITUDE != 0.0 && WORK_PLACE_LONGITUDE != 0.0)){
//                       textInputOutOffRange.visibility = View.VISIBLE
//                       //textInputLateReason.visibility = View.VISIBLE
//                       //    textViewDistance?.text = "Distance : ${distance/1000} kms"
//                       textViewDistance?.text = "Distance : ${DecimalFormat("##.##").format(distance/1000)} kms"
//
//                       textViewRange?.setTextColor(resources.getColor(R.color.color_physics))
//                       textViewRange?.text = "Your current location is not in range"
//
//                   }else{
//                       textInputOutOffRange.visibility = View.GONE
//                       textInputLateReason.visibility = View.GONE
//                       textViewDistance?.setBackgroundColor(resources.getColor(R.color.gray_100))
//                       textViewRange?.setBackgroundColor(resources.getColor(R.color.gray_100))
//                       punchInButton.visibility = View.GONE
//                       refreshButton.visibility = View.VISIBLE
//                   }
//
//
//                   punchInButton.setOnClickListener {
//
//                       if(staffPunchViewModel.validateField(editOutOffRange,
//                               "Give Out Off Range Reason",requireActivity(),binding.constraintLayoutContent)){
//
//                           Global.user_default_latitude = user_map_latitude
//                           Global.user_default_longitude = user_map_longitude
//
//                           Global.outOffRangeReason = editOutOffRange.text.toString()
//                           Global.lateReason = editTextLateReason.text.toString()
//
//                           if (mapFragment != null)
//                               requireActivity().supportFragmentManager.beginTransaction().remove(mapFragment!!).commit();
//
//
//                           var fragmentManager = requireActivity().supportFragmentManager
//                           var fragmentTransaction = fragmentManager.beginTransaction()
//                           fragmentTransaction.replace(R.id.nav_staff_host_fragment, SelfieFragment())
////            .addToBackStack("home").commit()
//                               .commit()
//                           dialog.dismiss()
//
//                       }
//                   }
//               }
//               else{
//                   Log.i(TAG, "No reason for early coming before")
//
//
//                   punchInButton.setOnClickListener {
//                       if (mapFragment != null)
//                           requireActivity().supportFragmentManager.beginTransaction().remove(mapFragment!!).commit();
//
//                       Global.user_default_latitude = user_map_latitude
//                       Global.user_default_longitude = user_map_longitude
//
////                        onSelfieUploaded(commentUplodedFile)
//                       var fragmentManager = requireActivity().supportFragmentManager
//                       var fragmentTransaction = fragmentManager.beginTransaction()
//                       fragmentTransaction.replace(R.id.nav_staff_host_fragment, SelfieFragment())
////            .addToBackStack("home").commit()
//                           .commit()
//                       dialog.dismiss()
//
//
//                   }
//               }
//
//            //    }
//
//            }
//            else if(Utils.checkOnlyTime(Utils.formattedTimehmma(nowTime), Utils.formattedTimehmma(wrStartTime), "After")){
//               // distance = 50.0
//                textComeEarly!!.visibility = View.GONE
//
//                if(distance <= pUNCHDISTANCE /*&& distance != -1*/){
//                    Log.i(TAG,"distance<=100 $distance")
//                    textInputOutOffRange.visibility = View.GONE
//                    textInputLateReason.visibility = View.VISIBLE
//                    textViewDistance!!.visibility = View.GONE
//                    textViewRange!!.visibility = View.GONE
//
//                    punchInButton.setOnClickListener {
//
//                        if(staffPunchViewModel.validateField(editTextLateReason,
//                                "Give Late Reason",requireActivity(),binding.constraintLayoutContent)){
//
//                            Global.user_default_latitude = user_map_latitude
//                            Global.user_default_longitude = user_map_longitude
//
//                            Global.outOffRangeReason = editOutOffRange.text.toString()
//                            Global.lateReason = editTextLateReason.text.toString()
//
//                            if (mapFragment != null)
//                                requireActivity().supportFragmentManager.beginTransaction().remove(mapFragment!!).commit();
//
////                            onSelfieUploaded(commentUplodedFile)
//                            var fragmentManager = requireActivity().supportFragmentManager
//                            var fragmentTransaction = fragmentManager.beginTransaction()
//                            fragmentTransaction.replace(R.id.nav_staff_host_fragment, SelfieFragment())
////            .addToBackStack("home").commit()
//                                .commit()
//                            dialog.dismiss()
//
//                        }
//                    }
//
//                }
//                else if(distance > pUNCHDISTANCE){
//                    textViewDistance!!.visibility = View.VISIBLE
//                    textViewRange!!.visibility = View.VISIBLE
//
//
//                    if((user_map_longitude != 0.0 && user_map_latitude != 0.0) &&
//                        (WORK_PLACE_LATTITUDE != 0.0 && WORK_PLACE_LONGITUDE != 0.0)){
//                        textInputOutOffRange.visibility = View.VISIBLE
//                        textInputLateReason.visibility = View.VISIBLE
//                        //    textViewDistance?.text = "Distance : ${distance/1000} kms"
//                        textViewDistance?.text = "Distance : $distance kms"
//
//                        textViewRange?.setTextColor(resources.getColor(R.color.color_physics))
//                        textViewRange?.text = "Your current location is not in range"
//
//                    }else{
//                        textInputOutOffRange.visibility = View.GONE
//                        textInputLateReason.visibility = View.GONE
//                        textViewDistance?.setBackgroundColor(resources.getColor(R.color.gray_100))
//                        textViewRange?.setBackgroundColor(resources.getColor(R.color.gray_100))
//                        punchInButton.visibility = View.GONE
//                        refreshButton.visibility = View.VISIBLE
//                    }
//
//
//                    punchInButton.setOnClickListener {
//
//                        if(staffPunchViewModel.validateField(editOutOffRange,
//                                "Give Out Off Range Reason",requireActivity(),binding.constraintLayoutContent)
//                            &&staffPunchViewModel.validateField(editTextLateReason,
//                                "Give Late Reason",requireActivity(),binding.constraintLayoutContent)){
//
//                            Global.user_default_latitude = user_map_latitude
//                            Global.user_default_longitude = user_map_longitude
//
//                            Global.outOffRangeReason = editOutOffRange.text.toString()
//                            Global.lateReason = editTextLateReason.text.toString()
//
//                            if (mapFragment != null)
//                                requireActivity().supportFragmentManager.beginTransaction().remove(mapFragment!!).commit();
//
//
//                            var fragmentManager = requireActivity().supportFragmentManager
//                            var fragmentTransaction = fragmentManager.beginTransaction()
//                            fragmentTransaction.replace(R.id.nav_staff_host_fragment, SelfieFragment())
////            .addToBackStack("home").commit()
//                                .commit()
//                            dialog.dismiss()
//
//                        }
//                    }
//
//                }
//            }
//
//
//            refreshButton.setOnClickListener {
//                if (mapFragment != null)
//                    requireActivity().supportFragmentManager.beginTransaction().remove(mapFragment!!).commit();
//
//                dialog.dismiss()
//            }
//
////            if(wrStartTime >= nowTime) {
////                Log.i(TAG, "No reason for early coming")
////            }
////            else if(wrStartTime < nowTime) {
////                Log.i(TAG, "Late reason only")
////            }
////            else if(wrStartTime < nowTime){
////                Log.i(TAG,"Late reason and out of range reason")
////            }

            val imageViewClose = dialog?.findViewById(R.id.imageViewClose) as ImageView
            imageViewClose.setOnClickListener {
                if (mapFragment != null)
                    requireActivity().supportFragmentManager.beginTransaction()
                        .remove(mapFragment!!).commit();

                dialog?.dismiss()
            }


            dialog?.show()

        } catch (e: IllegalArgumentException) {
            Log.i(TAG, "IllegalArgumentException $e")
            // Toast.makeText(requireActivity(), "Try again", Toast.LENGTH_SHORT).show()
            Utils.getSnackBar4K(requireActivity(), "Try again", binding.constraintLayoutContent)
        }


    }

    private fun meterDistanceBetweenPoints(
        lat_a: Double,
        lng_a: Double,
        lat_b: Double,
        lng_b: Double
    ): Double {
        val pk = (180f / Math.PI).toFloat()
        val a1 = lat_a / pk
        val a2 = lng_a / pk
        val b1 = lat_b / pk
        val b2 = lng_b / pk
        val t1 =
            Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2)
        val t2 =
            Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2)
        val t3 = Math.sin(a1) * Math.sin(b1)
        val tt = Math.acos(t1 + t2 + t3)
        return 6371009 * tt///6371009.0D
    }


    fun onLocationPunchInCallBack(user_map_latitude: Double, user_map_longitude: Double) {

        constraintText?.visibility = View.VISIBLE
        constraintOutOffRange?.visibility = View.VISIBLE
        //constraintLateEarly
        constraintLateEarly?.visibility = View.VISIBLE
        constraintButtons?.visibility = View.VISIBLE
        progressLocation?.visibility = View.GONE

        var nowTimeSer = ""
        var workStrtTime = ""
        var workEdTime = ""

        try {
         //   var start: Array<String> = workStartTime.split("T".toRegex()).toTypedArray()
            val wrStartTime: Long = Utils.longConversionTime(workStartTime)

            workStrtTime = Utils.formattedTimehmma(wrStartTime)
        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.i(TAG, "ArrayIndexOutOfBoundsException $e")
        }

        nowTimeSer = try {
            val now: Array<String> = timeNow.split("T".toRegex()).toTypedArray()
            val nowTime = Utils.longConversionTime(now[1])
            Utils.formattedTimehmma(nowTime)

        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.i(TAG, "ArrayIndexOutOfBoundsException $e")
            val sdf = SimpleDateFormat("h:mm a")
            sdf.format(Date())
        }

        try {
            //var end: Array<String> = workEndTime.split("T".toRegex()).toTypedArray()
            val eTime: Long = Utils.longConversionTime(workEndTime)
            var wrEndTime = eTime - 1800000

            workEdTime = Utils.formattedTimehmma(wrEndTime)

        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.i(TAG, "ArrayIndexOutOfBoundsException $e")
        }

        // Log.i(TAG,"wrEndTime ${Utils.formattedTimeHHmmss(wrEndTime)}")


        var editOutOffRange = dialog?.findViewById(R.id.editOutOffRange) as TextInputEditText
        var editTextLateReason = dialog?.findViewById(R.id.editTextLateReason) as TextInputEditText
        var editTextEarlyReason =
            dialog?.findViewById(R.id.editTextEarlyReason) as TextInputEditText
        //textInputEarlyReason
        var textInputEarlyReason =
            dialog?.findViewById(R.id.textInputEarlyReason) as TextInputLayout
        var textInputOutOffRange =
            dialog?.findViewById(R.id.textInputOutOffRange) as TextInputLayout
        var textInputLateReason = dialog?.findViewById(R.id.textInputLateReason) as TextInputLayout
        val punchInButton = dialog?.findViewById(R.id.punchInButton) as AppCompatButton
        val refreshButton = dialog?.findViewById(R.id.refreshButton) as AppCompatButton
        textInputEarlyReason.visibility = View.GONE




        textComeEarly = dialog?.findViewById(R.id.textComeEarly) as TextView
        textViewDistance = dialog?.findViewById(R.id.textViewDistance) as TextView
        textViewRange = dialog?.findViewById(R.id.textViewRange) as TextView

        Log.i(TAG, "user_map_latitude $user_map_latitude")
        Log.i(TAG, "user_map_longitude $user_map_longitude")
        Log.i(TAG, "WORK_PLACE_LATTITUDE $WORK_PLACE_LATTITUDE")
        Log.i(TAG, "WORK_PLACE_LONGITUDE $WORK_PLACE_LONGITUDE")
        Log.i(TAG, "pUNCHDISTANCE $pUNCHDISTANCE")


//            val locationA = Location("Company Location")
//            locationA.latitude = WORK_PLACE_LATTITUDE
//            locationA.longitude = WORK_PLACE_LONGITUDE
        if ((user_map_latitude == 0.0 && user_map_longitude == 0.0) ||
            (WORK_PLACE_LATTITUDE == 0.0 && WORK_PLACE_LONGITUDE == 0.0)
        ) {
            punchInButton.visibility = View.GONE
            refreshButton.visibility = View.VISIBLE
            textInputOutOffRange.visibility = View.GONE
            textInputLateReason.visibility = View.GONE
            textViewDistance!!.visibility = View.VISIBLE
            textViewRange!!.visibility = View.VISIBLE
            textViewDistance?.setBackgroundColor(resources.getColor(R.color.gray_100))
            textViewRange?.setBackgroundColor(resources.getColor(R.color.gray_100))
        } else {
            punchInButton.visibility = View.VISIBLE
            refreshButton.visibility = View.GONE
        }
//            val locationB = Location("Current Location")
//            locationB.latitude = user_map_latitude
//            locationB.longitude = user_map_longitude
//            distance = locationA.distanceTo(locationB).toDouble()


        val office = LatLng(WORK_PLACE_LATTITUDE, WORK_PLACE_LONGITUDE)
        val currentLocation = LatLng(user_map_latitude, user_map_longitude)
        // var  sphericalDistance = SphericalUtil.computeDistanceBetween(office, currentLocation);
        var sphericalDistance = meterDistanceBetweenPoints(
            user_map_latitude,
            user_map_longitude,
            WORK_PLACE_LATTITUDE,
            WORK_PLACE_LONGITUDE
        )
        distance = sphericalDistance
        // if(distance == 0){ distance = 1 }


        binding.textViewLatLong.text = "User Current Location \nLatitude $user_map_latitude " +
                "\nLongitude $user_map_longitude \n\nWork Place Location \nLatitude $WORK_PLACE_LATTITUDE \nLongitude $WORK_PLACE_LONGITUDE \n\n" +
                "Office Punch Radius  $pUNCHDISTANCE meters\nWork Start Time : $workStrtTime\nWork End Time : $workEdTime"




        Log.i(TAG, "distance $distance")

//        Log.i(TAG,"start $wrStartTime")
//        Log.i(TAG,"now $nowTime")
//        Log.i(TAG,"wrStartTime ${Utils.formattedTimehmma(wrStartTime)}")
//        Log.i(TAG,"wrEndTime ${Utils.formattedTimehmma(wrEndTime)}")
//        Log.i(TAG,"nowTime ${Utils.formattedTimehmma(nowTime)}")


//            val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
//
//            val st = "9:04 AM"
//            val nw = "6:00 PM"
//            val time1 = LocalTime.parse(nw, timeFormatter)
//            val time2 = LocalTime.parse(st, timeFormatter)
//            if (time1.isBefore(time2)) {
//                Log.i(TAG,"time1 before time2")
//            } else if (time1.isAfter(time2)) {
//                Log.i(TAG,"time1 after time2")
//            }


        if (Utils.checkOnlyTime(nowTimeSer, workStrtTime, "Before") ||
            Utils.checkOnlyTime(nowTimeSer, workStrtTime, "Equal")
        ) {
            textInputOutOffRange.visibility = View.GONE
            textInputLateReason.visibility = View.GONE
            textViewDistance!!.visibility = View.GONE
            textViewRange!!.visibility = View.GONE
            //  if(Utils.checkOnlyTime(Utils.formattedTimehmma(nowTime), Utils.formattedTimehmma(wrEndTime), "Before")){
            textComeEarly!!.text = "You are on time"
            textComeEarly!!.visibility = View.VISIBLE




            if (distance > pUNCHDISTANCE) {
                textViewDistance!!.visibility = View.VISIBLE
                textViewRange!!.visibility = View.VISIBLE



                if ((user_map_longitude != 0.0 && user_map_latitude != 0.0) &&
                    (WORK_PLACE_LATTITUDE != 0.0 && WORK_PLACE_LONGITUDE != 0.0)
                ) {
                    textInputOutOffRange.visibility = View.VISIBLE
                    //textInputLateReason.visibility = View.VISIBLE
                    //   textViewDistance?.text = "Distance : ${distance/1000} kms"
                    textViewDistance?.text =
                        "Distance : ${DecimalFormat("##.##").format(distance / 1000)} kms"

                    textViewRange?.setTextColor(resources.getColor(R.color.color_physics))
                    textViewRange?.text = "Your current location is not in range"

                    binding.textViewData.text = "Distance between user location to work place location is $distance meters\nYour current location is not in range\nDistance : " +
                            "${DecimalFormat("##.##").format(distance / 1000)} kms"

                } else {
                    textInputOutOffRange.visibility = View.GONE
                    textInputLateReason.visibility = View.GONE
                    textViewDistance?.setBackgroundColor(resources.getColor(R.color.gray_100))
                    textViewRange?.setBackgroundColor(resources.getColor(R.color.gray_100))
                    punchInButton.visibility = View.GONE
                    refreshButton.visibility = View.VISIBLE
                }


                punchInButton.setOnClickListener {

                    if (staffPunchViewModel.validateField(
                            editOutOffRange,
                            "Give Out Off Range Reason",
                            requireActivity(),
                            binding.constraintLayoutContent
                        )
                    ) {

                        Global.user_default_latitude = user_map_latitude
                        Global.user_default_longitude = user_map_longitude

                        Global.outOffRangeReason = editOutOffRange.text.toString()
                        Global.lateReason = editTextLateReason.text.toString()

                        if (mapFragment != null)
                            requireActivity().supportFragmentManager.beginTransaction()
                                .remove(mapFragment!!).commit();


                        var fragmentManager = requireActivity().supportFragmentManager
                        var fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.nav_staff_host_fragment, SelfieFragment())
//            .addToBackStack("home").commit()
                            .commit()
                        dialog?.dismiss()

                    }
                }
            }
            else if (distance <= pUNCHDISTANCE /* && distance != 0*/) {
                Log.i(TAG, "No reason for early coming before")

                binding.textViewData.text = "Distance between user location to work place location is $distance meters\n Your inside the campus \nDistance : " +
                        "${DecimalFormat("##.##").format(distance / 1000)} kms"
                punchInButton.setOnClickListener {
                    if (mapFragment != null)
                        requireActivity().supportFragmentManager.beginTransaction()
                            .remove(mapFragment!!).commit();

                    Global.user_default_latitude = user_map_latitude
                    Global.user_default_longitude = user_map_longitude

//                        onSelfieUploaded(commentUplodedFile)
                    var fragmentManager = requireActivity().supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_staff_host_fragment, SelfieFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    dialog?.dismiss()


                }
            }

            //    }

        } else if (Utils.checkOnlyTime(nowTimeSer, workStrtTime, "After")) {
            // distance = 50.0
            textComeEarly!!.visibility = View.GONE

            if (distance <= pUNCHDISTANCE /*&& distance != -1*/) {
                Log.i(TAG, "distance<=100 $distance")
                textInputOutOffRange.visibility = View.GONE
                textInputLateReason.visibility = View.VISIBLE
                textViewDistance!!.visibility = View.GONE
                textViewRange!!.visibility = View.GONE

                binding.textViewData.text = "Distance between user location to work place location is $distance meters\n Your inside the campus \nDistance : " +
                        "${DecimalFormat("##.##").format(distance / 1000)} kms"

                punchInButton.setOnClickListener {

                    if (staffPunchViewModel.validateField(
                            editTextLateReason,
                            "Give Late Reason", requireActivity(), binding.constraintLayoutContent
                        )
                    ) {

                        Global.user_default_latitude = user_map_latitude
                        Global.user_default_longitude = user_map_longitude

                        Global.outOffRangeReason = editOutOffRange.text.toString()
                        Global.lateReason = editTextLateReason.text.toString()

                        if (mapFragment != null)
                            requireActivity().supportFragmentManager.beginTransaction()
                                .remove(mapFragment!!).commit();

//                            onSelfieUploaded(commentUplodedFile)
                        var fragmentManager = requireActivity().supportFragmentManager
                        var fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.nav_staff_host_fragment, SelfieFragment())
//            .addToBackStack("home").commit()
                            .commit()
                        dialog?.dismiss()

                    }
                }

            }
            else if (distance > pUNCHDISTANCE) {
                textViewDistance!!.visibility = View.VISIBLE
                textViewRange!!.visibility = View.VISIBLE


                if ((user_map_longitude != 0.0 && user_map_latitude != 0.0) &&
                    (WORK_PLACE_LATTITUDE != 0.0 && WORK_PLACE_LONGITUDE != 0.0)
                ) {
                    textInputOutOffRange.visibility = View.VISIBLE
                    textInputLateReason.visibility = View.VISIBLE
                    //     textViewDistance?.text = "Distance : ${distance/1000} kms"
                    //  textViewDistance?.text = "Distance : $distance kms"
                    textViewDistance?.text =
                        "Distance : ${DecimalFormat("##.##").format(distance / 1000)} kms"

                    textViewRange?.setTextColor(resources.getColor(R.color.color_physics))
                    textViewRange?.text = "Your current location is not in range"

                    binding.textViewData.text = "Distance between user location to work place location is $distance meters\nYour current location is not in range\nDistance : " +
                            "${DecimalFormat("##.##").format(distance / 1000)} kms"

                } else {
                    textInputOutOffRange.visibility = View.GONE
                    textInputLateReason.visibility = View.GONE
                    textViewDistance?.setBackgroundColor(resources.getColor(R.color.gray_100))
                    textViewRange?.setBackgroundColor(resources.getColor(R.color.gray_100))
                    punchInButton.visibility = View.GONE
                    refreshButton.visibility = View.VISIBLE
                }


                punchInButton.setOnClickListener {

                    if (staffPunchViewModel.validateField(
                            editOutOffRange,
                            "Give Out Off Range Reason",
                            requireActivity(),
                            binding.constraintLayoutContent
                        )
                        && staffPunchViewModel.validateField(
                            editTextLateReason,
                            "Give Late Reason", requireActivity(), binding.constraintLayoutContent
                        )
                    ) {

                        Global.user_default_latitude = user_map_latitude
                        Global.user_default_longitude = user_map_longitude

                        Global.outOffRangeReason = editOutOffRange.text.toString()
                        Global.lateReason = editTextLateReason.text.toString()

                        if (mapFragment != null)
                            requireActivity().supportFragmentManager.beginTransaction()
                                .remove(mapFragment!!).commit();


                        var fragmentManager = requireActivity().supportFragmentManager
                        var fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.nav_staff_host_fragment, SelfieFragment())
//            .addToBackStack("home").commit()
                            .commit()
                        dialog?.dismiss()

                    }
                }

            }
        }


        refreshButton.setOnClickListener {
//            if (mapFragment != null)
//                requireActivity().supportFragmentManager.beginTransaction().remove(mapFragment!!).commit();
//
//            dialog?.dismiss()

            constraintText?.visibility = View.GONE
            constraintOutOffRange?.visibility = View.GONE
            //constraintLateEarly
            constraintLateEarly?.visibility = View.GONE
            constraintButtons?.visibility = View.GONE
            progressLocation?.visibility = View.VISIBLE

            getLastLocation()
        }

//            if(wrStartTime >= nowTime) {
//                Log.i(TAG, "No reason for early coming")
//            }
//            else if(wrStartTime < nowTime) {
//                Log.i(TAG, "Late reason only")
//            }
//            else if(wrStartTime < nowTime){
//                Log.i(TAG,"Late reason and out of range reason")
//            }

    }


    fun punchOutProcessMethod() {

        //   initView()

        timeNowMethod()

        try {
            //    enableGps()


            dialog = Dialog(requireActivity())
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setCancelable(false)
            dialog?.setContentView(R.layout.dialog_staff_attendance)
            // val promptView: View = layoutInflater.inflate(R.layout.dialog_staff_attendance, null)
//            dialog.setContentView(promptView)
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog?.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            dialog?.window!!.attributes = lp

            constraintText = dialog?.findViewById(R.id.constraintText) as ConstraintLayout
            constraintOutOffRange =
                dialog?.findViewById(R.id.constraintOutOffRange) as ConstraintLayout
            //constraintLateEarly
            constraintLateEarly = dialog?.findViewById(R.id.constraintLateEarly) as ConstraintLayout
            constraintButtons = dialog?.findViewById(R.id.constraintButtons) as ConstraintLayout

            progressLocation = dialog?.findViewById(R.id.progressLocation) as ProgressBar

            constraintText?.visibility = View.GONE
            constraintOutOffRange?.visibility = View.GONE
            //constraintLateEarly
            constraintLateEarly?.visibility = View.GONE
            constraintButtons?.visibility = View.GONE
            progressLocation?.visibility = View.VISIBLE


            mapFragment =
                requireActivity().supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            if (requestPermissions(requireActivity())) {
                if (mapFragment != null) {
                    mapFragment!!.getMapAsync(this)
                    Log.i(TAG, "onMapReady")
                }
            }
            if (!Places.isInitialized()) {
                Places.initialize(requireActivity(), resources.getString(R.string.google_maps_key));
            }


            val imageViewClose = dialog?.findViewById(R.id.imageViewClose) as ImageView
            imageViewClose.setOnClickListener {
                if (mapFragment != null)
                    requireActivity().supportFragmentManager.beginTransaction()
                        .remove(mapFragment!!).commit();

                dialog?.dismiss()
            }

            dialog?.show()


        } catch (e: IllegalArgumentException) {
            Log.i(TAG, "IllegalArgumentException $e")
            // Toast.makeText(requireActivity(), "Try again", Toast.LENGTH_SHORT).show()
            Utils.getSnackBar4K(requireActivity(), "Try again", binding.constraintLayoutContent)
        }
//        timeNowMethod()
//        if (isLocationEnabledBool()) {
//            // code if permission granted already
//            // else request permission
//        //    Log.i(TAG,"getPermission")
//        //    Log.i(TAG,"LATTITUDE $WORK_PLACE_LATTITUDE")
//         //   Log.i(TAG,"LONGITUDE $WORK_PLACE_LONGITUDE")
//            try{
//
//                val locationA = Location("Company Location")
//                locationA.latitude = WORK_PLACE_LATTITUDE!!
//                locationA.longitude = WORK_PLACE_LONGITUDE!!
//                val locationB = Location("Current Location")
//                locationB.latitude = Global.user_default_latitude
//                locationB.longitude = Global.user_default_longitude
//                var distance = locationA.distanceTo(locationB)
//
//                Log.i(TAG,"distance $distance")
//
//                //    textViewDistance?.text = "Distance : ${distance/1000} kms"
//                textViewDistance?.text = "Distance : ${DecimalFormat("##.##").format(distance/1000)} kms"
//
//
//                var end: Array<String> = workEndTime.split("T".toRegex()).toTypedArray()
//                val eTime: Long = Utils.longConversionTime(end[1])
//                var wrEndTime = eTime - 1800000
//
//               // Log.i(TAG,"wrEndTime ${Utils.formattedTimeHHmmss(wrEndTime)}")
//
//
//                var now: Array<String> = timeNow.split("T".toRegex()).toTypedArray()
//                val nowTime: Long = Utils.longConversionTime(now[1])
//
//
////                Log.i(TAG,"start ${end[1]}")
////                Log.i(TAG,"nowTime ${Utils.formattedTimeHHmmss(nowTime)}")
//                Log.i(TAG,"wrEndTime $wrEndTime");
//                Log.i(TAG,"nowTime $nowTime");
//
//                if(wrEndTime <= nowTime){
//                    Log.i(TAG,"No reason for late going")
//                    onSelfieUploaded(commentUplodedFile)
//                }else if(wrEndTime > nowTime){
//                    if(distance <= 100){
//                        Log.i(TAG,"Early going reason")
//                        val dialog = Dialog(requireActivity())
//                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                        dialog.setCancelable(false)
//                        dialog.setContentView(R.layout.dialog_staff_late_attendance)
//                        val lp = WindowManager.LayoutParams()
//                        lp.copyFrom(dialog.window!!.attributes)
//                        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//                        lp.gravity = Gravity.CENTER
//                        dialog.window!!.attributes = lp
//
//                        var editTextLateReason = dialog.findViewById(R.id.editTextLateReason) as TextInputEditText
//                        var editTextEarlyReason = dialog.findViewById(R.id.editTextEarlyReason) as TextInputEditText
//                        editTextLateReason.visibility = View.GONE
//                        editTextEarlyReason.visibility = View.VISIBLE
//
//                        val punchInButton = dialog.findViewById(R.id.punchInButton) as AppCompatButton
//                        punchInButton.setOnClickListener {
//
//                            if(staffPunchViewModel.validateField(editTextEarlyReason,
//                                    "Reason for leaving early",requireActivity(),binding.constraintLayoutContent)){
//
//                                Global.lateReason = editTextEarlyReason.text.toString()
//
//                                onSelfieUploaded(commentUplodedFile)
//                                dialog.dismiss()
//
//                            }
//                        }
//                        dialog.show()
//                    }
//                    else if(distance > 100){
//                        Log.i(TAG,"out off range reason and Early going reason")
//                        try{
//                            val dialog = Dialog(requireActivity())
//                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                            dialog.setCancelable(false)
//                            dialog.setContentView(R.layout.dialog_staff_attendance)
//                            val lp = WindowManager.LayoutParams()
//                            lp.copyFrom(dialog.window!!.attributes)
//                            lp.width = WindowManager.LayoutParams.MATCH_PARENT
//                            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//                            lp.gravity = Gravity.CENTER
//                            dialog.window!!.attributes = lp
//
//                            var editOutOffRange =
//                                dialog.findViewById(R.id.editOutOffRange) as TextInputEditText
//                            var editTextLateReason =
//                                dialog.findViewById(R.id.editTextLateReason) as TextInputEditText
//                            var editTextEarlyReason =
//                                dialog.findViewById(R.id.editTextEarlyReason) as TextInputEditText
//                            editTextLateReason.visibility = View.GONE
//                            editTextEarlyReason.visibility = View.VISIBLE
//
//                            mapFragment =
//                                requireActivity().supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//                            if (!requestPermissions(requireActivity())) {
//                                if (mapFragment != null) {
//                                    mapFragment!!.getMapAsync(this)
//                                }
//                            }
//                            if (!Places.isInitialized()) {
//                                Places.initialize(
//                                    requireActivity(),
//                                    resources.getString(R.string.google_maps_key)
//                                );
//                            }
//
//                            textViewDistance = dialog.findViewById(R.id.textViewDistance) as TextView
//                            textViewRange = dialog.findViewById(R.id.textViewRange) as TextView
//
//                            val imageViewClose = dialog.findViewById(R.id.imageViewClose) as ImageView
//                            imageViewClose.setOnClickListener {
//                                if (mapFragment != null)
//                                    requireActivity().supportFragmentManager.beginTransaction()
//                                        .remove(mapFragment!!).commit();
//
//                                dialog.dismiss()
//                            }
//
//                            val punchInButton =
//                                dialog.findViewById(R.id.punchInButton) as AppCompatButton
//                            punchInButton.setOnClickListener {
//
//                                if (staffPunchViewModel.validateField(
//                                        editOutOffRange,
//                                        "Give Out Off Range Reason",
//                                        requireActivity(),
//                                        binding.constraintLayoutContent
//                                    )
//                                    && staffPunchViewModel.validateField(
//                                        editTextEarlyReason,
//                                        "Reason for leaving early",
//                                        requireActivity(),
//                                        binding.constraintLayoutContent
//                                    )
//                                ) {
//
//                                    Global.outOffRangeReason = editOutOffRange.text.toString()
//                                    Global.lateReason = editTextEarlyReason.text.toString()
//
//                                    if (mapFragment != null)
//                                        requireActivity().supportFragmentManager.beginTransaction()
//                                            .remove(mapFragment!!).commit();
//
////                                val intent = Intent(requireActivity(), SelfieActivity()::class.java)
////                                startActivity(intent)
//                                    var fragmentManager = requireActivity().supportFragmentManager
//                                    var fragmentTransaction = fragmentManager.beginTransaction()
//                                    fragmentTransaction.replace(
//                                        R.id.nav_staff_host_fragment,
//                                        SelfieFragment()
//                                    )
////            .addToBackStack("home").commit()
//                                        .commit()
//                                    dialog.dismiss()
//
//                                }
//                            }
//
//                            dialog.show()
//
//                        }catch(e : IllegalArgumentException){
//                            Log.i(TAG,"IllegalArgumentException $e")
//                            // Toast.makeText(requireActivity(), "Try again", Toast.LENGTH_SHORT).show()
//                            Utils.getSnackBar4K(requireActivity(),"Try again",binding.constraintLayoutContent)
//                        }
//                    }
//
//                }
//
//
//
//
//
////                if (Utils.checkOnlyTime(Utils.formattedTimeHHmmss(nowTime), Utils.formattedTimeHHmmss(wrEndTime), "After") ||
////                            Utils.checkOnlyTime(Utils.formattedTimeHHmmss(nowTime),Utils.formattedTimeHHmmss(wrEndTime), "Equal")) {
////                    Log.i(TAG,"test end inside ")
////                }
////                else if(Utils.checkOnlyTime(Utils.formattedTimeHHmmss(nowTime), Utils.formattedTimeHHmmss(wrEndTime), "Before")){
////
////                    if(distance <= 100){
////                    }
////                    else if(distance > 100) {
////
////                }
//
//
//            }catch(e : NullPointerException){
//                Log.i(TAG,"e $e")
//                Utils.getSnackBar4K(requireActivity(),"Try again",binding.constraintLayoutContent)
//              //  Toast.makeText(requireActivity(), "Close dialog and try again", Toast.LENGTH_SHORT).show()
//            }
//
//        } else {
//            enableLocationSettings()
//        }
    }


    fun onLocationPunchOutCallBack(user_map_latitude: Double, user_map_longitude: Double) {
        constraintText?.visibility = View.VISIBLE
        constraintOutOffRange?.visibility = View.VISIBLE
        //constraintLateEarly
        constraintLateEarly?.visibility = View.VISIBLE
        constraintButtons?.visibility = View.VISIBLE
        progressLocation?.visibility = View.GONE
        var nowTimeSer = ""
        var workStrtTime = ""
        var workEdTime = ""

        try {
          //  var start: Array<String> = workStartTime.split("T".toRegex()).toTypedArray()
            val wrStartTime: Long = Utils.longConversionTime(workStartTime)

            workStrtTime = Utils.formattedTimehmma(wrStartTime)
        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.i(TAG, "ArrayIndexOutOfBoundsException $e")
        }

        nowTimeSer = try {
            val now: Array<String> = timeNow.split("T".toRegex()).toTypedArray()
            val nowTime = Utils.longConversionTime(now[1])
            Utils.formattedTimehmma(nowTime)

        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.i(TAG, "ArrayIndexOutOfBoundsException $e")
            val sdf = SimpleDateFormat("h:mm a")
            sdf.format(Date())
        }

        try {
         //   var end: Array<String> = workEndTime.split("T".toRegex()).toTypedArray()
            val eTime: Long = Utils.longConversionTime(workEndTime)
            var wrEndTime = eTime - 1800000

            workEdTime = Utils.formattedTimehmma(wrEndTime)

        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.i(TAG, "ArrayIndexOutOfBoundsException $e")
        }

        var editOutOffRange = dialog?.findViewById(R.id.editOutOffRange) as TextInputEditText
        var editTextLateReason = dialog?.findViewById(R.id.editTextLateReason) as TextInputEditText
        var editTextEarlyReason =
            dialog?.findViewById(R.id.editTextEarlyReason) as TextInputEditText
        //textInputEarlyReason
        var textInputEarlyReason =
            dialog?.findViewById(R.id.textInputEarlyReason) as TextInputLayout
        var textInputOutOffRange =
            dialog?.findViewById(R.id.textInputOutOffRange) as TextInputLayout
        var textInputLateReason = dialog?.findViewById(R.id.textInputLateReason) as TextInputLayout
        val punchInButton = dialog?.findViewById(R.id.punchInButton) as AppCompatButton
        val refreshButton = dialog?.findViewById(R.id.refreshButton) as AppCompatButton
        textInputLateReason.visibility = View.GONE

        //  mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?


        textComeEarly = dialog?.findViewById(R.id.textComeEarly) as TextView
        textViewDistance = dialog?.findViewById(R.id.textViewDistance) as TextView
        textViewRange = dialog?.findViewById(R.id.textViewRange) as TextView

        Log.i(TAG, "user_map_latitude $user_map_latitude")
        Log.i(TAG, "user_map_longitude $user_map_longitude")
        Log.i(TAG, "WORK_PLACE_LATTITUDE $WORK_PLACE_LATTITUDE")
        Log.i(TAG, "WORK_PLACE_LONGITUDE $WORK_PLACE_LONGITUDE")
        Log.i(TAG, "WORK_PLACE_LONGITUDE $pUNCHDISTANCE")

//            val locationA = Location("Company Location")
//            locationA.latitude = WORK_PLACE_LATTITUDE
//            locationA.longitude = WORK_PLACE_LONGITUDE
        if ((user_map_latitude == 0.0 && user_map_longitude == 0.0) ||
            (WORK_PLACE_LATTITUDE == 0.0 && WORK_PLACE_LONGITUDE == 0.0)
        ) {
            punchInButton.visibility = View.GONE
            refreshButton.visibility = View.VISIBLE
            textInputOutOffRange.visibility = View.GONE
            textInputEarlyReason.visibility = View.GONE
            textViewDistance!!.visibility = View.VISIBLE
            textViewRange!!.visibility = View.VISIBLE
            textViewDistance?.setBackgroundColor(resources.getColor(R.color.gray_100))
            textViewRange?.setBackgroundColor(resources.getColor(R.color.gray_100))
        } else {
            punchInButton.visibility = View.VISIBLE
            refreshButton.visibility = View.GONE
        }
//            val locationB = Location("Current Location")
//            locationB.latitude = user_map_latitude
//            locationB.longitude = user_map_longitude
//            distance = locationA.distanceTo(locationB).toDouble()


        val office = LatLng(WORK_PLACE_LATTITUDE, WORK_PLACE_LONGITUDE)
        val currentLocation = LatLng(user_map_latitude, user_map_longitude)
        var sphericalDistance = meterDistanceBetweenPoints(
            user_map_latitude,
            user_map_longitude,
            WORK_PLACE_LATTITUDE,
            WORK_PLACE_LONGITUDE
        )
        distance = sphericalDistance
        //   if(distance == 0 ){distance = 1}

        binding.textViewLatLong.text = "User Current Location \nLatitude $user_map_latitude " +
                "\nLongitude $user_map_longitude \n\nWork Place Location \nLatitude $WORK_PLACE_LATTITUDE \nLongitude $WORK_PLACE_LONGITUDE \n\n" +
                "Office Punch Radius  $pUNCHDISTANCE meters\nWork Start Time : $workStrtTime\nWork End Time : $workEdTime"

        Log.i(TAG, "distance $distance")


//        Log.i(TAG, "start $wrStartTime")
//        Log.i(TAG, "now $nowTime")
//        Log.i(TAG, "wrStartTime ${Utils.formattedTimehmma(wrStartTime)}")
//        Log.i(TAG, "wrEndTime ${Utils.formattedTimehmma(wrEndTime)}")
//        Log.i(TAG, "nowTime ${Utils.formattedTimehmma(nowTime)}")

        if (Utils.checkOnlyTime(nowTimeSer, workEdTime, "After") ||
            Utils.checkOnlyTime(nowTimeSer, workEdTime, "Equal")
        ) {
            textInputOutOffRange.visibility = View.GONE
            textInputEarlyReason.visibility = View.GONE
            textViewDistance!!.visibility = View.GONE
            textViewRange!!.visibility = View.GONE
            Log.i(TAG, "No reason for after leave")
            textComeEarly!!.text = "You can go now. Thank you"
            textComeEarly!!.visibility = View.VISIBLE

            if (distance > pUNCHDISTANCE) {
                Log.i(TAG, "early reason leave and out off reason")


                textViewDistance!!.visibility = View.VISIBLE
                textViewRange!!.visibility = View.VISIBLE


                if ((user_map_longitude != 0.0 && user_map_latitude != 0.0) &&
                    (WORK_PLACE_LATTITUDE != 0.0 && WORK_PLACE_LONGITUDE != 0.0)
                ) {
                    textInputOutOffRange.visibility = View.VISIBLE
                    // textInputEarlyReason.visibility = View.VISIBLE
                    //   textViewDistance?.text = "Distance : ${distance/1000} kms"
                    textViewDistance?.text =
                        "Distance : ${DecimalFormat("##.##").format(distance / 1000)} kms"

                    textViewRange?.setTextColor(resources.getColor(R.color.color_physics))
                    textViewRange?.text = "Your current location is not in range"


                    binding.textViewData.text = "Distance between user location to work place location is $distance meters\nYour current location is not in range\nDistance : " +
                            "${DecimalFormat("##.##").format(distance / 1000)} kms"

                } else {
                    textInputOutOffRange.visibility = View.GONE
                    textInputEarlyReason.visibility = View.GONE
                    textViewDistance?.setBackgroundColor(resources.getColor(R.color.gray_100))
                    textViewRange?.setBackgroundColor(resources.getColor(R.color.gray_100))
                    punchInButton.visibility = View.GONE
                    refreshButton.visibility = View.VISIBLE
                }


                punchInButton.setOnClickListener {

                    if (staffPunchViewModel.validateField(
                            editOutOffRange,
                            "Give Out Off Range Reason",
                            requireActivity(),
                            binding.constraintLayoutContent
                        )
                    ) {

                        Global.user_default_latitude = user_map_latitude
                        Global.user_default_longitude = user_map_longitude

                        Global.outOffRangeReason = editOutOffRange.text.toString()
                        Global.lateReason = editTextEarlyReason.text.toString()

                        if (mapFragment != null)
                            requireActivity().supportFragmentManager.beginTransaction()
                                .remove(mapFragment!!).commit();


                        var fragmentManager = requireActivity().supportFragmentManager
                        var fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.nav_staff_host_fragment, SelfieFragment())
//            .addToBackStack("home").commit()
                            .commit()
                        dialog?.dismiss()

                    }
                }

            }
            else if (distance <= pUNCHDISTANCE /* && distance != 0*/) {

                binding.textViewData.text = "Distance between user location to work place location is $distance meters\n Your inside the campus \nDistance : " +
                        "${DecimalFormat("##.##").format(distance / 1000)} kms"

                punchInButton.setOnClickListener {
                    if (mapFragment != null)
                        requireActivity().supportFragmentManager.beginTransaction()
                            .remove(mapFragment!!).commit();

                    Global.user_default_latitude = user_map_latitude
                    Global.user_default_longitude = user_map_longitude

                    // onSelfieUploaded(commentUplodedFile)
                    var fragmentManager = requireActivity().supportFragmentManager
                    var fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_staff_host_fragment, SelfieFragment())
//            .addToBackStack("home").commit()
                        .commit()
                    dialog?.dismiss()
                }
            }

        } else if (Utils.checkOnlyTime(nowTimeSer, workEdTime, "Before")) {
            textComeEarly!!.visibility = View.GONE
            if (distance <= pUNCHDISTANCE) {
                Log.i(TAG, "early reason leave only")

                Log.i(TAG, "distance<=100 $distance")
                textInputOutOffRange.visibility = View.GONE
                textInputEarlyReason.visibility = View.VISIBLE
                textViewDistance!!.visibility = View.GONE
                textViewRange!!.visibility = View.GONE

                binding.textViewData.text = "Distance between user location to work place location is $distance meters\n Your inside the campus \nDistance : " +
                        "${DecimalFormat("##.##").format(distance / 1000)} kms"

                punchInButton.setOnClickListener {

                    if (staffPunchViewModel.validateField(
                            editTextEarlyReason,
                            "Give Late Reason", requireActivity(), binding.constraintLayoutContent
                        )
                    ) {

                        Global.user_default_latitude = user_map_latitude
                        Global.user_default_longitude = user_map_longitude

                        Global.outOffRangeReason = editOutOffRange.text.toString()
                        Global.lateReason = editTextEarlyReason.text.toString()

                        if (mapFragment != null)
                            requireActivity().supportFragmentManager.beginTransaction()
                                .remove(mapFragment!!).commit();


                        var fragmentManager = requireActivity().supportFragmentManager
                        var fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.nav_staff_host_fragment, SelfieFragment())
//            .addToBackStack("home").commit()
                            .commit()
                        dialog?.dismiss()

                    }
                }


            }
            else if (distance > pUNCHDISTANCE) {
                Log.i(TAG, "early reason leave and out off reason")


                textViewDistance!!.visibility = View.VISIBLE
                textViewRange!!.visibility = View.VISIBLE


                if ((user_map_longitude != 0.0 && user_map_latitude != 0.0) &&
                    (WORK_PLACE_LATTITUDE != 0.0 && WORK_PLACE_LONGITUDE != 0.0)
                ) {
                    textInputOutOffRange.visibility = View.VISIBLE
                    textInputEarlyReason.visibility = View.VISIBLE
                    //    textViewDistance?.text = "Distance : ${distance/1000} kms"
                    // textViewDistance?.text = "Distance : $distance kms"
                    textViewDistance?.text =
                        "Distance : ${DecimalFormat("##.##").format(distance / 1000)} kms"

                    textViewRange?.setTextColor(resources.getColor(R.color.color_physics))
                    textViewRange?.text = "Your current location is not in range"


                    binding.textViewData.text = "Distance between user location to work place location is $distance meters\nYour current location is not in range\nDistance : " +
                            "${DecimalFormat("##.##").format(distance / 1000)} kms"

                } else {
                    textInputOutOffRange.visibility = View.GONE
                    textInputEarlyReason.visibility = View.GONE
                    textViewDistance?.setBackgroundColor(resources.getColor(R.color.gray_100))
                    textViewRange?.setBackgroundColor(resources.getColor(R.color.gray_100))
                    punchInButton.visibility = View.GONE
                    refreshButton.visibility = View.VISIBLE
                }


                punchInButton.setOnClickListener {

                    if (staffPunchViewModel.validateField(
                            editOutOffRange,
                            "Give Out Off Range Reason",
                            requireActivity(),
                            binding.constraintLayoutContent
                        )
                        && staffPunchViewModel.validateField(
                            editTextEarlyReason,
                            "Give Late Reason", requireActivity(), binding.constraintLayoutContent
                        )
                    ) {

                        Global.user_default_latitude = user_map_latitude
                        Global.user_default_longitude = user_map_longitude

                        Global.outOffRangeReason = editOutOffRange.text.toString()
                        Global.lateReason = editTextEarlyReason.text.toString()

                        if (mapFragment != null)
                            requireActivity().supportFragmentManager.beginTransaction()
                                .remove(mapFragment!!).commit();


                        var fragmentManager = requireActivity().supportFragmentManager
                        var fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.nav_staff_host_fragment, SelfieFragment())
//            .addToBackStack("home").commit()
                            .commit()
                        dialog?.dismiss()

                    }
                }

            }

        }

        refreshButton.setOnClickListener {

            getLastLocation()
            constraintText?.visibility = View.GONE
            constraintOutOffRange?.visibility = View.GONE
            //constraintLateEarly
            constraintLateEarly?.visibility = View.GONE
            constraintButtons?.visibility = View.GONE
            progressLocation?.visibility = View.VISIBLE

        }

    }

    //http://staff.teachdaily.in//ElixirApi/Staff/UploadSelfie


    fun startCountDownTimer(status: String) {
        binding.progressBarCircle.progress = 0
        binding.progressBarDashed.progress = 0
        binding.textViewTimer.text = "00 : 00 : 00"
        // jika state kondisi false atau pause, untuk menghindari start yang berulang" jika user menekan tombol start

        Log.i(TAG, "status $status")
        Log.i(TAG, "running $running")

        if (!running) {
            running = true
            job = CoroutineScope(Dispatchers.IO).launch {
                while (running) {
                    delay(8)

                    // cast anyone to double to get result double
                    val second = (increment / 90.0) % 60
                    val progress = (second / 60) * 100

                    withContext(Dispatchers.Main) {
                        //    Log.i(TAG,"second $second")

                        //     progressBarCircle?.setValue(second.toFloat());
                        // progressBarDashed?.progress = second.toInt()
                        binding.progressBarCircle.progress = progressHour * 20
                        binding.progressBarDashed.progress = second.toInt()
                        binding.textViewTimer.text = Lap.convertToDuration(increment)
                    }

                    increment += 1
                }
            }

            runBlocking {
                if (status == "PunchIn") {
                    job!!.cancel()
                }
                Log.d("jobs-.cancel()", "Work Is Cancelled")
            }

//            if(status == "PunchIn"){
//                running = false
//                job!!.cancel()
//            }else if(status == "PunchOut"){
//                running = true
//                job!!.start()
//            }
        }

    }

    @SuppressLint("HardwareIds")
    private fun getSystemDetail() {
        deviceBrand = Build.BRAND
        deviceId = "${
            Settings.Secure.getString(
                requireActivity().contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }"
        deviceModel = Build.MODEL
//
//
//
//        return "Brand: ${Build.BRAND} \n" +
//                "DeviceID: ${
//                    Settings.Secure.getString(
//                        requireActivity().contentResolver,
//                        Settings.Secure.ANDROID_ID
//                    )
//                } \n" +
//                "Model: ${Build.MODEL} \n" +
//                "ID: ${Build.ID} \n" +
//                "SDK: ${Build.VERSION.SDK_INT} \n" +
//                "Manufacture: ${Build.MANUFACTURER} \n" +
//                "Brand: ${Build.BRAND} \n" +
//                "User: ${Build.USER} \n" +
//                "Type: ${Build.TYPE} \n" +
//                "Base: ${Build.VERSION_CODES.BASE} \n" +
//                "Incremental: ${Build.VERSION.INCREMENTAL} \n" +
//                "Board: ${Build.BOARD} \n" +
//                "Host: ${Build.HOST} \n" +
//                "FingerPrint: ${Build.FINGERPRINT} \n" +
//                "Version Code: ${Build.VERSION.RELEASE}"
    }


    fun timeNowMethod() {
        staffPunchViewModel.getPunchAttendance(adminId, schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG, "timeNowMethod resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            var punchingAction = response.punchingAction

                            workStartTime = punchingAction.sTAFFINTIME!!
                            workEndTime = punchingAction.sTAFFOUTTIME!!
                            timeNow = punchingAction.tIMENOW!!
                            WORK_PLACE_LATTITUDE = punchingAction.lATTITUDE!!.toDouble()
                            WORK_PLACE_LONGITUDE = punchingAction.lONGITUDE!!.toDouble()


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
    }

//    2230
//    9442219914 ak steels


    fun getPunchStaffAttendance() {
        staffPunchViewModel.getPunchAttendance(adminId, schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG, "PunchStaffAttendance resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            var punchingAction = response.punchingAction
                            var punchingHistory = response.punchingHistory

                            if (punchingAction.action == "PunchIn") {
                                punchInButtonScr?.visibility = View.VISIBLE
                                punchOutButtonScr?.visibility = View.GONE
                                binding.progressBarCircle.progress = 0
                                binding.progressBarDashed.progress = 0
                                binding.textViewTimer.text = "00 : 00 : 00"
                                running = true
                                startCountDownTimer("PunchIn")
                            } else if (punchingAction.action == "PunchOut") {
                                punchInButtonScr?.visibility = View.GONE
                                punchOutButtonScr?.visibility = View.VISIBLE

                            }

                            //                            bottomAdminPunchList = BottomAdminPunchList(punchingAction,punchingHistory)
//                            bottomAdminPunchList.show(requireActivity().supportFragmentManager, "TAG")

                            if (punchingAction.sTAFFIMAGE != "") {
                                Glide.with(requireActivity()).load(
                                    Global.event_url + "/Photos/StaffImage/" + punchingAction.sTAFFIMAGE
                                ) //STAFF_IMAGE -> http://demo.passdaily.in/Photos/StaffImageA0D181192F902C6AE338BEDF36FC3251.jpg
                                    //STAFF_IMAGE -> 1A07304FC14301B29E49B4DA301B0EA5.png
                                    .apply(
                                        RequestOptions.centerCropTransform()
                                            .dontAnimate()
                                            .placeholder(R.drawable.round_account_button_with_user_inside)
                                    )
                                    .thumbnail(0.5f)
                                    .into(shapeImageView!!)
                            }
                            textViewStaff!!.text = punchingAction.sTAFFFNAME

                            if(punchingHistory.isNotEmpty()){
                                recyclerList!!.visibility = View.VISIBLE
                                textViewNoReport!!.visibility = View.GONE
                                mAdapter = PunchAttendanceAdapter(
                                    punchingHistory, requireActivity(), TAG
                                )
                                recyclerList!!.adapter = mAdapter
                            }else{
                                recyclerList!!.visibility = View.GONE
                                textViewNoReport!!.visibility = View.VISIBLE
                            }



                            workStartTime = punchingAction.sTAFFINTIME!!
                            workEndTime = punchingAction.sTAFFOUTTIME!!
                            timeNow = punchingAction.tIMENOW!!
                            WORK_PLACE_LATTITUDE = punchingAction.lATTITUDE!!.toDouble()
                            WORK_PLACE_LONGITUDE = punchingAction.lONGITUDE!!.toDouble()

                            pUNCHDISTANCE = punchingAction.pUNCHDISTANCE

                            var punchingDetails = response.punchingDetails
                            for (i in punchingDetails) {
                                Global.punchingId = i.pUNCHINGID
                                Global.workPunchInTime = i.pUNCHINTIME!!


                                if (!i.pUNCHINTIME.isNullOrEmpty() && i.pUNCHOUTTIME.isNullOrEmpty()) {

                                    var stTime: Array<String> =
                                        i.pUNCHINTIME!!.split("T".toRegex()).toTypedArray()
                                    val startTime: Long = Utils.longConversionTime(stTime[1])

                                    var tNow: Array<String> =
                                        timeNow.split("T".toRegex()).toTypedArray()
                                    val timeNow: Long = Utils.longConversionTime(tNow[1])

                                    increment =
                                        ((timeNow.toInt() - startTime.toInt()) / 11.105).toInt()
                                    var differnts = timeNow.toInt() - startTime.toInt()
                                    Log.i(TAG, "timeNow $timeNow")
                                    Log.i(TAG, "startTime $startTime")
                                    Log.i(TAG, "diff ${timeNow.toInt() - startTime.toInt()}")
                                    Log.i(TAG, "increment $increment")

                                    progressHour =
                                        TimeUnit.MILLISECONDS.toHours(differnts.toLong()).toInt()
                                    running = false
                                    startCountDownTimer("PunchOut")

                                }
                            }


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
    }

    class PunchAttendanceAdapter(
        var punchingHistory: ArrayList<StaffPunchingStatusModel.PunchingHistory>,
        var context: Context, var TAG : String)
        : RecyclerView.Adapter<PunchAttendanceAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewInTime: TextView = view.findViewById(R.id.textViewInTime)
            var textViewInTimeDate: TextView = view.findViewById(R.id.textViewInTimeDate)
            var textViewOutTime: TextView = view.findViewById(R.id.textViewOutTime)
            var textViewOutTimeDate: TextView = view.findViewById(R.id.textViewOutTimeDate)
            var textViewTotalTime : TextView = view.findViewById(R.id.textViewTotalTime)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.punch_list_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if(!punchingHistory[position].pUNCHINTIME.isNullOrBlank()) {
                val date: Array<String> =
                    punchingHistory[position].pUNCHINTIME.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                holder.textViewInTime.text = "In : ${Utils.parseDateTime(date[1])}"
                holder.textViewInTimeDate.text = Utils.parseDateToddMMMyyyy(date[0])
            }else{
                holder.textViewInTime.text = "I "
                holder.textViewInTimeDate.text = "--"
            }

            if(!punchingHistory[position].pUNCHOUTTIME.isNullOrBlank()) {
                val date: Array<String> =
                    punchingHistory[position].pUNCHOUTTIME.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                holder.textViewOutTime.text = "Out : ${Utils.parseDateTime(date[1])}"
                holder.textViewOutTimeDate.text = Utils.parseDateToddMMMyyyy(date[0])
            }else{
                holder.textViewOutTime.text = "Out "
                holder.textViewOutTimeDate.text = "--"
            }

            if(!punchingHistory[position].dURATION.isNullOrBlank()) {
                holder.textViewTotalTime.text = punchingHistory[position].dURATION
//                var hour = punchingHistory[position].dURATION.split(":")
//                var hours = ""
//                if(hour[0] != "00"){
//                    hours = "${hour[0]}HRS "
//                }
                // holder.textViewTotalTime.text = "$hours${hour[1]}MIN"
//                holder.textViewTotalTime.text = "${Utils.parseDateTimeHour(punchingHistory[position].dURATION)} HRS"  +
//                        " ${Utils.parseDateTimeMin(punchingHistory[position].dURATION)} MIN"
            }else{
                holder.textViewTotalTime.text = "--"
            }


            if (position == punchingHistory.size - 1) {
                val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
                params.bottomMargin = 30 // last item bottom margin
                holder.itemView.layoutParams = params
            } else {
                val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
                params.bottomMargin = 0 // other items bottom margin
                holder.itemView.layoutParams = params
            }


        }

        override fun getItemCount(): Int {
            return punchingHistory.size
        }
        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }

    }



//    private fun initView() {
////        contactViewModel.getContactUsObservable()
////            .observe(requireActivity(), {
////                if (it != null) {
////                    //  contactUs = it.ContactusList
////                    for(i in it.ContactusList.indices){
////                        LATTITUDE = it.ContactusList[i].LATTITUDE.toDouble()
////                        LONGITUDE = it.ContactusList[i].LONGITUDE.toDouble()
////                    }
////                    mapFragment?.getMapAsync(callback)
////                }
////            })
//        val studentId = 0
//        contactViewModel.getContactUsItems(studentId)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            for(contactUs in response.contactusList) {
//                                WORK_PLACE_LATTITUDE = contactUs.lATTITUDE.toDouble()
//                                WORK_PLACE_LONGITUDE = contactUs.lONGITUDE.toDouble()
//                            }
////                            if (mapFragment != null) {
////                              //  mapFragment?.getMapAsync(callback)
////                                mapFragment?.getMapAsync(this)
////                            }
//
////                            val requestId = "2"
////
////                            geofenceList.add(
////                                Geofence.Builder() // Set the request ID of the geofence. This is a string to identify this
////                                    // geofence.
////                                    .setRequestId(requestId)
////                                    .setCircularRegion(
////                                        LATTITUDE!!,
////                                        LONGITUDE!!,
////                                        radius.toFloat()
////                                    ) // add the radius in float.
////                                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
////                                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
////                                    .setNotificationResponsiveness(1000)
////                                    .build()
////                            )
////
////                            createGeofencingClient()
//
//                        }
//                        Status.ERROR -> {
//                            Log.i("TAG", "Error ${Status.ERROR}")
//                        }
//                        Status.LOADING -> {
//                            Log.i("TAG", "resource ${resource.status}")
//                            Log.i("TAG", "message ${resource.message}")
//                        }
//                    }
//                }
//            })
//    }

    fun isLocationEnabledBool(): Boolean {
        val lm =
            requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        // exceptions will be thrown if provider is not permitted.
        val gpsEnabled = try {
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: java.lang.Exception) {
            false
        }
        val networkEnabled = try {
            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: java.lang.Exception) {
            false
        }
        return gpsEnabled && networkEnabled
    }


    fun enableLocationSettings() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 1 * 1000
        locationRequest.fastestInterval = 2 * 1000
    //    locationRequest.smallestDisplacement = 50F;

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        LocationServices
            .getSettingsClient(requireActivity())
            .checkLocationSettings(builder.build())
            .addOnSuccessListener(
                requireActivity()
            ) { _: LocationSettingsResponse? -> }
            .addOnFailureListener(
                requireActivity()
            ) { ex: Exception? ->
                if (ex is ResolvableApiException) {
                    // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                        ex.startResolutionForResult(requireActivity(), REQUEST_LOCATION)


                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
    }


    @Synchronized
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(requireActivity())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        // googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 17f))

        /////user location code
        googleMap!!.uiSettings.isZoomControlsEnabled = true
        googleMap!!.uiSettings.isZoomGesturesEnabled = true
        googleMap!!.uiSettings.isCompassEnabled = true


        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
                googleMap!!.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            googleMap!!.isMyLocationEnabled = true
        }
    }


    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }

        user_map_latitude = location.latitude
        user_map_longitude = location.longitude
        //Showing Current Location Marker on Map
        //  val latLng = LatLng(location.latitude, location.longitude)
        val latLng = LatLng(user_map_latitude, user_map_longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        if (isAdded) {
            val locationManager =
                requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            val provider = locationManager.getBestProvider(Criteria(), true)
//            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                OnGPS();
//            } else {
//                getLocation();
//            }


            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
//            val locations = locationManager.getLastKnownLocation(provider!!)
//            val providerList = locationManager.allProviders
//            if (null != locations && providerList.size > 0) {
//                val user_map_longitude = locations.longitude
//                val user_map_latitude = locations.latitude
//
//
//                val geocoder = Geocoder(
//                    requireActivity().applicationContext,
//                    Locale.getDefault()
//                )
//                try {
//                    val listAddresses = geocoder.getFromLocation(
//                        user_map_latitude,
//                        user_map_longitude, 1
//                    )
//                    if (null != listAddresses && listAddresses.size > 0) {
//                        val state = listAddresses[0].adminArea
//                        val country = listAddresses[0].countryName
//                        val subLocality = listAddresses[0].subLocality
//                        markerOptions.title(
//                            "" + latLng + "," + subLocality + "," + state
//                                    + "," + country
//                        )
//                    }
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            ///////////////////////////current location in orange
            mCurrLocationMarker = googleMap!!.addMarker(
                markerOptions
                    .title("Your Location")
            )
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient!!,
                    this
                )
            }
        }
        if (Global.punchStatus == "In") {
            // onLocationPunchInCallBack(user_map_latitude,user_map_longitude)
            onLocationPunchInCallBack(user_map_latitude, user_map_longitude)

        } else if (Global.punchStatus == "Out") {
            onLocationPunchOutCallBack(user_map_latitude, user_map_longitude)
        }



        Log.i(TAG, "long - lat $user_map_longitude $user_map_latitude")



        try {
            val sydney = LatLng(WORK_PLACE_LATTITUDE, WORK_PLACE_LONGITUDE)
            googleMap!!.addMarker(
                MarkerOptions()
                    .position(sydney)
                    .title("Passdaily Pvt Ltd")
            )
            //   new_Marker();

//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18f))
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,17f))


            googleMap!!.addCircle(
                CircleOptions()
                    .center(LatLng(WORK_PLACE_LATTITUDE, WORK_PLACE_LONGITUDE))
                    .radius(pUNCHDISTANCE.toDouble())
                    .strokeWidth(1f)
                    .strokeColor(Color.parseColor("#500084d3"))
                    .fillColor(Color.parseColor("#500084d3"))
//                .radius(100.0)
//                .strokeWidth(2f)
//                .strokeColor(ContextCompat.getColor(this, R.color.light_blue))
//                .fillColor(ContextCompat.getColor(this, R.color.light_blue))
            )


        } catch (e: NullPointerException) {
            Toast.makeText(
                requireActivity(), "Close dialog and try again", Toast.LENGTH_SHORT
            ).show()
        }


    }


    override fun onStart() {
        super.onStart()
        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }
    }

    override fun onConnected(connection: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        mLocationRequest!!.interval = 5
        mLocationRequest!!.fastestInterval = 0
        mLocationRequest!!.numUpdates = 1



//        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
//        builder.setAlwaysShow(true)


        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
         //   LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

            LocationServices.FusedLocationApi.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
                mGoogleApiClient!!,
                mLocationRequest!!, this)
        }
    }
//
//
//    fun enableGps() {
//
//        val mLocationRequest = LocationRequest.create()
//            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//            .setInterval(2000)
//            .setFastestInterval(1000)
//
//        val settingsBuilder = LocationSettingsRequest.Builder()
//            .addLocationRequest(mLocationRequest)
//        settingsBuilder.setAlwaysShow(true)
//
//        val result = LocationServices.getSettingsClient(requireActivity()).checkLocationSettings(settingsBuilder.build())
//        result.addOnCompleteListener { task ->
//
//            //getting the status code from exception
//            try {
//                task.getResult(ApiException::class.java)
//            } catch (ex: ApiException) {
//
//                when (ex.statusCode) {
//
//                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
//
//                        Toast.makeText(requireActivity(),"GPS IS OFF",Toast.LENGTH_SHORT).show()
//
//                        // Show the dialog by calling startResolutionForResult(), and check the result
//                        // in onActivityResult().
//                        val resolvableApiException = ex as ResolvableApiException
//                        resolvableApiException.startResolutionForResult(requireActivity(),199
//                        )
//                    } catch (e: IntentSender.SendIntentException) {
//                        Toast.makeText(requireActivity(),"PendingIntent unable to execute request.",Toast.LENGTH_SHORT).show()
//
//                    }
//
//                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
//
//                        Toast.makeText(
//                            requireActivity(),
//                            "Something is wrong in your GPS",
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                    }
//
//
//                }
//            }
//
//
//
//        }
//
//
//    }


    private fun getGeofencePendingIntent(): PendingIntent? {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent
        }
        Log.i(TAG, "get Geofence Pending Intent")
//        Toast.makeText(applicationContext, "starting broadcast", Toast.LENGTH_SHORT).show()
//        val intent = Intent(this, MyBroadCastReceiver::class.java)
//        geofencePendingIntent =
//            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return geofencePendingIntent
    }


    private fun createGeofencingRequest(): GeofencingRequest {
        val builder = GeofencingRequest.Builder()
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        builder.addGeofences(geofenceList)
        return builder.build()
    }


    override fun onConnectionSuspended(p0: Int) {
        Log.i(TAG, "onConnectionSuspended")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.i(TAG, "onConnectionFailed")
    }


//    private fun requestPermissions(activity: Activity): Boolean {
//        Log.v(TAG, "requestPermissions() called")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val permissionsList: MutableList<String> = ArrayList()
//            val reasonList: MutableList<String> = ArrayList()
//            if (!addPermission(
//                    permissionsList,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    activity
//                )
//            ) {
//                reasonList.add(
//                    """
//                    LOCATION PERMISSION: Needs to be granted discover track Location!
//                   """.trimIndent()
//                )
//            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
//                secondPassR) {
//                if (!addPermission(
//                        permissionsList,
//                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//                        activity
//                    )
//                ) {
//                    reasonList.add(
//                        """
//                        BACKGROUND PERMISSION: Needs to be granted discover track Location!
//                        """.trimIndent()
//                    )
//                }
//            }
//            if (permissionsList.size > 0) {
//                activity.requestPermissions(
//                    permissionsList.toTypedArray(),  // newer Java recommended
//                    REQUEST_CODE_MULTIPLE_PERMISSIONS
//                )
//            } else {
//                return true
//            }
//        } else {
//            return true
//        }
//        return false
//    }
//
//    @TargetApi(23)
//    private fun addPermission(
//        permissionsList: MutableList<String>,
//        permission: String,
//        activity: Activity
//    ): Boolean {
//        Log.v(
//            TAG, "addPermission() called with: " + "permissionsList = " +
//                    "[" + permissionsList + "], permission = [" + permission + "]"
//        )
//        if (ActivityCompat.checkSelfPermission(
//                activity,
//                permission
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            permissionsList.add(permission)
//            // Check for Rationale Option
//            return activity.shouldShowRequestPermissionRationale(permission)
//        }
//        return true
//    }
//
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        @NonNull permissions: Array<String?>,
//        @NonNull grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        val permission = ""
//        Log.v(
//            TAG,
//            "onRequestPermissionsResult() called with: " + "requestCode = [" + requestCode +
//                    "], permissions = [" + permissions.contentToString() + "]," +
//                    " grantResults = [" + grantResults.contentToString() + "]"
//        )
//        var entryGranted = false
//        if (requestCode == REQUEST_CODE_MULTIPLE_PERMISSIONS) {
//            for (i in permissions.indices) {
//                when (permissions[i]) {
//                    Manifest.permission.ACCESS_FINE_LOCATION -> if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                        Log.d(TAG, "H@H: onRequestPermissionsResult: FINE LOCATION PERMISSION")
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                            Log.d(TAG, "H@H: Now requesting BACKGROUND PERMISSION for version 11+")
//                            secondPassR = true
//                            requestPermissions(requireActivity())
//                            return
//                        }
//                        entryGranted =true
//                    }
//                    Manifest.permission.ACCESS_BACKGROUND_LOCATION -> if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                        Log.d(TAG, "H@H: onRequestPermissionsResult: BACKGROUND PERMISSION")
//                        entryGranted =true
//                    }
//                }
//            }
//        }
//
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation()
//            }
//        }
//        Log.d(TAG, "Starting primary activity")
//        secondPassR = false
//        if(entryGranted){
//            if (mapFragment != null) {
//                mapFragment!!.getMapAsync(this)
//            }
//
//        }
//    }

    private fun requestPermissions(activity: Activity): Boolean {
        Log.i(TAG, "requestPermissions() called")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionsList: MutableList<String> = ArrayList()
            val reasonList: MutableList<String> = ArrayList()
            if (!addPermission(
                    permissionsList,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    activity
                )
            ) {
                reasonList.add(
                    """
                    LOCATION PERMISSION: Needs to be granted discover track Location!
                    """.trimIndent()
                )
            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
//                if (!addPermission(
//                        permissionsList,
//                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//                        activity
//                    )
//                ) {
//                    reasonList.add(
//                        """
//                        BACKGROUND PERMISSION: Needs to be granted discover track Location!
//                        """.trimIndent()
//                    )
//                }
//            }
            if (permissionsList.size > 0) {
                activity.requestPermissions(
                    permissionsList.toTypedArray(),  // newer Java recommended
                    REQUEST_CODE_MULTIPLE_PERMISSIONS
                )
            } else {
                return true
            }
        } else {
            return true
        }
        return false
    }

    @TargetApi(23)
    private fun addPermission(
        permissionsList: MutableList<String>,
        permission: String,
        activity: Activity
    ): Boolean {
        Log.i(
            TAG, "addPermission() called with: " + "permissionsList = " +
                    "[" + permissionsList + "], permission = [" + permission + "]"
        )
        if (ActivityCompat.checkSelfPermission(
                activity,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(permission)
            // Check for Rationale Option
            return activity.shouldShowRequestPermissionRationale(permission)
        }
        return true
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String?>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permission = ""
        Log.i(
            TAG,
            "onRequestPermissionsResult() called with: " + "requestCode = [" + requestCode +
                    "], permissions = [" + permissions.contentToString() + "]," +
                    " grantResults = [" + grantResults.contentToString() + "]"
        )
        var entryGranted = false
        if (requestCode == REQUEST_CODE_MULTIPLE_PERMISSIONS) {
            for (i in permissions.indices) {
                when (permissions[i]) {
                    Manifest.permission.ACCESS_FINE_LOCATION -> if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "H@H: onRequestPermissionsResult: FINE LOCATION PERMISSION")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            Log.d(TAG, "H@H: Now requesting BACKGROUND PERMISSION for version 11+")
                            secondPassR = true
                            requestPermissions(requireActivity())
                            return
                        }
                        entryGranted = true
                    }
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION -> if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "H@H: onRequestPermissionsResult: BACKGROUND PERMISSION")
                        entryGranted = true
                    }
                }
            }
        }
        Log.i(TAG, "Starting primary activity $entryGranted")
        secondPassR = false
        if (entryGranted) {
            if (mapFragment != null) {
                mapFragment!!.getMapAsync(this)
            }

        }
    }


    ///////////////////

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        //  Log.i(TAG,"lat ${location.latitude.toString()}")
                        //Log.i(TAG,"long ${location.longitude.toString()}")

                        user_map_latitude = location.latitude
                        user_map_longitude = location.longitude

                        onLocationPunchInCallBack(user_map_latitude, user_map_longitude)

                        //     binding.textViewLatLong.text = "Latitude :$user_map_latitude\nLongitude : $user_map_longitude"

                        //   latitudeTextView.setText(location.latitude.toString() + "")
                        //   longitTextView.setText(location.longitude.toString() + "")
                    }
                }
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions(requireActivity())
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        mLocationRequest.interval = 1 * 1000
        mLocationRequest.fastestInterval = 1 * 1000
        mLocationRequest.numUpdates = 1

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient?.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            //  latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude().toString() + "")
            // longitTextView.setText("Longitude: " + mLastLocation.getLongitude().toString() + "")
        }
    }

    // if location is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // method to check for permissions
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireActivity(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireActivity(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

//    // method to request for permissions
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(
//            requireActivity(), arrayOf(
//                android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ), PERMISSION_ID
//        )
//    }

    override fun onSelfieUploaded(filePath: String) {
        Log.i(TAG, "filePath $filePath")
        Log.i(TAG, "lat ${Global.user_default_latitude}")
        Log.i(TAG, "long ${Global.user_default_longitude}")

        Log.i(TAG, "outOffRange ${Global.outOffRangeReason}")
        Log.i(TAG, "lateReason ${Global.lateReason}")

        Log.i(TAG, "deviceBrand $deviceBrand")
        Log.i(TAG, "deviceId $deviceId")
        Log.i(TAG, "deviceModel $deviceModel")


//http://staff.teachdaily.in//ElixirApi/Staff/PunchIn
        var url = ""
        var jsonObject = JSONObject()
        if (Global.punchStatus == "In") {
            url = "Staff/PunchIn"
            try {
                jsonObject.put("SCHOOL_ID", schoolId)
                jsonObject.put("ADMIN_ID", adminId)
                jsonObject.put("PUNCH_IN_LATTITTUDE", Global.user_default_latitude)
                jsonObject.put("PUNCH_IN_LONGITTUDE", Global.user_default_longitude)
                jsonObject.put("PUNCH_IN_SELFIE", filePath)
                jsonObject.put("PUNCH_IN_OUTRANGE_REASON", Global.outOffRangeReason)
                jsonObject.put("PUNCH_IN_LATE_REASON", Global.lateReason)
                jsonObject.put("PUNCH_IN_DBRAND", deviceBrand)
                jsonObject.put("PUNCH_IN_DID", deviceId)
                jsonObject.put("PUNCH_IN_DMODEL", deviceModel)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Log.i(TAG, "jsonObject $jsonObject")

        } else if (Global.punchStatus == "Out") {
            url = "Staff/PunchOut"
            try {
                jsonObject.put("SCHOOL_ID", schoolId)
                jsonObject.put("ADMIN_ID", adminId)
                jsonObject.put("PUNCH_OUT_LATTITTUDE", Global.user_default_latitude)
                jsonObject.put("PUNCH_OUT_LONGITTUDE", Global.user_default_longitude)
                jsonObject.put("PUNCH_OUT_SELFIE", filePath)
                jsonObject.put("PUNCH_OUT_OUTRANGE_REASON", Global.outOffRangeReason)
                jsonObject.put("PUNCH_OUT_LATE_REASON", Global.lateReason)
                jsonObject.put("PUNCH_OUT_DBRAND", deviceBrand)
                jsonObject.put("PUNCH_OUT_DID", deviceId)
                jsonObject.put("PUNCH_OUT_DMODEL", deviceModel)
                jsonObject.put("PUNCHING_ID", Global.punchingId)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Log.i(TAG, "jsonObject $jsonObject")

        }

        var accademicRe =
            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        staffPunchViewModel.getCommonPostFun(url, accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG, "resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
//                                    chapterListener.onCreateClick("Chapter Created Successfully")
//                                    cancelFrg()
                                    Log.i(TAG, "SUCCESS ${Global.punchStatus}")
                                    //    getPunchStaffAttendance()

                                    var text = ""
                                    if (Global.punchStatus == "In") {
                                        text = "Punch In your session"
                                    } else if (Global.punchStatus == "Out") {
                                        text = "Punch Out your session"
                                    }

                                    var fragmentManager = requireActivity().supportFragmentManager
                                    var fragmentTransaction = fragmentManager.beginTransaction()
                                    fragmentTransaction.replace(
                                        R.id.nav_staff_host_fragment,
                                        StaffAttendanceSuccessFragment(text)
                                    )
//            .addToBackStack("home").commit()
                                        .commit()

//                                    if(Global.punchStatus == "In"){
//                                        running = false
//                                        startCountDownTimer("PunchOut")
//                                        punchInButton?.visibility =  View.GONE
//                                        punchOutButton?.visibility =  View.VISIBLE
//                                    }else if(Global.punchStatus == "Out"){
//                                      //  startCountDownTimer()
//                                        punchInButton?.visibility =  View.VISIBLE
//                                        punchOutButton?.visibility =  View.GONE
//
//                                        binding.progressBarCircle.progress = 0
//                                        binding.progressBarDashed.progress = 0
//                                        binding.textViewTimer.text = "00 : 00 : 00"
//                                        running = true
//                                        startCountDownTimer("PunchIn")
//
//                                    }
                                }
                                Utils.resultFun(response) == "FAILED INSERT" -> {
                                    Utils.getSnackBar4K(
                                        requireActivity(),
                                        Global.errorMessage,
                                        binding.constraintLayoutContent
                                    )
                                }
                                Utils.resultFun(response) == "FAILED UPDATE" -> {
                                    Utils.getSnackBar4K(
                                        requireActivity(),
                                        Global.errorMessage,
                                        binding.constraintLayoutContent
                                    )
                                }
                                Utils.resultFun(response) == "FAILED DUPLICATE" -> {
                                    Utils.getSnackBar4K(
                                        requireActivity(),
                                        Global.errorMessage,
                                        binding.constraintLayoutContent
                                    )
                                }
                                Utils.resultFun(response) == "FAILED DATABASE" -> {
                                    Utils.getSnackBar4K(
                                        requireActivity(),
                                        Global.errorMessage,
                                        binding.constraintLayoutContent
                                    )
                                }
                            }


                            Global.successMessage = ""
                            Global.errorMessage = ""
                            Global.punchStatus = ""
                            Global.punchingId = 0
                            Global.user_default_latitude = 0.0
                            Global.user_default_longitude = 0.0
                            Global.outOffRangeReason = ""
                            Global.lateReason = ""
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(
                                requireActivity(),
                                "Please try again after sometime",
                                binding.constraintLayoutContent
                            )
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG, "loading")
                        }
                    }
                }
            })


        //deviceBrand
        //deviceId =
        //deviceModel


    }

    override fun onErrorMessageClicker(message: String) {
        Log.i(TAG, "message $message")
    }


//    // If everything is alright then
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String?>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getLastLocation()
//            }
//        }
//    }


    private fun progressStart() {
        val dialog1 = ProgressBarDialog()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.isCancelable = false
        dialog1.show(transaction, ProgressBarDialog.TAG)
    }

    fun progressStop() {
        val fragment: ProgressBarDialog? =
            requireActivity().supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
        if (fragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment)
                .commitAllowingStateLoss()
        }
    }

}


//old code in punchinMethod(){
//
//
//        if (isLocationEnabledBool()) {
//            // code if permission granted already
//            // else request permission
//          //  Log.i(TAG,"getPermission")
//           // Log.i(TAG,"LATTITUDE $WORK_PLACE_LATTITUDE")
//          //  Log.i(TAG,"LONGITUDE $WORK_PLACE_LONGITUDE")
//
//            try{
//
//                val locationA = Location("Company Location")
//                locationA.latitude = WORK_PLACE_LATTITUDE!!
//                locationA.longitude = WORK_PLACE_LONGITUDE!!
//                val locationB = Location("Current Location")
//                locationB.latitude = Global.user_default_latitude
//                locationB.longitude = Global.user_default_longitude
//                var distance = locationA.distanceTo(locationB)
//
//                Log.i(TAG,"latitude ${Global.user_default_latitude}")
//                Log.i(TAG,"longitude ${Global.user_default_longitude}")
//
//                //    textViewDistance?.text = "Distance : ${distance/1000} kms"
//                textViewDistance?.text = "Distance : ${DecimalFormat("##.##").format(distance/1000)} kms"
//
//
//                var start: Array<String> = workStartTime.split("T".toRegex()).toTypedArray()
//                val wrStartTime: Long = Utils.longConversionTime(start[1])
//
//                var now: Array<String> = timeNow.split("T".toRegex()).toTypedArray()
//                val nowTime: Long = Utils.longConversionTime(now[1])
//
//
////                Log.i(TAG,"start ${Utils.formattedTime(wrStartTime)}")
////                Log.i(TAG,"nowTime ${Utils.formattedTime(nowTime)}")
//                Log.i(TAG,"start $wrStartTime")
//                Log.i(TAG,"nowTime $nowTime")
//
//
////                if(wrStartTime >= nowTime && distance <= 100){
////                    Log.i(TAG,"No reason for early coming")
////                }else if(wrStartTime < nowTime && distance <= 100){
////                    Log.i(TAG,"Late reason only")
////                } else if(wrStartTime < nowTime && distance > 100){
////                    Log.i(TAG,"Late reason and out of range reason")
////                }
//
//
//
//
//
//                if(wrStartTime >= nowTime && distance <= 100){
//
//                    Log.i(TAG,"No reason for early coming")
//
////                    onSelfieUploaded(commentUplodedFile)
//                    try{
//                        val dialog = Dialog(requireActivity())
//                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                        dialog.setCancelable(false)
//                        dialog.setContentView(R.layout.dialog_staff_attendance)
//                        val lp = WindowManager.LayoutParams()
//                        lp.copyFrom(dialog.window!!.attributes)
//                        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//                        lp.gravity = Gravity.CENTER
//                        dialog.window!!.attributes = lp
//
//                        var editOutOffRange = dialog.findViewById(R.id.editOutOffRange) as TextInputEditText
//                        var editTextLateReason = dialog.findViewById(R.id.editTextLateReason) as TextInputEditText
//                        var editTextEarlyReason = dialog.findViewById(R.id.editTextEarlyReason) as TextInputEditText
//                        var textInputOutOffRange = dialog.findViewById(R.id.textInputOutOffRange) as TextInputEditText
//                        textInputOutOffRange.visibility = View.GONE
//                        editTextLateReason.visibility = View.VISIBLE
//                        editTextEarlyReason.visibility = View.GONE
//
//                        mapFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//                        if(!requestPermissions(requireActivity())) {
//                            if (mapFragment != null) {
//                                mapFragment!!.getMapAsync(this)
//                            }
//                        }
//                        if (!Places.isInitialized()) {
//                            Places.initialize(requireActivity(), resources.getString(R.string.google_maps_key));
//                        }
//
//                        textViewDistance = dialog.findViewById(R.id.textViewDistance) as TextView
//                        textViewRange = dialog.findViewById(R.id.textViewRange) as TextView
//
//                        val imageViewClose = dialog.findViewById(R.id.imageViewClose) as ImageView
//                        imageViewClose.setOnClickListener {
//                            if (mapFragment != null)
//                                requireActivity().supportFragmentManager.beginTransaction().remove(mapFragment!!).commit();
//
//                            dialog.dismiss()
//                        }
//
//                        val punchInButton = dialog.findViewById(R.id.punchInButton) as AppCompatButton
//                        punchInButton.setOnClickListener {
//
//                            if(staffPunchViewModel.validateField(editOutOffRange,
//                                    "Give Out Off Range Reason",requireActivity(),binding.constraintLayoutContent)
//                                &&staffPunchViewModel.validateField(editTextLateReason,
//                                    "Give Late Reason",requireActivity(),binding.constraintLayoutContent)){
//
//                                Global.outOffRangeReason = editOutOffRange.text.toString()
//                                Global.lateReason = editTextLateReason.text.toString()
//
//                                if (mapFragment != null)
//                                    requireActivity().supportFragmentManager.beginTransaction().remove(mapFragment!!).commit();
//
//
//                                onSelfieUploaded(commentUplodedFile)
//                                dialog.dismiss()
//
//                            }
//                        }
//
//                        dialog.show()
//
//                    }catch(e : IllegalArgumentException){
//                        Log.i(TAG,"IllegalArgumentException $e")
//                        // Toast.makeText(requireActivity(), "Try again", Toast.LENGTH_SHORT).show()
//                        Utils.getSnackBar4K(requireActivity(),"Try again",binding.constraintLayoutContent)
//                    }
//
//
//
//                }
//                else if(wrStartTime < nowTime && distance <= 100){
//
//                    Log.i(TAG,"Late reason only")
//
////                    val dialog = Dialog(requireActivity())
////                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
////                    dialog.setCancelable(false)
////                    dialog.setContentView(R.layout.dialog_staff_late_attendance)
////                    val lp = WindowManager.LayoutParams()
////                    lp.copyFrom(dialog.window!!.attributes)
////                    lp.width = WindowManager.LayoutParams.MATCH_PARENT
////                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT
////                    lp.gravity = Gravity.CENTER
////                    dialog.window!!.attributes = lp
////
////                    var editTextLateReason = dialog.findViewById(R.id.editTextLateReason) as TextInputEditText
////                    var editTextEarlyReason = dialog.findViewById(R.id.editTextEarlyReason) as TextInputEditText
////                    editTextLateReason.visibility = View.VISIBLE
////                    editTextEarlyReason.visibility = View.GONE
////
////                    val punchInButton = dialog.findViewById(R.id.punchInButton) as AppCompatButton
////                    punchInButton.setOnClickListener {
////
////                        if(staffPunchViewModel.validateField(editTextLateReason,
////                                "Give Late Reason",requireActivity(),binding.constraintLayoutContent)){
////
////                            Global.lateReason = editTextLateReason.text.toString()
////
////                            onSelfieUploaded(commentUplodedFile)
////                            dialog.dismiss()
////
////                        }
////                    }
////                    dialog.show()
//
//
//                    try{
//                        val dialog = Dialog(requireActivity())
//                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                        dialog.setCancelable(false)
//                        dialog.setContentView(R.layout.dialog_staff_attendance)
//                        val lp = WindowManager.LayoutParams()
//                        lp.copyFrom(dialog.window!!.attributes)
//                        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//                        lp.gravity = Gravity.CENTER
//                        dialog.window!!.attributes = lp
//
//                        var editOutOffRange = dialog.findViewById(R.id.editOutOffRange) as TextInputEditText
//                        var editTextLateReason = dialog.findViewById(R.id.editTextLateReason) as TextInputEditText
//                        var editTextEarlyReason = dialog.findViewById(R.id.editTextEarlyReason) as TextInputEditText
//                        var textInputOutOffRange = dialog.findViewById(R.id.textInputOutOffRange) as TextInputEditText
//                        textInputOutOffRange.visibility = View.GONE
//                        editTextLateReason.visibility = View.VISIBLE
//                        editTextEarlyReason.visibility = View.GONE
//
//                        mapFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//                        if(!requestPermissions(requireActivity())) {
//                            if (mapFragment != null) {
//                                mapFragment!!.getMapAsync(this)
//                            }
//                        }
//                        if (!Places.isInitialized()) {
//                            Places.initialize(requireActivity(), resources.getString(R.string.google_maps_key));
//                        }
//
//                        textViewDistance = dialog.findViewById(R.id.textViewDistance) as TextView
//                        textViewRange = dialog.findViewById(R.id.textViewRange) as TextView
//
//                        val imageViewClose = dialog.findViewById(R.id.imageViewClose) as ImageView
//                        imageViewClose.setOnClickListener {
//                            if (mapFragment != null)
//                                requireActivity().supportFragmentManager.beginTransaction().remove(mapFragment!!).commit();
//
//                            dialog.dismiss()
//                        }
//
//                        val punchInButton = dialog.findViewById(R.id.punchInButton) as AppCompatButton
//                        punchInButton.setOnClickListener {
//
//                            if(staffPunchViewModel.validateField(editOutOffRange,
//                                    "Give Out Off Range Reason",requireActivity(),binding.constraintLayoutContent)
//                                &&staffPunchViewModel.validateField(editTextLateReason,
//                                    "Give Late Reason",requireActivity(),binding.constraintLayoutContent)){
//
//                                Global.outOffRangeReason = editOutOffRange.text.toString()
//                                Global.lateReason = editTextLateReason.text.toString()
//
//                                if (mapFragment != null)
//                                    requireActivity().supportFragmentManager.beginTransaction().remove(mapFragment!!).commit();
//
//
//                                onSelfieUploaded(commentUplodedFile)
//                                dialog.dismiss()
//
//                            }
//                        }
//
//                        dialog.show()
//
//                    }catch(e : IllegalArgumentException){
//                        Log.i(TAG,"IllegalArgumentException $e")
//                        // Toast.makeText(requireActivity(), "Try again", Toast.LENGTH_SHORT).show()
//                        Utils.getSnackBar4K(requireActivity(),"Try again",binding.constraintLayoutContent)
//                    }
//
//                }
//                else if(wrStartTime < nowTime && distance > 100){
//
//                    Log.i(TAG,"Late reason and out of range reason")
//
//                    try{
//                        val dialog = Dialog(requireActivity())
//                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                        dialog.setCancelable(false)
//                        dialog.setContentView(R.layout.dialog_staff_attendance)
//                        val lp = WindowManager.LayoutParams()
//                        lp.copyFrom(dialog.window!!.attributes)
//                        lp.width = WindowManager.LayoutParams.MATCH_PARENT
//                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
//                        lp.gravity = Gravity.CENTER
//                        dialog.window!!.attributes = lp
//
//                        var editOutOffRange = dialog.findViewById(R.id.editOutOffRange) as TextInputEditText
//                        var editTextLateReason = dialog.findViewById(R.id.editTextLateReason) as TextInputEditText
//                        var editTextEarlyReason = dialog.findViewById(R.id.editTextEarlyReason) as TextInputEditText
//                        var textInputOutOffRange = dialog.findViewById(R.id.textInputOutOffRange) as TextInputLayout
//                        textInputOutOffRange.visibility = View.VISIBLE
//                        editTextLateReason.visibility = View.VISIBLE
//                        editTextEarlyReason.visibility = View.GONE
//
//                        mapFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
//                        if(!requestPermissions(requireActivity())) {
//                            if (mapFragment != null) {
//                                mapFragment!!.getMapAsync(this)
//                            }
//                        }
//                        if (!Places.isInitialized()) {
//                            Places.initialize(requireActivity(), resources.getString(R.string.google_maps_key));
//                        }
//
//                        textViewDistance = dialog.findViewById(R.id.textViewDistance) as TextView
//                        textViewRange = dialog.findViewById(R.id.textViewRange) as TextView
//
//                        val imageViewClose = dialog.findViewById(R.id.imageViewClose) as ImageView
//                        imageViewClose.setOnClickListener {
//                            if (mapFragment != null)
//                                requireActivity().supportFragmentManager.beginTransaction().remove(mapFragment!!).commit();
//
//                            dialog.dismiss()
//                        }
//
//                        val punchInButton = dialog.findViewById(R.id.punchInButton) as AppCompatButton
//                        punchInButton.setOnClickListener {
//
//                            if(staffPunchViewModel.validateField(editOutOffRange,
//                                    "Give Out Off Range Reason",requireActivity(),binding.constraintLayoutContent)
//                                &&staffPunchViewModel.validateField(editTextLateReason,
//                                    "Give Late Reason",requireActivity(),binding.constraintLayoutContent)){
//
//                                Global.outOffRangeReason = editOutOffRange.text.toString()
//                                Global.lateReason = editTextLateReason.text.toString()
//
//                                if (mapFragment != null)
//                                    requireActivity().supportFragmentManager.beginTransaction().remove(mapFragment!!).commit();
//
////                                val intent = Intent(requireActivity(), SelfieActivity()::class.java)
////                                startActivity(intent)
//                                var fragmentManager = requireActivity().supportFragmentManager
//                                var fragmentTransaction = fragmentManager.beginTransaction()
//                                fragmentTransaction.replace(R.id.nav_staff_host_fragment, SelfieFragment(this))
////            .addToBackStack("home").commit()
//                                    .commit()
//                                dialog.dismiss()
//
//                            }
//                        }
//
//                        dialog.show()
//
//                    }catch(e : IllegalArgumentException){
//                        Log.i(TAG,"IllegalArgumentException $e")
//                       // Toast.makeText(requireActivity(), "Try again", Toast.LENGTH_SHORT).show()
//                        Utils.getSnackBar4K(requireActivity(),"Try again",binding.constraintLayoutContent)
//                    }
//
//
//                }
//
//
//
//
//
//
////                if ((Utils.checkOnlyTime(Utils.formattedTimeHHmmss(wrStartTime), Utils.formattedTimeHHmmss(nowTime), "Before") ||
////                            Utils.checkOnlyTime(Utils.formattedTimeHHmmss(wrStartTime), Utils.formattedTimeHHmmss(nowTime), "Equal"))
////                    && distance <= 100) {
//
//
//
////                    onSelfieUploaded(commentUplodedFile)
//
////                }else if((Utils.checkOnlyTime(Utils.formattedTimeHHmmss(wrStartTime), Utils.formattedTimeHHmmss(nowTime), "After")&& distance <= 100)){
//
//
//
////                }else if(Utils.checkOnlyTime(Utils.formattedTimeHHmmss(wrStartTime), Utils.formattedTimeHHmmss(nowTime), "After") && distance > 100){
////
////
////                }
//
//
//
//
//            }catch(e : NullPointerException){
//                Log.i(TAG,"e $e")
//           //     Toast.makeText(requireActivity(), "Try again", Toast.LENGTH_SHORT).show()
//                Utils.getSnackBar4K(requireActivity(),"Try again",binding.constraintLayoutContent)
//            }
//
//        } else {
//            enableLocationSettings()
//        }
//