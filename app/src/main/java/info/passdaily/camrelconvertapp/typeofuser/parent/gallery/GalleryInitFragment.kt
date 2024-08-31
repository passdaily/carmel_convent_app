package info.passdaily.camrelconvertapp.typeofuser.parent.gallery

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.tabs.TabLayout
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.FragmentGalleryInitBinding
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.services.localDB.parent.StudentDBHelper
import info.passdaily.camrelconvertapp.typeofuser.parent.absent.AbsentFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.absent.ItemClickListener
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image.GalleryImageFragment
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image.GalleryViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.video.GalleryVideoFragment
import kotlinx.coroutines.*

@DelicateCoroutinesApi
@Suppress("DEPRECATION")
class GalleryInitFragment : Fragment(), ItemClickListener {

    var TAG = "GalleryImageFragment"

    private var _binding: FragmentGalleryInitBinding? = null
    private val binding get() = _binding!!

    var STUDENTID = 0
    var CLASSID = 0
    var ACADEMICID = 0

    var viewPager: ViewPager? = null
    var tabLayout: TabLayout? = null

    var accedemicList: ArrayList<AccademicYearsModel.AccademicYear> =
        ArrayList<AccademicYearsModel.AccademicYear>()

/////gallery Image
    var  albumImageList: ArrayList<AlbumModel.Album> =
        ArrayList<AlbumModel.Album>()
//////gallery Video
    var  albumVideoList: ArrayList<AlbumModel.Album> =
        ArrayList<AlbumModel.Album>()

    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var accedemicId = 0
    var recyclerView : RecyclerView? =null

    var shimmerViewContainer : ShimmerFrameLayout? =null

    private lateinit var galleryViewModel: GalleryViewModel

    var mContext: Context? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (mContext == null) {
            mContext = context.applicationContext;
        }
        Log.i(TAG, "onAttach ")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Global.currentPage = 13
        Global.screenState = "landingpage"
        // Inflate the layout for this fragment
        galleryViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[GalleryViewModel::class.java]
    //    return inflater.inflate(R.layout.fragment_gallery_init, container, false)

        _binding = FragmentGalleryInitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var studentDBHelper = StudentDBHelper(requireActivity())
        var student = studentDBHelper.getStudentById(Global.studentId)
        STUDENTID = student.STUDENT_ID
        ACADEMICID = student.ACCADEMIC_ID
        CLASSID = student.CLASS_ID

        var textViewTitle = view.findViewById(R.id.textView32) as TextView
        textViewTitle.text = "Gallery"


        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView?.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
//        galleryViewModel.getAccademic(0)
//
//        constraintEmpty = view.findViewById(R.id.constraintEmpty)
//        imageViewEmpty = view.findViewById(R.id.imageViewEmpty)
//        textEmpty = view.findViewById(R.id.textEmpty)
//        if (isAdded) {
//            Glide.with(mContext!!)
//                .load(R.drawable.ic_empty_state_gallery)
//                .into(imageViewEmpty!!)
//        }
//        textEmpty?.text = "No Photos"

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)
//        recyclerViewAbsentList?.layoutManager = GridLayoutManager(requireActivity(),2)
        shimmerViewContainer = binding.shimmerViewContainer

        intiFunction()

    }

    private fun intiFunction() {
        galleryViewModel.getAccademicNew(ACADEMICID)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            accedemicList = response.accademicYears as ArrayList<AccademicYearsModel.AccademicYear>
                            if(accedemicList.isNotEmpty()){
                                accedemicId = accedemicList[0].aCCADEMICID
                            }
                            onClick(accedemicId)

                            if (isAdded) {
                                recyclerView?.adapter =
                                    AbsentFragment.AccademicAdapter(this, accedemicList, mContext!!)
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })


//        galleryViewModel.getAccademicObservable().observe(requireActivity(), {
//            if (it != null) {
//                accedemicList = it.accademicYears
//                if(accedemicList.isNotEmpty()){
//                    accedemicId = accedemicList[0].aCCADEMICID
//                }
//                getGalleryListFun(accedemicId,"")
//                if (isAdded) {
//                    recyclerView?.adapter =
//                        AbsentFragment.AccademicAdapter(this, accedemicList, mContext!!)
//                }
//            }
//        })
    }

    private fun getGalleryImageFun(accId: Int,dummy : String) {
        albumImageList = ArrayList<AlbumModel.Album>()
        val url = "AlbumImage/AlbumImageGet"
        galleryViewModel.getCommonAlbumCategory(url,accId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            Log.i(TAG,"response $response")
                            Log.i(TAG,"message ${resource.message}")
                                if (response.albumList.isNotEmpty()) {
                                    albumImageList = response.albumList
                            }
                            if(albumImageList.size != 0 && albumVideoList.size != 0){
                                getResult()
                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })
//        galleryViewModel.getImageAlbum(accId)
//        if(isAdded) {
//            galleryViewModel.getImageAlbumObservable()
//                .observe(requireActivity(), {
//                    if (it != null) {
//                        if (it.albumList.isNotEmpty()) {
//                            albumImageList = it.albumList
//                        }
//                        getGalleryListFun(accId)
//                    }
//                })
//        }

    }

    private fun getGalleryVideoFun(accId: Int){

        albumVideoList = ArrayList<AlbumModel.Album>()
        val url = "AlbumVideo/AlbumVideoGet"

        galleryViewModel.getCommonAlbumCategory(url,accId)
            .observe(requireActivity(), Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            Log.i(TAG,"response $response")
                            Log.i(TAG,"message ${resource.message}")
                            if (response.albumList.isNotEmpty()) {
                                albumVideoList = response.albumList
                            }
                            if(albumImageList.size != 0 && albumVideoList.size != 0){
                                getResult()
                            }

                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })

//        galleryViewModel.getVideoAlbum(accId)
//        if(isAdded) {
//            galleryViewModel.getVideoAlbumObservable()
//                .observe(requireActivity(), {
//                    if (it != null) {
//                        if (it.albumList.isNotEmpty()) {
//                            albumVideoList = it.albumList
//                        }
//                        getResult()
//                    }
//                })
//        }

        Log.i(TAG, "albumImageList $albumImageList")
        Log.i(TAG, "albumVideoList $albumVideoList")


    }


    private fun getResult(){

            val adapter = Global.MyPagerAdapter(childFragmentManager)
            adapter.addFragment(
                GalleryImageFragment(albumImageList),
                resources.getString(R.string.photo)
            )
            adapter.addFragment(
                GalleryVideoFragment(albumVideoList),
                resources.getString(R.string.video)
            )
           shimmerViewContainer?.visibility = View.GONE
            // adapter.addFragment(new DMKOfficial(), "Tweets");
            viewPager?.adapter = adapter
            viewPager?.currentItem = 0
            tabLayout?.setupWithViewPager(viewPager)

            viewPager?.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(
                    tabLayout
                )
            )
            tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager?.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

    }

    override fun onClick(id: Int) {
        shimmerViewContainer?.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.Main) {
                getGalleryImageFun(id, "")
                getGalleryVideoFun(id)

        }


    }


    override fun onDetach() {
        super.onDetach()
        mContext = null
        Log.i(TAG,"onDetach ")
    }

    override fun onDestroy() {
        super.onDestroy()
        mContext = null
        Log.i(TAG,"onDestroy ")
    }
}
