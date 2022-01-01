package com.daily.dayo.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.daily.dayo.databinding.DialogDefaultAlertBinding

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
            tvDefaultDialogConfirm.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        return mAlertDialog
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