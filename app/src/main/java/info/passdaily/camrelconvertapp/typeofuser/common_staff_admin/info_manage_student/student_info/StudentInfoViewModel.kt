package info.passdaily.camrelconvertapp.typeofuser.common_staff_admin.info_manage_student.student_info

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.android.material.textfield.TextInputEditText
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import info.passdaily.camrelconvertapp.services.Utils
import kotlinx.coroutines.Dispatchers
import okhttp3.RequestBody

class StudentInfoViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var TAG = "StudentInfoViewModel"
    fun getYearClassExam(adminId: Int,schoolId:Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getYearClassExam(adminId, schoolId )))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getStudentInfo(studentList: RequestBody?) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getStudentInfo(studentList)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getStudentInfoDelete(url : String, studentId: Int,accademicId :Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getStudentInfoDelete(url,studentId,accademicId)))
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

    fun validateField(edtField: TextInputEditText, message: String, context: Context, constraintLayout : ConstraintLayout): Boolean {
        return if (Utils.validateFieldIsEmpty(edtField.text.toString())) {
            Utils.getSnackBar4K(context, message, constraintLayout)
           // Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    fun validateFieldBott(edtField: TextInputEditText, message: String, context: Context, constraintLayout : ConstraintLayout): Boolean {
        return if (Utils.validateFieldIsEmpty(edtField.text.toString())) {
          //  Utils.getSnackBar4K(context, message, constraintLayout)
             Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
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

    fun validateFieldStrBott(edtField: String, message: String, context: Context, constraintLayout : ConstraintLayout): Boolean {
        return if (edtField == "-1") {
          //  Utils.getSnackBar4K(context, message, constraintLayout)
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }


}