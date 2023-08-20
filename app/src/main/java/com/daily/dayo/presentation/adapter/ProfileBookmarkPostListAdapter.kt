package com.daily.dayo.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.data.di.MainDispatcher
import com.daily.dayo.databinding.ItemProfilePostBinding
import com.daily.dayo.domain.model.BookmarkPost
import kotlinx.coroutines.*

class ProfileBookmarkPostListAdapter(
    private val requestManager: RequestManager,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : PagingDataAdapter<BookmarkPost, ProfileBookmarkPostListAdapter.ProfileBookmarkPostListViewHolder>(
    diffCallback
) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<BookmarkPost>() {
            override fun areItemsTheSame(oldItem: BookmarkPost, newItem: BookmarkPost) =
                oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: BookmarkPost, newItem: BookmarkPost): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileBookmarkPostListViewHolder {
        return ProfileBookmarkPostListViewHolder(
            ItemProfilePostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProfileBookmarkPostListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, bookmarkPost: BookmarkPost, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ProfileBookmarkPostListViewHolder(private val binding: ItemProfilePostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bookmarkPost: BookmarkPost?) {
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                ViewGroup.MarginLayoutParams.WRAP_CONTENT
            )

            with(binding.layoutProfilePostShimmer) {
                startShimmer()
                visibility = View.VISIBLE
            }
            binding.imgProfilePost.visibility = View.INVISIBLE

            CoroutineScope(mainDispatcher).launch {
                loadImageView(
                    requestManager = requestManager,
                    width = layoutParams.width,
                    height = layoutParams.width,
                    imgName = bookmarkPost?.thumbnailImage ?: "",
                    imgView = binding.imgProfilePost
                )
            }.invokeOnCompletion { throwable ->
                when (throwable) {
                    is CancellationException -> Log.e("Image Loading", "CANCELLED")
                    null -> {
                        binding.layoutProfilePostShimmer.stopShimmer()
                        binding.layoutProfilePostShimmer.visibility = View.GONE
                        binding.imgProfilePost.visibility = View.VISIBLE
                    }
                }
            }

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnDebounceClickListener {
                    if (bookmarkPost != null) {
                        listener?.onItemClick(itemView, bookmarkPost, pos)
                    }
                }
            }
        }
    }
}