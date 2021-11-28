package com.daily.dayo.write

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentWriteTagBinding
import com.daily.dayo.util.autoCleared
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class WriteTagFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteTagBinding>()
    private val args by navArgs<WriteTagFragmentArgs>()
    private val postTagList by lazy {args.postTagList}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteTagBinding.inflate(inflater, container, false)
        setBackButtonClickListener()

        (0 until postTagList.size).mapNotNull { index ->
            val chip = LayoutInflater.from(context).inflate(R.layout.item_write_post_tag_chip, null) as Chip
            val layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
            with(chip) {
                setTextAppearance(R.style.WritePostTagTextStyle)
                setOnCloseIconClickListener { binding.chipgroupWriteTagListSaved.removeView(chip as View)}
                text = "${postTagList[index].trim()}"
            }
            binding.chipgroupWriteTagListSaved.addView(chip, layoutParams)
        }

        setSubmitButtonClickListener()
        setEditTextAddTagKeyClickListener()
        return binding.root
    }

    private fun setBackButtonClickListener(){
        binding.btnWriteTagBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setSubmitButtonClickListener(){
        binding.btnWritePostTagSubmit.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set("postTagList", binding.chipgroupWriteTagListSaved.getAllChipsText())
            findNavController().popBackStack()
        }
    }

    private fun setEditTextAddTagKeyClickListener() {
       binding.etWriteTagAdd.setOnEditorActionListener { _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val chip = LayoutInflater.from(context).inflate(R.layout.item_write_post_tag_chip, null) as Chip
                    val layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
                    with(chip) {
                        setTextAppearance(R.style.WritePostTagTextStyle)
                        setOnCloseIconClickListener { binding.chipgroupWriteTagListSaved.removeView(chip as View)}
                        text = "#${binding.etWriteTagAdd.text.toString().trim()}"
                    }
                    binding.chipgroupWriteTagListSaved.addView(chip, layoutParams)
                    binding.etWriteTagAdd.setText("")
                    true
                }
                else -> false
            }
        }
    }

    private fun ChipGroup.getAllChipsText(): List<String> {
        return (0 until childCount).mapNotNull { index ->
            val currentChip = getChildAt(index) as? Chip
            currentChip?.text.toString()
        }
    }

    private fun ChipGroup.clearChips() {
        val chipViews = (0 until childCount).mapNotNull { index ->
            val view = getChildAt(index)
            if (view is Chip) view else null
        }
        chipViews.forEach { removeView(it) }
    }
}