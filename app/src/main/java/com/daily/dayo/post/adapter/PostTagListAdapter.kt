package com.daily.dayo.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.databinding.ItemPostTagBinding

class PostTagListAdapter: ListAdapter<String, PostTagListAdapter.PostTagListViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostTagListViewHolder {
        return PostTagListViewHolder(ItemPostTagBinding.inflate(LayoutInflater.from(parent?.context), parent, false))
    }

    override fun onBindViewHolder(holder: PostTagListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class PostTagListViewHolder(private val binding: ItemPostTagBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tagString: String) {
            binding.tvPostTag.text = tagString
        }
    }

}