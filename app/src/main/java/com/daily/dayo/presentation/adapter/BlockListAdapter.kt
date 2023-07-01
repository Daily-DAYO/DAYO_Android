package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.data.di.IoDispatcher
import com.daily.dayo.data.di.MainDispatcher
import com.daily.dayo.databinding.ItemBlockBinding
import com.daily.dayo.domain.model.BlockUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BlockListAdapter(
    private val requestManager: RequestManager,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) :
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
            CoroutineScope(mainDispatcher).launch {
                val profileImgBitmap = withContext(ioDispatcher) {
                    GlideLoadUtil.loadImageBackground(
                        requestManager = requestManager,
                        width = 45,
                        height = 45,
                        imgName = blockUser.profileImg ?: ""
                    )
                }
                GlideLoadUtil.loadImageView(
                    requestManager = requestManager,
                    width = 45,
                    height = 45,
                    img = profileImgBitmap,
                    imgView = binding.imgBlockUserProfile
                )
            }
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
