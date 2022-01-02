package com.daily.dayo.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.databinding.FragmentFolderSettingAddBinding
import com.daily.dayo.util.autoCleared

class FolderSettingAddFragment  : Fragment() {
    private var binding by autoCleared<FragmentFolderSettingAddBinding>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderSettingAddBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setConfirmButtonClickListener()
        return binding.root
    }
    private fun setBackButtonClickListener() {
        binding.btnFolderSettingAddBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun setConfirmButtonClickListener() {
        binding.tvFolderSettingAddConfirm.setOnClickListener {
            Toast.makeText(requireContext(), "확인 버튼 클릭", Toast.LENGTH_SHORT).show()
        }
    }
}