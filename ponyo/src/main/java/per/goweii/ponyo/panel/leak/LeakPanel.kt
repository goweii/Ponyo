package per.goweii.ponyo.panel.leak

import android.view.View
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class LeakPanel: BasePanel() {
    override fun getPanelLayoutRes(): Int = R.layout.ponyo_panel_leak

    override fun getPanelName(): String = "内存泄漏"

    override fun onPanelViewCreated(view: View) {
        LeakManager.attach(view)
    }

    override fun onFirstVisible() {
    }

    override fun onGone() {
    }
}