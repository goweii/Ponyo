package per.goweii.ponyo.widget

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.FrameLayout

class FloatRootView: FrameLayout {
    private var callback: Callback? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val handled = callback?.dispatchKeyEvent(event) ?: false
        if (handled) {
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    interface Callback {
        fun dispatchKeyEvent(event: KeyEvent): Boolean
    }

}