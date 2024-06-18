package daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import daily.dayo.presentation.common.GlideLoadUtil.loadImageView
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.ItemBlockBinding
import daily.dayo.domain.model.UserBlocked

class BlockListAdapter(private val requestManager: RequestManager) :
    RecyclerView.Adapter<BlockListAdapter.BlockListViewHolder>() {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<UserBlocked>() {
            override fun areItemsTheSame(oldItem: UserBlocked, newItem: UserBlocked) =
                oldItem.memberId == newItem.memberId

            override fun areContentsTheSame(oldItem: UserBlocked, newItem: UserBlocked): Boolean =
                oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<UserBlocked>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockListViewHolder {
        return BlockListViewHolder(
            ItemBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: BlockListViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListener {
        fun onItemClick(checkbox: CheckBox, blockUser: UserBlocked, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class BlockListViewHolder(private val binding: ItemBlockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(blockUser: UserBlocked) {
            binding.blockUser = blockUser.nickname
            loadImageView(
                requestManager = requestManager,
                width = binding.imgBlockUserProfile.width,
                height = binding.imgBlockUserProfile.height,
                imgName = blockUser.profileImg ?: "",
                imgView = binding.imgBlockUserProfile
            )
            setUnblockButtonClickListener(blockUser)
        }

        private fun setUnblockButtonClickListener(blockUser: UserBlocked) {
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                binding.btnBlockUserCancel.setOnDebounceClickListener {
                    listener?.onItemClick(binding.btnBlockUserCancel, blockUser, pos)
                }
            }
        }
    }
}
