package daily.dayo.presentation.fragment.mypage.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import daily.dayo.presentation.R
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.databinding.FragmentProfileFolderListBinding
import daily.dayo.presentation.adapter.ProfileFolderListAdapter
import daily.dayo.presentation.viewmodel.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import daily.dayo.domain.model.Folder
import daily.dayo.presentation.viewmodel.AccountViewModel
import kotlinx.coroutines.Dispatchers

class ProfileFolderListFragment : Fragment() {
    private var binding by autoCleared<FragmentProfileFolderListBinding> { onDestroyBindingView() }
    private val accountViewModel by activityViewModels<AccountViewModel>()
    private val profileViewModel by activityViewModels<ProfileViewModel>()
    private var profileFolderListAdapter: ProfileFolderListAdapter?= null
    private var glideRequestManager: RequestManager?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileFolderListBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        getProfileFolderList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRvProfileFolderListAdapter()
        setProfileFolderList()
    }

    private fun onDestroyBindingView() {
        glideRequestManager = null
        profileFolderListAdapter = null
        binding.rvProfileFolder.adapter = null
    }

    private fun setRvProfileFolderListAdapter() {
        profileFolderListAdapter = glideRequestManager?.let {
            ProfileFolderListAdapter(
                requestManager = it
            )
        }
        binding.rvProfileFolder.adapter = profileFolderListAdapter
        profileFolderListAdapter?.setOnItemClickListener(object : ProfileFolderListAdapter.OnItemClickListener {
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

    private fun getProfileFolderList() {
        profileViewModel.requestFolderList(memberId = accountViewModel.getCurrentUserInfo().memberId!!, true)
    }

    private fun setProfileFolderList() {
        profileViewModel.folderList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { folderList ->
                        binding.folderCount = folderList.size
                        profileFolderListAdapter?.submitList(folderList)
                    }
                }
                else -> {}
            }
        }
    }
}