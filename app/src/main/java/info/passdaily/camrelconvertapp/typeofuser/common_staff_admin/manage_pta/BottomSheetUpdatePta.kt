package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_pta

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.theartofdev.edmodo.cropper.CropImage
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.BottomSheetUpdatePtaBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.permission.sdk29AndUp
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.FileList
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class BottomSheetUpdatePta : BottomSheetDialogFragment {

    private lateinit var ptaViewModel: PtaViewModel


    var imageSelection = ""

    var fileNameList = ArrayList<FileList>()

    private var _binding: BottomSheetUpdatePtaBinding? = null
    private val binding get() = _binding!!

    lateinit var ptaDetailsListener: PTADetailsListener

    lateinit var studentsList: StudentsInfoListModel.Students

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var readPermission = false
    private var writePermission = false


    var toolbar : Toolbar? = null
    var constraintLeave : ConstraintLayout? = null

    var buttonSubmit : AppCompatButton? =null

    var shapeImageView : ShapeableImageView? =null

    var dobDate = ""

    var ptarole = arrayOf(
        "Select PTA Role",
        "President",
        "Vice-President",
        "Secretary",
        "Join-Secretary",
        "Cashier",
        "Member",
        "Other"
    )
    var typeStr ="-1"

    var type = arrayOf("Unpublished", "Published")
    var typeStatus =""
    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var adminRole = 0
    var schoolId = 0

    var typePtaRole =""

    var editPtaName : TextInputEditText? = null
    var spinnerPtaRole  : AppCompatSpinner? = null
    var editMobileNumber : TextInputEditText? = null
    var editEmailId : TextInputEditText? = null

    var editPtaAddress : TextInputEditText? = null
    var spinnerStatus  : AppCompatSpinner? = null

    lateinit var ptaListModel: ArrayList<ManagePtaListModel.Pta>

    var typStr = -1

    var position = 0
    var pb: ProgressDialog? = null

    var typGenderStr = -1
    constructor()

    constructor(
        ptaDetailsListener: PTADetailsListener,
        ptaListModel: ArrayList<ManagePtaListModel.Pta>,
        position:Int) {
        this.ptaDetailsListener = ptaDetailsListener
        this.ptaListModel = ptaListModel
        this.position = position
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
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

        ptaViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[PtaViewModel::class.java]

        _binding = BottomSheetUpdatePtaBinding.inflate(inflater, container, false)
        return binding.root
        // return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pb = ProgressDialog(requireActivity())
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Loading...")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)

        shapeImageView = binding.shapeImageView
        val imageViewPlus = binding.imageViewPlus

        constraintLeave  = binding.constraintLeave

        editPtaName  = binding.editPtaName

        spinnerPtaRole  = binding.spinnerPtaRole
        editMobileNumber  = binding.editMobileNumber
        editEmailId  = binding.editEmailId
        editPtaAddress = binding.editPtaAddress
        spinnerStatus  = binding.spinnerStatus

        editPtaName?.setText(ptaListModel[position].pTAMEMBERNAME)
        editMobileNumber?.setText(ptaListModel[position].pTAMEMBERMOBILE)
        editEmailId?.setText(ptaListModel[position].pTAMEMBEREMAIL)

        editPtaAddress?.setText(ptaListModel[position].pTAMEMBERADDRESS)




        if (ptaListModel[position].pTAMEMBERIMAGE.isNotEmpty()) {
            Glide.with(requireActivity()).load(Global.event_url + "/Photos/PtaMemberImage/" + ptaListModel[position].pTAMEMBERIMAGE)
                .into(shapeImageView!!)
            // profile_str=Global.event_url+"/Photos/StaffImage"+STAFF_IMAGE;
        }

        var ty1 = ""
        when (ptaListModel[position].pTAMEMBERROLE) {
            1 -> {
                ty1 =
                    "President" + "~" + "Vice-President" + "~" + "Secretary" + "~" + "Join-Secretary" + "~" + "Cashier" + "~" + "Member" +
                            "~" + "Other"
            }
            2 -> {
                ty1 =
                    "Vice-President" + "~" + "Secretary" + "~" + "Join-Secretary" + "~" + "Cashier" + "~" + "Member" + "~" + "Other" +
                            "~" + "President"
            }
            3 -> {
                ty1 =
                    "Secretary" + "~" + "Join-Secretary" + "~" + "Cashier" + "~" + "Member" + "~" + "Other" + "~" + "President" +
                            "~" + "Vice-President"
            }
            4 -> {
                ty1 =
                    "Join-Secretary" + "~" + "Cashier" + "~" + "Member" + "~" + "Other" + "~" + "President" + "~" + "Vice-President" +
                            "~"+ "Secretary"
            }
            5 -> {
                ty1 =
                    "Cashier" + "~" + "Member" + "~" + "Other" + "~" + "Secretary" + "~" + "Vice-President" + "~" + "President" + "~" +
                            "Join-Secretary"
            }
            6 -> {
                ty1 =
                    "Member" + "~" + "Other" + "~" + "President" + "~" + "Vice-President" + "~" + "Secretary" + "~" + "Join-Secretary" + "~" +
                            "Cashier"
            }
            7 -> {
                ty1 =
                    "Other" + "~" + "President" + "~" + "Vice-President" + "~" + "Secretary" + "~" + "Join-Secretary" + "~" + "Cashier" + "~" +
                            "Member"
            }
        }
        ptarole = ty1.split("~").toTypedArray()

        val spiPtaRole = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, ptarole)
        spiPtaRole.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPtaRole?.adapter = spiPtaRole
        spinnerPtaRole?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                if (ptarole[position] == "President") {
                    typePtaRole = "1"
                } else if (ptarole[position] == "Vice-President") {
                    typePtaRole = "2"
                } else if (ptarole[position] == "Secretary") {
                    typePtaRole = "3"
                } else if (ptarole[position] == "Join-Secretary") {
                    typePtaRole = "4"
                } else if (ptarole[position] == "Cashier") {
                    typePtaRole = "5"
                } else if (ptarole[position] == "Member") {
                    typePtaRole = "6"
                } else if (ptarole[position] == "Other") {
                    typePtaRole = "7"
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        //spinnerStatus

        var str = ""
        typStr = ptaListModel[position].pTAMEMBERSTATUS
        str = if (typStr == 0) {
            "UnPublished~Publish"
        } else {
            "Publish~UnPublished"
        }
        type = str.split("~").toTypedArray()
        val spiStatus = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, type)
        spiStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus?.adapter = spiStatus
        spinnerStatus?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                if (type[position] == "UnPublished") {
                    typeStatus = "0"
                } else if (type[position] == "Publish") {
                    typeStatus = "1"
                }

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

        imageViewPlus.setOnClickListener {
            if (requestPermission()) {
//                selectImage()
                val imageCollection = sdk29AndUp {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val intent = Intent(Intent.ACTION_PICK, imageCollection)
                //     intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.type = "image/*"
                startForResult.launch(intent)

            }
        }



        val submitButton = binding.buttonSubmit
        submitButton.setOnClickListener {

            if(ptaViewModel.validateField(editPtaName!!,"Enter PTA Member Name",requireActivity(),constraintLeave!!)
                && ptaViewModel.validateFieldStr(typePtaRole,"Select PTA Role",requireActivity(),constraintLeave!!)
                && ptaViewModel.validateField(editMobileNumber!!,"Enter Mobile Number",requireActivity(),constraintLeave!!)
                && ptaViewModel.validateField(editEmailId!!,"Enter Email ID",requireActivity(),constraintLeave!!)) {

                if(fileNameList.isNotEmpty()){
                  //  pb?.show()
                    progressStart()
                    onFileUploadClick(imageSelection)
                }else{
                   // pb?.show()
                    val jsonObject = JSONObject()
                    try {
                        jsonObject.put("PTA_MEMBER_NAME", editPtaName?.text.toString())
                        jsonObject.put("PTA_MEMBER_ROLE", typePtaRole)
                        jsonObject.put("PTA_MEMBER_MOBILE", editMobileNumber?.text.toString())
                        jsonObject.put("PTA_MEMBER_ADDRESS", editPtaAddress?.text.toString())
                        jsonObject.put("PTA_MEMBER_EMAIL", editEmailId?.text.toString())
                        jsonObject.put("PTA_MEMBER_IMAGE", ptaListModel[position].pTAMEMBERIMAGE)
                        jsonObject.put("PTA_MEMBER_STATUS", typeStatus)
                        jsonObject.put("PTA_MEMBER_ID", ptaListModel[position].pTAMEMBERID)
                        jsonObject.put("SCHOOL_ID", schoolId)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    Log.i(TAG, "jsonObject $jsonObject")
                    val accademicRe =
                        jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    ptaDetailsListener.onSubmitClicker("Pta/UpdatePta",accademicRe,position,
                        editPtaName?.text.toString(),typePtaRole,editMobileNumber?.text.toString(),
                        editPtaAddress?.text.toString(),editEmailId?.text.toString(),
                        ptaListModel[position].pTAMEMBERIMAGE,typeStatus
                    )
                }

            }
        }

        val constraintLeave = binding.constraintLeave
        constraintLeave.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLeave.windowToken, 0)
        }

        binding.textDeleteIcon.setOnClickListener {
            ptaDetailsListener.onDeleteClicker(ptaListModel[position])
        }

    }




    ///permission Part
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data
                Log.i(TAG, "data $data")

                //If single image selected
                if (data?.data != null) {
                    val imageUri: Uri? = data.data
                    fileNameList.add(
                        FileList(
                            0,
                            imageUri.toString(),
                            "",
                            "Local",
                            0
                        )
                    )
                    imageSelection = FileUtils.getReadablePathFromUri(requireActivity(), imageUri!!)!!
                    Glide.with(requireActivity())
                        .load(imageUri)
                        .into(shapeImageView!!)
                }

            }

        }


    fun onFileUploadClick(selectedFilePath: String) {


        var SERVER_URL = "PtaUpload/UploadFiles"

        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(TAG,"selectedFilePath $selectedFilePath");
        filesToUpload[0] = File(selectedFilePath)
        Log.i(TAG,"filesToUpload $filesToUpload");

        //  showProgress("Uploading media ...",perProgressBar,textViewProgress)
        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload,"", object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
              //  pb?.dismiss()
                progressStop()
                //   hideProgress(perProgressBar,textViewProgress)
                Log.i(TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {
                //  hideProgress(perProgressBar,textViewProgress)
               // submitFile(responses[0])
                progressStop()
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("PTA_MEMBER_NAME", editPtaName?.text.toString())
                    jsonObject.put("PTA_MEMBER_ROLE", typePtaRole)
                    jsonObject.put("PTA_MEMBER_MOBILE", editMobileNumber?.text.toString())
                    jsonObject.put("PTA_MEMBER_ADDRESS", editPtaAddress?.text.toString())
                    jsonObject.put("PTA_MEMBER_EMAIL", editEmailId?.text.toString())
                    jsonObject.put("PTA_MEMBER_IMAGE", responses[0])
                    jsonObject.put("PTA_MEMBER_STATUS", typeStatus)
                    jsonObject.put("PTA_MEMBER_ID", ptaListModel[position].pTAMEMBERID)
                    jsonObject.put("SCHOOL_ID", schoolId)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.i(TAG, "jsonObject $jsonObject")
                val accademicRe =
                    jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                ptaDetailsListener.onSubmitClicker("Pta/UpdatePta", accademicRe, position,
                    editPtaName?.text.toString(),typePtaRole,editMobileNumber?.text.toString(),
                    editPtaAddress?.text.toString(),editEmailId?.text.toString(),
                    responses[0],typeStatus)
            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                //  updateProgress(totalpercent, "Uploading file $filenumber", "", perProgressBar,textViewProgress)
                Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })

    }


