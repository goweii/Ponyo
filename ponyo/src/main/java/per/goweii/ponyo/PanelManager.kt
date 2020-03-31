package per.goweii.ponyo

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.min
import kotlin.math.pow

/**
 * @author CuiZhen
 * @date 2020/3/28
 */
internal class PanelManager(private val context: Context) {

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
            val rv_log = findViewById<RecyclerView>(R.id.rv_log)
            LogManager.attachTo(rv_log)
            addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View?) {
                    onAttachListener?.invoke()
                }

                override fun onViewDetachedFromWindow(v: View?) {
                    onDetachListener?.invoke()
                }
            })
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
    private var onAttachListener: (() -> Unit)? = null
    private var onDetachListener: (() -> Unit)? = null

    private var state: State = State.FLOAT

    fun icon(resId: Int) = apply {
        floatIcon.setImageResource(resId)
    }

    private fun attach() {
        if (isShown()) return
        floatView.visibility = View.INVISIBLE
        floatView.alpha = 0F
        floatIcon.visibility = View.INVISIBLE
        floatIcon.alpha = 0F
        floatPanel.visibility = View.INVISIBLE
        floatPanel.alpha = 0F
        floatView.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                floatView.viewTreeObserver.removeOnPreDrawListener(this)
                panelRectF.set(
                    0F, 0F,
                    floatView.width.toFloat(),
                    floatView.height.toFloat()
                )
                updateToRectF(floatRectF)
                floatIcon.visibility = View.INVISIBLE
                floatIcon.alpha = 0F
                floatPanel.visibility = View.INVISIBLE
                floatPanel.alpha = 0F
                startZooming2Panel()
                return true
            }
        })
        try {
            windowManager.addView(floatView, windowParams)
        } catch (e: Exception) {
        }
    }

    private fun detach() {
        if (!isShown()) return
        floatView.visibility = View.INVISIBLE
        floatView.alpha = 0F
        try {
            windowManager.removeView(floatView)
        } catch (e: Exception) {
        }
    }

    fun onAttachListener(listener: () -> Unit) {
        onAttachListener = listener
    }

    fun onDetachListener(listener: () -> Unit) {
        onDetachListener = listener
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

    fun isShown(): Boolean {
        return floatView.isAttachedToWindow
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
            interpolator = AccelerateDecelerateInterpolator()
            duration = 400L
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
                    floatIcon.visibility = View.VISIBLE
                    floatPanel.visibility = View.VISIBLE
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
        updateToRectF(currValue)
        val p = (currValue.width() - floatRectF.width()) / (panelRectF.width() - floatRectF.width())
        floatBg.alpha = p.acce()
        val minp = 0.2F
        val maxp = 0.5F
        val np = when {
            p < minp -> 0F
            p > maxp -> 1F
            else -> (p - minp) / (maxp - minp)
        }
        floatIcon.alpha = (1F - np).dece()
        floatPanel.alpha = np.dece()
    }

    private fun startZooming2Panel() {
        floatIcon.visibility = View.VISIBLE
        floatIcon.alpha = 1F
        floatPanel.visibility = View.INVISIBLE
        floatPanel.alpha = 0F
        state = State.PANEL
        startRectF.set(toRectF())
        endRectF.set(panelRectF)
        zoomAnimator.start()
    }

    private fun endZooming2Panel() {
        floatView.visibility = View.VISIBLE
        floatView.alpha = 1F
        floatIcon.visibility = View.INVISIBLE
        floatIcon.alpha = 0F
        floatPanel.visibility = View.VISIBLE
        floatPanel.alpha = 1F
    }

    private fun startZooming2Float() {
        floatIcon.visibility = View.INVISIBLE
        floatIcon.alpha = 0F
        floatPanel.visibility = View.VISIBLE
        floatPanel.alpha = 1F
        state = State.FLOAT
        startRectF.set(toRectF())
        endRectF.set(floatRectF)
        zoomAnimator.start()
    }

    private fun endZooming2Float() {
        floatView.visibility = View.INVISIBLE
        floatView.alpha = 0F
        floatIcon.visibility = View.VISIBLE
        floatIcon.alpha = 1F
        floatPanel.visibility = View.INVISIBLE
        floatPanel.alpha = 0F
        detach()
    }

    private fun toRectF(): RectF = RectF(
        floatWrapper.left.toFloat(),
        floatWrapper.top.toFloat(),
        floatWrapper.right.toFloat(),
        floatWrapper.bottom.toFloat()
    )

    private fun updateToRectF(rectF: RectF) {
        floatWrapper.layout(
            rectF.left.toInt(),
            rectF.top.toInt(),
            rectF.right.toInt(),
            rectF.bottom.toInt()
        )
        floatPanel.scrollX = rectF.left.toInt()
        floatPanel.scrollY = rectF.top.toInt()
        val sx = rectF.width() / panelRectF.width()
        val sy = rectF.height() / panelRectF.height()
        val s = min(sx, sy)
        floatIcon.scaleX = s
        floatIcon.scaleY = s
    }
}
