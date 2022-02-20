package com.daily.dayo.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daily.dayo.databinding.ItemProfileLikePostBinding
import com.daily.dayo.profile.model.BookmarkPostListData

class ProfileBookmarkPostListAdapter : RecyclerView.Adapter<ProfileBookmarkPostListAdapter.ProfileBookmarkPostListViewHolder>(){

    companion object{
        private val diffCallback = object : DiffUtil.ItemCallback<BookmarkPostListData>() {
            override fun areItemsTheSame(oldItem: BookmarkPostListData, newItem: BookmarkPostListData) =
                oldItem === newItem

            override fun areContentsTheSame(oldItem: BookmarkPostListData, newItem: BookmarkPostListData): Boolean =
                oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<BookmarkPostListData>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ProfileBookmarkPostListViewHolder {
        return ProfileBookmarkPostListViewHolder(
            ItemProfileLikePostBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )
    }

    override fun onBindViewHolder(holder: ProfileBookmarkPostListViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListener{
        fun onItemClick(v: View, likePost: BookmarkPostListData, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    inner class ProfileBookmarkPostListViewHolder(private val binding: ItemProfileLikePostBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(likePost : BookmarkPostListData) {
            Glide.with(binding.imgProfileLikePost.context)
                .load("http://117.17.198.45:8080/images/" + likePost.thumbnailImage)
                .into(binding.imgProfileLikePost)

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,likePost,pos)
                }
            }
        }
    }
}