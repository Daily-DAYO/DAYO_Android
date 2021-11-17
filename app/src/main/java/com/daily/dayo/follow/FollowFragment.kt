package com.daily.dayo.follow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.daily.dayo.databinding.FragmentFollowBinding
import com.daily.dayo.util.autoCleared
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class FollowFragment : Fragment() {
    private var binding by autoCleared<FragmentFollowBinding>()
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
        binding = FragmentFollowBinding.inflate(inflater, container, false)
        viewPager = binding.pagerFollow
        tabLayout = binding.tabsFollow

        setBackButtonClickListener()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val pagerAdapter = FollowFragmentPagerStateAdapter(requireActivity())
        pagerAdapter.addFragment(FollowerListFragment())
        pagerAdapter.addFragment(FollowingListFragment())
        viewPager.adapter = pagerAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("ViewPagerFragment", "Page ${position+1}")
            }
        })

        TabLayoutMediator(tabLayout, viewPager){ tab, position ->
            when (position){
                0 -> tab.text = "팔로워"
                1 -> tab.text = "팔로잉"
            }
        }.attach()
    }

    private fun setBackButtonClickListener(){
        binding.btnFollowBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}