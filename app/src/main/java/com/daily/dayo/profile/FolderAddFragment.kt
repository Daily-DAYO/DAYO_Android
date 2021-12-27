package com.daily.dayo.profile

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.DayoApplication
import com.daily.dayo.SharedManager
import com.daily.dayo.databinding.FragmentFolderAddBinding
import com.daily.dayo.util.autoCleared

class FolderAddFragment : Fragment() {
    private var binding by autoCleared<FragmentFolderAddBinding>()
    private val folderAddViewModel by activityViewModels<FolderAddViewModel>()
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
            createFolder()
            Toast.makeText(requireContext(), "확인 버튼 클릭", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createFolder(){
        val name:String = binding.etPostFolderAddSetTitle.text.toString()
        val memberId:String = SharedManager(DayoApplication.applicationContext()).getCurrentUser().id.toString()
        folderAddViewModel.requestCreateFolder(memberId, name, null, null)
    }
}