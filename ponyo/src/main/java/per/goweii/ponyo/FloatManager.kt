package per.goweii.ponyo

import android.animation.Animator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import per.goweii.ponyo.log.Ponlog
import kotlin.math.min
import kotlin.math.pow

/**
 * @author CuiZhen
 * @date 2020/3/28
 */
class FloatManager(private val context: Context) {

    enum class State {
        FLOAT, DRAGGING, FLING, PANEL, ZOOMING_TO_FLOAT, ZOOMING_TO_PANEL
    }

    private val panelRectF: RectF by lazy {
        RectF(0F, 0F, floatView.measuredWidth.toFloat(), floatView.measuredHeight.toFloat())
    }
    private val floatRectF: RectF by lazy {
        val defSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            48F,
            context.resources.displayMetrics
        )
        RectF(0F, 0F, defSize, defSize)
    }
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
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            x = 0
            y = 0
        }

    private val floatView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.layout_float, null).apply {
            setOnTouchListener { _, event ->
                if (event.x >= floatWrapper.left && event.x <= floatWrapper.right &&
                    event.y >= floatWrapper.top && event.y <= floatWrapper.bottom
                ) {
                    floatWrapper.performClick()
                }
                true
            }
        }
    }
    private val floatIcon: ImageView by lazy {
        floatView.findViewById<ImageView>(R.id.iv_icon).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            alpha = 1F
        }
    }
    private val floatPanel: View by lazy {
        floatView.findViewById<View>(R.id.tv_panel).apply {
            alpha = 0F
        }
    }
    private val floatWrapper: FrameLayout by lazy {
        floatView.findViewById<FrameLayout>(R.id.fl_float).apply {
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val f = 1F - (view.layoutParams.width.toFloat() - floatRectF.width()) /
                            (panelRectF.width() - floatRectF.width())
                    val radius =
                        (min(
                            view.layoutParams.width,
                            view.layoutParams.height
                        ).toFloat() / 2F) * f
                    outline.setRoundRect(
                        0, 0, view.layoutParams.width, view.layoutParams.height,
                        radius
                    )
                }
            }
            setOnClickListener {
                onFloatIconClicked()
            }
            viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    panelRectF.set(
                        0F, 0F,
                        floatView.measuredWidth.toFloat(),
                        floatView.measuredHeight.toFloat()
                    )
                    val width = floatRectF.width()
                    val height = floatRectF.height()
                    floatRectF.left = 0F
                    floatRectF.top = context.resources.displayMetrics.heightPixels * 0.6F
                    floatRectF.right = floatRectF.left + width
                    floatRectF.bottom = floatRectF.top + height
                    updateToRectF(floatRectF)
                    return true
                }
            })
        }
    }

    private var state: State = State.FLOAT

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

    private fun onFloatIconClicked() {
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
                state = State.ZOOMING_TO_PANEL
                startZooming2Panel()
            }
            State.ZOOMING_TO_PANEL -> {
                state = State.ZOOMING_TO_FLOAT
                startZooming2Float()
            }
        }
    }

    private val evaluator = object : TypeEvaluator<RectF> {
        private val currValue = RectF()
        override fun evaluate(f: Float, sv: RectF, ev: RectF): RectF {
            val sf = when {
                ev.width() > sv.width() -> f.acce()
                ev.width() < sv.width() -> f.dece()
                else -> f
            }
            val cx = sv.centerX() + (ev.centerX() - sv.centerX()) * sf
            val cy = sv.centerY() + (ev.centerY() - sv.centerY()) * sf
            val w = sv.width() + (ev.width() - sv.width()) * f
            val h = sv.height() + (ev.height() - sv.height()) * f
            val l = cx - w / 2F
            val t = cy - h / 2F
            currValue.set(l, t, l + w, t + h)
            return currValue
        }
    }

    private fun Float.acce() = -this.pow(2F) + 2F * this

    private fun Float.dece() = 1F - (1F - this).pow(0.5F)

    private val zoomAnimator: ValueAnimator by lazy {
        ValueAnimator.ofObject(evaluator, RectF(), RectF()).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 500L
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    when (state) {
                        State.ZOOMING_TO_FLOAT -> {
                            endZooming2Float()
                        }
                        State.ZOOMING_TO_PANEL -> {
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

    private fun currFraction(): Float {
        return 1F - (floatWrapper.layoutParams.width.toFloat() - floatRectF.width()) /
                (panelRectF.width() - floatRectF.width())
    }

    private fun onZooming(currRectF: RectF) {
        floatWrapper.updateToRectF(currRectF)
        val f = currFraction().dece()
        floatIcon.alpha = f
        floatPanel.alpha = 1F - f
    }

    private fun startZooming2Panel() {
        val startRectF = floatWrapper.toRectF()
        val endRectF = RectF(panelRectF)
        if (startRectF.width() == floatRectF.width() && startRectF.height() == floatRectF.height()) {
            floatRectF.set(startRectF)
        }
        zoomAnimator.setObjectValues(startRectF, endRectF)
        zoomAnimator.start()
    }

    private fun endZooming2Panel() {
        state = State.PANEL
    }

    private fun startZooming2Float() {
        val startRectF = floatWrapper.toRectF()
        val endRectF = RectF(floatRectF)
        zoomAnimator.setObjectValues(startRectF, endRectF)
        zoomAnimator.start()
    }

    private fun endZooming2Float() {
        state = State.FLOAT
    }

    private fun View.toRectF(): RectF = (this.layoutParams as ViewGroup.MarginLayoutParams).let {
        RectF(
            it.leftMargin.toFloat(),
            it.topMargin.toFloat(),
            it.leftMargin.toFloat() + width.toFloat(),
            it.topMargin.toFloat() + height.toFloat()
        )
    }

    private fun View.updateToRectF(rectF: RectF) {
        val lp = layoutParams as ViewGroup.MarginLayoutParams
        lp.width = rectF.width().toInt()
        lp.height = rectF.height().toInt()
        lp.leftMargin = rectF.left.toInt()
        lp.topMargin = rectF.top.toInt()
        requestLayout()
    }

}
