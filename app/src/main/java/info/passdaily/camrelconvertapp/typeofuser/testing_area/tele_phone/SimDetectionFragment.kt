//package info.passdaily.teach_daily_app.typeofuser.testing_area.tele_phone
//
//import android.Manifest
//import android.content.ClipboardManager
//import android.content.Context
//import android.content.IntentFilter
//import android.content.pm.PackageManager.PERMISSION_GRANTED
//import android.os.Build
//import android.os.Bundle
//import android.provider.Settings
//import android.telephony.SubscriptionInfo
//import android.telephony.SubscriptionManager
//import android.telephony.TelephonyManager
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.result.ActivityResultLauncher
//import androidx.annotation.NonNull
//import androidx.core.app.ActivityCompat
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.ViewModelProviders
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.gms.auth.api.phone.SmsRetriever
//import com.google.android.material.textfield.TextInputEditText
//import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
//import info.passdaily.teach_daily_app.R
//import info.passdaily.teach_daily_app.databinding.FragmentSimDetectionBinding
//import info.passdaily.teach_daily_app.landingpage.SmsBroadcastReceiver
//import info.passdaily.teach_daily_app.landingpage.firstpage.viewmodel.LoginParentViewModel
//import info.passdaily.teach_daily_app.lib.telephone.TelephonyInfo
//import info.passdaily.teach_daily_app.services.ViewModelFactory
//import info.passdaily.teach_daily_app.services.client_manager.ApiClient
//import info.passdaily.teach_daily_app.services.client_manager.NetworkLayer
//import info.passdaily.teach_daily_app.typeofuser.staff.ToolBarClickListener
//import java.util.regex.Matcher
//import java.util.regex.Pattern
//
//
//class SimDetectionFragment : Fragment() ,SmsBroadcastReceiver.OTPReceiveListener {
//
//    var TAG  = "SimDetectionFragment"
//
//    private var smsVerifyCatcher: SmsVerifyCatcher? = null
//
//    private var _binding: FragmentSimDetectionBinding? = null
//    private val binding get() = _binding!!
//
//    var wantPermission: String = Manifest.permission.READ_PHONE_STATE
//    private val PERMISSION_REQUEST_CODE = 1
//
//     var recyclerViewSim : RecyclerView? = null
//
//    var textViewNoSim : TextView? = null
//
//    private lateinit var loginParentViewModel: LoginParentViewModel
//
//    var smsBroadcastReceiver: SmsBroadcastReceiver? = null
//    private val REQ_USER_CONSENT = 200
//
//    private var smsReceiver: SmsBroadcastReceiver? = null
//
//
//
//    var simList = ArrayList<SimDetectList>()
//    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
//
//    var passwordEditField : TextInputEditText? = null
//    var toolBarClickListener : ToolBarClickListener? = null
//    var mContext : Context? = null
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (mContext == null) {
//            mContext = context.applicationContext
//        }
//        try {
//            toolBarClickListener = context as ToolBarClickListener
//        } catch (e: Exception) {
//            Log.i(TAG, "Exception $e")
//        }
//    }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // Inflate the layout for this fragment
//        toolBarClickListener?.setToolbarName("Sim Detection")
//
//        loginParentViewModel = ViewModelProviders.of(
//            this,
//            ViewModelFactory(ApiClient(NetworkLayer.services))
//        )[LoginParentViewModel::class.java]
//
//        _binding = FragmentSimDetectionBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//        passwordEditField = binding.usernameEditField
//
//        val appSignatureHashHelper = AppSignatureHashHelper(requireActivity())
//
//        // This code requires one time to get Hash keys do comment and share key
//
//        // This code requires one time to get Hash keys do comment and share key
//        Log.i(TAG, "HashKey: " + appSignatureHashHelper.appSignatures[0])
//
//        startSMSListener()
//
//        val textViewHasCode = binding.textViewHasCode
//        textViewHasCode.text = appSignatureHashHelper.appSignatures[0]
//
//        textViewHasCode.setOnClickListener {
//            val cm: ClipboardManager = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            cm.text = appSignatureHashHelper.appSignatures[0]
//            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
//        }
//
//
//        recyclerViewSim = binding.recyclerViewSim
//
//        textViewNoSim = binding.textViewNoSim
//
//        recyclerViewSim?.layoutManager = GridLayoutManager(requireActivity(),2)
//
//
//      //  isDualSimOrNot()
//
//        //init SmsVerifyCatcher
//        //init SmsVerifyCatcher
////        smsVerifyCatcher = SmsVerifyCatcher(requireActivity(),
////            OnSmsCatchListener { message ->
////                val code: String = getOtpFromMessage(message) //Parse verification code
////                binding.passwordEditField.setText(code) //set code in edit text
////                //then you can send verification code to server
////            })
////
////        smsVerifyCatcher?.setPhoneNumberFilter("CP-PASDLY");
//
//        if (!checkPermission()) {
//            requestPermission();
//        }
//        else {
////            simList = ArrayList<SimDetectList>()
////
////            //      Log.i(TAG, "Phone number: " + getPhone());
////            val telephonyManager =
////                context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//////            val getSimSerialNumber = telephonyManager.simSerialNumber
//////            val getSimNumber = telephonyManager.line1Number
////
////            Log.i(TAG, "Phone number: " + telephonyManager.line1Number);
////
////            val subscriptionManager = SubscriptionManager.from(requireActivity())
////            val subsInfoList: List<SubscriptionInfo> =
////                subscriptionManager.activeSubscriptionInfoList
////
////
////            for (subscriptionInfo in subsInfoList) {
////                Log.i(TAG, "Current list = $subscriptionInfo")
////                val index: Int = subscriptionInfo.simSlotIndex
////                Log.i(TAG, " Number is  " + subscriptionInfo.iccId)
////                Log.i(TAG, " displayName  " + subscriptionInfo.displayName)
////                simList.add(SimDetectList(subscriptionInfo.displayName.toString(),""))
//////                if (index == 1) {
//////                    Log.i(TAG, " Number is  " + subscriptionInfo.number)
//////                } else Log.i(TAG, " Number is  " + subscriptionInfo.number)
////            }
//            detectSim()
//        }
//
//
//
//        startSMSListener()
//
////            if (SubscriptionManager.from(requireActivity()).activeSubscriptionInfoCount > 0) {
////                val list: ArrayList<String> = ArrayList()
////                for (i in 0 until SubscriptionManager.from(requireActivity()).activeSubscriptionInfoList.size) {
////                    Log.i(TAG, "Phone number: " + telephonyManager.createForSubscriptionId(i).simOperator);
////                    Log.i(TAG, "Phone number: " + telephonyManager.createForSubscriptionId(i).simState);
////                    Log.i(TAG, "Phone number: " + telephonyManager.createForSubscriptionId(i).line1Number);
////
////                    Log.i(TAG, "Single or Dula Sim "+telephonyManager.phoneCount);
////                    Log.i(TAG, "Defualt device ID "+getDeviceId(requireActivity()))
////                    Log.i(TAG, "Single 1 "+telephonyManager.getDeviceId(0));
////                    Log.i(TAG, "Single 2 "+telephonyManager.getDeviceId(1));
////                    _lst.add(
////                        """
////
////                ${java.lang.String.valueOf(phoneMgr.createForSubscriptionId(i).getCallState())}
////                """.trimIndent()
////                    )
////                    _lst.add(
////                        """
////
////                IMEI NUMBER : ${phoneMgr.createForSubscriptionId(i).getImei()}
////                """.trimIndent()
////                    )
////                    _lst.add(
////                        """
////
////                MOBILE NUMBER : ${phoneMgr.createForSubscriptionId(i).getLine1Number()}
////                """.trimIndent()
////                    )
////                    _lst.add(
////                        """
////
////                SERIAL NUMBER : ${phoneMgr.createForSubscriptionId(i).getSimSerialNumber()}
////                """.trimIndent()
////                    )
////                    _lst.add(
////                        """
////
////                SIM OPERATOR NAME : ${phoneMgr.createForSubscriptionId(i).getSimOperatorName()}
////                """.trimIndent()
////                    )
////                    _lst.add(
////                        """
////
////                MEI NUMBER : ${phoneMgr.createForSubscriptionId(i).getMeid()}
////                """.trimIndent()
////                    )
////                    _lst.add(
////                        """
////
////                SIM STATE : ${
////                            java.lang.String.valueOf(
////                                phoneMgr.createForSubscriptionId(i).getSimState()
////                            )
////                        }
////                """.trimIndent()
////                    )
////                    _lst.add(
////                        """
////
////                COUNTRY ISO : ${phoneMgr.createForSubscriptionId(i).getSimCountryIso()}
////                """.trimIndent()
////                    )
////                }
//
//
////        binding.buttonTakeTest.setOnClickListener {
////            if (loginParentViewModel.validateField(
////                    binding.usernameEditField,  binding.constraintLayout,
////                    "Please give mobile number", requireActivity()
////                )
////                && loginParentViewModel.validateMobileField(
////                    binding.usernameEditField,  binding.constraintLayout,
////                    "Please give valid mobile number", requireActivity()
////                )
////            ) {
////                loginParentViewModel.getRegistrationDetails(binding.usernameEditField.text.toString())
////                    .observe(requireActivity(), Observer {
////                        it?.let { resource ->
////                            when (resource.status) {
////                                Status.SUCCESS -> {
////                                    val response = resource.data?.body()!!
////                                    when {
////                                        Utils.resultFun(response) == "NOTHING" -> {
////                                            Utils.getSnackBar4K(
////                                                requireActivity(),
////                                                "This Mobile number is not registered",
////                                                binding.constraintLayout
////                                            )
////                                        }
////                                        else -> {
////                                                Utils.getSnackBarGreen(
////                                                    requireActivity(),
////                                                    "OTP is sent to your mobile Number",
////                                                    binding.constraintLayout
////                                                )
//////                                                var fragmentManager =
//////                                                    requireActivity().supportFragmentManager
//////                                                var fragmentTransaction =
//////                                                    fragmentManager.beginTransaction()
//////                                                fragmentTransaction.replace(
//////                                                    R.id.sim_host_fragment,
//////                                                    OtpFragment()
//////                                                ).commit()
////
////                                        }
////                                    }
////                                }
////                                Status.ERROR -> {
////                                    Log.i("TAG", "Error ${Status.ERROR}")
////                                }
////                                Status.LOADING -> {
////                                    Log.i("TAG", "resource ${resource.status}")
////                                    Log.i("TAG", "message ${resource.message}")
////                                }
////                            }
////                        }
////                    })
////            }
////        }
//
//
//    }
//
//
//    class SimDetectAdapter(
//        var simList: ArrayList<SimDetectList>,
//        var context: Context
//    ) :
//        RecyclerView.Adapter<SimDetectAdapter.ViewHolder>() {
//        var index = -1
//        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var textViewSimName: TextView = view.findViewById(R.id.textViewSimName)
//            var imageViewIcon : ImageView = view.findViewById(R.id.imageViewIcon)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val itemView = LayoutInflater.from(parent.context)
//                .inflate(R.layout.sim_adapter, parent, false)
//            return ViewHolder(itemView)
//        }
//
//        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.textViewSimName.text = simList[position].simOperator
//            holder.itemView.setOnClickListener {
//                index = position;
//                notifyDataSetChanged()
//            }
//
//            if (index == position) {
//                holder.textViewSimName.setTextColor(context.resources.getColor(R.color.red_200))
//                holder.imageViewIcon.setColorFilter(context.resources.getColor(R.color.red_200));
//            } else {
//                holder.textViewSimName.setTextColor(context.resources.getColor(R.color.black))
//                holder.imageViewIcon.setColorFilter(context.resources.getColor(R.color.black));
//            }
//        }
//
//        override fun getItemCount(): Int {
//            return simList.size
//        }
//
//    }
//
//
//    fun detectSim(){
//        simList = ArrayList<SimDetectList>()
//
//        //      Log.i(TAG, "Phone number: " + getPhone());
//        val telephonyManager =
//            context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
////            val getSimSerialNumber = telephonyManager.simSerialNumber
////            val getSimNumber = telephonyManager.line1Number
//
//        Log.i(TAG, "Phone number: " + telephonyManager.line1Number);
//
//        val subscriptionManager = SubscriptionManager.from(requireActivity())
//        val subsInfoList: List<SubscriptionInfo> =
//            subscriptionManager.activeSubscriptionInfoList
//
//
//        for (subscriptionInfo in subsInfoList) {
//            Log.i(TAG, "Current list = $subscriptionInfo")
//            val index: Int = subscriptionInfo.simSlotIndex
//            Log.i(TAG, " Number is  " + subscriptionInfo.iccId)
//            Log.i(TAG, " displayName  " + subscriptionInfo.displayName)
//            simList.add(SimDetectList(subscriptionInfo.displayName.toString(),""))
////                if (index == 1) {
////                    Log.i(TAG, " Number is  " + subscriptionInfo.number)
////                } else Log.i(TAG, " Number is  " + subscriptionInfo.number)
//        }
//
//        if(simList.size == 0){
//            textViewNoSim?.visibility = View.VISIBLE
//            recyclerViewSim?.visibility = View.GONE
//        }else{
//            textViewNoSim?.visibility = View.GONE
//            recyclerViewSim?.visibility = View.VISIBLE
//        }
//
//        recyclerViewSim?.adapter =SimDetectAdapter(simList,requireActivity())
//    }
//
//
//
//    fun getOtpFromMessage(message: String?) : String{
//        // This will match any 6 digit number in the message
//        //val pattern: Pattern = Pattern.compile("(|^)\\d{6}")
//        var otp = ""
//        //val pattern = Pattern.compile("[^\\d]*[\\d]+[^\\d]+([\\d]+)+[^\\d]+([\\d]+)")
//        //val pattern = Pattern.compile("(|^)\\d{6}")
//        val pattern = Pattern.compile("\\d+");
//        val matcher: Matcher = pattern.matcher(message!!)
//
//      //  Log.i(TAG,"Otp here ${matcher.matches()}")
//        while (matcher.find()) {
//
//            if(matcher.group().length == 6){
//                otp = matcher.group()
//            }
//            //otpText.setText(matcher.group(0))
//           // loginParentViewModel.sendData(matcher.group(1));
//           // Log.i(TAG,"Otp here ${matcher.matches()}")
//      //      otp = matcher.group(2)!!
////            Log.i(TAG,"Otp here ${matcher.group(0)}")
//            Log.i(TAG,"Otp here ${matcher.group()}")
//         //   Log.i(TAG,"Otp here ${matcher.group(2)}")
//        }
//        return otp
//    }
//
//    fun getDeviceId(context: Context): String? {
//        return Settings.Secure.getString(
//            context.contentResolver,
//            Settings.Secure.ANDROID_ID
//        )
//    }
//
////    private fun getPhone(): String {
////        val telephonyManager =
////            context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
////        return if (ActivityCompat.checkSelfPermission(
////                activity!!,
////                wantPermission
////            ) != PackageManager.PERMISSION_GRANTED
////        ) {
////            ""
////        } else telephonyManager?.line1Number
////    }
//
//
//    private fun isDualSimOrNot() {
//        val telephonyInfo = TelephonyInfo.getInstance(requireActivity())
////        val imeiSIM1 = telephonyInfo.imeiSIM1
////        val imeiSIM2 = telephonyInfo.imeiSIM2
//        val isSIM1Ready = telephonyInfo.isSIM1Ready
//        val isSIM2Ready = telephonyInfo.isSIM2Ready
//        val isDualSIM = telephonyInfo.isDualSIM
//        Log.i(TAG, "DUAL SIM : $isDualSIM SIM1 READY : $isSIM1Ready SIM2 READY : $isSIM2Ready")
//    }
//
//
////    private fun requestPermission(permission: String) {
////        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
////            Toast.makeText(
////                activity,
////                "Phone state permission allows us to get phone number. Please allow it for additional functionality.",
////                Toast.LENGTH_LONG
////            ).show()
////        }
////        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), PERMISSION_REQUEST_CODE)
////    }
//
//
//
////    override fun onRequestPermissionsResult(requestCode: Int,
////                                            permissions: Array<String>, grantResults: IntArray) {
////        when (requestCode) {
////            PERMISSION_REQUEST_CODE -> {
////                // If request is cancelled, the result arrays are empty.
////                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                    // permission was granted.
////                    //proceedAfterPermission() // permission was granted.
////                    Log.i( TAG,"Permission granted!");
////                } else {
////                    Log.i( TAG,"Permission denied!");
////                    // permission denied.
////                }
////                return
////            }
////        }
////    }
//
//
//    private fun requestPermission() {
//        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_PHONE_NUMBERS,
//            Manifest.permission.READ_PHONE_STATE), PERMISSION_REQUEST_CODE)
//    }
//
//    private fun checkPermission(): Boolean {
//        return if (Build.VERSION.SDK_INT >= 23) {
//                    ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_PHONE_NUMBERS) == PERMISSION_GRANTED
//                            && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_PHONE_STATE) == PERMISSION_GRANTED
//
////            val result: Int = ContextCompat.checkSelfPermission(requireActivity(), permission)
////            result == PERMISSION_GRANTED
//        } else {
//            true
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        //    registerBroadcastReceiver()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        //  unregisterReceiver(smsBroadcastReceiver)
//    }
//
//    override fun onOTPReceived(otp: String?) {
//        passwordEditField?.setText(getOtpFromMessage(otp))
//
//        if (smsReceiver != null) {
//            if (isAdded){
//                requireActivity().unregisterReceiver(smsReceiver);
//                smsReceiver = null;
//            }
//            //  Log.i(TAG,"not onOTPReceived ")
//        }
//    }
//
//    override fun onOTPTimeOut() {
//        Log.i(TAG,"onOTPTimeOut")
//        if (smsReceiver != null) {
//            Log.i(TAG,"not onOTPTimeOut ")
//            if (isAdded){
//                requireActivity().unregisterReceiver(smsReceiver);
//                smsReceiver = null;
//            }
//        }
//    }
//
//    override fun onOTPReceivedError(error: String?) {
//        Log.i(TAG,"onOTPReceivedError $error")
//    }
//
//
//    private fun startSMSListener() {
//        try {
//            smsReceiver = SmsBroadcastReceiver()
//            smsReceiver?.setOTPListener(this)
//            val intentFilter = IntentFilter()
//            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
//            requireActivity().registerReceiver(smsReceiver, intentFilter)
//            val client = SmsRetriever.getClient(requireActivity())
//            val task = client.startSmsRetriever()
//            task.addOnSuccessListener {
//                // API successfully started
//                Log.i(TAG,"addOnSuccessListener")
//            }
//            task.addOnFailureListener {
//                // Fail to start API
//                Log.i(TAG,"addOnFailureListener")
//            }
//        } catch (e: Exception) {
//            Log.i(TAG,"Exception $e")
//            e.printStackTrace()
//        }
//    }
//    /**
//     * need for Android 6 real time permissions
//     */
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        @NonNull permissions: Array<String>,
//        @NonNull grantResults: IntArray
//    ) {
//      //  smsVerifyCatcher?.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
//
//
//
//}
//
//
//class SimDetectList(var simOperator : String,var fileName: String)