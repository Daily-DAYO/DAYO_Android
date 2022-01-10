package com.daily.dayo.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daily.dayo.BR
import com.daily.dayo.databinding.ItemPostCommentBinding
import com.daily.dayo.post.model.PostCommentContent
import com.google.android.material.snackbar.Snackbar

class PostCommentAdapter : ListAdapter<PostCommentContent, PostCommentAdapter.PostCommentViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PostCommentContent>() {
            override fun areItemsTheSame(oldItem: PostCommentContent, newItem: PostCommentContent) =
                oldItem.commentId == newItem.commentId

            override fun areContentsTheSame(oldItem: PostCommentContent, newItem: PostCommentContent): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCommentViewHolder
    = PostCommentViewHolder (ItemPostCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: PostCommentAdapter.PostCommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: MutableList<PostCommentContent>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class PostCommentViewHolder(private val binding: ItemPostCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(postCommentContent: PostCommentContent) {
            with(binding) {
                imgPostCommentDelete.setOnClickListener {
                    Snackbar.make(it, "삭제버튼 클릭", Snackbar.LENGTH_SHORT).show()
                }
                Glide.with(imgPostCommentUserProfile.context)
                    .load("http://www.endlesscreation.kr:8080/images/" + postCommentContent.profileImg)
                    .into(imgPostCommentUserProfile)
            }
            setBindingSetVariable(postCommentContent)
        }
        private fun setBindingSetVariable(postCommentContent: PostCommentContent) {
            with(binding) {
                setVariable(BR.comment, postCommentContent)
                executePendingBindings()
            }
        }
    }
}