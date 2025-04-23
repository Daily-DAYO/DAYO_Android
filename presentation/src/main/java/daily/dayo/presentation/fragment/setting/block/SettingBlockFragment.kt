package daily.dayo.presentation.fragment.setting.block

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
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentSettingBlockBinding
import daily.dayo.presentation.adapter.BlockListAdapter
import daily.dayo.presentation.viewmodel.ProfileSettingViewModel
import daily.dayo.presentation.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.domain.model.UserBlocked

@AndroidEntryPoint
class SettingBlockFragment : Fragment() {
    private var binding by autoCleared<FragmentSettingBlockBinding> { onDestroyBindingView() }
    private val profileViewModel by viewModels<ProfileViewModel>()
    private val profileSettingViewModel by viewModels<ProfileSettingViewModel>()
    private var blockListAdapter: BlockListAdapter? = null
    private var glideRequestManager: RequestManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBlockBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
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

    private fun onDestroyBindingView() {
        glideRequestManager = null
        blockListAdapter = null
        binding.rvBlock.adapter = null
    }

    private fun setBlockListAdapter() {
//        blockListAdapter = glideRequestManager?.let { requestManager ->
//            BlockListAdapter(requestManager = requestManager)
//        }
//        binding.rvBlock.adapter = blockListAdapter
//        blockListAdapter?.setOnItemClickListener(object :
//            BlockListAdapter.OnItemClickListener {
//            override fun onItemClick(checkbox: CheckBox, blockUser: UserBlocked, position: Int) {
//                unblockUser(blockUser.memberId, position)
//            }
//        })
    }

    private fun setBlockList() {
//        profileSettingViewModel.requestBlockList()
//        profileSettingViewModel.blockList.observe(viewLifecycleOwner) {
//            when (it.status) {
//                Status.SUCCESS -> it.data?.let { blockList ->
//                    blockListAdapter?.submitList(blockList)
//                }
//                else -> {}
//            }
//        }
    }

    private fun unblockUser(memberId: String, position: Int) {
//        profileViewModel.requestUnblockMember(memberId = memberId)
//        profileViewModel.unblockSuccess.observe(viewLifecycleOwner) {
//            if (it.getContentIfNotHandled() == true) {
//                profileSettingViewModel.requestBlockList()
//            }
//        }
    }

    private fun setBackButtonClickListener() {
        binding.btnSettingBlockBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

}