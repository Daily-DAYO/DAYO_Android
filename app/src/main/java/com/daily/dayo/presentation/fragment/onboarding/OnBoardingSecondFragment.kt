package com.daily.dayo.presentation.fragment.onboarding

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daily.dayo.common.autoCleared
import com.daily.dayo.databinding.FragmentOnBoardingSecondBinding

class OnBoardingSecondFragment : Fragment() {
    private var binding by autoCleared<FragmentOnBoardingSecondBinding>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnBoardingSecondBinding.inflate(inflater, container, false)
        setMessageTextSpan()
        return binding.root
    }

    private fun setMessageTextSpan() {
        val spannableText: Spannable = binding.tvOnboardingSecondMessage.text as Spannable
        spannableText.setSpan(StyleSpan(Typeface.BOLD), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}