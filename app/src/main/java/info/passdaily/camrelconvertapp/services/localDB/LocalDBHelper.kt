package info.passdaily.camrelconvertapp.services.localDB

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class LocalDBHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {


    ///{ staff
    //ADMIN_ID: 2,
    //STAFF_ID: 3,
    //ADMIN_ROLE: 3,
    //ADMIN_ROLE_NAME: "TEACHER",
    //LOGIN_ROLE: "TEACHER",
    //LOGIN_STATUS: "SUCCESS",
    //}

//    {  Admin
//    ADMIN_ID: 1,
//    STAFF_ID: 1,
//    ADMIN_ROLE: 1,
//    ADMIN_ROLE_NAME: "ADMIN",
//    LOGIN_ROLE: "ADMIN",
//    LOGIN_STATUS: "SUCCESS",
//    }

    //{ parent
    //PLOGIN_ID: 3,
    //LOGIN_ROLE: "PARENT",
    //}

    companion object {
        private const val DATABASE_NAME = "UserDataBase";
        private const val DATABASE_VERSION = 4
        private const val TABLE = "UserTable"
        private const val ADMIN_ID = "adminId"
        private const val STAFF_ID = "staffId"
        private const val STAFF_NAME = "staffName"
        private const val STAFF_IMAGE = "staffImage"
        private const val ADMIN_ROLE = "adminRole"
        private const val PLOGIN_ID = "pLoginId"
        private const val LOGIN_ROLE = "loginRole"
        private const val BASE_URL = "baseUrl"
        private const val SCHOOL_ID = "schoolId"

        private const val CREATE_USER_TABLE = "CREATE TABLE " + TABLE + " (" +
                ADMIN_ID + " VARCHAR," +
                STAFF_ID + " VARCHAR," +
                STAFF_NAME + " VARCHAR," +
                STAFF_IMAGE + " VARCHAR," +
                ADMIN_ROLE + " VARCHAR," +
                PLOGIN_ID + " VARCHAR," +
                LOGIN_ROLE + " VARCHAR," +
                BASE_URL + " VARCHAR," +
                SCHOOL_ID + " VARCHAR)"
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
    fun insertUser(user: UserModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ADMIN_ID, user.ADMIN_ID) // UserModel id
        contentValues.put(STAFF_ID, user.STAFF_ID) // UserModel id
        contentValues.put(STAFF_NAME, user.STAFF_NAME) // UserModel id
        contentValues.put(STAFF_IMAGE, user.STAFF_IMAGE) // UserModel id
        contentValues.put(ADMIN_ROLE, user.ADMIN_ROLE) // UserModel id
        contentValues.put(PLOGIN_ID, user.PLOGIN_ID) // UserModel id
        contentValues.put(LOGIN_ROLE, user.LOGIN_ROLE) // UserModel token
        contentValues.put(BASE_URL, user.BASE_URL) // UserModel token
        contentValues.put(SCHOOL_ID, user.SCHOOL_ID) // UserModel token
        // Inserting Row
        val success = db.insert(TABLE, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    //method to read data
    @SuppressLint("Recycle", "Range")
    fun viewUser(): List<UserModel> {
        val userlist: ArrayList<UserModel> = ArrayList<UserModel>()
        val selectQuery = "SELECT  * FROM $TABLE"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var adminId: Int
        var staffId: Int
        var staffName : String
        var staffImage : String
        var adminRole: Int
        var pLoginId: Int
        var loginRole: String
        var baseUrl: String
        var schoolId: Int
        if (cursor.moveToFirst()) {
            do {
                adminId = cursor.getInt(cursor.getColumnIndex("adminId"))
                staffId = cursor.getInt(cursor.getColumnIndex("staffId"))
                staffName = cursor.getString(cursor.getColumnIndex("staffName"))
                staffImage = cursor.getString(cursor.getColumnIndex("staffImage"))
                adminRole = cursor.getInt(cursor.getColumnIndex("adminRole"))
                pLoginId = cursor.getInt(cursor.getColumnIndex("pLoginId"))
                loginRole = cursor.getString(cursor.getColumnIndex("loginRole"))
                baseUrl = cursor.getString(cursor.getColumnIndex("baseUrl"))
                schoolId = cursor.getInt(cursor.getColumnIndex("schoolId"))
                val user = UserModel(ADMIN_ID =  adminId,
                    STAFF_ID = staffId,
                    STAFF_NAME = staffName,
                    STAFF_IMAGE = staffImage,
                    ADMIN_ROLE =  adminRole,PLOGIN_ID = pLoginId, LOGIN_ROLE = loginRole, BASE_URL = baseUrl, SCHOOL_ID = schoolId)
                userlist.add(user)
            } while (cursor.moveToNext())
            cursor.close();
            db.close();
        } else {
            adminId =  0
            staffId = 0
            staffName = ""
            staffImage = ""
            adminRole =  0
            pLoginId = 0
            loginRole = ""
            baseUrl = ""
            schoolId = 0
            val user = UserModel(ADMIN_ID =  adminId,
                STAFF_ID = staffId,
                STAFF_NAME = staffName,
                STAFF_IMAGE = staffImage,
                ADMIN_ROLE =  adminRole,PLOGIN_ID = pLoginId, LOGIN_ROLE = loginRole, BASE_URL = baseUrl, SCHOOL_ID = schoolId)
            userlist.add(user)
            cursor.close();
            db.close();
        }
        return userlist
    }

    //method to update data
    fun updateUser(user: UserModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ADMIN_ID, user.ADMIN_ID) // UserModel id
        contentValues.put(STAFF_ID, user.STAFF_ID) // UserModel id
        contentValues.put(STAFF_NAME, user.STAFF_NAME) // UserModel id
        contentValues.put(STAFF_IMAGE, user.STAFF_IMAGE) // UserModel id
        contentValues.put(ADMIN_ROLE, user.ADMIN_ROLE) // UserModel id
        contentValues.put(PLOGIN_ID, user.PLOGIN_ID) // UserModel id
        contentValues.put(LOGIN_ROLE, user.LOGIN_ROLE) // UserModel token
        contentValues.put(BASE_URL, user.BASE_URL) // UserModel token
        contentValues.put(SCHOOL_ID, user.SCHOOL_ID) // UserModel token
        // Updating Row
        val success = db.update(TABLE, contentValues, "id=" + user.PLOGIN_ID, null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    //method to delete data
    fun deleteUser(user: UserModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(PLOGIN_ID, user.PLOGIN_ID) // UserModel UserId
        // Deleting Row
        val success = db.delete(TABLE, "id=" + user.PLOGIN_ID, null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    //method to delete data
    fun deleteUserID(id: Int): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(PLOGIN_ID, id) // UserModel UserId
        // Deleting Row
        val success = db.delete(TABLE, "adminId=$id", null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    fun deleteData(context: Context) {
        context.deleteDatabase(DATABASE_NAME)
    }
//    //method to delete data
//    fun deleteStaffID(id: Int): Int {
//        val db = this.writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put(ADMIN_ID, id) // UserModel UserId
//        // Deleting Row
//        val success = db.delete(TABLE, "adminId=$id", null)
//        //2nd argument is String containing nullColumnHack
//        db.close() // Closing database connection
//        return success
//    }

    ////usermodel
    class UserModel(var ADMIN_ID: Int,
                    var STAFF_ID: Int,
                    var STAFF_NAME : String,
                    var STAFF_IMAGE : String,
                    var ADMIN_ROLE: Int,
                    var PLOGIN_ID: Int,
                    var LOGIN_ROLE: String,var BASE_URL : String,var SCHOOL_ID : Int)
}