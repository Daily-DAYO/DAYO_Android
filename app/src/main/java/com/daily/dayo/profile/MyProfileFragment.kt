package com.daily.dayo.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.SharedManager
import com.daily.dayo.databinding.FragmentMyProfileBinding
import com.daily.dayo.profile.adapter.MyProfileFragmentPagerStateAdapter
import com.daily.dayo.profile.viewmodel.MyProfileViewModel
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MyProfileFragment : Fragment() {
    private var binding by autoCleared<FragmentMyProfileBinding>()
    private val myProfileViewModel by activityViewModels<MyProfileViewModel>()
    private lateinit var viewPager : ViewPager2
    private lateinit var tabLayout: TabLayout
    private val currentUser = SharedManager(DayoApplication.applicationContext().applicationContext).getCurrentUser()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        viewPager = binding.pagerMyProfile
        tabLayout = binding.tabsMyProfile

        setFollowerCountButtonClickListener()
        setFollowingCountButtonClickListener()
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

    private fun setMyProfileOptionClickListener() {
        binding.btnMyProfileOption.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_myProfileOptionFragment)
        }
    }

    private fun setMyProfileDescription() {
        myProfileViewModel.requestMyProfile()
        myProfileViewModel.myProfile.observe(viewLifecycleOwner, Observer {
            when(it.status) {
                Status.SUCCESS -> {
                    it.data?.let { userInfo ->
                        binding.userInfo = userInfo
                        Glide.with(requireContext())
                            .load("http://117.17.198.45:8080/images/" + userInfo.profileImg)
                            .into(binding.imgMyProfileUserProfile)
                    }
                }
            }
        })
    }
    private fun setMyProfileEditButtonClickListener() {
        binding.btnMyProfileEdit.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileFragment_to_myProfileEditFragment)
        }
    }

    private fun setFollowerCountButtonClickListener(){
        binding.layoutMyProfileFollowerCount.setOnClickListener {
            Navigation.findNavController(it).navigate(MyProfileFragmentDirections.actionMyProfileFragmentToFollowFragment(currentUser.memberId?:"", currentUser.nickname?:""))
        }
    }

    private fun setFollowingCountButtonClickListener(){
        binding.layoutMyProfileFollowingCount.setOnClickListener {
            Navigation.findNavController(it).navigate(MyProfileFragmentDirections.actionMyProfileFragmentToFollowFragment(currentUser.memberId?:"", currentUser.nickname?:""))
        }
    }
}