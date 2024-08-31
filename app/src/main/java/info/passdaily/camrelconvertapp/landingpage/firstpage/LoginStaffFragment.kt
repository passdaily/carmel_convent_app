package info.passdaily.camrelconvertapp.landingpage.firstpage

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.LoginStaffViewModel
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.admin.MainActivityAdmin
import info.passdaily.camrelconvertapp.typeofuser.staff.MainActivityStaff

@Suppress("DEPRECATION")
class LoginStaffFragment(var institutionCode : String,var teacherName : String) : Fragment() {
    var TAG = "LoginStaffFragment"

    var imageBackPress : ImageView?= null
    var textForgot : TextView? =null

    var buttonLogin: AppCompatButton? = null
    var constraintLayout : ConstraintLayout? = null

    var usernameEditField: TextInputEditText? = null
    var passwordEditField: TextInputEditText? = null
    var institutionalEditField: TextInputEditText? = null

    var checkTeams = false

    private lateinit var loginStaffViewModel: LoginStaffViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[LoginStaffViewModel::class.java]

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_staff, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Global.screenState = "LoginFragment"

        constraintLayout = view.findViewById(R.id.constraintLayout) as ConstraintLayout
        usernameEditField = view.findViewById(R.id.usernameEditField) as TextInputEditText
        passwordEditField = view.findViewById(R.id.passwordEditField) as TextInputEditText
        institutionalEditField = view.findViewById(R.id.institutionalEditField) as TextInputEditText

        institutionalEditField?.setText(institutionCode)

