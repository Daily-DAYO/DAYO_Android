package com.daily.dayo.presentation.adapter

import android.animation.Animator
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.databinding.library.baseAdapters.BR
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.GlideLoadUtil.loadImageBackgroundProfile
import com.daily.dayo.common.GlideLoadUtil.loadImagePreload
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.common.GlideLoadUtil.loadImageViewProfile
import com.daily.dayo.common.extension.navigateSafe
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.ItemMainPostBinding
import com.daily.dayo.domain.model.Post
import com.daily.dayo.presentation.fragment.home.HomeFragmentDirections
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeNewAdapter(
    val rankingShowing: Boolean,
    private val requestManager: RequestManager,
    private val likeListener: (Int, Boolean) -> Unit
) : ListAdapter<Post, HomeNewAdapter.HomeNewViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post) =
                oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem == newItem

            override fun getChangePayload(oldItem: Post, newItem: Post): Any? {
                return if (oldItem.heart != newItem.heart && oldItem.heartCount != newItem.heartCount) true else null
            }
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
            ), likeListener
        )
    }

    override fun onBindViewHolder(
        holder: HomeNewAdapter.HomeNewViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            this.onBindViewHolder(holder, position)
        } else {
            if (payloads[0] == true) {
                holder.bindLikeState(getItem(position).heart)
            }
        }
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

    inner class HomeNewViewHolder(
        private val binding: ItemMainPostBinding,
        private val likeListener: (Int, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        lateinit var postContent: Post

        init {
            binding.lottieMainPostLike.setOnDebounceClickListener(300L) {
                likeListener(postContent.postId ?: 0, (it as LottieAnimationView).progress == 0F)
            }
        }

        fun bind(postContent: Post, currentPosition: Int) {
            this.postContent = postContent
            loadImages()
            setRanking(currentPosition)

            setBindingSetVariable(
                postContent.apply {
                    binding.lottieMainPostLike.progress = if (heart) 1F else 0F
                }
            )
            postContent.postId?.let { setRootClickListener(it, postContent.nickname) }
            postContent.memberId?.let { setNicknameClickListener(it) }
        }

        private fun setBindingSetVariable(post: Post) {
            with(binding) {
                setVariable(BR.post, post)
                executePendingBindings()
            }
        }

        fun bindLikeState(isLiked: Boolean) {
            setLottieClickListener(isLiked)
            setHeartCount(isLiked)
        }

        private fun setLottieClickListener(isLiked: Boolean) {
            with(binding.lottieMainPostLike) {
                this.removeAllAnimatorListeners()
                this.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {
                        this@with.setOnDebounceClickListener(0L) {}
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        this@with.setOnDebounceClickListener(0L) {
                            likeListener(
                                postContent.postId ?: 0,
                                (it as LottieAnimationView).progress == 0F
                            )
                        }
                    }

                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationRepeat(animation: Animator?) {}
                })

                if (isLiked) this.playAnimation()
                else this.progress = 0F
            }
        }

        private fun setHeartCount(isLiked: Boolean) {
            with(binding) {
                post?.let {
                    if (isLiked) {
                        it.heartCount = it.heartCount + 1
                    } else {
                        if (it.heartCount <= 0) it.heartCount = 0
                        else it.heartCount = it.heartCount - 1
                    }
                    setBindingSetVariable(it)
                }
            }
        }

        private fun setRootClickListener(postId: Int, nickname: String) {
            binding.root.setOnDebounceClickListener {
                Navigation.findNavController(it).navigateSafe(
                    currentDestinationId = R.id.HomeFragment,
                    action = R.id.action_homeFragment_to_postFragment,
                    args = HomeFragmentDirections.actionHomeFragmentToPostFragment(postId).arguments
                )
            }
        }

        private fun setNicknameClickListener(memberId: String) {
            binding.tvMainPostUserNickname.setOnDebounceClickListener {
                Navigation.findNavController(it)
                    .navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment(memberId = memberId))
            }
        }

        private fun startShimmer() {
            with(binding.layoutContentsShimmer) {
                startShimmer()
                visibility = View.VISIBLE
                setShimmerDisplay()
            }
        }

        private fun stopShimmer() {
            with(binding.layoutContentsShimmer) {
                stopShimmer()
                visibility = View.GONE
                setShimmerDisplay()
            }
        }

        private fun setShimmerDisplay() {
            binding.layoutContents.isVisible = !binding.layoutContentsShimmer.isVisible
        }

        private fun setRanking(currentPosition: Int) {
            with(binding) {
                layoutPostRankingNumber.isVisible = !(currentPosition > 3 || !rankingShowing)
                tvPostRankingNumber.text = (currentPosition + 1).toString()
            }
        }

        private fun loadImages() {
            startShimmer()

            val postImg = binding.imgMainPost
            val userThumbnailImg = binding.imgMainPostUserProfile


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
                    null -> { stopShimmer() }
                }
            }
        }
    }
}