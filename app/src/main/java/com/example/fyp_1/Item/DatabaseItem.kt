package com.example.fyp_1.Item

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fyp_1.Branch.DataBranch
import com.example.fyp_1.Branch.databaseBranch
import com.example.fyp_1.Item.databaseItemLocate.Companion.BRANCH_ID_COL

class databaseItem (private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object {
        // creating constant variables for the database.
        // below variable is for the database name.
        private const val DATABASE_NAME = "Item.db"

        // below int is for the database version.
        private const val DATABASE_VERSION = 1

        // below variable is for the table name.
        const val TABLE_NAME = "item"

        const val ITEM_ID_COL = "itemId"
        private const val ITEM_NAME_COL = "itemName"
        private const val ITEM_PRICE_COL = "itemPrice"
        private const val ITEM_IMAGE_COL = "itemImage"
    }

    // this method is for creating a database by running an SQLite query
    override fun onCreate(db: SQLiteDatabase) {
        // on below line we are creating
        // an SQLite query and we are
        // setting our column names
        // along with their data types.
        val createTableQuery = ("CREATE TABLE $TABLE_NAME (" +
                "$ITEM_ID_COL INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$ITEM_NAME_COL TEXT," +
                "$ITEM_PRICE_COL TEXT," +
                "$ITEM_IMAGE_COL BLOB)")

        // at last, we are calling an exec SQL
        // method to execute the above SQL query
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    //insert data
    fun insertItem(item: DataItem){
        val db = writableDatabase //modify data
        val values = ContentValues().apply{
            //will store the content in data class
            put(databaseItem.ITEM_NAME_COL, item.itemName)
            put(databaseItem.ITEM_PRICE_COL, item.itemPrice)
            put(databaseItem.ITEM_IMAGE_COL, item.itemImage)
        }
        db.insert(databaseItem.TABLE_NAME, null, values)
        db.close()
    }

    //get data need to extend the data class
    fun getAllItems(): List<DataItem>{
        val itemList = mutableListOf<DataItem>() //flexible to carry all data
        val db = readableDatabase //read data
        val query = "SELECT * FROM ${databaseItem.TABLE_NAME}"
        //execute the query using cursor so can iterate through rows of the table
        val cursor = db.rawQuery(query, null)

        //retrieve first then just can display in recycler view
        while(cursor.moveToNext()){
            val itemID = cursor.getInt(cursor.getColumnIndexOrThrow(databaseItem.ITEM_ID_COL))
            val itemName = cursor.getString(cursor.getColumnIndexOrThrow(databaseItem.ITEM_NAME_COL))
            val itemPrice = cursor.getString(cursor.getColumnIndexOrThrow(databaseItem.ITEM_PRICE_COL))
            val itemImage = cursor.getBlob(cursor.getColumnIndexOrThrow(databaseItem.ITEM_IMAGE_COL));
            //pass the argument in the variable and then add into the list
            val item = DataItem(itemID, itemName, itemPrice, itemImage)
            itemList.add(item)
        }
        cursor.close()
        db.close()
        //itemList act as a list which consists of all the retrieved data
        //from database, will use this function in item view activity
        return itemList
    }

    //update item get from id
    fun updateItem(item: DataItem){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(databaseItem.ITEM_NAME_COL, item.itemName)
            put(databaseItem.ITEM_PRICE_COL, item.itemPrice)
            put(databaseItem.ITEM_IMAGE_COL, item.itemImage)
        }
        val whereClause = "${databaseItem.ITEM_ID_COL} = ?"
        val whereArgs = arrayOf(item.itemID.toString())
        db.update(databaseItem.TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getItemByID(itemId: Int): DataItem {
        val db = readableDatabase
        val query = "SELECT * FROM ${databaseItem.TABLE_NAME} WHERE ${databaseItem.ITEM_ID_COL} = $itemId"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val itemID = cursor.getInt(cursor.getColumnIndexOrThrow(databaseItem.ITEM_ID_COL))
        val itemName = cursor.getString(cursor.getColumnIndexOrThrow(databaseItem.ITEM_NAME_COL))
        val itemPrice = cursor.getString(cursor.getColumnIndexOrThrow(databaseItem.ITEM_PRICE_COL))
        val itemImage = cursor.getBlob(cursor.getColumnIndexOrThrow(databaseItem.ITEM_IMAGE_COL))

        cursor.close()
        db.close()
        return DataItem(itemID, itemName, itemPrice, itemImage)
    }

    //delete item
    fun deleteItem(itemId: Int){
        val db = writableDatabase
        val whereClause = "${databaseItem.ITEM_ID_COL} = ?"
        val whereArgs = arrayOf(itemId.toString())
        db.delete(databaseItem.TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    fun getItemNameById(itemId: Int): String {
        val db = readableDatabase
        val query = "SELECT ${databaseItem.ITEM_NAME_COL} FROM ${databaseItem.TABLE_NAME} WHERE ${databaseItem.ITEM_ID_COL} = ?"
        val selectionArgs = arrayOf(itemId.toString())
        val cursor = db.rawQuery(query, selectionArgs)
        val itemName = if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndexOrThrow(databaseItem.ITEM_NAME_COL))
        } else {
            "" // Item name not found
        }
        cursor.close()
        db.close()
        return itemName
    }

    //for searching item using search view
    fun searchItems(keyword: String, branchId: Int, itemIds: List<Int>): List<DataItem> {
        val itemList = mutableListOf<DataItem>()
        val db = readableDatabase

        // Check if the item with this itemId exists in the specified branch and matches the keyword
        val query = "SELECT * FROM ${databaseItem.TABLE_NAME} WHERE " +
                "$ITEM_NAME_COL LIKE '%$keyword%' OR " +
                "$ITEM_PRICE_COL LIKE '%$keyword%' AND " +
                "$ITEM_ID_COL IN (${itemIds.joinToString()})"

        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            val itemID = cursor.getInt(cursor.getColumnIndexOrThrow(databaseItem.ITEM_ID_COL))

            // Ensure the item is associated with the specified branch
            if (itemIds.contains(itemID)) {
                val itemName = cursor.getString(cursor.getColumnIndexOrThrow(databaseItem.ITEM_NAME_COL))
                val itemPrice = cursor.getString(cursor.getColumnIndexOrThrow(databaseItem.ITEM_PRICE_COL))
                val itemImage = cursor.getBlob(cursor.getColumnIndexOrThrow(databaseItem.ITEM_IMAGE_COL))
                val item = DataItem(itemID, itemName, itemPrice, itemImage)
                itemList.add(item)
            }
        }

        cursor.close()
        db.close()
        return itemList
    }

    private fun isItemInBranch(itemId: Int, branchId: Int): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM ${databaseItemLocate.TABLE_NAME} WHERE " +
                "$ITEM_ID_COL = $itemId AND $BRANCH_ID_COL = $branchId"
        val cursor = db.rawQuery(query, null)

        val result = cursor.count > 0

        cursor.close()
        db.close()

        return result
    }


    fun getItemImageById(itemId: Int): ByteArray {
        val db = readableDatabase
        val query = "SELECT $ITEM_IMAGE_COL FROM $TABLE_NAME WHERE $ITEM_ID_COL = ?"
        val selectionArgs = arrayOf(itemId.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        cursor.moveToFirst()
        val itemImage = cursor.getBlob(cursor.getColumnIndexOrThrow(ITEM_IMAGE_COL))

        cursor.close()
        db.close()

        return itemImage
    }

}