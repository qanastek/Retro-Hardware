package com.example.retro_hardware.controllers

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.retro_hardware.R
import com.example.retro_hardware.models.Item
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
    }

    private fun fillUpFields() {

        this.productName.text = item.name

        if (item.brand != null && !item.brand.isBlank()) {
            this.brand.text = item.brand
        } else {
            this.brand.text = "No brand"
        }

        if (item.description != null && !item.description.isBlank()) {
            this.description.text = item.description
        } else {
            this.description.text = "No description"
        }

        if (item.year != null && item.year.toInt() != 0) {
            this.year.text = item.year.toString()
        } else {
            this.year.text = "XXXX"
        }

        if (this.item != null && this.item.categories != null && this.item.categories.size > 0) {

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

        // Status
        status.text = item.isWorking()
        status.setTextColor(item.isWorkingColor(this))

    }
}
