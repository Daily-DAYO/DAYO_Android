package com.daily.dayo.presentation.fragment.mypage.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
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
    private lateinit var glideRequestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

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
        profileLikePostListAdapter = ProfileLikePostListAdapter(requestManager = glideRequestManager)
        binding.rvProfileLikePost.adapter = profileLikePostListAdapter
        profileLikePostListAdapter.setOnItemClickListener(object :
            ProfileLikePostListAdapter.OnItemClickListener {
            override fun onItemClick(v: View, likePost: LikePost, pos: Int) {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToPostFragment(
                        likePost.postId
                    )
                )
            }
        })
    }

    private fun setProfileLikePostList() {
        profileViewModel.requestAllMyLikePostList()
        profileViewModel.likePostList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { likePostList ->
                        profileLikePostListAdapter.submitList(likePostList)
                    }
                }
            }
        }
    }
}