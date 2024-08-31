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
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.theartofdev.edmodo.cropper.CropImage
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.DialogCreatePtaBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.FileUtils
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.permission.sdk29AndUp
import info.passdaily.camrelconvertapp.services.retrofit.RetrofitClientStaff
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.CreateAssignmentDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.UpdateAssignmentDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_staff.DialogCreateStaff
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.FileList
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.ArrayList

@Suppress("DEPRECATION")
class DialogCreatePta : DialogFragment {

    private lateinit var ptaViewModel: PtaViewModel

    companion object {
        var TAG = "DialogCreatePta"
    }

    var imageSelection = ""

    var fileNameList = ArrayList<FileList>()

    private var _binding: DialogCreatePtaBinding? = null
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
    var pb: ProgressDialog? = null

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var adminRole = 0
    var schoolId = 0

    var type = arrayOf( "Published","Unpublished")
    var typeStatus =""

    var typePtaRole =""

    var editPtaName : TextInputEditText? = null
    var spinnerPtaRole  : AppCompatSpinner? = null
    var editMobileNumber : TextInputEditText? = null
    var editEmailId : TextInputEditText? = null

    var editPtaAddress : TextInputEditText? = null
    var spinnerStatus  : AppCompatSpinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyleWhite)
    }

    constructor(ptaDetailsListener: PTADetailsListener) {
        this.ptaDetailsListener = ptaDetailsListener

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

        _binding = DialogCreatePtaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pb = ProgressDialog(requireActivity())
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Loading...")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)

        toolbar = binding.toolbar
        toolbar?.setNavigationIcon(R.drawable.ic_back_arrow_black)
        toolbar?.title = "Create PTA"
        toolbar?.setTitleTextColor(requireActivity().resources.getColor(R.color.black))

        toolbar?.setNavigationOnClickListener {
            cancelFrg()
        }

        shapeImageView = binding.shapeImageView
        val imageViewPlus = binding.imageViewPlus

        constraintLeave  = binding.constraintLeave

        editPtaName  = binding.editPtaName

        spinnerPtaRole  = binding.spinnerPtaRole
        editMobileNumber  = binding.editMobileNumber
        editEmailId  = binding.editEmailId
        editPtaAddress = binding.editPtaAddress
        spinnerStatus  = binding.spinnerStatus


        val spiPtaRole = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, ptarole)
        spiPtaRole.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPtaRole?.adapter = spiPtaRole
        spinnerPtaRole?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                typePtaRole = if (position == 0) {
                    "-1"
                } else {
                    position.toString()
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        //spinnerStatus
        val spiStatus = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, type)
        spiStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus?.adapter = spiStatus
        spinnerStatus?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                if (position == 0) {
                    typeStatus = "1"
                } else if (position == 1) {
                    typeStatus = "0"
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
//                && ptaViewModel.validateField(editEmailId!!,"Enter Email ID",requireActivity(),constraintLeave!!)
            ) {

                if(fileNameList.isNotEmpty()){
                    pb?.show()
                    onFileUploadClick(imageSelection)
                }else{
                    pb?.show()
                    submitFile("")
                }


            }
        }

        val constraintLeave = binding.constraintLeave
        constraintLeave.setOnClickListener {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraintLeave.windowToken, 0)
        }

    }


    ///permission Part
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data
                Log.i(DialogCreateStaff.TAG, "data $data")

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
        Log.i(DialogCreateStaff.TAG,"selectedFilePath $selectedFilePath");
        filesToUpload[0] = File(selectedFilePath)
        Log.i(DialogCreateStaff.TAG,"filesToUpload $filesToUpload");

        //  showProgress("Uploading media ...",perProgressBar,textViewProgress)
        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload,"", object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                pb?.dismiss()
                //   hideProgress(perProgressBar,textViewProgress)
                Log.i(DialogCreateStaff.TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {
                //  hideProgress(perProgressBar,textViewProgress)
                submitFile(responses[0])
//                for (i in responses.indices) {
//                    //val str = responses[i]
//                    textViewProgress.text = "Uploaded"
//                    perProgressBar.visibility = View.GONE
//                    imageViewDownloadButton.visibility = View.GONE
//                    //   Log.i(TAG, "RESPONSE $i ${responses[i]}")
//                    //submitFile(responses[i],fILETITLE,position)
//                    // if ((position + 1) == fileNameList.size) {
//                    //  arrayListItems += responses[i]+","
//                    fileNameList[position].fILENAME = responses[i]
//                    // }
//
//                }
            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                //  updateProgress(totalpercent, "Uploading file $filenumber", "", perProgressBar,textViewProgress)
                Log.i(DialogCreateStaff.TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })

        
//        if(selectedFilePath.isNotEmpty()) {
//            var SERVER_URL = "StaffUpload/UploadFiles"
//            Log.i(TAG, "selectedFilePath $selectedFilePath")
//
//            var imagenPerfil =
//                if (selectedFilePath.contains(".jpg") || selectedFilePath
//                        .contains(".jpeg")
//                    || selectedFilePath.contains(".png") || selectedFilePath
//                        .contains(".JPG") || selectedFilePath.contains(".JPEG")
//                    || selectedFilePath.contains(".PNG")
//                ) {
//                    val file1 = File(selectedFilePath)
//                    val requestFile: RequestBody =
//                        file1.asRequestBody("image/*".toMediaTypeOrNull())
//                    MultipartBody.Part.createFormData(
//                        "STUDY_METERIAL_FILE",
//                        file1.name,
//                        requestFile
//                    )
//
//                } else {
//                    val requestFile: RequestBody = RequestBody.create(
//                        "multipart/form-data".toMediaTypeOrNull(),
//                        selectedFilePath!!
//                    )
//                    // MultipartBody.Part is used to send also the actual file name
//                    MultipartBody.Part.createFormData(
//                        "STUDY_METERIAL_FILE",
//                        selectedFilePath,
//                        requestFile
//                    )
//                }
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
//                        Log.i(CreateAssignmentDialog.TAG, "response  $response")
//                        var uploadedFileName = response!!.dETAILS
//                        submitFile(uploadedFileName)
//                    }
//
//                }
//
//                override fun onFailure(call: Call<FileResultModel>, t: Throwable) {
//                    Log.i(CreateAssignmentDialog.TAG, "Throwable  $t")
//                }
//            })
//        }else{
//            submitFile("")
//        }

    }


    


