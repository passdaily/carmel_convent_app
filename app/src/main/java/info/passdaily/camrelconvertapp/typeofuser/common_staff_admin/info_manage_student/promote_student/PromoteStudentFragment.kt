package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.promote_student

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentPromoteStudentBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.home.HomeFragmentStaff
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.manage_guardian.ManageGuardianFragment
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.RequestBody

@Suppress("DEPRECATION")
class PromoteStudentFragment : Fragment() {

    var TAG = "PromoteStudentFragment"
    private lateinit var promoteStudentViewModel: PromoteStudentViewModel
    private var _binding: FragmentPromoteStudentBinding? = null
    private val binding get() = _binding!!

    var aCCADEMICID = 0
    var cLASSID = 0

    var aCCADEMICIDTO = 0
    var cLASSIDTO = 0


    var isWorking= false
    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()



    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null

    var spinnerAcademicTo : AppCompatSpinner? = null
    var spinnerClassTo : AppCompatSpinner? = null
    var toolBarClickListener : ToolBarClickListener? = null

    var constraintLayoutContent : ConstraintLayout? = null


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
        toolBarClickListener?.setToolbarName("Promote Student")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        promoteStudentViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[PromoteStudentViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentPromoteStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraintLayoutContent = binding.constraintLayoutContent

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass

        spinnerAcademicTo = binding.spinnerAcademicTo
        spinnerClassTo  = binding.spinnerClassTo

        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                aCCADEMICID = getYears[position].aCCADEMICID
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        spinnerClass?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                cLASSID = getClass[position].cLASSID
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        spinnerAcademicTo?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                aCCADEMICIDTO = getYears[position].aCCADEMICID
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        spinnerClassTo?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                cLASSIDTO = getClass[position].cLASSID
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.buttonSubmit.setOnClickListener {

//  Get_promote_studentURL=Global.url+"ManageStudents/PromoteStudents?AccademicIdFrom="+froms_aid+"&ClassIdFrom="+fromscid+
// "&AccademicIdTo="+tos_aid+"&ClassIdTo="+toscid;

            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure want to Promote Student?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->

                    getPromoteStudent()
                }
                .setNegativeButton(
                    "No"
                ) { dialog, _ -> //  Action for 'NO' Button
                    dialog.cancel()
                }
            //Creating dialog box
            val alert = builder.create()
            //Setting the title manually
            alert.setTitle("Promote Student")
            alert.show()
            val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            buttonbackground.setTextColor(Color.BLACK)
            val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            buttonbackground1.setTextColor(Color.BLACK)
        }

        initFunction()



        var buttonCancel = binding.buttonCancel
        buttonCancel.setOnClickListener {
            var fragmentManager = requireActivity().supportFragmentManager
            var fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, HomeFragmentStaff()).commit()
            true
        }
    }

    private fun initFunction() {
        promoteStudentViewModel.getYearClassExam(adminId, schoolId )
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getYears = response.years as ArrayList<GetYearClassExamModel.Year>
                            var years = Array(getYears.size){""}
                            for (i in getYears.indices) {
                                years[i] = getYears[i].aCCADEMICTIME
                            }
                            if (spinnerAcademic != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademic?.adapter = adapter
                            }
                            if (spinnerAcademicTo != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademicTo?.adapter = adapter
                            }


                            getClass = response.classList as java.util.ArrayList<GetYearClassExamModel.Class>
                            var classX = Array(getClass.size){""}
                            for (i in getClass.indices) {
                                classX[i] = getClass[i].cLASSNAME
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    classX
                                )
                                spinnerClass?.adapter = adapter
                            }

                            if (spinnerClassTo != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    classX
                                )
                                spinnerClassTo?.adapter = adapter
                            }

                            Log.i(TAG,"initFunction SUCCESS")

                        }
                        Status.ERROR -> {
                            Log.i(TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"initFunction LOADING")
                        }
                    }
                }
            })
    }


    private fun getPromoteStudent() {
        promoteStudentViewModel.getPromoteStudent(aCCADEMICID,cLASSID,
            aCCADEMICIDTO,cLASSIDTO)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "INSERTED" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Students Promoted Successfully", constraintLayoutContent!!)
                                    initFunction()
                                }
                                Utils.resultFun(response) == "UPDATED" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Students Promotion Updated", constraintLayoutContent!!)
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Students Promotion Failed", constraintLayoutContent!!)
                                }
                            }
                            Log.i(TAG, "getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            progressStop()
                            Log.i(TAG, "getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG, "getSubjectList LOADING")
                        }
                    }
                }
            })
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
