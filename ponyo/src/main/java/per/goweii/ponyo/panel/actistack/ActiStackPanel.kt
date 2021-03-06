package per.goweii.ponyo.panel.actistack

import android.view.View
import android.widget.TextView
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.Panel

class ActiStackPanel: Panel() {
    override fun getLayoutRes(): Int = R.layout.ponyo_panel_actistack

    override fun getPanelName(): String = "活动栈"

    override fun onCreated(view: View) {
        val tv_actistack: TextView = view.findViewById(R.id.tv_actistack)
        ActiStackManager.attach(tv_actistack)
    }
}