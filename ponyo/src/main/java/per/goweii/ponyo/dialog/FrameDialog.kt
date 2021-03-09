package per.goweii.ponyo.dialog

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnPreDraw
import per.goweii.ponyo.R
import per.goweii.ponyo.utils.statusBarHeight

class FrameDialog(
    private val parent: FrameLayout
) {
    private val child: View = LayoutInflater.from(parent.context)
        .inflate(R.layout.ponyo_frame_dialog, parent, false)
    private val backgroundView: ImageView
    private val contentParent: FrameLayout
    private lateinit var contentView: View

    private var animStyle: AnimStyle = AnimStyle.Scale

    private var dataBinder: (FrameDialog.() -> Unit)? = null

    val isShown: Boolean get() = child.parent == parent

    init {
        child.tag = this
        backgroundView = child.findViewById(R.id.ponyo_frame_dialog_iv_background)
        contentParent = child.findViewById(R.id.ponyo_frame_dialog_fl_content)
        backgroundView.setOnClickListener { dismiss() }
        contentParent.setPadding(0, contentParent.context.statusBarHeight, 0, 0)
    }

    fun content(contentView: View) = apply {
        this.contentView = contentView
    }

    fun content(@LayoutRes contentViewRes: Int) = apply {
        this.contentView = LayoutInflater.from(contentParent.context)
            .inflate(contentViewRes, contentParent, false)
    }

    fun animStyle(animStyle: AnimStyle) = apply {
        this.animStyle = animStyle
    }

    fun show() {
        if (isShown) return
        dataBinder?.invoke(this)
        contentView.isFocusable = true
        contentView.isClickable = true
        val contentParams = contentView.layoutParams as FrameLayout.LayoutParams
        contentParams.gravity = Gravity.CENTER
        contentParent.addView(contentView)
        parent.addView(child)
        child.doOnPreDraw {
            doAnimIn().apply {
                start()
            }
        }
    }

    fun dismiss() {
        if (!isShown) return
        doAnimOut().apply {
            doOnEnd { parent.removeView(child) }
            start()
        }
    }

    fun bindData(dataBinder: FrameDialog.() -> Unit) = apply {
        this.dataBinder = dataBinder
    }

    fun <V : View> getView(@IdRes id: Int): V {
        return contentView.findViewById(id)
    }

    private fun doAnimIn(): Animator {
        val backgroundAnim =
            ObjectAnimator.ofFloat(
                backgroundView,
                "alpha",
                0F, 1F
            ).apply {
                duration = ANIM_DURATION
                interpolator = AccelerateDecelerateInterpolator()
            }
        val contentAnim = when (animStyle) {
            AnimStyle.Scale ->
                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(
                            contentView,
                            "scaleX",
                            0.9F, 1F
                        ).apply {
                            duration = ANIM_DURATION
                            interpolator = AccelerateDecelerateInterpolator()
                        },
                        ObjectAnimator.ofFloat(
                            contentView,
                            "scaleY",
                            0.9F, 1F
                        ).apply {
                            duration = ANIM_DURATION
                            interpolator = AccelerateDecelerateInterpolator()
                        },
                        ObjectAnimator.ofFloat(
                            contentView,
                            "alpha",
                            0F,
                            1F
                        ).apply {
                            duration = ANIM_DURATION
                            interpolator = AccelerateDecelerateInterpolator()
                        }
                    )
                }
            AnimStyle.Bottom ->
                ObjectAnimator.ofFloat(
                    contentView,
                    "translationY",
                    contentParent.height.toFloat() - contentView.top.toFloat(), 0F
                ).apply {
                    duration = ANIM_DURATION
                    interpolator = AccelerateDecelerateInterpolator()
                }
        }
        return AnimatorSet().apply {
            playTogether(backgroundAnim, contentAnim)
        }
    }

    private fun doAnimOut(): Animator {
        val backgroundAnim =
            ObjectAnimator.ofFloat(
                backgroundView, "alpha",
                backgroundView.alpha, 0F
            ).apply {
                duration = ANIM_DURATION
                interpolator = AccelerateDecelerateInterpolator()
            }
        val contentAnim = when (animStyle) {
            AnimStyle.Scale ->
                AnimatorSet().apply {
                    playTogether(
                        ObjectAnimator.ofFloat(
                            contentView,
                            "scaleX",
                            contentView.scaleX, 0.9F
                        ).apply {
                            duration = ANIM_DURATION
                            interpolator = AccelerateDecelerateInterpolator()
                        },
                        ObjectAnimator.ofFloat(
                            contentView,
                            "scaleY",
                            contentView.scaleY, 0.9F
                        ).apply {
                            duration = ANIM_DURATION
                            interpolator = AccelerateDecelerateInterpolator()
                        },
                        ObjectAnimator.ofFloat(
                            contentView,
                            "alpha",
                            contentView.alpha, 0F
                        ).apply {
                            duration = ANIM_DURATION
                            interpolator = AccelerateDecelerateInterpolator()
                        }
                    )
                }
            AnimStyle.Bottom ->
                ObjectAnimator.ofFloat(
                    contentView,
                    "translationY",
                    contentView.translationY,
                    contentParent.height.toFloat() - contentView.top.toFloat()
                ).apply {
                    duration = ANIM_DURATION
                    interpolator = AccelerateDecelerateInterpolator()
                }
        }
        return AnimatorSet().apply {
            playTogether(backgroundAnim, contentAnim)
        }
    }

    companion object {
        private const val ANIM_DURATION = 300L

        fun with(parent: FrameLayout): FrameDialog {
            return FrameDialog(parent)
        }
    }

    enum class AnimStyle {
        Scale,
        Bottom
    }

}