package com.puzzle.adappter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.util.lruCache
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
    private val op = BitmapFactory.Options()
    private val bitmapSize = 200
    private val cacheSize = (Runtime.getRuntime().maxMemory() / 4).toInt()
    private val imageCache = lruCache<String, Bitmap>(
        maxSize = cacheSize,
        sizeOf = { _, bitmap ->
            bitmap.byteCount
        })

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
        holder.imageView.setImageResource(R.color.white)
        XXMainScope().launch {
            val bitmap = getBitmap(imageList[position])
            holder.imageView.setImageBitmap(bitmap)
        }
        holder.imageView.setOnClickListener {
            onSelected(this, holder.layoutPosition)
        }
    }

    private suspend fun getBitmap(path: String): Bitmap {
        var bitmap = imageCache[path]
        if (bitmap == null) {
            bitmap = compressedBitmap(path)
            imageCache.put(path, bitmap)
        }
        return bitmap
    }

    private suspend fun compressedBitmap(path: String): Bitmap = withContext(Dispatchers.IO) {
        val exifInterface = ExifInterface(path)
        val imageHeight = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)?.toInt() ?: 0
        val imageWidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)?.toInt() ?: 0
        val scalingRatio = if (imageHeight > imageWidth) {
            imageHeight / bitmapSize
        } else {
            imageWidth / bitmapSize
        }
        op.inSampleSize = scalingRatio
        BitmapFactory.decodeFile(path, op)
    }

    override fun getItemCount() = imageList.size
}

class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView = itemView.findViewById<ImageView>(R.id.ItemImageView)
}