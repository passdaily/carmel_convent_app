package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.gallery

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
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
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.ShimmerFrameLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentGalleryStaffBinding
import info.passdaily.camrelconvertapp.databinding.FragmentUploadImageBinding
import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
import info.passdaily.camrelconvertapp.lib.ProgressBarDialog
import info.passdaily.camrelconvertapp.lib.video.Video_Activity
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayerStaff
import info.passdaily.camrelconvertapp.services.localDB.LocalDBHelper
import info.passdaily.camrelconvertapp.services.permission.sdk29AndUp
import info.passdaily.camrelconvertapp.services.retrofit.RetrofitClientStaff
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.descriptive_exam.DescriptiveExamListener
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.descriptive_exam.DescriptiveExamStaffFragment
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album.ManageAlbumViewModel
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_assignment.CreateAssignmentDialog
import info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.study_material.FileList
import info.passdaily.camrelconvertapp.typeofuser.staff.ToolBarClickListener
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

@Suppress("DEPRECATION")
class ListingVideoFragment(var galleryClicker: GalleryClicker) : Fragment(),GallerySelectListener {

   // lateinit var galleryClicker: GalleryClicker

    var TAG = "ListingImageFragment"
    private lateinit var galleryStaffViewModel: GalleryStaffViewModel
    private var _binding: FragmentGalleryStaffBinding? = null
    private val binding get() = _binding!!

    var aLBUMCATEGORYID = 0


    var pb: ProgressDialog? = null

    private lateinit var localDBHelper : LocalDBHelper
    var adminId = 0

    var getAlbumCategory = ArrayList<ImageCategoryModel.ImageCat>()

   // var spinnerAcademic : AppCompatSpinner? = null
   var recyclerViewFiles  : RecyclerView? = null
    var recyclerView : RecyclerView? = null

    lateinit var mAdapter : GalleryStaffAdapter

    var constraintLayoutContent : ConstraintLayout? = null
    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var shimmerViewContainer: ShimmerFrameLayout? = null

    var getGalleryImageList = ArrayList<GalleryImageVideoModel.ImageGetAll>()

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

//    constructor(
//        galleryClicker: GalleryClicker
//    ) {
//        this.galleryClicker = galleryClicker
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        localDBHelper = LocalDBHelper(requireActivity())
        var user = localDBHelper.viewUser()
        adminId = user[0].ADMIN_ID

        galleryStaffViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayerStaff.services))
        )[GalleryStaffViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentGalleryStaffBinding.inflate(inflater, container, false)
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
        constraintEmpty = binding.constraintEmpty
        imageViewEmpty = binding.imageViewEmpty
        textEmpty = binding.textEmpty
        Glide.with(this)
            .load(R.drawable.ic_empty_progress_report)
            .into(imageViewEmpty!!)
        shimmerViewContainer = binding.shimmerViewContainer

//        spinnerAcademic = binding.spinnerAcademic

        recyclerViewFiles = binding.recyclerViewFiles
        recyclerViewFiles?.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)


        recyclerView = binding.recyclerView
        recyclerView?.layoutManager = GridLayoutManager(requireActivity(),2)

