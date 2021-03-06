package per.goweii.ponyo.panel.sp

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.Panel

class SpPanel : Panel() {

    private val spAdapter by lazy { SpAdapter() }
    private val spNameAdapter by lazy {
        SpNameAdapter { selectSp(it) }
    }
    private var rv_sp_name: RecyclerView? = null
    private var rv_sp: RecyclerView? = null

    override fun getLayoutRes(): Int = R.layout.ponyo_panel_sp

    override fun getPanelName(): String = "首选项"

    override fun onCreated(view: View) {
        rv_sp_name = view.findViewById(R.id.rv_sp_name)
        rv_sp = view.findViewById(R.id.rv_sp)
        rv_sp_name?.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        rv_sp_name?.adapter = spNameAdapter
        rv_sp?.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        rv_sp?.adapter = spAdapter
    }

    override fun onVisible(view: View) {
        super.onVisible(view)
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

    private fun selectSp(spName: String?) {
        spName?.let {
            val data = SpManager.readSp(context, spName)
            spAdapter.set(data)
        } ?: run {
            spAdapter.set(emptyList())
        }
    }
}