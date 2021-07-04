package per.goweii.ponyo.panel

import android.content.Context
import android.view.View
import android.view.ViewGroup
import per.goweii.ponyo.dialog.FrameDialog

abstract class Panel {
    lateinit var view: View private set
    val context: Context get() = view.context
    var isCreated = false
        private set
    var isAttached = false
        private set
    var isFirstAttach = true
        private set
    var isVisible = false
        private set
    var isFirstVisible = true
        private set

    abstract fun getPanelName(): String

    abstract fun getLayoutRes(): Int

    fun createView(container: ViewGroup): View {
        throw UnsupportedOperationException()
    }

    fun destroyView() {
    }

    open fun onCreated(view: View) {}

    open fun onAttached(view: View) {}

    open fun onVisible(view: View) {}

    open fun onInvisible(view: View) {}

    open fun onDetached(view: View) {}

    open fun onDestroy(view: View) {}

    private fun dispatchCreated() {
    }

    fun dispatchAttach() {
    }

    private fun dispatchVisible() {
    }

    private fun dispatchDestroy() {
    }

    fun dispatchDetach() {
    }

    private fun dispatchInvisible() {
    }

    fun dispatchVisibleChanged(visible: Boolean) {
    }

    fun makeDialog(): FrameDialog? {
        throw UnsupportedOperationException()
    }
}