package per.goweii.ponyo

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.RectF
import android.os.Build
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnLayout
import androidx.viewpager.widget.ViewPager
import net.lucode.hackware.magicindicator.MagicIndicator
import per.goweii.ponyo.dialog.FrameDialog
import per.goweii.ponyo.panel.PanelManager
import per.goweii.ponyo.widget.FloatRootView
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

    private val currRectF: RectF = RectF()
    private val floatRectF: RectF = RectF()
    private val panelRectF: RectF = RectF()
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
    private val iconView: ImageView = rootView.findViewById(R.id.ponyo_panel_icon)
    private val panelView: LinearLayout = rootView.findViewById(R.id.ponyo_panel_content)
    private val viewPager: ViewPager = rootView.findViewById(R.id.ponyo_panel_vp)
    private val indicator: MagicIndicator = rootView.findViewById(R.id.ponyo_panel_indicator)
    private val dialogContainer: FrameLayout = rootView.findViewById(R.id.ponyo_panel_dialog)

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
                            handleBack()
                            return true
                        }
                    }
                    return false
                }
            })
        }
        panelView.apply {
            fitsSystemWindows = true
        }
        PanelManager.attachTo(viewPager, indicator, dialogContainer)
    }

    private fun handleBack() {
        if (dialogContainer.childCount == 0) {
            dismiss()
            return
        }
        val dialogView = dialogContainer.getChildAt(dialogContainer.childCount - 1)
        val dialog = dialogView.tag as FrameDialog
        dialog.dismiss()
    }

    fun onAttachListener(listener: (() -> Unit)? = null) {
        onAttachListener = listener
    }

    fun onDetachListener(listener: (() -> Unit)? = null) {
        onDetachListener = listener
    }

    fun show() {
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

    fun updateFloatRect(rectF: RectF) {
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
                rootView.left.toFloat(),
                rootView.top.toFloat(),
                rootView.right.toFloat(),
                rootView.bottom.toFloat()
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


    private val startRectF: RectF = RectF()
    private val endRectF: RectF = RectF()
    private val zoomAnimator: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0F, 1F).apply {
            interpolator = DecelerateInterpolator()
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

    private fun onZooming(f: Float, startRect: RectF, endRect: RectF) {
        val sf = when {
            endRect.width() > startRect.width() -> f.acce()
            endRect.width() < startRect.width() -> f.dece()
            else -> f
        }
        val cx = startRect.centerX() + (endRect.centerX() - startRect.centerX()) * sf
        val cy = startRect.centerY() + (endRect.centerY() - startRect.centerY()) * sf
        val w = startRect.width() + (endRect.width() - startRect.width()) * f
        val h = startRect.height() + (endRect.height() - startRect.height()) * f
        val l = cx - w / 2F
        val t = cy - h / 2F
        updateToRectF(RectF(l, t, l + w, t + h))
    }

    private fun startZooming2Panel() {
        state = State.PANEL
        startRectF.set(currRectF)
        endRectF.set(panelRectF)
        zoomAnimator.start()
    }

    private fun endZooming2Panel() {
        PanelManager.onShow()
    }

    private fun startZooming2Float() {
        state = State.FLOAT
        startRectF.set(currRectF)
        endRectF.set(floatRectF)
        zoomAnimator.start()
        PanelManager.onHide()
    }

    private fun endZooming2Float() {
        detach()
    }

    private fun updateToRectF(rectF: RectF) {
        currRectF.set(rectF)
        val f = (currRectF.width() - floatRectF.width()) / (panelRectF.width() - floatRectF.width())
        val r = (1F - f).dece() * min(currRectF.width(), currRectF.height())
        rootView.updateToRect(rectF, r)
        val minp = 0.1F
        val maxp = 0.3F
        val np = when {
            f < minp -> 0F
            f > maxp -> 1F
            else -> (f - minp) / (maxp - minp)
        }
        panelView.alpha = np.dece()
    }
}
