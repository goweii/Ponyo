package per.goweii.ponyo

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnPreDraw
import kotlin.math.*

@SuppressLint("ClickableViewAccessibility", "InflateParams")
internal class FloatWindow(private val context: Context) : GestureDetector.OnGestureListener,
    ViewTreeObserver.OnGlobalLayoutListener {

    private enum class State {
        IDLE, DRAGGING, FLING
    }

    private enum class Mode {
        ICON, ASSERT, ERROR
    }

    private val panelWindow: PanelWindow = PanelWindow(context)
    private val currRect: RectF = RectF()
    private val boundRect: RectF by lazy {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(dm)
        RectF(0F, 0F, dm.widthPixels.toFloat(), dm.heightPixels.toFloat())
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
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            width = defSize
            height = defSize
            x = boundRect.width().toInt() - width
            y = ((boundRect.height() - height) * 0.6F).toInt()
        }

    private val gestureDetector: GestureDetector by lazy {
        GestureDetector(context, this)
    }

    private val rootView: View = LayoutInflater.from(context).inflate(R.layout.ponyo_float, null)
    private val iconView: ImageView = rootView.findViewById(R.id.ponyo_iv_icon)
    private val logView: TextView = rootView.findViewById(R.id.ponyo_tv_log)

    private var state: State = State.IDLE
    private var dragStartX = 0f
    private var dragStartY = 0f

    private val visibleRunnable = Runnable { toVisible() }
    private val invisibleRunnable = Runnable { toInvisible() }

    private var currMode = Mode.ICON
    private var lastSwitchModeTime = 0L
    private var switchModeAnimRunning = false
    private val autoSwitchModeRunnable = Runnable { autoSwitchMode() }

    init {
        rootView.apply {
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val radius = min(view.width, view.height).toFloat() / 2F
                    outline.setRoundRect(0, 0, view.width, view.height, radius)
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

    fun isShown() = this.rootView.isAttachedToWindow

    fun isExpand() = panelWindow.isShown()

    fun expand() {
        panelWindow.show()
    }

    fun collapse() {
        panelWindow.dismiss()
    }

    fun toggle() {
        panelWindow.toggle()
    }

    fun show() {
        attach()
    }

    fun dismiss() {
        if (panelWindow.isShown()) {
            panelWindow.onDetachListener {
                panelWindow.onDetachListener(null)
                detach()
            }
            collapse()
        } else {
            detach()
        }
    }

    private fun attach() {
        if (isShown()) return
        rootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        rootView.doOnPreDraw { runInvisible() }
        try {
            windowManager.addView(this.rootView, windowParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun detach() {
        if (!isShown()) return
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
        try {
            windowManager.removeView(this.rootView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun update() {
        if (!isShown()) return
        try {
            windowManager.updateViewLayout(this.rootView, windowParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateByXY(x: Int? = null, y: Int? = null) {
        x?.let { windowParams.x += x }
        y?.let { windowParams.y += y }
        update()
    }

    private fun updateToXY(x: Int? = null, y: Int? = null) {
        x?.let { windowParams.x = x }
        y?.let { windowParams.y = y }
        update()
    }

    override fun onDown(e: MotionEvent): Boolean {
        state = State.IDLE
        runVisible()
        return true
    }

    private fun onUp(e: MotionEvent) {
        when (state) {
            State.DRAGGING -> {
                state = State.FLING
                onDragEnd()
            }
        }
        runInvisible()
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        toggle()
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float): Boolean {
        when (state) {
            State.IDLE -> {
                state = State.DRAGGING
                onDragStart()
            }
            State.DRAGGING -> {
                onDragging(e2.rawX - e1.rawX, e2.rawY - e1.rawY)
            }
            State.FLING -> {
                state = State.DRAGGING
                onDragStart()
            }
        }
        return true
    }

    override fun onLongPress(e: MotionEvent) {
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, vX: Float, vY: Float): Boolean {
        return true
    }

    private fun onDragStart() {
        dragStartX = windowParams.x.toFloat()
        dragStartY = windowParams.y.toFloat()
        flingAnim?.cancel()
    }

    private fun onDragging(moveX: Float, moveY: Float) {
        val x = dragStartX + moveX
        val y = dragStartY + moveY
        updateToXY(x.toInt(), y.toInt())
    }

    private fun onDragEnd() {
        val startX: Float = windowParams.x.toFloat()
        val startY: Float = windowParams.y.toFloat()
        val endX = when {
            startX < boundRect.left -> boundRect.left
            startX > boundRect.right - rootView.width -> boundRect.right - rootView.width
            else -> startX
        }
        val endY = when {
            startY < boundRect.top -> boundRect.top
            startY > boundRect.bottom - rootView.height -> boundRect.bottom - rootView.height
            else -> startY
        }
        flingAnim = AnimatorSet().apply {
            playTogether(
                ValueAnimator.ofFloat(startX, endX).apply {
                    addUpdateListener {
                        val x = it.animatedValue as Float
                        updateToXY(x = x.toInt())
                    }
                },
                ValueAnimator.ofFloat(startY, endY).apply {
                    addUpdateListener {
                        val y = it.animatedValue as Float
                        updateToXY(y = y.toInt())
                    }
                }
            )
            start()
        }
    }

    override fun onGlobalLayout() {
        currRect.set(
            windowParams.x.toFloat(),
            windowParams.y.toFloat(),
            windowParams.x.toFloat() + windowParams.width.toFloat(),
            windowParams.y.toFloat() + windowParams.height.toFloat()
        )
        panelWindow.updateFloatRect(currRect)
    }

    private fun runVisible() {
        rootView.removeCallbacks(visibleRunnable)
        rootView.removeCallbacks(invisibleRunnable)
        rootView.postDelayed(visibleRunnable, 0)
    }

    private fun runInvisible() {
        rootView.removeCallbacks(visibleRunnable)
        rootView.removeCallbacks(invisibleRunnable)
        rootView.postDelayed(invisibleRunnable, 3000)
    }

    private fun toVisible() {
        rootView.animate().alpha(1F).start()
    }

    private fun toInvisible() {
        rootView.animate().alpha(0.6F).start()
    }

    private var unreadAssertCount = 0
    private var unreadErrorCount = 0

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

    private fun switchToIconIfNeeded() {
        if (unreadAssertCount == 0 && unreadErrorCount == 0) {
            rootView.removeCallbacks(autoSwitchModeRunnable)
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
        rootView.removeCallbacks(autoSwitchModeRunnable)
        rootView.postDelayed(autoSwitchModeRunnable, delay)
        switchModeAnimRunning = true
        rootView.animate()
            .setInterpolator(AccelerateInterpolator())
            .setDuration(100)
            .scaleX(0F)
            .withEndAction {
                switchToModeImmediately(false)
                rootView.animate()
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
                    logView.setBackgroundResource(R.drawable.ponyo_log_a_checked)
                }
            }
            Mode.ERROR -> {
                logView.text = formatCount(unreadErrorCount)
                if (!onlyChangeCount) {
                    logView.visibility = View.VISIBLE
                    logView.setBackgroundResource(R.drawable.ponyo_log_e_checked)
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
            }
        }

    private var flingAnim: Animator? = null

}
