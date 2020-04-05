package per.goweii.ponyo.panel.db

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class DbPanel : BasePanel() {

    private val dbKeyAdapter by lazy { DbAdapter() }
    private val dbValueAdapter by lazy { DbAdapter() }
    private lateinit var rv_db_key: RecyclerView
    private lateinit var rv_db_value: RecyclerView
    private lateinit var ll_db: LinearLayout
    private lateinit var ll_table: LinearLayout

    override fun getPanelLayoutRes(): Int = R.layout.panel_db

    override fun getPanelName(): String = "数据库"

    override fun onPanelViewCreated(view: View) {
        ll_db = view.findViewById(R.id.ll_db)
        ll_table = view.findViewById(R.id.ll_table)
        rv_db_key = view.findViewById(R.id.rv_db_key)
        rv_db_value = view.findViewById(R.id.rv_db_value)
        rv_db_key.adapter = dbKeyAdapter
        rv_db_value.adapter = dbValueAdapter
        init()
    }

    private fun init() {
        DbManager.findAllDb(context)
        for (db in DbManager.dbs) {
            val tv = createTabTextView()
            tv.text = db.name
            tv.setOnClickListener {
                selectDb(db)
            }
            ll_db.addView(tv)
        }
    }

    private fun selectDb(db: DbManager.Db) {
        ll_table.removeAllViews()
        var index = -1
        DbManager.dbs.forEachIndexed { i, d ->
            if (db == d) {
                index = i
                ll_db.getChildAt(i).isSelected = true
            } else {
                ll_db.getChildAt(i).isSelected = false
            }
        }
        if (index == -1) {
            return
        }
        for (table in db.tables) {
            val tv = createTabTextView()
            tv.text = table.name
            tv.setOnClickListener {
                selectTable(table)
            }
            ll_table.addView(tv)
        }
    }

    private fun selectTable(table: DbManager.Table) {
        for (i in 0 until ll_table.childCount) {
            val view = ll_table.getChildAt(i) as TextView
            view.isSelected = table.name == view.text.toString()
        }
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
                _h.forEach {
                    listKey.add(it.key)
                }
            }
            _h.forEach {
                listValue.add(it.value)
            }
        }
        rv_db_key.layoutManager = GridLayoutManager(context, size, GridLayoutManager.VERTICAL, false)
        rv_db_value.layoutManager = GridLayoutManager(context, size, GridLayoutManager.VERTICAL, false)
        dbKeyAdapter.set(listKey)
        dbValueAdapter.set(listValue)
    }

    private fun createTabTextView(): TextView {
        return LayoutInflater.from(ll_db.context)
            .inflate(R.layout.tab_db, ll_db, false).apply {
                this as TextView
                text = getPanelName()
            } as TextView
    }
}