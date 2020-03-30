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
            if (INSTANCE != null && INSTANCE.items.size <= 0) {

                // TODO : Make the thread here
                FetchItems().execute()
            }

            return INSTANCE
        }

        /**
         * Add an Item to the list of Items
         */
        fun addItem(item: Item) {
            getInstance().items.add(item)
        }

        /**
         * Return an array list of Items
         */
        fun getItems(): ArrayList<Item> {
            return getInstance().items
        }

        /**
         * Get a collection url
         */
        fun getUrlCollection(): String {
            return "${Api.BASE_URL}/catalog"
        }
    }
}