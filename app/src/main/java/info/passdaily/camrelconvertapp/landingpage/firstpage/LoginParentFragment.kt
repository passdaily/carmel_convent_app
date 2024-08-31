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
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.LoginParentViewModel
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.parent.MainActivityParent

@Suppress("DEPRECATION")
class LoginParentFragment(var number: String,var institutionCode : String) : Fragment() {
    var TAG = "LoginParentFragment"

    var imageBackPress: ImageView? = null
    var textForgot: TextView? = null

    var buttonLogin: AppCompatButton? = null
    var usernameEditField: TextInputEditText? = null
    var passwordEditField: TextInputEditText? = null
    var institutionalEditField: TextInputEditText? = null

    var checkTeams = false

    private lateinit var loginParentViewModel: LoginParentViewModel

    var constraintLayout : ConstraintLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        loginParentViewModel =
//            ViewModelProvider(this).get(LoginParentViewModel::class.java)

        loginParentViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[LoginParentViewModel::class.java]

        loginParentViewModel.init();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_parent, container, false)
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Global.screenState = "LoginFragment"
        constraintLayout = view.findViewById(R.id.constraintLayout) as ConstraintLayout

        usernameEditField = view.findViewById(R.id.usernameEditField) as TextInputEditText
        passwordEditField = view.findViewById(R.id.passwordEditField) as TextInputEditText
        institutionalEditField = view.findViewById(R.id.institutionalEditField) as TextInputEditText

        usernameEditField?.setText(number)
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

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewModelProviders.of(activity!!).get(LoginParentViewModel::class.java).getMessage()!!.observe(requireActivity()
        ) { message ->
            passwordEditField!!.setText(message)
        }

        //loginParentViewModel.init();
       // loginParentViewModel.sendData("");
    }

    private fun loginFunction() {

        if(loginParentViewModel.validateField(usernameEditField!!,constraintLayout!!,
                "Please give mobile number",requireActivity())
            &&loginParentViewModel.validateMobileField(usernameEditField!!,constraintLayout!!,
                "Please give valid mobile number",requireActivity())
                        && loginParentViewModel.validateField(passwordEditField!!,constraintLayout!!,
                "Please give password",requireActivity())
            &&  loginParentViewModel.validateField(institutionalEditField!!,constraintLayout!!,
                "Please give institutional code",requireActivity())) {

            loginParentViewModel.getLoginDetails(
                usernameEditField?.text.toString(),
                passwordEditField?.text.toString(),
                institutionalEditField?.text.toString()
            ).observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG, "resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if (response.pLOGINID != 0 && response.lOGINROLE.isNotEmpty()) {
                                Log.i(TAG, "pLOGINID " + response.pLOGINID)
                                Log.i(TAG, "lOGINROLE " + response.lOGINROLE)
                                val localDBHelper = LocalDBHelper(requireActivity())
                                localDBHelper.insertUser(
                                    LocalDBHelper.UserModel(
                                        response.pLOGINID,////as admin id for parent it is only for delete ///local database purpose
                                        0,
                                        "",
                                        "",
                                        4,
                                        response.pLOGINID,
                                        response.lOGINROLE,
                                        "",
                                        response.sCHOOLID
                                    )
                                )
                                startActivity(
                                    Intent(
                                        requireActivity(),
                                        MainActivityParent::class.java
                                    )
                                )
                                requireActivity().finish();
                                Toast.makeText(
                                    requireActivity(),
                                    "Login Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else {
                                progressStop()
                                Utils.getSnackBar4K(
                                    requireActivity(),
                                    "Login Failed.Please check Mobile number or Password",
                                    constraintLayout!!
                                )
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

    fun progressStop() {
        val fragment: ProgressBarDialog? =
            requireActivity().supportFragmentManager.findFragmentByTag(ProgressBarDialog.TAG) as ProgressBarDialog?
        if (fragment != null) {
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
        }
    }

}