package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
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
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_message.MessageDeliveryTabFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_voice.send_to_voice.UploadAudioFile
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener

@Suppress("DEPRECATION")
class QuickVoiceMessageFragment : Fragment(),VoiceMessageClickListener {

    var TAG = "QuickVoiceMessageFragment"
    private lateinit var quickVoiceMessageViewModel: QuickVoiceMessageViewModel
    private var _binding: FragmentQuickNotificationBinding? = null
    private val binding get() = _binding!!
    var staffId = 0

    private lateinit var localDBHelper : LocalDBHelper
    var toolBarClickListener : ToolBarClickListener? = null

    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var constraintLayoutContent : ConstraintLayout? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null


    var fab : FloatingActionButton? = null

    private val REQUEST_RECORD_AUDIO = 0
    private val AUDIO_FILE_PATH =
        Environment.getExternalStorageDirectory().absolutePath + "/Passdaily"+ "/Audio_Record1.mp3"

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
        toolBarClickListener?.setToolbarName("Voice Message")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        staffId = user[0].ADMIN_ID


        // Inflate the layout for this fragment
        quickVoiceMessageViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[QuickVoiceMessageViewModel::class.java]

        _binding = FragmentQuickNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        constraintLayoutContent = binding.constraintLayoutContent
        shimmerViewContainer= binding.shimmerViewContainer


        viewPager = binding.pager
        tabLayout = binding.tabLayout

//        FileUtils.requestPermission(requireActivity(), Manifest.permission.RECORD_AUDIO)
//        FileUtils.requestPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)

        fab = binding.fab
        fab?.visibility = View.VISIBLE
        fab?.setOnClickListener {
            val intent  = Intent(mContext, UploadAudioFile::class.java)
            startActivity(intent)

//            AndroidAudioRecorder.with(requireActivity()) // Required
//                .setFilePath(AUDIO_FILE_PATH)
//                .setColor(ContextCompat.getColor(requireActivity(), R.color.white))
//                .setRequestCode(REQUEST_RECORD_AUDIO) // Optional
//                .setSource(AudioSource.MIC)
//                .setChannel(AudioChannel.STEREO)
//                .setSampleRate(AudioSampleRate.HZ_48000)
//                .setAutoStart(false)
//                .setKeepDisplayOn(true) // Start recording
//                .record()

//            val dialog1 = CreateVoiceDialog()
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
//            dialog1.show(transaction, CreateVoiceDialog.TAG)
        }

        initFunction()

    }

    private fun initFunction() {
        quickVoiceMessageViewModel.getVoiceMessageListStaff(staffId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            Global.voiceMessageList = response.voiceList as ArrayList<VoiceMessageListModel.Voice>
                            getNotificationSentDetails()

                            Log.i(TAG,"initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            constraintLayoutContent?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            Global.voiceMessageList = ArrayList<VoiceMessageListModel.Voice>()
                            Log.i(TAG,"initFunction LOADING")
                        }
                    }
                }
            })

    }

    private fun getNotificationSentDetails() {
        quickVoiceMessageViewModel.getVoiceMessageDeliveryListStaff(staffId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            Global.messageDeliveryList = response.messageDeliveryList as ArrayList<MessageDeliveryListModel.SmsDelivery>
                            getTabResult()

                            Log.i(TAG,"getNotificationSentDetails SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getNotificationSentDetails ERROR")
                        }
                        Status.LOADING -> {
                            Global.messageDeliveryList = ArrayList<MessageDeliveryListModel.SmsDelivery>()
                            Log.i(TAG,"getNotificationSentDetails LOADING")
                        }
                    }
                }
            })
    }


    private fun getTabResult() {

        val adapter = Global.MyPagerAdapter(childFragmentManager)
        adapter.addFragment(
            VoiceMessageTabFragment(this), resources.getString(R.string.message_template)
        )
        adapter.addFragment(MessageDeliveryTabFragment(), resources.getString(R.string.message_delivery))
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

    override fun onCreateClick(message: String) {
        Log.i(TAG,"onCreateClick")
        initFunction()
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun onFailedMessage(message: String) {
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
    }

}

interface VoiceMessageClickListener {
    fun onCreateClick(message: String)
    fun onFailedMessage(message: String)
}