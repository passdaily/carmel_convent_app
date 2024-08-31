package info.passdaily.camrelconvertapp.typeofuser.parent.assignment

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
import okhttp3.RequestBody

class AssignmentDetailsViewModel(private val mainRepository: MainRepository) : ViewModel() {
    // TODO: Implement the ViewModel

    var TAG = "AssignmentDetailsViewModel"


//    var getAssignmentDetails: MutableLiveData<AssignmentDetailsModel> = MutableLiveData()
//
//
//    fun getAssignmentDetailsObservable(): MutableLiveData<AssignmentDetailsModel> {
//        return getAssignmentDetails
//    }
//
//    fun getAssignmentDetails(academicId: Int,classId : Int,studentId: Int,subjectId : Int) {
//        Log.i(TAG, "academicId $academicId")
//        Log.i(TAG, "classId $classId")
//        Log.i(TAG, "studentId $studentId")
//        Log.i(TAG, "subjectId $subjectId")
//        val apiInterface = RetrofitClient.create().getAssignmentDetails(academicId,classId,studentId,subjectId)
//        apiInterface.enqueue(object : Callback<AssignmentDetailsModel> {
//            override fun onResponse(
//                call: Call<AssignmentDetailsModel>, response: Response<AssignmentDetailsModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getAssignmentDetails.postValue(response.body())
//                } else {
//                    getAssignmentDetails.postValue(null)
//                }
//            }
//            override fun onFailure(call: Call<AssignmentDetailsModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getAssignmentDetails.postValue(null)
//            }
//        })
//    }


    fun validateField(edtField: EditText, constraintLayout: ConstraintLayout, context: Context): Boolean {
        return if (Utils.validateFieldIsEmpty(edtField.text.toString())) {
            Utils.getSnackBar4K(context, "Answer Cannot be Empty", constraintLayout)
            false
        } else {
            edtField.error = null
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


    ////create enquiry
    fun getDeleteAssignmentFile(assignmentFileId: Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getDeleteAssignmentFile(assignmentFileId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getAssignmentDetails(academicId: Int,classId : Int,studentId: Int,assignmentId : Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getAssignmentDetails(academicId,classId,studentId,assignmentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}