package per.goweii.ponyo

import android.animation.Animator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import per.goweii.ponyo.log.Ponlog
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * @author CuiZhen
 * @date 2020/3/28
 */
class PanelManager(private val context: Context) {

    enum class State {
        FLOAT, PANEL
    }

    private val panelRectF: RectF by lazy {
        RectF(0F, 0F, 0F, 0F)
    }
    private val floatRectF: RectF by lazy {
        RectF(0F, 0F, 0F, 0F)
    }
    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    @SuppressLint("RtlHardcoded")
    private val windowParams: WindowManager.LayoutParams =
        WindowManager.LayoutParams().apply {
            windowAnimations = 0
            rotationAnimation = 0
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY - 1
            } else {
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT - 1
            }
            format = PixelFormat.RGBA_8888
            gravity = Gravity.TOP or Gravity.LEFT
            flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            x = 0
            y = 0
        }

    private val floatView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.layout_float, null).apply {
        }
    }
    private val floatWrapper: View by lazy {
        floatView.findViewById<View>(R.id.wrapper).apply {
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val f = 1F - (view.width.toFloat() - floatRectF.width()) /
                            (panelRectF.width() - floatRectF.width())
                    val radius = (min(view.width, view.height).toFloat() / 2F) * f
                    outline.setRoundRect(0, 0, view.width, view.height, radius)
                }
            }
        }
    }
    private val floatBg: View by lazy {
        floatView.findViewById<View>(R.id.bg).apply {
            alpha = 0F
        }
    }
    private val floatIcon: ImageView by lazy {
        floatView.findViewById<ImageView>(R.id.icon).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }
    private val floatPanel: View by lazy {
        floatView.findViewById<View>(R.id.panel).apply {
        }
    }

    private var state: State = State.FLOAT

    fun icon(resId: Int) = apply {
        floatIcon.setImageResource(resId)
    }

    private fun attach() {
        floatView.visibility = View.INVISIBLE
        floatView.alpha = 0F
        floatView.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                floatView.viewTreeObserver.removeOnPreDrawListener(this)
                panelRectF.set(
                    0F, 0F,
                    floatView.width.toFloat(),
                    floatView.height.toFloat()
                )
                floatWrapper.updateToRectF(floatRectF)
                floatPanel.alpha = 0F
                floatIcon.alpha = 1F
                startZooming2Panel()
                return true
            }
        })
        try {
            windowManager.addView(floatView, windowParams)
        } catch (e: Exception) {
            Ponlog.e { e }
        }
    }

    private fun detach() {
        floatView.visibility = View.INVISIBLE
        floatView.alpha = 0F
        try {
            windowManager.removeView(floatView)
        } catch (e: Exception) {
            Ponlog.e { e }
        }
    }

    fun show(rectF: RectF) {
        floatRectF.set(rectF)
        if (!floatView.isAttachedToWindow) {
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

    fun dismiss(rectF: RectF) {
        floatRectF.set(rectF)
        if (floatView.isAttachedToWindow) {
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

    fun toggle(rectF: RectF) {
        floatRectF.set(rectF)
        if (!floatView.isAttachedToWindow) {
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
        if (floatView.isAttachedToWindow) {
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

    private fun Float.acce() = -this.pow(2F) + 2F * this

    private fun Float.dece() = 1F - (1F - this).pow(0.5F)

    private val zoomAnimator: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0F, 1F).apply {
            interpolator = DecelerateInterpolator()
            duration = 350L
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    when (state) {
                        State.FLOAT -> {
                            endZooming2Float()
                        }
                        State.PANEL -> {
                            endZooming2Panel()
                        }
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationStart(animation: Animator) {
                    floatView.visibility = View.VISIBLE
                    floatView.alpha = 1F
                }
            })
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
        floatWrapper.updateToRectF(currValue)
        val p = (currValue.width() - floatRectF.width()) / (panelRectF.width() - floatRectF.width())
        floatBg.alpha = p.acce()
        floatIcon.alpha = (1F - p).dece()
        floatPanel.alpha = p.dece()
    }

    private fun startZooming2Panel() {
        state = State.PANEL
        startRectF.set(floatWrapper.toRectF())
        endRectF.set(panelRectF)
        zoomAnimator.start()
    }

    private fun endZooming2Panel() {
        floatView.visibility = View.VISIBLE
        floatView.alpha = 1F
    }

    private fun startZooming2Float() {
        state = State.FLOAT
        startRectF.set(floatWrapper.toRectF())
        endRectF.set(floatRectF)
        zoomAnimator.start()
    }

    private fun endZooming2Float() {
        floatView.visibility = View.INVISIBLE
        floatView.alpha = 0F
        detach()
    }

    private fun View.toRectF(): RectF = let {
        RectF(
            it.left.toFloat(),
            it.top.toFloat(),
            it.right.toFloat(),
            it.bottom.toFloat()
        )
    }

    private fun View.updateToRectF(rectF: RectF) {
        layout(
            rectF.left.toInt(),
            rectF.top.toInt(),
            rectF.right.toInt(),
            rectF.bottom.toInt()
        )
        val sx = rectF.width() / panelRectF.width()
        val sy = rectF.height() / panelRectF.height()
        val s = min(sx, sy)
        floatIcon.scaleX = s
        floatIcon.scaleY = s
    }
}
