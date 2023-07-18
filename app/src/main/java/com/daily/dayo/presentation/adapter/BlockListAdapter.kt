package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil.BLOCK_USER_THUMBNAIL_SIZE
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.ItemBlockBinding
import com.daily.dayo.domain.model.BlockUser

class BlockListAdapter(private val requestManager: RequestManager) :
    RecyclerView.Adapter<BlockListAdapter.BlockListViewHolder>() {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<BlockUser>() {
            override fun areItemsTheSame(oldItem: BlockUser, newItem: BlockUser) =
                oldItem.memberId == newItem.memberId

            override fun areContentsTheSame(oldItem: BlockUser, newItem: BlockUser): Boolean =
                oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<BlockUser>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockListViewHolder {
        return BlockListViewHolder(
            ItemBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BlockListViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListener {
        fun onItemClick(checkbox: CheckBox, blockUser: BlockUser, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class BlockListViewHolder(private val binding: ItemBlockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(blockUser: BlockUser) {
            binding.blockUser = blockUser.nickname
            loadImageView(
                requestManager = requestManager,
                width = BLOCK_USER_THUMBNAIL_SIZE,
                height = BLOCK_USER_THUMBNAIL_SIZE,
                imgName = blockUser.profileImg ?: "",
                imgView = binding.imgBlockUserProfile
            )
            setUnblockButtonClickListener(blockUser)
        }

        private fun setUnblockButtonClickListener(blockUser: BlockUser) {
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.btnBlockUserCancel.setOnDebounceClickListener {
                    listener?.onItemClick(binding.btnBlockUserCancel, blockUser, pos)
                }
            }
        }
    }
}
