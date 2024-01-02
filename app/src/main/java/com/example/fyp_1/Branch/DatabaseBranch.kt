package com.example.fyp_1.Branch

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class databaseBranch (private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object {
        private const val DATABASE_NAME = "Branch.db"
        private const val DATABASE_VERSION = 2
        const val TABLE_NAME = "Branch"
        const val BRANCH_ID_COL = "branchID"
        const val BRANCH_NAME_COL = "branchName"
        private const val BRANCH_LOCATION_COL = "branchLocation"
    }

    override fun onCreate(db: SQLiteDatabase) {
        //create table
        val createTableQuery = ("CREATE TABLE $TABLE_NAME (" +
                "$BRANCH_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$BRANCH_NAME_COL TEXT, " +
                "$BRANCH_LOCATION_COL TEXT)")
        //execute the table
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    // Insert data and return true if successful, false if branch location already exists
    fun insertBranch(branch: DataBranch): Boolean {
        writableDatabase.use { db ->
            // Check if the branch location already exists
            if (!isBranchLocationExists(db, branch.branchLocation)) {
                val values = ContentValues().apply {
                    // Will store the content in the data class
                    put(BRANCH_NAME_COL, branch.branchName)
                    put(BRANCH_LOCATION_COL, branch.branchLocation)
                }
                db.insert(TABLE_NAME, null, values)
                return true // Insertion successful
            } else {
                return false // Branch location already exists
            }
        }
    }

    // Function to check if the branch location already exists in the database
    private fun isBranchLocationExists(db: SQLiteDatabase, branchLocation: String): Boolean {
        val query = "SELECT * FROM $TABLE_NAME WHERE $BRANCH_LOCATION_COL = ?"
        val selectionArgs = arrayOf(branchLocation)
        db.rawQuery(query, selectionArgs).use { cursor ->
            return cursor.count > 0
        }
    }

    //get data need to extend the data class
    fun getAllBranches(): List<DataBranch>{
        val branchList = mutableListOf<DataBranch>() //flexible to carry all data
        val db = readableDatabase //read data
        val query = "SELECT * FROM $TABLE_NAME"
        //execute the query using cursor so can iterate through rows of the table
        val cursor = db.rawQuery(query, null)

        //retrieve first then just can display in recycler view
        while(cursor.moveToNext()){
            val branchID = cursor.getInt(cursor.getColumnIndexOrThrow(BRANCH_ID_COL))
            val branchName = cursor.getString(cursor.getColumnIndexOrThrow(BRANCH_NAME_COL))
            val branchLocation = cursor.getString(cursor.getColumnIndexOrThrow(BRANCH_LOCATION_COL))
            //pass the argument in the variable and then add into the list
            val branch = DataBranch(branchID, branchName, branchLocation)
            branchList.add(branch)
        }
        cursor.close()
        db.close()
        //branchList act as a list which consists of all the retrieved data
        //from database, will use this function in branch view activity
        return branchList
    }

    //update branch get from id
    fun updateBranch(branch: DataBranch){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(BRANCH_NAME_COL, branch.branchName)
            put(BRANCH_LOCATION_COL, branch.branchLocation)
        }
        val whereClause = "$BRANCH_ID_COL = ?"
        val whereArgs = arrayOf(branch.branchID.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getBranchByID(branchId: Int): DataBranch{
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $BRANCH_ID_COL = $branchId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val branchID = cursor.getInt(cursor.getColumnIndexOrThrow(BRANCH_ID_COL))
        val branchName = cursor.getString(cursor.getColumnIndexOrThrow(BRANCH_NAME_COL))
        val branchLocation = cursor.getString(cursor.getColumnIndexOrThrow(BRANCH_LOCATION_COL))

        cursor.close()
        db.close()
        return DataBranch(branchID, branchName, branchLocation)
    }

    //delete branch
    fun deleteBranch(branchId: Int){
        val db = writableDatabase
        val whereClause = "$BRANCH_ID_COL = ?"
        val whereArgs = arrayOf(branchId.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    //get the branchId for associative entity
    fun getBranchIdByName(branchName: String): Int {
        val db = readableDatabase
        val query = "SELECT $BRANCH_ID_COL FROM $TABLE_NAME WHERE $BRANCH_NAME_COL = ?"
        val selectionArgs = arrayOf(branchName)
        val cursor = db.rawQuery(query, selectionArgs)
        val branchId = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(BRANCH_ID_COL))
        } else {
            -1 // Branch not found
        }
        cursor.close()
        db.close()
        return branchId
    }

    // Add this method to get branch name by ID
    fun getBranchNameById(branchId: Int): String {
        val db = readableDatabase
        val query = "SELECT $BRANCH_NAME_COL FROM $TABLE_NAME WHERE $BRANCH_ID_COL = $branchId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val branchName = cursor.getString(cursor.getColumnIndexOrThrow(BRANCH_NAME_COL))

        cursor.close()
        db.close()
        return branchName
    }
}