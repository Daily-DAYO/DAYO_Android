package com.daily.dayo.presentation.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.daily.dayo.R
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentHomeBinding
import com.daily.dayo.presentation.adapter.HomeFragmentPagerStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var binding by autoCleared<FragmentHomeBinding>(onDestroy = {
        onDestroyBindingView()
    })
    private var mediator: TabLayoutMediator? = null
    private var pagerAdapter: HomeFragmentPagerStateAdapter? = null
    private val pageChangeCallBack = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            pagerAdapter?.refreshFragment(position, pagerAdapter!!.fragments[position])
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        setSearchClickListener()
    }

    private fun onDestroyBindingView() {
        mediator?.detach()
        mediator = null
        pagerAdapter = null
        with (binding.pagerHomePost) {
            unregisterOnPageChangeCallback(pageChangeCallBack)
            adapter = null
        }
    }

    private fun setSearchClickListener() {
        binding.btnPostSearch.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun initViewPager() {
        pagerAdapter =
            HomeFragmentPagerStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        pagerAdapter?.addFragment(HomeDayoPickPostListFragment())
        pagerAdapter?.addFragment(HomeNewPostListFragment())
        for (i in 0 until pagerAdapter!!.itemCount) {
            pagerAdapter?.refreshFragment(i, pagerAdapter!!.fragments[i])
        }

        with(binding.pagerHomePost) {
            isUserInputEnabled = false // DISABLE SWIPE
            adapter = pagerAdapter
            registerOnPageChangeCallback(pageChangeCallBack)
        }

        initTabLayout()
    }

    private fun initTabLayout() {
        mediator = TabLayoutMediator(
            binding.tabsActionbarHomeCategory,
            binding.pagerHomePost
        ) { tab, position ->
            when (position) {
                0 ->
                    tab.text = "DAYO PICK"
                1 ->
                    tab.text = "NEW"
            }
        }
        mediator?.attach()
    }
}