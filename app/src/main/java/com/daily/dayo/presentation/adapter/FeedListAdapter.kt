package com.daily.dayo.presentation.adapter

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.Navigation
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.common.convertCountPlace
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.ItemFeedPostBinding
import com.daily.dayo.domain.model.Post
import com.daily.dayo.domain.model.categoryKR
import com.daily.dayo.presentation.fragment.feed.FeedFragmentDirections
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedListAdapter(private val requestManager: RequestManager) :
    PagingDataAdapter<Post, FeedListAdapter.FeedListViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post) =
                oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun likePostClick(button: ImageButton, post: Post, position: Int)
        fun bookmarkPostClick(button: ImageButton, post: Post, position: Int)
        fun tagPostClick(chip: Chip)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun updateItemAtPosition(position: Int, newPost: Post) {
        getItem(position).run { newPost }
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedListViewHolder {
        return FeedListViewHolder(
            ItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FeedListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FeedListViewHolder(private val binding: ItemFeedPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post?) {
            binding.post = post
            binding.heartCountStr =
                if (post?.heartCount != null) convertCountPlace(post.heartCount ?: 0) else "0"
            binding.commentCountStr =
                if (post?.commentCount != null) convertCountPlace(post.commentCount) else "0"
            binding.categoryKR = post?.category?.let { categoryKR(it) }
            CoroutineScope(Dispatchers.Main).launch {
                val postImgBitmap: Bitmap?
                val userThumbnailImgBitmap: Bitmap?
                postImgBitmap = withContext(Dispatchers.IO) {
                    loadImageBackground(
                        requestManager = requestManager,
                        width = binding.imgFeedPost.width,
                        height = binding.imgFeedPost.width,
                        imgName = post?.thumbnailImage ?: ""
                    )
                }
                userThumbnailImgBitmap = withContext(Dispatchers.IO) {
                    loadImageBackground(
                        requestManager = requestManager,
                        width = binding.imgFeedPostUserProfile.width,
                        height = binding.imgFeedPostUserProfile.height,
                        imgName = post?.userProfileImage ?: ""
                    )
                }
                loadImageView(
                    requestManager = requestManager,
                    width = binding.imgFeedPostUserProfile.width,
                    height = binding.imgFeedPostUserProfile.height,
                    img = userThumbnailImgBitmap,
                    imgView = binding.imgFeedPostUserProfile
                )
                loadImageView(
                    requestManager = requestManager,
                    width = binding.imgFeedPost.width,
                    height = binding.imgFeedPost.width,
                    img = postImgBitmap,
                    imgView = binding.imgFeedPost
                )
            }

            val isMine = (post?.memberId == DayoApplication.preferences.getCurrentUser().memberId)
            setPostOptionClickListener(
                isMine = isMine,
                postId = post?.postId!!,
                memberId = post.memberId!!
            )
            setOnUserProfileClickListener(postMemberId = post.memberId)
            setOnPostClickListener(postId = post.postId, nickname = post.nickname)

            // 해시태그
            if (post.hashtags?.isNotEmpty() == true) {
                binding.layoutFeedPostTagList.visibility = View.VISIBLE
                setTagList(post.hashtags)
            } else {
                binding.layoutFeedPostTagList.visibility = View.GONE
            }

            // 좋아요
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.btnFeedPostLike.setOnDebounceClickListener {
                    listener?.likePostClick(
                        button = binding.btnFeedPostLike,
                        post = post,
                        position = pos
                    )
                }
            }

            // 북마크
            if (pos != RecyclerView.NO_POSITION) {
                binding.btnFeedPostBookmark.setOnDebounceClickListener {
                    listener?.bookmarkPostClick(
                        button = binding.btnFeedPostBookmark,
                        post = post,
                        position = pos
                    )
                }
            }
        }

        private fun setTagList(tagList: List<String>) {
            binding.chipgroupFeedPostTagList.removeAllViews()
            if (!tagList.isNullOrEmpty()) {
                (tagList.indices).mapNotNull { index ->
                    val chip = LayoutInflater.from(binding.chipgroupFeedPostTagList.context)
                        .inflate(R.layout.item_post_tag, null) as Chip
                    val layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                        ViewGroup.MarginLayoutParams.WRAP_CONTENT
                    )
                    with(chip) {
                        chipBackgroundColor =
                            ColorStateList(
                                arrayOf(
                                    intArrayOf(-android.R.attr.state_pressed),
                                    intArrayOf(android.R.attr.state_pressed)
                                ),
                                intArrayOf(
                                    resources.getColor(R.color.gray_6_F6F6F6, context?.theme),
                                    resources.getColor(
                                        R.color.primary_green_23C882, context?.theme
                                    )
                                )
                            )

                        setTextColor(
                            ColorStateList(
                                arrayOf(
                                    intArrayOf(-android.R.attr.state_pressed),
                                    intArrayOf(android.R.attr.state_pressed)
                                ),
                                intArrayOf(
                                    resources.getColor(R.color.gray_1_313131, context?.theme),
                                    resources.getColor(
                                        R.color.white_FFFFFF, context?.theme
                                    )
                                )
                            )
                        )
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12F)
                        text = "# ${tagList[index].trim()}"

                        setOnDebounceClickListener {
                            listener?.tagPostClick(chip = this)
                        }
                    }
                    binding.chipgroupFeedPostTagList.addView(chip, layoutParams)
                }
            }
        }

        private fun setOnUserProfileClickListener(postMemberId: String) {
            binding.imgFeedPostUserProfile.setOnDebounceClickListener {
                Navigation.findNavController(it)
                    .navigate(FeedFragmentDirections.actionFeedFragmentToProfileFragment(memberId = postMemberId))
            }
            binding.tvFeedPostUserNickname.setOnDebounceClickListener {
                Navigation.findNavController(it)
                    .navigate(FeedFragmentDirections.actionFeedFragmentToProfileFragment(memberId = postMemberId))
            }
        }

        private fun setOnPostClickListener(postId: Int, nickname: String) {
            binding.tvFeedPostContent.setOnDebounceClickListener {
                Navigation.findNavController(it)
                    .navigate(FeedFragmentDirections.actionFeedFragmentToPostFragment(postId = postId))
            }
            binding.btnFeedPostComment.setOnDebounceClickListener {
                Navigation.findNavController(it)
                    .navigate(FeedFragmentDirections.actionFeedFragmentToPostFragment(postId = postId))
            }
        }

        private fun setPostOptionClickListener(isMine: Boolean, postId: Int, memberId: String) {
            binding.btnFeedPostOption.setOnDebounceClickListener {
                if (isMine) {
                    Navigation.findNavController(it)
                        .navigate(
                            FeedFragmentDirections.actionFeedFragmentToPostOptionMineFragment(
                                postId = postId
                            )
                        )
                } else {
                    Navigation.findNavController(it)
                        .navigate(
                            FeedFragmentDirections.actionFeedFragmentToPostOptionFragment(
                                postId = postId, memberId = memberId
                            )
                        )
                }
            }
        }
    }
}