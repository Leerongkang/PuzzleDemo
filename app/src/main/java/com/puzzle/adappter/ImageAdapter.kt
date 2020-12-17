package com.puzzle.adappter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lrk.puzzle.demo.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class ImageAdapter(val imageList: List<String>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    lateinit var onselect: OnSelectedListener
    class ImageViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.ItemImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image,parent,false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        GlobalScope.launch {
            withContext(Dispatchers.Default){
                val op = BitmapFactory.Options().apply {
                    inSampleSize = 20
                }
                val bitmap = BitmapFactory.decodeFile(imageList[position],op)
                withContext(Dispatchers.Main){
                    holder.imageView.setImageBitmap(bitmap)
                }
            }
        }
        holder.imageView.setOnClickListener {
            onselect.onSelect(imageList[position],position)
        }
    }

    override fun getItemCount() = imageList.size

    fun setOnSelectedListener(listener: OnSelectedListener){
        onselect = listener
    }

    fun interface OnSelectedListener{
        fun onSelect(path: String,position: Int)
    }
}