package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.attendance_marking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class AttendanceMakingViewModel (private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "AttendanceMakingViewModel"
    fun getYearClassExam(adminId: Int,school:Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getYearClassExam(adminId,school)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getStudentDetails(cLASSID: Int,aCCADEMICID: Int,Date : String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getStudentDetails(cLASSID,aCCADEMICID,Date)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}