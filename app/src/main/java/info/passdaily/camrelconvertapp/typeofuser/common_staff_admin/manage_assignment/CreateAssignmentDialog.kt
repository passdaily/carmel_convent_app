package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityCreateAssigmentBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.permission.sdk29AndUp
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.FileList
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.zoom_scheduled.CreateZoomScheduled
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*


@Suppress("DEPRECATION")
class CreateAssignmentDialog : DialogFragment, AssignmentCreateListener {

    lateinit var assignmentListener: AssignmentListener

    companion object {
        var TAG = "CreateAssignmentDialog"
    }

    private var _binding: ActivityCreateAssigmentBinding? = null
    private val binding get() = _binding!!

    var toolbar: Toolbar? = null

    var SERVER_URL = "Assignment/UploadFiles"
    var fileNames = ArrayList<String>()

    var aCCADEMICID = 0
    var cLASSID = 0
    var sUBJECTID = 0
    var adminId = 0
    var adminRole = 0
    var schoolId = 0
    var typeStr = ""
    var getYears = ArrayList<GetYearClassExamModel.Year>()
    var getClass = ArrayList<GetYearClassExamModel.Class>()
    var getSubject = ArrayList<SubjectsModel.Subject>()

    var listItems = arrayOf("")
    var checkedItems = BooleanArray(0)
    var classIds= ""
    var classes = ""

    var fromStr = ""
    var toStr = ""

    var startDate = ""
    var endDate = ""
    var type = arrayOf("Published","Unpublished")

    var pb: ProgressDialog? = null

    var arrayListItems = ""

    var spinnerAcademic: AppCompatSpinner? = null
//    var spinnerClass: AppCompatSpinner? = null
    var spinnerClass : TextView? = null
    var spinnerSubject: AppCompatSpinner? = null
    var spinnerStatus: AppCompatSpinner? = null
    var selectChapter: TextInputEditText? = null
    var selectPages: TextInputEditText? = null

    var editTextTitle: TextInputEditText? = null
    var editTextDesc: TextInputEditText? = null
    var editTextOutOffMark: TextInputEditText? = null

    var textStartDate: TextInputEditText? = null
    var textEndDate: TextInputEditText? = null
    var textStartTime: TextInputEditText? = null
    var textEndTime: TextInputEditText? = null

    var fileNameList = ArrayList<FileList>()
    var dummyFileName = ArrayList<String>()

    var textViewNoFilesStudent: TextView? = null
    var recyclerView: RecyclerView? = null

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private var readPermission = false
    private var writePermission = false
    var maxCount = 10
    var maxCountSelection = 10

    var constraintLayoutCamera: ConstraintLayout? = null
    var constraintLayoutAudio: ConstraintLayout? = null
    var constraintLayoutPDF: ConstraintLayout? = null
    var constraintLayoutVideo: ConstraintLayout? = null

    var buttonTakeTest: AppCompatButton? = null

    var textViewSelect: TextView? = null
    var constraintLayoutUpload: ConstraintLayout? = null

    private lateinit var localDBHelper: LocalDBHelper

    lateinit var studyMaterialAdapter: StudyMaterialAdapter

    var selectedChapList = ArrayList<NumberList>()
    var selectedPageList = ArrayList<NumberList>()

    private lateinit var assignmentStaffViewModel: AssignmentStaffViewModel

    var startTime = ""
    var endTime = ""

    lateinit var bottomSheetNumbers: BottomSheetNumbers




    constructor(assignmentListener: AssignmentListener) {
        this.assignmentListener = assignmentListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE
        schoolId = user[0].SCHOOL_ID

        assignmentStaffViewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[AssignmentStaffViewModel::class.java]

        _binding = ActivityCreateAssigmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding = ActivityCreateAssigmentBinding.inflate(layoutInflater)
//        setContentView(binding.root)

        pb = ProgressDialog(requireActivity())
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Loading...")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)

        toolbar = binding.toolbar
//        if (toolbar != null) {
//            setSupportActionBar(toolbar)
//            supportActionBar!!.title = "Create Assignment"
//            // Customize the back button
//            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
//            supportActionBar!!.setDisplayShowTitleEnabled(true)
//            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
//                onBackPressed()
//            })
//        }

        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_white)
        toolbar?.title = "Create Homework"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.white))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }



        spinnerAcademic = binding.spinnerAcademic
        spinnerClass = binding.spinnerClassText
        spinnerClass?.visibility = View.VISIBLE///class Text
        binding.spinnerClass.visibility = View.GONE ///class Spinner
        spinnerSubject = binding.spinnerSubject
        textStartDate = binding.textStartDate
        textEndDate = binding.textEndDate
        textStartTime = binding.textStartTime
        textEndTime = binding.textEndTime
        editTextTitle = binding.editTextTitle
        editTextDesc = binding.editTextDesc

        editTextOutOffMark = binding.editTextOutOffMark
        spinnerStatus = binding.spinnerStatus
        selectChapter = binding.selectChapter
        selectPages = binding.selectPages



        selectChapter?.inputType = InputType.TYPE_NULL
        selectChapter?.keyListener = null
        selectPages?.inputType = InputType.TYPE_NULL
        selectPages?.keyListener = null

        constraintLayoutCamera = binding.constraintLayoutCamera
        constraintLayoutAudio = binding.constraintLayoutAudio
        constraintLayoutPDF = binding.constraintLayoutPDF
        constraintLayoutVideo = binding.constraintLayoutVideo

        textViewSelect = binding.textViewSelect
        textViewNoFilesStudent = binding.textViewNoFilesStudent
        constraintLayoutUpload = binding.constraintLayoutUpload

        recyclerView = binding.recyclerView
        //recyclerView?.layoutManager = GridLayoutManager(requireActivity(),4)
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())

        textViewNoFilesStudent?.visibility = View.GONE
        recyclerView?.visibility = View.VISIBLE

        textStartDate?.inputType = InputType.TYPE_NULL
        textStartDate?.keyListener = null
        textEndDate?.inputType = InputType.TYPE_NULL
        textEndDate?.keyListener = null

        textStartTime?.inputType = InputType.TYPE_NULL
        textStartTime?.keyListener = null
        textEndTime?.inputType = InputType.TYPE_NULL
        textEndTime?.keyListener = null

        editTextOutOffMark!!.setText("0")

        var expandingLayout = binding.expandingLayout
        var expandingButton = binding.expandingButton

        expandingButton.setOnClickListener {

            if (expandingLayout.visibility == View.VISIBLE) {
                // The transition of the hiddenView is carried out by the TransitionManager class.
                // Here we use an object of the AutoTransition Class to create a default transition
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    TransitionManager.beginDelayedTransition(binding.constraintLayout33, AutoTransition())
                }
                expandingLayout.visibility = View.GONE
                binding.textViewAdd.text = "Add More"
                binding.imageViewAdd.setImageResource(R.drawable.ic_icon_add)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    TransitionManager.beginDelayedTransition(binding.constraintLayout33, AutoTransition())
                }
                expandingLayout.visibility = View.VISIBLE
                binding.textViewAdd.text = "Add Less"
                binding.imageViewAdd.setImageResource(R.drawable.ic_substraction_icon)
            }
        }

        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                aCCADEMICID = getYears[position].aCCADEMICID
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

