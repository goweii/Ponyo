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
        FLOAT, PANEL, ZOOMING_TO_FLOAT, ZOOMING_TO_PANEL
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

    fun isShown() = this.floatView.isAttachedToWindow

    fun show(rectF: RectF) {
        floatRectF.set(rectF)
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
        attach()
    }

    private fun attach() {
        floatView.visibility = View.INVISIBLE
        floatView.alpha = 0F
        try {
            windowManager.addView(floatView, windowParams)
        } catch (e: Exception) {
            Ponlog.e { e }
        }
    }

    fun dismiss(rectF: RectF) {
        floatRectF.set(rectF)
        startZooming2Float()
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
                            floatView.visibility = View.INVISIBLE
                            floatView.alpha = 0F
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
                    floatView.visibility = View.VISIBLE
                    floatView.alpha = 1F
                }
            })
            addUpdateListener {
                onZooming(it.animatedValue as RectF)
            }
        }
    }

    private fun onZooming(currRectF: RectF) {
        floatWrapper.updateToRectF(currRectF)
        val f = (currRectF.width() - floatRectF.width()) / (panelRectF.width() - floatRectF.width())
        floatBg.alpha = (f).dece()
        floatIcon.alpha = (1F - f).dece()
        floatPanel.alpha = f.dece()
    }

    private fun startZooming2Panel() {
        state = State.ZOOMING_TO_PANEL
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
        floatView.visibility = View.VISIBLE
        floatView.alpha = 1F
    }

    private fun startZooming2Float() {
        state = State.ZOOMING_TO_FLOAT
        val startRectF = floatWrapper.toRectF()
        val endRectF = RectF(floatRectF)
        zoomAnimator.setObjectValues(startRectF, endRectF)
        zoomAnimator.start()
    }

    private fun endZooming2Float() {
        state = State.FLOAT
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
