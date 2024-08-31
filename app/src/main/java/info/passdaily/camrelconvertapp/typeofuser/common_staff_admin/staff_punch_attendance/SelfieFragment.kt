package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.staff_punch_attendance

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Camera
import android.icu.text.SimpleDateFormat
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivitySelfieBinding
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.lib.upload_progress.FileUploader
import info.passdaily.camrelconvertapp.services.FileUtils
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Utils.Companion.compressImage
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.typeofuser.testing_area.staff_attendance_test.Overlay
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SelfieFragment() : Fragment() {



    private lateinit var cameraExecutor: ExecutorService
   // private lateinit var binding: ActivitySelfieBinding
    private var _binding: ActivitySelfieBinding? = null
    private val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File

    var toolbar: Toolbar? = null

    private var mCamera: Camera? = null

    private lateinit var overlay: Overlay

    private var detector: FaceDetector? = null

    var editedBitmap: Bitmap? = null

    var imageViewCapture : ImageView? = null
    var textViewStatus : TextView? = null

    var photoFile: File? = null
    val CAPTURE_IMAGE_REQUEST = 1
    var mCurrentPhotoPath: String? = null

    var imageTakenPath = ""

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0
    var adminRole = 0

    var pb: ProgressDialog? = null

    var filePath = ""

    var fragment: Fragment = this
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var cameraSelector: CameraSelector

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.screenState = "staffAttendance"
        // Inflate the layout for this fragment
        _binding = ActivitySelfieBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pb = ProgressDialog(requireActivity())
        pb?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        pb?.setMessage("Capturing...")
        pb?.isIndeterminate = true
        pb?.setCancelable(false)

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID
        adminRole = user[0].ADMIN_ROLE


        imageViewCapture = binding.ImageViewCapture

        // textViewStatus = binding.textViewStatus

//        overlay = Overlay(this)
//        val layoutOverlay = ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        )
//        this.addContentView(overlay,layoutOverlay)



//        detector = FaceDetector.Builder(applicationContext)
//            .setTrackingEnabled(false)
//            .setLandmarkType(FaceDetector.ALL_LANDMARKS)
//           // .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
//            .build()
        //startACamera()

        // Set up the listener for take photo button
        binding.captureButton.setOnClickListener {
//            if (allPermissionsGranted()) {
////                startCamera()
//                takePhoto()
//            } else {
//                ActivityCompat.requestPermissions(
//                    this,
//                    REQUIRED_PERMISSIONS,
//                    REQUEST_CODE_PERMISSIONS
//                )
//            }

            //  captureImage()

            pb?.show()
            takeAPhoto()

        }

        binding.retakeButton.setOnClickListener {
            // if(imageTaken){
            binding.retakeButton.visibility = View.GONE
            imageViewCapture?.visibility = View.GONE
            binding.viewFinder.visibility = View.VISIBLE
            binding.captureButton.visibility = View.VISIBLE
            binding.submitButton.visibility = View.GONE

//            val fdelete = File(imageTakenPath)
//            if (fdelete.exists()) {
//                if (fdelete.delete()) {
//                    Log.i(TAG,"file Deleted :$imageTakenPath")
//                } else {
//                    Log.i(TAG,"file not Deleted :$imageTakenPath")
//                }
//            }
            startACamera()
            //   }
        }
        binding.submitButton.setOnClickListener {
            //   finish()
            progressStart()
            selfieImage(filePath)
        }




        val cameraProviderResult = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permissionGranted->
            if(permissionGranted){
                // cut and paste the previous startCamera() call here.
                startACamera()
            }else {
                Snackbar.make(binding.root,"The camera permission is required", Snackbar.LENGTH_INDEFINITE).show()
            }
        }

        cameraProviderResult.launch(android.Manifest.permission.CAMERA)

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())
        cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    }


    private fun startACamera(){
        //  imageTaken = false
        // listening for data from the camera
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()


            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // connecting a preview use case to the preview in the xml file.
            val preview = Preview.Builder().build().also{
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            try{
                // clear all the previous use cases first.
                cameraProvider.unbindAll()
                // binding the lifecycle of the camera to the lifecycle of the application.
                cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)
            } catch (e: Exception) {
                Log.i(TAG, "Use case binding failed")
            }

        }, ContextCompat.getMainExecutor(requireActivity()))
    }


    private fun takeAPhoto(){
        //imageCapture?.let{
        val imageCapture = imageCapture ?: return
        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = true
        }
        if(!outputDirectory.exists()) {
            outputDirectory = getOutputDirectory()
        }
        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )


        //Create a storage location whose fileName is timestamped in milliseconds.
        // val fileName = "JPEG_${System.currentTimeMillis()}"
        //  val file = File(externalMediaDirs[0],fileName)

        // Save the image in the above file
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .setMetadata(metadata).build()

        /* pass in the details of where and how the image is taken.(arguments 1 and 2 of takePicture)
        pass in the details of what to do after an image is taken.(argument 3 of takePicture) */

        imageCapture.takePicture(
            outputFileOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults){
                    var photoUri = photoFile.toUri()
                    Log.i(TAG,"The image has been saved in $photoUri")
                    var selectedFilePath = FileUtils.getReadablePathFromUri(requireActivity(), photoUri)

                    filePath = compressImage(selectedFilePath,requireActivity())

                    showImageMethod(photoFile.toUri())
                   // filePath = photoFile.toUri()
                    pb?.dismiss()

//                        binding.viewFinder.visibility = View.GONE
//                        imageViewCapture?.visibility = View.VISIBLE
//                        Glide.with(this@SelfieActivity)
//                            .load(file.toUri())
//                            .into(imageViewCapture!!)



                }

                override fun onError(exception: ImageCaptureException) {
                    pb?.dismiss()
                    Toast.makeText(
                        binding.root.context,
                        "Error taking photo",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(TAG, "Error taking photo:$exception")
                }

            })
        //   }
    }
    fun showImageMethod(file :Uri){
        requireActivity().runOnUiThread {
            imageTakenPath = file.toString()
            binding.retakeButton.visibility = View.VISIBLE
            binding.captureButton.visibility = View.GONE
            binding.viewFinder.visibility = View.GONE
            imageViewCapture?.visibility = View.VISIBLE
            binding.submitButton.visibility = View.VISIBLE

            Log.i(TAG,"file $file")
            Glide.with(requireActivity())
                .load(file)
                .into(imageViewCapture!!)
            //    imageTaken = true
        }
    }




    fun selfieImage(uriFilePath : String){
        //http://staff.teachdaily.in//ElixirApi/Staff/UploadSelfie
       // var selectedFilePath = FileUtils.getReadablePathFromUri(requireActivity(), uriFilePath)
        var SERVER_URL = "Staff/UploadSelfie"
//        if (selectedFilePath!!.contains(".jpg") || selectedFilePath.contains(".jpeg") || selectedFilePath.contains(".png")
//            || selectedFilePath.contains(".JPG") || selectedFilePath.contains(".JPEG") || selectedFilePath.contains(".PNG")) {
//
//            selectedFilePath = compressImage(selectedFilePath,requireActivity())
//         //   Log.i(TAG, "FilePath_scaled $selectedFilePath");
//        }

        val filesToUpload = arrayOfNulls<File>(1)

        Log.i(TAG,"selectedFilePath $uriFilePath");
        filesToUpload[0] = File(uriFilePath)

        val fileUploader = FileUploader(adminRole)
        fileUploader.uploadFiles(SERVER_URL, "STUDY_METERIAL_FILE", filesToUpload,"", object :
            FileUploader.FileUploaderCallback {
            override fun onError() {
                progressStop()
                Log.i(TAG,"onError ")
            }

            override fun onFinish(responses: Array<String>) {

                var commentUplodedFile = responses[0]
                Log.i(TAG,"commentUplodedFile $commentUplodedFile")
                // submitFile(commentUplodedFile)
                progressStop()
             //   staffPunchInterface.onSelfieUploaded(commentUplodedFile)
                //     Global.selfieImageResponse = commentUplodedFile

//               requireActivity().supportFragmentManager.beginTransaction().remove(fragment).commit();

                var fragmentManager = requireActivity().supportFragmentManager
                var fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.nav_staff_host_fragment, StaffAttendanceFragment(1,commentUplodedFile)).commit()
            }

            override fun onProgressUpdate(currentpercent: Int, totalpercent: Int, filenumber: Int) {
                Log.i(TAG,"Progress Status $currentpercent $totalpercent $filenumber")
            }
        })
    }



