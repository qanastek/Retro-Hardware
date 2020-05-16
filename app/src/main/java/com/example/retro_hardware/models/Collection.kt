package com.example.retro_hardware.models

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import android.database.sqlite.SQLiteOpenHelper
import android.net.ConnectivityManager
import android.util.Log
import com.example.retro_hardware.models.Threads.FetchItems
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Collection : SQLiteOpenHelper {

    /**
     * Context
     */
    private lateinit var context: Context

    /**
     * Array list of items
     */
    var items: ArrayList<Item> = arrayListOf()

    /**
     * Static methods
     */
    companion object {

        // Class name
        val TAG: String = this::class.java.canonicalName as String

        // Current database version
        const val DATABASE_VERSION: Int = 3

        // Database name
        const val DATABASE_NAME = "retro.hardware"

        /**
         * Columnn names
         */
        const val COLLECTION_TABLE  = "collection"
        const val COLLECTION_ID  = "id" // Item id
        const val COLLECTION_YEAR  = "year"
        const val COLLECTION_NAME  = "name"
        const val COLLECTION_BRAND  = "brand"
        const val COLLECTION_DESCRIPTION  = "description"
        const val COLLECTION_WORKING  = "working"

        const val PICTURES_TABLE  = "pictures"
        const val PICTURES_ID  = "id" // Image id
        const val PICTURES_ITEM_ID  = "item_id" // Item id
        const val PICTURES_DESCRIPTION  = "description"

        const val CATEGORIES_TABLE  = "categories"
        const val CATEGORIES_ITEM_ID  = "item_id" // Item id
        const val CATEGORIES_NAME  = "name"

        const val TECHNICAL_DETAILS_TABLE  = "technicalDetails"
        const val TECHNICAL_DETAILS_ITEM_ID  = "item_id" // Item id
        const val TECHNICAL_DETAILS_NAME  = "name"

        const val TIME_FRAME_TABLE  = "timeFrame"
        const val TIME_FRAME_ITEM_ID  = "item_id" // Item id
        const val TIME_FRAME_YEAR  = "year"

        const val DEMOS_TABLE  = "demos"
        const val DEMOS_ITEM_ID  = "item_id" // Item id
        const val DEMOS_DATE  = "date" // Date of the next demo

        /**
         * Get the collection url
         */
        fun getUrlCollection(): String {
            return "${Api.BASE_URL}/catalog"
        }

        /**
         * Get a collection url
         */
        fun getUrlDemos(): String {
            return "${Api.BASE_URL}/demos"
        }

        /**
         * Check if the phote can reach the internet
         */
        fun isOnline(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            // Log.d("------------", (networkInfo != null && networkInfo.isConnected).toString())

            return networkInfo != null && networkInfo.isConnected
        }
    }

    /**
     * Constructor
     */
    constructor(context: Context) : super(context, DATABASE_NAME, null, DATABASE_VERSION) {
        this.context = context

        // Load the database into the array list
        this.loadItems()
    }

    /**
     * When the database is instanciated
     */
    override fun onCreate(db: SQLiteDatabase?) {

        // If the database is null
        if (db == null) { return }

        Log.d("----------","onCreate")

        /**
         * Create the collection table
         */
        val SQL_CREATE_COLLECTION_QUERY: String = "CREATE TABLE $COLLECTION_TABLE (" +
                "$COLLECTION_ID TEXT PRIMARY KEY, " +
                "$COLLECTION_NAME TEXT NOT NULL, " +
                "$COLLECTION_BRAND TEXT, " +
                "$COLLECTION_YEAR INTEGER, " +
                "$COLLECTION_DESCRIPTION TEXT," +
                "$COLLECTION_WORKING BOOLEAN, " +
                " UNIQUE ($COLLECTION_NAME, $COLLECTION_BRAND) ON CONFLICT ROLLBACK);"

        db.execSQL(SQL_CREATE_COLLECTION_QUERY)

        /**
         * Create the pictures table
         */
        val SQL_CREATE_PICTURES_QUERY: String = "CREATE TABLE $PICTURES_TABLE (" +
                "$PICTURES_ID TEXT PRIMARY KEY NOT NULL, " +
                "$PICTURES_ITEM_ID TEXT NOT NULL, " +
                "$PICTURES_DESCRIPTION TEXT)"

        db.execSQL(SQL_CREATE_PICTURES_QUERY)

        /**
         * Create the categories table
         */
        val SQL_CREATE_CATEGORIES_QUERY: String = "CREATE TABLE $CATEGORIES_TABLE (" +
                "$CATEGORIES_ITEM_ID TEXT NOT NULL, " +
                "$CATEGORIES_NAME TEXT NOT NULL)"

        db.execSQL(SQL_CREATE_CATEGORIES_QUERY)

        /**
         * Create the technical details table
         */
        val SQL_CREATE_TECHNICAL_DETAILS_QUERY: String = "CREATE TABLE $TECHNICAL_DETAILS_TABLE (" +
                "$TECHNICAL_DETAILS_ITEM_ID TEXT NOT NULL, " +
                "$TECHNICAL_DETAILS_NAME TEXT NOT NULL)"

        db.execSQL(SQL_CREATE_TECHNICAL_DETAILS_QUERY)

        /**
         * Create the time frame table
         */
        val SQL_CREATE_TIME_FRAME_QUERY: String = "CREATE TABLE $TIME_FRAME_TABLE (" +
                "$TIME_FRAME_ITEM_ID TEXT NOT NULL, " +
                "$TIME_FRAME_YEAR INTEGER NOT NULL)"

        db.execSQL(SQL_CREATE_TIME_FRAME_QUERY)

        /**
         * Create the demos table
         */
        val SQL_CREATE_DEMOS_QUERY: String = "CREATE TABLE $DEMOS_TABLE (" +
                "$DEMOS_ITEM_ID TEXT NOT NULL, " +
                "$DEMOS_DATE TEXT NOT NULL, " +
                "UNIQUE ($DEMOS_ITEM_ID, $DEMOS_DATE) ON CONFLICT ROLLBACK);"

        db.execSQL(SQL_CREATE_DEMOS_QUERY)

        // Fetch the items from the API
        FetchItems().execute()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        // If the internet connection is enabled
        if (isOnline(this.context)) {
            FetchItems().execute()
        }
    }

    /**
     * Return all the categories available
     */
    fun getCategories(): ArrayList<String> {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT DISTINCT $CATEGORIES_NAME FROM $CATEGORIES_TABLE" , null)

        cursor?.moveToFirst()

        val res: ArrayList<String> = ArrayList()

        if (cursor.moveToFirst()) {

            // Load all the categories
            while (!cursor.isAfterLast) {

                val category: String = cursor.getString(cursor.getColumnIndex(CATEGORIES_NAME))
                res.add(category)

                cursor.moveToNext()
            }
        }

        return res
    }

    /**
     * Return the minimum and maximum date
     */
    fun getExtremeYears(): Pair<Int,Int> {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT MIN($COLLECTION_YEAR) AS \'MIN\', MAX($COLLECTION_YEAR) AS \'MAX\' FROM $COLLECTION_TABLE WHERE $COLLECTION_YEAR > 0" , null)

        cursor?.moveToFirst()

        // Min and max years
        var min: Int = 0
        var max: Int = 9999

        if (cursor.moveToFirst()) {

            // Load all the categories
            while (!cursor.isAfterLast) {

                min = cursor.getInt(cursor.getColumnIndex("MIN"))
                max = cursor.getInt(cursor.getColumnIndex("MAX"))

                break;
            }
        }

        return Pair(min,max)
    }

    /**
     * Return all the brands available
     */
    fun getBrands(): ArrayList<String> {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT DISTINCT $COLLECTION_BRAND FROM $COLLECTION_TABLE WHERE LENGTH($COLLECTION_BRAND) > 1" , null)

        cursor?.moveToFirst()

        val res: ArrayList<String> = ArrayList()

        if (cursor.moveToFirst()) {

            // Load all the categories
            while (!cursor.isAfterLast) {

                val brand: String = cursor.getString(cursor.getColumnIndex(COLLECTION_BRAND))
                res.add(brand)

                cursor.moveToNext()
            }
        }

        return res
    }

    /**
     * Fills the ContentValues of the item with a Item
     */
    private fun contentValuesItem(item: Item): ContentValues {

        val values = ContentValues()

        values.put(COLLECTION_ID,item.id)
        values.put(COLLECTION_BRAND,item.brand)
        values.put(COLLECTION_DESCRIPTION,item.description)
        values.put(COLLECTION_NAME,item.name)
        values.put(COLLECTION_WORKING,item.working)
        values.put(COLLECTION_YEAR,item.year)

        return values
    }

    /**
     * Add an Item to the database
     */
    fun addItem(item: Item): Boolean {

        // If the item is empty
        if (item == null) { return false }

        // Connect to the db
        val db = this.writableDatabase

        // Log.d(TAG, "Add " + item.name + " with id " + item.id);

        // The the content values for the item itself
        val itemValues: ContentValues = contentValuesItem(item)
        // Insert the item into it's table
        val itemStatus: Long = db.insertWithOnConflict(COLLECTION_TABLE, null, itemValues, CONFLICT_IGNORE)

        /**
         * If have categories
         */
        var categoriesStatus: Long = 0
        if (item.categories.size > 0) {

            // Foreach categories
            for (category in item.categories) {

                val values = ContentValues()
                values.put(CATEGORIES_ITEM_ID,item.id)
                values.put(CATEGORIES_NAME,category)

                // Insertion
                val statusTemp = db.insertWithOnConflict(CATEGORIES_TABLE, null, values, CONFLICT_IGNORE)

                // Keep the error if it's one
                if (statusTemp == -1L) { categoriesStatus = statusTemp }
            }
        }

        /**
         * If have pictures
         */
        var pictureStatus: Long = 0
        if (item.pictures.size > 0) {

            // Foreach picture
            for (picture in item.pictures) {

                val valuesPicture = ContentValues()

                valuesPicture.put(PICTURES_ID, picture.key)
                valuesPicture.put(PICTURES_ITEM_ID, item.id)
                valuesPicture.put(PICTURES_DESCRIPTION,picture.value)

                // Insertion
                val statusTemp = db.insertWithOnConflict(PICTURES_TABLE, null, valuesPicture, CONFLICT_IGNORE)

                // Keep the error if it's one
                if (statusTemp == -1L) { pictureStatus = statusTemp }
            }
        }

        /**
         * If have technical details
         */
        var technicalDetailsStatus: Long = 0
        if (item.technicalDetails.size > 0) {

            // Foreach technical detail
            for (detail in item.technicalDetails) {

                val values = ContentValues()
                values.put(TECHNICAL_DETAILS_ITEM_ID,item.id)
                values.put(TECHNICAL_DETAILS_NAME,detail)

                // Insertion
                val statusTemp = db.insertWithOnConflict(TECHNICAL_DETAILS_TABLE, null, values, CONFLICT_IGNORE)

                // Keep the error if it's one
                if (statusTemp == -1L) { technicalDetailsStatus = statusTemp }
            }
        }

        /**
         * If have time frame
         */
        var timeFrameStatus: Long = 0
        if (item.timeFrame.size > 0) {

            // Foreach technical detail
            for (date in item.timeFrame) {

                val values = ContentValues()
                values.put(TIME_FRAME_ITEM_ID,item.id)
                values.put(TIME_FRAME_YEAR,date)

                // Insertion
                val statusTemp = db.insertWithOnConflict(TIME_FRAME_TABLE, null, values, CONFLICT_IGNORE)

                // Keep the error if it's one
                if (statusTemp == -1L) { timeFrameStatus = statusTemp }
            }
        }

        // Close the stream with the db
        db.close()

        // Return if everything have been inserted correctly
        return itemStatus != -1L && categoriesStatus != -1L && pictureStatus != -1L && technicalDetailsStatus != -1L && timeFrameStatus != -1L
    }

    /**
     * Insert the demo in the DB
     */
    fun addDemo(id: String, date: String) {

        // Connect to the db
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(DEMOS_ITEM_ID,id)
        values.put(DEMOS_DATE,date)

        // Insertion
        val status = db.insertWithOnConflict(DEMOS_TABLE, null, values, CONFLICT_IGNORE)
    }

    /**
     * Returns a cursor of all the items of the database
     */
    private fun fetchAllItems(): Cursor {

        val db = this.readableDatabase

        val cursor = db.query(
            COLLECTION_TABLE, null,
            null, null, null, null, "${COLLECTION_YEAR.toString()} ASC", null
        )

        cursor?.moveToFirst()

        return cursor
    }

    /**
     * Returns a cursor of all the categories of the item
     */
    private fun fetchAllCategories(itemId: String): Cursor {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $CATEGORIES_TABLE WHERE $CATEGORIES_ITEM_ID = \"$itemId\"", null)

        cursor?.moveToFirst()

        return cursor
    }

    /**
     * Returns a cursor of all the pictures of the item
     */
    private fun fetchAllPictures(itemId: String): Cursor {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $PICTURES_TABLE WHERE $PICTURES_ITEM_ID = \"$itemId\"", null)

        cursor?.moveToFirst()

        return cursor
    }

    /**
     * Returns a cursor of all the technical details of the item
     */
    private fun fetchAllTechnicalDetails(itemId: String): Cursor {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TECHNICAL_DETAILS_TABLE WHERE $TECHNICAL_DETAILS_ITEM_ID = \"$itemId\"", null)

        cursor?.moveToFirst()

        return cursor
    }

    /**
     * Returns a cursor of all the time frame of the item
     */
    private fun fetchAllTimeFrame(itemId: String): Cursor {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TIME_FRAME_TABLE WHERE $TIME_FRAME_ITEM_ID = \"$itemId\"", null)

        cursor?.moveToFirst()

        return cursor
    }

    /**
     * Returns a cursor of all the demos dates of the item
     */
    private fun fetchAllDemosDates(itemId: String): Cursor {

        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $DEMOS_TABLE WHERE $DEMOS_ITEM_ID = \'${itemId}\'", null)

        cursor?.moveToFirst()

        return cursor
    }

    /**
     * Transform a cursor to an item
     */
    private fun cursorToItem(cursor: Cursor): Item {

        /**
         * Get the item from the DB
         */
        var item = Item()
        item.id = cursor.getString(cursor.getColumnIndex(COLLECTION_ID))
        item.name = cursor.getString(cursor.getColumnIndex(COLLECTION_NAME))
        item.description = cursor.getString(cursor.getColumnIndex(COLLECTION_DESCRIPTION))
        item.year = cursor.getShort(cursor.getColumnIndex(COLLECTION_YEAR))
        item.brand = cursor.getString(cursor.getColumnIndex(COLLECTION_BRAND))
        item.working = cursor.getInt(cursor.getColumnIndex(COLLECTION_WORKING)) == 1 // Like boolean

        /**
         * Get all the items from the DB
         */
        val cursorCategories: Cursor = fetchAllCategories(item.id)
        if (cursorCategories.moveToFirst()) {

            while (!cursorCategories.isAfterLast) {

                item.categories.add(cursorCategories.getString(cursorCategories.getColumnIndex(CATEGORIES_NAME)))
                cursorCategories.moveToNext()
            }
        }

        /**
         * Get all the pictures from the DB
         */
        val cursorPictures: Cursor = fetchAllPictures(item.id)
        if (cursorPictures.moveToFirst()) {

            while (!cursorPictures.isAfterLast) {

                val idItem = cursorPictures.getString(cursorPictures.getColumnIndex(PICTURES_ITEM_ID))
                val idPic = cursorPictures.getString(cursorPictures.getColumnIndex(PICTURES_ID))
                val desc = cursorPictures.getString(cursorPictures.getColumnIndex(PICTURES_DESCRIPTION))

                // Log.d("------", "$idPic = $desc \\ $idItem")

                item.pictures[idPic] = desc

                cursorPictures.moveToNext()
            }
        }

        /**
         * Get all the technical details from the DB
         */
        val cursorTechnicalDetails: Cursor = fetchAllTechnicalDetails(item.id)
        if (cursorTechnicalDetails.moveToFirst()) {

            while (!cursorTechnicalDetails.isAfterLast) {

                item.technicalDetails.add(cursorTechnicalDetails.getString(cursorTechnicalDetails.getColumnIndex(TECHNICAL_DETAILS_NAME)))

                cursorTechnicalDetails.moveToNext()
            }
        }

        /**
         * Get all the time frame from the DB
         */
        val cursorTimeFrame: Cursor = fetchAllTimeFrame(item.id)
        if (cursorTimeFrame.moveToFirst()) {

            while (!cursorTimeFrame.isAfterLast) {

                item.timeFrame.add(cursorTimeFrame.getShort(cursorTimeFrame.getColumnIndex(TIME_FRAME_YEAR)))

                cursorTimeFrame.moveToNext()
            }
        }

        /**
         * Fetch the demos from the DB
         */
        val cursorDemos: Cursor = fetchAllDemosDates(item.id)
        if (cursorDemos.moveToFirst()) {

            while (!cursorDemos.isAfterLast) {

                // Date as String
                val dateString: String =  cursorDemos.getString(1)

                // Date as Date
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                val date: Date = format.parse(dateString)

                // Insert
                item.demos.add(date)

                cursorDemos.moveToNext()
            }
        }

        return item
    }

    /**
     * Fetch the items and put them into the list
     */
    @Synchronized fun loadItems() {

        val res: ArrayList<Item> = ArrayList()

        // Get all the items
        val cursorItems: Cursor = fetchAllItems()

        if (cursorItems.moveToFirst()) {

            while (!cursorItems.isAfterLast) {

                // Get the item from the API
                val item: Item = this.cursorToItem(cursorItems)

                // Add it to the items
                res.add(item)

                // Move the cursor
                cursorItems.moveToNext()
            }
        }

        this.items = res
    }

    /**
     * Return an array list of Items
     */
    @Synchronized fun getItems(): MutableList<Item> {
        return this.items
    }

    /**
     * Return the item of the array
     */
    @Synchronized fun getItem(index: Int): Item {
        return this.items[index]
    }
}