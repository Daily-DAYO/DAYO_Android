package com.daily.dayo.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(private val spanCount: Int, private val topSpacing: Int, private val horizontalSpacing: Int): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        if(position % 2 == 0) { outRect.right = horizontalSpacing }
        else { outRect.left = horizontalSpacing }

        if(position >= spanCount) {
            outRect.top = topSpacing
        }
    }
}