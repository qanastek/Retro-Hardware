package com.example.retro_hardware.models

import android.content.Context
import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.example.retro_hardware.R
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Parcelize
class Item(
    var id: String,
    var name: String,
    var description: String,
    var year: Short,
    var brand: String,
    var working: Boolean,
    var pictures: HashMap<String,String>,
    var technicalDetails: ArrayList<String>,
    var categories: ArrayList<String>,
    var timeFrame: ArrayList<Short>,
    var demos: ArrayList<Date>
): Parcelable, IPrintable {

    /**
     * Empty Constructor
     */
    constructor(): this(
        id = "",
        name = "",
        description = "",
        year = 0,
        brand = "",
        working = false,
        pictures = hashMapOf<String,String>(),
        technicalDetails = arrayListOf<String>(),
        categories = arrayListOf<String>(),
        timeFrame = arrayListOf<Short>(),
        demos = arrayListOf<Date>()
    )

    /**
     * Static methods
     */
    companion object {

        // Return the object URL
        fun fetchItemUrl(itemId: String): String {
            return "${Api.BASE_URL}/items/$itemId"
        }

        // Return the object
        fun fetchItem(itemId: String): Item? {
            return null
        }
    }

    /**
     * Return a list of links
     * URL | DESC
     */
    fun getImagesUrl(): ArrayList<Pair<String,String>> {

        // The output list:  URL | DESC
        val res: ArrayList<Pair<String,String>> = arrayListOf()

        // For each pictures in teh HashMap
        for (img in pictures) {

            // URL
            val url = this.getUrlImage(img.key)

            // Description
            res.add(Pair<String,String>(url,img.value))
        }
        return res
    }

    /**
     * Get the thumbnail url
     */
    fun getUrlThumbnail(): String {
        return "${Api.BASE_URL}/items/${this.id}/thumbnail"
    }

    /**
     * Return the working status
     */
    fun isWorking(): String {
        return if(this.working) "WORKING" else "BROKEN"
    }

    /**
     * Return the color of the working status
     */
    fun isWorkingColor(context: Context): Int {
        return ContextCompat.getColor(context, if(this.working) R.color.success else R.color.danger)
    }

    /**
     * Get a image url
     */
    fun getUrlImage(imageId: String): String {
        return "${Api.BASE_URL}/items/${this.id}/images/$imageId"
    }

    override fun toString(): String {
        return "[${this.id}, ${this.name}]"
    }
}