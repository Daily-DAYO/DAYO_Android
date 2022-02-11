package com.daily.dayo.post

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentPostBinding
import com.daily.dayo.post.adapter.PostCommentAdapter
import com.daily.dayo.post.adapter.PostImageSliderAdapter
import com.daily.dayo.post.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import android.widget.ImageView
import androidx.core.content.ContextCompat
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.daily.dayo.DayoApplication
import com.daily.dayo.SharedManager
import com.daily.dayo.post.model.PostCommentContent
import com.daily.dayo.post.model.RequestCreatePostComment
import com.daily.dayo.post.model.RequestLikePost
import com.google.android.material.chip.Chip
import com.daily.dayo.util.*
import kotlinx.coroutines.CancellationException

@AndroidEntryPoint
class PostFragment : Fragment() {
    private var binding by autoCleared<FragmentPostBinding>()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val args by navArgs<PostFragmentArgs>()
    private lateinit var mAlertDialog: AlertDialog

    private lateinit var postCommentAdapter : PostCommentAdapter
    private lateinit var postImageSliderAdapter: PostImageSliderAdapter
    private lateinit var indicators : Array<ImageView?>
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
            onShowKeyboard = { keyboardHeight ->
                binding.layoutScrollPost.run {
                    scrollTo(0, binding.layoutScrollPost.bottom + keyboardHeight)
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setCommentListAdapter()
        setImageSlider()
        setPostDetailCollect()
        setPostCommentCollect()
        setPostBookmarkClickListener()
        setCreatePostComment()
        setPostCommentClickListener()
        observePostCommentDeleteStateCallback()
        return binding.root
    }

    override fun onDestroy() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroy()
    }

