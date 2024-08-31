package info.passdaily.camrelconvertapp.landingpage.firstpage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class FaqViewModel(private val mainRepository: MainRepository) : ViewModel() {
    val TAG : String = "FaqViewModel"

//    var getfaqModel: MutableLiveData<FaqModel> = MutableLiveData()
//
//
//    fun getfaqModelObservable() : MutableLiveData<FaqModel> {
//        return getfaqModel
//    }
//
//    fun getFaqItems(StudentId: Int, AboutType : Int) {
//        val apiInterface = RetrofitClient.create().faqitems(StudentId,AboutType)
//        apiInterface.enqueue(object : Callback<FaqModel> {
//            override fun onResponse(
//                call: Call<FaqModel>, response: Response<FaqModel>
//            ) {
//                Log.i(TAG,"getfaqModel "+response.body())
//                if(response.isSuccessful){
//                    getfaqModel.postValue(response.body())
//                }else{
//                    getfaqModel.postValue(null)
//                }
//            }
//            override fun onFailure(call: Call<FaqModel>, t: Throwable) {
//                getfaqModel.postValue(null)
//                Log.i(TAG, "onFailure $t")
//            }
//        })
//    }


    fun getFaqItems(StudentId: Int, AboutType : Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getFaqItems(StudentId,AboutType)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}