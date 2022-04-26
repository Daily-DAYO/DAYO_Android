package com.daily.dayo.setting

import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daily.dayo.R
import com.daily.dayo.databinding.FragmentWithdrawBinding
import com.daily.dayo.util.autoCleared

class WithdrawFragment : Fragment() {
    private var binding by autoCleared<FragmentWithdrawBinding>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWithdrawBinding.inflate(inflater, container, false)
        setFinalMessageTextSpan()
        setOnClickWithdrawOtherReason()
        setBackButtonClickListener()
        setWithdrawButtonActivation()
        setWithdrawButtonClickListener()
        return binding.root
    }

    private fun setBackButtonClickListener() {
        binding.btnWithdrawBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setOnClickWithdrawOtherReason(){
        binding.radiogroupWithdrawReason.setOnCheckedChangeListener { _, id ->
            if(binding.radiogroupWithdrawReason.checkedRadioButtonId!=-1) binding.btnWithdraw.isSelected = true
            when(id) {
                binding.radiobuttonWithdrawReasonOther.id -> binding.etWithdrawReasonOther.visibility = View.VISIBLE
                else -> binding.etWithdrawReasonOther.visibility = View.INVISIBLE
            }
        }
    }

    private fun setWithdrawButtonActivation(){
        if(binding.radiogroupWithdrawReason.checkedRadioButtonId==-1) binding.btnWithdraw.isSelected = false
    }

    private fun setWithdrawButtonClickListener(){
        binding.btnWithdraw.setOnClickListener {
            // TODO : 회원 탈퇴 관련 API 호출 (탈퇴 및 사유 저장)
            //  로그인 정보 삭제 후 로그인 화면으로 이동
        }
    }

    private fun setFinalMessageTextSpan(){
        val spannableText : Spannable = binding.tvWithdrawFinalMessage.text as Spannable
        spannableText.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_green_23C882)), 16, 35, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}