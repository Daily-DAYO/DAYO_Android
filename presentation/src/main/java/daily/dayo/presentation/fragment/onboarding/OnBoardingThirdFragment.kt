package daily.dayo.presentation.fragment.onboarding

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import daily.dayo.presentation.common.autoCleared
import daily.dayo.presentation.databinding.FragmentOnBoardingThirdBinding

class OnBoardingThirdFragment : Fragment() {
    private var binding by autoCleared<FragmentOnBoardingThirdBinding>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnBoardingThirdBinding.inflate(inflater, container, false)
        setMessageTextSpan()
        return binding.root
    }

    private fun setMessageTextSpan() {
        val spannableText: Spannable = binding.tvOnboardingThirdMessage.text as Spannable
        spannableText.setSpan(StyleSpan(Typeface.BOLD), 7, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}