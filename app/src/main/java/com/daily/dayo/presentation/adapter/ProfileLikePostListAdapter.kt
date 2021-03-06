package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.databinding.ItemProfileLikePostBinding
import com.daily.dayo.domain.model.LikePost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileLikePostListAdapter(private val requestManager: RequestManager) :
    RecyclerView.Adapter<ProfileLikePostListAdapter.ProfileLikePostListViewHolder>() {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<LikePost>() {
            override fun areItemsTheSame(oldItem: LikePost, newItem: LikePost) =
                oldItem === newItem

            override fun areContentsTheSame(oldItem: LikePost, newItem: LikePost): Boolean =
                oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<LikePost>) = differ.submitList(list)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileLikePostListViewHolder {
        return ProfileLikePostListViewHolder(
            ItemProfileLikePostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProfileLikePostListViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, likePost: LikePost, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ProfileLikePostListViewHolder(private val binding: ItemProfileLikePostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(likePost: LikePost) {
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.MATCH_PARENT
            )

            CoroutineScope(Dispatchers.Main).launch {
                val profileListPostImage = withContext(Dispatchers.IO) {
                    loadImageBackground(
                        requestManager = requestManager,
                        width = layoutParams.width,
                        height = layoutParams.width,
                        imgName = likePost.thumbnailImage
                    )
                }
                loadImageView(
                    requestManager = requestManager,
                    width = layoutParams.width,
                    height = layoutParams.width,
                    img = profileListPostImage,
                    imgView = binding.imgProfileLikePost
                )
            }

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, likePost, pos)
                }
            }
        }
    }


}