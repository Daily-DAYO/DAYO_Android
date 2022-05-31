package com.daily.dayo.common

import android.content.Context
import androidx.core.content.ContextCompat
import com.daily.dayo.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object SetTextInputLayout {
    fun setEditTextTheme(context: Context, textInputLayout: TextInputLayout, editText: TextInputEditText, isEditTextEmpty: Boolean) {
        if(isEditTextEmpty) {
            textInputLayout.defaultHintTextColor = context.resources.getColorStateList(R.color.gray_4_D3D2D2, context.theme)
            if(!textInputLayout.isErrorEnabled) {
                editText.backgroundTintList = context.resources.getColorStateList(R.color.gray_5_EDEDED, context.theme)
            }
        } else {
            textInputLayout.defaultHintTextColor = context.resources.getColorStateList(R.color.gray_3_9C9C9C, context.theme)
            if(!textInputLayout.isErrorEnabled) {
                editText.backgroundTintList = context.resources.getColorStateList(R.color.primary_green_23C882, context.theme)
            }
        }
    }

    fun setEditTextErrorTheme(context: Context, textInputLayout: TextInputLayout, editText: TextInputEditText, errorMessage: String?, pass: Boolean) {
        with(textInputLayout) {
            error = errorMessage
            errorIconDrawable = null
            if(pass) {
                isErrorEnabled = false
                editText.backgroundTintList = context.resources.getColorStateList(R.color.primary_green_23C882, context.theme)
            } else {
                isErrorEnabled = true
                editText.backgroundTintList = context.resources.getColorStateList(R.color.red_FF4545, context.theme)
            }
        }
    }

    fun setEditTextErrorThemeWithIcon(context: Context, textInputLayout: TextInputLayout, editText: TextInputEditText, errorMessage: String?, pass: Boolean) {
        with(textInputLayout) {
            error = errorMessage
            if(pass) {
                isErrorEnabled = false
                editText.backgroundTintList = context.resources.getColorStateList(R.color.primary_green_23C882, context.theme)
                endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_check_ok_sign)
            } else {
                isErrorEnabled = true
                editText.backgroundTintList = context.resources.getColorStateList(R.color.red_FF4545, context.theme)
                endIconDrawable = null
            }
        }
    }
}