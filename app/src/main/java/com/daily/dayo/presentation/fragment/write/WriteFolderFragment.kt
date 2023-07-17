package com.daily.dayo.presentation.fragment.write

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.common.*
import com.daily.dayo.common.dialog.LoadingAlertDialog
import com.daily.dayo.databinding.FragmentWriteFolderBinding
import com.daily.dayo.domain.model.Folder
import com.daily.dayo.presentation.adapter.WriteFolderAdapter
import com.daily.dayo.presentation.viewmodel.WriteViewModel
import kotlinx.coroutines.launch

class WriteFolderFragment : Fragment() {
    private var binding by autoCleared<FragmentWriteFolderBinding> { onDestroyBindingView() }
    private val writeViewModel by activityViewModels<WriteViewModel>()
    private val writeFolderAdapter by lazy {
        WriteFolderAdapter(
            this::onFolderClicked,
            writeViewModel.postFolderId.value!!
        )
    }
    private lateinit var loadingAlertDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteFolderBinding.inflate(inflater, container, false)
        loadingAlertDialog = LoadingAlertDialog.createLoadingDialog(requireContext())
        setBackButtonClickListener()
        setFolderAddButtonClickListener()
        setRvWriteFolderListAdapter()
        setWriteFolderList()
        return binding.root
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

    override fun onStop() {
        super.onStop()
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }

    private fun onDestroyBindingView() {
        binding.rvWriteFolderListSaved.adapter = null
    }

    private fun setBackButtonClickListener() {
        binding.btnWriteFolderBack.setOnDebounceClickListener {
            displayLoadingDialog()
            findNavController().navigateUp()
        }
    }

    private fun setFolderAddButtonClickListener() {
        binding.tvWriteFolderAdd.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_writeFolderFragment_to_folderAddFragment)
        }
    }

    private fun setRvWriteFolderListAdapter() {
        binding.rvWriteFolderListSaved.adapter = writeFolderAdapter
    }

    private fun onFolderClicked(folder: Folder) {
        writeViewModel.setFolderId(folder.folderId.toString())
        writeViewModel.setFolderName(folder.title)
        displayLoadingDialog()
        findNavController().navigateUp()
    }

    private fun setWriteFolderList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                writeViewModel.requestAllMyFolderList()
                writeViewModel.folderList.observe(viewLifecycleOwner) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.let { folderList ->
                                writeFolderAdapter.submitList(folderList)
                                if (folderList.size < 5) {
                                    ButtonActivation.setTextViewButtonActive(
                                        requireContext(),
                                        binding.tvWriteFolderAdd
                                    )
                                } else {
                                    ButtonActivation.setTextViewButtonInactive(
                                        requireContext(),
                                        binding.tvWriteFolderAdd
                                    )
                                }
                            }
                        }
                        else -> { }
                    }
                }
            }
        }
    }

    private fun displayLoadingDialog() {
        writeViewModel.showWriteOptionDialog.value = Event(true)
        LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
        LoadingAlertDialog.resizeDialogFragment(requireContext(), loadingAlertDialog, 0.8f)
    }
}