package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album.upload_image

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.CircularProgressIndicator
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentUploadImageBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.permission.sdk29AndUp
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album.ManageAlbumViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.CreateAssignmentDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.FileList
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File

@Suppress("DEPRECATION")
class UploadImageFragment : Fragment(),ImageSelectListener {

    var TAG = "UploadImageFragment"
    private lateinit var manageAlbumViewModel: ManageAlbumViewModel
    private var _binding: FragmentUploadImageBinding? = null
    private val binding get() = _binding!!

    var aLBUMCATEGORYID = 0

    var arraylist = ""

    var SERVER_URL ="AlbumImage/UploadFiles"

    var fileNames = ArrayList<String>()

    var pb: ProgressDialog? = null

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var adminRole = 0

    var getAlbumCategory = ArrayList<ImageCategoryModel.ImageCat>()

    var spinnerAcademic : AppCompatSpinner? = null
    var recyclerView : RecyclerView? = null

    lateinit var studyMaterialAdapter : StudyMaterialAdapter

    var constraintLayoutContent : ConstraintLayout? = null

    var fileNameList = ArrayList<FileList>()
    var dummyFileName = ArrayList<String>()

    var toolBarClickListener : ToolBarClickListener? = null

    var constraintLayoutUpload : ConstraintLayout? = null

    var buttonSubmit : AppCompatButton? = null

    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private var readPermission = false
    private var writePermission = false
    var maxCount = 5
    var maxCountSelection = 5

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
        toolBarClickListener?.setToolbarName("Upload Image")
        // Inflate the layout for this fragment
        //  return inflater.inflate(R.layout.fragment_objective_exam_list, container, false)
        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE

        manageAlbumViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[ManageAlbumViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentUploadImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pb = ProgressDialog(requireActivity())
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Loading...")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)

        constraintLayoutContent = binding.constraintLayoutContent


        spinnerAcademic = binding.spinnerAcademic

        constraintLayoutUpload = binding.constraintLayoutUpload
        recyclerView = binding.recyclerView
      ///  recyclerView?.layoutManager = GridLayoutManager(requireActivity(),4)

        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())

        binding.accedemicText.text = requireActivity().resources.getText(R.string.select_year)


