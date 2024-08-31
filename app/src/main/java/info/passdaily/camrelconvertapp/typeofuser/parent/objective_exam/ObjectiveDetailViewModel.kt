package info.passdaily.camrelconvertapp.typeofuser.parent.objective_exam

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class ObjectiveDetailViewModel(private val mainRepository: MainRepository) : ViewModel(){

    var TAG = "objectiveDetailViewModel"


//    var getObjectiveDetails: MutableLiveData<ObjectiveDetailModel> = MutableLiveData()
//
//
//    fun getObjectiveDetailsObservable(): MutableLiveData<ObjectiveDetailModel> {
//        return getObjectiveDetails
//    }
//
//    fun getObjectiveDetails(studentId: Int,OExamId : Int) {
//        Log.i(TAG, "studentId $studentId")
//        Log.i(TAG, "ExamId $OExamId")
//        val apiInterface = RetrofitClient.create().getObjectiveDetails(studentId,OExamId)
//        apiInterface.enqueue(object : Callback<ObjectiveDetailModel> {
//            override fun onResponse(
//                call: Call<ObjectiveDetailModel>, response: Response<ObjectiveDetailModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getObjectiveDetails.postValue(response.body())
//                } else {
//                    getObjectiveDetails.postValue(null)
//                }
//            }
//            override fun onFailure(call: Call<ObjectiveDetailModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getObjectiveDetails.postValue(null)
//            }
//        })
//    }


    fun getObjectiveDetails(studentId: Int,OExamId : Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getObjectiveDetailsNew(studentId,OExamId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


}