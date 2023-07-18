package com.daily.dayo.presentation.fragment.mypage.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.GlideLoadUtil.PROFILE_USER_THUMBNAIL_SIZE
import com.daily.dayo.common.GlideLoadUtil.loadImageViewProfile
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentMyPageBinding
import com.daily.dayo.presentation.adapter.ProfileFragmentPagerStateAdapter
import com.daily.dayo.presentation.viewmodel.ProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MyPageFragment : Fragment() {
    private var binding by autoCleared<FragmentMyPageBinding> { onDestroyBindingView() }
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private var glideRequestManager: RequestManager? = null
    private var mediator: TabLayoutMediator? = null
    private var pagerAdapter: ProfileFragmentPagerStateAdapter? = null
    private val pageChangeCallBack = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            pagerAdapter?.let {
                it.refreshFragment(position, it.fragments[position])
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProfileDescription()
        setViewPager()
        setViewPagerChangeEvent()
        setMyProfileOptionClickListener()
    }

    private fun onDestroyBindingView() {
        mediator?.detach()
        mediator = null
        glideRequestManager = null
        pagerAdapter = null
        with(binding.pagerMyPage) {
            unregisterOnPageChangeCallback(pageChangeCallBack)
            adapter = null
        }
    }

    private fun setProfileDescription() {
        profileViewModel.requestMyProfile()
        profileViewModel.profileInfo.observe(viewLifecycleOwner) {
            it?.let { profile ->
                binding.profile = profile
                glideRequestManager?.let { requestManager ->
                    loadImageViewProfile(
                        requestManager = requestManager,
                        width = PROFILE_USER_THUMBNAIL_SIZE,
                        height = PROFILE_USER_THUMBNAIL_SIZE,
                        imgName = profile.profileImg,
                        imgView = binding.imgMyPageUserProfile
                    )
                }

                profile.memberId?.let {
                    setFollowerCountButtonClickListener(
                        memberId = profile.memberId,
                        nickname = profile.nickname
                    )
                    setFollowingCountButtonClickListener(
                        memberId = profile.memberId,
                        nickname = profile.nickname
                    )
                }
            }
        }
    }

    private fun setViewPagerChangeEvent() {
        binding.pagerMyPage.registerOnPageChangeCallback(pageChangeCallBack)
    }

    private fun setViewPager() {
        pagerAdapter =
            ProfileFragmentPagerStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        pagerAdapter?.addFragment(ProfileFolderListFragment())
        pagerAdapter?.addFragment(ProfileLikePostListFragment())
        pagerAdapter?.addFragment(ProfileBookmarkPostListFragment())
        binding.pagerMyPage.adapter = pagerAdapter

        mediator = TabLayoutMediator(binding.tabsMyPage, binding.pagerMyPage) { tab, position ->
            when (position) {
                0 -> tab.text = "작성한 글"
                1 -> tab.text = "좋아요"
                2 -> tab.text = "북마크"
            }
        }
        mediator?.attach()
    }

    private fun setMyProfileOptionClickListener() {
        binding.btnMyPageOption.setOnDebounceClickListener {
            findNavController().navigate(
                MyPageFragmentDirections.actionMyPageFragmentToProfileOptionFragment(
                    isMine = true,
                    memberId = DayoApplication.preferences.getCurrentUser().memberId!!
                )
            )
        }
    }

    private fun setFollowerCountButtonClickListener(memberId: String, nickname: String) {
        binding.layoutMyPageFollowerCount.setOnDebounceClickListener { v ->
            Navigation.findNavController(v).navigate(
                MyPageFragmentDirections.actionMyPageFragmentToFollowFragment(
                    memberId = memberId,
                    nickname = nickname,
                    initPosition = 0
                )
            )
        }
    }

    private fun setFollowingCountButtonClickListener(memberId: String, nickname: String) {
        binding.layoutMyPageFollowingCount.setOnDebounceClickListener { v ->
            Navigation.findNavController(v).navigate(
                MyPageFragmentDirections.actionMyPageFragmentToFollowFragment(
                    memberId = memberId,
                    nickname = nickname,
                    initPosition = 1
                )
            )
        }
    }
}