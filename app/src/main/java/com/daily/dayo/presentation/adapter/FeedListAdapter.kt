package com.daily.dayo.presentation.adapter

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.databinding.ItemFeedPostBinding
import com.daily.dayo.domain.model.Comment
import com.daily.dayo.domain.model.Post
import com.daily.dayo.domain.model.categoryKR
import com.daily.dayo.presentation.fragment.feed.FeedFragmentDirections
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedListAdapter(private val requestManager: RequestManager) :
    ListAdapter<Post, FeedListAdapter.FeedListViewHolder>(diffCallback) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedListViewHolder {
        return FeedListViewHolder(
            ItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FeedListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun submitList(list: MutableList<Post>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class FeedListViewHolder(private val binding: ItemFeedPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.post = post
            binding.categoryKR = post.category?.let { categoryKR(it) }
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.MATCH_PARENT
            )
            CoroutineScope(Dispatchers.Main).launch {
                val postImgBitmap: Bitmap?
                val userThumbnailImgBitmap: Bitmap?
                postImgBitmap = withContext(Dispatchers.IO) {
                    loadImageBackground(
                        requestManager = requestManager,
                        width = 158,
                        height = 158,
                        imgName = post.thumbnailImage ?: ""
                    )
                }
                userThumbnailImgBitmap = withContext(Dispatchers.IO) {
                    loadImageBackground(
                        requestManager = requestManager,
                        width = 17,
                        height = 17,
                        imgName = post.userProfileImage ?: ""
                    )
                }
                loadImageView(
                    requestManager = requestManager,
                    width = layoutParams.width,
                    height = layoutParams.width,
                    img = userThumbnailImgBitmap,
                    imgView = binding.imgFeedPostUserProfile
                )
                loadImageView(
                    requestManager = requestManager,
                    width = layoutParams.width,
                    height = layoutParams.width,
                    img = postImgBitmap,
                    imgView = binding.imgFeedPost
                )
            }

            val isMine = (post.memberId == DayoApplication.preferences.getCurrentUser().memberId)
            setPostOptionClickListener(isMine = isMine, postId = post.postId!!)
            setOnUserProfileClickListener(postMemberId = post.memberId!!)
            setOnPostClickListener(postId = post.postId, nickname = post.nickname)

            // ??????
            if (post.commentCount!! > 2) {
                post.comments?.let {
                    setCommentList(
                        comments = it.subList(0, 2),
                        postId = post.postId!!,
                        nickname = post.nickname
                    )
                }
                binding.tvFeedPostMoreComment.visibility = View.VISIBLE
            } else {
                post.comments?.let {
                    setCommentList(
                        comments = it,
                        postId = post.postId!!,
                        nickname = post.nickname
                    )
                }
                binding.tvFeedPostMoreComment.visibility = View.GONE
            }

            // ????????????
            if (post.hashtags?.isNotEmpty() == true) {
                binding.layoutFeedPostTagList.visibility = View.VISIBLE
                setTagList(post.hashtags)
            } else {
                binding.layoutFeedPostTagList.visibility = View.GONE
            }

            // ?????????
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.btnFeedPostLike.setOnClickListener {
                    listener?.likePostClick(
                        button = binding.btnFeedPostLike,
                        post = post,
                        position = pos
                    )
                }
            }

            // ?????????
            if (pos != RecyclerView.NO_POSITION) {
                binding.btnFeedPostBookmark.setOnClickListener {
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

                        setOnClickListener {
                            listener?.tagPostClick(chip = this)
                        }
                    }
                    binding.chipgroupFeedPostTagList.addView(chip, layoutParams)
                }
            }
        }

        private fun setCommentList(comments: List<Comment>, postId: Int, nickname: String) {
            val feedCommentAdapter = FeedCommentAdapter()
            binding.rvFeedPostComment.adapter = feedCommentAdapter
            feedCommentAdapter.submitList(comments.toMutableList())
            feedCommentAdapter.setOnItemClickListener(object :
                FeedCommentAdapter.OnItemClickListener {
                override fun onItemClick(v: View, comment: Comment, position: Int) {
                    Navigation.findNavController(v).navigate(
                        FeedFragmentDirections.actionFeedFragmentToPostFragment(
                            postId = postId
                        )
                    )
                }
            })
        }

        private fun setOnUserProfileClickListener(postMemberId: String) {
            binding.imgFeedPostUserProfile.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(FeedFragmentDirections.actionFeedFragmentToProfileFragment(memberId = postMemberId))
            }
            binding.tvFeedPostUserNickname.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(FeedFragmentDirections.actionFeedFragmentToProfileFragment(memberId = postMemberId))
            }
        }

        private fun setOnPostClickListener(postId: Int, nickname: String) {
            binding.tvFeedPostContent.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(FeedFragmentDirections.actionFeedFragmentToPostFragment(postId = postId))
            }
            binding.tvFeedPostMoreComment.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(FeedFragmentDirections.actionFeedFragmentToPostFragment(postId = postId))
            }
        }

        private fun setPostOptionClickListener(isMine: Boolean, postId: Int) {
            binding.btnFeedPostOption.setOnClickListener {
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
                                postId = postId
                            )
                        )
                }
            }
        }
    }
}