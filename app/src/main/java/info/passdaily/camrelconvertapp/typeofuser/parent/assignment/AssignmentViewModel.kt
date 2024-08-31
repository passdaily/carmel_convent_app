package info.passdaily.camrelconvertapp.typeofuser.parent.assignment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class AssignmentViewModel(private val mainRepository: MainRepository) : ViewModel() {
    // TODO: Implement the ViewModel

    var TAG = "AssignmentViewModel"

//    var getSubjectsModel: MutableLiveData<SubjectsModel> = MutableLiveData()
//
//    var getAssignmentList: MutableLiveData<AssignmentListModel> = MutableLiveData()
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
//    fun getAssignmentListObservable(): MutableLiveData<AssignmentListModel> {
//        return getAssignmentList
//    }
//
//    fun getAssignmentList(academicId: Int,classId : Int,studentId: Int,subjectId : Int) {
//        Log.i(TAG, "academicId $academicId")
//        Log.i(TAG, "classId $classId")
//        Log.i(TAG, "studentId $studentId")
//        Log.i(TAG, "subjectId $subjectId")
//        val apiInterface = RetrofitClient.create().getAssignmentList(academicId,classId,studentId,subjectId)
//        apiInterface.enqueue(object : Callback<AssignmentListModel> {
//            override fun onResponse(
//                call: Call<AssignmentListModel>, response: Response<AssignmentListModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getAssignmentList.postValue(response.body())
//                } else {
//                    getAssignmentList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<AssignmentListModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getAssignmentList.postValue(null)
//            }
//        })
//    }


    fun getAssignmentList(academicId: Int,classId : Int,studentId: Int,subjectId : Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAssignmentListNew(academicId,classId,studentId,subjectId)))
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