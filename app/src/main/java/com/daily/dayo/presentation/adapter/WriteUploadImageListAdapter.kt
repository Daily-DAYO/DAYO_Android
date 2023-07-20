package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.ItemWritePostUploadImageBinding
import com.daily.dayo.databinding.ItemWritePostUploadImageMenuBinding

class WriteUploadImageListAdapter(
    private val requestManager: RequestManager,
    private val postId: Int
) : ListAdapter<String, RecyclerView.ViewHolder>(diffCallback) {
    companion object {
        private const val MENU_ITEM_VIEW_TYPE = 1
        private const val IMAGE_ITEM_VIEW_TYPE = 2

        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem
        }
    }

    interface OnItemClickListener {
        fun deleteUploadImageClick(pos: Int)
        fun addUploadImageClick(pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && getItem(position) == "") MENU_ITEM_VIEW_TYPE else IMAGE_ITEM_VIEW_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WriteUploadImageMenuViewHolder -> {
                holder.bind()
            }
            is WriteUploadImageListViewHolder -> {
                holder.bind(getItem(position))
            }
            else -> {}
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            MENU_ITEM_VIEW_TYPE -> {
                WriteUploadImageMenuViewHolder(
                    ItemWritePostUploadImageMenuBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                WriteUploadImageListViewHolder(
                    ItemWritePostUploadImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun submitList(list: List<String>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class WriteUploadImageListViewHolder(private val binding: ItemWritePostUploadImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            with(binding.imgUpload) {
                clipToOutline = true
                requestManager.load(item).centerCrop().into(this)
            }

            with(binding.btnImgUploadDelete) {
                isClickable = postId == 0
                visibility = if (postId == 0) View.VISIBLE else View.INVISIBLE
                if (postId == 0) {
                    setOnDebounceClickListener {
                        listener?.deleteUploadImageClick(pos = bindingAdapterPosition)
                    }
                }
            }
        }
    }

    inner class WriteUploadImageMenuViewHolder(private val binding: ItemWritePostUploadImageMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnDebounceClickListener {
                listener?.addUploadImageClick(pos = bindingAdapterPosition)
            }
        }
    }
}