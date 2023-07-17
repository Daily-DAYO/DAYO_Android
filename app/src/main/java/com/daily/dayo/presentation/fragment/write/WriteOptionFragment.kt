package com.daily.dayo.presentation.fragment.write

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.common.Event
import com.daily.dayo.common.Px
import com.daily.dayo.common.ReplaceUnicode.trimBlankText
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.dialog.LoadingAlertDialog
import com.daily.dayo.common.dp
import com.daily.dayo.common.extension.navigateSafe
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentWriteOptionBinding
import com.daily.dayo.presentation.viewmodel.WriteViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WriteOptionFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentWriteOptionBinding> {
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private val loadingAlertDialog by lazy { LoadingAlertDialog.createLoadingDialog(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteOptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUploadStateCallBack()
        setUploadButtonClickListener()
        setOptionTagListOriginalValue()
        setOptionTagClickListener()
        setOptionFolderClickListener()
        setFolderDescription()
    }

    private fun setUploadButtonClickListener() {
        binding.btnWriteOptionConfirm.setOnDebounceClickListener {
            when {
                writeViewModel.postFolderId.value.isNullOrEmpty() -> { // 폴더 미선택시 글 업로드 불가
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.write_post_upload_alert_message_empty_folder),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
                    writeViewModel.requestUploadPost()
                    Toast.makeText(
                        requireContext(),
                        R.string.write_post_upload_alert_message_loading,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun observeUploadStateCallBack() {
        with(writeViewModel) {
            writeSuccess.observe(viewLifecycleOwner) { isSuccess ->
                setUploadResultCallback(isSuccess)
            }
            writeEditSuccess.observe(viewLifecycleOwner) { isSuccess ->
                setUploadResultCallback(isSuccess)
            }
        }
    }

    private fun setUploadResultCallback(isSuccess: Event<Boolean>) {
        if (isSuccess.getContentIfNotHandled() == true) {
            writeViewModel.writePostId.value?.getContentIfNotHandled()?.let { writePostId ->
                findNavController().navigateSafe(
                    currentDestinationId = R.id.WriteOptionFragment,
                    action = R.id.action_writeOptionFragment_to_postFragment,
                    args = WriteOptionFragmentDirections.actionWriteOptionFragmentToPostFragment(
                        writePostId
                    ).arguments
                )
            }
        } else if (isSuccess.getContentIfNotHandled() == false) {
            Toast.makeText(
                requireContext(),
                R.string.write_post_upload_alert_message_fail,
                Toast.LENGTH_SHORT
            ).show()
        } else {
        }
    }

    private fun setFolderDescription() {
        writeViewModel.postFolderId.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.tvWriteOptionDescriptionFolder.text = writeViewModel.postFolderName.value
            }
        }
    }

    private fun setOptionTagListOriginalValue() {
        writeViewModel.postTagList.observe(viewLifecycleOwner) {
            binding.tvWriteOptionDescriptionTag.isVisible = it.isNullOrEmpty()
            if (!it.isNullOrEmpty()) {
                (it.indices).mapNotNull { index ->
                    val chip = LayoutInflater.from(context)
                        .inflate(R.layout.item_write_post_tag_chip, null) as Chip
                    val layoutParams = ViewGroup.MarginLayoutParams(
                        ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                        48.dp
                    )
                    with(chip) {
                        setTextAppearance(R.style.WritePostTagTextStyle)
                        isCloseIconVisible = false
                        isCheckable = false
                        ensureAccessibleTouchTarget(42.Px)
                        text = "# ${trimBlankText(it[index])}"
                    }
                    binding.chipgroupWriteOptionTagListSaved.addView(chip, layoutParams)
                }
            }
        }
    }

    private fun setOptionTagClickListener() {
        binding.layoutWriteOptionTag.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.WriteOptionFragment,
                action = R.id.action_writeOptionFragment_to_writeTagFragment
            )
        }
    }

    private fun setOptionFolderClickListener() {
        binding.layoutWriteOptionFolder.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.WriteOptionFragment,
                action = R.id.action_writeOptionFragment_to_writeFolderFragment
            )
        }
    }
}