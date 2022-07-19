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
import com.daily.dayo.common.GlideLoadUtil
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentProfileBookmarkPostListBinding
import com.daily.dayo.domain.model.BookmarkPost
import com.daily.dayo.presentation.adapter.ProfileBookmarkPostListAdapter
import com.daily.dayo.presentation.viewmodel.ProfileViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        loadingPost()
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
                        loadPostThumbnail(bookmarkPostList)
                    }
                }
            }
        }
    }


    private fun loadPostThumbnail(postList: List<BookmarkPost>) {
        val thumbnailImgList = emptyList<Bitmap>().toMutableList()

        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                thumbnailImgList.add(withContext(Dispatchers.IO) {
                    GlideLoadUtil.loadImageBackground(
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
                    for (i in 0 until (if (postList.size >= 8) 8 else postList.size)) {
                        loadedPostList[i].preLoadThumbnail = thumbnailImgList[i]
                    }
                    profileBookmarkPostListAdapter.submitList(postList.toMutableList())
                    completeLoadPost()
                    thumbnailImgList.clear()
                }
            }
        }
    }

    private fun loadingPost() {
        binding.layoutProfileBookmarkPostShimmer.startShimmer()
        binding.layoutProfileBookmarkPostShimmer.visibility = View.VISIBLE
        binding.rvProfileBookmarkPost.visibility = View.INVISIBLE
    }

    private fun completeLoadPost() {
        binding.layoutProfileBookmarkPostShimmer.stopShimmer()
        binding.layoutProfileBookmarkPostShimmer.visibility = View.GONE
        binding.rvProfileBookmarkPost.visibility = View.VISIBLE
    }
}