package com.daily.dayo.presentation.fragment.mypage.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.daily.dayo.R
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
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
            tab.setCustomView(R.layout.tab_follow)
            val tvFollow = tab.customView?.findViewById<TextView>(R.id.tv_follow)
            val tvFollowCount = tab.customView?.findViewById<TextView>(R.id.tv_follow_count)

            when (position){
                0 -> {
                    tvFollow?.text = getText(R.string.follower)
                    tvFollowCount?.text = followViewModel.followerCount.value.toString()
                    followViewModel.memberId.observe(viewLifecycleOwner) { memberId ->
                        followViewModel.requestListAllFollower(memberId)
                    }
                    followViewModel.followerCount.observe(viewLifecycleOwner) {
                        when(it.status){
                            Status.SUCCESS -> {
                                it.data?.let { followerCount ->
                                    tvFollowCount?.text = followerCount.toString()
                                }
                            }
                            Status.ERROR -> { "0" }
                            Status.LOADING -> {}
                        }
                    }
                }
                1 -> {
                    tvFollow?.text = getText(R.string.following)
                    followViewModel.memberId.observe(viewLifecycleOwner) {
                        followViewModel.requestListAllFollowing(it)
                    }
                    followViewModel.followingCount.observe(viewLifecycleOwner) {
                        when(it.status){
                            Status.SUCCESS -> {
                                it.data?.let { followingCount ->
                                    tvFollowCount?.text = followingCount.toString()
                                }
                            }
                            Status.ERROR -> { tvFollowCount?.text = "0" }
                            Status.LOADING -> {}
                        }
                    }
                }
            }
        }.attach()

        viewPager.post{
            viewPager.setCurrentItem(args.initPosition, false)
        }
    }

    private fun setBackButtonClickListener(){
        binding.btnFollowBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setFollowFragmentDescription(){
        binding.tvFollowUserNickname.text = args.nickname
        followViewModel.setMemberId(args.memberId)
    }
}