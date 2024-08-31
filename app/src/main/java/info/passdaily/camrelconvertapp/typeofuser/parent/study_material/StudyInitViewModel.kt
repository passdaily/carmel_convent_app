package info.passdaily.camrelconvertapp.typeofuser.parent.study_material

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class StudyInitViewModel(private val mainRepository: MainRepository) : ViewModel() {
    // TODO: Implement the ViewModel

    var TAG = "AssignmentViewModel"

//    var getSubjectsModel: MutableLiveData<SubjectsModel> = MutableLiveData()
//
//    var getStudyMaterial: MutableLiveData<StudyMaterialModel> = MutableLiveData()
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
//    fun getStudyMaterialObservable(): MutableLiveData<StudyMaterialModel> {
//        return getStudyMaterial
//    }

//    fun getStudyMaterial(academicId: Int,classId : Int,studentId: Int,subjectId : Int) {
//        Log.i(TAG, "studentId $studentId")
//        val apiInterface = RetrofitClient.create().getStudyMaterialList(academicId,classId,studentId,subjectId)
//        apiInterface.enqueue(object : Callback<StudyMaterialModel> {
//            override fun onResponse(
//                call: Call<StudyMaterialModel>, response: Response<StudyMaterialModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getStudyMaterial.postValue(response.body())
//                } else {
//                    getStudyMaterial.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<StudyMaterialModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getStudyMaterial.postValue(null)
//            }
//        })
//    }


    fun getStudyMaterial(academicId: Int,classId : Int,studentId: Int,subjectId : Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getStudyMaterialListNew(academicId,classId,studentId,subjectId)))
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


    fun getMaterialList(materialId : Int) = liveData(Dispatchers.IO) {

        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getMaterialList(materialId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}