package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daily.dayo.databinding.ItemProfileFolderBinding
import com.daily.dayo.domain.model.Folder

class ProfileFolderListAdapter :
    RecyclerView.Adapter<ProfileFolderListAdapter.ProfileFolderListViewHolder>() {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Folder>() {
            override fun areItemsTheSame(oldItem: Folder, newItem: Folder) =
                oldItem === newItem

            override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean =
                oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<Folder>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFolderListViewHolder {
        return ProfileFolderListViewHolder(
            ItemProfileFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProfileFolderListViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, folder: Folder, pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ProfileFolderListViewHolder(private val binding: ItemProfileFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(folder: Folder) {
            binding.folder = folder
            Glide.with(binding.btnProfileFolderItem.context)
                .load("http://117.17.198.45:8080/images/" + folder.thumbnailImage)
                .into(binding.btnProfileFolderItem)

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, folder, pos)
                }
            }
        }
    }
}