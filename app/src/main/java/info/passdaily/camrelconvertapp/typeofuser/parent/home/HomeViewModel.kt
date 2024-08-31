package info.passdaily.camrelconvertapp.typeofuser.parent.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class HomeViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    var TAG = "HomeViewModel";


//    var getDashBoardItems: MutableLiveData<DashBoardModel> = MutableLiveData()
//
//    fun getDashBoardObservable(): MutableLiveData<DashBoardModel> {
//        return getDashBoardItems
//    }
//
//    fun getDashBoard(studentId: Int) {
//        Log.i(TAG, "studentId $studentId")
//        val apiInterface = RetrofitClient.create().dashboardItems(studentId)
//        apiInterface.enqueue(object : Callback<DashBoardModel> {
//            override fun onResponse(
//                call: Call<DashBoardModel>, response: Response<DashBoardModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i("DashboardDesigner", "response " + response.body())
//                    getDashBoardItems.postValue(response.body())
//                } else {
//                    getDashBoardItems.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<DashBoardModel>, t: Throwable) {
//                Log.i("DashboardDesigner", "t $t")
//                getDashBoardItems.postValue(null)
//            }
//        })
//    }

    fun getDashBoard(studentId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getDashBoardNew(studentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


}