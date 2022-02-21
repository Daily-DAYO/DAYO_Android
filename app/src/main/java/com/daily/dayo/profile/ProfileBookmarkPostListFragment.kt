package com.daily.dayo.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.daily.dayo.DayoApplication
import com.daily.dayo.SharedManager
import com.daily.dayo.databinding.FragmentProfileBookmarkPostListBinding
import com.daily.dayo.profile.adapter.ProfileBookmarkPostListAdapter
import com.daily.dayo.profile.model.BookmarkPostListData
import com.daily.dayo.profile.viewmodel.MyProfileViewModel
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared

class ProfileBookmarkPostListFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileBookmarkPostListBinding>()
    private val myProfileViewModel by activityViewModels<MyProfileViewModel>()
    private lateinit var profileBookmarkPostListAdapter: ProfileBookmarkPostListAdapter


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
        profileBookmarkPostListAdapter = ProfileBookmarkPostListAdapter()
        binding.rvProfileBookmarkPost.adapter = profileBookmarkPostListAdapter
        profileBookmarkPostListAdapter.setOnItemClickListener(object : ProfileBookmarkPostListAdapter.OnItemClickListener{
            override fun onItemClick(v: View, bookmarkPost: BookmarkPostListData, pos: Int) {
                findNavController().navigate(MyProfileFragmentDirections.actionMyProfileFragmentToPostFragment(
                    bookmarkPost.postId,
                    SharedManager(DayoApplication.applicationContext()).getCurrentUser().nickname!!))
            }
        })
    }

    private fun setProfileLikePostList(){
        myProfileViewModel.requestAllMyBookmarkPostList()
        myProfileViewModel.bookmarkPostList.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS -> {
                    it.data?.let { bookmarkPostList ->
                        profileBookmarkPostListAdapter.submitList(bookmarkPostList.data)
                    }
                }
            }
        })
    }
}