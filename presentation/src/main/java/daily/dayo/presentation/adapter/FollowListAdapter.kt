package daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import daily.dayo.domain.model.MyFollower
import daily.dayo.domain.model.User
import daily.dayo.presentation.R
import daily.dayo.presentation.common.GlideLoadUtil.FOLLOW_USER_THUMBNAIL_SIZE
import daily.dayo.presentation.common.GlideLoadUtil.loadImageView
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.ItemFollowBinding
import daily.dayo.presentation.fragment.mypage.follow.FollowFragmentDirections

class FollowListAdapter(private val requestManager: RequestManager, private val userInfo: User) :
    RecyclerView.Adapter<FollowListAdapter.FollowListViewHolder>() {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<MyFollower>() {
            override fun areItemsTheSame(oldItem: MyFollower, newItem: MyFollower) =
                oldItem.memberId == newItem.memberId

            override fun areContentsTheSame(oldItem: MyFollower, newItem: MyFollower): Boolean =
                oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<MyFollower>) = differ.submitList(list)

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
        fun onItemClick(button: Button, follow: MyFollower, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class FollowListViewHolder(private val binding: ItemFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(follow: MyFollower) {
            binding.follow = follow
            binding.isMine =
                follow.memberId == userInfo.memberId
            loadImageView(
                requestManager = requestManager,
                width = FOLLOW_USER_THUMBNAIL_SIZE,
                height = FOLLOW_USER_THUMBNAIL_SIZE,
                imgName = follow.profileImg ?: "",
                imgView = binding.imgFollowUserProfile
            )

            setRootClickListener(follow.memberId)
            setFollowButtonClickListener(follow)
        }

        private fun setRootClickListener(memberId: String) {
            binding.root.setOnDebounceClickListener {
                Navigation.findNavController(it).navigateSafe(
                    currentDestinationId = R.id.FollowFragment,
                    action = R.id.action_followFragment_to_profileFragment,
                    args = FollowFragmentDirections.actionFollowFragmentToProfileFragment(memberId = memberId).arguments
                )
            }
        }

        private fun setFollowButtonClickListener(follow: MyFollower) {
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.btnFollowUserFollow.setOnDebounceClickListener {
                    listener?.onItemClick(binding.btnFollowUserFollow, follow, pos)
                }
            }
        }
    }

}