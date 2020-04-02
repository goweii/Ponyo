package per.goweii.ponyo.panel.db

import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import per.goweii.ponyo.R
import per.goweii.ponyo.panel.BasePanel

class DbPanel : BasePanel() {

    private val dbAdapter by lazy { DbAdapter() }
    private lateinit var gridLayoutManager: GridLayoutManager

    override fun getPanelLayoutRes(): Int = R.layout.panel_db

    override fun getPanelName(): String = "数据库"

    override fun onPanelViewCreated(view: View) {
        val rv_db = view.findViewById<RecyclerView>(R.id.rv_db)
        gridLayoutManager = GridLayoutManager(view.context, 1, GridLayoutManager.VERTICAL, false)
        rv_db.layoutManager = gridLayoutManager
        rv_db.adapter = dbAdapter
        init()
    }

    private fun init() {
        DbManager.findAllDb(context)
        DbManager.readTable(DbManager.dbs[5].tables[3])
    }
}