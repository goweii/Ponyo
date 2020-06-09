package per.goweii.ponyo.panel.sp

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class SpPanel : BasePanel() {

    private val spAdapter by lazy { SpAdapter() }
    private val spNameAdapter by lazy {
        SpNameAdapter { selectSp(it) }
    }
    private lateinit var rv_sp_name: RecyclerView
    private lateinit var rv_sp: RecyclerView

    override fun getPanelLayoutRes(): Int = R.layout.panel_sp

    override fun getPanelName(): String = "首选项"

    override fun onPanelViewCreated(view: View) {
        rv_sp_name = view.findViewById(R.id.rv_sp_name)
        rv_sp = view.findViewById(R.id.rv_sp)
        rv_sp_name.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_sp_name.adapter = spNameAdapter
        rv_sp.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_sp.adapter = spAdapter
    }

    override fun onFirstVisible() {
    }

    override fun onVisible() {
        super.onVisible()
        var selected: String? = null
        spNameAdapter.get().forEach {
            if (it.selected) {
                selected = it.name
                return@forEach
            }
        }
        SpManager.findAllSp(context)
        val newData = SpManager.spNames
        var selectIndex = -1
        selected?.let { _selected ->
            newData.forEachIndexed { index, name ->
                if (_selected == name) {
                    selectIndex = index
                    return@forEachIndexed
                }
            }
        }
        spNameAdapter.set(SpManager.spNames, selectIndex)
        if (selectIndex == -1) {
            selectSp(null)
        }
    }

    override fun onGone() {
    }

    private fun selectSp(spName: String?) {
        spName?.let {
            val data = SpManager.readSp(context, spName)
            spAdapter.set(data)
        } ?: run {
            spAdapter.set(emptyList())
        }
    }
}