//    private fun selectImage() {
//        Log.i(TAG, "selectImage")
//        CropImage.activity()
//            .setGuidelines(CropImageView.Guidelines.ON)
////            .setScaleType(CropImageView.ScaleType.FIT_CENTER)
//            .setAspectRatio(1,1) //You can skip this for free form aspect ratio)
//            .start(requireActivity(),this)
//
//    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG, "requestCode $requestCode")
        Log.i(TAG, "resultCode $resultCode")
        Log.i(TAG, "data $data")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val resultUri = result.uri
                Log.i(TAG, "uri $resultUri");
                imageSelection = Utils.getPathFromUri(requireActivity(), resultUri)!!
                Log.i(TAG, "imageUpload $imageSelection");
//                // Set uri as Image in the ImageView:
                Glide.with(requireActivity())
                    .load(resultUri)
                    .into(shapeImageView!!)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Log.i(TAG, "error $error");
            }
        }
    }



//    private fun submitFile(uploadedFileName: String) {
//
//        var url = "Pta/UpdatePta"
//
//        val jsonObject = JSONObject()
//        try {
//            jsonObject.put("PTA_MEMBER_NAME", editPtaName?.text.toString())
//            jsonObject.put("PTA_MEMBER_ROLE", typePtaRole)
//            jsonObject.put("PTA_MEMBER_MOBILE", editMobileNumber?.text.toString())
//            jsonObject.put("PTA_MEMBER_ADDRESS", editPtaAddress?.text.toString())
//            jsonObject.put("PTA_MEMBER_EMAIL", editEmailId?.text.toString())
//            jsonObject.put("PTA_MEMBER_IMAGE", uploadedFileName)
//            jsonObject.put("PTA_MEMBER_STATUS", typeStatus)
//            jsonObject.put("PTA_MEMBER_ID", ptaListModel[position].pTAMEMBERID)
//            jsonObject.put("SCHOOL_ID", schoolId)
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//        Log.i(TAG, "jsonObject $jsonObject")
//        val accademicRe =
//            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
//        ptaViewModel.getCommonPostFun(url, accademicRe)
//            .observe(requireActivity(), Observer {
//                it?.let { resource ->
//                    Log.i(TAG, "resource ${resource.message}")
//                    Log.i(TAG, "errorBody ${resource.data?.errorBody()}")
//                    when (resource.status) {
//                        Status.SUCCESS -> {
//                            val response = resource.data?.body()!!
//                          //  progressStop()
//                            pb?.dismiss()
//                            when {
//                                Utils.resultFun(response) == "1" -> {
//                                    ptaDetailsListener.onShowMessageClicker("PTA Details Updated Successfully")
//                                    dialog?.dismiss()
//                                }
//                                Utils.resultFun(response) == "-1" -> {
//                                    ptaDetailsListener.onErrorMessageClicker("PTA Details Already Exist")
//                                }
//                                else -> {
//                                    ptaDetailsListener.onErrorMessageClicker("PTA updation Failed, Please Contact Support")
//                                }
//                            }
//                        }
//                        Status.ERROR -> {
//                        //    progressStop()
//                            pb?.dismiss()
//                            ptaDetailsListener.onErrorMessageClicker("Please try again after sometime")
//                        }
//                        Status.LOADING -> {
//                         //   progressStart()
//                            Log.i(CreateAssignmentDialog.TAG, "loading")
//                        }
//                    }
//                }
//            })
//
//    }

    fun progressStart() {
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

    companion object {
        var TAG = "BottomSheetFragment"
    }
}