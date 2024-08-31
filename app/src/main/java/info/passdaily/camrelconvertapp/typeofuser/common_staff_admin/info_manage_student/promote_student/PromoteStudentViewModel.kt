package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.promote_student

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class PromoteStudentViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "GuardianViewModel"
    fun getYearClassExam(adminId: Int,schoolId:Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getYearClassExam(adminId,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getPromoteStudent(accademicIdFrom: Int,classIdFrom: Int, accademicIdTo: Int,classIdTo: Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getPromoteStudent(
                accademicIdFrom,classIdFrom,accademicIdTo,classIdTo)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getStudentsPromoteList(accademicId : Int,classId : Int,schoolId: Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getStudentsPromoteList(accademicId,classId,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun sendStudentsPromote(studentId: Int,studentRollNo: Int, accademicId: Int,classId: Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.sendStudentsPromote(studentId,studentRollNo,accademicId,classId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getStudentReallocationList(accademicId: Int,classId: Int,gender: Int,rollNo: Int,schoolId: Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getStudentReallocationList(accademicId,classId,gender,rollNo, schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    //    ///get Roll Number Update
    //    suspend fun getRollNumberUpdate(studAccademicId : Int,rollNumber : String)
    //            = apiHelper.getRollNumberUpdate(studAccademicId,rollNumber)

    fun getRollNumberUpdate(studAccademicId: Int,rollNumber: String) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getRollNumberUpdate(studAccademicId,rollNumber)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getCommonGetFuntion(url : String,map : HashMap<String?, Int>) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getCommonGetFuntion(url,map)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}