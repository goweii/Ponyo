package per.goweii.ponyo.panel.tm

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class TmPanel: BasePanel() {

    override fun getPanelLayoutRes(): Int = R.layout.ponyo_panel_tm

    override fun getPanelName(): String = "耗时"

    override fun onPanelViewCreated(view: View) {
        val rv_tm: RecyclerView = view.findViewById(R.id.rv_tm)
        rv_tm.layoutManager = LinearLayoutManager(context)
        val tmAdapter = TmAdapter()
        rv_tm.adapter = tmAdapter
        TmManager.attach(tmAdapter)
    }
}