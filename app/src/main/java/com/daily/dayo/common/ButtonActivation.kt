package com.daily.dayo.common

import android.content.Context
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.daily.dayo.R

object ButtonActivation {
    fun setSignupButtonActive(context: Context, button: AppCompatButton) {
        button.isEnabled = true
        button.background = ContextCompat.getDrawable(context, R.drawable.button_default_signup_next_button_active)
    }
    fun setSignupButtonInactive(context: Context, button: AppCompatButton) {
        button.isEnabled = false
        button.background = ContextCompat.getDrawable(context, R.drawable.button_default_signup_next_button_inactive)
    }

    fun setTextViewButtonActive(context: Context, textView: TextView) {
        textView.isEnabled = true
        textView.setTextColor(ContextCompat.getColor(context, R.color.primary_green_23C882))
    }
    fun setTextViewButtonInactive(context: Context, textView: TextView) {
        textView.isEnabled = false
        textView.setTextColor(ContextCompat.getColor(context, R.color.gray_4_D3D2D2))
    }

    fun setTextViewConfirmButtonActive(context: Context, textView: TextView) {
        textView.isEnabled = true
        textView.setTextColor(ContextCompat.getColor(context, R.color.gray_1_313131))
    }
    fun setTextViewConfirmButtonInactive(context: Context, textView: TextView) {
        textView.isEnabled = false
        textView.setTextColor(ContextCompat.getColor(context, R.color.gray_4_D3D2D2))
    }
}