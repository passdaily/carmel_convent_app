package info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import info.passdaily.camrelconvertapp.R
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.ViewModelFactory
import info.passdaily.camrelconvertapp.services.client_manager.ApiClient
import info.passdaily.camrelconvertapp.services.client_manager.NetworkLayer

@Suppress("DEPRECATION")
class GalleryImageFragment(var albumList: ArrayList<AlbumModel.Album>) : Fragment(){
    var TAG = "GalleryImageFragment"

    var constraintEmpty: ConstraintLayout? = null
    var imageViewEmpty: ImageView? = null
    var textEmpty: TextView? = null
    var accedemicId = 0
    var recyclerView : RecyclerView? =null

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
    ): View? {

        // Inflate the layout for this fragment
        galleryViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient(NetworkLayer.services))
        )[GalleryViewModel::class.java]

        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




//        recyclerView?.layoutManager =
//            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
//        galleryViewModel.getAccademic(0)


        constraintEmpty = view.findViewById(R.id.constraintEmpty)
        imageViewEmpty = view.findViewById(R.id.imageViewEmpty)
        textEmpty = view.findViewById(R.id.textEmpty)
        if (isAdded) {
            Glide.with(mContext!!)
                .load(R.drawable.ic_empty_state_gallery)
                .into(imageViewEmpty!!)
        }
        textEmpty?.text = "No Photos"


        recyclerView = view.findViewById(R.id.recyclerView)


        if(albumList.size > 0){
            constraintEmpty?.visibility = View.GONE
            recyclerView?.visibility = View.VISIBLE
        }else{
            constraintEmpty?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        }
        recyclerView?.layoutManager = GridLayoutManager(requireActivity(),2)
        recyclerView?.adapter = GalleryAdapter(albumList,requireActivity())


//        intiFunction()

    }

//    private fun getGalleryListFun(accId: Int) {
//
//        galleryViewModel.getAlbum(accId)
//        galleryViewModel.getAlbumObservable()
//            .observe(requireActivity(), {
//
//                if (it != null) {
//                    if (it.albumList.isNotEmpty()) {
//                        constraintEmpty?.visibility = View.GONE
//                        recyclerViewAbsentList?.visibility = View.VISIBLE
//                        if (isAdded) {
//                            recyclerViewAbsentList?.adapter =
//                                GalleryAdapter(it.albumList, mContext!!)
//                        }
//                    } else {
//                        constraintEmpty?.visibility = View.VISIBLE
//                        recyclerViewAbsentList?.visibility = View.GONE
//                    }
//                }
//            })
//
//    }
//
//    private fun intiFunction() {
//        galleryViewModel.getAccademicObservable().observe(requireActivity(), {
//            if (it != null) {
//                accedemicList = it.accademicYears
//                if(accedemicList.isNotEmpty()){
//                    accedemicId = accedemicList[0].aCCADEMICID
//                }
//                getGalleryListFun(accedemicId)
//                if (isAdded) {
//                    recyclerView?.adapter =
//                        AbsentFragment.AccademicAdapter(this, accedemicList, mContext!!)
//                }
//            }
//        })
//    }
//
//    override fun onClick(id: Int) {
//        galleryViewModel.getAlbum(
//            id
//        )
//        galleryViewModel.getAlbumObservable()
//            .observe(requireActivity(), {
//
//                if (it != null) {
//                    if (it.albumList.isNotEmpty()) {
//                        constraintEmpty?.visibility = View.GONE
//                        recyclerViewAbsentList?.visibility = View.VISIBLE
//                        if (isAdded) {
//                            recyclerViewAbsentList?.adapter =
//                                GalleryAdapter(it.albumList, mContext!!)
//                        }
//                    } else {
//                        constraintEmpty?.visibility = View.VISIBLE
//                        recyclerViewAbsentList?.visibility = View.GONE
//                    }
//                }
//            })
//    }

    class GalleryAdapter(var albumList: List<AlbumModel.Album>, var mContext: Context)
        : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textViewName: TextView = view.findViewById(R.id.textViewName)
            var textViewDate: TextView = view.findViewById(R.id.textViewDate)
            var textViewCount: TextView = view.findViewById(R.id.textViewCount)
            var imageViewFile: ImageView = view.findViewById(R.id.imageViewFile)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.album_adapter_image, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
          holder.textViewName.text = albumList[position].aLBUMCATEGORYNAME

            holder.textViewCount.text = albumList[position].cOUNT.toString()

            holder.itemView.setOnClickListener {
                val intent  = Intent(mContext, ImageActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ALBUM_CATEGORY_ID", albumList[position].aLBUMCATEGORYID)
                intent.putExtra("ALBUM_CATEGORY_NAME", albumList[position].aLBUMCATEGORYNAME)
                mContext.startActivity(intent)
            }
            Glide.with(mContext)
                .load(R.drawable.ic_image_view)
                .into(holder.imageViewFile)

//            if (!albumList[position].aLBUMCATEGORYNAME.isNullOrBlank()) {
//                val date: Array<String> =
//                    albumList[position].aBSENTDATE.split("T".toRegex()).toTypedArray()
//                val dddd: Long = Utils.longconversion(date[0] + " " + date[1])
//                startDate = Utils.formattedDateWords(dddd)
//            }
        }

        override fun getItemCount(): Int {
           return albumList.size
        }

    }

}

