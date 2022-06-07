package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.databinding.ItemFeedPostCommentBinding
import com.daily.dayo.domain.model.Comment
import com.daily.dayo.presentation.fragment.feed.FeedFragmentDirections

class FeedCommentAdapter : ListAdapter<Comment, FeedCommentAdapter.FeedCommentViewHolder>(
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
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedCommentViewHolder
            = FeedCommentViewHolder (ItemFeedPostCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: FeedCommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(comments: MutableList<Comment>?) {
        super.submitList(comments)
    }

    inner class FeedCommentViewHolder(private val binding: ItemFeedPostCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.comment = comment
            setOnNicknameClickListener(comment.memberId)

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, comment, pos)
                }
            }
        }
        private fun setOnNicknameClickListener(commentMemberId: String){
            binding.tvFeedPostCommentUserNickname.setOnClickListener {
                Navigation.findNavController(it).navigate(FeedFragmentDirections.actionFeedFragmentToProfileFragment(memberId = commentMemberId))
            }
        }
    }
}