package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputEditText
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
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.quick_regional_message.BottomSheetUpdateRegionalMessage
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.bottom_sheet.BottomSheetCreateStudyMaterial
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.bottom_sheet.BottomSheetUpdateStudyMaterial
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener

@Suppress("DEPRECATION")
class StudyMaterialStaffFragment : Fragment(),MaterialListener {

    var TAG = "StudyMaterialStaffFragment"
    private lateinit var studyMaterialStaffViewModel: StudyMaterialStaffViewModel
    private var _binding: FragmentObjectiveExamListBinding? = null
    private val binding get() = _binding!!

    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0
    var toolBarClickListener : ToolBarClickListener? = null

    lateinit var bottomSheetCreateStudyMaterial: BottomSheetCreateStudyMaterial
    lateinit var bottomSheetUpdateStudyMaterial: BottomSheetUpdateStudyMaterial

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0
    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()

    var getMaterialList = ArrayList<MaterialListStaffModel.Material>()

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
        toolBarClickListener?.setToolbarName("Manage Study Material")
        // Inflate the layout for this fragment
      //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID
        // Inflate the layout for this fragment
        studyMaterialStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[StudyMaterialStaffViewModel::class.java]


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
//        if(resources.getBoolean(R.bool.is_tab)) {
            recyclerViewVideo?.layoutManager = GridLayoutManager(requireActivity(),2)
//        } else {
//            recyclerViewVideo?.layoutManager = LinearLayoutManager(requireActivity())
//        }



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
                getMaterialListStaff(aCCADEMICID,cLASSID,sUBJECTID,schoolId)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        initFunction()

        var fab = binding.fab
        fab.setOnClickListener {
//            val dialog1 = CreateStudyMaterialDialog(this)
//            val transaction = requireActivity().supportFragmentManager.beginTransaction()
//            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
//            dialog1.show(transaction, CreateStudyMaterialDialog.TAG)

            bottomSheetCreateStudyMaterial = BottomSheetCreateStudyMaterial(this,getYears,getClass,getSubject)
            bottomSheetCreateStudyMaterial.show(requireActivity().supportFragmentManager, "TAG")
        }

