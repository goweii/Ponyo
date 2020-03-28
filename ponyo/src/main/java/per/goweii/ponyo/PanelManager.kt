package per.goweii.ponyo

import android.animation.Animator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.Scroller
import per.goweii.ponyo.log.Ponlog
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

/**
 * @author CuiZhen
 * @date 2020/3/28
 */
class PanelManager(
    private val context: Context
) : GestureDetector.OnGestureListener {
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
            windowAnimations = 0
            rotationAnimation = 0
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            }
            format = PixelFormat.RGBA_8888
            gravity = Gravity.TOP or Gravity.LEFT
            flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
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

    private val floatView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.layout_float, null).apply {
            elevation = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                3F,
                context.resources.displayMetrics
            )
            viewTreeObserver.addOnGlobalLayoutListener {
                this@PanelManager.computeScroll()
            }
            setOnTouchListener { _, event ->
                false
            }
        }
    }
    private val floatIcon: ImageView by lazy {
        floatView.findViewById<ImageView>(R.id.iv_icon).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    updateOutline(outline)
                }
            }
            setOnTouchListener { _, event ->
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

    enum class State {
        FLOAT, DRAGGING, FLING, PANEL, ZOOMING_TO_FLOAT, ZOOMING_TO_PANEL
    }

    fun icon(resId: Int) = apply {
        floatIcon.setImageResource(resId)
    }

    fun show() {
        if (floatView.isAttachedToWindow) return
        try {
            windowManager.addView(floatView, windowParams)
        } catch (e: Exception) {
            Ponlog.e { e }
        }
    }

    fun dismiss() {
        if (!floatView.isAttachedToWindow) return
        try {
            windowManager.removeView(floatView)
        } catch (e: Exception) {
            Ponlog.e { e }
        }
    }

    fun update() {
        if (!floatView.isAttachedToWindow) return
        try {
            windowManager.updateViewLayout(floatView, windowParams)
        } catch (e: Exception) {
            Ponlog.e { e }
        }
    }

    private fun updateLocation(x: Int, y: Int) {
        val inx = x >= fenceRect.left && x <= fenceRect.right
        val iny = y >= fenceRect.top && x <= fenceRect.bottom
        if (inx || iny) {
            windowParams.x = x.range(fenceRect.left.toInt(), fenceRect.right.toInt())
            windowParams.y = y.range(fenceRect.top.toInt(), fenceRect.bottom.toInt())
        } else {
            scroller.abortAnimation()
        }
    }

    private fun updateSize(w: Int, h: Int) {
        windowParams.width = w
        windowParams.height = h
    }

    private fun updateOutline(outline: Outline) {
        val f = 1F - (floatIcon.layoutParams.width.toFloat() - defSize.toFloat()) /
                (fenceRect.width().toFloat() - defSize.toFloat())
        val radius = (min(floatIcon.layoutParams.width, floatIcon.layoutParams.height).toFloat() / 2F) * f
        outline.setRoundRect(
            0, 0, floatIcon.layoutParams.width, floatIcon.layoutParams.height,
            radius
        )
    }

    private fun Int.range(from: Int, to: Int) = when {
        this < from -> from
        this > to -> to
        else -> this
    }

    override fun onDown(e: MotionEvent): Boolean {
        Log.d("FloatManager", "onDown")
        return true
    }

    private fun onUp(e: MotionEvent) {
        Log.d("FloatManager", "onUp")
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
        Log.d("FloatManager", "onShowPress")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Log.d("FloatManager", "onSingleTapUp")
        scroller.abortAnimation()
        when (state) {
            State.FLOAT -> {
                state = State.ZOOMING_TO_PANEL
                startZooming2Panel()
            }
            State.FLING -> {
                state = State.ZOOMING_TO_PANEL
                startZooming2Panel()
            }
            State.DRAGGING -> {
                state = State.ZOOMING_TO_PANEL
                startZooming2Panel()
            }
            State.PANEL -> {
                state = State.ZOOMING_TO_FLOAT
                startZooming2Float()
            }
            State.ZOOMING_TO_FLOAT -> {
                // ignore
            }
            State.ZOOMING_TO_PANEL -> {
                // ignore
            }
        }
        return true
    }

    private val evaluator = object : TypeEvaluator<RectF> {
        private val currValue = RectF()
        override fun evaluate(fraction: Float, startValue: RectF, endValue: RectF): RectF {
            val l = startValue.left + (endValue.left - startValue.left) * fraction
            val t = startValue.top + (endValue.top - startValue.top) * fraction
            val w = startValue.width() + (endValue.width() - startValue.width()) * fraction
            val h = startValue.height() + (endValue.height() - startValue.height()) * fraction
            currValue.set(l, t, l + w, t + h)
            return currValue
        }
    }

    private val zoomAnimator: ValueAnimator by lazy {
        ValueAnimator.ofObject(evaluator, RectF(), RectF()).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 3000L
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    when (state) {
                        State.ZOOMING_TO_FLOAT -> {
                            state = State.FLOAT
                            endZooming2Float()
                        }
                        State.ZOOMING_TO_PANEL -> {
                            state = State.PANEL
                            endZooming2Panel()
                        }
                        else -> {
                            // ignore
                        }
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationStart(animation: Animator) {
                }
            })
            addUpdateListener {
                onZooming(it.animatedValue as RectF)
            }
        }
    }

    private fun calcPercent(): Float {
        return 1F - (windowParams.width.toFloat() - defSize.toFloat()) /
                (fenceRect.width().toFloat() - defSize.toFloat())
    }

    private val lastRectF: RectF by lazy { RectF() }

    private fun startZooming2Panel() {
        val startRectF = windowParams.toRectF()
        if (startRectF.width() == defSize.toFloat() && startRectF.height() == defSize.toFloat()) {
            lastRectF.set(startRectF)
        }
        updateLocation(fenceRect.left.toInt(), fenceRect.top.toInt())
        updateSize(fenceRect.width().toInt(), fenceRect.height().toInt())
        update()
        floatIcon.offsetToRectF(startRectF)
        zoomAnimator.setObjectValues(startRectF, fenceRect)
        zoomAnimator.start()
    }

    private fun endZooming2Panel() {
    }

    private fun startZooming2Float() {
        val currRectF: RectF = zoomAnimator.animatedValue as RectF
        zoomAnimator.setObjectValues(currRectF, lastRectF)
        zoomAnimator.start()
    }

    private fun endZooming2Float() {
        updateLocation(lastRectF.left.toInt(), lastRectF.top.toInt())
        updateSize(lastRectF.width().toInt(), lastRectF.height().toInt())
        update()
        floatIcon.offsetToRectF(RectF(0F, 0F, lastRectF.width(), lastRectF.height()))
    }

    private fun WindowManager.LayoutParams.toRectF(): RectF = this.let {
        RectF(
            it.x.toFloat(),
            it.y.toFloat(),
            it.x.toFloat() + width.toFloat(),
            it.y.toFloat() + height.toFloat()
        )
    }

    private fun onZooming(currRectF: RectF) {
        floatIcon.offsetToRectF(currRectF)
    }

    private fun View.offsetToRectF(rectF: RectF) {
        val lp = layoutParams as ViewGroup.MarginLayoutParams
        lp.width = rectF.width().toInt()
        lp.height = rectF.height().toInt()
        lp.leftMargin = rectF.left.toInt()
        lp.topMargin = rectF.top.toInt()
        requestLayout()
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        Log.d("FloatManager", "onScroll[$distanceX,$distanceY]")
        e2.setLocation(e2.rawX, e2.rawY)
        velocityTracker.addMovement(e2)
        val touchX = e2.rawX
        val touchY = e2.rawY
        when (state) {
            State.FLING -> {
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
            State.FLOAT -> {
            }
            State.PANEL -> {
            }
            State.ZOOMING_TO_FLOAT -> {
            }
            State.ZOOMING_TO_PANEL -> {
            }
        }
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        Log.d("FloatManager", "onLongPress")
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.d("FloatManager", "onFling[$velocityX,$velocityY]")
        return false
    }

    private fun onDragStart() {
        Log.d("FloatManager", "onDragStart")
        dragStartX = windowParams.x.toFloat()
        dragStartY = windowParams.y.toFloat()
        dragPath.reset()
        dragPath.rewind()
        dragPath.moveTo(windowParams.x.toFloat(), windowParams.y.toFloat())
    }

    private fun onDragging(moveX: Float, moveY: Float) {
        Log.d("FloatManager", "onDragging[$moveX,$moveY]")
        val x = dragStartX + moveX
        val y = dragStartY + moveY
        val cX: Float = (x + windowParams.x) / 2f
        val cY: Float = (y + windowParams.y) / 2f
        dragPath.quadTo(dragStartX, dragStartY, cX, cY)
        updateLocation(x.toInt(), y.toInt())
        update()
    }

    private fun onDragEnd(velocityX: Float, velocityY: Float) {
        Log.d("FloatManager", "onDragEnd[$velocityX,$velocityY]")
        val startX: Float = windowParams.x.toFloat()
        val startY: Float = windowParams.y.toFloat()
        val startCenterX = startX + floatView.width / 2f
        val startCenterY = startY + floatView.height / 2f
        val endX: Float
        endX = if (startCenterX < fenceRect.width() / 2f) {
            0f
        } else {
            fenceRect.width() - floatView.width.toFloat()
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
        Log.d("FloatManager", "onDragEnd degrees=$degrees")
        Log.d("FloatManager", "onDragEnd end[$endX,$endY]")
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
