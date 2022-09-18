package com.daily.dayo.presentation.fragment.mypage.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.daily.dayo.R
import com.daily.dayo.common.DefaultDialogConfigure
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentProfileOptionBinding

class ProfileOptionFragment : DialogFragment() {
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
        binding.layoutProfileOptionFolderSetting.setOnClickListener {
            findNavController().navigate(R.id.action_profileOptionFragment_to_folderSettingFragment)
        }
    }

    private fun setOptionProfileEditClickListener() {
        binding.layoutProfileOptionEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileOptionFragment_to_profileEditFragment)
        }
    }

    private fun setOptionSettingClickListener() {
        binding.layoutProfileOptionSetting.setOnClickListener {
            findNavController().navigate(R.id.action_profileOptionFragment_to_settingFragment)
        }
    }

    private fun setOptionReportUserClickListener() {
        binding.layoutOtherProfileOptionReportUser.setOnClickListener {
            findNavController().navigate(
                ProfileOptionFragmentDirections.actionProfileOptionFragmentToReportUserFragment(
                    memberId = args.memberId
                )
            )
        }
    }
}
