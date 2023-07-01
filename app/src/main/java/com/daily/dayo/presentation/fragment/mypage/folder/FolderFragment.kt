package com.daily.dayo.presentation.fragment.mypage.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.GlideLoadUtil.loadImageBackground
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentFolderBinding
import com.daily.dayo.presentation.adapter.FolderPostListAdapter
import com.daily.dayo.presentation.viewmodel.FolderViewModel
import kotlinx.coroutines.*

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
        setFolderPostList()
        setAdapterLoadStateListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFolderDetail()
    }

    override fun onResume() {
        super.onResume()
        getFolderPostList()
    }

    private fun setBackButtonClickListener() {
        binding.btnFolderBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setFolderOptionClickListener() {
        binding.btnFolderOption.setOnDebounceClickListener {
            findNavController().navigate(
                FolderFragmentDirections.actionFolderFragmentToFolderOptionFragment(
                    args.folderId
                )
            )
        }
    }

    private fun setFolderDetail() {
        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.MarginLayoutParams.MATCH_PARENT,
            ViewGroup.MarginLayoutParams.MATCH_PARENT
        )

        folderViewModel.requestFolderInfo(args.folderId)
        folderViewModel.folderInfo.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { folder ->
                        binding.folder = folder
                        binding.isMine = folder.memberId == DayoApplication.preferences.getCurrentUser().memberId
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                        }
                        if (folder.memberId == DayoApplication.preferences.getCurrentUser().memberId) binding.btnFolderOption.isVisible =
                            true
                    }
                }
            }
        }
    }

    private fun setRvFolderPostListAdapter() {
        folderPostListAdapter = FolderPostListAdapter(
            requestManager = glideRequestManager,
            mainDispatcher = Dispatchers.Main,
            ioDispatcher = Dispatchers.IO
        )
        binding.rvFolderPost.adapter = folderPostListAdapter
    }

    private fun getFolderPostList() {
        folderViewModel.requestFolderPostList(args.folderId)
    }

    private fun setFolderPostList() {
        folderViewModel.folderPostList.observe(viewLifecycleOwner) {
            folderPostListAdapter.submitData(this.lifecycle, it)
        }
    }

    private fun setAdapterLoadStateListener() {
        var isInitialLoad = false
        folderPostListAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.NotLoading && !isInitialLoad) {
                val isListEmpty = folderPostListAdapter.itemCount == 0
                binding.isEmpty = isListEmpty
                if (isListEmpty || loadState.append is LoadState.NotLoading) {
                    completeLoadPost()
                    isInitialLoad = true
                }
            }
        }
    }

    private fun completeLoadPost() {
        binding.layoutFolderPostShimmer.stopShimmer()
        binding.layoutFolderPostShimmer.visibility = View.GONE
        binding.rvFolderPost.visibility = View.VISIBLE
    }
}