package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.databinding.ItemPostImageSliderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostImageSliderAdapter(private val requestManager: RequestManager) :
    ListAdapter<String, PostImageSliderAdapter.PostImageViewHolder>(
        diffCallback
    ) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageViewHolder {
        return PostImageViewHolder(
            ItemPostImageSliderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostImageViewHolder, position: Int) {
        holder.bindSliderImage(getItem(position))
    }

    override fun submitList(list: List<String>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class PostImageViewHolder(private val binding: ItemPostImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindSliderImage(imageURL: String?) {
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.MATCH_PARENT
            )
            CoroutineScope(Dispatchers.Main).launch {
                val postImage = withContext(Dispatchers.IO) {
                    GlideLoadUtil.loadImageBackground(
                        requestManager = requestManager,
                        width = layoutParams.width,
                        height = layoutParams.width,
                        imgName = imageURL ?: ""
                    )
                }
                loadImageView(
                    requestManager = requestManager,
                    width = layoutParams.width,
                    height = layoutParams.width,
                    img = postImage,
                    imgView = binding.imgSlider
                )
            }
        }
    }
}