package info.passdaily.camrelconvertapp.typeofuser.parent.map

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityMapBinding
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import java.io.IOException
import java.util.*


@Suppress("DEPRECATION")
class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener,MapClickListener {

    var TAG = "MapActivity"

    private lateinit var binding: ActivityMapBinding
    private lateinit var trackViewModel: TrackViewModel

    var VEHICLE_NAME : String?= null
    var TERMINAL_ID  : String?= null
    var SIM_NUMBER  : String?= null
    var VEHICLE_ID = 0
    var origin = LatLng(11.0642679, 76.970206)

    var line: Polyline? = null
//    var lat = arrayOf(
//        "11.0642679",
//        "11.0642679",
//        "11.0644799",
//        "11.0645282",
//        "11.0645449",
//        "11.0645479",
//        "11.0645509",
//        "11.0644996",
//        "11.0644996",
//        "11.0639836"
//    )
//    var lng = arrayOf(
//        "76.970206",
//        "76.970206",
//        "76.970198",
//        "76.9702753",
//        "76.970639",
//        "76.9711886",
//        "76.9717673",
//        "76.9719602",
//        "76.9719602",
//        "76.9718724"
//    )

    var markerOptions: MarkerOptions? = null
  //  var markred1 = 0
    var mapMarker: Marker? = null
    var mapFragment: SupportMapFragment? = null
    var googleMap: GoogleMap? = null
    var mLocationRequest: LocationRequest? = null

    var lat = 0.0
    var lng = 0.0

    var date = ""
    var powerAcc = 0
    var speed = ""

    var thr = false

    var isRunning = false

    var constraintFirstLayout: ConstraintLayout? = null

    var mGoogleApiClient: GoogleApiClient? = null

    var mLastLocation: Location? = null
    var mCurrLocationMarker: Marker? = null
    var longitude = 0.0
    var latitude = 0.0

    var toolbar: Toolbar? = null

    var constraintTrackStatus : ConstraintLayout? = null
    var textViewTitle : TextView? = null
    var textViewTime : TextView? = null
    var textViewIdle : TextView? = null
    var textViewSpeed: TextView? = null
    var textViewDate: TextView? = null
    var textSpeed: TextView? = null
    var textIdle: TextView? = null

    var imageViewPower : ImageView? = null
    var imageViewSignal : ImageView? = null
    var imageViewBattery : ImageView? = null

    var slide_down: Animation? = null
    var slide_up:Animation? = null

    var secondPassR = false
    private val REQUEST_CODE_MULTIPLE_PERMISSIONS = 57

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_map)
        val extras = intent.extras
        if (extras != null) {
            VEHICLE_NAME = extras.getString("VEHICLE_NAME")
            TERMINAL_ID = extras.getString("TERMINAL_ID")
            SIM_NUMBER = extras.getString("SIM_NUMBER")
            VEHICLE_ID = extras.getInt("VEHICLE_ID")
        }

        trackViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[TrackViewModel::class.java]
        //Bundle[{VEHICLE_ID=1, SIM_NUMBER=7025016669, TERMINAL_ID=352503090699858, VEHICLE_NAME=Bus No : 1}]


        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        constraintFirstLayout = binding.constraintFirstLayout

        toolbar = binding.toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = VEHICLE_NAME
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //   toolbar.setTitle("About-Us");
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }

        constraintTrackStatus = binding.constraintTrackStatus
        textViewDate = binding.textViewDate
        textViewTime = binding.textViewTime
        textViewTitle = binding.textViewTitle
        textViewIdle = binding.textViewIdle
        textViewSpeed = binding.textViewSpeed
        textSpeed = binding.textSpeed
        textIdle = binding.textIdle
        imageViewPower  = binding.imageViewPower
        imageViewSignal  = binding.imageViewSignal
        imageViewBattery  = binding.imageViewBattery

        textViewDate?.setBackgroundColor(resources.getColor(R.color.gray_100))
        textViewTime?.setBackgroundColor(resources.getColor(R.color.gray_100))
        textViewTitle?.setBackgroundColor(resources.getColor(R.color.gray_100))
        textViewIdle?.setBackgroundColor(resources.getColor(R.color.gray_100))
        textViewSpeed?.setBackgroundColor(resources.getColor(R.color.gray_100))
        textSpeed?.setBackgroundColor(resources.getColor(R.color.gray_100))
        textIdle?.setBackgroundColor(resources.getColor(R.color.gray_100))



        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if(requestPermissions(this)) {
            if (mapFragment != null) {
                mapFragment!!.getMapAsync(this)
            }

            handler.postDelayed(runnable, 5000)
        }


        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, resources.getString(R.string.google_maps_api_key));
        }
