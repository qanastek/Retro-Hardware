package com.example.retro_hardware.models.Threads

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import com.example.retro_hardware.models.Collection
import com.example.retro_hardware.models.Collection.Companion.isOnline
import com.example.retro_hardware.models.Item
import java.net.URL

class FetchImage: AsyncTask<ImageView, Void, Bitmap> {

    var imageView: ImageView? = null
    var url: String? = null
    lateinit var context: Context

    constructor(imageView: ImageView, url: String?, context: Context) {
        this.imageView = imageView
        this.url = url
        this.context = context
    }

    /**
     * After downloading
     */
    override fun onPostExecute(result: Bitmap?) {
        imageView!!.setImageBitmap(result)
    }

    override fun doInBackground(vararg params: ImageView?): Bitmap? {

        Log.d("FetchItems","doInBackground")

        if (Collection.isOnline(this.context)) {
            val url = URL(url)
            return BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } else {
            return null
        }
    }

}