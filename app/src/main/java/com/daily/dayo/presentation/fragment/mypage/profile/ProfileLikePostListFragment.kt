package com.daily.dayo.presentation.fragment.mypage.profile

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentProfileLikePostListBinding
import com.daily.dayo.domain.model.LikePost
import com.daily.dayo.presentation.adapter.ProfileLikePostListAdapter
import com.daily.dayo.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        loadingPost()
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
                        loadPostThumbnail(likePostList)
                    }
                }
            }
        }
    }

    private fun loadPostThumbnail(postList: List<LikePost>) {
        val thumbnailImgList = emptyList<Bitmap>().toMutableList()

        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0 until (if (postList.size >= 8) 8 else postList.size)) {
                thumbnailImgList.add(withContext(Dispatchers.IO) {
                    loadImageBackground(
                        context = requireContext(),
                        height = 158,
                        width = 158,
                        imgName = postList[i].thumbnailImage
                    )
                })
            }
        }.invokeOnCompletion { throwable ->
            when (throwable) {
                is CancellationException -> Log.e("Image Loading", "CANCELLED")
                null -> {
                    var loadedPostList = postList.toMutableList()
                    for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                        loadedPostList[i].preLoadThumbnail = thumbnailImgList[i]
                    }
                    binding.likeCount = postList.size
                    profileLikePostListAdapter.submitList(postList.toMutableList())
                    completeLoadPost()
                    thumbnailImgList.clear()
                }
            }
        }
    }

    private fun loadingPost() {
        binding.layoutProfileLikePostShimmer.startShimmer()
        binding.layoutProfileLikePostShimmer.visibility = View.VISIBLE
        binding.rvProfileLikePost.visibility = View.INVISIBLE
    }

    private fun completeLoadPost() {
        binding.layoutProfileLikePostShimmer.stopShimmer()
        binding.layoutProfileLikePostShimmer.visibility = View.GONE
        binding.rvProfileLikePost.visibility = View.VISIBLE
    }
}