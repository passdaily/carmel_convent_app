package info.passdaily.camrelconvertapp.lib

import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.IOException
import java.io.File
import java.io.FileInputStream


class ProgressRequestBody(
    private val mFile: File,
    private val content_type: String,
    private val mListener: ImageUploadCallback?
) :
    RequestBody() {
    private val mPath: String? = null
    private val upload: String? = null

    // checks when the function is called second time
    private var writeToCall = 0

    init {
        writeToCall = 0
    }

    override fun contentType(): MediaType? {
        return "$content_type/*".toMediaTypeOrNull()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mFile.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        writeToCall++ // update the counter
        val fileLength = mFile.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var uploaded: Long = 0
        FileInputStream(mFile).use { `in` ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())
            while (`in`.read(buffer).also { read = it } != -1) {
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
                if (writeToCall == 2) { // updating the progress
                    handler.post(ProgressUpdater(uploaded, fileLength))
                }
            }
        }
    }

    private inner class ProgressUpdater constructor(
        private val mUploaded: Long,
        private val mTotal: Long
    ) :
        Runnable {
        override fun run() {
            mListener?.onProgressUpdate((100 * mUploaded / mTotal).toInt())
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }

}
//class ProgressRequestBody(
//    file: File,
//    private val content_type: String,
//    listener: ImageUploadCallback?
//) :
//    RequestBody() {
//    private val mFile: File
//    private val mPath: String? = null
//    private val mListener: ImageUploadCallback?
//    private val upload: String? = null
//
//    init {
//        mFile = file
//        mListener = listener //callback passed from the activity
//    }
//
//    @Nullable
//    override fun contentType(): MediaType? {
//        return "$content_type/*".toMediaTypeOrNull()
//    }
//
//    @Throws(IOException::class)
//    override fun contentLength(): Long {
//        return mFile.length()
//    }
//
//    @Throws(IOException::class)
//    override fun writeTo(@NonNull sink: BufferedSink) {
//        val fileLength: Long = mFile.length()
//        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
//        var uploaded: Long = 0
//        FileInputStream(mFile).use { `in` ->
//            var read: Int
//            val handler = Handler(Looper.getMainLooper())
//            while (`in`.read(buffer).also { read = it } != -1) {
//                uploaded += read.toLong()
//                sink.write(buffer, 0, read)
//                handler.post(ProgressUpdater(uploaded, fileLength))
//            }
//        }
//    }
//
//    private inner class ProgressUpdater internal constructor(
//        private val mUploaded: Long,
//        private val mTotal: Long
//    ) :
//        Runnable {
//        override fun run() {
//            mListener?.onProgressUpdate((100 * mUploaded / mTotal).toInt()) //updating the UI of the progress
//        }
//    }
//
//    companion object {
//        private const val DEFAULT_BUFFER_SIZE = 2048
//    }
//}