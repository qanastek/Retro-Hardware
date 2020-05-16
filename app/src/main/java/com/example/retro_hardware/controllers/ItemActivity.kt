package com.example.retro_hardware.controllers

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.retro_hardware.R
import com.example.retro_hardware.models.CalendarAdapter
import com.example.retro_hardware.models.ImageAdapter
import com.example.retro_hardware.models.Item
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlin.collections.ArrayList


class ItemActivity : AppCompatActivity() {

    // Current item
    lateinit var item: Item

    // List of images: URL | DESC
    lateinit var itemImages: ArrayList<Pair<String,String>>

    /**
     * Fields
     */
    lateinit var chipGroup: ChipGroup
    lateinit var timeFrame: ChipGroup
    lateinit var productName: TextView
    lateinit var brand: TextView
    lateinit var year: TextView
    lateinit var description: TextView
    lateinit var contentSpecs: TextView
    lateinit var status: TextView
    lateinit var image: ImageView
    lateinit var imagesList: RecyclerView
    lateinit var imagesLinearLayout: LinearLayout
    lateinit var calendarLayout: LinearLayout
    lateinit var calendarView: RecyclerView

    companion object {

        // Adapters
        lateinit var adapterImage: ImageAdapter
        lateinit var adapterCalendar: CalendarAdapter

        // The list of images
        lateinit var images: List<String>

        // Last image clicked
        lateinit var lastClickedImage: Pair<String,String>

        /**
         * Zoom
         */
        lateinit var dialog: Dialog
        lateinit var viewZoom: View
        lateinit var imageViewZoom: ImageView
        lateinit var descriptionZoom: TextView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        this.item = intent.getParcelableExtra("item")

        initFields()
        fillUpFields()
        initZoom()
    }

    /**
     * Initialize the activity
     */
    private fun initFields() {

        Log.d("initFields",item.demos.toString())

        this.chipGroup = findViewById(R.id.chipGroup)
        this.timeFrame = findViewById(R.id.timeFrame)
        this.productName = findViewById(R.id.productName)
        this.brand = findViewById(R.id.brand)
        this.year = findViewById(R.id.year)
        this.description = findViewById(R.id.description)
        this.contentSpecs = findViewById(R.id.contentSpecs)
        this.status = findViewById(R.id.status)
        this.imagesList = findViewById(R.id.imagesList)
        this.image = findViewById(R.id.image)
        this.imagesLinearLayout = findViewById(R.id.imagesLinearLayout)
        this.calendarLayout = findViewById(R.id.calendarLayout)
        this.calendarView = findViewById(R.id.calendarView)
    }

    /**
     * Share button
     */
    fun share(view: View) {

        val title = "Look at it's cool hardware \uD83D\uDDA5"
        val content = "It's a ${brand.text} ${productName.text}.️ \n ${lastClickedImage.second}"

        // Create the intent
        val shareIntent: Intent? = ShareCompat.IntentBuilder.from(this)
        .setType("text/plain")
        .setText(content + " \n \n \uD83D\uDDBC️ Look at this picture: " +  lastClickedImage.first)
        .setSubject(title)
        .intent

        startActivity(Intent.createChooser(shareIntent, "Send to"))
    }

    /**
     * Initialize the zoom modal
     */
    private fun initZoom() {

        // Basics
        viewZoom = layoutInflater.inflate(R.layout.zoom_image,null)
        dialog = Dialog(this, android.R.style.Theme_NoTitleBar_Fullscreen)
        dialog.setContentView(viewZoom)

        // Link fields
        imageViewZoom = viewZoom.findViewById(R.id.imageViewZoom)
        descriptionZoom = viewZoom.findViewById(R.id.descriptionZoom)
    }

    /**
     * Fill up the fields of the ItemActivity
     */
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
            this.year.text = "Not available"
        }

        // Load the image
        Glide.with(this).load(item!!.getUrlThumbnail()).placeholder(R.drawable.no_image).into(image)

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

        /**
         * Check if have timeFrame
         */
        if (this.item.timeFrame.size > 0) {

            // If yes, add them
            for (timeFrameItem in this.item.timeFrame) {

                val chip = Chip(timeFrame.context)
                chip.text= timeFrameItem.toString()

                chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.chipColor))
                chip.isClickable = false
                chip.isCheckable = false
                timeFrame.addView(chip)

            }

        } else {

            val chip = Chip(timeFrame.context)
            chip.text = "EMPTY"
            chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.chipColor))
            chip.isClickable = false
            chip.isCheckable = false
            timeFrame.addView(chip)
        }

        /**
         * Check if have technicalDetails
         */
        if (this.item.technicalDetails.size > 0) {

            var i = 1

            // If yes, add them
            for (detail in this.item.technicalDetails) {

                if (i % 2 == 0) {
                    contentSpecs.text = contentSpecs.text.toString() + "\n"
                }

                contentSpecs.text = contentSpecs.text.toString() + "• " + detail
                i++
            }

        } else {
            contentSpecs.text = "No technical details available"
        }

        // Fetch the images
        this.itemImages = this.item.getImagesUrl()

        // If have images
        if (itemImages.size > 0) {

            // Create the adpater
            adapterImage = ImageAdapter(this, itemImages)

            // If isn't empty
            imagesList.adapter = adapterImage
            imagesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

            // Update the view
            adapterImage.notifyDataSetChanged()

        } else {

            // Create a textView
            val noImages = TextView(this)

            // Configure it
            noImages.text = "No images available"
            noImages.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F)
            noImages.setTextColor(ContextCompat.getColor(this,R.color.textColorTertiary))

            // Add it
            imagesLinearLayout.addView(noImages)
        }

        // Status
        status.text = item.isWorking()
        status.setTextColor(item.isWorkingColor(this))

        /**
         * Check if have categories
         */
        if (this.item.demos.size > 0) {

            // Create the adpater
            adapterCalendar = CalendarAdapter(this, this.item)

            // If isn't empty
            calendarView.adapter = adapterCalendar
            calendarView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            // Update the view
            adapterCalendar.notifyDataSetChanged()

        } else {

            // Create a textView
            val noDate = TextView(this)

            // Configure it
            noDate.text = "No dates available"
            noDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F)
            noDate.setTextColor(ContextCompat.getColor(this,R.color.textColorTertiary))

            // Add it
            calendarLayout.addView(noDate)
        }

    }
}