//        spinnerClass?.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long
//            ) {
//                cLASSID = getClass[position].cLASSID
//                getSubjectList(cLASSID)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }

        spinnerClass?.setOnClickListener {

            val builder = AlertDialog.Builder(requireActivity(),R.style.DialogTheme)
            builder.setTitle("Choose Class")
            builder.setMultiChoiceItems(listItems, checkedItems) { dialog, which, isChecked ->
                Log.i(TAG,"checkedItems ${checkedItems.size}")
                checkedItems[which] = isChecked
                //  val currentItem: Array<String> = selectedItems[which]
            }
            builder.setCancelable(false)
            builder.setPositiveButton(
                "Apply"
            ) { dialog: DialogInterface?, which: Int ->
                var classNames= ""
                classes = ""
                classIds = ""
                for (i in checkedItems.indices) {
                    if (checkedItems[i]) {
                        classNames += "${getClass[i].cLASSNAME}, "
                        classIds += "${getClass[i].cLASSID},"
                    }
                }
                if(classIds != "") {
                    Log.i(TAG, "classIds ${classIds.substring(0, classIds.length - 1)}")
                    classes = classIds.substring(0, classIds.length - 1)
                    spinnerClass?.text = classNames
                    getSubjectList(classIds.substring(0, classIds.length - 1));
                }else{
                    Utils.getSnackBar4K(requireActivity(),"Select atleast one class",binding.constraintLayoutContent)
                }

            }
            builder.setNegativeButton(
                "CANCEL"
            ) { dialog: DialogInterface?, which: Int ->
                spinnerClass?.text = "Click to Select Class"
                Arrays.fill(
                    checkedItems,
                    false
                )
            }

            builder.create()

            val alertDialog = builder.create()
            alertDialog.show()
            val buttonbackground: Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            buttonbackground.setTextColor(Color.BLACK)

            val buttonbackground1: Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            buttonbackground1.setTextColor(Color.BLACK)
        }

        spinnerSubject?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                sUBJECTID = getSubject[position].sUBJECTID
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        textStartDate!!.isClickable = true;
        textEndDate!!.isClickable = true;
        textStartTime!!.isClickable = true;
        textEndTime!!.isClickable = true;
        selectChapter!!.isClickable = true;
        selectPages!!.isClickable = true;

        textStartDate?.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(textStartDate?.windowToken, 0)
            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    fromStr = "$year-$s-$dayOfMonth"
                    startDate = "$s/$dayOfMonth/$year"
                    if (toStr.isNotEmpty()) {
                        if (Utils.checkDatesAfter(fromStr, toStr)) {
                            textStartDate?.setText(Utils.dateformat(fromStr))
                        } else {
                            Toast.makeText(requireActivity(), "Give Valid Date", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        textStartDate?.setText(Utils.dateformat(fromStr))
                    }


                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("Start Date")
            mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        }


        textEndDate?.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(textEndDate?.windowToken, 0)

            val mcurrentDate1 = Calendar.getInstance()
            val mYear = mcurrentDate1[Calendar.YEAR]
            val mMonth = mcurrentDate1[Calendar.MONTH]
            val mDay = mcurrentDate1[Calendar.DAY_OF_MONTH]
            val mDatePicker3 = DatePickerDialog(
                requireActivity(), { _, year, month, dayOfMonth ->
                    val s = month + 1
                    toStr = "$year-$s-$dayOfMonth"

                    if (fromStr.isNotEmpty()) {
                        if (Utils.checkDatesAfter(fromStr, toStr)) {
                            textEndDate?.setText(Utils.dateformat(toStr))
                            endDate = "$s/$dayOfMonth/$year"
                        } else {
                            Toast.makeText(requireActivity(), "Give Valid Date", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(requireActivity(), "Give Start Date", Toast.LENGTH_SHORT)
                            .show()
                    }
                }, mYear, mMonth, mDay
            )
            //    mDatePicker3.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker3.setTitle("End Date")
            mDatePicker3.datePicker.minDate = Date().time
            mDatePicker3.show()
            mDatePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            mDatePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

        }


        textStartTime?.setOnClickListener(View.OnClickListener { // Get Current Time
            val c = Calendar.getInstance()
            val mHour = c[Calendar.HOUR_OF_DAY]
            val mMinute = c[Calendar.MINUTE]

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(
                requireActivity(),
                { _, hourOfDay, minute ->
                    val isPM = hourOfDay >= 12
                    startTime = String.format(
                        "%02d:%02d %s",
                        if (hourOfDay == 12 || hourOfDay == 0) 12 else hourOfDay % 12,
                        minute,
                        if (isPM) "PM" else "AM"
                    )
                    textStartTime?.setText(
                        String.format(
                            "%02d:%02d %s",
                            if (hourOfDay == 12 || hourOfDay == 0) 12 else hourOfDay % 12,
                            minute,
                            if (isPM) "PM" else "AM"
                        )
                    )
                    // end_time.setText(hourOfDay + ":" + minute +AM_PM);
                    Log.i(CreateZoomScheduled.TAG, "end_time $startTime")
                }, mHour, mMinute, false
            )
            timePickerDialog.setTitle("Start Time")
            timePickerDialog.show()
            timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        })

        textEndTime?.setOnClickListener(View.OnClickListener { // Get Current Time
            val c = Calendar.getInstance()
            val mHour = c[Calendar.HOUR_OF_DAY]
            val mMinute = c[Calendar.MINUTE]

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(
                requireActivity(),
                { _, hourOfDay, minute ->
                    val isPM = hourOfDay >= 12
                    endTime = String.format(
                        "%02d:%02d %s",
                        if (hourOfDay == 12 || hourOfDay == 0) 12 else hourOfDay % 12,
                        minute,
                        if (isPM) "PM" else "AM"
                    )
                    textEndTime?.setText(
                        String.format(
                            "%02d:%02d %s",
                            if (hourOfDay == 12 || hourOfDay == 0) 12 else hourOfDay % 12,
                            minute,
                            if (isPM) "PM" else "AM"
                        )
                    )
                    // end_time.setText(hourOfDay + ":" + minute +AM_PM);
                    Log.i(CreateZoomScheduled.TAG, "end_time $endTime")
                }, mHour, mMinute, false
            )
            timePickerDialog.setTitle("End Time")
            timePickerDialog.show()
            timePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            timePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        })


        val status =
            ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, type)
        status.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus?.adapter = status
        spinnerStatus?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (type[position] == "Unpublished") {
                    typeStr = "0"
                } else if (type[position] == "Published") {
                    typeStr = "1"
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

//        permissionsLauncherMulti = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//            permissions.entries.forEach {
//                Log.i(TAG, "permission ${it.key} = ${it.value}")
//
//                Log.i(TAG, "permission ${permissions.entries}")
//            }
//        }
        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    readPermission = permissions[Manifest.permission.READ_MEDIA_IMAGES]!! &&
//                            permissions[Manifest.permission.READ_MEDIA_VIDEO]!! && permissions[Manifest.permission.READ_MEDIA_AUDIO]
//                            ?: readPermission
//                }else{
//                    readPermission = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
//                    writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermission
//                }
                Log.i(TAG,"permissions $permissions")
                readPermission = permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermission

                Log.i(TAG,"readPermission $readPermission")
                Log.i(TAG,"writePermission $writePermission")

            }

        constraintLayoutCamera?.setOnClickListener {
            if (requestPermission()) {
                if (fileNameList.size < maxCount) {
                    maxCountSelection = maxCount - fileNameList.size
                    Toast.makeText(
                        requireActivity(),
                        "Select $maxCountSelection ",
                        Toast.LENGTH_SHORT
                    ).show()

                    val imageCollection = sdk29AndUp {
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val intent = Intent(Intent.ACTION_PICK, imageCollection)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.type = "image/*"
                    startForResult.launch(intent)
                } else {
                    Utils.getSnackBar4K(
                        requireActivity(),
                        "Maximum Count Reached",
                        binding.constraintLayoutContent
                    )
                }
            }
        }

        constraintLayoutAudio?.setOnClickListener {
            if (requestPermission()) {
                mimeTypeFun("audio/*", Intent.ACTION_GET_CONTENT)
            }
        }

        constraintLayoutVideo?.setOnClickListener {
            if (requestPermission()) {
                mimeTypeFun("video/*", Intent.ACTION_PICK)
            }
        }

        constraintLayoutPDF?.setOnClickListener {
            if (requestPermission()) {
                mimeTypeFun("application/pdf",Intent.ACTION_OPEN_DOCUMENT)


//                val mimeTypes = arrayOf("application/pdf")
//                val intent = Intent(Intent.ACTION_GET_CONTENT)
//                intent.type = "application/pdf"
//                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
//                intent.addCategory(Intent.CATEGORY_OPENABLE)
//
//                try {
//                    startForResult.launch(intent)
//                } catch (e: ActivityNotFoundException) {
//                    // Handle the case where no app supporting the intent is available
//                    Log.i(TAG,"No app available to pick PDF files.")
//                }

//                if (fileNameList.size < maxCount) {
//            maxCountSelection = maxCount - fileNameList.size
//                    Toast.makeText(requireActivity(), "Select $maxCountSelection ", Toast.LENGTH_SHORT)
//                        .show()
//
//
//                    val intent: Intent
//                    if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
//                        val mimeTypes = arrayOf("application/pdf")
//                        intent = Intent("com.sec.android.app.myfiles.PICK_DATA")
//                        intent.putExtra("CONTENT_TYPE", mimeTypes)
//                        intent.addCategory(Intent.CATEGORY_DEFAULT)
//                    } else {
//                        val mimeTypes = arrayOf(
//                        "application/msword",
//                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",  // .doc & .docx
//                        "application/vnd.ms-powerpoint",
//                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",  // .ppt & .pptx
//                        "application/vnd.ms-excel",
//                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",  // .xls & .xlsx
//                        "text/plain",
//                            "application/pdf",
//                        "application/zip",
//                        "application/vnd.android.package-archive"
//                        )
//                        intent = Intent(Intent.ACTION_GET_CONTENT) // or ACTION_OPEN_DOCUMENT
//                        intent.type = "*/*"
//                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
//                        intent.addCategory(Intent.CATEGORY_OPENABLE)
//                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//                    }
//                    startForResult.launch(intent)
//                }
            }
        }

//        selectChapter!!.setOnFocusChangeListener {v: View, m: Boolean ->
//            // Perform tasks here
//
//
//        }
//
//
//        selectPages!!.setOnFocusChangeListener { v: View, m: Boolean ->
//
//            bottomSheetNumbers = BottomSheetNumbers("Select Pages",this,selectedPageList,2)
//            bottomSheetNumbers.show(requireActivity().supportFragmentManager, "TAG")
//


        selectChapter!!.setOnClickListener {
            //  val mergedArray = getDistinctStudents(numberCountList(), selectedList)
            //  Log.i(TAG,"mergedArray ${mergedArray.size}")
            //  Log.i(TAG,"mergedArray $mergedArray")
            bottomSheetNumbers = BottomSheetNumbers("Select Chapter", this, selectedChapList, 1)
            bottomSheetNumbers.show(requireActivity().supportFragmentManager, "TAG")

        }


        selectPages!!.setOnClickListener {
            // val mergedArray = getDistinctStudents(numberCountList(), selectedList)
            //  Log.i(TAG,"mergedArray ${mergedArray.size}")
            //  Log.i(TAG,"mergedArray $mergedArray")
            bottomSheetNumbers = BottomSheetNumbers("Select Pages", this, selectedPageList, 2)
            bottomSheetNumbers.show(requireActivity().supportFragmentManager, "TAG")
        }

        val buttonSubmit = binding.buttonSubmit
        buttonSubmit.text = resources.getString(R.string.create_assignment)
        buttonSubmit.setOnClickListener {
            fileNames = ArrayList<String>()
            if (
//                assignmentStaffViewModel.validateField(
//                    textStartDate!!,
//                    "Start Date field cannot be empty",
//                    requireActivity(),
//                    binding.constraintLayoutContent
//                )
//                && assignmentStaffViewModel.validateField(
//                    textEndDate!!,
//                    "End Date field cannot be empty",
//                    requireActivity(),
//                    binding.constraintLayoutContent
//                )
//                && assignmentStaffViewModel.validateField(
//                    textStartTime!!,
//                    "Start Time field cannot be empty",
//                    requireActivity(),
//                    binding.constraintLayoutContent
//                )
//                && assignmentStaffViewModel.validateField(
//                    textEndTime!!,
//                    "End Time field cannot be empty",
//                    requireActivity(),
//                    binding.constraintLayoutContent
//                )
//                && assignmentStaffViewModel.validateField(
//                    editTextTitle!!,
//                    "Title field cannot be empty",
//                    requireActivity(),
//                    binding.constraintLayoutContent
//                )
//                &&
                assignmentStaffViewModel.validateField(
                    editTextDesc!!,
                    "Description field cannot be empty",
                    requireActivity(),
                    binding.constraintLayoutContent
                )
                && validateField(classIds,"Select atleast one class")

//                && assignmentStaffViewModel.validateField(
//                    editTextOutOffMark!!,
//                    "Description field cannot be empty",
//                    requireActivity(),
//                    binding.constraintLayoutContent
//                )
            ) {

                //  arrayListItems += responses[i]+","
                if (arrayListItems.isNotEmpty()) {
                    submitFile(Utils.removeLastChar(arrayListItems))
                } else {
                    submitFile("")
                }

            }

        }

        val constraintLayoutContent = binding.constraintLayoutContent
        constraintLayoutContent.setOnClickListener {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLayoutContent.windowToken, 0)
        }
        initFunction()
        Utils.setStatusBarColor(requireActivity())

        bottomSheetNumbers = BottomSheetNumbers()

        selectedChapList = numberCountList()
        selectedPageList = numberCountList()
    }

