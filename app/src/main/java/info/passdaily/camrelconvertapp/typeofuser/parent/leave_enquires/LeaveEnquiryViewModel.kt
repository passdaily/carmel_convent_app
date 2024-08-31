package info.passdaily.camrelconvertapp.typeofuser.parent.leave_enquires

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

class LeaveEnquiryViewModel(private val  mainRepository: MainRepository) : ViewModel() {


    var TAG = "LeaveEnquiryViewModel"

//    var getLeaveList: MutableLiveData<LeaveDetailsModel> = MutableLiveData()
//
//    var getEnquiryList: MutableLiveData<EnquiryListModel> = MutableLiveData()
//
//    fun getLeaveListObservable(): MutableLiveData<LeaveDetailsModel> {
//        return getLeaveList
//    }
//
//    fun getEnquiryListObservable(): MutableLiveData<EnquiryListModel> {
//        return getEnquiryList
//    }
//
//
//    //    fun getLeaveList(accademic: Int, classId: Int, studentId: Int, studentRollNo: Int, submitDate : String ,toDate: String) {
//     //  Log.i(TAG, "studentId $studentId")
//      fun getLeaveList(jsonObject: JSONObject) {
//    Log.i(TAG, "jsonObject $jsonObject")
//        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
//      val apiInterface = RetrofitClient.create().getLeaveList(accademicRe)
//        apiInterface.enqueue(object : Callback<LeaveDetailsModel> {
//            override fun onResponse(
//                call: Call<LeaveDetailsModel>, response: Response<LeaveDetailsModel>
//            ) {
//                Log.i(TAG, "errorBody " + response.errorBody().toString())
//                Log.i(TAG, "message " + response.message())
//                Log.i(TAG, "isSuccessful " + response.isSuccessful)
//                Log.i(TAG, "body " + response.body().toString())
//                Log.i(TAG, "code " + response.code())
//                Log.i(TAG, "response " + response.body())
//                Log.i(TAG, "headers " + response.headers())
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getLeaveList.postValue(response.body())
//                } else {
//                    getLeaveList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<LeaveDetailsModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getLeaveList.postValue(null)
//            }
//        })
//    }
//
//
//    fun getEnquiryList(jsonObject: JSONObject) {
//        Log.i(TAG, "jsonObject $jsonObject")
//        val accademicRe =  jsonObject.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
//        val apiInterface = RetrofitClient.create().getEnquiryList(accademicRe)
//        apiInterface.enqueue(object : Callback<EnquiryListModel> {
//            override fun onResponse(
//                call: Call<EnquiryListModel>, response: Response<EnquiryListModel>
//            ) {
//
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getEnquiryList.postValue(response.body())
//                } else {
//                    getEnquiryList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<EnquiryListModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getEnquiryList.postValue(null)
//            }
//        })
//    }


    fun getEnquiryList(accademicRe: RequestBody?) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getEnquiryListNew(accademicRe)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getLeaveList(accademicRe: RequestBody?) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getLeaveListNew(accademicRe)))
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


    ////delete enquiry
    fun getDeleteEnquiryFun(url : String,enquiryId  : Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getDeleteEnquiryFun(url,enquiryId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    ////delete enquiry
    fun getDeleteLeaveFun(url : String,leaveId: Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getDeleteLeaveFun(url,leaveId)))
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


}