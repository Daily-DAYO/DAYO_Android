package com.daily.dayo.presentation.fragment.mypage.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.common.GlideLoadUtil.PROFILE_USER_THUMBNAIL_SIZE
import com.daily.dayo.common.GlideLoadUtil.loadImageViewProfile
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentProfileBinding
import com.daily.dayo.domain.model.Folder
import com.daily.dayo.presentation.adapter.ProfileFolderListAdapter
import com.daily.dayo.presentation.adapter.ProfileFragmentPagerStateAdapter
import com.daily.dayo.presentation.viewmodel.ProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers

class ProfileFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileBinding> {
        onDestroyBindingView()
    }
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private val args by navArgs<ProfileFragmentArgs>()
    private var profileFolderListAdapter: ProfileFolderListAdapter? = null
    private var glideRequestManager: RequestManager? = null
    private var pagerAdapter: ProfileFragmentPagerStateAdapter? = null
    private var mediator: TabLayoutMediator? = null
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
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setProfile()
    }

    private fun onDestroyBindingView() {
        mediator?.detach()
        mediator = null
        profileFolderListAdapter = null
        glideRequestManager = null
        pagerAdapter = null
        with(binding.pagerProfile) {
            unregisterOnPageChangeCallback(pageChangeCallBack)
            adapter = null
        }
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
        getMyProfileDescription()
    }

    private fun setOtherProfile() {
        requireActivity().findViewById<ConstraintLayout>(R.id.layout_bottom_navigation_main).visibility =
            View.GONE
        setRvProfileFolderListAdapter()
        setProfileFolderList()
        setOtherProfileOptionClickListener()
        getOtherProfileDescription()
    }

    private fun getMyProfileDescription() {
        profileViewModel.requestMyProfile()
    }

    private fun getOtherProfileDescription() {
        profileViewModel.requestOtherProfile(memberId = profileViewModel.profileMemberId)
    }

    private fun setProfileDescription() {
        profileViewModel.profileInfo.observe(viewLifecycleOwner) {
            it?.let { profile ->
                binding.profile = profile
                glideRequestManager?.let { requestManager ->
                    loadImageViewProfile(
                        requestManager = requestManager,
                        width = PROFILE_USER_THUMBNAIL_SIZE,
                        height = PROFILE_USER_THUMBNAIL_SIZE,
                        imgName = profile.profileImg,
                        imgView = binding.imgProfileUserProfile
                    )
                }

                setFollowButtonClickListener()
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
        binding.pagerProfile.registerOnPageChangeCallback(pageChangeCallBack)
    }

    private fun setBackButtonClickListener() {
        binding.btnProfileBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setViewPager() {
        pagerAdapter =
            ProfileFragmentPagerStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        pagerAdapter?.addFragment(ProfileFolderListFragment())
        pagerAdapter?.addFragment(ProfileLikePostListFragment())
        pagerAdapter?.addFragment(ProfileBookmarkPostListFragment())
        binding.pagerProfile.adapter = pagerAdapter

        mediator = TabLayoutMediator(binding.tabsProfile, binding.pagerProfile) { tab, position ->
            when (position) {
                0 -> tab.text = "작성한 글"
                1 -> tab.text = "좋아요"
                2 -> tab.text = "북마크"
            }
        }
        mediator?.attach()
    }

    private fun setFollowButtonClickListener() {
        binding.btnProfileFollow.setOnDebounceClickListener {
            when (profileViewModel.profileInfo.value?.follow) {
                false -> setFollow()
                true -> setUnfollow()
                else -> {}
            }
        }
    }

    private fun setFollow() {
        profileViewModel.requestCreateFollow(followerId = profileViewModel.profileMemberId)
        profileViewModel.followSuccess.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { success ->
                if (success) {
                    getOtherProfileDescription()
                } else {
                    Toast.makeText(requireContext(), "네트워크 연결 상태가 불안정합니다", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun setUnfollow() {
        profileViewModel.requestDeleteFollow(followerId = profileViewModel.profileMemberId)
        profileViewModel.unfollowSuccess.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { success ->
                if (success) {
                    getOtherProfileDescription()
                } else {
                    Toast.makeText(requireContext(), "네트워크 연결 상태가 불안정합니다", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun setRvProfileFolderListAdapter() {
        profileFolderListAdapter = glideRequestManager?.let {
            ProfileFolderListAdapter(
                requestManager = it,
                mainDispatcher = Dispatchers.Main
            )
        }
        binding.rvProfileFolder.adapter = profileFolderListAdapter
        profileFolderListAdapter?.setOnItemClickListener(object :
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
        profileViewModel.folderList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { folderList ->
                        profileFolderListAdapter?.submitList(folderList)
                    }
                }
                else -> {}
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