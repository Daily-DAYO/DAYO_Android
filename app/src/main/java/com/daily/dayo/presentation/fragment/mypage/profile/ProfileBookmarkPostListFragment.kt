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
import com.daily.dayo.databinding.FragmentProfileBookmarkPostListBinding
import com.daily.dayo.domain.model.BookmarkPost
import com.daily.dayo.presentation.adapter.ProfileBookmarkPostListAdapter
import com.daily.dayo.presentation.viewmodel.ProfileViewModel

class ProfileBookmarkPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileBookmarkPostListBinding>()
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private lateinit var profileBookmarkPostListAdapter: ProfileBookmarkPostListAdapter
    private lateinit var glideRequestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBookmarkPostListBinding.inflate(inflater, container, false)
        setRvProfileBookmarkPostListAdapter()
        setProfileBookmarkPostList()
        setAdapterLoadStateListener()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getProfileBookmarkPostList()
    }

    private fun setRvProfileBookmarkPostListAdapter() {
        profileBookmarkPostListAdapter = ProfileBookmarkPostListAdapter(requestManager = glideRequestManager)
        binding.rvProfileBookmarkPost.adapter = profileBookmarkPostListAdapter
        profileBookmarkPostListAdapter.setOnItemClickListener(object : ProfileBookmarkPostListAdapter.OnItemClickListener {
            override fun onItemClick(v: View, bookmarkPost: BookmarkPost, pos: Int) {
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToPostFragment(
                        bookmarkPost.postId
                    )
                )
            }
        })
    }

    private fun getProfileBookmarkPostList() {
        profileViewModel.requestAllMyBookmarkPostList()
    }

    private fun setProfileBookmarkPostList() {
        profileViewModel.bookmarkPostList.observe(viewLifecycleOwner) {
            profileBookmarkPostListAdapter.submitData(this.lifecycle, it)
        }
    }

    private fun setAdapterLoadStateListener() {
        var isInitialLoad = false
        profileBookmarkPostListAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.NotLoading && !isInitialLoad) {
                val isListEmpty = profileBookmarkPostListAdapter.itemCount == 0
                binding.isEmpty = isListEmpty
                if (isListEmpty || loadState.append is LoadState.NotLoading) {
                    completeLoadPost()
                    isInitialLoad = true
                }
            }
        }
    }

    private fun completeLoadPost() {
        binding.layoutProfileBookmarkPostShimmer.stopShimmer()
        binding.layoutProfileBookmarkPostShimmer.visibility = View.GONE
        binding.rvProfileBookmarkPost.visibility = View.VISIBLE
    }
}