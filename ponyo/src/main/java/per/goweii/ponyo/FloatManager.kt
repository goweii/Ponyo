package per.goweii.ponyo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.TypedValue
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.Scroller
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

/**
 * @author CuiZhen
 * @date 2020/3/28
 */
internal class FloatManager(private val context: Context) : GestureDetector.OnGestureListener {

    private val panelManager: PanelManager by lazy {
        PanelManager(context).apply {
            onDetachListener {
                if (toDismiss) {
                    dismiss()
                }
            }
        }
    }

    private val fenceRect: RectF by lazy {
        val rect = Rect()
        windowManager.defaultDisplay.getRectSize(rect)
        RectF(rect)
    }
    private val defSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        48F,
        context.resources.displayMetrics
    ).toInt()
    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    @SuppressLint("RtlHardcoded")
    private val windowParams: WindowManager.LayoutParams =
        WindowManager.LayoutParams().apply {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            format = PixelFormat.RGBA_8888
            gravity = Gravity.TOP or Gravity.LEFT
            flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            width = defSize
            height = defSize
            x = fenceRect.width().toInt() - width
            y = ((fenceRect.height() - height) * 0.6F).toInt()
        }

    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(context, this)
    }
    private val scroller: Scroller by lazy {
        Scroller(context, DecelerateInterpolator())
    }
    private val velocityTracker: VelocityTracker by lazy {
        VelocityTracker.obtain()
    }
    private val dragPath: Path = Path()

    private val floatView: ImageView by lazy {
        ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            elevation = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                0F,
                context.resources.displayMetrics
            )
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val radius = (min(
                        this@FloatManager.floatView.measuredWidth,
                        this@FloatManager.floatView.measuredHeight
                    ).toFloat() / 2F)
                    outline.setRoundRect(
                        0,
                        0,
                        this@FloatManager.floatView.measuredWidth,
                        this@FloatManager.floatView.measuredHeight,
                        radius
                    )
                }
            }
            viewTreeObserver.addOnGlobalLayoutListener {
                this@FloatManager.computeScroll()
            }
            setOnTouchListener { _, event ->
                event.setLocation(event.rawX, event.rawY)
                val consumed = gestureDetector.onTouchEvent(event)
                when (event.action) {
                    MotionEvent.ACTION_UP -> onUp(event)
                }
                consumed
            }
        }
    }

    private var state: State = State.FLOAT
    private var dragStartX = 0f
    private var dragStartY = 0f
    private var dragStartEventX = 0f
    private var dragStartEventY = 0f

    private var toDismiss = false

    enum class State {
        FLOAT, DRAGGING, FLING
    }

    fun icon(resId: Int) = apply {
        this.floatView.setImageResource(resId)
        panelManager.icon(resId)
    }

    fun isShown() = this.floatView.isAttachedToWindow

    fun expand() {
        panelManager.show(currRectF())
    }

    fun collapse() {
        panelManager.dismiss(currRectF())
    }

    fun toggle() {
        panelManager.toggle(currRectF())
    }

    fun show() {
        if (isShown()) return
        toDismiss = false
        attach()
    }

    fun dismiss() {
        if (!isShown()) return
        if (panelManager.isShown()) {
            toDismiss = true
            collapse()
        } else {
            detach()
        }
    }

    fun attach() {
        if (isShown()) return
        try {
            windowManager.addView(this.floatView, windowParams)
        } catch (e: Exception) {
        }
    }

    fun detach() {
        if (!isShown()) return
        try {
            windowManager.removeView(this.floatView)
        } catch (e: Exception) {
        }
    }

    fun update() {
        if (!isShown()) return
        try {
            windowManager.updateViewLayout(this.floatView, windowParams)
        } catch (e: Exception) {
        }
    }

    private fun updateLocation(x: Int, y: Int) {
        val inx = x >= fenceRect.left && x <= fenceRect.right
        val iny = y >= fenceRect.top && x <= fenceRect.bottom
        if (inx || iny) {
            windowParams.x = x.range(fenceRect.left.toInt(), fenceRect.right.toInt())
            windowParams.y = y.range(fenceRect.top.toInt(), fenceRect.bottom.toInt())
            panelManager.update(currRectF())
        } else {
            scroller.abortAnimation()
        }
    }

    private fun Int.range(from: Int, to: Int) = when {
        this < from -> from
        this > to -> to
        else -> this
    }

    override fun onDown(e: MotionEvent): Boolean {
        scroller.abortAnimation()
        return true
    }

    private fun onUp(e: MotionEvent) {
        if (state != State.DRAGGING) {
            return
        }
        state = State.FLING
        velocityTracker.computeCurrentVelocity(
            1000,
            max(fenceRect.width(), fenceRect.height()).toFloat()
        )
        val vx = velocityTracker.xVelocity
        val vy = velocityTracker.yVelocity
        velocityTracker.clear()
        onDragEnd(vx, vy)
        return
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        toggle()
        return true
    }

    private fun currRectF(): RectF = RectF(
        windowParams.x.toFloat(),
        windowParams.y.toFloat(),
        windowParams.x.toFloat() + windowParams.width.toFloat(),
        windowParams.y.toFloat() + windowParams.height.toFloat()
    )

    override fun onScroll(
        e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float
    ): Boolean {
        velocityTracker.addMovement(e2)
        val touchX = e2.rawX
        val touchY = e2.rawY
        when (state) {
            State.FLOAT -> {
                state = State.DRAGGING
                dragStartEventX = touchX
                dragStartEventY = touchY
                onDragStart()
            }
            State.DRAGGING -> {
                val dargX = touchX - dragStartEventX
                val dargY = touchY - dragStartEventY
                onDragging(dargX, dargY)
            }
            State.FLING -> {
                state = State.DRAGGING
                dragStartEventX = touchX
                dragStartEventY = touchY
                onDragStart()
            }
        }
        return true
    }

    override fun onLongPress(e: MotionEvent) {
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    private fun onDragStart() {
        dragStartX = windowParams.x.toFloat()
        dragStartY = windowParams.y.toFloat()
        dragPath.reset()
        dragPath.rewind()
        dragPath.moveTo(windowParams.x.toFloat(), windowParams.y.toFloat())
    }

    private fun onDragging(moveX: Float, moveY: Float) {
        val x = dragStartX + moveX
        val y = dragStartY + moveY
        val cX: Float = (x + windowParams.x) / 2f
        val cY: Float = (y + windowParams.y) / 2f
        dragPath.quadTo(dragStartX, dragStartY, cX, cY)
        updateLocation(x.toInt(), y.toInt())
        update()
    }

    private fun onDragEnd(velocityX: Float, velocityY: Float) {
        val startX: Float = windowParams.x.toFloat()
        val startY: Float = windowParams.y.toFloat()
        val startCenterX = startX + this.floatView.width / 2f
        val startCenterY = startY + this.floatView.height / 2f
        val endX: Float
        endX = if (startCenterX < fenceRect.width() / 2f) {
            0f
        } else {
            fenceRect.width() - this.floatView.width.toFloat()
        }
        val endY: Float
        endY = if (velocityX == 0f && velocityY == 8f) {
            startY
        } else {
            val dx = endX - startX
            val dy = abs(dx) * (velocityY / abs(velocityX))
            startY + dy
        }
        val pm = PathMeasure(dragPath, false)
        val pos = FloatArray(2)
        val tan = FloatArray(2)
        pm.getPosTan(pm.length, pos, tan)
        val degrees = (atan2(
            tan[1].toDouble(),
            tan[0].toDouble()
        ) * 180f / Math.PI).toFloat()
        scroller.startScroll(
            startX.toInt(),
            startY.toInt(),
            (endX - startX).toInt(),
            (startY - startY).toInt()
        )
        computeScroll()
    }

    private fun computeScroll() {
        if (state != State.FLING) {
            return
        }
        if (scroller.computeScrollOffset()) {
            val x = scroller.currX.toFloat()
            val y = scroller.currY.toFloat()
            updateLocation(x.toInt(), y.toInt())
            update()
        } else {
            state = State.FLOAT
        }
    }

}
