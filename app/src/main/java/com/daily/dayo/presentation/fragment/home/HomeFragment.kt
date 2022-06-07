package com.daily.dayo.presentation.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.daily.dayo.R
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentHomeBinding
import com.daily.dayo.presentation.adapter.HomeFragmentPagerStateAdapter
import com.daily.dayo.presentation.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var binding by autoCleared<FragmentHomeBinding>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private lateinit var viewPager : ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewPager = binding.pagerHomePost
        tabLayout = binding.tabsActionbarHomeCategory

        setSearchClickListener()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val pagerAdapter = HomeFragmentPagerStateAdapter(requireActivity())
        pagerAdapter.addFragment(HomeDayoPickPostListFragment())
        pagerAdapter.addFragment(HomeNewPostListFragment())
        homeViewModel.requestDayoPickPostList()
        for (i in 0 until pagerAdapter.itemCount) {
            pagerAdapter.refreshFragment(i,pagerAdapter.fragments[i])
            Log.e("Refresh Fragment", "Page ${i+1}")
        }

        viewPager.adapter = pagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("ViewPagerFragment", "Page ${position+1}")
                when (position){
                    0 -> homeViewModel.requestDayoPickPostList()
                    1 -> homeViewModel.requestHomeNewPostList()
                }
            }
        })

        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            when (position){
                0 ->
                    tab.text = "DAYO PICK"
                1 ->
                    tab.text = "NEW"
            }
        }.attach()
    }

    private fun setSearchClickListener() {
        binding.btnPostSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }
}