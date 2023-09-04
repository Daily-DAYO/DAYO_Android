package daily.dayo.presentation.fragment.mypage.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import daily.dayo.presentation.R
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.common.dialog.DefaultDialogConfigure
import daily.dayo.presentation.common.dialog.DefaultDialogExplanationConfirm
import daily.dayo.presentation.common.setOnDebounceClickListener
import daily.dayo.presentation.databinding.FragmentProfileOptionBinding
import daily.dayo.presentation.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

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
            findNavController().navigate(R.id.action_profileOptionFragment_to_folderSettingFragment)
        }
    }

    private fun setOptionProfileEditClickListener() {
        binding.layoutProfileOptionEditProfile.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_profileOptionFragment_to_profileEditFragment)
        }
    }

    private fun setOptionSettingClickListener() {
        binding.layoutProfileOptionSetting.setOnDebounceClickListener {
            findNavController().navigate(R.id.action_profileOptionFragment_to_settingFragment)
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
        profileViewModel.requestBlockMember(args.memberId)
        profileViewModel.blockSuccess.observe(viewLifecycleOwner) {
            if (it.getContentIfNotHandled() == true) {
                Toast.makeText(
                    requireContext(),
                    R.string.other_profile_block_success_message,
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun setOptionReportUserClickListener() {
        binding.layoutOtherProfileOptionReportUser.setOnDebounceClickListener {
            findNavController().navigate(
                ProfileOptionFragmentDirections.actionProfileOptionFragmentToReportUserFragment(
                    memberId = args.memberId
                )
            )
        }
    }
}
