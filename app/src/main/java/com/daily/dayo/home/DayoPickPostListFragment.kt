package com.daily.dayo.home

import android.os.Bundle
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
import com.daily.dayo.util.GridSpacingItemDecoration
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DayoPickPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentDayoPickPostListBinding>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private lateinit var homeDayoPickAdapter : HomeDayoPickAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDayoPickPostListBinding.inflate(inflater, container, false)
        setRvDayoPickPostAdapter()
        setDayoPickPostListCollect()
        setPostLikeClickListener()
        return binding.root
    }

    private fun setRvDayoPickPostAdapter() {
        homeDayoPickAdapter = HomeDayoPickAdapter()
        binding.rvDayopickPost.adapter = homeDayoPickAdapter
        binding.rvDayopickPost.addItemDecoration(GridSpacingItemDecoration(2, 29.toPx(), 4.toPx()))
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
                homeViewModel.requestHomePostList()
            }
            radiobuttonDayopickPostCategoryScheduler.setOnClickListener {
                homeViewModel.requestHomePostListCategory(getString(R.string.scheduler_eng))
            }
            radiobuttonDayopickPostCategoryStudyplanner.setOnClickListener {
                homeViewModel.requestHomePostListCategory(getString(R.string.studyplanner_eng))
            }
            radiobuttonDayopickPostCategoryPocketbook.setOnClickListener {
                homeViewModel.requestHomePostListCategory(getString(R.string.pocketbook_eng))
            }
            radiobuttonDayopickPostCategory6holediary.setOnClickListener {
                homeViewModel.requestHomePostListCategory(getString(R.string.sixHoleDiary_eng))
            }
            radiobuttonDayopickPostCategoryDigital.setOnClickListener {
                homeViewModel.requestHomePostListCategory(getString(R.string.digital_eng))
            }
            radiobuttonDayopickPostCategoryDigital.setOnClickListener {
                homeViewModel.requestHomePostListCategory(getString(R.string.etc_eng))
            }
        }
    }

    private fun setPostLikeClickListener() {
        homeDayoPickAdapter.setOnItemClickListener(object : HomeDayoPickAdapter.OnItemClickListener{
            override fun likePostClick(btn: ImageButton, data: PostContent, pos: Int) {
                if(true) { // TODO: Like한 Post인지 아닌지 판단하는 조건 필요
                    btn.setImageDrawable(resources.getDrawable(R.drawable.ic_like_pressed, context?.theme))
                    homeViewModel.requestLikePost(RequestLikePost(data.id))
                } else {
                    btn.setImageDrawable(resources.getDrawable(R.drawable.ic_like_default, context?.theme))
                    homeViewModel.requestUnlikePost(data.id)
                }
            }
        })
    }

    fun Int.toPx() : Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}