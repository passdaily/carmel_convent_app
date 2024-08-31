package info.passdaily.camrelconvertapp.landingpage.firstpage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.LoginParentViewModel
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer

@Suppress("DEPRECATION")
class ForgotFragment : Fragment() {

    var imageBackPress: ImageView? = null

    private lateinit var loginParentViewModel: LoginParentViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginParentViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[LoginParentViewModel::class.java]
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Global.screenState = "landingpage"

        imageBackPress = view.findViewById(R.id.imageBackPress) as ImageView
        imageBackPress?.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.frameContainer, FirstScreenFragment()
            ).commit()
        }


        var constraintLayout = view.findViewById(R.id.constraintLayout) as ConstraintLayout
        var usernameEditField = view.findViewById(R.id.usernameEditField) as TextInputEditText
        var institutionalEditField = view.findViewById(R.id.institutionalEditField) as TextInputEditText
        var buttonTakeTest = view.findViewById(R.id.buttonTakeTest) as AppCompatButton

        buttonTakeTest.setOnClickListener {

            if (loginParentViewModel.validateField(
                    usernameEditField, constraintLayout,
                    "Please give mobile number", requireActivity())
                &&loginParentViewModel.validateMobileField(usernameEditField
                    ,constraintLayout,
                    "Please give valid mobile number",requireActivity())
                && loginParentViewModel.validateField(
                    institutionalEditField, constraintLayout,
                    "Please give institutional code", requireActivity()
                )) {
                loginParentViewModel.getRegistrationDetails(usernameEditField.text.toString(),institutionalEditField.text.toString())
                    .observe(requireActivity(), Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    val response = resource.data?.body()!!
                                    when {
                                        Utils.resultFun(response) == "NOTHING" -> {
                                            Utils.getSnackBar4K(
                                                requireActivity(),
                                                "This Mobile number is not registered",
                                                constraintLayout
                                            )
                                        }
                                        else -> {
                                            Utils.getSnackBarGreen(
                                                requireActivity(),
                                                "OTP is sent to your mobile Number",
                                                constraintLayout
                                            )

                                            var fragmentManager =
                                                requireActivity().supportFragmentManager
                                            var fragmentTransaction =
                                                fragmentManager.beginTransaction()
                                            fragmentTransaction.replace(
                                                R.id.frameContainer,
                                                LoginParentFragment(usernameEditField.text.toString(),institutionalEditField.text.toString())
                                            ).commit()
                                        }
                                    }
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

        }

        constraintLayout.setOnClickListener {

            Utils.hideFocusListener(view,requireActivity())
        }

        if(!Utils.isOnline(requireActivity())){
            Utils.getSnackBar4K(requireActivity(),requireActivity().resources.getString(R.string.no_internet),constraintLayout)
        }
    }


}