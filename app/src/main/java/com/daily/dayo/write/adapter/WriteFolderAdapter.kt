package com.daily.dayo.write.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.databinding.ItemFolderListBinding
import com.daily.dayo.profile.model.Folder

class WriteFolderAdapter(
    private val onFolderClicked: (Folder) -> Unit,
    private val selectedFolderId:String
): ListAdapter<Folder, WriteFolderAdapter.WriteFolderViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Folder>() {
            override fun areItemsTheSame(oldItem: Folder, newItem: Folder) =
                oldItem === newItem

            override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun submitList(list: List<Folder>?) {
        super.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WriteFolderViewHolder {
        return WriteFolderViewHolder(
            ItemFolderListBinding.inflate(LayoutInflater.from(parent.context),parent, false),
            onFolderClicked
        )
    }

    override fun onBindViewHolder(holder: WriteFolderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WriteFolderViewHolder(
        private val binding: ItemFolderListBinding,
        val onFolderClicked: (Folder) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        val folderName = binding.tvFolderName
        val folderPostCnt = binding.tvFolderPostCount
        val changeOrderImg = binding.imgFolderChangeOrder
        fun bind(folder: Folder) {
            changeOrderImg.visibility = View.GONE
            folderName.text = folder.name
            folderPostCnt.text = folder.postCount.toString()
            if(selectedFolderId!="" && selectedFolderId.toInt() == folder.folderId)
                binding.tvFolderName.setTypeface(null, Typeface.BOLD)
            else{
                binding.tvFolderName.setTypeface(null, Typeface.NORMAL)
            }

            binding.root.setOnClickListener {
                onFolderClicked(folder)
            }
       }
    }
}
