package com.daily.dayo.presentation.fragment.mypage.folder

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentFolderBinding
import com.daily.dayo.domain.model.FolderPost
import com.daily.dayo.presentation.adapter.FolderPostListAdapter
import com.daily.dayo.presentation.viewmodel.FolderViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FolderFragment : Fragment() {
    private var binding by autoCleared<FragmentFolderBinding>()
    private val folderViewModel by activityViewModels<FolderViewModel>()
    private val args by navArgs<FolderFragmentArgs>()
    private lateinit var folderPostListAdapter: FolderPostListAdapter
    private lateinit var glideRequestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setFolderOptionClickListener()
        setRvFolderPostListAdapter()
        loadingPost()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFolderDetail()
    }

    private fun setBackButtonClickListener() {
        binding.btnFolderBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setFolderOptionClickListener() {
        binding.btnFolderOption.setOnClickListener {
            findNavController().navigate(
                FolderFragmentDirections.actionFolderFragmentToFolderOptionFragment(
                    args.folderId
                )
            )
        }
    }

    private fun setRvFolderPostListAdapter() {
        folderPostListAdapter = FolderPostListAdapter(requestManager = glideRequestManager)
        binding.rvFolderPost.adapter = folderPostListAdapter
    }

    private fun setFolderDetail() {
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.MarginLayoutParams.MATCH_PARENT,
            ViewGroup.MarginLayoutParams.MATCH_PARENT
        )

        folderViewModel.requestDetailListFolder(args.folderId)
        folderViewModel.detailFolderList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { folder ->
                        binding.folder = folder
                        binding.isMine = folder.memberId == DayoApplication.preferences.getCurrentUser().memberId
                        CoroutineScope(Dispatchers.Main).launch {
                            val folderThumbnailImage = withContext(Dispatchers.IO) {
                                loadImageBackground(
                                    requestManager = glideRequestManager,
                                    width = layoutParams.width,
                                    height = 200,
                                    imgName = folder.thumbnailImage
                                )
                            }
                            loadImageView(
                                requestManager = glideRequestManager,
                                width = layoutParams.width,
                                height = 200,
                                img = folderThumbnailImage,
                                imgView = binding.imgFolderThumbnail
                            )
                        }
                        folder.posts?.let { it -> loadPostThumbnail(it) }
                        if (folder.memberId == DayoApplication.preferences.getCurrentUser().memberId) binding.btnFolderOption.isVisible =
                            true
                    }
                }
            }
        }
    }

    private fun loadPostThumbnail(postList: List<FolderPost>) {
        val thumbnailImgList = emptyList<Bitmap>().toMutableList()
        viewLifecycleOwner.lifecycleScope.launch {
            for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
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
                is CancellationException -> {
                    Log.e("Image Loading", "CANCELLED")
                    thumbnailImgList.clear()
                }
                null -> {
                    var loadedPostList = postList.toMutableList()
                    for (i in 0 until (if (postList.size >= 6) 6 else postList.size)) {
                        loadedPostList[i].preLoadThumbnail = thumbnailImgList[i]
                    }
                    folderPostListAdapter.submitList(postList.toMutableList())
                    completeLoadPost()
                    thumbnailImgList.clear()
                }
            }
        }
    }

    private fun loadingPost() {
        binding.layoutFolderPostShimmer.startShimmer()
        binding.layoutFolderPostShimmer.visibility = View.VISIBLE
        binding.rvFolderPost.visibility = View.INVISIBLE
    }

    private fun completeLoadPost() {
        binding.layoutFolderPostShimmer.stopShimmer()
        binding.layoutFolderPostShimmer.visibility = View.GONE
        binding.rvFolderPost.visibility = View.VISIBLE
    }
}