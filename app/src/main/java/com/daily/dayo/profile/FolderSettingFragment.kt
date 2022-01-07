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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFolderSettingBinding
import com.daily.dayo.profile.adapter.FolderSettingAdapter
import com.daily.dayo.profile.viewmodel.FolderSettingViewModel
import com.daily.dayo.util.Status
import com.daily.dayo.util.autoCleared
import kotlinx.coroutines.launch

class FolderSettingFragment : Fragment() {
    private var binding by autoCleared<FragmentFolderSettingBinding>()
    private val folderSettingViewModel by activityViewModels<FolderSettingViewModel>()
    private lateinit var folderSettingAdapter: FolderSettingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderSettingBinding.inflate(inflater, container, false)

        setBackButtonClickListener()
        setFolderAddButtonClickListener()
        setRvFolderSettingListAdapter()
        setProfileFolderList()

        return binding.root
    }

    private fun setBackButtonClickListener() {
        binding.btnFolderSettingBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setFolderAddButtonClickListener() {
        binding.tvFolderSettingAdd.setOnClickListener {
            findNavController().navigate(R.id.action_folderSettingFragment_to_folderSettingAddFragment)
        }
    }

    private fun setRvFolderSettingListAdapter() {
        val layoutManager = LinearLayoutManager(this.context)
        folderSettingAdapter = FolderSettingAdapter()
        binding.rvFolderSettingListSaved.adapter = folderSettingAdapter
        binding.rvFolderSettingListSaved.layoutManager = layoutManager
    }

    private fun setProfileFolderList(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                folderSettingViewModel.folderList.observe(viewLifecycleOwner, Observer {
                    when(it.status){
                        Status.SUCCESS -> {
                            it.data?.let { folderList ->
                                folderSettingAdapter.submitList(folderList.data)
                            }
                        }
                    }
                })
            }
        }
    }
}