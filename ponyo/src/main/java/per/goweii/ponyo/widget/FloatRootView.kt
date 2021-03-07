package per.goweii.ponyo.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.FrameLayout
import kotlin.math.max

class FloatRootView : FrameLayout {
    private var callback: Callback? = null
    private val clipRect: RectF = RectF()
    private val clipRadii: FloatArray = floatArrayOf(0F, 0F, 0F, 0F, 0F, 0F, 0F, 0F)
    private val clipPath: Path = Path()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (isInEditMode) {
            updateToRect(0F, 0F, measuredWidth.toFloat(), measuredHeight.toFloat(), 0F)
        }
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    fun updateToRect(left: Float, top: Float, right: Float, bottom: Float, radii: Float) {
        clipRect.set(left, top, right, bottom)
        clipRadii.fill(radii)
        updateToRect()
    }

    fun updateToRect(rect: RectF, radii: Float) {
        clipRect.set(rect)
        clipRadii.fill(radii)
        updateToRect()
    }

    private fun updateToRect() {
        clipPath.reset()
        clipPath.rewind()
        clipPath.addRoundRect(clipRect, clipRadii, Path.Direction.CW)
        invalidate()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (super.dispatchKeyEvent(event)) {
            return true
        }
        return callback?.dispatchKeyEvent(event) ?: false
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        canvas.clipPath(clipPath, Region.Op.INTERSECT)
        canvas.translate(clipRect.left, clipRect.top)
        val scale = max(clipRect.width() / width, clipRect.height() / height)
        canvas.scale(scale, scale)
        canvas.translate(
            -(width - clipRect.width() / scale) / 2F,
            -(height - clipRect.height() / scale) / 2F
        )
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    interface Callback {
        fun dispatchKeyEvent(event: KeyEvent): Boolean
    }
}