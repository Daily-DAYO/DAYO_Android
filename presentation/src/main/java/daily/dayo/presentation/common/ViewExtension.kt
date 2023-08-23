package daily.dayo.presentation.common

import android.view.View

fun View.setOnDebounceClickListener(interval: Long = 1000L, action: (View) -> Unit) {
    val debounceClickListener = object : View.OnClickListener {
        private var lastClickedMillis = 0L

        override fun onClick(view: View) {
            val now = System.currentTimeMillis()
            if (now - lastClickedMillis < interval) {
                return
            }
            lastClickedMillis = now
            action.invoke(view)
        }
    }
    setOnClickListener(debounceClickListener)
}
