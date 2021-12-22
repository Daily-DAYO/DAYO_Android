package com.daily.dayo.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.*
import com.daily.dayo.BR
import com.daily.dayo.R
import com.daily.dayo.databinding.ItemMainPostBinding
import com.daily.dayo.home.model.Post
import com.daily.dayo.home.model.PostContent

class HomeDayoPickAdapter : RecyclerView.Adapter<HomeDayoPickAdapter.HomeDayoPickViewHolder>(){
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post) =
                oldItem.data == newItem.data

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this,diffCallback)
    fun submitList(list : List<Post>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : HomeDayoPickViewHolder {
        return HomeDayoPickViewHolder(
            ItemMainPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(
        holder: HomeDayoPickViewHolder,
        position: Int
    ) {
        val item = differ.currentList[1].data[position]
        holder.bind(item)
    }

    inner class HomeDayoPickViewHolder(binding: ItemMainPostBinding): RecyclerView.ViewHolder(binding.root) {
        var nickname = binding.tvMainPostUserNickname
        fun bind(postContent: PostContent) {
            nickname.text = postContent.nickname
        //    setBindingSetVariable()
        //    setRootClickListener()
        }
        /*
        private fun setBindingSetVariable(post: Post) {
            with(binding) {
                setVariable(BR.post, post)
                setVariable(BR.postData, post.data)
                executePendingBindings()
            }
        }
        private fun setRootClickListener() {
            binding.root.setOnClickListener {
                it.findNavController().navigate(R.id.action_homeFragment_to_postFragment)
            }
        }
         */
    }
}