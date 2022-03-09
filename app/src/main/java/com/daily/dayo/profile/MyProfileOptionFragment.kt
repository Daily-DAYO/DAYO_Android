package com.daily.dayo.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentMyProfileOptionBinding
import com.daily.dayo.util.DefaultDialogConfigure
import com.daily.dayo.util.autoCleared

class MyProfileOptionFragment : DialogFragment() {
    private var binding by autoCleared<FragmentMyProfileOptionBinding>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileOptionBinding.inflate(inflater, container, false)
        // DialogFragment Radius 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Android Version 4.4 이하에서 Blue Line이 상단에 나타는 것 방지
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setGravity(Gravity.BOTTOM)

        setOptionFolderSettingClickListener()
        setOptionMyProfileEditClickListener()
        setOptionMyProfileSettingClickListener()
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
        binding.layoutMyProfileOptionFolderSetting.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileOptionFragment_to_folderSettingFragment)
        }
    }

    private fun setOptionMyProfileEditClickListener(){
        binding.layoutMyProfileOptionEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileOptionFragment_to_myProfileEditFragment)
        }
    }

    private fun setOptionMyProfileSettingClickListener(){
        binding.layoutMyProfileOptionSetting.setOnClickListener {
            findNavController().navigate(R.id.action_myProfileOptionFragment_to_myProfileSettingFragment)
        }
    }
}


