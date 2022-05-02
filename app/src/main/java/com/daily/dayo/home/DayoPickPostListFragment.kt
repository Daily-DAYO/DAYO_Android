package com.daily.dayo.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentDayoPickPostListBinding
import com.daily.dayo.home.adapter.HomeDayoPickAdapter
import com.daily.dayo.home.model.PostContent
import com.daily.dayo.post.model.RequestLikePost
import com.daily.dayo.home.viewmodel.HomeViewModel
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DayoPickPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentDayoPickPostListBinding>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private lateinit var homeDayoPickAdapter : HomeDayoPickAdapter
    private lateinit var currentCategory : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDayoPickPostListBinding.inflate(inflater, container, false)
        currentCategory = getString(R.string.all_eng)
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
                homeViewModel.postList.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { postList ->
                                homeDayoPickAdapter.submitList(postList.data?.toMutableList())
                            }
                        }
                        Status.LOADING -> {
                        }
                        Status.ERROR -> {

                        }
                    }
                })
            }
        }
        with(binding) {
            radiobuttonDayopickPostCategoryAll.setOnClickListener {
                setCategoryPostList(getString(R.string.all_eng))
            }
            radiobuttonDayopickPostCategoryScheduler.setOnClickListener {
                setCategoryPostList(getString(R.string.scheduler_eng))
            }
            radiobuttonDayopickPostCategoryStudyplanner.setOnClickListener {
                setCategoryPostList(getString(R.string.studyplanner_eng))
            }
            radiobuttonDayopickPostCategoryPocketbook.setOnClickListener {
                setCategoryPostList(getString(R.string.pocketbook_eng))
            }
            radiobuttonDayopickPostCategory6holediary.setOnClickListener {
                setCategoryPostList(getString(R.string.sixHoleDiary_eng))
            }
            radiobuttonDayopickPostCategoryDigital.setOnClickListener {
                setCategoryPostList(getString(R.string.digital_eng))
            }
            radiobuttonDayopickPostCategoryEtc.setOnClickListener {
                setCategoryPostList(getString(R.string.etc_eng))
            }
        }
    }

    private fun setCategoryPostList(selectCategory: String) {
        currentCategory = selectCategory
        if(selectCategory == getString(R.string.all_eng)) {
            homeViewModel.currentCategory = selectCategory
            homeViewModel.requestHomeDayoPickPostList()
        } else if(!selectCategory.isNullOrEmpty()) {
            homeViewModel.currentCategory = selectCategory
            homeViewModel.requestHomeDayoPickPostListCategory(selectCategory)
        }
    }

    private fun setPostLikeClickListener() {
        homeDayoPickAdapter.setOnItemClickListener(object : HomeDayoPickAdapter.OnItemClickListener{
            override fun likePostClick(btn: ImageButton, data: PostContent, pos: Int) {
                if(!data.heart) {
                    btn.setImageDrawable(resources.getDrawable(R.drawable.ic_like_pressed, context?.theme))
                    homeViewModel.requestLikePost(RequestLikePost(data.id))
                } else {
                    btn.setImageDrawable(resources.getDrawable(R.drawable.ic_like_default, context?.theme))
                    homeViewModel.requestUnlikePost(data.id)
                }.let { it.invokeOnCompletion { throwable ->
                        when (throwable) {
                            is CancellationException -> Log.e("Post Like Click", "CANCELLED")
                            null -> {
                                if(currentCategory != getString(R.string.all_eng)) {
                                    homeViewModel.requestHomeDayoPickPostListCategory(currentCategory)
                                } else {
                                    homeViewModel.requestHomeDayoPickPostList()
                                }
                            }
                        }
                } }
            }
        })
    }
}