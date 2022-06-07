package com.daily.dayo.presentation.fragment.home

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
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentHomeDayoPickPostListBinding
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.Post
import com.daily.dayo.presentation.adapter.HomeDayoPickAdapter
import com.daily.dayo.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeDayoPickPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentHomeDayoPickPostListBinding>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private lateinit var homeDayoPickAdapter: HomeDayoPickAdapter
    private lateinit var currentCategory: Category

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeDayoPickPostListBinding.inflate(inflater, container, false)
        currentCategory = Category.ALL
        setRvDayoPickPostAdapter()
        setDayoPickPostListCollect()
        setPostLikeClickListener()
        return binding.root
    }

    private fun setRvDayoPickPostAdapter() {
        homeDayoPickAdapter = HomeDayoPickAdapter(true)
        binding.rvDayopickPost.adapter = homeDayoPickAdapter
    }

    private fun setDayoPickPostListCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.postList.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.let { postList ->
                                homeDayoPickAdapter.submitList(postList.toMutableList())
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
            }
            radiobuttonDayopickPostCategoryScheduler.setOnClickListener {
                setCategoryPostList(Category.SCHEDULER)
            }
            radiobuttonDayopickPostCategoryStudyplanner.setOnClickListener {
                setCategoryPostList(Category.STUDY_PLANNER)
            }
            radiobuttonDayopickPostCategoryPocketbook.setOnClickListener {
                setCategoryPostList(Category.POCKET_BOOK)
            }
            radiobuttonDayopickPostCategory6holediary.setOnClickListener {
                setCategoryPostList(Category.SIX_DIARY)
            }
            radiobuttonDayopickPostCategoryDigital.setOnClickListener {
                setCategoryPostList(Category.GOOD_NOTE)
            }
            radiobuttonDayopickPostCategoryEtc.setOnClickListener {
                setCategoryPostList(Category.ETC)
            }
        }
    }

    private fun setCategoryPostList(selectCategory: Category) {
        currentCategory = selectCategory
        if (selectCategory == Category.ALL) {
            homeViewModel.currentCategory = selectCategory
            homeViewModel.requestHomeDayoPickPostList()
        } else {
            homeViewModel.currentCategory = selectCategory
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
                                    homeViewModel.requestHomeDayoPickPostListCategory(currentCategory)
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
}