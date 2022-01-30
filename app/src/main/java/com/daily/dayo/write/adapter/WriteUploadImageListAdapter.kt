package com.daily.dayo.write.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daily.dayo.databinding.ItemWritePostUploadImageBinding

class WriteUploadImageListAdapter (private val items: ArrayList<Uri>, val context: Context) :
    RecyclerView.Adapter<WriteUploadImageListAdapter.WriteUploadImageListViewHolder>() {
    interface OnItemClickListener{
        fun deleteUploadImageClick(pos: Int)
    }
    private var listener: OnItemClickListener?= null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: WriteUploadImageListViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WriteUploadImageListViewHolder = WriteUploadImageListViewHolder (
        ItemWritePostUploadImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    inner class WriteUploadImageListViewHolder(private val binding: ItemWritePostUploadImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Uri) {
            with(binding.imgUpload) {
                clipToOutline = true
                Glide.with(context)
                    .load(item)
                    .centerCrop()
                    .into(this)
            }

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION){
                binding.btnImgUploadDelete.setOnClickListener {
                    listener?.deleteUploadImageClick(pos)
                }
            }
        }
    }
}