package com.daily.dayo.write

import android.os.Bundle
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
import com.daily.dayo.databinding.FragmentWriteTagBinding
import com.daily.dayo.util.Event
import com.daily.dayo.util.autoCleared
import com.daily.dayo.write.viewmodel.WriteViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class WriteTagFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteTagBinding>()
    private val writeViewModel by activityViewModels<WriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteTagBinding.inflate(inflater, container, false)
        setBackButtonClickListener()
        setSubmitButtonClickListener()
        setEditTextAddTagKeyClickListener()
        initPreviousTagList()
        setTagCountLimit()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                writeViewModel.showWriteOptionDialog.value = Event(true)
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun setBackButtonClickListener(){
        binding.btnWriteTagBack.setOnClickListener {
            writeViewModel.showWriteOptionDialog.value = Event(true)
            findNavController().navigateUp()
        }
    }

    private fun setSubmitButtonClickListener(){
        binding.btnWritePostTagSubmit.setOnClickListener {
            writeViewModel.postTagList.replaceAll(binding.chipgroupWriteTagListSaved.getAllChipsTagText())
            writeViewModel.showWriteOptionDialog.value = Event(true)
            findNavController().navigateUp()
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
                        setOnCloseIconClickListener {
                            binding.chipgroupWriteTagListSaved.removeView(chip as View)
                            setTagCountLimit()
                        }
                        ensureAccessibleTouchTarget(42.toPx())
                        text = "# ${binding.etWriteTagAdd.text.toString().trim()}"
                    }
                    if(binding.chipgroupWriteTagListSaved.size < 8) {
                        binding.chipgroupWriteTagListSaved.addView(chip, layoutParams)
                        setTagCountLimit()
                    }
                    binding.etWriteTagAdd.setText("")
                    binding.etWriteTagAdd.clearFocus()
                    true
                }
                else -> false
            }
        }
    }

    private fun initPreviousTagList() {
        writeViewModel.postTagList.observe(viewLifecycleOwner) {
            (0 until it.size).mapNotNull { index ->
                val chip = LayoutInflater.from(context)
                    .inflate(R.layout.item_write_post_tag_chip, null) as Chip
                val layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT
                )
                with(chip) {
                    setTextAppearance(R.style.WritePostTagTextStyle)
                    setOnCloseIconClickListener {
                        binding.chipgroupWriteTagListSaved.removeView(chip as View)
                        setTagCountLimit()
                    }
                    text = "# ${it[index].trim()}"
                }
                binding.chipgroupWriteTagListSaved.addView(chip, layoutParams)
            }
            setTagCountLimit()
        }
    }

    private fun setTagCountLimit() {
        with(binding) {
            val currentTagCount = chipgroupWriteTagListSaved.size
            tagCount = chipgroupWriteTagListSaved.size
            if(currentTagCount >= 8){
                Toast.makeText(requireContext(), R.string.write_post_tag_alert_message_tag_size_fail_max, Toast.LENGTH_SHORT).show()
            } else if(currentTagCount > 0){
                tvWriteTagListCountSaved.setTextColor(resources.getColor(R.color.primary_green_23C882, context?.theme))
                btnWritePostTagSubmit.setTextColor(resources.getColor(R.color.gray_1_313131, context?.theme))
                btnWritePostTagSubmit.isEnabled = true
                imgWriteTagListEmpty.visibility = View.INVISIBLE
            } else {
                tvWriteTagListCountSaved.setTextColor(resources.getColor(R.color.gray_4_D3D2D2, context?.theme))
                btnWritePostTagSubmit.setTextColor(resources.getColor(R.color.gray_4_D3D2D2, context?.theme))
                btnWritePostTagSubmit.isEnabled = false
                imgWriteTagListEmpty.visibility = View.VISIBLE
            }
        }
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

    fun Int.toPx() : Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}