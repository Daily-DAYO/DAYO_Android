package daily.dayo.presentation.fragment.write

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
import daily.dayo.domain.model.Folder
import daily.dayo.presentation.R
import daily.dayo.presentation.adapter.WriteFolderAdapter
import daily.dayo.presentation.common.ButtonActivation
import daily.dayo.presentation.common.Event
import daily.dayo.presentation.common.Status
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.LoadingAlertDialog
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentWriteFolderBinding
import daily.dayo.presentation.viewmodel.WriteViewModel
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
    private val loadingAlertDialog by lazy { LoadingAlertDialog.createLoadingDialog(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteFolderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackButtonClickListener()
        setFolderAddButtonClickListener()
        setRvWriteFolderListAdapter()
        setWriteFolderList()
    }


    override fun onStop() {
        super.onStop()
        LoadingAlertDialog.hideLoadingDialog(loadingAlertDialog)
    }

    private fun onDestroyBindingView() {
        binding.rvWriteFolderListSaved.adapter = null
    }

    private fun setBackButtonClickListener() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                displayLoadingDialog()
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        binding.btnWriteFolderBack.setOnDebounceClickListener {
            displayLoadingDialog()
            findNavController().navigateUp()
        }
    }

    private fun setFolderAddButtonClickListener() {
        binding.tvWriteFolderAdd.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.WriteFolderFragment,
                action = R.id.action_writeFolderFragment_to_folderAddFragment
            )
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

                        else -> {}
                    }
                }
            }
        }
    }

    private fun displayLoadingDialog() {
        LoadingAlertDialog.showLoadingDialog(loadingAlertDialog)
        LoadingAlertDialog.resizeDialogFragment(requireContext(), loadingAlertDialog, 0.8f)
        writeViewModel.showWriteOptionDialog.value = Event(true)
    }
}