package per.goweii.ponyo.panel

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout

interface IPanel {
    fun getPanelName(): String
    fun createPanelView(container: FrameLayout): View
    fun onVisible()
    fun onGone()
}