//
//    @Throws(java.lang.Exception::class)
//    private fun scanFaces() {
//        val bitmap: Bitmap = decodeBitmapUri(this, imageUri)!!
//        if (detector!!.isOperational && bitmap != null) {
//            editedBitmap = Bitmap.createBitmap(
//                bitmap.width, bitmap
//                    .height, bitmap.config
//            )
//            val scale = resources.displayMetrics.density
//            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
//            paint.color = Color.rgb(255, 61, 61)
//            paint.textSize = (14 * scale).toInt().toFloat()
//            paint.setShadowLayer(1f, 0f, 1f, Color.WHITE)
//            paint.style = Paint.Style.STROKE
//            paint.strokeWidth = 3f
//            val canvas = Canvas(editedBitmap!!)
//            canvas.drawBitmap(bitmap, 0, 0, paint)
//            val frame = Frame.Builder().setBitmap(editedBitmap).build()
//            val faces: SparseArray<Face> = detector!!.detect(frame)
//           // scanResults.setText(null)
//            for (index in 0 until faces.size()) {
//                val face: Face = faces.valueAt(index)
//                canvas.drawRect(
//                    face.position.x,
//                    face.position.y,
//                    face.position.x + face.width,
//                    face.position.y + face.height, paint
//                )
//                Log.i(TAG,"Face " + (index + 1) + "\n")
//                Log.i(TAG,"${face.isSmilingProbability}")
//                Log.i(TAG,"${face.isLeftEyeOpenProbability}")
//                Log.i(TAG,"$face.isRightEyeOpenProbability")
//
////                scanResults.setText(scanResults.getText() + "Face " + (index + 1) + "\n")
////                scanResults.setText(scanResults.getText() + "Smile probability:" + "\n")
////                scanResults.setText(scanResults.getText() + String.valueOf(face.isSmilingProbability) + "\n")
////                scanResults.setText(scanResults.getText() + "Left Eye Open Probability: " + "\n")
////                scanResults.setText(scanResults.getText() + String.valueOf(face.isLeftEyeOpenProbability) + "\n")
////                scanResults.setText(scanResults.getText() + "Right Eye Open Probability: " + "\n")
////                scanResults.setText(scanResults.getText() + String.valueOf(face.isRightEyeOpenProbability) + "\n")
////                scanResults.setText(scanResults.getText() + "---------" + "\n")
//                for (landmark in face.landmarks) {
//                    val cx = landmark.position.x.toInt()
//                    val cy = landmark.position.y.toInt()
//                    canvas.drawCircle(cx.toFloat(), cy.toFloat(), 5f, paint)
//                }
//            }
//            if (faces.size() == 0) {
//                Log.i(TAG,"Scan Failed: Found nothing to scan")
//               // scanResults.setText("Scan Failed: Found nothing to scan")
//            } else {
//                Log.i(TAG,"No of Faces Detected: ")
////                imageView.setImageBitmap(editedBitmap)
////                scanResults.setText(scanResults.getText() + "No of Faces Detected: " + "\n")
////                scanResults.setText(scanResults.getText() + String.valueOf(faces.size()) + "\n")
////                scanResults.setText(scanResults.getText() + "---------" + "\n")
//            }
//        } else {
//            Log.i(TAG,"Could not set up the detector!")
//            //scanResults.setText("Could not set up the detector!")
//        }
//    }
//
//    @Throws(FileNotFoundException::class)
//    private fun decodeBitmapUri(ctx: Context, uri: Uri): Bitmap? {
//        val targetW = 600
//        val targetH = 600
//        val bmOptions: BitmapFactory.Options = BitmapFactory.Options()
//        bmOptions.inJustDecodeBounds = true
//        BitmapFactory.decodeStream(ctx.contentResolver.openInputStream(uri), null, bmOptions)
//        val photoW: Int = bmOptions.outWidth
//        val photoH: Int = bmOptions.outHeight
//        val scaleFactor = (photoW / targetW).coerceAtMost(photoH / targetH)
//        bmOptions.inJustDecodeBounds = false
//        bmOptions.inSampleSize = scaleFactor
//        return BitmapFactory.decodeStream(
//            ctx.contentResolver
//                .openInputStream(uri), null, bmOptions
//        )
//    }
//


    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.file_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }




    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireActivity())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val myPreviewView: PreviewView = binding.viewFinder
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(myPreviewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            // ImageAnalysis UseCase
            //  val analysisUseCase = ImageAnalysis.Builder().build().also { it.setAnalyzer(cameraExecutor, FaceAnalyzer(lifecycle,overlay)) }

//bind image analysis
            //bind image analysis
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(myPreviewView.width, myPreviewView.height))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                //  val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                // Analyze image. Make sure to close it before returning from the method, otherwise the pipeline will be blocked.

                //START configuration of the facedetector
                val realTimeOpts = FaceDetectorOptions.Builder()
                    .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                    .build()

                val detector: FaceDetector = FaceDetection.getClient(realTimeOpts)
                //END of configuration of face detector

                val image = InputImage.fromMediaImage(imageProxy.image as Image, imageProxy.imageInfo.rotationDegrees)
                Log.i(TAG, "InputImage: $image")

//                detector.process(image)
//                    .addOnSuccessListener(successListener)
//                    .addOnFailureListener(failureListener)
//                    .addOnCompleteListener{
//                        imageProxy.close()
//                    }
            }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

                cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageAnalysis)
                ///https://www.meekcode.com/blog/camerax-live-face-detection



            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireActivity()))
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity(), it
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireActivity()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.i(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    //  val msg = "Photo capture succeeded: $savedUri"
                    Log.i(TAG, "savedUri: $savedUri")
//                    val msg = "Attendance Marked"
//                    Toast.makeText(this@SelfieActivity, msg, Toast.LENGTH_SHORT).show()
//                    Log.i(TAG, msg)
//                    finish()
                }
            })
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyyMMMdd_HHmmss"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
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
        if (fragment != null) { requireActivity().supportFragmentManager.beginTransaction().remove(fragment)
            .commitAllowingStateLoss()
        }
    }

}