package com.daily.dayo.setting

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.daily.dayo.DayoApplication
import com.daily.dayo.R
import com.daily.dayo.SharedManager
import com.daily.dayo.databinding.FragmentWithdrawBinding
import com.daily.dayo.login.LoginActivity
import com.daily.dayo.login.LoginViewModel
import com.daily.dayo.util.autoCleared

class WithdrawFragment : Fragment() {
    private var binding by autoCleared<FragmentWithdrawBinding>()
    private val loginViewModel by activityViewModels<LoginViewModel>()

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
            if(binding.radiogroupWithdrawReason.checkedRadioButtonId!=-1) binding.btnWithdraw.isEnabled = true
            when(id) {
                binding.radiobuttonWithdrawReasonOther.id -> binding.etWithdrawReasonOther.visibility = View.VISIBLE
                else -> binding.etWithdrawReasonOther.visibility = View.GONE
            }
        }
    }

    private fun setWithdrawButtonActivation(){
        if(binding.radiogroupWithdrawReason.checkedRadioButtonId==-1) {
            binding.btnWithdraw.isEnabled = false
        }
    }

    private fun setWithdrawButtonClickListener(){
        binding.btnWithdraw.setOnClickListener {
            val reason = when(binding.radiogroupWithdrawReason.checkedRadioButtonId){
                R.id.radiobutton_withdraw_reason_1 -> binding.radiobuttonWithdrawReason1.text.toString()
                R.id.radiobutton_withdraw_reason_2 -> binding.radiobuttonWithdrawReason2.text.toString()
                R.id.radiobutton_withdraw_reason_3 -> binding.radiobuttonWithdrawReason3.text.toString()
                R.id.radiobutton_withdraw_reason_4 -> binding.radiobuttonWithdrawReason4.text.toString()
                R.id.radiobutton_withdraw_reason_5 -> binding.radiobuttonWithdrawReason5.text.toString()
                else -> binding.radiobuttonWithdrawReasonOther.text.toString()
            }
            loginViewModel.requestWithdraw(reason)
            loginViewModel.withdrawSuccess.observe(viewLifecycleOwner){
                if(it) {
                    SharedManager(DayoApplication.applicationContext()).clearPreferences()
                    val intent = Intent(this.activity, LoginActivity::class.java)
                    startActivity(intent)
                    this.requireActivity().finish()
                }
            }
        }
    }

    private fun setFinalMessageTextSpan(){
        val spannableText : Spannable = binding.tvWithdrawFinalMessage.text as Spannable
        spannableText.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_green_23C882)), 16, 35, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}