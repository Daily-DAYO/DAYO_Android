package com.daily.dayo.presentation.adapter

import android.animation.Animator
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.library.baseAdapters.BR
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.common.GlideLoadUtil.HOME_POST_THUMBNAIL_SIZE
import com.daily.dayo.common.GlideLoadUtil.HOME_USER_THUMBNAIL_SIZE
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.GlideLoadUtil.loadImagePreload
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.common.GlideLoadUtil.loadImageViewProfile
import com.daily.dayo.common.extension.navigateSafe
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.data.di.IoDispatcher
import com.daily.dayo.data.di.MainDispatcher
import com.daily.dayo.databinding.ItemMainPostBinding
import com.daily.dayo.domain.model.Post
import com.daily.dayo.presentation.fragment.home.HomeFragmentDirections
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeDayoPickAdapter(
    val rankingShowing: Boolean,
    private val requestManager: RequestManager,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ListAdapter<Post, HomeDayoPickAdapter.HomeDayoPickViewHolder>(diffCallback) {
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

            override fun getChangePayload(oldItem: Post, newItem: Post): Any? {
                return if (oldItem.heart != newItem.heart || oldItem.heartCount != newItem.heartCount) true else null
            }
        }
    }

    interface OnItemClickListener {
        fun likePostClick(post: Post)
    }

    private var clickListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeDayoPickViewHolder {
        return HomeDayoPickViewHolder(
            ItemMainPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: HomeDayoPickViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            this.onBindViewHolder(holder, position)
        } else {
            if (payloads[0] == true) {
                holder.bindLikeState(getItem(position))
            }
        }
    }

    override fun onBindViewHolder(holder: HomeDayoPickViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
        preloadFutureLoadingImages(position)
    }

    override fun getItemViewType(position: Int): Int {
        // ViewHolder Pattern의 이점을 잃었지만, 카테고리 간 이동하면서 랭킹 숫자가 잘못 표시되는 점을 해결
        return position
    }

    override fun submitList(list: MutableList<Post>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    private fun preloadFutureLoadingImages(position: Int) {
        if (position <= itemCount) {
            val endPosition = if (position + 6 > itemCount) {
                itemCount
            } else {
                position + 6
            }
            for (i in position until endPosition) {
                loadImagePreload(
                    requestManager = requestManager,
                    width = HOME_POST_THUMBNAIL_SIZE,
                    height = HOME_POST_THUMBNAIL_SIZE,
                    imgName = getItem(i).thumbnailImage ?: ""
                )
            }
        }
    }

    inner class HomeDayoPickViewHolder(
        private val binding: ItemMainPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        lateinit var postContent: Post

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
            binding.lottieMainPostLike.setOnDebounceClickListener(300L) {
                clickListener?.likePostClick(post = postContent)
            }
        }

        private fun setBindingSetVariable(post: Post) {
            with(binding) {
                setVariable(BR.post, post)
                executePendingBindings()
            }
        }

        fun bindLikeState(post: Post) {
            setLottieClickListener(post)
            setHeartCount(post)
        }

        private fun setLottieClickListener(post: Post) {
            with(binding.lottieMainPostLike) {
                setOnDebounceClickListener(300L) {
                    clickListener?.likePostClick(post = post)
                }

                this.removeAllAnimatorListeners()
                this.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {
                        this@with.setOnDebounceClickListener(0L) {}
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        this@with.setOnDebounceClickListener(0L) {
                            clickListener?.likePostClick(post = post)
                        }
                    }

                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationRepeat(animation: Animator?) {}
                })

                if (post.heart) this.playAnimation()
                else this.progress = 0F
            }
        }

        private fun setHeartCount(post: Post) {
            binding.post = post
            setBindingSetVariable(post)
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

            CoroutineScope(mainDispatcher).launch {
                val postImgBitmap: Bitmap?
                val userThumbnailImgBitmap: Bitmap?
                if (postContent.preLoadThumbnail == null) {
                    postImgBitmap = withContext(ioDispatcher) {
                        loadImageBackground(
                            requestManager = requestManager,
                            width = HOME_POST_THUMBNAIL_SIZE,
                            height = HOME_POST_THUMBNAIL_SIZE,
                            imgName = postContent.thumbnailImage ?: ""
                        )
                    }
                    userThumbnailImgBitmap = withContext(ioDispatcher) {
                        loadImageBackground(
                            requestManager = requestManager,
                            width = HOME_USER_THUMBNAIL_SIZE,
                            height = HOME_USER_THUMBNAIL_SIZE,
                            imgName = postContent.userProfileImage ?: ""
                        )
                    }
                } else {
                    postImgBitmap = postContent.preLoadThumbnail
                    userThumbnailImgBitmap = postContent.preLoadUserImg
                    postContent.preLoadThumbnail = null
                    postContent.preLoadUserImg = null
                }

                try {
                    loadImageView(
                        requestManager = requestManager,
                        width = HOME_POST_THUMBNAIL_SIZE,
                        height = HOME_POST_THUMBNAIL_SIZE,
                        img = postImgBitmap!!,
                        imgView = postImg
                    )
                } catch (loadException: IllegalStateException) {
                    loadImageView(
                        requestManager = requestManager,
                        width = HOME_POST_THUMBNAIL_SIZE,
                        height = HOME_POST_THUMBNAIL_SIZE,
                        imgName = postContent.thumbnailImage ?: "",
                        imgView = postImg
                    )
                } catch (imgNullException: NullPointerException) {
                    loadImageView(
                        requestManager = requestManager,
                        width = HOME_POST_THUMBNAIL_SIZE,
                        height = HOME_POST_THUMBNAIL_SIZE,
                        imgName = postContent.thumbnailImage ?: "",
                        imgView = postImg
                    )
                }
                try {
                    loadImageViewProfile(
                        requestManager = requestManager,
                        width = HOME_USER_THUMBNAIL_SIZE,
                        height = HOME_USER_THUMBNAIL_SIZE,
                        img = userThumbnailImgBitmap!!,
                        imgView = userThumbnailImg
                    )
                } catch (loadException: IllegalStateException) {
                    loadImageViewProfile(
                        requestManager = requestManager,
                        width = HOME_USER_THUMBNAIL_SIZE,
                        height = HOME_USER_THUMBNAIL_SIZE,
                        imgName = postContent.userProfileImage ?: "",
                        imgView = userThumbnailImg
                    )
                } catch (imgNullException: NullPointerException) {
                    loadImageViewProfile(
                        requestManager = requestManager,
                        width = HOME_USER_THUMBNAIL_SIZE,
                        height = HOME_USER_THUMBNAIL_SIZE,
                        imgName = postContent.userProfileImage ?: "",
                        imgView = userThumbnailImg
                    )
                }
            }.invokeOnCompletion { throwable ->
                when (throwable) {
                    is CancellationException -> Log.e("Image Loading", "CANCELLED")
                    null -> {
                        stopShimmer()
                    }
                }
            }
        }
    }
}