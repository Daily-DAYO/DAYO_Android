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
import kotlinx.coroutines.Dispatchers

class ProfileBookmarkPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileBookmarkPostListBinding> { onDestroyBindingView() }
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private var profileBookmarkPostListAdapter: ProfileBookmarkPostListAdapter? = null
    private var glideRequestManager: RequestManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBookmarkPostListBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRvProfileBookmarkPostListAdapter()
        setProfileBookmarkPostList()
        setAdapterLoadStateListener()
    }

    override fun onResume() {
        super.onResume()
        getProfileBookmarkPostList()
    }

    private fun onDestroyBindingView() {
        glideRequestManager = null
        profileBookmarkPostListAdapter = null
        binding.rvProfileBookmarkPost.adapter = null
    }

    private fun setRvProfileBookmarkPostListAdapter() {
        profileBookmarkPostListAdapter = glideRequestManager?.let { requestManager ->
            ProfileBookmarkPostListAdapter(
                requestManager = requestManager,
                mainDispatcher = Dispatchers.Main,
                ioDispatcher = Dispatchers.IO
            )
        }
        binding.rvProfileBookmarkPost.adapter = profileBookmarkPostListAdapter
        profileBookmarkPostListAdapter?.setOnItemClickListener(object :
            ProfileBookmarkPostListAdapter.OnItemClickListener {
            override fun onItemClick(v: View, bookmarkPost: BookmarkPost, pos: Int) {
                when (requireParentFragment()) {
                    is MyPageFragment -> MyPageFragmentDirections.actionMyPageFragmentToPostFragment(
                        bookmarkPost.postId
                    )
                    is ProfileFragment -> ProfileFragmentDirections.actionProfileFragmentToPostFragment(
                        bookmarkPost.postId
                    )
                    else -> null
                }?.let {
                    findNavController().navigate(it)
                }
            }
        })
    }

    private fun getProfileBookmarkPostList() {
        profileViewModel.requestAllMyBookmarkPostList()
    }

    private fun setProfileBookmarkPostList() {
        profileViewModel.bookmarkPostList.observe(viewLifecycleOwner) {
            profileBookmarkPostListAdapter?.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun setAdapterLoadStateListener() {
        var isInitialLoad = false
        profileBookmarkPostListAdapter?.let {
            it.addLoadStateListener { loadState ->
                if (loadState.refresh is LoadState.NotLoading && !isInitialLoad) {
                    val isListEmpty = it.itemCount == 0
                    binding.isEmpty = isListEmpty
                    if (isListEmpty || loadState.append is LoadState.NotLoading) {
                        completeLoadPost()
                        isInitialLoad = true
                    }
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