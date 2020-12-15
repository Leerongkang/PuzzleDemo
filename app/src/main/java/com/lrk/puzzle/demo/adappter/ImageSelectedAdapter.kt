package com.lrk.puzzle.demo.adappter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.lrk.puzzle.demo.R

class ImageSelectedAdapter(imageList: List<String>) :
    ImageAdapter(imageList) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_selected,parent,false))
    }
}