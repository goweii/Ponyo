package per.goweii.ponyo.panel.device

import android.view.View
import android.widget.TextView
import per.goweii.ponyo.R
import per.goweii.ponyo.device.Device
import per.goweii.ponyo.panel.BasePanel

class DevicePanel: BasePanel() {

    private lateinit var tv_device: TextView

    override fun getPanelLayoutRes(): Int = R.layout.ponyo_panel_device

    override fun getPanelName(): String = "设备信息"

    override fun onPanelViewCreated(view: View) {
        tv_device = view.findViewById(R.id.tv_device)
    }

    override fun onVisible(firstVisible: Boolean) {
        super.onVisible(firstVisible)
        tv_device.text = Device.toString()
    }

    override fun onGone() {
    }
}