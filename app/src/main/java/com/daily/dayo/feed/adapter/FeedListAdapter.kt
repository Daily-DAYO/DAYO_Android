package com.daily.dayo.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.databinding.ItemFeedPostBinding
import com.daily.dayo.feed.model.FeedContent

class FeedListAdapter : ListAdapter<FeedContent, FeedListAdapter.FeedListViewHolder>(diffCallback){
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<FeedContent>() {
            override fun areItemsTheSame(oldItem: FeedContent, newItem: FeedContent) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FeedContent, newItem: FeedContent): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    interface OnItemClickListener{
        fun likePostClick(btn: ImageButton, data: FeedContent, pos: Int)
    }
    private var listener: OnItemClickListener?= null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : FeedListViewHolder {
        return FeedListViewHolder(
            ItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )
    }

    override fun onBindViewHolder(holder: FeedListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
    }

    override fun submitList(list: MutableList<FeedContent>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class FeedListViewHolder(private val binding: ItemFeedPostBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(feedContent: FeedContent, currentPosition: Int) {
            binding.feedContent = feedContent
        }
    }
}