//    fun getDistinctStudents(studentsList: ArrayList<NumberList>, prefStudents: ArrayList<NumberList>): ArrayList<NumberList> {
//        var tempList= ArrayList<NumberList>()
//        for(i in studentsList.indices){
//            if(studentsList[i].isSelect )
//            tempList.add
//        }
    // val consolidatedStudents = studentsList + prefStudents
    // val distinctStudents = consolidatedStudents.distinctBy{ it.isSelect }
    //      return prefStudents.minus(distinctStudents)
//    }

//    fun concatenate(a: ArrayList<NumberList>, b: ArrayList<NumberList>): ArrayList<NumberList> {
//        val result = a.contains(a.size + b.size)
//        System.arraycopy(b, 0, result, a.size, b.size)
//        return result
//    }

    private fun submitFile(details: String) {


        // postParam.put("ACCADEMIC_ID",s_aid);
        //        postParam.put("CLASS_ID", scid);
        //        postParam.put("SUBJECT_ID", ssid);
        //        postParam.put("ASSIGNMENT_NAME", input_assignment_name.getText().toString());
        //        postParam.put("ASSIGNMENT_DESCRIPTION", input_assignment_desc.getText().toString());
        //        postParam.put("ASSIGNMENT_OUTOFF_MARK", input_assignment_outoff_mark.getText().toString());
        //        postParam.put("START_DATE", get_start_date);
        //        postParam.put("END_DATE", get_end_date);
        //        postParam.put("ADMIN_ID",Global.Admin_id);
        //        postParam.put("STATUS", str_type);
        //        postParam.put("ASSIGNMENT_FILE",details);

        val url = "Assignment/AssignmentAddNew"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("SCHOOL_ID", schoolId)
            jsonObject.put("ACCADEMIC_ID", aCCADEMICID)
            jsonObject.put("CLASS_IDs", classes)
            jsonObject.put("SUBJECT_ID", sUBJECTID)

            jsonObject.put("ASSIGNMENT_NAME", editTextTitle?.text.toString())
            jsonObject.put("ASSIGNMENT_DESCRIPTION", editTextDesc?.text.toString())
            jsonObject.put("ASSIGNMENT_OUTOFF_MARK", editTextOutOffMark?.text.toString())
            jsonObject.put("CHAPTER_NO", selectChapter?.text.toString())
            jsonObject.put("PAGE_NO", selectPages?.text.toString())

            jsonObject.put("START_DATE", "$startDate $startTime")
            jsonObject.put("END_DATE", "$endDate $endTime")
            jsonObject.put("ADMIN_ID", adminId)
            jsonObject.put("STATUS", typeStr)
            jsonObject.put("ASSIGNMENT_FILE", details);

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG, "jsonObject $jsonObject")
        val accademicRe =
            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        assignmentStaffViewModel.getCommonPostFun(url, accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            progressStop()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    assignmentListener.onCreateClick("Assignment Created Successfully")
                                    arrayListItems = ""
                                    cancelFrg()
                                }
                                Utils.resultFun(response) == "EXIST" -> {
                                    Utils.getSnackBar4K(
                                        requireActivity(),
                                        "Assignment Already Exist",
                                        binding.constraintLayoutContent!!
                                    )
                                }
                                else -> {
                                    Utils.getSnackBar4K(
                                        requireActivity(),
                                        "Assignment Creation Failed",
                                        binding.constraintLayoutContent!!
                                    )
                                }
                            }
                        }
                        Status.ERROR -> {
                            progressStop()
                            Utils.getSnackBar4K(
                                requireActivity(),
                                "Please try again after sometime",
                                binding.constraintLayoutContent!!
                            )
                        }
                        Status.LOADING -> {
                            progressStart()
                            Log.i(TAG, "loading")
                        }
                    }
                }
            })

    }

    fun validateField(edtField: String, message: String): Boolean {
        return if (Utils.validateFieldIsEmpty(edtField)) {
            Utils.getSnackBar4K(requireActivity(),message,binding.constraintLayoutContent)
            false
        } else {
            true
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
                            var years = Array(getYears.size) { "" }
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
                            listItems = Array(getClass.size){""}
                            for (i in getClass.indices) {
                                listItems[i] = getClass[i].cLASSNAME
                            }
                            checkedItems = BooleanArray(listItems.size)
                            Log.i(TAG,"initFunction checkedItems ${checkedItems.size}")
//                            var classX = Array(getClass.size) { "" }
//                            for (i in getClass.indices) {
//                                classX[i] = getClass[i].cLASSNAME
//                            }
//                            if (spinnerClass != null) {
//                                val adapter = ArrayAdapter(
//                                    requireActivity(),
//                                    android.R.layout.simple_spinner_dropdown_item,
//                                    classX
//                                )
//                                spinnerClass?.adapter = adapter
//                            }
                            Log.i(TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "initFunction LOADING")
                        }
                    }
                }
            })
    }

    fun getSubjectList(cLASSID: String) {

        val jsonObject = JSONObject()
        try {
            jsonObject.put("ADMIN_ID", adminId)
            jsonObject.put("CLASS_ID", cLASSID)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Log.i(TAG,"jsonObject ${jsonObject.toString()}")

        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        assignmentStaffViewModel.postSubjectStaff(accademicRe)
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
                            Log.i(TAG,"getSubjectList LOADING")
                        }
                    }
                }
            })
