package com.daily.dayo.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.databinding.FragmentWriteFolderAddBinding
import com.daily.dayo.util.autoCleared
import com.daily.dayo.write.viewmodel.WriteViewModel

class WriteFolderAddFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteFolderAddBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteFolderAddBinding.inflate(inflater, container, false)
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
            findNavController().popBackStack()
        }
    }

    private fun createFolder(){
        val name:String = binding.etPostFolderAddSetTitle.text.toString()
        val privacy:String = when(binding.radiogroupPostFolderAddSetPrivate.checkedRadioButtonId){
                binding.radiobuttonPostFolderAddSetPrivateAll.id -> "ALL"
                binding.radiobuttonPostFolderAddSetPrivateFollowing.id -> "FOLLOWING"
                binding.radiobuttonPostFolderAddSetPrivateOnlyMe.id -> "ONLY_ME"
                else -> ""
        }
        writeViewModel.requestCreateFolderInPost(name, privacy)
    }
}