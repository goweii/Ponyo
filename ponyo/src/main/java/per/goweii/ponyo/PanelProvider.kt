package per.goweii.ponyo

import android.widget.FrameLayout
import android.widget.LinearLayout
import per.goweii.ponyo.panel.IPanel
import per.goweii.ponyo.panel.log.LogPanel

object PanelProvider {

    private lateinit var container: FrameLayout
    private lateinit var tab: LinearLayout
    private val panels = arrayListOf<IPanel>().apply {
        add(LogPanel())
    }

    fun attach(container: FrameLayout, tab: LinearLayout) {
        this.container = container
        this.tab = tab
        initPanels()
    }

    fun onAttach(){
    }

    fun onShow(){
    }

    fun onHide(){
    }

    fun onDetach(){
    }

    private fun initPanels() {
        panels.forEach {
            container.addView(it.createPanelView(container))
            tab.addView(it.createPanelTab(tab))
        }
    }

}