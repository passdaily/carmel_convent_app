package info.passdaily.camrelconvertapp.typeofuser.parent.online_video

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import info.passdaily.camrelconvertapp.MainRepository
import info.passdaily.camrelconvertapp.services.Resource
import kotlinx.coroutines.Dispatchers

class OnlineVideoViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var TAG = "OnlineVideoViewModel"

//    var getSubjectList: MutableLiveData<SubjectsModel> = MutableLiveData()
//    var getChapterList: MutableLiveData<ChapterListModel> = MutableLiveData()
//
//    var getOnlineVideoList: MutableLiveData<YoutubeListModel> = MutableLiveData()

    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text

//    ////Online Video List
//    fun getOnlineVideoObservable(): MutableLiveData<YoutubeListModel> {
//        return getOnlineVideoList
//    }
//    ///Subject List
//    fun getSubjectListObservable(): MutableLiveData<SubjectsModel> {
//        return getSubjectList
//    }
//
//    ///Subject List
//    fun getChapterListObservable(): MutableLiveData<ChapterListModel> {
//        return getChapterList
//    }
//    ////AccademicId=7&ClassId=2&StudentId=1
//    fun getOnlineVideo(accademicId : Int, classId: Int,SubjectId: Int,ChapterId : Int) {
//        Log.i(TAG, "SubjectId $SubjectId")
//        val apiInterface = RetrofitClient.create().getOnlineVideoList(accademicId,classId,SubjectId,ChapterId)
//        apiInterface.enqueue(object : Callback<YoutubeListModel> {
//            override fun onResponse(
//                call: Call<YoutubeListModel>, response: Response<YoutubeListModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i("OnlineVideoFragment", "response " + response.body())
//                    getOnlineVideoList.postValue(response.body())
//                } else {
//                    getOnlineVideoList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<YoutubeListModel>, t: Throwable) {
//                Log.i("OnlineVideoFragment", "t $t")
//                getOnlineVideoList.postValue(null)
//            }
//        })
//    }
//
//    fun getSubjects(classId: Int,StudentId : Int) {
//        Log.i(TAG, "StudentId $StudentId")
//        Log.i(TAG, "classId $classId")
//        val apiInterface = RetrofitClient.create().getSubjectModel(classId,StudentId)
//        apiInterface.enqueue(object : Callback<SubjectsModel> {
//            override fun onResponse(
//                call: Call<SubjectsModel>, response: Response<SubjectsModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getSubjectList.postValue(response.body())
//                } else {
//                    getSubjectList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<SubjectsModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getSubjectList.postValue(null)
//            }
//        })
//    }
//
//
//    /////////Chapter List
//    fun getChapter(accademicId : Int, classId: Int,StudentId : Int,) {
//        Log.i(TAG, "StudentId $StudentId")
//        Log.i(TAG, "classId $classId")
//        Log.i(TAG, "accademicId $accademicId")
//        val apiInterface = RetrofitClient.create().getChapterModel(accademicId,classId,StudentId)
//        apiInterface.enqueue(object : Callback<ChapterListModel> {
//            override fun onResponse(
//                call: Call<ChapterListModel>, response: Response<ChapterListModel>
//            ) {
//                if (response.isSuccessful) {
//                    Log.i(TAG, "response " + response.body())
//                    getChapterList.postValue(response.body())
//                } else {
//                    getChapterList.postValue(null)
//                }
//            }
//
//            override fun onFailure(call: Call<ChapterListModel>, t: Throwable) {
//                Log.i(TAG, "t $t")
//                getChapterList.postValue(null)
//            }
//        })
//    }





    fun getOnlineVideo(academicId: Int,classId : Int,studentId: Int,subjectId : Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getOnlineVideo(academicId,classId,studentId,subjectId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getSubjects(classId : Int,studentId: Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getSubjects(classId,studentId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun getChapter(accademicId : Int, classId: Int,subjectId : Int) = liveData(
        Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = mainRepository.getChapter(accademicId,classId,subjectId)))
        } catch (exception: Exception) {
            Log.i(TAG, "exception $exception")
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

}