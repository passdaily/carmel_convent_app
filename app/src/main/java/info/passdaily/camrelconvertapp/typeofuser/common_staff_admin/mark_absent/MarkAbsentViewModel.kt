package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.mark_absent

import android.content.Context
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import info.passdaily.camrelconvertapp.services.Utils
import kotlinx.coroutines.Dispatchers
import okhttp3.RequestBody

class MarkAbsentViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "MarkAbsentViewModel"
    fun getYearClassExam(adminId: Int,schoolId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getYearClassExam(adminId, schoolId )))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getClassList(adminId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getClassList(adminId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    ////create enquiry
    fun getMarkAbsentStaff(cLASSID: Int,aCCADEMICID : Int,date: String,schoolId :Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getMarkAbsentStaff(cLASSID,aCCADEMICID,date,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun validateField(edtField: TextInputEditText, message: String, context: Context, constraintLayout : ConstraintLayout): Boolean {
        return if (Utils.validateFieldIsEmpty(edtField.text.toString())) {
            Utils.getSnackBar4K(context, message, constraintLayout)
            false
        } else {
            true
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

//    fun getCommonGetFuntion(url : String,map : Map<String, Int>) = liveData(Dispatchers.IO) {
//        emit(Resource.loading(data = null))
//        try {
//            emit(Resource.success(data = mainRepository.getCommonGetFuntion(url,map)))
//        } catch (exception: Exception) {
//            Log.i(TAG, "exception $exception")
//            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
//        }
//    }

    ////Mark Present Staff
    fun getMarkPresentStaff(attendanceId: Int,studentId : Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getMarkPresentStaff(attendanceId,studentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    // ////mark Full Present
    fun getMarkFullPresent(accademicId: Int,classId : Int,absentDate : String,adminId : Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getMarkFullPresent(accademicId,classId,absentDate,adminId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}