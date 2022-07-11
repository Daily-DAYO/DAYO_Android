package com.daily.dayo.presentation.fragment.mypage.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
import com.daily.dayo.presentation.adapter.FolderPostListAdapter
import com.daily.dayo.presentation.viewmodel.FolderViewModel
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
        setFolderDetail()
        setRvFolderPostListAdapter()
        return binding.root
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
                        binding.tvFolderName.text = folder.name
                        binding.tvFolderSubheading.text = folder.subheading
                        binding.tvFolderPostCount.text = folder.postCount.toString()
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
                        folder.posts?.let { it1 -> folderPostListAdapter.submitList(it1) }
                        if (folder.memberId == DayoApplication.preferences.getCurrentUser().memberId) binding.btnFolderOption.isVisible =
                            true
                    }
                }
            }
        }
    }
}