//package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.gallery
//
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.lifecycle.ViewModelProviders
//import androidx.viewpager.widget.ViewPager
//import com.facebook.shimmer.ShimmerFrameLayout
//import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import com.google.android.material.tabs.TabLayout
//import info.passdaily.camrelconvertapp.R
//import info.passdaily.camrelconvertapp.databinding.ActivityGalleryStaffBinding
//import info.passdaily.camrelconvertapp.model.CustomImageModel
//import info.passdaily.camrelconvertapp.model.DescriptiveExamStaffResultModel
//import info.passdaily.camrelconvertapp.model.UnAttendedListModel
//import info.passdaily.camrelconvertapp.services.Global
//import info.passdaily.camrelconvertapp.services.Utils
//import info.passdaily.camrelconvertapp.services.ViewModelFactory
//import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
//import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
//import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
//import kotlinx.coroutines.DelicateCoroutinesApi
//
//
//@Suppress("DEPRECATION")
//class GalleryViewActivity : AppCompatActivity(),GalleryClicker {
//
//    var TAG = "GalleryViewActivity"
//    private lateinit var binding: ActivityGalleryStaffBinding
//    private lateinit var galleryStaffViewModel: GalleryStaffViewModel
//
//    private lateinit var localDBHelper : LocalDBHelper
//    var adminId = 0
//    var toolbar: Toolbar? = null
//    var tabLayout: TabLayout? = null
//    var viewPager: ViewPager? = null
//
//    var addVideoFab: FloatingActionButton? = null
//    var addImageFab:FloatingActionButton? = null
//    var mAddFab: ExtendedFloatingActionButton? = null
//    var addImageTextView : TextView ? = null
//    var addVideoTextView : TextView? = null
//
//    // to check whether sub FABs are visible or not
//    var isAllFabsVisible: Boolean? = null
//
//
//    var onlineExamAttendees = ArrayList<DescriptiveExamStaffResultModel.OnlineExamAttendee>()
//    var onlineExamDetails = ArrayList<DescriptiveExamStaffResultModel.OnlineExamDetail>()
//
//    var unAttendedListModel = ArrayList<UnAttendedListModel.UnAttended>()
//
//    var constraintLayoutContent : ConstraintLayout? = null
//    var shimmerViewContainer: ShimmerFrameLayout? = null
//    @OptIn(DelicateCoroutinesApi::class)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        localDBHelper = LocalDBHelper(this)
//        var user = localDBHelper.viewUser()
//        adminId = user[0].ADMIN_ID
//        // Inflate the layout for this fragment
//
//
//        galleryStaffViewModel = ViewModelProviders.of(
//            this,
//            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
//        )[GalleryStaffViewModel::class.java]
//
//
//        binding = ActivityGalleryStaffBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        toolbar = binding.toolbar
//        if (toolbar != null) {
//            setSupportActionBar(toolbar)
//            supportActionBar!!.title = "Gallery"
//            // Customize the back button
//            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
//            supportActionBar!!.setDisplayShowTitleEnabled(true)
//            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
//                onBackPressed()
//            })
//        }
//
//        constraintLayoutContent = binding.constraintLayoutContent
//        shimmerViewContainer= binding.shimmerViewContainer
//        constraintLayoutContent?.visibility = View.INVISIBLE
//        shimmerViewContainer?.visibility = View.VISIBLE
//
//        viewPager = binding.pager
//        tabLayout = binding.tabLayout
//
//
////        mAddFab = binding.addFab
////        addVideoFab = binding.addVideoFab
////        addImageFab = binding.addImageFab
////        addImageTextView =binding.addImageTextView
////        addVideoTextView = binding.addVideoTextView
////
////        // Now set all the FABs and all the action name
////        // texts as GONE
////        addImageFab?.visibility = View.GONE;
////        addVideoFab?.visibility = View.GONE;
////        addImageTextView?.visibility = View.GONE;
////        addVideoTextView?.visibility = View.GONE;
////        // make the boolean variable as false, as all the
////        // action name texts and all the sub FABs are
////        // invisible
////        isAllFabsVisible = false;
////        // Set the Extended floating action button to
////        // shrinked state initially
////        mAddFab?.shrink();
////        // We will make all the FABs and action name texts
////        // visible only when Parent FAB button is clicked So
////        // we have to handle the Parent FAB button first, by
////        // using setOnClickListener you can see below
////        mAddFab!!.setOnClickListener {
////            if (!isAllFabsVisible!!) {
////                // when isAllFabsVisible becomes
////                // true make all the action name
////                // texts and FABs VISIBLE.
////                addImageFab?.show()
////                addVideoFab?.show()
////                addImageTextView?.visibility = View.VISIBLE
////                addVideoTextView?.visibility = View.VISIBLE
////                // Now extend the parent FAB, as
////                // user clicks on the shrinked
////                // parent FAB
////                mAddFab!!.extend()
////                // make the boolean variable true as
////                // we have set the sub FABs
////                // visibility to GONE
////                isAllFabsVisible = true
////            } else {
////                // when isAllFabsVisible becomes
////                // true make all the action name
////                // texts and FABs GONE.
////                addImageFab?.hide()
////                addVideoFab?.hide()
////                addImageTextView?.visibility = View.GONE
////                addVideoTextView?.visibility = View.GONE
////                // Set the FAB to shrink after user
////                // closes all the sub FABs
////                mAddFab!!.shrink()
////                // make the boolean variable false
////                // as we have set the sub FABs
////                // visibility to GONE
////                isAllFabsVisible = false
////            }
////        }
////
////        // below is the sample action to handle add person
////        // FAB. Here it shows simple Toast msg. The Toast
////        // will be shown only when they are visible and only
////        // when user clicks on them
////        // below is the sample action to handle add person
////        // FAB. Here it shows simple Toast msg. The Toast
////        // will be shown only when they are visible and only
////        // when user clicks on them
////        addImageFab?.setOnClickListener(View.OnClickListener {
////                Toast.makeText(
////                    this, "Person Added",
////                    Toast.LENGTH_SHORT
////                ).show()
////            })
////        // below is the sample action to handle add alarm
////        // FAB. Here it shows simple Toast msg The Toast
////        // will be shown only when they are visible and only
////        // when user clicks on them
////        // below is the sample action to handle add alarm
////        // FAB. Here it shows simple Toast msg The Toast
////        // will be shown only when they are visible and only
////        // when user clicks on them
////        addVideoFab?.setOnClickListener(
////            View.OnClickListener {
////                Toast.makeText(
////                    this, "Alarm Added",
////                    Toast.LENGTH_SHORT
////                ).show()
////            })
//
//
//        Utils.setStatusBarColor(this)
//
//        getTabResult()
//    }
//
//
//
//    private fun getTabResult() {
//
//        val adapter = Global.MyPagerAdapter(supportFragmentManager)
//        adapter.addFragment(
//            ListingImageFragment(this),
//            resources.getString(R.string.photo)
//        )
//        adapter.addFragment(
//            ListingVideoFragment(this),
//            resources.getString(R.string.video)
//        )
//        // adapter.addFragment(new DMKOfficial(), "Tweets");
//      //  constraintLayoutContent?.visibility = View.VISIBLE
//      //  shimmerViewContainer?.visibility = View.GONE
//        viewPager?.adapter = adapter
//        viewPager?.currentItem = 0
//        tabLayout?.setupWithViewPager(viewPager)
//
//        viewPager?.addOnPageChangeListener(
//            TabLayout.TabLayoutOnPageChangeListener(
//                tabLayout
//            )
//        )
//        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//               // onShowProgressClicker()
//                viewPager?.currentItem = tab.position
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {}
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//        })
//
//    }
//    override fun onShowProgressClicker() {
//        Log.i(TAG,"onShowProgressClicker");
//        constraintLayoutContent?.visibility = View.INVISIBLE
//        shimmerViewContainer?.visibility = View.VISIBLE
//    }
//
//    override fun onHideProgressClicker() {
//        Log.i(TAG,"onHideProgressClicker");
//        constraintLayoutContent?.visibility = View.VISIBLE
//        shimmerViewContainer?.visibility = View.GONE
//    }
//
//
//    override fun onStop() {
//        super.onStop()
//        Global.albumImageList =  ArrayList<CustomImageModel>()
//    }
//}
//
//
//interface GalleryClicker{
//
//    fun onShowProgressClicker();
//    fun onHideProgressClicker();
//
//}