package info.passdaily.camrelconvertapp.services.localDB.parent

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class OfflineStoreDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "OfflineDataBase";
        private const val DATABASE_VERSION = 1
        private const val TABLE = "OfflineTable"
        private const val YOUTUBE_ID = "youTubeId"
        private const val STUDENT_NAME = "studentName"
        private const val CLASS_NAME = "className"
        private const val SUBJECT_NAME = "subjectName"
        private const val CHAPTER_NAME = "chapterName"
        private const val VIDEO_TITLE = "videoTitle"
        private const val VIDEO_DESCRIPTION = "videoDesc"
        private const val ORIGINAL_FILE = "originalFile"
        private const val SUBJECT_ICON = "subjectIcon"

        var CREATE_OFFLINE_TABLE = "CREATE TABLE " + TABLE + " (" +
                YOUTUBE_ID + " VARCHAR," +
                STUDENT_NAME + " VARCHAR," +
                CLASS_NAME + " VARCHAR," +
                SUBJECT_NAME + " VARCHAR," +
                CHAPTER_NAME + " VARCHAR," +
                VIDEO_TITLE + " VARCHAR," +
                VIDEO_DESCRIPTION + " VARCHAR," +
                ORIGINAL_FILE + " VARCHAR," +
                SUBJECT_ICON + " VARCHAR)"

        private const val DELETE_OFFLINE_TABLE = "DROP TABLE IF EXISTS $TABLE"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //create table
        db?.execSQL(CREATE_OFFLINE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //drop table if exist
        db?.execSQL(DELETE_OFFLINE_TABLE)
        onCreate(db)
    }


    //method to insert data
    fun insert(offline: OfflineModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(YOUTUBE_ID, offline.YOUTUBE_ID) // OfflineModel YOUTUBE_ID
        contentValues.put(STUDENT_NAME, offline.STUDENT_NAME) // OfflineModel STUDENT_NAME
        contentValues.put(CLASS_NAME, offline.CLASS_NAME) // OfflineModel CLASS_NAME
        contentValues.put(SUBJECT_NAME, offline.SUBJECT_NAME) // OfflineModel SUBJECT_NAME
        contentValues.put(CHAPTER_NAME, offline.CHAPTER_NAME) // OfflineModel CHAPTER_NAME
        contentValues.put(VIDEO_TITLE, offline.VIDEO_TITLE) // OfflineModel VIDEO_TITLE
        contentValues.put(VIDEO_DESCRIPTION, offline.VIDEO_DESCRIPTION) // OfflineModel VIDEO_TITLE
        contentValues.put(ORIGINAL_FILE, offline.ORIGINAL_FILE) // OfflineModel VIDEO_TITLE
        contentValues.put(SUBJECT_ICON, offline.SUBJECT_ICON) // OfflineModel VIDEO_TITLE
        // Inserting Row
        val success = db.insert(TABLE, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }


    //method to read data
    @SuppressLint("Recycle", "Range")
    fun viewOfflineStore(): ArrayList<OfflineModel> {
        val userlist: ArrayList<OfflineModel> = ArrayList<OfflineModel>()
        val selectQuery = "SELECT  * FROM $TABLE"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var youTubeId: Int
        var studentName: String
        var className: String
        var subjectName: String
        var chapterName: String
        var videoTitle: String
        var videoDesc: String
        var originalFile: String
        var subjectIcon: String

        if (cursor.moveToFirst()) {
            do {
                youTubeId = cursor.getInt(cursor.getColumnIndex("youTubeId"))
                studentName = cursor.getString(cursor.getColumnIndex("studentName"))
                className = cursor.getString(cursor.getColumnIndex("className"))
                subjectName = cursor.getString(cursor.getColumnIndex("subjectName"))
                chapterName = cursor.getString(cursor.getColumnIndex("chapterName"))
                videoTitle = cursor.getString(cursor.getColumnIndex("videoTitle"))
                videoDesc = cursor.getString(cursor.getColumnIndex("videoDesc"))
                originalFile = cursor.getString(cursor.getColumnIndex("originalFile"))
                subjectIcon = cursor.getString(cursor.getColumnIndex("subjectIcon"))

                val user = OfflineModel(
                    YOUTUBE_ID = youTubeId,
                    STUDENT_NAME = studentName,
                    CLASS_NAME = className,
                    SUBJECT_NAME = subjectName,
                    CHAPTER_NAME = chapterName,
                    VIDEO_TITLE = videoTitle,
                    VIDEO_DESCRIPTION = videoDesc,
                    ORIGINAL_FILE = originalFile,
                    SUBJECT_ICON = subjectIcon
                )
                userlist.add(user)
            } while (cursor.moveToNext())
        }
//        else {
//            youTubeId = 0
//            studentName = "null"
//            className = "null"
//            subjectName = "null"
//            chapterName = "null"
//            videoTitle = "null"
//            videoDesc = "null"
//            originalFile = "null"
//            subjectIcon = "null"
//            val user = OfflineModel(
//                YOUTUBE_ID = youTubeId,
//                STUDENT_NAME = studentName,
//                CLASS_NAME = className,
//                SUBJECT_NAME = subjectName,
//                CHAPTER_NAME = chapterName,
//                VIDEO_TITLE = videoTitle,
//                VIDEO_DESCRIPTION = videoDesc,
//                ORIGINAL_FILE = originalFile,
//                SUBJECT_ICON = subjectIcon
//            )
//            userlist.add(user)
//            cursor.close();
//            db.close();
//        }
        cursor.close();
        db.close();
        return userlist
    }



    // Getting single country
    @SuppressLint("Recycle", "Range")
    fun viewOfflineVideo(id: Int): ArrayList<OfflineModel> {
        val userlist: ArrayList<OfflineModel> = ArrayList<OfflineModel>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE,
            arrayOf(
                YOUTUBE_ID,
                STUDENT_NAME,
                CLASS_NAME,
                SUBJECT_NAME,
                CHAPTER_NAME,
                VIDEO_TITLE,
                VIDEO_DESCRIPTION,
                ORIGINAL_FILE,
                SUBJECT_ICON
            ),
            "$YOUTUBE_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )
//        cursor?.moveToFirst();
        // return country
        var youTubeId: Int
        var studentName: String
        var className: String
        var subjectName: String
        var chapterName: String
        var videoTitle: String
        var videoDesc: String
        var originalFile: String
        var subjectIcon: String

        if (cursor.moveToFirst()) {
            do {
                youTubeId = cursor.getInt(cursor.getColumnIndex("youTubeId"))
                studentName = cursor.getString(cursor.getColumnIndex("studentName"))
                className = cursor.getString(cursor.getColumnIndex("className"))
                subjectName = cursor.getString(cursor.getColumnIndex("subjectName"))
                chapterName = cursor.getString(cursor.getColumnIndex("chapterName"))
                videoTitle = cursor.getString(cursor.getColumnIndex("videoTitle"))
                videoDesc = cursor.getString(cursor.getColumnIndex("videoDesc"))
                originalFile = cursor.getString(cursor.getColumnIndex("originalFile"))
                subjectIcon = cursor.getString(cursor.getColumnIndex("subjectIcon"))

                val user = OfflineModel(
                    YOUTUBE_ID = youTubeId,
                    STUDENT_NAME = studentName,
                    CLASS_NAME = className,
                    SUBJECT_NAME = subjectName,
                    CHAPTER_NAME = chapterName,
                    VIDEO_TITLE = videoTitle,
                    VIDEO_DESCRIPTION = videoDesc,
                    ORIGINAL_FILE = originalFile,
                    SUBJECT_ICON = subjectIcon
                )
                userlist.add(user)
            } while (cursor.moveToNext())
        }else {
            youTubeId = 0
            studentName = "null"
            className = "null"
            subjectName = "null"
            chapterName = "null"
            videoTitle = "null"
            videoDesc = "null"
            originalFile = "null"
            subjectIcon = "null"
            val user = OfflineModel(
                YOUTUBE_ID = youTubeId,
                STUDENT_NAME = studentName,
                CLASS_NAME = className,
                SUBJECT_NAME = subjectName,
                CHAPTER_NAME = chapterName,
                VIDEO_TITLE = videoTitle,
                VIDEO_DESCRIPTION = videoDesc,
                ORIGINAL_FILE = originalFile,
                SUBJECT_ICON = subjectIcon
            )
            userlist.add(user)
            cursor.close();
            db.close();
        }
        return userlist
    }

    //method to delete data
    fun deleteOfflineItem(id: Int): Int {
//        val db = this.writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put(YOUTUBE_ID, id) // OfflineModel YOUTUBE_ID
//        val success = db.delete(TABLE, "id=$id", null)
//        //2nd argument is String containing nullColumnHack
//        db.close() // Closing database connection
//        return success

        val db = this.writableDatabase
        // It's a good practice to use parameter ?, instead of concatenate string
        val success = db.delete(TABLE, "$YOUTUBE_ID= ?", arrayOf(id.toString()))
        db.close() // Closing database connection
        return success
    }

    ////usermodel
    class OfflineModel(
        var YOUTUBE_ID: Int, var STUDENT_NAME: String,
        var CLASS_NAME: String, var SUBJECT_NAME: String,
        var CHAPTER_NAME: String, var VIDEO_TITLE: String,
        var VIDEO_DESCRIPTION: String,
        var ORIGINAL_FILE: String, var SUBJECT_ICON: String
    )
}