package com.daily.dayo.presentation.fragment.onboarding

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.daily.dayo.databinding.FragmentOnBoardingFirstBinding
import com.daily.dayo.common.autoCleared

class OnBoardingFirstFragment : Fragment() {
    private var binding by autoCleared<FragmentOnBoardingFirstBinding>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnBoardingFirstBinding.inflate(inflater, container, false)
        setMessageTextSpan()
        return binding.root
    }

    private fun setMessageTextSpan() {
        val spannableText: Spannable = binding.tvOnboardingFirstMessage.text as Spannable
        spannableText.setSpan(StyleSpan(Typeface.BOLD), 0, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}