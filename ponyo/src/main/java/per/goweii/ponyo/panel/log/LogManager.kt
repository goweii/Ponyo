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
object LogManager : LogPrinter,
    CoroutineScope by CoroutineScope(newSingleThreadContext("LogCounterContext")) {
    private const val prePageCount = 100

    private val adapter: LogAdapter by lazy { LogAdapter() }
    private var recyclerView: RecyclerView? = null
    private var tvMore: TextView? = null
    private var layoutManager: LinearLayoutManager? = null

    private val logs: MutableList<LogEntity> = Collections.synchronizedList(LinkedList<LogEntity>())

    private var offset: Int = 0

    override fun print(level: Ponlog.Level, tag: String, body: LogBody, msg: String) {
        launch(this.coroutineContext) {
            val logEntity = LogEntity(level, tag, body, msg)
            logs.add(logEntity)
            if (logEntity.match()) {
                launch(Dispatchers.Main) {
                    adapter.add(data = logEntity)
                    if (!showMore()) {
                        if (adapter.itemCount > prePageCount) {
                            val count = adapter.itemCount - prePageCount
                            offset += count
                            adapter.remove(count = count)
                        }
                        scrollBottom()
                    }
                }
            }
        }
    }

    fun scrollBottom() {
        recyclerView?.scrollToPosition(adapter.itemCount - 1)
        recyclerView?.smoothScrollToPosition(adapter.itemCount - 1)
    }

    private fun showMore(): Boolean {
        return if (false == recyclerView?.canScrollVertically(1)) {
            tvMore?.visibility = View.INVISIBLE
            false
        } else {
            tvMore?.visibility = View.VISIBLE
            true
        }
    }

    fun attachTo(rv: RecyclerView, tvMore: TextView, itemClicked: (logEntity: LogEntity)->Unit) {
        recyclerView = rv
        rv.itemAnimator = null
        this.tvMore = tvMore
        adapter.onItemClicked(itemClicked)
        rv.adapter = adapter
        layoutManager = LogLinearLayoutManager(rv.context)
        rv.layoutManager = layoutManager
        adapter.set(logs)
        tvMore.setOnClickListener {
            scrollBottom()
        }
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                showMore()
            }
        })
    }

    private var a: Boolean = true
    private var e: Boolean = true
    private var w: Boolean = true
    private var d: Boolean = true
    private var i: Boolean = true
    private var v: Boolean = true

    fun notifyLevel(a: Boolean, e: Boolean, w: Boolean, d: Boolean, i: Boolean, v: Boolean) {
        this.a = a
        this.e = e
        this.w = w
        this.d = d
        this.i = i
        this.v = v
        val data = mutableListOf<LogEntity>()
        for (pos in offset until logs.size) {
            val item = logs[pos]
            if (item.match()) {
                data.add(item)
            }
        }
        adapter.set(data)
        showMore()
    }

    private var t: String = ""

    fun notifyTag(tag: String) {
        this.t = tag
        val data = mutableListOf<LogEntity>()
        for (pos in offset until logs.size) {
            val item = logs[pos]
            if (item.match()) {
                data.add(item)
            }
        }
        adapter.set(data)
        showMore()
    }

    private var s: String = ""

    fun notifySearch(key: String) {
        this.s = key
        val data = mutableListOf<LogEntity>()
        for (pos in offset until logs.size) {
            val item = logs[pos]
            if (item.match()) {
                data.add(item)
            }
        }
        adapter.set(data)
        showMore()
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
            if (item.match()) {
                data.add(item)
            }
        }
        adapter.add(0, data)
        showMore()
        return true
    }

    fun nextPage() {
        offset = logs.size
        adapter.clear()
    }

    private fun LogEntity.match(): Boolean {
        return matchLevel() && matchTag() && matchSearch()
    }

    private fun LogEntity.matchLevel(): Boolean {
        return when (this.level) {
            Ponlog.Level.ASSERT -> a
            Ponlog.Level.ERROR -> e
            Ponlog.Level.WARN -> w
            Ponlog.Level.INFO -> i
            Ponlog.Level.DEBUG -> d
            Ponlog.Level.VERBOSE -> v
        }
    }

    private fun LogEntity.matchTag(): Boolean {
        return this.tag.contains(t)
    }

    private fun LogEntity.matchSearch(): Boolean {
        return this.msg.contains(s) ||
                this.body.threadName.contains(s) ||
                this.body.classInfo.contains(s)
    }

}