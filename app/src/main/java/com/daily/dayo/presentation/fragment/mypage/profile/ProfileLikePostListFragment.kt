package com.daily.dayo.presentation.fragment.mypage.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentProfileLikePostListBinding
import com.daily.dayo.domain.model.LikePost
import com.daily.dayo.presentation.adapter.ProfileLikePostListAdapter
import com.daily.dayo.presentation.viewmodel.ProfileViewModel

class ProfileLikePostListFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileLikePostListBinding>()
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private lateinit var profileLikePostListAdapter: ProfileLikePostListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileLikePostListBinding.inflate(inflater, container, false)
        setRvProfileLikePostListAdapter()
        setProfileLikePostList()
        return binding.root
    }

    private fun setRvProfileLikePostListAdapter() {
        profileLikePostListAdapter = ProfileLikePostListAdapter()
        binding.rvProfileLikePost.adapter = profileLikePostListAdapter
        profileLikePostListAdapter.setOnItemClickListener(object : ProfileLikePostListAdapter.OnItemClickListener{
            override fun onItemClick(v: View, likePost: LikePost, pos: Int) {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToPostFragment(
                    likePost.postId))
            }
        })
    }

    private fun setProfileLikePostList(){
        profileViewModel.requestAllMyLikePostList()
        profileViewModel.likePostList.observe(viewLifecycleOwner) {
            when(it.status){
                Status.SUCCESS -> {
                    it.data?.let { likePostList ->
                        profileLikePostListAdapter.submitList(likePostList)
                    }
                }
            }
        }
    }
}