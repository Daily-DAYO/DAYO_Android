package com.daily.dayo.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.BR
import com.daily.dayo.R
import com.daily.dayo.databinding.ItemMainPostBinding
import com.daily.dayo.home.model.Post

class HomeDayoPickAdapater :
    ListAdapter<Post, HomeDayoPickAdapater.HomeDayoPickViewHolder>(feedDiffUtil){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HomeDayoPickViewHolder (
        ItemMainPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(
        holder: HomeDayoPickViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class HomeDayoPickViewHolder(private val binding: ItemMainPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            setBindingSetVariable()
            setRootClickListener()
        }
        private fun setBindingSetVariable() {
            with(binding) {
                setVariable(BR.post, post)
                executePendingBindings()
            }
        }
        private fun setRootClickListener() {
            binding.root.setOnClickListener {
                it.findNavController().navigate(R.id.action_homeFragment_to_postFragment)
            }
        }
    }
    companion object {
        private val feedDiffUtil = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post) =
                oldItem.data == newItem.data

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem == newItem
        }
    }
}