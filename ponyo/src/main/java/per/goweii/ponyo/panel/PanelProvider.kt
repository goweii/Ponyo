package per.goweii.ponyo.panel

import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.panel.actistack.ActiStackPanel
import per.goweii.ponyo.panel.db.DbPanel
import per.goweii.ponyo.panel.file.FilePanel
import per.goweii.ponyo.panel.log.LogPanel
import per.goweii.ponyo.panel.sp.SpPanel
import per.goweii.ponyo.panel.tm.TmPanel

object PanelProvider {

    private lateinit var container: FrameLayout
    private lateinit var tab: RecyclerView
    private val tabAdapter by lazy {
        PanelTabAdapter { selectPanel(it) }
    }
    private val panels = arrayListOf<IPanel>().apply {
        add(LogPanel())
        add(TmPanel())
        add(ActiStackPanel())
        add(DbPanel())
        add(SpPanel())
        add(FilePanel())
    }

    fun attach(container: FrameLayout, tab: RecyclerView) {
        this.container = container
        this.tab = tab
        this.tab.layoutManager = LinearLayoutManager(tab.context, LinearLayoutManager.HORIZONTAL, false)
        this.tab.adapter = tabAdapter
        tabAdapter.set(panels)
        panels.forEach {
            val panelView = it.createPanelView(container)
            panelView.visibility = View.GONE
            container.addView(panelView)
        }
        if (this.container.childCount > 0) {
            selectPanel(0)
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

    private fun selectPanel(index: Int) {
        tabAdapter.select(index)
        for (i in 0 until container.childCount) {
            val panel = panels[i]
            val panelView = container.getChildAt(i)
            if (i == index) {
                panel.onVisible()
                panelView.visibility = View.VISIBLE
            } else {
                panel.onGone()
                panelView.visibility = View.GONE
            }
        }
    }

}