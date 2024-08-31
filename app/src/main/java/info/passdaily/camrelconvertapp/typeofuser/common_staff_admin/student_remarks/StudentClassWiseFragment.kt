package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.student_remarks

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentManageGroupBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_group.GroupViewModel
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.RequestBody

@Suppress("DEPRECATION")
class StudentClassWiseFragment : Fragment() {

    var TAG = "ManagePublicMember"
    private lateinit var studentRemarkViewModel: StudentRemarkViewModel
    private var _binding: FragmentManageGroupBinding? = null
    private val binding get() = _binding!!

    var isWorking= false
    // http://demostaff.passdaily.in/ElixirApi/Group/GroupAdd
    var aCCADEMICID = 0
    var cLASSID = 0


    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId =0

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()


    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var toolBarClickListener : ToolBarClickListener? = null

    var getStudentDetails = ArrayList<StudentsClasswiseDetailsModel.Student>()
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
        toolBarClickListener?.setToolbarName("Student ClassWise")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID

        studentRemarkViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StudentRemarkViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentManageGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraintLayoutContent = binding.constraintLayoutContent
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        shimmerViewContainer = binding.shimmerViewContainer

        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClass

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())


        binding.accedemicText.text = requireActivity().resources.getText(R.string.select_year)
        binding.classText.text = requireActivity().resources.getText(R.string.select_class)

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
                getFinalList()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        initFunction()

        binding.fab.visibility = View.GONE
        binding.buttonSubmit.visibility = View.GONE

    }


    private fun initFunction() {
        studentRemarkViewModel.getYearClassExam(adminId, schoolId )
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


                            getClass = response.classList as ArrayList<GetYearClassExamModel.Class>
                            var classes = Array(getClass.size){""}
                            for (i in getClass.indices) {
                                classes[i] = getClass[i].cLASSNAME
                            }
                            if (spinnerClass != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    classes
                                )
                                spinnerClass?.adapter = adapter
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


    fun getFinalList(){
        studentRemarkViewModel.getStudentClasswise(cLASSID,aCCADEMICID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!

                            getStudentDetails = response.students as ArrayList<StudentsClasswiseDetailsModel.Student>
                            if(getStudentDetails.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewVideo!!.adapter =
                                        StudentDetailsAdapter(
                                            getStudentDetails,
                                            requireActivity()
                                        )
                                }
                            }else{
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }
                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            recyclerViewVideo?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerViewVideo?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getStudentDetails = ArrayList<StudentsClasswiseDetailsModel.Student>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }


    class StudentDetailsAdapter(
        var getStudentDetails: ArrayList<StudentsClasswiseDetailsModel.Student>,
        context: Context)
        : RecyclerView.Adapter<StudentDetailsAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textStudentName: TextView = view.findViewById(R.id.textStudentName)
            var textClassName: TextView = view.findViewById(R.id.textClassName)
            var textRollNo : TextView = view.findViewById(R.id.textRollNo)
            var textViewDob : TextView = view.findViewById(R.id.textViewDob)

            var textFatherName  : TextView = view.findViewById(R.id.textFatherName)
            var textMotherName  : TextView = view.findViewById(R.id.textMotherName)
            var textMobileNumber  : TextView = view.findViewById(R.id.textMobileNumber)
            var textGuardianName  : TextView = view.findViewById(R.id.textGuardianName)
            var textViewAddress  : TextView = view.findViewById(R.id.textViewAddress)
            var textGuardianNumber  : TextView = view.findViewById(R.id.textGuardianNumber)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.student_details_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textStudentName.text = getStudentDetails[position].sTUDENTFNAME
            holder.textClassName.text = "Class : ${getStudentDetails[position].cLASSNAME}"
            holder.textRollNo.text = "Roll.No : ${getStudentDetails[position].sTUDENTROLLNUMBER}"

            if(!getStudentDetails[position].sTUDENTDOB.isNullOrBlank()) {
                val date: Array<String> = getStudentDetails[position].sTUDENTDOB?.split("T".toRegex())!!.toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                holder.textViewDob.text = "D.O.B : ${Utils.formattedDateWords(dddd)}"
            }else{
                holder.textViewDob.text = "D.O.B : "
            }

            holder.textFatherName.text = "Father : ${getStudentDetails[position].sTUDENTFATHERNAME}"
            holder.textMotherName.text = "Mother : ${getStudentDetails[position].sTUDENTMOTHERNAME}"
            holder.textMobileNumber.text = "Contact.No : ${getStudentDetails[position].sTUDENTPHONENUMBER}"
            holder.textGuardianName.text = "Guardian : ${getStudentDetails[position].sTUDENTGUARDIANNAME}"

            if(!getStudentDetails[position].sTUDENTCADDRESS.isNullOrBlank())  {
                holder.textViewAddress.text = "Address : ${getStudentDetails[position].sTUDENTCADDRESS}"
            }else{
                holder.textViewAddress.text = "Address : "
            }

            holder.textGuardianNumber.text = "Guardian.No : ${getStudentDetails[position].sTUDENTGUARDIANNUMBER}"
        }

        override fun getItemCount(): Int {
           return getStudentDetails.size
        }

    }




}