//        locationPermissionRequest.launch(arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION))




        slide_down = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.slide_down
        )

        slide_up = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.slide_up
        )

        Utils.setStatusBarColor(this)
    }

    private fun initMethod() {
//        lat = lat.replace("[a-zA-Z]".toRegex(), "")
//        lng = lng.replace("[a-zA-Z]".toRegex(), "")

        trackViewModel.getGpsLocation(VEHICLE_ID,TERMINAL_ID!!,SIM_NUMBER!!)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            val  gpsLocation = response.gpsLocation
                            if(response.gpsLocation.isNotEmpty()) {
                                for(i in gpsLocation.indices){
                                    date = gpsLocation[i].lOCATIONDATE
                                    lat = gpsLocation[i].lATITUDE.replace("[a-zA-Z]".toRegex(), "").toDouble()
                                    lng = gpsLocation[i].lONGITUDE.replace("[a-zA-Z]".toRegex(), "").toDouble()
                                    speed = gpsLocation[i].sPEED
                                    powerAcc = gpsLocation[i].aCC2.toInt()
                                }
                                Log.i(TAG,"gpsLocation $gpsLocation")

                                onTrackMapClick(date,speed,powerAcc)
                            }else{
                                Utils.getSnackBar4K(this,"There is no signal from vehicle",constraintFirstLayout!!)
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"error")

                        }
                        Status.LOADING -> {
                            Log.i(TAG,"LOADING")
                        }
                    }
                }
            })
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 17f))

        /////user location code
        googleMap!!.uiSettings.isZoomControlsEnabled = true
        googleMap!!.uiSettings.isZoomGesturesEnabled = true
        googleMap!!.uiSettings.isCompassEnabled = true

        googleMap?.setOnMapClickListener(OnMapClickListener {
            constraintTrackStatus?.visibility = View.GONE
            constraintTrackStatus?.startAnimation(slide_down)
        })

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
                googleMap!!.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            googleMap!!.isMyLocationEnabled = true
        }

   //     addMarker()
        //drawPolylines();
        //Start

    }

    // Init
    private val handler = Handler()
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (!thr) {
                initMethod()
                handler.postDelayed(this, 5000)
            }
        }
    }

    @Synchronized
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient!!,
                mLocationRequest!!, this
            )
        }
    }

    override fun onConnectionSuspended(pos: Int) {
        Log.i(TAG,"onConnectionSuspended $pos")
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.i(TAG,"onConnectionFailed $connectionResult")
    }

    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }
