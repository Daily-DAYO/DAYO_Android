package com.daily.dayo.common

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.daily.dayo.R
import com.daily.dayo.databinding.DialogDefaltExplanationConfirmBinding
import com.daily.dayo.databinding.DialogDefaultAlertBinding
import com.daily.dayo.databinding.DialogDefaultConfirmBinding

object DefaultDialogAlert {
    fun createDialog(context: Context, dialogDescriptionResId: Int) : AlertDialog {
        val binding = DialogDefaultAlertBinding.inflate(LayoutInflater.from(context))
        val dialogBuilder = AlertDialog.Builder(context).setView(binding.root)
        val mAlertDialog = dialogBuilder.create()
        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Android Version 4.4 이하에서 Blue Line이 상단에 나타는 것 방지
        mAlertDialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        mAlertDialog.setCancelable(false)

        with(binding) {
            tvDefaultDialogDescription.setText(dialogDescriptionResId)
            tvDefaultDialogConfirm.setOnDebounceClickListener {
                mAlertDialog.dismiss()
            }
        }

        return mAlertDialog
    }
}

object DefaultDialogConfirm {
    fun createDialog(context: Context, dialogDescriptionResId: Int,
                     confirmButtonShow: Boolean, cancelButtonShow: Boolean,
                     confirmButtonTextResId: Int?, cancelButtonTextResId: Int?,
                     confirmButtonFunction: (() -> Unit)?, cancelButtonFunction: (() -> Unit)?) : AlertDialog {
        val binding = DialogDefaultConfirmBinding.inflate(LayoutInflater.from(context))
        val dialogBuilder = AlertDialog.Builder(context).setView(binding.root)
        val mAlertDialog = dialogBuilder.create()
        mAlertDialog.setCancelable(false)

        with(binding) {
            tvDefaultDialogDescription.setText(dialogDescriptionResId)

            if(confirmButtonShow){
                tvDefaultDialogConfirm.visibility = View.VISIBLE
                confirmButtonTextResId?.let { tvDefaultDialogConfirm.setText(it) }
            } else {
                tvDefaultDialogConfirm.visibility = View.GONE
            }
            if(cancelButtonShow){
                tvDefaultDialogCancel.visibility = View.VISIBLE
                cancelButtonTextResId?.let { tvDefaultDialogCancel.setText(it) }
            } else {
                tvDefaultDialogCancel.visibility = View.GONE
            }

            tvDefaultDialogConfirm.setOnDebounceClickListener {
                if (confirmButtonFunction != null) {
                    confirmButtonFunction()
                }
                mAlertDialog.dismiss()
            }
            tvDefaultDialogCancel.setOnDebounceClickListener {
                if(cancelButtonFunction != null) {
                    cancelButtonFunction()
                }
                mAlertDialog.dismiss()
            }
        }

        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return mAlertDialog
    }
}

object DefaultDialogExplanationConfirm {
    fun createDialog(context: Context, dialogDescriptionResId: Int, dialogExplanationResId: Int,
                     confirmButtonShow: Boolean = true, cancelButtonShow: Boolean,
                     confirmButtonTextResId: Int?, cancelButtonTextResId: Int?,
                     confirmButtonFunction: (() -> Unit)?, cancelButtonFunction: (() -> Unit)?) : AlertDialog {
        val binding = DialogDefaltExplanationConfirmBinding.inflate(LayoutInflater.from(context))
        val dialogBuilder = AlertDialog.Builder(context).setView(binding.root)
        val mAlertDialog = dialogBuilder.create()
        mAlertDialog.setCancelable(false)

        with(binding) {
            tvDefaultDialogDescription.setText(dialogDescriptionResId)
            tvDefaultDialogExplanation.setText(dialogExplanationResId)

            if(confirmButtonShow){
                tvDefaultDialogExplanationConfirm.visibility = View.VISIBLE
                confirmButtonTextResId?.let { tvDefaultDialogExplanationConfirm.setText(it) }
            } else {
                tvDefaultDialogExplanationConfirm.visibility = View.GONE
            }
            if(cancelButtonShow){
                tvDefaultDialogExplanationCancel.visibility = View.VISIBLE
                cancelButtonTextResId?.let { tvDefaultDialogExplanationCancel.setText(it) }
            } else {
                tvDefaultDialogExplanationConfirm.setBackgroundResource(R.drawable.selector_dialog_confirm_default_button)
                tvDefaultDialogExplanationCancel.visibility = View.GONE
            }

            tvDefaultDialogExplanationConfirm.setOnDebounceClickListener {
                if (confirmButtonFunction != null) {
                    confirmButtonFunction()
                }
                mAlertDialog.dismiss()
            }
            tvDefaultDialogExplanationCancel.setOnDebounceClickListener {
                if(cancelButtonFunction != null) {
                    cancelButtonFunction()
                }
                mAlertDialog.dismiss()
            }
        }

        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return mAlertDialog
    }
}

object DefaultDialogConfigure {
    fun getDeviceWidthSize(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT < 30){
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.x
        }else{
            val rect = windowManager.currentWindowMetrics.bounds
            return rect.width()
        }
    }
    fun getDeviceHeightSize(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT < 30){
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.y
        }else{
            val rect = windowManager.currentWindowMetrics.bounds
            return rect.height()
        }
    }

    fun dialogResize(context: Context, dialog: Dialog, width: Float, height: Float){
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT < 30){
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)

            val window = dialog.window
            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()

            window?.setLayout(x, y)
        }else{
            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialog.window
            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }
}