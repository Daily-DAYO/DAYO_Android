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
import com.daily.dayo.databinding.FragmentProfileLikePostListBinding
import com.daily.dayo.profile.adapter.ProfileLikePostListAdapter
import com.daily.dayo.profile.model.LikePostListData
import com.daily.dayo.profile.viewmodel.MyProfileViewModel
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared

class ProfileLikePostListFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileLikePostListBinding>()
    private val myProfileViewModel by activityViewModels<MyProfileViewModel>()
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
            override fun onItemClick(v: View, likePost: LikePostListData, pos: Int) {
                findNavController().navigate(MyProfileFragmentDirections.actionMyProfileFragmentToPostFragment(
                    likePost.postId,
                    SharedManager(DayoApplication.applicationContext()).getCurrentUser().nickname!!))
            }
        })
    }

    private fun setProfileLikePostList(){
        myProfileViewModel.requestAllMyLikePostList()
        myProfileViewModel.likePostList.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS -> {
                    it.data?.let { likePostList ->
                        profileLikePostListAdapter.submitList(likePostList.data)
                    }
                }
            }
        })
    }
}