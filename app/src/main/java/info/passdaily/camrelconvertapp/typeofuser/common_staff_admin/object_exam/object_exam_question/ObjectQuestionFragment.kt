package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.object_exam_question

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentObjectQuestionBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.downloader.Error
import info.passdaily.camrelconvertapp.services.downloader.OnDownloadListener
import info.passdaily.camrelconvertapp.services.downloader.PRDownloader
import info.passdaily.camrelconvertapp.services.downloader.StatusD
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.object_exam.ObjectiveExamStaffViewModel
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import java.io.File
import java.util.HashMap

@Suppress("DEPRECATION")
class ObjectQuestionFragment : Fragment(),ObjQuestionListener {

    var TAG = "ObjectQuestionFragment"
    private lateinit var objectiveExamStaffViewModel: ObjectiveExamStaffViewModel

    private var _binding: FragmentObjectQuestionBinding? = null
    private val binding get() = _binding!!

    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()
    var getObjectiveExam = ArrayList<ObjExamListStaffModel.OnlineExam>()

    var getQuestionOptionList = ArrayList<QuestionOptionsListModel.Question>()

    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0
    var oEXAMID = 0

    var toolBarClickListener : ToolBarClickListener? = null
    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var schoolId = 0

    var spinnerAcademic : AppCompatSpinner? = null
    var spinnerClass : AppCompatSpinner? = null
    var spinnerSubject : AppCompatSpinner? = null
    var spinnerExam : AppCompatSpinner? = null


    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var recyclerViewVideo : RecyclerView? = null

    private var readPermission = false
    private var writePermission = false

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    var pb: ProgressDialog? = null

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
        toolBarClickListener?.setToolbarName("Objective Question")

        // Inflate the layout for this fragment
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        schoolId = user[0].SCHOOL_ID
        // Inflate the layout for this fragment
        objectiveExamStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ObjectiveExamStaffViewModel::class.java]


        _binding = FragmentObjectQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pb = ProgressDialog(requireActivity())
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Downloading...")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)

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

        spinnerExam = binding.spinnerExam

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

        spinnerExam?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                oEXAMID = getObjectiveExam[position].oEXAMID
                getExamQuestion(aCCADEMICID,cLASSID,sUBJECTID,oEXAMID)

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }

        var fab = binding.fab
        fab.setOnClickListener {

            if(objectiveExamStaffViewModel.validateFieldQuestion(sUBJECTID,
                    "Create Subject for select class",requireActivity(),constraintLayoutContent!!)
                &&objectiveExamStaffViewModel.validateFieldQuestion(oEXAMID,
                    "Create Exam for select subject",requireActivity(),constraintLayoutContent!!)){
//                val dialog1 = CreateObjectiveQuestion(this,aCCADEMICID,cLASSID,sUBJECTID,oEXAMID)
//                val transaction = requireActivity().supportFragmentManager.beginTransaction()
//                transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
//                dialog1.show(transaction, CreateObjectiveQuestion.TAG)
            }

        }

        initFunction()


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
                            shimmerViewContainer?.visibility = View.GONE
