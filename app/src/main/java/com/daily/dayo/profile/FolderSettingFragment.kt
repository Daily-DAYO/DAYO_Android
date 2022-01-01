package com.daily.dayo.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFolderSettingBinding
import com.daily.dayo.util.autoCleared

class FolderSettingFragment : Fragment() {
    private var binding by autoCleared<FragmentFolderSettingBinding>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderSettingBinding.inflate(inflater, container, false)

        setBackButtonClickListener()
        setFolderAddButtonClickListener()
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
}