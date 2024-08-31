package info.passdaily.camrelconvertapp.services

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import info.passdaily.camrelconvertapp.services.retrofit.ApiInterface
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.concurrent.TimeUnit

object FileUtils {
    @JvmField
    var url = ApiInterface.BASE_URLS
    @JvmField
    var m_url = "http://madrasastaff.passdaily.in/ElixirApi/"
    private const val TAG = "FileUtils"
    fun requestPermission(activity: Activity?, permission: String) {
        if (ContextCompat.checkSelfPermission(activity!!, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), 0)
        }
    }

    @SuppressLint("DefaultLocale")
    fun getHumanTimeText(j: Long): String {
        return String.format(
            "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(j),
            TimeUnit.MILLISECONDS.toSeconds(j) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    j
                )
            )
        )
    }

    //content://com.android.providers.downloads.documents/document/msf%3A1000040597

    //content://com.android.providers.media.documents/document/document%3A1000040597




    fun getRealPathFromURI(uri: Uri, context: Context): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)
        val nameIndex =  returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = returnCursor.getLong(sizeIndex).toString()
        val file = File(context.filesDir, name)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream?.available() ?: 0
            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream?.read(buffers).also {
                    if (it != null) {
                        read = it
                    }
                } != -1) {
                outputStream.write(buffers, 0, read)
            }
            Log.e("File Size", "Size " + file.length())
            inputStream?.close()
            outputStream.close()
            Log.e("File Path", "Path " + file.path)

        } catch (e: java.lang.Exception) {
            Log.e("Exception", e.message!!)
        }
        return file.path
    }



    @WorkerThread
    fun getReadablePathFromUri(context: Context, uri: Uri): String? {
        var path: String? = null
        if ("file".equals(uri.scheme, ignoreCase = true)) {
            path = uri.path
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            path = getPath(context, uri)
        }else{
            // var uriString = uri
            path = if(uri.toString().contains("file://")){
                uri.toString().replace("file:///","")
            }else{
                getRealPathFromURI(uri,context)
            }
            Log.i(TAG,"path $path")
        }
        if (TextUtils.isEmpty(path)) {
            return path
        }
        Log.i(TAG, "get path from uri: $path")
        if (!isReadablePath(path)) {
            val index = path!!.lastIndexOf("/")
            val name = path.substring(index + 1)
            val dstPath = context.cacheDir.absolutePath + File.separator + name
            if (copyFile(context, uri, dstPath)) {
                path = dstPath
                Log.i(TAG, "copy file success: $path")
            } else {
                Log.i(TAG, "copy file fail!")
            }
        }
        return path
    }

    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                Log.i("External Storage", docId)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val dstPath =
                    context.cacheDir.absolutePath + File.separator + getFileName(context, uri)
                if (copyFile(context, uri, dstPath)) {
                    Log.i(TAG, "copy file success: $dstPath")
                    return dstPath
                } else {
                    Log.i(TAG, "copy file fail!")
                }
            } else if (isMediaDocument(uri)) {
                Log.i(TAG, "uri $uri")
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                contentUri = when (type) {
                    "image" -> {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    else -> {
                        // MediaStore.Files.getContentUri("external", split[1].toLong());
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                            MediaStore.getMediaUri(context, uri)
                            // MediaStore.Files.getContentUri(uri.toString())
                        }
                        else {
                            MediaStore.Files.getContentUri("external")
                        }
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getFileName(context: Context, uri: Uri?): String {
        val cursor = context.contentResolver.query(uri!!, null, null, null, null)
        val nameindex = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        return cursor.getString(nameindex)
    }

    //    private static String getDataColumn(Context context, Uri uri, String selection,String[] selectionArgs) {
    //        String realPath = null;
    //
    //        final String column = "_data";
    //        final String[] projection = {column};
    //        try {
    //            context.getContentResolver().takePersistableUriPermission(uri,
    //                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    //
    //            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
    //            if (cursor != null && cursor.moveToFirst()) {
    //                int columnIndex = cursor.getColumnIndexOrThrow(column);
    //                realPath = cursor.getString(columnIndex);
    //                cursor.close();
    //            }
    //        } catch (SecurityException e) {
    //            e.printStackTrace();
    //        }
    //        return realPath;
    //    }
    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var name = ""
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                name =  cursor.getString(columnIndex)
                return name
            }
        } finally {
            cursor?.close()
        }
        return name
    }

    ///content://com.android.providers.media.documents/document/document%3A1000023268
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isReadablePath(path: String?): Boolean {
        if (TextUtils.isEmpty(path)) {
            return false
        }
        val isLocalPath: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!TextUtils.isEmpty(path)) {
                val localFile = File(path)
                localFile.exists() && localFile.canRead()
            } else {
                false
            }
        } else {
            path!!.startsWith(File.separator)
        }
        return isLocalPath
    }

    private fun copyFile(context: Context, uri: Uri, dstPath: String): Boolean {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            outputStream = FileOutputStream(dstPath)
            val buff = ByteArray(100 * 1024)
            var len: Int
            while (inputStream!!.read(buff).also { len = it } != -1) {
                outputStream.write(buff, 0, len)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return true
    }
}