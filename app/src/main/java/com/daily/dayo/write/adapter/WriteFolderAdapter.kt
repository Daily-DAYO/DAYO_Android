package com.daily.dayo.write.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.databinding.ItemFolderListBinding
import com.daily.dayo.profile.model.Folder

class WriteFolderAdapter: RecyclerView.Adapter<WriteFolderAdapter.WriteFolderViewHolder>() {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Folder>() {
            override fun areItemsTheSame(oldItem: Folder, newItem: Folder) =
                oldItem.folderId == newItem.folderId

            override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<Folder>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WriteFolderViewHolder {
        return WriteFolderViewHolder(ItemFolderListBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: WriteFolderViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class WriteFolderViewHolder(binding: ItemFolderListBinding) : RecyclerView.ViewHolder(binding.root) {
        val folderName = binding.tvFolderName
        val folderPostCnt = binding.tvFolderPostCount
        fun bind(folder: Folder) {
            folderName.text = folder.name
            folderPostCnt.text = folder.postCount.toString()
       }
    }
}
