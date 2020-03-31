package per.goweii.ponyo

import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView

object PanelProvider {

    private lateinit var panelContainer: FrameLayout

    fun attach(container: FrameLayout) {
        panelContainer = container
        initLog()
    }

    private fun initLog() {
        val rv_log = panelContainer.findViewById<RecyclerView>(R.id.rv_log)
        val cb_e = panelContainer.findViewById<CheckBox>(R.id.cb_e)
        val cb_w = panelContainer.findViewById<CheckBox>(R.id.cb_w)
        val cb_d = panelContainer.findViewById<CheckBox>(R.id.cb_d)
        val cb_i = panelContainer.findViewById<CheckBox>(R.id.cb_i)
        val cb_v = panelContainer.findViewById<CheckBox>(R.id.cb_v)
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
        LogManager.notifyLevel(true, true, true, true, true)
    }

}