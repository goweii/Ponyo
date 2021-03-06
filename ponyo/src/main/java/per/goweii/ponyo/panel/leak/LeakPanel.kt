package per.goweii.ponyo.panel.leak

import android.view.View
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.Panel

class LeakPanel: Panel() {
    override fun getLayoutRes(): Int = R.layout.ponyo_panel_leak

    override fun getPanelName(): String = "内存泄漏"

    override fun onCreated(view: View) {
        LeakManager.attach(view)
    }
}