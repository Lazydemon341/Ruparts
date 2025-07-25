package com.ruparts.app.features.product.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.ruparts.app.R
import com.ruparts.app.features.product.domain.ProductPhotoItem

class ProductPhotosAdapter : ListAdapter<ProductPhotoItem, ProductPhotosAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_photo, parent, false) as ImageView
        return PhotoViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoItem = getItem(position)
        holder.bind(photoItem.url)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id.hashCode().toLong()
    }

    class PhotoViewHolder(private val imageView: ImageView) : RecyclerView.ViewHolder(imageView) {
        fun bind(photoUrl: String) {
            imageView.load(photoUrl) {
                crossfade(true)
            }
        }
    }

    class PhotoDiffCallback : DiffUtil.ItemCallback<ProductPhotoItem>() {
        override fun areItemsTheSame(oldItem: ProductPhotoItem, newItem: ProductPhotoItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductPhotoItem, newItem: ProductPhotoItem): Boolean {
            return oldItem == newItem
        }
    }
}
