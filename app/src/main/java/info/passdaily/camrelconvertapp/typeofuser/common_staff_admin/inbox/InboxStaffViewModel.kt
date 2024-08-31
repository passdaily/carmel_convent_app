package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.inbox

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class InboxStaffViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var TAG = "InboxStaffViewModel"

    fun getInboxStaff(adminId : Int,staffId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getInboxStaff(adminId,staffId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getStaffInboxViewById(vIRTUALMAILSENTSTAFFID: Int,adminId : Int,staffId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getStaffInboxViewById(vIRTUALMAILSENTSTAFFID,adminId,staffId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getInboxReadById(inboxId : Int, adminId : Int,staffId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getInboxReadById(inboxId,adminId,staffId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}