package per.goweii.ponyo.panel.db

import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class DbPanel : BasePanel() {

    private val dbAdapter by lazy { DbAdapter() }
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var rv_db: RecyclerView
    private lateinit var ll_db: LinearLayout
    private lateinit var ll_table: LinearLayout

    override fun getPanelLayoutRes(): Int = R.layout.panel_db

    override fun getPanelName(): String = "数据库"

    override fun onPanelViewCreated(view: View) {
        ll_db = view.findViewById(R.id.ll_db)
        ll_table = view.findViewById(R.id.ll_table)
        rv_db = view.findViewById(R.id.rv_db)
        gridLayoutManager = GridLayoutManager(view.context, 1, GridLayoutManager.VERTICAL, false)
        rv_db.layoutManager = gridLayoutManager
        rv_db.adapter = dbAdapter
        init()
    }

    private fun init() {
        DbManager.findAllDb(context)
        for (db in DbManager.dbs) {

        }
        val table = DbManager.readTable(DbManager.dbs[0].tables[0])
        if (!table.isNullOrEmpty()) {
            val size = table[0].size
            gridLayoutManager.spanCount = if (size > 0) size else 1
            val list = arrayListOf<String>()
            table.forEach { _h ->
                if (list.isEmpty()) {
                    _h.forEach {
                        list.add(it.key)
                    }
                }
                _h.forEach {
                    list.add(it.value)
                }
            }
            dbAdapter.set(list)
        }
    }

    private fun createTabForDb(): TextView {
        return TextView(context).apply {

        }
    }
}