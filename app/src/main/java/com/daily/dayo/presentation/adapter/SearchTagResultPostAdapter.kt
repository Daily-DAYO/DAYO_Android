package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.common.GlideApp
import com.daily.dayo.databinding.ItemSearchResultPostBinding
import com.daily.dayo.domain.model.Search

class SearchTagResultPostAdapter :
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
        ItemSearchResultPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))

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
            GlideApp.with(binding.imgSearchResultPost.context)
                .load("http://117.17.198.45:8080/images/" + postContent.thumbnailImage)
                .into(binding.imgSearchResultPost)

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, postContent, pos)
                }
            }
        }
    }
}