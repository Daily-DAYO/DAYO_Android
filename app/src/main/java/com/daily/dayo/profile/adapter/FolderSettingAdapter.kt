package com.daily.dayo.profile.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.databinding.ItemFolderListBinding
import com.daily.dayo.profile.model.Folder
import com.daily.dayo.profile.model.FolderOrder
import com.daily.dayo.util.ItemTouchHelperCallback
import java.util.*

class FolderSettingAdapter(val isChange:Boolean) :
    RecyclerView.Adapter<FolderSettingAdapter.FolderSettingViewHolder>(),
    ItemTouchHelperCallback.OnItemMoveListener{

    private lateinit var dragListener: OnStartDragListener
    lateinit var folderOrderList: MutableList<FolderOrder>

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Folder>() {
            override fun areItemsTheSame(oldItem: Folder, newItem: Folder) =
                oldItem.folderId == newItem.folderId

            override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<Folder>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderSettingViewHolder {
        return FolderSettingViewHolder(ItemFolderListBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: FolderSettingViewHolder, position: Int) {
        val item = differ.currentList[position]
        if(isChange) {
            holder.changeOrderImg.visibility = View.VISIBLE
            holder.itemView.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    dragListener.onStartDrag(holder)
                }
                return@setOnTouchListener false
            }
        } else {
            holder.changeOrderImg.visibility = View.GONE
        }
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    fun startDrag(listener: OnStartDragListener) {
        this.dragListener = listener
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(folderOrderList, fromPosition, toPosition)
        folderOrderList[toPosition].orderIndex = folderOrderList[fromPosition].orderIndex.also { folderOrderList[fromPosition].orderIndex = folderOrderList[toPosition].orderIndex }
        notifyItemMoved(fromPosition, toPosition)
    }

    class FolderSettingViewHolder(binding: ItemFolderListBinding) : RecyclerView.ViewHolder(binding.root) {
        val changeOrderImg = binding.imgFolderChangeOrder
        val folderName = binding.tvFolderName
        val folderPostCnt = binding.tvFolderPostCount
        fun bind(folder: Folder) {
            folderName.text = folder.name
            folderPostCnt.text = folder.postCount.toString()
        }
    }

}
