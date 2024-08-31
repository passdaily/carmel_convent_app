package info.passdaily.camrelconvertapp.typeofuser.parent.progress_report

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class ProgressViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "ProgressViewModel"


    fun getExamList(examId: Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getExamList(examId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getExamMarkDetails(
        ACADEMICID: Int,
        CLASSID: Int,
        EXAMID: Int,
        STUDENT_ROLL_NO: Int,
        STUDENT_ID: Int
    ) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getExamMarkDetails(CLASSID,ACADEMICID,EXAMID,STUDENT_ROLL_NO,STUDENT_ID)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}