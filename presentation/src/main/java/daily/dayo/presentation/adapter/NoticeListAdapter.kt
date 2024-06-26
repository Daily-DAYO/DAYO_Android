package daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import daily.dayo.domain.model.Notice
import daily.dayo.presentation.R
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.ItemNoticePostBinding
import daily.dayo.presentation.fragment.setting.notice.NoticeListFragmentDirections

class NoticeListAdapter :
    PagingDataAdapter<Notice, NoticeListAdapter.NoticeListViewHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Notice>() {
            override fun areItemsTheSame(oldItem: Notice, newItem: Notice): Boolean {
                return oldItem.noticeId == newItem.noticeId
            }

            override fun areContentsTheSame(oldItem: Notice, newItem: Notice): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeListViewHolder =
        NoticeListViewHolder(
            ItemNoticePostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: NoticeListAdapter.NoticeListViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class NoticeListViewHolder(private val binding: ItemNoticePostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notice: Notice) {
            binding.notice = notice
            binding.root.setOnDebounceClickListener {
                Navigation.findNavController(it).navigateSafe(
                    currentDestinationId = R.id.NoticeListFragment,
                    action = R.id.action_noticeListFragment_to_noticeDetailFragment,
                    args = NoticeListFragmentDirections.actionNoticeListFragmentToNoticeDetailFragment(notice).arguments
                )
            }
        }
    }
}