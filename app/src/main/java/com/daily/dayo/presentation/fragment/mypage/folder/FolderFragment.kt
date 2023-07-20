package com.daily.dayo.presentation.fragment.mypage.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.GlideLoadUtil.loadImageView
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentFolderBinding
import com.daily.dayo.presentation.adapter.FolderPostListAdapter
import com.daily.dayo.presentation.viewmodel.FolderViewModel
import kotlinx.coroutines.Dispatchers

class FolderFragment : Fragment() {
    private var binding by autoCleared<FragmentFolderBinding> { onDestroyBindingView() }
    private val folderViewModel by activityViewModels<FolderViewModel>()
    private val args by navArgs<FolderFragmentArgs>()
    private var folderPostListAdapter: FolderPostListAdapter? = null
    private var glideRequestManager: RequestManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            getFolderPostList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButtonClickListener()
        setFolderOptionClickListener()
        setRvFolderPostListAdapter()
        setFolderPostList()
        setAdapterLoadStateListener()
        setFolderDetail()
    }

    private fun onDestroyBindingView() {
        glideRequestManager = null
        folderPostListAdapter = null
        binding.rvFolderPost.adapter = null
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
                        glideRequestManager?.let { requestManager ->
                            loadImageView(
                                requestManager = requestManager,
                                width = layoutParams.width,
                                height = 200,
                                imgName = folder.thumbnailImage,
                                imgView = binding.imgFolderThumbnail
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun setRvFolderPostListAdapter() {
        folderPostListAdapter = glideRequestManager?.let { requestManager ->
            FolderPostListAdapter(
                requestManager = requestManager,
                mainDispatcher = Dispatchers.Main
            )
        }
        binding.rvFolderPost.adapter = folderPostListAdapter
    }

    private fun getFolderPostList() {
        folderViewModel.requestFolderPostList(args.folderId)
    }

    private fun setFolderPostList() {
        folderViewModel.folderPostList.observe(viewLifecycleOwner) {
            folderPostListAdapter?.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun setAdapterLoadStateListener() {
        var isInitialLoad = false
        folderPostListAdapter?.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.NotLoading && !isInitialLoad) {
                if (loadState.append is LoadState.NotLoading) {
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