package com.daily.dayo.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.GlideApp
import com.daily.dayo.databinding.ItemFollowBinding
import com.daily.dayo.domain.model.Follow
import com.daily.dayo.presentation.fragment.mypage.follow.FollowFragmentDirections

class FollowListAdapter : RecyclerView.Adapter<FollowListAdapter.FollowListViewHolder>() {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Follow>() {
            override fun areItemsTheSame(oldItem: Follow, newItem: Follow) =
                oldItem.memberId == newItem.memberId

            override fun areContentsTheSame(oldItem: Follow, newItem: Follow): Boolean =
                oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<Follow>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowListViewHolder {
        return FollowListViewHolder(
            ItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FollowListViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListener {
        fun onItemClick(checkbox: CheckBox, follow: Follow, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class FollowListViewHolder(private val binding: ItemFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(follow: Follow) {
            binding.follow = follow
            binding.isMine = follow.memberId == DayoApplication.preferences.getCurrentUser().memberId

            GlideApp.with(binding.imgFollowUserProfile.context)
                .load("http://117.17.198.45:8080/images/" + follow.profileImg)
                .into(binding.imgFollowUserProfile)

            setRootClickListener(follow.memberId)
            setFollowButtonClickListener(follow)
        }

        private fun setRootClickListener(memberId: String) {
            binding.root.setOnClickListener {
                Navigation.findNavController(it).navigate(FollowFragmentDirections.actionFollowFragmentToProfileFragment(memberId = memberId))
            }
        }

        private fun setFollowButtonClickListener(follow: Follow) {
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.btnFollowUserFollow.setOnClickListener {
                    listener?.onItemClick(binding.btnFollowUserFollow, follow, pos)
                }
            }
        }
    }

}