package com.example.retro_hardware.models.Threads

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.util.Log
import android.util.JsonReader
import com.example.retro_hardware.controllers.MainActivity
import com.example.retro_hardware.models.Collection
import com.example.retro_hardware.models.Item
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class FetchItems: AsyncTask<Void, Void, String> {

    constructor()

    companion object {

        val DONE: String = "DONE"
        val FAILED: String = "FAILED"
    }

    /**
     * Collect the data from the web and populate the collection
     */
    override fun doInBackground(vararg p0: Void?): String {

        // URL to get the collection
        val urlCollection: String = Collection.getUrlCollection()

        // Make a stream to the collection URL
        val inputStreamCollection = URL(urlCollection).openStream()

        // URL to get the demos
        val urlDemos: String = Collection.getUrlDemos()

        // Make a stream to the demo URL
        val inputStreamDemos = URL(urlDemos).openStream()

        // If one of them is empty return FAILED
        inputStreamCollection ?: return FAILED
        inputStreamDemos ?: return FAILED

        // Parse the JSON of collections
        this.readJsonStream(inputStreamCollection)

        // Read the JSON stream of demos
        this.readJsonStreamDemos(inputStreamDemos)

        return DONE
    }

    /**
     * After downloading
     */
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        // Load the items from the DB
        MainActivity.collection.loadItems()

        MainActivity.adapter?.notifyDataSetChanged()

        // Toast
//        val toast = Toast.makeText(MainActivity.context, "Download finished !", Toast.LENGTH_SHORT)
//        toast.show()
    }

    /**
     * Parse the stream as JSON
     */
    private fun readJsonStream(response: InputStream) {

        Log.d("FetchItems","fetchItems")

        val reader = JsonReader(InputStreamReader(response, "UTF-8"))

        try {
            readItems(reader)
        } finally {
            reader.close()
        }
    }

    /**
     * Parse the JSON stream of demos
     */
    private fun readJsonStreamDemos(response: InputStream) {

        Log.d("FetchDemos","FetchDemos")

        val readerDemos = JsonReader(InputStreamReader(response, "UTF-8"))

        try {

            // Start the object
            readerDemos.beginObject()

            // For each items
            while (readerDemos.hasNext()) {

                // Identifier of the item
                var id = readerDemos.nextName()
                var date = readerDemos.nextString()

                // Write in the DB
                MainActivity.collection.addDemo(id,date)
            }

            readerDemos.endObject()

        } catch (e: Exception) {
            error(e)
        }
        finally {
            readerDemos.close()
        }
    }

    /**
     * Parse the JSON content
     */
    private fun readItems(reader: JsonReader) {

        // Start the object
        reader.beginObject()

        // For each items
        while (reader.hasNext()) {

            // New item
            val item = Item()

            // Identifier of the item
            val id = reader.nextName()

            // If not null
            if (id != null) {

                // Set the id
                item.id = id
            }

            readObjectItems(reader, item)

            // Write in the DB
            MainActivity.collection.addItem(item)
        }

        reader.endObject()
    }

    /**
     * Parse all the items of the array
     */
    private fun readObjectItems(reader: JsonReader, item: Item) {

        reader.beginObject()

        while (reader.hasNext()) {

            val name = reader.nextName()

            if (name == "brand") {
                item.brand = reader.nextString()
            } else if (name == "year") {
                item.year = reader.nextInt().toShort()
            } else if (name == "description") {
                item.description = reader.nextString()
            }  else if (name == "name") {
                item.name = reader.nextString()
            }  else if (name == "working") {
                item.working = reader.nextBoolean()
            } else if (name == "categories") {

                reader.beginArray()

                // For each category
                while (reader.hasNext()) {
                    item.categories.add(reader.nextString())
                }

                reader.endArray()

            } else if (name == "technicalDetails") {

                reader.beginArray()

                // For each technical detail
                while (reader.hasNext()) {
                    item.technicalDetails.add(reader.nextString())
                }

                reader.endArray()

            } else if (name == "timeFrame") {

                reader.beginArray()

                // For each time frame
                while (reader.hasNext()) {
                    item.timeFrame.add(reader.nextInt().toShort())
                }

                reader.endArray()

            } else if (name == "pictures") {

                reader.beginObject()

                // For each time frame
                while (reader.hasNext()) {

                    // Identifier of the image
                    val idImg: String = reader.nextName()

                    // Description of the image
                    val descriptionImg: String = reader.nextString()

                    // Insert into the HashMap
                    item.pictures[idImg] = descriptionImg
                }

                reader.endObject()

            } else {
                reader.skipValue()
            }
        }

        reader.endObject()
    }
}