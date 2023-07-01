package com.daily.dayo.presentation.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.data.di.IoDispatcher
import com.daily.dayo.data.di.MainDispatcher
import com.daily.dayo.databinding.ItemFolderPostBinding
import com.daily.dayo.domain.model.FolderPost
import com.daily.dayo.presentation.fragment.mypage.folder.FolderFragmentDirections
import kotlinx.coroutines.*

class FolderPostListAdapter(
    private val requestManager: RequestManager,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PagingDataAdapter<FolderPost, FolderPostListAdapter.FolderPostListViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<FolderPost>() {
            override fun areItemsTheSame(oldItem: FolderPost, newItem: FolderPost) =
                oldItem === newItem

            override fun areContentsTheSame(oldItem: FolderPost, newItem: FolderPost): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderPostListViewHolder {
        return FolderPostListViewHolder(
            ItemFolderPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FolderPostListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FolderPostListViewHolder(private val binding: ItemFolderPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(folderPost: FolderPost?) {
            val layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.MarginLayoutParams.MATCH_PARENT,
                ViewGroup.MarginLayoutParams.MATCH_PARENT
            )

            binding.layoutFolderPostContentsShimmer.startShimmer()
            binding.layoutFolderPostContentsShimmer.visibility = View.VISIBLE
            binding.imgFolderPost.visibility = View.INVISIBLE

            CoroutineScope(mainDispatcher).launch {
                val folderPostImage: Bitmap?
                if (folderPost?.preLoadThumbnail == null) {
                    folderPostImage = withContext(ioDispatcher) {
                        loadImageBackground(
                            requestManager = requestManager,
                            width = layoutParams.width,
                            height = layoutParams.width,
                            imgName = folderPost?.thumbnailImage ?: ""
                        )
                    }
                } else {
                    folderPostImage = folderPost.preLoadThumbnail!!
                    folderPost.preLoadThumbnail = null
                }
                loadImageView(
                    requestManager = requestManager,
                    width = layoutParams.width,
                    height = layoutParams.width,
                    img = folderPostImage,
                    imgView = binding.imgFolderPost
                )
            }.invokeOnCompletion { throwable ->
                when (throwable) {
                    is CancellationException -> Log.e("Image Loading", "CANCELLED")
                    null -> {
                        binding.layoutFolderPostContentsShimmer.stopShimmer()
                        binding.layoutFolderPostContentsShimmer.visibility = View.GONE
                        binding.imgFolderPost.visibility = View.VISIBLE
                    }
                }
            }

            binding.root.setOnDebounceClickListener {
                Navigation.findNavController(it).navigate(FolderFragmentDirections.actionFolderFragmentToPostFragment(folderPost!!.postId))
            }
        }
    }
}