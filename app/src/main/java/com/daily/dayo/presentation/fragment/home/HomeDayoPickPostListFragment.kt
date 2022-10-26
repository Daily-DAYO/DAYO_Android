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
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
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
        binding = FragmentHomeDayoPickPostListBinding.inflate(inflater, container, false)
        setInitialCategory()
        setRvDayoPickPostAdapter()
        setDayoPickPostListCollect()
        setPostLikeClickListener()

        binding.layoutDayopickPostShimmer.startShimmer()
        return binding.root
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
        homeDayoPickAdapter = HomeDayoPickAdapter(true, mGlideRequestManager)
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
        with(binding) {
            radiobuttonDayopickPostCategoryAll.setOnDebounceClickListener {
                setCategoryPostList(Category.ALL)
                loadingPost()
            }
            radiobuttonDayopickPostCategoryScheduler.setOnDebounceClickListener {
                setCategoryPostList(Category.SCHEDULER)
                loadingPost()
            }
            radiobuttonDayopickPostCategoryStudyplanner.setOnDebounceClickListener {
                setCategoryPostList(Category.STUDY_PLANNER)
                loadingPost()
            }
            radiobuttonDayopickPostCategoryPocketbook.setOnDebounceClickListener {
                setCategoryPostList(Category.POCKET_BOOK)
                loadingPost()
            }
            radiobuttonDayopickPostCategory6holediary.setOnDebounceClickListener {
                setCategoryPostList(Category.SIX_DIARY)
                loadingPost()
            }
            radiobuttonDayopickPostCategoryDigital.setOnDebounceClickListener {
                setCategoryPostList(Category.GOOD_NOTE)
                loadingPost()
            }
            radiobuttonDayopickPostCategoryEtc.setOnDebounceClickListener {
                setCategoryPostList(Category.ETC)
                loadingPost()
            }
        }
    }

    private fun setCategoryPostList(selectCategory: Category) {
        currentCategory = selectCategory
        if (selectCategory == Category.ALL) {
            homeViewModel.currentDayoPickCategory = selectCategory
            homeViewModel.requestHomeDayoPickPostList()
        } else {
            homeViewModel.currentDayoPickCategory = selectCategory
            homeViewModel.requestHomeDayoPickPostListCategory(selectCategory)
        }
    }

    private fun setPostLikeClickListener() {
        homeDayoPickAdapter.setOnItemClickListener(object :
            HomeDayoPickAdapter.OnItemClickListener {
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
                                if (this@HomeDayoPickPostListFragment::currentCategory.isInitialized) {
                                    if (currentCategory != Category.ALL) {
                                        homeViewModel.requestHomeDayoPickPostListCategory(
                                            currentCategory
                                        )
                                    } else {
                                        homeViewModel.requestHomeDayoPickPostList()
                                    }
                                } else {
                                    homeViewModel.requestHomeDayoPickPostList()
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
                    var loadedPostList = postList.toMutableList()
                    for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                        loadedPostList[i].preLoadThumbnail = thumbnailImgList[i]
                        loadedPostList[i].preLoadUserImg = userImgList[i]
                    }
                    homeDayoPickAdapter.submitList(postList.toMutableList())
                    completeLoadPost()
                    thumbnailImgList.clear()
                    userImgList.clear()
                }
            }
        }
    }

    private fun loadingPost() {
        binding.layoutDayopickPostShimmer.startShimmer()
        binding.layoutDayopickPostShimmer.visibility = View.VISIBLE
        binding.rvDayopickPost.visibility = View.INVISIBLE
    }

    private fun completeLoadPost() {
        binding.layoutDayopickPostShimmer.stopShimmer()
        binding.layoutDayopickPostShimmer.visibility = View.GONE
        binding.rvDayopickPost.visibility = View.VISIBLE
    }
}