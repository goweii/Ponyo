package per.goweii.ponyo.panel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.*

abstract class BasePanel : IPanel {

    private lateinit var view: View
    protected lateinit var context: Context

    private var visible = false
    private var hadVisible = false

    override fun createPanelView(container: FrameLayout): View {
        context = container.context
        view = LayoutInflater.from(context).inflate(getPanelLayoutRes(), container, false)
        onPanelViewCreated(view)
        return view
    }

    abstract fun getPanelLayoutRes(): Int

    abstract fun onPanelViewCreated(view: View)

    override fun dispatchVisible(visible: Boolean) {
        if (this.visible == visible) return
        this.visible = visible
        if (this.visible) {
            if (hadVisible) {
                onVisible(false)
            } else {
                hadVisible = true
                onVisible(true)
            }
        } else {
            onGone()
        }
    }

    protected open fun onVisible(firstVisible: Boolean) {}

    protected open fun onGone() {}
}