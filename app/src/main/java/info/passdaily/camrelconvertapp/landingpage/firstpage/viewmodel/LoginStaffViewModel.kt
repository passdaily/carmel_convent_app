package info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel

import android.content.Context
import android.util.Log
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import info.passdaily.camrelconvertapp.services.Utils
import kotlinx.coroutines.Dispatchers

class LoginStaffViewModel(private val mainRepository: MainRepository) : ViewModel() {

    val TAG : String = "LoginStaffViewModel"



    fun getLoginDetails(username: String, password : String,schoolCode : String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getLoginDetailStaff(username,password,schoolCode)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getStaffAccountCreate(mobileNumber: String,schoolCode : String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getStaffAccountCreate(mobileNumber,schoolCode)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun validateField(edtField: EditText, constraintLayout: ConstraintLayout, message : String, context: Context): Boolean {
        return if (Utils.validateFieldIsEmpty(edtField.text.toString())) {
            Utils.getSnackBar4K(context, message, constraintLayout)
            false
        } else {
            edtField.error = null
            true
        }
    }

    fun validateMobileField(edtField: EditText, constraintLayout: ConstraintLayout, message : String, context: Context): Boolean {
        return if (Utils.validateMobile(edtField.text.toString())) {
            Utils.getSnackBar4K(context, message, constraintLayout)
            false
        } else {
            true
        }
    }

}