//        binding.accedemicText.text = requireActivity().resources.getText(R.string.select_album_category)
//
//        Log.i(TAG, "ListingImageFragment entered")
//        spinnerAcademic?.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long) {
//                aLBUMCATEGORYID = getAlbumCategory[position].aLBUMCATEGORYID
//
//                getImageFunction(aLBUMCATEGORYID)
//            }
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }

        initFunction()
    }

    private fun initFunction() {
        galleryStaffViewModel.getImageCategory("Video/VideoCategoryList",0)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!

                            getAlbumCategory = response.imageCatList as ArrayList<ImageCategoryModel.ImageCat>
                            if (isAdded) {
                                recyclerViewFiles?.adapter = SubjectAdapter(
                                    this,
                                    getAlbumCategory,
                                    mContext!!
                                )
                            }
//                            var years = Array(getAlbumCategory.size) { "" }
//                            for (i in getAlbumCategory.indices) {
//                                years[i] = getAlbumCategory[i].aLBUMCATEGORYNAME
//                            }
//                            if (spinnerAcademic != null) {
//                                val adapter = ArrayAdapter(
//                                    requireActivity(),
//                                    android.R.layout.simple_spinner_dropdown_item,
//                                    years
//                                )
//                                spinnerAcademic?.adapter = adapter
//                            }
                            if(getAlbumCategory.isNotEmpty()){
                                getImageFunction(getAlbumCategory[0].aLBUMCATEGORYID)
                            }
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


    override fun getImageFunction(aLBUMCATEGORYID : Int) {
        galleryStaffViewModel.getGalleryImageVideo("AlbumVideo/VideoGetAll",aLBUMCATEGORYID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    Log.i(TAG,"resource $resource")
                    when (resource.status) {
                        Status.SUCCESS -> {
                            shimmerViewContainer?.visibility = View.GONE
                            val response = resource.data?.body()!!
                            galleryClicker.onHideProgressClicker()
                            getGalleryImageList = response.imageGetAll as ArrayList<GalleryImageVideoModel.ImageGetAll>
                            if(getGalleryImageList.isNotEmpty()){

                                recyclerView?.visibility = View.VISIBLE
                                constraintEmpty?.visibility = View.GONE
                                if (isAdded) {
                                    mAdapter = GalleryStaffAdapter(
                                        this,
                                        getGalleryImageList,
                                        requireActivity(),
                                        TAG
                                    )
                                    recyclerView!!.adapter = mAdapter

                                }
                            }else{
                                recyclerView?.visibility = View.GONE
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
                            recyclerView?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.GONE

                            Glide.with(this)
                                .load(R.drawable.ic_no_internet)
                                .into(imageViewEmpty!!)
                            textEmpty?.text =  resources.getString(R.string.no_internet)
                            Log.i(TAG,"getSubjectList ERROR")
                        }
                        Status.LOADING -> {
                            recyclerView?.visibility = View.GONE
                            constraintEmpty?.visibility = View.GONE
                            shimmerViewContainer?.visibility = View.VISIBLE
                            getGalleryImageList = ArrayList<GalleryImageVideoModel.ImageGetAll>()
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

    class SubjectAdapter(
        val itemClickListener: GallerySelectListener,
        var subjects: ArrayList<ImageCategoryModel.ImageCat>,
        var context: Context
    ) :
        RecyclerView.Adapter<SubjectAdapter.ViewHolder>() {
        var index = 0

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textSubject: TextView = view.findViewById(R.id.textAssignmentName)
            var cardView: CardView = view.findViewById(R.id.cardView)
            var imageViewIcon: ImageView = view.findViewById(R.id.imageViewIcon)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.subjet_icon_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textSubject.text = subjects[position].aLBUMCATEGORYNAME
            holder.cardView.setOnClickListener {
                itemClickListener.getImageFunction(subjects[position].aLBUMCATEGORYID)
                index = position;
                notifyDataSetChanged()
            }
            Glide.with(context)
                .load(R.drawable.ic_exam_descriptive_icon)
                .into(holder.imageViewIcon)

            if (index == position) {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.green_400))
                holder.textSubject.setTextColor(context.resources.getColor(R.color.white))
                holder.imageViewIcon.setColorFilter(context.resources.getColor(R.color.white));
            } else {
                holder.cardView.setCardBackgroundColor(context.resources.getColor(R.color.white))
                holder.textSubject.setTextColor(context.resources.getColor(R.color.green_400))
                holder.imageViewIcon.setColorFilter(context.resources.getColor(R.color.green_400));
            }
        }

        override fun getItemCount(): Int {
            return subjects.size
        }

    }

    class GalleryStaffAdapter(
        var gallerySelectListener : GallerySelectListener,
        var getGalleryImageList: ArrayList<GalleryImageVideoModel.ImageGetAll>,
        var mContext: Context, var TAG: String
    ) : RecyclerView.Adapter<GalleryStaffAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageViewFile: ImageView = view.findViewById(R.id.imageViewFile)

        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.video_adapter, parent, false)
            return ViewHolder(itemView)
        }

        @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            Glide.with(context)
//                .load(Global.image_url+"/Video/"+getGalleryImageList[position].aLBUMFILE)
//                .apply(
//                    RequestOptions.centerCropTransform()
//                        .dontAnimate() //   .override(imageSize, imageSize)
//                        .placeholder(R.drawable.ic_image_view)
//                )
//                .thumbnail(0.5f)
//                .into(holder.imageViewFile)

            holder.itemView.setOnClickListener {

                val intent = Intent(mContext, ExoPlayerActivity::class.java)
                intent.putExtra("ALBUM_TITLE", getGalleryImageList[position].aLBUMTITLE)
                intent.putExtra("ALBUM_FILE", Global.image_url+"/video/"+getGalleryImageList[position].aLBUMFILE)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent)
//                val intent  = Intent(mContext, VideoPlayerActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                intent.putExtra("ALBUM_URL", Global.image_url+"/video/"+albumImageList[position].aLBUMFILE)
////                intent.putExtra("ALBUM_TITLE", albumImageList[position].aLBUMTITLE)
//                mContext.startActivity(intent)
                Global.ALBUM_URL = Global.image_url+"/video/"+getGalleryImageList[position].aLBUMFILE
                Global.ALBUM_TITLE = getGalleryImageList[position].aLBUMTITLE
                Global.ALBUM_CATEGORY_NAME = getGalleryImageList[position].aLBUMCATEGORYNAME
            }

        }

        override fun getItemCount(): Int {
            return getGalleryImageList.size
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewClick(position: Int, getGalleryImageList :GalleryImageVideoModel.ImageGetAll) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDeleteClick(position: Int, getGalleryImageList :GalleryImageVideoModel.ImageGetAll) {

    }

}

