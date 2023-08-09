package com.daily.dayo.presentation.fragment.write

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.common.ButtonActivation
import com.daily.dayo.common.ReplaceUnicode.trimBlankText
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.dialog.LoadingAlertDialog
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentWriteFolderAddBinding
import com.daily.dayo.domain.model.Privacy
import com.daily.dayo.presentation.viewmodel.WriteViewModel

class WriteFolderAddFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteFolderAddBinding> {
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private lateinit var loadingAlertDialog: AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteFolderAddBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        setBackButtonClickListener()
        setConfirmButtonClickListener()
        verifyFolderName()

        return binding.root
    }

    private fun setBackButtonClickListener() {
        binding.btnPostFolderAddBack.setOnDebounceClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setConfirmButtonClickListener() {
        binding.tvPostFolderAddConfirm.setOnDebounceClickListener {
            LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
            createFolder()
            writeViewModel.folderAddAccess.observe(viewLifecycleOwner) {
                if (it.getContentIfNotHandled() == true) {
                    findNavController().popBackStack()
                } else if (it.getContentIfNotHandled() == false) {
                    LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
                    Toast.makeText(
                        requireContext(),
                        R.string.folder_add_message_fail,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun createFolder() {
        val name: String = binding.etPostFolderAddSetTitle.text.toString()
        val privacy: Privacy =
            when (binding.radiogroupPostFolderAddSetPrivate.checkedRadioButtonId) {
                binding.radiobuttonPostFolderAddSetPrivateAll.id -> Privacy.ALL
                binding.radiobuttonPostFolderAddSetPrivateOnlyMe.id -> Privacy.ONLY_ME
                else -> Privacy.ALL
            }
        writeViewModel.requestCreateFolderInPost(name, privacy)
    }

    private fun verifyFolderName() {
        binding.etPostFolderAddSetTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (trimBlankText(s).isEmpty()) {
                    ButtonActivation.setTextViewConfirmButtonInactive(
                        requireContext(),
                        binding.tvPostFolderAddConfirm
                    )
                } else {
                    ButtonActivation.setTextViewConfirmButtonActive(
                        requireContext(),
                        binding.tvPostFolderAddConfirm
                    )
                }
            }
        })
    }
}