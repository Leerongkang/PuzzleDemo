package com.puzzle.adappter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lrk.puzzle.demo.R

class TemplateAdapter(
    var list: List<String>,
    val onTemplateSelect: (adapter: TemplateAdapter, holder: TemplateViewHolder) -> Unit
) :
    RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>() {

    var selectPosition = 0

    class TemplateViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
        val image = itemView.findViewById<ImageView>(R.id.templateItemImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        return TemplateViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_templte, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        val context = holder.image.context
        val prefix = context.getString(R.string.template_path_prefix)
        val path = if (position == selectPosition) {
            prefix + list[position] + context.getString(R.string.template_path_suffix_pressed)
        } else {
            prefix + list[position] + context.getString(R.string.template_path_suffix)
        }
        val inputStream = context.assets.open(path)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        holder.image.setImageBitmap(bitmap)
        holder.rootView.setOnClickListener {
            onTemplateSelect(this, holder)
        }
    }

    override fun getItemCount(): Int = list.size
}