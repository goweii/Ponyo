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
    var isFirstVisible = true
        private set

    abstract fun getPanelName(): String

    abstract fun getLayoutRes(): Int

    fun createView(container: ViewGroup): View {
        view = LayoutInflater.from(container.context).inflate(getLayoutRes(), container, false)
        isCreated = true
        return view
    }

    fun dispatchDestroyView() {
        dispatchInvisible()
        isCreated = false
        onDestroy(view)
    }

    open fun onCreated(view: View) {}

    open fun onVisible(view: View) {}

    open fun onInvisible(view: View) {}

    open fun onDestroy(view: View) {}

    fun dispatchAttach() {
        isAttached = true
        dispatchVisible()
    }

    fun dispatchViewCreated() {
        onCreated(view)
        dispatchVisible()
    }

    fun dispatchDetach() {
        dispatchInvisible()
        isAttached = false
    }

    private fun dispatchVisible() {
        if (isCreated && isAttached) {
            onVisible(view)
            if (isFirstVisible) {
                isFirstVisible = false
            }
        }
    }

    private fun dispatchInvisible() {
        if (isCreated && isAttached) {
            onInvisible(view)
        }
    }

    fun makeDialog(): FrameDialog? {
        return PanelManager.makeDialog()
    }
}