package com.example.retro_hardware.controllers

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.retro_hardware.R
import com.example.retro_hardware.models.Collection
import com.example.retro_hardware.models.Item
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class MainActivity : AppCompatActivity, SwipeRefreshLayout.OnRefreshListener {

    /**
     * Main screen
     */
    lateinit var dialog: Dialog
    lateinit var layInflat: LayoutInflater
    lateinit var viewFilter: View
    lateinit var rootLayout: LinearLayout

    lateinit var listView: ListView
    lateinit var searching: SearchView

    /**
     * Filtering window
     */
    lateinit var yearStart: EditText
    lateinit var yearEnd: EditText
    lateinit var order: ToggleButton
    lateinit var sortBy: RadioGroup
    lateinit var orderBy: RadioGroup
    lateinit var status: RadioGroup
    lateinit var categories: ChipGroup
    lateinit var brands: ChipGroup

    companion object {

        /**
         * The collection
         */
        lateinit var collection: Collection

        /**
         * Adapter
         */
        var adapter: UsersAdapter? = null
    }

    constructor() {
//        MainActivity.context = context
        Log.d("MainActivity", "constructor")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Fetch the database
        collection = Collection(this)

        // Initialize fields
        listView = findViewById(R.id.UsersListView)
        searching = findViewById(R.id.searching)

        listenSearch()

        // Create the adapter
        adapter = UsersAdapter(this)
        listView.adapter = adapter

        // Update the adapter
        adapter?.notifyDataSetChanged()

        listView.setOnItemClickListener { parent, view, position, id ->

            val item: Item? = adapter?.getItem(position) // The item that was clicked
            var intent = Intent(this, ItemActivity::class.java)
            intent.putExtra("item", item)
            startActivity(intent)
        }

        // Link the fields of the filters modal
        linkFiltersView()

        // Initialize the filters modal
        initializeFilters()
    }

    /**
     * Listen for the searchBar
     */
    private fun listenSearch() {

        // Searching
        searching.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter?.filter?.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.filter?.filter(newText)
                return false
            }

        })
    }

    /**
     * Link the controller with the filters view
     */
    private fun linkFiltersView() {

        layInflat = LayoutInflater.from(applicationContext)
        viewFilter = layoutInflater.inflate(R.layout.filters,null)
        dialog = Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen)
        dialog.setContentView(viewFilter)

        rootLayout = viewFilter.findViewById(R.id.root)

        yearStart = viewFilter.findViewById(R.id.yearStart)
        yearEnd = viewFilter.findViewById(R.id.yearEnd)
        order = viewFilter.findViewById(R.id.order)
        sortBy = viewFilter.findViewById(R.id.sortBy)
        orderBy = viewFilter.findViewById(R.id.orderBy)
        status = viewFilter.findViewById(R.id.status)
        categories = viewFilter.findViewById(R.id.categories)
        brands = viewFilter.findViewById(R.id.brands)
    }

    /**
     * Initialize th efilters view
     */
    private fun initializeFilters() {

        // Clear all chips currently inside
        categories.removeAllViews()
        brands.removeAllViews()

        // Load all the categories into the filter modal
        for (category in collection.getCategories()) {

            val chip = Chip(categories.context, null, R.style.ChipFilter)
            chip.text = category.toUpperCase()
            chip.isClickable = true
            chip.isCheckable = true
            chip.isAllCaps = true
            chip.setTextColor(Color.WHITE)
            chip.setChipBackgroundColorResource(R.color.chip_selector)
            categories.addView(chip)
        }
        // Load all the brands into the filter modal
        for (brand in collection.getBrands()) {

            val chip = Chip(brands.context, null, R.style.ChipFilter)
            chip.text = brand.toUpperCase()
            chip.isClickable = true
            chip.isCheckable = true
            chip.isAllCaps = true
            chip.setTextColor(Color.WHITE)
            chip.setChipBackgroundColorResource(R.color.chip_selector)
            brands.addView(chip)
        }
    }

    /**
     * When the user trigger the filtering button
     */
    fun filters(view: View) {
        dialog.show()
    }

    /**
     * Close the filters modal
     */
    fun cancel(view: View) {
        dialog.dismiss()
    }

    /**
     * Apply the filters and close the modal
     */
    fun apply(view: View) {

        /**
         * Fetch the results
         */
        var orderText: String =  if(order.isChecked) order.textOn.toString() else order.textOff.toString()

        val sortRes = dialog.findViewById<RadioButton>(sortBy.checkedRadioButtonId)
        val orderByRes = dialog.findViewById<RadioButton>(orderBy.checkedRadioButtonId)
        val statusRes = dialog.findViewById<RadioButton>(status.checkedRadioButtonId)

        // Debug
        Log.d("apply", "----------------")

        Log.d("apply", orderText)
        Log.d("apply",sortRes.text.toString())
        Log.d("apply",orderByRes.text.toString())
        Log.d("apply",statusRes.text.toString())
        Log.d("apply",yearStart.text.toString())
        Log.d("apply",yearEnd.text.toString())

        var selectedBrands: ArrayList<String> = getAllTheSelectedBrands()
        Log.d("apply", selectedBrands.toString())

        var selectedCategories: ArrayList<String> = getAllTheSelectedCategories()
        Log.d("apply", selectedCategories.toString())

        Log.d("apply", "----------------")

        dialog.dismiss()
    }

    /**
     * Return all the selected brands in the filters modal
     */
    private fun getAllTheSelectedBrands(): ArrayList<String> {

        var res: ArrayList<String> = ArrayList()

        // Get all the selected brands
        for (i in 0 until brands.childCount) {

            // Get the current chi^p
            var currentChip: Chip = brands.getChildAt(i) as Chip

            // If is selected
            if (currentChip.isChecked){

                // Get it
                res.add(currentChip.text.toString())
            }
        }

        return res
    }

    /**
     * Return all the selected categories in the filters modal
     */
    private fun getAllTheSelectedCategories(): ArrayList<String> {

        var res: ArrayList<String> = ArrayList()

        // Get all the selected brands
        for (i in 0 until categories.childCount) {

            // Get the current chi^p
            var currentChip: Chip = categories.getChildAt(i) as Chip

            // If is selected
            if (currentChip.isChecked){

                // Get it
                res.add(currentChip.text.toString())
            }
        }

        return res
    }

    override fun onRefresh() {
        Toast.makeText(this,"HERE", Toast.LENGTH_LONG)
    }

    public class UsersAdapter(context: Context): BaseAdapter(), Filterable {

        /**
         * Context
         */
        private var context: Context = context

        /**
         * The original list
         */
        var originalList = collection.getItems()

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
                    var filteredList : List<Item> = ArrayList()

                    // If not empty
                    if (constraint != null) {

                        // The typed text
                        var currentText = constraint.toString().toLowerCase()

                        // For each stored elements
                        for (item in originalList) {

                            // If the item name or brand contains the sequence then keep it
                            if (item.name.toLowerCase().contains(currentText) || (item.brand.toLowerCase().contains(currentText))) {
                                filteredList += item
                            }
                        }
                    } else {
                        filteredList += originalList
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
}
