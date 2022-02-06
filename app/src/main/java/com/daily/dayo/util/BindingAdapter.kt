package com.daily.dayo.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter

object BindingAdapter {
    @BindingAdapter("main", "secondText")
    @JvmStatic
    fun setBoldString(view: AppCompatTextView, maintext: String, sequence: String) {
        view.text = getBoldText(maintext, sequence)
    }

    @JvmStatic
    fun getBoldText(text: String, name: String): SpannableStringBuilder {
        val str = SpannableStringBuilder(text)
        val textPosition = text.indexOf(name)
        str.setSpan(android.text.style.StyleSpan(Typeface.BOLD),
            textPosition, textPosition + name.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return str
    }

}