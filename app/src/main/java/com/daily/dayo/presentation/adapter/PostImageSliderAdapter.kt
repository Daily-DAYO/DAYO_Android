package com.daily.dayo.presentation.adapter

import android.animation.Animator
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.common.GlideLoadUtil
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.data.di.IoDispatcher
import com.daily.dayo.data.di.MainDispatcher
import com.daily.dayo.databinding.ItemPostImageSliderBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostImageSliderAdapter(
    private val requestManager: RequestManager,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ListAdapter<String, PostImageSliderAdapter.PostImageViewHolder>(
        diffCallback
    ) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    interface OnItemClickListener {
        fun postImageDoubleTap(lottieAnimationView: LottieAnimationView)
    }

    private var clickListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageViewHolder {
        return PostImageViewHolder(
            ItemPostImageSliderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostImageViewHolder, position: Int) {
        holder.bindSliderImage(getItem(position))

        val gestureDetector = GestureDetector(
            holder.itemView.context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    clickListener?.postImageDoubleTap(holder.itemView.findViewById(R.id.lottie_post_heart))
                    return super.onDoubleTap(e)
                }
            })
        holder.itemView.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    override fun submitList(list: List<String>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class PostImageViewHolder(private val binding: ItemPostImageSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindSliderImage(imageURL: String?) {
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.MATCH_PARENT
            )
            CoroutineScope(mainDispatcher).launch {
                val postImage = withContext(ioDispatcher) {
                    GlideLoadUtil.loadImageBackground(
                        requestManager = requestManager,
                        width = layoutParams.width,
                        height = layoutParams.width,
                        imgName = imageURL ?: ""
                    )
                }
                loadImageView(
                    requestManager = requestManager,
                    width = layoutParams.width,
                    height = layoutParams.width,
                    img = postImage,
                    imgView = binding.imgSlider,
                    placeholderLottie = binding.lottiePostImage
                )
            }
            binding.lottiePostHeart.let { bigLikeLottie ->
                bigLikeLottie.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator?) {}
                    override fun onAnimationEnd(animation: Animator?) {
                        bigLikeLottie.visibility = View.GONE
                    }

                    override fun onAnimationCancel(animation: Animator?) {}
                    override fun onAnimationRepeat(animation: Animator?) {}
                })
            }
        }
    }
}