package com.example.fyp_1.Item

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fyp_1.Branch.DataBranch
import com.example.fyp_1.Branch.databaseBranch

class databaseItemLocate (private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // creating constant variables for the database.
        // below variable is for the database name.
        private const val DATABASE_NAME = "ManageItem.db"

        // below int is for the database version.
        private const val DATABASE_VERSION = 2

        // below variable is for the table name.
        const val TABLE_NAME = "ManageItem"

        private const val MI_ID_COL = "itemLocateId"
        const val BRANCH_ID_COL = "branchId"
        const val ITEM_ID_COL = "itemId"
        private const val ITEM_LOCATION_COL = "location"


    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME (" +
                "$MI_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$BRANCH_ID_COL INTEGER," +
                "$ITEM_ID_COL INTEGER," +
                "$ITEM_LOCATION_COL TEXT," +
                "FOREIGN KEY ($BRANCH_ID_COL) REFERENCES Branch($BRANCH_ID_COL)," +
                "FOREIGN KEY ($ITEM_ID_COL) REFERENCES Item($ITEM_ID_COL))")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${databaseItemLocate.TABLE_NAME}")
        onCreate(db)
    }

    //insert data
    fun insertItemLocation(itemLocation: DataItemLocation){
        val db = writableDatabase //modify data
        val values = ContentValues().apply{
            //will store the content in data class
            put(databaseItemLocate.BRANCH_ID_COL, itemLocation.branchID)
            put(databaseItemLocate.ITEM_ID_COL, itemLocation.itemID)
            put(databaseItemLocate.ITEM_LOCATION_COL, itemLocation.location)
        }
        db.insert(databaseItemLocate.TABLE_NAME, null, values)
        db.close()
    }

    // Get data for a specific itemId
    fun getAllItemLocationByItemId(itemId: Int): List<DataItemLocation> {
        val itemLocationList = mutableListOf<DataItemLocation>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $ITEM_ID_COL = $itemId"

        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val itemLocateID = cursor.getInt(cursor.getColumnIndexOrThrow(MI_ID_COL))
            val branchID = cursor.getInt(cursor.getColumnIndexOrThrow(BRANCH_ID_COL))
            val itemID = cursor.getInt(cursor.getColumnIndexOrThrow(ITEM_ID_COL))
            val itemLocation = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_LOCATION_COL))

            val location = DataItemLocation(itemLocateID, branchID, itemID, itemLocation)
            itemLocationList.add(location)
        }

        cursor.close()
        db.close()
        return itemLocationList
    }

    //update item location get from id
    fun updateItemLocation(itemLocation: DataItemLocation){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(databaseItemLocate.BRANCH_ID_COL, itemLocation.branchID)
            put(databaseItemLocate.ITEM_LOCATION_COL, itemLocation.location)
        }
        val whereClause = "${databaseItemLocate.MI_ID_COL} = ?"
        val whereArgs = arrayOf(itemLocation.itemLocateID.toString())
        db.update(databaseItemLocate.TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getItemLocationByID(itemLocationId: Int): DataItemLocation{
        val db = readableDatabase
        val query = "SELECT * FROM ${databaseItemLocate.TABLE_NAME} WHERE ${databaseItemLocate.MI_ID_COL} = $itemLocationId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val itemLocateID = cursor.getInt(cursor.getColumnIndexOrThrow(databaseItemLocate.MI_ID_COL))
        val branchID = cursor.getInt(cursor.getColumnIndexOrThrow(databaseItemLocate.BRANCH_ID_COL))
        val itemID = cursor.getInt(cursor.getColumnIndexOrThrow(databaseItemLocate.ITEM_ID_COL))
        val itemLocation = cursor.getString(cursor.getColumnIndexOrThrow(databaseItemLocate.ITEM_LOCATION_COL))
        cursor.close()
        db.close()
        return DataItemLocation(itemLocateID, branchID, itemID, itemLocation)
    }

    //delete item location
    fun deleteItemLocation(itemLocateId: Int){
        val db = writableDatabase
        val whereClause = "${databaseItemLocate.MI_ID_COL} = ?"
        val whereArgs = arrayOf(itemLocateId.toString())
        db.delete(databaseItemLocate.TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    fun getItemLocationById(itemId: Int, branchId: Int): String {
        val db = readableDatabase
        val query = "SELECT $ITEM_LOCATION_COL FROM $TABLE_NAME WHERE $ITEM_ID_COL = $itemId AND $BRANCH_ID_COL = $branchId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val itemLocation = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_LOCATION_COL))

        cursor.close()
        db.close()
        return itemLocation
    }

    //search item
    fun getItemIdsByBranch(branchId: Int): List<Int> {
        val itemIdList = mutableListOf<Int>()
        val db = readableDatabase
        val query = "SELECT $ITEM_ID_COL FROM ${databaseItemLocate.TABLE_NAME} WHERE $BRANCH_ID_COL = $branchId"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val itemId = cursor.getInt(cursor.getColumnIndexOrThrow(ITEM_ID_COL))
            itemIdList.add(itemId)
        }

        cursor.close()
        db.close()

        return itemIdList
    }

    // Helper method to get branch IDs associated with the item
    fun getBranchIdsByItemId(itemId: Int): List<Int> {
        val branchIds = mutableListOf<Int>()
        val db = readableDatabase
        val query = "SELECT $BRANCH_ID_COL FROM $TABLE_NAME WHERE $ITEM_ID_COL = $itemId"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val branchId = cursor.getInt(cursor.getColumnIndexOrThrow(BRANCH_ID_COL))
            branchIds.add(branchId)
        }

        cursor.close()
        db.close()

        return branchIds
    }
}

