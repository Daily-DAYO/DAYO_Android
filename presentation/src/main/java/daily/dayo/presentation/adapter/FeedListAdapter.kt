package daily.dayo.presentation.adapter

import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.library.baseAdapters.BR
import androidx.navigation.Navigation
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import daily.dayo.presentation.R
import daily.dayo.presentation.common.GlideLoadUtil.loadImageView
import daily.dayo.presentation.common.ReplaceUnicode.trimBlankText
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.ItemFeedPostBinding
import daily.dayo.domain.model.Post
import daily.dayo.domain.model.categoryKR
import daily.dayo.presentation.fragment.feed.FeedFragmentDirections
import com.google.android.material.chip.Chip
import daily.dayo.domain.model.User
import daily.dayo.presentation.common.TimeChangerUtil.timeChange
import kotlinx.coroutines.Dispatchers

class FeedListAdapter(private val requestManager: RequestManager, private val currentUserInfo: User) :
    PagingDataAdapter<Post, FeedListAdapter.FeedListViewHolder>(diffCallback) {

    private var glideRequestManager: RequestManager? = null
    private var postImageSliderAdapter: PostImageSliderAdapter? = null
    private var indicators: Array<ImageView?>? = null

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post) =
                oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem.apply {
                    preLoadThumbnail = null
                    preLoadUserImg = null
                } == newItem.apply {
                    preLoadThumbnail = null
                    preLoadUserImg = null
                }

            // areItemTheSame()이 true, areContentsTheSame()이 false을 호출 하면 반환
            override fun getChangePayload(oldItem: Post, newItem: Post): Any? {
                return if (oldItem.heart != newItem.heart
                    || oldItem.heartCount != newItem.heartCount
                    || oldItem.commentCount != newItem.commentCount
                    || oldItem.bookmark != newItem.bookmark
                ) true else null
            }
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

    override fun onBindViewHolder(
        holder: FeedListViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            this.onBindViewHolder(holder, position)
        } else {
            if (payloads[0] == true) {
                if (getItem(position) == null) {
                    this.onBindViewHolder(holder, position)
                } else {
                    holder.bindReactionState(getItem(position)!!)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: FeedListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        glideRequestManager = null
        postImageSliderAdapter = null
        indicators = null
    }

    inner class FeedListViewHolder(private val binding: ItemFeedPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post?) {
            with(binding) {
                post?.let {
                    this.post = it
                    this.createDateTime = timeChange(
                        context = binding.tvFeedPostTime.context,
                        time = it.createDateTime ?: ""
                    )
                    this.heart = it.heart
                    this.heartCountStr = it.heartCount.toString()
                    this.commentCountStr = it.commentCount?.toString() ?: ""
                    this.bookmark = it.bookmark
                }
            }

            binding.categoryKR = post?.category?.let { categoryKR(it) }
            loadImageView(
                requestManager = requestManager,
                width = binding.imgFeedPostUserProfile.width,
                height = binding.imgFeedPostUserProfile.height,
                imgName = post?.userProfileImage ?: "",
                imgView = binding.imgFeedPostUserProfile
            )

            // 이미지
            setImageSlider()
            binding.viewFeedPostImageIndicators.removeAllViews()
            post?.postImages?.let {
                postImageSliderAdapter?.submitList(it)
                if (it.size > 1) setUpIndicators(it.size)
            }

            // 옵션
            val isMine = (post?.memberId == currentUserInfo.memberId)
            setPostOptionClickListener(
                isMine = isMine,
                postId = post?.postId!!,
                memberId = post.memberId!!
            )
            post.memberId?.let { memberId ->
                setOnUserProfileClickListener(postMemberId = memberId)
            }
            post.postId?.let { postId ->
                setOnPostClickListener(postId = postId, nickname = post.nickname)
            }

            // 해시태그
            if (post.hashtags?.isNotEmpty() == true) {
                binding.layoutFeedPostTagList.visibility = View.VISIBLE
                post.hashtags?.let { hashTags ->
                    setTagList(hashTags)
                }
            } else {
                binding.layoutFeedPostTagList.visibility = View.GONE
            }

            // 좋아요
            binding.btnFeedPostLike.setOnDebounceClickListener {
                listener?.likePostClick(
                    button = binding.btnFeedPostLike,
                    post = post,
                    position = bindingAdapterPosition
                )
            }

            postImageSliderAdapter?.setOnItemClickListener(object :
                PostImageSliderAdapter.OnItemClickListener {
                override fun postImageDoubleTap(lottieAnimationView: LottieAnimationView) {
                    if (!post.heart) {
                        lottieAnimationView.visibility = View.VISIBLE
                        lottieAnimationView.playAnimation()
                        listener?.likePostClick(
                            button = binding.btnFeedPostLike,
                            post = post,
                            position = bindingAdapterPosition
                        )
                    }
                }
            })

            // 북마크
            binding.btnFeedPostBookmark.setOnDebounceClickListener {
                listener?.bookmarkPostClick(
                    button = binding.btnFeedPostBookmark,
                    post = post,
                    position = bindingAdapterPosition
                )
            }
        }

        private fun setImageSlider() {
            glideRequestManager = Glide.with(binding.root)
            postImageSliderAdapter = glideRequestManager?.let { requestManager ->
                PostImageSliderAdapter(requestManager = requestManager)
            }

            with(binding.vpFeedPostImage) {
                adapter = postImageSliderAdapter
                overScrollMode = View.OVER_SCROLL_NEVER
                offscreenPageLimit = 1
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        setCurrentIndicator(position)
                    }
                })
            }
        }

        private fun setUpIndicators(count: Int) {
            indicators = arrayOfNulls(count)
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(16, 8, 16, 8)

            indicators?.let { indicators ->
                for (i in indicators.indices) {
                    indicators[i] = ImageView(binding.root.context)
                    indicators[i]!!.setImageDrawable(
                        ContextCompat.getDrawable(binding.root.context, R.drawable.ic_indicator_inactive)
                    )
                    indicators[i]!!.layoutParams = params
                    binding.viewFeedPostImageIndicators.addView(indicators[i])
                }
            }
            setCurrentIndicator(0)
        }

        private fun setCurrentIndicator(position: Int) {
            val childCount: Int = binding.viewFeedPostImageIndicators.childCount
            for (i in 0 until childCount) {
                val imageView = binding.viewFeedPostImageIndicators.getChildAt(i) as ImageView
                if (i == position) {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(binding.root.context, R.drawable.ic_indicator_active)
                    )
                } else {
                    imageView.setImageDrawable(
                        ContextCompat.getDrawable(binding.root.context, R.drawable.ic_indicator_inactive)
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
                                    resources.getColor(R.color.gray_6_F0F1F3, context?.theme),
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
                        text = "# ${trimBlankText(tagList[index])}"

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

        fun bindReactionState(post: Post) {
            setHeartState(post)
        }

        private fun setHeartState(post: Post) {
            binding.post?.let {
                it.heart = post.heart
                it.heartCount = post.heartCount
                it.bookmark = post.bookmark
            }
            setBindingSetVariable(post)
        }

        private fun setBindingSetVariable(post: Post) {
            with(binding) {
                setVariable(BR.heart, post.heart)
                setVariable(BR.heartCountStr, post.heartCount.toString())
                setVariable(BR.commentCountStr, post.commentCount.toString())
                setVariable(BR.bookmark, post.bookmark)
                executePendingBindings()
            }
        }
    }
}