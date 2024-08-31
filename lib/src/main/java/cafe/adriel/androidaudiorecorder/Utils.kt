package cafe.adriel.androidaudiorecorder

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.File

class Utils  {

    companion object{
        fun getRootDirPath(context: Context): String? {
            return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                val file: File =
                    ContextCompat.getExternalFilesDirs(context.applicationContext, null)[0]
                file.absolutePath
            } else {
                context.applicationContext.filesDir.absolutePath
            }
        }

    }
}