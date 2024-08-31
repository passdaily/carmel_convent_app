package info.passdaily.camrelconvertapp.typeofuser.parent.gallery.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.databinding.ActivityImageBinding
import info.passdaily.camrelconvertapp.lib.ExoPlayerActivity
//import info.passdaily.camrelconvertapp.lib.video.Video_Activity
//import info.passdaily.parentapp.lib.video_theme.VideoPlayerActivity
//import info.passdaily.parentapp.lib.videoplayer.VideoPlayerActivity
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.*
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image.GalleryViewModel
import info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image.ImageActivity


@Suppress("DEPRECATION")
class VideoActivity : AppCompatActivity() {

    var TAG = "VideoActivity"
    private lateinit var binding: ActivityImageBinding

    var toolbar : Toolbar? =null
    var ALBUM_CATEGORY_ID = 0
    var ALBUM_CATEGORY_NAME = ""

    private lateinit var galleryViewModel: GalleryViewModel

    private val modelList: ArrayList<VidModel> = ArrayList<VidModel>()

    var recyclerviewImage : RecyclerView? = null
//    var recyclerviewImage : ExoPlayerRecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        galleryViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[GalleryViewModel::class.java]


        var bundle :Bundle ?=intent.extras
        ALBUM_CATEGORY_ID = bundle!!.getInt("ALBUM_CATEGORY_ID") // 1
        ALBUM_CATEGORY_NAME = bundle.getString("ALBUM_CATEGORY_NAME")!! // 1


        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.activity_image)

        toolbar = binding.toolbar
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


        recyclerviewImage = binding.recyclerviewImage
        recyclerviewImage?.layoutManager = LinearLayoutManager(this)
       // recyclerviewImage?.itemAnimator = DefaultItemAnimator()



        getImageFun()

        Utils.setStatusBarColor(this)
    }

    private fun getImageFun() {


        val url = "AlbumVideo/AlbumVideoFilesGet"
        galleryViewModel.getGalleryImgVidList(url,ALBUM_CATEGORY_ID)
            .observe(this, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val response = resource.data?.body()!!
                            if (response.albumImageList.isNotEmpty()) {
//                                val urls: MutableList<String> = java.util.ArrayList()
                                val albumImageList = response.albumImageList

                                    recyclerviewImage?.adapter =
                                        ImageGalleryAdapter(albumImageList, this)

//                                for(i in albumImageList.indices){
//                                    modelList.add(VidModel(Global.image_url+"/video/"+albumImageList[i].aLBUMFILE,
//                                        "","video"))
//                                //    urls[i] = albumImageList[i].aLBUMFILE
//                                }


                                //set data object
                               // recyclerviewImage!!.setMediaObjects(modelList)
                                //Set Adapter
//                                recyclerviewImage!!.adapter = MediaRecyclerAdapter(modelList, initGlide())
//                                recyclerviewImage!!.smoothScrollToPosition(1)

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

//        galleryViewModel.getVideoGallery(
//            ALBUM_CATEGORY_ID)
//
//        galleryViewModel.getVideoGalleryObservable()
//            .observe(this) {
//
//                if (it != null) {
//                    if (it.albumImageList.isNotEmpty()) {
////                        constraintEmpty?.visibility = View.GONE
////                        recyclerviewImage?.visibility = View.VISIBLE
//                        recyclerviewImage?.adapter =
//                            ImageGalleryAdapter(it.albumImageList, this)
//
//                    }
////                    else {
////                        constraintEmpty?.visibility = View.VISIBLE
////                        recyclerViewAbsentList?.visibility = View.GONE
////                    }
//                }
//            }
    }

    class ImageGalleryAdapter(var albumImageList: List<GalleryImageModel.AlbumImage>, var mContext: Context) :
        RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            //var imageViewFile: StyledPlayerView = view.findViewById(R.id.imageViewFile)
            var imageViewFile: ImageView = view.findViewById(R.id.imageViewFile)
        }

        // exoPlayer nesnesi tanımlanıyor.
        private lateinit var simpleExoPlayer: SimpleExoPlayer

        // exoPlayer'da kullanmak icin DataSource nesnesi tanımı
       // val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.video_adapter, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            Glide.with(context)
//                .load(Global.image_url+"/Image/"+albumImageList[position].aLBUMFILE)
//                .into(holder.imageViewFile)
//            holder.imageViewFile.setVideoUrl(Global.image_url+"/video/"+albumImageList[position].aLBUMFILE);

            holder.itemView.setOnClickListener {

                val intent = Intent(mContext, ExoPlayerActivity::class.java)
                intent.putExtra("ALBUM_TITLE", albumImageList[position].aLBUMTITLE)
                intent.putExtra("ALBUM_FILE", Global.image_url+"/video/"+albumImageList[position].aLBUMFILE)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent)
//                val intent  = Intent(mContext, VideoPlayerActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                intent.putExtra("ALBUM_URL", Global.image_url+"/video/"+albumImageList[position].aLBUMFILE)
////                intent.putExtra("ALBUM_TITLE", albumImageList[position].aLBUMTITLE)
//                mContext.startActivity(intent)
                Global.ALBUM_URL = Global.image_url+"/video/"+albumImageList[position].aLBUMFILE
                Global.ALBUM_TITLE = albumImageList[position].aLBUMTITLE
                Global.ALBUM_CATEGORY_NAME = albumImageList[position].aLBUMCATEGORYNAME
            }

            // yeni bir instance baslatılması
//            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this)
//
//            // DataSource içerisini doldurma
//            mediaDataSourceFactory =
//                DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayerDemo"))
//
//            // media source nesnesine kullanılacak video türüne göre tanımlama ve url koyma islemi
//            val mediaSource =
//                ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(Uri.parse(URL))
//
//            // player'ı hazır hale getirme
//            simpleExoPlayer.prepare(mediaSource, false, false)
//
//            // play oynatılmaya hazır olduğunda video oynatma islemi
//            simpleExoPlayer.playWhenReady = true
//
//            // loyout dosyasındaki id degeri eslestirme
//            playerView.player = simpleExoPlayer
//
//            // player ekranına focuslanma ozelligi
//            playerView.requestFocus()
        }

        override fun getItemCount(): Int {
            return albumImageList.size
        }

    }

    class VidModel(val videoUrl : String,val imagePath : String,val name : String)


    private fun initGlide(): RequestManager {
        val options = RequestOptions()
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    override fun onDestroy() {
        if (recyclerviewImage != null) {
          //  recyclerviewImage!!.releasePlayer()
        }
        super.onDestroy()
    }
}