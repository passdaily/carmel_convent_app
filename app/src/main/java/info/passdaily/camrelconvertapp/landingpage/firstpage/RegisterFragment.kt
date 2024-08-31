package info.passdaily.camrelconvertapp.landingpage.firstpage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentRegisterBinding
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.LoginParentViewModel
import info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel.LoginStaffViewModel
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import org.json.JSONObject

@Suppress("DEPRECATION")
class RegisterFragment(var titleName: String, var buttonName: String) : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!


    var imageBackPress: ImageView? = null
    var usernameEditField: TextInputEditText? = null

    var institutionalEditField: TextInputEditText? = null

    var buttonTakeTest: AppCompatButton? = null

    var constraintLayout: ConstraintLayout? = null

    var imageParent: ImageView? = null
    var imageTeacher: ImageView? = null

    var boarderStudent: ImageView? = null
    var boarderTeacher: ImageView? = null

    var textViewTitle: TextView? = null

    var selectedBoolean = false
    var selectType = 0

    private lateinit var loginParentViewModel: LoginParentViewModel

    private lateinit var loginStaffViewModel: LoginStaffViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loginParentViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[LoginParentViewModel::class.java]

        loginStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[LoginStaffViewModel::class.java]
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
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
        constraintLayout = binding.constraintLayout
        usernameEditField = binding.usernameEditField

        institutionalEditField = binding.institutionalEditField


        boarderStudent = binding.boarderStudent
        boarderTeacher = binding.boarderTeacher

        textViewTitle = binding.textViewTitle
        textViewTitle?.text = titleName

        boarderStudent?.visibility = View.GONE
        boarderTeacher?.visibility = View.GONE

        imageParent = view.findViewById(R.id.imageParent) as ImageView
        imageParent?.setOnClickListener {
            boarderTeacher?.visibility = View.GONE
            boarderStudent?.visibility = View.VISIBLE
            selectedBoolean = true
            selectType = 1
        }

        imageTeacher = view.findViewById(R.id.imageTeacher) as ImageView
        imageTeacher?.setOnClickListener {
            boarderStudent?.visibility = View.GONE
            boarderTeacher?.visibility = View.VISIBLE
            selectedBoolean = true
            selectType = 2
        }


        buttonTakeTest = view.findViewById(R.id.buttonTakeTest) as AppCompatButton
        buttonTakeTest?.text = buttonName
        buttonTakeTest?.setOnClickListener {
            var localDBHelper = LocalDBHelper(requireActivity())

            if (selectedBoolean) {
                if (loginParentViewModel.validateField(
                        usernameEditField!!, constraintLayout!!,
                        "Please give mobile number", requireActivity()
                    )
                    && loginParentViewModel.validateMobileField(
                        usernameEditField!!, constraintLayout!!,
                        "Please give valid mobile number", requireActivity()
                    )
                    && loginParentViewModel.validateField(
                        institutionalEditField!!, constraintLayout!!,
                        "Please give institutional Code", requireActivity()
                    )
                    && loginParentViewModel.validateSelectType(
                        selectType, constraintLayout!!,
                        "Please select parent or staff", requireActivity()
                    )
                ) {

                    if (selectType == 1) {
                        loginParentViewModel.getRegistrationDetails(usernameEditField?.text.toString(),
                            institutionalEditField?.text.toString())
                            .observe(requireActivity(), Observer {
                                it?.let { resource ->
                                    when (resource.status) {
                                        Status.SUCCESS -> {
                                            val response = resource.data?.body()!!
                                            progressStop()
                                            when {
                                                Utils.resultFun(response) == "NOTHING" -> {
                                                    Utils.getSnackBar4K(
                                                        requireActivity(),
                                                        "This Mobile number is not registered",
                                                        constraintLayout!!
                                                    )
                                                }
                                                else -> {
                                                    Utils.getSnackBarGreen(
                                                        requireActivity(),
                                                        "OTP is sent to your mobile Number",
                                                        constraintLayout!!
                                                    )
                                                    var fragmentManager =
                                                        requireActivity().supportFragmentManager
                                                    var fragmentTransaction =
                                                        fragmentManager.beginTransaction()
                                                    fragmentTransaction.replace(
                                                        R.id.frameContainer,
                                                        LoginParentFragment(
                                                            usernameEditField?.text.toString(),
                                                            institutionalEditField?.text.toString(),
                                                        )
                                                    ).commit()
                                                }
                                            }
                                        }
                                        Status.ERROR -> {
                                            progressStop()
                                            Log.i("TAG", "Error ${Status.ERROR}")
                                        }
                                        Status.LOADING -> {
                                            progressStart()
                                            Log.i("TAG", "resource ${resource.status}")
                                            Log.i("TAG", "message ${resource.message}")
                                        }
                                    }
                                }
                            })


                    }
                    else if (selectType == 2) {

                        loginStaffViewModel.getStaffAccountCreate(usernameEditField?.text.toString(),institutionalEditField?.text.toString())
                            .observe(requireActivity(), Observer {
                                it?.let { resource ->
                                    when (resource.status) {
                                        Status.SUCCESS -> {
                                            val response = resource.data?.body()!!
                                            val json = JSONObject(response.toString())
                                            progressStop()
                                            if (json.isNull("StaffDetails") && json.isNull("LoginDetails")) {
                                                Toast.makeText(
                                                    requireActivity(),
                                                    "Your mobile number not registered. Please contact office or support.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            if (json.isNull("StaffDetails") || json.isNull("LoginDetails")) {
                                                val StaffDetails = json.getJSONObject("StaffDetails")
                                                val STAFF_FNAME = StaffDetails.getString("STAFF_FNAME")
                                                Toast.makeText(
                                                    requireActivity(),
                                                    "Dear $STAFF_FNAME, Login not yet created. Please contact office or support.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                            if (!json.isNull("StaffDetails") && !json.isNull("LoginDetails")) {
                                                val StaffDetails = json.getJSONObject("StaffDetails")
                                                val LoginDetails = json.getJSONObject("LoginDetails")
                                                val ADMIN_LOGIN_NAME = LoginDetails.getString("ADMIN_LOGIN_NAME")
                                                val STAFF_FNAME = StaffDetails.getString("STAFF_FNAME")


                                                Toast.makeText(
                                                    requireActivity(),
                                                    "Dear $STAFF_FNAME, Username and Password Sent to your mobile.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                var fragmentManager = requireActivity().supportFragmentManager
                                                var fragmentTransaction = fragmentManager.beginTransaction()
                                                fragmentTransaction.replace(R.id.frameContainer,
                                                    LoginStaffFragment(institutionalEditField!!.text.toString(),STAFF_FNAME)).commit()
                                            }


//                                            Utils.getSnackBarGreen(
//                                                requireActivity(),
//                                                "Username and password sent to your mobile Number",
//                                                constraintLayout!!
//                                            )

                                        }
                                        Status.ERROR -> {
                                            progressStop()
                                            Log.i("TAG", "Error ${Status.ERROR}")
                                        }
                                        Status.LOADING -> {
                                            progressStart()
                                            Log.i("TAG", "resource ${resource.status}")
                                            Log.i("TAG", "message ${resource.message}")
                                        }
                                    }
                                }
                            })


                    }
                }
            } else {
                Utils.getSnackBar4K(
                    requireActivity(),
                    "Select teacher or student",
                    constraintLayout!!
                )
            }

        }


        constraintLayout!!.setOnClickListener {
            Utils.hideFocusListener(view, requireActivity())
        }

        if (!Utils.isOnline(requireActivity())) {
            Utils.getSnackBar4K(
                requireActivity(),
                requireActivity().resources.getString(R.string.no_internet),
                constraintLayout
            )
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