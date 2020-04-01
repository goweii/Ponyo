package per.goweii.ponyo.panel

import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.log.LogManager

class LogPanel : BasePanel() {

    override fun getPanelLayoutRes(): Int = R.layout.panel_log

    override fun onPanelViewCreated(view: View) {
        val srl_log = view.findViewById<SmartRefreshLayout>(R.id.srl_log)
        srl_log.setEnableAutoLoadMore(false)
        srl_log.setOnRefreshListener {
            LogManager.lastPage()
            srl_log.finishRefresh()
        }
        srl_log.setOnLoadMoreListener {
            LogManager.nextPage()
            srl_log.finishLoadMore()
        }
        val rv_log = view.findViewById<RecyclerView>(R.id.rv_log)
        val cb_e = view.findViewById<CheckBox>(R.id.cb_e)
        val cb_w = view.findViewById<CheckBox>(R.id.cb_w)
        val cb_d = view.findViewById<CheckBox>(R.id.cb_d)
        val cb_i = view.findViewById<CheckBox>(R.id.cb_i)
        val cb_v = view.findViewById<CheckBox>(R.id.cb_v)
        val listener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            LogManager.notifyLevel(
                cb_e.isChecked,
                cb_w.isChecked,
                cb_d.isChecked,
                cb_i.isChecked,
                cb_v.isChecked
            )
        }
        cb_e.setOnCheckedChangeListener(listener)
        cb_w.setOnCheckedChangeListener(listener)
        cb_d.setOnCheckedChangeListener(listener)
        cb_i.setOnCheckedChangeListener(listener)
        cb_v.setOnCheckedChangeListener(listener)
        LogManager.attachTo(rv_log)
    }
}