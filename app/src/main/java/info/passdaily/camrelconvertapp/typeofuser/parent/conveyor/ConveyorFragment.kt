package info.passdaily.camrelconvertapp.typeofuser.parent.conveyor

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.databinding.FragmentConveyorBinding
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
class ConveyorFragment : Fragment() {

    var TAG = "ConveyorFragment"
    lateinit var binding : FragmentConveyorBinding

    private lateinit var conveyorViewModel: ConveyorViewModel

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0
    var STUDENT_ROLL_NO = 0   //P04439750.

    var constraintLayout : ConstraintLayout? = null

    var extendedFAB : ExtendedFloatingActionButton? = null
    var editDriverName: TextInputEditText? = null
    var editMobileNumber: TextInputEditText? = null
    var editVehicleNo: TextInputEditText? = null
    var editDriverAddress : TextInputEditText? = null
    var buttonSubmit : AppCompatButton? = null

    var mContext : Context? =null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG,"onAttach ")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        Global.currentPage = 19
        Global.screenState = "landingpage"

        conveyorViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[ConveyorViewModel::class.java]

        // Inflate the layout for this fragment
        binding = FragmentConveyorBinding.inflate(inflater, container, false)

        return binding.root


      //  return inflater.inflate(R.layout.fragment_conveyor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getProductById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID
        STUDENT_ROLL_NO = student.STUDENT_ROLL_NO

        constraintLayout  = binding.constraintLayout
        constraintLayout?.setOnClickListener {
            Utils.hideFocusListener(view,requireActivity())
        }

        extendedFAB = binding.extendedFAB
        editDriverName = binding.editDriverName
        editMobileNumber = binding.editMobileNumber
        editVehicleNo = binding.editVehicleNo
        editDriverAddress = binding.editDriverAddress

        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.setOnClickListener {
            Create_conveyor()
        }

        extendedFAB?.setOnClickListener {
            extendedFAB!!.visibility = View.GONE
            editDriverName!!.isEnabled = true
            editDriverName!!.requestFocus()
            editMobileNumber!!.isEnabled = true
            editVehicleNo!!.isEnabled = true
            editDriverAddress!!.isEnabled = true
            buttonSubmit!!.text = "Update"
            buttonSubmit!!.isEnabled = true

            editDriverName!!.setSelection(editDriverName!!.text!!.length)
            editMobileNumber!!.setSelection(editMobileNumber!!.text!!.length)
            editVehicleNo!!.setSelection(editVehicleNo!!.text!!.length)
            editDriverAddress!!.setSelection(editDriverAddress!!.text!!.length)
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        intiFunction()

    }

    private fun Create_conveyor() {

        if(conveyorViewModel.validateField(editDriverName!!) &&
            conveyorViewModel.validateField(editVehicleNo!!) &&
            conveyorViewModel.validateField(editDriverAddress!!) &&
            conveyorViewModel.validateField(editMobileNumber!!)){
            val url = "Conveyor/ConveyorCreate"

            val jsonObject = JSONObject()
            try {
                jsonObject.put("CONVEYORS_NAME", editDriverName!!.text.toString())
                jsonObject.put("CONVEYORS_MOBILE", editMobileNumber!!.text.toString())
                jsonObject.put("CONVEYORS_VEHICLE_NO", editVehicleNo!!.text.toString())
                jsonObject.put("CONVEYORS_ADDRESS", editDriverAddress!!.text.toString())
                jsonObject.put("STUDENT_ID", STUDENTID)
                jsonObject.put("CLASS_ID", CLASSID)
                jsonObject.put("CONVEYORS_STATUS", "1")
                jsonObject.put("CONVEYORS_DATE", "")

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            Log.i(TAG,"jsonObject $jsonObject")

            val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            conveyorViewModel.getCommonPostFun(url,accademicRe)
                .observe(requireActivity(), Observer {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                val response = resource.data?.body()!!

                                Log.i(TAG,"response $response")
                                when {
                                    Utils.resultFun(response) == "SUCCESS" -> {
                                        Utils.getSnackBarGreen(
                                            requireActivity(),
                                            "Create Conveyor",
                                            constraintLayout!!
                                        )
                                        extendedFAB!!.visibility = View.VISIBLE
                                        editDriverName!!.isEnabled = false
                                        editMobileNumber!!.isEnabled = false
                                        editVehicleNo!!.isEnabled = false
                                        editDriverAddress!!.isEnabled = false
                                        buttonSubmit!!.text = "Update"
                                        buttonSubmit!!.isEnabled = false
                                        // START_QUESTION_TIME = END_QUESTION_TIME
                                    }
                                    Utils.resultFun(response) == "UPDATE" -> {
                                        Utils.getSnackBarGreen(
                                            requireActivity(),
                                            "Update Conveyor",
                                            constraintLayout!!
                                        )
                                        extendedFAB!!.visibility = View.VISIBLE
                                        editDriverName!!.isEnabled = false
                                        editMobileNumber!!.isEnabled = false
                                        editVehicleNo!!.isEnabled = false
                                        editDriverAddress!!.isEnabled = false
                                        buttonSubmit!!.text = "Update"
                                        buttonSubmit!!.isEnabled = false
                                        // START_QUESTION_TIME = END_QUESTION_TIME
                                    }
                                    else -> {
                                        Utils.getSnackBar4K(
                                            requireActivity(),
                                            "Failed While submitting this Conveyor",
                                            constraintLayout!!
                                        )
                                    }
                                }
                            }
                            Status.ERROR -> {

                                Utils.getSnackBar4K(
                                    requireActivity(),
                                    "Please try again after sometime",
                                    constraintLayout!!
                                )
                            }
                            Status.LOADING -> {
                                Log.i(TAG,"loading")
                            }
                        }
                    }
                })


        }
    }

    private fun intiFunction() {
        conveyorViewModel.getConveyorGet(STUDENTID,CLASSID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            var conveyor = response.conveyor
                            for(i in conveyor.indices) {
                                editDriverName!!.setText(conveyor[i].cONVEYORSNAME)
                                editMobileNumber!!.setText(conveyor[i].cONVEYORSMOBILE)
                                editVehicleNo!!.setText(conveyor[i].cONVEYORSVEHICLENO)
                                editDriverAddress!!.setText(conveyor[i].cONVEYORSADDRESS)
                            }

                            if(conveyor.isNotEmpty()){
                                extendedFAB!!.visibility = View.VISIBLE
                                editDriverName!!.isEnabled = false
                                editMobileNumber!!.isEnabled = false
                                editVehicleNo!!.isEnabled = false
                                editDriverAddress!!.isEnabled = false
                                buttonSubmit!!.text = "Update"
                                buttonSubmit!!.isEnabled = false

                            }else{
                                extendedFAB!!.visibility = View.GONE
                                editDriverName!!.isEnabled = true
                                editDriverName!!.requestFocus()
                                editMobileNumber!!.isEnabled = true
                                editVehicleNo!!.isEnabled = true
                                editDriverAddress!!.isEnabled = true
                                buttonSubmit!!.text = "Create"
                                buttonSubmit!!.isEnabled = true
                                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })

    }

}