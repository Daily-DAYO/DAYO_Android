package com.daily.dayo.presentation.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.ItemFolderListBinding
import daily.dayo.domain.model.Folder

class WriteFolderAdapter(
    private val onFolderClicked: (Folder) -> Unit,
    private val selectedFolderId: String
) : ListAdapter<Folder, WriteFolderAdapter.WriteFolderViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Folder>() {
            override fun areItemsTheSame(oldItem: Folder, newItem: Folder) =
                oldItem.folderId == newItem.folderId

            override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean =
                oldItem == newItem
        }
    }

    override fun submitList(list: List<Folder>?) {
        super.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WriteFolderViewHolder {
        return WriteFolderViewHolder(
            ItemFolderListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
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
        fun bind(folder: Folder) {
            binding.folder = folder
            binding.isChangeEnable = false
            if (selectedFolderId != "" && selectedFolderId.toInt() == folder.folderId)
                binding.tvFolderName.setTypeface(null, Typeface.BOLD)
            else binding.tvFolderName.setTypeface(null, Typeface.NORMAL)

            binding.root.setOnDebounceClickListener {
                onFolderClicked(folder)
            }
        }
    }
}
