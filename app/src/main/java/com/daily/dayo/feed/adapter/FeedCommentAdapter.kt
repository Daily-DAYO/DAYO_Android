package com.daily.dayo.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.databinding.ItemFeedPostCommentBinding
import com.daily.dayo.post.model.PostCommentContent

class FeedCommentAdapter : ListAdapter<PostCommentContent, FeedCommentAdapter.FeedCommentViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PostCommentContent>() {
            override fun areItemsTheSame(oldItem: PostCommentContent, newItem: PostCommentContent) =
                oldItem.commentId == newItem.commentId

            override fun areContentsTheSame(oldItem: PostCommentContent, newItem: PostCommentContent): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedCommentViewHolder
            = FeedCommentViewHolder (ItemFeedPostCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: FeedCommentAdapter.FeedCommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<PostCommentContent>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class FeedCommentViewHolder(private val binding: ItemFeedPostCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(postCommentContent: PostCommentContent) {
            binding.feedComment = postCommentContent
        }
    }
}