//        assignmentStaffViewModel.getSubjectStaff(cLASSID, adminId)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                            getSubject = response.subjects as ArrayList<SubjectsModel.Subject>
//                            var subject = Array(getSubject.size) { "" }
//                            if (subject.isNotEmpty()) {
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
//                            Log.i(TAG, "getSubjectList SUCCESS")
//                        }
//                        Status.ERROR -> {
//                            Log.i(TAG, "getSubjectList ERROR")
//                        }
//                        Status.LOADING -> {
//                            getSubject = java.util.ArrayList<SubjectsModel.Subject>()
//
//                            Log.i(TAG, "getSubjectList LOADING")
//                        }
//                    }
//                }
//            })
    }

    fun mimeTypeFun(mimeTypes: String, actionOpenDocument: String) {
        if (fileNameList.size < maxCount) {
            maxCountSelection = maxCount - fileNameList.size
            Toast.makeText(requireActivity(), "Select $maxCountSelection ", Toast.LENGTH_SHORT)
                .show()

            val intent = Intent(actionOpenDocument); // or ACTION_OPEN_DOCUMENT //ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = mimeTypes;
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startForResult.launch(intent)
        } else {
            Utils.getSnackBar4K(
                requireActivity(),
                "Maximum Count Reached",
                binding.constraintLayoutContent
            )
        }
    }

    ///permission Part
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data
                Log.i(TAG, "data $data")

                //If multiple image selected
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0

                    val countPath = count + fileNameList.size
                    if (countPath > 10) {
                        Toast.makeText(
                            requireActivity(),
                            "You select more then $maxCount",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
//                        fileNameList.addAll(jsonArrayList)
                        for (i in 0 until count) {
                            val imageUri: Uri? = data.clipData?.getItemAt(i)?.uri
                            dummyFileName.add(imageUri!!.toString())
                            fileNameList.add(
                                FileList(
                                    0,
                                    imageUri.toString(),
                                    "",
                                    "Local",
                                    0
                                )
                            )
                        }
                    }
                    //     imageAdapter.addSelectedImages(selectedPaths)
                }

                //If single image selected
                else if (data?.data != null) {
                    val imageUri: Uri? = data.data
                    dummyFileName.add(imageUri!!.toString())
                    fileNameList.add(
                        FileList(
                            0,
                            imageUri.toString(),
                            "",
                            "Local",
                            0
                        )
                    )
                }
                if (fileNameList.size == 10) {
                    textViewSelect?.visibility = View.GONE
                    constraintLayoutUpload?.visibility = View.GONE
                } else {
                    textViewSelect?.visibility = View.VISIBLE
                    constraintLayoutUpload?.visibility = View.VISIBLE
                }
                textViewNoFilesStudent?.visibility = View.GONE
                recyclerView?.visibility = View.VISIBLE
                studyMaterialAdapter = StudyMaterialAdapter(
                    this,
                    this,
                    fileNameList,
                    requireActivity(),
                    TAG
                )
                recyclerView?.adapter = studyMaterialAdapter

            }

        }


    class StudyMaterialAdapter(
        var assignmentCreateListener: AssignmentCreateListener,
        var createAssignmentActivity: CreateAssignmentDialog,
        var materialList: ArrayList<FileList>,
        var context: Context, var TAG: String
    ) : RecyclerView.Adapter<StudyMaterialAdapter.ViewHolder>() {
        var downLoadPos = 0

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var imageView: ImageView = view.findViewById(R.id.imageView)
//            var imageViewOther: ImageView = view.findViewById(R.id.imageViewOther)
//            var imageViewDelete: ImageView = view.findViewById(R.id.imageViewDelete)
//
//            var constraintDownload  : ConstraintLayout = view.findViewById(R.id.constraintDownload)
//            var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
//            var imageViewClose : ImageView = view.findViewById(R.id.imageViewClose)
//            var textViewPercentage  : TextView = view.findViewById(R.id.textViewPercentage)

            var imageView: ImageView = view.findViewById(R.id.imageView)
            var imageViewOther: ImageView = view.findViewById(R.id.imageViewOther)
            var imageViewDelete: ImageView = view.findViewById(R.id.imageViewDelete)

            var imageViewDownloadButton: ImageView = view.findViewById(R.id.imageViewDownloadButton)

            var constraintDownload: ConstraintLayout = view.findViewById(R.id.constraintDownload)
            var progressBar: ProgressBar = view.findViewById(R.id.progressBar)
            var imageViewClose: ImageView = view.findViewById(R.id.imageViewClose)
            var textViewPercentage: TextView = view.findViewById(R.id.textViewPercentage)

            var textViewProgress: TextView = view.findViewById(R.id.textViewProgress)

            var perProgressBar: CircularProgressIndicator = view.findViewById(R.id.perProgressBar)

            var textViewTitle: TextView = view.findViewById(R.id.textViewTitle)

            var constraintText: ConstraintLayout = view.findViewById(R.id.constraintText)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.study_material_progress_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.textViewFileName.text = materialList[position].fILETITLE

            if (materialList[position].fILETITLE.isNotEmpty()) {
                holder.textViewTitle.text = materialList[position].fILETITLE
            } else {
                holder.textViewTitle.text = "Enter File Name"
            }

            if (materialList[position].fILETYPE == "Json") {
                holder.imageViewDownloadButton.visibility = View.GONE
                holder.perProgressBar.visibility = View.GONE
                holder.textViewProgress.visibility = View.GONE

                val path: String = materialList[position].fILENAME
                val mFile = java.io.File(path)
                if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                    || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")
                ) {
                    // Word document
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //  .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_word)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".pdf") ||
                    mFile.toString().contains(".PDF")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // PDF file
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //     .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_pdf)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                    || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")
                ) {
                    // Powerpoint file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_power_point)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                    || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // Excel file
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //  .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_excel)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                    || mFile.toString().contains(".png") || mFile.toString()
                        .contains(".JPG") || mFile.toString().contains(".JPEG")
                    || mFile.toString().contains(".PNG")
                ) {

                    // JPG file
                    holder.imageViewOther.visibility = View.GONE
                    holder.imageView.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(Global.event_url + "/AssignmentFile/" + path)
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_gallery)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageView)
                } else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                    // Text file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_text)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".mp3") || mFile.toString()
                        .contains(".wav") || mFile.toString().contains(".ogg")
                    || mFile.toString().contains(".m4a") || mFile.toString()
                        .contains(".aac") || mFile.toString().contains(".wma") ||
                    mFile.toString().contains(".MP3") || mFile.toString()
                        .contains(".WAV") || mFile.toString().contains(".OGG")
                    || mFile.toString().contains(".M4A") || mFile.toString()
                        .contains(".AAC") || mFile.toString().contains(".WMA")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(java.io.File(path))
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() // .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_file_voice)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
                } else {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        // .load(java.io.File(path))
                        .load(Global.event_url + "/AssignmentFile/" + path)
                        .apply(
                            RequestOptions.centerCropTransform()
                                .dontAnimate() //   .override(imageSize, imageSize)
                                .placeholder(R.drawable.ic_video_library)
                        )
                        .thumbnail(0.5f)
                        .into(holder.imageViewOther)
//                    holder.imageViewOther.visibility = View.VISIBLE
//                    holder.imageView.visibility = View.GONE
//                    Glide.with(context)
//                        .load(java.io.File(path))
//                        .apply(
//                            RequestOptions.centerCropTransform()
//                                .dontAnimate() //   .override(imageSize, imageSize)
//                                .placeholder(R.drawable.ic_file_video)
//                        )
//                        .thumbnail(0.5f)
//                        .into(holder.imageViewOther)
                }
            }
            else if (materialList[position].fILETYPE == "Local") {
                val path: String = materialList[position].fILENAME
                Log.i(TAG, "path $path")
                val mFile = FileUtils.getReadablePathFromUri(context, path.toUri())
                Log.i(TAG, "mFile $mFile")

                materialList[position].fILETYPE = "Uploaded"
                holder.imageViewDownloadButton.visibility = View.GONE
                holder.perProgressBar.visibility = View.VISIBLE
                holder.textViewProgress.visibility = View.VISIBLE//,materialList[position].fILENAME
                assignmentCreateListener.onFileUploadProgress(
                    position, mFile!!, holder.textViewTitle.text.toString(),
                    holder.perProgressBar, holder.imageViewDownloadButton, holder.textViewProgress
                )

                if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                    || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")
                ) {
                    // Word document
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_word)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".pdf") || mFile.toString().contains(".PDF")) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // PDF file

                    Glide.with(context)
                        .load(R.drawable.ic_file_pdf)
                        .into(holder.imageViewOther)

                } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                    || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")
                ) {
                    // Powerpoint file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_power_point)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                    || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // Excel file
                    Glide.with(context)
                        .load(R.drawable.ic_file_excel)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                    || mFile.toString().contains(".png") || mFile.toString()
                        .contains(".JPG") || mFile.toString().contains(".JPEG")
                    || mFile.toString().contains(".PNG")
                ) {
                    // JPG file
                    holder.imageViewOther.visibility = View.GONE
                    holder.imageView.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(materialList[position].fILENAME)
                        .into(holder.imageView)
                } else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                    // Text file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_text)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".mp3") || mFile.toString()
                        .contains(".wav") || mFile.toString().contains(".ogg")
                    || mFile.toString().contains(".m4a") || mFile.toString()
                        .contains(".aac") || mFile.toString().contains(".wma") ||
                    mFile.toString().contains(".MP3") || mFile.toString()
                        .contains(".WAV") || mFile.toString().contains(".OGG")
                    || mFile.toString().contains(".M4A") || mFile.toString()
                        .contains(".AAC") || mFile.toString().contains(".WMA")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_voice)
                        .into(holder.imageViewOther)
                } else {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
//                    Glide.with(context)
//                        .load(R.drawable.ic_file_video)
//                        .into(holder.imageViewOther)

                    try {
                        val thumb = ThumbnailUtils.createVideoThumbnail(
                            mFile,
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )
                        holder.imageViewOther.setImageBitmap(thumb)
                    } catch (e: java.lang.Exception) {
                        Log.i("TAG", "Exception $e")
                    }
                }
            }
            else if (materialList[position].fILETYPE == "Uploaded") {
                holder.imageViewDownloadButton.visibility = View.GONE
                holder.perProgressBar.visibility = View.GONE
                holder.textViewProgress.visibility = View.VISIBLE
                holder.textViewProgress.text = "Uploaded"

                val path: String = materialList[position].fILENAME
                Log.i(TAG, "path $path")
                val mFile = FileUtils.getReadablePathFromUri(context, path.toUri())
                Log.i(TAG, "mFile $mFile")

                if (mFile.toString().contains(".doc") || mFile.toString().contains(".docx")
                    || mFile.toString().contains(".DOC") || mFile.toString().contains(".DOCX")
                ) {
                    // Word document
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_word)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".pdf") || mFile.toString().contains(".PDF")) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // PDF file

                    Glide.with(context)
                        .load(R.drawable.ic_file_pdf)
                        .into(holder.imageViewOther)

                } else if (mFile.toString().contains(".ppt") || mFile.toString().contains(".pptx")
                    || mFile.toString().contains(".PPT") || mFile.toString().contains(".PPTX")
                ) {
                    // Powerpoint file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_power_point)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".xls") || mFile.toString().contains(".xlsx")
                    || mFile.toString().contains(".XLS") || mFile.toString().contains(".XLSX")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    // Excel file
                    Glide.with(context)
                        .load(R.drawable.ic_file_excel)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
                    || mFile.toString().contains(".png") || mFile.toString()
                        .contains(".JPG") || mFile.toString().contains(".JPEG")
                    || mFile.toString().contains(".PNG")
                ) {
                    // JPG file
                    holder.imageViewOther.visibility = View.GONE
                    holder.imageView.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(materialList[position].fILENAME)
                        .into(holder.imageView)
                } else if (mFile.toString().contains(".txt") || mFile.toString().contains(".TXT")) {
                    // Text file
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_text)
                        .into(holder.imageViewOther)
                } else if (mFile.toString().contains(".mp3") || mFile.toString()
                        .contains(".wav") || mFile.toString().contains(".ogg")
                    || mFile.toString().contains(".m4a") || mFile.toString()
                        .contains(".aac") || mFile.toString().contains(".wma") ||
                    mFile.toString().contains(".MP3") || mFile.toString()
                        .contains(".WAV") || mFile.toString().contains(".OGG")
                    || mFile.toString().contains(".M4A") || mFile.toString()
                        .contains(".AAC") || mFile.toString().contains(".WMA")
                ) {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
                    Glide.with(context)
                        .load(R.drawable.ic_file_voice)
                        .into(holder.imageViewOther)
                } else {
                    holder.imageViewOther.visibility = View.VISIBLE
                    holder.imageView.visibility = View.GONE
//                    Glide.with(context)
//                        .load(R.drawable.ic_file_video)
//                        .into(holder.imageViewOther)

                    try {
                        val thumb = ThumbnailUtils.createVideoThumbnail(
                            mFile!!,
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )
                        holder.imageViewOther.setImageBitmap(thumb)
                    } catch (e: java.lang.Exception) {
                        Log.i("TAG", "Exception $e")
                    }
                }
            }

            holder.imageViewDownloadButton.setOnClickListener {
                val path: String = materialList[position].fILENAME
                Log.i(TAG, "path $path")
                val mFile = FileUtils.getReadablePathFromUri(context, path.toUri())
                Log.i(TAG, "mFile $mFile")
                holder.imageViewDownloadButton.visibility = View.GONE
                holder.perProgressBar.visibility = View.VISIBLE
                holder.textViewProgress.visibility = View.VISIBLE//,materialList[position].fILENAME
                assignmentCreateListener.onFileUploadProgress(
                    position, mFile!!, holder.textViewTitle.text.toString(),
                    holder.perProgressBar, holder.imageViewDownloadButton, holder.textViewProgress
                )
            }




            holder.constraintText.setOnClickListener {
                val inflate: View = LayoutInflater.from(context)
                    .inflate(R.layout.user_input_dialog_box, null as ViewGroup?)
                val builder = AlertDialog.Builder(context)
                builder.setView(inflate)
                val editText = inflate.findViewById<View>(R.id.userInputDialog) as EditText
                if (materialList[position].fILETITLE.isNotEmpty() && holder.textViewTitle.text.toString() != "Enter File Name") {
                    editText.setText(holder.textViewTitle.text.toString())
                }

                builder.setCancelable(false).setPositiveButton(
                    "Submit"
                ) { dialogInterface, _ ->
                    materialList[position].fILETITLE = editText.text.toString()
                    holder.textViewTitle.text = materialList[position].fILETITLE
                    //  materialDetailsListener.submitFile(materialList[position].fILENAME,materialList[position].fILETITLE,position)
                    dialogInterface.cancel()
                }.setNegativeButton(
                    "Cancel"
                ) { dialogInterface, _ -> dialogInterface.cancel() }

                //Creating dialog box
                val alert = builder.create()
                //Setting the title manually
                alert.setTitle("File Name")
                alert.show()
                val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                buttonbackground.setTextColor(Color.BLACK)

                val buttonbackground1: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                buttonbackground1.setTextColor(Color.BLACK)

            }

            holder.imageViewDelete.setBackgroundResource(R.drawable.ic_file_close_icon)
            holder.imageViewDelete.setOnClickListener {
//                holder.constraintDownload.visibility = View.GONE
//                holder.textViewPercentage.visibility = View.GONE
//                PRDownloader.cancel(downLoadPos)
                assignmentCreateListener.onDeleteClick(position, materialList[position])
            }
        }

        override fun getItemCount(): Int {
            return materialList.size
        }

    }



