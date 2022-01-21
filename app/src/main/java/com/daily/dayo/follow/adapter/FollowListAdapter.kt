package com.daily.dayo.follow.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daily.dayo.DayoApplication
import com.daily.dayo.SharedManager
import com.daily.dayo.databinding.ItemFollowBinding
import com.daily.dayo.follow.FollowFragmentDirections
import com.daily.dayo.profile.model.FollowInfo

class FollowListAdapter : RecyclerView.Adapter<FollowListAdapter.FollowListViewHolder>() {

    companion object{
        private val diffCallback = object : DiffUtil.ItemCallback<FollowInfo>() {
            override fun areItemsTheSame(oldItem: FollowInfo, newItem: FollowInfo) =
                oldItem === newItem

            override fun areContentsTheSame(oldItem: FollowInfo, newItem: FollowInfo): Boolean =
                oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<FollowInfo>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : FollowListViewHolder {
        return FollowListViewHolder(
            ItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )
    }

    override fun onBindViewHolder(holder: FollowListViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class FollowListViewHolder(private val binding: ItemFollowBinding): RecyclerView.ViewHolder(binding.root) {

        val userProfileImg = binding.imgFollowUserProfile
        val userNickname = binding.tvFollowUserNickname
        val userFollowBtn = binding.btnFollowUserFollow

        fun bind(followInfo: FollowInfo) {
            Glide.with(userProfileImg.context)
                .load("http://117.17.198.45:8080/images/" + followInfo.profile)
                .into(userProfileImg)
            userNickname.text = followInfo.nickname
            userFollowBtn.isChecked = followInfo.isAccept

            setRootClickListener(followInfo.memberId)
        }

        private fun setRootClickListener(memberId : String){
            binding.root.setOnClickListener {
                if (memberId == SharedManager(DayoApplication.applicationContext()).getCurrentUser().memberId) {
                    Navigation.findNavController(it).navigate(FollowFragmentDirections.actionFollowFragmentToMyProfileFragment())
                }
                else{
                    Navigation.findNavController(it).navigate(FollowFragmentDirections.actionFollowFragmentToOtherProfileFragment(memberId))
                }
            }
        }
    }

}