    private fun setBackButtonClickListener(){
        binding.btnPostBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setCommentListAdapter() {
        postCommentAdapter = PostCommentAdapter()
        binding.rvPostCommentList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPostCommentList.adapter = postCommentAdapter
    }

    private fun setPostOptionClickListener(writerNickname: String) {
        val currentUserNickname = SharedManager(DayoApplication.applicationContext()).getCurrentUser().nickname.toString()
        binding.btnPostOption.setOnClickListener {
            if(writerNickname == currentUserNickname) {
                Navigation.findNavController(it).navigate(PostFragmentDirections.actionPostFragmentToPostOptionMineFragment(args.id))
            } else {
                Navigation.findNavController(it).navigate(PostFragmentDirections.actionPostFragmentToPostOptionFragment(args.id))
            }
        }
    }

    private fun setPostDetailCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.requestPostDetail(args.id)
                postViewModel.postDetail.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { postDetail ->
                                postImageSliderAdapter.submitList(postDetail.images)
                                setupIndicators(postDetail.images.size)
                                postDetail?.let {
                                    with(binding) {
                                        post = postDetail
                                        postCategory = when(postDetail.category) {
                                            getString(R.string.scheduler_eng) -> getString(R.string.scheduler)
                                            getString(R.string.studyplanner_eng) -> getString(R.string.studyplanner)
                                            getString(R.string.digital_eng) -> getString(R.string.digital)
                                            getString(R.string.pocketbook_eng) -> getString(R.string.pocketbook)
                                            getString(R.string.sixHoleDiary_eng) -> getString(R.string.sixHoleDiary)
                                            getString(R.string.etc_eng) -> getString(R.string.etc)
                                            else -> ""
                                        }
                                        postCreateTime = TimeChangerUtil.timeChange(requireContext(), postDetail.createDateTime)
                                        setPostLikeClickListener(postDetail.heart)
                                        setTagList(postDetail.hashtags)
                                        setPostOptionClickListener(postDetail.nickname)
                                        Glide.with(requireContext())
                                            .load("http://117.17.198.45:8080/images/" + postDetail.profileImg)
                                            .into(imgPostUserProfile)
                                        executePendingBindings()
                                    }
                                }
                            }
                        }
                        Status.LOADING -> { }
                        Status.ERROR -> { }
                    }
                })
            }
        }
    }
    private fun setPostCommentCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.requestPostComment(args.id)
                postViewModel.postComment.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { postComment ->
                                postCommentAdapter.submitList(postComment.data?.toMutableList())
                                binding.postCommentCount = postComment.count
                            }
                        }
                        Status.LOADING -> { }
                        Status.ERROR -> { }
                    }
                })
            }
        }
    }
    private fun setPostCommentClickListener() {
        postCommentAdapter.setOnItemClickListener(object : PostCommentAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: PostCommentContent, pos: Int) {
                // Item 자체를 클릭하는 경우 나타나는 Event 작성
            }

            override fun DeletePostCommentClick(data: PostCommentContent, pos: Int) {
                val deleteComment = {
                    postViewModel.requestDeletePostComment(data.commentId)
                    refreshPostComment() }

                mAlertDialog = DefaultDialogConfirm.createDialog(requireContext(), R.string.post_comment_delete_alert_message, true, true, null, null,
                    deleteComment,{ this@PostFragment.mAlertDialog.dismiss() } )
                mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                if(!mAlertDialog.isShowing) {
                    mAlertDialog.show()
                    DefaultDialogConfigure.dialogResize(requireContext(), mAlertDialog, 0.7f, 0.19f)
                }
            }
        })
    }
    private fun observePostCommentDeleteStateCallback() {
        postViewModel.postCommentDeleteSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess.getContentIfNotHandled() == true) {
                Toast.makeText(requireContext(), R.string.post_comment_delete_alert_message_success, Toast.LENGTH_SHORT).show()
            } else if(isSuccess.getContentIfNotHandled() == false) {
                Toast.makeText(requireContext(), R.string.post_comment_delete_alert_message_fail, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setTagList(tagList : List<String>) {
        binding.chipgroupPostTagList.removeAllViews()
        if(!tagList.isNullOrEmpty()){
            (0 until tagList.size).mapNotNull { index ->
                val chip = LayoutInflater.from(context).inflate(R.layout.item_post_tag, null) as Chip
                val layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
                with(chip) {
                    chipBackgroundColor =
                        ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_pressed), intArrayOf(android.R.attr.state_pressed)),
                    intArrayOf(resources.getColor(R.color.gray_7_F6F6F6, context?.theme), resources.getColor(R.color.primary_green_23C882, context?.theme)))

                    setTextColor(
                        ColorStateList(arrayOf(intArrayOf(-android.R.attr.state_pressed), intArrayOf(android.R.attr.state_pressed)),
                        intArrayOf(resources.getColor(R.color.gray_1_313131,context?.theme), resources.getColor(R.color.white_FFFFFF, context?.theme)))
                    )
                    ensureAccessibleTouchTarget(42.toPx())
                    text = "# ${tagList[index].trim()}"
                }
                binding.chipgroupPostTagList.addView(chip, layoutParams)
            }
        }
    }

    private fun setImageSlider() {
        postImageSliderAdapter = PostImageSliderAdapter()
        with(binding.vpPostImage) {
            adapter = postImageSliderAdapter
            offscreenPageLimit = 1
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicator(position)
                }
            })
        }
    }

    // TODO: Indicator Setup 코드 개선 필요
    private fun setupIndicators(count: Int) {
        indicators = arrayOfNulls<ImageView>(count)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(16, 8, 16, 8)
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i]!!.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_indicator_inactive)
            )
            indicators[i]!!.layoutParams = params
            binding.viewPostImageIndicators.addView(indicators[i])
        }

        // TODO: addView로 인하여 Indicator가 비정상적으로 많아지는 현상 임시 해결
        if (indicators.size < binding.viewPostImageIndicators.childCount) {
            for(i in binding.viewPostImageIndicators.childCount-1 downTo (indicators.size)) {
                binding.viewPostImageIndicators.removeViewAt(i)
            }
        }
        setCurrentIndicator(0)
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount: Int = binding.viewPostImageIndicators.getChildCount()
        for (i in 0 until childCount) {
            val imageView = binding.viewPostImageIndicators.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_indicator_active)
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_indicator_inactive)
                )
            }
        }
    }

    private fun setPostLikeClickListener(isChecked: Boolean) {
        with(binding.btnPostLike) {
            setOnClickListener {
                if(!isChecked) {
                    setImageDrawable(resources.getDrawable(R.drawable.ic_post_like_checked, context?.theme))
                    postViewModel.requestLikePost(RequestLikePost(args.id))
                } else {
                    setImageDrawable(resources.getDrawable(R.drawable.ic_post_like_default, context?.theme))
                    postViewModel.requestUnlikePost(args.id)
                }.let { it.invokeOnCompletion { throwable ->
                    when (throwable) {
                        is CancellationException -> Log.e("Post Like Click", "CANCELLED")
                        null -> {
                            postViewModel.requestPostDetail(args.id)
                        }
                    }
                } }
            }
        }
    }

    private fun setPostBookmarkClickListener() {
        binding.btnPostBookmark.setOnClickListener {
            if(binding.btnPostBookmark.isSelected) {
                // TODO: Bookmark한 Post인지 아닌지 판단하는 조건 필요
                // TODO : bookmark 서버 연동 필요
                binding.btnPostBookmark.isSelected = false
            } else {
                binding.btnPostBookmark.isSelected = true
            }
        }
    }

    private fun setCreatePostComment() {
        Glide.with(requireContext())
            .load("http://117.17.198.45:8080/images/" + SharedManager(requireContext()).getCurrentUser().profileImg)
            .into(binding.imgPostCommentMyProfile)

        binding.tvPostCommentUpload.setOnClickListener {
            if(!binding.etPostCommentDescription.text.toString().trim().isNullOrEmpty()) {
                val commentDescription = RequestCreatePostComment(contents = binding.etPostCommentDescription.text.toString(), postId = args.id)
                postViewModel.requestCreatePostComment(commentDescription)
                with(binding.etPostCommentDescription) {
                    setText("")
                    clearFocus()
                    HideKeyBoardUtil.hide(requireContext(), this)
                }
                refreshPostComment()
            }
        }
    }
    private fun refreshPostComment() {
        // TODO : Handler 수정 필요
        Handler().postDelayed(
            {with(binding.rvPostCommentList.adapter as PostCommentAdapter) {
                postViewModel.requestPostComment(args.id)
                submitList(postViewModel.postComment.value?.data?.data?.subList(0, postCommentAdapter.itemCount)?.toMutableList())
            }},60
        )
        Handler().postDelayed({afterCreatedScroll()}, 200)

    }
    private fun afterCreatedScroll() {
        binding.layoutScrollPost.fullScroll(View.FOCUS_DOWN)
    }

    fun Int.toPx() : Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}