package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daily.dayo.databinding.ItemFolderPostBinding
import com.daily.dayo.domain.model.FolderPost
import com.daily.dayo.presentation.fragment.mypage.folder.FolderFragmentDirections

class FolderPostListAdapter: RecyclerView.Adapter<FolderPostListAdapter.FolderPostListViewHolder>(){

    companion object{
        private val diffCallback = object : DiffUtil.ItemCallback<FolderPost>() {
            override fun areItemsTheSame(oldItem: FolderPost, newItem: FolderPost) =
                oldItem === newItem

            override fun areContentsTheSame(oldItem: FolderPost, newItem: FolderPost): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<FolderPost>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : FolderPostListViewHolder {
        return FolderPostListViewHolder(
            ItemFolderPostBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )
    }

    override fun onBindViewHolder(holder: FolderPostListViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class FolderPostListViewHolder(private val binding: ItemFolderPostBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(folderPost: FolderPost) {
            Glide.with(binding.imgFolderPost.context)
                .load("http://117.17.198.45:8080/images/" + folderPost.thumbnailImage)
                .into(binding.imgFolderPost)

            binding.root.setOnClickListener {
                Navigation.findNavController(it).navigate(FolderFragmentDirections.actionFolderFragmentToPostFragment(folderPost.postId))
            }
        }
    }


}