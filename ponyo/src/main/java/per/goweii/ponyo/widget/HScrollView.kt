package per.goweii.ponyo.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.HorizontalScrollView
import kotlin.math.abs

class HScrollView : HorizontalScrollView {
    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop
    private var downX = 0F
    private var downY = 0F

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = ev.x
                val moveY = ev.y
                val offX = moveX - downX
                val offY = moveY - downY
                if (abs(offX) >= touchSlop && abs(offY) < touchSlop) {
                    if (offX > 0 && !canScrollHorizontally(-1)) {
                        parent?.requestDisallowInterceptTouchEvent(false)
                        return false
                    } else if (offX < 0 && !canScrollHorizontally(1)) {
                        parent?.requestDisallowInterceptTouchEvent(false)
                        return false
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(true)
            }
            else -> {
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}