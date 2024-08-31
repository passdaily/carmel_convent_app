package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_event

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

class ManageEventViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "ManageEventViewModel"
    fun getYearClassExam(adminId: Int,schoolId:Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getYearClassExam(adminId, schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getManageEvents(academicId: Int,eventType:Int,adminId : Int,schoolId:Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getManageEvent(academicId, eventType,adminId,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun deleteEventItem(eventId: Int,adminId :Int,schoolId:Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.deleteEventItem(eventId,adminId,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getAcademicList(academicId: Int,schoolId :Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAcademicList(academicId,schoolId)))
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

    fun validateField(edtField: String, message: String, context: Context, constraintLayout : ConstraintLayout): Boolean {
        return if (Utils.validateFieldIsEmpty(edtField)) {
            Utils.getSnackBar4K(context, message, constraintLayout)
            false
        } else {
            true
        }
    }

    fun validateFieldStr(edtField: String, message: String, context: Context, constraintLayout : ConstraintLayout): Boolean {
        return if (edtField == "-1") {
            Utils.getSnackBar4K(context, message, constraintLayout)
            false
        } else {
            true
        }
    }

    fun validateFieldVideo(typeStr : String, edtField: String, message: String, context: Context, constraintLayout : ConstraintLayout): Boolean {
        return when (typeStr) {
            "2" -> {
                if (Utils.validateFieldIsEmpty(edtField)) {
                    Utils.getSnackBar4K(context, "Upload Video", constraintLayout)
                    false
                }else{
                    true
                }
            }
            "3" -> {
                if (Utils.validateFieldIsEmpty(edtField)) {
                    Utils.getSnackBar4K(context, "Upload youtube link", constraintLayout)
                    false
                }else{
                    true
                }
            }
            else -> {
                true
            }
        }
    }

}