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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentHomeNewPostListBinding
import com.daily.dayo.domain.model.Category
import com.daily.dayo.domain.model.Post
import com.daily.dayo.presentation.adapter.HomeDayoPickAdapter
import com.daily.dayo.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeNewPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentHomeNewPostListBinding>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private lateinit var homeNewPostListAdapter: HomeDayoPickAdapter
    private lateinit var currentCategory: Category

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeNewPostListBinding.inflate(inflater, container, false)
        currentCategory = Category.ALL
        setRvNewPostAdapter()
        setNewPostListCollect()
        setPostLikeClickListener()
        return binding.root
    }

    private fun setRvNewPostAdapter() {
        homeNewPostListAdapter = HomeDayoPickAdapter(false)
        binding.rvNewPost.adapter = homeNewPostListAdapter
    }

    private fun setNewPostListCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.postList.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.let { postList ->
                                homeNewPostListAdapter.submitList(postList.toMutableList())
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
            }
            radiobuttonNewPostCategoryScheduler.setOnClickListener {
                setCategoryPostList(Category.SCHEDULER)
            }
            radiobuttonNewPostCategoryStudyplanner.setOnClickListener {
                setCategoryPostList(Category.STUDY_PLANNER)
            }
            radiobuttonNewPostCategoryPocketbook.setOnClickListener {
                setCategoryPostList(Category.POCKET_BOOK)
            }
            radiobuttonNewPostCategory6holediary.setOnClickListener {
                setCategoryPostList(Category.SIX_DIARY)
            }
            radiobuttonNewPostCategoryDigital.setOnClickListener {
                setCategoryPostList(Category.GOOD_NOTE)
            }
            radiobuttonNewPostCategoryEtc.setOnClickListener {
                setCategoryPostList(Category.ETC)
            }
        }
    }

    private fun setCategoryPostList(selectCategory: Category) {
        currentCategory = selectCategory
        if (selectCategory == Category.ALL) {
            homeViewModel.currentCategory = selectCategory
            homeViewModel.requestHomeNewPostList()
        } else {
            homeViewModel.currentCategory = selectCategory
            homeViewModel.requestHomeNewPostListCategory(category = selectCategory)
        }
    }

    private fun setPostLikeClickListener() {
        homeNewPostListAdapter.setOnItemClickListener(object :
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
}