package com.daily.dayo.post

import android.graphics.Canvas
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.daily.dayo.R
import java.lang.Float.max
import java.lang.Float.min
import kotlin.math.absoluteValue

class SwipeHelperCallback : Callback() {
    private var currentPosition: Int? = null
    private var previousPosition: Int? = null
    private var currentDx = 0f
    private var clamp = 0f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, LEFT or RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        currentDx = 0f
        previousPosition = viewHolder.adapterPosition
        getDefaultUIUtil().clearView(getView(viewHolder))
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        viewHolder?.let {
            currentPosition = viewHolder.adapterPosition
            getDefaultUIUtil().onSelected(getView(it))
        }
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        val isClamped = getTag(viewHolder)
        // 현재 View가 고정되어있지 않고 사용자가 -clamp 이상 swipe시 isClamped true로 변경 아닐시 false로 변경
        setTag(viewHolder, !isClamped && currentDx <= -clamp)
        return 2f
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ACTION_STATE_SWIPE) {
            val view = getView(viewHolder)
            val isClamped = getTag(viewHolder)

            val x =  clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)
            deleteButtonActivate(x.absoluteValue, isClamped, recyclerView)
            currentDx = x

            getDefaultUIUtil().onDraw(
                c,
                recyclerView,
                view,
                x,
                dY,
                actionState,
                isCurrentlyActive
            )
        }
    }
    private fun clampViewPositionHorizontal(
        view: View,
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ) : Float {
        // View의 가로 길이의 절반까지만 swipe 되도록
        val min: Float = -view.width.toFloat()/2
        // RIGHT 방향으로 swipe 막기
        val max: Float = 0f

        val x = if (isClamped) {
            // View가 고정되었을 때 swipe되는 영역 제한
            if (isCurrentlyActive) dX - clamp else -clamp
        } else {
            dX
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            min(max(min, x), max)
        } else {
            TODO("VERSION.SDK_INT < N")
        }
    }

    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        // isClamped를 view의 tag로 관리
        viewHolder.itemView.tag = isClamped
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder) : Boolean {
        // isClamped를 view의 tag로 관리
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    fun setClamp(clamp: Float) {
        this.clamp = clamp
    }

    // 다른 View가 swipe 되거나 터치되면 고정 해제
    fun removePreviousClamp(recyclerView: RecyclerView) {
        if (currentPosition == previousPosition)
            return
        previousPosition?.let {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
            getView(viewHolder).translationX = 0f
            setTag(viewHolder, false)
            previousPosition = null
        }
    }

    // Swipe되었을 때 Delete 버튼 활성화
    fun deleteButtonActivate(x : Float, isClamped: Boolean, recyclerView: RecyclerView) {
        if(isClamped) {
            currentPosition?.let {
                // Delete TextView Width 크기를 Swipe로 늘어나는 만큼만 조정
                recyclerView[currentPosition!!].findViewById<TextView>(R.id.tv_delete).width = x.toInt()
                recyclerView[currentPosition!!].findViewById<TextView>(R.id.tv_delete).isEnabled = true
            }
        }else {
            previousPosition?.let {
                // Delete TextView Width 크기를 WRAP_CONTENT로 복구
                ConstraintSet().constrainDefaultWidth(recyclerView[previousPosition!!].findViewById<TextView>(R.id.tv_delete).id, ConstraintSet.WRAP_CONTENT)
                recyclerView[previousPosition!!].findViewById<TextView>(R.id.tv_delete).isEnabled = false
            }
        }
    }

    private fun getView(viewHolder: RecyclerView.ViewHolder): View {
        return (viewHolder as SwipeListAdapter.SwipeViewHolder).itemView.findViewById(R.id.layout_post_comment)
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }

}