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
import com.daily.dayo.common.GlideLoadUtil.loadImageBackgroundProfile
import com.daily.dayo.common.GlideLoadUtil.loadImageViewProfile
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentMyPageBinding
import com.daily.dayo.presentation.adapter.ProfileFragmentPagerStateAdapter
import com.daily.dayo.presentation.viewmodel.ProfileViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyPageFragment : Fragment() {
    private var binding by autoCleared<FragmentMyPageBinding>()
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private lateinit var glideRequestManager: RequestManager
    private lateinit var pagerAdapter: ProfileFragmentPagerStateAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProfileDescription()
        setViewPager()
        setViewPagerChangeEvent()
        setMyProfileOptionClickListener()
    }

    private fun setProfileDescription() {
        profileViewModel.requestProfile(memberId = DayoApplication.preferences.getCurrentUser().memberId!!)
        profileViewModel.profileInfo.observe(viewLifecycleOwner) {
            it?.let { profile ->
                binding.profile = profile
                CoroutineScope(Dispatchers.Main).launch {
                    val userProfileThumbnailImage = withContext(Dispatchers.IO) {
                        loadImageBackgroundProfile(
                            requestManager = glideRequestManager,
                            width = 70, height = 70, imgName = profile.profileImg
                        )
                    }
                    loadImageViewProfile(
                        requestManager = glideRequestManager,
                        width = 70,
                        height = 70,
                        img = userProfileThumbnailImage,
                        imgView = binding.imgMyPageUserProfile
                    )
                }

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

    private fun setViewPagerChangeEvent() {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                pagerAdapter.refreshFragment(position, pagerAdapter.fragments[position])
            }
        })
    }

    private fun setViewPager() {
        viewPager = binding.pagerMyPage
        tabLayout = binding.tabsMyPage
        pagerAdapter = ProfileFragmentPagerStateAdapter(this)
        pagerAdapter.addFragment(ProfileFolderListFragment())
        pagerAdapter.addFragment(ProfileLikePostListFragment())
        pagerAdapter.addFragment(ProfileBookmarkPostListFragment())
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "작성한 글"
                1 -> tab.text = "좋아요"
                2 -> tab.text = "북마크"
            }
        }.attach()
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