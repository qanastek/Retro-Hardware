package com.example.retro_hardware.models

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.retro_hardware.R
import com.example.retro_hardware.controllers.ItemActivity

/**
     * The adapter for the horizontal recyclerView of images
     */
    public class ImageAdapter(private val context: Context, private val images: ArrayList<Pair<String,String>>): RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val inflater = LayoutInflater.from(this.context)

            val imageView = inflater.inflate(R.layout.image_gallery_item, parent, false)

            return ViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            // Image url
            ItemActivity.lastClickedImage = images[position]

            // URL and Description
            val imageUrl: String = ItemActivity.lastClickedImage.first
            val imageDesc: String = ItemActivity.lastClickedImage.second

            // Get image
            Glide.with(context).load(imageUrl).centerCrop().placeholder(R.drawable.no_image).into(holder.image)

            /**
             * On click, zoomIn
             */
            holder.image.setOnClickListener() {

                // Load the image
                Glide.with(ItemActivity.viewZoom).load(imageUrl).into(ItemActivity.imageViewZoom)

                // Set description
                ItemActivity.descriptionZoom.text = imageDesc

                // Set animation
                (ItemActivity.dialog.window?.decorView as ViewGroup)
                .getChildAt(0).startAnimation(
                    AnimationUtils.loadAnimation(
                        context, android.R.anim.fade_in
                    )
                )

                // Display the modal
                ItemActivity.dialog.show()
            }
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
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