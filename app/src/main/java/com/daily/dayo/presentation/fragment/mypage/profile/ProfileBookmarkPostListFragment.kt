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
        setRvProfileLikePostListAdapter()
        setProfileLikePostList()
        return binding.root
    }

    private fun setRvProfileLikePostListAdapter() {
        profileBookmarkPostListAdapter = ProfileBookmarkPostListAdapter(requestManager = glideRequestManager)
        binding.rvProfileBookmarkPost.adapter = profileBookmarkPostListAdapter
        profileBookmarkPostListAdapter.setOnItemClickListener(object : ProfileBookmarkPostListAdapter.OnItemClickListener{
            override fun onItemClick(v: View, bookmarkPost: BookmarkPost, pos: Int) {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToPostFragment(
                    bookmarkPost.postId))
            }
        })
    }

    private fun setProfileLikePostList(){
        profileViewModel.requestAllMyBookmarkPostList()
        profileViewModel.bookmarkPostList.observe(viewLifecycleOwner) {
            when(it.status){
                Status.SUCCESS -> {
                    it.data?.let { bookmarkPostList ->
                        profileBookmarkPostListAdapter.submitList(bookmarkPostList)
                    }
                }
            }
        }
    }
}