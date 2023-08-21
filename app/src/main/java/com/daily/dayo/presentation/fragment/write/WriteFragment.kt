package com.daily.dayo.presentation.fragment.write

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.daily.dayo.R
import com.daily.dayo.common.*
import com.daily.dayo.common.ReplaceUnicode.trimBlankText
import com.daily.dayo.common.extension.navigateSafe
import com.daily.dayo.databinding.FragmentWriteBinding
import com.daily.dayo.presentation.adapter.WriteUploadImageListAdapter
import com.daily.dayo.presentation.viewmodel.WriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.domain.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WriteFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteBinding> { onDestroyBindingView() }
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private val args by navArgs<WriteFragmentArgs>()
    private var uploadImageListAdapter: WriteUploadImageListAdapter? = null
    private var glideRequestManager: RequestManager? = null

    private var isCategorySelected = MutableStateFlow(false)
    private var isImageUploaded = MutableStateFlow(false)
    private var isLoadedEditPost: MutableLiveData<Boolean> = MutableLiveData(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteBinding.inflate(inflater, container, false)
        glideRequestManager = Glide.with(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setKeyboardMode()
        setBackButtonClickListener()
        setPostImagesAdapter()
        initWritingContents()
        observeUploadImages()
        observeEditTextCountLimit()
        setImageModifyListener()
        setCategoryClickListener()
        setUploadButtonClickListener()
        showOptionDialog()
    }

    private fun onDestroyBindingView() {
        glideRequestManager = null
        uploadImageListAdapter = null
        binding.rvImgUploadList.adapter = null
    }

    private fun setKeyboardMode() {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun setBackButtonClickListener() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                writeViewModel.resetWriteInfoValue()
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        binding.btnWritePostBack.setOnDebounceClickListener {
            writeViewModel.resetWriteInfoValue()
            findNavController().navigateUp()
        }
    }

    private fun showOptionDialog() {
        writeViewModel.showWriteOptionDialog.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                writeViewModel.showWriteOptionDialog.value = Event(false)
                findNavController().navigateSafe(
                    currentDestinationId = R.id.WriteFragment,
                    action = R.id.action_writeFragment_to_writeOptionFragment
                )
            }
        }
    }

    private fun observeEditTextCountLimit() {
        with(binding.etWriteDetail) {
            filters = arrayOf(InputFilter.LengthFilter(200))
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    binding.editTextCount = s.toString().length
                    if (s.toString().length > 200) {
                        Toast.makeText(
                            requireContext(),
                            R.string.write_post_upload_alert_message_edittext_length_fail_max,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    private fun initWritingContents() {
        if (args.postId != 0 && isLoadedEditPost.value == false) {
            isLoadedEditPost.value = true
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    isCategorySelected.emit(true)
                    isImageUploaded.emit(true)
                }
            }
            writeViewModel.requestPostDetail(args.postId)
            setOriginalContents()
        }
    }

    private fun setOriginalContents() {
        // 기존 글 수정하는 경우
        writeViewModel.writeCurrentPostDetail.observe(viewLifecycleOwner) { postDetail ->
            when (postDetail.category) {
                Category.SCHEDULER -> binding.radiobuttonWritePostCategoryScheduler.isChecked =
                    true
                Category.STUDY_PLANNER -> binding.radiobuttonWritePostCategoryStudyplanner.isChecked =
                    true
                Category.GOOD_NOTE -> binding.radiobuttonWritePostCategoryGoodnote.isChecked =
                    true
                Category.POCKET_BOOK -> binding.radiobuttonWritePostCategoryPocketbook.isChecked =
                    true
                Category.SIX_DIARY -> binding.radiobuttonWritePostCategorySixHoleDiary.isChecked =
                    true
                Category.ETC -> binding.radiobuttonWritePostCategoryEtc.isChecked =
                    true
                else -> {}
            }
            binding.etWriteDetail.setText(postDetail.contents)
        }
    }

    private fun setCategoryClickListener() {
        binding.radiogroupWritePostCategory.setOnCheckedChangeListener(object :
            RadioGridGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGridGroup?, checkedId: Int) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        isCategorySelected.emit(true)
                    }
                }
                setCheckedCategoryName(checkedId)
            }
        })
    }

    private fun setCheckedCategoryName(checkedId: Int) {
        writeViewModel.setPostCategory(
            when (checkedId) {
                R.id.radiobutton_write_post_category_scheduler -> Category.SCHEDULER
                R.id.radiobutton_write_post_category_studyplanner -> Category.STUDY_PLANNER
                R.id.radiobutton_write_post_category_goodnote -> Category.GOOD_NOTE
                R.id.radiobutton_write_post_category_pocketbook -> Category.POCKET_BOOK
                R.id.radiobutton_write_post_category_sixHoleDiary -> Category.SIX_DIARY
                R.id.radiobutton_write_post_category_etc -> Category.ETC
                else -> null
            }
        )
    }

    private fun setPostImagesAdapter() {
        uploadImageListAdapter = glideRequestManager?.let { requestManager ->
            WriteUploadImageListAdapter(
                requestManager = requestManager, postId = args.postId
            )
        }
        with(binding.rvImgUploadList) {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = uploadImageListAdapter
        }
    }

    private fun observeUploadImages() {
        writeViewModel.postImageUriList.observe(viewLifecycleOwner) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    isImageUploaded.emit(!it.isNullOrEmpty())
                }
            }
            uploadImageListAdapter?.submitList(
                if (args.postId != 0 || it.size >= 5) it
                else mutableListOf("") + it
            )
        }
    }

    private fun setImageModifyListener() {
        if (args.postId == 0) {
            writeViewModel.postImageUriList.observe(viewLifecycleOwner) {
                uploadImageListAdapter?.setOnItemClickListener(object :
                    WriteUploadImageListAdapter.OnItemClickListener {
                    override fun deleteUploadImageClick(pos: Int) {
                        writeViewModel.deleteUploadImage(if (it.size >= 5) pos else pos - 1, true)
                    }

                    override fun addUploadImageClick(pos: Int) {
                        findNavController().navigateSafe(
                            currentDestinationId = R.id.WriteFragment,
                            action = R.id.action_writeFragment_to_writeImageOptionFragment
                        )
                    }
                })
            }
        }
    }

    private fun setUploadButtonClickListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                isCategorySelected.combine(isImageUploaded) { categorySelected, imageSelected ->
                    if (!categorySelected) {
                        R.string.write_post_upload_alert_message_empty_category
                    } else if (!imageSelected) {
                        R.string.write_post_upload_alert_message_empty_image
                    } else {
                        null
                    }
                }.collect { toastMessage ->
                    with(binding.btnWritePostUpload) {
                        isSelected = (toastMessage == null)
                        setOnDebounceClickListener {
                            if (toastMessage != null) {
                                Toast.makeText(
                                    requireContext(),
                                    toastMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                with(writeViewModel) {
                                    setPostId(args.postId)
                                    setPostContents(
                                        trimBlankText(binding.etWriteDetail.text.toString()).ifEmpty { "" }
                                    )
                                }

                                findNavController().navigateSafe(
                                    currentDestinationId = R.id.WriteFragment,
                                    action = R.id.action_writeFragment_to_writeOptionFragment
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}