package com.daily.dayo.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentMyProfileOptionBinding
import com.daily.dayo.util.autoCleared
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyProfileOptionFragment : BottomSheetDialogFragment() {
    private var binding by autoCleared<FragmentMyProfileOptionBinding>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileOptionBinding.inflate(inflater, container, false)
        setOptionFolderSettingClickListener()
        setOptionMyProfileEditClickListener()
        return binding.root
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
}


