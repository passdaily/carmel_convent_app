package info.passdaily.camrelconvertapp.lib

interface ImageUploadCallback {
    fun onProgressUpdate(percentage: Int)
    fun onError(message: String?)
    fun onSuccess(message: String?)
}