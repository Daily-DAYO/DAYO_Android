package daily.dayo.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.ItemSearchKeywordRecentBinding

class SearchKeywordRecentAdapter : ListAdapter<String, SearchKeywordRecentAdapter.SearchKeywordRecentViewHolder> (
    diffCallback
) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    interface OnItemClickListener{
        fun onItemClick(v: View, keyword: String, pos : Int)
        fun deleteSearchKeywordRecentClick(keyword: String, pos: Int)
    }
    private var listener: OnItemClickListener?= null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchKeywordRecentViewHolder {
        return SearchKeywordRecentViewHolder(ItemSearchKeywordRecentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SearchKeywordRecentViewHolder, position: Int) {
        holder.bindKeyword(getItem(position))
    }

    override fun submitList(list: MutableList<String>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class SearchKeywordRecentViewHolder(private val binding: ItemSearchKeywordRecentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindKeyword(keyword: String?) {
            binding.tvSearchRecentKeywordItem.text = keyword

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION) {
                itemView.setOnDebounceClickListener {
                    if (keyword != null) {
                        listener?.onItemClick(itemView, keyword, pos)
                    }
                }
                binding.btnSearchRecentKeywordItemDelete.setOnDebounceClickListener{
                    if (keyword != null) {
                        listener?.deleteSearchKeywordRecentClick(keyword, pos)
                    }
                }
            }
        }
    }
}