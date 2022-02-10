package com.daily.dayo.write.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.databinding.ItemFolderListBinding
import com.daily.dayo.profile.model.Folder

class WriteFolderAdapter: ListAdapter<Folder, WriteFolderAdapter.WriteFolderViewHolder>(diffCallback) {

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
        return WriteFolderViewHolder(ItemFolderListBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: WriteFolderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnItemClickListener{
        fun onItemClick(v: View, folder: Folder, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    inner class WriteFolderViewHolder(binding: ItemFolderListBinding) : RecyclerView.ViewHolder(binding.root) {
        val folderName = binding.tvFolderName
        val folderPostCnt = binding.tvFolderPostCount
        val changeOrderImg = binding.imgFolderChangeOrder
        fun bind(folder: Folder) {
            changeOrderImg.visibility = View.GONE
            folderName.text = folder.name
            folderPostCnt.text = folder.postCount.toString()

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,folder,pos)
                }
            }
       }
    }
}
