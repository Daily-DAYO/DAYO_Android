package com.daily.dayo.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentMyProfileBinding
import com.daily.dayo.home.DayoPickPostListFragment
import com.daily.dayo.home.HomeFragmentPagerStateAdapter
import com.daily.dayo.home.NewPostListFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = requireNotNull(_binding)
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
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        viewPager = binding.pagerMyProfile
        tabLayout = binding.tabsMyProfile

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
    }
}