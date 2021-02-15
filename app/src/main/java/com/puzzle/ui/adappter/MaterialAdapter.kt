package com.puzzle.ui.adappter

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
import com.puzzle.material.DOWNLOAD_STATE_DOWNLOADING
import com.puzzle.material.DOWNLOAD_STATE_NOT_DOWNLOAD
import com.puzzle.material.Material
import com.puzzle.ui.view.ProgressView


/**
 * [MainActivity] 中 [materialRecyclerView] 的 Adapter
 * 用于显示拼图模板缩略图
 *
 * @param materialList 素材列表
 *
 * @param onMaterialSelectListener 素材点击事件（下载或应用）
 *                          [adapter] : 当前的 adapter, 用于更新 RecyclerView，如 notifyItemChanged
 *                          [holder]  : 点击的 TemplateViewHolder，用于获取点击的下标
 */
class MaterialAdapter(
    var materialList: List<Material>,
    val onMaterialSelectListener: OnMaterialSelectListener
) : RecyclerView.Adapter<MaterialViewHolder>(), View.OnClickListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val rootView = layoutInflater.inflate(R.layout.item_material, parent, false)
        val materialViewHolder = MaterialViewHolder(rootView)
        materialViewHolder.thumbnailImageView.setOnClickListener(this@MaterialAdapter)
        return materialViewHolder
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = materialList[position]
        holder.thumbnailImageView.setImageResource(R.color.disabled)
        holder.thumbnailImageView.tag = holder
        // [Glide] 加载图片, 使用 [WebpDrawable] 加载 webp 格式的动图
        val centerInside = CenterInside()
        Glide.with(holder.rootView.context)
            .load(material.thumbnailUrl)
            .optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(centerInside))
            .into(holder.thumbnailImageView)
        // 同步下载进度
        holder.downloadProgressView.progress = material.downloadProgress.toFloat()
        // 动态素材图标
        holder.videoIconImageView.visibility = if (material.beDynamic != 0) {
                                                   View.VISIBLE
                                               } else {
                                                   View.INVISIBLE
                                               }
        // 下载图片
        holder.downloadIconImageView.visibility = if (material.beDownload == DOWNLOAD_STATE_NOT_DOWNLOAD) {
                                                      View.VISIBLE
                                                  } else {
                                                      View.INVISIBLE
                                                  }
        // 新素材图标
//        holder.newIconImageView.visibility = if (material.downloadProgress <= DOWNLOAD_STATE_NOT_DOWNLOAD) {
//                                                 View.VISIBLE
//                                             } else {
//                                                 View.GONE
//                                             }
        // 下载进度
        holder.downloadProgressView.visibility = if (material.beDownload == DOWNLOAD_STATE_DOWNLOADING) {
                                                     View.VISIBLE
                                                 } else {
                                                     View.INVISIBLE
                                                 }
    }

    override fun getItemCount() = materialList.size

    override fun onClick(v: View) {
        val holder = v.tag as MaterialViewHolder
        onMaterialSelectListener(this, holder)
    }
}

/**
 * [MaterialAdapter] 对应的 ViewHolder
 */
class MaterialViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
    val thumbnailImageView: ImageView = rootView.findViewById(R.id.materialThumbnailImageView)
    val videoIconImageView: ImageView = rootView.findViewById(R.id.videoIconImageView)
    val newIconImageView: ImageView = rootView.findViewById(R.id.newIconImageView)
    val downloadIconImageView: ImageView = rootView.findViewById(R.id.downloadIconImageView)
    val downloadProgressView: ProgressView = rootView.findViewById(R.id.downloadProgressView)
}

/**
 * [MaterialViewHolder] 的点击事件
 */
typealias OnMaterialSelectListener = (adapter: MaterialAdapter, holder: MaterialViewHolder) -> Unit