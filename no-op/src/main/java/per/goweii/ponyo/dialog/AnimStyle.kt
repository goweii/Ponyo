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
        throw UnsupportedOperationException()
    }

    override fun createOutAnim(targetView: View): Animator {
        throw UnsupportedOperationException()
    }
}

object ScaleAnimStyle : AnimStyle() {
    override fun createInAnim(targetView: View): Animator {
        throw UnsupportedOperationException()
    }

    override fun createOutAnim(targetView: View): Animator {
        throw UnsupportedOperationException()
    }
}

object BottomAnimStyle : AnimStyle() {
    override fun createInAnim(targetView: View): Animator {
        throw UnsupportedOperationException()
    }

    override fun createOutAnim(targetView: View): Animator {
        throw UnsupportedOperationException()
    }
}