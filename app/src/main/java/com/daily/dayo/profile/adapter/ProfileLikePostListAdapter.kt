package com.daily.dayo.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daily.dayo.databinding.ItemProfileLikePostBinding
import com.daily.dayo.profile.model.LikePostListData

class ProfileLikePostListAdapter : RecyclerView.Adapter<ProfileLikePostListAdapter.ProfileLikePostListViewHolder>(){

    companion object{
        private val diffCallback = object : DiffUtil.ItemCallback<LikePostListData>() {
            override fun areItemsTheSame(oldItem: LikePostListData, newItem: LikePostListData) =
                oldItem === newItem

            override fun areContentsTheSame(oldItem: LikePostListData, newItem: LikePostListData): Boolean =
                oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<LikePostListData>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ProfileLikePostListViewHolder {
        return ProfileLikePostListViewHolder(
            ItemProfileLikePostBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )
    }

    override fun onBindViewHolder(holder: ProfileLikePostListViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListener{
        fun onItemClick(v: View, likePost: LikePostListData, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    inner class ProfileLikePostListViewHolder(private val binding: ItemProfileLikePostBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(likePost : LikePostListData) {
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