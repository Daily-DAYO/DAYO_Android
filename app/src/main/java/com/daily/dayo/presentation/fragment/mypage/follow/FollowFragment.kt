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
import com.daily.dayo.R
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentFollowBinding
import com.daily.dayo.presentation.adapter.FollowFragmentPagerStateAdapter
import com.daily.dayo.presentation.viewmodel.FollowViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator

class FollowFragment : Fragment() {
    private var binding by autoCleared<FragmentFollowBinding> { onDestroyBindingView() }
    private val followViewModel by activityViewModels<FollowViewModel>()
    private var mediator: TabLayoutMediator? = null
    private var pagerAdapter: FollowFragmentPagerStateAdapter? = null
    private val args by navArgs<FollowFragmentArgs>()
    private val tabSelectedListener = object : OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            when (tab.position) {
                0 -> followViewModel.requestListAllFollower(args.memberId)
                1 -> followViewModel.requestListAllFollowing(args.memberId)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}
        override fun onTabReselected(tab: TabLayout.Tab) {}
    }

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

        setTabLayout()
        setBackButtonClickListener()
        setFollowFragmentDescription()
        setOnTabSelectedListener()
    }

    private fun onDestroyBindingView() {
        binding.apply {
            pagerFollow.adapter = null
            tabsFollow.removeOnTabSelectedListener(tabSelectedListener)
        }
        mediator?.detach()
        mediator = null
        pagerAdapter = null
    }

    private fun setTabLayout() {
        pagerAdapter =
            FollowFragmentPagerStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        pagerAdapter?.addFragment(FollowerListFragment())
        pagerAdapter?.addFragment(FollowingListFragment())
        binding.pagerFollow.adapter = pagerAdapter

        mediator = TabLayoutMediator(binding.tabsFollow, binding.pagerFollow) { tab, position ->
            tab.setCustomView(R.layout.tab_follow)
            val tvFollow = tab.customView?.findViewById<TextView>(R.id.tv_follow)
            val tvFollowCount = tab.customView?.findViewById<TextView>(R.id.tv_follow_count)
            when (position) {
                0 -> {
                    tvFollow?.text = getText(R.string.follower)
                    followViewModel.requestListAllFollower(args.memberId)
                    followViewModel.followerCount.observe(viewLifecycleOwner) {
                        when (it.status) {
                            Status.SUCCESS -> {
                                it.data?.let { followerCount ->
                                    tvFollowCount?.text = followerCount.toString()
                                }
                            }
                            Status.ERROR -> {
                                "0"
                            }
                            Status.ERROR -> {
                                tvFollowCount?.text = "0"
                            }
                            Status.LOADING -> {}
                        }
                    }
                }
                1 -> {
                    tvFollow?.text = getText(R.string.following)
                    followViewModel.requestListAllFollowing(args.memberId)
                    followViewModel.followingCount.observe(viewLifecycleOwner) {
                        when (it.status) {
                            Status.SUCCESS -> {
                                it.data?.let { followingCount ->
                                    tvFollowCount?.text = followingCount.toString()
                                }
                            }
                            Status.ERROR -> {
                                tvFollowCount?.text = "0"
                            }
                            Status.LOADING -> {}
                        }
                    }
                }
            }
        }
        mediator?.attach()

        binding.pagerFollow.post {
            binding.pagerFollow.setCurrentItem(args.initPosition, false)
        }
    }

    private fun setOnTabSelectedListener() {
        binding.tabsFollow.addOnTabSelectedListener(tabSelectedListener)
    }

    private fun setBackButtonClickListener() {
        binding.btnFollowBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setFollowFragmentDescription() {
        binding.tvFollowUserNickname.text = args.nickname
        followViewModel.memberId = args.memberId
    }
}