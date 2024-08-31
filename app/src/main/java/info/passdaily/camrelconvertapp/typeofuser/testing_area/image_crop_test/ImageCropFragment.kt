//package info.passdaily.camrelconvertapp.typeofuser.testing_area.image_crop_test
//
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import androidx.activity.result.ActivityResultLauncher
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.annotation.Nullable
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import com.bumptech.glide.Glide
//import com.theartofdev.edmodo.cropper.CropImage
//import info.passdaily.camrelconvertapp.R
//import info.passdaily.camrelconvertapp.databinding.FragmentImageCropBinding
//import info.passdaily.camrelconvertapp.databinding.FragmentSimDetectionBinding
//import info.passdaily.camrelconvertapp.services.FileUtils
//import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
//import java.io.File
//
//
//class ImageCropFragment : Fragment() {
//
//    var TAG = "ImageCropFragment"
//
//    private var _binding: FragmentImageCropBinding? = null
//    private val binding get() = _binding!!
//
//
//    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
//
//    private var readPermission = false
//    private var writePermission = false
//
//    var imageViewPick : ImageView? = null
//    var imageViewCrop : ImageView? = null
//
//    var toolBarClickListener : ToolBarClickListener? = null
//    var mContext : Context? = null
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (mContext == null) {
//            mContext = context.applicationContext
//        }
//        try {
//            toolBarClickListener = context as ToolBarClickListener
//        } catch (e: Exception) {
//            Log.i(TAG, "Exception $e")
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        toolBarClickListener?.setToolbarName("Image Crop")
//
//        _binding = FragmentImageCropBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        imageViewPick = view.findViewById(R.id.imageViewPick)
//
//        imageViewCrop = view.findViewById(R.id.imageViewCrop)
//
//        imageViewPick?.setOnClickListener {
//            if (requestPermission()) {
//                CropImage.activity().start(requireActivity(),this);
//            }
//        }
//
//
//        permissionsLauncher =
//            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//                readPermission =
//                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
//                writePermission = permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
//                    ?: writePermission
//            }
//
//
//    }
//
//    override fun onActivityResult(
//        requestCode: Int,
//        resultCode: Int,
//        @Nullable data: Intent?
//    ) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            val result = CropImage.getActivityResult(data)
//            if (resultCode == android.app.Activity.RESULT_OK) {
//                val resultUri: Uri = result.uri
//                val mFile = FileUtils.getReadablePathFromUri(context, resultUri)
//                val file = resultUri.path?.let { File(it) }
//                Log.i(TAG,"resultUri $resultUri")
//                Log.i(TAG,"originalUri ${result.originalUri}")
//                Log.i(TAG,"mFile $mFile")
////                Picasso.get()
////                    .load(result.toString())
////                    .placeholder(R.drawable.image_icon)
////                    .into(imageViewPick!!);
//                Glide.with(requireActivity())
//                    .load(result.originalUri)
//                    .into(imageViewCrop!!)
//            }
//        }
//    }
//
//    fun requestPermission(): Boolean {
//
//        val hasReadPermission = ContextCompat.checkSelfPermission(
//            requireActivity(),
//            android.Manifest.permission.READ_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED
//
//        val hasWritePermission = ContextCompat.checkSelfPermission(
//            requireActivity(),
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED
//
//        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
//
//        readPermission = hasReadPermission
//        writePermission = hasWritePermission || minSdk29
//
//        val permissions = readPermission && writePermission
//
//        val permissionToRequests = mutableListOf<String>()
//        if (!writePermission) {
//            permissionToRequests.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
//
//        if (!readPermission) {
//            permissionToRequests.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
//
//        if (permissionToRequests.isNotEmpty()) {
//            permissionsLauncher.launch(permissionToRequests.toTypedArray())
//        }
//
//        return permissions
//    }
//
//}