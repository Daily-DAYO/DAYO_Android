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
import com.daily.dayo.common.GlideLoadUtil.COMMENT_USER_THUMBNAIL_SIZE
import com.daily.dayo.common.GlideLoadUtil.loadImageViewProfile
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.ItemPostCommentBinding
import com.daily.dayo.domain.model.Comment
import com.daily.dayo.presentation.fragment.post.PostFragmentDirections
import com.google.android.material.snackbar.Snackbar

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
                layoutPostCommentDelete.setOnDebounceClickListener {
                    Snackbar.make(it, "삭제버튼 클릭", Snackbar.LENGTH_SHORT).show()
                }
                loadImageViewProfile(
                    requestManager = requestManager,
                    width = COMMENT_USER_THUMBNAIL_SIZE,
                    height = COMMENT_USER_THUMBNAIL_SIZE,
                    imgName = comment.profileImg,
                    imgView = imgPostCommentUserProfile
                )
            }

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnDebounceClickListener {
                    listener?.onItemClick(itemView, comment, pos)
                }
                binding.layoutPostCommentDelete.setOnDebounceClickListener {
                    listener?.DeletePostCommentClick(comment, pos)
                }
            }

            setOnProfileClickListener(comment.memberId)
            setBindingSetVariable(comment)
        }

        private fun setOnProfileClickListener(commentMemberId: String) {
            binding.imgPostCommentUserProfile.setOnDebounceClickListener {
                Navigation.findNavController(it)
                    .navigate(PostFragmentDirections.actionPostFragmentToProfileFragment(memberId = commentMemberId))
            }
            binding.tvPostCommentUserNickname.setOnDebounceClickListener {
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