//Showing Current Location Marker on Map
        //Showing Current Location Marker on Map
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val provider = locationManager.getBestProvider(Criteria(), true)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val locations = locationManager.getLastKnownLocation(provider!!)
        val providerList = locationManager.allProviders
        if (null != locations && providerList.size > 0) {
            longitude = locations.longitude
            latitude = locations.latitude
            val geocoder = Geocoder(
                applicationContext,
                Locale.getDefault()
            )
            try {
                val listAddresses = geocoder.getFromLocation(
                    latitude,
                    longitude, 1
                )
                if (null != listAddresses && listAddresses.size > 0) {
                    val state = listAddresses[0].adminArea
                    val country = listAddresses[0].countryName
                    val subLocality = listAddresses[0].subLocality
                    markerOptions.title(
                        "" + latLng + "," + subLocality + "," + state
                                + "," + country
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        ///////////////////////////current location in orange
        mCurrLocationMarker = googleMap!!.addMarker(
            markerOptions
                .title("You Are Here")
        )
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap!!.animateCamera(CameraUpdateFactory.zoomTo(17f))
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient!!,
                this
            )
        }
    }

//    private val locationPermissionRequest = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            when {
//                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
//                    // Precise location access granted.
//                }
//                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
//                    // Only approximate location access granted.
//                } else -> {
//                // No location access granted.
//            }
//
//            }
//        }
//    }
    //Location Base movement in Map
    private fun addMarker() {


//        val options = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)
        val la = lat
        val lg = lng
        val currentLatLng = LatLng(la, lg)
//            val point: LatLng = currentLatLng
//            options.add(point)
//        line = googleMap!!.addPolyline(options)

       // markred1 = 1
        if (mapMarker == null) {
            markerOptions = MarkerOptions()
            markerOptions!!.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
            markerOptions!!.position(currentLatLng)
            mapMarker = googleMap!!.addMarker(markerOptions!!)
            ///  mapMarker.setTitle(dt);
            googleMap!!.setOnMarkerClickListener { //    Toast.makeText(MapsActivity1.this,"test click", Toast.LENGTH_SHORT).show();
                // Start animation
                constraintTrackStatus?.startAnimation(slide_up)
                constraintTrackStatus?.visibility = View.VISIBLE
                false
            }
        }
        else {
            val temp = Location(LocationManager.GPS_PROVIDER)
            temp.latitude = la
            temp.longitude = lg
            moveVechile(mapMarker!!, temp)
        }

        //move map camera
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
        // mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }

    fun moveVechile(myMarker: Marker, finalPosition: Location) {
        val startPosition = myMarker.position
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val interpolator: Interpolator = AccelerateDecelerateInterpolator()
        val durationInMs = 3000f
        val hideMarker = false
        handler.post(object : Runnable {
            var elapsed: Long = 0
            var t = 0f
            var v = 0f
            override fun run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start
                t = elapsed / durationInMs
                v = interpolator.getInterpolation(t)
                val currentPosition = LatLng(
                    startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                    startPosition.longitude * (1 - t) + finalPosition.longitude * t
                )
                myMarker.position = currentPosition
                // myMarker.setRotation(finalPosition.getBearing());

                // Repeat till progress is completeelse
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                    // handler.postDelayed(this, 100);
                } else {
                    myMarker.isVisible = !hideMarker
                }
            }
        })
    }


    override fun onPause() {
        super.onPause()
        isRunning = false
    }

    override fun onResume() {
        super.onResume()
        isRunning = true
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        thr = true
    }

    @SuppressLint("SetTextI18n")
    override fun onTrackMapClick(date: String, speed: String, powerAcc: Int) {
        textViewDate?.setBackgroundColor(resources.getColor(R.color.white))
        textViewTime?.setBackgroundColor(resources.getColor(R.color.white))
        textViewTitle?.setBackgroundColor(resources.getColor(R.color.white))
        textViewIdle?.setBackgroundColor(resources.getColor(R.color.white))
        textViewSpeed?.setBackgroundColor(resources.getColor(R.color.white))
        textSpeed?.setBackgroundColor(resources.getColor(R.color.white))
        textIdle?.setBackgroundColor(resources.getColor(R.color.white))

        textSpeed?.text = "Speed"
        textIdle?.text = "Idle"
        val date1: Array<String> = date.split("T".toRegex()).toTypedArray()
        val ddddd: Long = Utils.longconversion(date1[0] + " " + date1[1])
        textViewTime?.text =Utils.formattedTime(ddddd)
        textViewDate?.text =Utils.dateformat(date1[0])
        textViewTitle?.text =VEHICLE_NAME
        textViewSpeed?.text = "$speed Km/hr"
        textViewIdle?.text = "00:00 hr"

        when (powerAcc) {
            0 -> {
                imageViewPower?.setImageResource(R.drawable.ic_power_settings_off_24dp)
                imageViewSignal?.setImageResource(R.drawable.ic_signal_cellular_4_bar_off_24dp)
                imageViewBattery?.setImageResource(R.drawable.ic_battery_charging_full_off_24dp)
            }
            1 -> {
                imageViewPower?.setImageResource(R.drawable.ic_power_settings_green_24dp)
                imageViewSignal?.setImageResource(R.drawable.ic_signal_cellular_4_bar_black_24dp)
                imageViewBattery?.setImageResource(R.drawable.ic_battery_charging_full_black_24dp)
            }
        }
        addMarker()
//        val bottomSheet = TrackBottomFragment(VEHICLE_NAME,date,speed,powerAcc)
//        bottomSheet.isCancelable = false
//        bottomSheet.show(supportFragmentManager, "TAG")
    }


    private fun requestPermissions(activity: Activity): Boolean {
        Log.v(TAG, "requestPermissions() called")
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.R || secondPassR) {
                if (!addPermission(
                        permissionsList,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        activity
                    )
                ) {
                    reasonList.add(
                        """
                        BACKGROUND PERMISSION: Needs to be granted discover track Location!
                        """.trimIndent()
                    )
                }
            }
            if (permissionsList.size > 0) {
//                if (reasonList.size > 0) {
//                    // Need Rationale
//                    val message = StringBuilder(reasonList[0])
//                    for (i in 1 until reasonList.size) {
//                        message.append(" ").append(reasonList[i])
//                    }
//                    val builder = AlertDialog.Builder(
//                        ContextThemeWrapper(
//                            activity,
//                            R.style.Theme_AppCompat_Light
//                        )
//                    )
//                    builder.setTitle("Needs the following permissions:")
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        builder.setMessage(
//                            Html.fromHtml(
//                                message.toString(),
//                                Html.FROM_HTML_MODE_LEGACY
//                            )
//                        )
//                    } else {
//                        builder.setMessage(Html.fromHtml(message.toString()))
//                    }
//                    builder.setPositiveButton(android.R.string.ok, null)
//                    builder.setOnDismissListener { dialog: DialogInterface? ->
//                        Log.v(TAG, "Requesting permissions")
//                        activity.requestPermissions(
//                            permissionsList.toTypedArray(),  // newer Java recommended
//                            REQUEST_CODE_MULTIPLE_PERMISSIONS
//                        )
//                    }
//                    builder.show()
//                    return false
//                }
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
        Log.v(
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
        Log.v(
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
                        Log.d(TAG, "H@H: onRequestPermissionsResult: FINE LOCATION PERMISSION")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            Log.d(TAG, "H@H: Now requesting BACKGROUND PERMISSION for version 11+")
                            secondPassR = true
                            requestPermissions(this@MapActivity)
                            return
                        }
                        entryGranted =true
                    }
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION -> if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "H@H: onRequestPermissionsResult: BACKGROUND PERMISSION")
                        entryGranted =true
                    }
                }
            }
        }
        Log.d(TAG, "Starting primary activity")
        secondPassR = false
        if(entryGranted){
            if (mapFragment != null) {
                mapFragment!!.getMapAsync(this)
            }

            handler.postDelayed(runnable, 5000)
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK", okListener)
            .create()
            .show()
    }
}

interface MapClickListener{
    fun onTrackMapClick(date: String, speed: String, powerAcc : Int)
}