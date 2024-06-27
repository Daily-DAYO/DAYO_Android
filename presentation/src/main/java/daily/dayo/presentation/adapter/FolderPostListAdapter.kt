package daily.dayo.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import daily.dayo.domain.model.FolderPost
import daily.dayo.presentation.R
import daily.dayo.presentation.common.GlideLoadUtil.loadImageView
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.ItemFolderPostBinding
import daily.dayo.presentation.fragment.mypage.folder.FolderFragmentDirections
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderPostListAdapter(private val requestManager: RequestManager) :
    PagingDataAdapter<FolderPost, FolderPostListAdapter.FolderPostListViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<FolderPost>() {
            override fun areItemsTheSame(oldItem: FolderPost, newItem: FolderPost) =
                oldItem.postId == newItem.postId

            override fun areContentsTheSame(oldItem: FolderPost, newItem: FolderPost): Boolean =
                oldItem == newItem
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

            CoroutineScope(Dispatchers.Main).launch {
                loadImageView(
                    requestManager = requestManager,
                    width = layoutParams.width,
                    height = layoutParams.width,
                    imgName = folderPost?.thumbnailImage ?: "",
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
                Navigation.findNavController(it)
                    .navigateSafe(
                        currentDestinationId = R.id.FolderFragment,
                        action = R.id.action_folderFragment_to_postFragment,
                        args = FolderFragmentDirections.actionFolderFragmentToPostFragment(folderPost!!.postId).arguments
                    )
            }
        }
    }
}