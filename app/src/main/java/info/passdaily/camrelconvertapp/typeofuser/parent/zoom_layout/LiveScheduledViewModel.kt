package info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout

import android.util.Log
import androidx.lifecycle.LiveData
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

class LiveScheduledViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "LiveScheduledViewModel"
    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text
//    var getZoomScheduleList: MutableLiveData<ZoomScheduleListModel> = MutableLiveData()
//
//
//    fun getZoomScheduleObservable(): MutableLiveData<ZoomScheduleListModel> {
//        return getZoomScheduleList
//    }
//////AccademicId=7&ClassId=2&StudentId=1
//    fun getZoomSchedule(accademicId : Int, classId: Int,studentId: Int) {
//        Log.i(TAG, "studentId $studentId")
//        val apiInterface = RetrofitClient.create().getZoomScheduleList(accademicId,classId,studentId)
//        apiInterface.enqueue(object : Callback<ZoomScheduleListModel> {
//            override fun onResponse(
//                call: Call<ZoomScheduleListModel>, response: Response<ZoomScheduleListModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i("DashboardDesigner", "response " + response.body())
//                    getZoomScheduleList.postValue(response.body())
//                } else {
//                    getZoomScheduleList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<ZoomScheduleListModel>, t: Throwable) {
//                Log.i("DashboardDesigner", "t $t")
//                getZoomScheduleList.postValue(null)
//            }
//        })
//    }


    fun getZoomScheduleList(academicId: Int,classId : Int,studentId: Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getZoomScheduleList(academicId,classId,studentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}