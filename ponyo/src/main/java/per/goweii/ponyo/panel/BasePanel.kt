package per.goweii.ponyo.panel

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
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

    override fun createPanelTab(container: LinearLayout): View {
        return LayoutInflater.from(container.context)
            .inflate(R.layout.tab, container, false).apply {
                this as TextView
                text = getPanelName()
            }
    }

    abstract fun getPanelLayoutRes(): Int
    abstract fun getPanelName(): String

    abstract fun onPanelViewCreated(view: View)
}