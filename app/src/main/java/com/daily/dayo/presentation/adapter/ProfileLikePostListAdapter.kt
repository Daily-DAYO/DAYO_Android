package com.daily.dayo.presentation.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.data.di.IoDispatcher
import com.daily.dayo.data.di.MainDispatcher
import com.daily.dayo.databinding.ItemProfilePostBinding
import com.daily.dayo.domain.model.LikePost
import kotlinx.coroutines.*

class ProfileLikePostListAdapter(
    private val requestManager: RequestManager,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) :
    PagingDataAdapter<LikePost, ProfileLikePostListAdapter.ProfileLikePostListViewHolder>(
        diffCallback
    ) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<LikePost>() {
            override fun areItemsTheSame(oldItem: LikePost, newItem: LikePost) =
                oldItem === newItem

            override fun areContentsTheSame(oldItem: LikePost, newItem: LikePost): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileLikePostListViewHolder {
        return ProfileLikePostListViewHolder(
            ItemProfilePostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProfileLikePostListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, likePost: LikePost, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ProfileLikePostListViewHolder(private val binding: ItemProfilePostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(likePost: LikePost?) {
            if (likePost == null) return
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.MATCH_PARENT
            )

            binding.layoutProfilePostShimmer.startShimmer()
            binding.layoutProfilePostShimmer.visibility = View.VISIBLE
            binding.imgProfilePost.visibility = View.INVISIBLE

            CoroutineScope(mainDispatcher).launch {
                val profileListPostImage: Bitmap?
                if (likePost.preLoadThumbnail == null) {
                    profileListPostImage = withContext(ioDispatcher) {
                        loadImageBackground(
                            requestManager = requestManager,
                            width = layoutParams.width,
                            height = layoutParams.width,
                            imgName = likePost.thumbnailImage
                        )
                    }
                } else {
                    profileListPostImage = likePost.preLoadThumbnail
                    likePost.preLoadThumbnail = null
                }
                loadImageView(
                    requestManager = requestManager,
                    width = layoutParams.width,
                    height = layoutParams.width,
                    img = profileListPostImage!!,
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
                    listener?.onItemClick(itemView, likePost, pos)
                }
            }
        }
    }
}