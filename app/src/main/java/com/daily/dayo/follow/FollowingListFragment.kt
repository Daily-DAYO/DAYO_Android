package com.daily.dayo.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.daily.dayo.databinding.FragmentFollowingListBinding
import com.daily.dayo.follow.adapter.FollowListAdapter
import com.daily.dayo.follow.viewmodel.FollowViewModel
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared

class FollowingListFragment : Fragment() {
    private var binding by autoCleared<FragmentFollowingListBinding>()
    private val followViewModel by activityViewModels<FollowViewModel>()
    private lateinit var followingListAdapter: FollowListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowingListBinding.inflate(inflater, container, false)
        setRvFollowingListAdapter()
        setFollowingList()
        return binding.root
    }

    private fun setRvFollowingListAdapter(){
        followingListAdapter = FollowListAdapter()
        binding.rvFollowing.adapter = followingListAdapter
    }

    private fun setFollowingList(){
        followViewModel.memberId.observe(viewLifecycleOwner, Observer {
            followViewModel.requestListAllFollowing(it)
        })

        followViewModel.followingList.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS -> {
                    it.data?.let { followingList ->
                        followingListAdapter.submitList(followingList.data)
                    }
                }
            }
        })
    }
}