package info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel

import android.content.Context
import android.util.Log
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import info.passdaily.camrelconvertapp.services.Utils
import kotlinx.coroutines.Dispatchers

class LoginParentViewModel(private val mainRepository: MainRepository) : ViewModel() {
    val TAG : String = "LoginParentViewModel"

    private var stringMutableLiveData: MutableLiveData<String?>? = null

    fun init() {
        stringMutableLiveData = MutableLiveData()
    }

    fun sendData(msg: String?) {
        stringMutableLiveData!!.value = msg
    }

    fun getMessage(): LiveData<String?>? {
        return stringMutableLiveData
    }

//    var getLoginModel: MutableLiveData<LoginModel> = MutableLiveData()
//
//
//    fun getLoginDetailsObservable(): MutableLiveData<LoginModel> {
//        return getLoginModel
//    }
//
//    fun getLoginDetails(username: String, password : String) {
//        Log.i(TAG, "Username $username")
//        Log.i(TAG, "Password $password")
//        val apiInterface = RetrofitClient.create().loginPage(username,password)
//        apiInterface.enqueue(object : Callback<LoginModel> {
//            override fun onResponse(
//                call: Call<LoginModel>, response: Response<LoginModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getLoginModel.postValue(response.body())
//                } else {
//                    getLoginModel.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<LoginModel>, t: Throwable) {
//                Log.i("DashboardDesigner", "t $t")
//                getLoginModel.postValue(null)
//            }
//        })
//    }


    fun getLoginDetails(username: String, password : String,schoolCode: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getLoginDetails(username,password, schoolCode)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getRegistrationDetails(mobileNumber: String,schoolCode: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getRegistrationDetails(mobileNumber, schoolCode)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun validateField(edtField: EditText, constraintLayout: ConstraintLayout,message : String, context: Context): Boolean {
        return if (Utils.validateFieldIsEmpty(edtField.text.toString())) {
            Utils.getSnackBar4K(context, message, constraintLayout)
            false
        } else {
            edtField.error = null
            true
        }
    }

    fun validateMobileField(edtField: EditText, constraintLayout: ConstraintLayout,message : String, context: Context): Boolean {
        return if (Utils.validateMobile(edtField.text.toString())) {
            Utils.getSnackBar4K(context, message, constraintLayout)
            false
        } else {
            true
        }
    }


    fun validateSelectType(selectType: Int, constraintLayout: ConstraintLayout,message : String, context: Context): Boolean {
        return if (selectType == 0) {
            Utils.getSnackBar4K(context, message, constraintLayout)
            false
        } else {
            true
        }
    }
}