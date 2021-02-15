package com.puzzle.ui.adappter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.puzzle.R

/**
 * [MainActivity] 中 [templateRecyclerView] 的 Adapter
 * 用于显示拼图模板缩略图
 *
 * @param templateList:     拼图模板缩略图的路径
 *
 * @param onTemplateSelectListener: 拼图模板点击事件
 *                          [adapter] : 当前的 adapter, 用于更新 RecyclerView，如 notifyItemChanged
 *                          [holder]  : 点击的 TemplateViewHolder，用于获取点击的下标
 */
class TemplateAdapter(
    var templateList: List<String>,
    val onTemplateSelectListener: OnTemplateSelectListener
) : RecyclerView.Adapter<TemplateViewHolder>(), View.OnClickListener {

    //  当前点击选中的模板 和 上次选中的模板，用于更新界面中的选中状态
    var currentSelectPos = 0
    var lastSelectedPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_templte, parent, false)
        val templateViewHolder = TemplateViewHolder(itemView)
        templateViewHolder.imageView.setOnClickListener(this@TemplateAdapter)
        return templateViewHolder
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.imageView.tag = holder       // 通过 tag，绑定点击
        val context = holder.imageView.context
        val prefix = context.getString(R.string.template_path_prefix)
        val path: String = if (position == currentSelectPos) {
            "$prefix${templateList[position]}${context.getString(R.string.template_path_suffix_pressed)}"
        } else {
            "$prefix${templateList[position]}${context.getString(R.string.template_path_suffix)}"
        }
        Glide.with(holder.imageView.context).load("file:///android_asset/$path").into(holder.imageView)
    }

    override fun getItemCount(): Int = templateList.size

    override fun onClick(v: View) {
        onTemplateSelectListener(this, v.tag as TemplateViewHolder)
        lastSelectedPos = currentSelectPos      // 更新选中状态
    }

}
/**
 * TemplateAdapter 对应的 ViewHolder
 */
class TemplateViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
    val imageView: ImageView = rootView.findViewById(R.id.templateItemImage)
}

typealias OnTemplateSelectListener = (adapter: TemplateAdapter, holder: TemplateViewHolder) -> Unit