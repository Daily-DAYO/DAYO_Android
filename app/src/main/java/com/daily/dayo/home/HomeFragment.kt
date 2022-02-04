package com.daily.dayo.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.daily.dayo.databinding.FragmentHomeBinding
import com.daily.dayo.home.adapter.HomeFragmentPagerStateAdapter
import com.daily.dayo.home.viewmodel.HomeViewModel
import com.daily.dayo.util.autoCleared
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

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val pagerAdapter = HomeFragmentPagerStateAdapter(requireActivity())
        pagerAdapter.addFragment(DayoPickPostListFragment())
        pagerAdapter.addFragment(NewPostListFragment())
        homeViewModel.requestHomePostList()
        for (i in 0 until pagerAdapter.itemCount) {
            pagerAdapter.refreshFragment(i,pagerAdapter.fragments[i])
            Log.e("Refresh Fragment", "Page ${i+1}")
        }

        viewPager.adapter = pagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("ViewPagerFragment", "Page ${position+1}")
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
}