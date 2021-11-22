package com.daily.dayo.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.daily.dayo.databinding.FragmentDayoPickPostListBinding
import com.daily.dayo.home.adapter.HomeDayoPickAdapater
import com.daily.dayo.home.viewmodel.HomeViewModel
import com.daily.dayo.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DayoPickPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentDayoPickPostListBinding>()
    private val homeViewModel by activityViewModels<HomeViewModel>()

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
        binding.rvDayopickPost.adapter = HomeDayoPickAdapater()
    }

    private fun setDayoPickPostListCollect() {
        with(viewLifecycleOwner){
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    homeViewModel.postList.collect { postList ->
                        postList?.let {
                            with(binding.rvDayopickPost.adapter as HomeDayoPickAdapater) {
                                submitList(postList)
                            }
                        }
                    }
                }
            }
        }
    }
}