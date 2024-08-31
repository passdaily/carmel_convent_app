package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentObjectiveExamListBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.parent.leave_enquires.EnquiryFragment
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.util.*

@Suppress("DEPRECATION")
class AssignmentStaffFragment : Fragment(),AssignmentListener {


    private lateinit var assignmentStaffViewModel: AssignmentStaffViewModel
    private var _binding: FragmentObjectiveExamListBinding? = null
    private val binding get() = _binding!!


    var TAG = "AssignmentStaffFragment"


    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0
    var toolBarClickListener : ToolBarClickListener? = null

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getClassList = ArrayList<GetYearClassExamModel.Class>()


    var getSubject = ArrayList<SubjectsModel.Subject>()

    var getAssignmentList = ArrayList<AssignmentListStaffModel.Assignment>()

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
//    var spinnerClass : TextView? = null
    var spinnerSubject : AppCompatSpinner? = null

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null


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
        toolBarClickListener?.setToolbarName("Manage Homework")
        // Inflate the layout for this fragment
      //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID
        // Inflate the layout for this fragment
        assignmentStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[AssignmentStaffViewModel::class.java]


        _binding = FragmentObjectiveExamListBinding.inflate(inflater, container, false)
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
        spinnerSubject = binding.spinnerSubject

        recyclerViewVideo = binding.recyclerViewVideo
        recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())


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
                getSubjectList(cLASSID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
//        spinnerClass?.setOnClickListener {
//
//            val builder = AlertDialog.Builder(requireActivity(),R.style.DialogTheme)
//            builder.setTitle("Choose Class")
//            builder.setMultiChoiceItems(listItems, checkedItems) { dialog, which, isChecked ->
//                Log.i(TAG,"checkedItems ${checkedItems.size}")
//                checkedItems[which] = isChecked
//              //  val currentItem: Array<String> = selectedItems[which]
//            }
//            builder.setCancelable(false)
//            builder.setPositiveButton(
//                "Apply"
//            ) { dialog: DialogInterface?, which: Int ->
//                var classNames= ""
//                classIds = ""
//                for (i in checkedItems.indices) {
//                    if (checkedItems[i]) {
//                        classNames += "${getClass[i].cLASSNAME}, "
//                        classIds += "${getClass[i].cLASSID},"
//                    }
//                }
//                if(classIds != "") {
//                    Log.i(TAG, "classIds ${classIds.substring(classIds.length - 1)}")
//                  //  val classes = classIds.substring(classIds.length - 1)
//                    spinnerClass?.text = classNames
//                    getSubjectList(classIds.substring(0,classIds.length - 1))
//                }else{
//                    Utils.getSnackBar4K(requireActivity(),"Select atleast one class",constraintLayoutContent)
//                }
//
//            }
//            builder.setNegativeButton(
//                "CANCEL"
//            ) { dialog: DialogInterface?, which: Int ->
//                spinnerClass?.text = "Click to Select Class"
//                    Arrays.fill(
//                    checkedItems,
//                    false
//                )
//            }
//
//            builder.create()
//
//            val alertDialog = builder.create()
//            alertDialog.show()
//            val buttonbackground: Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
//            buttonbackground.setTextColor(Color.BLACK)
//
//            val buttonbackground1: Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
//            buttonbackground1.setTextColor(Color.BLACK)
//        }


        spinnerSubject?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                sUBJECTID = getSubject[position].sUBJECTID
                getObjectiveExamList(aCCADEMICID,cLASSID,sUBJECTID)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        initFunction()

        var fab = binding.fab
        fab.setOnClickListener {
//            val intent = Intent(requireActivity(), CreateAssignmentActivity::class.java)
//            startActivity(intent)
            val dialog1 = CreateAssignmentDialog(this)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, CreateAssignmentDialog.TAG)
        }


    }


    private fun initFunction() {
        assignmentStaffViewModel.getYearClassExam(adminId,schoolId)
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

                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            if(isAdded){
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)
                                textEmpty?.text =  resources.getString(R.string.no_internet)
                            }
                            Log.i(TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            getAssignmentList = ArrayList<AssignmentListStaffModel.Assignment>()
                            Glide.with(this)
                                .load(R.drawable.ic_empty_progress_report)
                                .into(imageViewEmpty!!)

                            textEmpty?.text =  resources.getString(R.string.loading)
                            Log.i(TAG,"initFunction LOADING")
                        }
                    }
                }
            })
    }

    //{
    //    "ADMIN_ID" : 8,
    //
    //    "CLASS_ID" :"13,22"
    //}
    fun getSubjectList(cLASSID: Int){
//        val jsonObject = JSONObject()
//        try {
//            jsonObject.put("ADMIN_ID", adminId)
//            jsonObject.put("CLASS_ID", cLASSID)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//
//        Log.i(TAG,"jsonObject ${jsonObject.toString()}")
//
//        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
//
//        assignmentStaffViewModel.postSubjectStaff(accademicRe)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            getSubject = response.subjects as ArrayList<SubjectsModel.Subject>
//                            var subject = Array(getSubject.size){""}
//                            if(subject.isNotEmpty()){
//                                for (i in getSubject.indices) {
//                                    subject[i] = getSubject[i].sUBJECTNAME
//                                }
//                            }
//                            if (spinnerSubject != null) {
//                                val adapter = ArrayAdapter(
//                                    requireActivity(),
//                                    android.R.layout.simple_spinner_dropdown_item,
//                                    subject
//                                )
//                                spinnerSubject?.adapter = adapter
//                            }
//
//                            Log.i(TAG,"getSubjectList SUCCESS")
//                        }
//                        Status.ERROR -> {
//                            Log.i(TAG,"getSubjectList ERROR")
//                        }
//                        Status.LOADING -> {
//                            getSubject = ArrayList<SubjectsModel.Subject>()
//                            getAssignmentList = ArrayList<AssignmentListStaffModel.Assignment>()
//                            recyclerViewVideo?.adapter = AssignmentAdapter(
//                                this,
//                                getAssignmentList,
//                                requireActivity(),
//                                TAG
//                            )
//
//                            Log.i(TAG,"getSubjectList LOADING")
//                        }
//                    }
//                }
//            })


        assignmentStaffViewModel.getSubjectStaff(cLASSID,adminId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            getSubject = response.subjects as ArrayList<SubjectsModel.Subject>
                            var subject = Array(getSubject.size){""}
                            if(subject.isNotEmpty()){
                                for (i in getSubject.indices) {
                                    subject[i] = getSubject[i].sUBJECTNAME
                                }
                            }
                            if (spinnerSubject != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    subject
                                )
                                spinnerSubject?.adapter = adapter
                            }

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            getSubject = ArrayList<SubjectsModel.Subject>()
                            getAssignmentList = ArrayList<AssignmentListStaffModel.Assignment>()
                            recyclerViewVideo?.adapter = AssignmentAdapter(
                                this,
                                getAssignmentList,
                                requireActivity(),
                                TAG
                            )

                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    private fun getObjectiveExamList(aCCADEMICID: Int, cLASSID: Int, sUBJECTID: Int) {
        assignmentStaffViewModel.getAssignmentListStaff(aCCADEMICID,cLASSID,sUBJECTID,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getAssignmentList= response.assignmentList as ArrayList<AssignmentListStaffModel.Assignment>
                            if(getAssignmentList.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewVideo!!.adapter =
                                        AssignmentAdapter(
                                            this,
                                            getAssignmentList,
                                            requireActivity(),
                                            TAG
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
                            getAssignmentList = ArrayList<AssignmentListStaffModel.Assignment>()
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


    class AssignmentAdapter(
        var assignmentListener: AssignmentListener,
        var assignmentList: ArrayList<AssignmentListStaffModel.Assignment>,
        var context: Context,
        var TAG: String) : RecyclerView.Adapter<AssignmentAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
            var textViewDesc: TextView = view.findViewById(R.id.textViewDesc)
           // var textStartDate: TextView = view.findViewById(R.id.textStartDate)
          //  var textEndDate: TextView = view.findViewById(R.id.textEndDate)
          //  var textStartTime: TextView = view.findViewById(R.id.textStartTime)
          //  var textEndTime: TextView = view.findViewById(R.id.textEndTime)
            var textOutOfMark: TextView = view.findViewById(R.id.textOutOfMark)
            var textSubmission: TextView = view.findViewById(R.id.textSubmission)
            var imageViewMore : ImageView = view.findViewById(R.id.imageViewMore)
            var detailsConstraint : ConstraintLayout = view.findViewById(R.id.detailsConstraint)
            var textViewPublish : TextView = view.findViewById(R.id.textViewPublish)
           // var imageView : ImageView = view.findViewById(R.id.imageView)

           var textViewEndDate: TextView = view.findViewById(R.id.textViewEndDate)/////start date and time
            var cardViewStatus : CardView = view.findViewById(R.id.cardViewStatus)
            var textSubjectName: TextView = view.findViewById(R.id.textSubjectName)
            var imageViewSubject : ImageView  = view.findViewById(R.id.imageViewSubject)

        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.assignmentlist_staff_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textSubjectName.text =assignmentList[position].sUBJECTNAME
            holder.textViewTitle.text = assignmentList[position].aSSIGNMENTNAME
            holder.textViewDesc.text = assignmentList[position].aSSIGNMENTDESCRIPTION

            if(!assignmentList[position].sTARTDATE.isNullOrBlank()) {
                val date: Array<String> =
                    assignmentList[position].sTARTDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
//                holder.textStartDate.text = "Start Date : ${Utils.formattedDateWords(dddd)}"
//                holder.textStartTime.text = "${Utils.formattedTime(dddd)}"
                holder.textViewEndDate.text = "${Utils.formattedDateWords(dddd)} ${Utils.formattedTime(dddd)}"
            }

            if(!assignmentList[position].eNDDATE.isNullOrBlank()) {
                val date: Array<String> = assignmentList[position].eNDDATE.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
             //   holder.textEndDate.text = "End Date : ${Utils.formattedDateWords(dddd)}"
            //    holder.textEndTime.text = "${Utils.formattedTime(dddd)}"
            }

            holder.textOutOfMark.text = "Out Of Marks : ${assignmentList[position].aSSIGNMENTOUTOFFMARK}"
            holder.textSubmission.text = "Total Submission : ${assignmentList[position].tOTALSUBMIT}"


            when (assignmentList[position].sUBJECTICON) {
                "English.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_english))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_english))
                    Glide.with(context)
                        .load(R.drawable.ic_study_english)
                        .into(holder.imageViewSubject)
                }
                "Chemistry.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_chemistry))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_chemistry))
                    Glide.with(context)
                        .load(R.drawable.ic_study_chemistry)
                        .into(holder.imageViewSubject)
                }
                "Biology.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_bio))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_bio))
                    Glide.with(context)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.imageViewSubject)
                }
                "Maths.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_maths))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_maths))
                    Glide.with(context)
                        .load(R.drawable.ic_study_maths)
                        .into(holder.imageViewSubject)
                }
                "Hindi.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_hindi))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_hindi))
                    Glide.with(context)
                        .load(R.drawable.ic_study_hindi)
                        .into(holder.imageViewSubject)
                }
                "Physics.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_physics))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_physics))
                    Glide.with(context)
                        .load(R.drawable.ic_study_physics)
                        .into(holder.imageViewSubject)
                }
                "Malayalam.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_malayalam))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_malayalam))
                    Glide.with(context)
                        .load(R.drawable.ic_study_malayalam)
                        .into(holder.imageViewSubject)
                }
                "Arabic.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_arabic))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_arabic))
                    Glide.with(context)
                        .load(R.drawable.ic_study_arabic)
                        .into(holder.imageViewSubject)
                }
                "Accountancy.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_accounts))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_accounts))
                    Glide.with(context)
                        .load(R.drawable.ic_study_accountancy)
                        .into(holder.imageViewSubject)
                }
                "Social.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_social))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_social))
                    Glide.with(context)
                        .load(R.drawable.ic_study_social)
                        .into(holder.imageViewSubject)
                }
                "Economics.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_econonics))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_econonics))
                    Glide.with(context)
                        .load(R.drawable.ic_study_economics)
                        .into(holder.imageViewSubject)
                }
                "BasicScience.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_bio))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_bio))
                    Glide.with(context)
                        .load(R.drawable.ic_study_biology)
                        .into(holder.imageViewSubject)
                }
                "Computer.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_computer))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_computer))
                    Glide.with(context)
                        .load(R.drawable.ic_study_computer)
                        .into(holder.imageViewSubject)
                }
                "General.png" -> {
                    holder.cardViewStatus.setCardBackgroundColor(context.resources.getColor(R.color.color100_general))
                    holder.textSubjectName.setTextColor(context.resources.getColor(R.color.color_general))
                    Glide.with(context)
                        .load(R.drawable.ic_study_general)
                        .into(holder.imageViewSubject)
                }
            }
