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
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentHomeDayoPickPostListBinding
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.Post
import com.daily.dayo.presentation.adapter.HomeDayoPickAdapter
import com.daily.dayo.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeDayoPickPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentHomeDayoPickPostListBinding>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private lateinit var homeDayoPickAdapter: HomeDayoPickAdapter

    lateinit var mGlideRequestManager: RequestManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGlideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeDayoPickPostListBinding.inflate(inflater, container, false)
        startLoadingView()
        setInitialCategory()
        setRvDayoPickPostAdapter()
        setDayoPickPostListCollect()
        setPostLikeClickListener()
        setEmptyViewActionClickListener()
        setDayoPickPostListRefreshListener()
        return binding.root
    }

    override fun onResume() {
        loadPosts(homeViewModel.currentDayoPickCategory)
        super.onResume()
    }

    private fun setDayoPickPostListRefreshListener() {
        binding.swipeRefreshLayoutDayoPickPost.setOnRefreshListener {
            loadPosts(homeViewModel.currentDayoPickCategory)
        }
    }

    private fun setInitialCategory() {
        with(binding) {
            radiogroupDayopickPostCategory.check(
                when (homeViewModel.currentDayoPickCategory) {
                    Category.ALL -> radiobuttonDayopickPostCategoryAll.id
                    Category.SCHEDULER -> radiobuttonDayopickPostCategoryScheduler.id
                    Category.STUDY_PLANNER -> radiobuttonDayopickPostCategoryStudyplanner.id
                    Category.GOOD_NOTE -> radiobuttonDayopickPostCategoryDigital.id
                    Category.POCKET_BOOK -> radiobuttonDayopickPostCategoryPocketbook.id
                    Category.SIX_DIARY -> radiobuttonDayopickPostCategory6holediary.id
                    Category.ETC -> radiobuttonDayopickPostCategoryEtc.id
                }
            )
        }
    }

    private fun setRvDayoPickPostAdapter() {
        homeDayoPickAdapter = HomeDayoPickAdapter(true, mGlideRequestManager, this::toggleLikeStatus)
        binding.rvDayopickPost.adapter = homeDayoPickAdapter
    }

    private fun setDayoPickPostListCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.dayoPickPostList.observe(viewLifecycleOwner) {
                // TODO : 하단 BottomNavigation을 통해 Fragment를 이동하고 나서 돌아오고나서 카테고리를 선택하면 observe가 중복해서 생겨난다
                //  해당 LiveData 객체가 1개 더 생성되는듯 하다
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { postList ->
                            binding.swipeRefreshLayoutDayoPickPost.isRefreshing = false
                            loadInitialPostThumbnail(postList)
                            binding.layoutDayopickPostEmpty.isVisible = postList.isEmpty()
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
            radiobuttonDayopickPostCategoryAll.setOnDebounceClickListener {
                loadPosts(Category.ALL)
            }
            radiobuttonDayopickPostCategoryScheduler.setOnDebounceClickListener {
                loadPosts(Category.SCHEDULER)
            }
            radiobuttonDayopickPostCategoryStudyplanner.setOnDebounceClickListener {
                loadPosts(Category.STUDY_PLANNER)
            }
            radiobuttonDayopickPostCategoryPocketbook.setOnDebounceClickListener {
                loadPosts(Category.POCKET_BOOK)
            }
            radiobuttonDayopickPostCategory6holediary.setOnDebounceClickListener {
                loadPosts(Category.SIX_DIARY)
            }
            radiobuttonDayopickPostCategoryDigital.setOnDebounceClickListener {
                loadPosts(Category.GOOD_NOTE)
            }
            radiobuttonDayopickPostCategoryEtc.setOnDebounceClickListener {
                loadPosts(Category.ETC)
            }
        }
    }

    private fun loadPosts(selectCategory: Category) {
        with(homeViewModel) {
            currentDayoPickCategory = selectCategory
            requestDayoPickPostList()
        }
    }

    private fun setPostLikeClickListener() {
        homeDayoPickAdapter.setOnItemClickListener(object :
            HomeDayoPickAdapter.OnItemClickListener {
            override fun likePostClick(btn: ImageButton, post: Post, position: Int) {
                with(post) {
                    try {
                        if (!heart) {
                            homeViewModel.requestLikePost(postId!!)
                        } else {
                            homeViewModel.requestUnlikePost(post.postId!!)
                        }
                    } catch (postIdNullException: NullPointerException) {
                        Log.e(this@HomeDayoPickPostListFragment.tag, "PostId Null Exception Occurred")
                        loadPosts(homeViewModel.currentDayoPickCategory)
                    }
                }
            }
        })
    }

    private fun setEmptyViewActionClickListener() {
        binding.btnDayopickPostEmptyAction.setOnDebounceClickListener {
            val homeViewPager = requireActivity().findViewById<ViewPager2>(R.id.pager_home_post)
            val current = homeViewPager.currentItem
            if (current == 0){
                homeViewPager.setCurrentItem(1, true)
            }
            else{
                homeViewPager.setCurrentItem(current-1, true)
            }
        }
    }

    private fun loadInitialPostThumbnail(postList: List<Post>) {
        val thumbnailImgList = emptyList<Bitmap>().toMutableList()
        val userImgList = emptyList<Bitmap>().toMutableList()

        viewLifecycleOwner.lifecycleScope.launch {
            for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                thumbnailImgList.add(withContext(Dispatchers.IO) {
                    loadImageBackground(
                        context = requireContext(),
                        height = 158,
                        width = 158,
                        imgName = postList[i].thumbnailImage ?: ""
                    )
                })
                userImgList.add(withContext(Dispatchers.IO) {
                    loadImageBackground(
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
                    for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                        with(postList[i]) {
                            preLoadThumbnail = thumbnailImgList[i]
                            preLoadUserImg = userImgList[i]
                        }
                    }
                    homeDayoPickAdapter.submitList(postList.toMutableList())
                    stopLoadingView()
                    thumbnailImgList.clear()
                    userImgList.clear()
                }
            }
        }
    }

    private fun startLoadingView() {
        with(binding) {
            with(layoutDayopickPostShimmer) {
                startShimmer()
                visibility = View.VISIBLE
            }
            rvDayopickPost.visibility = View.INVISIBLE
        }
    }

    private fun stopLoadingView() {
        with(binding) {
            with(layoutDayopickPostShimmer) {
                stopShimmer()
                visibility = View.GONE
            }
            rvDayopickPost.visibility = View.VISIBLE
        }
    }

    private fun toggleLikeStatus(id: Int, isLiked: Boolean) {
        homeViewModel.toggleLikeStatusDayoPick(id, isLiked)
    }
}