package com.daily.dayo.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.daily.dayo.R
import com.daily.dayo.SharedManager
import com.daily.dayo.databinding.FragmentMyProfileBinding
import com.daily.dayo.profile.adapter.MyProfileFragmentPagerStateAdapter
import com.daily.dayo.profile.viewmodel.MyProfileViewModel
import com.daily.dayo.util.autoCleared
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MyProfileFragment : Fragment() {
    private var binding by autoCleared<FragmentMyProfileBinding>()
    private val myProfileViewModel by activityViewModels<MyProfileViewModel>()
    private lateinit var viewPager : ViewPager2
    private lateinit var tabLayout: TabLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        viewPager = binding.pagerMyProfile
        tabLayout = binding.tabsMyProfile

        setFollowerButtonClickListener()
        setFollowingButtonClickListener()
        setMyProfileOptionClickListener()
        setMyProfileEditButtonClickListener()
        setMyProfileDescription()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val pagerAdapter = MyProfileFragmentPagerStateAdapter(requireActivity())
        pagerAdapter.addFragment(ProfileFolderListFragment())
        pagerAdapter.addFragment(ProfileLikePostListFragment())
        pagerAdapter.addFragment(ProfileBookmarkPostListFragment())
        viewPager.adapter = pagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("ViewPagerFragment", "Page ${position+1}")
            }
        })

        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            when (position){
                0 -> tab.text = "작성한 글"
                1 -> tab.text = "좋아요"
                2 -> tab.text = "북마크"
            }
        }.attach()
    }

    private fun setFollowerButtonClickListener(){
        binding.layoutMyProfileFollowerCount.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_followFragment)
        }
    }

    private fun setFollowingButtonClickListener(){
        binding.layoutMyProfileFollowingCount.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_followFragment)
        }
    }

    private fun setMyProfileOptionClickListener() {
        binding.btnMyProfileOption.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_myProfileOptionFragment)
        }
    }

    private fun setMyProfileDescription() {
        binding.userInfo = SharedManager(requireContext()).getCurrentUser()
        Glide.with(requireContext())
            .load("http://www.endlesscreation.kr:8080/images/" + SharedManager(requireContext()).getCurrentUser().profileImg.toString())
            .into(binding.imgMyProfileUserProfile)
    }
    private fun setMyProfileEditButtonClickListener() {
        binding.btnMyProfileEdit.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_myProfileEditFragment)
        }
    }
}