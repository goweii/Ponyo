package per.goweii.ponyo

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.graphics.PixelFormat
import android.graphics.RectF
import android.os.Build
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnLayout
import androidx.viewpager.widget.ViewPager
import net.lucode.hackware.magicindicator.MagicIndicator
import per.goweii.ponyo.panel.PanelManager
import per.goweii.ponyo.widget.FloatRootView
import per.goweii.ponyo.widget.IconView
import kotlin.math.min
import kotlin.math.pow

/**
 * @author CuiZhen
 * @date 2020/3/28
 */
@SuppressLint("InflateParams")
internal class PanelWindow(context: Context) {
    enum class State {
        FLOAT, PANEL
    }

    private val panelRectF: RectF = RectF()
    private val floatRectF: RectF = RectF()
    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    @SuppressLint("RtlHardcoded")
    private val windowParams: WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
        windowAnimations = 0
        rotationAnimation = 0
        type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        format = PixelFormat.TRANSPARENT
        gravity = Gravity.TOP or Gravity.LEFT
        flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
        x = 0
        y = 0
    }
    private val rootView: FloatRootView = LayoutInflater.from(context)
        .inflate(R.layout.ponyo_panel, null) as FloatRootView
    private val wrapperView: FrameLayout = rootView.findViewById(R.id.wrapper)
    private val iconView: IconView = rootView.findViewById(R.id.icon)
    private val panelView: FrameLayout = rootView.findViewById(R.id.panel)
    private val viewPager: ViewPager = rootView.findViewById(R.id.vp_panel)
    private val indicator: MagicIndicator = rootView.findViewById(R.id.indicator)
    private val dialogContainer: FrameLayout = rootView.findViewById(R.id.dialog)

    private var onAttachListener: (() -> Unit)? = null
    private var onDetachListener: (() -> Unit)? = null

    private var state: State = State.FLOAT

    init {
        rootView.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            systemUiVisibility = systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View?) {
                    onAttachListener?.invoke()
                    PanelManager.onAttach()
                }

                override fun onViewDetachedFromWindow(v: View?) {
                    onDetachListener?.invoke()
                    PanelManager.onDetach()
                }
            })
            setCallback(object : FloatRootView.Callback {
                override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                    if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                        if (event.action == KeyEvent.ACTION_UP) {
                            dismiss()
                            return true
                        }
                    }
                    return false
                }
            })
        }
        wrapperView.apply {
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val f = 1F - (view.scaledWidth - floatRectF.width()) /
                            (panelRectF.width() - floatRectF.width())
                    val radius = (min(view.width, view.height).toFloat() / 2F) * f
                    when {
                        width < height -> {
                            val h = height - (height - width) * f
                            outline.setRoundRect(0, 0, width, h.toInt(), radius)
                        }
                        width > height -> {
                            val w = width - (width - height) * f
                            outline.setRoundRect(0, 0, w.toInt(), height, radius)
                        }
                        else -> {
                            outline.setRoundRect(0, 0, width, height, radius)
                        }
                    }
                }
            }
        }
        panelView.apply {
            fitsSystemWindows = true
        }
        PanelManager.attachTo(viewPager, indicator, dialogContainer)
    }

    fun onAttachListener(listener: (() -> Unit)? = null) {
        onAttachListener = listener
    }

    fun onDetachListener(listener: (() -> Unit)? = null) {
        onDetachListener = listener
    }

    fun show(rectF: RectF?) {
        rectF?.let { floatRectF.set(it) }
        if (!rootView.isAttachedToWindow) {
            attach()
        } else {
            if (zoomAnimator.isRunning) {
                when (state) {
                    State.FLOAT -> {
                        state = State.PANEL
                        zoomAnimator.reverse()
                    }
                    State.PANEL -> {
                    }
                }
            } else {
                attach()
            }
        }
    }

    fun isShown(): Boolean {
        return rootView.isAttachedToWindow
    }

    fun dismiss() {
        if (rootView.isAttachedToWindow) {
            if (zoomAnimator.isRunning) {
                when (state) {
                    State.FLOAT -> {
                    }
                    State.PANEL -> {
                        state = State.FLOAT
                        zoomAnimator.reverse()
                    }
                }
            } else {
                startZooming2Float()
            }
        }
    }

    fun toggle() {
        if (!rootView.isAttachedToWindow) {
            attach()
        } else {
            if (zoomAnimator.isRunning) {
                state = when (state) {
                    State.FLOAT -> {
                        State.PANEL
                    }
                    State.PANEL -> {
                        State.FLOAT
                    }
                }
                zoomAnimator.reverse()
            } else {
                startZooming2Float()
            }
        }
    }

    fun update(rectF: RectF) {
        floatRectF.set(rectF)
        if (rootView.isAttachedToWindow) {
            if (zoomAnimator.isRunning) {
                when (state) {
                    State.FLOAT -> {
                        endRectF.set(floatRectF)
                    }
                    State.PANEL -> {
                        startRectF.set(floatRectF)
                    }
                }
            }
        }
    }

    private fun attach() {
        if (isShown()) return
        rootView.doOnLayout {
            panelRectF.set(
                0F, 0F,
                rootView.width.toFloat(),
                rootView.height.toFloat()
            )
            updateToRectF(floatRectF)
            startZooming2Panel()
        }
        try {
            windowManager.addView(rootView, windowParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun detach() {
        if (!isShown()) return
        try {
            windowManager.removeView(rootView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun Float.acce() = -this.pow(2F) + 2F * this

    private fun Float.dece() = 1F - (1F - this).pow(0.5F)

    private val zoomAnimator: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0F, 1F).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 400L
            doOnEnd {
                when (state) {
                    State.FLOAT -> {
                        endZooming2Float()
                    }
                    State.PANEL -> {
                        endZooming2Panel()
                    }
                }
            }
            addUpdateListener {
                val f = it.animatedValue as Float
                onZooming(f, startRectF, endRectF)
            }
        }
    }

    private val startRectF: RectF = RectF()
    private val endRectF: RectF = RectF()

    private fun onZooming(f: Float, sv: RectF, ev: RectF) {
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
        val currValue = RectF(l, t, l + w, t + h)
        updateToRectF(currValue)
        val p = (currValue.width() - floatRectF.width()) / (panelRectF.width() - floatRectF.width())
        val minp = 0.2F
        val maxp = 0.5F
        val np = when {
            p < minp -> 0F
            p > maxp -> 1F
            else -> (p - minp) / (maxp - minp)
        }
        panelView.alpha = np.dece()
        iconView.alpha = (1F - np).dece()
    }

    private fun startZooming2Panel() {
        state = State.PANEL
        startRectF.set(toRectF())
        endRectF.set(panelRectF)
        zoomAnimator.start()
    }

    private fun endZooming2Panel() {
        PanelManager.onShow()
    }

    private fun startZooming2Float() {
        state = State.FLOAT
        startRectF.set(toRectF())
        endRectF.set(floatRectF)
        zoomAnimator.start()
        PanelManager.onHide()
    }

    private fun endZooming2Float() {
        detach()
    }

    private fun toRectF(): RectF = RectF(
        wrapperView.left.toFloat(),
        wrapperView.top.toFloat(),
        wrapperView.left.toFloat() + wrapperView.scaledWidth,
        wrapperView.top.toFloat() + wrapperView.scaledHeight
    )

    private val View.scaledWidth: Float get() = scaleX * width
    private val View.scaledHeight: Float get() = scaleY * height

    private fun updateToRectF(rectF: RectF) {
        val sx = rectF.width() / panelRectF.width()
        val sy = rectF.height() / panelRectF.height()
        wrapperView.pivotX = 0F
        wrapperView.pivotY = 0F
        wrapperView.scaleX = sx
        wrapperView.scaleY = sx
        wrapperView.left = rectF.left.toInt()
        wrapperView.top = rectF.top.toInt()
        wrapperView.right = wrapperView.left + panelRectF.width().toInt()
        wrapperView.bottom = wrapperView.top + panelRectF.height().toInt()
        wrapperView.invalidateOutline()
    }
}
