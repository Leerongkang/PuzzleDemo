package com.puzzle.adappter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lrk.puzzle.demo.R
import com.puzzle.coroutine.XXMainScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageAdapter(
    val imageList: List<String>,
    private val isSelected: Boolean = false,
    private val onSelected: (adapter: ImageAdapter, position: Int) -> Unit
) :
    RecyclerView.Adapter<ImageViewHolder>() {
    private val op = BitmapFactory.Options().apply {
        inSampleSize = 20
    }
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
        XXMainScope().launch {
            val bitmap = compressedBitmap(imageList[position])
            holder.imageView.setImageBitmap(bitmap)
        }
        holder.imageView.setOnClickListener {
            onSelected(this, position)
        }
    }

    private suspend fun compressedBitmap(path: String): Bitmap = withContext(Dispatchers.Default) {
        BitmapFactory.decodeFile(path, op)
    }

    override fun getItemCount() = imageList.size
}

class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView = itemView.findViewById<ImageView>(R.id.ItemImageView)
}