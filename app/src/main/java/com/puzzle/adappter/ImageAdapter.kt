package com.puzzle.adappter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.puzzle.R

/**
 * [ImageSelectActivity] 中 [allImageRecyclerView] 和 [imageSelectedRecyclerView] 的Adapter
 * 用于显示带待选择图片的缩略图
 *
 * @param imageList     导入图片路径的列表
 *
 * @param isSelected    false  为allImageRecyclerView的Adapter
 *                      true 为imageSelectedRecyclerView的Adapter
 *
 * @param onSelected    图片点击事件
 *                      [adapter] :  当前的 adapter, 用于更新 RecyclerView，如 notifyItemRemoved
 *                      [position]:  当前点击的下标，使用 [ImageViewHolder.layoutPosition]
 */
class ImageAdapter(
    val imageList: List<String>,
    private val isSelected: Boolean = false,
    private val onSelected: OnImageSelectedListener
) : RecyclerView.Adapter<ImageViewHolder>(), View.OnClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val resId: Int = if (isSelected) {
                             R.layout.item_image_selected       // 选中图片
                         } else {
                             R.layout.item_image                // 待选择图片
                         }
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(resId, parent, false)
        val viewHolder = ImageViewHolder(itemView)
        viewHolder.imageView.setOnClickListener(this@ImageAdapter)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageResource(R.color.white)
        Glide.with(holder.imageView.context)
             .load("file://${imageList[position]}")
             .into(holder.imageView)
        holder.imageView.tag = holder       // 通过 tag，绑定点击
    }

    override fun getItemCount() = imageList.size

    override fun onClick(v: View) {
        val imageViewHolder = v.tag as ImageViewHolder
        onSelected(this, imageViewHolder.layoutPosition)
    }
}

/**
 * ImageAdapter 对应的 ViewHolder
 */
class ImageViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
    val imageView: ImageView = rootView.findViewById(R.id.itemImageView)
}

typealias OnImageSelectedListener = (adapter: ImageAdapter, position: Int) -> Unit