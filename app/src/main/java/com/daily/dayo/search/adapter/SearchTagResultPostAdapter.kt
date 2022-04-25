package com.daily.dayo.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daily.dayo.databinding.ItemSearchResultPostBinding
import com.daily.dayo.search.model.SearchTagPostContent

class SearchTagResultPostAdapter :
    ListAdapter<SearchTagPostContent, SearchTagResultPostAdapter.SearchTagResultPostViewHolder>(
        SearchTagResultPostAdapter.diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<SearchTagPostContent>() {
            override fun areItemsTheSame(
                oldItem: SearchTagPostContent,
                newItem: SearchTagPostContent,
            ) =
                oldItem == newItem

            override fun areContentsTheSame(
                oldItem: SearchTagPostContent,
                newItem: SearchTagPostContent,
            ): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, postContent: SearchTagPostContent, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SearchTagResultPostAdapter.SearchTagResultPostViewHolder = SearchTagResultPostViewHolder(
        ItemSearchResultPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(
        holder: SearchTagResultPostAdapter.SearchTagResultPostViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<SearchTagPostContent>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class SearchTagResultPostViewHolder(private val binding: ItemSearchResultPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(postContent: SearchTagPostContent) {
            Glide.with(binding.imgSearchResultPost.context)
                .load("http://117.17.198.45:8080/images/" + postContent.thumbnailImage)
                .into(binding.imgSearchResultPost)

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    if (postContent != null) {
                        listener?.onItemClick(itemView, postContent, pos)
                    }
                }
            }
        }
    }
}