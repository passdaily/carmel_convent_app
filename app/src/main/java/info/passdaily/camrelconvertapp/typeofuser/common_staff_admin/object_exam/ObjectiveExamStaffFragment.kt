package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.PopupMenu
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
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener

@Suppress("DEPRECATION")
class ObjectiveExamStaffFragment : Fragment(),ObjectiveExamListener {

    var TAG = "ObjectiveExamStaffFragment"
    private lateinit var objectiveExamStaffViewModel: ObjectiveExamStaffViewModel
    private var _binding: FragmentObjectiveExamListBinding? = null
    private val binding get() = _binding!!

    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0
    var toolBarClickListener : ToolBarClickListener? = null

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()

    var getObjectiveExam = ArrayList<ObjExamListStaffModel.OnlineExam>()

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
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
        toolBarClickListener?.setToolbarName("Manage Objective Exam")
        // Inflate the layout for this fragment
      //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID
        // Inflate the layout for this fragment
        objectiveExamStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ObjectiveExamStaffViewModel::class.java]


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
            val dialog1 = CreateObjectiveExam(this)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
            dialog1.show(transaction, CreateObjectiveExam.TAG)
        }



    }


    private fun initFunction() {
        objectiveExamStaffViewModel.getYearClassExam(adminId,schoolId)
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
                            Log.i(TAG,"initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            if(isAdded) {
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text = resources.getString(R.string.no_internet)
                            }
                            Log.i(TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            constraintEmpty?.visibility = View.VISIBLE
                            getObjectiveExam = ArrayList<ObjExamListStaffModel.OnlineExam>()
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

    fun getSubjectList(cLASSID : Int){
        objectiveExamStaffViewModel.getSubjectStaff(cLASSID,adminId)
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
                            getObjectiveExam = ArrayList<ObjExamListStaffModel.OnlineExam>()
                            recyclerViewVideo?.adapter = ObjectiveExamAdapter(
                                this,
                                getObjectiveExam,
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
        objectiveExamStaffViewModel.getOnlineExamListStaff(aCCADEMICID,cLASSID,sUBJECTID,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getObjectiveExam= response.onlineExamList as ArrayList<ObjExamListStaffModel.OnlineExam>
                            if(getObjectiveExam.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewVideo!!.adapter =
                                        ObjectiveExamAdapter(
                                            this,
                                            getObjectiveExam,
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
                            getObjectiveExam = ArrayList<ObjExamListStaffModel.OnlineExam>()
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


    class ObjectiveExamAdapter(
        var objectiveExamListener: ObjectiveExamListener,
        var objectiveExam: ArrayList<ObjExamListStaffModel.OnlineExam>,
        var context : Context,
        var TAG: String) : RecyclerView.Adapter<ObjectiveExamAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
            var textViewDesc: TextView = view.findViewById(R.id.textViewDesc)
            var textStartDate: TextView = view.findViewById(R.id.textStartDate)
            var textEndDate: TextView = view.findViewById(R.id.textEndDate)
            var textStartTime: TextView = view.findViewById(R.id.textStartTime)
            var textEndTime: TextView = view.findViewById(R.id.textEndTime)
            var imageView : ImageView = view.findViewById(R.id.imageView)
            var detailsConstraint : ConstraintLayout = view.findViewById(R.id.detailsConstraint)
            var imageViewMore  : ImageView = view.findViewById(R.id.imageViewMore)
            var textViewClass : TextView = view.findViewById(R.id.textViewClass)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.objectiveexam_staff_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textViewTitle.text = objectiveExam[position].oEXAMNAME
            holder.textViewDesc.text = objectiveExam[position].sUBJECTNAME

            holder.textViewClass.text = "Class : ${objectiveExam[position].cLASSNAME}"

            if(!objectiveExam[position].sTARTTIME.isNullOrBlank()) {
                val date: Array<String> =
                    objectiveExam[position].sTARTTIME.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                holder.textStartDate.text = Utils.formattedDateWords(dddd)
                holder.textStartTime.text = Utils.formattedTime(dddd)
            }

            if(!objectiveExam[position].eNDTIME.isNullOrBlank()) {
                val date: Array<String> = objectiveExam[position].eNDTIME.split("T".toRegex()).toTypedArray()
                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
                holder.textEndDate.text = Utils.formattedDateWords(dddd)
                holder.textEndTime.text = Utils.formattedTime(dddd)
            }

            when (objectiveExam[position].sUBJECTICON) {
                "English.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_sub2_english)
                        .into(holder.imageView)
                }
                "Chemistry.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_sub2_chemistry)
                        .into(holder.imageView)
                }
                "Biology.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_sub2_biology)
                        .into(holder.imageView)
                }
                "Maths.png" -> {

                    Glide.with(context)
                        .load(R.drawable.ic_sub2_maths)
                        .into(holder.imageView)
                }
                "Hindi.png" -> {

                    Glide.with(context)
                        .load(R.drawable.ic_sub2_hindi)
                        .into(holder.imageView)
                }
                "Physics.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_sub2_physics)
                        .into(holder.imageView)
                }
                "Malayalam.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_sub2_malayalam)
                        .into(holder.imageView)
                }
                "Arabic.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_sub2_arabic)
                        .into(holder.imageView)
                }
                "Accountancy.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_sub2_accountancy)
                        .into(holder.imageView)
                }
                "Social.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_sub2_social)
                        .into(holder.imageView)
                }
                "Economics.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_sub2_economics)
                        .into(holder.imageView)
                }
                "BasicScience.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_sub2_biology)
                        .into(holder.imageView)
                }
                "Computer.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_sub2_computer)
                        .into(holder.imageView)
                }
                "General.png" -> {
                    Glide.with(context)
                        .load(R.drawable.ic_sub2_general)
                        .into(holder.imageView)
                }
            }


            holder.detailsConstraint.setOnClickListener {
                val intent = Intent(context, ObjExamDetailsActivity::class.java)
                intent.putExtra("EXAM_ID", objectiveExam[position].oEXAMID)
                intent.putExtra("ACCADEMIC_ID", objectiveExam[position].aCCADEMICID)
                intent.putExtra("CLASS_ID", objectiveExam[position].cLASSID)
                intent.putExtra("SUBJECT_ID", objectiveExam[position].sUBJECTID)
                intent.putExtra("STATUS", objectiveExam[position].sTATUS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent)
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
                popupMenu.menu.findItem(R.id.menu_report).isVisible = false
                popupMenu.menu.findItem(R.id.menu_offline_video).isVisible = false
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            objectiveExamListener.onUpdateClickListener(objectiveExam[position])
                            true
                        }
                        R.id.menu_video -> {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Are you sure want to delete exam?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { _, _ ->
                                    objectiveExamListener.onDeleteClickListener("OnlineExam/OnlineExamDelete",objectiveExam[position].oEXAMID)
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
            return objectiveExam.size
        }

    }

    override fun onCreateClick(message: String) {
        Log.i(TAG,"onCreateClick")
        initFunction()
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun onUpdateClickListener(onlineExam: ObjExamListStaffModel.OnlineExam) {
        val dialog1 = UpdateObjectiveExam(this,onlineExam)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
        dialog1.show(transaction, UpdateObjectiveExam.TAG)
    }

    override fun onDeleteClickListener(url: String, oEXAMID: Int) {
        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["OexamId"] = oEXAMID
        objectiveExamStaffViewModel.getCommonGetFuntion(url,paramsMap)
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

interface ObjectiveExamListener{
    fun onCreateClick(message: String)

    fun onUpdateClickListener(onlineExam: ObjExamListStaffModel.OnlineExam)

    fun onDeleteClickListener(url: String, oEXAMID: Int)
}