package info.passdaily.camrelconvertapp.typeofuser.parent.gallery.image

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GalleryViewModel(private val mainRepository: MainRepository) :  ViewModel() {

    var TAG = "GalleryViewModel"

//    var getAccademic: MutableLiveData<AccademicYearsModel> = MutableLiveData()
//
//    fun getAccademicObservable(): MutableLiveData<AccademicYearsModel> {
//        return getAccademic
//    }
//
//    fun getAccademic(accademic : Int) {
//
//        val apiInterface = RetrofitClient.create().getAccademic(accademic)
//        apiInterface.enqueue(object : Callback<AccademicYearsModel> {
//            override fun onResponse(
//                call: Call<AccademicYearsModel>, response: Response<AccademicYearsModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getAccademic.postValue(response.body())
//                } else {
//                    getAccademic.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<AccademicYearsModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getAccademic.postValue(null)
//            }
//        })
//    }


    fun getAccademicNew(academicId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAccademic(academicId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

//////////////Get Image Album ////////////////////////////////////

    fun getCommonAlbumCategory(url : String,academicId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getCommonAlbumCategory(url,academicId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
//    var getImageAlbumList: MutableLiveData<AlbumModel> = MutableLiveData()
//
//    fun getImageAlbumObservable(): MutableLiveData<AlbumModel> {
//        return getImageAlbumList
//    }
//
//    fun getImageAlbum(accademicId: Int) {
//        Log.i(TAG, "accademicId $accademicId")
//        val apiInterface = RetrofitClient.create().getImageAlbum(accademicId)
//        apiInterface.enqueue(object : Callback<AlbumModel> {
//            override fun onResponse(
//                call: Call<AlbumModel>, response: Response<AlbumModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getImageAlbumList.postValue(response.body())
//                } else {
//                    getImageAlbumList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<AlbumModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getImageAlbumList.postValue(null)
//            }
//        })
//    }
///////////////////////////////////////////////////////////////////////////////

    //////////////Get Video Album ////////////////////////////////////
//    var getVideoAlbumList: MutableLiveData<AlbumModel> = MutableLiveData()
//
//    fun getVideoAlbumObservable(): MutableLiveData<AlbumModel> {
//        return getVideoAlbumList
//    }
//
//    fun getVideoAlbum(accademicId: Int) {
//        Log.i(TAG, "accademicId $accademicId")
//        val apiInterface = RetrofitClient.create().getVideoAlbum(accademicId)
//        apiInterface.enqueue(object : Callback<AlbumModel> {
//            override fun onResponse(
//                call: Call<AlbumModel>, response: Response<AlbumModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getVideoAlbumList.postValue(response.body())
//                } else {
//                    getVideoAlbumList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<AlbumModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getVideoAlbumList.postValue(null)
//            }
//        })
//    }
///////////////////////////////////////////////////////////////////////////////


    fun getGalleryImgVidList(url : String,academicId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getGalleryImgVidList(url,academicId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }



    /////////////////////////////////////////Image Activity ///////////////////////////
//    var getVideoGalleryList: MutableLiveData<GalleryImageModel> = MutableLiveData()
//
//    fun getVideoGalleryObservable(): MutableLiveData<GalleryImageModel> {
//        return getVideoGalleryList
//    }
//
//    fun getVideoGallery(albumCategoryId: Int) {
//        Log.i(TAG, "albumCategoryId $albumCategoryId")
//        val apiInterface = RetrofitClient.create().getVideoGallery(albumCategoryId)
//        apiInterface.enqueue(object : Callback<GalleryImageModel> {
//            override fun onResponse(
//                call: Call<GalleryImageModel>, response: Response<GalleryImageModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getVideoGalleryList.postValue(response.body())
//                } else {
//                    getVideoGalleryList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<GalleryImageModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getVideoGalleryList.postValue(null)
//            }
//        })
//    }
//////////////////////////////////////////////////////////////////////





    /////////////////////////////////////////Image Activity ///////////////////////////
//    var getImageGalleryList: MutableLiveData<GalleryImageModel> = MutableLiveData()
//
//    fun getImageGalleryObservable(): MutableLiveData<GalleryImageModel> {
//        return getImageGalleryList
//    }
//
//    fun getImageGallery(albumCategoryId: Int) {
//        Log.i(TAG, "albumCategoryId $albumCategoryId")
//        val apiInterface = RetrofitClient.create().getImageGallery(albumCategoryId)
//        apiInterface.enqueue(object : Callback<GalleryImageModel> {
//            override fun onResponse(
//                call: Call<GalleryImageModel>, response: Response<GalleryImageModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getImageGalleryList.postValue(response.body())
//                } else {
//                    getImageGalleryList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<GalleryImageModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getImageGalleryList.postValue(null)
//            }
//        })
//    }
//////////////////////////////////////////////////////////////////////


}