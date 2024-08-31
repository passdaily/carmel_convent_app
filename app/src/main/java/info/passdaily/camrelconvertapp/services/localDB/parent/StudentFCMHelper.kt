package info.passdaily.camrelconvertapp.services.localDB.parent

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class StudentFCMHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "StudentFcmTable";
        private const val DATABASE_VERSION = 1
        private const val TABLE = "StudentFcmTable"
        private const val STUDENT_ID = "studentId"
        private const val STUDENT_IMAGE = "studentImage"
        private const val CLASS_ID = "classId"
        private const val CLASS_NAME = "className"
        private const val STUDENT_NAME = "studentName"
        private const val CLASS_SECTION = "classSection"

        private const val CREATE_USER_TABLE = "CREATE TABLE " + TABLE + " (" +
                STUDENT_ID + " VARCHAR," +
                STUDENT_IMAGE + " VARCHAR," +
                CLASS_ID + " VARCHAR," +
                CLASS_NAME + " VARCHAR," +
                STUDENT_NAME + " VARCHAR," +
                CLASS_SECTION + " VARCHAR)"


        private const val DELETE_USER_TABLE = "DROP TABLE IF EXISTS $TABLE"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //create table
        db?.execSQL(CREATE_USER_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //drop table if exist
        db?.execSQL(DELETE_USER_TABLE)
        onCreate(db)
    }


    //method to insert data
    fun insertUser(student: StudentModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(STUDENT_ID, student.STUDENT_ID) // UserModel id
        contentValues.put(STUDENT_IMAGE, student.STUDENT_IMAGE) // UserModel id
        contentValues.put(CLASS_ID, student.CLASS_ID) // UserModel token
        contentValues.put(CLASS_NAME, student.CLASS_NAME) // UserModel id
        contentValues.put(STUDENT_NAME, student.STUDENT_NAME) // UserModel token
        contentValues.put(CLASS_SECTION, student.CLASS_SECTION) // UserModel id
        // Inserting Row
        val success = db.insert(TABLE, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }



    //method to read data
    @SuppressLint("Recycle", "Range")
    fun viewFcmUser(): ArrayList<StudentModel> {
        val userlist: ArrayList<StudentModel> = ArrayList<StudentModel>()
        val selectQuery = "SELECT  * FROM $TABLE"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var studentId: Int
        var studentImage: String
        var classId: Int
        var className: String
        var studentName: String
        var classSection: String
        if (cursor.moveToFirst()) {
            do {
                studentId = cursor.getInt(cursor.getColumnIndex("studentId"))
                studentImage = cursor.getString(cursor.getColumnIndex("studentImage"))
                classId = cursor.getInt(cursor.getColumnIndex("classId"))
                className = cursor.getString(cursor.getColumnIndex("className"))
                studentName = cursor.getString(cursor.getColumnIndex("studentName"))
                classSection = cursor.getString(cursor.getColumnIndex("classSection"))

                val user = StudentModel(
                    STUDENT_ID = studentId, STUDENT_IMAGE = studentImage, CLASS_ID = classId,
                    CLASS_NAME = className, STUDENT_NAME = studentName,
                    CLASS_SECTION = classSection
                )
                userlist.add(user)
            } while (cursor.moveToNext())
            cursor.close();
            db.close();
        } else {
            studentId = 0
            studentImage = ""
            classId = 0
            className = "0"
            studentName = "0"
            classSection="0"
            val user = StudentModel(
                STUDENT_ID = studentId,STUDENT_IMAGE = studentImage, CLASS_ID = classId,
                CLASS_NAME = className, STUDENT_NAME = studentName,
                CLASS_SECTION = classSection
            )
            userlist.add(user)
            cursor.close();
            db.close();
        }
        return userlist
    }



    @SuppressLint("Range", "Recycle")
    fun getProductByStudent(id: String): StudentModel {
        val db = this.readableDatabase
        //  val c = db.rawQuery("SELECT * FROM tbl1 WHERE name = '" + name.toString() + "'", null)
        val cursor = db.query(
            TABLE,
            arrayOf(
                STUDENT_ID,
                STUDENT_IMAGE,
                CLASS_ID,
                CLASS_NAME,
                STUDENT_NAME,
                CLASS_SECTION
            ),
            "$STUDENT_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                return StudentModel(
                    STUDENT_ID = cursor.getInt(cursor.getColumnIndex("studentId")),
                    STUDENT_IMAGE = cursor.getString(cursor.getColumnIndex("studentImage")),
                    CLASS_ID = cursor.getInt(cursor.getColumnIndex("classId")),
                    CLASS_NAME = cursor.getString(cursor.getColumnIndex("className")),
                    STUDENT_NAME = cursor.getString(cursor.getColumnIndex("studentName")),
                    CLASS_SECTION = cursor.getString(cursor.getColumnIndex("classSection"))
                )
            } while (cursor.moveToNext())
        }else{
            return StudentModel(
                STUDENT_ID = 0,
                STUDENT_IMAGE = "",
                CLASS_ID = 0,
                CLASS_NAME = "",
                STUDENT_NAME = "",
                CLASS_SECTION = ""
            )
        }
        cursor.close()
        db.close()
        // return contact
    }

    @SuppressLint("Range", "Recycle")
    fun getProductByClassId(id: String): StudentModel {
        val db = this.readableDatabase
        //  val c = db.rawQuery("SELECT * FROM tbl1 WHERE name = '" + name.toString() + "'", null)
        val cursor = db.query(
            TABLE,
            arrayOf(
                STUDENT_ID,
                STUDENT_IMAGE,
                CLASS_ID,
                CLASS_NAME,
                STUDENT_NAME,
                CLASS_SECTION
            ),
            "$CLASS_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                return StudentModel(
                    STUDENT_ID = cursor.getInt(cursor.getColumnIndex("studentId")),
                    STUDENT_IMAGE = cursor.getString(cursor.getColumnIndex("studentImage")),
                    CLASS_ID = cursor.getInt(cursor.getColumnIndex("classId")),
                    CLASS_NAME = cursor.getString(cursor.getColumnIndex("className")),
                    STUDENT_NAME = cursor.getString(cursor.getColumnIndex("studentName")),
                    CLASS_SECTION = cursor.getString(cursor.getColumnIndex("classSection"))
                )
            } while (cursor.moveToNext())
        }else{
            return StudentModel(
                STUDENT_ID = 0,
                STUDENT_IMAGE = "",
                CLASS_ID = 0,
                CLASS_NAME = "",
                STUDENT_NAME = "",
                CLASS_SECTION = ""
            )
        }
        cursor.close()
        db.close()
        // return contact
    }


    @SuppressLint("Range", "Recycle")
    fun getProductByClassSec(id: String): StudentModel {
        val db = this.readableDatabase
        //  val c = db.rawQuery("SELECT * FROM tbl1 WHERE name = '" + name.toString() + "'", null)
        val cursor = db.query(
            TABLE,
            arrayOf(
                STUDENT_ID,
                STUDENT_IMAGE,
                CLASS_ID,
                CLASS_NAME,
                STUDENT_NAME,
                CLASS_SECTION
            ),
            "$CLASS_SECTION=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                return StudentModel(
                    STUDENT_ID = cursor.getInt(cursor.getColumnIndex("studentId")),
                    STUDENT_IMAGE = cursor.getString(cursor.getColumnIndex("studentImage")),
                    CLASS_ID = cursor.getInt(cursor.getColumnIndex("classId")),
                    CLASS_NAME = cursor.getString(cursor.getColumnIndex("className")),
                    STUDENT_NAME = cursor.getString(cursor.getColumnIndex("studentName")),
                    CLASS_SECTION = cursor.getString(cursor.getColumnIndex("classSection"))
                )
            } while (cursor.moveToNext())
        }else{
            return StudentModel(
                STUDENT_ID = 0,
                STUDENT_IMAGE = "",
                CLASS_ID = 0,
                CLASS_NAME = "",
                STUDENT_NAME = "",
                CLASS_SECTION = ""
            )
        }
        cursor.close()
        db.close()
        // return contact
    }



    //method to delete data
    fun deleteUserID(id: Int): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(STUDENT_ID, id) // UserModel UserId
        // Deleting Row
        val success = db.delete(TABLE, "studentId=$id", null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }



    ////StudentModel
    class StudentModel(var STUDENT_ID: Int,var STUDENT_IMAGE: String, var CLASS_ID: Int,
                       var CLASS_NAME: String, var STUDENT_NAME: String,
                       var CLASS_SECTION: String)

}