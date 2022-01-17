package com.daily.dayo.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.DayoApplication
import com.daily.dayo.MainActivity
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentFolderOptionBinding
import com.daily.dayo.profile.viewmodel.FolderOptionViewModel
import com.daily.dayo.util.DefaultDialogConfigure
import com.daily.dayo.util.DefaultDialogExplanationConfirm
import com.daily.dayo.util.autoCleared
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response

class FolderOptionFragment : BottomSheetDialogFragment()  {
    private var binding by autoCleared<FragmentFolderOptionBinding>()
    private val args by navArgs<FolderOptionFragmentArgs>()
    private val folderOptionViewModel by activityViewModels<FolderOptionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderOptionBinding.inflate(inflater, container, false)
        setDeleteClickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setDeleteClickListener(){
        binding.layoutFolderOptionDelete.setOnClickListener {
            val mAlertDialog = DefaultDialogExplanationConfirm.createDialog(requireContext(), R.string.folder_delete_description_message, R.string.folder_delete_explanation_message,true, true, R.string.confirm,  R.string.cancel,
                { folderDelete() }, null)
            if(!mAlertDialog.isShowing) {
                mAlertDialog.show()
                DefaultDialogConfigure.dialogResize(requireContext(), mAlertDialog, 0.7f, 0.25f)
            }
            mAlertDialog.setOnCancelListener { mAlertDialog.dismiss()}
            folderOptionViewModel.deleteSuccess.observe(viewLifecycleOwner, Observer {
                mAlertDialog.dismiss()
                if(it) {
                    findNavController().navigate(FolderOptionFragmentDirections.actionFolderOptionFragmentToMyProfileFragment())
                } else {

                }
            })
        }
    }

    fun folderDelete(){
        folderOptionViewModel.requestDeleteFolder(args.folderId)
    }
}