package info.passdaily.camrelconvertapp.typeofuser.parent.fees

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FeeInitFragmentBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import info.passdaily.camrelconvertapp.typeofuser.parent.absent.ItemClickListener
import kotlinx.coroutines.*

@DelicateCoroutinesApi
@Suppress("DEPRECATION")
class FeesInitFragment : Fragment() {

    var TAG = "GalleryImageFragment"

    private var _binding: FeeInitFragmentBinding? = null
    private val binding get() = _binding!!

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var viewPager: ViewPager? = null
    var tabLayout: TabLayout? = null

    var feesDetailsModel = ArrayList<FeesDetailsModel.FeesPaidDetail>()


    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var accedemicId = 0
    var recyclerView : RecyclerView? =null

    var shimmerViewContainer : ShimmerFrameLayout? =null

    private lateinit var feesDetailViewModel: FeesDetailViewModel

    var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG, "onAttach ")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.currentPage = 16
        Global.screenState = "landingpage"
        // Inflate the layout for this fragment
        feesDetailViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[FeesDetailViewModel::class.java]
    //    return inflater.inflate(R.layout.fragment_gallery_init, container, false)

        _binding = FeeInitFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getProductById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID
        STUDENT_ROLL_NO = student.STUDENT_ROLL_NO

        var textViewTitle = view.findViewById(R.id.textView32) as TextView
        textViewTitle.text ="Fees Details"




        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)
//        recyclerViewAbsentList?.layoutManager = GridLayoutManager(requireActivity(),2)
        initMethod()

    }


    private fun initMethod() {
        //Fees/FeesPaidListGet?ClassId=1&AccademicId=8&StudentId=533&StudentRollNo=1
        feesDetailViewModel.getFeesDetails(CLASSID,ACADEMICID,STUDENTID,STUDENT_ROLL_NO)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            feesDetailsModel = response.feesPaidDetails
                            getResult()
                        }
                        Status.ERROR -> {
                           Log.i(TAG,"ERROR ")

                        }
                        Status.LOADING -> {
                            feesDetailsModel = ArrayList<FeesDetailsModel.FeesPaidDetail>()
                        }
                    }
                }
            })
    }




    private fun getResult(){

            val adapter = Global.MyPagerAdapter(childFragmentManager)
            adapter.addFragment(
                PayFeesFragment(),
                resources.getString(R.string.payfee)
            )
            adapter.addFragment(
                FeesDetailFragment(feesDetailsModel),
                resources.getString(R.string.pay_history)
            )
           shimmerViewContainer?.visibility = View.GONE
            // adapter.addFragment(new DMKOfficial(), "Tweets");
            viewPager?.adapter = adapter
            viewPager?.currentItem = 1
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


    override fun onDetach() {
        super.onDetach()
        mContext = null
        Log.i(TAG,"onDetach ")
    }

    override fun onDestroy() {
        super.onDestroy()
        mContext = null
        Log.i(TAG,"onDestroy ")
    }
}