        spinnerAcademic?.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long) {
                aLBUMCATEGORYID = getAlbumCategory[position].aLBUMCATEGORYID
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        initFunction()

        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                readPermission =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                    ?: writePermission

            }



        constraintLayoutUpload?.setOnClickListener {
            if (requestPermission()) {
                if (fileNameList.size < maxCount) {
                    maxCountSelection = maxCount - fileNameList.size
//                    Utils.getSnackBarGreen(requireActivity(),"Select $maxCountSelection ",constraintLayoutContent!!)
                   Toast.makeText(requireActivity(), "Select $maxCountSelection ", Toast.LENGTH_SHORT).show()

                    val imageCollection = sdk29AndUp {
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val intent = Intent(Intent.ACTION_PICK, imageCollection)
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.type = "image/*"
                    startForResult.launch(intent)
                } else {
                    Utils.getSnackBar4K(requireActivity(), "Maximum Count Reached", binding.constraintLayoutContent)
                }
            }
        }
        buttonSubmit = binding.buttonSubmit
        buttonSubmit?.visibility = View.GONE
        buttonSubmit?.setOnClickListener {
//            if (fileNameList.size != 0) {
//                //    progressStart();
//                pb = ProgressDialog.show(requireActivity(), "", "Uploading...", true)
//                Thread {
//                    //creating new thread to handle Http Operations
//                    try {
//                        for (i in fileNameList.indices) {
//                            onFileUploadClick(i, fileNameList[i])
//                            SystemClock.sleep(5000)
//                        }
//                        //Your code goes here
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        Log.i(CreateAssignmentDialog.TAG, "Exception  ${e.printStackTrace()}")
//                    }
//                }.start()
//            } else {
//                Utils.getSnackBar4K(requireActivity(), "Select Atleast one image",  constraintLayoutContent!!)
//            }
        }

    }

    private fun initFunction() {
        manageAlbumViewModel.getImageCategory("Image/ImageCategoryList",0)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getAlbumCategory = response.imageCatList as ArrayList<ImageCategoryModel.ImageCat>
                            var years = Array(getAlbumCategory.size) { "" }
                            for (i in getAlbumCategory.indices) {
                                years[i] = getAlbumCategory[i].aLBUMCATEGORYNAME
                            }
                            if (spinnerAcademic != null) {
                                val adapter = ArrayAdapter(
                                    requireActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    years
                                )
                                spinnerAcademic?.adapter = adapter
                            }
                            Log.i(CreateAssignmentDialog.TAG, "initFunction SUCCESS")
                        }
                        Status.ERROR -> {
                            Log.i(CreateAssignmentDialog.TAG, "initFunction ERROR")
                        }
                        Status.LOADING -> {
                            Log.i(CreateAssignmentDialog.TAG, "initFunction LOADING")
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

    ///permission Part
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = it.data
                Log.i(CreateAssignmentDialog.TAG, "data $data")

                //If multiple image selected
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount ?: 0

                    val countPath = count + fileNameList.size
                    if (countPath > 5) {
                       Utils.getSnackBar4K(requireActivity(),"You select more then $maxCount",constraintLayoutContent!!)
                        //Toast.makeText(requireActivity(), "You select more then $maxCount", Toast.LENGTH_SHORT)
                         //   .show()
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
                if(fileNameList.size == 5){
                    constraintLayoutUpload?.visibility = View.GONE
                }else{
                    constraintLayoutUpload?.visibility = View.VISIBLE
                }
                recyclerView?.visibility = View.VISIBLE
                studyMaterialAdapter = StudyMaterialAdapter(
                    this,
                    fileNameList,
                    requireActivity(),
                    CreateAssignmentDialog.TAG
                )
                recyclerView?.adapter = studyMaterialAdapter

            }

        }


    @SuppressLint("NewApi")
    override fun onFileUploadClick( position: Int,
                                    fILEPATHName : String,
//        fILENAME: String,
                                    fILETITLE: String,
                                    perProgressBar: CircularProgressIndicator,
                                    imageViewDownloadButton: ImageView,
                                    textViewProgress: TextView) {



        val filesToUpload = arrayOfNulls<File>(1)
        // var selectedFilePath = FileUtils.getReadablePathFromUri(this, fILEPATHName.toUri())
        Log.i(TAG,"selectedFilePath $fILEPATHName");
        filesToUpload[0] = File(fILEPATHName)
        Log.i(TAG,"filesToUpload $filesToUpload");

        showProgress("Uploading media ...",perProgressBar,textViewProgress)
        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload, "",object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                hideProgress(perProgressBar,textViewProgress)
                Log.i(TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {
                hideProgress(perProgressBar,textViewProgress)
                for (i in responses.indices) {
                    //val str = responses[i]
                    textViewProgress.text = "Uploaded"
                    perProgressBar.visibility = View.GONE
                    imageViewDownloadButton.visibility = View.GONE

//                    arraylist += responses[i]+","
//                    if ((position + 1) == fileNameList.size) {
//                        Log.i(TAG,"removeLast ${removeLastChar(arraylist)}")
//                        submitFile(removeLastChar(arraylist),fILETITLE,position)
//
//                    }
                    submitFile(responses[i],fILETITLE,position)

                }
            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                updateProgress(totalpercent, "Uploading file $filenumber", "", perProgressBar,textViewProgress)
                Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })
//        pb!!.setMessage("Uploading  : ${position + 1} / ${fileNameList.size}")
//        if (fileList.fILETYPE == "Local") {
//            var selectedFilePath =
//                FileUtils.getReadablePathFromUri(requireActivity(), fileList.fILENAME.toUri())
//            Log.i(CreateAssignmentDialog.TAG, "position  $position")
//            val i = Log.i(CreateAssignmentDialog.TAG, "fileName  $selectedFilePath")
//
//            var imagenPerfil = if (selectedFilePath.toString().contains(".jpg") || selectedFilePath.toString()
//                    .contains(".jpeg")
//                || selectedFilePath.toString().contains(".png") || selectedFilePath.toString()
//                    .contains(".JPG") || selectedFilePath.toString().contains(".JPEG")
//                || selectedFilePath.toString().contains(".PNG")
//            ) {
//                val file1 = File(selectedFilePath!!)
//                val requestFile: RequestBody = file1.asRequestBody("image/*".toMediaTypeOrNull())
//                MultipartBody.Part.createFormData(
//                    "STUDY_METERIAL_FILE",
//                    file1.name,
//                    requestFile
//                )
//            } else {
//                val requestFile: RequestBody = RequestBody.create(
//                    "multipart/form-data".toMediaTypeOrNull(),
//                    selectedFilePath!!
//                )
//                // MultipartBody.Part is used to send also the actual file name
//                MultipartBody.Part.createFormData(
//                    "STUDY_METERIAL_FILE",
//                    selectedFilePath,
//                    requestFile
//                )
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
//                        Log.i(CreateAssignmentDialog.TAG, "response  $response")
//                        fileNames.add(response!!.dETAILS)
//                    }
//                    if ((position + 1) == fileNameList.size) {
//                        pb?.dismiss()
//                        val arraylist = java.lang.String.join(",", fileNames)
//                        Log.i(CreateAssignmentDialog.TAG, "str  $arraylist")
//                        submitFile(arraylist)
//                    }
//
//                }
//
//                override fun onFailure(call: Call<FileResultModel>, t: Throwable) {
//                    Log.i(CreateAssignmentDialog.TAG, "Throwable  $t")
//                }
//            })
//        } else {
//            Log.i(CreateAssignmentDialog.TAG, "str  ${fileNameList[position].fILENAME}")
//        }
//
//        if ((position + 1) == fileNameList.size) {
//
//            if (fileNames.size != 0) {
//                val arraylist = java.lang.String.join(",", fileNames)
//                Log.i(TAG, "str  $arraylist")
//                submitFile(arraylist)
//            }else{
//                submitFile("")
//            }
//        }
    }

    private fun removeLastChar(s: String): String{
//returns the string after removing the last character
        return s.substring(0, s.length - 1)
    }

    fun updateProgress(
        progress: Int,
        title: String?,
        msg: String?,
        perProgressBar: CircularProgressIndicator,
        textViewProgress: TextView
    ) {
        Log.i(TAG,"updateProgress $progress")
//        perProgressBar.setTitle(title)
//        perProgressBar.setMessage(msg)
        textViewProgress.text = "$progress %"
        perProgressBar.progress = progress
    }

    fun showProgress(str: String?, perProgressBar: CircularProgressIndicator, textViewProgress: TextView) {
        Log.i(TAG,"showProgress $str")
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
            Log.i(TAG,"hideProgress")
            // if (perProgressBar.isShowing) perProgressBar.dismiss()
        } catch (e: java.lang.Exception) {
        }
    }
    private fun submitFile(details: String,fILETITLE : String,position : Int) {


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

        val url = "Image/AlbumImageAdd"
        val jsonObject = JSONObject()
        try {
            jsonObject.put("ALBUM_TITLE", fILETITLE)
            jsonObject.put("ALBUM_FILE", details)
            jsonObject.put("ALBUM_CATEGORY_ID", aLBUMCATEGORYID)
            jsonObject.put("ALBUM_CREATED", adminId)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i(TAG,"jsonObject $jsonObject")
        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        manageAlbumViewModel.getCommonPostFun(url,accademicRe)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            pb?.dismiss()
                            when {
                                Utils.resultFun(response) == "SUCCESS" -> {
                                    Utils.getSnackBarGreen(requireActivity(), "Image Uploaded Successfully",  constraintLayoutContent!!)
                                       arraylist = ""
                                    if ((position + 1) == fileNameList.size) {
                                        fileNameList.clear()
//                                        studyMaterialAdapter.notifyDataSetChanged()
//                                        if (fileNameList.size == 0) {
                                            constraintLayoutUpload?.visibility = View.VISIBLE
                                            recyclerView?.visibility = View.GONE
//                                        }
                                        // materialListener.onCreateClick("")
//                                        fileNameList.removeAt(position)
                                        studyMaterialAdapter.notifyDataSetChanged()
                                    }

                                }
                                Utils.resultFun(response) == "FAIL" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Error while Uploading",  constraintLayoutContent!!)
                                }
                                Utils.resultFun(response) == "EXIST" -> {
                                    Utils.getSnackBar4K(requireActivity(), "Already File Existing",  constraintLayoutContent!!)
                                }
                            }
                        }
                        Status.ERROR -> {
                            pb?.dismiss()
                            Utils.getSnackBar4K(requireActivity(), "Please try again after sometime",  binding.constraintLayoutContent!!)
                        }
                        Status.LOADING -> {
                            Log.i(TAG,"loading")
                        }
                    }
                }
            })

    }


    class StudyMaterialAdapter(
        var imageSelectListener : ImageSelectListener,
        var materialList: ArrayList<FileList>,
        var context: Context, var TAG: String
    ) : RecyclerView.Adapter<StudyMaterialAdapter.ViewHolder>() {
        var downLoadPos = 0
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView: ImageView = view.findViewById(R.id.imageView)
            var imageViewOther: ImageView = view.findViewById(R.id.imageViewOther)
            var imageViewDelete: ImageView = view.findViewById(R.id.imageViewDelete)

            var imageViewDownloadButton : ImageView = view.findViewById(R.id.imageViewDownloadButton)

            var constraintDownload  : ConstraintLayout = view.findViewById(R.id.constraintDownload)
            var progressBar : ProgressBar = view.findViewById(R.id.progressBar)
            var imageViewClose : ImageView = view.findViewById(R.id.imageViewClose)
            var textViewPercentage  : TextView = view.findViewById(R.id.textViewPercentage)

            var textViewProgress   : TextView = view.findViewById(R.id.textViewProgress)

            var perProgressBar : CircularProgressIndicator = view.findViewById(R.id.perProgressBar)

            var textViewTitle : TextView = view.findViewById(R.id.textViewTitle)

            var constraintText : ConstraintLayout = view.findViewById(R.id.constraintText)

        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.study_material_progress_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            holder.textViewFileName.text = materialList[position].fILETITLE


            if(materialList[position].fILETITLE.isNotEmpty()){
                holder.textViewTitle.text = materialList[position].fILETITLE
            }else{
                holder.textViewTitle.text ="Enter File Name"
            }


            val path: String = materialList[position].fILENAME
            Log.i(TAG, "path $path")
            val mFile = FileUtils.getReadablePathFromUri(context, path.toUri())
            Log.i(TAG, "mFile $mFile")
            if(materialList[position].fILETYPE == "Local"){
                materialList[position].fILETYPE = "Json"
                holder.imageViewDownloadButton.visibility  =  View.GONE
                holder.perProgressBar.visibility  =  View.VISIBLE
                holder.textViewProgress.visibility  =  View.VISIBLE//,materialList[position].fILENAME
                imageSelectListener.onFileUploadClick(position,mFile!!,holder.textViewTitle.text.toString(),
                    holder.perProgressBar, holder.imageViewDownloadButton,holder.textViewProgress)
            }
//            else{
//               // imageSelectListener.onDeleteClick(position, materialList[position])
//                notifyItemRemoved(position)
//            }



            if (mFile.toString().contains(".jpg") || mFile.toString().contains(".jpeg")
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
            }

            holder.imageViewDownloadButton.setOnClickListener {
//                val path: String = materialList[position].fILENAME
//                Log.i(TAG, "path $path")
//                val mFile = FileUtils.getReadablePathFromUri(context, path.toUri())
//                Log.i(TAG, "mFile $mFile")
                holder.imageViewDownloadButton.visibility  =  View.GONE
                holder.perProgressBar.visibility  =  View.VISIBLE
                holder.textViewProgress.visibility  =  View.VISIBLE//,materialList[position].fILENAME
                imageSelectListener.onFileUploadClick(position,mFile!!,holder.textViewTitle.text.toString(),
                    holder.perProgressBar, holder.imageViewDownloadButton,holder.textViewProgress)
            }

            holder.constraintText.setOnClickListener {
                val inflate: View = LayoutInflater.from(context)
                    .inflate(R.layout.user_input_dialog_box, null as ViewGroup?)
                val builder = AlertDialog.Builder(context)
                builder.setView(inflate)
                val editText = inflate.findViewById<View>(R.id.userInputDialog) as EditText
                if(materialList[position].fILETITLE.isNotEmpty() && holder.textViewTitle.text.toString() != "Enter File Name"){
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
                imageSelectListener.onDeleteClick(position, materialList[position])
            }
        }

        override fun getItemCount(): Int {
            return materialList.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemViewType(position: Int): Int {
            return position
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


    @SuppressLint("NotifyDataSetChanged")
    override fun onDeleteClick(position: Int, fileList: FileList) {
        fileNameList.removeAt(position)
        studyMaterialAdapter.notifyDataSetChanged()
        constraintLayoutUpload?.visibility = View.VISIBLE
        if (fileNameList.size == 0) {
            recyclerView?.visibility = View.GONE

        }
    }

}

interface ImageSelectListener {
    fun onDeleteClick(
        position: Int,
        fileList: FileList
    )
    fun onFileUploadClick(
        position: Int,
        fILEPATHName : String,
//        fILENAME: String,
        fILETITLE: String,
        perProgressBar: CircularProgressIndicator,
        imageViewDownloadButton: ImageView,
        textViewProgress: TextView)
}
