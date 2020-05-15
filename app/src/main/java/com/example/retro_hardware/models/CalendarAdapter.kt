package com.example.retro_hardware.models

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.retro_hardware.R
import java.util.*

/**
     * The adapter for the horizontal recyclerView of images
     */
    public class CalendarAdapter(private val context: Context, private val item: Item): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val inflater = LayoutInflater.from(this.context)

            val calendarViewAdapter = inflater.inflate(R.layout.calendar_item, parent, false)

            return ViewHolder(calendarViewAdapter)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            // Get the date
            var dateCalendarItem: Date = item.demos[position]

            // Get the day in number
            holder.numberDate.text = DateFormat.format("dd", dateCalendarItem)

            // Get the month in letter
            holder.dayDate.text = DateFormat.format("MMM", dateCalendarItem)

            // Get the product name
            holder.labelPresentation.text = "Presentation of the $item.name"
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItemCount(): Int {
            return item.demos.size
        }

        class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

            var numberDate: TextView
            var dayDate: TextView
            var labelPresentation: TextView

            init {
                numberDate = view.findViewById(R.id.numberDate)
                dayDate = view.findViewById(R.id.dayDate)
                labelPresentation = view.findViewById(R.id.labelPresentation)
            }

        }
    }