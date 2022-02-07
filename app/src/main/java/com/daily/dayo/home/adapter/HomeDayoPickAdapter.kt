package com.daily.dayo.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.Navigation
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.daily.dayo.BR
import com.daily.dayo.databinding.ItemMainPostBinding
import com.daily.dayo.home.HomeFragmentDirections
import com.daily.dayo.home.model.PostContent

class HomeDayoPickAdapter(val rankingShowing:Boolean) : ListAdapter<PostContent, HomeDayoPickAdapter.HomeDayoPickViewHolder>(diffCallback){
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PostContent>() {
            override fun areItemsTheSame(oldItem: PostContent, newItem: PostContent) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PostContent, newItem: PostContent): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    interface OnItemClickListener{
        fun likePostClick(btn: ImageButton, data: PostContent, pos: Int)
    }
    private var listener: OnItemClickListener?= null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : HomeDayoPickViewHolder {
        // ViewHolder 객체를 생성 후 Return
        return HomeDayoPickViewHolder(ItemMainPostBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )
    }

    override fun onBindViewHolder(holder: HomeDayoPickViewHolder, position: Int) {
        // ViewHolder가 재활용될 때 사용되는 메소드
        // ViewHolder에 Data Binding
        val item = getItem(position)
        holder.bind(item, position)
    }

    override fun submitList(list: MutableList<PostContent>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    // Item View를 저장하는 Class
    // 생성된 ViewHolder에 값을 지정
    inner class HomeDayoPickViewHolder(private val binding: ItemMainPostBinding): RecyclerView.ViewHolder(binding.root) {
        // ViewHolder에 필요한 Data들
        var postImg = binding.imgMainPost
        var rankingNumber = binding.tvPostRankingNumber
        var userThumbnailImg = binding.imgMainPostUserProfile

        fun bind(postContent: PostContent, currentPosition: Int) {
            if(currentPosition > 3 || !rankingShowing){
                binding.layoutPostRankingNumber.visibility = View.INVISIBLE
            } else {
                binding.layoutPostRankingNumber.visibility = View.VISIBLE
                rankingNumber.text = (currentPosition+1).toString()
            }
            Glide.with(postImg.context)
                .load("http://117.17.198.45:8080/images/" + postContent.thumbnailImage)
                .centerCrop()
                .into(postImg)
            Glide.with(userThumbnailImg.context)
                .load("http://117.17.198.45:8080/images/" + postContent.userProfileImage)
                .centerCrop()
                .into(userThumbnailImg)

            setBindingSetVariable(postContent)
            setRootClickListener(postContent.id, postContent.nickname)
            setNicknameClickListener(postContent.memberId)

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION) {
                binding.btnMainPostLike.setOnClickListener {
                    listener?.likePostClick(binding.btnMainPostLike, postContent, pos)
                }
            }
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

        private fun setNicknameClickListener(memberId:String){
            binding.tvMainPostUserNickname.setOnClickListener {
                Navigation.findNavController(it).navigate(HomeFragmentDirections.actionHomeFragmentToOtherProfileFragment(memberId))
            }
        }
    }
}