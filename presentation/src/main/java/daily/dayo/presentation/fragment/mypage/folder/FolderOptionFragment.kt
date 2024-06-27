package daily.dayo.presentation.fragment.mypage.folder

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import daily.dayo.presentation.R
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.DefaultDialogConfigure
import daily.dayo.presentation.common.dialog.DefaultDialogExplanationConfirm
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentFolderOptionBinding
import daily.dayo.presentation.viewmodel.FolderViewModel

class FolderOptionFragment : DialogFragment() {
    private var binding by autoCleared<FragmentFolderOptionBinding>()
    private val args by navArgs<FolderOptionFragmentArgs>()
    private val folderViewModel by activityViewModels<FolderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderOptionBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setGravity(Gravity.BOTTOM)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEditClickListenter()
        setDeleteClickListener()
    }

    override fun onResume() {
        super.onResume()
        resizePostOptionDialogFragment()
    }

    private fun resizePostOptionDialogFragment() {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = DefaultDialogConfigure.getDeviceWidthSize(requireContext())
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    private fun setEditClickListenter() {
        binding.layoutFolderOptionEdit.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.FolderOptionFragment,
                action = R.id.action_folderOptionFragment_to_folderEditFragment,
                args = FolderOptionFragmentDirections.actionFolderOptionFragmentToFolderEditFragment(args.folderId).arguments
            )
        }
    }

    private fun setDeleteClickListener() {
        binding.layoutFolderOptionDelete.setOnDebounceClickListener {
            val mAlertDialog = DefaultDialogExplanationConfirm.createDialog(requireContext(),
                R.string.folder_delete_description_message,
                R.string.folder_delete_explanation_message,
                true,
                true,
                R.string.confirm,
                R.string.cancel,
                { folderDelete() },
                { findNavController().popBackStack() })
            if (!mAlertDialog.isShowing) {
                mAlertDialog.show()
                DefaultDialogConfigure.dialogResize(requireContext(), mAlertDialog, 0.7f, 0.21f)
            }
        }
    }

    private fun folderDelete() {
        folderViewModel.requestDeleteFolder(args.folderId)
        folderViewModel.deleteSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                findNavController().navigateSafe(
                    currentDestinationId = R.id.FolderOptionFragment,
                    action = R.id.action_folderOptionFragment_to_myPageFragment,
                    args = FolderOptionFragmentDirections.actionFolderOptionFragmentToMyPageFragment().arguments
                )
            }
        }
    }
}