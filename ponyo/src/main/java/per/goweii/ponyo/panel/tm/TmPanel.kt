package per.goweii.ponyo.panel.tm

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.Panel

class TmPanel: Panel() {

    override fun getLayoutRes(): Int = R.layout.ponyo_panel_tm

    override fun getPanelName(): String = "耗时"

    override fun onCreated(view: View) {
        val rv_tm: RecyclerView = view.findViewById(R.id.rv_tm)
        rv_tm.layoutManager = LinearLayoutManager(view.context)
        val tmAdapter = TmAdapter()
        rv_tm.adapter = tmAdapter
        TmManager.attach(tmAdapter)
    }
}