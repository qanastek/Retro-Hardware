package com.example.retro_hardware.controllers

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.retro_hardware.R
import com.example.retro_hardware.models.Collection
import com.example.retro_hardware.models.Item
import com.example.retro_hardware.models.MainAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.item_row.view.*


class MainActivity : AppCompatActivity, SwipeRefreshLayout.OnRefreshListener {

    /**
     * Main screen
     */
    lateinit var layInflat: LayoutInflater
    lateinit var viewFilter: View
    lateinit var rootLayout: LinearLayout
    lateinit var scrollFilter: ScrollView

    // The listView
    lateinit var listView: ListView

    lateinit var searching: SearchView

    companion object {

        /**
         * The collection
         */
        lateinit var collection: Collection

        /**
         * Adapter
         */
        var adapter: MainAdapter? = null

        /**
         * Filters dialog
         */
        lateinit var dialog: Dialog

        /**
         * Empty list image
         */
        lateinit var emptyList: ImageView

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
        lateinit var defaultOrder: RadioButton
        lateinit var alphabetical: RadioButton
        lateinit var both: RadioButton

        /**
         * Return all the selected categories in the filters modal
         */
        fun getAllTheSelectedCategories(): ArrayList<String> {

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

        /**
         * Return all the selected brands in the filters modal
         */
        fun getAllTheSelectedBrands(): ArrayList<String> {

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

        // Listen to the searchBar
        listenSearch()

        // Link the fields of the filters modal
        linkFiltersView()

        // Initialize the filters modal
        initializeFilters()

        // Create and set the adapter
        adapter = MainAdapter(this)
        listView.adapter = adapter

        // Update the adapter
        adapter?.notifyDataSetChanged()

        // On click on item
        listView.setOnItemClickListener { parent, view, position, id ->

            val item: Item? = adapter?.getItem(position) // The item that was clicked
            var intent = Intent(this, ItemActivity::class.java)

            val options = ActivityOptionsCompat
            .makeSceneTransitionAnimation(this, view.cardRow, "robot")

            intent.putExtra("item", item)
            startActivity(intent, options.toBundle())
        }
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

        /**
         * Filters modal
         */
        layInflat = LayoutInflater.from(applicationContext)
        viewFilter = layoutInflater.inflate(R.layout.filters,null)
        dialog = Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen)
        dialog.setContentView(viewFilter)

        rootLayout = viewFilter.findViewById(R.id.root)
        scrollFilter = viewFilter.findViewById(R.id.scrollFilter)

        // Image empty list
        emptyList = findViewById(R.id.emptyList)

        /**
         * Fields
         */
        yearStart = viewFilter.findViewById(R.id.yearStart)
        yearEnd = viewFilter.findViewById(R.id.yearEnd)
        order = viewFilter.findViewById(R.id.order)
        sortBy = viewFilter.findViewById(R.id.sortBy)
        orderBy = viewFilter.findViewById(R.id.orderBy)
        status = viewFilter.findViewById(R.id.status)
        categories = viewFilter.findViewById(R.id.categories)
        brands = viewFilter.findViewById(R.id.brands)
        defaultOrder = viewFilter.findViewById(R.id.defaultOrder)
        alphabetical = viewFilter.findViewById(R.id.alphabetical)
        both = viewFilter.findViewById(R.id.both)

        // Get the close button on the searchView
        val closeButtonId: Int = searching.context.resources.getIdentifier("android:id/search_close_btn", null, null)
        val closeButton = searching.findViewById(closeButtonId) as ImageView

        // On close collapse the searchView
        closeButton.setOnClickListener {
            searching.onActionViewCollapsed()
            true
        }
    }

    /**
     * Initialize the filters view
     */
    private fun initializeFilters() {

        // Clear all chips currently inside
        categories.removeAllViews()
        brands.removeAllViews()

        var extreme: Pair<Int,Int>  = collection.getExtremeYears()
        yearStart.setText(extreme.first.toString(), TextView.BufferType.EDITABLE)
        yearEnd.setText(extreme.second.toString(), TextView.BufferType.EDITABLE)

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
     * On click expand the searchBar
     */
    fun expandSearchBar(view: View) {
        searching.onActionViewExpanded()
    }

    /**
     * When the user trigger the filtering button
     */
    fun filters(view: View) {

        // Unfocus the elements
        clearFocuses()

        // Go to the top of the scroll view
        scrollFilter.scrollTo(0,0)

        // Display the dialog
        dialog.show()
    }

    /**
     * Close the filters modal
     */
    fun cancel(view: View) {

        /**
         * Clear fields
         */
        yearStart.text.clear()
        yearEnd.text.clear()
        order.isChecked = true
        sortBy.clearCheck()
        orderBy.clearCheck()
        status.clearCheck()
        categories.clearCheck()
        brands.clearCheck()

        /**
         * Set default
         */
        defaultOrder.isChecked = true
        alphabetical.isChecked = true
        both.isChecked = true

        var extreme: Pair<Int,Int>  = collection.getExtremeYears()
        yearStart.setText(extreme.first.toString(), TextView.BufferType.EDITABLE)
        yearEnd.setText(extreme.second.toString(), TextView.BufferType.EDITABLE)

        // Update the vue
        adapter?.filter?.filter(searching.query)

        dialog.dismiss()
    }

    /**
     * Clear the focus of the filters view
     */
    private fun clearFocuses() {
        yearStart.clearFocus()
        yearEnd.clearFocus()
    }

    /**
     * Apply the filters and close the modal
     */
    fun apply(view: View) {

        // Update the vue
        adapter?.filter?.filter(searching.query)

        dialog.dismiss()
    }

    override fun onRefresh() {
        Toast.makeText(this,"HERE", Toast.LENGTH_LONG)
    }
}
