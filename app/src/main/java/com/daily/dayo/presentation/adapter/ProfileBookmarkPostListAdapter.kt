package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil
import com.daily.dayo.databinding.ItemProfileLikePostBinding
import com.daily.dayo.domain.model.BookmarkPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileBookmarkPostListAdapter(private val requestManager: RequestManager) :
    RecyclerView.Adapter<ProfileBookmarkPostListAdapter.ProfileBookmarkPostListViewHolder>() {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<BookmarkPost>() {
            override fun areItemsTheSame(oldItem: BookmarkPost, newItem: BookmarkPost) =
                oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: BookmarkPost, newItem: BookmarkPost): Boolean =
                oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<BookmarkPost>) = differ.submitList(list)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileBookmarkPostListViewHolder {
        return ProfileBookmarkPostListViewHolder(
            ItemProfileLikePostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProfileBookmarkPostListViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, bookmarkPost: BookmarkPost, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ProfileBookmarkPostListViewHolder(private val binding: ItemProfileLikePostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bookmarkPost: BookmarkPost) {
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
            )

            CoroutineScope(Dispatchers.Main).launch {
                val userThumbnailImgBitmap = withContext(Dispatchers.IO) {
                    GlideLoadUtil.loadImageBackground(
                        requestManager = requestManager,
                        width = layoutParams.width,
                        height = layoutParams.width,
                        imgName = bookmarkPost.thumbnailImage
                    )
                }
                GlideLoadUtil.loadImageView(
                    requestManager = requestManager,
                    width = layoutParams.width,
                    height = layoutParams.width,
                    img = userThumbnailImgBitmap,
                    imgView = binding.imgProfileLikePost
                )
            }

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, bookmarkPost, pos)
                }
            }
        }
    }
}