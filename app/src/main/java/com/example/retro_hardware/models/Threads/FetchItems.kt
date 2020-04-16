package com.example.retro_hardware.models.Threads

import android.os.AsyncTask
import android.util.Log
import android.util.JsonReader
import android.widget.Toast
import com.example.retro_hardware.controllers.MainActivity
import com.example.retro_hardware.models.Collection
import com.example.retro_hardware.models.Item
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL


class FetchItems: AsyncTask<Void, Void, String> {

    /**
     * Array list of items
     */
    private val items: ArrayList<Item> = arrayListOf()

    constructor()

    // ArrayList of Item
//    var items: ArrayList<Item> = arrayListOf()

    companion object {
        val DONE: String = "DONE"
        val FAILED: String = "FAILED"
    }

    /**
     * Collect the data from the web and populate the collection
     */
    override fun doInBackground(vararg p0: Void?): String {

        // URL to get the collection
        val url: String = Collection.getUrlCollection()

        // Make a stream to the URL
        val inputStream = URL(url).openStream()

        // If empty return FAILED
        inputStream ?: return FAILED

        // Parse the JSON
        this.readJsonStream(inputStream)

        return DONE
    }

    /**
     * After downloading
     */
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        // Add all the items
        MainActivity.collection.addItems(items)

        // Toast
//        val toast = Toast.makeText(MainActivity.context, "Finish the download !", Toast.LENGTH_SHORT)
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

            // Add it to the collection
            this.items.add(item)
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