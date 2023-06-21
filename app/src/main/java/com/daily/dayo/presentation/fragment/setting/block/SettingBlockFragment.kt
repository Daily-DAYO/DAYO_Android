package com.daily.dayo.presentation.fragment.setting.block

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.common.Status
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentSettingBlockBinding
import com.daily.dayo.domain.model.BlockUser
import com.daily.dayo.presentation.adapter.BlockListAdapter
import com.daily.dayo.presentation.viewmodel.ProfileSettingViewModel
import com.daily.dayo.presentation.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingBlockFragment : Fragment() {
    private var binding by autoCleared<FragmentSettingBlockBinding>()
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val profileSettingViewModel by viewModels<ProfileSettingViewModel>()
    private lateinit var blockListAdapter: BlockListAdapter
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
        binding = FragmentSettingBlockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButtonClickListener()
        setBlockListAdapter()
    }

    override fun onResume() {
        super.onResume()
        setBlockList()
    }

    private fun setBlockListAdapter() {
        blockListAdapter = BlockListAdapter(requestManager = glideRequestManager)
        binding.rvBlock.adapter = blockListAdapter
        blockListAdapter.setOnItemClickListener(object :
            BlockListAdapter.OnItemClickListener {
            override fun onItemClick(checkbox: CheckBox, blockUser: BlockUser, position: Int) {
                unblockUser(blockUser.memberId, position)
            }
        })
    }

    private fun setBlockList() {
        profileSettingViewModel.requestBlockList()
        profileSettingViewModel.blockList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> it.data?.let { blockList ->
                    blockListAdapter.submitList(blockList)
                }
            }
        }
    }

    private fun unblockUser(memberId: String, position: Int) {
        profileViewModel.requestUnblockMember(memberId = memberId)
        profileViewModel.unblockSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                profileSettingViewModel.requestBlockList()
            }
        }
    }

    private fun setBackButtonClickListener() {
        binding.btnSettingBlockBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

}