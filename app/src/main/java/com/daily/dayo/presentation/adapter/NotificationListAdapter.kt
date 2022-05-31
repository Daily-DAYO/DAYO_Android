package com.daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.databinding.ItemNotificationBinding
import com.daily.dayo.domain.model.Notification

class NotificationListAdapter : ListAdapter<Notification, NotificationListAdapter.NotificationListViewHolder>(
    diffCallback
){
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                oldItem == newItem
        }
    }

    interface OnItemClickListener{
        fun notificationItemClick(pos: Int)
    }

    private var listener: OnItemClickListener?= null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        return NotificationListViewHolder(
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )
    }

    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
    }

    override fun submitList(list: MutableList<Notification>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class NotificationListViewHolder(private val binding: ItemNotificationBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification, position: Int) {
            binding.tvNotificationTitle.text = "content.title"
            binding.tvNotificationDescription.text = "content.description"
            binding.tvNotificationTime.text = "content.time"
        }
    }
}