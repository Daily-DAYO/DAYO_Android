package com.daily.dayo.presentation.fragment.mypage.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.common.GlideLoadUtil.loadImageBackgroundProfile
import com.daily.dayo.common.GlideLoadUtil.loadImageViewProfile
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentProfileBinding
import com.daily.dayo.domain.model.Folder
import com.daily.dayo.presentation.adapter.ProfileFolderListAdapter
import com.daily.dayo.presentation.adapter.ProfileFragmentPagerStateAdapter
import com.daily.dayo.presentation.viewmodel.ProfileViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileBinding>()
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private val args by navArgs<ProfileFragmentArgs>()
    private lateinit var profileFolderListAdapter: ProfileFolderListAdapter
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
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setProfile()
    }

    private fun setProfile() {
        if (args.memberId == DayoApplication.preferences.getCurrentUser().memberId) {
            profileViewModel.profileMemberId = args.memberId!!
            binding.isMine = true
            setMyProfile()
        } else {
            profileViewModel.profileMemberId = args.memberId!!
            binding.isMine = false
            setOtherProfile()
        }

        setProfileDescription()
        setBackButtonClickListener()
    }

    private fun setMyProfile() {
        setViewPager()
        setViewPagerChangeEvent()
        setMyProfileOptionClickListener()
    }

    private fun setOtherProfile() {
        requireActivity().findViewById<ConstraintLayout>(R.id.layout_bottom_navigation_main).visibility =
            View.GONE
        setRvProfileFolderListAdapter()
        setProfileFolderList()
        setOtherProfileOptionClickListener()
    }

    private fun setProfileDescription() {
        profileViewModel.requestProfile(memberId = profileViewModel.profileMemberId)
        profileViewModel.profileInfo.observe(viewLifecycleOwner) {
            it?.let { profile ->
                binding.profile = profile
                viewLifecycleOwner.lifecycleScope.launch {
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
                        imgView = binding.imgProfileUserProfile
                    )
                }

                setFollowButtonClickListener(follow = profile.follow)
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

    private fun setBackButtonClickListener() {
        binding.btnProfileBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setViewPager() {
        viewPager = binding.pagerProfile
        tabLayout = binding.tabsProfile
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

    private fun setFollowButtonClickListener(follow: Boolean) {
        binding.btnProfileFollow.setOnDebounceClickListener {
            when (follow) {
                false -> setFollow()
                true -> setUnfollow()
            }
        }
    }

    private fun setFollow() {
        profileViewModel.requestCreateFollow(followerId = profileViewModel.profileMemberId)
        profileViewModel.followSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                setProfileDescription()
            }
        }
    }

    private fun setUnfollow() {
        profileViewModel.requestDeleteFollow(followerId = profileViewModel.profileMemberId)
        profileViewModel.unfollowSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                setProfileDescription()
            }
        }
    }

    private fun setRvProfileFolderListAdapter() {
        profileFolderListAdapter = ProfileFolderListAdapter(
            requestManager = glideRequestManager,
            mainDispatcher = Dispatchers.Main,
            ioDispatcher = Dispatchers.IO
        )
        binding.rvProfileFolder.adapter = profileFolderListAdapter
        profileFolderListAdapter.setOnItemClickListener(object :
            ProfileFolderListAdapter.OnItemClickListener {
            override fun onItemClick(v: View, folder: Folder, pos: Int) {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToFolderFragment(
                        folderId = folder.folderId!!
                    )
                )
            }
        })
    }

    private fun setProfileFolderList() {
        profileViewModel.requestFolderList(
            memberId = profileViewModel.profileMemberId,
            isMine = false
        )
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.folderList.observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { folderList ->
                            profileFolderListAdapter.submitList(folderList)
                        }
                    }
                }
            }
        }
    }

    private fun setMyProfileOptionClickListener() {
        binding.btnProfileOption.setOnDebounceClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToProfileOptionFragment(
                    isMine = true,
                    memberId = profileViewModel.profileMemberId
                )
            )
        }
    }

    private fun setOtherProfileOptionClickListener() {
        binding.btnProfileOption.setOnDebounceClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionProfileFragmentToProfileOptionFragment(
                    isMine = false,
                    memberId = profileViewModel.profileMemberId
                )
            )
        }
    }

    private fun setFollowerCountButtonClickListener(memberId: String, nickname: String) {
        binding.layoutProfileFollowerCount.setOnDebounceClickListener { v ->
            Navigation.findNavController(v).navigate(
                ProfileFragmentDirections.actionProfileFragmentToFollowFragment(
                    memberId = memberId,
                    nickname = nickname,
                    initPosition = 0
                )
            )
        }
    }

    private fun setFollowingCountButtonClickListener(memberId: String, nickname: String) {
        binding.layoutProfileFollowingCount.setOnDebounceClickListener { v ->
            Navigation.findNavController(v).navigate(
                ProfileFragmentDirections.actionProfileFragmentToFollowFragment(
                    memberId = memberId,
                    nickname = nickname,
                    initPosition = 1
                )
            )
        }
    }
}