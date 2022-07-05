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
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.common.GlideApp
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentProfileBinding
import com.daily.dayo.domain.model.Folder
import com.daily.dayo.presentation.adapter.ProfileFolderListAdapter
import com.daily.dayo.presentation.adapter.ProfileFragmentPagerStateAdapter
import com.daily.dayo.presentation.viewmodel.ProfileViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileBinding>()
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private val args by navArgs<ProfileFragmentArgs>()
    private lateinit var profileFolderListAdapter: ProfileFolderListAdapter
    private lateinit var pagerAdapter : ProfileFragmentPagerStateAdapter
    private lateinit var viewPager : ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProfile()
    }

    override fun onResume() {
        super.onResume()
        setProfile()
    }

    private fun setProfile(){
        if(args.memberId == null) {
            profileViewModel.profileMemberId = DayoApplication.preferences.getCurrentUser().memberId.toString()
            binding.isMine = true
            setMyProfile()
        }
        else if(args.memberId == DayoApplication.preferences.getCurrentUser().memberId){
            profileViewModel.profileMemberId = args.memberId!!
            binding.isMine = true
            setMyProfile()
        }
        else {
            profileViewModel.profileMemberId = args.memberId!!
            binding.isMine = false
            setOtherProfile()
        }

        setProfileDescription()
    }

    private fun setMyProfile(){
        setViewPager()
        setViewPagerChangeEvent()
        setMyProfileOptionClickListener()
    }

    private fun setOtherProfile(){
        requireActivity().findViewById<ConstraintLayout>(R.id.layout_bottom_navigation_main).visibility = View.GONE
        setRvProfileFolderListAdapter()
        setProfileFolderList()
        setBackButtonClickListener()
        setHomeButtonClickListener()
    }

    private fun setProfileDescription() {
        profileViewModel.requestProfile(memberId = profileViewModel.profileMemberId)
        profileViewModel.profileInfo.observe(viewLifecycleOwner) {
            when(it.status) {
                Status.SUCCESS -> {
                    it.data?.let { profile ->
                        binding.profile = profile
                        GlideApp.with(requireContext())
                            .load("http://117.17.198.45:8080/images/" + profile.profileImg)
                            .into(binding.imgProfileUserProfile)

                        setFollowButtonClickListener(follow = profile.follow)
                        setFollowerCountButtonClickListener(memberId = profile.memberId, nickname = profile.nickname)
                        setFollowingCountButtonClickListener(memberId = profile.memberId, nickname = profile.nickname)
                    }
                }
            }
        }
    }

    private fun setViewPagerChangeEvent(){
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                pagerAdapter.refreshFragment(position ,pagerAdapter.fragments[position])
            }
        })
    }

    private fun setBackButtonClickListener() {
        binding.btnProfileBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setHomeButtonClickListener(){
        binding.btnProfileHome.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }
    }

    private fun setViewPager(){
        viewPager = binding.pagerProfile
        tabLayout = binding.tabsProfile
        pagerAdapter = ProfileFragmentPagerStateAdapter(requireActivity())
        pagerAdapter.addFragment(ProfileFolderListFragment())
        pagerAdapter.addFragment(ProfileLikePostListFragment())
        pagerAdapter.addFragment(ProfileBookmarkPostListFragment())
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            when (position){
                0 -> tab.text = "작성한 글"
                1 -> tab.text = "좋아요"
                2 -> tab.text = "북마크"
            }
        }.attach()
    }

    private fun setFollowButtonClickListener(follow:Boolean) {
        binding.btnProfileFollow.setOnClickListener {
            when (follow) {
                false -> setFollow()
                true -> setUnfollow()
            }
        }
    }

    private fun setFollow(){
        profileViewModel.requestCreateFollow(followerId = profileViewModel.profileMemberId)
        profileViewModel.followSuccess.observe(viewLifecycleOwner) {
            if(it.getContentIfNotHandled() == true) {
                setProfileDescription()
            }
        }
    }

    private fun setUnfollow(){
        profileViewModel.requestDeleteFollow(followerId = profileViewModel.profileMemberId)
        profileViewModel.unfollowSuccess.observe(viewLifecycleOwner) {
            if(it.getContentIfNotHandled() == true) {
                setProfileDescription()
            }
        }
    }

    private fun setRvProfileFolderListAdapter(){
        profileFolderListAdapter = ProfileFolderListAdapter()
        binding.rvProfileFolder.adapter = profileFolderListAdapter
        profileFolderListAdapter.setOnItemClickListener(object : ProfileFolderListAdapter.OnItemClickListener{
            override fun onItemClick(v: View, folder: Folder, pos: Int) {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToFolderFragment(folderId = folder.folderId!!))
            }
        })
    }

    private fun setProfileFolderList(){
        profileViewModel.requestFolderList(memberId = profileViewModel.profileMemberId, isMine = false)
        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.folderList.observe(viewLifecycleOwner) {
                when(it.status){
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
        binding.btnProfileOption.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileOptionFragment)
        }
    }

    private fun setFollowerCountButtonClickListener(memberId:String, nickname:String){
        binding.layoutProfileFollowerCount.setOnClickListener { v ->
            Navigation.findNavController(v).navigate(
                ProfileFragmentDirections.actionProfileFragmentToFollowFragment(
                    memberId = memberId,
                    nickname = nickname
                )
            )
        }
    }

    private fun setFollowingCountButtonClickListener(memberId:String, nickname:String){
        binding.layoutProfileFollowingCount.setOnClickListener { v ->
            Navigation.findNavController(v).navigate(
                ProfileFragmentDirections.actionProfileFragmentToFollowFragment(
                    memberId = memberId,
                    nickname = nickname
                )
            )
        }
    }
}