//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    fun requestPermission(): Boolean {
//
//        var permissionList = arrayOf(Manifest.permission.READ_MEDIA_IMAGES,
//            Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO)
//        var hasReadPermission = false
//
//        var hasWritePermission = false
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//
//            for(i in permissionList.indices){
//                hasReadPermission = ContextCompat.checkSelfPermission(
//                    requireActivity(),
//                    permissionList[i]
//                ) == PackageManager.PERMISSION_GRANTED
//
//            }
//
//
////            val hasReadPermissionVideo = ContextCompat.checkSelfPermission(
////                requireActivity(),
////                permission[1]
////            ) == PackageManager.PERMISSION_GRANTED
////
////            val hasReadPermissionAudio = ContextCompat.checkSelfPermission(
////                requireActivity(),
////                permission[2]
////            ) == PackageManager.PERMISSION_GRANTED
//
//        }else{
//            hasReadPermission = ContextCompat.checkSelfPermission(
//                requireActivity(),
//                android.Manifest.permission.READ_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED
//
//            hasWritePermission = ContextCompat.checkSelfPermission(
//                requireActivity(),
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED
//
//        }
//
//        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
//
//
//        writePermission = hasWritePermission || minSdk29
//        readPermission = hasReadPermission
//
//        val permissions = readPermission && writePermission
//
//        val permissionToRequests = mutableListOf<String>()
//        if (!writePermission) {
//            permissionToRequests.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
//
//        if (!readPermission) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_IMAGES)
//                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_AUDIO)
//                permissionToRequests.add(android.Manifest.permission.READ_MEDIA_VIDEO)
//            }else{
//                permissionToRequests.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//            }
//
//
//        }
//
//        if (permissionToRequests.isNotEmpty()) {
//            permissionsLauncher.launch(permissionToRequests.toTypedArray())
//        }
//
//        return permissions
//    }


    private val PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onDeleteClick(position: Int, fileList: FileList) {
        fileNameList.removeAt(position)
        studyMaterialAdapter.notifyDataSetChanged()
        textViewSelect?.visibility = View.VISIBLE
        constraintLayoutUpload?.visibility = View.VISIBLE
        if (fileNameList.size == 0) {
            textViewNoFilesStudent?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE

        }
    }


    @SuppressLint("NewApi")
    override fun onFileUploadClick(position: Int, fileList: FileList) {
//        pb!!.setMessage("Uploading  : ${position + 1} / ${fileNameList.size}")
//        if (fileList.fILETYPE == "Local") {
//            var selectedFilePath =
//                FileUtils.getReadablePathFromUri(requireActivity(), fileList.fILENAME.toUri())
//            Log.i(TAG, "position  $position")
//            val i = Log.i(TAG, "fileName  $selectedFilePath")
//
//            var imagenPerfil = if (selectedFilePath.toString().contains(".jpg") || selectedFilePath.toString()
//                    .contains(".jpeg")
//                || selectedFilePath.toString().contains(".png") || selectedFilePath.toString()
//                    .contains(".JPG") || selectedFilePath.toString().contains(".JPEG")
//                || selectedFilePath.toString().contains(".PNG")
//            ) {
//                val file1 = File(selectedFilePath!!)
//                val requestFile: RequestBody = file1.asRequestBody("image/*".toMediaTypeOrNull())
//                MultipartBody.Part.createFormData("STUDY_METERIAL_FILE", file1.name,
//                    requestFile
//                )
//            } else {
//                val requestFile: RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), selectedFilePath!!)
//                // MultipartBody.Part is used to send also the actual file name
//                MultipartBody.Part.createFormData("STUDY_METERIAL_FILE", selectedFilePath, requestFile)
//            }
//
//
//            val apiInterface = RetrofitClientStaff.create().fileUploadAssignment(
//                SERVER_URL,
//                imagenPerfil
//            )
//            apiInterface.enqueue(object : Callback<FileResultModel> {
//                override fun onResponse(
//                    call: Call<FileResultModel>,
//                    resource: Response<FileResultModel>
//                ) {
//                    val response = resource.body()
//                    if (resource.isSuccessful) {
//                        Log.i(TAG, "response  $response")
//                        fileNames.add(response!!.dETAILS)
//                    }
//                    if ((position + 1) == fileNameList.size) {
//                        pb?.dismiss()
//                        val arraylist = java.lang.String.join(",", fileNames)
//                        Log.i(TAG, "str  $arraylist")
//                        submitFile(arraylist)
//                    }
//
//                }
//
//                override fun onFailure(call: Call<FileResultModel>, t: Throwable) {
//                    Log.i(TAG, "Throwable  $t")
//                }
//            })
//        } else {
//            Log.i(TAG, "str  ${fileNameList[position].fILENAME}")
//        }
//
//        if ((position + 1) == fileNameList.size) {
//            pb?.dismiss()
//            if (fileNames.size != 0) {
//                val arraylist = java.lang.String.join(",", fileNames)
//                Log.i(TAG, "str  $arraylist")
//                submitFile(arraylist)
//            }else{
//                submitFile("")
//            }
//        }
    }

    override fun onFileUploadProgress(
        position: Int,
        fILEPATHName: String,
//        fILENAME: String,
        fILETITLE: String,
        perProgressBar: CircularProgressIndicator,
        imageViewDownloadButton: ImageView,
        textViewProgress: TextView
    ) {
        Log.i(TAG, "fILENAME $fILEPATHName");

//       var fileName = fILEPATHName
//        if (fILEPATHName.contains(".jpg") ||
//            fILEPATHName.contains(".jpeg") ||
//            fILEPATHName.contains(".png") ||
//            fILEPATHName.contains(".JPG") ||
//            fILEPATHName.contains(".JPEG") ||
//            fILEPATHName.contains(".PNG")
//        ) {
//            fileName = FileUtilsJava.compressImage(fILEPATHName)
//            Log.i(TAG,"FilePath_scaled $fileName");
//        }

        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(TAG, "selectedFilePath $fILEPATHName");
        filesToUpload[0] = File(fILEPATHName)
        Log.i(TAG, "filesToUpload $filesToUpload");

        showProgress("Uploading media ...", perProgressBar, textViewProgress)
        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload, "",object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                hideProgress(perProgressBar, textViewProgress)
                Log.i(TAG, "onError ")
            }

            override fun onFinish(responses: Array<String>) {
                hideProgress(perProgressBar, textViewProgress)
                Log.i(TAG,"responses $responses")
                for (i in responses.indices) {
                    //val str = responses[i]
                    textViewProgress.text = "Uploaded"
                    perProgressBar.visibility = View.GONE
                    imageViewDownloadButton.visibility = View.GONE
                    //   Log.i(TAG, "RESPONSE $i ${responses[i]}")
                    //submitFile(responses[i],fILETITLE,position)
                    // if ((position + 1) == fileNameList.size) {
                    arrayListItems += responses[i] + ","
                    // }

                }
            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                updateProgress(
                    totalpercent,
                    "Uploading file $filenumber",
                    "",
                    perProgressBar,
                    textViewProgress
                )
              //  Log.i(TAG, "Progress Status $currentpercent $totalpercent $filenumber")
            }
        })
    }

    override fun onSelectNumberCount(
        selectedList: ArrayList<NumberList>,
        index: Int,
        numbersTxt: String
    ) {

        if (index == 1) {
            this.selectedChapList = selectedList
            selectChapter?.setText(numbersTxt.dropLast(1))

            if (selectedList.size <= 0) {
                this.selectedChapList = numberCountList()
            }

        } else if (index == 2) {
            this.selectedPageList = selectedList
            selectPages?.setText(numbersTxt.dropLast(1))

            if (selectedList.size <= 0) {
                this.selectedPageList = numberCountList()
            }
        }

        Log.i(TAG, "selectedList ${selectedList.size}")
        Log.i(TAG, "selectedList $selectedList")
        bottomSheetNumbers.dismiss()

    }

    fun numberCountList(): ArrayList<NumberList> {

        val iList: ArrayList<NumberList> = ArrayList()
        for (i in 0..60)
            iList.add(NumberList(i.toString(), false))
        return iList

    }


    fun updateProgress(
        progress: Int,
        title: String?,
        msg: String?,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView
    ) {
  //      Log.i(TAG, "updateProgress $progress")
//        perProgressBar.setTitle(title)
//        perProgressBar.setMessage(msg)
        textViewProgress.text = "$progress %"
        perProgressBar.progress = progress
    }

    fun showProgress(
        str: String?,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView
    ) {
        Log.i(TAG, "showProgress $str")
        try {
            //perProgressBar.setCancelable(false)
            // perProgressBar.setTitle("Please wait")
            //perProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            perProgressBar.max = 100 // Progress Dialog Max Value
            // perProgressBar.setMessage(str)
//            if (perProgressBar.isShowing) perProgressBar.dismiss()
//            perProgressBar.show()
        } catch (e: java.lang.Exception) {
        }
    }

    fun hideProgress(perProgressBar: CircularProgressIndicator, textViewProgress: TextView) {
        try {
            Log.i(TAG, "hideProgress")
            // if (perProgressBar.isShowing) perProgressBar.dismiss()
        } catch (e: java.lang.Exception) {
        }
    }

    override fun onViewClick(fILENAME: String) {
        val file = File(Utils.getRootDirPath(requireActivity()) +"/catch/staff/Assignment")
        if (!file.exists()) {
            file.mkdirs()
        } else {
            Log.i(TAG, "Already existing")
        }
        val mFile = File("$file/$fILENAME")
        //var pdfURI = Uri.fromFile(mFile)
        var pdfURI = FileProvider.getUriForFile(requireActivity(), requireActivity().packageName + ".provider", mFile)
        Log.i(TAG, "file $mFile")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = pdfURI

        if (mFile.toString().contains(".doc") || mFile.toString().contains(".DOC")) {
            // || url.toString().contains(".docx")
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
        }
        //     intent.setDataAndType(pdfURI, "image/jpeg");
        //     intent.setDataAndType(pdfURI, "image/jpeg");
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
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
            requireActivity().supportFragmentManager.beginTransaction().remove(fragment)
                .commitAllowingStateLoss()
        }
    }

    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }
    }
}

class FileList(val fILEID: Int, val fILENAME: String, val fILETYPE: String, val fILEDELETE: Int)

interface AssignmentCreateListener {
    fun onDeleteClick(
        position: Int,
        fileList: FileList
    )

    fun onFileUploadClick(
        position: Int,
        fileList: FileList
    )

    fun onFileUploadProgress(
        position: Int,
        fILEPATHName: String,
//        fILENAME: String,
        fILETITLE: String,
        perProgressBar: CircularProgressIndicator,
        imageViewDownloadButton: ImageView,
        textViewProgress: TextView
    )


    fun onSelectNumberCount(
        selectedList: ArrayList<NumberList>,
        index: Int,
        numbersTxt: String
    )


    fun onViewClick(fILENAME: String)
}