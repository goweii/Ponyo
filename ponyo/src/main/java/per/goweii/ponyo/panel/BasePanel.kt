package per.goweii.ponyo.panel

import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.PanelProvider
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.log.LogManager

abstract class BasePanel : IPanel {

    private lateinit var view: View

    override fun createPanelView(container: FrameLayout): View {
        view = LayoutInflater.from(container.context).inflate(getPanelLayoutRes(), container, false)
        onPanelViewCreated(view)
        return view
    }

    abstract fun getPanelLayoutRes(): Int

    abstract fun onPanelViewCreated(view: View)
}