        bottomSheetCreateStudyMaterial = BottomSheetCreateStudyMaterial()
        bottomSheetUpdateStudyMaterial = BottomSheetUpdateStudyMaterial()
    }


    fun initFunction() {
        studyMaterialStaffViewModel.getYearClassExam(adminId,schoolId)
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
                            Log.i(TAG,"initFunction ERROR")
                            constraintEmpty?.visibility = View.VISIBLE
                            if(isAdded) {
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text = resources.getString(R.string.no_internet)
                            }
                        }
                        Status.LOADING -> {
                            constraintEmpty?.visibility = View.VISIBLE
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
        studyMaterialStaffViewModel.getSubjectStaff(cLASSID,adminId)
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
                            getMaterialList = ArrayList<MaterialListStaffModel.Material>()
                            recyclerViewVideo?.adapter = StudyMaterialAdapter(
                                this,
                                getMaterialList,
                                requireActivity(),
                                TAG
                            )

                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    private fun getMaterialListStaff(aCCADEMICID: Int, cLASSID: Int, sUBJECTID: Int,schoolId :Int) {
        studyMaterialStaffViewModel.getMaterialListStaff(aCCADEMICID,cLASSID,sUBJECTID,schoolId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getMaterialList = response.materialList as ArrayList<MaterialListStaffModel.Material>
                            if(getMaterialList.isNotEmpty()){
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewVideo!!.adapter =
                                        StudyMaterialAdapter(
                                            this,
                                            getMaterialList,
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
                            getMaterialList = ArrayList<MaterialListStaffModel.Material>()
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


    class StudyMaterialAdapter(
        var materialListener: MaterialListener,
        var materialList: ArrayList<MaterialListStaffModel.Material>,
        var mContext: Context,
        var TAG: String) : RecyclerView.Adapter<StudyMaterialAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var cardViewStatus : CardView = view.findViewById(R.id.cardViewStatus)
            var textSubjectName: TextView = view.findViewById(R.id.textSubjectName)
            var imageViewSubject : ImageView  = view.findViewById(R.id.imageViewSubject)

            var textTotalFiles: TextView = view.findViewById(R.id.textTotalFiles)
//            var textViewDate: TextView = view.findViewById(R.id.textViewDate)
            var textViewTitle : TextView = view.findViewById(R.id.textViewTitle)
            var textViewClass  : TextView = view.findViewById(R.id.textViewClass)
//            var textViewDesc : TextView = view.findViewById(R.id.textViewDesc)
            var detailsConstraint : ConstraintLayout = view.findViewById(R.id.detailsConstraint)
            var imageViewMore  : ImageView = view.findViewById(R.id.imageViewMore)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.study_material_staff_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textSubjectName.text =materialList[position].sUBJECTNAME
            holder.textViewClass.text = materialList[position].cLASSNAME
            holder.textTotalFiles.text = "Files : ${materialList[position].tOTALFILE}"

//            if(!materialList[position].sTUDYMETERIALDATE.isNullOrBlank()) {
//                val date: Array<String> = materialList[position].sTUDYMETERIALDATE.split("T".toRegex()).toTypedArray()
//                val dddd: Long = Utils.longconversion(date[0] +" "+date[1])
//                holder.textViewDate.text = "Date : ${Utils.formattedDateWords(dddd)}"
//            }else{
//                holder.textViewDate.text = "No Date"
//            }

            when (materialList[position].sUBJECTICON) {
                "English.png" -> {
                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_english)
                        .into(holder.imageViewSubject)
                }
                "Chemistry.png" -> {
                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_chemistry)
                        .into(holder.imageViewSubject)
                }
                "Biology.png" -> {
                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_biology)
                        .into(holder.imageViewSubject)
                }
                "Maths.png" -> {

                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_maths)
                        .into(holder.imageViewSubject)
                }
                "Hindi.png" -> {

                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_hindi)
                        .into(holder.imageViewSubject)
                }
                "Physics.png" -> {
                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_physics)
                        .into(holder.imageViewSubject)
                }
                "Malayalam.png" -> {
                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_malayalam)
                        .into(holder.imageViewSubject)
                }
                "Arabic.png" -> {
                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_arabic)
                        .into(holder.imageViewSubject)
                }
                "Accountancy.png" -> {
                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_accoundancy)
                        .into(holder.imageViewSubject)
                }
                "Social.png" -> {
                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_social)
                        .into(holder.imageViewSubject)
                }
                "Economics.png" -> {
                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_economics)
                        .into(holder.imageViewSubject)
                }
                "BasicScience.png" -> {
                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_biology)
                        .into(holder.imageViewSubject)
                }
                "Computer.png" -> {
                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_computer)
                        .into(holder.imageViewSubject)
                }
                "General.png" -> {
                    Glide.with(mContext)
                        .load(R.drawable.ic_folder_general)
                        .into(holder.imageViewSubject)
                }
            }


            holder.textViewTitle.text =materialList[position].sTUDYMETERIALTITLE
          //  holder.textViewDesc.text =materialList[position].sTUDYMETERIALDESCRIPTION

            holder.imageViewMore.setOnClickListener(View.OnClickListener {
                val popupMenu = PopupMenu(mContext, holder.imageViewMore)
                popupMenu.inflate(R.menu.video_adapter_menu)
                popupMenu.menu.findItem(R.id.menu_edit).icon = mContext.resources.getDrawable(R.drawable.ic_icon_edit)
                popupMenu.menu.findItem(R.id.menu_report).icon = mContext.resources.getDrawable(R.drawable.ic_icon_about_gray)
                popupMenu.menu.findItem(R.id.menu_video).icon = mContext.resources.getDrawable(R.drawable.ic_icon_close)
                popupMenu.menu.findItem(R.id.menu_offline_video).icon = mContext.resources.getDrawable(R.drawable.ic_icon_delete_gray)
                popupMenu.menu.findItem(R.id.menu_open).icon = mContext.resources.getDrawable(R.drawable.ic_icon_download)
                popupMenu.menu.findItem(R.id.menu_download).icon = mContext.resources.getDrawable(R.drawable.ic_icon_download)

                popupMenu.menu.findItem(R.id.menu_open).isVisible = false
                popupMenu.menu.findItem(R.id.menu_download).isVisible = false
                popupMenu.menu.findItem(R.id.menu_report).isVisible = false
                popupMenu.menu.findItem(R.id.menu_offline_video).isVisible = false
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit -> {
                            materialListener.onUpdateClickListener(materialList[position])
                            true
                        }
                        R.id.menu_video -> {
                            val builder = AlertDialog.Builder(mContext)
                            builder.setMessage("Are you sure want to delete chapter?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { _, _ ->
                                    materialListener.onDeleteClickListener("StudyMaterial/MaterialDrop",materialList[position].sTUDYMETERIALID)
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

            holder.detailsConstraint.setOnClickListener {
                val intent = Intent(mContext, StudyMaterialDetailsActivity::class.java)
                intent.putExtra("STUDY_MATERIAL_ID", materialList[position].sTUDYMETERIALID)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent)
            }


        }

        override fun getItemCount(): Int {
            return materialList.size
        }

    }

    override fun onCreateClick(message: String, flagPos : Int) {
        Log.i(TAG,"onCreateClick")
        if(flagPos == 1)bottomSheetCreateStudyMaterial.dismiss()
        else if(flagPos == 2) bottomSheetUpdateStudyMaterial.dismiss()
        initFunction()
        Utils.getSnackBarGreen(requireActivity(),message,constraintLayoutContent!!)

    }

    override fun onFailedClick(message: String) {
        Utils.getSnackBar4K(requireActivity(),message,constraintLayoutContent!!)
    }

    override fun OnValidation(editText: TextInputEditText, message: String) : Boolean {
        return studyMaterialStaffViewModel.validateField(editText, message,requireActivity(),constraintLayoutContent!!)
    }

    override fun onUpdateClickListener(materialList: MaterialListStaffModel.Material) {
//        val dialog1 = UpdateStudyMaterialDialog(this,materialList)
//        val transaction = requireActivity().supportFragmentManager.beginTransaction()
//        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
//        dialog1.show(transaction, UpdateStudyMaterialDialog.TAG)
        bottomSheetUpdateStudyMaterial = BottomSheetUpdateStudyMaterial(this,getYears,getClass,getSubject,materialList)
        bottomSheetUpdateStudyMaterial.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onDeleteClickListener(url: String, materialId: Int) {

        //OnlineVideo/ChapterDelete?ChapterId=

        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["MaterialId"] = materialId
        studyMaterialStaffViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "1" -> {
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

interface MaterialListener{
    fun onCreateClick(message: String, flagPos :Int)

    fun onFailedClick(message: String)

    fun OnValidation(editText: TextInputEditText, message: String): Boolean

    fun onUpdateClickListener(materialList: MaterialListStaffModel.Material)

    fun onDeleteClickListener(url: String, materialId: Int)
}