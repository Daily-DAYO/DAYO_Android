package com.daily.dayo.presentation.fragment.mypage.follow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentFollowBinding
import com.daily.dayo.presentation.adapter.FollowFragmentPagerStateAdapter
import com.daily.dayo.presentation.viewmodel.FollowViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FollowFragment : Fragment() {
    private var binding by autoCleared<FragmentFollowBinding>()
    private val followViewModel by activityViewModels<FollowViewModel>()
    private lateinit var pagerAdapter: FollowFragmentPagerStateAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private val args by navArgs<FollowFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewPager()
        setBackButtonClickListener()
        setFollowFragmentDescription()
    }

    private fun setViewPager(){
        viewPager = binding.pagerFollow
        tabLayout = binding.tabsFollow
        pagerAdapter = FollowFragmentPagerStateAdapter(requireActivity())
        pagerAdapter.addFragment(FollowerListFragment())
        pagerAdapter.addFragment(FollowingListFragment())
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            when (position){
                0 -> tab.text = "팔로워"
                1 -> tab.text = "팔로잉"
            }
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("ViewPagerFragment", "Page ${position+1}")
            }
        })
    }

    private fun setBackButtonClickListener(){
        binding.btnFollowBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setFollowFragmentDescription(){
        binding.tvFollowUserNickname.text = args.nickname
        followViewModel.setMemberId(args.memberId)
    }
}