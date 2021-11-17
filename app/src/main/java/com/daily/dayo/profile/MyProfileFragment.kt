package com.daily.dayo.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentMyProfileBinding
import com.daily.dayo.home.DayoPickPostListFragment
import com.daily.dayo.home.HomeFragmentPagerStateAdapter
import com.daily.dayo.home.NewPostListFragment
import com.daily.dayo.util.autoCleared
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MyProfileFragment : Fragment() {

    private var binding by autoCleared<FragmentMyProfileBinding>()
    private lateinit var viewPager : ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val pagerAdapter = MyProfileFragmentPagerStateAdapter(requireActivity())
        pagerAdapter.addFragment(ProfileFolderListFragment())
        pagerAdapter.addFragment(ProfileLikePostListFragment())
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
}