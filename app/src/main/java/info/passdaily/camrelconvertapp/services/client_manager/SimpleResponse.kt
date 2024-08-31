package info.passdaily.camrelconvertapp.services.client_manager

import retrofit2.Response
import java.lang.Exception

data class SimpleResponse<T>(
    val status : Status,
    val data : Response<T>?,
    val exception : Exception?
) {

    companion object{
        fun<T> success (data: Response<T>): SimpleResponse<T> {
            return SimpleResponse(
                status = Status.Success,
                data = data,
                exception = null
            )
        }
        fun<T> failure (exception: Exception?): SimpleResponse<T> {
            return SimpleResponse(
                status = Status.Failure,
                data = null,
                exception = exception
            )
        }
    }
    sealed class Status{
        object Success : Status()
        object Failure :  Status()
    }
    val failed :Boolean
       get() = this.status == Status.Failure

    val isSuccessful : Boolean
      get() = !failed && this.data?.isSuccessful == true

    val body : T
       get() = this.data!!.body()!!

    val code : Boolean
        get() = !failed && this.data?.code() == 500 || this.data?.code() == 502

}