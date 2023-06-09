package com.daily.dayo.presentation.fragment.mypage.folder

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.common.autoCleared
import com.daily.dayo.common.dialog.DefaultDialogConfigure
import com.daily.dayo.common.dialog.DefaultDialogExplanationConfirm
import com.daily.dayo.common.setOnDebounceClickListener
import com.daily.dayo.databinding.FragmentFolderOptionBinding
import com.daily.dayo.presentation.viewmodel.FolderViewModel

class FolderOptionFragment : DialogFragment()  {
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

    private fun setEditClickListenter(){
        binding.layoutFolderOptionEdit.setOnDebounceClickListener {
            findNavController().navigate(FolderOptionFragmentDirections.actionFolderOptionFragmentToFolderEditFragment(args.folderId))
        }
    }

    private fun setDeleteClickListener(){
        binding.layoutFolderOptionDelete.setOnDebounceClickListener {
            val mAlertDialog = DefaultDialogExplanationConfirm.createDialog(requireContext(), R.string.folder_delete_description_message, R.string.folder_delete_explanation_message,true, true, R.string.confirm,  R.string.cancel,
                { folderDelete() }, {findNavController().popBackStack()})
            if(!mAlertDialog.isShowing) {
                mAlertDialog.show()
                DefaultDialogConfigure.dialogResize(requireContext(), mAlertDialog, 0.7f, 0.21f)
            }
        }
    }

    private fun folderDelete(){
        folderViewModel.requestDeleteFolder(args.folderId)
        folderViewModel.deleteSuccess.observe(viewLifecycleOwner) {
            if(it.getContentIfNotHandled() == true) {
                findNavController().navigate(FolderOptionFragmentDirections.actionFolderOptionFragmentToProfileFragment(memberId = DayoApplication.preferences.getCurrentUser().memberId!!))
            }
        }
    }
}