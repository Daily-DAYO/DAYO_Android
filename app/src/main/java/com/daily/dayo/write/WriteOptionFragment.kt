package com.daily.dayo.write

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentWriteOptionBinding
import com.daily.dayo.util.DefaultDialogConfigure
import com.daily.dayo.util.DefaultDialogConfirm
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
            var mAlertDialog = DefaultDialogConfirm.createDialog(requireContext(), R.string.write_post_upload_loading_message,
            false, false, null, null, null, null)
            if(mAlertDialog != null && !mAlertDialog.isShowing) {
                mAlertDialog.show()
                DefaultDialogConfigure.dialogResize(requireContext(), mAlertDialog, 0.7f, 0.14f)
            }
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