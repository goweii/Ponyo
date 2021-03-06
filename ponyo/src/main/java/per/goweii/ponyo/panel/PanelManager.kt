package per.goweii.ponyo.panel

import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import per.goweii.ponyo.R
import per.goweii.ponyo.dialog.FrameDialog
import per.goweii.ponyo.panel.actistack.ActiStackPanel
import per.goweii.ponyo.panel.db.DbPanel
import per.goweii.ponyo.panel.device.DevicePanel
import per.goweii.ponyo.panel.file.FilePanel
import per.goweii.ponyo.panel.leak.LeakPanel
import per.goweii.ponyo.panel.log.LogPanel
import per.goweii.ponyo.panel.sp.SpPanel
import per.goweii.ponyo.panel.tm.TmPanel

internal object PanelManager {
    private var dialogContainer: FrameLayout? = null
    private var viewPager: ViewPager? = null

    private val panels = arrayListOf<Panel>()

    init {
        addPanel(LogPanel())
        addPanel(TmPanel())
        addPanel(ActiStackPanel())
        addPanel(LeakPanel())
        addPanel(DbPanel())
        addPanel(SpPanel())
        addPanel(FilePanel())
        addPanel(DevicePanel())
    }

    fun addPanel(panel: Panel) {
        panels.add(panel)
    }

    fun attachTo(pager: ViewPager, indicator: MagicIndicator, dialog: FrameLayout) {
        this.dialogContainer = dialog
        this.viewPager = pager
        pager.offscreenPageLimit = panels.size
        val pagerAdapter = PanelPagerAdapter(panels)
        pager.adapter = pagerAdapter
        val commonNavigator = CommonNavigator(pager.context)
        commonNavigator.leftPadding =
            indicator.context.resources.getDimension(R.dimen.ponyo_margin_def).toInt()
        commonNavigator.rightPadding =
            indicator.context.resources.getDimension(R.dimen.ponyo_margin_def).toInt()
        commonNavigator.adapter = PanelNavigatorAdapter(pager, panels)
        indicator.navigator = commonNavigator
        ViewPagerHelper.bind(indicator, pager)
    }

    fun onAttach() {
        panels.forEach { it.dispatchAttach() }
    }

    fun onShow() {
    }

    fun onHide() {
    }

    fun onDetach() {
        panels.forEach { it.dispatchDetach() }
    }

    fun makeDialog(): FrameDialog? {
        val dc = dialogContainer ?: return null
        return FrameDialog.with(dc)
    }
}