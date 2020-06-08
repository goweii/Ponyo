package per.goweii.ponyo.panel.actistack

import android.view.View
import android.widget.TextView
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class ActiStackPanel: BasePanel() {
    override fun getPanelLayoutRes(): Int = R.layout.panel_actistack

    override fun getPanelName(): String = "活动栈"

    override fun onPanelViewCreated(view: View) {
        val tv_actistack: TextView = view.findViewById(R.id.tv_actistack)
        ActiStackManager.attach(tv_actistack)
    }

    override fun onFirstVisible() {
    }

    override fun onGone() {
    }
}