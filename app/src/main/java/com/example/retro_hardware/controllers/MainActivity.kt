package com.example.retro_hardware.controllers

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.retro_hardware.R
import com.example.retro_hardware.models.Collection
import com.example.retro_hardware.models.Item
import com.example.retro_hardware.models.User

class MainActivity : AppCompatActivity, SwipeRefreshLayout.OnRefreshListener {

    companion object {

        /**
         * The collection
         */
        var collection: Collection = Collection.getInstance()

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

        var listView: ListView = findViewById(R.id.UsersListView)

        adapter = UsersAdapter(this)
        listView.adapter = adapter

        // Update the view
        adapter?.notifyDataSetChanged()
    }

    override fun onRefresh() {
        Toast.makeText(this,"HERE", Toast.LENGTH_LONG)
    }

    public class UsersAdapter(context: Context): BaseAdapter() {

        /**
         * Context
         */
        private val context: Context?

        /**
         * Constructor
         */
        init {
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
//            val text1 = TextView(this.context)
//            text1.text = this.users[position].name
//            return  text1

            val inflater = LayoutInflater.from(this.context)

            // Row
            val row = inflater.inflate(R.layout.item_row, viewGroup, false)

            val item: Item =  collection.getItem(position)

            // Name
            val nameText = row.findViewById<TextView>(R.id.name)
            nameText.text = item.name

            // Age
            val brandText = row.findViewById<TextView>(R.id.brand)
            brandText.text = item.brand

            return row
        }

        override fun getItem(position: Int): Item {
            return collection.getItem(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return collection.getItems().size
        }

    }
}
