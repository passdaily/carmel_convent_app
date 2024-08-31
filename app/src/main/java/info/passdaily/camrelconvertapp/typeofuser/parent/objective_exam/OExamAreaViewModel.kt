package info.passdaily.camrelconvertapp.typeofuser.parent.objective_exam

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers
import okhttp3.RequestBody

class OExamAreaViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "OExamAreaViewModel"

    // suspend fun questionListForStart(
    //        @Query("OexamId") OexamId: Int,
    //        @Query("OexamAttemptId") OexamAttemptId: Int
    //        //category
    fun getQuestionList(OexamId: Int,OexamAttemptId : Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getQuestionListNew(OexamId,OexamAttemptId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


//    fun getOnlineExamStatus(academicId: Int,classId : Int,studentId : Int ,oExamId : Int) = liveData(
//        Dispatchers.IO) {
//        emit(Resource.loading(data = null))
//        try {
//            emit(Resource.success(data = mainRepository.getOnlineExamStatus(academicId,classId,studentId,oExamId)))
//        } catch (exception: Exception) {
//            Log.i(TAG, "exception $exception")
//            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
//        }
//    }


    fun getOptionsList(QuestionId: Int,OexamAttemptId : Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getOptionsList(QuestionId,OexamAttemptId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getSubmitAndNext(submitAndNextItems: RequestBody?) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getSubmitAndNext(submitAndNextItems)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getDeleteOption(optionItems: RequestBody?) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getDeleteOption(optionItems)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getSaveAndExit(saveExitItems: RequestBody?) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getSaveAndExit(saveExitItems)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getDoAutoEnd(doAutoEndItems: RequestBody?) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getDoAutoEnd(doAutoEndItems)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getFinishExam(finishExamItems: RequestBody?) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getFinishExam(finishExamItems)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getObjectiveExamResult(academicId: Int,studentId: Int,oExamId: Int,OexamAttemptId : Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getObjectiveExamResult(academicId,studentId,oExamId,
                    OexamAttemptId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


}