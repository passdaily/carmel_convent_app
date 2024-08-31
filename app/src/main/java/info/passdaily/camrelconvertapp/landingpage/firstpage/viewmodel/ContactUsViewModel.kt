package info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class ContactUsViewModel(private val mainRepository: MainRepository) : ViewModel() {

    val TAG : String = "ContactUsViewModel"
//
//    var getContactUs: MutableLiveData<ContactUsModel> = MutableLiveData()
//
//
//    fun getContactUsObservable() : MutableLiveData<ContactUsModel> {
//        return getContactUs
//    }
//
//
//    fun getContactUsItems(StudentId: Int) {
//        val apiInterface = RetrofitClient.create().contactUsItem(StudentId)
//        apiInterface.enqueue(object : Callback<ContactUsModel> {
//            override fun onResponse(
//                call: Call<ContactUsModel>, response: Response<ContactUsModel>
//            ) {
//                Log.i(TAG,"getfaqModel "+response.body())
//                if(response.isSuccessful){
//                    getContactUs.postValue(response.body())
//                }else{
//                    getContactUs.postValue(null)
//                }
//            }
//            override fun onFailure(call: Call<ContactUsModel>, t: Throwable) {
//                getContactUs.postValue(null)
//                Log.i(TAG, "onFailure $t")
//            }
//        })
//    }

    fun getContactUsItems(StudentId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getContactUsItems(StudentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}