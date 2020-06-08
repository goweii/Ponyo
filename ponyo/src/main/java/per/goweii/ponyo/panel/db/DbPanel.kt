package per.goweii.ponyo.panel.db

import android.view.View
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

    override fun onFirstVisible() {
    }

    override fun onVisible() {
        super.onVisible()
        var selectDb: DbManager.Db? = null
        dbTabAdapter.get().forEach {
            if (it.selected) {
                selectDb = it.db
                return@forEach
            }
        }
        DbManager.findAllDb(context)
        val newData = DbManager.dbs
        var selectIndex = -1
        selectDb?.let { _selectDb ->
            newData.forEachIndexed { index, db ->
                if (db.path == _selectDb.path) {
                    selectIndex = index
                    return@forEachIndexed
                }
            }
        }
        dbTabAdapter.set(newData, selectIndex)
        if (selectIndex == -1) {
            selectDb(null)
        }
    }

    override fun onGone() {
    }

    private fun selectDb(db: DbManager.Db?) {
        db?.let {
            tableTabAdapter.set(db.tables)
        } ?: run {
            tableTabAdapter.set(emptyList())
        }
        selectTable(null)
    }

    private fun selectTable(table: DbManager.Table?) {
        table?.let {
            showTable(it)
        } ?: run {
            rv_db_key.layoutManager =
                GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
            rv_db_value.layoutManager =
                GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
            dbKeyAdapter.set(emptyList())
            dbValueAdapter.set(emptyList())
        }
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