//            holder.detailsConstraint.setOnClickListener {
//                val intent = Intent(context, ObjExamDetailsActivity::class.java)
//                intent.putExtra("EXAM_ID", objectiveExam[position].oEXAMID)
//                intent.putExtra("ACCADEMIC_ID", objectiveExam[position].aCCADEMICID)
//                intent.putExtra("CLASS_ID", objectiveExam[position].cLASSID)
//                intent.putExtra("STATUS", objectiveExam[position].sTATUS)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent)
//            }


            if (assignmentList[position].sTATUS == 1) {
                holder.detailsConstraint.setBackgroundColor(context.resources.getColor(R.color.green_light100))
                holder.textViewPublish.setTextColor(context.resources.getColor(R.color.green_300))
                holder.textViewPublish.text = "Published"
            } else {
                holder.detailsConstraint.setBackgroundColor(context.resources.getColor(R.color.color100_physics))
                holder.textViewPublish.setTextColor(context.resources.getColor(R.color.color_physics))
                holder.textViewPublish.text = "Unpublish"
            }

            holder.imageViewMore.setOnClickListener(View.OnClickListener {
                val popupMenu = PopupMenu(context, holder.imageViewMore)
                popupMenu.inflate(R.menu.video_adapter_menu)
                popupMenu.menu.findItem(R.id.menu_edit).icon = context.resources.getDrawable(R.drawable.ic_icon_edit)
                popupMenu.menu.findItem(R.id.menu_report).icon = context.resources.getDrawable(R.drawable.ic_icon_about_gray)
                popupMenu.menu.findItem(R.id.menu_video).icon = context.resources.getDrawable(R.drawable.ic_icon_close)
                popupMenu.menu.findItem(R.id.menu_offline_video).icon = context.resources.getDrawable(R.drawable.ic_icon_delete_gray)
                popupMenu.menu.findItem(R.id.menu_download).icon = context.resources.getDrawable(R.drawable.ic_icon_download)
                popupMenu.menu.findItem(R.id.menu_open).icon = context.resources.getDrawable(R.drawable.ic_icon_download)

                popupMenu.menu.findItem(R.id.menu_open).isVisible = false
                popupMenu.menu.findItem(R.id.menu_download).isVisible = false
                popupMenu.menu.findItem(R.id.menu_offline_video).isVisible = false
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            assignmentListener.onUpdateClickListener(assignmentList[position])
                            true
                        }
                        R.id.menu_report -> {
                            val intent = Intent(context, AssignmentDetailStaffActivity::class.java)
                            intent.putExtra("ASSIGNMENT_ID", assignmentList[position].aSSIGNMENTID)
                            intent.putExtra("ACCADEMIC_ID", assignmentList[position].aCCADEMICID)
                            intent.putExtra("CLASS_ID", assignmentList[position].cLASSID)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent)
                            true
                        }
                        R.id.menu_video -> {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Are you sure want to delete exam?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { _, _ ->
                                    //Assignment/AssignmentDelete?AssignmentId=
                                    assignmentListener.onDeleteClickListener("Assignment/AssignmentDelete",assignmentList[position].aSSIGNMENTID)
                                }
                                .setNegativeButton(
                                    "No"
                                ) { dialog, _ -> //  Action for 'NO' Button
                                    dialog.cancel()
                                }
                            //Creating dialog box
                            val alert = builder.create()
                            //Setting the title manually
                            alert.setTitle("Delete")
                            alert.show()
                            val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                            buttonbackground.setTextColor(Color.BLACK)
                            val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                            buttonbackground1.setTextColor(Color.BLACK)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            })
        }

        override fun getItemCount(): Int {
            return assignmentList.size
        }

    }

    override fun onCreateClick(message: String) {
        Log.i(TAG,"onCreateClick")
        initFunction()
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun onUpdateClickListener(assignmentList: AssignmentListStaffModel.Assignment) {
        val dialog1 = UpdateAssignmentDialog(this,assignmentList)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.show(transaction, UpdateAssignmentDialog.TAG)
    }

    override fun onDeleteClickListener(url: String, assignmentId: Int) {
        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["AssignmentId"] = assignmentId
        assignmentStaffViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Exam Deleted Successfully", constraintLayoutContent!!)
                                    initFunction()
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Exam Deletion Failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }

    override fun onPublishClickListener(url: String, assignmentId: Int) {
        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["OexamId"] = assignmentId
        assignmentStaffViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "0" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Exam Deleted Successfully", constraintLayoutContent!!)
                                    initFunction()
                                }
                                else -> {
                                    Utils.getSnackBar4K(requireActivity(), "Exam Deletion Failed", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })
    }

}

interface AssignmentListener{
    fun onCreateClick(message: String)

    fun onUpdateClickListener(assignmentList: AssignmentListStaffModel.Assignment)

    fun onDeleteClickListener(url: String, assignmentId: Int)

    fun onPublishClickListener(url: String, assignmentId: Int)
}