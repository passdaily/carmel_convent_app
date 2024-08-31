package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.manage_album

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

class ManageAlbumViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "ManageAlbumViewModel"

    fun getYearClassExam(adminId: Int,schoolId :Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getYearClassExam(adminId,schoolId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getManageAlbumList(aCCADEMICID: Int,aLBUMYYPE: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getManageAlbumList(aCCADEMICID,aLBUMYYPE)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getImageCategory(url : String,albumCatId: Int,) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getImageCategory(url,albumCatId)))
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

    fun getAlbumCategoryDelete(url : String,albumCatId: Int)  = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAlbumCategoryDelete(url,albumCatId)))
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

    fun validateFieldStr(edtField: String,message : String, context: Context, constraintLayout: ConstraintLayout): Boolean {
        return if (Utils.validateFieldIsEmpty(edtField)) {
            Utils.getSnackBar4K(context, message, constraintLayout)
            false
        } else {
            true
        }
    }
}