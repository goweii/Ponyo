package per.goweii.ponyo.panel.sp

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class SpPanel : BasePanel() {

    private val spAdapter by lazy { SpAdapter() }
    private lateinit var rv_sp: RecyclerView
    private lateinit var ll_sp: LinearLayout

    override fun getPanelLayoutRes(): Int = R.layout.panel_sp

    override fun getPanelName(): String = "SP"

    override fun onPanelViewCreated(view: View) {
        rv_sp = view.findViewById(R.id.rv_sp)
        ll_sp = view.findViewById(R.id.ll_sp)
        rv_sp.layoutManager = LinearLayoutManager(context)
        rv_sp.adapter = spAdapter
        init()
    }

    private fun init() {
        SpManager.findAllSp(context)
        for (spName in SpManager.spNames) {
            val tv = createTabTextView()
            tv.text = spName
            tv.setOnClickListener {
                selectSp(spName)
            }
            ll_sp.addView(tv)
        }
    }

    private fun selectSp(spName: String) {
        for (i in 0 until ll_sp.childCount) {
            val view = ll_sp.getChildAt(i) as TextView
            view.isSelected = spName == view.text.toString()
        }
        showSp(spName)
    }

    private fun showSp(spName: String) {
        val data = SpManager.readSp(context, spName)
        spAdapter.set(data)
    }

    private fun createTabTextView(): TextView {
        return LayoutInflater.from(ll_sp.context)
            .inflate(R.layout.tab_db, ll_sp, false).apply {
                this as TextView
                text = getPanelName()
            } as TextView
    }
}