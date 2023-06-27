package com.daily.dayo.presentation.fragment.home

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.common.GlideLoadUtil
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.extension.navigateSafe
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentHomeNewPostListBinding
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.Post
import com.daily.dayo.presentation.adapter.HomeNewAdapter
import com.daily.dayo.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeNewPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentHomeNewPostListBinding>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private lateinit var homeNewAdapter: HomeNewAdapter

    lateinit var mGlideRequestManager: RequestManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGlideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeNewPostListBinding.inflate(inflater, container, false)
        startLoadingView()
        setInitialCategory()
        setRvNewPostAdapter()
        setNewPostListCollect()
        setPostLikeClickListener()
        setEmptyViewActionClickListener()
        setNewPostListRefreshListener()
        return binding.root
    }

    override fun onResume() {
        loadPosts(homeViewModel.currentNewCategory)
        super.onResume()
    }

    private fun setNewPostListRefreshListener() {
        binding.swipeRefreshLayoutNewPost.setOnRefreshListener {
            loadPosts(homeViewModel.currentNewCategory)
        }
    }

    private fun setInitialCategory() {
        with(binding) {
            radiogroupNewPostCategory.check(
                when (homeViewModel.currentNewCategory) {
                    Category.ALL -> radiobuttonNewPostCategoryAll.id
                    Category.SCHEDULER -> radiobuttonNewPostCategoryScheduler.id
                    Category.STUDY_PLANNER -> radiobuttonNewPostCategoryStudyplanner.id
                    Category.GOOD_NOTE -> radiobuttonNewPostCategoryDigital.id
                    Category.POCKET_BOOK -> radiobuttonNewPostCategoryPocketbook.id
                    Category.SIX_DIARY -> radiobuttonNewPostCategory6holediary.id
                    Category.ETC -> radiobuttonNewPostCategoryEtc.id
                }
            )
        }
    }

    private fun setRvNewPostAdapter() {
        homeNewAdapter = HomeNewAdapter(false, mGlideRequestManager, this::toggleLikeStatus)
        binding.rvNewPost.adapter = homeNewAdapter
    }

    private fun setNewPostListCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.newPostList.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { postList ->
                            binding.swipeRefreshLayoutNewPost.isRefreshing = false
                            loadPostThumbnail(postList)
                            binding.layoutNewPostEmpty.isVisible = postList.isEmpty()
                        }
                    }
                    Status.LOADING -> {
                    }
                    Status.ERROR -> {

                    }
                }
            }
        }
        with(binding) {
            radiobuttonNewPostCategoryAll.setOnDebounceClickListener {
                loadPosts(Category.ALL)
            }
            radiobuttonNewPostCategoryScheduler.setOnDebounceClickListener {
                loadPosts(Category.SCHEDULER)
            }
            radiobuttonNewPostCategoryStudyplanner.setOnDebounceClickListener {
                loadPosts(Category.STUDY_PLANNER)
            }
            radiobuttonNewPostCategoryPocketbook.setOnDebounceClickListener {
                loadPosts(Category.POCKET_BOOK)
            }
            radiobuttonNewPostCategory6holediary.setOnDebounceClickListener {
                loadPosts(Category.SIX_DIARY)
            }
            radiobuttonNewPostCategoryDigital.setOnDebounceClickListener {
                loadPosts(Category.GOOD_NOTE)
            }
            radiobuttonNewPostCategoryEtc.setOnDebounceClickListener {
                loadPosts(Category.ETC)
            }
        }
    }

    private fun loadPosts(selectCategory: Category) {
        with(homeViewModel) {
            currentNewCategory = selectCategory
            requestNewPostList()
        }
    }

    private fun setPostLikeClickListener() {
        homeNewAdapter.setOnItemClickListener(object :
            HomeNewAdapter.OnItemClickListener {
            override fun likePostClick(btn: ImageButton, post: Post, position: Int) {
                with(post) {
                    try {
                        if (!heart) {
                            homeViewModel.requestLikePost(postId!!)
                        } else {
                            homeViewModel.requestUnlikePost(post.postId!!)
                        }
                    } catch (postIdNullException: NullPointerException) {
                        Log.e(this@HomeNewPostListFragment.tag, "PostId Null Exception Occurred")
                        loadPosts(homeViewModel.currentNewCategory)
                    }
                }
            }
        })
    }

    private fun setEmptyViewActionClickListener() {
        binding.btnNewPostEmptyAction.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.HomeFragment,
                action = R.id.action_homeFragment_to_writeFragment
            )
        }
    }

    private fun loadPostThumbnail(postList: List<Post>) {
        val thumbnailImgList = emptyList<Bitmap>().toMutableList()
        val userImgList = emptyList<Bitmap>().toMutableList()

        viewLifecycleOwner.lifecycleScope.launch {
            for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                thumbnailImgList.add(withContext(Dispatchers.IO) {
                    GlideLoadUtil.loadImageBackground(
                        context = requireContext(),
                        height = 158,
                        width = 158,
                        imgName = postList[i].thumbnailImage ?: ""
                    )
                })
                userImgList.add(withContext(Dispatchers.IO) {
                    GlideLoadUtil.loadImageBackground(
                        context = requireContext(),
                        height = 17,
                        width = 17,
                        imgName = postList[i].userProfileImage ?: ""
                    )
                })
            }
        }.invokeOnCompletion { throwable ->
            when (throwable) {
                is CancellationException -> Log.e("Image Loading", "CANCELLED")
                null -> {
                    var loadedPostList = postList.toMutableList()
                    for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                        loadedPostList[i].preLoadThumbnail = thumbnailImgList[i]
                        loadedPostList[i].preLoadUserImg = userImgList[i]
                    }
                    homeNewAdapter.submitList(postList.toMutableList())
                    stopLoadingView()
                    thumbnailImgList.clear()
                    userImgList.clear()
                }
            }
        }
    }

    private fun startLoadingView() {
        with(binding) {
            with(layoutNewPostShimmer) {
                startShimmer()
                visibility = View.VISIBLE
            }
            rvNewPost.visibility = View.INVISIBLE
        }
    }

    private fun stopLoadingView() {
        with(binding) {
            with(layoutNewPostShimmer) {
                stopShimmer()
                visibility = View.GONE
            }
            rvNewPost.visibility = View.VISIBLE
        }
    }

    private fun toggleLikeStatus(id: Int, isLiked: Boolean) {
        homeViewModel.toggleLikeStatusNewPost(id, isLiked)
    }
}