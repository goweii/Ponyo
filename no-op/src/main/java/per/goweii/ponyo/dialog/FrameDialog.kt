package per.goweii.ponyo.dialog

import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

class FrameDialog(
    private val parent: FrameLayout
) {
    val isShown: Boolean get() = false

    init {
    }

    fun content(contentView: View) = apply {
    }

    fun content(@LayoutRes contentViewRes: Int) = apply {
    }

    fun animStyle(animStyle: AnimStyle) = apply {
    }

    fun show() {
    }

    fun dismiss() {
    }

    fun bindData(dataBinder: FrameDialog.() -> Unit) = apply {
    }

    fun <V : View> getView(@IdRes id: Int): V {
        throw UnsupportedOperationException()
    }

    companion object {
        fun with(parent: FrameLayout): FrameDialog {
            return FrameDialog(parent)
        }
    }

}