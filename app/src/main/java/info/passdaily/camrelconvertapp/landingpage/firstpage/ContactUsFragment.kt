package info.passdaily.camrelconvertapp.landingpage.firstpage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.ContactUsViewModel
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
@Suppress("DEPRECATION")
class ContactUsFragment : Fragment(){

    var imageBackPress : ImageView? =null
    private lateinit var contactViewModel: ContactUsViewModel

    var mapFragment :SupportMapFragment?=null
    var LATTITUDE : Double? =null
    var LONGITUDE : Double? =null

    var mMap: GoogleMap? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        contactViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[ContactUsViewModel::class.java]
        return inflater.inflate(R.layout.fragment_contact_us, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Global.screenState = "landingpage"

        var constraintLayout = view.findViewById(R.id.constraintLayout) as ConstraintLayout

        imageBackPress = view.findViewById(R.id.imageBackPress) as ImageView
        imageBackPress?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, FirstScreenFragment()
            ).commit()
        }
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
       // contactViewModel.getContactUsItems(0)
        initView()

        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), resources.getString(R.string.google_maps_key));
        }

        if(!Utils.isOnline(requireActivity())){
            Utils.getSnackBar4K(requireActivity(),requireActivity().resources.getString(R.string.no_internet),constraintLayout)
        }


    }

    private fun initView() {
//        contactViewModel.getContactUsObservable()
//            .observe(requireActivity(), {
//                if (it != null) {
//                    //  contactUs = it.ContactusList
//                    for(i in it.ContactusList.indices){
//                        LATTITUDE = it.ContactusList[i].LATTITUDE.toDouble()
//                        LONGITUDE = it.ContactusList[i].LONGITUDE.toDouble()
//                    }
//                    mapFragment?.getMapAsync(callback)
//                }
//            })
        val studentId = 0
        contactViewModel.getContactUsItems(studentId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            for(contactUs in response.contactusList) {
                                LATTITUDE = contactUs.lATTITUDE.toDouble()
                                LONGITUDE = contactUs.lONGITUDE.toDouble()
                            }
                            mapFragment?.getMapAsync(callback)
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

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        val sydney = LatLng(LATTITUDE!!, LONGITUDE!!)
        googleMap.addMarker(MarkerOptions()
            .position(sydney)
            .title("Passdaily Pvt Ltd"))
        //   new_Marker();

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18f))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,17f))
    }
}