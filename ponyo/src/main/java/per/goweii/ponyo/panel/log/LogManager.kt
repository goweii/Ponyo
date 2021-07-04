package per.goweii.ponyo.panel.log

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import per.goweii.ponyo.Ponyo
import per.goweii.ponyo.log.LogLine
import per.goweii.ponyo.log.Logcat
import java.util.*

/**
 * @author CuiZhen
 * @date 2020/3/29
 */
@Suppress("EXPERIMENTAL_API_USAGE")
object LogManager : Logcat.OnCatchListener {
    private const val prePageCount = 100

    private val adapter: LogAdapter by lazy { LogAdapter() }
    private var recyclerView: RecyclerView? = null
    private var tvMore: TextView? = null
    private var layoutManager: LinearLayoutManager? = null

    private val logs = LinkedList<LogLine>()
    private var offset: Int = 0

    private var unreadAssertCount = 0
        set(value) {
            field = value
            Ponyo.onLoggerAssert(field)
        }
    private var unreadErrorCount = 0
        set(value) {
            field = value
            Ponyo.onLoggerError(field)
        }

    fun clearUnreadCount() {
        unreadAssertCount = 0
        unreadErrorCount = 0
    }

    fun scrollBottom() {
        if (adapter.itemCount > 0) {
            recyclerView?.scrollToPosition(adapter.itemCount - 1)
            recyclerView?.smoothScrollToPosition(adapter.itemCount - 1)
        }
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

    fun attachTo(rv: RecyclerView, tvMore: TextView, itemClicked: (logLine: LogLine) -> Unit) {
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
                loadPage()
                showMore()
            }
        })
    }

    override fun onCatch(logLines: List<LogLine>) {
        addLog(logLines)
    }

    fun clear() {
        logs.clear()
        offset = logs.size
        adapter.clear()
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
        val data = mutableListOf<LogLine>()
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
        val data = mutableListOf<LogLine>()
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
        val data = mutableListOf<LogLine>()
        for (pos in offset until logs.size) {
            val item = logs[pos]
            if (item.match()) {
                data.add(item)
            }
        }
        adapter.set(data)
        showMore()
    }

    private fun prevPage(): Boolean {
        if (offset == 0) {
            return false
        }
        val oldOffset = offset
        offset = if (offset < prePageCount) 0 else offset - prePageCount
        val data = mutableListOf<LogLine>()
        for (pos in offset until oldOffset) {
            val item = logs[pos]
            if (item.match()) {
                data.add(item)
            }
        }
        adapter.addAll(0, data)
        showMore()
        return true
    }

    private var pageLoading = false

    private fun loadPage() {
        if (pageLoading) return
        val lm = layoutManager ?: return
        val firstIndex = lm.findFirstVisibleItemPosition()
        if (firstIndex <= 10) {
            pageLoading = true
            recyclerView?.post {
                prevPage()
                pageLoading = false
            }
        }
    }

    private fun addLog(logLines: List<LogLine>) {
        logs.addAll(logLines)
        if (recyclerView?.isShown != true) {
            var assertCount = 0
            var errorCount = 0
            logLines.forEach {
                when (it.level) {
                    Log.ASSERT -> assertCount++
                    Log.ERROR -> errorCount++
                }
            }
            unreadAssertCount += assertCount
            unreadErrorCount += errorCount
        }
        logLines.filter { it.match() }.let { lines ->
            adapter.addAll(data = lines)
            if (lines.isNotEmpty() && !showMore()) {
                if (adapter.itemCount > prePageCount) {
                    val count = adapter.itemCount - prePageCount
                    offset += count
                    adapter.remove(count = count)
                }
                scrollBottom()
            }
        }
    }

    private fun LogLine.match(): Boolean {
        return matchLevel() && matchTag() && matchSearch()
    }

    private fun LogLine.matchLevel(): Boolean {
        return when (this.level) {
            Log.ASSERT -> a
            Log.ERROR -> e
            Log.WARN -> w
            Log.INFO -> i
            Log.DEBUG -> d
            Log.VERBOSE -> v
            else -> false
        }
    }

    private fun LogLine.matchTag(): Boolean {
        return this.tag.contains(t)
    }

    private fun LogLine.matchSearch(): Boolean {
        return this.message.contains(s)
    }

}