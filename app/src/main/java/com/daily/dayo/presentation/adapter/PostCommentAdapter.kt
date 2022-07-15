package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.BR
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.GlideLoadUtil
import com.daily.dayo.common.GlideLoadUtil.loadImageViewProfile
import com.daily.dayo.databinding.ItemPostCommentBinding
import com.daily.dayo.domain.model.Comment
import com.daily.dayo.presentation.fragment.post.PostFragmentDirections
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostCommentAdapter(private val requestManager: RequestManager) :
    ListAdapter<Comment, PostCommentAdapter.PostCommentViewHolder>(
        diffCallback
    ) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment) =
                oldItem.commentId == newItem.commentId

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
                oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, comment: Comment, position: Int)
        fun DeletePostCommentClick(comment: Comment, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCommentViewHolder =
        PostCommentViewHolder(
            ItemPostCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PostCommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<Comment>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class PostCommentViewHolder(private val binding: ItemPostCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            val currentUserNickname =
                DayoApplication.preferences.getCurrentUser().nickname.toString()
            if (comment.nickname == currentUserNickname) {
                binding.layoutPostCommentDelete.visibility = View.VISIBLE
            } else {
                binding.layoutPostCommentDelete.visibility = View.INVISIBLE
            }

            with(binding) {
                layoutPostCommentDelete.setOnClickListener {
                    Snackbar.make(it, "삭제버튼 클릭", Snackbar.LENGTH_SHORT).show()
                }
                CoroutineScope(Dispatchers.Main).launch {
                    val userThumbnailImgBitmap = withContext(Dispatchers.IO) {
                        GlideLoadUtil.loadImageBackground(
                            requestManager = requestManager,
                            width = 40,
                            height = 40,
                            imgName = comment.profileImg
                        )
                    }
                    loadImageViewProfile(
                        requestManager = requestManager,
                        width = 40,
                        height = 40,
                        img = userThumbnailImgBitmap,
                        imgView = imgPostCommentUserProfile
                    )
                }
            }

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, comment, pos)
                }
                binding.layoutPostCommentDelete.setOnClickListener {
                    listener?.DeletePostCommentClick(comment, pos)
                }
            }

            setOnProfileClickListener(comment.memberId)
            setBindingSetVariable(comment)
        }

        private fun setOnProfileClickListener(commentMemberId: String) {
            binding.imgPostCommentUserProfile.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(PostFragmentDirections.actionPostFragmentToProfileFragment(memberId = commentMemberId))
            }
            binding.tvPostCommentUserNickname.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(PostFragmentDirections.actionPostFragmentToProfileFragment(memberId = commentMemberId))
            }
        }

        private fun setBindingSetVariable(comment: Comment) {
            with(binding) {
                setVariable(BR.comment, comment)
                setVariable(
                    BR.commentCreateTime,
                    comment.createTime
                )
                executePendingBindings()
            }
        }
    }
}