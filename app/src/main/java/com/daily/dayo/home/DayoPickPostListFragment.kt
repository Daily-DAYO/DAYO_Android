package com.daily.dayo.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.daily.dayo.databinding.FragmentDayoPickPostListBinding
import com.daily.dayo.home.adapter.HomeDayoPickAdapter
import com.daily.dayo.home.viewmodel.HomeViewModel
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
        return binding.root
    }

    private fun setRvDayoPickPostAdapter() {
        homeDayoPickAdapter = HomeDayoPickAdapter()
        binding.rvDayopickPost.adapter = homeDayoPickAdapter
    }

    private fun setDayoPickPostListCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.postList.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { postList ->
                                homeDayoPickAdapter.submitList(postList.posts)
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
    }
}