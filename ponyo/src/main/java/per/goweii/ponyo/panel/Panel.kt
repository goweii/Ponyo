package per.goweii.ponyo.panel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import per.goweii.ponyo.dialog.FrameDialog

@Suppress("MemberVisibilityCanBePrivate")
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
        view = LayoutInflater.from(container.context).inflate(getLayoutRes(), container, false)
        dispatchCreated()
        return view
    }

    fun destroyView() {
        dispatchDestroy()
    }

    open fun onCreated(view: View) {}

    open fun onAttached(view: View) {}

    open fun onVisible(view: View) {}

    open fun onInvisible(view: View) {}

    open fun onDetached(view: View) {}

    open fun onDestroy(view: View) {}

    private fun dispatchCreated() {
        if (isCreated) return
        isCreated = true
        onCreated(view)
        if (!isAttached) return
        onAttached(view)
        isFirstAttach = false
        if (!isVisible) return
        onVisible(view)
        isFirstVisible = false
    }

    fun dispatchAttach() {
        if (isAttached) return
        isAttached = true
        if (!isCreated) return
        onAttached(view)
        isFirstAttach = false
        if (!isVisible) return
        onVisible(view)
        isFirstVisible = false
    }

    private fun dispatchVisible() {
        if (isVisible) return
        if (!isCreated) return
        if (!isAttached) return
        isVisible = true
        onVisible(view)
        isFirstVisible = false
    }

    private fun dispatchDestroy() {
        if (!isCreated) return
        if (isAttached) {
            if (isVisible) {
                isVisible = false
                onInvisible(view)
            }
            isAttached = false
            onDetached(view)
        }
        isCreated = false
        onDestroy(view)
    }

    fun dispatchDetach() {
        if (!isCreated) return
        if (!isAttached) return
        if (isVisible) {
            isVisible = false
            onInvisible(view)
        }
        isAttached = false
        onDetached(view)
    }

    private fun dispatchInvisible() {
        if (!isCreated) return
        if (!isAttached) return
        if (!isVisible) return
        isVisible = false
        onInvisible(view)
    }

    fun dispatchVisibleChanged(visible: Boolean) {
        if (visible) {
            dispatchVisible()
        } else {
            dispatchInvisible()
        }
    }

    fun makeDialog(): FrameDialog? {
        return PanelManager.makeDialog()
    }
}