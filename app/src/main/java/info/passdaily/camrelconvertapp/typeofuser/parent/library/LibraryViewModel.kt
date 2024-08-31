package info.passdaily.camrelconvertapp.typeofuser.parent.library

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class LibraryViewModel(private val mainRepository: MainRepository) : ViewModel() {


    var TAG = "LibraryViewModel"

    var getLibraryList: MutableLiveData<LibraryModel> = MutableLiveData()


    fun getLibraryListObservable(): MutableLiveData<LibraryModel> {
        return getLibraryList
    }

//    fun getLibraryList(studentId: Int) {
//        Log.i(TAG, "studentId $studentId")
//        val apiInterface = RetrofitClient.create().getLibrary(studentId)
//        apiInterface.enqueue(object : Callback<LibraryModel> {
//            override fun onResponse(
//                call: Call<LibraryModel>, response: Response<LibraryModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getLibraryList.postValue(response.body())
//                } else {
//                    getLibraryList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<LibraryModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getLibraryList.postValue(null)
//            }
//        })
//    }


    fun getLibraryList(studentId: Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getLibraryNew(studentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}