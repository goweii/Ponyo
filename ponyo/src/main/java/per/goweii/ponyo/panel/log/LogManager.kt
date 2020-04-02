package per.goweii.ponyo.panel.log

import android.view.View
import android.widget.TextView
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

    private const val prePageCount = 100
    private const val minNotifyTime = 0L

    private val adapter: LogAdapter by lazy {
        LogAdapter()
    }
    private var recyclerView: RecyclerView? = null
    private var tvMore: TextView? = null
    private var layoutManager: LinearLayoutManager? = null

    private val logs: MutableList<LogEntity> = Collections.synchronizedList(LinkedList<LogEntity>())
    private val logCaches: MutableList<LogEntity> =
        Collections.synchronizedList(LinkedList<LogEntity>())

    private var offset: Int = 0

    private var lastNotifyTime = 0L
    private var job: Job? = null

    override fun print(level: Ponlog.Level, tag: String, body: LogBody, msg: String) {
        logCaches.add(LogEntity(level, tag, body, msg))
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
        launch {
            logs.addAll(logCaches)
            val iterator = logCaches.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                when (item.level) {
                    Ponlog.Level.ERROR -> if (!e) iterator.remove()
                    Ponlog.Level.WARN -> if (!w) iterator.remove()
                    Ponlog.Level.INFO -> if (!i) iterator.remove()
                    Ponlog.Level.DEBUG -> if (!d) iterator.remove()
                    Ponlog.Level.VERBOSE -> if (!v) iterator.remove()
                }
            }
            adapter.add(data = logCaches)
            logCaches.clear()
            if (false == recyclerView?.canScrollVertically(1)) {
                if (adapter.itemCount > prePageCount) {
                    val count = adapter.itemCount - prePageCount
                    offset += count
                    adapter.remove(count = count)
                }
                recyclerView?.scrollToPosition(adapter.itemCount - 1)
                recyclerView?.smoothScrollToPosition(adapter.itemCount - 1)
                tvMore?.visibility = View.GONE
            } else {
                tvMore?.visibility = View.VISIBLE
            }
        }
    }

    fun attachTo(rv: RecyclerView, tvMore: TextView) {
        recyclerView = rv
        rv.itemAnimator = null
        this.tvMore = tvMore
        rv.adapter = adapter
        layoutManager = LogLinearLayoutManager(rv.context)
        rv.layoutManager = layoutManager
        adapter.set(logs)
        tvMore.setOnClickListener {
            recyclerView?.scrollToPosition(adapter.itemCount - 1)
            recyclerView?.smoothScrollToPosition(adapter.itemCount - 1)
        }
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (false == LogManager.recyclerView?.canScrollVertically(1)) {
                    LogManager.tvMore?.visibility = View.GONE
                }
            }
        })
    }

    private var e: Boolean = true
    private var w: Boolean = true
    private var d: Boolean = true
    private var i: Boolean = true
    private var v: Boolean = true

    fun notifyLevel(e: Boolean, w: Boolean, d: Boolean, i: Boolean, v: Boolean) {
        this.e = e
        this.w = w
        this.d = d
        this.i = i
        this.v = v
        val data = mutableListOf<LogEntity>()
        for (pos in offset until logs.size) {
            val item = logs[pos]
            when (item.level) {
                Ponlog.Level.ERROR -> if (this.e) data.add(item)
                Ponlog.Level.WARN -> if (this.w) data.add(item)
                Ponlog.Level.INFO -> if (this.i) data.add(item)
                Ponlog.Level.DEBUG -> if (this.d) data.add(item)
                Ponlog.Level.VERBOSE -> if (this.v) data.add(item)
            }
        }
        adapter.set(data)
    }

    fun lastPage(): Boolean {
        if (offset == 0) {
            return false
        }
        val oldOffset = offset
        offset = if (offset < prePageCount) 0 else offset - prePageCount
        val data = mutableListOf<LogEntity>()
        for (pos in offset until oldOffset) {
            val item = logs[pos]
            when (item.level) {
                Ponlog.Level.ERROR -> if (this.e) data.add(item)
                Ponlog.Level.WARN -> if (this.w) data.add(item)
                Ponlog.Level.INFO -> if (this.i) data.add(item)
                Ponlog.Level.DEBUG -> if (this.d) data.add(item)
                Ponlog.Level.VERBOSE -> if (this.v) data.add(item)
            }
        }
        adapter.add(0, data)
        recyclerView?.scrollToPosition(data.size - 1)
        recyclerView?.smoothScrollToPosition(data.size - 1)
        return true
    }

    fun nextPage() {
        offset = logs.size
        adapter.clear()
    }

}