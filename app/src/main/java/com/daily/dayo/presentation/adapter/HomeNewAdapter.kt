package com.daily.dayo.presentation.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.library.baseAdapters.BR
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.GlideLoadUtil.loadImageBackgroundProfile
import com.daily.dayo.common.GlideLoadUtil.loadImagePreload
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.common.GlideLoadUtil.loadImageViewProfile
import com.daily.dayo.databinding.ItemMainPostBinding
import com.daily.dayo.domain.model.Post
import com.daily.dayo.presentation.fragment.home.HomeFragmentDirections
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeNewAdapter(val rankingShowing: Boolean, private val requestManager: RequestManager) :
    ListAdapter<Post, HomeNewAdapter.HomeNewViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post) =
                oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun likePostClick(btn: ImageButton, post: Post, position: Int)
    }

    private var clickListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeNewViewHolder {
        return HomeNewViewHolder(
            ItemMainPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HomeNewViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)

        //Preload
        if (position <= itemCount) {
            val endPosition = if (position + 6 > itemCount) {
                itemCount
            } else {
                position + 6
            }
            for (i in position until endPosition) {
                loadImagePreload(
                    requestManager = requestManager,
                    width = 158,
                    height = 158,
                    imgName = getItem(i).thumbnailImage ?: ""
                )
            }
        }
    }

    override fun submitList(list: MutableList<Post>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class HomeNewViewHolder(private val binding: ItemMainPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var postImg = binding.imgMainPost
        var rankingNumber = binding.tvPostRankingNumber
        var userThumbnailImg = binding.imgMainPostUserProfile

        fun bind(postContent: Post, currentPosition: Int) {
            binding.layoutContentsShimmer.startShimmer()
            binding.layoutContentsShimmer.visibility = View.VISIBLE
            binding.layoutContents.visibility = View.INVISIBLE

            if (currentPosition > 3 || !rankingShowing) {
                binding.layoutPostRankingNumber.visibility = View.INVISIBLE
            } else {
                binding.layoutPostRankingNumber.visibility = View.VISIBLE
                rankingNumber.text = (currentPosition + 1).toString()
            }
            CoroutineScope(Dispatchers.Main).launch {
                val postImgBitmap: Bitmap?
                val userThumbnailImgBitmap: Bitmap?
                if (postContent.preLoadThumbnail == null) {
                    postImgBitmap = withContext(Dispatchers.IO) {
                        loadImageBackground(
                            requestManager = requestManager,
                            width = 158,
                            height = 158,
                            imgName = postContent.thumbnailImage ?: ""
                        )
                    }
                    userThumbnailImgBitmap = withContext(Dispatchers.IO) {
                        loadImageBackgroundProfile(
                            requestManager = requestManager,
                            width = 17,
                            height = 17,
                            imgName = postContent.userProfileImage ?: ""
                        )
                    }
                } else {
                    postImgBitmap = postContent.preLoadThumbnail
                    userThumbnailImgBitmap = postContent.preLoadUserImg
                    postContent.preLoadThumbnail = null
                    postContent.preLoadUserImg = null
                }
                loadImageView(
                    requestManager = requestManager,
                    width = 158,
                    height = 158,
                    img = postImgBitmap!!,
                    imgView = postImg
                )
                loadImageViewProfile(
                    requestManager = requestManager,
                    width = 17,
                    height = 17,
                    img = userThumbnailImgBitmap!!,
                    imgView = userThumbnailImg
                )
            }.invokeOnCompletion { throwable ->
                when (throwable) {
                    is CancellationException -> Log.e("Image Loading", "CANCELLED")
                    null -> {
                        binding.layoutContentsShimmer.stopShimmer()
                        binding.layoutContentsShimmer.visibility = View.GONE
                        binding.layoutContents.visibility = View.VISIBLE
                    }
                }
            }

            setBindingSetVariable(postContent)
            postContent.postId?.let { setRootClickListener(it, postContent.nickname) }
            postContent.memberId?.let { setNicknameClickListener(it) }

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.btnMainPostLike.setOnClickListener {
                    clickListener?.likePostClick(binding.btnMainPostLike, postContent, pos)
                }
            }
        }

        private fun setBindingSetVariable(post: Post) {
            with(binding) {
                setVariable(BR.post, post)
                executePendingBindings()
            }
        }

        private fun setRootClickListener(postId: Int, nickname: String) {
            binding.root.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(HomeFragmentDirections.actionHomeFragmentToPostFragment(postId))
            }
        }

        private fun setNicknameClickListener(memberId: String) {
            binding.tvMainPostUserNickname.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment(memberId = memberId))
            }
        }
    }
}