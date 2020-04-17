package com.example.retro_hardware.controllers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.retro_hardware.R
import com.example.retro_hardware.models.Collection
import com.example.retro_hardware.models.Item
import com.example.retro_hardware.models.Threads.FetchImage


class MainActivity : AppCompatActivity, SwipeRefreshLayout.OnRefreshListener {

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
            FetchImage(thumb, item!!.getUrlThumbnail(),this.context).execute()

            // Name
            val nameText = row.findViewById<TextView>(R.id.name)
            nameText.text = item.name

            // Age
            val brandText = row.findViewById<TextView>(R.id.brand)
            brandText.text = item.brand

            return row
        }

        override fun getItem(position: Int): Item {
            Log.d("MainActivity","getItem")
            return currentList[position]
        }

        override fun getItemId(position: Int): Long {
            Log.d("MainActivity","getItemId")
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
