package daily.dayo.presentation.fragment.post

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
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.domain.model.Comment
import daily.dayo.domain.model.categoryKR
import daily.dayo.presentation.R
import daily.dayo.presentation.adapter.PostCommentAdapter
import daily.dayo.presentation.adapter.PostImageSliderAdapter
import daily.dayo.presentation.common.GlideLoadUtil
import daily.dayo.presentation.common.HideKeyBoardUtil
import daily.dayo.presentation.common.KeyboardVisibilityUtils
import daily.dayo.presentation.common.ReplaceUnicode.trimBlankText
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.TimeChangerUtil
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.DefaultDialogConfigure
import daily.dayo.presentation.common.dialog.DefaultDialogConfirm
import daily.dayo.presentation.common.dialog.LoadingAlertDialog
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentPostBinding
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.HomeViewModel
import daily.dayo.presentation.viewmodel.PostViewModel
import daily.dayo.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostFragment : Fragment() {
    private var binding by autoCleared<FragmentPostBinding> {
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
        keyboardVisibilityUtils.detachKeyboardListeners()
        postViewModel.cleanUpPostDetail()
        onDestroyBindingView()
    }
    private val accountViewModel by activityViewModels<AccountViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val searchViewModel by activityViewModels<SearchViewModel>()
    private val args by navArgs<PostFragmentArgs>()
    private lateinit var mAlertDialog: AlertDialog
    private var glideRequestManager: RequestManager? = null
    private var postCommentAdapter: PostCommentAdapter? = null
    private var postImageSliderAdapter: PostImageSliderAdapter? = null
    private var indicators: Array<ImageView?>? = null
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
            onShowKeyboard = { keyboardHeight ->
                binding.layoutScrollPost.run {
                    scrollTo(0, binding.layoutScrollPost.bottom + keyboardHeight)
                }
            })

        setBackButtonClickListener()
        setCommentListAdapter()
        setImageSlider()
        setPostDetailCollect()
        setPostCommentCollect()
        setCreatePostComment()
        setPostCommentClickListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.postFragment = this
    }

    private fun onDestroyBindingView() {
        glideRequestManager = null
        postImageSliderAdapter = null
        postCommentAdapter = null
        indicators = null
        binding.rvPostCommentList.adapter = null
    }

    private fun setBackButtonClickListener() {
        binding.btnPostBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setOnUserProfileClickListener(memberId: String) {
        binding.imgPostUserProfile.setOnDebounceClickListener {
            Navigation.findNavController(it)
                .navigateSafe(
                    currentDestinationId = R.id.PostFragment,
                    action = R.id.action_postFragment_to_profileFragment,
                    args = PostFragmentDirections.actionPostFragmentToProfileFragment(memberId = memberId).arguments
                )
        }
        binding.tvPostUserNickname.setOnDebounceClickListener {
            Navigation.findNavController(it)
                .navigateSafe(
                    currentDestinationId = R.id.PostFragment,
                    action = R.id.action_postFragment_to_profileFragment,
                    args = PostFragmentDirections.actionPostFragmentToProfileFragment(memberId = memberId).arguments
                )
        }
    }

    private fun setCommentListAdapter() {
        postCommentAdapter = glideRequestManager?.let { requestManager ->
            PostCommentAdapter(requestManager = requestManager, userInfo = accountViewModel.getCurrentUserInfo())
        }
        binding.rvPostCommentList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPostCommentList.adapter = postCommentAdapter
    }

    private fun setPostOptionClickListener(isMine: Boolean, memberId: String) {
        binding.btnPostOption.setOnDebounceClickListener {
            if (isMine) {
                Navigation.findNavController(it)
                    .navigateSafe(
                        currentDestinationId = R.id.PostFragment,
                        action = R.id.action_postFragment_to_postOptionMineFragment,
                        args = PostFragmentDirections.actionPostFragmentToPostOptionMineFragment(
                            postId = args.postId
                        ).arguments
                    )
            } else {
                Navigation.findNavController(it)
                    .navigateSafe(
                        currentDestinationId = R.id.PostFragment,
                        action = R.id.action_postFragment_to_postOptionFragment,
                        args = PostFragmentDirections.actionPostFragmentToPostOptionFragment(
                            postId = args.postId,
                            memberId = memberId
                        ).arguments
                    )
            }
        }
    }

    private fun setPostDetailCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                postViewModel.postDetail.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.let { post ->
                                updatePostStatus(post.heart, post.heartCount, null)
                                binding.post = post
                                binding.createDateTime = TimeChangerUtil.timeChange(context = binding.tvPostTime.context, time = post.createDateTime)
                                binding.categoryKR = post.category?.let { category -> categoryKR(category) }
                                binding.executePendingBindings()
                                postImageSliderAdapter?.submitList(post.images)
                                viewLifecycleOwner.lifecycleScope.launch {
                                    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                        glideRequestManager?.let { requestManager ->
                                            GlideLoadUtil.loadImageViewProfile(
                                                requestManager = requestManager,
                                                width = binding.imgPostUserProfile.width,
                                                height = binding.imgPostUserProfile.height,
                                                imgName = post.profileImg,
                                                imgView = binding.imgPostUserProfile
                                            )
                                        }
                                    }
                                }

                                val isMine =
                                    (post.memberId == accountViewModel.getCurrentUserInfo().memberId)
                                binding.btnPostLike.isSelected = post.heart
                                setPostLikeClickListener(isChecked = post.heart)
                                setPostLikeDoubleTap(isChecked = post.heart)
                                post.memberId?.let { memberId ->
                                    setOnUserProfileClickListener(memberId)
                                    setPostOptionClickListener(isMine = isMine, memberId = memberId)
                                }
                                post.images?.let { it1 -> if (it1.size > 1) setupIndicators(it1.size) }
                                post.bookmark?.let { it1 -> setPostBookmarkClickListener(it1) }
                                post.hashtags?.let { it1 -> setTagList(it1) }
                            }
                        }

                        Status.LOADING -> {}
                        Status.ERROR -> {}
                    }
                }
                postViewModel.requestPostDetail(args.postId)
            }
        }
    }

    private fun setPostCommentCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.requestPostComment(args.postId)
                postViewModel.postComments.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.data?.let { postComment ->
                                postCommentAdapter?.submitList(postComment.toMutableList())
                                updatePostStatus(null, null, postComment.size)
                                binding.commentCount = postComment.size
                                binding.commentCountStr = postComment.size.toString()
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
        postCommentAdapter?.setOnItemClickListener(object : PostCommentAdapter.OnItemClickListener {
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
                                resources.getColor(R.color.gray_6_F0F1F3, context?.theme),
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
                    text = "# ${trimBlankText(tagList[index])}"
                    setOnDebounceClickListener {
                        searchViewModel.searchKeyword = trimBlankText(tagList[index])
                        Navigation.findNavController(it).navigateSafe(
                            currentDestinationId = R.id.PostFragment,
                            action = R.id.action_postFragment_to_searchResultFragment
                        )
                    }
                }
                binding.chipgroupPostTagList.addView(chip, layoutParams)
            }
        }
    }

    private fun setImageSlider() {
        postImageSliderAdapter = glideRequestManager?.let { requestManager ->
            PostImageSliderAdapter(
                requestManager = requestManager
            )
        }
        with(binding.vpPostImage) {
            adapter = postImageSliderAdapter
            setPageTransformer { page, position ->
                // To Disable image changing animation
            }
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
        indicators = arrayOfNulls(count)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(16, 8, 16, 8)

        indicators?.let { indicators ->
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
            setOnDebounceClickListener {
                setPostLike(isChecked)
            }
        }
    }

    private fun setPostLikeDoubleTap(isChecked: Boolean) {
        postImageSliderAdapter?.setOnItemClickListener(object :
            PostImageSliderAdapter.OnItemClickListener {
            override fun postImageDoubleTap(lottieAnimationView: LottieAnimationView) {
                setPostLike(isChecked, lottieAnimationView)
            }
        })
    }

    private fun setPostLike(isChecked: Boolean, lottieAnimationView: LottieAnimationView? = null) {
        if (!isChecked) {
            binding.btnPostLike.isSelected = true
            postViewModel.requestLikePost(postId = args.postId)
            if (lottieAnimationView != null) {
                lottieAnimationView.visibility = View.VISIBLE
                lottieAnimationView.playAnimation()
            }
        } else {
            if (lottieAnimationView == null) {
                binding.btnPostLike.isSelected = false
                postViewModel.requestUnlikePost(postId = args.postId)
            }
        }
    }

    private fun setPostBookmarkClickListener(isChecked: Boolean) {
        with(binding.btnPostBookmark) {
            setOnDebounceClickListener {
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                glideRequestManager?.let { requestManager ->
                    GlideLoadUtil.loadImageViewProfile(
                        requestManager = requestManager,
                        width = binding.imgPostCommentMyProfile.width,
                        height = binding.imgPostCommentMyProfile.height,
                        imgName = accountViewModel.getCurrentUserInfo().profileImg ?: "",
                        imgView = binding.imgPostCommentMyProfile
                    )
                }
            }
        }

        binding.tvPostCommentUpload.setOnDebounceClickListener {
            val currentCommentEditText = trimBlankText(binding.etPostCommentDescription.text)
            if (currentCommentEditText.isNotEmpty()) {
                LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
                postViewModel.requestCreatePostComment(
                    contents = currentCommentEditText,
                    postId = args.postId,
                    mentionedUser = emptyList()
                )
                with(binding.etPostCommentDescription) {
                    setText("")
                    clearFocus()
                    HideKeyBoardUtil.hide(requireContext(), this)
                }
                postViewModel.postCommentCreateSuccess.observe(viewLifecycleOwner) {
                    if (it.getContentIfNotHandled() == true) {
                        refreshPostComment()
                        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
                    }
                }
            }
        }
    }

    fun setLikeCountClickListener() {
        findNavController().navigateSafe(
            currentDestinationId = R.id.PostFragment,
            action = PostFragmentDirections.actionPostFragmentToPostLikeUsersFragment(postId = args.postId)
        )
    }

    private fun refreshPostComment() {
        // TODO : Handler 수정 필요
        Handler().postDelayed(
            {
                binding.rvPostCommentList.adapter?.let {
                    with(it as PostCommentAdapter) {
                        postViewModel.requestPostComment(args.postId)
                        submitList(
                            postViewModel.postComments.value?.data?.data?.subList(
                                0,
                                it.itemCount
                            )?.toMutableList()
                        )
                    }
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

    // Home에서도 업데이트한 내용을 반영할 수 있도록 Status 업데이트
    private fun updatePostStatus(
        heart: Boolean? = null,
        heartCount: Int? = null,
        commentCount: Int? = null
    ) {
        homeViewModel.setPostStatus(
            args.postId,
            heart,
            heartCount,
            commentCount
        )
    }
}