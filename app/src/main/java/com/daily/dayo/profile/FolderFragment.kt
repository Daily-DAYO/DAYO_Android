package com.daily.dayo.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.daily.dayo.DayoApplication
import com.daily.dayo.SharedManager
import com.daily.dayo.databinding.FragmentFolderBinding
import com.daily.dayo.profile.adapter.FolderPostListAdapter
import com.daily.dayo.profile.viewmodel.FolderViewModel
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import kotlinx.coroutines.launch

class FolderFragment : Fragment(){
    private var binding by autoCleared<FragmentFolderBinding>()
    private val folderViewModel by activityViewModels<FolderViewModel>()
    private val args by navArgs<FolderFragmentArgs>()
    private lateinit var folderPostListAdapter:FolderPostListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderBinding.inflate(inflater,container,false)
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
            findNavController().navigate(FolderFragmentDirections.actionFolderFragmentToFolderOptionFragment(args.folderId))
        }
    }

    private fun setRvFolderPostListAdapter(){
        folderPostListAdapter = FolderPostListAdapter()
        binding.rvFolderPost.adapter = folderPostListAdapter
    }

    private fun setFolderDetail(){
        folderViewModel.requestDetailListFolder(args.folderId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                folderViewModel.detailFolderList.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { detailFolderList ->
                                binding.tvFolderName.text = detailFolderList.name
                                binding.tvFolderSubheading.text = detailFolderList.subheading
                                binding.tvFolderPostCount.text = detailFolderList.count.toString()
                                Glide.with(binding.imgFolderThumbnail.context)
                                    .load("http://117.17.198.45:8080/images/" + detailFolderList.thumbnailImage)
                                    .into(binding.imgFolderThumbnail)
                                folderPostListAdapter.submitList(detailFolderList.data)
                                if(detailFolderList.memberId == SharedManager(DayoApplication.applicationContext()).getCurrentUser().memberId) binding.btnFolderOption.isVisible = true
                            }
                        }
                    }
                })
            }
        }
    }
}