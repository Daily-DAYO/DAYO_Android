package com.daily.dayo.post.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.daily.dayo.databinding.ItemPostImageSliderBinding

class PostImageSliderAdapter : ListAdapter<String, PostImageSliderAdapter.PostImageViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageViewHolder {
        return PostImageViewHolder(ItemPostImageSliderBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: PostImageViewHolder, position: Int) {
        holder.bindSliderImage(getItem(position))
    }

    override fun submitList(list: List<String>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class PostImageViewHolder(private val binding: ItemPostImageSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindSliderImage(imageURL: String?) {
            Glide.with(binding.imgSlider.context)
                .load("http://117.17.198.45:8080/images/" + imageURL)
                .into(binding.imgSlider)
        }
    }

}