package info.passdaily.camrelconvertapp.typeofuser.parent.description_exam

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class DescriptiveDetailViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "DescriptiveDetailViewModel"


//    var getDescriptiveDetails: MutableLiveData<DescriptiveDetailModel> = MutableLiveData()
//
//
//    fun getDescriptiveDetailsObservable(): MutableLiveData<DescriptiveDetailModel> {
//        return getDescriptiveDetails
//    }
//
//    fun getDescriptiveDetails(studentId: Int,ExamId : Int) {
//        Log.i(TAG, "studentId $studentId")
//        Log.i(TAG, "ExamId $ExamId")
//        val apiInterface = RetrofitClient.create().getDescriptiveDetails(studentId,ExamId)
//        apiInterface.enqueue(object : Callback<DescriptiveDetailModel> {
//            override fun onResponse(
//                call: Call<DescriptiveDetailModel>, response: Response<DescriptiveDetailModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getDescriptiveDetails.postValue(response.body())
//                } else {
//                    getDescriptiveDetails.postValue(null)
//                }
//            }
//            override fun onFailure(call: Call<DescriptiveDetailModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getDescriptiveDetails.postValue(null)
//            }
//        })
//    }


    fun getDescriptiveDetails(studentId: Int,ExamId : Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getDescriptiveDetailsNew(studentId,ExamId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}