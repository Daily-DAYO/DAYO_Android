package com.daily.dayo.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil.HOME_POST_THUMBNAIL_SIZE
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.ItemSearchResultPostBinding
import com.daily.dayo.domain.model.Search
import kotlinx.coroutines.*

class SearchTagResultPostAdapter(private val requestManager: RequestManager) :
    PagingDataAdapter<Search, SearchTagResultPostAdapter.SearchTagResultPostViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Search>() {
            override fun areItemsTheSame(oldItem: Search, newItem: Search) =
                oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: Search, newItem: Search) =
                oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, postContent: Search, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchTagResultPostViewHolder = SearchTagResultPostViewHolder(
        ItemSearchResultPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: SearchTagResultPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: MutableList<Search>?) = differ.submitList(list?.let { ArrayList(it) })


    inner class SearchTagResultPostViewHolder(private val binding: ItemSearchResultPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(postContent: Search?) {
            binding.layoutSearchResultPostContentsShimmer.startShimmer()
            binding.layoutSearchResultPostContentsShimmer.visibility = View.VISIBLE
            binding.imgSearchResultPost.visibility = View.INVISIBLE

            CoroutineScope(Dispatchers.Main).launch {
                loadImageView(
                    requestManager = requestManager,
                    width = HOME_POST_THUMBNAIL_SIZE,
                    height = HOME_POST_THUMBNAIL_SIZE,
                    imgName = postContent?.thumbnailImage ?: "",
                    imgView = binding.imgSearchResultPost
                )
            }.invokeOnCompletion { throwable ->
                when (throwable) {
                    is CancellationException -> Log.e("Image Loading", "CANCELLED")
                    null -> {
                        binding.layoutSearchResultPostContentsShimmer.stopShimmer()
                        binding.layoutSearchResultPostContentsShimmer.visibility = View.GONE
                        binding.imgSearchResultPost.visibility = View.VISIBLE
                    }
                }
            }

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnDebounceClickListener {
                    if (postContent != null) {
                        listener?.onItemClick(itemView, postContent, pos)
                    }
                }
            }
        }
    }
}