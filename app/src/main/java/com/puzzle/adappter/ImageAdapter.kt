package com.puzzle.adappter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.util.lruCache
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.puzzle.R
import com.puzzle.coroutine.XXMainScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.roundToInt

class ImageAdapter(
    val imageList: List<String>,
    private val isSelected: Boolean = false,
    private val onSelected: (adapter: ImageAdapter, position: Int) -> Unit
) :
    RecyclerView.Adapter<ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val resId = if (isSelected) {
            R.layout.item_image_selected
        } else {
            R.layout.item_image
        }
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(resId, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageResource(R.color.white)
        Glide.with(holder.imageView.context)
             .load("file://${imageList[position]}")
             .into(holder.imageView)
        holder.imageView.setOnClickListener {
            onSelected(this, holder.layoutPosition)
        }
    }

    override fun getItemCount() = imageList.size
}

class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView = itemView.findViewById<ImageView>(R.id.ItemImageView)
}