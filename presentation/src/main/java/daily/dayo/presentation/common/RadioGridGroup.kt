package daily.dayo.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.CompoundButton
import android.widget.GridLayout
import androidx.appcompat.widget.AppCompatRadioButton
import java.util.concurrent.atomic.AtomicInteger

/**
 * This class is used to create a multiple-exclusion scope for a set of radio
 * buttons. Checking one radio button that belongs to a radio group unchecks
 * any previously checked radio button within the same group.
 *
 * Adapted from https://gist.github.com/saiaspire/a73135cfee1110a64cb0ab3451b6ca33.
 *
 * Intially, all of the radio buttons are unchecked. While it is not possible
 * to uncheck a particular radio button, the radio group can be cleared to
 * remove the checked state.
 *
 *
 * The selection is identified by the unique id of the radio button as defined
 * in the XML layout file.
 *
 *
 * See
 * [GridLayout.LayoutParams][android.widget.GridLayout.LayoutParams]
 * for layout attributes.
 *
 * @see AppCompatRadioButton
 */
class RadioGridGroup : GridLayout {
    var checkedCheckableImageButtonId = NOTHING_CHECKED
    private var childOnCheckedChangeListener: CompoundButton.OnCheckedChangeListener? = null
    private var protectFromCheckedChange = false
    private var onCheckedChangeListener: OnCheckedChangeListener? = null
    private var passThroughListener: PassThroughHierarchyChangeListener? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        childOnCheckedChangeListener = CheckedStateTracker()
        passThroughListener = PassThroughHierarchyChangeListener()
        super.setOnHierarchyChangeListener(passThroughListener)
    }

    override fun setOnHierarchyChangeListener(listener: OnHierarchyChangeListener) {
        passThroughListener?.mOnHierarchyChangeListener = listener
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (checkedCheckableImageButtonId != NOTHING_CHECKED) {
            protectFromCheckedChange = true
            setCheckedStateForView(checkedCheckableImageButtonId, true)
            protectFromCheckedChange = false
            setCheckedId(checkedCheckableImageButtonId)
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child is AppCompatRadioButton) {
            val button = child
            if (button.isChecked) {
                protectFromCheckedChange = true
                if (checkedCheckableImageButtonId != NOTHING_CHECKED) {
                    setCheckedStateForView(checkedCheckableImageButtonId, false)
                }
                protectFromCheckedChange = false
                setCheckedId(button.id)
            }
        }
        super.addView(child, index, params)
    }

    fun check(id: Int) {
        if (id != NOTHING_CHECKED && id == checkedCheckableImageButtonId) {
            return
        }
        if (checkedCheckableImageButtonId != NOTHING_CHECKED) {
            setCheckedStateForView(checkedCheckableImageButtonId, false)
        }
        if (id != NOTHING_CHECKED) {
            setCheckedStateForView(id, true)
        }
        setCheckedId(id)
    }

    private fun setCheckedId(id: Int) {
        checkedCheckableImageButtonId = id
        if (onCheckedChangeListener != null) {
            onCheckedChangeListener?.onCheckedChanged(this, checkedCheckableImageButtonId)
        }
    }

    private fun setCheckedStateForView(viewId: Int, checked: Boolean) {
        val checkedView = findViewById<View>(viewId)
        if (checkedView != null && checkedView is AppCompatRadioButton) {
            checkedView.isChecked = checked
        }
    }

    fun clearCheck() {
        check(NOTHING_CHECKED)
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        onCheckedChangeListener = listener
    }

    override fun onInitializeAccessibilityEvent(event: AccessibilityEvent) {
        super.onInitializeAccessibilityEvent(event)
        event.className = RadioGridGroup::class.java.name
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.className = RadioGridGroup::class.java.name
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(group: RadioGridGroup?, checkedId: Int)
    }

    private inner class CheckedStateTracker : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (protectFromCheckedChange) {
                return
            }
            protectFromCheckedChange = true
            if (checkedCheckableImageButtonId != NOTHING_CHECKED) {
                setCheckedStateForView(checkedCheckableImageButtonId, false)
            }
            protectFromCheckedChange = false
            val id = buttonView.id
            setCheckedId(id)
        }
    }

    private inner class PassThroughHierarchyChangeListener : OnHierarchyChangeListener {
        var mOnHierarchyChangeListener: OnHierarchyChangeListener? = null
        override fun onChildViewAdded(parent: View, child: View) {
            if (parent === this@RadioGridGroup && child is AppCompatRadioButton) {
                var id = child.getId()
                // generates an id if it's missing
                if (id == NO_ID) {
                    id = generateViewId()
                    child.setId(id)
                }
                child.setOnCheckedChangeListener(
                    childOnCheckedChangeListener
                )
            }
            mOnHierarchyChangeListener?.onChildViewAdded(parent, child)
        }

        override fun onChildViewRemoved(parent: View, child: View) {
            if (parent === this@RadioGridGroup && child is AppCompatRadioButton) {
                child.setOnCheckedChangeListener(null)
            }
            mOnHierarchyChangeListener?.onChildViewRemoved(parent, child)
        }
    }

    companion object {
        const val NOTHING_CHECKED = -1
        private const val TRANSPARENT_COLOR = 0x00FFFFFF

        private val nextGeneratedId = AtomicInteger(1)
        fun generateViewId(): Int {
            while (true) {
                val result = nextGeneratedId.get()

                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                var newValue = result + 1
                if (newValue > TRANSPARENT_COLOR) newValue = 1 // Roll over to 1, not 0.
                if (nextGeneratedId.compareAndSet(result, newValue)) {
                    return result
                }
            }
        }
    }
}