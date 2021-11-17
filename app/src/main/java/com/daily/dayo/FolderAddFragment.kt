package com.daily.dayo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.daily.dayo.databinding.FragmentFolderAddBinding
import com.daily.dayo.util.autoCleared

class FolderAddFragment : Fragment() {
    private var binding by autoCleared<FragmentFolderAddBinding>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderAddBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setConfirmButtonClickListener()

        return binding.root
    }
    private fun setBackButtonClickListener() {
        binding.tvPostFolderAddBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun setConfirmButtonClickListener() {
        binding.tvPostFolderAddConfirm.setOnClickListener {
            Toast.makeText(requireContext(), "확인 버튼 클릭", Toast.LENGTH_SHORT).show()
        }
    }
}