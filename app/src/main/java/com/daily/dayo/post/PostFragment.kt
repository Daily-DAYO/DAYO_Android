package com.daily.dayo.post

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import android.widget.ImageView
import androidx.core.content.ContextCompat
import android.widget.LinearLayout
import androidx.lifecycle.*
import com.daily.dayo.post.adapter.PostTagListAdapter
import com.daily.dayo.post.model.RequestCreatePostComment

@AndroidEntryPoint
class PostFragment : Fragment() {
    private var binding by autoCleared<FragmentPostBinding>()
    private val postViewModel by activityViewModels<PostViewModel>()
    private val args by navArgs<PostFragmentArgs>()
    private lateinit var postCommentAdapter : PostCommentAdapter
    private lateinit var postImageSliderAdapter: PostImageSliderAdapter
    private lateinit var postTagListAdapter: PostTagListAdapter
    private lateinit var indicators : Array<ImageView?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setCommentListAdapter()
        setImageSlider()
        setTagList()
        setPostOptionClickListener()
        postViewModel.requestPostDetail(args.id)
        postViewModel.requestPostComment(args.id)
        setPostDetailCollect()
        setPostCommentCollect()
        setCreatePostComment()
        return binding.root
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

    private fun setPostOptionClickListener() {
        binding.btnPostOption.setOnClickListener {
            //PostOptionFragment().show(requireActivity().supportFragmentManager, "PostOptionDialog")
            findNavController().navigate(R.id.action_postFragment_to_postOptionFragment)
        }
    }

    private fun setPostDetailCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.postDetail.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { postDetail ->
                                postImageSliderAdapter.submitList(postDetail.images)
                                postTagListAdapter.submitList(postDetail.hashtags)
                                setupIndicators(postDetail.images.size)
                                postDetail?.let {
                                    with(binding) {
                                        post = postDetail
                                        // isMine =
                                        Glide.with(requireContext())
                                            .load(postDetail.profileImg)
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
                postViewModel.postComment.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { postComment ->
                                postCommentAdapter.submitList(postComment.data?.toMutableList())
                            }
                        }
                        Status.LOADING -> { }
                        Status.ERROR -> { }
                    }
                })
            }
        }
    }
    private fun setTagList() {
        postTagListAdapter = PostTagListAdapter()
        binding.rvPostTagList.adapter = postTagListAdapter
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

    private fun setCreatePostComment() {
        binding.tvPostCommentUpload.setOnClickListener {
            val commentDescription = RequestCreatePostComment(
                contents = binding.etPostCommentDescription.text.toString(),
                postId = args.id)
            postViewModel.requestCreatePostComment(commentDescription)

            // TODO : Handler 수정 필요
            Handler().postDelayed(
                {with(binding.rvPostCommentList.adapter as PostCommentAdapter) {
                    postViewModel.requestPostComment(args.id)
                    submitList(postViewModel.postComment.value?.data?.data?.subList(0, postCommentAdapter.itemCount)?.toMutableList())
                }},60
            )
            Handler().postDelayed({afterCreatedScroll()}, 200)
        }
    }
    private fun afterCreatedScroll() {
        binding.layoutScrollPost.fullScroll(View.FOCUS_DOWN)
    }
}