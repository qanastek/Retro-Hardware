package com.example.retro_hardware.controllers

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retro_hardware.R
import com.example.retro_hardware.models.Item
import com.example.retro_hardware.models.Threads.FetchImage
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class ItemActivity : AppCompatActivity() {

    lateinit var item: Item

    lateinit var chipGroup: ChipGroup
    lateinit var productName: TextView
    lateinit var brand: TextView
    lateinit var year: TextView
    lateinit var description: TextView
    lateinit var status: TextView
    lateinit var imagesList: RecyclerView

    companion object {

        // Adapter
        lateinit var adapter: ImageAdapter

        // The list of images
        lateinit var images: List<String>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        this.item = intent.getParcelableExtra("item")

        initFields()
        fillUpFields()
    }

    private fun initFields() {
        this.chipGroup = findViewById(R.id.chipGroup)
        this.productName = findViewById(R.id.productName)
        this.brand = findViewById(R.id.brand)
        this.year = findViewById(R.id.year)
        this.description = findViewById(R.id.description)
        this.status = findViewById(R.id.status)
        this.imagesList = findViewById(R.id.imagesList)
    }

    private fun fillUpFields() {

        this.productName.text = item.name

        if (!item.brand.isBlank()) {
            this.brand.text = item.brand
        } else {
            this.brand.text = "No brand"
        }

        if (!item.description.isBlank()) {
            this.description.text = item.description
        } else {
            this.description.text = "No description"
        }

        if (item.year.toInt() != 0) {
            this.year.text = item.year.toString()
        } else {
            this.year.text = "XXXX"
        }

        /**
         * Check if have categories
         */
        if (this.item.categories.size > 0) {

            // If yes, add them
            for (category in this.item.categories) {

                val chip = Chip(chipGroup.context)
                chip.text= category.toUpperCase()

                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.chipColor))
                chip.isClickable = false
                chip.isCheckable = false
                chipGroup.addView(chip)

            }
        } else {

            val chip = Chip(chipGroup.context)
            chip.text= "EMPTY"
            chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.chipColor))
            chip.isClickable = false
            chip.isCheckable = false
            chipGroup.addView(chip)
        }

        // Images
        adapter = ImageAdapter(this, this.item.getImagesUrl())

        // If isn't empty
        imagesList.adapter = adapter
        imagesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Update the view
        adapter.notifyDataSetChanged()

        // Status
        status.text = item.isWorking()
        status.setTextColor(item.isWorkingColor(this))

    }

    public class ImageAdapter(private val context: Context, private val images: List<String>): RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val inflater = LayoutInflater.from(this.context)

            val imageView = inflater.inflate(R.layout.image_gallery_item, parent, false)

            return ViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            // Image url
            val imageUrl: String = images[position]

            // Get image
            FetchImage(holder.image, imageUrl).execute()
        }

        override fun getItemCount(): Int {
            return images.size
        }

        class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

            var image: ImageView

            init {
                image = view.findViewById(R.id.image)
            }

        }

    }
}
