package com.example.retro_hardware.models

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.retro_hardware.R
import com.example.retro_hardware.controllers.MainActivity

public class UsersAdapter(context: Context): BaseAdapter(), Filterable {

        /**
         * Context
         */
        private var context: Context = context

        /**
         * The original list
         */
        var originalList = MainActivity.collection.items

        /**
         * The current list
         */
        var currentList = ArrayList(originalList)

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

            val inflater = LayoutInflater.from(this.context)

            // Row
            val row = inflater.inflate(R.layout.item_row, viewGroup, false)

            val item: Item = currentList[position]

            // Thumbnail
            val thumb = row.findViewById<ImageView>(R.id.image)

            /**
             * Load the image, reduce the resolution and put it in the cache
             */
            Glide.with(this.context).load(item!!.getUrlThumbnail()).centerCrop().placeholder(R.drawable.no_image).into(thumb)

            // Name
            val nameText = row.findViewById<TextView>(R.id.name)
            nameText.text = item.name

            // Age
            val brandText = row.findViewById<TextView>(R.id.brand)
            brandText.text = item.brand

            return row
        }

        override fun getItem(position: Int): Item {
            return currentList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return currentList.size
        }

        override fun getFilter(): Filter {

            return object : Filter() {

                override fun performFiltering(constraint: CharSequence?): FilterResults {

                    // Filtered list
                    var filteredList : ArrayList<Item> = originalList

                    /**
                     * Fetch the results
                     */
                    var orderText: String =  if(MainActivity.order.isChecked) MainActivity.order.textOn.toString() else MainActivity.order.textOff.toString()

                    val sortRes = MainActivity.dialog.findViewById<RadioButton>(MainActivity.sortBy.checkedRadioButtonId)
                    val orderByRes = MainActivity.dialog.findViewById<RadioButton>(MainActivity.orderBy.checkedRadioButtonId)
                    val statusRes = MainActivity.dialog.findViewById<RadioButton>(MainActivity.status.checkedRadioButtonId)

                    /**
                     * SortBy
                     */
                    when(sortRes.text.toString()) {

                        "chronological" -> {

                            // Depending of the sort type
                            when(orderText) {

                                "ASC" -> {
                                    filteredList.sortBy { it.year }
                                }
                                "DESC" -> {
                                    filteredList.sortByDescending { it.year }
                                }
                            }
                        }
                        "alphabetical" -> {

                            // Depending of the sort type
                            when(orderText) {

                                "ASC" -> {
                                    filteredList.sortBy { it.name }
                                }
                                "DESC" -> {
                                    filteredList.sortByDescending { it.name }
                                }
                            }
                        }
                    }

                    // If empty take min/max dates
                    // Starting year
                    Log.d("apply",MainActivity.yearStart.text.toString())
                    if (MainActivity.yearStart.text.toString().isNotBlank()) {

                        var yearStartCompare: String = MainActivity.yearStart.text.toString()
                        filteredList = filteredList.filter { it.year >=  yearStartCompare.toShort()} as ArrayList<Item>
                    }

                    /**
                     * Ending year
                     */
                    Log.d("apply",MainActivity.yearEnd.text.toString())
                    if (MainActivity.yearEnd.text.toString().isNotBlank()) {

                        var yearEndCompare: String = MainActivity.yearEnd.text.toString()
                        filteredList = filteredList.filter { it.year <=  yearEndCompare.toShort()} as ArrayList<Item>
                    }

                    /**
                     * If empty take all brands
                     */
                    var selectedBrands: ArrayList<String> = MainActivity.getAllTheSelectedBrands()
                    if (selectedBrands.size > 0) {

                        filteredList = filteredList
                        .filter { it -> selectedBrands
                        .map { ma -> ma.toUpperCase() }
                        .contains(it.brand.toUpperCase()) } as ArrayList<Item>
                    }

                    /**
                     * If empty take all categories
                     */
                    var selectedCategories: ArrayList<String> = MainActivity.getAllTheSelectedCategories()
                    if (selectedCategories.size > 0) {

                        filteredList = filteredList
                        .filter { it -> selectedCategories
                        .map { ma -> ma.toUpperCase() }
                        .containsAll(it.categories
                        .map { mp -> mp.toUpperCase() }) } as ArrayList<Item>
                    }

                    /**
                     * Sort according to the searchBar input
                     */
                    if (constraint != null) {

                        // The typed text
                        var currentText = constraint.toString().toLowerCase()

                        // Sort by name and brand
                        filteredList = filteredList
                        .filter { it.name.toLowerCase()
                        .contains(currentText.toLowerCase()) || it.brand.toLowerCase()
                        .contains(currentText.toLowerCase()) } as ArrayList<Item>

                    } else {
                        filteredList = originalList
                    }

                    // Convert to filterResults
                    var res: FilterResults = FilterResults()
                    res.values = filteredList

                    // Return it
                    return res
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                    if (results != null) {

                        currentList.clear()
                        currentList.addAll(results.values as kotlin.collections.Collection<Item>)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }