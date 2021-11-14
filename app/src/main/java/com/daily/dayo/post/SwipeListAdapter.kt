package com.daily.dayo.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.databinding.ItemPostCommentBinding
import com.google.android.material.snackbar.Snackbar

class SwipeListAdapter : RecyclerView.Adapter<SwipeListAdapter.SwipeViewHolder>() {
    // TODO : 서버에서 Comment List 받아오는 코드 추가. (현재 임시 Item 추가)
    private val items : MutableList<String> = mutableListOf<String>().apply {
        for (i in 0..10){
            add("$i")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwipeViewHolder = SwipeViewHolder (
        ItemPostCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: SwipeListAdapter.SwipeViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = items.size

    inner class SwipeViewHolder(private val binding: ItemPostCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.tvDelete.setOnClickListener {
                Snackbar.make(it, "삭제버튼 클릭", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}