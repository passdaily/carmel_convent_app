package info.passdaily.camrelconvertapp.typeofuser.parent.conveyor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import info.passdaily.camrelconvertapp.services.Utils
import kotlinx.coroutines.Dispatchers
import okhttp3.RequestBody

class ConveyorViewModel (private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "AnnualReportViewModel"

    fun getConveyorGet(StudentId : Int,ClassId : Int) = liveData(
        Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getConveyorGet(StudentId,ClassId)))
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



    fun validateField(edtField: TextInputEditText): Boolean {
        return if (Utils.validateFieldIsEmpty(edtField.text.toString())) {
            edtField.error = "Field Cannot be empty"
            false
        } else {
            edtField.error = null
            true
        }
    }
}