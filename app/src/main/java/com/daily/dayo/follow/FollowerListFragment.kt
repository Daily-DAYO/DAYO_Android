package com.daily.dayo.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.daily.dayo.databinding.FragmentFollowerListBinding
import com.daily.dayo.follow.adapter.FollowListAdapter
import com.daily.dayo.follow.viewmodel.FollowViewModel
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared

class FollowerListFragment : Fragment(){
    private var binding by autoCleared<FragmentFollowerListBinding>()
    private val followViewModel by activityViewModels<FollowViewModel>()
    private lateinit var followerListAdapter: FollowListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowerListBinding.inflate(inflater, container, false)
        setRvFollowerListAdapter()
        setFollowerList()
        return binding.root
    }

    private fun setRvFollowerListAdapter(){
        followerListAdapter = FollowListAdapter()
        binding.rvFollower.adapter = followerListAdapter
    }

    private fun setFollowerList(){
        followViewModel.memberId.observe(viewLifecycleOwner, Observer {
            followViewModel.requestListAllFollower(it)
        })

        followViewModel.followerList.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS -> {
                    it.data?.let { followerList ->
                        followerListAdapter.submitList(followerList.data)
                    }
                }
            }
        })
    }

}