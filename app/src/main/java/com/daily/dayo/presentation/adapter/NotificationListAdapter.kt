package com.daily.dayo.presentation.adapter

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.common.GlideLoadUtil
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.ItemNotificationBinding
import com.daily.dayo.domain.model.Notification
import com.daily.dayo.domain.model.Topic
import com.daily.dayo.presentation.fragment.notification.NotificationFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationListAdapter(private val requestManager: RequestManager) :
    ListAdapter<Notification, NotificationListAdapter.NotificationListViewHolder>(
        diffCallback
    ) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem.alarmId == newItem.alarmId

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                oldItem == newItem
        }
    }

    interface OnItemClickListener{
        fun notificationItemClick(alarmId: Int, alarmCheck: Boolean, position: Int)
    }

    private var listener: OnItemClickListener?= null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationListViewHolder {
        return NotificationListViewHolder(
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
    }

    override fun submitList(list: List<Notification>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class NotificationListViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification, position: Int) {
            binding.notification = notification

            if(notification.image != null) {
                val layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.MarginLayoutParams.MATCH_PARENT,
                    ViewGroup.MarginLayoutParams.MATCH_PARENT
                )
                CoroutineScope(Dispatchers.Main).launch {
                    val thumbnailImage = withContext(Dispatchers.IO) {
                        GlideLoadUtil.loadImageBackground(
                            requestManager = requestManager,
                            width = 56,
                            height = 56,
                            imgName = notification.image ?: ""
                        )
                    }
                    GlideLoadUtil.loadImageView(
                        requestManager = requestManager,
                        width = layoutParams.width,
                        height = layoutParams.width,
                        img = thumbnailImage,
                        imgView = binding.ivNotificationThumbnail
                    )
                }
            }

            val spanContent = SpannableStringBuilder()
            spanContent.append(notification.nickname)
            spanContent.setSpan(
                object : ClickableSpan() {
                    override fun onClick(v: View) {
                        Navigation.findNavController(v).navigate(
                            NotificationFragmentDirections.actionNotificationFragmentToProfileFragment(
                                memberId = notification.memberId
                            )
                        )
                    }

                    override fun updateDrawState(textPaint: TextPaint) {
                        textPaint.color = ContextCompat.getColor(binding.tvNotificationTitle.context, R.color.gray_1_313131)
                        textPaint.isUnderlineText = false
                        textPaint.typeface = Typeface.DEFAULT_BOLD
                    }
                }, 0, notification.nickname?.length ?: 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spanContent.append(notification.content)
            binding.tvNotificationTitle.movementMethod = LinkMovementMethod.getInstance()
            binding.tvNotificationTitle.text = spanContent

            binding.root.setOnDebounceClickListener {
                // alarm check
                notification.alarmId?.let { alarmId ->
                    listener?.notificationItemClick(
                        alarmId = alarmId,
                        alarmCheck = notification.check!!,
                        position = position
                    )
                }

                // move to alarm content
                when (notification.topic) {
                    Topic.COMMENT, Topic.HEART -> {
                        notification.postId?.let { postId ->
                            Navigation.findNavController(it).navigate(
                                NotificationFragmentDirections.actionNotificationFragmentToPostFragment(
                                    postId = postId
                                )
                            )
                        }
                    }
                    Topic.FOLLOW ->
                        Navigation.findNavController(it).navigate(
                            NotificationFragmentDirections.actionNotificationFragmentToProfileFragment(
                                memberId = notification.memberId
                            )
                        )
                    Topic.NOTICE -> {

                    }
                    else -> {}
                }
            }
        }

    }
}