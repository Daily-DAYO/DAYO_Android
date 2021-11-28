package com.daily.dayo.write

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentWriteOptionBinding
import com.daily.dayo.util.autoCleared
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WriteOptionFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentWriteOptionBinding>()
    private val args by navArgs<WriteOptionFragmentArgs>()
    private val postTagList by lazy {args.postTagList}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteOptionBinding.inflate(inflater, container, false)
        setUploadButtonClickListener()
        setOptionTagListOriginalValue()
        setOptionTagClickListener()
        setOptionFolderClickListener()
        return binding.root
    }

    private fun setUploadButtonClickListener() {
        binding.btnWriteOptionConfirm.setOnClickListener {
            Toast.makeText(requireContext(),"업로드 버튼 클릭", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setOptionTagListOriginalValue() {
        if(!postTagList.isNullOrEmpty()){
            (0 until postTagList.size).mapNotNull { index ->
                binding.tvWriteOptionDescriptionTag.text =
                    binding.tvWriteOptionDescriptionTag.text.toString() + postTagList[index].toString()
            }
        }
    }
    private fun setOptionTagClickListener() {
        binding.layoutWriteOptionTag.setOnClickListener {
            if(postTagList.isNullOrEmpty()) {
                val navigateWithDataPassAction = WriteOptionFragmentDirections.actionWriteOptionFragmentToWriteTagFragment(emptyArray())
                findNavController().navigate(navigateWithDataPassAction)
            } else {
                val navigateWithDataPassAction = WriteOptionFragmentDirections.actionWriteOptionFragmentToWriteTagFragment(postTagList)
                findNavController().navigate(navigateWithDataPassAction)
            }
        }
    }

    private fun setOptionFolderClickListener() {
        binding.layoutWriteOptionFolder.setOnClickListener {
            findNavController().navigate(R.id.action_writeOptionFragment_to_writeFolderFragment)
        }
    }
}