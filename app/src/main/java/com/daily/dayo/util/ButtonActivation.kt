package com.daily.dayo.util

import android.content.Context
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
}