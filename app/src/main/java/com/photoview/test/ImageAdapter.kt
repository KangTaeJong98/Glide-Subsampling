package com.photoview.test

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter : ListAdapter<String, ImageHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ImageHolder(parent)

    override fun onBindViewHolder(holder: ImageHolder, position: Int) = holder.onBind(getItem(position))

    override fun onViewAttachedToWindow(holder: ImageHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttached()
    }

    override fun onViewDetachedFromWindow(holder: ImageHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetached()
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        }
    }
}