package info.passdaily.camrelconvertapp.typeofuser.parent.zoom_layout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class JoinLiveInitViewModel(private val mainRepository: MainRepository) : ViewModel() {
    // TODO: Implement the ViewModel

    var TAG = "AssignmentViewModel"

//    var getSubjectsModel: MutableLiveData<SubjectsModel> = MutableLiveData()
//
//    var getLiveClassList: MutableLiveData<LiveClassListModel> = MutableLiveData()
//
//    fun getSubjectObservable(): MutableLiveData<SubjectsModel> {
//        return getSubjectsModel
//    }
//
//    fun getSubjects(classId : Int,studentId: Int) {
//
//        val apiInterface = RetrofitClient.create().getSubjectModel(classId,studentId)
//        apiInterface.enqueue(object : Callback<SubjectsModel> {
//            override fun onResponse(
//                call: Call<SubjectsModel>, response: Response<SubjectsModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getSubjectsModel.postValue(response.body())
//                } else {
//                    getSubjectsModel.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<SubjectsModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getSubjectsModel.postValue(null)
//            }
//        })
//    }
//
//
//    fun getLiveClassObservable(): MutableLiveData<LiveClassListModel> {
//        return getLiveClassList
//    }
//
//    fun getLiveClassList(academicId: Int,classId : Int,studentId: Int,subjectId : Int) {
//        Log.i(TAG, "studentId $studentId")
//        val apiInterface = RetrofitClient.create().getJoinLive(academicId,classId,studentId,subjectId)
//        apiInterface.enqueue(object : Callback<LiveClassListModel> {
//            override fun onResponse(
//                call: Call<LiveClassListModel>, response: Response<LiveClassListModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getLiveClassList.postValue(response.body())
//                } else {
//                    getLiveClassList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<LiveClassListModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getLiveClassList.postValue(null)
//            }
//        })
//    }


    fun getLiveClassList(academicId: Int,classId : Int,subjectId: Int,studentId : Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getJoinLiveNew(academicId,classId,subjectId,studentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getSubjects(classId : Int,studentId: Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getSubjectModelNew(classId,studentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getMeetingAttend(zLIVECLASSID : Int,aCCADEMICID: Int
    ,STUDENTID : Int, mStatus : String) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getMeetingAttend(zLIVECLASSID,aCCADEMICID, STUDENTID,mStatus)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

}