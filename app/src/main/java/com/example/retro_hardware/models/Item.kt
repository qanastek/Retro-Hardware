package com.example.retro_hardware.models

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.example.retro_hardware.R
import com.example.retro_hardware.controllers.MainActivity
import kotlinx.android.parcel.Parcelize

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
    var timeFrame: ArrayList<Short>
): Parcelable {

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
        timeFrame = arrayListOf<Short>()
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
     */
    fun getImagesUrl(): ArrayList<String> {

        // The output list
        val res: ArrayList<String> = arrayListOf()

        // For each pictures in teh HashMap
        for (img in pictures) {
            res.add(this.getUrlImage(img.key))
        }

        return res
    }

    /**
     * Get the thumbnail url
     */
    fun getUrlThumbnail(): String {
        return "${Api.BASE_URL}/items/${this.id}/thumbnail"
    }

    fun isWorking(): String {

        if (this.working) {
            return "WORKING"
        }

        return "BROKEN"
    }
    fun isWorkingColor(context: Context): Int {

        if (this.working) {
            return ContextCompat.getColor(context, R.color.success)
        }

        return ContextCompat.getColor(context, R.color.danger)
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