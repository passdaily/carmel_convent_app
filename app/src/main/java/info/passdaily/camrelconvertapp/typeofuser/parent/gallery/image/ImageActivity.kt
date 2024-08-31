package info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.lib.RecyclerTouchListener
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Global
import info.passdaily.camrelconvertapp.services.Status
import info.passdaily.camrelconvertapp.services.Utils
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer

@Suppress("DEPRECATION")
class ImageActivity : AppCompatActivity() {

    var TAG = "ImageActivity"
    var toolbar : Toolbar? =null
    var ALBUM_CATEGORY_ID = 0
    var ALBUM_CATEGORY_NAME = ""


    private lateinit var galleryViewModel: GalleryViewModel

    var recyclerviewImage : RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        galleryViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[GalleryViewModel::class.java]

        var bundle =intent.extras
        if (bundle != null) {
            ALBUM_CATEGORY_ID = bundle.getInt("ALBUM_CATEGORY_ID") // 1
            ALBUM_CATEGORY_NAME = bundle.getString("ALBUM_CATEGORY_NAME")!! // 1
        }

        setContentView(R.layout.activity_image)

        toolbar = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = ALBUM_CATEGORY_NAME
            // Customize the back button
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            supportActionBar!!.setDisplayShowTitleEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //   toolbar.setTitle("About-Us");
            toolbar?.setNavigationOnClickListener(View.OnClickListener { // perform whatever you want on back arrow click
                onBackPressed()
            })
        }

        recyclerviewImage = findViewById(R.id.recyclerviewImage);
        recyclerviewImage?.layoutManager = GridLayoutManager (this,2)
        recyclerviewImage?.setHasFixedSize(true)
        recyclerviewImage?.addOnItemTouchListener(RecyclerTouchListener(this,
                recyclerviewImage!!, object : RecyclerTouchListener.ClickListener {
                    override  fun onClick(view: View?, position: Int) {
                        val extra = Bundle()
                        extra.putInt("position", position)
                        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
                        val newFragment = SlideshowDialogFragment.newInstance()
                        newFragment.arguments = extra
                        newFragment.show(ft, newFragment.TAG)
                    }

                    override fun onLongClick(view: View?, position: Int) {}
                })
        )

//        val staggeredAdapter = StaggeredAdapter(placeList)
//        recyclerviewImage?.setAdapter(staggeredAdapter)

        getImageFun()

        Utils.setStatusBarColor(this)

    }

    private fun getImageFun() {

        val url = "AlbumImage/AlbumImageFilesGet"
        galleryViewModel.getGalleryImgVidList(url,ALBUM_CATEGORY_ID)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            val albumImageLit = response.albumImageList

                            for(i in albumImageLit.indices){
                                Global.albumImageList.add(
                                    CustomImageModel(
                                        Global.image_url + "/Image/" + albumImageLit[i].aLBUMFILE
                                    )
                                )
                            }
                            if (albumImageLit.isNotEmpty()) {
                                recyclerviewImage?.adapter =
                                    ImageGalleryAdapter(albumImageLit, this)

                            }
                        }
                        Status.ERROR -> {
                            Log.i(TAG, "Error ${Status.ERROR}")
                        }
                        Status.LOADING -> {
                            Global.albumImageList = ArrayList<CustomImageModel>()
                            Log.i(TAG, "resource ${resource.status}")
                            Log.i(TAG, "message ${resource.message}")
                        }
                    }
                }
            })
//        galleryViewModel.getImageGallery(
//            ALBUM_CATEGORY_ID)
//
//        galleryViewModel.getImageGalleryObservable()
//            .observe(this, {
//
//                if (it != null) {
//                    if (it.albumImageList.isNotEmpty()) {
////                        constraintEmpty?.visibility = View.GONE
////                        recyclerviewImage?.visibility = View.VISIBLE
//                        recyclerviewImage?.adapter =
//                                ImageGalleryAdapter(it.albumImageList, this)
//
//                    }
////                    else {
////                        constraintEmpty?.visibility = View.VISIBLE
////                        recyclerViewAbsentList?.visibility = View.GONE
////                    }
//                }
//            })
    }


    class ImageGalleryAdapter(var albumImageList: List<GalleryImageModel.AlbumImage>, var context: Context) :
        RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageViewFile: ImageView = view.findViewById(R.id.imageViewFile)
          //  var imageViewFile :ZoomImageView = view.findViewById(R.id.imageViewFile)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            Glide.with(context)
//                .load(Global.image_url+"/Image/"+albumImageList[position].aLBUMFILE)
//                .into(holder.imageViewFile)

            Glide.with(context)
                .load(Global.image_url+"/Image/"+albumImageList[position].aLBUMFILE)
                .apply(
                    RequestOptions.centerCropTransform()
                        .dontAnimate() //   .override(imageSize, imageSize)
                        .placeholder(R.drawable.ic_image_view)
                )
                .thumbnail(0.5f)
                .into(holder.imageViewFile)

    }

        override fun getItemCount(): Int {
           return albumImageList.size
        }

    }

    override fun onStop() {
        super.onStop()
        Global.albumImageList =  ArrayList<CustomImageModel>()
        Log.i(TAG,"onStop")
    }


}

