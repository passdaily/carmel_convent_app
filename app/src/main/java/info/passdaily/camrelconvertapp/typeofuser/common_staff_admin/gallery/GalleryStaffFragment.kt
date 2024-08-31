package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.gallery

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityGalleryStaffBinding
import info.passdaily.camrelconvertapp.databinding.FragmentGalleryStaffBinding
import info.passdaily.camrelconvertapp.model.CustomImageModel
import info.passdaily.camrelconvertapp.model.DescriptiveExamStaffResultModel
import info.passdaily.camrelconvertapp.model.UnAttendedListModel
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener

class GalleryStaffFragment : Fragment(),GalleryClicker {

    var TAG = "GalleryViewActivity"
   //private lateinit var binding: ActivityGalleryStaffBinding
    private var _binding: ActivityGalleryStaffBinding? = null
    private val binding get() = _binding!!

    private lateinit var galleryStaffViewModel: GalleryStaffViewModel

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var toolbar: Toolbar? = null
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null

    var addVideoFab: FloatingActionButton? = null
    var addImageFab: FloatingActionButton? = null
    var mAddFab: ExtendedFloatingActionButton? = null
    var addImageTextView : TextView? = null
    var addVideoTextView : TextView? = null

    // to check whether sub FABs are visible or not
    var isAllFabsVisible: Boolean? = null


    var onlineExamAttendees = ArrayList<DescriptiveExamStaffResultModel.OnlineExamAttendee>()
    var onlineExamDetails = ArrayList<DescriptiveExamStaffResultModel.OnlineExamDetail>()

    var unAttendedListModel = ArrayList<UnAttendedListModel.UnAttended>()

    var constraintLayoutContent : ConstraintLayout? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var toolBarClickListener : ToolBarClickListener? = null
    var mContext : Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(mContext ==null){
            mContext = context.applicationContext
        }
        try {
            toolBarClickListener = context as ToolBarClickListener
        }catch(e : Exception){
            Log.i(TAG,"Exception $e")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.screenState = "landingpage"
        toolBarClickListener?.setToolbarName("Gallery")

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        // Inflate the layout for this fragment


        galleryStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[GalleryStaffViewModel::class.java]


        // Inflate the layout for this fragment
        _binding = ActivityGalleryStaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraintLayoutContent = binding.constraintLayoutContent
        shimmerViewContainer= binding.shimmerViewContainer
        constraintLayoutContent?.visibility = View.INVISIBLE
        shimmerViewContainer?.visibility = View.VISIBLE

        viewPager = binding.pager
        tabLayout = binding.tabLayout


        getTabResult()
    }

    private fun getTabResult() {

        val adapter = Global.MyPagerAdapter(requireActivity().supportFragmentManager)
        adapter.addFragment(
            ListingImageFragment(this),
            resources.getString(R.string.photo)
        )
        adapter.addFragment(
            ListingVideoFragment(this),
            resources.getString(R.string.video)
        )
        // adapter.addFragment(new DMKOfficial(), "Tweets");
        //  constraintLayoutContent?.visibility = View.VISIBLE
        //  shimmerViewContainer?.visibility = View.GONE
        viewPager?.adapter = adapter
        viewPager?.currentItem = 0
        tabLayout?.setupWithViewPager(viewPager)

        viewPager?.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                tabLayout
            )
        )
        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // onShowProgressClicker()
                viewPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }
    override fun onShowProgressClicker() {
        Log.i(TAG,"onShowProgressClicker");
        constraintLayoutContent?.visibility = View.INVISIBLE
        shimmerViewContainer?.visibility = View.VISIBLE
    }

    override fun onHideProgressClicker() {
        Log.i(TAG,"onHideProgressClicker");
        constraintLayoutContent?.visibility = View.VISIBLE
        shimmerViewContainer?.visibility = View.GONE
    }


    override fun onStop() {
        super.onStop()
        Global.albumImageList =  ArrayList<CustomImageModel>()
    }


}



interface GalleryClicker{

    fun onShowProgressClicker();
    fun onHideProgressClicker();

}