//    private fun selectImage() {
//        Log.i(TAG, "selectImage")
//        CropImage.activity()
////            .setGuidelines(CropImageView.Guidelines.ON)
////            .setScaleType(CropImageView.ScaleType.FIT_CENTER)
////            .setAspectRatio(1,1) //You can skip this for free form aspect ratio)
//            .start(requireActivity(),this)
//
//    }
//
//
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        Log.i(TAG, "requestCode $requestCode")
//        Log.i(TAG, "resultCode $resultCode")
//        Log.i(TAG, "data $data")
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result = CropImage.getActivityResult(data)
//            if (resultCode == AppCompatActivity.RESULT_OK) {
//                val resultUri = result.uri
//                Log.i(TAG, "uri $resultUri");
//                imageSelection = Utils.getPathFromUri(requireActivity(), resultUri)!!
//                Log.i(TAG, "imageUpload $imageSelection");
////                // Set uri as Image in the ImageView:
//                Glide.with(requireActivity())
//                    .load(resultUri)
//                    .into(shapeImageView!!)
//
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                val error = result.error
//            }
//        }
//    }



    private fun submitFile(uploadedFileName: String) {

        var url = "Pta/AddPta"

        val jsonObject = JSONObject()
        try {
            jsonObject.put("PTA_MEMBER_NAME", editPtaName?.text.toString())
            jsonObject.put("PTA_MEMBER_ROLE", typePtaRole)
            jsonObject.put("PTA_MEMBER_MOBILE", editMobileNumber?.text.toString())
            jsonObject.put("PTA_MEMBER_ADDRESS", editPtaAddress?.text.toString())
            jsonObject.put("PTA_MEMBER_EMAIL", editEmailId?.text.toString())
            jsonObject.put("PTA_MEMBER_IMAGE", uploadedFileName)
            jsonObject.put("PTA_MEMBER_STATUS", typeStatus)
            jsonObject.put("SCHOOL_ID", schoolId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG, "jsonObject $jsonObject")
        val accademicRe =
            jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        ptaViewModel.getCommonPostFun(url, accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG, "resource ${resource.message}")
                    Log.i(TAG, "errorBody ${resource.data?.errorBody()}")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            pb?.dismiss()
                        //    progressStop()
                            when {
                                Utils.resultFun(response) == "1" -> {
                                    ptaDetailsListener.onShowMessageClicker("PTA Details Added Successfully")
                                    cancelFrg()
                                }
                                Utils.resultFun(response) == "-1" -> {
                                    ptaDetailsListener.onErrorMessageClicker("PTA Details Already Exist")
                                }
                                else -> {
                                    ptaDetailsListener.onErrorMessageClicker("PTA Adding Failed, Please Contact Support")
                                }
                            }
                        }
                        Status.ERROR -> {
                            pb?.dismiss()
                         //   progressStop()
                            ptaDetailsListener.onErrorMessageClicker("Please try again after sometime")
                        }
                        Status.LOADING -> {
                      //      progressStart()
                            Log.i(CreateAssignmentDialog.TAG, "loading")
                        }
                    }
                }
            })

    }


    private fun cancelFrg() {
        val prev = requireActivity().supportFragmentManager.findFragmentByTag(TAG)
        if (prev != null) {
            val df = prev as DialogFragment
            df.dismiss()
        }
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