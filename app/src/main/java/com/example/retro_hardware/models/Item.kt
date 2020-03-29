package com.example.retro_hardware.models

class Item(
    private val id: String,
    val description: String,
    val brand: String,
    val working: Boolean,
    private val pictures: HashMap<String,String>,
    val technicalDetails: ArrayList<String>,
    val categories: ArrayList<String>,
    val timeFrame: ArrayList<Short>
) {

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
    fun getImages(): ArrayList<String> {

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
    private fun getUrlThumbnail(): String {
        return "${Api.BASE_URL}/items/${this.id}/thumbnail"
    }

    /**
     * Get a image url
     */
    private fun getUrlImage(imageId: String): String {
        return "${Api.BASE_URL}/items/${this.id}/images/$imageId"
    }
}