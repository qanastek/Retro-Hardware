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
        var currentList: ArrayList<Item> = ArrayList(originalList)

        /**
         * Adjacency list
         */
        lateinit var adjacencyListPrintable: ArrayList<IPrintable>

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

            val inflater = LayoutInflater.from(this.context)

            val item: Item

            // Order by value
            var orderByResBtn = MainActivity.dialog.findViewById<RadioButton>(MainActivity.orderBy.checkedRadioButtonId)
            var orderByRes = orderByResBtn.text.toString().toUpperCase()

            // If order by is enabled
            if (orderByRes != "DEFAULT") {

                // If it's an header
                if (adjacencyListPrintable[position] is Header ) {

                    // Header
                    val headerRow = inflater.inflate(R.layout.section_header, viewGroup, false)

                    // Category
                    val headerName = headerRow.findViewById<TextView>(R.id.headerName)
                    headerName.text = (adjacencyListPrintable[position] as Header).category

                    return headerRow

                } else {

                    // Else get the item
                    item = adjacencyListPrintable[position] as Item
                }

            } else {

                item = currentList[position]
            }

            // Row
            val row = inflater.inflate(R.layout.item_row, viewGroup, false)

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

            if (brandText == null) {
                brandText?.visibility = View.INVISIBLE
            } else {
                brandText?.text = item.brand
            }

            return row
        }

        /**
         * Make a adjency list
         */
        private fun makeAdjacencyList(currentList: ArrayList<Item>): ArrayList<IPrintable> {

            // The adjacency list
            var adjacency: HashMap<String, ArrayList<Item>> = HashMap()

            // The result
            var res: ArrayList<IPrintable> = ArrayList()

            // Order by value
            var orderByResBtn = MainActivity.dialog.findViewById<RadioButton>(MainActivity.orderBy.checkedRadioButtonId)
            var orderByRes = orderByResBtn.text.toString().toUpperCase()

            // Other category/time frame
            val OTHERS = "OTHERS"

            if (orderByRes == "CATEGORIES") {

                // Sort by category asc
                currentList.sortBy { it.categories[0] }

                // Create all categories and add the item to them
                currentList.forEach {

                    var category = it.categories[0]

                    // Add the "Others" section
                    if (category.isNullOrEmpty() && !adjacency.containsKey(OTHERS)) {
                        adjacency[OTHERS] = ArrayList()
                    }

                    // If exist
                    if (category.isNullOrEmpty()) {
                        adjacency[OTHERS]?.add(it)
                    }
                    else if (adjacency.containsKey(category)) {
                        adjacency[category]?.add(it)
                    } else {
                        adjacency[category] = ArrayList()
                        adjacency[category]?.add(it)
                    }
                }
            }
            else if(orderByRes == "CHRONOLOGY") {

                // Sort by time frame asc
                currentList.sortBy { it.timeFrame[0] }

                // Create all time frames and add the item to them
                currentList.forEach {

                    var timeFrameKey = it.timeFrame[0].toString()

                    // Add the "Others" section
                    if (timeFrameKey.isNullOrEmpty() && !adjacency.containsKey(OTHERS)) {
                        adjacency[OTHERS] = ArrayList()
                    }

                    // If exist
                    if (timeFrameKey.isNullOrEmpty()) {
                        adjacency[OTHERS]?.add(it)
                    }
                    if (adjacency.containsKey(timeFrameKey)) {
                        adjacency[timeFrameKey]?.add(it)
                    } else {
                        adjacency[timeFrameKey] = ArrayList()
                        adjacency[timeFrameKey]?.add(it)
                    }
                }
            }

            // For each categories or time frame make the header and add the items
            adjacency.keys.forEach { key ->

                // Add the header
                res.add(Header(key))

                // Add the elements
                adjacency[key]?.forEach {  item ->
                    res.add(item)
                }
            }

            return res
        }

        override fun getItem(position: Int): Item {

            // Order by value
            var orderByResBtn = MainActivity.dialog.findViewById<RadioButton>(MainActivity.orderBy.checkedRadioButtonId)
            var orderByRes = orderByResBtn.text.toString().toUpperCase()

            // If order by is enabled
            if (orderByRes != "DEFAULT") {
                return adjacencyListPrintable[position] as Item
            }

            // Else
            return currentList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        // Get the number of elements
        override fun getCount(): Int {

            // Order by value
            var orderByResBtn = MainActivity.dialog.findViewById<RadioButton>(MainActivity.orderBy.checkedRadioButtonId)
            var orderByRes = orderByResBtn.text.toString().toUpperCase()

            // If order by is enabled
            if (orderByRes != "DEFAULT") {
                return adjacencyListPrintable.size
            }

            // Else
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
                    val statusRes = MainActivity.dialog.findViewById<RadioButton>(MainActivity.status.checkedRadioButtonId)

                    /**
                     * Status
                     */
                    when(statusRes.text.toString()) {

                        "WORKING" -> {
                            filteredList = filteredList.filter { it.working?.equals(true) } as ArrayList<Item>
                        }
                        "BROKEN" -> {
                            filteredList = filteredList.filter { it.working?.equals(false) } as ArrayList<Item>
                        }
                    }

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
                        filteredList = filteredList.filter { it.year.toInt() == 0 || it.year >=  yearStartCompare.toShort()} as ArrayList<Item>
                    }

                    /**
                     * Ending year
                     */
                    Log.d("apply",MainActivity.yearEnd.text.toString())
                    if (MainActivity.yearEnd.text.toString().isNotBlank()) {

                        var yearEndCompare: String = MainActivity.yearEnd.text.toString()
                        filteredList = filteredList.filter { it.year.toInt() == 0 || it.year <=  yearEndCompare.toShort()} as ArrayList<Item>
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

                    // Order by value
                    var orderByResBtn = MainActivity.dialog.findViewById<RadioButton>(MainActivity.orderBy.checkedRadioButtonId)
                    var orderByRes = orderByResBtn.text.toString().toUpperCase()

                    // If order by is enabled
                    if (orderByRes != "DEFAULT") {

                        // Make the adjacency list as an array
                        adjacencyListPrintable = makeAdjacencyList(filteredList)

                        // Set it as result
                        res.values = adjacencyListPrintable

                    } else {

                        // Normal filtered result
                        res.values = filteredList
                    }

                    // Return it
                    return res
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                    if (results != null) {

                        // Order by value
                        var orderByResBtn = MainActivity.dialog.findViewById<RadioButton>(MainActivity.orderBy.checkedRadioButtonId)
                        var orderByRes = orderByResBtn.text.toString().toUpperCase()

                        // If order by is disabled
                        if (orderByRes == "DEFAULT") {

                            currentList.clear()
                            currentList.addAll(results.values as kotlin.collections.Collection<Item>)
                        }

                        notifyDataSetChanged()
                    }
                }
            }
        }
    }