package com.daily.dayo.presentation.fragment.post

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.common.*
import com.daily.dayo.databinding.FragmentPostBinding
import com.daily.dayo.domain.model.Comment
import com.daily.dayo.domain.model.categoryKR
import com.daily.dayo.presentation.adapter.PostCommentAdapter
import com.daily.dayo.presentation.adapter.PostImageSliderAdapter
import com.daily.dayo.presentation.viewmodel.PostViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class PostFragment : Fragment() {
    private var binding by autoCleared<FragmentPostBinding>()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val args by navArgs<PostFragmentArgs>()
    private lateinit var mAlertDialog: AlertDialog
    private lateinit var glideRequestManager: RequestManager

    private lateinit var postCommentAdapter: PostCommentAdapter
    private lateinit var postImageSliderAdapter: PostImageSliderAdapter
    private lateinit var indicators: Array<ImageView?>
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
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
        setCreatePostComment()
        setPostCommentClickListener()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyboardVisibilityUtils.detachKeyboardListeners()
    }

    private fun setBackButtonClickListener() {
        binding.btnPostBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setOnUserProfileClickListener(memberId: String) {
        binding.imgPostUserProfile.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(PostFragmentDirections.actionPostFragmentToProfileFragment(memberId = memberId))
        }
        binding.tvPostUserNickname.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(PostFragmentDirections.actionPostFragmentToProfileFragment(memberId = memberId))
        }
    }

    private fun setCommentListAdapter() {
        postCommentAdapter = PostCommentAdapter(requestManager = glideRequestManager)
        binding.rvPostCommentList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPostCommentList.adapter = postCommentAdapter
    }

    private fun setPostOptionClickListener(isMine: Boolean, memberId: String) {
        binding.btnPostOption.setOnClickListener {
            if (isMine) {
                Navigation.findNavController(it)
                    .navigate(
                        PostFragmentDirections.actionPostFragmentToPostOptionMineFragment(
                            postId = args.postId
                        )
                    )
            } else {
                Navigation.findNavController(it)
                    .navigate(PostFragmentDirections.actionPostFragmentToPostOptionFragment(postId = args.postId, memberId = memberId))
            }
        }
    }

    private fun setPostDetailCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.requestPostDetail(args.postId)
                postViewModel.postDetail.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.let { post ->
                                binding.post = post
                                binding.categoryKR = post.category?.let { it1 -> categoryKR(it1) }
                                binding.executePendingBindings()
                                postImageSliderAdapter.submitList(post.postImages)
                                CoroutineScope(Dispatchers.Main).launch {
                                    val userThumbnailImgBitmap = withContext(Dispatchers.IO) {
                                        GlideLoadUtil.loadImageBackground(
                                            requestManager = glideRequestManager,
                                            width = 40,
                                            height = 40,
                                            imgName = post.userProfileImage
                                        )
                                    }
                                    GlideLoadUtil.loadImageViewProfile(
                                        requestManager = glideRequestManager,
                                        width = 40,
                                        height = 40,
                                        img = userThumbnailImgBitmap,
                                        imgView = binding.imgPostUserProfile
                                    )
                                }

                                val isMine =
                                    (post.memberId == DayoApplication.preferences.getCurrentUser().memberId)

                                setPostLikeClickListener(isChecked = post.heart)
                                post.memberId?.let { memberId ->
                                    setOnUserProfileClickListener(memberId)
                                    setPostOptionClickListener(isMine = isMine, memberId = memberId)
                                }
                                post.postImages?.let { it1 -> setupIndicators(it1.size) }
                                post.bookmark?.let { it1 -> setPostBookmarkClickListener(it1) }
                                post.hashtags?.let { it1 -> setTagList(it1) }
                            }
                        }
                        Status.LOADING -> {}
                        Status.ERROR -> {}
                    }
                }
            }
        }
    }

    private fun setPostCommentCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.requestPostComment(args.postId)
                postViewModel.postComment.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.let { postComment ->
                                postCommentAdapter.submitList(postComment.toMutableList())
                                binding.postCommentCount = postComment.size
                            }
                        }
                        Status.LOADING -> {}
                        Status.ERROR -> {}
                    }
                }
            }
        }
    }

    private fun setPostCommentClickListener() {
        postCommentAdapter.setOnItemClickListener(object : PostCommentAdapter.OnItemClickListener {
            override fun onItemClick(v: View, comment: Comment, position: Int) {
                // Item 자체를 클릭하는 경우 나타나는 Event 작성
            }

            override fun DeletePostCommentClick(comment: Comment, position: Int) {
                val deleteComment = {
                    postViewModel.requestDeletePostComment(commentId = comment.commentId)
                    postViewModel.postCommentDeleteSuccess.observe(viewLifecycleOwner) { isSuccess ->
                        if (isSuccess.getContentIfNotHandled() == true) {
                            refreshPostComment()
                            Toast.makeText(
                                requireContext(),
                                R.string.post_comment_delete_alert_message_success,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (isSuccess.getContentIfNotHandled() == false) {
                            Toast.makeText(
                                requireContext(),
                                R.string.post_comment_delete_alert_message_fail,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                mAlertDialog = DefaultDialogConfirm.createDialog(
                    requireContext(),
                    R.string.post_comment_delete_alert_message,
                    true,
                    true,
                    null,
                    null,
                    deleteComment
                ) { this@PostFragment.mAlertDialog.dismiss() }
                mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                if (!mAlertDialog.isShowing) {
                    mAlertDialog.show()
                    DefaultDialogConfigure.dialogResize(requireContext(), mAlertDialog, 0.7f, 0.19f)
                }
            }
        })
    }

    private fun setTagList(tagList: List<String>) {
        binding.chipgroupPostTagList.removeAllViews()
        if (!tagList.isNullOrEmpty()) {
            (tagList.indices).mapNotNull { index ->
                val chip =
                    LayoutInflater.from(context).inflate(R.layout.item_post_tag, null) as Chip
                val layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT
                )
                with(chip) {
                    chipBackgroundColor =
                        ColorStateList(
                            arrayOf(
                                intArrayOf(-android.R.attr.state_pressed),
                                intArrayOf(android.R.attr.state_pressed)
                            ),
                            intArrayOf(
                                resources.getColor(R.color.gray_6_F6F6F6, context?.theme),
                                resources.getColor(R.color.primary_green_23C882, context?.theme)
                            )
                        )

                    setTextColor(
                        ColorStateList(
                            arrayOf(
                                intArrayOf(-android.R.attr.state_pressed),
                                intArrayOf(android.R.attr.state_pressed)
                            ),
                            intArrayOf(
                                resources.getColor(R.color.gray_1_313131, context?.theme),
                                resources.getColor(R.color.white_FFFFFF, context?.theme)
                            )
                        )
                    )
                    ensureAccessibleTouchTarget(42.toPx())
                    text = "# ${tagList[index].trim()}"
                    setOnClickListener {
                        keyboardVisibilityUtils.detachKeyboardListeners() // TODO : 임시 처리, 화면을 벗어나므로 detach 처리 필요
                        Navigation.findNavController(it).navigate(
                            PostFragmentDirections.actionPostFragmentToSearchResultFragment(tagList[index].trim())
                        )
                    }
                }
                binding.chipgroupPostTagList.addView(chip, layoutParams)
            }
        }
    }

    private fun setImageSlider() {
        postImageSliderAdapter = PostImageSliderAdapter(requestManager = glideRequestManager)
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
            for (i in binding.viewPostImageIndicators.childCount - 1 downTo (indicators.size)) {
                binding.viewPostImageIndicators.removeViewAt(i)
            }
        }
        setCurrentIndicator(0)
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount: Int = binding.viewPostImageIndicators.childCount
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
                if (!isChecked) {
                    postViewModel.requestLikePost(postId = args.postId)
                } else {
                    postViewModel.requestUnlikePost(postId = args.postId)
                }.let {
                    it.invokeOnCompletion { throwable ->
                        when (throwable) {
                            is CancellationException -> Log.e("Post Like Click", "CANCELLED")
                            null -> {
                                postViewModel.requestPostDetail(postId = args.postId)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setPostBookmarkClickListener(isChecked: Boolean) {
        with(binding.btnPostBookmark) {
            setOnClickListener {
                if (!isChecked) {
                    postViewModel.requestBookmarkPost(postId = args.postId)
                } else {
                    postViewModel.requestDeleteBookmarkPost(postId = args.postId)
                }.let {
                    it.invokeOnCompletion { throwable ->
                        when (throwable) {
                            is CancellationException -> Log.e("Post Bookmark Click", "CANCELLED")
                            null -> {
                                postViewModel.requestPostDetail(postId = args.postId)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setCreatePostComment() {
        CoroutineScope(Dispatchers.Main).launch {
            val userThumbnailImgBitmap = withContext(Dispatchers.IO) {
                GlideLoadUtil.loadImageBackground(
                    requestManager = glideRequestManager,
                    width = 40,
                    height = 40,
                    imgName = DayoApplication.preferences.getCurrentUser().profileImg ?: ""
                )
            }
            GlideLoadUtil.loadImageViewProfile(
                requestManager = glideRequestManager,
                width = 40,
                height = 40,
                img = userThumbnailImgBitmap,
                imgView = binding.imgPostCommentMyProfile
            )
        }

        binding.tvPostCommentUpload.setOnClickListener {
            if (binding.etPostCommentDescription.text.toString().trim().isNotEmpty()) {
                postViewModel.requestCreatePostComment(
                    contents = binding.etPostCommentDescription.text.toString(),
                    postId = args.postId
                )
                with(binding.etPostCommentDescription) {
                    setText("")
                    clearFocus()
                    HideKeyBoardUtil.hide(requireContext(), this)
                }
                postViewModel.postCommentCreateSuccess.observe(viewLifecycleOwner) {
                    if (it.getContentIfNotHandled() == true) refreshPostComment()
                }
            }
        }
    }

    private fun refreshPostComment() {
        // TODO : Handler 수정 필요
        Handler().postDelayed(
            {
                with(binding.rvPostCommentList.adapter as PostCommentAdapter) {
                    postViewModel.requestPostComment(args.postId)
                    submitList(
                        postViewModel.postComment.value?.data?.subList(
                            0,
                            postCommentAdapter.itemCount
                        )?.toMutableList()
                    )
                }
            }, 60
        )
        Handler().postDelayed({ afterCreatedScroll() }, 200)

    }

    private fun afterCreatedScroll() {
        binding.layoutScrollPost.fullScroll(View.FOCUS_DOWN)
    }

    fun Int.toPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}