package per.goweii.ponyo.dialog

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

sealed class AnimStyle {
    abstract fun createInAnim(targetView: View): Animator
    abstract fun createOutAnim(targetView: View): Animator

    companion object {
        const val DURATION = 300L
    }
}

object AlphaAnimStyle : AnimStyle() {
    override fun createInAnim(targetView: View): Animator {
        return ObjectAnimator.ofFloat(targetView, "alpha", 0F, 1F).apply {
            duration = DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    override fun createOutAnim(targetView: View): Animator {
        return ObjectAnimator.ofFloat(targetView, "alpha", targetView.alpha, 0F).apply {
            duration = DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }
    }
}

object ScaleAnimStyle : AnimStyle() {
    override fun createInAnim(targetView: View): Animator {
        return AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(targetView, "scaleX", 0.9F, 1F).apply {
                    duration = DURATION
                    interpolator = AccelerateDecelerateInterpolator()
                },
                ObjectAnimator.ofFloat(targetView, "scaleY", 0.9F, 1F).apply {
                    duration = DURATION
                    interpolator = AccelerateDecelerateInterpolator()
                },
                ObjectAnimator.ofFloat(targetView, "alpha", 0F, 1F).apply {
                    duration = (DURATION * 0.8F).toLong()
                    interpolator = AccelerateDecelerateInterpolator()
                }
            )
        }
    }

    override fun createOutAnim(targetView: View): Animator {
        return AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(targetView, "scaleX", targetView.scaleX, 0.9F).apply {
                    duration = DURATION
                    interpolator = AccelerateDecelerateInterpolator()
                },
                ObjectAnimator.ofFloat(targetView, "scaleY", targetView.scaleY, 0.9F).apply {
                    duration = DURATION
                    interpolator = AccelerateDecelerateInterpolator()
                },
                ObjectAnimator.ofFloat(targetView, "alpha", targetView.alpha, 0F).apply {
                    duration = (DURATION * 0.8F).toLong()
                    interpolator = AccelerateDecelerateInterpolator()
                }
            )
        }
    }
}

object BottomAnimStyle : AnimStyle() {
    override fun createInAnim(targetView: View): Animator {
        val parentView = targetView.parent as View
        return ObjectAnimator.ofFloat(
            targetView, "translationY",
            parentView.height.toFloat() - targetView.top.toFloat(), 0F
        ).apply {
            duration = DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    override fun createOutAnim(targetView: View): Animator {
        val parentView = targetView.parent as View
        return ObjectAnimator.ofFloat(
            targetView, "translationY",
            targetView.translationY, parentView.height.toFloat() - targetView.top.toFloat()
        ).apply {
            duration = DURATION
            interpolator = AccelerateDecelerateInterpolator()
        }
    }
}