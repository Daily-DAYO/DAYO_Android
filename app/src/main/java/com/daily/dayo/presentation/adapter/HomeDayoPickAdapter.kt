package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.library.baseAdapters.BR
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.daily.dayo.R
import com.daily.dayo.databinding.ItemMainPostBinding
import com.daily.dayo.domain.model.Post
import com.daily.dayo.presentation.fragment.home.HomeFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeDayoPickAdapter(val rankingShowing: Boolean) :
    ListAdapter<Post, HomeDayoPickAdapter.HomeDayoPickViewHolder>(diffCallback) {
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

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeDayoPickViewHolder {
        return HomeDayoPickViewHolder(ItemMainPostBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        )
    }

    override fun onBindViewHolder(holder: HomeDayoPickViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
    }

    override fun submitList(list: MutableList<Post>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class HomeDayoPickViewHolder(private val binding: ItemMainPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var postImg = binding.imgMainPost
        var rankingNumber = binding.tvPostRankingNumber
        var userThumbnailImg = binding.imgMainPostUserProfile

        fun bind(postContent: Post, currentPosition: Int) {
            if (currentPosition > 3 || !rankingShowing) {
                binding.layoutPostRankingNumber.visibility = View.INVISIBLE
            } else {
                binding.layoutPostRankingNumber.visibility = View.VISIBLE
                rankingNumber.text = (currentPosition + 1).toString()
            }
            CoroutineScope(Dispatchers.Main).launch {
                val postImgBitmap = withContext(Dispatchers.IO) {
                    Glide.with(postImg.context)
                        .asBitmap()
                        .override(158, 158)
                        .placeholder(R.drawable.ic_dayo_circle_grayscale)
                        .load("http://117.17.198.45:8080/images/" + postContent.thumbnailImage)
                        .priority(Priority.HIGH)
                        .submit()
                        .get()
                }
                val userThumbnailImgBitmap = withContext(Dispatchers.IO) {
                    Glide.with(postImg.context)
                        .asBitmap()
                        .override(158, 158)
                        .load("http://117.17.198.45:8080/images/" + postContent.userProfileImage)
                        .submit()
                        .get()
                }

                Glide.with(postImg.context)
                    .load(postImgBitmap)
                    .override(158, 158)
                    .thumbnail(0.1f)
                    .placeholder(R.drawable.ic_dayo_circle_grayscale)
                    .priority(Priority.HIGH)
                    .centerCrop()
                    .into(postImg)
                Glide.with(userThumbnailImg.context)
                    .load(userThumbnailImgBitmap)
                    .override(158, 158)
                    .centerCrop()
                    .into(userThumbnailImg)
            }

            setBindingSetVariable(postContent)
            postContent.postId?.let { setRootClickListener(it, postContent.nickname) }
            postContent.memberId?.let { setNicknameClickListener(it) }

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.btnMainPostLike.setOnClickListener {
                    listener?.likePostClick(binding.btnMainPostLike, postContent, pos)
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
                Navigation.findNavController(it).navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment(memberId = memberId))
            }
        }
    }
}