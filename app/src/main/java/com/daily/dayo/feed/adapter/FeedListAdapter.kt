package com.daily.dayo.feed.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.SharedManager
import com.daily.dayo.databinding.ItemFeedPostBinding
import com.daily.dayo.feed.FeedFragmentDirections
import com.daily.dayo.feed.model.FeedContent
import com.daily.dayo.post.model.PostCommentContent
import com.daily.dayo.util.TimeChangerUtil
import com.google.android.material.chip.Chip

class FeedListAdapter : ListAdapter<FeedContent, FeedListAdapter.FeedListViewHolder>(diffCallback){
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<FeedContent>() {
            override fun areItemsTheSame(oldItem: FeedContent, newItem: FeedContent) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FeedContent, newItem: FeedContent): Boolean =
                oldItem.hashCode() == newItem.hashCode()
        }
    }

    interface OnItemClickListener{
        fun likePostClick(btn: ImageButton, data: FeedContent, pos: Int)
        fun bookmarkPostClick(btn: ImageButton, data: FeedContent, pos: Int)
    }

    private var listener: OnItemClickListener?= null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : FeedListViewHolder {
        return FeedListViewHolder(
            ItemFeedPostBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        )
    }

    override fun onBindViewHolder(holder: FeedListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
    }

    override fun submitList(list: MutableList<FeedContent>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    inner class FeedListViewHolder(private val binding: ItemFeedPostBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(feedContent: FeedContent, currentPosition: Int) {
            binding.feedContent = feedContent
            binding.feedContentCategory = when(feedContent.category) {
                "SCHEDULER" -> "스케줄러"
                "STUDY_PLANNER" -> "스터디 플래너"
                "GOOD_NOTE" -> "굿노트"
                "POCKET_BOOK" -> "포켓북"
                "SIX_DIARY" -> "6공 다이어리"
                "ETC" -> "기타"
                else -> ""
            }
            binding.feedContentCreateTime = TimeChangerUtil.timeChange(binding.tvFeedPostTime.context, feedContent.localDateTime)
            Glide.with(binding.imgFeedPostUserProfile.context)
                .load("http://117.17.198.45:8080/images/" + feedContent.userProfileImage)
                .centerCrop()
                .into(binding.imgFeedPostUserProfile)
            Glide.with(binding.imgFeedPost.context)
                .load("http://117.17.198.45:8080/images/" + feedContent.thumbnailImage)
                .centerCrop()
                .into(binding.imgFeedPost)
            setOnUserProfileClickListener(feedContent.memberId)
            setOnPostClickListener(feedContent.id, feedContent.nickname)

            // 댓글
            if(feedContent.commentCount > 2) {
                setCommentList(feedContent.comments.subList(0,2))
                binding.tvFeedPostMoreComment.visibility = View.VISIBLE
            }
            else{
                setCommentList(feedContent.comments)
                binding.tvFeedPostMoreComment.visibility = View.GONE
            }

            // 해시태그
            if(feedContent.hashtags.isNotEmpty()) {
                binding.layoutFeedPostTagList.visibility = View.VISIBLE
                setTagList(feedContent.hashtags)
            }
            else{
                binding.layoutFeedPostTagList.visibility = View.GONE
            }

            // 좋아요
            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION) {
                binding.btnFeedPostLike.setOnClickListener {
                    listener?.likePostClick(binding.btnFeedPostLike, feedContent, pos)
                }
            }

            // 북마크
            if(pos!= RecyclerView.NO_POSITION) {
                binding.btnFeedPostBookmark.setOnClickListener {
                    listener?.bookmarkPostClick(binding.btnFeedPostBookmark, feedContent, pos)
                }
            }
        }

        private fun setTagList(tagList : List<String>) {
            binding.chipgroupFeedPostTagList.removeAllViews()
            if(!tagList.isNullOrEmpty()){
                (tagList.indices).mapNotNull { index ->
                    val chip = LayoutInflater.from(binding.chipgroupFeedPostTagList.context).inflate(R.layout.item_post_tag, null) as Chip
                    val layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
                    with(chip) {
                        chipBackgroundColor =
                            ColorStateList(
                                arrayOf(intArrayOf(-android.R.attr.state_pressed), intArrayOf(android.R.attr.state_pressed)),
                                intArrayOf(resources.getColor(R.color.gray_6_F6F6F6, context?.theme), resources.getColor(
                                    R.color.primary_green_23C882, context?.theme))
                            )

                        setTextColor(
                            ColorStateList(
                                arrayOf(intArrayOf(-android.R.attr.state_pressed), intArrayOf(android.R.attr.state_pressed)),
                                intArrayOf(resources.getColor(R.color.gray_1_313131,context?.theme), resources.getColor(
                                    R.color.white_FFFFFF, context?.theme))
                            )
                        )
                        text = "# ${tagList[index].trim()}"
                    }
                    binding.chipgroupFeedPostTagList.addView(chip, layoutParams)
                }
            }
        }

        private fun setCommentList(comments:List<PostCommentContent>){
            val feedCommentAdapter = FeedCommentAdapter()
            binding.rvFeedPostComment.adapter = feedCommentAdapter
            feedCommentAdapter.submitList(comments?.toMutableList())
        }

        private fun setOnUserProfileClickListener(postMemberId:String){
            binding.imgFeedPostUserProfile.setOnClickListener {
                if (postMemberId == SharedManager(DayoApplication.applicationContext()).getCurrentUser().memberId) {
                    Navigation.findNavController(it).navigate(FeedFragmentDirections.actionFeedFragmentToMyProfileFragment())
                }
                else{
                    Navigation.findNavController(it).navigate(FeedFragmentDirections.actionFeedFragmentToOtherProfileFragment(postMemberId))
                }
            }
            binding.tvFeedPostUserNickname.setOnClickListener {
                if (postMemberId == SharedManager(DayoApplication.applicationContext()).getCurrentUser().memberId) {
                    Navigation.findNavController(it).navigate(FeedFragmentDirections.actionFeedFragmentToMyProfileFragment())
                }
                else{
                    Navigation.findNavController(it).navigate(FeedFragmentDirections.actionFeedFragmentToOtherProfileFragment(postMemberId))
                }
            }
        }

        private fun setOnPostClickListener(postId:Int, nickname:String){
            binding.tvFeedPostContent.setOnClickListener{
                Navigation.findNavController(it).navigate(FeedFragmentDirections.actionFeedFragmentToPostFragment(postId,nickname))
            }
            binding.tvFeedPostMoreComment.setOnClickListener{
                Navigation.findNavController(it).navigate(FeedFragmentDirections.actionFeedFragmentToPostFragment(postId,nickname))
            }
        }
    }
}