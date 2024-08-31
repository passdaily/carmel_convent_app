package info.passdaily.camrelconvertapp.typeofuser.parent.description_exam

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class DescriptiveExamViewModel(private val mainRepository: MainRepository) : ViewModel() {
    // TODO: Implement the ViewModel

    var TAG = "AssignmentViewModel"

//    var getSubjectsModel: MutableLiveData<SubjectsModel> = MutableLiveData()
//
//    var getDescriptiveList: MutableLiveData<DescriptiveExamModel> = MutableLiveData()
//
//    fun getSubjectObservable(): MutableLiveData<SubjectsModel> {
//        return getSubjectsModel
//    }
//
//    fun getSubjects(classId : Int,studentId: Int) {
//
//        val apiInterface = RetrofitClient.create().getSubjectModel(classId,studentId)
//        apiInterface.enqueue(object : Callback<SubjectsModel> {
//            override fun onResponse(
//                call: Call<SubjectsModel>, response: Response<SubjectsModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getSubjectsModel.postValue(response.body())
//                } else {
//                    getSubjectsModel.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<SubjectsModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getSubjectsModel.postValue(null)
//            }
//        })
//    }
//
//
//    fun getDescriptiveObservable(): MutableLiveData<DescriptiveExamModel> {
//        return getDescriptiveList
//    }
//
//    fun getDescriptiveList(academicId: Int,classId : Int,studentId: Int,subjectId : Int) {
//        Log.i(TAG, "studentId $studentId")
//        val apiInterface = RetrofitClient.create().getDescriptiveList(academicId,classId,subjectId,studentId)
//        apiInterface.enqueue(object : Callback<DescriptiveExamModel> {
//            override fun onResponse(
//                call: Call<DescriptiveExamModel>, response: Response<DescriptiveExamModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getDescriptiveList.postValue(response.body())
//                } else {
//                    getDescriptiveList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<DescriptiveExamModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getDescriptiveList.postValue(null)
//            }
//        })
//    }

    fun getDescriptiveList(academicId: Int,classId : Int,subjectId: Int,studentId : Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getDescriptiveListNew(academicId,classId,subjectId,studentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getSubjects(classId : Int,studentId: Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getSubjectModelNew(classId,studentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}