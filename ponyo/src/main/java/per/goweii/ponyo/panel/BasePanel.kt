package per.goweii.ponyo.panel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import per.goweii.ponyo.R

abstract class BasePanel : IPanel {

    private lateinit var view: View
    protected lateinit var context: Context

    override fun createPanelView(container: FrameLayout): View {
        context = container.context
        view = LayoutInflater.from(context).inflate(getPanelLayoutRes(), container, false)
        onPanelViewCreated(view)
        return view
    }

    abstract fun getPanelLayoutRes(): Int

    abstract fun onPanelViewCreated(view: View)
}