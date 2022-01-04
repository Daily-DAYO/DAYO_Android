package com.daily.dayo.write.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daily.dayo.databinding.ItemWritePostUploadImageBinding

class WriteUploadImageListAdapter
    (private val items: ArrayList<Uri>, val context: Context) :
    RecyclerView.Adapter<WriteUploadImageListAdapter.WriteUploadImageListViewHolder>() {
    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: WriteUploadImageListViewHolder, position: Int) {
        val item = items[position]
        holder.image.clipToOutline = true
        Glide.with(context)
            .load(item)
            .centerCrop()
            .into(holder.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WriteUploadImageListViewHolder = WriteUploadImageListViewHolder (
        ItemWritePostUploadImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    inner class WriteUploadImageListViewHolder(private val binding: ItemWritePostUploadImageBinding) : RecyclerView.ViewHolder(binding.root) {
        var image = binding.imgUpload
        fun bind(listener: View.OnClickListener, item: String) {
            binding.root.setOnClickListener(listener)
        }
    }
}