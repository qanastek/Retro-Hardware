package com.example.retro_hardware.controllers

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
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
import java.net.URL


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

    public class UsersAdapter(context: Context): BaseAdapter() {

        /**
         * Context
         */
        private var context: Context = context

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

            val inflater = LayoutInflater.from(this.context)

            // Row
            val row = inflater.inflate(R.layout.item_row, viewGroup, false)

            val item: Item =  collection.getItem(position)

            // Thumbnail
            val thumb = row.findViewById<ImageView>(R.id.image)
            FetchImage(thumb, item).execute()

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
            return collection.getItem(position)
        }

        override fun getItemId(position: Int): Long {
            Log.d("MainActivity","getItemId")
            return position.toLong()
        }

        override fun getCount(): Int {
            return collection.getItems().size
        }

    }
}
