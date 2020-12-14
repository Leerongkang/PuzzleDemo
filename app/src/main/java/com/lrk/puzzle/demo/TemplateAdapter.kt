package com.lrk.puzzle.demo

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.meitu.puzzle.R

class TemplateAdapter(val context: Context, var list: List<String>) : RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>() {

    lateinit var onTemplateSelectListener: OnTemplateSelectListener

    var select = 0

    class TemplateViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
        val image = itemView.findViewById<ImageView>(R.id.templateItemImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        return TemplateViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_templte, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        val path = if (position == select) list[position] + "_pressed" else list[position]
        val ins = context.assets.open("templates/$path")
        val bitmap = BitmapFactory.decodeStream(ins)
        holder.image.setImageBitmap(bitmap)
        holder.rootView.setOnClickListener {
            onTemplateSelectListener.onTemplateSelect(holder)
        }
    }

    override fun getItemCount(): Int {
        return 13
    }

    fun setOnTemplateSelectListener(block: (holder: TemplateViewHolder) -> Unit) {
        onTemplateSelectListener = object : OnTemplateSelectListener {
            override fun onTemplateSelect(holder: TemplateViewHolder) {
                block(holder)
            }
        }
    }

    interface OnTemplateSelectListener {
        fun onTemplateSelect(holder: TemplateViewHolder)
    }
}