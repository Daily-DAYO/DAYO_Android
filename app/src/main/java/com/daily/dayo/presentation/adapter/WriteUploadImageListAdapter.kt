package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.databinding.ItemWritePostUploadImageBinding

class WriteUploadImageListAdapter(
    private val items: ArrayList<String>,
    private val requestManager: RequestManager,
    val postId: Int
) :
    RecyclerView.Adapter<WriteUploadImageListAdapter.WriteUploadImageListViewHolder>() {
    interface OnItemClickListener {
        fun deleteUploadImageClick(pos: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: WriteUploadImageListViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WriteUploadImageListViewHolder = WriteUploadImageListViewHolder(
        ItemWritePostUploadImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    inner class WriteUploadImageListViewHolder(private val binding: ItemWritePostUploadImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            with(binding.imgUpload) {
                clipToOutline = true
                requestManager.load(item).centerCrop().into(this)
            }

            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.btnImgUploadDelete.setOnClickListener {
                    listener?.deleteUploadImageClick(pos)
                }
            }
            if (postId != 0) {
                binding.btnImgUploadDelete.visibility = View.INVISIBLE
            }
        }
    }
}