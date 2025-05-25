package daily.dayo.presentation.fragment.mypage.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import daily.dayo.presentation.R
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.DefaultDialogConfigure
import daily.dayo.presentation.common.dialog.DefaultDialogExplanationConfirm
import daily.dayo.presentation.common.extension.navigateSafe
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentProfileOptionBinding
import daily.dayo.presentation.viewmodel.ProfileViewModel

@AndroidEntryPoint
class ProfileOptionFragment : DialogFragment() {
    private val profileViewModel by viewModels<ProfileViewModel>()
    private var binding by autoCleared<FragmentProfileOptionBinding>()
    private val args by navArgs<ProfileOptionFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileOptionBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setGravity(Gravity.BOTTOM)

        binding.isMine = args.isMine

        // My Profile
        setOptionFolderSettingClickListener()
        setOptionProfileEditClickListener()
        setOptionSettingClickListener()

        // Other Profile
        setOptionReportUserClickListener()
        setBlockUserClickListener()

        return binding.root
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

    private fun setOptionFolderSettingClickListener() {
        binding.layoutProfileOptionFolderSetting.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.ProfileOptionFragment,
                action = R.id.action_profileOptionFragment_to_folderSettingFragment
            )
        }
    }

    private fun setOptionProfileEditClickListener() {
        binding.layoutProfileOptionEditProfile.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.ProfileOptionFragment,
                action = R.id.action_profileOptionFragment_to_profileEditFragment
            )
        }
    }

    private fun setOptionSettingClickListener() {
        binding.layoutProfileOptionSetting.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.ProfileOptionFragment,
                action = R.id.action_profileOptionFragment_to_settingFragment
            )
        }
    }

    private fun setBlockUserClickListener() {
        binding.layoutOtherProfileOptionBlockUser.setOnDebounceClickListener {
            val blockAlertDialog = DefaultDialogExplanationConfirm.createDialog(requireContext(),
                R.string.other_profile_block_message,
                R.string.other_profile_block_explanation_message,
                true,
                true,
                R.string.confirm,
                R.string.cancel,
                { blockUser() },
                { dismiss() })

            if (!blockAlertDialog.isShowing) {
                blockAlertDialog.show()
                DefaultDialogConfigure.dialogResize(
                    requireContext(),
                    blockAlertDialog,
                    0.7f,
                    0.23f
                )
            }
        }
    }

    private fun blockUser() {
    }

    private fun setOptionReportUserClickListener() {
        binding.layoutOtherProfileOptionReportUser.setOnDebounceClickListener {
            findNavController().navigateSafe(
                currentDestinationId = R.id.ProfileOptionFragment,
                action = R.id.action_profileOptionFragment_to_reportUserFragment,
                args = ProfileOptionFragmentDirections.actionProfileOptionFragmentToReportUserFragment(
                    memberId = args.memberId
                ).arguments
            )
        }
    }
}