//                            constraintEmpty?.visibility = View.VISIBLE
//                            if(isAdded) {
//                                Glide.with(this)
//                                    .load(R.drawable.ic_empty_progress_report)
//                                    .into(imageViewEmpty!!)
//
//                                textEmpty?.text = resources.getString(R.string.no_internet)
//                            }
                            Log.i(TAG,"initFunction ERROR")
                        }
                        Status.LOADING -> {
                            shimmerViewContainer?.visibility = View.VISIBLE
//                            constraintEmpty?.visibility = View.VISIBLE
//                         //  getObjectiveExam = ArrayList<ObjExamListStaffModel.OnlineExam>()
//                            Glide.with(this)
//                                .load(R.drawable.ic_empty_progress_report)
//                                .into(imageViewEmpty!!)
//
//                            textEmpty?.text =  resources.getString(R.string.loading)
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
                            }else{
                                sUBJECTID = 0
                                getObjectiveExamList(aCCADEMICID,cLASSID,0)
                                shimmerViewContainer?.visibility = View.GONE
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
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
                            shimmerViewContainer?.visibility = View.GONE
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
//                            shimmerViewContainer?.visibility = View.VISIBLE
                            getSubject = ArrayList<SubjectsModel.Subject>()
//                            getObjectiveExam = ArrayList<ObjExamListStaffModel.OnlineExam>()
//                            recyclerViewVideo?.adapter =
//                                ObjectiveExamStaffFragment.ObjectiveExamAdapter(
//                                    this,
//                                    getObjectiveExam,
//                                    requireActivity(),
//                                    TAG
//                                )

                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    fun getObjectiveExamList(aCCADEMICID: Int, cLASSID: Int, sUBJECTID: Int){
        objectiveExamStaffViewModel.getOnlineExamListStaff(
            aCCADEMICID,
            cLASSID,
            sUBJECTID,
            schoolId
        )
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            getObjectiveExam = response.onlineExamList as ArrayList<ObjExamListStaffModel.OnlineExam>
                            var exam = Array(getObjectiveExam.size){""}
                            if(exam.isNotEmpty()){
                                for (i in getObjectiveExam.indices) {
                                    exam[i] = getObjectiveExam[i].oEXAMNAME
                                }
                            }else{
                                oEXAMID = 0
                                shimmerViewContainer?.visibility = View.GONE
                                recyclerViewVideo?.visibility = View.GONE
                                constraintEmpty?.visibility = View.VISIBLE
                                Glide.with(this)
                                    .load(R.drawable.ic_empty_progress_report)
                                    .into(imageViewEmpty!!)

                                textEmpty?.text =  resources.getString(R.string.no_results)
                            }
                            if (spinnerExam != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    exam
                                )
                                spinnerExam?.adapter = adapter
                            }

                            Log.i(TAG,"getSubjectList SUCCESS")
                        }
                        Status.ERROR -> {
                            shimmerViewContainer?.visibility = View.GONE
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            getObjectiveExam = ArrayList<ObjExamListStaffModel.OnlineExam>()
//                            shimmerViewContainer?.visibility = View.VISIBLE
//                            getSubject = ArrayList<SubjectsModel.Subject>()
//                            getObjectiveExam = ArrayList<ObjExamListStaffModel.OnlineExam>()
//                            recyclerViewVideo?.adapter =
//                                ObjectiveExamStaffFragment.ObjectiveExamAdapter(
//                                    this,
//                                    getObjectiveExam,
//                                    requireActivity(),
//                                    TAG
//                                )

                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
    }

    private fun getExamQuestion(aCCADEMICID: Int, cLASSID: Int, sUBJECTID: Int, oEXAMID: Int) {
        objectiveExamStaffViewModel.getObjQuestionOptionListStaff(aCCADEMICID,cLASSID,sUBJECTID,oEXAMID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            getQuestionOptionList= response.questionList as ArrayList<QuestionOptionsListModel.Question>
                            var getOptionList= response.optionList as ArrayList<QuestionOptionsListModel.Option>
                            if(getQuestionOptionList.isNotEmpty()){
                                for(i in getQuestionOptionList.indices){
                                    var options = ArrayList<QuestionOptionsListModel.Option>()
                                    for (opt in getOptionList.indices){
                                        if(getQuestionOptionList[i].qUESTIONID == getOptionList[opt].qUESTIONID){
                                            options.add(getOptionList[opt])
                                            getQuestionOptionList[i].optionList = options
                                        }
                                    }
                                }
                                recyclerViewVideo?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    recyclerViewVideo!!.adapter =
                                        ObjectiveExamAdapter(
                                            this@ObjectQuestionFragment,
                                            this,
                                            getQuestionOptionList,
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
                            getQuestionOptionList = ArrayList<QuestionOptionsListModel.Question>()
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
        var objectQuestionFragment: ObjectQuestionFragment,
        var objQuestionListener: ObjQuestionListener,
        var questionOptionList : ArrayList<QuestionOptionsListModel.Question>,
        var context : Context,
        var TAG: String) : RecyclerView.Adapter<ObjectiveExamAdapter.ViewHolder>() {
        var downLoadPos = 0
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
            var textViewQuestion: TextView = view.findViewById(R.id.textViewQuestion)
            var imageViewMore  : ImageView = view.findViewById(R.id.imageViewMore)
            var recyclerView  : RecyclerView = view.findViewById(R.id.recyclerView)
            var imageViewFile  : ImageView = view.findViewById(R.id.imageViewFile)
            var imageMore  : ImageView = view.findViewById(R.id.imageMore) /////Download and open icon in list
            var imageViewFileCon : ConstraintLayout = view.findViewById(R.id.imageViewFileCon)
            var progressDialog = ProgressDialog(context)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.object_question_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            Log.i(TAG,"oEXAMNAME ${questionOptionList[position].oEXAMNAME}")
            Log.i(TAG,"qUESTIONTITLE ${questionOptionList[position].qUESTIONTITLE}")
            Log.i(TAG,"qUESTIONCONTENT ${questionOptionList[position].qUESTIONCONTENT}")
            Log.i(TAG,"qUESTIONTYPEID ${questionOptionList[position].qUESTIONTYPEID}")

            holder.textViewTitle.text = questionOptionList[position].oEXAMNAME
            holder.textViewQuestion.text = "${position+1}. ${questionOptionList[position].qUESTIONTITLE}"

           // Log.i(TAG,"optionList ${questionOptionList[position].optionList}")
            holder.recyclerView.layoutManager =  GridLayoutManager(context,2)
            holder.recyclerView.adapter = OptionAdapter(questionOptionList[position].optionList,context)

            if(questionOptionList[position].qUESTIONCONTENT.isNotEmpty()) {
                val path: String = questionOptionList[position].qUESTIONCONTENT
                val mFile = java.io.File(path)
                Log.i(TAG,"File  ${Global.event_url + "/OExamFile/" + path}")
                if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                    || mFile.toString().contains(".png") || mFile.toString()
                        .contains(".JPG") || mFile.toString().contains(".JPEG")
                    || mFile.toString().contains(".PNG")
                ) {
                    holder.imageViewFileCon.visibility = View.VISIBLE
                    // JPG file
                    Glide.with(context)
                        .load(Global.event_url + "/OExamFile/" + path)
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_gallery)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewFile)
                }else if (mFile.toString().contains(".mp3") || mFile.toString()
                        .contains(".wav") || mFile.toString().contains(".ogg")
                    || mFile.toString().contains(".m4a") || mFile.toString()
                        .contains(".aac") || mFile.toString().contains(".wma") ||
                    mFile.toString().contains(".MP3") || mFile.toString()
                        .contains(".WAV") || mFile.toString().contains(".OGG")
                    || mFile.toString().contains(".M4A") || mFile.toString()
                        .contains(".AAC") || mFile.toString().contains(".WMA")
                ) {
                    holder.imageViewFileCon.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() // .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_voice)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewFile)
                } else {
                    holder.imageViewFileCon.visibility = View.VISIBLE
//                    Glide.with(context)
//                        .load(Global.event_url + "/OExamFile/" +path)
//                        .apply(
//                            RequestOptions.centerCropTransform()
//                                .dontAnimate() //   .override(imageSize, imageSize)
//                                .placeholder(R.drawable.ic_file_video)
//                        )
//                        .thumbnail(0.5f)
//                        .into(holder.imageViewFile)

                    Glide.with(context)
                        .load(Global.event_url + "/OExamFile/" + path)
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_video_library)
                        )
                        .priority(Priority.HIGH)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .thumbnail(0.5f)
                        .into(holder.imageViewFile)
                }
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
                            objQuestionListener.onUpdateClickListener(questionOptionList[position],questionOptionList[position].optionList)
                            true
                        }
                        R.id.menu_video -> {
                            val builder = AlertDialog.Builder(context)
                            builder.setMessage("Are you sure want to delete exam?")
                                .setCancelable(false)
                                .setPositiveButton("Yes") { _, _ ->
                                    objQuestionListener.onDeleteClickListener("OnlineExam/QuestionDelete",questionOptionList[position].qUESTIONID)
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


            //////this imageview for total list file download and open
            holder.imageMore.setOnClickListener {

                val popupMenu = PopupMenu(context, holder.imageMore)
                popupMenu.inflate(R.menu.video_adapter_menu)
                popupMenu.menu.findItem(R.id.menu_edit).icon = context.resources.getDrawable(R.drawable.ic_icon_edit)
                popupMenu.menu.findItem(R.id.menu_report).icon = context.resources.getDrawable(R.drawable.ic_icon_about_gray)
                popupMenu.menu.findItem(R.id.menu_video).icon = context.resources.getDrawable(R.drawable.ic_icon_close)
                popupMenu.menu.findItem(R.id.menu_offline_video).icon = context.resources.getDrawable(R.drawable.ic_icon_delete_gray)
                popupMenu.menu.findItem(R.id.menu_download).icon = context.resources.getDrawable(R.drawable.ic_icon_download)
                popupMenu.menu.findItem(R.id.menu_open).icon = context.resources.getDrawable(R.drawable.ic_icon_download)

                popupMenu.menu.findItem(R.id.menu_edit).isVisible = false
                popupMenu.menu.findItem(R.id.menu_report).isVisible = false
                popupMenu.menu.findItem(R.id.menu_video).isVisible = false
                popupMenu.menu.findItem(R.id.menu_offline_video).isVisible = false


                val file = File(Utils.getRootDirPath(context)!!)
                if (!file.exists()) {
                    file.mkdirs()
                } else {
                    Log.i(TAG, "Already existing")
                }

                val check = File(file.path +"/catch/staff/OExam/"+ questionOptionList[position].qUESTIONCONTENT)
                Log.i(TAG,"check $check")
                if (!check.isFile) {
                    popupMenu.menu.findItem(R.id.menu_download).isVisible = true
                    popupMenu.menu.findItem(R.id.menu_open).isVisible = false
                }else{
                    popupMenu.menu.findItem(R.id.menu_download).isVisible = false
                    popupMenu.menu.findItem(R.id.menu_open).isVisible = true
                }

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_download -> {
//                            materialDetailsListener.onUpdateClickListener(materialList[position])
                            if(objectQuestionFragment.requestPermission()) {
//                                holder.constraintDownload.visibility = View.VISIBLE
//                                holder.textViewPercentage.visibility = View.VISIBLE
                                if (StatusD.RUNNING === PRDownloader.getStatus(position)) {
                                    PRDownloader.pause(position)
                                }

                                if (StatusD.PAUSED === PRDownloader.getStatus(position)) {
                                    PRDownloader.resume(position)
                                }
                                downLoadPos = position

                                var fileUrl = Global.event_url+"/OExamFile/" + questionOptionList[position].qUESTIONCONTENT
                                var fileLocation = Utils.getRootDirPath(context) +"/catch/staff/OExam/"
                                //var fileLocation = Environment.getExternalStorageDirectory().absolutePath + "/Passdaily/File/"
                                var fileName = questionOptionList[position].qUESTIONCONTENT
                                Log.i(TAG, "fileUrl $fileUrl")
                                Log.i(TAG, "fileLocation $fileLocation")
                                Log.i(TAG, "fileName $fileName")

                                downLoadPos = PRDownloader.download(
                                    fileUrl, fileLocation, fileName
                                )
                                    .build()
                                    .setOnStartOrResumeListener {
                                        //  holder.imageViewClose.visibility = View.VISIBLE
                                    }
                                    .setOnPauseListener {
                                        //   holder.imageViewClose.visibility = View.VISIBLE
                                    }
                                    .setOnCancelListener {
                                        //  holder.imageViewClose.visibility = View.VISIBLE
                                        downLoadPos = 0
                                        //    holder.constraintDownload.visibility = View.GONE
                                        //   holder.textViewPercentage.text ="Loading... "
                                        //  holder.imageViewClose.visibility = View.GONE
                                        holder.progressDialog.dismiss()
                                        Utils.getSnackBar4K(context, "Download Cancelled", objectQuestionFragment.constraintLayoutContent!!)
                                        PRDownloader.cancel(downLoadPos)

                                    }
                                    .setOnProgressListener { progress ->
                                        val progressPercent: Long =
                                            progress.currentBytes * 100 / progress.totalBytes
//                                        holder.textViewPercentage.text = Utils.getProgressDisplayLine(
//                                            progress.currentBytes,
//                                            progress.totalBytes
//                                        )
                                        holder.progressDialog.setMessage("Downloading : ${Utils.getProgressDisplayLine(
                                            progress.currentBytes, progress.totalBytes)}")
                                        holder.progressDialog.show()
                                    }
                                    .start(object : OnDownloadListener {
                                        override fun onDownloadComplete() {
//                                            holder.constraintDownload.visibility = View.GONE
//                                            holder.textViewPercentage.visibility = View.GONE
                                            holder.progressDialog.dismiss()
                                            Utils.getSnackBarGreen(context, "Download Completed", objectQuestionFragment.constraintLayoutContent!!)
                                        }

                                        override fun onError(error: Error) {
                                            Log.i(TAG, "Error $error")
                                            holder.progressDialog.dismiss()
                                            Utils.getSnackBar4K(context, "$error", objectQuestionFragment.constraintLayoutContent!!)
//                                            Toast.makeText(
//                                                context,
//                                                "Some Error occured",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                            holder.constraintDownload.visibility = View.GONE
//                                            holder.textViewPercentage.visibility = View.GONE
                                        }
                                    })

                            }
                            true
                        }
                        R.id.menu_open -> {
                            if(objectQuestionFragment.requestPermission()) {
                                objQuestionListener.onOpenFileClick(questionOptionList[position].qUESTIONCONTENT)
                            }
                            true
                        }
                        else -> false
                    }

                }
                popupMenu.show()

            }


        }

        override fun getItemCount(): Int {
            return questionOptionList.size
        }



        class OptionAdapter(var optionList: List<QuestionOptionsListModel.Option>, context: Context) :
            RecyclerView.Adapter<OptionAdapter.ViewHolder>() {
            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                var imageOption: ImageView = view.findViewById(R.id.imageOption)
                var textOptionName: TextView = view.findViewById(R.id.textOptionName)
            }
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.option_adapter, parent, false)
                return ViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.textOptionName.text = optionList[position].oPTIONTEXT

                if(optionList[position].iSRIGHTOPTION == 1) {
                    holder.imageOption.setImageResource(R.drawable.ic_checked_black)
                }else if(optionList[position].iSRIGHTOPTION == 0) {
                    holder.imageOption.setImageResource(R.drawable.ic_check_outline_gray)
                }
            }

            override fun getItemCount(): Int {
                return optionList.size
            }

        }

    }

    override fun onCreateClick(message: String) {
        getExamQuestion(aCCADEMICID,cLASSID,sUBJECTID,oEXAMID)
    }

    override fun onUpdateClickListener(
        questionList: QuestionOptionsListModel.Question,
        optionList: ArrayList<QuestionOptionsListModel.Option>
    ) {
//        val dialog1 = UpdateObjectiveQuestion(this,questionList
//            ,optionList,aCCADEMICID,cLASSID,sUBJECTID)
//        val transaction = requireActivity().supportFragmentManager.beginTransaction()
//        transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
//        dialog1.show(transaction, UpdateObjectiveQuestion.TAG)
    }

    override fun onOpenFileClick(fILEPATHName: String) {
        Log.i(TAG, "fILEPATHName $fILEPATHName")

        // Create URI
        var dwload =
            File(Utils.getRootDirPath(requireActivity())!!+"/catch/staff/OExam/")

        if (!dwload.exists()) {
            dwload.mkdirs()
        } else {
            //  Toast.makeText(getActivity(), "Already existing", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Already existing")
        }

        val mFile = File("$dwload/$fILEPATHName")
        var pdfURI = FileProvider.getUriForFile(requireActivity(), requireActivity().packageName + ".provider", mFile)
        Log.i(TAG, "file $mFile")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = pdfURI
        try {
            if (mFile.toString().contains(".doc") || mFile.toString().contains(".DOC")) {
                // Word document
                intent.setDataAndType(pdfURI, "application/msword")
            } else if (mFile.toString().contains(".pdf") || mFile.toString().contains(".PDF")) {
                // PDF file
                intent.setDataAndType(pdfURI, "application/pdf")
            } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")
            ) {
                // Powerpoint file
                intent.setDataAndType(pdfURI, "application/vnd.ms-powerpoint")
            } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
            ) {
                // Excel file
                intent.setDataAndType(pdfURI, "application/vnd.ms-excel")
            } else if (mFile.toString().contains(".docx") || mFile.toString().contains(".DOCX")) {
                // docx file
                intent.setDataAndType(
                    pdfURI,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                )
            } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                || mFile.toString().contains(".png") || mFile.toString()
                    .contains(".JPG") || mFile.toString().contains(".JPEG")
                || mFile.toString().contains(".PNG")
            ) {
                // JPG file
                intent.setDataAndType(pdfURI, "image/*")
            } else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                // Text file
                intent.setDataAndType(pdfURI, "text/plain")
            } else if (mFile.toString().contains(".mp3") || mFile.toString()
                    .contains(".wav") || mFile.toString().contains(".ogg")
                || mFile.toString().contains(".m4a") || mFile.toString()
                    .contains(".aac") || mFile.toString().contains(".wma") ||
                mFile.toString().contains(".MP3") || mFile.toString()
                    .contains(".WAV") || mFile.toString().contains(".OGG")
                || mFile.toString().contains(".M4A") || mFile.toString()
                    .contains(".AAC") || mFile.toString().contains(".WMA")
            ) {
                intent.setDataAndType(pdfURI, "audio/*")

            } else if (mFile.toString().contains(".MP4") || mFile.toString().contains(".MOV")
                || mFile.toString().contains(".WMV") || mFile.toString()
                    .contains(".AVI") || mFile.toString().contains(".AVCHD") || mFile.toString()
                    .contains(".FLV")
                || mFile.toString().contains(".F4V")
                || mFile.toString().contains(".SWF")
                || mFile.toString().contains(".WEBM")
                || mFile.toString().contains(".HTML5")
                || mFile.toString().contains(".MKV")
            ) {
                // JPG file
                intent.setDataAndType(pdfURI, "video/*")
            }
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(intent)
        }catch(e : Exception){
            Log.i(TAG, "exception $e");
            Utils.getSnackBar4K(requireActivity(),"File format doesn't support",constraintLayoutContent!!)
        }
    }

    override fun onDeleteClickListener(url: String, qUESTIONID: Int) {
        //OnlineExam/QuestionDelete?QuestionId=
        val paramsMap: HashMap<String?, Int> = HashMap()
        paramsMap["QuestionId"] = qUESTIONID
        objectiveExamStaffViewModel.getCommonGetFuntion(url,paramsMap)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            when {
                                Utils.resultFun(response) == "0" -> {
                                     Utils.getSnackBarGreen(requireActivity(), "Question Deleted Successfully", constraintLayoutContent!!)
                                    onCreateClick("Deleted Successfully")
                                }
                                Utils.resultFun(response) == "1" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Failed while delete", constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime", constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            Log.i(UpdateObjectiveQuestion.TAG,"loading")
                        }
                    }
                }
            })
    }


    fun requestPermission(): Boolean {

        var hasReadPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        }else {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }


        val hasWritePermission = ContextCompat.checkSelfPermission(
            requireActivity(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermission = hasReadPermission
        writePermission = hasWritePermission || minSdk29

        val permissions = readPermission && writePermission


        val permissionToRequests = mutableListOf<String>()
        if (!writePermission) {
            permissionToRequests.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!readPermission) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_IMAGES)
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_VIDEO)
                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_AUDIO)
            }else {
                permissionToRequests.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionToRequests.isNotEmpty()) {
            permissionsLauncher.launch(permissionToRequests.toTypedArray())
        }

        return permissions
    }

}
interface ObjQuestionListener{
    fun onCreateClick(message: String)

    fun onUpdateClickListener(
        questionList: QuestionOptionsListModel.Question,
        optionList: ArrayList<QuestionOptionsListModel.Option>
    )

    fun onOpenFileClick(fILEPATHName: String,)

    fun onDeleteClickListener(url: String, qUESTIONID: Int)
}