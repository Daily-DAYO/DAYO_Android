package com.daily.dayo.presentation.fragment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentOnBoardingFourthBinding

class OnBoardingFourthFragment : Fragment() {
    private var binding by autoCleared<FragmentOnBoardingFourthBinding>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnBoardingFourthBinding.inflate(inflater, container, false)

        return binding.root
    }
}