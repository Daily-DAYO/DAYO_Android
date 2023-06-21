package com.daily.dayo.presentation.fragment.mypage.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
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
        setAdapterLoadStateListener()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getProfileLikePostList()
    }

    private fun setRvProfileLikePostListAdapter() {
        profileLikePostListAdapter = ProfileLikePostListAdapter(requestManager = glideRequestManager)
        binding.rvProfileLikePost.adapter = profileLikePostListAdapter
        profileLikePostListAdapter.setOnItemClickListener(object :
            ProfileLikePostListAdapter.OnItemClickListener {
            override fun onItemClick(v: View, likePost: LikePost, pos: Int) {
                when (requireParentFragment()) {
                    is MyPageFragment -> MyPageFragmentDirections.actionMyPageFragmentToPostFragment(likePost.postId)
                    is ProfileFragment -> ProfileFragmentDirections.actionProfileFragmentToPostFragment(likePost.postId)
                    else -> null
                }?.let {
                    findNavController().navigate(it)
                }
            }
        })
    }

    private fun getProfileLikePostList() {
        profileViewModel.requestAllMyLikePostList()
    }

    private fun setProfileLikePostList() {
        profileViewModel.likePostList.observe(viewLifecycleOwner) {
            profileLikePostListAdapter.submitData(this.lifecycle, it)
        }
    }

    private fun setAdapterLoadStateListener() {
        var isInitialLoad = false
        profileLikePostListAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.NotLoading && !isInitialLoad) {
                val isListEmpty = profileLikePostListAdapter.itemCount == 0
                binding.isEmpty = isListEmpty
                if (isListEmpty || loadState.append is LoadState.NotLoading) {
                    completeLoadPost()
                    isInitialLoad = true
                }
            }
        }
    }

    private fun completeLoadPost() {
        binding.layoutProfileLikePostShimmer.stopShimmer()
        binding.layoutProfileLikePostShimmer.visibility = View.GONE
        binding.rvProfileLikePost.visibility = View.VISIBLE
    }
}