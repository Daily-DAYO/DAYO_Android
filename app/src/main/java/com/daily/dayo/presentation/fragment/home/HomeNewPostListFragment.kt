package com.daily.dayo.presentation.fragment.home

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentHomeNewPostListBinding
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.Post
import com.daily.dayo.presentation.adapter.HomeNewAdapter
import com.daily.dayo.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeNewPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentHomeNewPostListBinding>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private lateinit var homeNewAdapter: HomeNewAdapter
    private lateinit var currentCategory: Category

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
        setInitialCategory()
        currentCategory = Category.ALL
        setRvNewPostAdapter()
        setNewPostListCollect()
        setPostLikeClickListener()

        binding.layoutNewPostShimmer.startShimmer()
        return binding.root
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
        homeNewAdapter = HomeNewAdapter(false, mGlideRequestManager)
        binding.rvNewPost.adapter = homeNewAdapter
    }

    private fun setNewPostListCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.newPostList.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.let { postList ->
                                loadPostThumbnail(postList)
                            }
                        }
                        Status.LOADING -> {
                        }
                        Status.ERROR -> {

                        }
                    }
                }
            }
        }
        with(binding) {
            radiobuttonNewPostCategoryAll.setOnClickListener {
                setCategoryPostList(Category.ALL)
                loadingPost()
            }
            radiobuttonNewPostCategoryScheduler.setOnClickListener {
                setCategoryPostList(Category.SCHEDULER)
                loadingPost()
            }
            radiobuttonNewPostCategoryStudyplanner.setOnClickListener {
                setCategoryPostList(Category.STUDY_PLANNER)
                loadingPost()
            }
            radiobuttonNewPostCategoryPocketbook.setOnClickListener {
                setCategoryPostList(Category.POCKET_BOOK)
                loadingPost()
            }
            radiobuttonNewPostCategory6holediary.setOnClickListener {
                setCategoryPostList(Category.SIX_DIARY)
                loadingPost()
            }
            radiobuttonNewPostCategoryDigital.setOnClickListener {
                setCategoryPostList(Category.GOOD_NOTE)
                loadingPost()
            }
            radiobuttonNewPostCategoryEtc.setOnClickListener {
                setCategoryPostList(Category.ETC)
                loadingPost()
            }
        }
    }

    private fun setCategoryPostList(selectCategory: Category) {
        currentCategory = selectCategory
        if (selectCategory == Category.ALL) {
            homeViewModel.currentNewCategory = selectCategory
            homeViewModel.requestHomeNewPostList()
        } else {
            homeViewModel.currentNewCategory = selectCategory
            homeViewModel.requestHomeNewPostListCategory(category = selectCategory)
        }
    }

    private fun setPostLikeClickListener() {
        homeNewAdapter.setOnItemClickListener(object :
            HomeNewAdapter.OnItemClickListener {
            override fun likePostClick(btn: ImageButton, post: Post, position: Int) {
                if (!post.heart) {
                    homeViewModel.requestLikePost(post.postId!!)
                } else {
                    homeViewModel.requestUnlikePost(post.postId!!)
                }.let {
                    it.invokeOnCompletion { throwable ->
                        when (throwable) {
                            is CancellationException -> Log.e("Post Like Click", "CANCELLED")
                            null -> {
                                if (currentCategory != Category.ALL) {
                                    homeViewModel.requestHomeNewPostListCategory(currentCategory)
                                } else {
                                    homeViewModel.requestHomeNewPostList()
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun loadPostThumbnail(postList: List<Post>) {
        val thumbnailImgList = emptyList<Bitmap>().toMutableList()
        val userImgList = emptyList<Bitmap>().toMutableList()

        CoroutineScope(Dispatchers.Main).launch {
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
                    completeLoadPost()
                    var loadedPostList = postList.toMutableList()
                    for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                        loadedPostList[i].preLoadThumbnail = thumbnailImgList[i]
                        loadedPostList[i].preLoadUserImg = userImgList[i]
                    }
                    homeNewAdapter.submitList(postList.toMutableList())
                    thumbnailImgList.clear()
                    userImgList.clear()
                }
            }
        }
    }

    private fun loadingPost() {
        binding.layoutNewPostShimmer.startShimmer()
        binding.layoutNewPostShimmer.visibility = View.VISIBLE
        binding.radiogroupNewPostCategory.visibility = View.INVISIBLE
        binding.rvNewPost.visibility = View.INVISIBLE
    }

    private fun completeLoadPost() {
        binding.layoutNewPostShimmer.stopShimmer()
        binding.layoutNewPostShimmer.visibility = View.GONE
        binding.radiogroupNewPostCategory.visibility = View.VISIBLE
        binding.rvNewPost.visibility = View.VISIBLE
    }
}