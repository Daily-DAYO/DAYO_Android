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
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentHomeDayoPickPostListBinding
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.Post
import com.daily.dayo.presentation.adapter.HomeDayoPickAdapter
import com.daily.dayo.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
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
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
        }
        with(binding) {
            radiobuttonDayopickPostCategoryAll.setOnClickListener {
                setCategoryPostList(Category.ALL)
                loadingPost()
            }
            radiobuttonDayopickPostCategoryScheduler.setOnClickListener {
                setCategoryPostList(Category.SCHEDULER)
                loadingPost()
            }
            radiobuttonDayopickPostCategoryStudyplanner.setOnClickListener {
                setCategoryPostList(Category.STUDY_PLANNER)
                loadingPost()
            }
            radiobuttonDayopickPostCategoryPocketbook.setOnClickListener {
                setCategoryPostList(Category.POCKET_BOOK)
                loadingPost()
            }
            radiobuttonDayopickPostCategory6holediary.setOnClickListener {
                setCategoryPostList(Category.SIX_DIARY)
                loadingPost()
            }
            radiobuttonDayopickPostCategoryDigital.setOnClickListener {
                setCategoryPostList(Category.GOOD_NOTE)
                loadingPost()
            }
            radiobuttonDayopickPostCategoryEtc.setOnClickListener {
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
                                if (currentCategory != Category.ALL) {
                                    homeViewModel.requestHomeDayoPickPostListCategory(
                                        currentCategory
                                    )
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

        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                thumbnailImgList.add(withContext(Dispatchers.IO) {
                    Glide.with(requireContext())
                        .asBitmap()
                        .override(158, 158)
                        .load("http://117.17.198.45:8080/images/" + postList[i].thumbnailImage)
                        .priority(Priority.HIGH)
                        .submit()
                        .get()
                })
                userImgList.add(withContext(Dispatchers.IO) {
                    Glide.with(requireContext())
                        .asBitmap()
                        .override(17, 17)
                        .load("http://117.17.198.45:8080/images/" + postList[i].userProfileImage)
                        .submit()
                        .get()
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
        binding.radiogroupDayopickPostCategory.visibility = View.INVISIBLE
        binding.rvDayopickPost.visibility = View.INVISIBLE
    }

    private fun completeLoadPost() {
        binding.layoutDayopickPostShimmer.stopShimmer()
        binding.layoutDayopickPostShimmer.visibility = View.GONE
        binding.radiogroupDayopickPostCategory.visibility = View.VISIBLE
        binding.rvDayopickPost.visibility = View.VISIBLE
    }
}