package info.passdaily.camrelconvertapp.typeofuser.parent.absent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class AbsentViewModel(private val mainRepository: MainRepository) :  ViewModel() {

    var TAG = "AbsentViewModel"

//    var getAccademic: MutableLiveData<AccademicYearsModel> = MutableLiveData()
//
//    var getAbsentList: MutableLiveData<AttendanceReportModel> = MutableLiveData()
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
//
//
//    fun getAbsentObservable(): MutableLiveData<AttendanceReportModel> {
//        return getAbsentList
//    }
//
//    fun getAbsents(studentId: Int,academicId: Int) {
//        Log.i(TAG, "studentId $studentId")
//        val apiInterface = RetrofitClient.create().getAbsents(studentId,academicId)
//        apiInterface.enqueue(object : Callback<AttendanceReportModel> {
//            override fun onResponse(
//                call: Call<AttendanceReportModel>, response: Response<AttendanceReportModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getAbsentList.postValue(response.body())
//                } else {
//                    getAbsentList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<AttendanceReportModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getAbsentList.postValue(null)
//            }
//        })
//    }


    fun getAbsentsNew(studentId: Int,academicId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAbsents(studentId,academicId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getAccademicNew(academicId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAccademic(academicId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }



}