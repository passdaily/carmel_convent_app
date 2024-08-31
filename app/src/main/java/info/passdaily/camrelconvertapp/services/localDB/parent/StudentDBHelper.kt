package info.passdaily.camrelconvertapp.services.localDB.parent

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

@Suppress("UNREACHABLE_CODE")
class StudentDBHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "StudentDataBase";
        private const val DATABASE_VERSION = 1
        private const val TABLE = "StudentTable"
        private const val CLASS_ID = "class_Id"
        private const val STUDENT_ID = "student_Id"
        private const val STUDENT_ROLL_NO = "student_rollno"
        private const val ACCADEMIC_ID = "academic_Id"
        private const val ACCADEMIC_TIME = "academic_time"

        private const val CREATE_USER_TABLE = "CREATE TABLE " + TABLE + " (" +
                CLASS_ID + " VARCHAR," +
                STUDENT_ID + " VARCHAR," +
                STUDENT_ROLL_NO + " VARCHAR," +
                ACCADEMIC_ID + " VARCHAR," +
                ACCADEMIC_TIME + " VARCHAR)"
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
        contentValues.put(CLASS_ID, student.CLASS_ID) // UserModel id
        contentValues.put(STUDENT_ID, student.STUDENT_ID) // UserModel token
        contentValues.put(STUDENT_ROLL_NO, student.STUDENT_ROLL_NO) // UserModel id
        contentValues.put(ACCADEMIC_ID, student.ACCADEMIC_ID) // UserModel token
        contentValues.put(ACCADEMIC_TIME, student.ACCADEMIC_TIME) // UserModel id
        // Inserting Row
        val success = db.insert(TABLE, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    //method to read data
    @SuppressLint("Recycle", "Range")
    fun viewUser(): List<StudentModel> {
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
        var classId: Int
        var studentId: Int
        var studentRollNo: Int
        var academicId: Int
        var academicTime: String
        if (cursor.moveToFirst()) {
            do {
                classId = cursor.getInt(cursor.getColumnIndex("class_Id"))
                studentId = cursor.getInt(cursor.getColumnIndex("student_Id"))
                studentRollNo = cursor.getInt(cursor.getColumnIndex("student_rollno"))
                academicId = cursor.getInt(cursor.getColumnIndex("academic_Id"))
                academicTime = cursor.getString(cursor.getColumnIndex("academic_time"))

                val user = StudentModel(CLASS_ID = classId, STUDENT_ID = studentId,
                    STUDENT_ROLL_NO = studentRollNo, ACCADEMIC_ID = academicId,
                            ACCADEMIC_TIME = academicTime)
                userlist.add(user)
            } while (cursor.moveToNext())
            cursor.close();
            db.close();
        } else {
            classId = 0
            studentId = 0
            studentRollNo = 0
            academicId = 0
            academicTime="null"
            val user = StudentModel(CLASS_ID = classId, STUDENT_ID = studentId,
                STUDENT_ROLL_NO = studentRollNo, ACCADEMIC_ID = academicId,
                ACCADEMIC_TIME = academicTime)
            userlist.add(user)
            cursor.close();
            db.close();
        }
        return userlist
    }
    // Getting single country
    @SuppressLint("Recycle")
    fun getCountry(id: Int): StudentModel {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE,
            arrayOf(
                CLASS_ID,
                STUDENT_ID, STUDENT_ROLL_NO, ACCADEMIC_ID, ACCADEMIC_TIME
            ),
            "$STUDENT_ID=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )
//        cursor?.moveToFirst();
        // return country
        if (cursor.moveToFirst()) {
            do {
                return StudentModel(
                    cursor!!.getString(0).toInt(),
                    cursor.getString(1).toInt(),
                    cursor.getString(2).toInt(),
                    cursor.getInt(3),
                    cursor.getString(4)
                )
            } while (cursor.moveToNext())
        }else{
            return StudentModel(
                CLASS_ID = 0,
                STUDENT_ID= 0,
                STUDENT_ROLL_NO = 0,
                ACCADEMIC_ID = 0,
                ACCADEMIC_TIME = ""
            )
        }
        cursor.close();
        db.close();
    }
    ////
    fun getProductById(Id: Int): StudentModel {
        val db: SQLiteDatabase = this.readableDatabase
        val selectQuery = "SELECT " +
                CLASS_ID + "," +
                STUDENT_ID + ","+
                STUDENT_ROLL_NO + "," +
                ACCADEMIC_ID + "," +
                ACCADEMIC_ID +
                " FROM " + TABLE + " WHERE " +
                STUDENT_ID + "=?" // It's a good practice to use parameter ?, instead of concatenate string
        val cursor = db.rawQuery(selectQuery, arrayOf(Id.toString()))
        if (cursor.moveToFirst()) {
            do {
                return StudentModel(
                    cursor!!.getString(0).toInt(),
                    cursor.getString(1).toInt(),
                    cursor.getString(2).toInt(),
                    cursor.getInt(3),
                    cursor.getString(4)
                )
            } while (cursor.moveToNext())
        }else{
            return StudentModel(
                CLASS_ID = 0,
                STUDENT_ID= 0,
                STUDENT_ROLL_NO = 0,
                ACCADEMIC_ID = 0,
                ACCADEMIC_TIME = ""
            )
        }
        cursor.close();
        db.close();
    }
    ////

    @SuppressLint("Range", "Recycle")
    fun getStudentById(id: Int): StudentModel {
        val db = this.readableDatabase
      //  val c = db.rawQuery("SELECT * FROM tbl1 WHERE name = '" + name.toString() + "'", null)
        val cursor = db.query(
            TABLE,
            arrayOf(
                CLASS_ID,
                STUDENT_ID, STUDENT_ROLL_NO, ACCADEMIC_ID, ACCADEMIC_TIME
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
                CLASS_ID = cursor.getInt(cursor.getColumnIndex("class_Id")),
                STUDENT_ID= cursor.getInt(cursor.getColumnIndex("student_Id")),
                STUDENT_ROLL_NO = cursor.getInt(cursor.getColumnIndex("student_rollno")),
                ACCADEMIC_ID = cursor.getInt(cursor.getColumnIndex("academic_Id")),
                ACCADEMIC_TIME = cursor.getString(cursor.getColumnIndex("academic_time"))
             )
            } while (cursor.moveToNext())
        }else{
            return StudentModel(
                CLASS_ID = 0,
                STUDENT_ID= 0,
                STUDENT_ROLL_NO = 0,
                ACCADEMIC_ID = 0,
                ACCADEMIC_TIME = ""
            )
        }
        cursor.close()
        db.close()
        // return contact
    }




    //method to update data
    fun updateUser(student: StudentModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(CLASS_ID, student.CLASS_ID) // UserModel id
        contentValues.put(STUDENT_ID, student.STUDENT_ID) // UserModel token
        contentValues.put(STUDENT_ROLL_NO, student.STUDENT_ROLL_NO) // UserModel id
        contentValues.put(ACCADEMIC_ID, student.ACCADEMIC_ID) // UserModel token
        contentValues.put(ACCADEMIC_TIME, student.ACCADEMIC_TIME) // UserModel id

        // It's a good practice to use parameter ?, instead of concatenate string
        val success = db.update(
            TABLE,
            contentValues,
            "$STUDENT_ROLL_NO= ?",
            arrayOf(student.STUDENT_ROLL_NO.toString())
        )
        // Updating Row
   //     val success = db.update(TABLE, contentValues, "id=$STUDENT_ID", null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    //method to delete data
    fun deleteUser(student: StudentModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(STUDENT_ROLL_NO, student.STUDENT_ROLL_NO) // UserModel UserId
        // Deleting Row
        val success = db.delete(TABLE, "id=" + student.STUDENT_ROLL_NO, null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    //method to delete data
    fun deleteUserID(id: Int): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(STUDENT_ID, id) // UserModel UserId
        // Deleting Row
        val success = db.delete(TABLE, "id=$id", null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    ////StudentModel
    class StudentModel(var CLASS_ID: Int, var STUDENT_ID: Int,
                       var STUDENT_ROLL_NO: Int, var ACCADEMIC_ID: Int,
                       var ACCADEMIC_TIME: String)
}