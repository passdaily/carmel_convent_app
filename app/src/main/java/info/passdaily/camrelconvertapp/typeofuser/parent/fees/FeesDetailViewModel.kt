package info.passdaily.camrelconvertapp.typeofuser.parent.fees

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.model.*
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers
import okhttp3.RequestBody

class FeesDetailViewModel(private val mainRepository: MainRepository) : ViewModel() {


    var TAG = "FeesDetailViewModel"


    fun getFeesDetails(CLASSID: Int, ACADEMICID: Int, STUDENTID: Int, STUDENT_ROLL_NO: Int)
    = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getFeesDetails(CLASSID,ACADEMICID,STUDENTID,STUDENT_ROLL_NO)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

}