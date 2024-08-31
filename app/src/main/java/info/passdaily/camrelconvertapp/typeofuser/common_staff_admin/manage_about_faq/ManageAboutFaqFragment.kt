package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_about_faq

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentQuickNotificationBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import java.util.ArrayList

@Suppress("DEPRECATION")
class ManageAboutFaqFragment : Fragment(),ManageAboutUsListener {

    var TAG = "ManageAboutFaqFragment"
    private lateinit var manageAboutFaqViewModel: ManageAboutFaqViewModel
    private var _binding: FragmentQuickNotificationBinding? = null
    private val binding get() = _binding!!
    var adminId = 0
    var schoolId = 0

    private lateinit var localDBHelper : LocalDBHelper
    var toolBarClickListener : ToolBarClickListener? = null

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var constraintLayoutContent : ConstraintLayout? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null



//    var aboutUsFaqList = ArrayList<AboutusFaqListModel.Aboutus>()
//    var faqAboutUsList = ArrayList<AboutusFaqListModel.Aboutus>()

    var fab : FloatingActionButton? = null

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.screenState = "staffhomepage"
        toolBarClickListener?.setToolbarName("Manage About Us/FAQ")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        // Inflate the layout for this fragment
        manageAboutFaqViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ManageAboutFaqViewModel::class.java]

        _binding = FragmentQuickNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        constraintLayoutContent = binding.constraintLayoutContent
        shimmerViewContainer= binding.shimmerViewContainer


        viewPager = binding.pager
        tabLayout = binding.tabLayout

        fab = binding.fab
        fab?.visibility = View.GONE
        fab?.setOnClickListener {
//            val dialog1 = CreateNotificationDialog(this)
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
//            dialog1.show(transaction, CreateNotificationDialog.TAG)
        }

        getNotificationSentDetails()

    }

    private fun getNotificationSentDetails() {
        manageAboutFaqViewModel.getAboutUsFaq(adminId,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                           // aboutUsFaqList = response.aboutusList as ArrayList<AboutusFaqListModel.Aboutus>

                            for (i in response.aboutusList.iterator()){
                                if(i.aBTFAQTYPE == 1){
                                    Global.aboutUsFaqList.add(i)
                                }else if(i.aBTFAQTYPE == 2){
                                    Global.faqAboutUsList.add(i)
                                }
                            }
                            getTabResult()

                            Log.i(TAG,"getNotificationSentDetails SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getNotificationSentDetails ERROR")
                        }
                        Status.LOADING -> {
                            Global.aboutUsFaqList = ArrayList<AboutusFaqListModel.Aboutus>()
                            Global.faqAboutUsList = ArrayList<AboutusFaqListModel.Aboutus>()
                            Log.i(TAG,"getNotificationSentDetails LOADING")
                        }
                    }
                }
            })
    }


    private fun getTabResult() {

        val adapter = Global.MyPagerAdapter(childFragmentManager)
        adapter.addFragment(
            AboutUsTabFragment(this,1), resources.getString(R.string.about_us))
        adapter.addFragment(
            AboutUsTabFragment(this,2), resources.getString(R.string.faq))

        // adapter.addFragment(new DMKOfficial(), "Tweets");
        constraintLayoutContent?.visibility = View.VISIBLE
        shimmerViewContainer?.visibility = View.GONE
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
                viewPager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


    }

    override fun onSuccessMessage(message: String) {
        Log.i(TAG,"onCreateClick")
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)
        getNotificationSentDetails()
    }

    override fun onFailedMessage(message: String) {
        Log.i(TAG,"onCreateClick")
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
    }

}

interface ManageAboutUsListener {
    fun onSuccessMessage(message: String)
    fun onFailedMessage(message: String)
}