package com.puzzle.adappter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.puzzle.R

/**
 * ImageSelectActivity中allImageRecyclerView 和 imageSelectedRecyclerView 的Adapter
 * 用于显示图片的缩略图
 *
 * @param isSelected    false  为allImageRecyclerView的Adapter
 *                      true 为imageSelectedRecyclerView的Adapter
 */
class ImageAdapter(
    val imageList: List<String>,
    private val isSelected: Boolean = false,
    private val onSelected: (adapter: ImageAdapter, position: Int) -> Unit
) : RecyclerView.Adapter<ImageViewHolder>(), View.OnClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val resId: Int = if (isSelected) {
                             R.layout.item_image_selected
                         } else {
                             R.layout.item_image
                         }
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(resId, parent, false)
        val viewHolder = ImageViewHolder(itemView).apply {
            imageView.setOnClickListener(this@ImageAdapter)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageResource(R.color.white)
        Glide.with(holder.imageView.context)
             .load("file://${imageList[position]}")
             .into(holder.imageView)
        holder.imageView.tag = holder
    }

    override fun getItemCount() = imageList.size
    override fun onClick(v: View) {
        val imageViewHolder = v.tag as ImageViewHolder
        onSelected(this, imageViewHolder.layoutPosition)
    }
}

/**
 * ImageAdapter对应的ViewHolder
 */
class ImageViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
    val imageView: ImageView = itemView.findViewById(R.id.itemImageView)
}