package per.goweii.ponyo

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.log.LogBody
import per.goweii.ponyo.log.LogPrinter
import per.goweii.ponyo.log.Ponlog
import java.util.*

/**
 * @author CuiZhen
 * @date 2020/3/29
 */
object LogManager : LogPrinter {

    data class Log(
        val level: Ponlog.Level,
        val tag: String,
        val body: LogBody,
        val msg: String
    )

    private val logs: MutableList<Log> = Collections.synchronizedList(LinkedList<Log>())
    private val adapter: LogAdapter by lazy { LogAdapter(logs) }
    private var recyclerView: RecyclerView? = null

    init {
        Ponlog.addLogPrinter(this)
    }

    override fun print(level: Ponlog.Level, tag: String, body: LogBody, msg: String) {
        logs.add(Log(level, tag, body, msg))
        adapter.notifyItemInserted(logs.size - 1)
        recyclerView?.scrollToPosition(adapter.itemCount - 1)
    }

    fun attachTo(rv: RecyclerView) {
        recyclerView = rv
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)
    }

}