package com.puzzle.adappter

import Material
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.puzzle.R

class MaterialAdapter(
    var materialList: List<Material>,
    val onTemplateSelectListener: OnTemplateSelectListener
) : RecyclerView.Adapter<MaterialViewHolder>(), View.OnClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val rootView = layoutInflater.inflate(R.layout.item_material, parent, false)
        return MaterialViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = materialList[position]
        holder.thumbnailImageView.setImageResource(R.color.disabled)
        val centerInside = CenterInside()
        Glide.with(holder.rootView.context)
            .load(material.thumbnailUrl)
            .optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(centerInside))
            .into(holder.thumbnailImageView)
        if (material.beDynamic != 0) {
            holder.videoIconImageView.visibility = View.VISIBLE
        } else {
            holder.videoIconImageView.visibility = View.GONE
        }
//        holder.newIconImageView.visibility = View.GONE
    }

    override fun getItemCount() = materialList.size

    override fun onClick(v: View) {
    }
}

/**
 * TemplateAdapter 对应的 ViewHolder
 */
class MaterialViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
    val thumbnailImageView: ImageView = rootView.findViewById(R.id.materialThumbnailImageView)
    val videoIconImageView: ImageView = rootView.findViewById(R.id.videoIconImageView)
    val newIconImageView: ImageView = rootView.findViewById(R.id.newIconImageView)
    val downloadIconImageView: ImageView = rootView.findViewById(R.id.downloadIconImageView)
}