package com.example.retro_hardware.models

import android.util.Log
import com.example.retro_hardware.models.Threads.FetchItems

class Collection {

    private constructor()

    /**
     * Array list of items
     */
    private val items: ArrayList<Item> = arrayListOf()

    /**
     * Static methods
     */
    companion object {

        // Instance of the singleton
        private val INSTANCE: Collection = Collection()

        // Get the singleton instance
        @Synchronized fun getInstance(): Collection {

            /**
             * If the collection isn't populated
             */
            if (INSTANCE.items.size <= 0) {
                FetchItems().execute()
            }

            return INSTANCE
        }

        /**
         * Get a collection url
         */
        fun getUrlCollection(): String {
            return "${Api.BASE_URL}/catalog"
        }
    }

    /**
     * Add an Item to the list of Items
     */
    fun addItem(item: Item) {
        getInstance().items.add(item)
    }

    /**
     * Add an Items to the list of Items
     */
    fun addItems(items: ArrayList<Item>) {
        getInstance().items.addAll(items)
    }

    /**
     * Return an array list of Items
     */
    @Synchronized fun getItems(): ArrayList<Item> {
        return getInstance().items
    }

    /**
     * Return the item of the array
     */
    @Synchronized fun getItem(index: Int): Item {
        return getInstance().items[index]
    }
}