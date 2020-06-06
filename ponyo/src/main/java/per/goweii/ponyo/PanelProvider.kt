package per.goweii.ponyo

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import per.goweii.ponyo.panel.IPanel
import per.goweii.ponyo.panel.actistack.ActiStackPanel
import per.goweii.ponyo.panel.db.DbPanel
import per.goweii.ponyo.panel.log.LogPanel
import per.goweii.ponyo.panel.sp.SpPanel
import per.goweii.ponyo.panel.tm.TmPanel

object PanelProvider {

    private lateinit var container: FrameLayout
    private lateinit var tab: LinearLayout
    private val panels = arrayListOf<IPanel>().apply {
        add(LogPanel())
        add(DbPanel())
        add(TmPanel())
        add(ActiStackPanel())
        add(SpPanel())
    }

    fun attach(container: FrameLayout, tab: LinearLayout) {
        this.container = container
        this.tab = tab
        initPanels()
        if (PanelProvider.container.childCount > 0) {
            selectPanel(PanelProvider.container.getChildAt(0))
        }
    }

    fun onAttach() {
    }

    fun onShow() {
    }

    fun onHide() {
    }

    fun onDetach() {
    }

    private fun initPanels() {
        panels.forEach {
            val panelView = it.createPanelView(container)
            val tabView = it.createPanelTab(tab)
            panelView.visibility = View.GONE
            tabView.isSelected = false
            tabView.setOnClickListener {
                selectPanel(panelView)
            }
            container.addView(panelView)
            tab.addView(tabView)
        }
    }

    private fun selectPanel(panel: View) {
        for (i in 0 until container.childCount) {
            val panelView = container.getChildAt(i)
            val tabView = tab.getChildAt(i)
            if (panelView == panel) {
                panelView.visibility = View.VISIBLE
                tabView.isSelected = true
            } else {
                panelView.visibility = View.GONE
                tabView.isSelected = false
            }
        }
    }

}