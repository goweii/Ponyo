package per.goweii.ponyo.panel

import android.annotation.SuppressLint
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
import per.goweii.ponyo.panel.net.NetPanel
import per.goweii.ponyo.panel.leak.LeakPanel
import per.goweii.ponyo.panel.log.LogPanel
import per.goweii.ponyo.panel.shell.ShellPanel
import per.goweii.ponyo.panel.sp.SpPanel
import per.goweii.ponyo.panel.tm.TmPanel

@SuppressLint("StaticFieldLeak")
internal object PanelManager {
    private var dialogContainer: FrameLayout? = null
    private var viewPager: ViewPager? = null

    private var pagerAdapter: PanelPagerAdapter? = null
    private var navigatorAdapter: PanelNavigatorAdapter? = null

    private val panels = arrayListOf<Panel>()

    init {
        panels.add(LogPanel())
        panels.add(NetPanel())
        panels.add(TmPanel())
        panels.add(ActiStackPanel())
        panels.add(LeakPanel())
        panels.add(DbPanel())
        panels.add(SpPanel())
        panels.add(FilePanel())
        panels.add(DevicePanel())
        panels.add(ShellPanel())
    }

    fun addPanel(panel: Panel) {
        panels.add(panel)
        pagerAdapter?.setPanels(panels)
        navigatorAdapter?.setPanels(panels)
    }

    fun attachTo(pager: ViewPager, indicator: MagicIndicator, dialog: FrameLayout) {
        this.dialogContainer = dialog
        this.viewPager = pager
        pager.offscreenPageLimit = panels.size
        pagerAdapter = PanelPagerAdapter()
        pager.adapter = pagerAdapter
        val commonNavigator = CommonNavigator(pager.context)
        commonNavigator.leftPadding =
            indicator.context.resources.getDimension(R.dimen.ponyo_margin_def).toInt()
        commonNavigator.rightPadding =
            indicator.context.resources.getDimension(R.dimen.ponyo_margin_def).toInt()
        navigatorAdapter = PanelNavigatorAdapter(pager)
        commonNavigator.adapter = navigatorAdapter
        indicator.navigator = commonNavigator
        ViewPagerHelper.bind(indicator, pager)
        pagerAdapter?.setPanels(panels)
        navigatorAdapter?.setPanels(panels)
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