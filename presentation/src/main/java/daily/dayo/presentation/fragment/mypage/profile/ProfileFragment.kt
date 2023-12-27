package daily.dayo.presentation.fragment.mypage.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.tabs.TabLayoutMediator
import daily.dayo.domain.model.Folder
import daily.dayo.presentation.R
import daily.dayo.presentation.adapter.ProfileFolderListAdapter
import daily.dayo.presentation.adapter.ProfileFragmentPagerStateAdapter
import daily.dayo.presentation.common.GlideLoadUtil.PROFILE_USER_THUMBNAIL_SIZE
import daily.dayo.presentation.common.GlideLoadUtil.loadImageViewProfile
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentProfileBinding
import daily.dayo.presentation.viewmodel.AccountViewModel
import daily.dayo.presentation.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileBinding> {
        onDestroyBindingView()
    }
    private val accountViewModel by activityViewModels<AccountViewModel>()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScrollPosition()
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
        profileViewModel.cleanUpFolders()
    }

    private fun setScrollPosition() {
        with(binding) {
            ViewCompat.requestApplyInsets(layoutProfile)
            ViewCompat.requestApplyInsets(layoutProfileAppBar)
        }
    }

    private fun setProfile() {
        if (args.memberId == accountViewModel.getCurrentUserInfo().memberId) {
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
        getProfileFolderList()
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
                binding.profile = profile.data
                glideRequestManager?.let { requestManager ->
                    profile.data?.let { profile ->
                        loadImageViewProfile(
                            requestManager = requestManager,
                            width = PROFILE_USER_THUMBNAIL_SIZE,
                            height = PROFILE_USER_THUMBNAIL_SIZE,
                            imgName = profile.profileImg,
                            imgView = binding.imgProfileUserProfile
                        )
                    }
                }

                setFollowButtonClickListener()
                profile.data?.let { profile ->
                    profile.memberId?.let { memberId ->
                        setFollowerCountButtonClickListener(
                            memberId = memberId,
                            nickname = profile.nickname
                        )
                        setFollowingCountButtonClickListener(
                            memberId = memberId,
                            nickname = profile.nickname
                        )
                    }
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
            when (profileViewModel.profileInfo.value?.data?.follow) {
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
                requestManager = it
            )
        }
        binding.rvProfileFolder.adapter = profileFolderListAdapter
        profileFolderListAdapter?.setOnItemClickListener(object :
            ProfileFolderListAdapter.OnItemClickListener {
            override fun onItemClick(v: View, folder: Folder, pos: Int) {
                findNavController().navigateSafe(
                    currentDestinationId = R.id.ProfileFragment,
                    action = R.id.action_profileFragment_to_folderFragment,
                    args = ProfileFragmentDirections.actionProfileFragmentToFolderFragment(
                        folderId = folder.folderId!!
                    ).arguments
                )
            }
        })
    }

    private fun getProfileFolderList() {
        profileViewModel.requestFolderList(
            memberId = profileViewModel.profileMemberId,
            isMine = false
        )
    }

    private fun setProfileFolderList() {
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
            findNavController().navigateSafe(
                currentDestinationId = R.id.ProfileFragment,
                action = R.id.action_profileFragment_to_profileOptionFragment,
                args = ProfileFragmentDirections.actionProfileFragmentToProfileOptionFragment(
                    isMine = true,
                    memberId = profileViewModel.profileMemberId
                ).arguments
            )
        }
    }

    private fun setOtherProfileOptionClickListener() {
        binding.btnProfileOption.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.ProfileFragment,
                action = R.id.action_profileFragment_to_profileOptionFragment,
                args = ProfileFragmentDirections.actionProfileFragmentToProfileOptionFragment(
                    isMine = false,
                    memberId = profileViewModel.profileMemberId
                ).arguments
            )
        }
    }

    private fun setFollowerCountButtonClickListener(memberId: String, nickname: String) {
        binding.layoutProfileFollowerCount.setOnDebounceClickListener { v ->
            Navigation.findNavController(v).navigateSafe(
                currentDestinationId = R.id.ProfileFragment,
                action = R.id.action_profileFragment_to_followFragment,
                args = ProfileFragmentDirections.actionProfileFragmentToFollowFragment(
                    memberId = memberId,
                    nickname = nickname,
                    initPosition = 0
                ).arguments
            )
        }
    }

    private fun setFollowingCountButtonClickListener(memberId: String, nickname: String) {
        binding.layoutProfileFollowingCount.setOnDebounceClickListener { v ->
            Navigation.findNavController(v).navigateSafe(
                currentDestinationId = R.id.ProfileFragment,
                action = R.id.action_profileFragment_to_followFragment,
                args = ProfileFragmentDirections.actionProfileFragmentToFollowFragment(
                    memberId = memberId,
                    nickname = nickname,
                    initPosition = 1
                ).arguments
            )
        }
    }
}