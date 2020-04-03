package per.goweii.ponyo.panel.db

import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class DbPanel : BasePanel() {

    private val dbAdapter by lazy { DbAdapter() }
    private lateinit var rv_db: RecyclerView
    private lateinit var ll_db: LinearLayout
    private lateinit var ll_table: LinearLayout

    override fun getPanelLayoutRes(): Int = R.layout.panel_db

    override fun getPanelName(): String = "数据库"

    override fun onPanelViewCreated(view: View) {
        ll_db = view.findViewById(R.id.ll_db)
        ll_table = view.findViewById(R.id.ll_table)
        rv_db = view.findViewById(R.id.rv_db)
        rv_db.adapter = dbAdapter
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
                ll_db.getChildAt(i).alpha = 1F
            } else {
                ll_db.getChildAt(i).alpha = 0.6F
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
            if (table.name == view.text.toString()) {
                view.alpha = 1F
            } else {
                view.alpha = 0.6F
            }
        }
        showTable(table)
    }

    private fun showTable(table: DbManager.Table) {
        val dbList = DbManager.readTable(table)
        val list = arrayListOf<String>()
        val size = if (dbList.isNullOrEmpty()) {
            1
        } else {
            if (dbList[0].isNotEmpty()) dbList[0].size else 1
        }
        dbList?.forEach { _h ->
            if (list.isEmpty()) {
                _h.forEach {
                    list.add(it.key)
                }
            }
            _h.forEach {
                list.add(it.value)
            }
        }
        rv_db.layoutManager = GridLayoutManager(context, size, GridLayoutManager.VERTICAL, false)
        dbAdapter.set(list)
    }

    private fun createTabTextView(): TextView {
        return TextView(context).apply {
            alpha = 0.6F
            gravity = Gravity.CENTER
            val size = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10F,
                context.resources.displayMetrics
            ).toInt()
            setPadding(size, 0, size, 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
            setTextColor(ContextCompat.getColor(context, R.color.colorOnBackground))
        }
    }
}