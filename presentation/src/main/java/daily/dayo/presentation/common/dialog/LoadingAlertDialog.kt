package daily.dayo.presentation.common.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import daily.dayo.presentation.R

object LoadingAlertDialog {
    fun createLoadingDialog(context: Context): AlertDialog {
        val dialogView =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.dialog_loading_wait,
                null
            )
        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        return alertDialog
    }

    fun resizeDialogFragment(context: Context, dialog: AlertDialog, dialogSizeRatio: Float = 0.9f) {
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = getDeviceWidthSize(context)
        params?.width = (deviceWidth * dialogSizeRatio).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    fun showLoadingDialog(dialog: AlertDialog) {
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Radius 적용을 위해 필요
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.show()
    }

    fun hideLoadingDialog(dialog: AlertDialog) {
        dialog.dismiss()
    }

    fun getDeviceWidthSize(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.x
        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            return rect.width()
        }
    }
}