package com.daily.dayo.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.databinding.ItemNotificationBinding
import com.daily.dayo.feed.model.FeedContent
import com.daily.dayo.notification.model.NotificationContent

class NotificationListAdapter : ListAdapter<NotificationContent, NotificationListAdapter.NotificationListViewHolder>(diffCallback){
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<NotificationContent>() {
            override fun areItemsTheSame(oldItem: NotificationContent, newItem: NotificationContent) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: NotificationContent, newItem: NotificationContent): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    interface OnItemClickListener{
        fun notificationItemClick(pos: Int)
    }
    private var listener: OnItemClickListener?= null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListAdapter.NotificationListViewHolder {
        return NotificationListViewHolder(
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )
    }

    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
    }

    override fun submitList(list: MutableList<NotificationContent>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class NotificationListViewHolder(private val binding: ItemNotificationBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(content: NotificationContent, currentPosition: Int) {
            binding.tvNotificationTitle.text = "content.title"
            binding.tvNotificationDescription.text = "content.description"
            binding.tvNotificationTime.text = "content.time"
        }
    }
}