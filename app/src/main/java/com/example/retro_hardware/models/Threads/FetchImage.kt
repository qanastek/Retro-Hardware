package com.example.retro_hardware.models.Threads

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import com.example.retro_hardware.models.Item
import java.net.URL


class FetchImage: AsyncTask<ImageView, Void, Bitmap> {

    var imageView: ImageView? = null
    var item: Item? = null

    /**
     * Array list of items
     */
    private val items: ArrayList<Item> = arrayListOf()

    constructor(imageView: ImageView, item: Item) {
        this.imageView = imageView
        this.item = item
    }

    /**
     * After downloading
     */
    override fun onPostExecute(result: Bitmap?) {
        imageView!!.setImageBitmap(result)
    }

    override fun doInBackground(vararg params: ImageView?): Bitmap {

        Log.d("FetchItems","doInBackground")

        val url = URL(item!!.getUrlThumbnail())
        return BitmapFactory.decodeStream(url.openConnection().getInputStream())
    }

}