package per.goweii.ponyo.panel.device

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.BuildConfig
import per.goweii.ponyo.R
import per.goweii.ponyo.device.Device
import per.goweii.ponyo.panel.BasePanel

class DevicePanel: BasePanel() {
    private var rv_device: RecyclerView? = null
    private var adapter: DeviceAdapter? = null

    override fun getPanelLayoutRes(): Int = R.layout.ponyo_panel_device

    override fun getPanelName(): String = "设备信息"

    override fun onPanelViewCreated(view: View) {
        rv_device = view.findViewById(R.id.rv_device)
        rv_device?.layoutManager = LinearLayoutManager(view.context)
        adapter = DeviceAdapter()
        rv_device?.adapter = adapter
    }

    override fun onVisible(firstVisible: Boolean) {
        super.onVisible(firstVisible)
        val deviceInfos = Device.toMap()
        deviceInfos["调试模式"] = BuildConfig.DEBUG.toString()
        adapter?.set(deviceInfos)
    }

    override fun onGone() {
    }
}