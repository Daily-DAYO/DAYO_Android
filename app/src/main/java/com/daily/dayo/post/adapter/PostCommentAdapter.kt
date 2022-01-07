package com.daily.dayo.post.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.databinding.ItemPostCommentBinding
import com.google.android.material.snackbar.Snackbar

class PostCommentAdapter : RecyclerView.Adapter<PostCommentAdapter.PostCommentViewHolder>() {
    // TODO : 서버에서 Comment List 받아오는 코드 추가. (현재 임시 Item 추가)
    private val items : MutableList<String> = mutableListOf<String>().apply {
        for (i in 0..10){
            add("$i")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCommentAdapter.PostCommentViewHolder = PostCommentViewHolder (
        ItemPostCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: PostCommentAdapter.PostCommentViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = items.size

    inner class PostCommentViewHolder(private val binding: ItemPostCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.imgPostCommentDelete.setOnClickListener {
                Snackbar.make(it, "삭제버튼 클릭", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}