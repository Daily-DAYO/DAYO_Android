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
import com.daily.dayo.DayoApplication
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentProfileFolderListBinding
import com.daily.dayo.domain.model.Folder
import com.daily.dayo.presentation.adapter.ProfileFolderListAdapter
import com.daily.dayo.presentation.viewmodel.ProfileViewModel

class ProfileFolderListFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileFolderListBinding>()
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private lateinit var profileFolderListAdapter: ProfileFolderListAdapter
    private lateinit var glideRequestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileFolderListBinding.inflate(inflater, container, false)
        setRvProfileFolderListAdapter()
        setProfileFolderList()
        return binding.root
    }

    private fun setRvProfileFolderListAdapter() {
        profileFolderListAdapter = ProfileFolderListAdapter(requestManager = glideRequestManager)
        binding.rvProfileFolder.adapter = profileFolderListAdapter
        profileFolderListAdapter.setOnItemClickListener(object : ProfileFolderListAdapter.OnItemClickListener {
            override fun onItemClick(v: View, folder: Folder, pos: Int) {
                when (requireParentFragment()) {
                    is MyPageFragment -> MyPageFragmentDirections.actionMyPageFragmentToFolderFragment(folderId = folder.folderId!!)
                    is ProfileFragment -> ProfileFragmentDirections.actionProfileFragmentToFolderFragment(folderId = folder.folderId!!)
                    else -> null
                }?.let {
                    findNavController().navigate(it)
                }
            }
        })
    }

    private fun setProfileFolderList() {
        profileViewModel.requestFolderList(memberId = DayoApplication.preferences.getCurrentUser().memberId!!, true)
        profileViewModel.folderList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { folderList ->
                        binding.folderCount = folderList.size
                        profileFolderListAdapter.submitList(folderList)
                    }
                }
            }
        }
    }
}