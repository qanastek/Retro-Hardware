package com.example.retro_hardware.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.retro_hardware.R
import com.example.retro_hardware.models.Item

class ItemActivity : AppCompatActivity() {

    lateinit var item: Item

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

        // Status
        status.text = item.isWorking()
        status.setTextColor(item.isWorkingColor(this))

    }
}