        imageBackPress = view.findViewById(R.id.imageBackPress) as ImageView
        imageBackPress?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, SelectUserFragment()
            ).commit()
        }
        textForgot = view.findViewById(R.id.textForgot) as TextView
        textForgot?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, RegisterFragment("Forgot Password","Continue")
            ).commit()
        }


        buttonLogin = view.findViewById(R.id.buttonTakeTest) as AppCompatButton
        val textViewTerms = view.findViewById<TextView>(R.id.textViewTerms)
        removeLinksUnderline(textViewTerms)

        val checkBoxTeams = view.findViewById<AppCompatCheckBox>(R.id.checkBox)

        checkBoxTeams.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                println("check box clicked")
                checkTeams = true
            } else {
                checkTeams = false
                println("Somthing happen wrong")
            }
        }


        buttonLogin?.setOnClickListener {
            //  && institutionalEditField?.text.toString().isNotEmpty())
            if(checkTeams) {
                loginFunction()
            }else{
                Utils.getSnackBar4K(requireActivity(), "Need to agree with our terms of services and privacy policy",
                    constraintLayout!!)
            }
        }

        constraintLayout!!.setOnClickListener {
            Utils.hideFocusListener(view,requireActivity())
        }

        if(!Utils.isOnline(requireActivity())){
            Utils.getSnackBar4K(requireActivity(),requireActivity().resources.getString(R.string.no_internet),constraintLayout)
        }

    }

    fun removeLinksUnderline(textView: TextView) {
        val spannable = SpannableString(textView.text)
        for (urlSpan in spannable.getSpans(
            0, spannable.length,
            URLSpan::class.java
        )) {
            spannable.setSpan(object : URLSpan(urlSpan.url) {
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }
            }, spannable.getSpanStart(urlSpan), spannable.getSpanEnd(urlSpan), 0)
        }
        textView.text = spannable
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun loginFunction() {
        var localDBHelper = LocalDBHelper(requireActivity())
        if(localDBHelper.viewUser().isNotEmpty()){
            for(i in localDBHelper.viewUser().indices){
                Log.i(TAG,"ADMIN_ID ${localDBHelper.viewUser()[i].ADMIN_ID}")
                Log.i(TAG,"STAFF_ID ${localDBHelper.viewUser()[i].STAFF_ID}")
                Log.i(TAG,"BASE_URL ${localDBHelper.viewUser()[i].BASE_URL}")
                Log.i(TAG,"PLOGIN_ID ${localDBHelper.viewUser()[i].PLOGIN_ID}")
                Log.i(TAG,"ADMIN_ROLE ${localDBHelper.viewUser()[i].ADMIN_ROLE}")
            }

        }

        if(loginStaffViewModel.validateField(usernameEditField!!,constraintLayout!!,
                "Please give username",requireActivity())
//            &&loginStaffViewModel.validateMobileField(usernameEditField!!,constraintLayout!!,
//                "Please give valid mobile number",requireActivity())
            && loginStaffViewModel.validateField(passwordEditField!!,constraintLayout!!,
                "Please give password",requireActivity())
            &&  loginStaffViewModel.validateField(institutionalEditField!!,constraintLayout!!,
                "Please give institutional code",requireActivity())) {


//            val service: RetrofitClient = retrofit.create(RetrofitClient::class.java)
//            service.profilePicture("https://s3.amazon.com/profile-picture/path")
            loginStaffViewModel.getLoginDetails(
                usernameEditField?.text.toString(),
                passwordEditField?.text.toString(),
                institutionalEditField?.text.toString()
            ).observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when (response.aDMINROLE) {
                                1 -> {
                                    Log.i(TAG, "aDMINID " + response.aDMINID)
                                    Log.i(TAG, "sTAFFID " + response.sTAFFID)
                                    Utils.getSnackBarGreen(
                                        requireActivity(), "Your are an admin",
                                        constraintLayout!!
                                    )
                                    if(teacherName.isEmpty()){
                                        teacherName = "Admin"
                                    }

                                    localDBHelper.insertUser(
                                        LocalDBHelper.UserModel(
                                            response.aDMINID,
                                            response.sTAFFID,
                                            "response.sTAFFFNAME",
                                            "response.sTAFFIMAGE",
                                            response.aDMINROLE,
                                            0,
                                            response.lOGINROLE,
                                            "",
                                        response.sCHOOL_ID
                                        )
                                    )
                                    startActivity(Intent(requireActivity(),
                                        MainActivityAdmin::class.java))

                                    requireActivity().finish();
                                    Toast.makeText(
                                        requireActivity(),
                                        "Login Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }3 -> {
                                    Log.i(TAG, "aDMINID " + response.aDMINID)
                                    Log.i(TAG, "sTAFFID " + response.sTAFFID)
                                    Utils.getSnackBarGreen(
                                        requireActivity(), "Your are a Staff",
                                        constraintLayout!!
                                    )
                                if(teacherName.isEmpty()){
                                    teacherName = "Teacher"
                                }

                                    localDBHelper.insertUser(
                                        LocalDBHelper.UserModel(
                                            response.aDMINID,
                                            response.sTAFFID,
                                            "response.sTAFFFNAME",
                                            "response.sTAFFIMAGE",
                                            response.aDMINROLE,
                                            0,
                                            response.lOGINROLE,
                                            "",
                                            response.sCHOOL_ID
                                        )
                                    )
                                    startActivity(Intent(requireActivity(),
                                        MainActivityStaff::class.java))

                                    requireActivity().finish();
                                    Toast.makeText(
                                        requireActivity(),
                                        "Login Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                5 -> {
                                    Log.i(TAG, "aDMINID " + response.aDMINID)
                                    Log.i(TAG, "sTAFFID " + response.sTAFFID)
                                    Utils.getSnackBarGreen(
                                        requireActivity(), "Your are in management",
                                        constraintLayout!!
                                    )
                                    if(teacherName.isEmpty()){
                                        teacherName = "Management"
                                    }

                                    localDBHelper.insertUser(
                                        LocalDBHelper.UserModel(
                                            response.aDMINID,
                                            response.sTAFFID,
                                            "response.sTAFFFNAME",
                                            "response.sTAFFIMAGE",
                                            response.aDMINROLE,
                                            0,
                                            response.lOGINROLE,
                                            "",
                                            response.sCHOOL_ID
                                        )
                                    )
                                    startActivity(Intent(requireActivity(),
                                        MainActivityAdmin::class.java))

                                    requireActivity().finish();
                                    Toast.makeText(
                                        requireActivity(),
                                        "Login Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {
                                    progressStop()
                                    Utils.getSnackBar4K(
                                        requireActivity(), "Username and password does not match",
                                        constraintLayout!!
                                    )
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(requireActivity(),"Error While Login",constraintLayout!!)
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }

                }
            })
        }
    }


    private fun progressStart() {
        val dialog1 = ProgressBarDialog()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.isCancelable = false
        dialog1.show(transaction, ProgressBarDialog.TAG)
    }

    private fun progressStop() {
        val fragment: ProgressBarDialog? =
            requireActivity().supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
        if (fragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }
}