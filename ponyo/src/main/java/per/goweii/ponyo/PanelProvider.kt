package per.goweii.ponyo

import android.widget.FrameLayout
import per.goweii.ponyo.panel.LogPanel

object PanelProvider {

    private lateinit var panelContainer: FrameLayout
    private val logPanel by lazy { LogPanel() }

    fun attach(container: FrameLayout) {
        panelContainer = container
        initPanels()
    }

    private fun initPanels() {
        panelContainer.addView(
            logPanel.createPanelView(panelContainer),
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

}