package com.daily.dayo.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.daily.dayo.databinding.FragmentOtherProfileBinding
import com.daily.dayo.profile.adapter.ProfileFolderListAdapter
import com.daily.dayo.profile.viewmodel.OtherProfileViewModel
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import kotlinx.coroutines.launch

class OtherProfileFragment : Fragment() {
    private var binding by autoCleared<FragmentOtherProfileBinding>()
    private val otherProfileViewModel by activityViewModels<OtherProfileViewModel>()
    private val args by navArgs<OtherProfileFragmentArgs>()
    private lateinit var profileFolderListAdapter: ProfileFolderListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtherProfileBinding.inflate(inflater,container,false)
        setRvProfileFolderListAdapter()
        setProfileFolderList()
        setOtherProfileDescription()
        return binding.root
    }

    private fun setRvProfileFolderListAdapter(){
        val layoutManager = LinearLayoutManager(this.context)
        profileFolderListAdapter = ProfileFolderListAdapter()
        binding.rvOtherProfileFolder.adapter = profileFolderListAdapter
        binding.rvOtherProfileFolder.layoutManager = layoutManager
    }

    private fun setProfileFolderList(){
        otherProfileViewModel.requestAllFolderList(args.memberId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                otherProfileViewModel.folderList.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { folderList ->
                                profileFolderListAdapter.submitList(folderList.data)
                            }
                        }
                    }
                })
            }
        }
    }
    private fun setOtherProfileDescription() {

    }
}