package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.enquiry

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers
import okhttp3.RequestBody

class EnquiryViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "LeaveViewModel"

    fun getYearClassExam(adminId: Int,schoolId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getYearClassExam(adminId, schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
    // suspend fun getClassDetailsStaff(adminId : Int)
    //            = apiHelper.getClassDetailsStaff(adminId)


    fun getEnquiryListGet(classId: Int, accademicId: Int, schoolId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getEnquiryListGet(classId,accademicId,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    ////create enquiry
    fun getCommonPostFun(url : String,accademicRe: RequestBody?) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getCommonPostFun(url,accademicRe)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getEnquiryGetById(qUERYID: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getEnquiryGetById(qUERYID)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}