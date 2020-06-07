package per.goweii.ponyo.panel.db

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class DbPanel : BasePanel() {

    private val dbTabAdapter by lazy { DbTabAdapter { selectDb(it) } }
    private val tableTabAdapter by lazy { TableTabAdapter { selectTable(it) } }
    private val dbKeyAdapter by lazy { DbAdapter() }
    private val dbValueAdapter by lazy { DbAdapter() }
    private lateinit var rv_db_tab: RecyclerView
    private lateinit var rv_table_tab: RecyclerView
    private lateinit var rv_db_key: RecyclerView
    private lateinit var rv_db_value: RecyclerView

    override fun getPanelLayoutRes(): Int = R.layout.panel_db

    override fun getPanelName(): String = "数据库"

    override fun onPanelViewCreated(view: View) {
        rv_db_tab = view.findViewById(R.id.rv_db_tab)
        rv_table_tab = view.findViewById(R.id.rv_table_tab)
        rv_db_key = view.findViewById(R.id.rv_db_key)
        rv_db_value = view.findViewById(R.id.rv_db_value)
        rv_db_tab.layoutManager = LinearLayoutManager(context, GridLayoutManager.HORIZONTAL, false)
        rv_db_tab.adapter = dbTabAdapter
        rv_table_tab.layoutManager =
            LinearLayoutManager(context, GridLayoutManager.HORIZONTAL, false)
        rv_table_tab.adapter = tableTabAdapter
        rv_db_key.adapter = dbKeyAdapter
        rv_db_value.adapter = dbValueAdapter
    }

    override fun onVisible() {
        DbManager.findAllDb(context)
        dbTabAdapter.set(DbManager.dbs)
    }

    override fun onGone() {
    }

    private fun selectDb(db: DbManager.Db) {
        tableTabAdapter.set(db.tables)
    }

    private fun selectTable(table: DbManager.Table) {
        showTable(table)
    }

    private fun showTable(table: DbManager.Table) {
        val dbList = DbManager.readTable(table)
        val listKey = arrayListOf<String>()
        val listValue = arrayListOf<String>()
        val size = if (dbList.isNullOrEmpty()) {
            1
        } else {
            if (dbList[0].isNotEmpty()) dbList[0].size else 1
        }
        dbList?.forEach { _h ->
            if (listKey.isEmpty()) {
                _h.forEach { listKey.add(it.key) }
            }
            _h.forEach { listValue.add(it.value) }
        }
        rv_db_key.layoutManager =
            GridLayoutManager(context, size, GridLayoutManager.VERTICAL, false)
        rv_db_value.layoutManager =
            GridLayoutManager(context, size, GridLayoutManager.VERTICAL, false)
        dbKeyAdapter.set(listKey)
        dbValueAdapter.set(listValue)
    }
}