package com.puzzle.adappter

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

class TemplateAdapter(
    var list: List<String>,
    val onTemplateSelect: (adapter: TemplateAdapter, holder: TemplateViewHolder) -> Unit
) :
    RecyclerView.Adapter<TemplateViewHolder>() {

    var currentSelectPos = 0
    var lastSelectedPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        return TemplateViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_templte, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        val context = holder.image.context
        val prefix = context.getString(R.string.template_path_prefix)
        val path = if (position == currentSelectPos) {
            prefix + list[position] + context.getString(R.string.template_path_suffix_pressed)
        } else {
            prefix + list[position] + context.getString(R.string.template_path_suffix)
        }
        Glide.with(holder.image.context).load("file:///android_asset/$path").into(holder.image)
        holder.rootView.setOnClickListener {
            onTemplateSelect(this, holder)
            lastSelectedPos = currentSelectPos
        }
    }

    override fun getItemCount(): Int = list.size

}

class TemplateViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
    val image = itemView.findViewById<ImageView>(R.id.templateItemImage)
}