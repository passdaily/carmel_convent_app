package info.passdaily.camrelconvertapp.typeofuser.parent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class MainActivityParentViewModel(private val mainRepository: MainRepository) : ViewModel() {
    var TAG = "MainActivityParentViewModel";
    //    String Get_json_child_url= Global.url+"Login/GetChildrenDetails?parentId=";
//    var getChildDetails: MutableLiveData<ChildrensModel> = MutableLiveData()
//
//    var getDashBoardItems: MutableLiveData<DashBoardModel> = MutableLiveData()
//    fun getChildrenDetailsObservable(): MutableLiveData<ChildrensModel> {
//        return getChildDetails
//    }
//
//    fun getChildrenDetails(ParentId: Int) {
//        Log.i(TAG, "ParentId $ParentId")
//        val apiInterface = RetrofitClient.create().childrenDetails(ParentId)
//        apiInterface.enqueue(object : Callback<ChildrensModel> {
//            override fun onResponse(
//                call: Call<ChildrensModel>, response: Response<ChildrensModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getChildDetails.postValue(response.body())
//                } else {
//                    getChildDetails.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<ChildrensModel>, t: Throwable) {
//                Log.i("DashboardDesigner", "t $t")
//                getChildDetails.postValue(null)
//            }
//        })
//    }
//
//    fun getDashBoardObservable(): MutableLiveData<DashBoardModel> {
//        return getDashBoardItems
//    }
//
//    fun getDashBoard(studentId: Int) {
//        Log.i(TAG, "studentId $studentId")
//        val apiInterface = RetrofitClient.create().dashboardItems(studentId)
//        apiInterface.enqueue(object : Callback<DashBoardModel> {
//            override fun onResponse(
//                call: Call<DashBoardModel>, response: Response<DashBoardModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i("DashboardDesigner", "response " + response.body())
//                    getDashBoardItems.postValue(response.body())
//                } else {
//                    getDashBoardItems.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<DashBoardModel>, t: Throwable) {
//                Log.i("DashboardDesigner", "t $t")
//                getDashBoardItems.postValue(null)
//            }
//        })
//    }


    fun getChildrenDetails(ParentId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getChildrenDetailsNew(ParentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getDashBoard(studentId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getDashBoardNew(studentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getBlockStatus(accademicId : Int,studentId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getBlockStatus(accademicId,studentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getLogOutUser(pLoginID1 : Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getLogOutUser(pLoginID1)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


}