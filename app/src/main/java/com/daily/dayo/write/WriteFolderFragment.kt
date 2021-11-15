package com.daily.dayo.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentWriteFolderBinding

class WriteFolderFragment : Fragment() {
    private var _binding: FragmentWriteFolderBinding? = null
    private val binding get() = requireNotNull(_binding)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWriteFolderBinding.inflate(inflater, container, false)

        setBackButtonClickListener()
        setConfirmButtonClickListener()
        setFolderAddButtonClickListener()
        return binding.root
    }

    private fun setBackButtonClickListener() {
        binding.btnWriteFolderBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun setFolderAddButtonClickListener() {
        binding.tvWriteFolderAdd.setOnClickListener {
            findNavController().navigate(R.id.action_writeFolderFragment_to_folderAddFragment)
        }
    }
    private fun setConfirmButtonClickListener() {
        binding.tvWritePostFolderConfirm.setOnClickListener {
            Toast.makeText(requireContext(), "확인 버튼 클릭", Toast.LENGTH_SHORT).show()
        }
    }
}