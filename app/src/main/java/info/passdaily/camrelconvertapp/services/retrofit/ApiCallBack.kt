package info.passdaily.camrelconvertapp.services.retrofit

interface ApiCallBack<T> {

    fun  onFailure(error : String)
    fun  onError(error : String)
    fun  onSuccess(response : T)
}