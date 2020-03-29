package com.example.retro_hardware.controllers

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.retro_hardware.R
import com.example.retro_hardware.models.User

class MainActivity : AppCompatActivity() {

    val users = arrayListOf<User>(
        User("Donald Trump",65),
        User("Barack Obama",58),
        User("Warren Buffet",72),
        User("Labrak Yanis",21),
        User("Labrak Chaima",12),
        User("Lewis Hamilton",32),
        User("Larry Ellison",55),
        User("Steve Jobs",49),
        User("Steve Bozniak",55),
        User("Emanuel Macron",47)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.UsersListView)

        listView.adapter = UsersAdapter(this, users)
    }

    private class UsersAdapter(context: Context, users: ArrayList<User>): BaseAdapter() {

        /**
         * Context
         */
        private val context: Context?

        /**
         * Users list
         */
        private val users: ArrayList<User>

        /**
         * Constructor
         */
        init {
            this.context = context
            this.users = users
        }

        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
//            val text1 = TextView(this.context)
//            text1.text = this.users[position].name
//            return  text1

            val inflater = LayoutInflater.from(this.context)

            // Row
            val row = inflater.inflate(R.layout.user_row, viewGroup, false)

            // Name
            val nameText = row.findViewById<TextView>(R.id.name)
            nameText.text = this.users[position].name

            // Age
            val ageText = row.findViewById<TextView>(R.id.age)
            val age = this.users[position].age.toString()
            ageText.text = "$age ans"

            return row
        }

        override fun getItem(position: Int): User {
            return this.users[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return this.users.size
        }

    }
}
