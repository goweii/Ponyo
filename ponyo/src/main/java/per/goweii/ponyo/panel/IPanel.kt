package per.goweii.ponyo.panel

import android.view.View
import android.widget.FrameLayout

interface IPanel {
    fun createPanelView(container: FrameLayout): View
}