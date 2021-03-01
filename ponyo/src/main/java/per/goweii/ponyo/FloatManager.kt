package per.goweii.ponyo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.Scroller
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

/**
 * @author CuiZhen
 * @date 2020/3/28
 */
@SuppressLint("ClickableViewAccessibility", "InflateParams")
internal class FloatManager(private val context: Context) : GestureDetector.OnGestureListener,
    ViewTreeObserver.OnGlobalLayoutListener {

    private enum class State {
        FLOAT, DRAGGING, FLING
    }

    private enum class Mode {
        ICON, ASSERT, ERROR, WARN
    }

    private val panelManager: PanelManager by lazy {
        PanelManager(context)
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
            format = PixelFormat.TRANSPARENT
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

    private val floatView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.ponyo_float, null).apply {
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val radius = min(
                        view.width,
                        view.height
                    ).toFloat() / 2F
                    outline.setRoundRect(
                        0,
                        0,
                        view.width,
                        view.height,
                        radius
                    )
                }
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
    private val iconView: ImageView by lazy {
        floatView.findViewById<ImageView>(R.id.ponyo_iv_icon)
    }
    private val logView: TextView by lazy {
        floatView.findViewById<TextView>(R.id.ponyo_tv_log)
    }

    private var state: State = State.FLOAT
    private var dragStartX = 0f
    private var dragStartY = 0f
    private var dragStartEventX = 0f
    private var dragStartEventY = 0f

    private val visibleRunnable = Runnable { toVisible() }
    private val invisibleRunnable = Runnable { toInvisible() }

    private var currMode = Mode.ICON
    private var lastSwitchModeTime = 0L
    private var switchModeAnimRunning = false
    private val autoSwitchModeRunnable = Runnable { autoSwitchMode() }

    fun icon(resId: Int) = apply {
        this.iconView.setImageResource(resId)
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
        detach()
        floatView.post { attach() }
    }

    fun show() {
        attach()
    }

    fun dismiss() {
        if (panelManager.isShown()) {
            panelManager.onDetachListener {
                panelManager.onDetachListener(null)
                detach()
            }
            collapse()
        } else {
            detach()
        }
    }

    private fun attach() {
        if (isShown()) return
        floatView.viewTreeObserver.addOnGlobalLayoutListener(this)
        floatView.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                floatView.viewTreeObserver.removeOnPreDrawListener(this)
                runInvisible()
                return true
            }
        })
        try {
            windowManager.addView(this.floatView, windowParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun detach() {
        if (!isShown()) return
        floatView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        try {
            windowManager.removeView(this.floatView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun update() {
        if (!isShown()) return
        try {
            windowManager.updateViewLayout(this.floatView, windowParams)
        } catch (e: Exception) {
            e.printStackTrace()
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
        runVisible()
        scroller.abortAnimation()
        return true
    }

    private fun onUp(e: MotionEvent) {
        runInvisible()
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

    override fun onGlobalLayout() {
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

    private fun runVisible() {
        floatView.removeCallbacks(visibleRunnable)
        floatView.removeCallbacks(invisibleRunnable)
        floatView.postDelayed(visibleRunnable, ViewConfiguration.getTapTimeout().toLong())
    }

    private fun runInvisible() {
        floatView.removeCallbacks(visibleRunnable)
        floatView.removeCallbacks(invisibleRunnable)
        floatView.postDelayed(invisibleRunnable, 3000)
    }

    private fun toVisible() {
        floatView.animate().alpha(1F).start()
    }

    private fun toInvisible() {
        floatView.animate().alpha(0.6F).start()
    }

    private var unreadAssertCount = 0
    private var unreadErrorCount = 0
    private var unreadWarnCount = 0

    fun setLogAssertCount(count: Int) {
        unreadAssertCount = count
        if (unreadAssertCount > 0) {
            switchToMode(Mode.ASSERT, ignoreLastSwitchTime = true)
        } else {
            switchToIconIfNeeded()
        }
    }

    fun setLogErrorCount(count: Int) {
        unreadErrorCount = count
        if (unreadErrorCount > 0) {
            switchToMode(Mode.ERROR, ignoreLastSwitchTime = true)
        } else {
            switchToIconIfNeeded()
        }
    }

    fun setLogWarnCount(count: Int) {
        unreadWarnCount = count
        if (unreadWarnCount > 0) {
            switchToMode(Mode.WARN, ignoreLastSwitchTime = true)
        } else {
            switchToIconIfNeeded()
        }
    }

    private fun switchToIconIfNeeded() {
        if (unreadAssertCount == 0 &&
            unreadErrorCount == 0 &&
            unreadWarnCount == 0
        ) {
            floatView.removeCallbacks(autoSwitchModeRunnable)
            switchToMode(Mode.ICON, ignoreAnimRunning = true, ignoreLastSwitchTime = true)
        }
    }

    private fun autoSwitchMode() {
        switchToMode(nextMode)
    }

    private fun switchToMode(
        mode: Mode,
        ignoreAnimRunning: Boolean = false,
        ignoreLastSwitchTime: Boolean = false
    ) {
        if (!ignoreAnimRunning && switchModeAnimRunning) {
            return
        }
        if (currMode == mode) {
            switchToModeImmediately(true)
            return
        }
        val currTime = System.currentTimeMillis()
        if (!ignoreLastSwitchTime && currTime - lastSwitchModeTime < 3000) {
            return
        }
        lastSwitchModeTime = currTime
        currMode = mode
        floatView.removeCallbacks(autoSwitchModeRunnable)
        floatView.postDelayed(autoSwitchModeRunnable, delay)
        switchModeAnimRunning = true
        floatView.animate()
            .setInterpolator(AccelerateInterpolator())
            .setDuration(100)
            .scaleX(0F)
            .withEndAction {
                switchToModeImmediately(false)
                floatView.animate()
                    .setInterpolator(DecelerateInterpolator())
                    .setDuration(100)
                    .scaleX(1F)
                    .withEndAction {
                        switchModeAnimRunning = false
                        switchToModeImmediately(true)
                    }
                    .start()
            }
            .start()
    }

    private fun switchToModeImmediately(onlyChangeCount: Boolean) {
        fun formatCount(count: Int): String {
            return when {
                count < 0 -> "0"
                count > 999 -> "999+"
                else -> "$count"
            }
        }
        when (currMode) {
            Mode.ICON -> {
                if (!onlyChangeCount) {
                    logView.visibility = View.GONE
                    logView.text = ""
                    logView.setBackgroundColor(Color.TRANSPARENT)
                }
            }
            Mode.ASSERT -> {
                logView.text = formatCount(unreadAssertCount)
                if (!onlyChangeCount) {
                    logView.visibility = View.VISIBLE
                    logView.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.ponyo_colorLogAssert
                        )
                    )
                }
            }
            Mode.ERROR -> {
                logView.text = formatCount(unreadErrorCount)
                if (!onlyChangeCount) {
                    logView.visibility = View.VISIBLE
                    logView.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.ponyo_colorLogError
                        )
                    )
                }
            }
            Mode.WARN -> {
                logView.text = formatCount(unreadWarnCount)
                if (!onlyChangeCount) {
                    logView.visibility = View.VISIBLE
                    logView.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.ponyo_colorLogWarn
                        )
                    )
                }
            }
        }
    }

    private val nextMode: Mode
        get() {
            fun Mode.next(): Mode {
                val nextIndex = when (ordinal) {
                    Mode.values().size - 1 -> 0
                    else -> ordinal + 1
                }
                return when (Mode.values()[nextIndex]) {
                    Mode.ICON -> Mode.ICON
                    Mode.ASSERT -> {
                        if (unreadAssertCount > 0) {
                            Mode.ASSERT
                        } else {
                            Mode.ASSERT.next()
                        }
                    }
                    Mode.ERROR -> {
                        if (unreadErrorCount > 0) {
                            Mode.ERROR
                        } else {
                            Mode.ERROR.next()
                        }
                    }
                    Mode.WARN -> {
                        if (unreadWarnCount > 0) {
                            Mode.WARN
                        } else {
                            Mode.WARN.next()
                        }
                    }
                }
            }
            return currMode.next()
        }

    private val delay: Long
        get() {
            return when (currMode) {
                Mode.ICON -> 5000L
                Mode.ASSERT -> 3000L
                Mode.ERROR -> 3000L
                Mode.WARN -> 3000L
            }
        }

}
