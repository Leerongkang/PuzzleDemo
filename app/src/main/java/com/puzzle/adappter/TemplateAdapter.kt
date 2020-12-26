package com.puzzle.adappter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.puzzle.R

/**
 * MainActivity中templateRecyclerView的Adapter
 * 用于显示拼图模板缩略图
 */
class TemplateAdapter(
    var list: List<String>,
    val onTemplateSelect: (adapter: TemplateAdapter, holder: TemplateViewHolder) -> Unit
) :
    RecyclerView.Adapter<TemplateViewHolder>() {

    var currentSelectPos = 0
    var lastSelectedPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_templte, parent, false)
        return TemplateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        val context = holder.imageView.context
        val prefix = context.getString(R.string.template_path_prefix)
        val path: String = if (position == currentSelectPos) {
                                "$prefix${list[position]}${context.getString(R.string.template_path_suffix_pressed)}"
                            } else {
                                "$prefix${list[position]}${context.getString(R.string.template_path_suffix)}"
                            }
        Glide.with(holder.imageView.context).load("file:///android_asset/$path").into(holder.imageView)
        holder.rootView.setOnClickListener {
            onTemplateSelect(this, holder)
            lastSelectedPos = currentSelectPos
        }
    }

    override fun getItemCount(): Int = list.size

}
/**
 * TemplateAdapter对应的ViewHolder
 */
class TemplateViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
    val imageView: ImageView = itemView.findViewById(R.id.templateItemImage)
}