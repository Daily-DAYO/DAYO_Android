package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.databinding.ItemSearchResultPostBinding
import com.daily.dayo.domain.model.Search
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchTagResultPostAdapter(private val requestManager: RequestManager) :
    ListAdapter<Search, SearchTagResultPostAdapter.SearchTagResultPostViewHolder>(
        diffCallback
    ) {
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

    override fun onBindViewHolder(
        holder: SearchTagResultPostViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<Search>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class SearchTagResultPostViewHolder(private val binding: ItemSearchResultPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(postContent: Search) {
            CoroutineScope(Dispatchers.Main).launch {
                val postImage = withContext(Dispatchers.IO) {
                    loadImageBackground(
                        requestManager = requestManager,
                        width = 158,
                        height = 158,
                        imgName = postContent.thumbnailImage
                    )
                }
                loadImageView(
                    requestManager = requestManager,
                    width = 158,
                    height = 158,
                    img = postImage,
                    imgView = binding.imgSearchResultPost
                )
            }

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, postContent, pos)
                }
            }
        }
    }
}