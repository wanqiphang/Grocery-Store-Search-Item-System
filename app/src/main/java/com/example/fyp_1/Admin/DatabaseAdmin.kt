package com.example.fyp_1.Admin

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class databaseAdmin (private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "Admin.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "Admin"
        private const val ID_COL = "adminID"
        private const val NAME_COL = "adminName"
        private const val PHONE_NO_COL = "adminPhoneNo"
        private const val EMAIL_COL = "adminEmail"
        private const val PASSWORD_COL = "adminPassword"
    }

    override fun onCreate(db: SQLiteDatabase) {
        //create table
        val createTableQuery = ("CREATE TABLE $TABLE_NAME (" +
                "$ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$NAME_COL TEXT, " +
                "$PHONE_NO_COL TEXT, " +
                "$EMAIL_COL TEXT, " +
                "$PASSWORD_COL TEXT)")
        //execute the table
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    //the function will return the row ID of the newly inserted record
    fun insertAdmin(name: String, phoneNo: Int, email: String, password: String): Long {
        // Check if the email already exists in the database
        if (!isEmailExists(email)) {
            val values = ContentValues().apply {
                put(NAME_COL, name)
                put(PHONE_NO_COL, phoneNo)
                put(EMAIL_COL, email)
                put(PASSWORD_COL, password)
            }
            val db = writableDatabase // write data
            return db.insert(TABLE_NAME, null, values)
        } else {
            // Return -1L to indicate that the insertion failed due to duplicate email
            return -1L
        }
    }

    //check email is not duplicated when new admin signup
    private fun isEmailExists(email: String): Boolean {
        val db = readableDatabase // read or view data
        val selection = "$EMAIL_COL = ?"
        val selectionArgs = arrayOf(email)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        val emailExists = cursor.count > 0
        cursor.close()
        return emailExists
    }

    //if user present in database then true otherwise false
    fun readAdmin(email: String, password: String): Boolean{
        val db = readableDatabase //read or view data
        //condition for the query where the email and password
        //must match with the provided email and password
        val selection = "$EMAIL_COL = ? AND $PASSWORD_COL = ?"
        //use to replace the '?'
        val selectionArgs = arrayOf(email, password)
        //cursor act as the pointer of the rows in database
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        //if signup successful then assign +ve otherwise failed then assign -ve
        val adminExists = cursor.count > 0
        cursor.close()
        return adminExists
    }
}