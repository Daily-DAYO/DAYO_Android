package com.daily.dayo.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.daily.dayo.BR
import com.daily.dayo.databinding.ItemMainPostBinding
import com.daily.dayo.home.HomeFragmentDirections
import com.daily.dayo.home.model.PostContent

class HomeDayoPickAdapter : RecyclerView.Adapter<HomeDayoPickAdapter.HomeDayoPickViewHolder>(){
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PostContent>() {
            override fun areItemsTheSame(oldItem: PostContent, newItem: PostContent) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PostContent, newItem: PostContent): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this,diffCallback)
    fun submitList(list: List<PostContent>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : HomeDayoPickViewHolder {
        // ViewHolder 객체를 생성 후 Return
        return HomeDayoPickViewHolder(ItemMainPostBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: HomeDayoPickViewHolder, position: Int) {
        // ViewHolder가 재활용될 때 사용되는 메소드
        // ViewHolder에 Data Binding
        val item = differ.currentList[position]
        holder.bind(item, position)
    }

    // Item View를 저장하는 Class
    // 생성된 ViewHolder에 값을 지정
    inner class HomeDayoPickViewHolder(private val binding: ItemMainPostBinding): RecyclerView.ViewHolder(binding.root) {
        // ViewHolder에 필요한 Data들
        var postImg = binding.imgMainPost
        var rankingNumber = binding.tvPostRankingNumber
        var userThumbnailImg = binding.imgMainPostUserProfile

        fun bind(postContent: PostContent, currentPosition: Int) {
            rankingNumber.text = (currentPosition+1).toString()
            Glide.with(postImg.context)
                .load("http://www.endlesscreation.kr:8080/images/" + postContent.thumbnailImage)
                .into(postImg)
            Glide.with(userThumbnailImg.context)
                .load(postContent.userProfileImage)
                .into(userThumbnailImg)

            setBindingSetVariable(postContent)
            setRootClickListener(postContent.id, postContent.nickname)
        }
        private fun setBindingSetVariable(postContent: PostContent) {
            with(binding) {
                setVariable(BR.postData, postContent)
                executePendingBindings()
            }
        }
        private fun setRootClickListener(postId: Int, nickname: String) {
            binding.root.setOnClickListener {
                Navigation.findNavController(it).navigate(HomeFragmentDirections.actionHomeFragmentToPostFragment(postId, nickname))
            }
        }
    }
}