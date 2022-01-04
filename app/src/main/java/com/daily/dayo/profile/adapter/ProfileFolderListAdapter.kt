package com.daily.dayo.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.databinding.ItemProfileFolderBinding
import com.daily.dayo.profile.model.Folder

class ProfileFolderListAdapter : RecyclerView.Adapter<ProfileFolderListAdapter.ProfileFolderListViewHolder>(){

    companion object{
        private val diffCallback = object : DiffUtil.ItemCallback<Folder>() {
            override fun areItemsTheSame(oldItem: Folder, newItem: Folder) =
                oldItem.folderId == newItem.folderId

            override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<Folder>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ProfileFolderListViewHolder {
        return ProfileFolderListViewHolder(
            ItemProfileFolderBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )
    }

    override fun onBindViewHolder(holder: ProfileFolderListViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ProfileFolderListViewHolder(private val binding: ItemProfileFolderBinding): RecyclerView.ViewHolder(binding.root) {

        val thumbnailImg = binding.btnProfileFolderItem
        val folderPostCnt = binding.tvProfileFolderPostCount
        val folderName = binding.tvProfileFolderName
        val folderSubheading = binding.tvProfileFolderSubheading

        fun bind(folder: Folder) {
            Glide.with(thumbnailImg.context)
                .load("http://www.endlesscreation.kr:8080/images/" + folder.thumbnailImage)
                .into(thumbnailImg)
            folderPostCnt.text = folder.postCount.toString()
            folderName.text = folder.name
            folderSubheading.text = folder.subheading
        }
    }


}