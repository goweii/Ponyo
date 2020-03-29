package per.goweii.ponyo

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import per.goweii.ponyo.log.LogBody
import per.goweii.ponyo.log.LogPrinter
import per.goweii.ponyo.log.Ponlog
import java.util.*

/**
 * @author CuiZhen
 * @date 2020/3/29
 */
object LogManager : LogPrinter, CoroutineScope by MainScope() {

    data class Log(
        val level: Ponlog.Level,
        val tag: String,
        val body: LogBody,
        val msg: String
    )

    private val adapter: LogAdapter by lazy { LogAdapter(logs) }
    private var recyclerView: RecyclerView? = null

    private val logs: MutableList<Log> = Collections.synchronizedList(LinkedList<Log>())
    private val logCaches: MutableList<Log> = Collections.synchronizedList(LinkedList<Log>())

    private const val minNotifyTime = 100L
    private var lastNotifyTime = 0L
    private var job: Job? = null

    init {
        Ponlog.addLogPrinter(this)
    }

    override fun print(level: Ponlog.Level, tag: String, body: LogBody, msg: String) {
        logCaches.add(Log(level, tag, body, msg))
        job?.cancel()
        val currTime = System.currentTimeMillis()
        if (currTime - lastNotifyTime > minNotifyTime) {
            lastNotifyTime = currTime
            notifyAdapter()
        } else {
            job = launch(Dispatchers.IO) {
                delay(minNotifyTime)
                notifyAdapter()
            }
        }
    }

    @Synchronized
    private fun notifyAdapter() {
        val count = logCaches.size
        logs.addAll(logCaches)
        logCaches.clear()
        adapter.notifyItemInserted(logs.size - count)
        recyclerView?.scrollToPosition(adapter.itemCount - 1)
    }

    fun attachTo(rv: RecyclerView) {
        recyclerView = rv
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)
    }

}