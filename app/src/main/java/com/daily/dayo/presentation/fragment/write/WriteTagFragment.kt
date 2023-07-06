package com.daily.dayo.presentation.fragment.write

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.common.Event
import com.daily.dayo.common.HideKeyBoardUtil
import com.daily.dayo.common.ReplaceUnicode.replaceBlankText
import com.daily.dayo.common.ReplaceUnicode.trimBlankText
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.dialog.LoadingAlertDialog
import com.daily.dayo.common.dp
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentWriteTagBinding
import com.daily.dayo.presentation.viewmodel.WriteViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class WriteTagFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteTagBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private lateinit var loadingAlertDialog: AlertDialog
    private val originalTags by lazy {
        (writeViewModel.postTagList.value ?: arrayListOf()).toMutableList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                displayLoadingDialog()
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWriteTagBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButtonClickListener()
        setSubmitButtonClickListener()
        setEditTextAddTagKeyClickListener()
        setEditTextAddTagLimit()
        observeTags()
        HideKeyBoardUtil.hideTouchDisplay(requireActivity(), requireView())
    }

    override fun onStop() {
        super.onStop()
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }

    private fun setBackButtonClickListener() {
        binding.btnWriteTagBack.setOnDebounceClickListener {
            displayLoadingDialog()
            findNavController().navigateUp()
        }
    }

    private fun setSubmitButtonClickListener() {
        binding.btnWritePostTagSubmit.setOnDebounceClickListener {
            displayLoadingDialog()
            findNavController().navigateUp()
        }
    }

    private fun setEditTextAddTagKeyClickListener() {
        binding.etWriteTagAdd.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val removeBlankTag = replaceBlankText(trimBlankText(binding.etWriteTagAdd.text))
                    if (removeBlankTag.isNotEmpty()
                        && !binding.chipgroupWriteTagListSaved
                            .getAllChipsTagText()
                            .contains(removeBlankTag)
                        && binding.chipgroupWriteTagListSaved.size < MAX_TAG_COUNT
                    ) {
                        writeViewModel.addPostTag(removeBlankTag)
                    }
                    binding.etWriteTagAdd.setText("")
                    true
                }
                else -> false
            }
        }
    }

    private fun setEditTextAddTagLimit() {
        binding.tagCountMax = MAX_TAG_COUNT

        val lengthFilter = InputFilter.LengthFilter(MAX_TAG_LENGTH)
        binding.etWriteTagAdd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (trimBlankText(s).length >= MAX_TAG_LENGTH) {
                    Toast.makeText(
                        requireContext(),
                        String.format(
                            getString(R.string.write_post_tag_alert_message_tag_length_fail_max),
                            MAX_TAG_LENGTH
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        binding.etWriteTagAdd.filters = arrayOf(lengthFilter)
    }

    private fun observeTags() {
        writeViewModel.postTagList.observe(viewLifecycleOwner) {
            with(binding) {
                tagCount = it.size
                isClickEnable = originalTags != it
            }
            displayTagCountLimitMessage(tagCount = it.size)
            binding.chipgroupWriteTagListSaved.clearChips()

            (0 until it.size).mapNotNull { index ->
                val chip = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_write_post_tag_chip, null) as Chip
                val layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                    48.dp
                )
                with(chip) {
                    setTextAppearance(R.style.WritePostTagTextStyle)
                    setOnCloseIconClickListener {
                        writeViewModel.removePostTag(
                            trimBlankText(
                                (chip.text as String).replace(
                                    "#",
                                    ""
                                )
                            )
                        )
                    }
                    text = "# ${trimBlankText(it[index])}"
                }
                binding.chipgroupWriteTagListSaved.addView(chip, layoutParams)
            }
        }
    }

    private fun displayTagCountLimitMessage(tagCount: Int) {
        if (tagCount >= MAX_TAG_COUNT) {
            Toast.makeText(
                requireContext(),
                String.format(
                    getString(R.string.write_post_tag_alert_message_tag_size_fail_max),
                    MAX_TAG_COUNT
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun displayLoadingDialog() {
        writeViewModel.showWriteOptionDialog.value = Event(true)
        LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
        LoadingAlertDialog.resizeDialogFragment(requireContext(), loadingAlertDialog, 0.8f)
    }

    private fun ChipGroup.getAllChipsTagText(): List<String> {
        return (0 until childCount).mapNotNull { index ->
            val currentChip = getChildAt(index) as? Chip
            currentChip?.text.toString().split("# ")[1]
        }
    }

    private fun ChipGroup.clearChips() {
        val chipViews = (0 until childCount).mapNotNull { index ->
            val view = getChildAt(index)
            if (view is Chip) view else null
        }
        chipViews.forEach { removeView(it) }
    }

    companion object {
        private const val MAX_TAG_COUNT = 8
        private const val MAX_TAG_LENGTH = 15
    }
}